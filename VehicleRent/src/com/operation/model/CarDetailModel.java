package com.operation.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.master.model.CarAllocationModel;
import com.master.model.GeneralMasterModel;
import com.user.model.UserModel;

@Entity
@Table(name="carDetails",uniqueConstraints= {@UniqueConstraint(columnNames={"registrationNo"})})
public class CarDetailModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	/*Car Identity Id*/
	@Column(name="carId",updatable=true,nullable=true,length=10,unique=false)
	private String carId=null;
	/*Entry Date with time*/
	@Column(name="insDate",nullable=false,updatable=true,length=25)
	private Date insDate = new Date();
	/* Entry UserId for update or insert */
	@ManyToOne()
	@JoinColumn(name = "userId", nullable = false, updatable = true)
	private UserModel userId = new UserModel();
	/*Car Owner Type C=Company, V=Vendor*/
	@Column(name="ownType",updatable=true,nullable=false,length=1,unique=false)
	private String ownType=null;
	/*Car Owner either from company or vendor table*/
	@Column(name="ownName",updatable=true,nullable=false,length=50,unique=false)
	private String ownName=null;
	/* Branch Id*/
	@ManyToOne()
	@JoinColumn(name = "branchId", nullable = false, updatable = true)
	private GeneralMasterModel branchId = new GeneralMasterModel();
	/* Fuel Type Id*/
	@ManyToOne()
	@JoinColumn(name = "fuelId", referencedColumnName="id", nullable = false, updatable = true)
	private GeneralMasterModel fuelId = new GeneralMasterModel();
	/*Car Model*/
	@ManyToOne()
	@JoinColumn(name="modelId",referencedColumnName="id",nullable=false,updatable=true)
	private GeneralMasterModel model = null;
	/*Car Make*/
	@ManyToOne()
	@JoinColumn(name="makeId",referencedColumnName="id",nullable=false,updatable=true)
	private GeneralMasterModel make = null;
	/*Car Registration no.*/
	@Column(name="registrationNo",updatable=true,nullable=false,length=20,unique=true)
	private String registrationNo=null;
	/*Car Engine No.*/
	@Column(name="engineNo",updatable=true,nullable=true,length=20,unique=false)
	private String engineNo=null;
	/*Car Chasis No.*/
	@Column(name="chasisNo",updatable=true,nullable=true,length=20,unique=false)
	private String chasisNo=null;
	/*Car Key No.*/
	@Column(name="keyNo",updatable=true,nullable=true,length=20,unique=false)
	private String keyNo=null;
	/*Car Body color*/
	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="bodyColorId",referencedColumnName="id",nullable=false,updatable=true)
	private GeneralMasterModel bodyColor = null;
	/*Car Body Style*/
	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="bodyStyleId",referencedColumnName="id",nullable=false,updatable=true)
	private GeneralMasterModel bodyStyle= null;
	/*Car Purchased From*/
	@Column(name="purchFrom",updatable=true,nullable=true,length=50,unique=false)
	private String purchFrom=null;
	/*Car Purchased Date*/
	@Column(name="purchDate",updatable=true,nullable=true,length=25,unique=false)
	private Date purchDate=null;
	/*Car Manufacturing Year*/
	@Column(name="manufacturerYear",updatable=true,nullable=true,length=4,unique=false)
	private Integer manufacturerYear = null;
	/*Ex Showroom Price*/
	@Column(name="exShPrice",updatable=true,nullable=true,length=20,unique=false)
	private Double exShPrice=null;
	/*Ex Showroom Price*/
	@Column(name="onRoadPrice",updatable=true,nullable=true,length=20,unique=false)
	private Double onRoadPrice=null;
	/*Hypothication To*/
	@Column(name="hypothicationTo",updatable=true,nullable=true,length=50,unique=false)
	private String hypothicationTo=null;
	/*EMI*/
	@Column(name="emiRs",updatable=true,nullable=true,length=15,unique=false)
	private Double emiRs=null;
	/*EMI StartDate*/
	@Column(name="hypothicationDate",updatable=true,nullable=true,length=25,unique=false)
	private Date hypothicationDate=null;
	/*EMI End Date*/
	@Column(name="lastDateOfEmi",updatable=true,nullable=true,length=25,unique=false)
	private Date lastDateOfEmi=null;
	/*Car Status*/
	@Column(name="status",updatable=true,nullable=true,length=1,unique=false)
	private String status=null;
	/*Car Registration Authority*/
	@Column(name="regAuth",updatable=true,nullable=true,length=50,unique=false)
	private String regAuth=null;
	/*GPS Enabled*/
	@Column(name="gpsEnabled",updatable=true,nullable=true,length=1,unique=false)
	private String gpsEnabled=null;
	/*GPS Company*/
	@Column(name="gpsCompany",updatable=true,nullable=true,length=50,unique=false)
	private String gpsCompany=null;
	/*Date of Sale*/
	@Column(name="saleDate",updatable=true,nullable=true,length=25,unique=false)
	private Date saleDate=null;
	/*Byer Name*/
	@Column(name="byerName",updatable=true,nullable=true,length=50,unique=false)
	private String byerName=null;
	/*Byer Mobile*/
	@Column(name="byerMobile",updatable=true,nullable=true,length=30,unique=false)
	private String byerMobile=null;
	/*Salling Amount*/
	@Column(name="saleAmt",updatable=true,nullable=true,length=15,unique=false)
	private Double saleAmt=null;
	/*Min. Business Required*/
	@Column(name="minBusReq",updatable=true,nullable=true,length=15,unique=false)
	private Double minBusReq=null;
	/*Min. Kms Required*/
	@Column(name="minKmsReq",updatable=true,nullable=true,length=15,unique=false)
	private Double minKmsReq=null;
	/*Mileage*/
	@Column(name="mileage",updatable=true,nullable=true,length=10,unique=false)
	private Double mileage=null;
		
	/*Insurance Details*/
	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, targetEntity=InsuranceDetailModel.class, mappedBy = "carDetailModel")
	private List<InsuranceDetailModel> insuranceDetailModel = null;
	/*Tax Details*/
	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, targetEntity=TaxDetailModel.class,mappedBy = "carDetailModel")
	private List<TaxDetailModel> taxDetailModel = null;
/*	Maintenance Details
	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "carDetailModel", fetch=FetchType.EAGER)
	private MaintenanceDetailModel maintenanceDetailModel = new MaintenanceDetailModel();
*/	
	@OneToMany(cascade=CascadeType.ALL,targetEntity=CarAllocationModel.class,mappedBy="carDetailModelId")
	private List<CarAllocationModel> carAllocationModels = null;
	
	@Transient
	private String dutySlipStatus = null;
	
	@Transient
	private String reserveBy = null;
	
	@Transient
	private String unReserveBy = null;
	
	@Transient
	private Date  reserveDateTime = null;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getInsDate() {
		return insDate;
	}
	public void setInsDate(Date insDate) {
		this.insDate = insDate;
	}
	public UserModel getUserId() {
		return userId;
	}
	public void setUserId(UserModel userId) {
		this.userId = userId;
	}
	public String getOwnType() {
		return ownType;
	}
	public void setOwnType(String ownType) {
		this.ownType = ownType;
	}
	public String getOwnName() {
		return ownName;
	}
	public void setOwnName(String ownName) {
		this.ownName = ownName;
	}
	public GeneralMasterModel getBranchId() {
		return branchId;
	}
	public void setBranchId(GeneralMasterModel branchId) {
		this.branchId = branchId;
	}
	public GeneralMasterModel getFuelId() {
		return fuelId;
	}
	public void setFuelId(GeneralMasterModel fuelId) {
		this.fuelId = fuelId;
	}
	public GeneralMasterModel getModel() {
		return model;
	}
	public void setModel(GeneralMasterModel model) {
		this.model = model;
	}
	public GeneralMasterModel getMake() {
		return make;
	}
	public void setMake(GeneralMasterModel make) {
		this.make = make;
	}
	public String getRegistrationNo() {
		return registrationNo;
	}
	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}
	public String getEngineNo() {
		return engineNo;
	}
	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}
	public String getChasisNo() {
		return chasisNo;
	}
	public void setChasisNo(String chasisNo) {
		this.chasisNo = chasisNo;
	}
	public String getKeyNo() {
		return keyNo;
	}
	public void setKeyNo(String keyNo) {
		this.keyNo = keyNo;
	}
	public GeneralMasterModel getBodyColor() {
		return bodyColor;
	}
	public void setBodyColor(GeneralMasterModel bodyColor) {
		this.bodyColor = bodyColor;
	}
	public GeneralMasterModel getBodyStyle() {
		return bodyStyle;
	}
	public void setBodyStyle(GeneralMasterModel bodyStyle) {
		this.bodyStyle = bodyStyle;
	}
	public String getPurchFrom() {
		return purchFrom;
	}
	public void setPurchFrom(String purchFrom) {
		this.purchFrom = purchFrom;
	}
	public Date getPurchDate() {
		return purchDate;
	}
	public void setPurchDate(Date purchDate) {
		this.purchDate = purchDate;
	}
	public Integer getManufacturerYear() {
		return manufacturerYear;
	}
	public void setManufacturerYear(Integer manufacturerYear) {
		this.manufacturerYear = manufacturerYear;
	}
	public Double getExShPrice() {
		return exShPrice;
	}
	public void setExShPrice(Double exShPrice) {
		this.exShPrice = exShPrice;
	}
	public Double getOnRoadPrice() {
		return onRoadPrice;
	}
	public void setOnRoadPrice(Double onRoadPrice) {
		this.onRoadPrice = onRoadPrice;
	}
	public String getHypothicationTo() {
		return hypothicationTo;
	}
	public void setHypothicationTo(String hypothicationTo) {
		this.hypothicationTo = hypothicationTo;
	}
	public Double getEmiRs() {
		return emiRs;
	}
	public void setEmiRs(Double emiRs) {
		this.emiRs = emiRs;
	}
	public Date getHypothicationDate() {
		return hypothicationDate;
	}
	public void setHypothicationDate(Date hypothicationDate) {
		this.hypothicationDate = hypothicationDate;
	}
	public Date getLastDateOfEmi() {
		return lastDateOfEmi;
	}
	public void setLastDateOfEmi(Date lastDateOfEmi) {
		this.lastDateOfEmi = lastDateOfEmi;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRegAuth() {
		return regAuth;
	}
	public void setRegAuth(String regAuth) {
		this.regAuth = regAuth;
	}
	public String getGpsEnabled() {
		return gpsEnabled;
	}
	public void setGpsEnabled(String gpsEnabled) {
		this.gpsEnabled = gpsEnabled;
	}
	public String getGpsCompany() {
		return gpsCompany;
	}
	public void setGpsCompany(String gpsCompany) {
		this.gpsCompany = gpsCompany;
	}
	public Date getSaleDate() {
		return saleDate;
	}
	public void setSaleDate(Date saleDate) {
		this.saleDate = saleDate;
	}
	public String getByerName() {
		return byerName;
	}
	public void setByerName(String byerName) {
		this.byerName = byerName;
	}
	public String getByerMobile() {
		return byerMobile;
	}
	public void setByerMobile(String byerMobile) {
		this.byerMobile = byerMobile;
	}
	public Double getSaleAmt() {
		return saleAmt;
	}
	public void setSaleAmt(Double saleAmt) {
		this.saleAmt = saleAmt;
	}
	public Double getMinBusReq() {
		return minBusReq;
	}
	public void setMinBusReq(Double minBusReq) {
		this.minBusReq = minBusReq;
	}
	public Double getMinKmsReq() {
		return minKmsReq;
	}
	public void setMinKmsReq(Double minKmsReq) {
		this.minKmsReq = minKmsReq;
	}
	public Double getMileage() {
		return mileage;
	}
	public void setMileage(Double mileage) {
		this.mileage = mileage;
	}
/*	public MaintenanceDetailModel getMaintenanceDetailModel() {
		return maintenanceDetailModel;
	}
	public void setMaintenanceDetailModel(MaintenanceDetailModel maintenanceDetailModel) {
		this.maintenanceDetailModel = maintenanceDetailModel;
	}
	
	
*/
	public String getDutySlipStatus() {
		return dutySlipStatus;
	}
	public void setDutySlipStatus(String dutySlipStatus) {
		this.dutySlipStatus = dutySlipStatus;
	}
	public String getReserveBy() {
		return reserveBy;
	}
	public void setReserveBy(String reserveBy) {
		this.reserveBy = reserveBy;
	}
	public String getUnReserveBy() {
		return unReserveBy;
	}
	public void setUnReserveBy(String unReserveBy) {
		this.unReserveBy = unReserveBy;
	}
	public Date getReserveDateTime() {
		return reserveDateTime;
	}
	public void setReserveDateTime(Date reserveDateTime) {
		this.reserveDateTime = reserveDateTime;
	}

	@JsonIgnore
	public List<CarAllocationModel> getCarAllocationModels() {
		return carAllocationModels;
	}
	public void setCarAllocationModels(List<CarAllocationModel> carAllocationModels) {
		this.carAllocationModels = carAllocationModels;
	}
	public String getCarId() {
		return carId;
	}
	public void setCarId(String carId) {
		this.carId = carId;
	}
	public List<InsuranceDetailModel> getInsuranceDetailModel() {
		return insuranceDetailModel;
	}
	public void setInsuranceDetailModel(List<InsuranceDetailModel> insuranceDetailModel) {
		this.insuranceDetailModel = insuranceDetailModel;
	}
	public List<TaxDetailModel> getTaxDetailModel() {
		return taxDetailModel;
	}
	public void setTaxDetailModel(List<TaxDetailModel> taxDetailModel) {
		this.taxDetailModel = taxDetailModel;
	}
	
}
