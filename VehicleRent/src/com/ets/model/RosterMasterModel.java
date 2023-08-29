package com.ets.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.corporate.model.AutorizedUserModel;
import com.corporate.model.CorporateModel;
import com.master.model.GeneralMasterModel;
import com.user.model.UserModel;

@Entity
@Table(name="rosterDetails", uniqueConstraints= {@UniqueConstraint(columnNames={"corporateId","shiftTime","rosterDate","authoriseClientId"})})
public class RosterMasterModel  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@ManyToOne(targetEntity=CorporateModel.class)
	@JoinColumn(name="corporateId",referencedColumnName="id",nullable=false,updatable=true,unique=false)
	private CorporateModel corporateId = null;
	
	@Column(name="authoriseClientId",updatable=true,nullable=false,length=10,unique=false)
	private Long authoriseClientId = null;
	
	@Column(name="mobileNo",updatable=true,nullable=false,length=10,unique=false)
	private String mobileNo = null;
	
	@ManyToOne()
	@JoinColumn(name="branch",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel branch = null;
	
	@ManyToOne()
	@JoinColumn(name="outlet",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel outlet = null;
	
	@ManyToOne()
	@JoinColumn(name="bookedBy",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private AutorizedUserModel bookedBy = null;
	
	@ManyToOne()
	@JoinColumn(name="rosterTakenBy",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private UserModel rosterTakenBy = null;
	
	@Column(name="carType",updatable=true,nullable=false,unique=false)
	private Long carType = null;
	
	@ManyToOne()
	@JoinColumn(name="shiftTime",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel shiftTime=null;
	
	@Temporal(TemporalType.DATE)
	@Column(name="rosterDate",updatable=true,nullable=true,length=10,unique=false)
	private Date rosterDate = null;
	
	@Column(name="routeNo",updatable=true,nullable=true,length=10,unique=false)
	private String routeNo = null;
	
	@Column(name="pickUpTime",updatable=true,nullable=true,unique=false)
	private String pickUpTime = null;
	
	@Column(name="isPickup",updatable=true,nullable=true,unique=false)
	private String isPickup = "N";
	
	@Column(name="isDrop",updatable=true,nullable=true,unique=false)
	private String isDrop = "N";
	
	@Column(name="insDate",nullable=false,updatable=true,length=25)
	private Date insDate = new Date();
	
	@ManyToOne()
	@JoinColumn(name = "userId", nullable = false, updatable = true)
	private UserModel userId = null;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CorporateModel getCorporateId() {
		return corporateId;
	}

	public void setCorporateId(CorporateModel corporateId) {
		this.corporateId = corporateId;
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

	public Long getAuthoriseClientId() {
		return authoriseClientId;
	}

	public void setAuthoriseClientId(Long authoriseClientId) {
		this.authoriseClientId = authoriseClientId;
	}

	public AutorizedUserModel getBookedBy() {
		return bookedBy;
	}

	public void setBookedBy(AutorizedUserModel bookedBy) {
		this.bookedBy = bookedBy;
	}

	public UserModel getRosterTakenBy() {
		return rosterTakenBy;
	}

	public void setRosterTakenBy(UserModel rosterTakenBy) {
		this.rosterTakenBy = rosterTakenBy;
	}

	public Long getCarType() {
		return carType;
	}

	public void setCarType(Long carType) {
		this.carType = carType;
	}

	public GeneralMasterModel getShiftTime() {
		return shiftTime;
	}

	public void setShiftTime(GeneralMasterModel shiftTime) {
		this.shiftTime = shiftTime;
	}

	public Date getRosterDate() {
		return rosterDate;
	}

	public void setRosterDate(Date rosterDate) {
		this.rosterDate = rosterDate;
	}

	public String getRouteNo() {
		return routeNo;
	}

	public void setRouteNo(String routeNo) {
		this.routeNo = routeNo;
	}

	public String getPickUpTime() {
		return pickUpTime;
	}

	public void setPickUpTime(String pickUpTime) {
		this.pickUpTime = pickUpTime;
	}

	public String getIsPickup() {
		return isPickup;
	}

	public void setIsPickup(String isPickup) {
		this.isPickup = isPickup;
	}

	public String getIsDrop() {
		return isDrop;
	}

	public void setIsDrop(String isDrop) {
		this.isDrop = isDrop;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	
		
}
