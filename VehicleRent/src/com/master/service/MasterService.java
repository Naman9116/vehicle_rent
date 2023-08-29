package com.master.service;

import java.util.List;
import com.master.model.MasterModel;

public interface MasterService {
	
	public List<MasterModel> list(int pageNumber) ;
	
	public String save(MasterModel cityMasterModel);
	
	public MasterModel formFillForEdit(Long formFillForEditId);
	
	public String delete(Long idForDelete) ;
	
	public String update(MasterModel cityMasterModel) ;

}
