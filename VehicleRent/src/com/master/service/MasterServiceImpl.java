package com.master.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.master.dao.MasterDao;
import com.master.model.MasterModel;

@Service("MasterService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class MasterServiceImpl implements MasterService {
	
	@Autowired
	private MasterDao masterDao;

	public MasterServiceImpl() {
		
	}

	@Override
	public List<MasterModel> list(int pageNumber) {
		return masterDao.list(pageNumber);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(MasterModel masterModel) {
		return masterDao.save(masterModel);
	}

	@Override
	public MasterModel formFillForEdit(Long formFillForEditId) {
		return masterDao.formFillForEdit(formFillForEditId);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return masterDao.delete(idForDelete);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(MasterModel masterModel) {
		return masterDao.update(masterModel);
	}
}
