package com.relatedInfo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.relatedInfo.dao.RelatedInfoDao;
import com.relatedInfo.model.RelatedInfoModel;

@Service("relatedInfoService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class RelatedInfoServiceImpl implements RelatedInfoService{
	
	@Autowired
	private RelatedInfoDao relatedInfoDao;

	public RelatedInfoServiceImpl() {
		
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return relatedInfoDao.delete(idForDelete);
	}
	
	@Override
	public RelatedInfoModel formFillForEdit(Long formFillForEditId) {
		return relatedInfoDao.formFillForEdit(formFillForEditId);
	}
	
	@Override
	public List<RelatedInfoModel> list(String pageFor) {
		return relatedInfoDao.list(pageFor);
	}
	
	@Override
	public List<RelatedInfoModel> listMasterWise(String searchCriteria,String pageFor) {
		return relatedInfoDao.listMasterWise(searchCriteria,pageFor);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(RelatedInfoModel relatedInfoModel) {
		return relatedInfoDao.save(relatedInfoModel);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(RelatedInfoModel relatedInfoModel) {
		return relatedInfoDao.update(relatedInfoModel);
	}
}
