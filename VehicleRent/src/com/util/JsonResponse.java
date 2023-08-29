package com.util;

import java.util.List;

import com.billing.model.CoverLetterModel;
import com.common.model.AddressDetailModel;
import com.common.model.ContactDetailModel;
import com.corporate.model.AutorizedUserModel;
import com.corporate.model.CorporateModel;
import com.ets.model.CarRateModel;
import com.ets.model.LocationMasterModel;
import com.ets.model.RosterMasterModel;
import com.master.model.CarAllocationModel;
import com.master.model.ChauffeurModel;
import com.master.model.CityMasterModel;
import com.master.model.CorporateTariffDetModel;
import com.master.model.CorporateTariffModel;
import com.master.model.CorporateTaxDetModel;
import com.master.model.DistrictMasterModel;
import com.master.model.GeneralMasterModel;
import com.master.model.MappingMasterModel;
import com.master.model.MasterModel;
import com.master.model.TariffSchemeParaModel;
import com.master.model.TaxationModel;
import com.master.model.VendorModel;
import com.operation.model.BookingDetailModel;
import com.operation.model.BookingMasterModel;
import com.operation.model.CarDetailModel;
import com.operation.model.DutySlipModel;
import com.operation.model.PassengerDetailModel;
import com.relatedInfo.model.RelatedInfoModel;
import com.toursandtravels.model.BookingModel;
import com.toursandtravels.model.CustomerModel;
import com.toursandtravels.model.TDutySlipModel;
import com.user.model.UserAccessModel;
import com.user.model.UserModel;

public class JsonResponse {

	private String status = null;
	private String Check = null;
	private Object result = null;
	private String dataGrid = null;
	private String dataString1=null;
	private String dataString2=null;
	private String saveId=null;
	private Object[] dataArray=null;
	
	private MasterModel masterModel = null;
	private GeneralMasterModel generalMasterModel = null;
	private MappingMasterModel mappingMasterModel = null;
	private UserModel userModel = null;
	private ContactDetailModel contactDetailModel = null;
	private CarDetailModel carDetailModel = null;
	private BookingMasterModel bookingMasterModel = null;
	private BookingDetailModel bookingDetailModel = null;
	private CorporateTariffModel corporateTariffModel=null; 
	private RelatedInfoModel relatedInfoModel=null;
	private DistrictMasterModel districtMasterModel=null;
	private CorporateModel corporateModel=null;
	private TaxationModel taxationModel=null;
	private VendorModel vendorModel=null;
	private ChauffeurModel chauffeurModel=null;
	private CarAllocationModel carAllocationModel=null;
	private AutorizedUserModel autorizedUserModel=null;
	private AddressDetailModel addressDetail=null;
	private DutySlipModel dutySlipModel = null; 
	private TariffSchemeParaModel tariffSchemeParaModel = null;
	private List<GeneralMasterModel> generalMasterModelList = null;
	private List<GeneralMasterModel> autorizedUserModelList = null;
	private List<GeneralMasterModel> branchList = null;
	private List<GeneralMasterModel> terminalList = null;
	private List<GeneralMasterModel> carModelList = null;
	private List<GeneralMasterModel> coverLetterList = null;
	private List<GeneralMasterModel> corpTariffList = null;
	private List<BookingMasterModel> bookingMasterList = null;
	private List<BookingDetailModel> bookingDetailList = null;
	private List<GeneralMasterModel> corpRentalList = null;
	private List<GeneralMasterModel> zoneList = null;
	private List<GeneralMasterModel> locationList = null;
	private List<AutorizedUserModel> autorizedUserModels= null;
	
	private List<DistrictMasterModel> districtMasterModelList=null;
	private List<CityMasterModel> cityMasterModelsList=null;
	private List<CorporateTariffDetModel> corporateTariffDetModelList=null; 
	private List<CarDetailModel> carDetailModelList=null; 
	private List<DutySlipModel> dutySlipModelList=null; 
	private List<GeneralMasterModel> companyList = null;
	private long dsIdCount;
	private Long bookingDetailIdCount;
	private Long bookingDetailId;
	private Long carAllocationId;
	private List<CorporateTaxDetModel> corporateTaxDetModelList=null;
	private List<CarAllocationModel> carAllocationModels = null;
	private List<TariffSchemeParaModel> tariffSchemeParaModelList = null;
	private List<CorporateTariffModel> corporateTariffModelList=null; 
	private List<UserAccessModel> userAccessModel=null;
	private LocationMasterModel locationMasterModel=null;
	private List<LocationMasterModel> locationMasterModelList=null;

	private CustomerModel customerModel=null;
	private List<CustomerModel> customerModelList=null;
	private BookingModel bookingModel=null;
	private List<BookingModel> BookingModelList=null;

	private RosterMasterModel rosterMasterModel = null;
	private List<RosterMasterModel> rosterMasterModelList = null;
	private List<CarRateModel>  CarRateModelList=null;
	private List<PassengerDetailModel> passengerDetailModelList=null;
	private List<CoverLetterModel> coverLetterModelList=null;
	
	public List<PassengerDetailModel> getPassengerDetailModelList() {
		return passengerDetailModelList;
	}

	public void setPassengerDetailModelList(List<PassengerDetailModel> passengerDetailModelList) {
		this.passengerDetailModelList = passengerDetailModelList;
	}

	public List<CarRateModel> getCarRateModelList() {
		return CarRateModelList;
	}

	public void setCarRateModelList(List<CarRateModel> carRateModelList) {
		CarRateModelList = carRateModelList;
	}

	private TDutySlipModel tDutySlipModel=null;
	
	public MasterModel getMasterModel() {
		return masterModel;
	}

	public void setMasterModel(MasterModel masterModel) {
		this.masterModel = masterModel;
	}

	public GeneralMasterModel getGeneralMasterModel() {
		return generalMasterModel;
	}

	public void setGeneralMasterModel(GeneralMasterModel generalMasterModel) {
		this.generalMasterModel = generalMasterModel;
	}

	public String getDataGrid() {
		return dataGrid;
	}

	public void setDataGrid(String dataGrid) {
		this.dataGrid = dataGrid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public MappingMasterModel getMappingMasterModel() {
		return mappingMasterModel;
	}

	public void setMappingMasterModel(MappingMasterModel mappingMasterModel) {
		this.mappingMasterModel = mappingMasterModel;
	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public ContactDetailModel getContactDetailModel() {
		return contactDetailModel;
	}

	public void setContactDetailModel(ContactDetailModel contactDetailModel) {
		this.contactDetailModel = contactDetailModel;
	}

	public CarDetailModel getCarDetailModel() {
		return carDetailModel;
	}

	public void setCarDetailModel(CarDetailModel carDetailModel) {
		this.carDetailModel = carDetailModel;
	}

	public String getDataString1() {
		return dataString1;
	}

	public void setDataString1(String dataString1) {
		this.dataString1 = dataString1;
	}

	public String getDataString2() {
		return dataString2;
	}

	public void setDataString2(String dataString2) {
		this.dataString2 = dataString2;
	}

	public BookingMasterModel getBookingMasterModel() {
		return bookingMasterModel;
	}

	public void setBookingMasterModel(BookingMasterModel bookingMasterModel) {
		this.bookingMasterModel = bookingMasterModel;
	}

	public BookingDetailModel getBookingDetailModel() {
		return bookingDetailModel;
	}

	public void setBookingDetailModel(BookingDetailModel bookingDetailModel) {
		this.bookingDetailModel = bookingDetailModel;
	}

	public String getSaveId() {
		return saveId;
	}

	public void setSaveId(String saveId) {
		this.saveId = saveId;
	}

	public Object[] getDataArray() {
		return dataArray;
	}

	public void setDataArray(Object[] dataArray) {
		this.dataArray = dataArray;
	}

	public RelatedInfoModel getRelatedInfoModel() {
		return relatedInfoModel;
	}

	public void setRelatedInfoModel(RelatedInfoModel relatedInfoModel) {
		this.relatedInfoModel = relatedInfoModel;
	}

	public DistrictMasterModel getDistrictMasterModel() {
		return districtMasterModel;
	}

	public void setDistrictMasterModel(DistrictMasterModel districtMasterModel) {
		this.districtMasterModel = districtMasterModel;
	}

	public CorporateModel getCorporateModel() {
		return corporateModel;
	}

	public void setCorporateModel(CorporateModel corporateModel) {
		this.corporateModel = corporateModel;
	}

	public TaxationModel getTaxationModel() {
		return taxationModel;
	}

	public void setTaxationModel(TaxationModel taxationModel) {
		this.taxationModel = taxationModel;
	}

	public DutySlipModel getDutySlipModel() {
		return dutySlipModel;
	}

	public void setDutySlipModel(DutySlipModel dutySlipModel) {
		this.dutySlipModel = dutySlipModel;
	}

	public List<GeneralMasterModel> getGeneralMasterModelList() {
		return generalMasterModelList;
	}

	public void setGeneralMasterModelList(List<GeneralMasterModel> generalMasterModelList) {
		this.generalMasterModelList = generalMasterModelList;
	}

	public List<DistrictMasterModel> getDistrictMasterModelList() {
		return districtMasterModelList;
	}

	public void setDistrictMasterModelList(List<DistrictMasterModel> districtMasterModelList) {
		this.districtMasterModelList = districtMasterModelList;
	}

	public List<CityMasterModel> getCityMasterModelsList() {
		return cityMasterModelsList;
	}

	public void setCityMasterModelsList(List<CityMasterModel> cityMasterModelsList) {
		this.cityMasterModelsList = cityMasterModelsList;
	}

	public CorporateTariffModel getCorporateTariffModel() {
		return corporateTariffModel;
	}

	public void setCorporateTariffModel(CorporateTariffModel corporateTariffModel) {
		this.corporateTariffModel = corporateTariffModel;
	}

	public List<CorporateTariffDetModel> getCorporateTariffDetModelList() {
		return corporateTariffDetModelList;
	}

	public void setCorporateTariffDetModelList(List<CorporateTariffDetModel> corporateTariffDetModelList) {
		this.corporateTariffDetModelList = corporateTariffDetModelList;
	}

	public AutorizedUserModel getAutorizedUserModel() {
		return autorizedUserModel;
	}

	public void setAutorizedUserModel(AutorizedUserModel autorizedUserModel) {
		this.autorizedUserModel = autorizedUserModel;
	}

	public VendorModel getVendorModel() {
		return vendorModel;
	}

	public void setVendorModel(VendorModel vendorModel) {
		this.vendorModel = vendorModel;
	}

	public ChauffeurModel getChauffeurModel() {
		return chauffeurModel;
	}

	public void setChauffeurModel(ChauffeurModel chauffeurModel) {
		this.chauffeurModel = chauffeurModel;
	}

	public List<GeneralMasterModel> getAutorizedUserModelList() {
		return autorizedUserModelList;
	}

	public void setAutorizedUserModelList(List<GeneralMasterModel> autorizedUserModelList) {
		this.autorizedUserModelList = autorizedUserModelList;
	}

	public List<GeneralMasterModel> getBranchList() {
		return branchList;
	}

	public void setBranchList(List<GeneralMasterModel> branchList) {
		this.branchList = branchList;
	}

	public List<GeneralMasterModel> getCorpTariffList() {
		return corpTariffList;
	}

	public void setCorpTariffList(List<GeneralMasterModel> corpTariffList) {
		this.corpTariffList = corpTariffList;
	}

	public AddressDetailModel getAddressDetail() {
		return addressDetail;
	}

	public void setAddressDetail(AddressDetailModel addressDetailList) {
		this.addressDetail = addressDetailList;
	}

	public List<BookingMasterModel> getBookingMasterList() {
		return bookingMasterList;
	}

	public void setBookingMasterList(List<BookingMasterModel> bookingMasterList) {
		this.bookingMasterList = bookingMasterList;
	}

	public List<BookingDetailModel> getBookingDetailList() {
		return bookingDetailList;
	}

	public void setBookingDetailList(List<BookingDetailModel> bookingDetailList) {
		this.bookingDetailList = bookingDetailList;
	}

	public List<GeneralMasterModel> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<GeneralMasterModel> companyList) {
		this.companyList = companyList;
	}

	public List<GeneralMasterModel> getTerminalList() {
		return terminalList;
	}

	public void setTerminalList(List<GeneralMasterModel> terminalList) {
		this.terminalList = terminalList;
	}

	public CarAllocationModel getCarAllocationModel() {
		return carAllocationModel;
	}

	public void setCarAllocationModel(CarAllocationModel carAllocationModel) {
		this.carAllocationModel = carAllocationModel;
	}

	public List<CarDetailModel> getCarDetailModelList() {
		return carDetailModelList;
	}

	public void setCarDetailModelList(List<CarDetailModel> carDetailModelList) {
		this.carDetailModelList = carDetailModelList;
	}

	public List<DutySlipModel> getDutySlipModelList() {
		return dutySlipModelList;
	}

	public void setDutySlipModelList(List<DutySlipModel> dutySlipModelList) {
		this.dutySlipModelList = dutySlipModelList;
	}

	public long getDsIdCount() {
		return dsIdCount;
	}

	public void setDsIdCount(long dsIdCount) {
		this.dsIdCount = dsIdCount;
	}

	public Long getBookingDetailIdCount() {
		return bookingDetailIdCount;
	}

	public void setBookingDetailIdCount(Long bookingDetailIdCount) {
		this.bookingDetailIdCount = bookingDetailIdCount;
	}

	public Long getBookingDetailId() {
		return bookingDetailId;
	}

	public void setBookingDetailId(Long bookingDetailId) {
		this.bookingDetailId = bookingDetailId;
	}

	public Long getCarAllocationId() {
		return carAllocationId;
	}

	public void setCarAllocationId(Long carAllocationId) {
		this.carAllocationId = carAllocationId;
	}

	public List<GeneralMasterModel> getCarModelList() {
		return carModelList;
	}

	public void setCarModelList(List<GeneralMasterModel> carModelList) {
		this.carModelList = carModelList;
	}

	public List<GeneralMasterModel> getCorpRentalList() {
		return corpRentalList;
	}

	public void setCorpRentalList(List<GeneralMasterModel> corpRentalList) {
		this.corpRentalList = corpRentalList;
	}

	public List<CorporateTaxDetModel> getCorporateTaxDetModelList() {
		return corporateTaxDetModelList;
	}

	public void setCorporateTaxDetModelList(List<CorporateTaxDetModel> corporateTaxDetModelList) {
		this.corporateTaxDetModelList = corporateTaxDetModelList;
	}

	public TariffSchemeParaModel getTariffSchemeParaModel() {
		return tariffSchemeParaModel;
	}

	public void setTariffSchemeParaModel(TariffSchemeParaModel tariffSchemeParaModel) {
		this.tariffSchemeParaModel = tariffSchemeParaModel;
	}

	public List<CarAllocationModel> getCarAllocationModels() {
		return carAllocationModels;
	}

	public void setCarAllocationModels(List<CarAllocationModel> carAllocationModels) {
		this.carAllocationModels = carAllocationModels;
	}

	public List<TariffSchemeParaModel> getTariffSchemeParaModelList() {
		return tariffSchemeParaModelList;
	}

	public void setTariffSchemeParaModelList(List<TariffSchemeParaModel> tariffSchemeParaModelList) {
		this.tariffSchemeParaModelList = tariffSchemeParaModelList;
	}

	public List<CorporateTariffModel> getCorporateTariffModelList() {
		return corporateTariffModelList;
	}

	public void setCorporateTariffModelList(List<CorporateTariffModel> corporateTariffModelList) {
		this.corporateTariffModelList = corporateTariffModelList;
	}

	public List<UserAccessModel> getUserAccessModel() {
		return userAccessModel;
	}

	public void setUserAccessModel(List<UserAccessModel> userAccessModel) {
		this.userAccessModel = userAccessModel;
	}

	public String getCheck() {
		return Check;
	}

	public void setCheck(String check) {
		Check = check;
	}

	public LocationMasterModel getLocationMasterModel() {
		return locationMasterModel;
	}

	public void setLocationMasterModel(LocationMasterModel locationMasterModel) {
		this.locationMasterModel = locationMasterModel;
	}

	public List<LocationMasterModel> getLocationMasterModelList() {
		return locationMasterModelList;
	}

	public void setLocationMasterModelList(List<LocationMasterModel> locationMasterModelList) {
		this.locationMasterModelList = locationMasterModelList;
	}

	public CustomerModel getCustomerModel() {
		return customerModel;
	}

	public void setCustomerModel(CustomerModel customerModel) {
		this.customerModel = customerModel;
	}

	public List<CustomerModel> getCustomerModelList() {
		return customerModelList;
	}

	public void setCustomerModelList(List<CustomerModel> customerModelList) {
		this.customerModelList = customerModelList;
	}

	public BookingModel getBookingModel() {
		return bookingModel;
	}

	public void setBookingModel(BookingModel bookingModel) {
		this.bookingModel = bookingModel;
	}

	public List<BookingModel> getBookingModelList() {
		return BookingModelList;
	}

	public void setBookingModelList(List<BookingModel> bookingModelList) {
		BookingModelList = bookingModelList;
	}

	public RosterMasterModel getRosterMasterModel() {
		return rosterMasterModel;
	}

	public void setRosterMasterModel(RosterMasterModel rosterMasterModel) {
		this.rosterMasterModel = rosterMasterModel;
	}

	public List<RosterMasterModel> getRosterMasterModelList() {
		return rosterMasterModelList;
	}

	public void setRosterMasterModelList(List<RosterMasterModel> rosterMasterModelList) {
		this.rosterMasterModelList = rosterMasterModelList;
	}

	public TDutySlipModel gettDutySlipModel() {
		return tDutySlipModel;
	}

	public void settDutySlipModel(TDutySlipModel tDutySlipModel) {
		this.tDutySlipModel = tDutySlipModel;
	}

	public List<CoverLetterModel> getCoverLetterModelList() {
		return coverLetterModelList;
	}

	public void setCoverLetterModelList(List<CoverLetterModel> coverLetterModelList) {
		this.coverLetterModelList = coverLetterModelList;
	}

	public List<GeneralMasterModel> getCoverLetterList() {
		return coverLetterList;
	}

	public void setCoverLetterList(List<GeneralMasterModel> coverLetterList) {
		this.coverLetterList = coverLetterList;
	}

	public List<GeneralMasterModel> getZoneList() {
		return zoneList;
	}

	public void setZoneList(List<GeneralMasterModel> zoneList) {
		this.zoneList = zoneList;
	}

	public List<GeneralMasterModel> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<GeneralMasterModel> locationList) {
		this.locationList = locationList;
	}

	public List<AutorizedUserModel> getAutorizedUserModels() {
		return autorizedUserModels;
	}

	public void setAutorizedUserModels(List<AutorizedUserModel> autorizedUserModels) {
		this.autorizedUserModels= autorizedUserModels;
	}
}
