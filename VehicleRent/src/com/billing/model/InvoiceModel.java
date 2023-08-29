package com.billing.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name="invoice")
public class InvoiceModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@Column(name="compName",updatable=true,nullable=true,length=100,unique=false)
	private String compName=null;
	
	@Column(name="compPIN",updatable=true,nullable=true,length=6,unique=false)
	private int compPIN=0;

	@Column(name="compState",updatable=true,nullable=true,length=50,unique=false)
	private String compState=null;

	@Column(name="brName",updatable=true,nullable=true,length=50,unique=false)
	private String brName=null;

	@Column(name="compId",updatable=true,nullable=true,length=20,unique=false)
	private Long compId=null;

	@Column(name="branchId",updatable=true,nullable=true,length=20,unique=false)
	private Long branchId=null;

	@Column(name="hubName",updatable=true,nullable=true,length=50,unique=false)
	private String hubName=null;

	@Column(name="hubId",updatable=true,nullable=true,length=50,unique=false)
	private Long hubId=null;

	@Column(name="brGSTIN",updatable=true,nullable=true,length=20,unique=false)
	private String brGSTIN=null;

	@Column(name="brPAN",updatable=true,nullable=true,length=15,unique=false)
	private String brPAN=null;

	@Column(name="brOffice",updatable=true,nullable=true,length=500,unique=false)
	private String brOffice=null;

	@Column(name="headOffice",updatable=true,nullable=true,length=500,unique=false)
	private String headOffice=null;

	@Column(name="headCont",updatable=true,nullable=true,length=100,unique=false)
	private String headCont=null;

	@Column(name="headEmail",updatable=true,nullable=true,length=100,unique=false)
	private String headEmail=null;

	@Column(name="headWebsite",updatable=true,nullable=true,length=100,unique=false)
	private String headWebsite=null;

	@Column(name="corpName",updatable=true,nullable=true,length=100,unique=false)
	private String corpName=null;
	
	@Column(name="corpId",updatable=true,nullable=true,length=20,unique=false)
	private Long corpId=null;

	@Column(name="corpAddr",updatable=true,nullable=true,length=500,unique=false)
	private String corpAddr=null;

	@Column(name="paymentMode",updatable=true,nullable=true,length=50,unique=false)
	private String paymentMode=null;
	
	@Column(name="invoiceNo",updatable=true,nullable=true,length=50,unique=false)
	private String invoiceNo=null;

	@Column(name="refNo",updatable=true,nullable=true,length=50,unique=false)
	private String refNo=null;

	@Column(name="invoiceDt",updatable=true,nullable=true,length=25,unique=false)
	private Date invoiceDt=null;
	
	@Column(name="dsNo",updatable=true,nullable=true,length=50,unique=false)
	private String dsNo=null;

	@Column(name="dsDt",updatable=true,nullable=true,length=25,unique=false)
	private Date dsDt=null;

	@Column(name="vehUsedDt",updatable=true,nullable=true,length=25,unique=false)
	private Date vehUsedDt=null;
	
	@Column(name="vehUsedAt",updatable=true,nullable=true,length=500,unique=false)
	private String vehUsedAt=null;

	@Column(name="corpGSTIN",updatable=true,nullable=true,length=20,unique=false)
	private String corpGSTIN=null;

	@Column(name="bookedBy",updatable=true,nullable=true,length=50,unique=false)
	private String bookedBy=null;
	
	@Column(name="bookedById",updatable=true,nullable=true,length=20,unique=false)
	private Long bookedById=null;

	@Column(name="usedBy",updatable=true,nullable=true,length=100,unique=false)
	private String usedBy=null;

	@Column(name="closedBy",updatable=true,nullable=true,length=100,unique=false)
	private String closedBy=null;

	@Column(name="closedById",updatable=true,nullable=true,length=20,unique=false)
	private Long closedById=null;

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

	@Column(name="rentalType",updatable=true,nullable=true,length=50,unique=false)
	private String rentalType=null;

	@Column(name="rentalTypeId",updatable=true,nullable=true,length=20,unique=false)
	private Long rentalTypeId=null;

	@Column(name="basicFair",updatable=true,nullable=true,length=15,unique=false)
	private Double basicFair=0.00;

	@Column(name="totalBasicFair",updatable=true,nullable=true,length=15,unique=false)
	private Double totalBasicFair=0.00;

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

	@Column(name="kmUsed",updatable=true,nullable=true,length=10,unique=false)
	private Double kmUsed=0.00;
	
	@Column(name="hrsUsed",updatable=true,nullable=true,length=15,unique=false)
	private String hrsUsed=null;

	@Column(name="totalAmt",updatable=true,nullable=true,length=15,unique=false)
	private Double totalAmt=0.00;
	
	@Column(name="taxHeading",updatable=true,nullable=true,length=250,unique=false)
	private String taxHeading=null;

	@Column(name="taxValues",updatable=true,nullable=true,length=100,unique=false)
	private String taxValues=null;

	@Column(name="grandTotal",updatable=true,nullable=true,length=15,unique=false)
	private Double grandTotal=0.00;

	@Column(name="invoiceType",updatable=true,nullable=true,length=2,unique=false)
	private String invoiceType=null;
	
	@Column(name="status",updatable=true,nullable=true,length=2,unique=false)
	private String status=null;

	@Column(name="creationDate",updatable=true,nullable=true,length=25,unique=false)
	private Date creationDate= new Date();

	@Column(name="createdBy",updatable=true,nullable=true,length=25,unique=false)
	private long createdBy = 0l;
	
	@Column(name="remark",updatable=true,nullable=true,length=200,unique=false)
	private String remark=null;
	
	@Column(name="bankName",updatable=true,nullable=true,length=50,unique=false)
	private String bankName=null;
	
	@Column(name="bankAccNo",updatable=true,nullable=true,length=50,unique=false)
	private String bankAccNo=null;
	
	@Column(name="bankIFSC",updatable=true,nullable=true,length=20,unique=false)
	private String bankIFSC=null;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER,mappedBy="invoiceModel")
	@org.hibernate.annotations.OrderBy(clause = "dsNo")
	private List<InvoiceDetailModel> invoiceDetailModel=new ArrayList<InvoiceDetailModel>(0);

	@Column(name="coverLetterId",updatable=true,nullable=true,length=20,unique=false)
	private Long coverLetterId=null;
	
	@Column(name="isParkTaxable",updatable=true,nullable=true,length=1,unique=false)
	private String isParkTaxable=null;
	
	
	@Transient
	Long lNewCoverLetterId = null;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public String getBrName() {
		return brName;
	}

	public void setBrName(String brName) {
		this.brName = brName;
	}

	public String getBrGSTIN() {
		return brGSTIN;
	}

	public void setBrGSTIN(String brGSTIN) {
		this.brGSTIN = brGSTIN;
	}

	public String getBrPAN() {
		return brPAN;
	}

	public void setBrPAN(String brPAN) {
		this.brPAN = brPAN;
	}

	public String getBrOffice() {
		return brOffice;
	}

	public void setBrOffice(String brOffice) {
		this.brOffice = brOffice;
	}

	public String getHeadOffice() {
		return headOffice;
	}

	public void setHeadOffice(String headOffice) {
		this.headOffice = headOffice;
	}

	public String getHeadCont() {
		return headCont;
	}

	public void setHeadCont(String headCont) {
		this.headCont = headCont;
	}

	public String getHeadWebsite() {
		return headWebsite;
	}

	public void setHeadWebsite(String headWebsite) {
		this.headWebsite = headWebsite;
	}

	public String getCorpName() {
		return corpName;
	}

	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

	public String getCorpAddr() {
		return corpAddr;
	}

	public void setCorpAddr(String corpAddr) {
		this.corpAddr = corpAddr;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public Date getInvoiceDt() {
		return invoiceDt;
	}

	public void setInvoiceDt(Date invoiceDt) {
		this.invoiceDt = invoiceDt;
	}

	public String getDsNo() {
		return dsNo;
	}

	public void setDsNo(String dsNo) {
		this.dsNo = dsNo;
	}

	public String getCorpGSTIN() {
		return corpGSTIN;
	}

	public void setCorpGSTIN(String corpGSTIN) {
		this.corpGSTIN = corpGSTIN;
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

	public Double getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(Double grandTotal) {
		this.grandTotal = grandTotal;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public String getHeadEmail() {
		return headEmail;
	}

	public void setHeadEmail(String headEmail) {
		this.headEmail = headEmail;
	}

	public String getCompState() {
		return compState;
	}

	public void setCompState(String compState) {
		this.compState = compState;
	}

	public int getCompPIN() {
		return compPIN;
	}

	public void setCompPIN(int compPIN) {
		this.compPIN = compPIN;
	}

	public Double getStateTax() {
		return stateTax;
	}

	public void setStateTax(Double stateTax) {
		this.stateTax = stateTax;
	}

	public String getRentalType() {
		return rentalType;
	}

	public void setRentalType(String rentalType) {
		this.rentalType = rentalType;
	}

	public List<InvoiceDetailModel> getInvoiceDetailModel() {
		return invoiceDetailModel;
	}

	public void setInvoiceDetailModel(List<InvoiceDetailModel> invoiceDetailModel) {
		this.invoiceDetailModel = invoiceDetailModel;
	}

	public Double getTotalBasicFair() {
		return totalBasicFair;
	}

	public void setTotalBasicFair(Double totalBasicFair) {
		this.totalBasicFair = totalBasicFair;
	}

	public Double getBasicFair() {
		return basicFair;
	}

	public void setBasicFair(Double basicFair) {
		this.basicFair = basicFair;
	}

	public String getClosedBy() {
		return closedBy;
	}

	public void setClosedBy(String closedBy) {
		this.closedBy = closedBy;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getHubName() {
		return hubName;
	}

	public void setHubName(String hubName) {
		this.hubName = hubName;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public Long getHubId() {
		return hubId;
	}

	public void setHubId(Long hubId) {
		this.hubId = hubId;
	}

	public Long getCorpId() {
		return corpId;
	}

	public void setCorpId(Long corpId) {
		this.corpId = corpId;
	}

	public Long getBookedById() {
		return bookedById;
	}

	public void setBookedById(Long bookedById) {
		this.bookedById = bookedById;
	}

	public Long getClosedById() {
		return closedById;
	}

	public void setClosedById(Long closedById) {
		this.closedById = closedById;
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

	public Long getCompId() {
		return compId;
	}

	public void setCompId(Long compId) {
		this.compId = compId;
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

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAccNo() {
		return bankAccNo;
	}

	public void setBankAccNo(String bankAccNo) {
		this.bankAccNo = bankAccNo;
	}

	public String getBankIFSC() {
		return bankIFSC;
	}

	public void setBankIFSC(String bankIFSC) {
		this.bankIFSC = bankIFSC;
	}

	public Double getGuideCharge() {
		return guideCharge;
	}

	public void setGuideCharge(Double guideCharge) {
		this.guideCharge = guideCharge;
	}

	public Long getCoverLetterId() {
		return coverLetterId;
	}

	public void setCoverLetterId(Long coverLetterId) {
		this.coverLetterId = coverLetterId;
	}

	public Long getlNewCoverLetterId() {
		return lNewCoverLetterId;
	}

	public void setlNewCoverLetterId(Long lNewCoverLetterId) {
		this.lNewCoverLetterId = lNewCoverLetterId;
	}

	public String getIsParkTaxable() {
		return isParkTaxable;
	}

	public void setIsParkTaxable(String isParkTaxable) {
		this.isParkTaxable = isParkTaxable;
	}
}
