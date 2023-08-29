package com.operation.model;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.corporate.model.AutorizedUserModel;
import com.corporate.model.CorporateModel;
import com.user.model.UserModel;

@Entity
@Table(name="bookingMaster")
public class BookingMasterModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;

	@Column(name="bookingNo",updatable=true,nullable=true,length=40,unique=false)
	private String bookingNo=null;
	
	@Column(name="bookingDate",updatable=true,nullable=false,length=30,unique=false)
	private Date bookingDate=null;

	@ManyToOne(targetEntity=CorporateModel.class)
	@JoinColumn(name="corporateId",referencedColumnName="id",nullable=false,updatable=true)
	private CorporateModel corporateId = null;

	@ManyToOne(targetEntity=AutorizedUserModel.class)
	@JoinColumn(name="bookedBy",referencedColumnName="id",nullable=false,updatable=true)
	private AutorizedUserModel bookedBy = null;
	
	@Column(name="bookedFor",updatable=true,nullable=true,length=1)
	private String bookedFor=null;

	@Column(name="bookedForName",updatable=true,nullable=true,length=50)
	private String bookedForName=null;
	
	@Column(name="mobileNo",updatable=true,nullable=true,length=20)
	private String mobileNo=null;
	
	@Column(name="referenceNo",updatable=true,nullable=true,length=20)
	private String referenceNo=null;
	
	@ManyToOne(targetEntity=UserModel.class)
	@JoinColumn(name="bookingTakenBy",updatable=true,nullable=true,unique=false,referencedColumnName="id")
	private UserModel bookingTakenBy=null;

	@ManyToOne(targetEntity=AutorizedUserModel.class)
	@JoinColumn(name="webBookingTakenBy",updatable=true,nullable=true,unique=false,referencedColumnName="id")
	private AutorizedUserModel webBookingTakenBy=null;

	@Column(name="smsToClient",updatable=true,nullable=true,length=1)
	private String smsToClient=null;
	
	@Column(name="smsToBooker",updatable=true,nullable=true,length=1)
	private String smsToBooker=null;
	
	@Column(name="smsToOther",updatable=true,nullable=true,length=20)
	private String smsToOther=null;
	
	@Column(name="emailToClient",updatable=true,nullable=true,length=1)
	private String emailToClient=null;
	
	@Column(name="emailToBooker",updatable=true,nullable=true,length=1)
	private String emailToBooker=null;
	
	@Column(name="emailToOther",updatable=true,nullable=true,length=50)
	private String emailToOther=null;
	
	@Column(name="status",updatable=true,nullable=true,length=1,unique=false )
	private String status=null;
	
	@Column(name="bookingPlatform",updatable=true,nullable=true,length=1,unique=false )
	private String bookingPlatform=null;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER,mappedBy="bookingMasterModel")
	private List<BookingDetailModel> bookingDetailModel=new ArrayList<BookingDetailModel>(0);
	
	@Column(name="clientApproveDt",updatable=true,nullable=true,length=30,unique=false)
	private Date clientApproveDt=new Date();

	/*Transient Variables*/
	@Transient
	private String companyName = null;
	@Transient
	private String companyCode = null;
	@Transient
	private String cityName = null;
	@Transient
	private String reportingAddress = null;
	@Transient
	private String dropAt = null;
	@Transient
	private String carModelName = null;
	@Transient
	private String bookingDetailStatus = null;
	@Transient
	private String pickUpTime = null;
	@Transient
	private String clientEmail = null;
	@Transient
	private String bookerEmail = null;
	@Transient
	private String mobName = null;
	@Transient
	private String registrationNo = null;

	@Transient
	private Long mobId = null;
	@Transient
	private Long dutySlipId = null;

	@Transient
	private Date pickUpDate=null;

	public String getBookingDetailStatus() {
		return bookingDetailStatus;
	}

	public void setBookingDetailStatus(String bookingDetailStatus) {
		this.bookingDetailStatus = bookingDetailStatus;
	}

	public String getReportingAddress() {
		return reportingAddress;
	}

	public void setReportingAddress(String reportingAddress) {
		this.reportingAddress = reportingAddress;
	}

	public String getDropAt() {
		return dropAt;
	}

	public void setDropAt(String dropAt) {
		this.dropAt = dropAt;
	}

	public String getCarModelName() {
		return carModelName;
	}

	public void setCarModelName(String carModelName) {
		this.carModelName = carModelName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getBookingNo() {
		return bookingNo;
	}

	public void setBookingNo(String bookingNo) {
		this.bookingNo = bookingNo;
	}

	public UserModel getBookingTakenBy() {
		return bookingTakenBy;
	}

	public void setBookingTakenBy(UserModel bookingTakenBy) {
		this.bookingTakenBy = bookingTakenBy;
	}

	public List<BookingDetailModel> getBookingDetailModel() {
		return bookingDetailModel;
	}

	public void setBookingDetailModel(List<BookingDetailModel> bookingDetailModel) {
		this.bookingDetailModel = bookingDetailModel;
	}

	public CorporateModel getCorporateId() {
		return corporateId;
	}

	public void setCorporateId(CorporateModel corporateId) {
		this.corporateId = corporateId;
	}

	public AutorizedUserModel getBookedBy() {
		return bookedBy;
	}

	public void setBookedBy(AutorizedUserModel bookedBy) {
		this.bookedBy = bookedBy;
	}

	public String getBookedFor() {
		return bookedFor;
	}

	public void setBookedFor(String bookedFor) {
		this.bookedFor = bookedFor;
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

	public String getBookedForName() {
		return bookedForName;
	}

	public void setBookedForName(String bookedForName) {
		this.bookedForName = bookedForName;
	}

	public String getSmsToClient() {
		return smsToClient;
	}

	public void setSmsToClient(String smsToClient) {
		this.smsToClient = smsToClient;
	}

	public String getSmsToBooker() {
		return smsToBooker;
	}

	public void setSmsToBooker(String smsToBooker) {
		this.smsToBooker = smsToBooker;
	}

	public String getSmsToOther() {
		return smsToOther;
	}

	public void setSmsToOther(String smsToOther) {
		this.smsToOther = smsToOther;
	}

	public String getEmailToClient() {
		return emailToClient;
	}

	public void setEmailToClient(String emailToClient) {
		this.emailToClient = emailToClient;
	}

	public String getEmailToBooker() {
		return emailToBooker;
	}

	public void setEmailToBooker(String emailToBooker) {
		this.emailToBooker = emailToBooker;
	}

	public String getEmailToOther() {
		return emailToOther;
	}

	public void setEmailToOther(String emailToOther) {
		this.emailToOther = emailToOther;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getPickUpTime() {
		return pickUpTime;
	}

	public void setPickUpTime(String pickUpTime) {
		this.pickUpTime = pickUpTime;
	}

	public String getClientEmail() {
		return clientEmail;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

	public String getBookerEmail() {
		return bookerEmail;
	}

	public void setBookerEmail(String bookerEmail) {
		this.bookerEmail = bookerEmail;
	}

	public String getBookingPlatform() {
		return bookingPlatform;
	}

	public void setBookingPlatform(String bookingPlatform) {
		this.bookingPlatform = bookingPlatform;
	}

	public Date getClientApproveDt() {
		return clientApproveDt;
	}

	public void setClientApproveDt(Date clientApproveDt) {
		this.clientApproveDt = clientApproveDt;
	}

	public Date getPickUpDate() {
		return pickUpDate;
	}

	public void setPickUpDate(Date pickUpDate) {
		this.pickUpDate = pickUpDate;
	}

	public Long getMobId() {
		return mobId;
	}

	public void setMobId(Long mobId) {
		this.mobId = mobId;
	}

	public String getMobName() {
		return mobName;
	}

	public void setMobName(String mobName) {
		this.mobName = mobName;
	}

	public Long getDutySlipId() {
		return dutySlipId;
	}

	public void setDutySlipId(Long dutySlipId) {
		this.dutySlipId = dutySlipId;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public AutorizedUserModel getWebBookingTakenBy() {
		return webBookingTakenBy;
	}

	public void setWebBookingTakenBy(AutorizedUserModel webBookingTakenBy) {
		this.webBookingTakenBy = webBookingTakenBy;
	}
	
}
