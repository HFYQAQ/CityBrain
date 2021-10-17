package cn.edu.neu.citybrain.dto;

import com.google.gson.JsonObject;

public class AlgorithmDetail {
    /**
     * 行程时间的数据来源
     */
    private String travelTimeSource = "";
    /**
     * 行程时间的算法模型
     */
    private String travelTimeAlgType = "";
    private JsonObject travelTimeAlgDetail = new JsonObject();
    
    /**
     * 延误时长的数据来源
     */
    private String delayDurSource = "";
    /**
     * 延误时长的算法模型
     */
    private String delayDurAlgType = "";
    private JsonObject delayDurAlgDetail = new JsonObject();
    
    /**
     * 停车次数的数据来源
     */
    private String stopCntSource = "";
    /**
     * 停车次数的算法模型
     */
    private String stopCntAlgType = "";
    private JsonObject stopCntAlgDetail = new JsonObject();
    
    /**
     * 排队长度的数据来源
     */
    private String queueLenSource = "";
    /**
     * 排队长度的算法模型
     */
    private String queueLenAlgType = "";
    private JsonObject queueLenAlgDetail = new JsonObject();

    /**
     * 停车时长的数据来源
     */
    private String stopDurSource = "";
    /**
     * 停车时长的算法模型
     */
    private String stopDurAlgType = "";
    private JsonObject stopDurAlgDetail = new JsonObject();

    public String getTravelTimeSource() {
        return travelTimeSource;
    }

    public void setTravelTimeSource(String travelTimeSource) {
        this.travelTimeSource = travelTimeSource;
    }

    public void setDelayDurSource(String delayDurSource) {
        this.delayDurSource = delayDurSource;
    }

    public void setStopCntSource(String stopCntSource) {
        this.stopCntSource = stopCntSource;
    }

    public void setQueueLenSource(String queueLenSource) {
        this.queueLenSource = queueLenSource;
    }

    public void addTravelTimeSource(String travelTimeSource) {
        if("".equals(this.travelTimeSource)) {
            this.travelTimeSource = travelTimeSource;
        } else {
            if(!this.travelTimeSource.contains(travelTimeSource)) {
                this.travelTimeSource = this.travelTimeSource + "#" + travelTimeSource;
            }
        }
    }

    public String getTravelTimeAlgType() {
        return travelTimeAlgType;
    }

    public void setTravelTimeAlgType(String travelTimeAlgType) {
        this.travelTimeAlgType = travelTimeAlgType;
    }

    public String getDelayDurSource() {
        return delayDurSource;
    }

    public void addDelayDurSource(String delayDurSource) {
        if ("".equals(this.delayDurSource)) {
            this.delayDurSource = delayDurSource;
        } else {
            if(!this.delayDurSource.contains(delayDurSource)) {
                this.delayDurSource = this.delayDurSource + "#" + delayDurSource;
            }
        }
    }

    public String getDelayDurAlgType() {
        return delayDurAlgType;
    }

    public void setDelayDurAlgType(String delayDurAlgType) {
        this.delayDurAlgType = delayDurAlgType;
    }

    public String getStopCntSource() {
        return stopCntSource;
    }

    public void addStopCntSource(String stopCntSource) {
        if ("".equals(this.stopCntSource)) {
            this.stopCntSource = stopCntSource;
        } else {
            if(!this.stopCntSource.contains(stopCntSource)) {
                this.stopCntSource = this.stopCntSource + "#" + stopCntSource;
            }
        }
    }

    public String getStopCntAlgType() {
        return stopCntAlgType;
    }

    public void setStopCntAlgType(String stopCntAlgType) {
        this.stopCntAlgType = stopCntAlgType;
    }

    public String getQueueLenSource() {
        return queueLenSource;
    }

    public void addQueueLenSource(String queueLenSource) {
        if ("".equals(this.queueLenSource)) {
            this.queueLenSource = queueLenSource;
        } else {
            if(!this.queueLenSource.contains(queueLenSource)) {
                this.queueLenSource = this.queueLenSource + "#" + queueLenSource;
            }
        }
    }

    public String getQueueLenAlgType() {
        return queueLenAlgType;
    }

    public void setQueueLenAlgType(String queueLenAlgType) {
        this.queueLenAlgType = queueLenAlgType;
    }

    public String getStopDurSource() {
        return stopDurSource;
    }

    public void setStopDurSource(String stopDurSource) {
        this.stopDurSource = stopDurSource;
    }

    public String getStopDurAlgType() {
        return stopDurAlgType;
    }

    public void setStopDurAlgType(String stopDurAlgType) {
        this.stopDurAlgType = stopDurAlgType;
    }

	public JsonObject getTravelTimeAlgDetail() {
		return travelTimeAlgDetail;
	}

	public void setTravelTimeAlgDetail(JsonObject travelTimeAlgDetail) {
		this.travelTimeAlgDetail = travelTimeAlgDetail;
	}

	public JsonObject getDelayDurAlgDetail() {
		return delayDurAlgDetail;
	}

	public void setDelayDurAlgDetail(JsonObject delayDurAlgDetail) {
		this.delayDurAlgDetail = delayDurAlgDetail;
	}

	public JsonObject getStopCntAlgDetail() {
		return stopCntAlgDetail;
	}

	public void setStopCntAlgDetail(JsonObject stopCntAlgDetail) {
		this.stopCntAlgDetail = stopCntAlgDetail;
	}

	public JsonObject getQueueLenAlgDetail() {
		return queueLenAlgDetail;
	}

	public void setQueueLenAlgDetail(JsonObject queueLenAlgDetail) {
		this.queueLenAlgDetail = queueLenAlgDetail;
	}

	public JsonObject getStopDurAlgDetail() {
		return stopDurAlgDetail;
	}

	public void setStopDurAlgDetail(JsonObject stopDurAlgDetail) {
		this.stopDurAlgDetail = stopDurAlgDetail;
	}
}
