package com.master.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cityMaster")
public class CityMasterModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public CityMasterModel(){
		
	}
	
	@Id
	@GeneratedValue
	@Column(name="id",nullable=false)
	private Long id;
	
	@Column(name="name",unique=false,updatable=true,nullable=false,length=100)
	private String name=null;

	@Column(name="pincode",unique=false,updatable=true,nullable=false,length=100)
	private Long pincode=null;

	@Column(name="distId",nullable=true,updatable=true)
	private Long district=null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getPincode() {
		return pincode;
	}

	public void setPincode(Long pincode) {
		this.pincode = pincode;
	}

	public Long getDistrict() {
		return district;
	}

	public void setDistrict(Long district) {
		this.district = district;
	}

	

		
	
}
