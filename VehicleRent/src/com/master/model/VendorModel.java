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
@Table(name="vendor",uniqueConstraints= {@UniqueConstraint(columnNames={"name","agDate"})})
public class VendorModel implements Serializable{

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
	@JoinColumn(name = "userId",nullable=false, updatable = true)
	private UserModel userId = new UserModel();

	/*Vendor Name*/
	@Column(name="name",nullable=false,updatable=true,length=50)
	private String name = null;
	/*PAN */
	@Column(name="pan",nullable=false,updatable=true,length=10)
	private String pan = null;
	/*ServiceTax */
	@Column(name="sTaxNo",nullable=true,updatable=true,length=20)
	private String sTaxNo = null;
	/*TAN*/
	@Column(name="tan",nullable=true,updatable=true,length=10)
	private String tan = null;
	/*LSTNo*/
	@Column(name="lstno",nullable=true,updatable=true,length=20)
	private String lstno = null;
	/*Date Of Agreement*/
	@Column(name="agDate",nullable=false,updatable=true,length=25)
	private Date agDate = null;
	/*HelpLineNo*/
	@Column(name="helpLineNo",nullable=true,updatable=true,length=30)
	private String helpLineNo = null;
	/*HelpLineNo*/
	@Column(name="email",nullable=true,updatable=true,length=50)
	private String email = null;
	/*Organization Type*/
	@ManyToOne()
	@JoinColumn(name="orgTypeId",nullable=false,updatable=true)
	private GeneralMasterModel orgTypeId = new GeneralMasterModel();
	/*Duties Allow*/
	@Column(name="dutyAllow",nullable=true,updatable=true,length=1)
	private String dutyAllow = null;
	/*Status*/
	@Column(name="status",nullable=true,updatable=true,length=1)
	private String status = null;
	/*Bank Account Holder*/
	@Column(name="bankAcName",nullable=true,updatable=true,length=50)
	private String bankAcName = null;
	/*Bank Account Number*/
	@Column(name="bankAcNo",nullable=true,updatable=true,length=25)
	private String bankAcNo = null;
	/*Bank Name*/
	@Column(name="bankName",nullable=true,updatable=true,length=50)
	private String bankName = null;
	/*Bank NEFT Code*/
	@Column(name="bankNEFT",nullable=true,updatable=true,length=20)
	private String bankNEFT = null;
	/*Bank RTGS Code*/
	@Column(name="bankRTGS",nullable=true,updatable=true,length=20)
	private String bankRTGS = null;
	
	/*Contact Person*/
	@Column(name="contPerson",nullable=true,updatable=true,length=50)
	private String contPerson = null;
	
	/*Contact Person MobileNo*/
	@Column(name="contPersonMobile",nullable=true,updatable=true,length=50)
	private String contPersonMobile = null;

	/*Permanent Address*/
	@Column(name="perAddress",nullable=true,updatable=true,length=200)
	private String perAddress = null;
	
	/*Permanent Address PIN*/
	@Column(name="perAddressPIN",nullable=true,updatable=true,length=6)
	private String perAddressPIN = null;

	/*Temporary Address*/
	@Column(name="temAddress",nullable=true,updatable=true,length=200)
	private String temAddress = null;
	
	/*Temporary Address PIN*/
	@Column(name="temAddressPIN",nullable=true,updatable=true,length=6)
	private String temAddressPIN = null;

	/*Is Vendor Also a Chauffeur*/
	@Column(name="isChauffeur",nullable=true,updatable=true,length=1)
	private String isChauffeur= "N";
	
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getsTaxNo() {
		return sTaxNo;
	}
	public void setsTaxNo(String sTaxNo) {
		this.sTaxNo = sTaxNo;
	}
	public Date getAgDate() {
		return agDate;
	}
	public void setAgDate(Date agDate) {
		this.agDate = agDate;
	}
	public GeneralMasterModel getOrgTypeId() {
		return orgTypeId;
	}
	public void setOrgTypeId(GeneralMasterModel orgTypeId) {
		this.orgTypeId = orgTypeId;
	}
	public String getPan() {
		return pan;
	}
	public void setPan(String pan) {
		this.pan = pan;
	}
	public String getTan() {
		return tan;
	}
	public void setTan(String tan) {
		this.tan = tan;
	}
	public String getLstno() {
		return lstno;
	}
	public void setLstno(String lstno) {
		this.lstno = lstno;
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
	public String getBankAcName() {
		return bankAcName;
	}
	public void setBankAcName(String bankAcName) {
		this.bankAcName = bankAcName;
	}
	public String getBankAcNo() {
		return bankAcNo;
	}
	public void setBankAcNo(String bankAcNo) {
		this.bankAcNo = bankAcNo;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankNEFT() {
		return bankNEFT;
	}
	public void setBankNEFT(String bankNEFT) {
		this.bankNEFT = bankNEFT;
	}
	public String getBankRTGS() {
		return bankRTGS;
	}
	public void setBankRTGS(String bankRTGS) {
		this.bankRTGS = bankRTGS;
	}
	public String getContPerson() {
		return contPerson;
	}
	public void setContPerson(String contPerson) {
		this.contPerson = contPerson;
	}
	public String getContPersonMobile() {
		return contPersonMobile;
	}
	public void setContPersonMobile(String contPersonMobile) {
		this.contPersonMobile = contPersonMobile;
	}
	public String getPerAddress() {
		return perAddress;
	}
	public void setPerAddress(String perAddress) {
		this.perAddress = perAddress;
	}
	public String getPerAddressPIN() {
		return perAddressPIN;
	}
	public void setPerAddressPIN(String perAddressPIN) {
		this.perAddressPIN = perAddressPIN;
	}
	public String getTemAddress() {
		return temAddress;
	}
	public void setTemAddress(String temAddress) {
		this.temAddress = temAddress;
	}
	public String getTemAddressPIN() {
		return temAddressPIN;
	}
	public void setTemAddressPIN(String temAddressPIN) {
		this.temAddressPIN = temAddressPIN;
	}
	public String getHelpLineNo() {
		return helpLineNo;
	}
	public void setHelpLineNo(String helpLineNo) {
		this.helpLineNo = helpLineNo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIsChauffeur() {
		return isChauffeur;
	}
	public void setIsChauffeur(String isChauffeur) {
		this.isChauffeur = isChauffeur;
	}
}
