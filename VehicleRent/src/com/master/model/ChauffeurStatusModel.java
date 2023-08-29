package com.master.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.common.model.AddressDetailModel;
import com.common.model.ContactDetailModel;
import com.operation.model.DutySlipModel;
import com.user.model.UserModel;

@Entity
@Table(name="ChauffeurStatus")
public class ChauffeurStatusModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	/*Entry Date with time*/
	
	@Column(name="insDate",nullable=false,updatable=true,length=25)
	private Date insDate = new Date();
	
	@ManyToOne()
	@JoinColumn(name = "chauffeurId", nullable = true, updatable = true)
	private ChauffeurModel chauffeurId = null;
	
	@ManyToOne()
	@JoinColumn(name = "dutySlipId", nullable = true, updatable = true)
	private DutySlipModel dutySlipId = null;
	
	/*Status*/
	@Column(name="status",nullable=false,updatable=true,length=1)
	private String status = null;
	
	/*Remarks*/
	@Column(name="remarks",nullable=true,updatable=true,length=100)
	private String remarks = null;	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getInsDate() {
		return insDate;
	}
	public void setInsDate(Date insDate) {
		this.insDate = insDate;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ChauffeurModel getChauffeurId() {
		return chauffeurId;
	}
	public void setChauffeurId(ChauffeurModel chauffeurId) {
		this.chauffeurId = chauffeurId;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public DutySlipModel getDutySlipId() {
		return dutySlipId;
	}
	public void setDutySlipId(DutySlipModel dutySlipId) {
		this.dutySlipId = dutySlipId;
	}
	
		
}
