package com.operation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.common.model.AddressDetailModel;
import com.operation.dao.BookingMasterDao;
import com.operation.model.BookingDetailModel;
import com.operation.model.BookingMasterModel;
import com.operation.model.DutySlipModel;
import com.operation.model.PassengerDetailModel;
import com.relatedInfo.model.RelatedInfoModel;

@Service("bookingMasterService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class BookingMasterServiceImpl implements BookingMasterService{
	
	@Autowired
	private BookingMasterDao bookingMasterDao;

	public BookingMasterServiceImpl() {
		
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return bookingMasterDao.delete(idForDelete);
	}
	
	@Override
	public BookingMasterModel formFillForEdit(Long formFillForEditId) {
		return bookingMasterDao.formFillForEdit(formFillForEditId);
	}
	
	@Override
	public List<BookingMasterModel> list(String sCriteria, String sValue) {
		return bookingMasterDao.list(sCriteria,sValue);
	}

	@Override
	public List<BookingDetailModel> list(Long lMasterId){
		return bookingMasterDao.list(lMasterId);
	}
	@Override
	public List<BookingMasterModel> listMasterWise(String bookingMasterCode,String searchCriteria) {
		return bookingMasterDao.listMasterWise(bookingMasterCode,searchCriteria);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BookingMasterModel save(BookingMasterModel bookingMasterModel) {
		return bookingMasterDao.save(bookingMasterModel);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BookingMasterModel update(BookingMasterModel bookingMasterModel) {
		return bookingMasterDao.update(bookingMasterModel);
	}

	@Override
	public BookingDetailModel formFillForDetailEdit(Long formFillForEditId) {
		return bookingMasterDao.formFillForDetailEdit(formFillForEditId);
	}

	@Override
	public List<BookingDetailModel> list(String jobType,String[] sCriteriaList, String[] sValueList) {
		return bookingMasterDao.list(jobType,sCriteriaList,sValueList);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String deleteDetailRecord(Long idForDelete){
		return bookingMasterDao.deleteDetailRecord(idForDelete);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BookingDetailModel updateDetail(BookingDetailModel bookingDetailModel) {
		return bookingMasterDao.updateDetail(bookingDetailModel);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public DutySlipModel allocateOrDispatch(DutySlipModel dutySlipModel,String op){
		return bookingMasterDao.allocateOrDispatch(dutySlipModel,op);
	}
	
	@Override
	public DutySlipModel getAllocationDetails(Long bookingDetailId){
		return bookingMasterDao.getAllocationDetails(bookingDetailId);
	}
	@Override
	public List<AddressDetailModel> getAddress(Long bookingDetailId){
		return bookingMasterDao.getAddress( bookingDetailId);
	}
	@Override
	public List<RelatedInfoModel> getEmail(Long bookingDetailId){
		return bookingMasterDao.getEmail( bookingDetailId);
	}
	@Override
	public List<BookingDetailModel> getList(Long lMasterId,String bookingFromDate,String bookingToDate){
		return bookingMasterDao.getList(lMasterId,bookingFromDate,bookingToDate);
	}
	
	@Override
	public List<DutySlipModel> getReservedList(Long lMasterId, String bookingFromDate, String bookingToDate){
		return bookingMasterDao.getReservedList(lMasterId,bookingFromDate,bookingToDate);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String updateBookingNoForWeb(BookingMasterModel bookingMasterModel){
		return bookingMasterDao.updateBookingNoForWeb(bookingMasterModel);
	}
	@Override
	public List<BookingDetailModel> getBookingDetailAsPerBookingMaster(Long bookingMasterId){
		return bookingMasterDao.getBookingDetailAsPerBookingMaster(bookingMasterId);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(PassengerDetailModel passengerDetailModel) {
		return bookingMasterDao.save(passengerDetailModel);
	}
	@Override
	public List<PassengerDetailModel> getPassengerDetails(Long bookingDetailId){
		return bookingMasterDao.getPassengerDetails(bookingDetailId);
	}
	@Override
	public String deletePassengerDetails(Long  id){
		return bookingMasterDao.deletePassengerDetails(id);
	}

	
}
