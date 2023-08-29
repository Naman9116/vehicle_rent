package com.master.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.common.model.AddressDetailModel;
import com.corporate.model.CorporateModel;
import com.user.model.UserModel;

@JsonIgnoreProperties(value={"corporateTariffDetModel"})
@Entity
@Table(name = "corporateTariff", uniqueConstraints = { @UniqueConstraint(columnNames = { "corporateId","fuelHikeDate","branchId"}) })
public class CorporateTariffModel implements Serializable {

	private static final long serialVersionUID = 1L;
	/* Unique Id */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Long id;
	/* Corporate Id for which parameter enters */
	@ManyToOne()
	@JoinColumn(name = "corporateId", nullable = false, updatable = true)
	private CorporateModel corporateId = new CorporateModel();

	@ManyToOne()
	@JoinColumn(name = "branchId", nullable = false, updatable = true)
	private GeneralMasterModel branchId = new GeneralMasterModel();

	@Column(name = "fuelRate", nullable = true, updatable = true, length = 20)
	private Double fuelRate = null;
	/* Current Fuel Rate */
	@Column(name = "currFuelRate", nullable = true, updatable = true, length = 20)
	private Double currFuelRate = null;
	/* Fuel Hike Date */
	@Column(name = "fuelHikeDate", nullable = true, updatable = true, length = 25)
	private Date fuelHikeDate = null;

	/* Entry Date with time */
	@Column(name = "insDate", nullable = false, updatable = true, length = 25)
	private Date insDate = new Date();

	/* Entry UserId for update or insert */
	@ManyToOne()
	@JoinColumn(name = "userId", nullable = false, updatable = true)
	private UserModel userId = new UserModel();
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "corporateTariffModel",fetch=FetchType.LAZY)
	private List<CorporateTariffDetModel> corporateTariffDetModel=new ArrayList<CorporateTariffDetModel>();

	@Column(name="gstin",nullable=true,updatable=true,length=15)
	private String gstin = null;

	/*Corporate Address*/
	@Transient
	AddressDetailModel addressDetailModel = null;
	@OneToOne(cascade = CascadeType.ALL)
	private AddressDetailModel entityAddress = null;
	
	/* Getter and Setter Defined */
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

	public Double getFuelRate() {
		return fuelRate;
	}

	public void setFuelRate(Double fuelRate) {
		this.fuelRate = fuelRate;
	}

	public Double getCurrFuelRate() {
		return currFuelRate;
	}

	public void setCurrFuelRate(Double currFuelRate) {
		this.currFuelRate = currFuelRate;
	}

	public Date getFuelHikeDate() {
		return fuelHikeDate;
	}

	public void setFuelHikeDate(Date fuelHikeDate) {
		this.fuelHikeDate = fuelHikeDate;
	}

	public GeneralMasterModel getBranchId() {
		return branchId;
	}

	public void setBranchId(GeneralMasterModel branchId) {
		this.branchId = branchId;
	}

	public List<CorporateTariffDetModel> getCorporateTariffDetModel() {
		return corporateTariffDetModel;
	}

	public void setCorporateTariffDetModel(List<CorporateTariffDetModel> corporateTariffDetModel) {
		this.corporateTariffDetModel = corporateTariffDetModel;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
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


}
