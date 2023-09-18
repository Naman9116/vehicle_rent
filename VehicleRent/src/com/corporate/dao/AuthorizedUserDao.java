package com.corporate.dao;

import java.util.List;

import com.common.model.ContactDetailModel;
import com.corporate.model.AutorizedUserModel;
import com.ets.model.LocationMasterModel;
import com.master.model.GeneralMasterModel;

public interface AuthorizedUserDao {
	public List<AutorizedUserModel> list(String pageFor,Long corporateId) ;
	public List<AutorizedUserModel> listMasterWise(String searchCriteria) ;
	public String save(AutorizedUserModel autorizedUserModel);
	public AutorizedUserModel formFillForEdit(Long formFillForEditId);
	public String delete(Long idForDelete) ;
	public String update(AutorizedUserModel autorizedUserModel) ;
	public ContactDetailModel getBookerMobile(Long bookedById);
	public List<GeneralMasterModel> getZoneAsPerCorporate(Long corporateId);
	public List<LocationMasterModel> getLocationAsPerCorporate(Long corporateId, Long zoneId);
	public Long mobileNoisPresent(String mobileNo);
	public Long mobileNoisPresentid(String mobileNo);
}
