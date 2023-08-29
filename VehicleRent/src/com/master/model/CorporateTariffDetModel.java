package com.master.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.common.model.AddressDetailModel;
import com.corporate.model.CorporateModel;
import com.user.model.UserModel;

@Entity
@Table(name = "corporateTariffDet", uniqueConstraints = { @UniqueConstraint(columnNames = { "corporateTariffId",
		"carCatId", "tariffId", "tariffValue" }) })
public class CorporateTariffDetModel implements Serializable {

	private static final long serialVersionUID = 1L;
	/* Unique Id */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Long id;
	/* Corporate Id for which parameter enters */

	@ManyToOne()
	@JoinColumn(name = "corporateTariffId", referencedColumnName="id", nullable = false, updatable = true)
	private CorporateTariffModel corporateTariffModel = new CorporateTariffModel();
	/* Car category */
	@ManyToOne()
	@JoinColumn(name = "carCatId", nullable = false, updatable = true)
	private GeneralMasterModel carCatId = new GeneralMasterModel();
	/* Tariff Master Id */
	@ManyToOne()
	@JoinColumn(name = "tariffId", nullable = false, updatable = true)
	private GeneralMasterModel tariffId = new GeneralMasterModel();
	/* Tariff Value */
	@Column(name = "tariffValue", nullable = true, updatable = true, length = 20)
	private Double tariffValue = null;

	@Transient
	private String name= null;
	
	@Transient
	private String gstin= null;

	@Transient
	AddressDetailModel addressDetailModel = null;

	/* Getter and Setter Defined */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public GeneralMasterModel getCarCatId() {
		return carCatId;
	}

	public void setCarCatId(GeneralMasterModel carCatId) {
		this.carCatId = carCatId;
	}

	public GeneralMasterModel getTariffId() {
		return tariffId;
	}

	public void setTariffId(GeneralMasterModel tariffId) {
		this.tariffId = tariffId;
	}

	public Double getTariffValue() {
		return tariffValue;
	}

	public void setTariffValue(Double tariffValue) {
		this.tariffValue = tariffValue;
	}

	public CorporateTariffModel getCorporateTariffModel() {
		return corporateTariffModel;
	}

	public void setCorporateTariffModel(CorporateTariffModel corporateTariffModel) {
		this.corporateTariffModel = corporateTariffModel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public AddressDetailModel getAddressDetailModel() {
		return addressDetailModel;
	}

	public void setAddressDetailModel(AddressDetailModel addressDetailModel) {
		this.addressDetailModel = addressDetailModel;
	}
	
}
