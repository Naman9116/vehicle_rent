package com.operation.service;

import java.util.List;

import com.master.model.CarAllocationModel;
import com.operation.model.CarDetailModel;
import com.operation.model.DeallocateHistoryModel;
import com.operation.model.DutySlipModel;
import com.operation.viewModel.BookingDetailsWithVehicleAllocationModel;
import com.operation.viewModel.DutySlipReceiveViewModel;

public interface DutySlipService {
	public List<BookingDetailsWithVehicleAllocationModel> list(int pageNumber) ;
	public List<BookingDetailsWithVehicleAllocationModel> listMasterWise(int pageNumber,String searchCriteria);
	public List<CarDetailModel> getVehicleTypeData(Long selectCarTypeId,String searchCriteria,Long bookingId) ;
	public List getDataOnBookingSelection_DutySlip(Long bookingDetailId);
	public List<DutySlipReceiveViewModel> listDutySlipReceive(int pageNumber,String searchCriteria);
	public String saveDutySlipReceive(List<DutySlipModel> dutySlipModels);
	public List<DutySlipReceiveViewModel> listDutySlipClose(int pageNumber,String searchCriteria);
	public List getDataOnSelection_DutySlipClose(Long dutySlipId);
	public String saveDutySlipClose(DutySlipModel dutySlipModel);
	public List calculateFare(String kms,String days,String hours,String tariff); 
	public List<DutySlipReceiveViewModel> listDutySlipUnBilled(int pageNumber,String searchCriteria[]);
	public List<DutySlipReceiveViewModel> listDutySlipInvoice(int pageNumber,String searchCriteria[]);
	public DutySlipModel getDSDetails(Long dutySlipDetailsId);
	public CarAllocationModel getCarOwnerType(Long carDetailId);
	public DutySlipModel getInvoiceNo(Long id);
	public String getInvoiceNo();
	public DutySlipModel getDsModel(Long bookingDetailsId);
	public DutySlipModel getDsRowCount(Long booDetId);
	public List<DutySlipModel> getlist(Long bookingDetailId,Long carDetailId);		

	public String save(DutySlipModel dutySlipModel);
	public DutySlipModel formFillForEdit(Long formFillForEditId);
	public String delete(Long idForDelete) ;
	public String update(DutySlipModel dutySlipModel) ;
	public String saveDutySlipUnBilled(List<DutySlipModel> dutySlipModels);
	public String saveDeallocationModel(DeallocateHistoryModel deallocateHistoryModel);
	
}
