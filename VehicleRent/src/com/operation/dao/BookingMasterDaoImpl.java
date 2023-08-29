package com.operation.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.common.model.AddressDetailModel;
import com.master.model.GeneralMasterModel;
import com.operation.model.BookingDetailModel;
import com.operation.model.BookingMasterModel;
import com.operation.model.DutySlipModel;
import com.operation.model.PassengerDetailModel;
import com.relatedInfo.model.RelatedInfoModel;

@Repository("bookingMasterDao")
public class BookingMasterDaoImpl implements BookingMasterDao{
	private static final Logger logger = Logger.getLogger(BookingMasterDaoImpl.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	@Autowired
	private SessionFactory sessionFactory;
	private static int pageSize = 5;
	
	@SuppressWarnings("unchecked")
	public List<BookingMasterModel> list(String sCriteria, String sValue) {
		List<BookingMasterModel> bookingMasterModel = null;
		String sSearchCriteria = "" ;
		if(!sValue.equalsIgnoreCase("")){
			if(sCriteria.equalsIgnoreCase("M")){
				sSearchCriteria = " WHERE mobileNo like '"+sValue+"%'" ;
			}else if (sCriteria.equalsIgnoreCase("B")){
				sSearchCriteria = " WHERE bookingNo like '"+sValue+"%'" ;
			}else if (sCriteria.equalsIgnoreCase("N")){
				sSearchCriteria = " WHERE bookedForName like '"+sValue+"%'" ;
			}
		}
		String sSql = "FROM BookingMasterModel " + sSearchCriteria + "ORDER BY id DESC";
		bookingMasterModel = sessionFactory.getCurrentSession().createQuery(sSql).list();
		return bookingMasterModel;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<BookingDetailModel> list(String jobType,String[] sCriteriaList, String[] sValueList) {
		List<BookingDetailModel> bookingDetailModel = null;
		String currentTime = (new Date().getHours() <= 9 ? "0" + new Date().getHours() : new Date().getHours() )+ ":" + 
							 (new Date().getMinutes() <= 9 ? "0" + new Date().getMinutes() : new Date().getMinutes());
		String sOrderBy = " order by timestamp(pickUpDate, pickUpTime)";
		String sSearchCriteria = " WHERE " ;
		boolean isExpiredCurDate = false;
		for(int iCount=0; iCount < sCriteriaList.length; iCount++){
			String sCriteria = sCriteriaList[iCount];
			String sValue    = sValueList[iCount];
			if(sCriteria.equals("bookingFromDate") || sCriteria.equals("bookingToDate")){
				try {
						Date dDate = dateFormat.parse(sValue);
						sValue = new SimpleDateFormat("yyyy-MM-dd").format(dDate);
						
						Calendar calendar=Calendar.getInstance();
						calendar.setTime(dDate);
						String sCurDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

						if(sCriteria.equals("bookingFromDate")){
							if(jobType.equals("Current") || jobType.equals("webBooking")){ 
								sValue = sCurDateTime;
							}else { 
								calendar.set(Calendar.HOUR_OF_DAY, 00);
								calendar.set(Calendar.MINUTE, 00);
								dDate=calendar.getTime();
								sValue = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dDate);
							}
						}
						if(sCriteria.equals("bookingToDate")){
							if((jobType.equals("Expired") && sValue.equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date())))) 
								sValue = sCurDateTime;
							else if(jobType.equals("Current") || jobType.equals("Expired") || jobType.equals("Status")|| jobType.equals("Advance") || jobType.equals("Cancelled")){ 
								calendar.set(Calendar.HOUR_OF_DAY, 23);
								calendar.set(Calendar.MINUTE, 59);
								dDate=calendar.getTime();
								sValue = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dDate);
							}
						}
					} catch (ParseException e) {
						logger.error("",e);
					}
			}
			if(sCriteria.equals("bookingFromDate")){
				if(jobType.equals("Current"))
					sSearchCriteria += " (timestamp(pickUpDate,pickUpTime) >='" + sValue + "') And (status is NULL OR status = 'N' OR status = 'A') And ";
				else if (jobType.equals("Expired") && isExpiredCurDate )
					sSearchCriteria += " (timestamp(pickUpDate,pickUpTime) < '" + sValue + "') And ";
				else
					sSearchCriteria += " (timestamp(pickUpDate,pickUpTime) >='" + sValue + "') And ";
			}else if(sCriteria.equals("bookingToDate")){
					sSearchCriteria += "(timestamp(pickUpDate,pickUpTime) <='" + sValue + "') And ";
			}else if(sCriteria.equals("status")){
				if(jobType.equals("Advance")){
					if(sValue.equals("N"))
						sSearchCriteria += " (status is NULL OR status in ('N','M')) And ";
					else
						sSearchCriteria += "(" + sCriteria + "='" + sValue + "' OR '" + sValue + "' = '0' ) And (status is null OR status in( 'A','N','M') ) And ";
				}else if(jobType.equals("webBooking")){
					sSearchCriteria += "(" + sCriteria + " in ('W','P') ) And";
				}else if(jobType.equals("Cancelled")){
					sSearchCriteria += "(" + sCriteria + "='X' OR " + sCriteria + "='E' ) And";
				}else if(jobType.equals("Status")){
					sOrderBy = " order by timestamp(pickUpDate, pickUpTime) desc";
					if(!sValue.equals("0"))
						sSearchCriteria += "(" + sCriteria + "='" + sValue + "' ) And";
					else 
						sSearchCriteria += "(" + sCriteria + " in ('D') ) And";
				}else if(jobType.equals("Expired")){
					sSearchCriteria += "( status is NULL OR status in ('W','P','N') ) And";
				}
			}else if(sCriteria.equals("regNo")){
				if(jobType.equals("Status")){
					sSearchCriteria += "(dutySlipModel.carDetailModel.registrationNo = '" +sValue +"' OR '" + sValue + "' = '0') And";
				}
			}else if(sCriteria.equals("branchesAssigned")){
				sSearchCriteria += " ('" + sValue +"' LIKE concat('%:',str(branch.id),':%') ) And";
			}else{
				sSearchCriteria += "(" + sCriteria + "='" + sValue + "' OR '" + sValue + "' = '0' ) And";
			}
		}
		sSearchCriteria = sSearchCriteria.substring(0,sSearchCriteria.length() - 4);
		String sSql = "FROM BookingDetailModel " + sSearchCriteria + sOrderBy;
		logger.info("sSql : " + sSql);
		bookingDetailModel = sessionFactory.getCurrentSession().createQuery(sSql).list();
		return bookingDetailModel;
	}

	@SuppressWarnings("unchecked")
	public List<BookingDetailModel> list(Long lMasterId) {
		List<BookingDetailModel> bookingDetailModel = null;
		String sSql = "FROM BookingDetailModel WHERE bookingMasterModel.id = "+lMasterId+" ";
		bookingDetailModel = sessionFactory.getCurrentSession().createQuery(sSql).list();
		return bookingDetailModel;
	}

	@SuppressWarnings("unchecked")
	public List<BookingMasterModel> listMasterWise(String bookingMasterCode,String searchCriteria) {
		List<BookingMasterModel> bookingMasterModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BookingMasterModel.class).
		addOrder(Order.desc("id"));
		if(bookingMasterCode!=null && !bookingMasterCode.equalsIgnoreCase(""))
			criteria.add(Restrictions.eq("bookingMasterCode", bookingMasterCode));
		if(searchCriteria!=null && !searchCriteria.trim().equalsIgnoreCase(""))
			criteria.add(Restrictions.like("bookingMasterName", searchCriteria,MatchMode.ANYWHERE));
		bookingMasterModel = (List<BookingMasterModel>)criteria.list();
		return bookingMasterModel;
	}

	@SuppressWarnings("null")
	public BookingMasterModel save(BookingMasterModel bookingMasterModel) {
		RelatedInfoModel relatedInfoModel = null;
		String sSql = "FROM RelatedInfoModel WHERE parentId = " + bookingMasterModel.getBookingDetailModel().get(0).getCompany().getId();
		
		@SuppressWarnings("unchecked")
		List<RelatedInfoModel>  relatedInfoModelList =  (List<RelatedInfoModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();

		long lBookingNo=0l;
		String sCompCode="";
		if(relatedInfoModelList.size() > 0){
			relatedInfoModel = relatedInfoModelList.get(0);
			lBookingNo = relatedInfoModel.getLastBooking() == null ? 0 : relatedInfoModel.getLastBooking();
			sCompCode  = relatedInfoModel.getGeneralMasterModel().getItemCode();
			lBookingNo++;
		}
		sSql = "FROM GeneralMasterModel WHERE id = " + bookingMasterModel.getBookingDetailModel().get(0).getBranch().getId();
		@SuppressWarnings("unchecked")
		List<GeneralMasterModel> generalMasterModelList  =  (List<GeneralMasterModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
		String sBranchCode = generalMasterModelList.get(0).getItemCode();
		bookingMasterModel.setBookingNo( lBookingNo +"-"+sBranchCode.toUpperCase()+"-"+new SimpleDateFormat("yyyy").format(bookingMasterModel.getBookingDate()));
		sessionFactory.getCurrentSession().save(bookingMasterModel);
		if(relatedInfoModel != null) {
			relatedInfoModel.setLastBooking(lBookingNo);
			sessionFactory.getCurrentSession().update(relatedInfoModel);
		}
		return bookingMasterModel;
	}
	
	public BookingMasterModel formFillForEdit(Long formFillForEditId) {
		BookingMasterModel bookingMasterModel=null;
		bookingMasterModel =(BookingMasterModel) sessionFactory.getCurrentSession().get(BookingMasterModel.class, new Long(formFillForEditId));
		return bookingMasterModel;
	}
	
	public BookingDetailModel formFillForDetailEdit(Long formFillForEditId) {
		BookingDetailModel bookingDetailModel=null;
		bookingDetailModel =(BookingDetailModel) sessionFactory.getCurrentSession().get(BookingDetailModel.class, new Long(formFillForEditId));
		return bookingDetailModel;
	}

	public String delete(Long idForDelete) {
		BookingMasterModel bookingMasterModel =(BookingMasterModel) sessionFactory.getCurrentSession().get(BookingMasterModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(bookingMasterModel);
		return "deleteSuccess";
	}
	
	public BookingMasterModel update(BookingMasterModel bookingMasterModel) {
		sessionFactory.getCurrentSession().update(bookingMasterModel);
		return bookingMasterModel;
	}
	
	public String deleteDetailRecord(Long idForDelete){
		BookingDetailModel bookingDetailModel =(BookingDetailModel) sessionFactory.getCurrentSession().get(BookingDetailModel.class, new Long(idForDelete));
		bookingDetailModel.getBookingMasterModel().getBookingDetailModel().remove(bookingDetailModel);
		sessionFactory.getCurrentSession().delete(bookingDetailModel);
		return "deleteSuccess";
	}

	public BookingDetailModel updateDetail(BookingDetailModel bookingDetailModel) {
		sessionFactory.getCurrentSession().update(bookingDetailModel);
		return bookingDetailModel;
	}
	
	public DutySlipModel allocateOrDispatch(DutySlipModel dutySlipModel, String op){
		RelatedInfoModel relatedInfoModel = null;
		String sCompCode="", sBranchCode ="", sDutySlipNo="";
		long lDutySlipNo=0l;
		/*generating DS No.*/
		if(dutySlipModel.getDutySlipStatus().equals("D")){
			String sSql = "FROM RelatedInfoModel WHERE parentId = " + dutySlipModel.getBookingDetailModel().getCompany().getId();
			@SuppressWarnings("unchecked")
			List<RelatedInfoModel>  relatedInfoModelList =  (List<RelatedInfoModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
			if(relatedInfoModelList.size() > 0){
				relatedInfoModel = relatedInfoModelList.get(0);
				lDutySlipNo = relatedInfoModel.getLastDS() == null ? 0 : relatedInfoModel.getLastDS();
				sCompCode  = relatedInfoModel.getGeneralMasterModel().getItemCode();
				lDutySlipNo++;
			}
			sSql = "FROM GeneralMasterModel WHERE id = " + dutySlipModel.getBookingDetailModel().getBranch().getId();
			@SuppressWarnings("unchecked")
			List<GeneralMasterModel> generalMasterModelList  =  (List<GeneralMasterModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
			sBranchCode = generalMasterModelList.get(0).getItemCode();
			sDutySlipNo = lDutySlipNo +"-"+sBranchCode.toUpperCase()+"-"+new SimpleDateFormat("yyyy").format(dutySlipModel.getDutySlipCreatedByDate());
		}
		
		if(op.equals("S")){/*Saving New DS*/
			if(dutySlipModel.getDutySlipStatus().equals("D")){ /*With DS No.*/
				dutySlipModel.setDutySlipNo(sDutySlipNo);
				sessionFactory.getCurrentSession().save(dutySlipModel);
				if(relatedInfoModel != null) {
					relatedInfoModel.setLastDS(lDutySlipNo);
					sessionFactory.getCurrentSession().update(relatedInfoModel);
				}
			}else{
				sessionFactory.getCurrentSession().save(dutySlipModel); /*Without DS No.*/
			}
		}else{ /*Updating DS*/
			if(dutySlipModel.getDutySlipStatus().equals("D")){/*With DS No.*/
				dutySlipModel.setDutySlipNo( sDutySlipNo);
				sessionFactory.getCurrentSession().merge(dutySlipModel);
				if(relatedInfoModel != null) {
					relatedInfoModel.setLastDS(lDutySlipNo);
					sessionFactory.getCurrentSession().update(relatedInfoModel);
				}
			}else{
				sessionFactory.getCurrentSession().merge(dutySlipModel); /*Without DS No.*/
			}
		}	
		sessionFactory.getCurrentSession().createQuery("UPDATE BookingDetailModel SET status = '"+dutySlipModel.getBookingDetailModel().getStatus()+"' WHERE id = "+dutySlipModel.getBookingDetailModel().getId()).executeUpdate();
		return dutySlipModel;
	}
	
	@SuppressWarnings("unchecked")
	public DutySlipModel getAllocationDetails(Long bookingDetailId){
		List<DutySlipModel> dutySlipModels = null;
		dutySlipModels = sessionFactory.getCurrentSession().createQuery("FROM DutySlipModel WHERE bookingDetailModel.id="+bookingDetailId).list();
		if(dutySlipModels!=null && dutySlipModels.size()>0){
			return dutySlipModels.get(0);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<AddressDetailModel> getAddress(Long bookingDetailId) {
		List<AddressDetailModel> addressDetailModel = null;
		addressDetailModel = sessionFactory
				.getCurrentSession()
				.createQuery("select a from BookingDetailModel b , RelatedInfoModel r, AddressDetailModel a "
						  +  "where (case when (select count(*) from RelatedInfoModel r1 where r1.generalMasterModel.id = b.branch.id and LENGTH(COALESCE(r1.gstin,'')) <> 15) > 0 then b.company.id else b.branch.id end) = r.generalMasterModel.id And "
						  +  "	    a.id = r.entityAddress.id AND b.id ="+ bookingDetailId).list();
		return addressDetailModel;
	}
	@SuppressWarnings("unchecked")
	public List<RelatedInfoModel> getEmail(Long bookingDetailId) {
		List<RelatedInfoModel> relatedInfoModel = null;
		String sSql = "select r from BookingDetailModel b , RelatedInfoModel r where b.company.id = r.generalMasterModel.id	AND b.id ="	+ bookingDetailId;
		relatedInfoModel = sessionFactory.getCurrentSession().createQuery(sSql).list();

		List<RelatedInfoModel> relatedInfoModelBr = null;
		sSql = "select br from RelatedInfoModel br,  BookingDetailModel b where (case when (select count(*) from RelatedInfoModel r1 where r1.generalMasterModel.id = b.branch.id and LENGTH(COALESCE(r1.gstin,'')) <> 15) > 0 then b.company.id else b.branch.id end) = br.generalMasterModel.id And b.id ="	+ bookingDetailId;
		relatedInfoModelBr = sessionFactory.getCurrentSession().createQuery(sSql).list();

		relatedInfoModel.get(0).setGstinBr(relatedInfoModelBr.get(0).getGstin());
		
		return relatedInfoModel;
	}
	@SuppressWarnings("unchecked")
	public List<BookingDetailModel> getList(Long lMasterId,String bookingFromDate,String bookingToDate) {
		try{
			Date dDate = dateFormat.parse(bookingFromDate);
			bookingFromDate = new SimpleDateFormat("yyyy-MM-dd").format(dDate);
			
			dDate = dateFormat.parse(bookingToDate);
			bookingToDate = new SimpleDateFormat("yyyy-MM-dd").format(dDate);
			
		}catch(Exception e){
			logger.error("",e);
		}		
		
		List<BookingDetailModel> bookingDetailModels = null;
		bookingDetailModels = sessionFactory.getCurrentSession()
								.createQuery("FROM BookingDetailModel  WHERE bookingMasterModel.id = '"+lMasterId+"'	AND "
											+"	(status is null OR status = 'N' ) And "
											+ " pickUpDate >='"+bookingFromDate+"' And pickUpDate <='"+bookingToDate+"'").list();
		return bookingDetailModels;
	}
	
	@SuppressWarnings("unchecked")
	public List<DutySlipModel> getReservedList(Long lMasterId, String bookingFromDate, String bookingToDate){
		try{
			Date dDate = dateFormat.parse(bookingFromDate);
			bookingFromDate = new SimpleDateFormat("yyyy-MM-dd").format(dDate);
			
			dDate = dateFormat.parse(bookingToDate);
			bookingToDate = new SimpleDateFormat("yyyy-MM-dd").format(dDate);
			
		}catch(Exception e){
			logger.error("",e);
		}		
		List<DutySlipModel> dutySlipModels = null;
		dutySlipModels = sessionFactory.getCurrentSession()
								.createQuery("FROM DutySlipModel  "
										+ "	  WHERE dutySlipStatus = 'M' AND "
										+ "         bookingDetailModel.id in (SELECT id "
										+ "									  FROM BookingDetailModel  "
										+ "									  WHERE bookingMasterModel.id = '"+lMasterId+"' AND "
										+ " 										pickUpDate >='"+bookingFromDate+"' And pickUpDate <='"+bookingToDate+"') order by carDetailModel.id").list();
		return dutySlipModels;
	}
	public String updateBookingNoForWeb(BookingMasterModel bookingMasterModel) {
		RelatedInfoModel relatedInfoModel = null;
		String sSql = "FROM RelatedInfoModel WHERE parentId = " + bookingMasterModel.getBookingDetailModel().get(0).getCompany().getId();
		
		@SuppressWarnings("unchecked")
		List<RelatedInfoModel>  relatedInfoModelList =  (List<RelatedInfoModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();

		long lBookingNo=0l;
		String sCompCode="";
		if(relatedInfoModelList.size() > 0){
			relatedInfoModel = relatedInfoModelList.get(0);
			lBookingNo = relatedInfoModel.getLastBooking() == null ? 0 : relatedInfoModel.getLastBooking();
			sCompCode  = relatedInfoModel.getGeneralMasterModel().getItemCode();
			lBookingNo++;
		}
		sSql = "FROM GeneralMasterModel WHERE id = " + bookingMasterModel.getBookingDetailModel().get(0).getBranch().getId();
		@SuppressWarnings("unchecked")
		List<GeneralMasterModel> generalMasterModelList  =  (List<GeneralMasterModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
		String sBranchCode = generalMasterModelList.get(0).getItemCode();
		bookingMasterModel.setBookingNo( lBookingNo +"-"+sBranchCode.toUpperCase()+"-"+new SimpleDateFormat("yyyy").format(bookingMasterModel.getBookingDate()));
		bookingMasterModel.setStatus(null);
		sessionFactory.getCurrentSession().update(bookingMasterModel);
		relatedInfoModel.setLastBooking(lBookingNo);
		sessionFactory.getCurrentSession().update(relatedInfoModel);
		return "Success";
	}
	
	@SuppressWarnings("unchecked")
	public List<BookingDetailModel> getBookingDetailAsPerBookingMaster(Long bookingMasterId){
		List<BookingDetailModel> bookingDetailModels = null;
		bookingDetailModels = sessionFactory.getCurrentSession().createQuery("FROM BookingDetailModel WHERE bookingMasterModel.id="+bookingMasterId).list();
		return bookingDetailModels;
	}
	
	@Override
	public String save(PassengerDetailModel   passengerDetailModel) {
		sessionFactory.getCurrentSession().save(passengerDetailModel);	
		return "Success";
	}
	
	@SuppressWarnings("unchecked")
	public List<PassengerDetailModel> getPassengerDetails(Long bookingDetailId){
		List<PassengerDetailModel> passengerDetailModels = null;
		passengerDetailModels = sessionFactory.getCurrentSession().createQuery("FROM PassengerDetailModel WHERE bookingDetailId.id="+bookingDetailId).list();
		return passengerDetailModels;
	}
	
	@Override
	public String deletePassengerDetails(Long  id) {
		sessionFactory.getCurrentSession().createQuery("Delete From PassengerDetailModel WHERE bookingDetailId.id="+id).executeUpdate();
		return "Success";
	}
}
