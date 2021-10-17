package cn.edu.neu.citybrain.dto;

import java.io.Serializable;

public class InterFridSeqTurnDirIndex implements Serializable {
    private String statMonth;

    private String interId;

    private String fRid;

    private Long turnDirNo;

    private Byte dayOfWeek;

    private Long stepIndex;

    private Double avgSpeed;

    private Double avgSpeedTravelTime;

    private Double avgTraceTravelTime;

    private Double avgDelayDur;

    private Double avgQueueLen;

    private Double avgStopCarCnt;

    private Double avgStopNum;

    private String month;

    private String adcode;

    private static final long serialVersionUID = 1L;

    public String getStatMonth() {
        return statMonth;
    }

    public void setStatMonth(String statMonth) {
        this.statMonth = statMonth == null ? null : statMonth.trim();
    }

    public String getInterId() {
        return interId;
    }

    public void setInterId(String interId) {
        this.interId = interId == null ? null : interId.trim();
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

    public Byte getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Byte dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Long getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(Long stepIndex) {
        this.stepIndex = stepIndex;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month == null ? null : month.trim();
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode == null ? null : adcode.trim();
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Double getAvgSpeedTravelTime() {
        return avgSpeedTravelTime;
    }

    public void setAvgSpeedTravelTime(Double avgSpeedTravelTime) {
        this.avgSpeedTravelTime = avgSpeedTravelTime;
    }

    public Double getAvgTraceTravelTime() {
        return avgTraceTravelTime;
    }

    public void setAvgTraceTravelTime(Double avgTraceTravelTime) {
        this.avgTraceTravelTime = avgTraceTravelTime;
    }

    public Double getAvgDelayDur() {
        return avgDelayDur;
    }

    public void setAvgDelayDur(Double avgDelayDur) {
        this.avgDelayDur = avgDelayDur;
    }

    public Double getAvgQueueLen() {
        return avgQueueLen;
    }

    public void setAvgQueueLen(Double avgQueueLen) {
        this.avgQueueLen = avgQueueLen;
    }

    public Double getAvgStopCarCnt() {
        return avgStopCarCnt;
    }

    public void setAvgStopCarCnt(Double avgStopCarCnt) {
        this.avgStopCarCnt = avgStopCarCnt;
    }

    public Double getAvgStopNum() {
        return avgStopNum;
    }

    public void setAvgStopNum(Double avgStopNum) {
        this.avgStopNum = avgStopNum;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return "InterFridSeqTurnDirIndex{" +
                "statMonth='" + statMonth + '\'' +
                ", interId='" + interId + '\'' +
                ", fRid='" + fRid + '\'' +
                ", turnDirNo=" + turnDirNo +
                ", dayOfWeek=" + dayOfWeek +
                ", stepIndex=" + stepIndex +
                ", avgSpeed=" + avgSpeed +
                ", avgSpeedTravelTime=" + avgSpeedTravelTime +
                ", avgTraceTravelTime=" + avgTraceTravelTime +
                ", avgDelayDur=" + avgDelayDur +
                ", avgQueueLen=" + avgQueueLen +
                ", avgStopCarCnt=" + avgStopCarCnt +
                ", avgStopNum=" + avgStopNum +
                ", month='" + month + '\'' +
                ", adcode='" + adcode + '\'' +
                '}';
    }
}
