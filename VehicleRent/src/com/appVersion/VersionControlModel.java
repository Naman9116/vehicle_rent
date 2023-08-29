package com.appVersion;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="appVersion")
public class VersionControlModel {

	public VersionControlModel(){
		
	}
	public VersionControlModel(Double version){
		this.version=version;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id",nullable=false)
	private Integer id;
	
	@Column(name="version",nullable=false,updatable=false)
	private Double version=0.00;
	
	@Column(name="versionDate",nullable=true,updatable=false)
	private Date versionDate=new Date();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getVersion() {
		return version;
	}

	public void setVersion(Double version) {
		this.version = version;
	}

	public Date getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}
	
	
	
}
