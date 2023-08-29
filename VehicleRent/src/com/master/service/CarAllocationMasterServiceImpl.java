package com.master.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.master.dao.CarAllocationMasterDao;
import com.master.model.CarAllocationModel;
import com.operation.model.CarDetailModel;

@Service("carAllocationMasterService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CarAllocationMasterServiceImpl implements CarAllocationMasterService{
	
	@Autowired
	private CarAllocationMasterDao carAllocationMasterDao;

	public CarAllocationMasterServiceImpl() {}
	
	@Override
	public List<CarAllocationModel> list() {
		return carAllocationMasterDao.list();
	}

	@Override
	public CarAllocationModel formFillForEdit(Long formFillForEditId) {
		return carAllocationMasterDao.formFillForEdit(formFillForEditId);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(CarAllocationModel carAllocationModel) {
		return carAllocationMasterDao.save(carAllocationModel);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(CarAllocationModel carAllocationModel) {
		return carAllocationMasterDao.update(carAllocationModel);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return carAllocationMasterDao.delete(idForDelete);
	}
	
	@Override
	@Transactional(readOnly = true)
	public CarAllocationModel getCarRelDetailsBasedOnRegNo(String carRegNo){
		return carAllocationMasterDao.getCarRelDetailsBasedOnRegNo(carRegNo);
	}
}
