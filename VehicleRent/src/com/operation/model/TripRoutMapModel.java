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

import org.joda.time.DateTime;

import com.master.model.ChauffeurModel;
import com.toursandtravels.model.TDutySlipModel;


@Entity
@Table(name="tripRoutMap")
public class TripRoutMapModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@ManyToOne()
	@JoinColumn(name="chauffeurId",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private ChauffeurModel chauffeurId=null;
	
	@ManyToOne()
	@JoinColumn(name="dutySlipId",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private DutySlipModel dutySlipId=null;

	@ManyToOne()
	@JoinColumn(name="tDutySlipId",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private TDutySlipModel tDutySlipId=null;

	@Column(name="insDate",updatable=true,nullable=true,length=25,unique=false)
	private Date insDate=null;
	
	@Column(name="insTime",updatable=true,nullable=true,length=8,unique=false)
	private String insTime=null;

	@Column(name="latitude",updatable=true,nullable=true,length=50,unique=false)
	private String latitude=null;
	
	@Column(name="longitude",updatable=true,nullable=true,length=50,unique=false)
	private String longitude=null;

	public ChauffeurModel getChauffeurId() {
		return chauffeurId;
	}

	public void setChauffeurId(ChauffeurModel chauffeurId) {
		this.chauffeurId = chauffeurId;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DutySlipModel getDutySlipId() {
		return dutySlipId;
	}

	public void setDutySlipId(DutySlipModel dutySlipId) {
		this.dutySlipId = dutySlipId;
	}

	public TDutySlipModel gettDutySlipId() {
		return tDutySlipId;
	}

	public void settDutySlipId(TDutySlipModel tDutySlipId) {
		this.tDutySlipId = tDutySlipId;
	}

	public Date getInsDate() {
		return insDate;
	}

	public void setInsDate(Date insDate) {
		this.insDate = insDate;
	}

	public String getInsTime() {
		return insTime;
	}

	public void setInsTime(String insTime) {
		this.insTime = insTime;
	}
	
	
}
