package com.master.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="distMaster")
public class DistrictMasterModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public DistrictMasterModel(){
		
	}
	
	@Id
	@GeneratedValue
	@Column(name="id",nullable=false)
	private Long id;
	
	@Column(name="name",unique=false,updatable=true,nullable=false,length=100)
	private String name=null;
	
	@Column(name="stateId",nullable=true,updatable=true)
	private Long state=null;

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

	public Long getState() {
		return state;
	}

	public void setState(Long state) {
		this.state = state;
	}

		
	
}
