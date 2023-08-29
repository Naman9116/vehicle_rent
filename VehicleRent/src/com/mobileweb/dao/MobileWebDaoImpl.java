package com.mobileweb.dao;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.corporate.model.AutorizedUserModel;
import com.master.model.ChauffeurModel;
import com.master.model.ChauffeurStatusModel;
import com.operation.model.BookingMasterModel;
import com.operation.model.DutySlipModel;
import com.operation.model.TripRoutMapModel;
import com.util.Utilities;


@Repository("mobileWebDao")

public class MobileWebDaoImpl implements MobileWebDao {
	private static final Logger logger = Logger.getLogger(MobileWebDaoImpl.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public AutorizedUserModel userAsUserName(String sUserName) {
		AutorizedUserModel autorizedUserModel = null;
		String sSql = "FROM AutorizedUserModel WHERE userName='"+sUserName+"'";
		@SuppressWarnings("unchecked")
		List<AutorizedUserModel> autorizedUserModelList= (List<AutorizedUserModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
		if(autorizedUserModelList.size() > 0){
			autorizedUserModel =(AutorizedUserModel) sessionFactory.getCurrentSession().get(AutorizedUserModel.class, autorizedUserModelList.get(0).getId().longValue());
			if(autorizedUserModel.getCorporateId().getEntityAddress() != null)
				autorizedUserModel.setoAddress(autorizedUserModel.getCorporateId().getEntityAddress().getAddress1() + " " + 
											   autorizedUserModel.getCorporateId().getEntityAddress().getAddress2());
		}
		return autorizedUserModel;
	}

	@Override
	public ChauffeurModel driverAsUserName(String sUserName) {
		ChauffeurModel chauffeurModel = null;
		String sSql = "FROM ChauffeurModel WHERE userName='"+sUserName+"'";
		@SuppressWarnings("unchecked")
		List<ChauffeurModel> chauffeurModelList= (List<ChauffeurModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
		if(chauffeurModelList.size() > 0){
			chauffeurModel = chauffeurModelList.get(0);
			chauffeurModel =(ChauffeurModel) sessionFactory.getCurrentSession().get(ChauffeurModel.class, chauffeurModel.getId().longValue());
		}
		return chauffeurModel;
	}

	@Override
	public List<BookingMasterModel> SearchBooking(Long id, String status, String toDate, String fromDate) {
		fromDate = Utilities.dateDeshSqlFormat(fromDate);
		toDate = Utilities.dateDeshSqlFormat(toDate);
		String sCondition = "";
		if(status.equals("N")){
			sCondition = " And bm.bookedBy.id=" + id + " And bd.pickUpDate >= current_date() And bd.pickUpDate between '"+fromDate+"' And '"+toDate+"' And (bd.status is null OR bd.status in ('N','W','E','X') OR  (select count(*) from DutySlipModel ds  where ds.dutySlipStatus not in ('F','R','I','C') And ds.bookingDetailModel.id = bd.id) > 0)";
		}else if (status.equals("P")){
			sCondition = " And (bd.status = 'P' And bm.bookedBy.id in (select Z.id from AutorizedUserModel Z where Z.parentId = '"+id+"')) ";
		}else if(status.equals("")){
			sCondition = " And bm.bookedBy.id=" + id + " And bd.pickUpDate < current_date() And bd.pickUpDate between '"+fromDate+"' And '"+toDate+"' And (bd.status is null OR bd.status in ('N','W','E','X') OR  (select count(*) from DutySlipModel ds  where ds.dutySlipStatus in ('F','R','I','C') And ds.bookingDetailModel.id = bd.id) > 0)";
		}else{
			sCondition = " And bm.bookedBy.id=" + id + " And bd.pickUpDate < current_date() And bd.pickUpDate between '"+fromDate+"' And '"+toDate+"' And ((select count(*) from DutySlipModel ds  where ds.dutySlipStatus not in ('A','D','M','U','X') And ds.dutySlipStatus = '"+status+"' And ds.bookingDetailModel.id = bd.id) > 0)";
		}
		String sSql = "SELECT bm.id as id, bm.bookedForName as bookedForName,bm.bookingDate as bookingDate,"
					+ " bm.bookingNo as bookingNo,bd.pickUpTime as pickUpTime, bd.pickUpDate as pickUpDate, "
					+ "	(select case when count(ds2.dutySlipStatus) = 0 then bd.status else ds2.dutySlipStatus end from DutySlipModel ds2 where ds2.bookingDetailModel.id = bd.id) as bookingDetailStatus, bd.reportingAddress as reportingAddress ,"
					+ " bd.toBeRealeseAt as dropAt,bd.carModel.name as carModelName, bm.mobileNo as mobileNo, bd.mob.id as mobId, "
					+ " (select gm.name from GeneralMasterModel gm where gm.id = bd.mob.id) as mobName"
					+ " FROM BookingMasterModel bm , BookingDetailModel bd "
					+ " WHERE bd.bookingMasterModel.id=bm.id " + sCondition ;
		@SuppressWarnings("unchecked")
		List<BookingMasterModel> bookingMasterModel= (List<BookingMasterModel>) sessionFactory.getCurrentSession().createQuery(sSql).setResultTransformer(Transformers.aliasToBean(BookingMasterModel.class)).list();
		
		return bookingMasterModel;
	}

	@Override
	public BookingMasterModel save(BookingMasterModel   bookingMasterModel) {
		sessionFactory.getCurrentSession().save(bookingMasterModel);	
		return bookingMasterModel;
	}
	public String saveChStatus(ChauffeurStatusModel chauffeurStatusModel) {
		sessionFactory.getCurrentSession().save(chauffeurStatusModel);
		return "saveSuccess";
	}
	
	public String updateChStatus(ChauffeurStatusModel chauffeurStatusModel) {
		sessionFactory.getCurrentSession().update(chauffeurStatusModel);
		return "updateSuccess";
	}

	public ChauffeurStatusModel getChauffeurStatusModel(Long id){
		ChauffeurStatusModel chauffeurStatusModel =null;
		chauffeurStatusModel =(ChauffeurStatusModel) sessionFactory.getCurrentSession()
							.createQuery("SELECT CHS FROM ChauffeurStatusModel CHS where CHS.chauffeurId.id = "+id+" And CHS.insDate = (SELECT MAX(A.insDate) From ChauffeurStatusModel A where A.chauffeurId.id = CHS.chauffeurId.id)").uniqueResult();
		return chauffeurStatusModel;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<BookingMasterModel> getBookingAsPerChauffeurId(Long chauffeurId,String flag) {
		List<BookingMasterModel> bookingMasterModelList = null;
		String sCondition="";
		if(flag.equals("N")){
			sCondition=" And ds.dutySlipStatus not in ('F','R','C','I') ";
		}else if(flag.equals("C")){
			sCondition=" And ds.dutySlipStatus in('F','R','C','I') ";
		}

		String sSql = "SELECT bm.id as id,ds.id as dutySlipId, bm.bookedForName as bookedForName,bm.bookingDate as bookingDate,"
					+ " bm.bookingNo as bookingNo,bd.pickUpTime as pickUpTime, bd.pickUpDate as pickUpDate, "
					+ "	ds.dutySlipStatus as bookingDetailStatus,ds.carDetailModel.registrationNo as registrationNo, bd.reportingAddress as reportingAddress ,"
					+ " bd.toBeRealeseAt as dropAt,bd.carModel.name as carModelName, bm.mobileNo as mobileNo, bd.mob.id as mobId, "
					+ " (select gm.name from GeneralMasterModel gm where gm.id = bd.mob.id) as mobName"
					+ " FROM BookingMasterModel bm , BookingDetailModel bd, DutySlipModel ds "
					+ " WHERE bd.bookingMasterModel.id=bm.id And ds.bookingDetailModel.id=bd.id AND "
					+ " 	  ds.chauffeurModel.id="+chauffeurId + sCondition + " ORDER BY pickUpDate Desc";
		bookingMasterModelList= sessionFactory.getCurrentSession()
				.createQuery(sSql).setResultTransformer(Transformers.aliasToBean(BookingMasterModel.class)).list();
		return bookingMasterModelList;
	}
	
	public String[] getChauffeurDutyCount(Long chauffeurId, String flag){
		String[] sOtherInfo = new String[2];
		String sCondition="";
		if(flag.equals("N")){
			sCondition=" And ( ds.dutySlipStatus not in ('F','C','R','I') ";
		}else if(flag.equals("C")){
			sCondition=" And ds.dutySlipStatus in('F','C','R','I') ";
		}
		sOtherInfo[0] =String.valueOf((Long) sessionFactory.getCurrentSession()
							.createQuery("SELECT COUNT(ds.id) FROM DutySlipModel ds where ds.chauffeurModel.id = "+chauffeurId + " " + sCondition).uniqueResult());
		sOtherInfo[1] =(String) sessionFactory.getCurrentSession()
				.createQuery("SELECT B.carDetailModelId.registrationNo FROM ChauffeurModel A, CarAllocationModel B where A.id = B.chauffeurId.id And B.carStatus = 'Y' And A.id = "+chauffeurId ).uniqueResult();
		return sOtherInfo;
	}

	@Override
	public String saveTripRouteMap(TripRoutMapModel tripRoutMapModel) {
		sessionFactory.getCurrentSession().save(tripRoutMapModel);
		return "saveSuccess";
	}

	@Override
	public BookingMasterModel update(BookingMasterModel bookingMasterModel) {
		sessionFactory.getCurrentSession().update(bookingMasterModel);
		return bookingMasterModel;
	}
	
}
