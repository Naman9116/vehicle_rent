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

import com.common.model.AddressDetailModel;
import com.common.model.ContactDetailModel;
import com.master.model.GeneralMasterModel;
import com.user.model.UserModel;

@Entity
@Table(name="corporateDetails",uniqueConstraints= {@UniqueConstraint(columnNames={"name","compId","branId","penIndia","agreementDt"})})
@JsonIgnoreProperties(value={"entityContact","entityAddress"})
public class CorporateModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	/*Entry Date with time*/
	@Column(name="insDate",nullable=false,updatable=true,length=25)
	private Date insDate = new Date();
	
	/*Corporate Name*/
	@Column(name="name",nullable=false,updatable=true,length=70)
	private String name = null;
	/*Agreement Date%*/
	@Column(name="agreementDt",nullable=false,updatable=true,length=25)
	private Date agreementDt = null;
	/*Agreement Expiry Date %*/
	@Column(name="expiryDt",nullable=false,updatable=true,length=25)
	private Date expiryDt = null;
	/*Service Tax %*/
	@Column(name="sTax",nullable=true,updatable=true,length=20)
	private Double sTax = null;
	/*discount %*/
	@Column(name="discount",nullable=true,updatable=true,length=20)
	private Double discount = null;
	/*education cess%*/
	@Column(name="eduCess",nullable=true,updatable=true,length=20)
	private Double eduCess = null;
	/*HEC %*/
	@Column(name="hec",nullable=true,updatable=true,length=20)
	private Double hec = null;
	/*Service Tac Calculated on Billing*/
	@Column(name="sTaxCalBill",nullable=true,updatable=true,length=1)
	private String sTaxCalBill = null;
	/*Service Tac Calculated on FuelSurcharge*/
	@Column(name="sTaxCalFuel",nullable=true,updatable=true,length=1)
	private String sTaxCalFuel = null;
	/*Service Tac Calculated on parking*/
	@Column(name="sTaxCalPark",nullable=true,updatable=true,length=1)
	private String sTaxCalPark = null;
	
	/*Discount calculated on Billing*/
	@Column(name="sDiscCalBill",nullable=true,updatable=true,length=1)
	private String sDiscCalBill = null;
	/*Discount calculated on Extra Km*/
	@Column(name="sDiscCalKm",nullable=true,updatable=true,length=1)
	private String sDiscCalKm = null;
	/*Discount calculated on Extra Hrs*/
	@Column(name="sDiscCalHrs",nullable=true,updatable=true,length=1)
	private String sDiscCalHrs = null;
	
	/*VAT calculated on Parking also Y=yes, N=No*/
	@Column(name="vatOnPark",nullable=true,updatable=true,length=1)
	private String  vatOnPark = null;

	@Column(name="tariffs",nullable=true,updatable=true,length=100)
	private String tariffs;

	/*Booking allow for corporate Y=Yes, N=No*/
	@Column(name="bookAllow",nullable=true,updatable=true,length=1)
	private String bookAllow = null;
	/*Credit cycle in days*/
	@Column(name="crCycle",nullable=true,updatable=true,length=10)
	private long crCycle = 0l;
	
	/*branch ID*/
	@Column(name="branId",nullable=true,updatable=true,length=10)
	private Long branId;

	/*Flag for all branches*/
	@Column(name="penIndia",nullable=true,updatable=true,length=1)
	private String penIndia;
	
	/*Flag for all branches*/
	@Column(name="status",nullable=true,updatable=true,length=1)
	private String status;
	
	/*Flag for all PassengerDetails*/
	@Column(name="isPassengerInfo",nullable=true,updatable=true,length=1)
	private String isPassengerInfo=null;
	
	
	
	public String getIsPassengerInfo() {
		return isPassengerInfo;
	}

	public void setIsPassengerInfo(String isPassengerInfo) {
		this.isPassengerInfo = isPassengerInfo;
	}

	/*Corporate Address*/
	@Transient
	AddressDetailModel addressDetailModel = null;
	@OneToOne(cascade = CascadeType.ALL)
	private AddressDetailModel entityAddress = null;
	/*Corporate contact details*/
	@Transient
	ContactDetailModel contactDetailModel = null;
	@OneToOne(cascade = CascadeType.ALL)
	private ContactDetailModel entityContact = null;
	
	@ManyToOne()
	@JoinColumn(name="compId",nullable=false,updatable=true)
	private GeneralMasterModel compId = new GeneralMasterModel();
	
	@ManyToOne()
	@JoinColumn(name="billingCycle",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel billingCycle = new GeneralMasterModel();

	@ManyToOne()
	@JoinColumn(name="userId",nullable=false,updatable=true)
	private UserModel userId = new UserModel();

	/*MinuteRounding Slab*/
	@Column(name="minRoundSlab",nullable=true,updatable=true,length=10)
	private Long minRoundSlab;
	
	@Column(name="roundedOff",nullable=true,updatable=true,length=1)
	private String roundedOff;
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getAgreementDt() {
		return agreementDt;
	}

	public void setAgreementDt(Date agreementDt) {
		this.agreementDt = agreementDt;
	}

	public Date getExpiryDt() {
		return expiryDt;
	}

	public void setExpiryDt(Date expiryDt) {
		this.expiryDt = expiryDt;
	}

	public Double getsTax() {
		return sTax;
	}

	public void setsTax(Double sTax) {
		this.sTax = sTax;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getEduCess() {
		return eduCess;
	}

	public void setEduCess(Double eduCess) {
		this.eduCess = eduCess;
	}

	public Double getHec() {
		return hec;
	}

	public void setHec(Double hec) {
		this.hec = hec;
	}

	public String getVatOnPark() {
		return vatOnPark;
	}

	public void setVatOnPark(String vatOnPark) {
		this.vatOnPark = vatOnPark;
	}

	public String getBookAllow() {
		return bookAllow;
	}

	public void setBookAllow(String bookAllow) {
		this.bookAllow = bookAllow;
	}

	public long getCrCycle() {
		return crCycle;
	}

	public void setCrCycle(long crCycle) {
		this.crCycle = crCycle;
	}

	public AddressDetailModel getAddressDetailModel() {
		return addressDetailModel;
	}

	public void setAddressDetailModel(AddressDetailModel addressDetailModel) {
		this.addressDetailModel = addressDetailModel;
	}

	public AddressDetailModel getEntityAddress() {
		return entityAddress;
	}

	public void setEntityAddress(AddressDetailModel entityAddress) {
		this.entityAddress = entityAddress;
	}

	public ContactDetailModel getContactDetailModel() {
		return contactDetailModel;
	}

	public void setContactDetailModel(ContactDetailModel contactDetailModel) {
		this.contactDetailModel = contactDetailModel;
	}

	public ContactDetailModel getEntityContact() {
		return entityContact;
	}

	public void setEntityContact(ContactDetailModel entityContact) {
		this.entityContact = entityContact;
	}

	public GeneralMasterModel getCompId() {
		return compId;
	}

	public void setCompId(GeneralMasterModel compId) {
		this.compId = compId;
	}

	public GeneralMasterModel getBillingCycle() {
		return billingCycle;
	}

	public void setBillingCycle(GeneralMasterModel billingCycle) {
		this.billingCycle = billingCycle;
	}

	public String getsTaxCalBill() {
		return sTaxCalBill;
	}

	public void setsTaxCalBill(String sTaxCalBill) {
		this.sTaxCalBill = sTaxCalBill;
	}

	public String getsTaxCalFuel() {
		return sTaxCalFuel;
	}

	public void setsTaxCalFuel(String sTaxCalFuel) {
		this.sTaxCalFuel = sTaxCalFuel;
	}

	public String getsTaxCalPark() {
		return sTaxCalPark;
	}

	public void setsTaxCalPark(String sTaxCalPark) {
		this.sTaxCalPark = sTaxCalPark;
	}

	public String getsDiscCalBill() {
		return sDiscCalBill;
	}

	public void setsDiscCalBill(String sDiscCalBill) {
		this.sDiscCalBill = sDiscCalBill;
	}

	public String getsDiscCalKm() {
		return sDiscCalKm;
	}

	public void setsDiscCalKm(String sDiscCalKm) {
		this.sDiscCalKm = sDiscCalKm;
	}

	public String getsDiscCalHrs() {
		return sDiscCalHrs;
	}

	public void setsDiscCalHrs(String sDiscCalHrs) {
		this.sDiscCalHrs = sDiscCalHrs;
	}

	public String getPenIndia() {
		return penIndia;
	}

	public void setPenIndia(String penIndia) {
		this.penIndia = penIndia;
	}

	public void setBranId(Long branId) {
		this.branId = branId;
	}

	public Long getBranId() {
		return branId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public UserModel getUserId() {
		return userId;
	}

	public void setUserId(UserModel userId) {
		this.userId = userId;
	}

	public String getTariffs() {
		return tariffs;
	}

	public void setTariffs(String tariffs) {
		this.tariffs = tariffs;
	}

	public Long getMinRoundSlab() {
		return minRoundSlab;
	}

	public void setMinRoundSlab(Long minRoundSlab) {
		this.minRoundSlab = minRoundSlab;
	}

	public String getRoundedOff() {
		return roundedOff;
	}

	public void setRoundedOff(String roundedOff) {
		this.roundedOff = roundedOff;
	}
}
