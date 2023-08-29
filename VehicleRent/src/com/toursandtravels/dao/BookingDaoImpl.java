package com.toursandtravels.dao;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.common.model.AddressDetailModel;
import com.corporate.model.CorporateModel;
import com.ets.model.LocationMasterModel;
import com.master.model.CarAllocationModel;
import com.master.model.CityMasterModel;
import com.master.model.GeneralMasterModel;
import com.mobileweb.dao.MobileWebDaoImpl;
import com.operation.dao.BookingMasterDaoImpl;
import com.operation.model.BookingDetailModel;
import com.operation.model.BookingMasterModel;
import com.operation.model.DutySlipModel;
import com.relatedInfo.model.RelatedInfoModel;
import com.toursandtravels.model.BookingModel;
import com.toursandtravels.model.CustomerModel;
import com.toursandtravels.model.TDutySlipModel;

@Repository("bookingDao")
public class BookingDaoImpl implements BookingDao {

	private static final Logger logger = Logger.getLogger(BookingDaoImpl.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public BookingModel save(BookingModel   bookingModel) {
		RelatedInfoModel relatedInfoModel = null;
		String sSql = "FROM RelatedInfoModel WHERE parentId = " + bookingModel.getCompany().getId();
		
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
		sSql = "FROM GeneralMasterModel WHERE id = " + bookingModel.getBranch().getId();
		@SuppressWarnings("unchecked")
		List<GeneralMasterModel> generalMasterModelList  =  (List<GeneralMasterModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
		String sBranchCode = generalMasterModelList.get(0).getItemCode();
		bookingModel.setBookingNo( lBookingNo +"-"+sBranchCode.toUpperCase()+"-"+new SimpleDateFormat("yyyy").format(bookingModel.getBookingDate()));
		sessionFactory.getCurrentSession().save(bookingModel);	
		relatedInfoModel.setLastBooking(lBookingNo);
		sessionFactory.getCurrentSession().update(relatedInfoModel);
		return bookingModel;
	}
	
	@Override
	public String update(BookingModel   bookingModel) {
		sessionFactory.getCurrentSession().update(bookingModel);	
		return "SUCCESS";
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<BookingModel> getBooking() {
		List<BookingModel> bookingModelList = null;
	
		String sSql = "SELECT B FROM BookingModel B WHERE B.bookingDate=current_date() AND B.status is null" ;
		bookingModelList= sessionFactory.getCurrentSession().createQuery(sSql).list();
		return bookingModelList;
	}
	@Override
	public BookingModel formFillForEditBooking(Long id){
		BookingModel bookingModel =null;
		bookingModel =(BookingModel) sessionFactory.getCurrentSession()
							.createQuery("SELECT B FROM BookingModel B  where id = "+id).uniqueResult();
		return bookingModel;
	}
	@Override
	public String delete(Long idForDelete) {
		BookingModel bookingModel =(BookingModel) sessionFactory.getCurrentSession().get(BookingModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(bookingModel);
		return "deleteSuccess";
	}
	
	@Override
	public CustomerModel getCustomerModelAsPerMobile(String mobileNo){
		CustomerModel customerModel =new CustomerModel();
		customerModel =(CustomerModel) sessionFactory.getCurrentSession()
							.createQuery("SELECT C FROM CustomerModel C  WHERE mobileNo = "+mobileNo).uniqueResult();
		return customerModel;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GeneralMasterModel> getTarrifAsPerRentleType(long id){
		List<GeneralMasterModel> generalMasterModelList =null;
		generalMasterModelList =sessionFactory.getCurrentSession()
				.createQuery("SELECT GM.id as id ,GM.name as name FROM GeneralMasterModel GM WHERE GM.id in(SELECT subMasterValuesModel.id FROM MappingMasterModel WHERE masterValuesModel.id="+id+")").setResultTransformer(Transformers.aliasToBean(GeneralMasterModel.class)).list();
		return generalMasterModelList;
	}
	
	@Override
	public BookingModel getBookingModelAsPerMobile(String mobileNo){
		BookingModel bookingModel =null;
		bookingModel =(BookingModel) sessionFactory.getCurrentSession()
							.createQuery("From BookingModel where insDate = "
									+ "(select max(insDate) from BookingModel where mobileNo='"+mobileNo+"')").uniqueResult();
		return bookingModel;
	}
	@Override
	@SuppressWarnings("unchecked")
	public List<BookingModel> list(String jobType,String[] sCriteriaList, String[] sValueList) {
		List<BookingModel> bookingModel = null;
		String currentTime = (new Date().getHours() <= 9 ? "0" + new Date().getHours() : new Date().getHours() )+ ":" + 
							 (new Date().getMinutes() <= 9 ? "0" + new Date().getMinutes() : new Date().getMinutes());
		String sOrderBy = " order by pickUpDate, pickUpTime";
		String sSearchCriteria = " WHERE " ;
		for(int iCount=0; iCount < sCriteriaList.length; iCount++){
			String sCriteria = sCriteriaList[iCount];
			String sValue    = sValueList[iCount];
			if(sCriteria.equals("bookingFromDate") || sCriteria.equals("bookingToDate")){
				try {
						Date dDate = dateFormat.parse(sValue);
						sValue = new SimpleDateFormat("yyyy-MM-dd").format(dDate);
					} catch (ParseException e) {
						logger.error("",e);
					}
			}
			if(sCriteria.equals("bookingFromDate")){
				if(jobType.equals("Current"))
					sSearchCriteria += " (pickUpDate ='" + sValue + "') And (status is NULL OR status = 'N' OR status = 'A') And ";
				else if(jobType.equals("Status") || jobType.equals("Cancelled"))
					sSearchCriteria += "(pickUpDate >='" + sValue + "') And ";
				else if(jobType.equals("Expired"))
					sSearchCriteria += "(pickUpDate >='" + sValue + "') And ";				
				else
					sSearchCriteria += "(pickUpDate >='" + sValue + "') And ";
			}else if(sCriteria.equals("bookingToDate")){
				if(jobType.equals("Expired"))
					sSearchCriteria += " ( (pickUpDate <='" + sValue + "' And '" + sValue + "' != current_date() ) Or ( '" + sValue + "' = current_date() And pickUpDate <='" + sValue + "' And pickUpTime < '"+ currentTime+"') ) And ";
				else
					sSearchCriteria += "(pickUpDate <='" + sValue + "') And ";
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
					sOrderBy = " order by pickUpDate desc, pickUpTime desc";
					if(!sValue.equals("0"))
						sSearchCriteria += "(" + sCriteria + "='" + sValue + "' ) And";
					else 
						sSearchCriteria += "(" + sCriteria + " in ('D') ) And";
				}else if(jobType.equals("Expired")){
					sSearchCriteria += "( status is NULL OR status = 'N' ) And";
				}
			}else if(sCriteria.equals("regNo")){
				if(jobType.equals("Status")){
					sSearchCriteria += "(tDutySlipModel.carDetailModel.registrationNo = '" +sValue +"' OR '" + sValue + "' = '0') And";
				}
			}else{
				sSearchCriteria += "(" + sCriteria + "='" + sValue + "' OR '" + sValue + "' = '0' ) And";
			}
		}
		sSearchCriteria = sSearchCriteria.substring(0,sSearchCriteria.length() - 4);
		String sSql = "FROM BookingModel " + sSearchCriteria + sOrderBy;
		bookingModel = sessionFactory.getCurrentSession().createQuery(sSql).list();
		return bookingModel;
	}
	@Override
	public TDutySlipModel allocateOrDispatch(TDutySlipModel tDutySlipModel, String op){
		if(op.equals("S")){
			if(tDutySlipModel.getDutySlipStatus().equals("D")){
				RelatedInfoModel relatedInfoModel = null;
				String sSql = "FROM RelatedInfoModel WHERE parentId = " + tDutySlipModel.getBookingModel().getCompany().getId();
				@SuppressWarnings("unchecked")
				List<RelatedInfoModel>  relatedInfoModelList =  (List<RelatedInfoModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
				long lDutySlipNo=0l;
				String sCompCode="";
				if(relatedInfoModelList.size() > 0){
					relatedInfoModel = relatedInfoModelList.get(0);
					lDutySlipNo = relatedInfoModel.getLastDS() == null ? 0 : relatedInfoModel.getLastDS();
					sCompCode  = relatedInfoModel.getGeneralMasterModel().getItemCode();
					lDutySlipNo++;
				}
				sSql = "FROM GeneralMasterModel WHERE id = " + tDutySlipModel.getBookingModel().getBranch().getId();
				@SuppressWarnings("unchecked")
				List<GeneralMasterModel> generalMasterModelList  =  (List<GeneralMasterModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
				String sBranchCode = generalMasterModelList.get(0).getItemCode();
				tDutySlipModel.setDutySlipNo( lDutySlipNo +"-"+sBranchCode.toUpperCase()+"-"+new SimpleDateFormat("yyyy").format(tDutySlipModel.getDutySlipCreatedByDate()));
				sessionFactory.getCurrentSession().save(tDutySlipModel);
				relatedInfoModel.setLastDS(lDutySlipNo);
				sessionFactory.getCurrentSession().update(relatedInfoModel);
			}else{
				sessionFactory.getCurrentSession().save(tDutySlipModel);
			}
			
		}else{
			if(tDutySlipModel.getDutySlipStatus().equals("D")){
				RelatedInfoModel relatedInfoModel = null;
				String sSql = "FROM RelatedInfoModel WHERE parentId = " + tDutySlipModel.getBookingModel().getCompany().getId();
				@SuppressWarnings("unchecked")
				List<RelatedInfoModel>  relatedInfoModelList =  (List<RelatedInfoModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
				long lDutySlipNo=0l;
				String sCompCode="";
				if(relatedInfoModelList.size() > 0){
					relatedInfoModel = relatedInfoModelList.get(0);
					lDutySlipNo = relatedInfoModel.getLastDS() == null ? 0 : relatedInfoModel.getLastDS();
					sCompCode  = relatedInfoModel.getGeneralMasterModel().getItemCode();
					lDutySlipNo++;
				}
				sSql = "FROM GeneralMasterModel WHERE id = " + tDutySlipModel.getBookingModel().getBranch().getId();
				@SuppressWarnings("unchecked")
				List<GeneralMasterModel> generalMasterModelList  =  (List<GeneralMasterModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
				String sBranchCode = generalMasterModelList.get(0).getItemCode();
				tDutySlipModel.setDutySlipNo( lDutySlipNo +"-"+sBranchCode.toUpperCase()+"-"+new SimpleDateFormat("yyyy").format(tDutySlipModel.getDutySlipCreatedByDate()));
				sessionFactory.getCurrentSession().merge(tDutySlipModel);
				relatedInfoModel.setLastDS(lDutySlipNo);
				sessionFactory.getCurrentSession().update(relatedInfoModel);
			}else{
				sessionFactory.getCurrentSession().merge(tDutySlipModel);
			}
		}	
		sessionFactory.getCurrentSession().createQuery("UPDATE BookingModel SET status = '"+tDutySlipModel.getBookingModel().getStatus()+"' WHERE id = "+tDutySlipModel.getBookingModel().getId()).executeUpdate();
		return tDutySlipModel;
	}
	@Override
	@SuppressWarnings("unchecked")
	public TDutySlipModel  getDsRowCount(Long booDetId){
		List<TDutySlipModel> tDutySlipModel = null;
		tDutySlipModel = sessionFactory.getCurrentSession().createQuery("FROM DutySlipModel WHERE dutySlipStatus='U' And bookingDetailModel.id="+booDetId).list();
		if(tDutySlipModel!=null && tDutySlipModel.size()>0){
			return tDutySlipModel.get(0);
		}
		return null;
	}
	@Override
	@SuppressWarnings("unchecked")
	public List<AddressDetailModel> getAddress(Long bookingId) {
		List<AddressDetailModel> addressDetailModel = null;
		addressDetailModel = sessionFactory
				.getCurrentSession()
				.createQuery(
						"select a from BookingModel b , RelatedInfoModel r, AddressDetailModel a where b.branch.id = r.generalMasterModel.id	AND a.id = r.entityAddress.id	AND b.id ="
								+ bookingId).list();

		return addressDetailModel;
	}
	@Override
	@SuppressWarnings("unchecked")
	public List<RelatedInfoModel> getEmail(Long bookingId) {
		List<RelatedInfoModel> relatedInfoModel = null;
		relatedInfoModel = sessionFactory
				.getCurrentSession()
				.createQuery(
						"select r from BookingModel b , RelatedInfoModel r, AddressDetailModel a where b.company.id = r.generalMasterModel.id	AND a.id = r.entityAddress.id	AND b.id ="
								+ bookingId).list();
		return relatedInfoModel;
	}
	@Override
	@SuppressWarnings("unchecked")
	public List<TDutySlipModel> getlist(Long bookingId, Long carDetailId){
		String sSql = "FROM BookingModel WHERE id = " + bookingId;
		List<BookingModel>  BookingModels =  (List<BookingModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
		List<TDutySlipModel> tDutySlipModels = null;
		System.out.println("hello "+BookingModels.get(0).getPickUpDate());
		tDutySlipModels = sessionFactory.getCurrentSession()
						.createQuery("SELECT D FROM TDutySlipModel D, BookingModel B  where D.carDetailModel.id  ='"+carDetailId+"' "
										+ "AND D.bookingModel.id = B.id AND B.status <> 'N' And B.status is not null And  B.pickUpDate = '"+BookingModels.get(0).getPickUpDate()+"'").list();
		return tDutySlipModels;
		
	}
}
