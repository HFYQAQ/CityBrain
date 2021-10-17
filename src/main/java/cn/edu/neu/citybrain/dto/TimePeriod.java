package cn.edu.neu.citybrain.dto;

/**
 * 描述一个时间段
 */
public class TimePeriod {
    private String startTime;
    private String endTime;

    public TimePeriod(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public long getStartStepIndex(long winSize) {
        return getStepIndex(startTime, winSize);
    }

    public long getEndStepIndex(long winSize) {
        return getStepIndex(endTime, winSize);

    }

    /**
     * 判断指定的时间片是否属于当前时间段
     * @param stepIndex
     * @param winSize
     * @return
     */
    public boolean isBelong(long stepIndex, long winSize){
        long startIndex = getStartStepIndex(winSize);
        long endIndex = getEndStepIndex(winSize);
        if(startIndex <= endIndex){
            if(stepIndex >= startIndex && stepIndex <= endIndex){
                return true;
            }else {
                return false;
            }
        }else {
            if(stepIndex >= startIndex || stepIndex <= endIndex){
                return  true;
            }else {
                return false;
            }
        }
    }

    private long getStepIndex(String timeStr, long winSize){
        String[] splitTime = timeStr.split(":");
        long stepIndex = (Long.valueOf(splitTime[0]) * 3600 + Long.valueOf(splitTime[1]) * 60) / winSize;
        return stepIndex;
    }
}