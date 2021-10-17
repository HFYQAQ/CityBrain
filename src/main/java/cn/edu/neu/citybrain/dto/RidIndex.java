package cn.edu.neu.citybrain.dto;

/**
 * rid的指标数据
 */
public class RidIndex {
    /**
     * rid
     */
    private String rid;
    /**
     * rid的行程时间
     */
    private Double travelTime;
    /**
     * rid速度
     */
    private Double speed;
    /**
     * 速度置信度
     */
    private Double reliabilityCode;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public Double getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Double travelTime) {
        this.travelTime = travelTime;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getReliabilityCode() {
        return reliabilityCode;
    }

    public void setReliabilityCode(Double reliabilityCode) {
        this.reliabilityCode = reliabilityCode;
    }
}

