package com.relatedInfo.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name="relatedInfoDetails",uniqueConstraints= {@UniqueConstraint(columnNames={"parentType","parentId"})})
@JsonIgnoreProperties(value={"entityContact","entityAddress"})
public class RelatedInfoModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@Column(name="lstNo",nullable=true,updatable=true,length=20)
	private String lstNo = null;
	
	@Column(name="cstNo",nullable=true,updatable=true,length=20)
	private String cstNo = null;
	
	@Column(name="panNo",nullable=true,updatable=true,length=20)
	private String panNo = null;
	
	@Column(name="tanNo",nullable=true,updatable=true,length=20)
	private String tanNo = null;
	
	@Column(name="girNo",nullable=true,updatable=true,length=20)
	private String girNo = null;

	@Column(name="regDate",nullable=true,updatable=true,length=10)
	private String regDate = null;

	@Column(name="email",nullable=true,updatable=true,length=50)
	private String email = null;
	
	@Column(name="helpLine",nullable=true,updatable=true,length=50)
	private String helpLine = null;

	@Column(name="webSite",nullable=true,updatable=true,length=50)
	private String webSite = null;
	
	@Column(name="airportTerminals",nullable=true,updatable=true,length=100)
	private String airportTerminals = "";

	@OneToOne(cascade = CascadeType.ALL)
	private ContactDetailModel entityContact = null;
	
	@OneToOne(cascade = CascadeType.ALL)
	private AddressDetailModel entityAddress = null;
	
	@ManyToOne()
	@JoinColumn(name="parentId",nullable=false,updatable=true)
	private GeneralMasterModel generalMasterModel = new GeneralMasterModel();

	@Column(name="parentType",nullable=false,updatable=true)
	private String parentType = null;

	@Column(name="lastBooking",nullable=true,updatable=true,length=10)
	private Long lastBooking = null;

	@Column(name="lastDS",nullable=true,updatable=true,length=10)
	private Long lastDS = null;

	@Column(name="lastInvoice",nullable=true,updatable=true,length=10)
	private Long lastInvoice = null;

	@Column(name="lastCoverLetter",nullable=true,updatable=true,length=10)
	private Long lastCoverLetter = 0l;

	@Column(name="gstin",nullable=true,updatable=true,length=15)
	private String gstin = null;

	@Column(name="bankName",updatable=true,nullable=true,length=50,unique=false)
	private String bankName=null;
	
	@Column(name="bankAccNo",updatable=true,nullable=true,length=50,unique=false)
	private String bankAccNo=null;
	
	@Column(name="bankIFSC",updatable=true,nullable=true,length=20,unique=false)
	private String bankIFSC=null;
	
	@Transient
	private String companyName = null;
	
	@Transient
	private String branchName = null;

	@Transient
	private String outletName = null;
	
	@Transient
	AddressDetailModel addressDetailModel = null;

	@Transient
	ContactDetailModel contactDetailModel = null;
	
	@Transient
	private String parentValue = null;
	
	@Transient
	private Long company = null;

	@Transient
	private Long branch = null;

	@Transient
	private Long outlet = null;
	
	@Transient
	private String code = null;

	@Transient
	private String gstinBr = null;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}

	public Long getBranch() {
		return branch;
	}

	public void setBranch(Long branch) {
		this.branch = branch;
	}

	public Long getOutlet() {
		return outlet;
	}

	public void setOutlet(Long outlet) {
		this.outlet = outlet;
	}

	public String getLstNo() {
		return lstNo;
	}

	public void setLstNo(String lstNo) {
		this.lstNo = lstNo;
	}

	public String getCstNo() {
		return cstNo;
	}

	public void setCstNo(String cstNo) {
		this.cstNo = cstNo;
	}

	public String getPanNo() {
		return panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}

	public String getTanNo() {
		return tanNo;
	}

	public void setTanNo(String tanNo) {
		this.tanNo = tanNo;
	}

	public String getGirNo() {
		return girNo;
	}

	public void setGirNo(String girNo) {
		this.girNo = girNo;
	}



	public GeneralMasterModel getGeneralMasterModel() {
		return generalMasterModel;
	}

	public void setGeneralMasterModel(GeneralMasterModel generalMasterModel) {
		this.generalMasterModel = generalMasterModel;
	}

	public String getParentType() {
		return parentType;
	}

	public void setParentType(String parentType) {
		this.parentType = parentType;
	}

	public String getParentValue() {
		return parentValue;
	}

	public void setParentValue(String parentValue) {
		this.parentValue = parentValue;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHelpLine() {
		return helpLine;
	}

	public void setHelpLine(String helpLine) {
		this.helpLine = helpLine;
	}

	public String getWebSite() {
		return webSite;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getOutletName() {
		return outletName;
	}

	public void setOutletName(String outletName) {
		this.outletName = outletName;
	}

	public ContactDetailModel getEntityContact() {
		return entityContact;
	}

	public void setEntityContact(ContactDetailModel entityContact) {
		this.entityContact = entityContact;
	}

	public AddressDetailModel getEntityAddress() {
		return entityAddress;
	}

	public void setEntityAddress(AddressDetailModel entityAddress) {
		this.entityAddress = entityAddress;
	}

	public AddressDetailModel getAddressDetailModel() {
		return addressDetailModel;
	}

	public void setAddressDetailModel(AddressDetailModel addressDetailModel) {
		this.addressDetailModel = addressDetailModel;
	}

	public ContactDetailModel getContactDetailModel() {
		return contactDetailModel;
	}

	public void setContactDetailModel(ContactDetailModel contactDetailModel) {
		this.contactDetailModel = contactDetailModel;
	}

	public String getAirportTerminals() {
		return airportTerminals;
	}

	public void setAirportTerminals(String airportTerminals) {
		this.airportTerminals = airportTerminals;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getLastBooking() {
		return lastBooking;
	}

	public void setLastBooking(Long lastBooking) {
		this.lastBooking = lastBooking;
	}

	public Long getLastDS() {
		return lastDS;
	}

	public void setLastDS(Long lastDS) {
		this.lastDS = lastDS;
	}

	public Long getLastInvoice() {
		return lastInvoice;
	}

	public void setLastInvoice(Long lastInvoice) {
		this.lastInvoice = lastInvoice;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getGstinBr() {
		return gstinBr;
	}

	public void setGstinBr(String gstinBr) {
		this.gstinBr = gstinBr;
	}

	public Long getLastCoverLetter() {
		return lastCoverLetter;
	}

	public void setLastCoverLetter(Long lastCoverLetter) {
		this.lastCoverLetter = lastCoverLetter;
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
}
