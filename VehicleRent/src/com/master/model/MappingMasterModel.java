package com.master.model;

import java.io.Serializable;

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
@Table(name="mappingMaster")
public class MappingMasterModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	
	@Transient
	private MasterModel masterIdModel=null;
	@Transient
	private MasterModel subMasterIdModel=null;
	@Transient
    private Long masterId = null;
	@Transient
    private Long subMasterId = null;

	@ManyToOne
	@JoinColumn(name="parentId",referencedColumnName="id",nullable=false,updatable=true)
	private GeneralMasterModel masterValuesModel=null;
	
	@ManyToOne
	@JoinColumn(name="childId",referencedColumnName="id",nullable=false,updatable=true)
	private GeneralMasterModel subMasterValuesModel=null;

	@Transient
    private Long masterValue = null;

	@Transient
    private Long subMasterValues = null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MasterModel getMasterIdModel() {
		return masterIdModel;
	}

	public void setMasterIdModel(MasterModel masterIdModel) {
		this.masterIdModel = masterIdModel;
	}

	public MasterModel getSubMasterIdModel() {
		return subMasterIdModel;
	}

	public void setSubMasterIdModel(MasterModel subMasterIdModel) {
		this.subMasterIdModel = subMasterIdModel;
	}

	public Long getMasterId() {
		return masterId;
	}

	public void setMasterId(Long masterId) {
		this.masterId = masterId;
	}

	public Long getSubMasterId() {
		return subMasterId;
	}

	public void setSubMasterId(Long subMasterId) {
		this.subMasterId = subMasterId;
	}

	public GeneralMasterModel getMasterValuesModel() {
		return masterValuesModel;
	}

	public void setMasterValuesModel(GeneralMasterModel masterValuesModel) {
		this.masterValuesModel = masterValuesModel;
	}

	public GeneralMasterModel getSubMasterValuesModel() {
		return subMasterValuesModel;
	}

	public void setSubMasterValuesModel(GeneralMasterModel subMasterValuesModel) {
		this.subMasterValuesModel = subMasterValuesModel;
	}

	public Long getMasterValue() {
		return masterValue;
	}

	public void setMasterValue(Long masterValue) {
		this.masterValue = masterValue;
	}

	public Long getSubMasterValues() {
		return subMasterValues;
	}

	public void setSubMasterValues(Long subMasterValues) {
		this.subMasterValues = subMasterValues;
	}
	
	
		
}
