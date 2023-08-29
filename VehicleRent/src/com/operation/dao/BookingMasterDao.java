package com.operation.dao;

import java.util.List;

import com.common.model.AddressDetailModel;
import com.operation.model.BookingDetailModel;
import com.operation.model.BookingMasterModel;
import com.operation.model.DutySlipModel;
import com.operation.model.PassengerDetailModel;
import com.relatedInfo.model.RelatedInfoModel;

public interface BookingMasterDao {
	public List<BookingMasterModel> list(String sCriteria, String sValue) ;
	public List<BookingDetailModel> list(Long lMasterId);
	public List<BookingMasterModel> listMasterWise(String bookingMasterCode,String searchCriteria) ;
	public BookingMasterModel save(BookingMasterModel bookingMasterModel);
	public BookingMasterModel formFillForEdit(Long formFillForEditId);
	public String delete(Long idForDelete) ;
	public BookingMasterModel update(BookingMasterModel bookingMasterModel) ;
	public BookingDetailModel formFillForDetailEdit(Long formFillForEditId);
	public List<BookingDetailModel> list(String jobType, String[] sCriteriaList, String[] sValueList);
	public BookingDetailModel updateDetail(BookingDetailModel bookingDetailModel);
	public String deleteDetailRecord(Long idForDelete);
	public DutySlipModel allocateOrDispatch(DutySlipModel dutySlipModel, String op);
	public DutySlipModel getAllocationDetails(Long bookingDetailId);
	public List<AddressDetailModel> getAddress(Long bookingDetailId);
	public List<RelatedInfoModel> getEmail(Long bookingDetailId);
	public List<BookingDetailModel> getList(Long lMasterId,String bookingFromDate,String bookingToDate);
	public List<DutySlipModel> getReservedList(Long lMasterId, String bookingFromDate, String bookingToDate);
	public String updateBookingNoForWeb(BookingMasterModel bookingMasterModel);
	public List<BookingDetailModel> getBookingDetailAsPerBookingMaster(Long bookingMasterId);
	public String save(PassengerDetailModel   passengerDetailModel) ;
	public List<PassengerDetailModel> getPassengerDetails(Long bookingDetailId);
	public String deletePassengerDetails(Long  id);
}
