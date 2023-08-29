package com.master.service;

import java.util.List;

import com.master.model.CorporateTaxDetModel;
import com.master.model.TaxationModel;

public interface TaxationMasterService {
	public List<TaxationModel> list() ;
	public TaxationModel formFillForEdit(Long formFillForEditId);

	public String save(TaxationModel taxationModel);
	public String update(TaxationModel taxationModel);
	public String delete(Long idForDelete) ;
	
	public String saveCorpTaxDetail(CorporateTaxDetModel corporateTaxDetModel);
	public String updateCorpTaxDetail(CorporateTaxDetModel corporateTaxDetModel) ;
}
