package cn.edu.neu.citybrain.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class SigninterfridseqIndex implements Serializable {
    private static final long serialVersionUID = 1L;

    private String interId; //
    private String interName; //
    private String fRid; //
    private Long turnDirNo; //
    private String tRid;
    private Double fRidseqLen; // from ridInfo
    private String fRidseq; //
    private Double benchmarkNostopTravelTime;

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

    public String getfRid() {
        return fRid;
    }

    public void setfRid(String fRid) {
        this.fRid = fRid;
    }

    public Long getTurnDirNo() {
        return turnDirNo;
    }

    public void setTurnDirNo(Long turnDirNo) {
        this.turnDirNo = turnDirNo;
    }

    public String gettRid() {
        return tRid;
    }

    public void settRid(String tRid) {
        this.tRid = tRid;
    }

    public Double getfRidseqLen() {
        return fRidseqLen;
    }

    public void setfRidseqLen(Double fRidseqLen) {
        this.fRidseqLen = fRidseqLen;
    }

    public String getfRidseq() {
        return fRidseq;
    }

    public Double getBenchmarkNostopTravelTime() {
        return benchmarkNostopTravelTime;
    }

    public void setBenchmarkNostopTravelTime(Double benchmarkNostopTravelTime) {
        this.benchmarkNostopTravelTime = benchmarkNostopTravelTime;
    }

    public void setfRidseq(String fRidseq) {
        this.fRidseq = fRidseq;
    }

    /**
     * 返回进口道序列的所有rid
     * @return
     */
    public List<String> getRids(){
        String[] ridsArray = this.fRidseq.split("#");
        List<String> returnList = Arrays.asList(ridsArray);
        return returnList;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
