package cn.edu.neu.citybrain.dto;

public class RidInfo {
    private String rid;
    private Double length;
    private Double benchmarkSpeed;
    private Double benchmarkTravelTime;
    private Double benchmarkNostopSpeed;
    private Double benchmarkNostopTravelTime;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getBenchmarkSpeed() {
        return benchmarkSpeed;
    }

    public void setBenchmarkSpeed(Double benchmarkSpeed) {
        this.benchmarkSpeed = benchmarkSpeed;
    }

    public Double getBenchmarkTravelTime() {
        return benchmarkTravelTime;
    }

    public void setBenchmarkTravelTime(Double benchmarkTravelTime) {
        this.benchmarkTravelTime = benchmarkTravelTime;
    }
    
    public Double getBenchmarkNostopSpeed() {
        return benchmarkNostopSpeed;
    }

    public void setBenchmarkNostopSpeed(Double benchmarkNostopSpeed) {
        this.benchmarkNostopSpeed = benchmarkNostopSpeed;
    }

    public Double getBenchmarkNostopTravelTime() {
        return benchmarkNostopTravelTime;
    }

    public void setBenchmarkNostopTravelTime(Double benchmarkNostopTravelTime) {
        this.benchmarkNostopTravelTime = benchmarkNostopTravelTime;
    }
}