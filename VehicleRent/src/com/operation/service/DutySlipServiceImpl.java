package com.operation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.master.model.CarAllocationModel;
import com.operation.dao.DutySlipDao;
import com.operation.model.CarDetailModel;
import com.operation.model.DeallocateHistoryModel;
import com.operation.model.DutySlipModel;
import com.operation.viewModel.BookingDetailsWithVehicleAllocationModel;
import com.operation.viewModel.DutySlipReceiveViewModel;

@Service("dutySlipService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DutySlipServiceImpl implements DutySlipService {

	@Autowired
	private DutySlipDao dutySlipDao;

	public DutySlipServiceImpl() {

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return dutySlipDao.delete(idForDelete);
	}

	@Override
	public DutySlipModel formFillForEdit(Long formFillForEditId) {
		return dutySlipDao.formFillForEdit(formFillForEditId);
	}

	@Override
	public List<BookingDetailsWithVehicleAllocationModel> list(int pageNumber) {
		return dutySlipDao.list(pageNumber);
	}

	@Override
	public List<CarDetailModel> getVehicleTypeData(Long selectCarTypeId, String searchCriteria, Long bookingId) {
		return dutySlipDao.getVehicleTypeData(selectCarTypeId, searchCriteria, bookingId);
	}

	@Override
	public List<BookingDetailsWithVehicleAllocationModel> listMasterWise(int pageNumber, String searchCriteria) {
		return dutySlipDao.listMasterWise(pageNumber, searchCriteria);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(DutySlipModel dutySlipModel) {
		return dutySlipDao.save(dutySlipModel);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(DutySlipModel dutySlipModel) {
		return dutySlipDao.update(dutySlipModel);
	}

	public List getDataOnBookingSelection_DutySlip(Long bookingDetailId) {
		return dutySlipDao.getDataOnBookingSelection_DutySlip(bookingDetailId);
	}

	@Override
	public List<DutySlipReceiveViewModel> listDutySlipReceive(int pageNumber, String searchCriteria) {
		return dutySlipDao.listDutySlipReceive(pageNumber, searchCriteria);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String saveDutySlipReceive(List<DutySlipModel> dutySlipModels) {
		return dutySlipDao.saveDutySlipReceive(dutySlipModels);
	}

	@Override
	public List<DutySlipReceiveViewModel> listDutySlipClose(int pageNumber, String searchCriteria) {
		return dutySlipDao.listDutySlipClose(pageNumber, searchCriteria);
	}

	@Override
	public List getDataOnSelection_DutySlipClose(Long dutySlipId) {
		return dutySlipDao.getDataOnSelection_DutySlipClose(dutySlipId);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String saveDutySlipClose(DutySlipModel dutySlipModel) {
		return dutySlipDao.saveDutySlipClose(dutySlipModel);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String saveDutySlipUnBilled(List<DutySlipModel> dutySlipModels) {
		return dutySlipDao.saveDutySlipUnBilled(dutySlipModels);
	}

	@Override
	public List<DutySlipReceiveViewModel> listDutySlipUnBilled(int pageNumber, String searchCriteria[]) {
		return dutySlipDao.listDutySlipUnBilled(pageNumber, searchCriteria);
	}

	@Override
	public List<DutySlipReceiveViewModel> listDutySlipInvoice(int pageNumber, String searchCriteria[]) {
		return dutySlipDao.listDutySlipInvoice(pageNumber, searchCriteria);
	}

	@Override
	public List calculateFare(String kms, String days, String hours, String tariff) {
		return dutySlipDao.calculateFare(kms, days, hours, tariff);
	}

	@Override
	public DutySlipModel getDSDetails(Long dutySlipDetailsId) {
		return dutySlipDao.getDSDetails(dutySlipDetailsId);
	}

	@Override
	public CarAllocationModel getCarOwnerType(Long carDetailId) {
		return dutySlipDao.getCarOwnerType(carDetailId);
	}

	@Override
	public DutySlipModel getInvoiceNo(Long id) {
		return dutySlipDao.getInvoiceNo(id);
	}

	@Override
	public String getInvoiceNo() {
		return dutySlipDao.getInvoiceNo();
	}
	
	@Override
	public DutySlipModel getDsModel(Long bookingDetailsId){
		return dutySlipDao.getDsModel(bookingDetailsId);
	}
	
	@Override
	public String saveDeallocationModel(DeallocateHistoryModel deallocateHistoryModel){
		return dutySlipDao.saveDeallocationModel(deallocateHistoryModel);
	}
	
	@Override
	public DutySlipModel getDsRowCount(Long booDetId){
		return dutySlipDao.getDsRowCount( booDetId);
	}

	@Override
	public List<DutySlipModel> getlist(Long bookingDetailId, Long carDetailId){
		return dutySlipDao.getlist(bookingDetailId,carDetailId);
	}
}
