package com.user.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.common.model.AddressDetailModel;
import com.common.model.ContactDetailModel;
import com.master.model.GeneralMasterModel;
import com.relatedInfo.model.RelatedInfoModel;

@Entity
@Table(name="users")


public class UserModel implements Serializable{

	private static final long serialVersionUID = 1L;
	public UserModel(){
		
	}
	
	public UserModel(String sCode,String userName,String password,String gender){
		this.userName=userName;
		this.password=password;
		this.gender=gender;
	}
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@Column(name="name",updatable=true,nullable=false,length=50,unique=true)
	private String userName=null;

	@Column(name="firstname",updatable=true,nullable=false,length=50,unique=false)
	private String userFirstName=null;

	@Column(name="lastname",updatable=true,nullable=false,length=50,unique=false)
	private String userLastName=null;

	@Column(name="mobile",updatable=true,nullable=false,length=50,unique=false)
	private String userMobile=null;

	@Column(name="password",updatable=true,nullable=false,length=50,unique=false)
	private String password=null;

	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="role",referencedColumnName="id",nullable=true,updatable=true)
	private GeneralMasterModel userRole = null;

	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="status",referencedColumnName="id",nullable=true,updatable=true)
	private GeneralMasterModel userStatus = null;
	
	@Transient
	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="department",referencedColumnName="id",nullable=true,updatable=true)
	private GeneralMasterModel userDept = null; 

	@Transient
	@Column(name="gender",updatable=true,nullable=true,length=8,unique=false)
	private String gender=null;

	@Transient
	@Column(name="dob",updatable=true,nullable=true,length=25,unique=false)
	private Date dob=null;

	@Transient
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "userModel")
	private ContactDetailModel contactDetailModel = null;

	@Transient
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "userModel")
	private AddressDetailModel addressDetailModel = null;

	@Transient
	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="userTypeId",referencedColumnName="id",nullable=true,updatable=true)
	private GeneralMasterModel userType = null;
	
	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="companyId",nullable=true,updatable=true)
	private GeneralMasterModel company = null;
	
	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="branchId",nullable=true,updatable=true)
	private GeneralMasterModel branch = null;
	
	@Column(name="assignBranches",updatable=true,nullable=true,length=100,unique=false)
	private String assignBranches=null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ContactDetailModel getContactDetailModel() {
		return contactDetailModel;
	}

	public void setContactDetailModel(ContactDetailModel contactDetailModel) {
		this.contactDetailModel = contactDetailModel;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public AddressDetailModel getAddressDetailModel() {
		return addressDetailModel;
	}

	public void setAddressDetailModel(AddressDetailModel addressDetailModel) {
		this.addressDetailModel = addressDetailModel;
	}

	public GeneralMasterModel getUserType() {
		return userType;
	}

	public void setUserType(GeneralMasterModel userType) {
		this.userType = userType;
	}

	public GeneralMasterModel getBranch() {
		return branch;
	}

	public void setBranch(GeneralMasterModel branch) {
		this.branch = branch;
	}

	public GeneralMasterModel getCompany() {
		return company;
	}

	public void setCompany(GeneralMasterModel company) {
		this.company = company;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public GeneralMasterModel getUserRole() {
		return userRole;
	}

	public void setUserRole(GeneralMasterModel userRole) {
		this.userRole = userRole;
	}

	public GeneralMasterModel getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(GeneralMasterModel userStatus) {
		this.userStatus = userStatus;
	}

	public GeneralMasterModel getUserDept() {
		return userDept;
	}

	public void setUserDept(GeneralMasterModel userDept) {
		this.userDept = userDept;
	}

	public String getAssignBranches() {
		return assignBranches;
	}

	public void setAssignBranches(String assignBranches) {
		this.assignBranches = assignBranches;
	}

}
