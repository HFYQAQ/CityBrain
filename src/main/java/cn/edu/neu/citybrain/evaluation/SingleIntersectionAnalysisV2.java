package cn.edu.neu.citybrain.evaluation;

import cn.edu.neu.citybrain.db.DBConstants;
import cn.edu.neu.citybrain.db.DBQuery;
import cn.edu.neu.citybrain.dto.*;
import cn.edu.neu.citybrain.dto.my.TurnGranularityInfo;
import cn.edu.neu.citybrain.util.CityBrainUtil;
import cn.edu.neu.citybrain.util.SignalOptConstant;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class SingleIntersectionAnalysisV2 {
    private ExecutorService executorService;

    private Map<String, List<TurnGranularityInfo>> turnGranularityInfoMap;
    private Map<String, Set<PhaseInfo>> interAndDirMapPhaseNo;
    private Map<String, Set<String>> interLaneMap;

    private Map<String, Double> ridHistTraveltimeMap = new HashMap<>(); // dws_tfc_state_rid_tpwkd_index_m
    private Map<String, Double> interFridSeqTurndirHistIndex = new HashMap<>(); // dws_tfc_state_signinterfridseq_tpwkd_delaydur_m
    private Map<String, List<InterSignalOperPlan>> interSignalOperPlansM = new HashMap<>(); // dwd_tfc_ctl_intersignal_oper_rt
    private Map<Object, InterfridlaneTpMultiflowDto> laneFlowMap = new HashMap<>(); // dws_tfc_trl_interfridlane_tp_smtmultiflow_rt

    private Random random = new Random(System.currentTimeMillis());

    /**
     * 当前计算的参数信息
     */
    ParamIntersectionAnalaysis params = new ParamIntersectionAnalaysis();
    /**
     * 当前计算的时间片信息
     */
    private TimeSlice currTimeSlice = null;

    //////////////////////////////////////////////算法内置默认参数阈值////////////////////////////////////////////////////////////
    /**
     * 历史速度和实时速度比值的上限值
     */
    private static double MAX_HIST_REALTIME_SPEED_RATIO = 100;
    /**
     * 历史速度和实时速度比值的下限值
     */
    private static double MIN_HIST_REALTIME_SPEED_RATIO = 0.01;
    /**
     * 使用默认速度填补速度的置信度默认值
     */
    private static final double DEFAULT_FILLSPEED_RELIABILITY_CODE = 20;
    /**
     * 使用历史速度填补实时速度的置信度
     */
    private static final double HIST_FILLSPEED_RELIABILITY_CODE = 49;
    /**
     * 历史速度的置信度默认值 历史表中没有带出，所以暂时使用默认值
     */
    private static final double HIST_SPEED_RELIABILITY_CODE = 80;
    /**
     * 夜间延误时间最大值
     */
    private int defaultLimitDelay = 60;
    /**
     * 默认的红灯时间
     */
    private static double DEFAULT_RED_TIME = 120;
    /**
     * 默认的绿灯时间
     */
    private static double DEFAULT_SPLIT_TIME = 40;
    /**
     * 使用默认红灯/绿灯时间的置信度
     */
    private static double DEFAULT_SPLIT_RELIABILITY = 20;
    /**
     * 默认限制夜间停车次数
     */
    private static Double DEFAULT_LIMIT_STOPCNT = 0.5D;

    private String table1;
    private String table2;
    String sql_table1;
    String sql_table2;

    public SingleIntersectionAnalysisV2(ExecutorService executorService, String table1, String table2, String sqlTable1, String sqlTable2) {
        this.executorService = executorService;
        this.table1 = table1;
        this.table2 = table2;
        this.sql_table1 = sqlTable1;
        this.sql_table2 = sqlTable2;
    }

    public Map<String, List<fRidSeqTurnDirIndexDTO>> evaluate(long stepIndex1mi, long stepIndex10mi, long dayOfWeek, long timestamp,
                                                 Map<String, List<TurnGranularityInfo>> turnGranularityInfoMap,
                                                 Map<String, Set<PhaseInfo>> interAndDirMapPhaseNo,
                                                 Map<String, Set<String>> interLaneMap) {
        this.turnGranularityInfoMap = turnGranularityInfoMap;
        this.interAndDirMapPhaseNo = interAndDirMapPhaseNo;
        this.interLaneMap = interLaneMap;

        Map<String, List<fRidSeqTurnDirIndexDTO>> allResult = null;

        try {
            prepareData(stepIndex1mi, stepIndex10mi, dayOfWeek, timestamp);
            allResult = caculate();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_MAP;
        }

        return allResult;
    }

    private void prepareData(long stepIndex1mi, long stepIndex10mi, long dayOfWeek, long timestamp) {
        currTimeSlice = new TimeSlice(stepIndex1mi, "1mi"); // HFY: 不知道tp怎么传过来，姑且设置个1mi吧

        DBQuery dbQuery = new DBQuery(executorService);
        // 指标1
        dbQuery.add(
                table1,
                sql_table1,
                RidIndex.class,
                new ArrayList<String>() {
                    {
                        add("rid");
                        add("travelTime");
                    }
                },
                dayOfWeek,
                stepIndex10mi);
        dbQuery.add(
                table2,
                sql_table2,
                InterFridSeqTurnDirIndex.class,
                new ArrayList<String>() {
                    {
                        add("fRid");
                        add("turnDirNo");
                        add("avgTraceTravelTime");
                    }
                },
                dayOfWeek,
                stepIndex10mi);
//        // 指标3
//        dbQuery.add(
//                DBConstants.dwd_tfc_ctl_intersignal_oper_rt,
//                DBConstants.sql_dwd_tfc_ctl_intersignal_oper_rt,
//                InterSignalOperPlan.class,
//                new ArrayList<String>() {
//                    {
//                        add("interId");
//                        add("phasePlanId");
//                        add("cycleStartTime");
//                        add("phaseName");
//                        add("splitTime");
//                        add("cycleTime");
//                        add("greenTime");
//                    }
//                },
//                timestamp);
        // 指标4
//        dbQuery.add(
//                DBConstants.dws_tfc_trl_interfridlane_tp_smtmultiflow_rt,
//                DBConstants.sql_dws_tfc_trl_interfridlane_tp_smtmultiflow_rt,
//                InterfridlaneTpMultiflowDto.class,
//                new ArrayList<String>() {
//                    {
//                        add("interId");
//                        add("rid");
//                        add("laneId");
//                        add("flow");
//                        add("reliabilityCode");
//                    }
//                },
//                stepIndex1mi / 5);

        dbQuery.execute();

        // 指标1
        dbQuery.<RidIndex>get(table1)
                .forEach(op -> ridHistTraveltimeMap.put(op.getRid(), op.getTravelTime()));
        dbQuery.<InterFridSeqTurnDirIndex>get(table2)
                .forEach(op -> {
                    String key = CityBrainUtil.concat(op.getfRid(), op.getTurnDirNo());
                    interFridSeqTurndirHistIndex.put(key, op.getAvgTraceTravelTime());
                });
//        // 指标3
//        interSignalOperPlansM = dbQuery.<InterSignalOperPlan>get(DBConstants.dwd_tfc_ctl_intersignal_oper_rt)
//                .stream()
//                .collect(Collectors.groupingBy(InterSignalOperPlan::getInterId));
//        // 指标4
//        dbQuery.<InterfridlaneTpMultiflowDto>get(DBConstants.dws_tfc_trl_interfridlane_tp_smtmultiflow_rt)
//                .forEach(op -> laneFlowMap.put(op.getLaneId(), op));
    }

    private Map<String, List<fRidSeqTurnDirIndexDTO>> caculate() throws Exception {
        Map<String, List<fRidSeqTurnDirIndexDTO>> allResults = new HashMap<>();

        for (Map.Entry<String, List<TurnGranularityInfo>> entry : turnGranularityInfoMap.entrySet()) {
            List<fRidSeqTurnDirIndexDTO> results = new ArrayList<>();

            List<TurnGranularityInfo> list = entry.getValue();
            for (TurnGranularityInfo turnGranularityInfo : list) {
                fRidSeqTurnDirIndexDTO curResult = new fRidSeqTurnDirIndexDTO(turnGranularityInfo.getInterId(), turnGranularityInfo.getRid(), turnGranularityInfo.getTurnDirNo());

                // 指标1
                calTravelTime(turnGranularityInfo, curResult);
                // 指标2
                calDelay(turnGranularityInfo, curResult);
                // 指标3
                calOneDirectionStopCnt(turnGranularityInfo, curResult);
                // 指标4
                calOneDirectionQueue(turnGranularityInfo, curResult);

                results.add(curResult);
            }
            allResults.put(entry.getKey(), results);
        }
        return allResults;
    }

    private void calTravelTime(TurnGranularityInfo turnGranularityInfo, fRidSeqTurnDirIndexDTO curResult) {
        double traveltimeHistRidseq = sumRidseqHistoryTraveltime(turnGranularityInfo);
        double traveltimeRTRidseq = sumRidseqRealtimeTraveltime(turnGranularityInfo);

        String key = CityBrainUtil.concat(turnGranularityInfo.getRid(), turnGranularityInfo.getTurnDirNo());
        Double avgTraceTravelTime = interFridSeqTurndirHistIndex.get(key);
        double traveltimeHistTurn = avgTraceTravelTime != null ? avgTraceTravelTime : traveltimeRTRidseq;

        double traveltimeRTTurn = traveltimeHistTurn / traveltimeHistRidseq * traveltimeRTRidseq;
        curResult.setTravelTime(traveltimeRTTurn);
    }

    private double sumRidseqHistoryTraveltime(TurnGranularityInfo turnGranularityInfo) {
        double traveltimeHistRidseq = 0.0d;
        String[] fRids = turnGranularityInfo.getfRidseq().split("#");
        for (String fRid : fRids) {
            if (ridHistTraveltimeMap.containsKey(fRid)) {
                traveltimeHistRidseq += ridHistTraveltimeMap.get(fRid);
            } else {
                traveltimeHistRidseq += getOneRidBenchmarkTravelTime(fRid);
            }
        }
        return traveltimeHistRidseq;
    }

    private double getOneRidBenchmarkTravelTime(String subRid) {
        return random.nextInt(70) + random.nextDouble();
//        return 300.0 / 40.0 * 3.6;
    }

    private double sumRidseqRealtimeTraveltime(TurnGranularityInfo turnGranularityInfo) {
        double traveltimeRTRidseq = 0.0d;
        String[] fRids = turnGranularityInfo.getfRidseq().split("#");
        for (String fRid : fRids) {
            if (turnGranularityInfoMap.containsKey(fRid)) {
                traveltimeRTRidseq += turnGranularityInfoMap.get(fRid).get(0).getRidGranularityInfo().getTravelTime();
            } else if (ridHistTraveltimeMap.containsKey(fRid)) {
                traveltimeRTRidseq += ridHistTraveltimeMap.get(fRid);
            } else {
                traveltimeRTRidseq += getOneRidBenchmarkTravelTime(fRid);
            }
        }
        return traveltimeRTRidseq;
    }

    private void calDelay(TurnGranularityInfo turnGranularityInfo, fRidSeqTurnDirIndexDTO curResult) {
        double standardTraveltime = turnGranularityInfo.getBenchmarkNostopTravelTime();
        double delayTime = Math.max(0, curResult.getTravelTime() - standardTraveltime);
        curResult.setBenchmarkTravelTime(standardTraveltime);
        curResult.setDelay(delayTime);
    }

    /**
     * 计算转向停车次数
     */
    private void calOneDirectionStopCnt(TurnGranularityInfo turnGranularityInfo, fRidSeqTurnDirIndexDTO curResult) {
        //获取当前转向的周期绿信比数据
        CycleSplit cycleSplit = getCycleSplit(turnGranularityInfo, curResult);

        //根据配时数据计算停车次数
        double numOfStop = calStopCnt(cycleSplit.getRedTime(), curResult.getDelay());

//        String stopCntSrcType = CommonFunction.getSrcType(StaticConstantClass.DEVCTYPENO_NONE);
        curResult.setStopCnt(numOfStop);
//        if(curResult.isStopcntValid()) {
//            String signalOperSrcType = CommonFunction.getSrcType(StaticConstantClass.DEVCTYPENO_SIGNAL);
//            stopCntSrcType = CommonFunction.StrBinaOr(curResult.getDelaySrcType(), signalOperSrcType);
//            curResult.getAlgDetail().addStopCntSource(curResult.getAlgDetail().getDelayDurSource());
//            curResult.getAlgDetail().setStopCntAlgType(SignalOptConstant.ALG_TYPE_STAT);
//        }
//        curResult.setStopCntSrcType(stopCntSrcType);
        //修正停车次数
        boolean isCorrectStopcnt = isCorrectStopcnt(curResult);
        if (isCorrectStopcnt) {
            correctStopcnt(cycleSplit.getRedTime(), curResult);
        }
    }

    /**
     * 计算当前转向的排队长度
     */
    private void calOneDirectionQueue(TurnGranularityInfo turnGranularityInfo, fRidSeqTurnDirIndexDTO curResult) {
        String keyInterFridTurndir = CityBrainUtil.concat(turnGranularityInfo.getInterId(), turnGranularityInfo.getRid(), turnGranularityInfo.getTurnDirNo());
        Set<String> interLaneList = this.interLaneMap.getOrDefault(keyInterFridTurndir, Collections.emptySet());
        Map<String, ValueAndReliability> laneFlow = new HashMap<>();
        interLaneList.forEach(laneId -> laneFlow.put(laneId, new ValueAndReliability(0D, 0D)));

        double finalQueue = -1.0;
//        String srcType = CommonFunction.getSrcType(-1L);
        String flowSrcType = "";
        if (curResult.isStopcntValid()) {
            //获取当前转向的周期绿信比数据
            CycleSplit cycleSplit = getCycleSplit(turnGranularityInfo, curResult);

//            srcType = curResult.getStopCntSrcType();
            if (!interLaneList.isEmpty()) {
                //计算当前转向所有车道的流量
                for (String laneId : interLaneList) {
                    InterfridlaneTpMultiflowDto currLaneFlow = this.laneFlowMap.get(laneId);
                    if (currLaneFlow != null && currLaneFlow.getFlow() > 0) {
                        ValueAndReliability var = new ValueAndReliability(currLaneFlow.getFlow(), currLaneFlow.getReliabilityCode());
                        ValueAndReliability old = laneFlow.get(laneId);
                        if (old == null || var.getReliability() > old.getReliability()) {
                            laneFlow.put(laneId, var);
                        }

//                        if (flowSrcType.isEmpty()) {
//                            flowSrcType = currLaneFlow.getMultiType();
//                        } else {
//                            flowSrcType = CommonFunction.StrBinaOr(flowSrcType, currLaneFlow.getMultiType());
//                        }
                    }
                }
            }

            double volume = laneFlow.values().stream().mapToDouble(o -> o.getValue()).sum();

            //排队长度=停车次数*平均周期车道流量
            if (!laneFlow.isEmpty() && volume > StaticConstantClass.ZERO_BOUNDARY) {
                //将流量转换为周期流量
                double cycleVolume = volume * cycleSplit.getCycleTime() / currTimeSlice.getWinSize();
                finalQueue = curResult.getStopCnt() * params.getDefaultCarLength() * cycleVolume / laneFlow.size();
//                srcType = CommonFunction.StrBinaOr(srcType, flowSrcType);
//                curResult.getAlgDetail().addQueueLenSource(SignalOptConstant.SOURCE_DETECTOR);
            } else {
                double greenLostTime = params.getDefaultGreenLostTime();
                if(cycleSplit.getSplitTime() <= greenLostTime) {
                    greenLostTime = 0;
                }

                double defaultSatu = getDefaultSaturation(currTimeSlice, curResult.getTurnDirNo());
                finalQueue = curResult.getStopCnt() * defaultSatu * (cycleSplit.getSplitTime() - greenLostTime) * 1.0 / getRatioOfQueueNum(turnGranularityInfo.getTurnDirNo()) * params.getDefaultCarLength();

            }

//            curResult.getAlgDetail().addQueueLenSource(curResult.getAlgDetail().getStopCntSource());
//            curResult.getAlgDetail().setQueueLenAlgType(SignalOptConstant.ALG_TYPE_STAT);
        }

        //排队长度不能超过rid序列的长度
        finalQueue = Math.min(finalQueue, turnGranularityInfo.getfRidseqLen());
//        finalQueue = Math.min(finalQueue, 1);

//        curResult.setQueueSrcType(srcType);
        curResult.setQueue(finalQueue);
    }

    /**
     * 获取排队长度计算时的默认饱和度
     */
    private Double getDefaultSaturation(TimeSlice currTimeSlice, long turnDirNo) {
        //早晚高峰时间段饱和度使用饱和状态的值
        boolean isRushTime = false;
        TimePeriod morningPeriod = params.getDayPeriods().get(PeriodType.MorningRush);
        if(null != morningPeriod) {
            isRushTime = morningPeriod.isBelong(currTimeSlice.getStepIndex(), currTimeSlice.getWinSize());
        }
        TimePeriod eveningPeriod = params.getDayPeriods().get(PeriodType.EveningRush);
        if(null != eveningPeriod) {
            isRushTime = eveningPeriod.isBelong(currTimeSlice.getStepIndex(), currTimeSlice.getWinSize());
        }

        return isRushTime ? 0.8 : 0.4;
    }

    /**
     * 车道车头时距，直行按照2.25，转弯按照2.4
     */
    private double getRatioOfQueueNum(long turnDir) {
        if (turnDir == SignalOptConstant.TURN_DIR_NO_STRAIGHT) {
            return 2.25;
        } else {
            return 2.4;
        }
    }


    /**
     * 获取当前转向的红灯时间
     */
    private CycleSplit getCycleSplit(TurnGranularityInfo turnGranularityInfo, fRidSeqTurnDirIndexDTO curResult) {
        List<InterSignalOperPlan> interSignalOperPlans = interSignalOperPlansM.get(turnGranularityInfo.getInterId());
        CycleSplit cycleSplit = new CycleSplit();
        cycleSplit.setSource("default");
        if(params.isUseDefaultRedTime()) {
            cycleSplit.setSplitTime(0D);
            cycleSplit.setRedTime(DEFAULT_RED_TIME);
            cycleSplit.setSplitTime(DEFAULT_SPLIT_TIME);
            cycleSplit.setCycleTime(DEFAULT_RED_TIME + DEFAULT_SPLIT_TIME);
//            cycleSplit.setReliabilityCode(DEFAULT_SPLIT_RELIABILITY);
        } else {
            cycleSplit.setSplitTime(-1.0);
            cycleSplit.setRedTime(-1.0);
            cycleSplit.setSplitTime(-1.0);
            cycleSplit.setCycleTime(-1.0);
//            cycleSplit.setReliabilityCode(100D);
        }

        double actualSplit = 0.0;
        double actualCycle = 0.0;

        String keyInterFridTurndir = CityBrainUtil.concat(turnGranularityInfo.getInterId(), turnGranularityInfo.getRid(), turnGranularityInfo.getTurnDirNo());
        //当前路口的相位通行方向
        // bug--by xfyue 不知该使用哪个阶段
        if (interAndDirMapPhaseNo.containsKey(keyInterFridTurndir)) {
            Set<PhaseInfo> interPhaseList = interAndDirMapPhaseNo.get(keyInterFridTurndir);
            if (null != interSignalOperPlans && interSignalOperPlans.size() > 0 && interPhaseList != null && interPhaseList.size() > 0) {
                for (InterSignalOperPlan oneSignalOper : interSignalOperPlans) {
                    PhaseInfo currOperPhase = oneSignalOper.getPhaseInfo();
                    for (PhaseInfo onePhaseInfo : interPhaseList) {
                        if (currOperPhase.equals(onePhaseInfo)) {
                            actualSplit += oneSignalOper.getSplitTime();
                            actualCycle = oneSignalOper.getCycleTime();
                        }
                    }
                }
                //红灯时间 > 全红时间（3秒） * 相位数
                if (actualSplit > 0 && actualCycle > 0 && actualCycle - actualSplit > 3 * interSignalOperPlans.size()) {
                    double redTime = calRedTime(actualCycle, actualSplit, params.getDefaultGreenLostTime());
                    cycleSplit.setRedTime(redTime);
                    cycleSplit.setSplitTime(actualSplit);
                    cycleSplit.setCycleTime(actualCycle);
                    cycleSplit.setSource(SignalOptConstant.SOURCE_SIGNAL);
                    curResult.getAlgDetail().addStopCntSource(SignalOptConstant.SOURCE_SIGNAL);
                }
            }
        }

        return cycleSplit;
    }

    /**
     * 计算红灯时间
     */

    private Double calRedTime(Double cycleTime, Double splitTime, double greenLostTime){
        ValueAndReliability result = new ValueAndReliability(-1.0, -1.0);
        double redTime = 0.0d;
        if(cycleTime > 0 && splitTime > 0){
            redTime = cycleTime - splitTime + greenLostTime;
            if(redTime < greenLostTime){
                redTime = greenLostTime;
            }
        }
        return redTime;
    }

    /**
     * 计算停车次数
     */
    private Double calStopCnt(double redTime, double delayTime) {
        Double numOfStop = -1.0;
        if (redTime > 0) {
            numOfStop = 1.0 * delayTime / redTime;
        }
        if(numOfStop > params.getMaxStopcnt()){
            numOfStop = params.getMaxStopcnt();
        }
        return numOfStop;
    }

    /**
     * 修正夜间停车次数
     */
    private void correctStopcnt(Double redTime, fRidSeqTurnDirIndexDTO currFridSeqTurnDirRtIndex) {
        currFridSeqTurnDirRtIndex.setStopCnt(DEFAULT_LIMIT_STOPCNT);
        if(redTime > 0) {
            double delay = redTime * currFridSeqTurnDirRtIndex.getStopCnt();
            currFridSeqTurnDirRtIndex.setDelay(delay);
            currFridSeqTurnDirRtIndex.setTravelTime(delay + currFridSeqTurnDirRtIndex.getBenchmarkTravelTime());
        }
    }

    /**
     * 是否需要修正停车次数
     */
    private boolean isCorrectStopcnt(fRidSeqTurnDirIndexDTO ridTurnDirIndex) {
        double numOfStop = ridTurnDirIndex.getStopCnt();
        boolean isCorrect = false;
        if (numOfStop > DEFAULT_LIMIT_STOPCNT) {
            // 2018.9.2 by zz 修正凌晨延误时间
            if (params.getLimitStartIndex() > params.getLimitEndIndex()) {
                if ((currTimeSlice.getStepIndex() >= params.getLimitStartIndex() && currTimeSlice.getStepIndex() <= params.getMaxStepIndex()) || (currTimeSlice.getStepIndex() >= 0 && currTimeSlice.getStepIndex() <= params.getLimitEndIndex())) {
                    isCorrect = true;
                }
            } else {
                if (currTimeSlice.getStepIndex() >= params.getLimitStartIndex() && currTimeSlice.getStepIndex() <= params.getLimitEndIndex()) {
                    isCorrect = true;
                }
            }
        }
        return isCorrect;
    }


    /**
     * 是否需要修正延误时间
     */
    private boolean isCorrectDelay(fRidSeqTurnDirIndexDTO ridTurnDirIndex) {
        double delayTime = ridTurnDirIndex.getDelay();
        boolean isCorrect = false;
        //是否需要修正延误时间
        if (delayTime > this.defaultLimitDelay) {
            // 2018.9.2 by zz 修正凌晨延误时间
            if (params.getLimitStartIndex() > params.getLimitEndIndex()) {
                if ((currTimeSlice.getStepIndex() >= params.getLimitStartIndex() && currTimeSlice.getStepIndex() <= params.getMaxStepIndex()) || (currTimeSlice.getStepIndex() >= 0 && currTimeSlice.getStepIndex() <= params.getLimitEndIndex())) {
                    isCorrect = true;
                }
            } else {
                if (currTimeSlice.getStepIndex() >= params.getLimitStartIndex() && currTimeSlice.getStepIndex() <= params.getLimitEndIndex() && delayTime > this.defaultLimitDelay) {
                    isCorrect = true;
                }
            }
        }
        return isCorrect;
    }
}
