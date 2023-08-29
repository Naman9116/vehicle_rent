package com.common.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.master.model.CityMasterModel;
import com.master.model.DistrictMasterModel;
import com.master.model.StateMasterModel;
import com.user.model.UserModel;

@Entity
@Table(name="addressDetails")
@JsonIgnoreProperties(value={"userModel","entityAddress"})
public class AddressDetailModel implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@Column(name="address1",updatable=true,nullable=true,length=250,unique=false)
	private String address1=null;
	
	@Column(name="address2",updatable=true,nullable=true,length=250,unique=false)
	private String address2=null;
	
	@Column(name="landMark",updatable=true,nullable=true,length=200,unique=false)
	private String landMark=null;
	
	@Column(name="pincode",updatable=true,nullable=true,length=6,unique=false)
	private Long pincode;

	@ManyToOne(targetEntity=StateMasterModel.class)
	@JoinColumn(name="stateId",referencedColumnName="id",nullable=true,updatable=true)
	private StateMasterModel state = null;

	@ManyToOne(targetEntity=DistrictMasterModel.class)
	@JoinColumn(name="districtId",referencedColumnName="id",nullable=true,updatable=true)
	private DistrictMasterModel district = null;

	@ManyToOne(targetEntity=CityMasterModel.class)
	@JoinColumn(name="cityId",referencedColumnName="id",nullable=true,updatable=true)
	private CityMasterModel city = null;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="userId",referencedColumnName="id",nullable=true,updatable=true)
	private UserModel userModel=null;
	
	@Column(name="entityType",updatable=true,nullable=true,length=20,unique=false)
	private String entityType;
	
	@Column(name="entityId",updatable=true,nullable=true,length=15,unique=false)
	private Long entityId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getLandMark() {
		return landMark;
	}

	public void setLandMark(String landMark) {
		this.landMark = landMark;
	}

	public Long getPincode() {
		return pincode;
	}

	public void setPincode(Long pincode) {
		this.pincode = pincode;
	}

	public StateMasterModel getState() {
		return state;
	}

	public void setState(StateMasterModel state) {
		this.state = state;
	}

	public DistrictMasterModel getDistrict() {
		return district;
	}

	public void setDistrict(DistrictMasterModel district) {
		this.district = district;
	}

	public CityMasterModel getCity() {
		return city;
	}

	public void setCity(CityMasterModel city) {
		this.city = city;
	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
}
