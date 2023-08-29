package com.toursandtravels.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.common.model.AddressDetailModel;
import com.master.model.GeneralMasterModel;
import com.relatedInfo.model.RelatedInfoModel;
import com.toursandtravels.dao.BookingDao;
import com.toursandtravels.model.BookingModel;
import com.toursandtravels.model.CustomerModel;
import com.toursandtravels.model.TDutySlipModel;


@Service("bookingService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class BookingServiceImpl implements BookingService {
	@Autowired
	private BookingDao bookingDao;
	public BookingServiceImpl() {}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BookingModel save(BookingModel   bookingModel) {
		return bookingDao.save(bookingModel);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(BookingModel   bookingModel){
		return bookingDao.update(bookingModel);
	}
	
	@Override
	public List<BookingModel> getBooking()  {
		return bookingDao.getBooking();
	}
	
	@Override
	public BookingModel formFillForEditBooking(Long id)  {
		return bookingDao.formFillForEditBooking(id);
	}
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return bookingDao.delete(idForDelete);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public CustomerModel getCustomerModelAsPerMobile(String mobileNo){
		return bookingDao.getCustomerModelAsPerMobile(mobileNo);
	}
	@Override
	public List<GeneralMasterModel> getTarrifAsPerRentleType(long id){
		return bookingDao.getTarrifAsPerRentleType(id);
	}

	@Override
	public BookingModel getBookingModelAsPerMobile(String mobileNo){
		return bookingDao.getBookingModelAsPerMobile(mobileNo);
	}
	@Override
	public	List<BookingModel> list(String jobType,String[] sCriteriaList, String[] sValueList){
		return bookingDao.list(jobType,sCriteriaList,sValueList);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public TDutySlipModel allocateOrDispatch(TDutySlipModel tDutySlipModel, String op){
		return bookingDao.allocateOrDispatch(tDutySlipModel,op);
	}
	@Override
	public TDutySlipModel  getDsRowCount(Long booDetId){
		return bookingDao.getDsRowCount(booDetId);
	}
	@Override
	public List<AddressDetailModel> getAddress(Long bookingId){
		return bookingDao.getAddress(bookingId);
	}
	@Override
	public List<RelatedInfoModel> getEmail(Long bookingId){
		return bookingDao.getEmail(bookingId);
	}
	@Override
	public List<TDutySlipModel> getlist(Long bookingId, Long carDetailId){
		return bookingDao.getlist(bookingId,carDetailId);
	}
	
}
