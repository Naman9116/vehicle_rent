package com.master.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="corpTaxDet",uniqueConstraints= {@UniqueConstraint(columnNames={"corporateId","taxationId","insDate"})})
public class CorporateTaxDetModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="Id")
	private Long id =null;
	
	/*Entry Date with time*/
	@Column(name="insDate",nullable=false,updatable=true,length=25)
	private Date insDate = new Date();
	
	@Column(name = "corporateId",  nullable = false, updatable = true)
	private Long corporateModelId = null;
	
	@Column(name = "taxationId",  nullable = false, updatable = true)
	private Long taxationModelId = null;
	
	@Column(name="value",nullable=true,updatable=true,length=10)
	private Double taxVal = null;
	
	/* Entry UserId for update or insert */
	@Column(name = "userId", nullable = false, updatable = true)
	private Long userId = null;
	
	@Transient 
	private String name = null;

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

	public Long getCorporateModelId() {
		return corporateModelId;
	}

	public void setCorporateModelId(Long corporateModelId) {
		this.corporateModelId = corporateModelId;
	}

	public Long getTaxationModelId() {
		return taxationModelId;
	}

	public void setTaxationModelId(Long taxationModelId) {
		this.taxationModelId = taxationModelId;
	}

	public Double getTaxVal() {
		return taxVal;
	}

	public void setTaxVal(Double taxVal) {
		this.taxVal = taxVal;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
