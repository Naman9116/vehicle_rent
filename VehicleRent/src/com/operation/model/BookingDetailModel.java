package com.operation.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.common.model.AddressDetailModel;
import com.master.model.CorporateTariffDetModel;
import com.master.model.GeneralMasterModel;
import com.relatedInfo.model.RelatedInfoModel;
import com.user.model.UserModel;

@Entity
@Table(name="bookingDetails")
@JsonIgnoreProperties(value={"bookingMasterModel","dutySlipModel"})
public class BookingDetailModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@ManyToOne(targetEntity=BookingMasterModel.class)
	@JoinColumn(name="bookingMasterId",referencedColumnName="id",nullable=false,updatable=true)
	private BookingMasterModel bookingMasterModel=null; 
	
	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="company",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel company=null;
	
	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="branch",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel branch=null;

	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="outlet",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel outlet=null;

	@Column(name="pickUpDate",updatable=true,nullable=true,length=30,unique=false)
	private Date pickUpDate=null;

	@Column(name="startTime",updatable=true,nullable=true,length=10,unique=false)
	private String startTime=null;
	
	@Column(name="pickUpTime",updatable=true,nullable=true,length=10,unique=false)
	private String pickUpTime=null;
	
	@Column(name="flightNo",updatable=true,nullable=true,length=50,unique=false)
	private String flightNo=null;
	
	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="terminal",updatable=true,nullable=true,referencedColumnName="id",unique=false)
	private GeneralMasterModel terminal=null;

	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="mob",referencedColumnName="id",nullable=true,updatable=true)
	private GeneralMasterModel mob = null;

	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="carModelId",referencedColumnName="id",nullable=true,updatable=true)
	private GeneralMasterModel carModel = null;
	
	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="rentalTypeId",referencedColumnName="id",nullable=true,updatable=true)
	private GeneralMasterModel rentalType = null;

	@ManyToOne(targetEntity=GeneralMasterModel.class)
	@JoinColumn(name="tariffId",referencedColumnName="id",nullable=true,updatable=true)
	private GeneralMasterModel tariff = null;

	@Column(name="reportAt",updatable=true,nullable=true,length=1)
	private String reportAt=null;

	@Column(name="reportingAddress",updatable=true,nullable=true,length=200,unique=false)
	private String reportingAddress=null;
	
	@Column(name="reportingAddressLatLong",updatable=true,nullable=true,length=200,unique=false)
	private String reportingAddressLatLong=null;

	@Column(name="toBeRealeseAt",updatable=true,nullable=true,length=200,unique=false)
	private String toBeRealeseAt=null;

	@Column(name="toBeRealeseAtLatLong",updatable=true,nullable=true,length=50,unique=false)
	private String toBeRealeseAtLatLong=null;

	@Column(name="instruction",updatable=true,nullable=true,length=150,unique=false)
	private String instruction=null;
	
	@Column(name="status",updatable=true,nullable=true,length=1,unique=false)
	private String status=null;
	
	@Column(name="cancelReason",updatable=true,nullable=true,length=200,unique=false )
	private String cancelReason=null;
	
	@Column(name="cancelDate",updatable=true,nullable=true,length=30,unique=false)
	private  Date cancelDate=null;
	
	@ManyToOne(targetEntity=UserModel.class)
	@JoinColumn(name="cancelBy",updatable=true,nullable=true,unique=false,referencedColumnName="id")
	private UserModel cancelBy=null;
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "bookingDetailModel", cascade = CascadeType.ALL)
	@NotFound(action=NotFoundAction.IGNORE)
	private DutySlipModel dutySlipModel = null;
	
/*	@OneToOne(cascade = CascadeType.ALL,mappedBy="bookingDetailId",optional=true)
	@NotFound(action=NotFoundAction.IGNORE)
	private List<PassengerDetailModel> passengerDetailModel = null;
*/	
	@Transient
	private GeneralMasterModel carType = null;
	
	@Transient
	private AddressDetailModel address = null;
	
	@Transient
	private RelatedInfoModel relatedInfo = null;
	
	@Transient
	private GeneralMasterModel generalMaster = null;
	
	@Transient
	private List<CorporateTariffDetModel>  corporateTariffDetModels = null;

	@Transient
	private List<DutySlipModel> dutyslip = null;

	@Transient
	private String reportAtType;
	
	@Transient
	private String releaseAtType;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BookingMasterModel getBookingMasterModel() {
		return bookingMasterModel;
	}

	public void setBookingMasterModel(BookingMasterModel bookingMasterModel) {
		this.bookingMasterModel = bookingMasterModel;
	}

	public Date getPickUpDate() {
		return pickUpDate;
	}

	public void setPickUpDate(Date pickUpDate) {
		this.pickUpDate = pickUpDate;
	}

	public GeneralMasterModel getOutlet() {
		return outlet;
	}

	public void setOutlet(GeneralMasterModel outlet) {
		this.outlet = outlet;
	}

	public GeneralMasterModel getTariff() {
		return tariff;
	}

	public void setTariff(GeneralMasterModel tariff) {
		this.tariff = tariff;
	}
	
	public GeneralMasterModel getRentalType() {
		return rentalType;
	}

	public void setRentalType(GeneralMasterModel rentalType) {
		this.rentalType = rentalType;
	}

	public String getFlightNo() {
		return flightNo;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getPickUpTime() {
		return pickUpTime;
	}

	public void setPickUpTime(String pickUpTime) {
		this.pickUpTime = pickUpTime;
	}

	public String getReportingAddress() {
		return reportingAddress;
	}

	public void setReportingAddress(String reportingAddress) {
		this.reportingAddress = reportingAddress;
	}

	public String getToBeRealeseAt() {
		return toBeRealeseAt;
	}

	public void setToBeRealeseAt(String toBeRealeseAt) {
		this.toBeRealeseAt = toBeRealeseAt;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public GeneralMasterModel getCarModel() {
		return carModel;
	}

	public void setCarModel(GeneralMasterModel carModel) {
		this.carModel = carModel;
	}

	public GeneralMasterModel getBranch() {
		return branch;
	}

	public void setBranch(GeneralMasterModel branch) {
		this.branch = branch;
	}

	public String getReportAt() {
		return reportAt;
	}

	public void setReportAt(String reportAt) {
		this.reportAt = reportAt;
	}

	public GeneralMasterModel getMob() {
		return mob;
	}

	public void setMob(GeneralMasterModel mob) {
		this.mob = mob;
	}
	
	public GeneralMasterModel getCompany() {
		return company;
	}

	public void setCompany(GeneralMasterModel company) {
		this.company = company;
	}
	
	public GeneralMasterModel getCarType() {
		return carType;
	}

	public void setCarType(GeneralMasterModel carType) {
		this.carType = carType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
	
	

	public AddressDetailModel getAddress() {
		return address;
	}

	public void setAddress(AddressDetailModel address) {
		this.address = address;
	}
    
	public RelatedInfoModel getRelatedInfo() {
		return relatedInfo;
	}

	public void setRelatedInfo(RelatedInfoModel relatedInfo) {
		this.relatedInfo = relatedInfo;
	}

	@Override
	public String toString() {
		return "BookingDetailModel [id=" + id + ", bookingMasterModel=" + bookingMasterModel + ", branch=" + branch
				+ ", outlet=" + outlet + ", bookingDate=" + pickUpDate
				+ ", startTime=" + startTime + ", pickUpTime=" + pickUpTime + ", flightNo=" + flightNo + ", mob=" + mob
				+ ", carModel=" + carModel + ", rentalType=" + rentalType + ", tariff="
				+ tariff + ", reportAt=" + reportAt + ", reportingAddress=" + reportingAddress + ", toBeRealeseAt="
				+ toBeRealeseAt + ", instruction=" + instruction + "]";
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public UserModel getCancelBy() {
		return cancelBy;
	}

	public void setCancelBy(UserModel cancelBy) {
		this.cancelBy = cancelBy;
	}

	public DutySlipModel getDutySlipModel() {
		return dutySlipModel;
	}

	public void setDutySlipModel(DutySlipModel dutySlipModel) {
		this.dutySlipModel = dutySlipModel;
	}

	public GeneralMasterModel getGeneralMaster() {
		return generalMaster;
	}

	public void setGeneralMaster(GeneralMasterModel generalMaster) {
		this.generalMaster = generalMaster;
	}

	public List<CorporateTariffDetModel> getCorporateTariffDetModels() {
		return corporateTariffDetModels;
	}

	public void setCorporateTariffDetModels(List<CorporateTariffDetModel> corporateTariffDetModels) {
		this.corporateTariffDetModels = corporateTariffDetModels;
	}

	public List<DutySlipModel> getDutyslip() {
		return dutyslip;
	}

	public void setDutyslip(List<DutySlipModel> dutyslip) {
		this.dutyslip = dutyslip;
	}

	public GeneralMasterModel getTerminal() {
		return terminal;
	}

	public void setTerminal(GeneralMasterModel terminal) {
		this.terminal = terminal;
	}

	public String getReportingAddressLatLong() {
		return reportingAddressLatLong;
	}

	public void setReportingAddressLatLong(String reportingAddressLatLong) {
		this.reportingAddressLatLong = reportingAddressLatLong;
	}

	public String getToBeRealeseAtLatLong() {
		return toBeRealeseAtLatLong;
	}

	public void setToBeRealeseAtLatLong(String toBeRealeseAtLatLong) {
		this.toBeRealeseAtLatLong = toBeRealeseAtLatLong;
	}

	public String getReportAtType() {
		return reportAtType;
	}

	public void setReportAtType(String reportAtType) {
		this.reportAtType = reportAtType;
	}

	public String getReleaseAtType() {
		return releaseAtType;
	}

	public void setReleaseAtType(String releaseAtType) {
		this.releaseAtType = releaseAtType;
	}

/*	public List<PassengerDetailModel> getPassengerDetailModel() {
		return passengerDetailModel;
	}

	public void setPassengerDetailModel(List<PassengerDetailModel> passengerDetailModel) {
		this.passengerDetailModel = passengerDetailModel;
	}
*/
}
