package cn.edu.neu.citybrain.dto;

import java.io.Serializable;

import cn.edu.neu.citybrain.function.CommonFunction;

public class TimeSlice implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long stepIndex;
	private String dt = "";
	private String tp;
	private long dataStartTime = -1;
	private Long winSize;
	
	public TimeSlice(Long stepIndex, String tp) {
		this.stepIndex = stepIndex;
		this.tp = tp;
		this.winSize = CommonFunction.getWinsize(tp);
	}
	
	public Long getStepIndex() {
		return stepIndex;
	}
	public void setStepIndex(Long stepIndex) {
		this.stepIndex = stepIndex;
	}
	public String getDt() {
		return this.dt;
	}
	public void setDt(String dt) {
		this.dt = dt;
	}
	public String getTp() {
		return tp;
	}
	public void setTp(String tp) {
		this.tp = tp;
	}
	public long getDataStartTime() {
		return this.dataStartTime;
	}
	public void setDataStartTime(long dataStartTime) {
		this.dataStartTime = dataStartTime;
	}
	public Long getWinSize() {
		return winSize;
	}
	public void setWinSize(Long winSize) {
		this.winSize = winSize;
	}
	
	public String getDataStartTimeStr() {
		return "";
	}
}
