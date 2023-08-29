package com.common.service;

import java.util.List;
import java.util.Map;

import com.common.model.AddressDetailModel;
import com.master.model.CityMasterModel;
import com.master.model.CorporateTariffModel;
import com.master.model.DistrictMasterModel;
import com.master.model.GeneralMasterModel;
import com.master.model.MasterModel;
import com.master.model.StateMasterModel;
import com.operation.model.CarDetailModel;
import com.user.model.UserModel;

public interface MasterDataService {
	
	public List<UserModel> userLogin(String userCode, String password) ;
	public List<GeneralMasterModel> getGeneralMasterData_UsingCode(String masterCode) ;
	public List<GeneralMasterModel> getGeneralMasterData_Menu(String userRole);
	public List<GeneralMasterModel> getGeneralMasterData_UsingId(String masterId) ;
	public List<MasterModel> getMasterData(String masterCode) ;
	public List<GeneralMasterModel> getGeneralMasterData_WithMapping(String parentId) ;
	public List<UserModel> getUserDara_UsingCode(String parentCode,String childCode) ;
	public List<CarDetailModel> getCarModel_usingCarType(Long carTypeId);
	public List<StateMasterModel> getStateMasterData();
	public List<DistrictMasterModel> getDistrictMasterData(Long stateId,Long distId);
	public List<CityMasterModel> getCityMasterData(Long districtId,Long pincode);
	public List<DistrictMasterModel> getStateDistMasterData_usingPincode(Long pincode);
	public List<CorporateTariffModel> selectTariffOnTheBasisOfCustomer(String customerId, String bookingDate);
	public List<CorporateTariffModel> selectTariffOnTheBasisOfDutySlipId(String dutySlipId);
	public List<GeneralMasterModel> getBranchDataUsingCompany(String companyId) ;
	public Map<String,String> getUserAccess(String UserId, String sMenuPage) ;
	public AddressDetailModel getAddressDataFromID(Long addressID, String sFor);
	public GeneralMasterModel getVehicleCategoryForVehicleModel(Long carModelId);
	public List<CarDetailModel> getCarRegistrationNumberList();
	public List<CarDetailModel> getCarRegistrationNoList(String sModelNo);
}
