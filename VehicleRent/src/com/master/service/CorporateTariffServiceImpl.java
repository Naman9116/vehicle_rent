package com.master.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.master.dao.CorporateTariffDao;
import com.master.model.CorporateTariffModel;

@Service("tariffParameterMasterService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CorporateTariffServiceImpl implements CorporateTariffService{
	
	@Autowired
	private CorporateTariffDao corporateTariffMasterDao;

	public CorporateTariffServiceImpl() {}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return corporateTariffMasterDao.delete(idForDelete);
	}
	
	@Override
	public CorporateTariffModel formFillForEdit(Long formFillForEditId) {
		return corporateTariffMasterDao.formFillForEdit(formFillForEditId);
	}
	
	@Override
	public List<CorporateTariffModel> list() {
		return corporateTariffMasterDao.list();
	}
	
	@Override
	public List<CorporateTariffModel> listMasterWise(String searchCriteria) {
		return corporateTariffMasterDao.listMasterWise(searchCriteria);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(CorporateTariffModel tariffParameterMasterModel, String controlsWithValue) {
		return corporateTariffMasterDao.save(tariffParameterMasterModel, controlsWithValue);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(CorporateTariffModel tariffParameterMasterModel,String controlsWithValue) {
		return corporateTariffMasterDao.update(tariffParameterMasterModel,controlsWithValue);
	}

	@Override
	public List<CorporateTariffModel> getCommonTariffParameter_TariffParameterMaster(CorporateTariffModel corporateTariffModel) {
		return corporateTariffMasterDao.getCommonTariffParameter_TariffParameterMaster(corporateTariffModel);
	}
	@Override
	public List<CorporateTariffModel >list(String corporateId,String branchID){
		return corporateTariffMasterDao.list(corporateId,branchID);
	}
	
	@Override
	public List<CorporateTariffModel >getCorporateTariff(Long corporateId){
		return corporateTariffMasterDao.getCorporateTariff(corporateId);
	}
	
	@Override
	public List<CorporateTariffModel >getCorporateFuelHikeDate(Long corporateId, Long branchId){
		return corporateTariffMasterDao.getCorporateFuelHikeDate(corporateId, branchId);
	}
	
	@Override
	public List<CorporateTariffModel >listAsPerDate(Long id){
		return corporateTariffMasterDao.listAsPerDate(id);
	}
}
