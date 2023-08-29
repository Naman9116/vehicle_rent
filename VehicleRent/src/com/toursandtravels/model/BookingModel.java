package com.toursandtravels.model;
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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.common.model.AddressDetailModel;
import com.corporate.model.AutorizedUserModel;
import com.corporate.model.CorporateModel;
import com.master.model.CityMasterModel;
import com.master.model.GeneralMasterModel;
import com.operation.model.DutySlipModel;
import com.relatedInfo.model.RelatedInfoModel;
import com.user.model.UserModel;

@Entity
@Table(name="bookings")
@JsonIgnoreProperties(value={"tDutySlipModel"})
public class BookingModel implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@Column(name="bookingNo",updatable=true,nullable=true,length=40,unique=false)
	private String bookingNo=null;
	
	@Column(name="bookingDate",updatable=true,nullable=false,length=30,unique=false)
	private Date bookingDate=null;

	@ManyToOne(targetEntity=CustomerModel.class)
	@JoinColumn(name="customerId",referencedColumnName="id",nullable=false,updatable=true)
	private CustomerModel customerId = null;
	
	@ManyToOne(targetEntity=UserModel.class)
	@JoinColumn(name="bookingTakenBy",updatable=true,nullable=false,unique=false,referencedColumnName="id")
	private UserModel bookingTakenBy=null;
	
	@Column(name="customerName",updatable=true,nullable=true)
	private String customerName=null;

	@Column(name="mobileNo",updatable=true,nullable=true,length=20)
	private String mobileNo=null;
	
	@Column(name="referenceNo",updatable=true,nullable=true,length=20)
	private String referenceNo=null;
	

	@ManyToOne()
	@JoinColumn(name="company",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel company=null;
	
	@ManyToOne()
	@JoinColumn(name="branch",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel branch=null;

	@ManyToOne()
	@JoinColumn(name="outlet",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel outlet=null;

	@Column(name="pickUpDate",updatable=true,nullable=true,length=30,unique=false)
	private Date pickUpDate=null;

	@Column(name="startTime",updatable=true,nullable=true,length=10,unique=false)
	private String startTime=null;
	
	@Column(name="pickUpTime",updatable=true,nullable=true,length=10,unique=false)
	private String pickUpTime=null;
	
	@Column(name="flightNo",updatable=true,nullable=true,length=50,unique=false)
	private String flightNo=null;
	
	@Column(name="terminal",updatable=true,nullable=true,length=50,unique=false)
	private String terminal=null;

	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="mob",referencedColumnName="id",nullable=true,updatable=true)
	private GeneralMasterModel mob = null;

	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="carModelId",referencedColumnName="id",nullable=true,updatable=true)
	private GeneralMasterModel carModel = null;
	
	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="rentalTypeId",referencedColumnName="id",nullable=true,updatable=true)
	private GeneralMasterModel rentalType = null;

	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="tariffId",referencedColumnName="id",nullable=true,updatable=true)
	private GeneralMasterModel tariff = null;
	
	@Column(name="reportingAddress",updatable=true,nullable=true,length=200,unique=false)
	private String reportingAddress=null;
	
	@Column(name="toBeRealeseAt",updatable=true,nullable=true,length=200,unique=false)
	private String toBeRealeseAt=null;

	@Column(name="instruction",updatable=true,nullable=true,length=150,unique=false)
	private String instruction=null;
	
	@Column(name="status",updatable=true,nullable=true,length=1,unique=false)
	private String status=null;
	
	@Column(name="insDate",updatable=true,nullable=true,length=40,unique=false)
	private Date insDate=new Date();
	
	@ManyToOne(targetEntity=UserModel.class)
	@JoinColumn(name="cancelBy",updatable=true,nullable=true,unique=false,referencedColumnName="id")
	private UserModel cancelBy=null;
	
	@Column(name="cancelReason",updatable=true,nullable=true,length=200,unique=false )
	private String cancelReason=null;
	
	@Column(name="cancelDate",updatable=true,nullable=true,length=30,unique=false)
	private  Date cancelDate=null;
	
	@OneToOne(cascade = CascadeType.ALL,mappedBy="bookingModel",optional=true)
	@NotFound(action=NotFoundAction.IGNORE)
	private TDutySlipModel tDutySlipModel = null;
	
	@Transient
	private AddressDetailModel address = null;
	
	@Transient
	private RelatedInfoModel relatedInfo = null;
	
	
	
	public UserModel getCancelBy() {
		return cancelBy;
	}

	public void setCancelBy(UserModel cancelBy) {
		this.cancelBy = cancelBy;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public TDutySlipModel gettDutySlipModel() {
		return tDutySlipModel;
	}

	public void settDutySlipModel(TDutySlipModel tDutySlipModel) {
		this.tDutySlipModel = tDutySlipModel;
	}

	public AddressDetailModel getAddress() {
		return address;
	}

	public void setAddress(AddressDetailModel address) {
		this.address = address;
	}

	public RelatedInfoModel getRelatedInfo() {
		return relatedInfo;
	}

	public void setRelatedInfo(RelatedInfoModel relatedInfo) {
		this.relatedInfo = relatedInfo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public UserModel getBookingTakenBy() {
		return bookingTakenBy;
	}

	public void setBookingTakenBy(UserModel bookingTakenBy) {
		this.bookingTakenBy = bookingTakenBy;
	}

	public Date getInsDate() {
		return insDate;
	}

	public void setInsDate(Date insDate) {
		this.insDate = insDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBookingNo() {
		return bookingNo;
	}

	public void setBookingNo(String bookingNo) {
		this.bookingNo = bookingNo;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public CustomerModel getCustomerId() {
		return customerId;
	}

	public void setCustomerId(CustomerModel customerId) {
		this.customerId = customerId;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	

	public GeneralMasterModel getCompany() {
		return company;
	}

	public void setCompany(GeneralMasterModel company) {
		this.company = company;
	}

	public GeneralMasterModel getBranch() {
		return branch;
	}

	public void setBranch(GeneralMasterModel branch) {
		this.branch = branch;
	}

	public GeneralMasterModel getOutlet() {
		return outlet;
	}

	public void setOutlet(GeneralMasterModel outlet) {
		this.outlet = outlet;
	}

	public Date getPickUpDate() {
		return pickUpDate;
	}

	public void setPickUpDate(Date pickUpDate) {
		this.pickUpDate = pickUpDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getPickUpTime() {
		return pickUpTime;
	}

	public void setPickUpTime(String pickUpTime) {
		this.pickUpTime = pickUpTime;
	}

	public String getFlightNo() {
		return flightNo;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public GeneralMasterModel getMob() {
		return mob;
	}

	public void setMob(GeneralMasterModel mob) {
		this.mob = mob;
	}

	public GeneralMasterModel getCarModel() {
		return carModel;
	}

	public void setCarModel(GeneralMasterModel carModel) {
		this.carModel = carModel;
	}

	public GeneralMasterModel getRentalType() {
		return rentalType;
	}

	public void setRentalType(GeneralMasterModel rentalType) {
		this.rentalType = rentalType;
	}

	public GeneralMasterModel getTariff() {
		return tariff;
	}

	public void setTariff(GeneralMasterModel tariff) {
		this.tariff = tariff;
	}

	public String getReportingAddress() {
		return reportingAddress;
	}

	public void setReportingAddress(String reportingAddress) {
		this.reportingAddress = reportingAddress;
	}

	public String getToBeRealeseAt() {
		return toBeRealeseAt;
	}

	public void setToBeRealeseAt(String toBeRealeseAt) {
		this.toBeRealeseAt = toBeRealeseAt;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	
}
