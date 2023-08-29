package com.operation.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="DeallocationHistoryDetails")
public class DeallocateHistoryModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@Column(name="bookingDetailId",nullable=true,updatable=true)
	private Long bookingDetailModelId = null; 
	
	@Column(name="status",updatable=true,nullable=true,length=1,unique=false)
	private String status = null;
	
	@Column(name="bookingNo",updatable=true,nullable=true,length=40,unique=false)
	private String bookingNo = null;
	
	@Column(name="carDetailId",nullable=false,updatable=true)
	private Long carDetailModelId = null;
	
	@Column(name="ChauffeurId",nullable=true,updatable=true)
	private Long chauffeurModelId = null;

	@Column(name="vendorId",nullable=true,updatable=true)
	private Long vendorModelId = null;
	
	@Column(name="isUpdatedChauffeur",updatable=true,nullable=true,length=1,unique=false)
	private String isUpdatedChauffeur=null;
	
	@Column(name="chauffeurName",updatable=true,nullable=true,length=60,unique=false)
	private String chauffeurName=null;
	
	@Column(name="chauffeurMobile",updatable=true,nullable=true,length=10,unique=false)
	private String chauffeurMobile=null;
	
	@Column(name="isDsManual",updatable=true,nullable=true,length=1,unique=false)
	private String isDsManual=null;
	
	@Column(name="manualSlipNo",updatable=true,nullable=true,length=30,unique=false)
	private String manualSlipNo=null;
	
	@Column(name="manualSlipRemarks",updatable=true,nullable=true,length=120,unique=false)
	private String manualSlipRemarks=null;
	
	@Column(name="allocationDateTime",updatable=true,nullable=true,length=30,unique=false)
	private Date allocationDateTime= null;
	
	@Column(name="deallocationDateTime",updatable=true,nullable=true,length=30,unique=false)
	private Date deallocationDateTime= null;
	
	@Column(name="deallocatedBy",nullable=true,updatable=true)
	private Long deallocatedBy=null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBookingDetailModelId() {
		return bookingDetailModelId;
	}

	public void setBookingDetailModelId(Long bookingDetailModelId) {
		this.bookingDetailModelId = bookingDetailModelId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBookingNo() {
		return bookingNo;
	}

	public void setBookingNo(String bookingNo) {
		this.bookingNo = bookingNo;
	}

	public Long getCarDetailModelId() {
		return carDetailModelId;
	}

	public void setCarDetailModelId(Long carDetailModelId) {
		this.carDetailModelId = carDetailModelId;
	}

	public Long getChauffeurModelId() {
		return chauffeurModelId;
	}

	public void setChauffeurModelId(Long chauffeurModelId) {
		this.chauffeurModelId = chauffeurModelId;
	}

	public Long getVendorModelId() {
		return vendorModelId;
	}

	public void setVendorModelId(Long vendorModelId) {
		this.vendorModelId = vendorModelId;
	}

	public String getIsUpdatedChauffeur() {
		return isUpdatedChauffeur;
	}

	public void setIsUpdatedChauffeur(String isUpdatedChauffeur) {
		this.isUpdatedChauffeur = isUpdatedChauffeur;
	}

	public String getChauffeurName() {
		return chauffeurName;
	}

	public void setChauffeurName(String chauffeurName) {
		this.chauffeurName = chauffeurName;
	}

	public String getChauffeurMobile() {
		return chauffeurMobile;
	}

	public void setChauffeurMobile(String chauffeurMobile) {
		this.chauffeurMobile = chauffeurMobile;
	}

	public String getIsDsManual() {
		return isDsManual;
	}

	public void setIsDsManual(String isDsManual) {
		this.isDsManual = isDsManual;
	}

	public String getManualSlipNo() {
		return manualSlipNo;
	}

	public void setManualSlipNo(String manualSlipNo) {
		this.manualSlipNo = manualSlipNo;
	}

	public String getManualSlipRemarks() {
		return manualSlipRemarks;
	}

	public void setManualSlipRemarks(String manualSlipRemarks) {
		this.manualSlipRemarks = manualSlipRemarks;
	}

	public Date getAllocationDateTime() {
		return allocationDateTime;
	}

	public void setAllocationDateTime(Date allocationDateTime) {
		this.allocationDateTime = allocationDateTime;
	}

	public Date getDeallocationDateTime() {
		return deallocationDateTime;
	}

	public void setDeallocationDateTime(Date deallocationDateTime) {
		this.deallocationDateTime = deallocationDateTime;
	}

	public Long getDeallocatedBy() {
		return deallocatedBy;
	}

	public void setDeallocatedBy(Long deallocatedBy) {
		this.deallocatedBy = deallocatedBy;
	}
	
	
}
