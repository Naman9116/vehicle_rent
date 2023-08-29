package com.master.dao;

import java.util.List;

import com.master.model.CarAllocationModel;
import com.operation.model.CarDetailModel;

public interface CarAllocationMasterDao {
	public List<CarAllocationModel> list() ;
	public CarAllocationModel formFillForEdit(Long formFillForEditId);
	public String save(CarAllocationModel carAllocationModel);
	public String update(CarAllocationModel carAllocationModel);
	public String delete(Long idForDelete) ;
	public CarAllocationModel getCarRelDetailsBasedOnRegNo(String carRegNo);
}
