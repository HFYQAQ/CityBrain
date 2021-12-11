package cn.edu.neu.citybrain.dto.my;

public class RoadMetric {
    private String interId;
    private String fRid;
    private Long turnDirNo;

    private String dt;
    private Long stepIndex1mi;

    private Double travelTime;
    private Double delay;
    private Double stopCnt;
    private Double queue;

    public RoadMetric(String interId, String fRid, Long turnDirNo, String dt, Long stepIndex1mi, Double travelTime, Double delay, Double stopCnt, Double queue) {
        this.interId = interId;
        this.fRid = fRid;
        this.turnDirNo = turnDirNo;

        this.dt = dt;
        this.stepIndex1mi = stepIndex1mi;

        this.travelTime = travelTime;
        this.delay = delay;
        this.stopCnt = stopCnt;
        this.queue = queue;
    }

    public void setInterId(String interId) {
        this.interId = interId;
    }

    public String getInterId() {
        return interId;
    }

    public void setfRid(String fRid) {
        this.fRid = fRid;
    }

    public String getfRid() {
        return fRid;
    }

    public void setTurnDirNo(Long turnDirNo) {
        this.turnDirNo = turnDirNo;
    }

    public Long getTurnDirNo() {
        return turnDirNo;
    }

    public void setTravelTime(Double travelTime) {
        this.travelTime = travelTime;
    }

    public Double getTravelTime() {
        return travelTime;
    }

    public void setDelay(Double delay) {
        this.delay = delay;
    }

    public Double getDelay() {
        return delay;
    }

    public void setStopCnt(Double stopCnt) {
        this.stopCnt = stopCnt;
    }

    public Double getStopCnt() {
        return stopCnt;
    }

    public void setQueue(Double queue) {
        this.queue = queue;
    }

    public Double getQueue() {
        return queue;
    }

    public String getDt() {
        return dt;
    }

    public Long getStepIndex1mi() {
        return stepIndex1mi;
    }

    @Override
    public String toString() {
        return "RoadMetric{" +
                "interId='" + interId + '\'' +
                ", fRid='" + fRid + '\'' +
                ", turnDirNo=" + turnDirNo +
                ", dt='" + dt + '\'' +
                ", stepIndex1mi=" + stepIndex1mi +
                ", travelTime=" + travelTime +
                ", delay=" + delay +
                ", stopCnt=" + stopCnt +
                ", queue=" + queue +
                '}';
    }
}
