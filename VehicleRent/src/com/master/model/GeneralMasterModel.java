package com.master.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.user.model.UserAccessModel;
import com.user.model.UserModel;

@JsonIgnoreProperties(value={"mappingMasterModel","userAccessModel","userModel","masterValuesModel"})
@Entity
@Table(name="generalMaster",uniqueConstraints= {@UniqueConstraint(columnNames={"name","masterId"})})
public class GeneralMasterModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public GeneralMasterModel(){
		
	}
	
	public GeneralMasterModel(String sCode,String name,String remark,Long masterId){
		this.name=name;
		this.remark=remark;
		this.masterId=masterId;
	}
	
	@Id
	@GeneratedValue
	@Column(name="id",nullable=false)
	private Long id;
	
	@Column(name="name",unique=false,updatable=true,nullable=false,length=100)
	private String name=null;
	
	@Column(name="itemCode",unique=false,updatable=true,nullable=true,length=10)
	private String itemCode=null;

	@Column(name="remark",unique=false,updatable=true,length=250)
	private String remark=null;

	@ManyToOne(optional=true)
	@JoinColumn(name="masterId",referencedColumnName="id",nullable=true,updatable=true)
	private MasterModel masterModel=null;
	
	@Column(name="sortId",unique=false,nullable=true,updatable=true)
	private Long sortId;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "masterValuesModel",fetch=FetchType.LAZY)
	private Set<MappingMasterModel> masterValuesModel=new HashSet<MappingMasterModel>(0);
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subMasterValuesModel",fetch=FetchType.LAZY)
	private Set<MappingMasterModel> mappingMasterModel=new HashSet<MappingMasterModel>(0);

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "menuId",fetch=FetchType.LAZY)
	private Set<UserAccessModel> userAccessModel=new HashSet<UserAccessModel>(0);
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "userRole",fetch=FetchType.LAZY)
	private Set<UserModel> userModel=new HashSet<UserModel>(0);

	@Transient
    private Long masterId = null;

	@Transient
    private Double doubleValue = null;

	@Transient
    private Long extraId = null;
	
	@Transient
    private String extraName = null;

	@Transient
	private Date efDate  = null;

	@Transient
	private TariffSchemeParaModel tariffSchemeParaModel=null;

	@Transient
    private Long zoneId = null;
	
	@Transient
    private String zoneName = null;

	public MasterModel getMasterModel() {
		return masterModel;
	}

	public void setMasterModel(MasterModel masterModel) {
		this.masterModel = masterModel;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getMasterId() {
		return masterId;
	}

	public void setMasterId(Long masterId) {
		this.masterId = masterId;
	}

	public Set<MappingMasterModel> getMappingMasterModel() {
		return mappingMasterModel;
	}

	public void setMappingMasterModel(Set<MappingMasterModel> mappingMasterModel) {
		this.mappingMasterModel = mappingMasterModel;
	}

	public Set<UserAccessModel> getUserAccessModel() {
		return userAccessModel;
	}

	public void setUserAccessModel(Set<UserAccessModel> userAccessModel) {
		this.userAccessModel = userAccessModel;
	}

	public Set<UserModel> getUserModel() {
		return userModel;
	}

	public void setUserModel(Set<UserModel> userModel) {
		this.userModel = userModel;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public Set<MappingMasterModel> getMasterValuesModel() {
		return masterValuesModel;
	}

	public void setMasterValuesModel(Set<MappingMasterModel> masterValuesModel) {
		this.masterValuesModel = masterValuesModel;
	}

	public Long getExtraId() {
		return extraId;
	}

	public void setExtraId(Long extraId) {
		this.extraId = extraId;
	}

	public Long getSortId() {
		return sortId;
	}

	public void setSortId(Long sortId) {
		this.sortId = sortId;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public TariffSchemeParaModel getTariffSchemeParaModel() {
		return tariffSchemeParaModel;
	}

	public void setTariffSchemeParaModel(TariffSchemeParaModel tariffSchemeParaModel) {
		this.tariffSchemeParaModel = tariffSchemeParaModel;
	}

	public Date getEfDate() {
		return efDate;
	}

	public void setEfDate(Date efDate) {
		this.efDate = efDate;
	}

	public String getExtraName() {
		return extraName;
	}

	public void setExtraName(String extraName) {
		this.extraName = extraName;
	}
	public Long getZoneId() {
		return zoneId;
	}

	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}


}
