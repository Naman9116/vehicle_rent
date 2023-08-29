package com.master.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table( name = "mastersMap", uniqueConstraints= {@UniqueConstraint(columnNames={"parentId","childId"})})
public class MasterMapModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public MasterMapModel(){
		
	}
	
	public MasterMapModel(Long parentId, Long childId){
		this.masterValuesModel.setId(parentId);
		this.subMasterValuesModel.setId(childId);
	}
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;

	@ManyToOne()
	@JoinColumn(name="parentId",referencedColumnName="id",nullable=false,updatable=true)
	private MasterModel masterValuesModel=null;
	
	@ManyToOne()
	@JoinColumn(name="childId",referencedColumnName="id",nullable=false,updatable=true)
	private MasterModel subMasterValuesModel=null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MasterModel getMasterValuesModel() {
		return masterValuesModel;
	}

	public void setMasterValuesModel(MasterModel masterValuesModel) {
		this.masterValuesModel = masterValuesModel;
	}

	public MasterModel getSubMasterValuesModel() {
		return subMasterValuesModel;
	}

	public void setSubMasterValuesModel(MasterModel subMasterValuesModel) {
		this.subMasterValuesModel = subMasterValuesModel;
	}
}