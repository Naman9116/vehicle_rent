package com.master.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.master.dao.ChauffeurMasterDao;
import com.master.model.ChauffeurModel;

@Service("chauffeurMasterService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ChauffeurMasterServiceImpl implements ChauffeurMasterService{
	
	@Autowired
	private ChauffeurMasterDao chauffeurMasterDao;

	public ChauffeurMasterServiceImpl() {}
	
	@Override
	public List<ChauffeurModel> list() {
		return chauffeurMasterDao.list();
	}

	@Override
	public ChauffeurModel formFillForEdit(Long formFillForEditId) {
		return chauffeurMasterDao.formFillForEdit(formFillForEditId);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(ChauffeurModel chauffeurModel) {
		return chauffeurMasterDao.save(chauffeurModel);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(ChauffeurModel chauffeurModel) {
		return chauffeurMasterDao.update(chauffeurModel);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return chauffeurMasterDao.delete(idForDelete);
	}
	
	@Override
	public ChauffeurModel formFillForEditChauffeurDetails(Long formFillForEditId, String formFillForEditMob) {
		return chauffeurMasterDao.formFillForEditChauffeurDetails(formFillForEditId,formFillForEditMob);
	}
}
