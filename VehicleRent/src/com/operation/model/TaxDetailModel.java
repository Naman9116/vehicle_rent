package com.operation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name="taxDetails")
@JsonIgnoreProperties(value={"carDetailModel"})
public class TaxDetailModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@Column(name="rtoTax",updatable=true,nullable=true,length=20,unique=false)
	private String rtoTax=null;
	
	@Column(name="startDate",updatable=true,nullable=true,length=25,unique=false)
	private Date startDate=null;

	@Column(name="rtoEndDate",updatable=true,nullable=true,length=25,unique=false)
	private Date  rtoEndDate=null;

	@ManyToOne()
	@JoinColumn(name="carDetailId",referencedColumnName="id",nullable=true,updatable=true)
	private CarDetailModel carDetailModel=null; 
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL,mappedBy="taxDetailModel")
	private List<StatePermitDetailModel> statePermitDetailModel=new ArrayList<StatePermitDetailModel>(0);  

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRtoTax() {
		return rtoTax;
	}

	public void setRtoTax(String rtoTax) {
		this.rtoTax = rtoTax;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getRtoEndDate() {
		return rtoEndDate;
	}

	public void setRtoEndDate(Date rtoEndDate) {
		this.rtoEndDate = rtoEndDate;
	}

	public CarDetailModel getCarDetailModel() {
		return carDetailModel;
	}

	public void setCarDetailModel(CarDetailModel carDetailModel) {
		this.carDetailModel = carDetailModel;
	}

	public List<StatePermitDetailModel> getStatePermitDetailModel() {
		return statePermitDetailModel;
	}

	public void setStatePermitDetailModel(List<StatePermitDetailModel> statePermitDetailModel) {
		this.statePermitDetailModel = statePermitDetailModel;
	}

	
	
}
