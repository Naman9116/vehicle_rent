package com.ets.model;


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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.corporate.model.CorporateModel;
import com.master.model.CityMasterModel;
import com.master.model.GeneralMasterModel;
import com.user.model.UserModel;

@Entity
@Table(name="locationMaster")
@JsonIgnoreProperties(value={"carRateModel"})
public class LocationMasterModel  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@ManyToOne(targetEntity=CorporateModel.class)
	@JoinColumn(name="corporateId",referencedColumnName="id",nullable=false,updatable=true)
	private CorporateModel corporateId = null;
	
	@ManyToOne()
	@JoinColumn(name="branch",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel branch=null;
	
	@ManyToOne()
	@JoinColumn(name="outlet",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel outlet=null;
	
	@ManyToOne()
	@JoinColumn(name="zone",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel zone=null;
	
	@ManyToOne()
	@JoinColumn(name="location",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel location=null;
	
	@Column(name="kms",updatable=true,nullable=true,unique=false)
	private Long kms=null;
	
	@Column(name="pinCode",updatable=true,nullable=true,unique=false)
	private Long pinCode=null;
	
	@ManyToOne()
	@JoinColumn(name="city",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private CityMasterModel city=null;
	
	@Column(name="insDate",nullable=false,updatable=true,length=25)
	private Date insDate = new Date();
	
	@ManyToOne()
	@JoinColumn(name = "userId", nullable = false, updatable = true)
	private UserModel userId = null;
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy="locationMasterId")
	@NotFound(action=NotFoundAction.IGNORE)
	private List<CarRateModel> carRateModel=new ArrayList<CarRateModel>(0);
	
	public List<CarRateModel> getCarRateModel() {
		return carRateModel;
	}

	public void setCarRateModel(List<CarRateModel> carRateModel) {
		this.carRateModel = carRateModel;
	}

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

	public GeneralMasterModel getZone() {
		return zone;
	}

	public void setZone(GeneralMasterModel zone) {
		this.zone = zone;
	}

	public GeneralMasterModel getLocation() {
		return location;
	}

	public void setLocation(GeneralMasterModel location) {
		this.location = location;
	}

	public Long getKms() {
		return kms;
	}

	public void setKms(Long kms) {
		this.kms = kms;
	}

	public Long getPinCode() {
		return pinCode;
	}

	public void setPinCode(Long pinCode) {
		this.pinCode = pinCode;
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

	public CityMasterModel getCity() {
		return city;
	}

	public void setCity(CityMasterModel city) {
		this.city = city;
	}

	
	
	


}
