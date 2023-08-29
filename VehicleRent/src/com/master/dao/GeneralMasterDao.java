package com.master.dao;

import java.util.List;

import com.master.model.GeneralMasterModel;

public interface GeneralMasterDao {

	public List<GeneralMasterModel> list() ;
	
	public List<GeneralMasterModel> listMasterWise(Long masterId,String masterCode) ;
	
	public String save(GeneralMasterModel generalMasterModel, String masterCode);
	
	public GeneralMasterModel formFillForEdit(Long formFillForEditId, String masterCode);
	
	public String delete(Long idForDelete) ;
	
	public String update(GeneralMasterModel generalMasterModel, String masterCode) ;
	
	public Long saveGeneralMaster(GeneralMasterModel generalMasterModel);
	
	public String setSortOrder(Long masterId, String sortedArr) ;
}
