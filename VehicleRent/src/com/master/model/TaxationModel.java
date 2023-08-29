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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="taxation",uniqueConstraints= {@UniqueConstraint(columnNames={"parentId","efDate"})})
public class TaxationModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@ManyToOne()
	@JoinColumn(name="parentId",nullable=false,updatable=true)
	private GeneralMasterModel parentId = new GeneralMasterModel();

	/*Entry Date with time*/
	@Column(name="insDate",nullable=false,updatable=true,length=25)
	private Date insDate = new Date();

	/*Effect From Date %*/
	@Column(name="efDate",nullable=false,updatable=true,length=25)
	private Date efDate = null;

	@Column(name="calType",nullable=true,updatable=true,length=1)
	private String calType = null;

	@Column(name="taxPer",nullable=true,updatable=true,length=5)
	private Double taxPer = null;

	@Column(name="taxVal",nullable=true,updatable=true,length=10)
	private Double taxVal = null;

	@Column(name="endDate",nullable=true,updatable=true,length=25)
	private Date endDate = null;

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

	public Date getEfDate() {
		return efDate;
	}

	public void setEfDate(Date efDate) {
		this.efDate = efDate;
	}

	public String getCalType() {
		return calType;
	}

	public void setCalType(String calType) {
		this.calType = calType;
	}

	public Double getTaxPer() {
		return taxPer;
	}

	public void setTaxPer(Double taxPer) {
		this.taxPer = taxPer;
	}

	public Double getTaxVal() {
		return taxVal;
	}

	public void setTaxVal(Double taxVal) {
		this.taxVal = taxVal;
	}

	public GeneralMasterModel getParentId() {
		return parentId;
	}

	public void setParentId(GeneralMasterModel parentId) {
		this.parentId = parentId;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
