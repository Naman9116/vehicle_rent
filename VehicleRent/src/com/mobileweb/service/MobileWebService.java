package com.mobileweb.service;

import java.util.List;

import com.corporate.model.AutorizedUserModel;
import com.master.model.ChauffeurModel;
import com.master.model.ChauffeurStatusModel;
import com.operation.model.BookingMasterModel;
import com.operation.model.TripRoutMapModel;

public interface MobileWebService {
	public AutorizedUserModel userAsUserName(String sUserName);
	public List<BookingMasterModel> SearchBooking(Long id,String status,String toDate,String fromDate);
	public BookingMasterModel save(BookingMasterModel   bookingMasterModel);
	public BookingMasterModel update(BookingMasterModel bookingMasterModel);
	public ChauffeurModel driverAsUserName(String sUserName);
	public String updateChStatus(ChauffeurStatusModel chauffeurStatusModel);
	public String saveChStatus(ChauffeurStatusModel chauffeurStatusModel);
	public ChauffeurStatusModel getChauffeurStatusModel(Long id);
	public List<BookingMasterModel> getBookingAsPerChauffeurId(Long chauffeurId,String flag) ;
	public String[] getChauffeurDutyCount(Long chauffeurId, String flag);
	public String saveTripRouteMap(TripRoutMapModel tripRoutMapModel);
	
}