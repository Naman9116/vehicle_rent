package com.operation.service;

import java.util.List;
import com.operation.model.BookingMasterModel;
import com.operation.model.CarDetailModel;
import com.operation.model.VehicleAllocationModel;

public interface VehicleAllocationService {

	public List<BookingMasterModel> list(int pageNumber) ;

	public List<BookingMasterModel> listMasterWise(int pageNumber,String searchCriteria);
	
	public List<CarDetailModel> getVehicleTypeData(Long selectCarTypeId,String searchCriteria,Long bookingId) ;
	
	public String save(VehicleAllocationModel vehicleAllocationModel,String vehicleIdSelectedValue);
	
	public VehicleAllocationModel formFillForEdit(Long formFillForEditId);
	
	public String delete(Long idForDelete) ;
	
	public String update(VehicleAllocationModel vehicleAllocationModel) ;

}
