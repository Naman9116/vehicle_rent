package com.ets.dao;
import java.util.List;

import com.ets.model.CarRateModel;
import com.ets.model.LocationMasterModel;
import com.master.model.CityMasterModel;
import com.master.model.GeneralMasterModel;
public interface LocationMasterDao {
	public LocationMasterModel save(LocationMasterModel   locationMasterModel);
	public String update(LocationMasterModel   locationMasterModel) ;
	public List<LocationMasterModel> getLocationMaster(Long corporateId);
	public LocationMasterModel formFillForEditLocationMaster(Long id);
	public String delete(Long idForDelete) ;
	public List<CityMasterModel> getCityUsingPinCode(long pincode);
	public String save(CarRateModel   carRateModel) ;
	public List<CarRateModel> getCarRate(long locationId);
	public String update(CarRateModel   carRateModel);
	public String deleteCarRateModel(Long idForDelete) ;
	public List<CarRateModel> getCarRateListAsPerAEffectiveDate(long locationId,String effectiveDate);
	public List<GeneralMasterModel> getZoneAsBranch(Long branchId);
	List<GeneralMasterModel> getLocationAsZone(Long zoneId);
}
