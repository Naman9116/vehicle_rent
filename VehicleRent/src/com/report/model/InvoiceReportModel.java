package com.report.model;


import java.util.Date;

public class InvoiceReportModel {

	public InvoiceReportModel(){
		
	}
	public String heading;
	public String branchOffice;
	public String corporateOffice;
	
	public String customerName;
	public String invoiceNumber;
	public Date invoiceDate;
	public String dutySlipNo;
	public Date carUsedDate;
	public String bookedBy;
	public String usedBy;
	public String carBooked;
	public String carAlloted;
	public String carNo;
	public String chargeFor;
	public String tariff;
	public Integer extraHour;
	public Double extraHourRate;
	public Integer extraKms;
	public Double extraKmsRate;
	public Double outstationAllowence;
	public Double nightAllowence;
	public Double miscCharges;
	public Double parkingAndTollCharges;
	public Double interstateTaxes;
		
	public Integer kmsUsed;
	public Integer hrsUsed;
	public String vehicleUsedAt;
	
	public String getHeading() {
		return heading;
	}
	public void setHeading(String heading) {
		this.heading = heading;
	}
	public String getBranchOffice() {
		return branchOffice;
	}
	public void setBranchOffice(String branchOffice) {
		this.branchOffice = branchOffice;
	}
	public String getCorporateOffice() {
		return corporateOffice;
	}
	public void setCorporateOffice(String corporateOffice) {
		this.corporateOffice = corporateOffice;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	public Date getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public String getDutySlipNo() {
		return dutySlipNo;
	}
	public void setDutySlipNo(String dutySlipNo) {
		this.dutySlipNo = dutySlipNo;
	}
	public Date getCarUsedDate() {
		return carUsedDate;
	}
	public void setCarUsedDate(Date carUsedDate) {
		this.carUsedDate = carUsedDate;
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
	public String getCarBooked() {
		return carBooked;
	}
	public void setCarBooked(String carBooked) {
		this.carBooked = carBooked;
	}
	public String getCarAlloted() {
		return carAlloted;
	}
	public void setCarAlloted(String carAlloted) {
		this.carAlloted = carAlloted;
	}
	public String getCarNo() {
		return carNo;
	}
	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}
	public String getChargeFor() {
		return chargeFor;
	}
	public void setChargeFor(String chargeFor) {
		this.chargeFor = chargeFor;
	}
	public String getTariff() {
		return tariff;
	}
	public void setTariff(String tariff) {
		this.tariff = tariff;
	}
	public Integer getExtraHour() {
		return extraHour;
	}
	public void setExtraHour(Integer extraHour) {
		this.extraHour = extraHour;
	}
	public Double getExtraHourRate() {
		return extraHourRate;
	}
	public void setExtraHourRate(Double extraHourRate) {
		this.extraHourRate = extraHourRate;
	}
	public Integer getExtraKms() {
		return extraKms;
	}
	public void setExtraKms(Integer extraKms) {
		this.extraKms = extraKms;
	}
	public Double getExtraKmsRate() {
		return extraKmsRate;
	}
	public void setExtraKmsRate(Double extraKmsRate) {
		this.extraKmsRate = extraKmsRate;
	}
	public Double getOutstationAllowence() {
		return outstationAllowence;
	}
	public void setOutstationAllowence(Double outstationAllowence) {
		this.outstationAllowence = outstationAllowence;
	}
	public Double getNightAllowence() {
		return nightAllowence;
	}
	public void setNightAllowence(Double nightAllowence) {
		this.nightAllowence = nightAllowence;
	}
	public Double getMiscCharges() {
		return miscCharges;
	}
	public void setMiscCharges(Double miscCharges) {
		this.miscCharges = miscCharges;
	}
	public Double getParkingAndTollCharges() {
		return parkingAndTollCharges;
	}
	public void setParkingAndTollCharges(Double parkingAndTollCharges) {
		this.parkingAndTollCharges = parkingAndTollCharges;
	}
	public Double getInterstateTaxes() {
		return interstateTaxes;
	}
	public void setInterstateTaxes(Double interstateTaxes) {
		this.interstateTaxes = interstateTaxes;
	}
	public Integer getKmsUsed() {
		return kmsUsed;
	}
	public void setKmsUsed(Integer kmsUsed) {
		this.kmsUsed = kmsUsed;
	}
	public Integer getHrsUsed() {
		return hrsUsed;
	}
	public void setHrsUsed(Integer hrsUsed) {
		this.hrsUsed = hrsUsed;
	}
	public String getVehicleUsedAt() {
		return vehicleUsedAt;
	}
	public void setVehicleUsedAt(String vehicleUsedAt) {
		this.vehicleUsedAt = vehicleUsedAt;
	}
	
	
	
	public InvoiceReportModel (String branchOffice,String corporateOffice,String customerName, String invoiceNumber, Date invoiceDate,
			String dutySlipNo, Date carUsedDate, String bookedBy, String usedBy, String carBooked, String carAlloted,
			String carNo, String chargeFor, String tariff, Integer extraHour, Double extraHourRate, Integer extraKms,
			Double extraKmsRate, Double outstationAllowence, Double nightAllowence, Double miscCharges,
			Double parkingAndTollCharges, Double interstateTaxes, Integer kmsUsed, Integer hrsUsed, String vehicleUsedAt) {
		this.branchOffice=branchOffice;
		this.corporateOffice=corporateOffice;
		this.customerName=customerName;
		this.invoiceNumber=invoiceNumber;
		this.invoiceDate=invoiceDate;
		this.dutySlipNo=dutySlipNo;
		this.carUsedDate=carUsedDate;
		this.bookedBy=bookedBy;
		this.usedBy=usedBy;
		this.carBooked=carBooked;
		this.carAlloted=carAlloted;
		this.carNo=carNo;
		this.chargeFor=chargeFor;
		this.tariff=tariff;
		this.extraHour=extraHour;
		this.extraHourRate=extraHourRate;
		this.extraKms=extraKms;
		this.extraKmsRate=extraKmsRate;
		this.outstationAllowence=outstationAllowence;
		this.nightAllowence=nightAllowence;
		this.miscCharges=miscCharges;
		this.parkingAndTollCharges=parkingAndTollCharges;
		this.interstateTaxes=interstateTaxes;
		this.kmsUsed=kmsUsed;
		this.hrsUsed=hrsUsed;
		this.vehicleUsedAt=vehicleUsedAt;
	}


}
