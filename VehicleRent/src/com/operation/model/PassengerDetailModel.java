package com.operation.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="passengerDetails")
public class PassengerDetailModel implements Serializable{

private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@ManyToOne(targetEntity=BookingDetailModel.class)
	@JoinColumn(name="bookingDetailId",referencedColumnName="id",nullable=false,updatable=true,unique=false)
	private BookingDetailModel bookingDetailId = null;
	
	@Column(name="name",updatable=true,nullable=false,length=50,unique=false)
	private String name=null;
	
	@Column(name="mobile",updatable=true,nullable=false,length=20,unique=false)
	private String mobile=null;
	
	@Column(name="age",updatable=true,nullable=false,length=10,unique=false)
	private String age=null;
	
	@Column(name="sex",updatable=true,nullable=false,length=10,unique=false)
	private String sex=null;
	
	@Column(name="idDetails",updatable=true,nullable=true,length=50,unique=false)
	private String idDetails=null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BookingDetailModel getBookingDetailId() {
		return bookingDetailId;
	}

	public void setBookingDetailId(BookingDetailModel bookingDetailId) {
		this.bookingDetailId = bookingDetailId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobileNo) {
		this.mobile = mobileNo;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getIdDetails() {
		return idDetails;
	}

	public void setIdDetails(String idDetails) {
		this.idDetails = idDetails;
	}
	
	
	
}
