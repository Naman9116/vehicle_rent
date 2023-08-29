package com.corporate.model;

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

import com.common.model.ContactDetailModel;
import com.ets.model.LocationMasterModel;
import com.master.model.CityMasterModel;
import com.master.model.GeneralMasterModel;
import com.user.model.UserModel;

@Entity
@Table(name="autorizedUser",uniqueConstraints= {@UniqueConstraint(columnNames={"corporateId","name"})})
@JsonIgnoreProperties(value={"entityContact"})
public class AutorizedUserModel implements Serializable{
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
	
	/* Corporate Id for which user belongs */
	@ManyToOne()
	@JoinColumn(name = "corporateId", nullable = false, updatable = true)
	private CorporateModel corporateId = new CorporateModel();
	/*Authorization Type*/
	@Column(name="authTypeAdmin",nullable=true,updatable=true,length=1)
	private String authTypeAdmin = null;
	/*Authorization Type*/
	@Column(name="authTypeClient",nullable=true,updatable=true,length=1)
	private String authTypeClient = null;
	/*Authoriser User Name*/
	@Column(name="name",nullable=false,updatable=true,length=50)
	private String name = null;
	/*Employee Code of Authoriser User*/
	@Column(name="empCode",nullable=false,updatable=true,length=30)
	private String empCode = null;
	/*Employee Gender*/
	@Column(name="gender",nullable=true,updatable=true,length=1)
	private String gender = null;
	/*Priority of the user*/
	@ManyToOne()
	@JoinColumn(name="priorityId",nullable=false,updatable=true)
	private GeneralMasterModel priorityId = new GeneralMasterModel();
	/*Booking Allow*/
	@Column(name="bkgAllow",nullable=true,updatable=true,length=1)
	private String bkgAllow = null;
	/*Status Allow*/
	@Column(name="status",nullable=true,updatable=true,length=1)
	private String status = null;
	/*Authorised User Contact*/

	@OneToOne(cascade = CascadeType.ALL)
	private ContactDetailModel entityContact = null;
	/*Status Allow*/
	@Column(name="oAddress",nullable=true,updatable=true,length=150)
	private String oAddress = null;
	
	@Column(name="oAddressLatLong",nullable=true,updatable=true,length=150)
	private String oAddressLatLong = null;
	
	/*Status Allow*/
	@Column(name="hAddress",nullable=true,updatable=true,length=50)
	private String hAddress = null;
	
	@Column(name="hAddressLatLong",nullable=true,updatable=true,length=50)
	private String hAddressLatLong = null;
	
	/*Parent if user is client*/
	@Column(name="parentId",nullable=true,updatable=true,length=15)
	private Long parentId = 0l;
	
	/*userId for Login*/
	@Column(name="userName",nullable=true,updatable=true,length=50)
	private String userName = null;
	
	/*password for Login*/
	@Column(name="pswd",nullable=true,updatable=true,length=100)
	private String pswd = null;

	/*first login*/
	@Column(name="firstLogin",nullable=true,updatable=true,length=1)
	private String firstLogin = "Y";
	
	@Column(name="division",nullable=true,updatable=true,length=100)
	private String division = null;
	
	@ManyToOne()
	@JoinColumn(name="location",referencedColumnName="id",nullable=true,updatable=true)
	private LocationMasterModel location = null;

	@ManyToOne()
	@JoinColumn(name="zone",referencedColumnName="id",nullable=true,updatable=true)
	private GeneralMasterModel zone = null;
	
	@ManyToOne()
	@JoinColumn(name="city",referencedColumnName="id",nullable=true,updatable=true)
	private CityMasterModel city = null;

	@Column(name="etsAllow",nullable=true,updatable=true,length=1)
	private String etsAllow = "N";

	/*otp*/
	@Column(name="otp",nullable=true,updatable=true,length=10)
	private String otp = null;

	
	@Transient
	private String pageFor = null;
	
	@Transient
	ContactDetailModel contactDetailModel = null;
	
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
	public CorporateModel getCorporateId() {
		return corporateId;
	}
	public void setCorporateId(CorporateModel corporateId) {
		this.corporateId = corporateId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmpCode() {
		return empCode;
	}
	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}
	public GeneralMasterModel getPriorityId() {
		return priorityId;
	}
	public void setPriorityId(GeneralMasterModel priorityId) {
		this.priorityId = priorityId;
	}
	public String getBkgAllow() {
		return bkgAllow;
	}
	public void setBkgAllow(String bkgAllow) {
		this.bkgAllow = bkgAllow;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ContactDetailModel getEntityContact() {
		return entityContact;
	}
	public void setEntityContact(ContactDetailModel entityContact) {
		this.entityContact = entityContact;
	}
	public String getAuthTypeAdmin() {
		return authTypeAdmin;
	}
	public void setAuthTypeAdmin(String authTypeAdmin) {
		this.authTypeAdmin = authTypeAdmin;
	}
	public String getAuthTypeClient() {
		return authTypeClient;
	}
	public void setAuthTypeClient(String authTypeClient) {
		this.authTypeClient = authTypeClient;
	}
	public String getoAddress() {
		return oAddress;
	}
	public void setoAddress(String oAddress) {
		this.oAddress = oAddress;
	}
	public String gethAddress() {
		return hAddress;
	}
	public void sethAddress(String hAddress) {
		this.hAddress = hAddress;
	}
	public UserModel getUserId() {
		return userId;
	}
	public void setUserId(UserModel userId) {
		this.userId = userId;
	}
	public ContactDetailModel getContactDetailModel() {
		return contactDetailModel;
	}
	public void setContactDetailModel(ContactDetailModel contactDetailModel) {
		this.contactDetailModel = contactDetailModel;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getPageFor() {
		return pageFor;
	}
	public void setPageFor(String pageFor) {
		this.pageFor = pageFor;
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
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public LocationMasterModel getLocation() {
		return location;
	}
	public void setLocation(LocationMasterModel location) {
		this.location = location;
	}
	public GeneralMasterModel getZone() {
		return zone;
	}
	public void setZone(GeneralMasterModel zone) {
		this.zone = zone;
	}
	public CityMasterModel getCity() {
		return city;
	}
	public void setCity(CityMasterModel city) {
		this.city = city;
	}
	public String getEtsAllow() {
		return etsAllow;
	}
	public void setEtsAllow(String etsAllow) {
		this.etsAllow = etsAllow;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public String getoAddressLatLong() {
		return oAddressLatLong;
	}
	public void setoAddressLatLong(String oAddressLatLong) {
		this.oAddressLatLong = oAddressLatLong;
	}
	public String gethAddressLatLong() {
		return hAddressLatLong;
	}
	public void sethAddressLatLong(String hAddressLatLong) {
		this.hAddressLatLong = hAddressLatLong;
	}
}
