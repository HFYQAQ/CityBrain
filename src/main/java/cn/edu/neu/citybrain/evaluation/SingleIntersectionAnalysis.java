package cn.edu.neu.citybrain.evaluation;

import cn.edu.neu.citybrain.db.DBQuery;
import cn.edu.neu.citybrain.dto.*;
import cn.edu.neu.citybrain.util.SignalOptConstant;

import java.util.*;
import java.util.concurrent.ExecutorService;

import static cn.edu.neu.citybrain.db.DBConstants.*;
import static cn.edu.neu.citybrain.util.CityBrainUtil.concat;

public class SingleIntersectionAnalysis {
    private ExecutorService executorService;

    // 实时数据
    private Map<String, RidIndex> ridIndexRTMap = new HashMap<>();

    // 基础
    private Map<String, List<SigninterfridseqIndex>> seqMap = new HashMap<>();
    // 指标1
    private Map<String, Double> ridHistTraveltimeMap = new HashMap<>(); // dws_tfc_state_rid_tpwkd_index_m
    private Map<String, InterFridSeqTurnDirIndex> interFridSeqTurndirHistIndex = new HashMap<>(); // dws_tfc_state_signinterfridseq_tpwkd_delaydur_m

    // 指标2
    Map<String, RidInfo> ridIndexM = new HashMap<>(); // 存放 rid -> benchmarkNostopSpeed/映射的静态数据
    Map<String, SigninterfridseqIndex> fridseqIndexM = new HashMap<>();

    // 指标3
    Map<String, List<InterSignalOperPlan>> interSignalOperPlansM = new HashMap<>();

    // 指标4
    private Map<Object, InterfridlaneTpMultiflowDto> laneFlowMap = new HashMap<>();

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

    public SingleIntersectionAnalysis(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public List<fRidSeqTurnDirIndexDTO> evaluate(long dayOfWeek, long stepIndex,
                         List<RidIndex> ridIndexRTList,
                         Map<String, List<SigninterfridseqIndex>> seqMap,
                         Map<String, SigninterfridseqIndex> fridseqIndexM,
                         Map<String, RidInfo> ridIndexM,
                         Map<String, List<InterSignalOperPlan>> interSignalOperPlansM) {
        List<fRidSeqTurnDirIndexDTO> allResult = null;

        try {
            prepareData(dayOfWeek, stepIndex, ridIndexRTList, seqMap,fridseqIndexM, ridIndexM,interSignalOperPlansM);
            allResult = caculate();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

        return allResult;
    }

    private void prepareData(long dayOfWeek, long stepIndex,
                             List<RidIndex> ridIndexRTList,
                             Map<String, List<SigninterfridseqIndex>> seqMap,
                             Map<String, SigninterfridseqIndex> fridseqIndexM,
                             Map<String, RidInfo> ridIndexM,
                             Map<String, List<InterSignalOperPlan>> interSignalOperPlansM) {
        currTimeSlice = new TimeSlice(stepIndex, "1mi"); // HFY: 不知道tp怎么传过来，姑且设置个1mi吧

        // 实时数据
        ridIndexRTList.forEach(op -> ridIndexRTMap.put(op.getRid(), op));

        // 基础
        this.seqMap = seqMap;
        this.fridseqIndexM = fridseqIndexM;
        this.ridIndexM = ridIndexM;
        this.interSignalOperPlansM = interSignalOperPlansM;

        DBQuery dbQuery = new DBQuery(executorService);
        // 指标1
        dbQuery.add(
                dws_tfc_state_rid_tpwkd_index_m,
                sql_dws_tfc_state_rid_tpwkd_index_m,
                RidIndex.class,
                new ArrayList<String>() {
                    {
                        add("rid");
                        add("travelTime");
                    }
                },
                dayOfWeek,
                stepIndex / 10);
        dbQuery.add(
                dws_tfc_state_signinterfridseq_tpwkd_delaydur_m,
                sql_dws_tfc_state_signinterfridseq_tpwkd_delaydur_m,
                InterFridSeqTurnDirIndex.class,
                new ArrayList<String>() {
                    {
                        add("fRid");
                        add("turnDirNo");
                        add("avgTraceTravelTime");
                    }
                },
                dayOfWeek,
                stepIndex / 10);

        dbQuery.execute();

        // 指标1
        dbQuery.<RidIndex>get(dws_tfc_state_rid_tpwkd_index_m)
                .forEach(op -> ridHistTraveltimeMap.put(op.getRid(), op.getTravelTime()));
        dbQuery.<InterFridSeqTurnDirIndex>get(dws_tfc_state_signinterfridseq_tpwkd_delaydur_m)
                .forEach(op -> {
                    String key = concat(op.getfRid(), op.getTurnDirNo());
                    interFridSeqTurndirHistIndex.put(key, op);
                });
    }

    private List<fRidSeqTurnDirIndexDTO> caculate() {
        List<fRidSeqTurnDirIndexDTO> allResults = new ArrayList<>();

        // 道路级forEach
        for (Map.Entry<String, RidIndex> ridIndex : ridIndexRTMap.entrySet()) {
            String rid = ridIndex.getKey();

            List<SigninterfridseqIndex> signinterfridseqIndices = seqMap.get(rid);
            if (signinterfridseqIndices == null) {
                continue;
            }

            // 转向级forEach
            for (SigninterfridseqIndex signinterfridseqIndex : signinterfridseqIndices) {
                fRidSeqTurnDirIndexDTO curResult = new fRidSeqTurnDirIndexDTO(signinterfridseqIndex.getInterId(), rid, signinterfridseqIndex.getTurnDirNo());

                // 指标1
                calTravelTime(rid, signinterfridseqIndex, curResult);
                // 指标2
                calDelay(rid, signinterfridseqIndex, curResult);
                // 指标3
                // 指标4

                allResults.add(curResult);
            }
        }
        return allResults;
    }

    private void calTravelTime(String rid, SigninterfridseqIndex signinterfridseqIndex, fRidSeqTurnDirIndexDTO curResult) {
        Long turnDirNo = signinterfridseqIndex.getTurnDirNo();

        double traveltimeHistRidseq = sumRidseqHistoryTraveltime(signinterfridseqIndex); // 2
        double traveltimeRTRidseq = sumRidseqRealtimeTraveltime(signinterfridseqIndex); // 4

        // 3
        double traveltimeHistTurn = 0.0d; // 3
        String key = concat(rid, turnDirNo);
        InterFridSeqTurnDirIndex interFridSeqTurnDirIndex = interFridSeqTurndirHistIndex.get(key);
        if (interFridSeqTurnDirIndex != null) {
            traveltimeHistTurn = interFridSeqTurnDirIndex.getAvgTraceTravelTime();
        } else {
            traveltimeHistTurn = traveltimeRTRidseq;
        }
        double traveltimeRTTurn = traveltimeHistTurn / traveltimeHistRidseq * traveltimeRTRidseq;
        curResult.setTravelTime(traveltimeRTTurn);
    }

    private void calDelay(String rid, SigninterfridseqIndex signinterfridseqIndex, fRidSeqTurnDirIndexDTO curResult) {
        Long turnDirNo = signinterfridseqIndex.getTurnDirNo();

        String key = concat(rid, turnDirNo);
        SigninterfridseqIndex interFridSeqIndex = fridseqIndexM.get(key);

        //计算延误
        double standardTraveltime = 0.0d;
        if(interFridSeqIndex != null){
            standardTraveltime = interFridSeqIndex.getBenchmarkNostopTravelTime();
        }else{
            standardTraveltime = ridIndexM.get(rid).getBenchmarkNostopTravelTime();
        }
        if(curResult.getTravelTime()!=null){

            double delayTime = Math.max(0, curResult.getTravelTime() - standardTraveltime);
            curResult.setBenchmarkTravelTime(standardTraveltime);
            curResult.setDelay(delayTime);
            //修正夜间延误
            boolean isCorrectDelay = isCorrectDelay(curResult);
            //为了保证指标对齐，延误修正后，也要修正行程时间
            if (isCorrectDelay) {
                curResult.setDelay((double) defaultLimitDelay);
                curResult.setTravelTime(standardTraveltime + defaultLimitDelay);
            }
        }
    }

    /**
     * 计算转向停车次数
     */
    private void calOneDirectionStopCnt(String rid, SigninterfridseqIndex signinterfridseqIndex, fRidSeqTurnDirIndexDTO curResult) {

        //获取当前转向的周期绿信比数据
        CycleSplit cycleSplit = getCycleSplit(signinterfridseqIndex, curResult);

        //根据配时数据计算停车次数
        double numOfStop = calStopCnt(cycleSplit.getRedTime(), curResult.getDelay());

        curResult.setStopCnt(numOfStop);

        if(curResult.isStopcntValid()) {
            curResult.getAlgDetail().addStopCntSource(curResult.getAlgDetail().getDelayDurSource());
            curResult.getAlgDetail().setStopCntAlgType(SignalOptConstant.ALG_TYPE_STAT);
        }

        //修正停车次数
        boolean isCorrectStopcnt = isCorrectStopcnt(curResult);
        if (isCorrectStopcnt) {
            correctStopcnt(cycleSplit.getRedTime(), curResult);
        }
    }

    /**
     * 计算当前转向的排队长度
     */
    private void calOneDirectionQueue(String rid, SigninterfridseqIndex signinterfridseqIndex, fRidSeqTurnDirIndexDTO curResult) {


//        String keyInterFridTurndir = intersectionBase.getInterId() + SignalOptConstant.Seperator + intersectionBase.getfRid() + SignalOptConstant.Seperator + intersectionBase.getTurnDirNo();
//        Set<String> interLaneList = this.interLaneMap.getOrDefault(keyInterFridTurndir, Collections.emptySet());
//        Map<String, ValueAndReliability> laneFlow = new HashMap<>();
//        interLaneList.forEach(laneId -> laneFlow.put(laneId, new ValueAndReliability(0D, 0D)));

        double finalQueue = -1.0;
        String flowSrcType = "";
        if (curResult.isStopcntValid()) {
            //获取当前转向的周期绿信比数据
            CycleSplit cycleSplit = getCycleSplit(signinterfridseqIndex, curResult);

////            if (!interLaneList.isEmpty()) {
//                //计算当前转向所有车道的流量
////            for (String laneId : interLaneList) {
//                    InterfridlaneTpMultiflowDto currLaneFlow = laneFlowMap.get(laneId);
//                    if (currLaneFlow != null && currLaneFlow.getFlow() > 0) {
//                        ValueAndReliability var = new ValueAndReliability(currLaneFlow.getFlow(), currLaneFlow.getReliabilityCode());
//                        ValueAndReliability old = laneFlow.get(laneId);
//                        if (old == null || var.getReliability() > old.getReliability()) {
//                            laneFlow.put(laneId, var);
//                        }
//
////                        if (flowSrcType.isEmpty()) {
////                            flowSrcType = currLaneFlow.getMultiType();
////                        } else {
////                            flowSrcType = CommonFunction.StrBinaOr(flowSrcType, currLaneFlow.getMultiType());
////                        }
//                    }
////                }
////            }

//            double volume = laneFlow.values().stream().mapToDouble(o -> o.getValue()).sum();


            //排队长度=停车次数*平均周期车道流量
//            if (!laneFlow.isEmpty() && volume > StaticConstantClass.ZERO_BOUNDARY) {
//                //将流量转换为周期流量
//                double cycleVolume = volume * cycleSplit.getCycleTime() / currTimeSlice.getWinSize();
//                finalQueue = curResult.getStopCnt() * params.getDefaultCarLength() * cycleVolume / laneFlow.size();
//
//            } else {
//                double greenLostTime = params.getDefaultGreenLostTime();
//                if(cycleSplit.getSplitTime() <= greenLostTime) {
//                    greenLostTime = 0;
//                }
//
//                double defaultSatu = getDefaultSaturation(currTimeSlice, curResult.getTurnDirNo());
//                finalQueue = curResult.getStopCnt() * defaultSatu * (cycleSplit.getSplitTime() - greenLostTime) * 1.0 / getRatioOfQueueNum(signinterfridseqIndex.getTurnDirNo()) * params.getDefaultCarLength();
//
//            }

        }

        //排队长度不能超过rid序列的长度
        finalQueue = Math.min(finalQueue, signinterfridseqIndex.getfRidseqLen());

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
    private CycleSplit getCycleSplit(SigninterfridseqIndex signinterfridseqIndex, fRidSeqTurnDirIndexDTO curResult) {
        List<InterSignalOperPlan> interSignalOperPlans = interSignalOperPlansM.get(signinterfridseqIndex.getInterId());
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

        //当前路口的相位通行方向
        // bug--by xfyue 不知该使用哪个阶段

//        if (null != interSignalOperPlans && interSignalOperPlans.size() > 0) {
//            for (InterSignalOperPlan oneSignalOper : interSignalOperPlans) {
//                PhaseInfo currOperPhase = oneSignalOper.getPhaseInfo();
//                for (PhaseInfo onePhaseInfo : interPhaseList) {
//                    if (currOperPhase.equals(onePhaseInfo)) {
//                        actualSplit += oneSignalOper.getSplitTime();
//                        actualCycle = oneSignalOper.getCycleTime();
//                    }
//                }
//            }
//            //红灯时间 > 全红时间（3秒） * 相位数
//            if (actualSplit > 0 && actualCycle > 0 && actualCycle - actualSplit > 3 * interSignalOperPlans.size()) {
//                Double redTime = calRedTime(actualCycle, actualSplit, params.getDefaultGreenLostTime());
//                cycleSplit.setRedTime(redTime);
//                cycleSplit.setSplitTime(actualSplit);
//                cycleSplit.setCycleTime(actualCycle);
//                cycleSplit.setSource(SignalOptConstant.SOURCE_SIGNAL);
//                curResult.getAlgDetail().addStopCntSource(SignalOptConstant.SOURCE_SIGNAL);
//            }
//        }

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

    private double sumRidseqHistoryTraveltime(SigninterfridseqIndex signinterfridseqIndex) {
        double traveltimeHistRidseq = 0.0d;
        List<String> rids = signinterfridseqIndex.getRids();
        for (String subRid : rids) {
            if (ridHistTraveltimeMap.containsKey(subRid)) {
                traveltimeHistRidseq += ridHistTraveltimeMap.get(subRid);
            } else {
                traveltimeHistRidseq += getOneRidBenchmarkTravelTime(subRid);
            }
        }
        return traveltimeHistRidseq;
    }

    private double sumRidseqRealtimeTraveltime(SigninterfridseqIndex signinterfridseqIndex) {
        double traveltimeRTRidseq = 0.0d;
        List<String> rids = signinterfridseqIndex.getRids();
        for (String subRid : rids) {
            if (ridIndexRTMap.containsKey(subRid)) {
                traveltimeRTRidseq += ridIndexRTMap.get(subRid).getTravelTime();
            } else if (ridHistTraveltimeMap.containsKey(subRid)) {
                traveltimeRTRidseq += ridHistTraveltimeMap.get(subRid);
            }
        }
        return traveltimeRTRidseq;
    }

    private double getOneRidBenchmarkTravelTime(String subRid) {
        return 300.0 / 40.0 * 3.6;
    }
}
