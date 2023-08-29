package com.master.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.master.dao.MappingMasterDao;
import com.master.model.MappingMasterModel;

@Service("mappingMasterService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class MappingMasterServiceImpl implements MappingMasterService{
	
	@Autowired
	private MappingMasterDao mappingMasterDao;

	public MappingMasterServiceImpl() {
		
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return mappingMasterDao.delete(idForDelete);
	}
	
	@Override
	public MappingMasterModel formFillForEdit(Long formFillForEditId) {
		return mappingMasterDao.formFillForEdit(formFillForEditId);
	}
	
	@Override
	public List<MappingMasterModel> list() {
		return mappingMasterDao.list();
	}
	
	@Override
	public List<MappingMasterModel> listMasterWise(Long masterId) {
		return mappingMasterDao.listMasterWise(masterId);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(MappingMasterModel mappingMasterModel,String selectedNotMappedCheckBoxValues) {
		return mappingMasterDao.save(mappingMasterModel,selectedNotMappedCheckBoxValues);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(MappingMasterModel mappingMasterModel) {
		return mappingMasterDao.update(mappingMasterModel);
	}
	
	@Override
	public Object[] fetchMasterValuesAndSubMasterNamesMappingMaster(Long selectedId){
		return mappingMasterDao.fetchMasterValuesAndSubMasterNamesMappingMaster(selectedId);
	}

	@Override
	public Object[] fetchSubMasterValuesMappingMaster(Long selectedId,Long parentId){
		return mappingMasterDao.fetchSubMasterValuesMappingMaster(selectedId,parentId);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String remove(MappingMasterModel mappingMasterModel,String selectedAlreadyMappedCheckBoxValues) {
		return mappingMasterDao.remove(mappingMasterModel,selectedAlreadyMappedCheckBoxValues);
	}

}
