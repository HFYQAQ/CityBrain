package cn.edu.neu.citybrain.dto;

import java.util.Objects;

public class TurnGranularityUnit {
    private String interId;
    private String frid;
    private Long turnDirNo;

    public TurnGranularityUnit(String interId, String frid, Long turnDirNo) {
        this.interId = interId;
        this.frid = frid;
        this.turnDirNo = turnDirNo;
    }

    public void setInterId(String interId) {
        this.interId = interId;
    }

    public String getInterId() {
        return interId;
    }

    public void setFrid(String frid) {
        this.frid = frid;
    }

    public String getFrid() {
        return frid;
    }

    public void setTurnDirNo(Long turnDirNo) {
        this.turnDirNo = turnDirNo;
    }

    public Long getTurnDirNo() {
        return turnDirNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TurnGranularityUnit that = (TurnGranularityUnit) o;
        return Objects.equals(interId, that.interId) && Objects.equals(frid, that.frid) && Objects.equals(turnDirNo, that.turnDirNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interId, frid, turnDirNo);
    }
}
