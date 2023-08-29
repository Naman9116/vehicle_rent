package com.master.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.master.dao.GeneralMasterDao;
import com.master.model.GeneralMasterModel;

@Service("generalMasterService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GeneralMasterServiceImpl implements GeneralMasterService{
	
	@Autowired
	private GeneralMasterDao generalMasterDao;

	public GeneralMasterServiceImpl() {
		
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return generalMasterDao.delete(idForDelete);
	}
	
	@Override
	public GeneralMasterModel formFillForEdit(Long formFillForEditId, String masterCode) {
		return generalMasterDao.formFillForEdit(formFillForEditId,masterCode);
	}
	
	@Override
	public List<GeneralMasterModel> list() {
		return generalMasterDao.list();
	}
	
	@Override
	public List<GeneralMasterModel> listMasterWise(Long masterId,String masterCode) {
		return generalMasterDao.listMasterWise(masterId,masterCode);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(GeneralMasterModel generalMasterModel, String masterCode) {
		return generalMasterDao.save(generalMasterModel,masterCode);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(GeneralMasterModel generalMasterModel, String masterCode) {
		return generalMasterDao.update(generalMasterModel, masterCode);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Long saveGeneralMaster(GeneralMasterModel generalMasterModel) {
		return generalMasterDao.saveGeneralMaster(generalMasterModel);
	}

	@Override
	public String setSortOrder(Long masterId, String sortedArr) {
		return generalMasterDao.setSortOrder(masterId,sortedArr);
	}
}
