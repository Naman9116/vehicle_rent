package com.operation.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="AdditionalDriverDetails")
public class AdditionalDriverDetailModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@Column(name="name",updatable=true,nullable=true,length=50,unique=false)
	private String  name=null;

	@Column(name="mobileNo",updatable=true,nullable=true,length=15,unique=false)
	private String  mobileNo=null;

	@OneToOne(cascade = CascadeType.ALL,fetch=FetchType.EAGER,optional=true)
	@JoinColumn(name="dutySlipId",referencedColumnName="id",nullable=true,updatable=true)
	private DutySlipModel dutySlipModel=null; 

	
}
