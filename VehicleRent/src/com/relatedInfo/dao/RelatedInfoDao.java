package com.relatedInfo.dao;

import java.util.List;

import com.relatedInfo.model.RelatedInfoModel;

public interface RelatedInfoDao {

	public List<RelatedInfoModel> list(String pageFor) ;
	
	public List<RelatedInfoModel> listMasterWise(String searchCriteria,String pageFor) ;
	
	public String save(RelatedInfoModel relatedInfoModel);
	
	public RelatedInfoModel formFillForEdit(Long formFillForEditId);
	
	public String delete(Long idForDelete) ;
	
	public String update(RelatedInfoModel relatedInfoModel) ;

}
