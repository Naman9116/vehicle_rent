package com.operation.model;

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
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name="maintenanceDetails")
@JsonIgnoreProperties(value={"serviceDetailModel","carDetailModel"})
public class MaintenanceDetailModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@Column(name="fitnessCertificateNo",updatable=true,nullable=true,length=40,unique=false)
	private String fitnessCertificateNo=null;
	
	@Column(name="fitnessUpTo",updatable=true,nullable=true,length=25,unique=false)
	private Date fitnessUpTo=null;

	@Column(name="pucNo",updatable=true,nullable=true,length=30,unique=false)
	private String  pucNo=null;

	@Column(name="pucUpTo",updatable=true,nullable=true,length=25,unique=false)
	private Date  pucUpTo=null;

	@OneToOne(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinColumn(name="carDetailId",referencedColumnName="id",nullable=true,updatable=true)
	private CarDetailModel carDetailModel=null; 
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER,mappedBy="maintenanceDetailModel")
	private List<ServiceDetailModel> serviceDetailModel=new ArrayList<ServiceDetailModel>(0);  

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFitnessCertificateNo() {
		return fitnessCertificateNo;
	}

	public void setFitnessCertificateNo(String fitnessCertificateNo) {
		this.fitnessCertificateNo = fitnessCertificateNo;
	}

	public Date getFitnessUpTo() {
		return fitnessUpTo;
	}

	public void setFitnessUpTo(Date fitnessUpTo) {
		this.fitnessUpTo = fitnessUpTo;
	}

	public String getPucNo() {
		return pucNo;
	}

	public void setPucNo(String pucNo) {
		this.pucNo = pucNo;
	}

	public Date getPucUpTo() {
		return pucUpTo;
	}

	public void setPucUpTo(Date pucUpTo) {
		this.pucUpTo = pucUpTo;
	}

	public CarDetailModel getCarDetailModel() {
		return carDetailModel;
	}

	public void setCarDetailModel(CarDetailModel carDetailModel) {
		this.carDetailModel = carDetailModel;
	}

	public List<ServiceDetailModel> getServiceDetailModel() {
		return serviceDetailModel;
	}

	public void setServiceDetailModel(List<ServiceDetailModel> serviceDetailModel) {
		this.serviceDetailModel = serviceDetailModel;
	}

	
	
}
