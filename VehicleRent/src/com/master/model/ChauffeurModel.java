package com.master.model;

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
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.common.model.AddressDetailModel;
import com.common.model.ContactDetailModel;
import com.user.model.UserModel;

@Entity
@Table(name="Chauffeur",uniqueConstraints= {@UniqueConstraint(columnNames={"name","drivingLicence"})})
public class ChauffeurModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	/*Entry Date with time*/
	@Column(name="insDate",nullable=false,updatable=true,length=25)
	private Date insDate = new Date();
	/* Entry UserId for update or insert */
	@ManyToOne()
	@JoinColumn(name = "userId", nullable = false, updatable = true)
	private UserModel userId = new UserModel();

	/*Vendor ID / Name*/
	@ManyToOne()
	@JoinColumn(name = "vendorId", nullable = false, updatable = true)
	private VendorModel vendorId = new VendorModel();
	/*Chauffeur Name*/
	@Column(name="name",nullable=false,updatable=true,length=50)
	private String name = null;
	/*MobileNo*/
	@Column(name="mobileNo",nullable=false,updatable=true,length=20)
	private String mobileNo = null;
	/*Driving Licence*/
	@Column(name="drivingLicence",nullable=false,updatable=true,length=20)
	private String drivingLicence = null;
	/*Driving Licence Issue Date*/
	@Column(name="dlIssue",nullable=false,updatable=true,length=25)
	private Date dlIssue = null;
	/*Driving Licence Valid Date*/
	@Column(name="dlValidUpto",nullable=false,updatable=true,length=25)
	private Date dlValidUpto = null;
	/*Driving Licence Authority*/
	@Column(name="dlAuthority",nullable=false,updatable=true,length=50)
	private String dlAuthority = null;
	/*Driving Licence Authority Address*/
	@Column(name="dlAuthorityAddr",nullable=true,updatable=true,length=250)
	private String dlAuthorityAddr = null;
	/*Date Of Birth*/
	@Column(name="dob",nullable=true,updatable=true,length=25)
	private Date dob = null;
	/*Date Of Joining*/
	@Column(name="doj",nullable=false,updatable=true,length=25)
	private Date doj = null;
	/*Badge No*/
	@Column(name="badgeNo",nullable=true,updatable=true,length=20)
	private String badgeNo = null;
	/*Blood Group*/
	@Column(name="bloodGrp",nullable=true,updatable=true,length=10)
	private String bloodGrp = null;
	/*Duties Allow*/
	@Column(name="dutyAllow",nullable=false,updatable=true,length=1)
	private String dutyAllow = null;
	/*Status*/
	@Column(name="status",nullable=false,updatable=true,length=1)
	private String status = null;
	/*Qualification*/
	@Column(name="qualification",nullable=true,updatable=true,length=100)
	private String qualification = null;
	/*Experiance*/
	@Column(name="experience",nullable=true,updatable=true,length=100)
	private String experience = null;
	
	/*Contact Person*/
	@Column(name="idMark",nullable=true,updatable=true,length=100)
	private String idMark = null;
	/*presentAddress*/
	@Column(name="presentAddress",nullable=true,updatable=true,length=200)
	private String presentAddress = null;
	/*Temporary Address PIN*/
	@Column(name="presentAddressPIN",nullable=true,updatable=true,length=6)
	private String presentAddressPIN = null;

	/*Permanent Address*/
	@Column(name="peramentAddress",nullable=true,updatable=true,length=200)
	private String peramentAddress = null;
	/*Permanent Address PIN*/
	@Column(name="peramentAddressPIN",nullable=true,updatable=true,length=6)
	private String peramentAddressPIN = null;
	/*Reference Name*/
	@Column(name="refName",nullable=true,updatable=true,length=50)
	private String refName = null;
	/*Reference Name*/
	@Column(name="refMobileNo",nullable=true,updatable=true,length=20)
	private String refMobileNo = null;
	/*Reference Name*/
	@Column(name="refAddress",nullable=true,updatable=true,length=200)
	private String refAddress = null;
	
	/*userId for Login*/
	@Column(name="userName",nullable=true,updatable=true,length=50)
	private String userName = null;
	
	/*password for Login*/
	@Column(name="pswd",nullable=true,updatable=true,length=100)
	private String pswd = null;

	/*first login*/
	@Column(name="firstLogin",nullable=true,updatable=true,length=1)
	private String firstLogin = "Y";

	/*otp*/
	@Column(name="otp",nullable=true,updatable=true,length=10)
	private String otp = null;
	/*regId for notification*/
	@Column(name="regId",nullable=true,updatable=true,unique=true)
	private String regId = null;

	@Transient
	private String refreshTime;
	
	@Transient
	private String refreshMeter;
	
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
	public VendorModel getVendorId() {
		return vendorId;
	}
	public void setVendorId(VendorModel vendorId) {
		this.vendorId = vendorId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getDrivingLicence() {
		return drivingLicence;
	}
	public void setDrivingLicence(String drivingLicence) {
		this.drivingLicence = drivingLicence;
	}
	public Date getDlIssue() {
		return dlIssue;
	}
	public void setDlIssue(Date dlIssue) {
		this.dlIssue = dlIssue;
	}
	public Date getDlValidUpto() {
		return dlValidUpto;
	}
	public void setDlValidUpto(Date dlValidUpto) {
		this.dlValidUpto = dlValidUpto;
	}
	public String getDlAuthority() {
		return dlAuthority;
	}
	public void setDlAuthority(String dlAuthority) {
		this.dlAuthority = dlAuthority;
	}
	public String getDlAuthorityAddr() {
		return dlAuthorityAddr;
	}
	public void setDlAuthorityAddr(String dlAuthorityAddr) {
		this.dlAuthorityAddr = dlAuthorityAddr;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public Date getDoj() {
		return doj;
	}
	public void setDoj(Date doj) {
		this.doj = doj;
	}
	public String getBadgeNo() {
		return badgeNo;
	}
	public void setBadgeNo(String badgeNo) {
		this.badgeNo = badgeNo;
	}
	public String getBloodGrp() {
		return bloodGrp;
	}
	public void setBloodGrp(String bloodGrp) {
		this.bloodGrp = bloodGrp;
	}
	public String getDutyAllow() {
		return dutyAllow;
	}
	public void setDutyAllow(String dutyAllow) {
		this.dutyAllow = dutyAllow;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	public String getExperience() {
		return experience;
	}
	public void setExperience(String experience) {
		this.experience = experience;
	}
	public String getIdMark() {
		return idMark;
	}
	public void setIdMark(String idMark) {
		this.idMark = idMark;
	}
	public String getPresentAddress() {
		return presentAddress;
	}
	public void setPresentAddress(String presentAddress) {
		this.presentAddress = presentAddress;
	}
	public String getPresentAddressPIN() {
		return presentAddressPIN;
	}
	public void setPresentAddressPIN(String presentAddressPIN) {
		this.presentAddressPIN = presentAddressPIN;
	}
	public String getPeramentAddress() {
		return peramentAddress;
	}
	public void setPeramentAddress(String peramentAddress) {
		this.peramentAddress = peramentAddress;
	}
	public String getPeramentAddressPIN() {
		return peramentAddressPIN;
	}
	public void setPeramentAddressPIN(String peramentAddressPIN) {
		this.peramentAddressPIN = peramentAddressPIN;
	}
	public String getRefName() {
		return refName;
	}
	public void setRefName(String refName) {
		this.refName = refName;
	}
	public String getRefMobileNo() {
		return refMobileNo;
	}
	public void setRefMobileNo(String refMobileNo) {
		this.refMobileNo = refMobileNo;
	}
	public String getRefAddress() {
		return refAddress;
	}
	public void setRefAddress(String refAddress) {
		this.refAddress = refAddress;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPswd() {
		return pswd;
	}
	public void setPswd(String pswd) {
		this.pswd = pswd;
	}
	public String getFirstLogin() {
		return firstLogin;
	}
	public void setFirstLogin(String firstLogin) {
		this.firstLogin = firstLogin;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public String getRefreshTime() {
		return refreshTime;
	}
	public void setRefreshTime(String refreshTime) {
		this.refreshTime = refreshTime;
	}
	public String getRefreshMeter() {
		return refreshMeter;
	}
	public void setRefreshMeter(String refreshMeter) {
		this.refreshMeter = refreshMeter;
	}
	public String getRegId() {
		return regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
}
