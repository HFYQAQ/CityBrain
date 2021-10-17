package cn.edu.neu.citybrain.dto;

import com.google.gson.JsonObject;

/**
 * 数值和置信度
 * @author qiutian.wtt
 */
public class ValueAndReliability implements Cloneable{
    private Double value;
    private Double reliability;
    private JsonObject detail;

    public ValueAndReliability(Double value, Double reliability) {
    	this();
        this.value = value;
        this.reliability = reliability;
    }

    public ValueAndReliability() {
    	this.value = 0D;
    	this.reliability = 0D;
    	this.detail = new JsonObject();
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getReliability() {
        return reliability;
    }

    public void setReliability(Double reliability) {
        this.reliability = reliability;
    }
    
    public JsonObject getDetail() {
    	return detail;
    }
    
    public ValueAndReliability addDetail(String property, String value) {
    	detail.addProperty(property, value);
    	return this;
    }
    
    public ValueAndReliability addDetail(String property, Number value) {
    	detail.addProperty(property, value);
    	return this;
    }
    
    public ValueAndReliability addDetail(String property, ValueAndReliability value) {
    	JsonObject detail = new JsonObject();
    	detail.addProperty("value", value.value);
    	detail.addProperty("reliability", value.reliability);
    	if (!"{}".equals(value.detail.toString()))
    		detail.add("detail", value.detail);
    	
    	this.detail.add(property, detail);
    	return this;
    }
}
