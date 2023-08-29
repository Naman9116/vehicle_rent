package com.ets.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ets.dao.LocationMasterDao;
import com.ets.model.CarRateModel;
import com.ets.model.LocationMasterModel;
import com.master.model.CityMasterModel;
import com.master.model.GeneralMasterModel;


@Service("locationMasterService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class LocationMasterServiceImpl implements LocationMasterService {
	@Autowired
	private LocationMasterDao  locationMasterDao;
	
	public LocationMasterServiceImpl() {}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public LocationMasterModel save(LocationMasterModel   locationMasterModel) {
		return locationMasterDao.save(locationMasterModel);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(LocationMasterModel   locationMasterModel) {
		return locationMasterDao.update(locationMasterModel);
	}
	@Override
	public List<LocationMasterModel> getLocationMaster(Long corporateId){
		return locationMasterDao.getLocationMaster(corporateId);
	}
	@Override
	public LocationMasterModel formFillForEditLocationMaster(Long id){
		return locationMasterDao.formFillForEditLocationMaster(id);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
	return locationMasterDao.delete(idForDelete);
	}
	@Override
	public List<CityMasterModel> getCityUsingPinCode(long pincode){
		return locationMasterDao.getCityUsingPinCode(pincode);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(CarRateModel   carRateModel) {
		return locationMasterDao.save(carRateModel);
	}
	@Override
	public List<CarRateModel> getCarRate(long locationId){
		return locationMasterDao.getCarRate(locationId);
	}
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(CarRateModel   carRateModel){
		return locationMasterDao.update(carRateModel);
	}
	
	@Override
	public List<CarRateModel> getCarRateListAsPerAEffectiveDate(long locationId,String effectiveDate){
		return locationMasterDao.getCarRateListAsPerAEffectiveDate(locationId,effectiveDate);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String deleteCarRateModel(Long idForDelete){
		return locationMasterDao.deleteCarRateModel(idForDelete);
	}

	@Override
	public List<GeneralMasterModel> getZoneAsBranch(Long branchId) {
		return locationMasterDao.getZoneAsBranch(branchId);
	}

	@Override
	public List<GeneralMasterModel> getLocationAsZone(Long zoneId) {
		return locationMasterDao.getLocationAsZone(zoneId);
	}
}
