package com.master.dao;

import java.util.List;

import com.master.model.MasterModel;
import com.operation.model.CarDetailModel;

public interface MasterDao {
	
	public List<MasterModel> list(int pageNumber) ;
	
	public String save(MasterModel cityMasterModel);
	
	public MasterModel formFillForEdit(Long formFillForEditId);
	
	public String delete(Long idForDelete) ;
	
	public String update(MasterModel masterModel) ;

}
