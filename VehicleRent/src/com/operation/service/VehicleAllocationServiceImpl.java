package com.operation.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.operation.dao.VehicleAllocationDao;
import com.operation.model.BookingMasterModel;
import com.operation.model.CarDetailModel;
import com.operation.model.VehicleAllocationModel;

@Service("vehicleAllocationService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class VehicleAllocationServiceImpl implements VehicleAllocationService{
	
	@Autowired
	private VehicleAllocationDao vehicleAllocationDao;

	public VehicleAllocationServiceImpl() {
		
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return vehicleAllocationDao.delete(idForDelete);
	}
	
	@Override
	public VehicleAllocationModel formFillForEdit(Long formFillForEditId) {
		return vehicleAllocationDao.formFillForEdit(formFillForEditId);
	}
	
	@Override
	public List<BookingMasterModel> list(int pageNumber) {
		return vehicleAllocationDao.list(pageNumber);
	}
	
	@Override
	public List<CarDetailModel> getVehicleTypeData(Long selectCarTypeId,String searchCriteria,Long bookingId){
		return vehicleAllocationDao.getVehicleTypeData(selectCarTypeId,searchCriteria,bookingId);
	}
	@Override
	public List<BookingMasterModel> listMasterWise(int pageNumber,String searchCriteria){
		return vehicleAllocationDao.listMasterWise(pageNumber,searchCriteria);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(VehicleAllocationModel vehicleAllocationModel,String vehicleIdSelectedValue) {
		return vehicleAllocationDao.save(vehicleAllocationModel,vehicleIdSelectedValue);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(VehicleAllocationModel vehicleAllocationModel) {
		return vehicleAllocationDao.update(vehicleAllocationModel);
	}
}
