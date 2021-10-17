package cn.edu.neu.citybrain.dto;

import cn.edu.neu.citybrain.function.CommonFunction;

public class SigninterfridseqMultiindex {
    private String statTime;
    private String interId;
    private String fRid;
    private Long turnDirNo;
    private String tRid;
    private Long stepIndex;
    private Double travelTime;
    private Double traveltimeReliabilityCode;
    private String traveltimeMultiType;
    private Double stopCnt;
    private Double stopcntReliabilityCode;
    private String stopcntMultiType;
    private Double delayDur;
    private Double delaydurReliabilityCode;
    private String delaydurMultiType;
    private Double queueLen;
    private Double queuelenReliabilityCode;
    private String queuelenMultiType;
    private Double stopDur;
    private Double stopDurReliabilityCode;
    private String stopDurMultiType;
    private AlgorithmDetail algDetail;
    private String dt;
    private String tp;
    private String dataVersion;
    private String adcode;

    public SigninterfridseqMultiindex() {
    }

    public String getStatTime() {
        return statTime;
    }

    public void setStatTime(String statTime) {
        this.statTime = statTime;
    }

    public String getInterId() {
        return interId;
    }

    public void setInterId(String interId) {
        this.interId = interId;
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
        if(null == tRid){
            this.tRid = "";
        }else {
            this.tRid = tRid;
        }
    }

    public Long getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(Long stepIndex) {
        this.stepIndex = stepIndex;
    }

    public Double getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Double travelTime) {
        this.travelTime = CommonFunction.trimTo(travelTime, 2);
    }

    public Double getTraveltimeReliabilityCode() {
        return traveltimeReliabilityCode;
    }

    public void setTraveltimeReliabilityCode(Double traveltimeReliabilityCode) {
        this.traveltimeReliabilityCode = CommonFunction.trimTo(traveltimeReliabilityCode, 0);
    }

    public Double getStopCnt() {
        return stopCnt;
    }

    public void setStopCnt(Double stopCnt) {
        this.stopCnt = CommonFunction.trimTo(stopCnt, 2);
    }

    public Double getStopcntReliabilityCode() {
        return stopcntReliabilityCode;
    }

    public void setStopcntReliabilityCode(Double stopcntReliabilityCode) {
        this.stopcntReliabilityCode = CommonFunction.trimTo(stopcntReliabilityCode, 0);
    }

    public Double getDelayDur() {
        return delayDur;
    }

    public void setDelayDur(Double delayDur) {
        this.delayDur = CommonFunction.trimTo(delayDur, 2);
    }

    public Double getDelaydurReliabilityCode() {
        return delaydurReliabilityCode;
    }

    public void setDelaydurReliabilityCode(Double delaydurReliabilityCode) {
        this.delaydurReliabilityCode = CommonFunction.trimTo(delaydurReliabilityCode, 0);
    }

    public Double getQueueLen() {
        return queueLen;
    }

    public void setQueueLen(Double queueLen) {
        this.queueLen = CommonFunction.trimTo(queueLen, 2);
    }

    public Double getQueuelenReliabilityCode() {
        return queuelenReliabilityCode;
    }

    public void setQueuelenReliabilityCode(Double queuelenReliabilityCode) {
        this.queuelenReliabilityCode = CommonFunction.trimTo(queuelenReliabilityCode, 0);
    }

    public String getTraveltimeMultiType() {
        return traveltimeMultiType;
    }

    public void setTraveltimeMultiType(String traveltimeMultiType) {
        this.traveltimeMultiType = traveltimeMultiType;
    }

    public String getStopcntMultiType() {
        return stopcntMultiType;
    }

    public void setStopcntMultiType(String stopcntMultiType) {
        this.stopcntMultiType = stopcntMultiType;
    }

    public String getDelaydurMultiType() {
        return delaydurMultiType;
    }

    public void setDelaydurMultiType(String delaydurMultiType) {
        this.delaydurMultiType = delaydurMultiType;
    }

    public String getQueuelenMultiType() {
        return queuelenMultiType;
    }

    public void setQueuelenMultiType(String queuelenMultiType) {
        this.queuelenMultiType = queuelenMultiType;
    }

    public Double getStopDur() {
        return stopDur;
    }

    public void setStopDur(Double stopDur) {
        this.stopDur =  CommonFunction.trimTo(stopDur, 2);;
    }

    public Double getStopDurReliabilityCode() {
        return stopDurReliabilityCode;
    }

    public void setStopDurReliabilityCode(Double stopDurReliabilityCode) {
        this.stopDurReliabilityCode = CommonFunction.trimTo(stopDurReliabilityCode, 0);;
    }

    public String getStopDurMultiType() {
        return stopDurMultiType;
    }

    public void setStopDurMultiType(String stopDurMultiType) {
        this.stopDurMultiType = stopDurMultiType;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getTp() {
        return tp;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }

    public String getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(String dataVersion) {
        this.dataVersion = dataVersion;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public AlgorithmDetail getAlgDetail() {
        return algDetail;
    }

    public void setAlgDetail(AlgorithmDetail algDetail) {
        this.algDetail = algDetail;
    }

    @Override
    public String toString() {
        return "SigninterfridseqMultiindex{" +
                "statTime='" + statTime + '\'' +
                ", interId='" + interId + '\'' +
                ", fRid='" + fRid + '\'' +
                ", turnDirNo=" + turnDirNo +
                ", tRid='" + tRid + '\'' +
                ", stepIndex=" + stepIndex +
                ", travelTime=" + travelTime +
                ", traveltimeReliabilityCode=" + traveltimeReliabilityCode +
                ", traveltimeMultiType='" + traveltimeMultiType + '\'' +
                ", stopCnt=" + stopCnt +
                ", stopcntReliabilityCode=" + stopcntReliabilityCode +
                ", stopcntMultiType='" + stopcntMultiType + '\'' +
                ", delayDur=" + delayDur +
                ", delaydurReliabilityCode=" + delaydurReliabilityCode +
                ", delaydurMultiType='" + delaydurMultiType + '\'' +
                ", queueLen=" + queueLen +
                ", queuelenReliabilityCode=" + queuelenReliabilityCode +
                ", queuelenMultiType='" + queuelenMultiType + '\'' +
                ", stopDur=" + stopDur +
                ", stopDurReliabilityCode=" + stopDurReliabilityCode +
                ", stopDurMultiType='" + stopDurMultiType + '\'' +
                ", algDetail=" + algDetail +
                ", dt='" + dt + '\'' +
                ", tp='" + tp + '\'' +
                ", dataVersion='" + dataVersion + '\'' +
                ", adcode='" + adcode + '\'' +
                '}';
    }
}
