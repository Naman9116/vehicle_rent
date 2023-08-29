package com.billing.model;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.operation.model.BookingDetailModel;

@Entity
@Table(name="coverLetter")
public class CoverLetterModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@Column(name="invoiceIds",updatable=true,nullable=true,length=500,unique=false)
	private String invoiceIds=null;
	
	@Column(name="letterNo",updatable=true,nullable=true,length=50,unique=false)
	private String letterNo= "";

	@Column(name="letterDate",updatable=true,nullable=true,length=25,unique=false)
	private Date letterDate= new Date();
	
	@Column(name="invFromDate",updatable=true,nullable=true,length=25,unique=false)
	private Date invFromDate= new Date();

	@Column(name="invToDate",updatable=true,nullable=true,length=25,unique=false)
	private Date invToDate= new Date();
	
	@Column(name="totalLetterAmount",updatable=true,nullable=true,length=20,unique=false)
	private Double totalLetterAmount= 0.00;

	@Column(name="mailingMode",updatable=true,nullable=true,length=50,unique=false)
	private String mailingMode=null;

	@Column(name="remarks",updatable=true,nullable=true,length=50,unique=false)
	private String remarks=null;

	@Column(name="branch",updatable=true,nullable=true,length=50,unique=false)
	private String branch=null;
	
	@Column(name="compId",updatable=true,nullable=true,length=20,unique=false)
	private Long compId=null;

	@Column(name="branchId",updatable=true,nullable=true,length=20,unique=false)
	private Long branchId=null;

	@Column(name="hub",updatable=true,nullable=true,length=50,unique=false)
	private String hub=null;
	
	@Column(name="hubId",updatable=true,nullable=true,length=20,unique=false)
	private Long hubId=null;

	@Column(name="corporate",updatable=true,nullable=true,length=70,unique=false)
	private String corporate=null;

	@Column(name="corporateId",updatable=true,nullable=true,length=20,unique=false)
	private Long corporateId=null;

	@Column(name="creationDate",updatable=true,nullable=true,length=25,unique=false)
	private Date creationDate= new Date();

	@Column(name="createdBy",updatable=true,nullable=true,length=15,unique=false)
	private long createdBy = 0l;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInvoiceIds() {
		return invoiceIds;
	}

	public void setInvoiceIds(String invoiceIds) {
		this.invoiceIds = invoiceIds;
	}

	public Date getLetterDate() {
		return letterDate;
	}

	public void setLetterDate(Date letterDate) {
		this.letterDate = letterDate;
	}

	public String getMailingMode() {
		return mailingMode;
	}

	public void setMailingMode(String mailingMode) {
		this.mailingMode = mailingMode;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getHub() {
		return hub;
	}

	public void setHub(String hub) {
		this.hub = hub;
	}

	public String getCorporate() {
		return corporate;
	}

	public void setCorporate(String corporate) {
		this.corporate = corporate;
	}

	public Double getTotalLetterAmount() {
		return totalLetterAmount;
	}

	public void setTotalLetterAmount(Double totalLetterAmount) {
		this.totalLetterAmount = totalLetterAmount;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public Long getHubId() {
		return hubId;
	}

	public void setHubId(Long hubId) {
		this.hubId = hubId;
	}

	public Long getCorporateId() {
		return corporateId;
	}

	public void setCorporateId(Long corporateId) {
		this.corporateId = corporateId;
	}

	public String getLetterNo() {
		return letterNo;
	}

	public void setLetterNo(String letterNo) {
		this.letterNo = letterNo;
	}

	public Date getInvFromDate() {
		return invFromDate;
	}

	public void setInvFromDate(Date invFromDate) {
		this.invFromDate = invFromDate;
	}

	public Date getInvToDate() {
		return invToDate;
	}

	public void setInvToDate(Date invToDate) {
		this.invToDate = invToDate;
	}

	public Long getCompId() {
		return compId;
	}

	public void setCompId(Long compId) {
		this.compId = compId;
	}
}
