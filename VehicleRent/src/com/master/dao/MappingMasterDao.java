package com.master.dao;

import java.util.List;

import com.master.model.MappingMasterModel;

public interface MappingMasterDao {

	public List<MappingMasterModel> list() ;
	
	public List<MappingMasterModel> listMasterWise(Long masterId) ;
	
	public String save(MappingMasterModel mappingMasterModel,String selectedNotMappedCheckBoxValues);
	
	public String remove(MappingMasterModel mappingMasterModel,String selectedAlreadyMappedCheckBoxValues);
	
	public MappingMasterModel formFillForEdit(Long formFillForEditId);
	
	public String delete(Long idForDelete) ;
	
	public String update(MappingMasterModel mappingMasterModel) ;
	
	public Object[] fetchMasterValuesAndSubMasterNamesMappingMaster(Long selectedId);
	
	public Object[] fetchSubMasterValuesMappingMaster(Long selectedId,Long parentId);

}
