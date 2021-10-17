package cn.edu.neu.citybrain.dto;
import com.google.gson.JsonObject;

public class CycleSplit {
    private Double splitTime;
    private Double cycleTime;
    private Double redTime;
    private Double reliabilityCode;
    private String source;

    public Double getSplitTime() {
        return splitTime;
    }

    public void setSplitTime(Double splitTime) {
        this.splitTime = splitTime;
    }

    public Double getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(Double cycleTime) {
        this.cycleTime = cycleTime;
    }

    public Double getRedTime() {
        return redTime;
    }

    public void setRedTime(Double redTime) {
        this.redTime = redTime;
    }

    public Double getReliabilityCode() {
        return reliabilityCode;
    }

    public void setReliabilityCode(Double reliabilityCode) {
        this.reliabilityCode = reliabilityCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("cycle_time", cycleTime);
        obj.addProperty("split_time", splitTime);
        obj.addProperty("red_time", redTime);
        obj.addProperty("reliability", reliabilityCode);
        obj.addProperty("source", source);
        return obj;
    }

    @Override
    public String toString() {
        return "CycleSplit{" +
                "splitTime=" + splitTime +
                ", cycleTime=" + cycleTime +
                ", redTime=" + redTime +
                '}';
    }
}
