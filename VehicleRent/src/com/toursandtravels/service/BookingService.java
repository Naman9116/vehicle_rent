package com.toursandtravels.service;
import java.util.List;

import com.common.model.AddressDetailModel;
import com.master.model.GeneralMasterModel;
import com.relatedInfo.model.RelatedInfoModel;
import com.toursandtravels.model.BookingModel;
import com.toursandtravels.model.CustomerModel;
import com.toursandtravels.model.TDutySlipModel;
public interface BookingService {


	public BookingModel save(BookingModel   bookingModel);
	public String update(BookingModel   bookingModel);
	public List<BookingModel> getBooking() ;
	public BookingModel formFillForEditBooking(Long id);
	public String delete(Long idForDelete) ;
	public CustomerModel getCustomerModelAsPerMobile(String mobileNo);
	public List<GeneralMasterModel> getTarrifAsPerRentleType(long id);
	public BookingModel getBookingModelAsPerMobile(String mobileNo);
	public	List<BookingModel> list(String jobType,String[] sCriteriaList, String[] sValueList);
	public TDutySlipModel allocateOrDispatch(TDutySlipModel tDutySlipModel, String op);
	public TDutySlipModel  getDsRowCount(Long booDetId);
	public List<AddressDetailModel> getAddress(Long bookingId) ;
	public List<RelatedInfoModel> getEmail(Long bookingId);
	public List<TDutySlipModel> getlist(Long bookingId, Long carDetailId);

}
