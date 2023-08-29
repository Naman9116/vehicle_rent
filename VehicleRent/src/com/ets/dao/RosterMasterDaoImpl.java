package com.ets.dao;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.corporate.model.AutorizedUserModel;
import com.ets.model.RosterMasterModel;
import com.master.model.GeneralMasterModel;
import com.mobileweb.dao.MobileWebDaoImpl;

@Repository("rosterMasterDao")
public class RosterMasterDaoImpl implements RosterMasterDao {
	
	private static final Logger logger = Logger.getLogger(MobileWebDaoImpl.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	private SessionFactory sessionFactory;
	
	
	@Override
	public AutorizedUserModel fillClientAsPerMobile(String mob, Long corporateId){
		AutorizedUserModel autorizedUserModel = null;
		autorizedUserModel = (AutorizedUserModel) sessionFactory.getCurrentSession().createQuery("FROM AutorizedUserModel A WHERE A.entityContact.personalMobile='"+mob+"' AND A.corporateId.id = " + corporateId).uniqueResult();
		return autorizedUserModel;
	}
	
	@Override
	public String save(RosterMasterModel rosterMasterModel) {
		sessionFactory.getCurrentSession().save(rosterMasterModel);	
		return "saveSuccess";
	}
	
	
	@Override
	public String update(RosterMasterModel rosterMasterModel) {
		sessionFactory.getCurrentSession().update(rosterMasterModel);	
		return "updateSuccess";
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<RosterMasterModel> listRoster() {
		List<RosterMasterModel> rosterMasterModelList = null;
	
		String sSql = "SELECT R FROM RosterMasterModel R WHERE rosterDate >= current_date()  ORDER BY  rosterDate" ;
		rosterMasterModelList= sessionFactory.getCurrentSession().createQuery(sSql).list();
		return rosterMasterModelList;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public GeneralMasterModel getCarModelName(Long id) {
		GeneralMasterModel generalMasterModel = null;
	
		String sSql = "SELECT G FROM GeneralMasterModel G WHERE G.id= "+id ;
		generalMasterModel= (GeneralMasterModel)sessionFactory.getCurrentSession().createQuery(sSql).uniqueResult();
		return generalMasterModel;
	}
	
	@Override
	public RosterMasterModel formFillForEditRosterMaster(Long id){
		RosterMasterModel rosterMasterModel =null;
		rosterMasterModel =(RosterMasterModel) sessionFactory.getCurrentSession()
							.createQuery("SELECT R FROM RosterMasterModel R  where id = "+id).uniqueResult();
		return rosterMasterModel;
	}
	
	@Override
	public String delete(Long idForDelete) {
		RosterMasterModel rosterMasterModel=(RosterMasterModel) sessionFactory.getCurrentSession().get(RosterMasterModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(rosterMasterModel);
		return "deleteSuccess";
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RosterMasterModel> fillRosterDetails(Long corporateId, Long branch,
			Long outlet, Long bookedBy, Long shiftTime, String rosterDate,
			Long rosterTakenBy) throws ParseException{
		List<RosterMasterModel> rosterMasterModelList =null;
		Date dDate = dateFormat.parse(rosterDate);
		rosterDate = new SimpleDateFormat("yyyy-MM-dd").format(dDate);
		rosterMasterModelList = sessionFactory.getCurrentSession()
							.createQuery("FROM RosterMasterModel L  where corporateId.id = "+corporateId+" AND branch.id = "+branch+" AND "
									+ "outlet.id = "+outlet+" AND bookedBy.id = "+bookedBy+" AND rosterTakenBy.id = "+rosterTakenBy+""
											+ "AND shiftTime.id = "+shiftTime+" AND rosterDate = '"+rosterDate+"'  ").list();
		return rosterMasterModelList;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<AutorizedUserModel > listAutorizedUsersByRoster(Long corporateId,Long branchId,Long outletId,Long bookedById,
			Long shiftTimeId,String rosterTaken,String routeNumber,String rosterFromDate,String rosterToDate){
		List<AutorizedUserModel> autorizedUserModelList = null;
	
		autorizedUserModelList= sessionFactory.getCurrentSession()
				.createQuery("FROM AutorizedUserModel WHERE id IN(SELECT A.authoriseClientId FROM RosterMasterModel A WHERE A.corporateId.id=:corporateId "
						+ "AND A.branch.id =:branchId AND A.outlet.id =:outletId AND A.bookedBy.id =:bookedById AND A.shiftTime.id =:shiftTimeId "
						+ "AND A.routeNo =:routeNumber)").setParameter("corporateId", corporateId)
				.setParameter("branchId", branchId).setParameter("outletId", outletId).
				setParameter("bookedById", bookedById).setParameter("shiftTimeId", shiftTimeId)
				.setParameter("routeNumber", routeNumber).list();
		return autorizedUserModelList;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<HashMap<String,String>> listCarsByRoster(Long corporateId,Long branchId,Long outletId,Long bookedById,
			Long shiftTimeId,String rosterTaken,String routeNumber,String rosterFromDate,String rosterToDate){
	
		Query query= sessionFactory.getCurrentSession().createQuery("SELECT A.id as id,A.name as name FROM GeneralMasterModel A"
				+ " WHERE A.id IN(SELECT B.carType FROM RosterMasterModel B WHERE B.corporateId.id=:corporateId "
				+ "AND B.branch.id =:branchId AND B.outlet.id =:outletId AND B.bookedBy.id =:bookedById AND B.shiftTime.id =:shiftTimeId "
				+ "AND B.routeNo =:routeNumber)").setParameter("corporateId", corporateId)
				.setParameter("branchId", branchId).setParameter("outletId", outletId)
				.setParameter("bookedById", bookedById).setParameter("shiftTimeId", shiftTimeId)
				.setParameter("routeNumber", routeNumber)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP );
				
		ArrayList<HashMap<String,String>>carsList=(ArrayList<HashMap<String,String>>)query.list();
		return carsList;
	}
}
