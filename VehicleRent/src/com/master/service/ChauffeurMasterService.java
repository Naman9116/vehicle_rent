package com.master.service;

import java.util.List;

import com.master.model.ChauffeurModel;

public interface ChauffeurMasterService {
	public List<ChauffeurModel> list() ;
	public ChauffeurModel formFillForEdit(Long formFillForEditId);
	public String save(ChauffeurModel vendorModel);
	public String update(ChauffeurModel vendorModel);
	public String delete(Long idForDelete) ;
	public ChauffeurModel formFillForEditChauffeurDetails(Long formFillForEditId, String formFillForEditMob);
}
