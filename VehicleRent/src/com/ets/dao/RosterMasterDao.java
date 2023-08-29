package com.ets.dao;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import com.corporate.model.AutorizedUserModel;
import com.ets.model.RosterMasterModel;
import com.master.model.GeneralMasterModel;
public interface RosterMasterDao {
	public AutorizedUserModel fillClientAsPerMobile(String mob, Long corporateId);
	public String save(RosterMasterModel rosterMasterModel);
	public String update(RosterMasterModel rosterMasterModel);
	public List<RosterMasterModel> listRoster();
	public GeneralMasterModel getCarModelName(Long id);
	public RosterMasterModel formFillForEditRosterMaster(Long id);
	public String delete(Long id);
	public List<RosterMasterModel> fillRosterDetails(Long corporateId, Long branch,
			Long outlet, Long bookedBy, Long shiftTime, String rosterDate,	Long rosterTakenBy) throws ParseException;
	
	public List<AutorizedUserModel> listAutorizedUsersByRoster(Long corporateId,Long branch,Long outlet,Long bookedBy,Long shiftTiming,String rosterTaken,String routeNumber,String rosterFromDate,String rosterToDate);
	public List<HashMap<String,String>> listCarsByRoster(Long corporateId,Long branch,Long outlet,Long bookedBy,Long shiftTiming,String rosterTaken,String routeNumber,String rosterFromDate,String rosterToDate);
}
