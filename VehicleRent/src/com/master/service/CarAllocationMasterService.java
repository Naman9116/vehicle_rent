package com.master.service;

import java.util.List;

import com.master.model.CarAllocationModel;
import com.operation.model.CarDetailModel;

public interface CarAllocationMasterService {
	public List<CarAllocationModel> list() ;
	public CarAllocationModel formFillForEdit(Long formFillForEditId);
	public String save(CarAllocationModel carAllocationModel);
	public String update(CarAllocationModel carAllocationModel);
	public String delete(Long idForDelete) ;
	public CarAllocationModel getCarRelDetailsBasedOnRegNo(String carRegNo);
}
