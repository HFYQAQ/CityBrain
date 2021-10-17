package cn.edu.neu.citybrain.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 路口实际执行的信号配时方案
 */
public class InterSignalOperPlan {
    private PhaseInfo phaseInfo;
    /**
     * 配时方案号
     */
    private String timePlanId = "";
    
    /**
     * 信号周期开始时间
     */
    private String cycleStartTime;
	/**
	 * 相位开始时间
	 */
	private String startTime;
	/**
	 * 相位结束时间
	 */
	private String endTime;
	/**
	 * 绿信时长
	 */
    private Long splitTime;
    /**
     * 相位方案周期时长，秒
     */
    private Long cycleTime;
    /**
     * 相位阶段绿灯时长,秒
     */
	private Long greenTime;
	/**
	 * 数据来源，SCATS或者人工录入
	 */
	private String source;
    
    private String interId;
    private String interName;
    /**
     * 相位方案号
     */
    private String phasePlanId;
    /**
     * 相位名称/阶段名称
     */
	private String phaseName;
	
    public PhaseInfo getPhaseInfo() {
    	if (this.phaseInfo == null) {
    		this.phaseInfo = new PhaseInfo(interId, phasePlanId, phaseName);
    	}
		return phaseInfo;
	}

	public void setPhaseInfo(PhaseInfo phaseInfo) {
		this.phaseInfo = phaseInfo;
	}

	public String getTimePlanId() {
		return timePlanId;
	}

	public void setTimePlanId(String timePlanId) {
		this.timePlanId = timePlanId;
	}

	public String getCycleStartTime() {
		return cycleStartTime;
	}

	public void setCycleStartTime(String cycleStartTime) {
		this.cycleStartTime = cycleStartTime;
	}
	
	public Long getCycleStartTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			return sdf.parse(this.cycleStartTime).getTime();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Long getSplitTime() {
		return splitTime;
	}

	public void setSplitTime(Long splitTime) {
		this.splitTime = splitTime;
	}

	public Long getCycleTime() {
		return cycleTime;
	}

	public void setCycleTime(Long cycleTime) {
		this.cycleTime = cycleTime;
	}
	
	public Long getGreenTime() {
		return greenTime;
	}

	public void setGreenTime(Long greenTime) {
		this.greenTime = greenTime;
	}
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getInterId() {
		return interId;
	}

	public void setInterId(String interId) {
		this.interId = interId;
	}
	
	public String getInterName() {
		return interName;
	}

	public void setInterName(String interName) {
		this.interName = interName;
	}

	public String getPhasePlanId() {
		return phasePlanId;
	}

	public void setPhasePlanId(String phasePlanId) {
		this.phasePlanId = phasePlanId;
	}

	public String getPhaseName() {
		return phaseName;
	}

	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}
}

