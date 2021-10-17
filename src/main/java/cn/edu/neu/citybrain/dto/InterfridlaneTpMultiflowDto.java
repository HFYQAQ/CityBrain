package cn.edu.neu.citybrain.dto;

public class InterfridlaneTpMultiflowDto {
    // 统计时间，yyyymmddhhmiss
    private String statTime;
    // 置信度最高的检测设备id
    private String reliableDevcId;
    // 置信度最高的检测设备类型编码
    private String reliableDevcTypeNo;
    // 车道id
    private String laneId;
    // 进口道rid
    private String rid;
    // 路口id
    private String interId;
    // 时间片序号
    private Long stepIndex;
    // 融合后流量
    private Double flow;
    // 置信度
    private Double reliabilityCode;
    // 融合数据源，使用10位开关形式，依赖数据源类型：第一位：高德、第二位：卡口、第三位：微波、第四位：线圈
    private String multiType;

    // 日期分区
    private String dt;
    // 时间粒度
    private String tp;
    // 高德数据版本，如20170131
    private String dataVersion;
    // 城市编码
    private String adcode;

    public String getStatTime() {
        return statTime;
    }

    public void setStatTime(String statTime) {
        this.statTime = statTime;
    }

    public String getReliableDevcId() {
        return reliableDevcId;
    }

    public void setReliableDevcId(String reliableDevcId) {
        this.reliableDevcId = reliableDevcId;
    }

    public String getReliableDevcTypeNo() {
        return reliableDevcTypeNo;
    }

    public void setReliableDevcTypeNo(String reliableDevcTypeNo) {
        this.reliableDevcTypeNo = reliableDevcTypeNo;
    }

    public String getLaneId() {
        return laneId;
    }

    public void setLaneId(String laneId) {
        this.laneId = laneId;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getInterId() {
        return interId;
    }

    public void setInterId(String interId) {
        this.interId = interId;
    }

    public Long getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(Long stepIndex) {
        this.stepIndex = stepIndex;
    }

    public Double getFlow() {
        return flow;
    }

    public void setFlow(Double flow) {
        this.flow = flow;
    }

    public Double getReliabilityCode() {
        return reliabilityCode;
    }

    public void setReliabilityCode(Double reliabilityCode) {
        this.reliabilityCode = reliabilityCode;
    }

    public String getMultiType() {
        return multiType;
    }

    public void setMultiType(String multiType) {
        this.multiType = multiType;
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

    @Override
    public String toString() {
        return "InterfridlaneTpMultiflowDto{" +
                "statTime='" + statTime + '\'' +
                ", reliableDevcId='" + reliableDevcId + '\'' +
                ", reliableDevcTypeNo='" + reliableDevcTypeNo + '\'' +
                ", laneId='" + laneId + '\'' +
                ", rid='" + rid + '\'' +
                ", interId='" + interId + '\'' +
                ", stepIndex=" + stepIndex +
                ", flow=" + flow +
                ", reliabilityCode=" + reliabilityCode +
                ", multiType='" + multiType + '\'' +
                ", dt='" + dt + '\'' +
                ", tp='" + tp + '\'' +
                ", dataVersion='" + dataVersion + '\'' +
                ", adcode='" + adcode + '\'' +
                '}';
    }
}
