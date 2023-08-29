package com.common.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.relatedInfo.model.RelatedInfoModel;
import com.user.model.UserModel;

@Entity
@Table(name="contactDetails")
@JsonIgnoreProperties(value={"userModel","entityContact"})
public class ContactDetailModel implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@Column(name="contactPerson1",updatable=true,nullable=true,length=50,unique=false)
	private String contactPerson1=null;
	
	@Column(name="contactPerson2",updatable=true,nullable=true,length=50,unique=false)
	private String contactPerson2=null;
	
	@Column(name="residentialPhone",updatable=true,nullable=true,length=50,unique=false)
	private String residentialPhone=null;
	
	@Column(name="officialPhone",updatable=true,nullable=true,length=50,unique=false)
	private String officialPhone=null;

	@Column(name="personalMobile",updatable=true,nullable=true,length=30,unique=false)
	private String personalMobile=null;

	@Column(name="officialMobile",updatable=true,nullable=true,length=30,unique=false)
	private String officialMobile=null;

	@Column(name="personalEmailId",updatable=true,nullable=true,length=50,unique=false)
	private String personalEmailId=null;

	@Column(name="officialEmailId",updatable=true,nullable=true,length=50,unique=false)
	private String officialEmailId=null;

	@Column(name="residentialFaxNo",updatable=true,nullable=true,length=50,unique=false)
	private String residentialFaxNo=null;

	@Column(name="officialFaxNo",updatable=true,nullable=true,length=50,unique=false)
	private String officialFaxNo=null;

	@Column(name="personalWebsite",updatable=true,nullable=true,length=50,unique=false)
	private String personalWebsite=null;

	@Column(name="officialWebsite",updatable=true,nullable=true,length=50,unique=false)
	private String officialWebsite=null;

	@OneToOne(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name="userId",nullable=true,updatable=true)
	private UserModel userModel=null; 
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContactPerson1() {
		return contactPerson1;
	}

	public void setContactPerson1(String contactPerson1) {
		this.contactPerson1 = contactPerson1;
	}

	public String getContactPerson2() {
		return contactPerson2;
	}

	public void setContactPerson2(String contactPerson2) {
		this.contactPerson2 = contactPerson2;
	}

	public String getResidentialPhone() {
		return residentialPhone;
	}

	public void setResidentialPhone(String residentialPhone) {
		this.residentialPhone = residentialPhone;
	}

	public String getOfficialPhone() {
		return officialPhone;
	}

	public void setOfficialPhone(String officialPhone) {
		this.officialPhone = officialPhone;
	}

	public String getPersonalMobile() {
		return personalMobile;
	}

	public void setPersonalMobile(String personalMobile) {
		this.personalMobile = personalMobile;
	}

	public String getOfficialMobile() {
		return officialMobile;
	}

	public void setOfficialMobile(String officialMobile) {
		this.officialMobile = officialMobile;
	}

	public String getPersonalEmailId() {
		return personalEmailId;
	}

	public void setPersonalEmailId(String personalEmailId) {
		this.personalEmailId = personalEmailId;
	}

	public String getOfficialEmailId() {
		return officialEmailId;
	}

	public void setOfficialEmailId(String officialEmailId) {
		this.officialEmailId = officialEmailId;
	}

	public String getResidentialFaxNo() {
		return residentialFaxNo;
	}

	public void setResidentialFaxNo(String residentialFaxNo) {
		this.residentialFaxNo = residentialFaxNo;
	}

	public String getOfficialFaxNo() {
		return officialFaxNo;
	}

	public void setOfficialFaxNo(String officialFaxNo) {
		this.officialFaxNo = officialFaxNo;
	}

	public String getPersonalWebsite() {
		return personalWebsite;
	}

	public void setPersonalWebsite(String personalWebsite) {
		this.personalWebsite = personalWebsite;
	}

	public String getOfficialWebsite() {
		return officialWebsite;
	}

	public void setOfficialWebsite(String officialWebsite) {
		this.officialWebsite = officialWebsite;
	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}
}
