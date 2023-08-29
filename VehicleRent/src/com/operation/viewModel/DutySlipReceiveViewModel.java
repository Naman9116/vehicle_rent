package com.operation.viewModel;

import java.util.Date;

public class DutySlipReceiveViewModel {
	
	private Long dutySlipId=null;
	private String hub;
	private String carNo;
	private String deviceStatus;
	private String allotedCar; 
	private String bookedCar; 
	private Date dutySlipDate;
	private String dutySlipNo;
	private String plan;
	private String corporate;
	private String bookedBy;
	private String usedBy;
	private String mobileNo=null;
	private Double tollAmount=null;
	private Double parkingAmount=null;
	private Double unBilledAmount=null;
	private String closedBy=null;
	private Date closedByDate=null;
	
	public Long getDutySlipId() {
		return dutySlipId;
	}
	public void setDutySlipId(Long dutySlipId) {
		this.dutySlipId = dutySlipId;
	}
	public String getHub() {
		return hub;
	}
	public void setHub(String hub) {
		this.hub = hub;
	}
	public String getCarNo() {
		return carNo;
	}
	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}
	
	public String getDeviceStatus() {
		return deviceStatus;
	}
	public void setDeviceStatus(String deviceStatus) {
		this.deviceStatus = deviceStatus;
	}
	public String getAllotedCar() {
		return allotedCar;
	}
	public void setAllotedCar(String allotedCar) {
		this.allotedCar = allotedCar;
	}
	public String getBookedCar() {
		return bookedCar;
	}
	public void setBookedCar(String bookedCar) {
		this.bookedCar = bookedCar;
	}
	public Date getDutySlipDate() {
		return dutySlipDate;
	}
	public void setDutySlipDate(Date dutySlipDate) {
		this.dutySlipDate = dutySlipDate;
	}
	public String getDutySlipNo() {
		return dutySlipNo;
	}
	public void setDutySlipNo(String dutySlipNo) {
		this.dutySlipNo = dutySlipNo;
	}
	public String getPlan() {
		return plan;
	}
	public void setPlan(String plan) {
		this.plan = plan;
	}
	public String getCorporate() {
		return corporate;
	}
	public void setCorporate(String corporate) {
		this.corporate = corporate;
	}
	public String getBookedBy() {
		return bookedBy;
	}
	public void setBookedBy(String bookedBy) {
		this.bookedBy = bookedBy;
	}
	public String getUsedBy() {
		return usedBy;
	}
	public void setUsedBy(String usedBy) {
		this.usedBy = usedBy;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public Double getTollAmount() {
		return tollAmount;
	}
	public void setTollAmount(Double tollAmount) {
		this.tollAmount = tollAmount;
	}
	public Double getParkingAmount() {
		return parkingAmount;
	}
	public void setParkingAmount(Double parkingAmount) {
		this.parkingAmount = parkingAmount;
	}
	public String getClosedBy() {
		return closedBy;
	}
	public void setClosedBy(String closedBy) {
		this.closedBy = closedBy;
	}
	public Date getClosedByDate() {
		return closedByDate;
	}
	public void setClosedByDate(Date closedByDate) {
		this.closedByDate = closedByDate;
	}
	public Double getUnBilledAmount() {
		return unBilledAmount;
	}
	public void setUnBilledAmount(Double unBilledAmount) {
		this.unBilledAmount = unBilledAmount;
	}
	
	

}
