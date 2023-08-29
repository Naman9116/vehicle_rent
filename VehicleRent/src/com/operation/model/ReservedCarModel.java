package com.operation.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.user.model.UserModel;

@Entity
@Table(name="reservedCar")
public class ReservedCarModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@ManyToOne(targetEntity=UserModel.class)
	@JoinColumn(name="reserveBy",referencedColumnName="id",nullable=true,updatable=true)
	private UserModel reserveBy = null;
	
	@ManyToOne(targetEntity=UserModel.class)
	@JoinColumn(name="unReserveBy",referencedColumnName="id",nullable=true,updatable=true)
	private UserModel unReserveBy = null;
	
	@Column(name="insertDateTime",updatable=true,nullable=true,length=30,unique=false)
	private Date insertDateTime = null;
	
	@Column(name="reserveDateTime",updatable=true,nullable=true,length=30,unique=false)
	private Date reserveDateTime = null;
	
	@ManyToOne(targetEntity=CarDetailModel.class)
	@JoinColumn(name="reservedCar",referencedColumnName="id",nullable=true,updatable=true)
	private CarDetailModel carDetailModel = null;
	
	/*@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="dutySlipId",referencedColumnName="id",updatable=true,nullable=true,unique=false)
	private DutySlipModel dutySlipModel =null;*/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserModel getReserveBy() {
		return reserveBy;
	}

	public void setReserveBy(UserModel reserveBy) {
		this.reserveBy = reserveBy;
	}

	public UserModel getUnReserveBy() {
		return unReserveBy;
	}

	public void setUnReserveBy(UserModel unReserveBy) {
		this.unReserveBy = unReserveBy;
	}

	public Date getInsertDateTime() {
		return insertDateTime;
	}

	public void setInsertDateTime(Date insertDateTime) {
		this.insertDateTime = insertDateTime;
	}

	public Date getReserveDateTime() {
		return reserveDateTime;
	}

	public void setReserveDateTime(Date reserveDateTime) {
		this.reserveDateTime = reserveDateTime;
	}

	public CarDetailModel getCarDetailModel() {
		return carDetailModel;
	}

	public void setCarDetailModel(CarDetailModel carDetailModel) {
		this.carDetailModel = carDetailModel;
	}
	
}
