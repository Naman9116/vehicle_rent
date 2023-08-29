package com.billing.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.operation.model.CarDetailModel;

@Entity
@Table(name="invoiceDetail")
public class InvoiceDetailModel implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@ManyToOne(targetEntity=InvoiceModel.class)
	@JoinColumn(name="invoiceId",referencedColumnName="id",nullable=false,updatable=true)
	private InvoiceModel invoiceModel = null;
	
	@Column(name="brName",updatable=true,nullable=true,length=50,unique=false)
	private String brName=null;

	@Column(name="branchId",updatable=true,nullable=true,length=20,unique=false)
	private Long branchId=null;

	@Column(name="hubName",updatable=true,nullable=true,length=50,unique=false)
	private String hubName=null;

	@Column(name="hubId",updatable=true,nullable=true,length=50,unique=false)
	private Long hubId=null;

	@Column(name="refNo",updatable=true,nullable=true,length=50,unique=false)
	private String refNo=null;
	
	@Column(name="dsNo",updatable=true,nullable=true,length=50,unique=false)
	private String dsNo=null;

	@Column(name="dsDt",updatable=true,nullable=true,length=25,unique=false)
	private Date dsDt=null;

	@Column(name="vehUsedDt",updatable=true,nullable=true,length=25,unique=false)
	private Date vehUsedDt=null;
	
	@Column(name="vehUsedAt",updatable=true,nullable=true,length=500,unique=false)
	private String vehUsedAt=null;

	@Column(name="bookedBy",updatable=true,nullable=true,length=50,unique=false)
	private String bookedBy=null;
	
	@Column(name="bookedById",updatable=true,nullable=true,length=20,unique=false)
	private Long bookedById=null;

	@Column(name="usedBy",updatable=true,nullable=true,length=100,unique=false)
	private String usedBy=null;

	@Column(name="vehBooked",updatable=true,nullable=true,length=50,unique=false)
	private String vehBooked=null;
	
	@Column(name="vehUsed",updatable=true,nullable=true,length=50,unique=false)
	private String vehUsed=null;

	@Column(name="vehNo",updatable=true,nullable=true,length=20,unique=false)
	private String vehNo=null;

	@Column(name="tariffName",updatable=true,nullable=true,length=50,unique=false)
	private String tariffName=null;
	
	@Column(name="tariffId",updatable=true,nullable=true,length=20,unique=false)
	private Long tariffId=null;

	@Column(name="basicFair",updatable=true,nullable=true,length=15,unique=false)
	private Double basicFair=0.00;

	@Column(name="totalBasicFair",updatable=true,nullable=true,length=15,unique=false)
	private Double totalBasicFair=0.00;
	
	@Column(name="rentalType",updatable=true,nullable=true,length=50,unique=false)
	private String rentalType=null;

	@Column(name="rentalTypeId",updatable=true,nullable=true,length=20,unique=false)
	private Long rentalTypeId=null;

	@Column(name="startTime",updatable=true,nullable=true,length=10,unique=false)
	private String startTime=null;

	@Column(name="closeTime",updatable=true,nullable=true,length=10,unique=false)
	private String closeTime=null;

	@Column(name="extraHr",updatable=true,nullable=true,length=10,unique=false)
	private String extraHr=null;

	@Column(name="extraHrRate",updatable=true,nullable=true,length=15,unique=false)
	private Double extraHrRate=0.00;

	@Column(name="extraHrTotal",updatable=true,nullable=true,length=15,unique=false)
	private Double extraHrTotal=0.00;

	@Column(name="openingKm",updatable=true,nullable=true,length=15,unique=false)
	private Long openingKm=null;
	
	@Column(name="closingKm",updatable=true,nullable=true,length=15,unique=false)
	private Long closingKm=null;

	@Column(name="extraKm",updatable=true,nullable=true,length=10,unique=false)
	private String extraKm=null;

	@Column(name="extraKmRate",updatable=true,nullable=true,length=15,unique=false)
	private Double extraKmRate=0.00;

	@Column(name="extraKmTotal",updatable=true,nullable=true,length=15,unique=false)
	private Double extraKmTotal=0.00;

	@Column(name="stateTax",updatable=true,nullable=true,length=15,unique=false)
	private Double stateTax=0.00;

	@Column(name="parking",updatable=true,nullable=true,length=15,unique=false)
	private Double parking=0.00;

	@Column(name="toll",updatable=true,nullable=true,length=15,unique=false)
	private Double toll=0.00;

	@Column(name="night",updatable=true,nullable=true,length=10,unique=false)
	private int night=0;

	@Column(name="nightAllow",updatable=true,nullable=true,length=15,unique=false)
	private Double nightAllow=0.00;

	@Column(name="nightAllowTotal",updatable=true,nullable=true,length=15,unique=false)
	private Double nightAllowTotal=0.00;

	@Column(name="days",updatable=true,nullable=true,length=10,unique=false)
	private Long days=1l;

	@Column(name="dayAllow",updatable=true,nullable=true,length=15,unique=false)
	private Double dayAllow=0.00;

	@Column(name="dayAllowTotal",updatable=true,nullable=true,length=15,unique=false)
	private Double dayAllowTotal=0.00;

	@Column(name="miscAllowTotal",updatable=true,nullable=true,length=15,unique=false)
	private Double miscAllowTotal=0.00;

	@Column(name="guideCharge",updatable=true,nullable=true,length=15,unique=false)
	private Double guideCharge=0.00;
	
	@Column(name="kmUsed",updatable=true,nullable=true,length=15,unique=false)
	private Double kmUsed=0.00;
	
	@Column(name="hrsUsed",updatable=true,nullable=true,length=15,unique=false)
	private String hrsUsed=null;

	@Column(name="taxHeading",updatable=true,nullable=true,length=250,unique=false)
	private String taxHeading=null;

	@Column(name="taxValues",updatable=true,nullable=true,length=100,unique=false)
	private String taxValues=null;

	@Column(name="totalAmt",updatable=true,nullable=true,length=15,unique=false)
	private Double totalAmt=0.00;

	@Column(name="grandTotal",updatable=true,nullable=true,length=15,unique=false)
	private Double grandTotal=0.00;

	@Column(name="remark",updatable=true,nullable=true,length=200,unique=false)
	private String remark=null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InvoiceModel getInvoiceModel() {
		return invoiceModel;
	}

	public void setInvoiceModel(InvoiceModel invoiceModel) {
		this.invoiceModel = invoiceModel;
	}
	
	public String getDsNo() {
		return dsNo;
	}

	public void setDsNo(String dsNo) {
		this.dsNo = dsNo;
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

	public String getVehBooked() {
		return vehBooked;
	}

	public void setVehBooked(String vehBooked) {
		this.vehBooked = vehBooked;
	}

	public String getVehUsed() {
		return vehUsed;
	}

	public void setVehUsed(String vehUsed) {
		this.vehUsed = vehUsed;
	}

	public String getVehNo() {
		return vehNo;
	}

	public void setVehNo(String vehNo) {
		this.vehNo = vehNo;
	}

	public String getTariffName() {
		return tariffName;
	}

	public void setTariffName(String tariffName) {
		this.tariffName = tariffName;
	}

	public String getExtraHr() {
		return extraHr;
	}

	public void setExtraHr(String extraHr) {
		this.extraHr = extraHr;
	}

	public Double getExtraHrRate() {
		return extraHrRate;
	}

	public void setExtraHrRate(Double extraHrRate) {
		this.extraHrRate = extraHrRate;
	}

	public Double getExtraHrTotal() {
		return extraHrTotal;
	}

	public void setExtraHrTotal(Double extraHrTotal) {
		this.extraHrTotal = extraHrTotal;
	}

	public String getExtraKm() {
		return extraKm;
	}

	public void setExtraKm(String extraKm) {
		this.extraKm = extraKm;
	}

	public Double getExtraKmRate() {
		return extraKmRate;
	}

	public void setExtraKmRate(Double extraKmRate) {
		this.extraKmRate = extraKmRate;
	}

	public Double getExtraKmTotal() {
		return extraKmTotal;
	}

	public void setExtraKmTotal(Double extraKmTotal) {
		this.extraKmTotal = extraKmTotal;
	}

	public Double getParking() {
		return parking;
	}

	public void setParking(Double parking) {
		this.parking = parking;
	}

	public Double getToll() {
		return toll;
	}

	public void setToll(Double toll) {
		this.toll = toll;
	}

	public int getNight() {
		return night;
	}

	public void setNight(int night) {
		this.night = night;
	}

	public Double getNightAllow() {
		return nightAllow;
	}

	public void setNightAllow(Double nightAllow) {
		this.nightAllow = nightAllow;
	}

	public Double getNightAllowTotal() {
		return nightAllowTotal;
	}

	public void setNightAllowTotal(Double nightAllowTotal) {
		this.nightAllowTotal = nightAllowTotal;
	}

	public Long getDays() {
		return days;
	}

	public void setDays(Long days) {
		this.days = days;
	}

	public Double getDayAllow() {
		return dayAllow;
	}

	public void setDayAllow(Double dayAllow) {
		this.dayAllow = dayAllow;
	}

	public Double getDayAllowTotal() {
		return dayAllowTotal;
	}

	public void setDayAllowTotal(Double dayAllowTotal) {
		this.dayAllowTotal = dayAllowTotal;
	}

	public Double getMiscAllowTotal() {
		return miscAllowTotal;
	}

	public void setMiscAllowTotal(Double miscAllowTotal) {
		this.miscAllowTotal = miscAllowTotal;
	}

	public Double getKmUsed() {
		return kmUsed;
	}

	public void setKmUsed(Double kmUsed) {
		this.kmUsed = kmUsed;
	}

	public String getHrsUsed() {
		return hrsUsed;
	}

	public void setHrsUsed(String hrsUsed) {
		this.hrsUsed = hrsUsed;
	}

	public Double getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(Double totalAmt) {
		this.totalAmt = totalAmt;
	}

	public Double getBasicFair() {
		return basicFair;
	}

	public void setBasicFair(Double basicFair) {
		this.basicFair = basicFair;
	}

	public Double getTotalBasicFair() {
		return totalBasicFair;
	}

	public void setTotalBasicFair(Double totalBasicFair) {
		this.totalBasicFair = totalBasicFair;
	}

	public String getRentalType() {
		return rentalType;
	}

	public void setRentalType(String rentalType) {
		this.rentalType = rentalType;
	}

	public Double getStateTax() {
		return stateTax;
	}

	public void setStateTax(Double stateTax) {
		this.stateTax = stateTax;
	}

	public String getVehUsedAt() {
		return vehUsedAt;
	}

	public void setVehUsedAt(String vehUsedAt) {
		this.vehUsedAt = vehUsedAt;
	}

	public void setDsDt(Date dsDt) {
		this.dsDt = dsDt;
	}

	public void setVehUsedDt(Date vehUsedDt) {
		this.vehUsedDt = vehUsedDt;
	}

	public Date getDsDt() {
		return dsDt;
	}

	public Date getVehUsedDt() {
		return vehUsedDt;
	}

	public Long getBookedById() {
		return bookedById;
	}

	public void setBookedById(Long bookedById) {
		this.bookedById = bookedById;
	}

	public Long getTariffId() {
		return tariffId;
	}

	public void setTariffId(Long tariffId) {
		this.tariffId = tariffId;
	}

	public Long getRentalTypeId() {
		return rentalTypeId;
	}

	public void setRentalTypeId(Long rentalTypeId) {
		this.rentalTypeId = rentalTypeId;
	}

	public String getBrName() {
		return brName;
	}

	public void setBrName(String brName) {
		this.brName = brName;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public String getHubName() {
		return hubName;
	}

	public void setHubName(String hubName) {
		this.hubName = hubName;
	}

	public Long getHubId() {
		return hubId;
	}

	public void setHubId(Long hubId) {
		this.hubId = hubId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}

	public Long getOpeningKm() {
		return openingKm;
	}

	public void setOpeningKm(Long openingKm) {
		this.openingKm = openingKm;
	}

	public Long getClosingKm() {
		return closingKm;
	}

	public void setClosingKm(Long closingKm) {
		this.closingKm = closingKm;
	}

	public String getTaxHeading() {
		return taxHeading;
	}

	public void setTaxHeading(String taxHeading) {
		this.taxHeading = taxHeading;
	}

	public String getTaxValues() {
		return taxValues;
	}

	public void setTaxValues(String taxValues) {
		this.taxValues = taxValues;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Double getGuideCharge() {
		return guideCharge;
	}

	public void setGuideCharge(Double guideCharge) {
		this.guideCharge = guideCharge;
	}

	public Double getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(Double grandTotal) {
		this.grandTotal = grandTotal;
	}

}
