package com.ets.service;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.corporate.model.AutorizedUserModel;
import com.ets.dao.RosterMasterDao;
import com.ets.model.RosterMasterModel;
import com.master.model.GeneralMasterModel;


@Service("rosterMasterService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class RosterMasterServiceImpl implements RosterMasterService {
	@Autowired
	private RosterMasterDao  rosterMasterDao;
	
	public RosterMasterServiceImpl() {}
	

	@Override
	public AutorizedUserModel fillClientAsPerMobile(String mob, Long corporateId){
		return rosterMasterDao.fillClientAsPerMobile(mob, corporateId);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(RosterMasterModel rosterMasterModel){
		return rosterMasterDao.save(rosterMasterModel);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(RosterMasterModel rosterMasterModel){
		return rosterMasterDao.update(rosterMasterModel);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<RosterMasterModel> listRoster(){
		return rosterMasterDao.listRoster();
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public GeneralMasterModel getCarModelName(Long id){
		return rosterMasterDao.getCarModelName(id);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public RosterMasterModel formFillForEditRosterMaster(long id){
		return rosterMasterDao.formFillForEditRosterMaster(id);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long id){
		return rosterMasterDao.delete(id);
	}
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<RosterMasterModel> fillRosterDetails(Long corporateId, Long branch,
			Long outlet, Long bookedBy, Long shiftTime, String rosterDate,	Long rosterTakenBy) throws ParseException{
		return rosterMasterDao.fillRosterDetails(corporateId,branch,outlet,bookedBy,shiftTime,rosterDate,rosterTakenBy);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<AutorizedUserModel> listAutorizedUsersByRoster(Long corporateId,Long branch,Long outlet,Long bookedBy,Long shiftTiming,String rosterTaken,String routeNumber,String rosterFromDate,String rosterToDate){
		return rosterMasterDao.listAutorizedUsersByRoster(corporateId,branch,outlet,bookedBy,shiftTiming,rosterTaken,routeNumber,rosterFromDate,rosterToDate);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<HashMap<String,String>> listCarsByRoster(Long corporateId,Long branch,Long outlet,Long bookedBy,Long shiftTiming,String rosterTaken,String routeNumber,String rosterFromDate,String rosterToDate){
		return rosterMasterDao.listCarsByRoster(corporateId,branch,outlet,bookedBy,shiftTiming,rosterTaken,routeNumber,rosterFromDate,rosterToDate);
	}
}
