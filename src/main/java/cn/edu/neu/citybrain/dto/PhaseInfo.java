package cn.edu.neu.citybrain.dto;

import java.util.Objects;

public class PhaseInfo {
    private String interId;
    private String fRid;
    private String turnDirNo;
    
    private String phasePlanId;
    private String phaseName;

    public String getInterId() {
        return interId;
    }

    public void setInterId(String interId) {
        this.interId = interId;
    }

    public String getfRid() {
		return fRid;
	}

	public void setfRid(String fRid) {
		this.fRid = fRid;
	}

	public String getTurnDirNo() {
		return turnDirNo;
	}

	public void setTurnDirNo(String turnDirNo) {
		this.turnDirNo = turnDirNo;
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

    public PhaseInfo() {
    }

    public PhaseInfo(String interId, String phasePlanId, String phaseName) {
        this.interId = interId;
        this.phasePlanId = phasePlanId;
        this.phaseName = phaseName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof PhaseInfo)) {return false;}
        PhaseInfo phaseInfo = (PhaseInfo) o;
        return Objects.equals(getInterId(), phaseInfo.getInterId()) &&
                Objects.equals(getPhasePlanId(), phaseInfo.getPhasePlanId()) &&
                Objects.equals(getPhaseName(), phaseInfo.getPhaseName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInterId(), getPhasePlanId(), getPhaseName());
    }

	@Override
	public String toString() {
		return "PhaseInfo [interId=" + interId + ", phasePlanId=" + phasePlanId
				+ ", phaseName=" + phaseName + "]";
	}
}