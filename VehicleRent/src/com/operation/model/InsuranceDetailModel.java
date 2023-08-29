package com.operation.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Null;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.NotFound;

import com.master.model.GeneralMasterModel;

@Entity
@Table(name="insuranceDetails")
@JsonIgnoreProperties(value="carDetailModel")
public class InsuranceDetailModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@ManyToOne()
	@JoinColumn(name="carDetailId",referencedColumnName="id",nullable=false,updatable=true)
	private CarDetailModel carDetailModel=null; 

	@Column(name="insurerName",updatable=true,nullable=true,length=50,unique=false)
	private String insurerName=null;

	@Column(name="policyNumber",updatable=true,nullable=true,length=20,unique=false)
	private String policyNumber=null;
	
	@Column(name="startDate",updatable=true,nullable=true,length=25,unique=false)
	private Date startDate=null;

	@Column(name="insuEndDate",updatable=true,nullable=true,length=25,unique=false)
	private Date  insuEndDate=null;

	@Column(name="premium",updatable=true,nullable=true,length=15,unique=false)
	private Double premium=null;

	@Column(name="coverageDescription",updatable=true,nullable=true,length=500,unique=false)
	private String coverageDescription=null;

	@Column(name="renewalDate",updatable=true,nullable=true,length=25,unique=false)
	private Date  renewalDate=null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Double getPremium() {
		return premium;
	}

	public void setPremium(Double premium) {
		this.premium = premium;
	}

	public String getCoverageDescription() {
		return coverageDescription;
	}

	public void setCoverageDescription(String coverageDescription) {
		this.coverageDescription = coverageDescription;
	}

	public Date getRenewalDate() {
		return renewalDate;
	}

	public void setRenewalDate(Date renewalDate) {
		this.renewalDate = renewalDate;
	}

	public CarDetailModel getCarDetailModel() {
		return carDetailModel;
	}

	public void setCarDetailModel(CarDetailModel carDetailModel) {
		this.carDetailModel = carDetailModel;
	}

	public String getInsurerName() {
		return insurerName;
	}

	public void setInsurerName(String insurerName) {
		this.insurerName = insurerName;
	}

	public Date getInsuEndDate() {
		return insuEndDate;
	}

	public void setInsuEndDate(Date insuEndDate) {
		this.insuEndDate = insuEndDate;
	}
}
