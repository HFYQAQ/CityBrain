package cn.edu.neu.citybrain.dto;

import java.io.Serializable;

public class ReliabilityRange implements Serializable {
	private static final long serialVersionUID = 1L;
	
    private Double queueUpperLimit = 70.0;
    private Double queueLowerLimit = 20.0;
    private Double stopCntUpperLimit = 70.0;
    private Double stopCntLowerLimit = 20.0;
    public Double getQueueUpperLimit() {
        return queueUpperLimit;
    }

    public void setQueueUpperLimit(Double queueUpperLimit) {
        this.queueUpperLimit = queueUpperLimit;
    }

    public Double getQueueLowerLimit() {
        return queueLowerLimit;
    }

    public void setQueueLowerLimit(Double queueLowerLimit) {
        this.queueLowerLimit = queueLowerLimit;
    }

    public Double getStopCntUpperLimit() {
        return stopCntUpperLimit;
    }

    public void setStopCntUpperLimit(Double stopCntUpperLimit) {
        this.stopCntUpperLimit = stopCntUpperLimit;
    }

    public Double getStopCntLowerLimit() {
        return stopCntLowerLimit;
    }

    public void setStopCntLowerLimit(Double stopCntLowerLimit) {
        this.stopCntLowerLimit = stopCntLowerLimit;
    }

    @Override
    public String toString() {
        return "ReliabilityRange{" +
                "queueUpperLimit=" + queueUpperLimit +
                ", queueLowerLimit=" + queueLowerLimit +
                ", stopCntUpperLimit=" + stopCntUpperLimit +
                ", stopCntLowerLimit=" + stopCntLowerLimit +
                '}';
    }
}