package com.operation.service;

import java.util.Date;
import java.util.List;

import com.operation.model.CarDetailModel;
import com.operation.model.DutySlipModel;

public interface CarDetailService {

	public List<CarDetailModel> list() ;

	public List<CarDetailModel> listMasterWise(String carDetailCode,String searchCriteria) ;
	
	public String save(CarDetailModel carDetailModel);
	
	public CarDetailModel formFillForEdit(Long formFillForEditId);
	
	public String delete(Long idForDelete) ;
	
	public String update(CarDetailModel carDetailModel) ;

	public CarDetailModel getCarDetailsBasedOnRegNo(String carRegNo);
	
	public List<CarDetailModel> getUnReservedCarlist(Date reserveDateTime, Long carSegment);
	
	public List<DutySlipModel> searchDutySlip(String bookingFromDate,String bookingToDate,String flag);
	
	public List<CarDetailModel> searchCarDetailsNotInDutySlip(String bookingFromDate,String bookingToDate);
	
}
