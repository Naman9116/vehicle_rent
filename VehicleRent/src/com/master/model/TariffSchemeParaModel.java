package com.master.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="tariffSchemePara",uniqueConstraints= {@UniqueConstraint(columnNames={"parentId"})})
public class TariffSchemeParaModel implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;

	@Column(name="parentId",nullable=false,updatable=true,length=25)
	private Long parentId ;

	/*Effect From Date %*/
	@Column(name="crDate",nullable=false,updatable=true,length=25)
	private Date crDate = new Date();

	@Column(name="minHR",nullable=true,updatable=true,length=10)
	private Double minHR = null;

	@Column(name="minKM",nullable=true,updatable=true,length=10)
	private Double minKM = null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Double getMinHR() {
		return minHR;
	}

	public void setMinHR(Double minHR) {
		this.minHR = minHR;
	}

	public Double getMinKM() {
		return minKM;
	}

	public void setMinKM(Double minKM) {
		this.minKM = minKM;
	}

	public Date getCrDate() {
		return crDate;
	}

	public void setCrDate(Date crDate) {
		this.crDate = crDate;
	}
}
