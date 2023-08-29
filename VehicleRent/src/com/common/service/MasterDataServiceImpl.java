package com.common.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.common.dao.MasterDataDao;
import com.common.model.AddressDetailModel;
import com.master.model.CityMasterModel;
import com.master.model.CorporateTariffModel;
import com.master.model.DistrictMasterModel;
import com.master.model.GeneralMasterModel;
import com.master.model.MasterModel;
import com.master.model.StateMasterModel;
import com.operation.model.CarDetailModel;
import com.user.model.UserModel;

@Service("masterDataService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class MasterDataServiceImpl implements MasterDataService {

	@Autowired
	private MasterDataDao masterDataDao;

	public MasterDataServiceImpl() {

	}

	@Override
	public List<GeneralMasterModel> getGeneralMasterData_UsingCode(String masterCode) {
		return masterDataDao.getGeneralMasterData_UsingCode(masterCode);
	}

	@Override
	public List<GeneralMasterModel> getGeneralMasterData_Menu(String userRole) {
		return masterDataDao.getGeneralMasterData_Menu(userRole);
	}

	@Override
	public List<GeneralMasterModel> getGeneralMasterData_UsingId(String masterId) {
		return masterDataDao.getGeneralMasterData_UsingId(masterId);
	}
	@Override
	public List<MasterModel> getMasterData(String masterCode) {
		return masterDataDao.getMasterData(masterCode);
	}

	@Override
	public List<GeneralMasterModel> getGeneralMasterData_WithMapping(String parentId) {
		return masterDataDao.getGeneralMasterData_WithMapping(parentId);
	}
	
	@Override
	public List<UserModel> getUserDara_UsingCode(String parentCode,String childCode) {
		return masterDataDao.getUserDara_UsingCode(parentCode,childCode);
	}
	
	@Override
	public List<CarDetailModel> getCarModel_usingCarType(Long carTypeId) {
		return masterDataDao.getCarModel_usingCarType(carTypeId);
	}

	@Override
	public List<StateMasterModel> getStateMasterData() {
		return masterDataDao.getStateMasterData();
	}

	@Override
	public List<DistrictMasterModel> getDistrictMasterData(Long stateId,Long distId) {
		return masterDataDao.getDistrictMasterData(stateId,distId);
	}

	@Override
	public List<CityMasterModel> getCityMasterData(Long districtId,Long pincode) {
		return masterDataDao.getCityMasterData(districtId,pincode);
	}

	@Override
	public List<DistrictMasterModel> getStateDistMasterData_usingPincode(Long pincode) {
		return masterDataDao.getStateDistMasterData_usingPincode(pincode);
	}

	@Override
	public List<CorporateTariffModel> selectTariffOnTheBasisOfCustomer(String customerId,String bookingDate) {
		return masterDataDao.selectTariffOnTheBasisOfCustomer(customerId,bookingDate);
	}

	@Override
	public List<CorporateTariffModel> selectTariffOnTheBasisOfDutySlipId(String dutySlipId) {
		return masterDataDao.selectTariffOnTheBasisOfDutySlipId(dutySlipId);
	}

	@Override
	public List<GeneralMasterModel> getBranchDataUsingCompany(String companyId) {
		return masterDataDao.getBranchDataUsingCompany(companyId);
	}

	@Override
	public List<UserModel> userLogin(String userName, String password) {
		return masterDataDao.userLogin(userName, password);
	}

	@Override
	public Map<String,String> getUserAccess(String UserId, String sMenuPage) {
		return masterDataDao.getUserAccess(UserId, sMenuPage);
	}

	@Override
	public AddressDetailModel getAddressDataFromID(Long addressID, String sFor) {
		return masterDataDao.getAddressDataFromID(addressID, sFor);
	}
	
	@Override
	public GeneralMasterModel getVehicleCategoryForVehicleModel(Long carModelId){
		return masterDataDao.getVehicleCategoryForVehicleModel(carModelId);
	}
	
	@Override
	public List<CarDetailModel> getCarRegistrationNumberList(){
		return masterDataDao.getCarRegistrationNumberList();
	}
	@Override
	public List<CarDetailModel> getCarRegistrationNoList(String sModelNo){
		return masterDataDao.getCarRegistrationNoList(sModelNo);
	}
}
