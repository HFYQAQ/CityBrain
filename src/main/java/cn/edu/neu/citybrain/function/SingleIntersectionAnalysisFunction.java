package cn.edu.neu.citybrain.function;

import cn.edu.neu.citybrain.db.DBConnection;
import cn.edu.neu.citybrain.db.DBQuery;
import cn.edu.neu.citybrain.db.JdbcSupport;
import cn.edu.neu.citybrain.dto.*;
import cn.edu.neu.citybrain.dto.my.RidGranularityInfo;
import cn.edu.neu.citybrain.dto.my.TurnGranularityInfo;
import cn.edu.neu.citybrain.evaluation.SingleIntersectionAnalysisV2;
import cn.edu.neu.citybrain.util.CityBrainUtil;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.mix.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.mix.api.windowing.windows.TimeWindow;
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

public class SingleIntersectionAnalysisFunction extends ProcessWindowFunction<Row, fRidSeqTurnDirIndexDTO, Tuple, TimeWindow> {
    private ExecutorService executorService;
    private boolean isExhibition;

    Map<String, List<SigninterfridseqIndex>> seqMap = new HashMap<>(); // 存放<rid, turnDirNo> -> ridseq映射的静态数据
    Map<String, RidInfo> ridIndexM = new HashMap<>(); // 存放 rid -> benchmarkNostopSpeed/映射的静态数据
    Map<String, SigninterfridseqIndex> fridseqIndexM = new HashMap<>();

    // metric
    private final String METRIC_SQL = "insert into statistic(job_name,subtask_index,dt,step_index_1mi,amount,duration,is_exhibition) values(?,?,?,?,?,?,?)";
    private Connection metricConnection;
    private PreparedStatement metricPS;

    public SingleIntersectionAnalysisFunction(boolean isExhibition) {
        this.isExhibition = isExhibition;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

        //metric
        metricConnection = DBConnection.getConnection();
        metricPS = metricConnection.prepareStatement(METRIC_SQL);
    }

    @Override
    // [0]tag, [1]inter_id, [2]rid, [3]turn_dir_no, [4]step_index_1mi, [5]step_index_10mi, [6]day_of_week, [7]timestamp
    // [8]travel_time, [9]speed, [10]reliability_code
    // [11]len
    // [12]lane_id
    // [13]f_ridseq, [14]benchmark_nostop_travel_time_3m
    // [15]phase_plan_id, [16]phase_name
    public void process(Tuple tuple, Context context, Iterable<Row> iterable, Collector<fRidSeqTurnDirIndexDTO> collector) throws Exception {
        long beforeProcess = System.currentTimeMillis();
        long amount = 0;

        Map<String, List<TurnGranularityInfo>> turnGranularityInfoMap = new HashMap<>();
        Map<String, Set<PhaseInfo>> interAndDirMapPhaseNo = new HashMap<>();
        Map<String, Set<String>> interLaneMap = new HashMap<>();
        Map<String, Double> ridHistTraveltimeMap = new HashMap<>();
        Map<String, Double> interFridSeqTurndirHistIndex = new HashMap<>();

        Set<String> state = new HashSet<>(); // 由于某些原因，需要去重

        Long stepIndex1mi = 0L;
        Long stepIndex10mi = 0L;
        Long dayOfWeek = 0L;
        Long timestamp = 0L;
        for (Row row : iterable) {
            amount++;

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
            Double benchmarkNostopTravelTime3m = Double.parseDouble((String) row.getField(14));

            // dwd_tfc_ctl_signal_phasedir
            String phasePlanID = (String) row.getField(15);
            String phaseName = (String) row.getField(16);

            // dws_tfc_state_rid_tpwkd_index_m
            String avgTravelTimeStr = (String) row.getField(17);
            Double avgTravelTime = (avgTravelTimeStr == null || avgTravelTimeStr.length() == 0) ? 0d : Double.parseDouble(avgTravelTimeStr);

            //dws_tfc_state_signinterfridseq_tpwkd_delaydur_m
            String avgTraceTravelTimeStr = (String) row.getField(18);
            Double avgTraceTravelTime = (avgTraceTravelTimeStr == null || avgTraceTravelTimeStr.length() == 0) ? 0d : Double.parseDouble(avgTraceTravelTimeStr);

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
            phaseInfo.setTurnDirNo(turnDirNo.toString());
            interAndDirMapPhaseNo.putIfAbsent(keyInterFridTurndir, new HashSet<>());
            interAndDirMapPhaseNo.get(keyInterFridTurndir).add(phaseInfo);

            // interLaneInfoList
            interLaneMap.putIfAbsent(keyInterFridTurndir, new TreeSet<>());
            interLaneMap.get(keyInterFridTurndir).add(laneId);

            // ridHistTraveltimeMap
            ridHistTraveltimeMap.put(fRid, avgTravelTime);

            // interFridSeqTurndirHistIndex
            String keyFridTurn = CityBrainUtil.concat(fRid, turnDirNo);
            interFridSeqTurndirHistIndex.put(keyFridTurn, avgTraceTravelTime);
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

        SingleIntersectionAnalysisV2 estimator = new SingleIntersectionAnalysisV2(this.executorService);
        List<fRidSeqTurnDirIndexDTO> results = estimator.evaluate(stepIndex1mi, stepIndex10mi, dayOfWeek, timestamp,
                turnGranularityInfoMap,
                interAndDirMapPhaseNo,
                interLaneMap,
                ridHistTraveltimeMap,
                interFridSeqTurndirHistIndex);

        long afterProcess = System.currentTimeMillis();

        // metric
        int taskIdx = getRuntimeContext().getIndexOfThisSubtask();
        long duration = afterProcess - beforeProcess;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dt = sdf.format(new Date(timestamp));
        upload(taskIdx, dt, stepIndex1mi, amount, duration, isExhibition);

        for (fRidSeqTurnDirIndexDTO fRidSeqTurnDirIndexDTO : results) {
            collector.collect(fRidSeqTurnDirIndexDTO);
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

    private void loadBaseData() {
        if (this.executorService == null) {
            return;
        }
        // 指标1，查询seq
        DBQuery dbQuery = new DBQuery(this.executorService);
        dbQuery.add(
                dws_tfc_state_signinterfridseq_tpwkd_delaydur_m,
                sql_dws_tfc_state_signinterfridseq_tpwkd_delaydur_m_indicator1,
                SigninterfridseqIndex.class,
                new ArrayList<String>() {
                    {
                        add("fRid");
                        add("turnDirNo");
                        add("fRidseq");
                    }
                }
        );
        // 指标2 --- 默认值
        dbQuery.add(
                dws_tfc_state_rid_nd_index_m,
                sql_dws_tfc_state_rid_nd_index_m,
                RidInfo.class,
                new ArrayList<String>() {
                    {
                        add("rid");
                        add("benchmarkTravelTime");
                        add("benchmarkNostopTravelTime");
                    }
                });
        // 指标2
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
        dbQuery.execute();

        seqMap = dbQuery.<SigninterfridseqIndex>get(dws_tfc_state_signinterfridseq_tpwkd_delaydur_m)
                .stream()
                .collect(Collectors.groupingBy(SigninterfridseqIndex::getfRid));

        // ridIndexM
        dbQuery.<RidInfo>get(dws_tfc_state_rid_nd_index_m)
                .forEach(op -> {
                    String key = op.getRid();
                    ridIndexM.put(key, op);
                });
//        dbQuery.<RidInfo>get(dwd_tfc_bas_rdnet_rid_info)
//                .forEach(op -> {
//                    List<RidInfo> seq = ridIndexM.get(op.getRid());
//                    RidInfo indexM = seq != null ? seq.get(0) : null;
//
//                    Double speed = indexM != null ? indexM.getBenchmarkSpeed() : ConstantUtil.DEFAULT_RID_SPEED;
//                    Double travelTime = indexM != null ? indexM.getBenchmarkTravelTime() : op.getLength() / ConstantUtil.DEFAULT_RID_SPEED;
//                    Double nostopSpeed = indexM != null ? indexM.getBenchmarkNostopSpeed() : ConstantUtil.DEFAULT_RID_SPEED;
//                    op.setBenchmarkSpeed(speed);
//                    op.setBenchmarkTravelTime(travelTime);
//                    op.setBenchmarkNostopSpeed(nostopSpeed);
//
//                    this.allRidInfo.put(op.getRid(), op);
//                });
        // fridseqIndexM
        dbQuery.<SigninterfridseqIndex>get(dws_tfc_state_signinterfridseq_nd_index_m)
                .forEach(op -> {
                    String key = CityBrainUtil.concat(op.getfRid(), op.getTurnDirNo());
                    fridseqIndexM.put(key, op);
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
