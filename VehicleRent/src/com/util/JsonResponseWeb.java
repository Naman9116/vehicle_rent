package com.util;

import com.corporate.model.AutorizedUserModel;
import com.master.model.ChauffeurModel;

public class JsonResponseWeb {
	private String status;
	private String result;
	private String reason;
	private String carRegNo;
	
	private AutorizedUserModel autorizedUserModel;
	private ChauffeurModel chauffeurModel;
	private Long chauffeurDutySlipCount;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public AutorizedUserModel getAutorizedUserModel() {
		return autorizedUserModel;
	}
	public void setAutorizedUserModel(AutorizedUserModel autorizedUserModel) {
		this.autorizedUserModel = autorizedUserModel;
	}
	public ChauffeurModel getChauffeurModel() {
		return chauffeurModel;
	}
	public void setChauffeurModel(ChauffeurModel chauffeurModel) {
		this.chauffeurModel = chauffeurModel;
	}
	public Long getChauffeurDutySlipCount() {
		return chauffeurDutySlipCount;
	}
	public void setChauffeurDutySlipCount(Long chauffeurDutySlipCount) {
		this.chauffeurDutySlipCount = chauffeurDutySlipCount;
	}
	public String getCarRegNo() {
		return carRegNo;
	}
	public void setCarRegNo(String carRegNo) {
		this.carRegNo = carRegNo;
	}
	
}
