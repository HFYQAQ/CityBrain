package cn.edu.neu.citybrain.metric.struct;

import cn.edu.neu.citybrain.util.CityBrainUtil;

public class Statistic {
    private String jobName;
    private long subtaskIndex;
    private String dt;
    private long stepIndex1mi;

    private long amount;
    private long duration;

    public Statistic() {

    }

    public Statistic(String jobName, int subtaskIndex, String dt, long stepIndex1mi, long amount, long duration) {
        this.jobName = jobName;
        this.subtaskIndex = subtaskIndex;
        this.dt = dt;
        this.stepIndex1mi = stepIndex1mi;
        this.amount = amount;
        this.duration = duration;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setSubtaskIndex(long subtaskIndex) {
        this.subtaskIndex = subtaskIndex;
    }

    public long getSubtaskIndex() {
        return subtaskIndex;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getDt() {
        return dt;
    }

    public void setStepIndex1mi(long stepIndex1mi) {
        this.stepIndex1mi = stepIndex1mi;
    }

    public long getStepIndex1mi() {
        return stepIndex1mi;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getAmount() {
        return amount;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public String getKeyJobDtIndex() {
        return CityBrainUtil.concat(jobName, dt, stepIndex1mi);
    }
}
