package com.master.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table( name = "masters", uniqueConstraints= {@UniqueConstraint(columnNames={"code","name"})})
public class MasterModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public MasterModel(){
		
	}
	
	public MasterModel(String name,String code,String remark,Long parentid){
		this.name=name;
		this.code=code;
		this.remark=remark;
		this.parentid=parentid;
	}
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;
	
	@Column(name="code",updatable=true,nullable=false,length=20,unique=true)
	private String code = null;

	@Column(name="name",updatable=true,nullable=false,length=50)
	private String name = null;

	@Column(name="remark",updatable=true,nullable=true,length=100)
	private String remark = null;
	
	@Column(name="parentid")
	private Long parentid = null;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getParentid() {
		return parentid;
	}

	public void setParentid(Long parentid) {
		this.parentid = parentid;
	}
	
	
}