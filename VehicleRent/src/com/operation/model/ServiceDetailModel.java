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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Entity
@Table(name="serviceDetails")
@JsonIgnoreProperties(value="maintenanceDetailModel")
public class ServiceDetailModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@Column(name="serviceDate",updatable=true,nullable=true,length=25,unique=false)
	private Date serviceDate=null;

	@Column(name="garadge",updatable=true,nullable=true,length=50,unique=false)
	private String  garadge=null;

	@Column(name="description",updatable=true,nullable=true,length=500,unique=false)
	private String description=null;

	@ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinColumn(name="maintenanceDetailId",referencedColumnName="id",nullable=true,updatable=true)
	private MaintenanceDetailModel maintenanceDetailModel=null; 
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getServiceDate() {
		return serviceDate;
	}

	public void setServiceDate(Date serviceDate) {
		this.serviceDate = serviceDate;
	}

	public String getGaradge() {
		return garadge;
	}

	public void setGaradge(String garadge) {
		this.garadge = garadge;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MaintenanceDetailModel getMaintenanceDetailModel() {
		return maintenanceDetailModel;
	}

	public void setMaintenanceDetailModel(MaintenanceDetailModel maintenanceDetailModel) {
		this.maintenanceDetailModel = maintenanceDetailModel;
	}


	
	
}
