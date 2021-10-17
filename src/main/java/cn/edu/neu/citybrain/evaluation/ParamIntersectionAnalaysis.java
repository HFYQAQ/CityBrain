package cn.edu.neu.citybrain.evaluation;

import cn.edu.neu.citybrain.dto.PeriodType;
import cn.edu.neu.citybrain.dto.ReliabilityRange;
import cn.edu.neu.citybrain.dto.TimePeriod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单路口分析时外部可传入的参数
 */
public class ParamIntersectionAnalaysis implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
     * 默认绿灯损失时间
     */
    private double defaultGreenLostTime = 5;
    /**
     * 默认车辆长度
     */
    private double defaultCarLength = 6.5;
    /**
     * 默认行车速度（公里/小时）
     */
    private int defaultRidSpeed = 40;
    /**
     * 夜间延误时间最大值
     */
    private int defaultLimitDelay = 60;
    /**
     * 停车次数最大值
     */
    private double maxStopcnt = 7.0;
    /**
     *     默认RID长度
     */
    private int defaultRidLen = 300;
    /**
     * 默认进行夜间延误时长限制开始时间
     */
    private String defaltLimitStartTime = "23:00";
    /**
     * 默认进行夜间延误时长限制结束时间
     */
    private String defaltLimitEndTime = "06:00";
    /**
     * 是否在没有配时数据时使用默认的红灯时间计算停车次数
     */
    private boolean isUseDefaultRedTime = true;
    /**
     * 起始限制延误时间片
     */
    private int limitStartIndex = 0;
    /**
     * 结束限制延误时间片
     */
    private int limitEndIndex = 72;
    /**
     * 每日最大时间片
     */
    private int maxStepIndex = 287;
    /**
     * 调试的frid
     */
    private List<String> debugFRids = new ArrayList<>();
    /**
     * 早晚高峰定义
     */
    protected Map<PeriodType, TimePeriod> dayPeriods = new HashMap<>();

    /**
     * 置信度的上下限
     */
    protected ReliabilityRange reliabityRange = new ReliabilityRange();

    /**
     * 是否保存旧表的数据
     */
    protected boolean isSaveOldTable = false;

    /**
     * 是否计算全部路口，如果是false,只计算dim
     */
    protected boolean isCalAllSignalInters = true;

    public ParamIntersectionAnalaysis() {
    }

    public double getDefaultGreenLostTime() {
        return defaultGreenLostTime;
    }

    public void setDefaultGreenLostTime(double defaultGreenLostTime) {
        this.defaultGreenLostTime = defaultGreenLostTime;
    }

    public double getDefaultCarLength() {
        return defaultCarLength;
    }

    public void setDefaultCarLength(double defaultCarLength) {
        this.defaultCarLength = defaultCarLength;
    }

    public int getDefaultRidSpeed() {
        return defaultRidSpeed;
    }

    public void setDefaultRidSpeed(int defaultRidSpeed) {
        this.defaultRidSpeed = defaultRidSpeed;
    }

    public int getDefaultLimitDelay() {
        return defaultLimitDelay;
    }

    public void setDefaultLimitDelay(int defaultLimitDelay) {
        this.defaultLimitDelay = defaultLimitDelay;
    }

    public String getDefaltLimitStartTime() {
        return defaltLimitStartTime;
    }

    public void setDefaltLimitStartTime(String defaltLimitStartTime) {
        this.defaltLimitStartTime = defaltLimitStartTime;
    }

    public String getDefaltLimitEndTime() {
        return defaltLimitEndTime;
    }

    public void setDefaltLimitEndTime(String defaltLimitEndTime) {
        this.defaltLimitEndTime = defaltLimitEndTime;
    }

    public boolean isUseDefaultRedTime() {
        return isUseDefaultRedTime;
    }

    public void setUseDefaultRedTime(boolean useDefaultRedTime) {
        isUseDefaultRedTime = useDefaultRedTime;
    }

    public int getLimitStartIndex() {
        return limitStartIndex;
    }

    public void setLimitStartIndex(int limitStartIndex) {
        this.limitStartIndex = limitStartIndex;
    }

    public int getLimitEndIndex() {
        return limitEndIndex;
    }

    public void setLimitEndIndex(int limitEndIndex) {
        this.limitEndIndex = limitEndIndex;
    }

    public int getMaxStepIndex() {
        return maxStepIndex;
    }

    public void setMaxStepIndex(int maxStepIndex) {
        this.maxStepIndex = maxStepIndex;
    }

    public List<String> getDebugFRids() {
        return debugFRids;
    }

    public void setDebugFRids(List<String> debugFRids) {
        this.debugFRids = debugFRids;
    }

    public int getDefaultRidLen() {
        return defaultRidLen;
    }

    public void setDefaultRidLen(int defaultRidLen) {
        this.defaultRidLen = defaultRidLen;
    }

    public Map<PeriodType, TimePeriod> getDayPeriods() {
        return dayPeriods;
    }

    public void setDayPeriods(Map<PeriodType, TimePeriod> dayPeriods) {
        this.dayPeriods = dayPeriods;
    }

    public ReliabilityRange getReliabityRange() {
        return reliabityRange;
    }

    public void setReliabityRange(ReliabilityRange reliabityRange) {
        this.reliabityRange = reliabityRange;
    }

    public boolean isSaveOldTable() {
        return isSaveOldTable;
    }

    public void setSaveOldTable(boolean saveOldTable) {
        isSaveOldTable = saveOldTable;
    }

    public boolean isCalAllSignalInters() {
        return isCalAllSignalInters;
    }

    public void setCalAllSignalInters(boolean calAllSignalInters) {
        isCalAllSignalInters = calAllSignalInters;
    }
    
    public Double getMaxStopcnt() {
        return maxStopcnt;
    }

    public void setMaxStopcnt(Double maxStopcnt) {
        this.maxStopcnt = maxStopcnt;
    }

    @Override
    public String toString() {
        return "ParamIntersectionAnalaysis{" +
                "defaultGreenLostTime=" + defaultGreenLostTime +
                ", defaultCarLength=" + defaultCarLength +
                ", defaultRidSpeed=" + defaultRidSpeed +
                ", defaultLimitDelay=" + defaultLimitDelay +
                ", defaltLimitStartTime='" + defaltLimitStartTime + '\'' +
                ", defaltLimitEndTime='" + defaltLimitEndTime + '\'' +
                ", isUseDefaultRedTime=" + isUseDefaultRedTime +
                ", limitStartIndex=" + limitStartIndex +
                ", limitEndIndex=" + limitEndIndex +
                ", maxStepIndex=" + maxStepIndex +
                ", reliabityRange=" + reliabityRange.toString() +
                '}';
    }
}

