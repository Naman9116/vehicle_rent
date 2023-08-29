package com.operation.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.master.model.ChauffeurModel;
import com.master.model.GeneralMasterModel;
import com.master.model.VendorModel;
import com.user.model.UserModel;

@Entity
@Table(name="historyDutySlipDetails")

public class HistoryDutySlipModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
public HistoryDutySlipModel(){
		
	}
	
	public HistoryDutySlipModel(Long id,Double tollTaxAmount,Double parkingAmount,Long dutySlipReceivedBy,Long invoiceGenerateBy){
		this.id=id;
		this.tollTaxAmount=tollTaxAmount;
		this.parkingAmount=parkingAmount;
		this.isDutySlipReceived=true;
		this.dutySlipReceivedBy=dutySlipReceivedBy;
		this.invoiceGenerateBy=invoiceGenerateBy;
	}
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@Column(name="dSId" ,updatable=true,nullable=true,unique=false)
	private Long dSId=null;
	
	@OneToOne(cascade = CascadeType.ALL,optional=true)
	@JoinColumn(name="bookingDetailId",referencedColumnName="id",nullable=true,updatable=true)
	private BookingDetailModel bookingDetailModel=null; 
	
	/*@ManyToOne(targetEntity=BookingDetailModel.class)
	@JoinColumn(name="bookingDetailId",referencedColumnName="id",nullable=false,updatable=true)
	private BookingDetailModel bookingDetailModel=null;*/
 
	@Column(name="dutySlipStatus",updatable=true,nullable=true,length=1,unique=false)
	private String dutySlipStatus=null;
	
	@Column(name="dutySlipNo",updatable=true,nullable=true,length=40,unique=false)
	private String dutySlipNo=null;
	
	@ManyToOne(targetEntity=CarDetailModel.class)
	@JoinColumn(name="carDetailId",referencedColumnName="id",nullable=false,updatable=true)
	private CarDetailModel carDetailModel=null;
	
	@ManyToOne(targetEntity=ChauffeurModel.class)
	@JoinColumn(name="ChauffeurId",referencedColumnName="id",nullable=true,updatable=true)
	private ChauffeurModel chauffeurModel=null;

	@ManyToOne(targetEntity=VendorModel.class)
	@JoinColumn(name="vendorId",referencedColumnName="id",nullable=true,updatable=true)
	private VendorModel vendorModel=null;
	
	@Column(name="isUpdatedChauffeur",updatable=true,nullable=true,length=1,unique=false)
	private String isUpdatedChauffeur=null;
	
	@Column(name="chauffeurName",updatable=true,nullable=true,length=60,unique=false)
	private String chauffeurName=null;
	
	@Column(name="chauffeurMobile",updatable=true,nullable=true,length=10,unique=false)
	private String chauffeurMobile=null;
	
	@Column(name="openKms",updatable=true,nullable=true,length=30,unique=false)
	private Long openKms=0l;
	
	@Column(name="isDsManual",updatable=true,nullable=true,length=1,unique=false)
	private String isDsManual=null;
	
	@Column(name="manualSlipNo",updatable=true,nullable=true,length=30,unique=false)
	private String manualSlipNo=null;
	
	@Column(name="manualSlipRemarks",updatable=true,nullable=true,length=120,unique=false)
	private String manualSlipRemarks=null;
	
	@Column(name="allocationDateTime",updatable=true,nullable=true,length=30,unique=false)
	private Date allocationDateTime= null;
	
	@Column(name="dispatchDateTime",updatable=true,nullable=true,length=30,unique=false)
	private Date dispatchDateTime= null;
	
	@ManyToOne(targetEntity=UserModel.class)
	@JoinColumn(name="dispatchedBy",referencedColumnName="id",nullable=true,updatable=true)
	private UserModel dispatchedBy=null;
	
	@Column(name="dutySlipDate",updatable=true,nullable=true,length=30,unique=false)
	private Date dutySlipDate=null;
	
	/*@OneToOne(cascade = CascadeType.ALL,mappedBy="dutySlipModel")
	private ReservedCarModel reservedCarModel = null;*/
	
	//@ManyToOne(targetEntity=UserModel.class)
	@Column(name="driverNameId",nullable=true,updatable=true)
	private Long driverNameId=null;
	
	@Column(name="timeFrom",updatable=true,nullable=true,length=10,unique=false)
	private String timeFrom=null;
	
	@Column(name="dateFrom",updatable=true,nullable=true,length=30,unique=false)
	private Date dateFrom=null;
	
	@Column(name="closeKms",updatable=true,nullable=true,length=30,unique=false)
	private Long closeKms=0l;
	
	@Column(name="timeTo",updatable=true,nullable=true,length=10,unique=false)
	private String timeTo=null;
	
	@Column(name="dateTo",updatable=true,nullable=true,length=30,unique=false)
	private Date dateTo=null;
	
	@Column(name="totalKms",updatable=true,nullable=true,length=30,unique=false)
	private Double totalKms=0.00;
	
	@Column(name="totalHrs",updatable=true,nullable=true,length=10,unique=false)
	private String totalHrs=null;
	
	@Column(name="totalDay",updatable=true,nullable=true,length=10,unique=false)
	private Long totalDay=1l;

	@Column(name="garageOutKms",updatable=true,nullable=true,length=10,unique=false)
	private String garageOutKms=null;

	@Column(name="garageInKms",updatable=true,nullable=true,length=10,unique=false)
	private String garageInKms=null;

	@Column(name="driverAdvance",updatable=true,nullable=true,length=30,unique=false)
	private Double driverAdvance=0.00;
	
	@Column(name="minChgKms",updatable=true,nullable=true,length=30,unique=false)
	private Double minChgKms=0.00;
	
	@Column(name="minChgHrs",updatable=true,nullable=true,length=30,unique=false)
	private Double minChgHrs=null;
	
	@Column(name="extraChgKms",updatable=true,nullable=true,length=30,unique=false)
	private Double extraChgKms=0.00;
	
	@Column(name="extraChgHrs",updatable=true,nullable=true,length=30,unique=false)
	private Double extraChgHrs=null;
	
	/*@ManyToOne(targetEntity=GeneralMasterModel.class,fetch=FetchType.LAZY)
	@JoinColumn(name="ownTypeId",referencedColumnName="id",nullable=false,updatable=true)
	private GeneralMasterModel ownType = null;*/
	
	@Column(name="stateTax",updatable=true,nullable=true,length=30,unique=false)
	private Double stateTax=0.00;

	@Column(name="nightAllow",updatable=true,nullable=true,length=30,unique=false)
	private Double nightAllow=0.00;

	@Column(name="outStationAllowRate",updatable=true,nullable=true,length=30,unique=false)
	private Double outStationAllowRate=0.00;
	
	@Column(name="miscCharge",updatable=true,nullable=true,length=30,unique=false)
	private Double miscCharge=0.00;
	
	@Column(name="bookingType",updatable=true,nullable=true,length=30,unique=false)
	private String bookingType=null;
	
	@Column(name="pickupFrom",updatable=true,nullable=true,length=30,unique=false)
	private String pickupFrom=null;
	
	@Column(name="dropTo",updatable=true,nullable=true,length=30,unique=false)
	private String dropTo=null;
	
	@Column(name="remarks",updatable=true,nullable=true,length=250,unique=false)
	private String remarks=null;

	/*@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "dutySlipModel", fetch=FetchType.EAGER,optional=true)
	private AdditionalDriverDetailModel additionalDriverDetailModel = null;*/

	@Column(name="parkingAmount",updatable=true,nullable=true,length=15,unique=false)
	private Double parkingAmount=0.00;
	
	@Column(name="tollTaxAmount",updatable=true,nullable=true,length=15,unique=false)
	private Double tollTaxAmount=0.00;
	
	@Column(name="isDutySlipReceived",updatable=true,nullable=true,length=5,unique=false)
	private Boolean isDutySlipReceived=false;

	@Column(name="isDutySlipClosed",updatable=true,nullable=true,length=5,unique=false)
	private Boolean isDutySlipClosed=false;

	@Column(name="deviceStatus",updatable=true,nullable=true,length=50,unique=false)
	private String deviceStatus=null;
	
	@Column(name="modeOfPayment",updatable=true,nullable=true,length=10,unique=false)
	private String modeOfPayment=null;
	
	@Column(name="dutySlipReceivedBy",nullable=true,updatable=true)
	private Long dutySlipReceivedBy = null;

	@Column(name="dutySlipReceivedByDate",updatable=true,nullable=true,length=30,unique=false)
	private Date dutySlipReceivedByDate=null;

	@Column(name="dutySlipCreatedBy",nullable=true,updatable=true)
	private Long dutySlipCreatedBy = null;

	@Column(name="dutySlipCreatedBydate",updatable=true,nullable=true,length=30,unique=false)
	private Date dutySlipCreatedByDate=null;

	@Column(name="dutySlipClosedBy",nullable=true,updatable=true)
	private Long dutySlipClosedBy = null;

	@Column(name="dutySlipClosedByDate",updatable=true,nullable=true,length=30,unique=false)
	private Date dutySlipClosedByDate=null;
	
	@Column(name="isInvoiceGenerated",updatable=true,nullable=true,length=5,unique=false)
	private Boolean isInvoiceGenerated=false;

	@Column(name="invoiceNo",updatable=true,nullable=true,length=30,unique=false)
	private String invoiceNo = null;

	@Column(name="invoiceGenerateDate",updatable=true,nullable=true,length=30,unique=false)
	private Date invoiceGenerateDate=null;

	@Column(name="invoiceGenerateBy",updatable=true,nullable=true,length=30,unique=false)
	private Long invoiceGenerateBy=null;
	
	@Column(name="totalFare",updatable=true,nullable=true,length=20,unique=false)
	private Double totalFare=null;
	
	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="tariffId",referencedColumnName="id",nullable=true,updatable=true)
	private GeneralMasterModel tariff = null;
	
	@Column(name="billingStatus",updatable=true, nullable=true, length=100)
	 private String billingStatus = null;
	
	@Column(name="fuelCharge",updatable=true,nullable=true,length=30,unique=false)
	private Double fuelCharge=null;
	
	@Column(name="guideCharge",updatable=true,nullable=true,length=20,unique=false)
	private Double guideCharge=null;
	
	@Column(name="totalNight",updatable=true,nullable=true,length=10,unique=false)
	private String totalNight=null;
	
	@Column(name="basicFare",updatable=true,nullable=true,length=20,unique=false)
	private Double basicFare=null;
	
	@Column(name="serviceTax",updatable=true,nullable=true,length=30,unique=false)
	private Double serviceTax=0.00;
	
	@Column(name="reserveDateTime",updatable=true,nullable=true,length=30,unique=false)
	private Date reserveDateTime= null;
	
	@Column(name="taxName",updatable=true,nullable=true,unique=false)
	private String taxName = null;
	
	@Column(name="taxPercentage",updatable=true, nullable=true, unique=false)
	private String taxPercentage = null;
	
	@Column(name="taxValues",updatable=true, nullable=true, unique=false)
	private String taxValues = null;
	
	@Column(name="nightAllowanceRate",updatable=true, nullable=true, length=30, unique=false)
	private Double nightAllowanceRate = null;
	
	@Column(name="dsUpdateDateTime",updatable=true,nullable=true,length=30,unique=false)
	private Date dsUpdateDateTime= null;
	
	@Column(name="dsUpdateBy",updatable=true,nullable=true,length=30,unique=false)
	private Long dsUpdateBy= null;
	
	@Column(name="dayAllow",updatable=true,nullable=true,length=30,unique=false)
	private Double dayAllow=0.00;
	
	
	public String getTotalNight() {
		return totalNight;
	}

	public void setTotalNight(String totalNight) {
		this.totalNight = totalNight;
	}

	public Double getBasicFare() {
		return basicFare;
	}

	public void setBasicFare(Double basicFare) {
		this.basicFare = basicFare;
	}

	public Double getServiceTax() {
		return serviceTax;
	}

	public void setServiceTax(Double serviceTax) {
		this.serviceTax = serviceTax;
	}

	public Double getFuelCharge() {
		return fuelCharge;
	}

	public void setFuelCharge(Double fuelCharge) {
		this.fuelCharge = fuelCharge;
	}

	public Double getGuideCharge() {
		return guideCharge;
	}

	public void setGuideCharge(Double guideCharge) {
		this.guideCharge = guideCharge;
	}

	@Transient
	private DutySlipModel dutySlipModel=null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBillingStatus() {
		return billingStatus;
	}

	public void setBillingStatus(String billingStatus) {
		this.billingStatus = billingStatus;
	}

	public String getManualSlipNo() {
		return manualSlipNo;
	}

	public void setManualSlipNo(String manualSlipNo) {
		this.manualSlipNo = manualSlipNo;
	}

	public String getDutySlipNo() {
		return dutySlipNo;
	}

	public void setDutySlipNo(String dutySlipNo) {
		this.dutySlipNo = dutySlipNo;
	}

	public Date getDutySlipDate() {
		return dutySlipDate;
	}

	public void setDutySlipDate(Date dutySlipDate) {
		this.dutySlipDate = dutySlipDate;
	}

	public Long getDriverNameId() {
		return driverNameId;
	}

	public void setDriverNameId(Long driverNameId) {
		this.driverNameId = driverNameId;
	}

	public Long getOpenKms() {
		return openKms;
	}

	public void setOpenKms(Long openKms) {
		this.openKms = openKms;
	}

	public String getTimeFrom() {
		return timeFrom;
	}

	public void setTimeFrom(String timeFrom) {
		this.timeFrom = timeFrom;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Long getCloseKms() {
		return closeKms;
	}

	public void setCloseKms(Long closeKms) {
		this.closeKms = closeKms;
	}

	public String getTimeTo() {
		return timeTo;
	}

	public void setTimeTo(String timeTo) {
		this.timeTo = timeTo;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public Double getTotalKms() {
		return totalKms;
	}

	public void setTotalKms(Double totalKms) {
		this.totalKms = totalKms;
	}

	public String getTotalHrs() {
		return totalHrs;
	}

	public void setTotalHrs(String totalHrs) {
		this.totalHrs = totalHrs;
	}

	public Long getTotalDay() {
		return totalDay;
	}

	public void setTotalDay(Long totalDay) {
		this.totalDay = totalDay;
	}

	public Double getDriverAdvance() {
		return driverAdvance;
	}

	public void setDriverAdvance(Double driverAdvance) {
		this.driverAdvance = driverAdvance;
	}

	public Double getMinChgKms() {
		return minChgKms;
	}

	public void setMinChgKms(Double minChgKms) {
		this.minChgKms = minChgKms;
	}

	public Double getMinChgHrs() {
		return minChgHrs;
	}

	public void setMinChgHrs(Double minChgHrs) {
		this.minChgHrs = minChgHrs;
	}

	public Double getExtraChgKms() {
		return extraChgKms;
	}

	public void setExtraChgKms(Double extraChgKms) {
		this.extraChgKms = extraChgKms;
	}

	public Double getExtraChgHrs() {
		return extraChgHrs;
	}

	public void setExtraChgHrs(Double extraChgHrs) {
		this.extraChgHrs = extraChgHrs;
	}
	
	public Double getStateTax() {
		return stateTax;
	}

	public void setStateTax(Double stateTax) {
		this.stateTax = stateTax;
	}

	public Double getNightAllow() {
		return nightAllow;
	}

	public void setNightAllow(Double nightAllow) {
		this.nightAllow = nightAllow;
	}

	public Double getOutStationAllowRate() {
		return outStationAllowRate;
	}

	public Double getMiscCharge() {
		return miscCharge;
	}

	public void setMiscCharge(Double miscCharge) {
		this.miscCharge = miscCharge;
	}

	public BookingDetailModel getBookingDetailModel() {
		return bookingDetailModel;
	}

	public void setBookingDetailModel(BookingDetailModel bookingDetailModel) {
		this.bookingDetailModel = bookingDetailModel;
	}

	public String getBookingType() {
		return bookingType;
	}

	public void setBookingType(String bookingType) {
		this.bookingType = bookingType;
	}

	public String getPickupFrom() {
		return pickupFrom;
	}

	public void setPickupFrom(String pickupFrom) {
		this.pickupFrom = pickupFrom;
	}

	public String getDropTo() {
		return dropTo;
	}

	public void setDropTo(String dropTo) {
		this.dropTo = dropTo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public Double getParkingAmount() {
		return parkingAmount;
	}

	public void setParkingAmount(Double parkingAmount) {
		this.parkingAmount = parkingAmount;
	}

	public Double getTollTaxAmount() {
		return tollTaxAmount;
	}

	public void setTollTaxAmount(Double tollTaxAmount) {
		this.tollTaxAmount = tollTaxAmount;
	}

	public Boolean getIsDutySlipReceived() {
		return isDutySlipReceived;
	}

	public void setIsDutySlipReceived(Boolean isDutySlipReceived) {
		this.isDutySlipReceived = isDutySlipReceived;
	}

	public String getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(String deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public Long getDutySlipReceivedBy() {
		return dutySlipReceivedBy;
	}

	public void setDutySlipReceivedBy(Long dutySlipReceivedBy) {
		this.dutySlipReceivedBy = dutySlipReceivedBy;
	}

	public String getGarageOutKms() {
		return garageOutKms;
	}

	public void setGarageOutKms(String garageOutKms) {
		this.garageOutKms = garageOutKms;
	}

	public String getGarageInKms() {
		return garageInKms;
	}

	public void setGarageInKms(String garageInKms) {
		this.garageInKms = garageInKms;
	}

	public Boolean getIsDutySlipClosed() {
		return isDutySlipClosed;
	}

	public void setIsDutySlipClosed(Boolean isDutySlipClosed) {
		this.isDutySlipClosed = isDutySlipClosed;
	}

	public String getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	
	@Transient
	private String corporate = null;
	@Transient
	private String bookedBy = null;
	@Transient
	private String usedBy = null;
	@Transient
	private String closedBy = null;
	@Transient
	private String hub = null;

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

	public String getClosedBy() {
		return closedBy;
	}

	public void setClosedBy(String closedBy) {
		this.closedBy = closedBy;
	}

	public String getHub() {
		return hub;
	}

	public void setHub(String hub) {
		this.hub = hub;
	}

	public Date getDutySlipReceivedByDate() {
		return dutySlipReceivedByDate;
	}

	public void setDutySlipReceivedByDate(Date dutySlipReceivedByDate) {
		this.dutySlipReceivedByDate = dutySlipReceivedByDate;
	}

	public Long getDutySlipCreatedBy() {
		return dutySlipCreatedBy;
	}

	public void setDutySlipCreatedBy(Long dutySlipCreatedBy) {
		this.dutySlipCreatedBy = dutySlipCreatedBy;
	}

	public Date getDutySlipCreatedByDate() {
		return dutySlipCreatedByDate;
	}

	public void setDutySlipCreatedByDate(Date dutySlipCreatedByDate) {
		this.dutySlipCreatedByDate = dutySlipCreatedByDate;
	}

	public Long getDutySlipClosedBy() {
		return dutySlipClosedBy;
	}

	public void setDutySlipClosedBy(Long dutySlipClosedBy) {
		this.dutySlipClosedBy = dutySlipClosedBy;
	}

	public Date getDutySlipClosedByDate() {
		return dutySlipClosedByDate;
	}

	public void setDutySlipClosedByDate(Date dutySlipClosedByDate) {
		this.dutySlipClosedByDate = dutySlipClosedByDate;
	}

	public Boolean getIsInvoiceGenerated() {
		return isInvoiceGenerated;
	}

	public void setIsInvoiceGenerated(Boolean isInvoiceGenerated) {
		this.isInvoiceGenerated = isInvoiceGenerated;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public Date getInvoiceGenerateDate() {
		return invoiceGenerateDate;
	}

	public void setInvoiceGenerateDate(Date invoiceGenerateDate) {
		this.invoiceGenerateDate = invoiceGenerateDate;
	}

	public Long getInvoiceGenerateBy() {
		return invoiceGenerateBy;
	}

	public void setInvoiceGenerateBy(Long invoiceGenerateBy) {
		this.invoiceGenerateBy = invoiceGenerateBy;
	}

	public GeneralMasterModel getTariff() {
		return tariff;
	}

	public void setTariff(GeneralMasterModel tariff) {
		this.tariff = tariff;
	}

	public Double getTotalFare() {
		return totalFare;
	}

	public void setTotalFare(Double totalFare) {
		this.totalFare = totalFare;
	}

	public String getDutySlipStatus() {
		return dutySlipStatus;
	}

	public void setDutySlipStatus(String dutySlipStatus) {
		this.dutySlipStatus = dutySlipStatus;
	}

	public CarDetailModel getCarDetailModel() {
		return carDetailModel;
	}

	public void setCarDetailModel(CarDetailModel carDetailModel) {
		this.carDetailModel = carDetailModel;
	}

	public ChauffeurModel getChauffeurModel() {
		return chauffeurModel;
	}

	public void setChauffeurModel(ChauffeurModel chauffeurModel) {
		this.chauffeurModel = chauffeurModel;
	}

	public VendorModel getVendorModel() {
		return vendorModel;
	}

	public void setVendorModel(VendorModel vendorModel) {
		this.vendorModel = vendorModel;
	}

	public String getIsUpdatedChauffeur() {
		return isUpdatedChauffeur;
	}

	public void setIsUpdatedChauffeur(String isUpdatedChauffeur) {
		this.isUpdatedChauffeur = isUpdatedChauffeur;
	}

	public String getChauffeurName() {
		return chauffeurName;
	}

	public void setChauffeurName(String chauffeurName) {
		this.chauffeurName = chauffeurName;
	}

	public String getChauffeurMobile() {
		return chauffeurMobile;
	}

	public void setChauffeurMobile(String chauffeurMobile) {
		this.chauffeurMobile = chauffeurMobile;
	}

	public String getIsDsManual() {
		return isDsManual;
	}

	public void setIsDsManual(String isDsManual) {
		this.isDsManual = isDsManual;
	}

	public String getManualSlipRemarks() {
		return manualSlipRemarks;
	}

	public void setManualSlipRemarks(String manualSlipRemarks) {
		this.manualSlipRemarks = manualSlipRemarks;
	}

	public Date getAllocationDateTime() {
		return allocationDateTime;
	}

	public void setAllocationDateTime(Date allocationDateTime) {
		this.allocationDateTime = allocationDateTime;
	}

	public Date getDispatchDateTime() {
		return dispatchDateTime;
	}

	public void setDispatchDateTime(Date dispatchDateTime) {
		this.dispatchDateTime = dispatchDateTime;
	}

	public UserModel getDispatchedBy() {
		return dispatchedBy;
	}

	public void setDispatchedBy(UserModel dispatchedBy) {
		this.dispatchedBy = dispatchedBy;
	}

	public DutySlipModel getDutySlipModel() {
		return dutySlipModel;
	}

	public void setDutySlipModel(DutySlipModel dutySlipModel) {
		this.dutySlipModel = dutySlipModel;
	}

	public Date getReserveDateTime() {
		return reserveDateTime;
	}

	public void setReserveDateTime(Date reserveDateTime) {
		this.reserveDateTime = reserveDateTime;
	}

	public String getTaxName() {
		return taxName;
	}

	public void setTaxName(String taxName) {
		this.taxName = taxName;
	}

	public String getTaxPercentage() {
		return taxPercentage;
	}

	public void setTaxPercentage(String taxPercentage) {
		this.taxPercentage = taxPercentage;
	}

	public String getTaxValues() {
		return taxValues;
	}

	public void setTaxValues(String taxValues) {
		this.taxValues = taxValues;
	}

	public Double getNightAllowanceRate() {
		return nightAllowanceRate;
	}

	public void setNightAllowanceRate(Double nightAllowanceRate) {
		this.nightAllowanceRate = nightAllowanceRate;
	}

	public Date getDsUpdateDateTime() {
		return dsUpdateDateTime;
	}

	public void setDsUpdateDateTime(Date dsUpdateDateTime) {
		this.dsUpdateDateTime = dsUpdateDateTime;
	}

	public Long getDsUpdateBy() {
		return dsUpdateBy;
	}

	public void setDsUpdateBy(Long dsUpdateBy) {
		this.dsUpdateBy = dsUpdateBy;
	}

	public Double getDayAllow() {
		return dayAllow;
	}

	public void setDayAllow(Double dayAllow) {
		this.dayAllow = dayAllow;
	}

	public Long getdSId() {
		return dSId;
	}

	public void setdSId(Long dSId) {
		this.dSId = dSId;
	}

	public void setOutStationAllowRate(Double outStationAllowRate) {
		this.outStationAllowRate = outStationAllowRate;
	}
	
	
	

}
