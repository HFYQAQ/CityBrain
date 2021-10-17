package cn.edu.neu.citybrain.dto.my;

/**
 * 转向级相关信息
 */
public class TurnGranularityInfo {
    private RidGranularityInfo ridGranularityInfo;

    private String interId;
    private String rid;
    private Long turnDirNo;

    private String laneId;

    private String fRidseq;
    private Double benchmarkNostopTravelTime;

    private String phasePlanID;
    private String phaseName;

    private Double fRidseqLen;

    public TurnGranularityInfo(
            RidGranularityInfo ridGranularityInfo,
            String interId, String rid, Long turnDirNo,
            String laneId,
            String fRidseq, Double benchmarkNostopTravelTime,
            String phasePlanID, String phaseName) {
        this.ridGranularityInfo = ridGranularityInfo;

        this.interId = interId;
        this.rid = rid;
        this.turnDirNo = turnDirNo;

        this.laneId = laneId;

        this.fRidseq = fRidseq;
        this.benchmarkNostopTravelTime = benchmarkNostopTravelTime;

        this.phasePlanID = phasePlanID;
        this.phaseName = phaseName;
    }

    public void setRidGranularityInfo(RidGranularityInfo ridGranularityInfo) {
        this.ridGranularityInfo = ridGranularityInfo;
    }

    public RidGranularityInfo getRidGranularityInfo() {
        return ridGranularityInfo;
    }

    public void setInterId(String interId) {
        this.interId = interId;
    }

    public String getInterId() {
        return interId;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getRid() {
        return rid;
    }

    public void setTurnDirNo(Long turnDirNo) {
        this.turnDirNo = turnDirNo;
    }

    public Long getTurnDirNo() {
        return turnDirNo;
    }

    public void setLaneId(String laneId) {
        this.laneId = laneId;
    }

    public String getLaneId() {
        return laneId;
    }

    public void setfRidseq(String fRidseq) {
        this.fRidseq = fRidseq;
    }

    public String getfRidseq() {
        return fRidseq;
    }

    public void setBenchmarkNostopTravelTime(Double benchmarkNostopTravelTime) {
        this.benchmarkNostopTravelTime = benchmarkNostopTravelTime;
    }

    public Double getBenchmarkNostopTravelTime() {
        return benchmarkNostopTravelTime;
    }

    public void setPhasePlanID(String phasePlanID) {
        this.phasePlanID = phasePlanID;
    }

    public String getPhasePlanID() {
        return phasePlanID;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setfRidseqLen(Double fRidseqLen) {
        this.fRidseqLen = fRidseqLen;
    }

    public Double getfRidseqLen() {
        return fRidseqLen;
    }
}
