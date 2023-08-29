package com.corporate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.corporate.dao.CorporateDao;
import com.corporate.model.CorporateModel;
import com.master.model.CorporateTaxDetModel;

@Service("corporateService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CorporateServiceImpl implements CorporateService{
	
	@Autowired
	private CorporateDao corporateDao;

	public CorporateServiceImpl() {
		
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return corporateDao.delete(idForDelete);
	}
	
	@Override
	public CorporateModel formFillForEdit(Long formFillForEditId) {
		return corporateDao.formFillForEdit(formFillForEditId);
	}
	
	@Override
	public List<CorporateModel> list() {
		return corporateDao.list();
	}
	
	@Override
	public List<CorporateModel> listMasterWise(String searchCriteria) {
		return corporateDao.listMasterWise(searchCriteria);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(CorporateModel corporateModel) {
		return corporateDao.save(corporateModel);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(CorporateModel corporateModel) {
		return corporateDao.update(corporateModel);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(CorporateTaxDetModel corporateTaxDetModel) {
		return corporateDao.save(corporateTaxDetModel);
	}
	
	@Override
	public List<CorporateTaxDetModel> formFillForEditCT(Long formFillForEditId) {
		return corporateDao.formFillForEditCT(formFillForEditId);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(CorporateTaxDetModel corporateTaxDetModel) {
		return corporateDao.update(corporateTaxDetModel);
	}
}
