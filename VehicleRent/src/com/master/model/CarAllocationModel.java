package com.master.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.operation.model.CarDetailModel;

@Entity
@Table(name="carAllocation")
public class CarAllocationModel implements Serializable{
		
	private static final long serialVersionUID = 1L;
	
	@Id 
	@GeneratedValue
	@Column(name="Id")
	private Long id =null;
	
	/*Entry Date with time*/
	@Column(name="insDate",nullable=false,updatable=true,length=25)
	private Date insDate = new Date();
	
	/* Entry UserId for update or insert */
	@ManyToOne()
	@JoinColumn(name = "carRegNo",referencedColumnName="registrationNo", nullable = true, updatable = true)
	private CarDetailModel carDetailModelId = null;
	
	@ManyToOne()
	@JoinColumn(name = "vendorId", nullable = true, updatable = true)
	private VendorModel vendorId = null;
	
	/*car Owner Type*/
	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="carOwnerType",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel carOwnerType=null;
	
	@Column(name="dateOfAllotment", nullable=true,updatable = true,length=30)
	private Date dateOfAllotment=null ;
	
	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="paymentTraffic",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel  paymentTraffic = null;
	
	@Column(name="companyShare", nullable=true,updatable=true,length=10)
	private Long companyShare;
	
	@Column(name="vendorShare", nullable=true,updatable=true,length=10)
	private Long vendorShare;
	
	@Column(name="dutyAllow",nullable=true,updatable=true,length=1)
	private String dutyAllow;
	
	@Column(name="carStatus",nullable=true,updatable=true,length=1)
	private String carStatus;
	
	@Column(name="dateOfDeallocation", nullable=true,updatable = true,length=30)
	private Date dateOfDeallocation =null;
	
	@Column(name="reason",nullable=true,updatable=true,length=200)
	private String reason;

	@ManyToOne()
	@JoinColumn(name = "chauffeurId", nullable = true, updatable = true)
	private ChauffeurModel chauffeurId = null;
	
	@Transient
	private Long noOfPendingDutySlip = null;
	
	@Transient
	private Long modelId = null;

	
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

	public CarDetailModel getCarDetailModelId() {
		return carDetailModelId;
	}

	public void setCarDetailModelId(CarDetailModel carDetailModelId) {
		this.carDetailModelId = carDetailModelId;
	}

	public GeneralMasterModel getCarOwnerType() {
		return carOwnerType;
	}

	public void setCarOwnerType(GeneralMasterModel carOwnerType) {
		this.carOwnerType = carOwnerType;
	}

	public Date getDateOfAllotment() {
		return dateOfAllotment;
	}

	public void setDateOfAllotment(Date dateOfAllotment) {
		this.dateOfAllotment = dateOfAllotment;
	}

	public GeneralMasterModel getPaymentTraffic() {
		return paymentTraffic;
	}

	public void setPaymentTraffic(GeneralMasterModel paymentTraffic) {
		this.paymentTraffic = paymentTraffic;
	}

	public Long getCompanyShare() {
		return companyShare;
	}

	public void setCompanyShare(Long companyShare) {
		this.companyShare = companyShare;
	}

	public Long getVendorShare() {
		return vendorShare;
	}

	public void setVendorShare(Long vendorShare) {
		this.vendorShare = vendorShare;
	}

	public String getDutyAllow() {
		return dutyAllow;
	}

	public void setDutyAllow(String dutyAllow) {
		this.dutyAllow = dutyAllow;
	}

	public String getCarStatus() {
		return carStatus;
	}

	public void setCarStatus(String carStatus) {
		this.carStatus = carStatus;
	}

	public Date getDateOfDeallocation() {
		return dateOfDeallocation;
	}

	public void setDateOfDeallocation(Date dateOfDeallocation) {
		this.dateOfDeallocation = dateOfDeallocation;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public VendorModel getVendorId() {
		return vendorId;
	}

	public void setVendorId(VendorModel vendorId) {
		this.vendorId = vendorId;
	}

	public ChauffeurModel getChauffeurId() {
		return chauffeurId;
	}

	public void setChauffeurId(ChauffeurModel chauffeurId) {
		this.chauffeurId = chauffeurId;
	}

	public Long getNoOfPendingDutySlip() {
		return noOfPendingDutySlip;
	}

	public void setNoOfPendingDutySlip(Long noOfPendingDutySlip) {
		this.noOfPendingDutySlip = noOfPendingDutySlip;
	}

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	
}
