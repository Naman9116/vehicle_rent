package com.corporate.service;

import java.util.List;

import com.corporate.model.CorporateModel;
import com.master.model.CorporateTaxDetModel;

public interface CorporateService {

	public List<CorporateModel> list() ;

	public List<CorporateModel> listMasterWise(String searchCriteria) ;
	
	public String save(CorporateModel corporateModel);
	
	public CorporateModel formFillForEdit(Long formFillForEditId);
	
	public String delete(Long idForDelete) ;
	
	public String update(CorporateModel corporateModel) ;
	
	public String save(CorporateTaxDetModel corporateTaxDetModel);
	
	public List<CorporateTaxDetModel> formFillForEditCT(Long formFillForEditId);
	
	public String update(CorporateTaxDetModel corporateTaxDetModel) ;

	public Long mobileNoisPresent(String mobileNo);

	public Long mobileNoIdPresent(String mobileNo);
	
		
	
}
