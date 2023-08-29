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

@Entity
@Table(name="carRate")
public class CarRateModel implements Serializable {
private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@ManyToOne(targetEntity=LocationMasterModel.class)
	@JoinColumn(name="locationMasterId",referencedColumnName="id",nullable=false,updatable=true,unique=false)
	private LocationMasterModel locationMasterId = null;
	
	
	@Column(name="carCategoryId",updatable=true,nullable=true,length=50,unique=false)
	private Long carCategoryId=null;
	
	@Column(name="oneWayRate",updatable=true,nullable=true,length=50,unique=false)
	private Double oneWayRate=null;
	
	@Column(name="twoWayRate",updatable=true,nullable=true,length=50,unique=false)
	private Double twoWayRate=null;
	
	@Column(name="oneWayRateVendor",updatable=true,nullable=true,length=50,unique=false)
	private Double oneWayRateVendor=null;
	
	@Column(name="twoWayRateVendor",updatable=true,nullable=true,length=50,unique=false)
	private Double twoWayRateVendor=null;
	
	@Column(name="effectiveDate",updatable=true,nullable=true,length=25,unique=false)
	private Date effectiveDate=null;

	@Column(name="updateDate",updatable=true,nullable=true,length=25,unique=false)
	private Date updateDate= new Date();

	@Column(name="status",updatable=true,nullable=true,length=25,unique=false)
	private String status="A";

	
	public Double getOneWayRateVendor() {
		return oneWayRateVendor;
	}

	public void setOneWayRateVendor(Double oneWayRateVendor) {
		this.oneWayRateVendor = oneWayRateVendor;
	}

	public Double getTwoWayRateVendor() {
		return twoWayRateVendor;
	}

	public void setTwoWayRateVendor(Double twoWayRateVendor) {
		this.twoWayRateVendor = twoWayRateVendor;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocationMasterModel getLocationMasterId() {
		return locationMasterId;
	}

	public void setLocationMasterId(LocationMasterModel locationMasterId) {
		this.locationMasterId = locationMasterId;
	}

	public Long getCarCategoryId() {
		return carCategoryId;
	}

	public void setCarCategoryId(Long carCategoryId) {
		this.carCategoryId = carCategoryId;
	}

	public Double getOneWayRate() {
		return oneWayRate;
	}

	public void setOneWayRate(Double oneWayRate) {
		this.oneWayRate = oneWayRate;
	}

	public Double getTwoWayRate() {
		return twoWayRate;
	}

	public void setTwoWayRate(Double twoWayRate) {
		this.twoWayRate = twoWayRate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
