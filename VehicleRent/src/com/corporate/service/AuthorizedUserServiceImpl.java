package com.corporate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.common.model.ContactDetailModel;
import com.corporate.dao.AuthorizedUserDao;
import com.corporate.model.AutorizedUserModel;
import com.ets.model.LocationMasterModel;
import com.master.model.GeneralMasterModel;

@Service("authorizedUserService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class AuthorizedUserServiceImpl implements AuthorizedUserService{
	
	@Autowired
	private AuthorizedUserDao authorizedUserDao;

	public AuthorizedUserServiceImpl() {
		
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return authorizedUserDao.delete(idForDelete);
	}
	
	@Override
	public AutorizedUserModel formFillForEdit(Long formFillForEditId) {
		return authorizedUserDao.formFillForEdit(formFillForEditId);
	}
	
	@Override
	public List<AutorizedUserModel> list(String pageFor,Long corporateId) {
		return authorizedUserDao.list(pageFor,corporateId);
	}
	
	@Override
	public List<AutorizedUserModel> listMasterWise(String searchCriteria) {
		return authorizedUserDao.listMasterWise(searchCriteria);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(AutorizedUserModel autorizedUserModel) {
		return authorizedUserDao.save(autorizedUserModel);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(AutorizedUserModel autorizedUserModel) {
		return authorizedUserDao.update(autorizedUserModel);
	}
	
	@Override
	public ContactDetailModel getBookerMobile(Long bookedById){
		return authorizedUserDao.getBookerMobile(bookedById);
	}
	
	@Override
	public List<LocationMasterModel> getLocationAsPerCorporate(Long corporateId, Long zoneId){
		return authorizedUserDao.getLocationAsPerCorporate(corporateId,zoneId);
	}
	
	@Override
	public List<GeneralMasterModel> getZoneAsPerCorporate(Long corporateId){
		return authorizedUserDao.getZoneAsPerCorporate(corporateId);
	}
	
}
