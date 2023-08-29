package com.master.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.master.dao.TaxationMasterDao;
import com.master.model.CorporateTaxDetModel;
import com.master.model.TaxationModel;

@Service("taxationMasterService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class TaxationMasterServiceImpl implements TaxationMasterService{
	
	@Autowired
	private TaxationMasterDao taxationMasterDao;

	public TaxationMasterServiceImpl() {}
	
	@Override
	public List<TaxationModel> list() {
		return taxationMasterDao.list();
	}

	@Override
	public TaxationModel formFillForEdit(Long formFillForEditId) {
		return taxationMasterDao.formFillForEdit(formFillForEditId);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(TaxationModel taxationModel) {
		return taxationMasterDao.save(taxationModel);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(TaxationModel taxationModel) {
		return taxationMasterDao.update(taxationModel);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return taxationMasterDao.delete(idForDelete);
	}
	@Override
	public String saveCorpTaxDetail(CorporateTaxDetModel corporateTaxDetModel){
		return taxationMasterDao.saveCorpTaxDetail(corporateTaxDetModel);
	}
	@Override
	public String updateCorpTaxDetail(CorporateTaxDetModel corporateTaxDetModel) {
		return taxationMasterDao.updateCorpTaxDetail(corporateTaxDetModel);
	}
}
