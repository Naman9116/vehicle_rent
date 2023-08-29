package com.toursandtravels.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="customerDetails")
public  class CustomerModel  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@Column(name="name",updatable=true,nullable=false,unique=false)
	private String name=null;
	
	@Column(name="createdDate",updatable=true,nullable=false,length=10,unique=false)
	private Date createdDate=null;
	
	@Column(name="mobileNo",updatable=true,nullable=false,unique=true,length=20)
	private String mobileNo=null;
	
	@Column(name="email",updatable=true,nullable=true)
	private String email=null;
	
	@Column(name="company",updatable=true,nullable=true)
	private Long company=null;
	

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}

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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
