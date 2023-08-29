package com.master.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.master.dao.VendorMasterDao;
import com.master.model.VendorModel;

@Service("vendorMasterService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class VendorMasterServiceImpl implements VendorMasterService{
	
	@Autowired
	private VendorMasterDao vendorMasterDao;

	public VendorMasterServiceImpl() {}
	
	@Override
	public List<VendorModel> list() {
		return vendorMasterDao.list();
	}

	@Override
	public VendorModel formFillForEdit(Long formFillForEditId) {
		return vendorMasterDao.formFillForEdit(formFillForEditId);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(VendorModel vendorModel) {
		return vendorMasterDao.save(vendorModel);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(VendorModel vendorModel) {
		return vendorMasterDao.update(vendorModel);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return vendorMasterDao.delete(idForDelete);
	}
}
