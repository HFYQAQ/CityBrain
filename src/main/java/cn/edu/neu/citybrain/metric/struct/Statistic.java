package cn.edu.neu.citybrain.metric.struct;

public class Statistic {
    private String jobName;
    private int subtaskIndex;
    private String dt;
    private long stepIndex1mi;

    private long amount;
    private long duration;

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setSubtaskIndex(int subtaskIndex) {
        this.subtaskIndex = subtaskIndex;
    }

    public int getSubtaskIndex() {
        return subtaskIndex;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getDt() {
        return dt;
    }

    public void setStep_index(long stepIndex1mi) {
        this.stepIndex1mi = stepIndex1mi;
    }

    public long getStep_index() {
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
}
