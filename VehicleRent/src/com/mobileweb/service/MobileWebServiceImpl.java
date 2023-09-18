package com.mobileweb.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.corporate.model.AutorizedUserModel;
import com.master.model.ChauffeurModel;
import com.master.model.ChauffeurStatusModel;
import com.mobileweb.dao.MobileWebDao;
import com.operation.model.BookingMasterModel;
import com.operation.model.TripRoutMapModel;

@Service("mobileWebService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class MobileWebServiceImpl implements MobileWebService {

	@Autowired
	private MobileWebDao  mobileWebDao;

	public MobileWebServiceImpl() {}

	@Override
	public AutorizedUserModel userAsUserName(String sUserName) {
		return mobileWebDao.userAsUserName(sUserName);
	}
	@Override
	public List<BookingMasterModel> SearchBooking(Long id, String status,String toDate, String fromDate){
		return mobileWebDao.SearchBooking(id, status,toDate,fromDate);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BookingMasterModel save(BookingMasterModel   bookingMasterModel){
		return mobileWebDao.save(bookingMasterModel);
	}

	@Override
	public ChauffeurModel driverAsUserName(String sUserName) {
		return mobileWebDao.driverAsUserName(sUserName);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String saveChStatus(ChauffeurStatusModel chauffeurStatusModel) {
		return mobileWebDao.saveChStatus(chauffeurStatusModel);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String updateChStatus(ChauffeurStatusModel chauffeurStatusModel) {
		return mobileWebDao.updateChStatus(chauffeurStatusModel);
	}
	
	@Override
	public ChauffeurStatusModel getChauffeurStatusModel(Long id){
		return mobileWebDao.getChauffeurStatusModel(id);
	}

	@Override
	public List<BookingMasterModel> getBookingAsPerChauffeurId(Long chauffeurId,String flag) {
		return mobileWebDao.getBookingAsPerChauffeurId(chauffeurId, flag);
	}

	@Override
	public String[] getChauffeurDutyCount(Long chauffeurId, String flag) {
		return mobileWebDao.getChauffeurDutyCount(chauffeurId, flag);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String saveTripRouteMap(TripRoutMapModel tripRoutMapModel) {
		return mobileWebDao.saveTripRouteMap(tripRoutMapModel);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BookingMasterModel update(BookingMasterModel bookingMasterModel) {
		return mobileWebDao.update(bookingMasterModel);
	}

	
}
