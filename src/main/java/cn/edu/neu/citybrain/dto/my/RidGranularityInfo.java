package cn.edu.neu.citybrain.dto.my;

/**
 * 道路级相关信息
 */
public class RidGranularityInfo {
    private Double travelTime;
    private Double speed;
    private Double reliabilityCode;

    private Double len;

    public RidGranularityInfo(
            Double travelTime, Double speed, Double reliabilityCode,
            Double len) {
        this.travelTime = travelTime;
        this.speed = speed;
        this.reliabilityCode = reliabilityCode;

        this.len = len;
    }

    public void setTravelTime(Double travelTime) {
        this.travelTime = travelTime;
    }

    public Double getTravelTime() {
        return travelTime;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setReliabilityCode(Double reliabilityCode) {
        this.reliabilityCode = reliabilityCode;
    }

    public Double getReliabilityCode() {
        return reliabilityCode;
    }

    public void setLen(Double len) {
        this.len = len;
    }

    public Double getLen() {
        return len;
    }
}
