package com.operation.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="vehicleAllocationDetails")
public class VehicleAllocationModel implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id",nullable=false )
	private Long id;
	
	@ManyToOne(targetEntity=BookingDetailModel.class)
	@JoinColumn(name="bookingId",referencedColumnName="id",nullable=false,updatable=true)
	private BookingDetailModel bookingId= null;

	@ManyToOne(targetEntity=CarDetailModel.class)
	@JoinColumn(name="carId",referencedColumnName="id",nullable=false,updatable=true)
	private CarDetailModel vehicleId= null;
	
	@Transient
	private Date bookingDate=null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BookingDetailModel getBookingId() {
		return bookingId;
	}

	public void setBookingId(BookingDetailModel bookingId) {
		this.bookingId = bookingId;
	}

	public CarDetailModel getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(CarDetailModel vehicleId) {
		this.vehicleId = vehicleId;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	
	
}
