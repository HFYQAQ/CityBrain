package cn.edu.neu.citybrain.dto;

import cn.edu.neu.citybrain.function.CommonFunction;
import cn.edu.neu.citybrain.util.SignalOptConstant;
import com.google.gson.JsonObject;

/**
 * rid转向级别的交通指标参数
 */
public class fRidSeqTurnDirIndexDTO {
    /**
     * 路口id
     */
    private String interId;
    /**
     * fRid
     */
    private String fRid;

    /**
     * 车流转向
     */
    private Long turnDirNo;
    /**
     * tRid
     */
    private String tRid;
    /**
     * 转向流量
     */
    private Double multiFlow;
    /**
     * 转向流量的时间片
     */
    private TimeSlice flowTimeSlice;
    /**
     * 转向流量的置信度
     */
    private Double flowReliability;
    /**
     * 流量的融合类型
     */
    private String flowMultiType;
    /**
     * rid序列的平均行程时间
     */
    private Double ridSeqTravelTime;
    /**
     * 行程时间
     */
    private Double travelTime;
    /**
     * 标准行程时间
     */
    private Double benchmarkTravelTime;
    /**
     * 速度
     */
    private Double speed;
    /**
     * 速度置信度
     */
    private Double reliabilityCode;
    /**
     * 延误
     */
    private Double delay;
    /**
     * 延误数据来源
     */
    private String delaySrcType;
    /**
     * 停车次数
     */
    private Double stopCnt;
    /**
     * 停车次数置信度
     */
    private Double stopCntReliability;

    /**
     * 停车次数的来源
     */
    private String stopCntSrcType;
    /**
     * 排队长度
     */
    private Double queue;
    /**
     * 排队长度置信度
     */
    private Double queueReliability;
    /**
     * 排队长度的来源
     */
    private String queueSrcType;
    
    /**
     * 结果来源算法的详细信息
     */
    private AlgorithmDetail algDetail = null;

    public String getInterId() {
        return interId;
    }

    public String getfRid() {
        return fRid;
    }

    public void setfRid(String fRid) {
        this.fRid = fRid;
    }

    public Long getTurnDirNo() {
        return turnDirNo;
    }

    public void setTurnDirNo(Long turnDirNo) {
        this.turnDirNo = turnDirNo;
    }

    public String gettRid() {
        return tRid;
    }

    public void settRid(String tRid) {
        this.tRid = tRid;
    }

    public fRidSeqTurnDirIndexDTO(String interId, String fRid, Long turnDirNo, String tRid) {
        this.interId = interId;
        this.fRid = fRid;
        this.turnDirNo = turnDirNo;
        this.tRid = tRid;
        algDetail = new AlgorithmDetail();
    }

    public fRidSeqTurnDirIndexDTO(String interId, String fRid, Long turnDirNo) {
        this.interId = interId;
        this.fRid = fRid;
        this.turnDirNo = turnDirNo;
        algDetail = new AlgorithmDetail();
    }

    public Double getMultiFlow() {
        return multiFlow;
    }

    public void setMultiFlow(Double multiFlow) {
        this.multiFlow = multiFlow;
    }

    public TimeSlice getFlowTimeSlice() {
        return flowTimeSlice;
    }

    public void setFlowTimeSlice(TimeSlice flowTimeSlice) {
        this.flowTimeSlice = flowTimeSlice;
    }

    public Double getFlowReliability() {
        return flowReliability;
    }

    public void setFlowReliability(Double flowReliability) {
        this.flowReliability = flowReliability;
    }

    public String getFlowMultiType() {
        return flowMultiType;
    }

    public void setFlowMultiType(String flowMultiType) {
        this.flowMultiType = flowMultiType;
    }

    public Double getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Double travelTime) {
        this.travelTime = travelTime;
    }

    public JsonObject getTravelTimeReliabilityDetail() {
		return algDetail.getTravelTimeAlgDetail();
	}

	public void setTravelTimeReliabilityDetail(JsonObject travelTimeReliabilityDetail) {
		algDetail.setTravelTimeAlgDetail(travelTimeReliabilityDetail);
	}

	public Double getRidSeqTravelTime() {
        return ridSeqTravelTime;
    }

    public void setRidSeqTravelTime(Double ridSeqTravelTime) {
        this.ridSeqTravelTime = ridSeqTravelTime;
    }

    public Double getBenchmarkTravelTime() {
        return benchmarkTravelTime;
    }

    public void setBenchmarkTravelTime(Double benchmarkTravelTime) {
        this.benchmarkTravelTime = benchmarkTravelTime;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getDelay() {
        return delay;
    }

    public void setDelay(Double delay) {
        this.delay = delay;
    }

    public boolean isDelayValid() {
        return delay != null && !Double.isNaN(delay) && !CommonFunction.isDoubleEquals(delay, SignalOptConstant.INVALID_VALUE);
    }
    public boolean isStopcntValid() {
        return stopCnt != null && !Double.isNaN(stopCnt) && !CommonFunction.isDoubleEquals(stopCnt, SignalOptConstant.INVALID_VALUE);
    }
    public void setInterId(String interId) {
        this.interId = interId;
    }

    public String getDelaySrcType() {
        return delaySrcType;
    }

    public void setDelaySrcType(String delaySrcType) {
        this.delaySrcType = delaySrcType;
    }
    
    public JsonObject getDelayReliabilityDetail() {
		return algDetail.getDelayDurAlgDetail();
	}

	public void setDelayReliabilityDetail(JsonObject detail) {
		algDetail.setDelayDurAlgDetail(detail);
	}

    public Double getStopCnt() {
        return stopCnt;
    }

    public void setStopCnt(Double stopCnt) {
        this.stopCnt = stopCnt;
    }

    public Double getStopCntReliability() {
        return stopCntReliability;
    }

    public void setStopCntReliability(Double stopCntReliability) {
        this.stopCntReliability = stopCntReliability;
    }

    public JsonObject getStopCntReliabilityDetail() {
		return algDetail.getStopCntAlgDetail();
	}

	public void setStopCntReliabilityDetail(JsonObject stopCntReliabilityDetail) {
		algDetail.setStopCntAlgDetail(stopCntReliabilityDetail);
	}

	public Double getQueue() {
        return queue;
    }

    public void setQueue(Double queue) {
        this.queue = queue;
    }

    public Double getQueueReliability() {
        return queueReliability;
    }

    public void setQueueReliability(Double queueReliability) {
        this.queueReliability = queueReliability;
    }

    public JsonObject getQueueReliabilityDetail() {
		return algDetail.getQueueLenAlgDetail();
	}

	public void setQueueReliabilityDetail(JsonObject queueReliabilityDetail) {
		algDetail.setQueueLenAlgDetail(queueReliabilityDetail);
	}

	public boolean isQueueValid() {
        return queue != null && !Double.isNaN(queue) && !CommonFunction.isDoubleEquals(queue, SignalOptConstant.INVALID_VALUE);
    }

    public Double getReliabilityCode() {
        return reliabilityCode;
    }

    public void setReliabilityCode(Double reliabilityCode) {
        this.reliabilityCode = reliabilityCode;
    }

    public String getStopCntSrcType() {
        return stopCntSrcType;
    }

    public void setStopCntSrcType(String stopCntSrcType) {
        this.stopCntSrcType = stopCntSrcType;
    }

    public String getQueueSrcType() {
        return queueSrcType;
    }

    public void setQueueSrcType(String queueSrcType) {
        this.queueSrcType = queueSrcType;
    }

    public AlgorithmDetail getAlgDetail() {
        return algDetail;
    }

    public void setAlgDetail(AlgorithmDetail algDetail) {
        this.algDetail = algDetail;
    }

    @Override
    public String toString() {
        return "fRidSeqTurnDirIndexDTO{" +
                "interId='" + interId + '\'' +
                ", fRid='" + fRid + '\'' +
                ", turnDirNo=" + turnDirNo +
                ", tRid='" + tRid + '\'' +
                ", travelTime=" + travelTime +
                ", delay=" + delay +
                ", stopCnt=" + stopCnt +
                ", queue=" + queue +
                '}';
    }

//    @Override
//    public String toString() {
//        return "fRidSeqTurnDirIndexDTO{" +
//                "interId='" + interId + '\'' +
//                ", fRid='" + fRid + '\'' +
//                ", turnDirNo=" + turnDirNo +
//                ", tRid='" + tRid + '\'' +
//                ", multiFlow=" + multiFlow +
//                ", flowTimeSlice=" + flowTimeSlice +
//                ", flowReliability=" + flowReliability +
//                ", flowMultiType='" + flowMultiType + '\'' +
//                ", ridSeqTravelTime=" + ridSeqTravelTime +
//                ", travelTime=" + travelTime +
//                ", benchmarkTravelTime=" + benchmarkTravelTime +
//                ", speed=" + speed +
//                ", reliabilityCode=" + reliabilityCode +
//                ", delay=" + delay +
//                ", delaySrcType='" + delaySrcType + '\'' +
//                ", stopCnt=" + stopCnt +
//                ", stopCntReliability=" + stopCntReliability +
//                ", stopCntSrcType='" + stopCntSrcType + '\'' +
//                ", queue=" + queue +
//                ", queueReliability=" + queueReliability +
//                ", queueSrcType='" + queueSrcType + '\'' +
//                ", algDetail=" + algDetail +
//                '}';
//    }
}
