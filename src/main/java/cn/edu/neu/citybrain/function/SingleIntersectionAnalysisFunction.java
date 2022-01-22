package cn.edu.neu.citybrain.function;

import cn.edu.neu.citybrain.db.DBConnection;
import cn.edu.neu.citybrain.db.DBConstants;
import cn.edu.neu.citybrain.db.DBQuery;
import cn.edu.neu.citybrain.db.JdbcSupport;
import cn.edu.neu.citybrain.dto.*;
import cn.edu.neu.citybrain.dto.my.RidGranularityInfo;
import cn.edu.neu.citybrain.dto.my.RoadMetric;
import cn.edu.neu.citybrain.dto.my.TurnGranularityInfo;
import cn.edu.neu.citybrain.evaluation.SingleIntersectionAnalysisV2;
import cn.edu.neu.citybrain.util.CityBrainUtil;
import cn.edu.neu.citybrain.util.ConstantUtil;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static cn.edu.neu.citybrain.db.DBConstants.*;

public class SingleIntersectionAnalysisFunction extends ProcessWindowFunction<Row, List<RoadMetric>, Tuple, TimeWindow> {
    private ExecutorService executorService;
    private boolean isExhibition;

    // base table
    Map<String, RidInfo> ridInfoMap = new HashMap<>();
    Map<String, InterLaneInfo> interLaneInfoMap = new HashMap<>();
    Map<String, SigninterfridseqIndex> seqNdIndexMap = new HashMap<>();
    Map<String, PhaseInfo> phaseInfoMap = new HashMap<>();
    // cache
    long curDayOfWeek = -1;
    Map<Long, List<RidIndex>> cache1;
    Map<Long, List<InterFridSeqTurnDirIndex>> cache2;

    // metric
    private final String METRIC_SQL = "insert into statistic(job_name,subtask_index,dt,step_index_1mi,amount,is_exhibition) values(?,?,?,?,?,?,?)";
    private Connection metricConnection;
    private PreparedStatement metricPS;

    public SingleIntersectionAnalysisFunction(boolean isExhibition) {
        this.isExhibition = isExhibition;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        loadBaseData();

        //metric
        metricConnection = DBConnection.getConnection();
        metricPS = metricConnection.prepareStatement(METRIC_SQL);
    }

    @Override
    // [0]tag, [1]rid, [2]travel_time, [3]speed, [4]reliability_code, [5]step_index_1mi, [6]step_index_10mi, [7]day_of_week, [8]timestamp
    public void process(Tuple tuple, Context context, Iterable<Row> iterable, Collector<List<RoadMetric>> collector) throws Exception {
        long beforeProcess = System.currentTimeMillis();
        long amount = 0;

        Map<String, List<TurnGranularityInfo>> turnGranularityInfoMap = new HashMap<>();
        Map<String, Set<PhaseInfo>> interAndDirMapPhaseNo = new HashMap<>();
        Map<String, Set<String>> interLaneMap = new HashMap<>();

        Set<String> state = new HashSet<>(); // 由于某些原因，需要去重

        Long stepIndex1mi = 0L;
        Long stepIndex10mi = 0L;
        Long dayOfWeek = 0L;
        Long timestamp = 0L;
        for (Row originRow : iterable) {
            amount++;

            Row[] rows = hashJoin(originRow, ridInfoMap, interLaneInfoMap, seqNdIndexMap, phaseInfoMap);
            if (rows == null) {
                continue;
            }
            for (Row row : rows) {
                // unit
                String interId = (String) row.getField(1);
                String fRid = (String) row.getField(2);
                Long turnDirNo = (Long) row.getField(3);
                stepIndex1mi = (Long) row.getField(4);
                stepIndex10mi = (Long) row.getField(5);
                dayOfWeek = (Long) row.getField(6);
                timestamp = (Long) row.getField(7);

                // dws_tfc_state_rid_tp_lastspeed_rt
                Double travelTime = (Double) row.getField(8);
                Double speed = (Double) row.getField(9);
                Double reliabilityCode = (Double) row.getField(10);

                // dwd_tfc_bas_rdnet_rid_info
                Double len = (Double) row.getField(11);

                // dwd_tfc_rltn_wide_inter_lane
                String laneId = (String) row.getField(12);

                // dws_tfc_state_signinterfridseq_nd_index_m
                String fRidSeq = (String) row.getField(13);
                Double benchmarkNostopTravelTime3m = (Double) row.getField(14);

                // dwd_tfc_ctl_signal_phasedir
                String phasePlanID = (String) row.getField(15);
                String phaseName = (String) row.getField(16);

                //----------------------
                // 每个窗口更新一次的数据
                //----------------------

                String keyInterFridTurndir = CityBrainUtil.concat(interId, fRid, turnDirNo);

                // turnGranularityInfoMap
                RidGranularityInfo ridGranularityInfo;
                if (!turnGranularityInfoMap.containsKey(fRid)) {
                    turnGranularityInfoMap.put(fRid, new ArrayList<>());
                    ridGranularityInfo = new RidGranularityInfo(travelTime, speed, reliabilityCode, len);
                } else {
                    ridGranularityInfo = turnGranularityInfoMap.get(fRid).get(0).getRidGranularityInfo();
                }
                if (!state.contains(keyInterFridTurndir)) {
                    turnGranularityInfoMap.get(fRid).add(
                            new TurnGranularityInfo(
                                    ridGranularityInfo,
                                    interId, fRid, turnDirNo,
                                    laneId,
                                    fRidSeq, benchmarkNostopTravelTime3m,
                                    phasePlanID, phaseName));
                    state.add(keyInterFridTurndir);
                }

                // interAndDirMapPhaseNo
                PhaseInfo phaseInfo = new PhaseInfo(interId, phasePlanID, phaseName);
                phaseInfo.setfRid(fRid);
                phaseInfo.setTurnDirNo(turnDirNo);
                interAndDirMapPhaseNo.putIfAbsent(keyInterFridTurndir, new HashSet<>());
                interAndDirMapPhaseNo.get(keyInterFridTurndir).add(phaseInfo);

                // interLaneInfoList
                interLaneMap.putIfAbsent(keyInterFridTurndir, new TreeSet<>());
                interLaneMap.get(keyInterFridTurndir).add(laneId);
            }
        }
        for (List<TurnGranularityInfo> turnGranularityInfos : turnGranularityInfoMap.values()) {
            if (turnGranularityInfos != null) {
                for (TurnGranularityInfo turnGranularityInfo : turnGranularityInfos) {
                    double fRidseqLen = 0.0d;
                    for (String fRid : turnGranularityInfo.getfRidseq().split("#")) {
                        List<TurnGranularityInfo> turnGranularityInfoList = turnGranularityInfoMap.get(fRid);
                        if (turnGranularityInfoList != null && turnGranularityInfoList.get(0) != null) {
                            fRidseqLen += turnGranularityInfoList.get(0).getRidGranularityInfo().getLen();
                        }
                    }

                    turnGranularityInfo.setfRidseqLen(fRidseqLen);
                }
            }
        }

        if (dayOfWeek != curDayOfWeek) {
            curDayOfWeek = dayOfWeek;
            updateCache();
        }

        SingleIntersectionAnalysisV2 estimator = new SingleIntersectionAnalysisV2(this.executorService);
        Map<String, List<fRidSeqTurnDirIndexDTO>> results = estimator.evaluate(stepIndex1mi, stepIndex10mi, dayOfWeek, timestamp,
                turnGranularityInfoMap,
                interAndDirMapPhaseNo,
                interLaneMap,
                cache1,
                cache2);

        long afterProcess = System.currentTimeMillis();

        // metric
        int taskIdx = getRuntimeContext().getIndexOfThisSubtask();
        long duration = afterProcess - beforeProcess;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dt = sdf.format(new Date(timestamp));
        upload(taskIdx, dt, stepIndex1mi, amount, duration, isExhibition);

        // metric
//        long duration = afterProcess - beforeProcess;
//        double throughoutput = amount * 1.0 / duration * 1000;
//        double delay = duration * 1.0 / amount;
//        System.out.println("[flink-cache] throughput: " + throughoutput + "/s     " + "delay: " + delay + "ms");

        for (Map.Entry<String, List<fRidSeqTurnDirIndexDTO>> entry : results.entrySet()) {
            List<fRidSeqTurnDirIndexDTO> list = entry.getValue();

            List<RoadMetric> res = new ArrayList<>();
            for (fRidSeqTurnDirIndexDTO fRidSeqTurnDirIndexDTO : list) {
                RoadMetric roadMetric = new RoadMetric(
                        fRidSeqTurnDirIndexDTO.getInterId(),
                        fRidSeqTurnDirIndexDTO.getfRid(),
                        fRidSeqTurnDirIndexDTO.getTurnDirNo(),
                        dt,
                        stepIndex1mi,
                        fRidSeqTurnDirIndexDTO.getTravelTime(),
                        fRidSeqTurnDirIndexDTO.getDelay(),
                        fRidSeqTurnDirIndexDTO.getStopCnt(),
                        fRidSeqTurnDirIndexDTO.getQueue());
                res.add(roadMetric);
            }
            collector.collect(res);
        }
    }

    private void upload(int taskIdx, String dt, Long stepIndex1mi, long amount, long duration, boolean isExhibition) throws Exception {
        metricPS.clearParameters();
        ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        String jobName = parameterTool.get("jobName");
        metricPS.setObject(1, jobName);
        metricPS.setObject(2, taskIdx);
        metricPS.setObject(3, dt);
        metricPS.setObject(4, stepIndex1mi);
        metricPS.setObject(5, amount);
        metricPS.setObject(6, duration);
        metricPS.setObject(7, isExhibition ? 1 : 0);
        metricPS.executeUpdate();
    }

    private void updateCache() {
        cache1 = null;
        cache2 = null;

        DBQuery dbQuery = new DBQuery(executorService);
        // 指标1
        dbQuery.add(
                DBConstants.dws_tfc_state_rid_tpwkd_index_m,
                DBConstants.sql_dws_tfc_state_rid_tpwkd_index_m,
                RidIndex.class,
                new ArrayList<String>() {
                    {
                        add("rid");
                        add("travelTime");
                        add("stepIndex");
                    }
                },
                curDayOfWeek);
        dbQuery.add(
                DBConstants.dws_tfc_state_signinterfridseq_tpwkd_delaydur_m,
                DBConstants.sql_dws_tfc_state_signinterfridseq_tpwkd_delaydur_m,
                InterFridSeqTurnDirIndex.class,
                new ArrayList<String>() {
                    {
                        add("fRid");
                        add("turnDirNo");
                        add("avgTraceTravelTime");
                        add("stepIndex");
                    }
                },
                curDayOfWeek);

        dbQuery.execute();

        cache1 = dbQuery.<RidIndex>get(DBConstants.dws_tfc_state_rid_tpwkd_index_m)
                .stream()
                .collect(Collectors.groupingBy(RidIndex::getStepIndex));
        cache2 = dbQuery.<InterFridSeqTurnDirIndex>get(DBConstants.dws_tfc_state_signinterfridseq_tpwkd_delaydur_m)
                .stream()
                .collect(Collectors.groupingBy(InterFridSeqTurnDirIndex::getStepIndex));
    }

    private Row[] hashJoin(Row row,
                         Map<String, RidInfo> ridInfoMap, // left join
                         Map<String, InterLaneInfo> interLaneInfoMap, // inner join
                         Map<String, SigninterfridseqIndex> seqNdIndexMap, // left join
                         Map<String, PhaseInfo> phaseInfoMap) { // left join
        // position
        String rid = (String) row.getField(1);
        // metric
        Double travelTime = (Double) row.getField(2);
        Double speed = (Double) row.getField(3);
        Double reliabilityCode = (Double) row.getField(4);
        // time
        Long stepIndex1mi = (Long) row.getField(5);
        Long stepIndex10mi = (Long) row.getField(6);
        Long dayOfWeek = (Long) row.getField(7);
        Long timestamp = (Long) row.getField(8);

        RidInfo ridInfo = ridInfoMap.get(rid); // ridInfo
        InterLaneInfo interLaneInfo = interLaneInfoMap.get(rid); // interLaneInfo
        if (interLaneInfo == null) {
            return null;
        }

        String interId = interLaneInfo.getInterId();
        String turnDirNoList = interLaneInfo.getTurnDirNoList();
        String[] turnDirNoSplits = turnDirNoList.split(",");
        Row[] rows = new Row[turnDirNoSplits.length];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = new Row(17);

            rows[i].setField(0, -1); // 适配tag
            rows[i].setField(1, interId);
            rows[i].setField(2, rid);
            Long turnDirNo = Long.parseLong(turnDirNoSplits[i]);
            rows[i].setField(3, turnDirNo);
            rows[i].setField(4, stepIndex1mi);
            rows[i].setField(5, stepIndex10mi);
            rows[i].setField(6, dayOfWeek);
            rows[i].setField(7, timestamp);
            rows[i].setField(8, travelTime);
            rows[i].setField(9, speed);
            rows[i].setField(10, reliabilityCode);
            Double length = ridInfo != null ? ridInfo.getLength() : 0d;
            rows[i].setField(11, length);
            rows[i].setField(12, interLaneInfo.getLaneId());

            String keyInterRidTurn = CityBrainUtil.concat(interId, rid, turnDirNo);
            SigninterfridseqIndex signinterfridseqIndex = seqNdIndexMap.get(keyInterRidTurn); // SigninterfridseqIndex
            PhaseInfo phaseInfo = phaseInfoMap.get(keyInterRidTurn); // PhaseInfo

            String fRidseq = "";
            Double benchmarkNostopTravelTime = 0d;
            if (signinterfridseqIndex != null) {
                fRidseq = signinterfridseqIndex.getfRidseq();
                benchmarkNostopTravelTime = signinterfridseqIndex.getBenchmarkNostopTravelTime();
            }
            rows[i].setField(13, fRidseq);
            rows[i].setField(14, benchmarkNostopTravelTime);

            String phasePlanId = "";
            String phaseName = "";
            if (phaseInfo != null) {
                phasePlanId = phaseInfo.getPhasePlanId();
                phaseName = phaseInfo.getPhaseName();
            }
            rows[i].setField(15, phasePlanId);
            rows[i].setField(16, phaseName);
        }
        return rows;
    }

    private void loadBaseData() {
        if (this.executorService == null) {
            return;
        }

        DBQuery dbQuery = new DBQuery(this.executorService);
        dbQuery.add(
                dwd_tfc_bas_rdnet_rid_info,
                sql_dwd_tfc_bas_rdnet_rid_info,
                RidInfo.class,
                new ArrayList<String>() {
                    {
                        add("rid");
                        add("length");
                    }
                }
        );
        dbQuery.add(
                dwd_tfc_rltn_wide_inter_lane,
                sql_dwd_tfc_rltn_wide_inter_lane,
                InterLaneInfo.class,
                new ArrayList<String>() {
                    {
                        add("interId");
                        add("rid");
                        add("turnDirNoList");
                        add("laneId");
                    }
                }
        );
        dbQuery.add(
                dws_tfc_state_signinterfridseq_nd_index_m,
                sql_dws_tfc_state_signinterfridseq_nd_index_m,
                SigninterfridseqIndex.class,
                new ArrayList<String>() {
                    {
                        add("interId");
                        add("fRid");
                        add("turnDirNo");
                        add("fRidseq");
                        add("benchmarkNostopTravelTime");
                    }
                });
        dbQuery.add(
                dwd_tfc_ctl_signal_phasedir,
                sql_dwd_tfc_ctl_signal_phasedir,
                PhaseInfo.class,
                new ArrayList<String>() {
                    {
                        add("interId");
                        add("fRid");
                        add("turnDirNo");
                        add("phasePlanId");
                        add("phaseName");
                    }
                }
        );

        dbQuery.execute();

        dbQuery.<RidInfo>get(dwd_tfc_bas_rdnet_rid_info)
                .forEach(op -> {
                    String rid = op.getRid();

                    ridInfoMap.put(rid, op);
                });
        dbQuery.<InterLaneInfo>get(dwd_tfc_rltn_wide_inter_lane)
                .forEach(op -> {
                    String rid = op.getRid();

                    interLaneInfoMap.put(rid, op);
                });
        dbQuery.<SigninterfridseqIndex>get(dws_tfc_state_signinterfridseq_nd_index_m)
                .forEach(op -> {
                    String keyInterFridTurn = CityBrainUtil.concat(op.getInterId(), op.getfRid(), op.getTurnDirNo());

                    seqNdIndexMap.put(keyInterFridTurn, op);
                });
        dbQuery.<PhaseInfo>get(dwd_tfc_ctl_signal_phasedir)
                .forEach(op -> {
                    String keyInterFridTurn = CityBrainUtil.concat(op.getInterId(), op.getfRid(), op.getTurnDirNo());

                    phaseInfoMap.put(keyInterFridTurn, op);
                });
    }

    @Override
    public void close() throws Exception {
        super.close();

        System.out.printf(
                "JdbcSupportConstructCnt=%d," +
                "requestCnt=%d," +
                "requestFailedCnt=%d\n",
                JdbcSupport.cnt,
                DBConnection.requestCnt,
                DBConnection.requestFailedCnt);

        if (this.executorService != null) {
            this.executorService.shutdown();
        }

        // metric
        if (metricPS != null) {
            metricPS.close();
        }
        if (metricConnection != null) {
            metricConnection.close();
        }
    }
}
