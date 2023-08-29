package com.master.dao;

import java.util.List;

import com.master.model.ChauffeurModel;

public interface ChauffeurMasterDao {
	public List<ChauffeurModel> list() ;
	public ChauffeurModel formFillForEdit(Long formFillForEditId);
	public String save(ChauffeurModel chauffeurModel);
	public String update(ChauffeurModel chauffeurModel);
	public String delete(Long idForDelete) ;
	public ChauffeurModel formFillForEditChauffeurDetails(Long formFillForEditId, String formFillForEditMob);
}
