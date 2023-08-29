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
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Entity
@Table(name="statePermitDetails")
@JsonIgnoreProperties(value="taxDetailModel")
public class StatePermitDetailModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;

	@Column(name="permitName",updatable=true,nullable=true,length=40,unique=false)
	private String permitName=null;
	
	@Column(name="permitNo",updatable=true,nullable=true,length=20,unique=false)
	private String permitNo=null;

	@Column(name="startDate",updatable=true,nullable=true,length=25,unique=false)
	private Date startDate=null;

	@Column(name="endDate",updatable=true,nullable=true,length=25,unique=false)
	private Date  endDate=null;

	@ManyToOne()
	@JoinColumn(name="taxDetailId",nullable=true,updatable=true)
	private TaxDetailModel taxDetailModel=null; 
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPermitName() {
		return permitName;
	}

	public void setPermitName(String permitName) {
		this.permitName = permitName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public TaxDetailModel getTaxDetailModel() {
		return taxDetailModel;
	}

	public void setTaxDetailModel(TaxDetailModel taxDetailModel) {
		this.taxDetailModel = taxDetailModel;
	}

	public String getPermitNo() {
		return permitNo;
	}

	public void setPermitNo(String permitNo) {
		this.permitNo = permitNo;
	}



	
	
}
