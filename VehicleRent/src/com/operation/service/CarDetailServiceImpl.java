package com.operation.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.operation.dao.CarDetailDao;
import com.operation.model.CarDetailModel;
import com.operation.model.DutySlipModel;

@Service("carDetailService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CarDetailServiceImpl implements CarDetailService{
	
	@Autowired
	private CarDetailDao carDetailDao;

	public CarDetailServiceImpl() {
		
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return carDetailDao.delete(idForDelete);
	}
	
	@Override
	public CarDetailModel formFillForEdit(Long formFillForEditId) {
		return carDetailDao.formFillForEdit(formFillForEditId);
	}
	
	@Override
	public List<CarDetailModel> list() {
		return carDetailDao.list();
	}
	
	@Override
	public List<CarDetailModel> listMasterWise(String carDetailCode,String searchCriteria) {
		return carDetailDao.listMasterWise(carDetailCode,searchCriteria);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(CarDetailModel carDetailModel) {
		return carDetailDao.save(carDetailModel);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(CarDetailModel carDetailModel) {
		return carDetailDao.update(carDetailModel);
	}
	
	@Override
	@Transactional(readOnly = true)
	public CarDetailModel getCarDetailsBasedOnRegNo(String carRegNo){
		return carDetailDao.getCarDetailsBasedOnRegNo(carRegNo);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CarDetailModel> getUnReservedCarlist(Date reserveDateTime, Long carSegment){
		return carDetailDao.getUnReservedCarlist(reserveDateTime, carSegment);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DutySlipModel> searchDutySlip(String bookingFromDate,String bookingToDate,String flag){
		return carDetailDao.searchDutySlip(bookingFromDate, bookingToDate, flag);
	}
	@Override
	@Transactional(readOnly = true)
	public List<CarDetailModel> searchCarDetailsNotInDutySlip(String bookingFromDate,String bookingToDate){
		return carDetailDao.searchCarDetailsNotInDutySlip(bookingFromDate, bookingToDate);
	}
}
