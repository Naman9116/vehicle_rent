package com.billing.dao;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.billing.model.CoverLetterModel;
import com.billing.model.InvoiceDetailModel;
import com.billing.model.InvoiceModel;
import com.billing.model.TransactionDetailModel;
import com.corporate.model.AutorizedUserModel;
import com.corporate.model.CorporateModel;
import com.master.model.CarAllocationModel;
import com.master.model.CorporateTariffDetModel;
import com.master.model.CorporateTaxDetModel;
import com.master.model.GeneralMasterModel;
import com.master.model.MasterModel;
import com.master.model.TariffSchemeParaModel;
import com.operation.model.BookingDetailModel;
import com.operation.model.BookingMasterModel;
import com.operation.model.CarDetailModel;
import com.operation.model.DutySlipModel;
import com.operation.model.HistoryDutySlipModel;
import com.operation.model.TempModel;
import com.relatedInfo.model.RelatedInfoModel;
import com.user.model.UserModel;


@Repository("billingDao")
public class BillingDaoImpl implements BillingDao {
	private static final Logger logger = Logger.getLogger(BillingDaoImpl.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	@Autowired
	private SessionFactory sessionFactory;
	private static int pageSize = 5;
	
	List<BookingDetailModel> bookingDetailModels=null;
	@SuppressWarnings("unchecked")
	public List<DutySlipModel> getInvoice( String[] sCriteriaList, String[] sValueList) {
		List<DutySlipModel> dutySlipModels = null;

		String sSearchCriteria = " WHERE ";
		for (int iCount = 0; iCount < sCriteriaList.length; iCount++) {
			String sCriteria = sCriteriaList[iCount];
			String sValue = sValueList[iCount];
			if (sCriteria.equals("invoiceFromDate") || sCriteria.equals("invoiceToDate")) {
				try {
					Date dDate = dateFormat.parse(sValue);
					sValue = new SimpleDateFormat("yyyy-MM-dd").format(dDate);
				} catch (ParseException e) {
					logger.error("",e);
				}
			}
			if (sCriteria.equals("invoiceFromDate")) {
				sSearchCriteria += "(invoiceDate  BETWEEN '" + sValue + "'  And ";
			} else if (sCriteria.equals("invoiceToDate")) {
				sSearchCriteria += "'" + sValue + "') And";
			} else if(sCriteria.equals("bookingDetailModel.bookingMasterModel.corporateId.id")){
					 if(!sValue.equals("0"))
						sSearchCriteria += "(" + sCriteria + "='" + sValue + "' ) And ";
			}else if(sCriteria.equals("invoiceNo")){
					if(!sValue.equals("0"))
					sSearchCriteria += "(invoiceNo = '" +sValue +"' OR '" + sValue + "' = '0') And ";
			}else if(sCriteria.equals("closedBy")){
					if(!sValue.equals("0"))
					sSearchCriteria += "(dutySlipClosedBy = '" +sValue +"' OR '" + sValue + "' = '0') And ";
			}else if(sCriteria.equals("usedBy")){
					if(!sValue.equals("0"))
					sSearchCriteria += "(bookingDetailModel.bookingMasterModel.bookedForName = '" +sValue +"' OR '" + sValue + "' = '0') And ";
			}else if(sCriteria.equals("bookedBy")){
					if(!sValue.equals("0"))
					sSearchCriteria += "(bookingDetailModel.bookingMasterModel.bookedBy.id = '" +sValue +"' OR '" + sValue + "' = '0') And ";
			}else {
				sSearchCriteria += "(" + sCriteria + "='" + sValue + "' OR '" + sValue + "' = '0' ) And ";
			}
		}
		sSearchCriteria += "(  dutySlipStatus = 'I' ) And";
		sSearchCriteria = sSearchCriteria.substring(0, sSearchCriteria.length() - 4);
		String sSql = " FROM DutySlipModel " + sSearchCriteria + " ORDER By invoiceNo asc, dutySlipDate asc";
		logger.info("Query for Invoice List Searching : "+sSql);
		dutySlipModels = sessionFactory.getCurrentSession().createQuery(sSql).list();
		return dutySlipModels;
	}
	@SuppressWarnings("unchecked")
	public List<GeneralMasterModel> getCarMasterId(long id) {
		List<GeneralMasterModel> genralMasterModel = null;
		genralMasterModel = sessionFactory.getCurrentSession()
													.createQuery("SELECT A FROM GeneralMasterModel A, MappingMasterModel B, MasterModel C"
															+ " WHERE A.id = B.masterValuesModel.id AND B.subMasterValuesModel.id = "+id+""
																	+ "AND A.masterModel.id = C.id AND C.code = 'VCT'").list();
		return genralMasterModel;
	}
	@SuppressWarnings("unchecked")
	public List<CorporateTariffDetModel> getTariffValue(long tId) {
		List<CorporateTariffDetModel> corporateTariffDetModel = null;
		corporateTariffDetModel= sessionFactory.getCurrentSession().createQuery("select c from CorporateTariffDetModel c where carCatId="+tId	).list();
		return corporateTariffDetModel;
	}
	@SuppressWarnings({ "unchecked", "null" })
	public List<DutySlipModel> list( String jobType, String[] sCriteriaList, String[] sValueList) {
		List<DutySlipModel> dutySlipModels = null;
		String sSearchCriteria = " WHERE ";
		boolean isForAll = true;
		for (int iCount = 0; iCount < sCriteriaList.length; iCount++) {
			String sCriteria = sCriteriaList[iCount];
			String sValue = sValueList[iCount];
			if(!sValue.equals("0")) isForAll = false;
			if (sCriteria.equals("bookingDetailModel.rentalType.id")) {
				if (jobType.equals("DutySlipReceive") || jobType.equals("DutySlipClose")) {
					if (!sValue.equals("0"))
						sSearchCriteria += "(" + sCriteria + "='" + sValue + "' ) And ";
				}
			} else if (sCriteria.equals("bookingDetailModel.bookingMasterModel.corporateId.id")) {
				if (jobType.equals("DutySlipReceive") || jobType.equals("DutySlipClose")
						|| jobType.equals("DutySlipUnbilled")) {
					if (!sValue.equals("0"))
						sSearchCriteria += "(" + sCriteria + "='" + sValue + "' ) And ";
				}
			} else if (sCriteria.equals("regNo")) {
				if (jobType.equals("DutySlipReceive") || jobType.equals("DutySlipClose")) {
					if (!sValue.equals("0"))
						sSearchCriteria += "(carDetailModel.registrationNo = '" + sValue + "') And ";
				}
			} else if (sCriteria.equals("closedBy")) {
				if (jobType.equals("DutySlipUnbilled")) {
					if (!sValue.equals("0"))
						sSearchCriteria += "(dutySlipClosedBy = '" + sValue + "') And ";
				}
			} else if (sCriteria.equals("usedBy")) {
				if (jobType.equals("DutySlipUnbilled")) {
					if (!sValue.equals("0"))
						sSearchCriteria += "(bookingDetailModel.bookingMasterModel.bookedForName = '" + sValue
								+ "' ) And ";
				}
			} else if (sCriteria.equals("bookedBy")) {
				if (jobType.equals("DutySlipUnbilled")) {
					if (!sValue.equals("0"))
						sSearchCriteria += "(bookingDetailModel.bookingMasterModel.bookedBy.id = '" + sValue
								+ "' ) And ";
				}
			} else {
				if (!sValue.equals("0"))
					sSearchCriteria += "(" + sCriteria + "='" + sValue + "' ) And ";
			}
		}
		if(isForAll) sSearchCriteria = sSearchCriteria + " A.id = B.bookingDetailModel.id And ";
		if(jobType.equals("DutySlipReceive")){
			sSearchCriteria += "(  dutySlipStatus in ('D','F') ) And ";
		}else if(jobType.equals("DutySlipClose")){
			sSearchCriteria += "(  dutySlipStatus = 'R' ) And ";
		}else if(jobType.equals("DutySlipUnbilled")){
			sSearchCriteria += "(  dutySlipStatus = 'C' ) And ";
		}
		sSearchCriteria = sSearchCriteria.substring(0, sSearchCriteria.length() - 4);
		String sSql= "";
		if(jobType.equals("DutySlipReceive")){
			 sSql = "FROM DutySlipModel " + sSearchCriteria + " ORDER By dispatchDateTime asc" ;
		}else if(jobType.equals("DutySlipClose")){
			 sSql = "FROM DutySlipModel " + sSearchCriteria + " ORDER By dutySlipReceivedByDate asc";
		}else if(jobType.equals("DutySlipUnbilled")){
			 sSql = "FROM DutySlipModel " + sSearchCriteria + " ORDER By dutySlipClosedByDate asc";
		}
		if(isForAll){ 
			sSql = "SELECT A.branch.id as branchId, A.branch.name as branchName, "
				 + "	   A.outlet.id as outletId, A.outlet.name as outletName, "
				 + "	   A.bookingMasterModel.corporateId.id as corporateId, A.bookingMasterModel.corporateId.name as corporateName,"
				 + "	   B.carDetailModel.id as carId, B.carDetailModel.registrationNo as carName,"
				 + " 	   A.rentalType.id as rentalTypeId, A.rentalType.name as rentalTypeName  "
				 + " FROM BookingDetailModel A, DutySlipModel B " + sSearchCriteria + ")";
			List<TempModel> tempModels = sessionFactory.getCurrentSession().createQuery(sSql).setResultTransformer(Transformers.aliasToBean(TempModel.class)).list();
			logger.info("Query Finish for Billing List Searching : " + sSql);
			dutySlipModels = new ArrayList<DutySlipModel>();
			for(TempModel TempModel : tempModels){
				DutySlipModel dutySlipModel = new DutySlipModel();
				BookingDetailModel bookingDetailModel = new BookingDetailModel();
				/*Branch*/
				GeneralMasterModel branch = new GeneralMasterModel();
				branch.setId(TempModel.getBranchId());
				branch.setName(TempModel.getBranchName());
				bookingDetailModel.setBranch(branch);
				/*Outlet*/
				GeneralMasterModel outlet = new GeneralMasterModel();
				outlet.setId(TempModel.getOutletId());
				outlet.setName(TempModel.getOutletName());
				bookingDetailModel.setOutlet(outlet);
				/*Rental Type*/
				GeneralMasterModel rentalType = new GeneralMasterModel();
				rentalType.setId(TempModel.getRentalTypeId());
				rentalType.setName(TempModel.getRentalTypeName());
				bookingDetailModel.setRentalType(rentalType);
				/*Corporate*/
				BookingMasterModel bookingMasterModel = new BookingMasterModel();
				CorporateModel corporateModel = new CorporateModel();
				corporateModel.setId(TempModel.getCorporateId());
				corporateModel.setName(TempModel.getCorporateName());
				bookingMasterModel.setCorporateId(corporateModel);
				bookingDetailModel.setBookingMasterModel(bookingMasterModel);
				/*Car No.*/
				CarDetailModel carDetailModel = new CarDetailModel();
				carDetailModel.setId(TempModel.getCarId());
				carDetailModel.setRegistrationNo(TempModel.getCarName());
				dutySlipModel.setCarDetailModel(carDetailModel);

				dutySlipModel.setBookingDetailModel(bookingDetailModel);
				dutySlipModels.add(dutySlipModel);
			}
		}else{
			dutySlipModels = sessionFactory.getCurrentSession().createQuery(sSql).list();
		}
//		dutySlipModel = sessionFactory.getCurrentSession().createQuery(sSql).setResultTransformer(Transformers.aliasToBean(DutySlipModel.class)).list();
		logger.info("Query Finish for Billing List Searching : " + sSql);
		return dutySlipModels;
	}
	@SuppressWarnings("unchecked")
	public List<AutorizedUserModel> getBookedBy() {
		List<AutorizedUserModel> autorizedUserModels = null;
		autorizedUserModels = sessionFactory.getCurrentSession().
				createQuery("SELECT DISTINCT name as name, id as id FROM AutorizedUserModel ").setResultTransformer(Transformers.aliasToBean(AutorizedUserModel.class)).list();
		return autorizedUserModels;
	}
	@SuppressWarnings("unchecked")
	public List <DutySlipModel> getds(String invoiceNo, Long lCorpId){
		List<DutySlipModel> dutySlipModels = null;
		String sSql = "SELECT A FROM DutySlipModel A, BookingDetailModel B, BookingMasterModel C "
					+ "		WHERE A.bookingDetailModel.id = B.id And B.bookingMasterModel.id = C.id And"
					+ "			  A.invoiceNo ='"+invoiceNo+"' And C.corporateId.id = '"+lCorpId+"' ";
		logger.info(sSql);
		dutySlipModels = sessionFactory.getCurrentSession().createQuery(sSql).list();
		return dutySlipModels;
	}
	@SuppressWarnings("unchecked")
	public List<CorporateTariffDetModel> getTariffValue(long tId,long corporateId,String sDate,long branchId) {
		List<CorporateTariffDetModel> corporateTariffDetModel = null;
		corporateTariffDetModel= sessionFactory.getCurrentSession()
				.createQuery("SELECT B.tariffValue as tariffValue, G.name as name, G.id as id, A.gstin as gstin, A.entityAddress as addressDetailModel "
						   + "FROM CorporateTariffModel A, CorporateTariffDetModel B, GeneralMasterModel G "
						   + "WHERE A.id = B.corporateTariffModel.id AND A.corporateId.id = "+corporateId+" AND "
						   + "		B.carCatId="	+tId+" AND A.branchId.id = " +branchId+ " And "
						   + "      A.fuelHikeDate = (SELECT max(C.fuelHikeDate) "
						   + "                        FROM CorporateTariffModel C, CorporateTariffDetModel D  "
						   + "                        WHERE C.id = D.corporateTariffModel.id And "
						   + "								C.corporateId.id = "+corporateId+" AND "
						   + "								C.branchId.id = A.branchId.id AND "	
						   + "								C.fuelHikeDate <= '"+sDate+"') AND B.tariffId = G.id"	).setResultTransformer(Transformers.aliasToBean(CorporateTariffDetModel.class)).list();
		return corporateTariffDetModel;
	}
	@SuppressWarnings("unchecked")
	public BookingMasterModel getDutySlipClosingBookingMasterDetail(long dutySlipDetailsId) {
	  List<BookingMasterModel> bookingMasterModels = null;
	  bookingMasterModels = sessionFactory.getCurrentSession().
	    createQuery("SELECT bookingNo as bookingNo FROM BookingMasterModel WHERE id ="
	    						+ "(SELECT bookingMasterModel as bookingMasterModel FROM BookingDetailModel WHERE id = "
	    								+ "(SELECT bookingDetailModel as bookingDetailModel FROM DutySlipModel where id = '"+dutySlipDetailsId+"') )")
	    										.setResultTransformer(Transformers.aliasToBean(BookingMasterModel.class)).list();
	   if(bookingMasterModels!=null && bookingMasterModels.size()>0){
			return bookingMasterModels.get(0);
		}
		return null;
	 }
	@SuppressWarnings("unchecked")
	public List<BookingMasterModel> getUsedBy() {
		List<BookingMasterModel> bookingMasterModels = null;
		bookingMasterModels = sessionFactory.getCurrentSession().
						createQuery("SELECT DISTINCT bookedForName as bookedForName FROM BookingMasterModel").setResultTransformer(Transformers.aliasToBean(BookingMasterModel.class)).list();
		return bookingMasterModels;
	}
	@SuppressWarnings("unchecked")
 	public List<UserModel> getClosedBy() {
		List<UserModel> userModels = null;
		userModels = sessionFactory.getCurrentSession().
				createQuery("SELECT DISTINCT userName as userName,id as id FROM UserModel ").setResultTransformer(Transformers.aliasToBean(UserModel.class)).list();
		return userModels;
	}
	@SuppressWarnings("unchecked")
	public List<DutySlipModel> getInvoiceNo(){
		 List<DutySlipModel> dutySlipModels = null;
		 dutySlipModels = sessionFactory.getCurrentSession()
				 	.createQuery("SELECT invoiceNo as invoiceNo FROM DutySlipModel where dutySlipStatus= 'I' ").setResultTransformer(Transformers.aliasToBean(DutySlipModel.class)).list();
			return dutySlipModels;
	}
	@SuppressWarnings("unchecked")
	public List<GeneralMasterModel> list(String corporateId) {
		List<GeneralMasterModel> generalMasterModel  = null;
		String sSql = "SELECT gm as name FROM GeneralMasterModel gm WHERE masterId=8 AND gm.id in (SELECT C.tariffId FROM CorporateTariffDetModel C WHERE corporateTariffId =(SELECT id FROM CorporateTariffModel WHERE corporateId ="+corporateId+"))";
		generalMasterModel  = sessionFactory.getCurrentSession().createQuery(sSql).list();
		return generalMasterModel ;
	}
	@SuppressWarnings("unchecked")
	public List<CarDetailModel> getCarRegistrationNo() {
		List<CarDetailModel> carDetailModel  = null;
		String sSql = "SELECT C FROM CarDetailModel C";
		carDetailModel  = sessionFactory.getCurrentSession().createQuery(sSql).list();
		return carDetailModel ;
	}
	@SuppressWarnings("unchecked")
	public List<BookingMasterModel> getBookedFor() {
		List<BookingMasterModel> bookingMasterModel = null;
		bookingMasterModel = sessionFactory.getCurrentSession().
				createQuery("SELECT DISTINCT B.bookedForName as bookedForName FROM BookingMasterModel B").list();
		return bookingMasterModel;
	}
	@SuppressWarnings("unchecked")
	public List<DutySlipModel> getDutySlipClosingDetail(String dutySlipRertrive) {
		List<DutySlipModel> dutySlipModels = null;
		dutySlipModels = sessionFactory.getCurrentSession().createQuery("FROM DutySlipModel WHERE dutySlipNo LIKE '"+dutySlipRertrive+"%'").list();
		for(DutySlipModel dutySlipModel : dutySlipModels){
			if(dutySlipModel.getDutySlipStatus().equals("I")){
				/*Find If invoice is multi or single*/
				String sSql = "select invoiceType from invoice where invoiceNo = '"+ dutySlipModel.getInvoiceNo() +"' and status = 'Y'";
				SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(sSql);
				List<String> result = (List<String>) sqlQuery.list();
				for(String row : result){
					dutySlipModel.setIsMultiInvoice(row.toString());
				}
				
				sSql = "select A.id from coverLetter A where find_in_set((select id from invoice where invoiceNo = '"+ dutySlipModel.getInvoiceNo() +"' and status = 'Y'),A.invoiceIds) <> 0";
				sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(sSql);
				List<BigInteger> rows = (List<BigInteger>) sqlQuery.list();
				for(BigInteger row : rows){
					dutySlipModel.setlCoverLetterId(Long.parseLong(row.toString()));
				}
			}
		}
		return dutySlipModels;
	}
	@SuppressWarnings("unchecked")
	public List<BookingMasterModel> getDutySlipClosingBookingMasterDetail(String dutySlipRertrive) {
		List<BookingMasterModel> bookingMasterModel = null;
		 bookingMasterModel = sessionFactory.getCurrentSession().
				createQuery("SELECT D FROM BookingMasterModel D WHERE D.id = (SELECT B.bookingMasterModel.id FROM BookingDetailModel B WHERE B.id = (SELECT M.bookingDetailModel.id FROM DutySlipModel M WHERE M.dutySlipNo LIKE '"+dutySlipRertrive+"%') )").list();
		return  bookingMasterModel;
	}
	public String save(HistoryDutySlipModel historyDutySlipModel) {
		sessionFactory.getCurrentSession().save(historyDutySlipModel);	
		return "saveSuccess";
	}
	@SuppressWarnings("unchecked")
	public CarAllocationModel getCarOwnerType(Long carDetailId){
		List<CarAllocationModel> carAllocationModels = null;
		carAllocationModels = sessionFactory.getCurrentSession()
				.createQuery("SELECT ca.carOwnerType as carOwnerType FROM CarAllocationModel ca, CarDetailModel cd "
											+ "WHERE ca.carDetailModelId = cd.registrationNo AND cd.id="+carDetailId).setResultTransformer(Transformers.aliasToBean(CarAllocationModel.class)).list();
		if(carAllocationModels!=null && carAllocationModels.size()>0){
			return carAllocationModels.get(0);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public  CorporateModel getCompany(String corporateId) {
		List<CorporateModel> corporateModels = null;
		corporateModels = sessionFactory.getCurrentSession().
				createQuery("from CorporateModel where id="+corporateId).list();
		if(corporateModels!=null && corporateModels.size()>0){
			return corporateModels.get(0);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<CorporateTariffDetModel> getTariffValues(long tId,long corporateId) {
	  List<CorporateTariffDetModel> corporateTariffDetModel = null;
	  corporateTariffDetModel= sessionFactory.getCurrentSession()
	    .createQuery("SELECT C.tariffValue as tariffValue,G.name as name FROM CorporateTariffDetModel C, GeneralMasterModel G WHERE C.carCatId=" +tId+" "
	             + "AND C.corporateTariffModel=(SELECT id FROM CorporateTariffModel WHERE corporateId = "+corporateId+") AND C.tariffId = G.id" ).setResultTransformer(Transformers.aliasToBean(CorporateTariffDetModel.class)).list();
	  return corporateTariffDetModel;
	 }
	@SuppressWarnings("unchecked")
	public  BookingDetailModel getBookingDetailId(long id) {
		List<BookingDetailModel> bookingDetailModel = null;
		bookingDetailModel = sessionFactory.getCurrentSession().
				createQuery("from BookingDetailModel where bookingMasterId="+id).list();
		bookingDetailModels=bookingDetailModel;
		if(bookingDetailModel!=null && bookingDetailModel.size()>0){
			return bookingDetailModel.get(0);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public DutySlipModel save(DutySlipModel dutySlipModel ,String sBookingNo) {
		RelatedInfoModel relatedInfoModel = null;
		String sSql = "FROM RelatedInfoModel WHERE parentId = " + dutySlipModel.getBookingDetailModel().getCompany().getId();

		List<RelatedInfoModel>  relatedInfoModelList =  (List<RelatedInfoModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
		long rId=00;
		long lDutySlipNo=0l;
		String sCompCode="";
		if(relatedInfoModelList.size() > 0){
			relatedInfoModel = relatedInfoModelList.get(0);
			lDutySlipNo = relatedInfoModel.getLastDS() == null ? 0 : relatedInfoModel.getLastDS();
			sCompCode  = relatedInfoModel.getGeneralMasterModel().getItemCode();
			rId=relatedInfoModel.getId();
			lDutySlipNo++;
		}
		sSql = "FROM GeneralMasterModel WHERE id = " + dutySlipModel.getBookingDetailModel().getBranch().getId();
		List<GeneralMasterModel> generalMasterModelList  =  (List<GeneralMasterModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
		String sBranchCode = generalMasterModelList.get(0).getItemCode();
		dutySlipModel.setDutySlipNo( lDutySlipNo +"-"+sBranchCode.toUpperCase()+"-"+new SimpleDateFormat("yyyy").format(dutySlipModel.getDutySlipCreatedByDate()));
		sessionFactory.getCurrentSession().save(dutySlipModel);
		relatedInfoModel.setLastDS(lDutySlipNo);
		sessionFactory.getCurrentSession().createQuery("UPDATE RelatedInfoModel SET lastDS = '"+lDutySlipNo+"' WHERE id = "+rId).executeUpdate();
		return dutySlipModel;
	}
	@SuppressWarnings("unchecked")
	public List<CarDetailModel> getAlocationCarRegistrationNo() {
		List<CarDetailModel> carDetailModel  = null;
		String sSql = "SELECT cd.registrationNo as registrationNo "
					+ "FROM CarDetailModel cd "
					+ "WHERE cd.registrationNo  in (SELECT ca.carDetailModelId "
					+ "								 FROM CarAllocationModel ca "
					+ "								 WHERE cd.registrationNo = ca.carDetailModelId and ca.carStatus = 'Y')";
		carDetailModel = sessionFactory.getCurrentSession().createQuery(sSql).setResultTransformer(Transformers.aliasToBean(CarDetailModel.class)).list();
		carDetailModel.addAll(sessionFactory.getCurrentSession().createQuery(""
				+ "SELECT cd.registrationNo as registrationNo FROM CarDetailModel cd where cd.ownType='A'")
				.setResultTransformer(Transformers.aliasToBean(CarDetailModel.class)).list());
		return carDetailModel ;
	}
	@SuppressWarnings("unchecked")
	public CarAllocationModel getCarOwner(String id) {
		List<CarAllocationModel> carAllocationModel  = null;
		String sSql = "SELECT C FROM CarAllocationModel C where id ='"+id+"'";
		carAllocationModel  = sessionFactory.getCurrentSession().createQuery(sSql).list();
		if(carAllocationModel!=null && carAllocationModel.size()>0){
			return carAllocationModel.get(0);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public CarAllocationModel getSearchCarReg(long id) {
		List<CarAllocationModel> carAllocationModel  = null;
		String sSql = "SELECT C FROM CarAllocationModel C where carDetailModelId. registrationNo =(SELECT registrationNo FROM CarDetailModel WHERE id  = "+id+")";
		carAllocationModel  = sessionFactory.getCurrentSession().createQuery(sSql).list();
		if(carAllocationModel!=null && carAllocationModel.size()>0){
			return carAllocationModel.get(0);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public TariffSchemeParaModel getTariffSchemePara(Long id) {
			List<TariffSchemeParaModel> tariffSchemeParaModels = null;
			tariffSchemeParaModels = sessionFactory.getCurrentSession().createQuery("FROM TariffSchemeParaModel WHERE parentId="+id).list();
			if( tariffSchemeParaModels.size()>0){
				return tariffSchemeParaModels.get(0);
			}
			return null;
		}
	@SuppressWarnings("unchecked")
	public List<CorporateTaxDetModel> getTaxs(long corporateId, String invoiceDate){
		  List<CorporateTaxDetModel> corporateTaxDetModels = null;
		  corporateTaxDetModels= sessionFactory.getCurrentSession().
		    createQuery("select C.name As name, A.taxVal As taxVal, C.sortId As userId from CorporateTaxDetModel A, TaxationModel B, GeneralMasterModel C "
					+ "where A.corporateModelId = " +corporateId+" And A.taxationModelId = B.id And B.parentId.id = C.id And  A.taxVal > 0 And B.efDate <= '"+invoiceDate+"' "
							+ "And  A.insDate = (SELECT max(D.insDate) FROM CorporateTaxDetModel D "
									+ "Where D.corporateModelId = A.corporateModelId And D.taxationModelId = A.taxationModelId And D.insDate <= '"+invoiceDate+"') ORDER BY userId").setResultTransformer(Transformers.aliasToBean(CorporateTaxDetModel.class)).list();
	  logger.info("Return : " + corporateTaxDetModels.size());
	  return corporateTaxDetModels;
	 }
	@SuppressWarnings("unchecked")
	public  DutySlipModel getCombineFare(String invoiceNo) {
		List<DutySlipModel> dutySlipModels = null;
		String sSql = "SELECT sum(coalesce(stateTax,0)) as stateTax,  sum(coalesce(miscCharge,0)) as miscCharge, "
				+ " sum(coalesce(parkingAmount,0)) as parkingAmount,  sum(coalesce(tollTaxAmount,0)) as tollTaxAmount,"
				+ " sum(coalesce(basicFare,0)) as basicFare,  sum(coalesce(totalFare,0)) as totalFare, "
				+ " sum(coalesce(guideCharge,0)) as guideCharge,  sum(coalesce(fuelCharge,0)) as fuelCharge "
				+ " FROM DutySlipModel where invoiceNo='"+invoiceNo+"' GROUP By invoiceNo ORDER By invoiceNo asc";
		dutySlipModels = sessionFactory.getCurrentSession().createQuery(sSql).setResultTransformer(Transformers.aliasToBean(DutySlipModel.class)).list();
		if(dutySlipModels!=null && dutySlipModels.size()>0){
			return dutySlipModels.get(0);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<CarAllocationModel> getAlocationCarNo(Long modelId) {
		List<CarAllocationModel> carAllocationModels  = null;
		String sSql = "FROM CarAllocationModel CA WHERE CA.carDetailModelId IN(SELECT C.registrationNo FROM CarDetailModel C WHERE C.model.id='"+modelId+"') AND CA.carStatus='Y'";
		carAllocationModels  = sessionFactory.getCurrentSession().createQuery(sSql).list();
		return carAllocationModels ;
	}
	@SuppressWarnings("unchecked")
	public List<TariffSchemeParaModel> getTariffSchemePara(Long[] id) {
			List<TariffSchemeParaModel> tariffSchemeParaModels = null;
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(TariffSchemeParaModel.class).add(Restrictions.in("parentId",id));
						 criteria.addOrder(Order.asc("minKM"));
			tariffSchemeParaModels = criteria.list();
			return tariffSchemeParaModels;
	}
	@SuppressWarnings("unchecked")
	public  BookingDetailModel getBookingDetailModel(long id) {
		List<BookingDetailModel> bookingDetailModel = null;
		bookingDetailModel = sessionFactory.getCurrentSession().
				createQuery("from BookingDetailModel where id="+id).list();
		bookingDetailModels=bookingDetailModel;
		if(bookingDetailModel!=null && bookingDetailModel.size()>0){
			return bookingDetailModel.get(0);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<CorporateTaxDetModel> getAllTaxes(long corporateId, String invoiceDate){
		  List<CorporateTaxDetModel> corporateTaxDetModels = null;
		  corporateTaxDetModels= sessionFactory.getCurrentSession().
		    createQuery("select C.name As name, A.taxVal As taxVal, C.sortId As userId from CorporateTaxDetModel A, TaxationModel B, GeneralMasterModel C "
					+ "where A.corporateModelId = " +corporateId+" And A.taxationModelId = B.id And B.parentId.id = C.id  And (B.endDate >='"+invoiceDate+"' OR B.endDate is null) " 
					+ "And  A.insDate = (SELECT max(D.insDate) FROM CorporateTaxDetModel D "
					+ "Where D.corporateModelId = A.corporateModelId And D.taxationModelId = A.taxationModelId And D.insDate <= '"+invoiceDate+"') ORDER BY C.sortId").setResultTransformer(Transformers.aliasToBean(CorporateTaxDetModel.class)).list();
	  logger.debug("Return : " + corporateTaxDetModels.size());
	  return corporateTaxDetModels;
	 }

	@Override
	public InvoiceModel saveInvoice(InvoiceModel invoiceModel) {
		String sInvoiceNo = invoiceModel.getInvoiceNo();
//		long lCorpId = invoiceModel.getCorpId();
		long lCoverLetterId = invoiceModel.getCoverLetterId() == null ? 0 : invoiceModel.getCoverLetterId();
		long lNewCoverLetterId = invoiceModel.getlNewCoverLetterId() == null ? 0 : invoiceModel.getlNewCoverLetterId();
		invoiceModel.setCoverLetterId(lNewCoverLetterId);	
		try{
			sessionFactory.getCurrentSession().createQuery("UPDATE InvoiceModel SET status = 'N' WHERE invoiceNo = '" + sInvoiceNo + "'").executeUpdate();
			invoiceModel.setStatus("Y");
			sessionFactory.getCurrentSession().save(invoiceModel);	
	
			/*Updating Cover Letter for Invoice*/
			String sSql = "update coverLetter "
					+ "		set invoiceIds = concat( (select group_concat(A.id) "
					+ "								  from invoice A 			"
					+ "								  where (A.invoiceNo in (select B.invoiceNo from invoice B where concat(',', invoiceIds) like concat('%,',B.id,',%' )) Or "
					+ "										 A.invoiceNo = '"+ sInvoiceNo +"') And "
					+ "                                      A.corpId = corporateId And "
					+ "									     A.status = 'Y'),','), "
					+ "			totalLetterAmount = (select sum(A.grandTotal) "
					+ "								 from invoice A  "
					+ "								 where (A.invoiceNo in (select B.invoiceNo from invoice B where concat(',', invoiceIds) like concat('%,',B.id,',%' )) Or "
					+ "										A.invoiceNo = '"+ sInvoiceNo +"') And "
					+ "                                     A.corpId = corporateId And "
					+ "									    A.status = 'Y') "     
					+ "	where (select count(*) from invoice C where concat(',',invoiceIds) like concat('%,',C.id,',%' ) and C.status ='N' and C.invoiceNo = '"+ sInvoiceNo +"') > 0 Or"
					+ "        id = '"+ lCoverLetterId +"' Or id = '"+ lNewCoverLetterId +"'";
			SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(sSql);
			sqlQuery.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("",e);
		}
		return invoiceModel;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceModel> getInvoice(String invoiceNo, long lCorpId) {
		String[] sArrInvoices = invoiceNo.split(","); 
		Long[] invoiceIds = new Long[sArrInvoices.length];
		for (int iLoop = 0; iLoop < sArrInvoices.length; iLoop++) {
			try{
				invoiceIds[iLoop] = Long.parseLong(sArrInvoices[iLoop]);
			}catch(Exception e){}
		}
		List<InvoiceModel> invoiceModels  = null;
		String sSql = "FROM InvoiceModel WHERE (invoiceNo = '"+invoiceNo+"' OR '"+invoiceNo+"' = '0' OR id in (:invoiceArray) ) And status = 'Y' And (corpId = '"+lCorpId+"' Or '"+lCorpId+"' = '0') Order By SUBSTRING(invoiceNo,1,5)";
		Query query = sessionFactory.getCurrentSession().createQuery(sSql.toString());
		query.setParameterList("invoiceArray", invoiceIds);
		invoiceModels  = (List<InvoiceModel>) query.list();
		return invoiceModels ;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<MasterModel> getAllInvoiceNo() {
		List<MasterModel> masterModels  = null;
		String sSql = "SELECT distinct A.invoiceNo as name, A.invoiceGenerateBy as id, C.corporateId.id as parentid "
					+ "FROM DutySlipModel A, BookingDetailModel B, BookingMasterModel C "
					+ "WHERE A.bookingDetailModel.id = B.id And B.bookingMasterModel.id = C.id And "
					+ "dutySlipStatus ='I' And A.invoiceNo Not In (SELECT D.invoiceNo FROM InvoiceModel D WHERE D.status = 'Y') ";
					
//					+ "(SELECT count(*) FROM InvoiceModel D "
//					+ "		WHERE D.invoiceNo = A.invoiceNo And D.corpId = C.corporateId.id And D.status = 'Y') = 0 ";
		masterModels  = (List<MasterModel>) sessionFactory.getCurrentSession().createQuery(sSql).setResultTransformer(Transformers.aliasToBean(MasterModel.class)).list();
		return masterModels ;
	}
	@SuppressWarnings("unchecked")
	public List<InvoiceModel> getInvoiceList( String[] sCriteriaList, String[] sValueList) {
		List<InvoiceModel> invoiceModels = null;
		Long[] sInvoices = null;
		String sSearchCriteria = " WHERE ", sType = "",sFromDate="",sToDate="";
		Date dtFrom = new Date(), dtTo = new Date();

		for (int iCount = 0; iCount < sCriteriaList.length; iCount++) {
			String sCriteria = sCriteriaList[iCount];
			String sValue = sValueList[iCount];
			if (sCriteria.equals("invoiceFromDate") || sCriteria.equals("invoiceToDate")) {
				try {
					Date dDate = dateFormat.parse(sValue);
					sValue = new SimpleDateFormat("yyyy-MM-dd").format(dDate);
					if(sCriteria.equals("invoiceFromDate")){
						sFromDate = sValue;
						dtFrom = dDate;
					}
					if(sCriteria.equals("invoiceToDate")){
						sToDate = sValue;
						dtTo = dDate;
					}
				} catch (ParseException e) {
					logger.error("",e);
				}
			}else if(sCriteria.equals("sType")){
				sType = sValue;
			}

			if(sCriteria.equals("corpName")){
				sSearchCriteria += " (A.corpId = '" + sValue + "' OR '" + sValue + "' = '0' ) And ";
			}else if(sCriteria.equals("invoiceNo")){
				sSearchCriteria += " (A.invoiceNo = '" +sValue +"' OR '" + sValue + "' = '0')  And ";
			}else if(sCriteria.equals("closedBy")){
				sSearchCriteria += " (A.closedBy = '" +sValue +"' OR '" + sValue + "' = '0')  And ";
			}else if(sCriteria.equals("bookedBy")){
				sSearchCriteria += " (A.bookedBy = '" +sValue +"' OR '" + sValue + "' = '0')  And ";
			}else if(sCriteria.equals("branch")){
				sSearchCriteria += " (A.branchId = '" +sValue +"' OR '" + sValue + "' = '0')  And ";
			}else if(sCriteria.equals("hub")){
				sSearchCriteria += " (A.hubId = '" +sValue +"' OR '" + sValue + "' = '0')  And ";
			}else if(sCriteria.equals("usedBy")){
				sSearchCriteria += " (A.usedBy = '" +sValue +"' OR '" + sValue + "' = '0')  And ";
			}else if(sCriteria.equals("status")){
				sSearchCriteria += " (A.status = '" +sValue +"' OR '" + sValue + "' = '0')  And ";
			}else if(sCriteria.equals("jobType")){
				if(sValue.equalsIgnoreCase("CoverLetter")){
					List<CoverLetterModel> coverLetterModels  = null;
					String sSql = "FROM CoverLetterModel";
					coverLetterModels  = (List<CoverLetterModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
					String coverLetterInvoices = "";
					for(CoverLetterModel coverLetterModel : coverLetterModels){
						if(coverLetterModel.getInvoiceIds() != null) coverLetterInvoices += coverLetterModel.getInvoiceIds();
					}
					if(!coverLetterInvoices.equals("")){
						String[] sInvArr = coverLetterInvoices.split(",");
						if(sInvArr != null){
							sInvoices = new Long[sInvArr.length];
							for(int iIdx = 0; iIdx < sInvArr.length ; iIdx++){
								if(sInvArr[iIdx].equals(""))
									sInvoices[iIdx] = Long.parseLong("0");
								else
									sInvoices[iIdx] = Long.parseLong(sInvArr[iIdx]);
							}
						}
					}else{
						sInvoices = new Long[1];
						sInvoices[0] = Long.parseLong("0");
					}
					sSearchCriteria += " (A.id not in (:coverLetterInvoices) )  And ";
				}
			}
		}
		
		if(sType.equals("I")){
			sSearchCriteria += " (A.invoiceDt  BETWEEN '" + sFromDate + "'  And '"+ sToDate +"') ";
		}else if(sType.equals("D")){
			sSearchCriteria += " ( (A.id in (SELECT B.invoiceModel.id FROM InvoiceDetailModel B Where B.dsDt BETWEEN '" + sFromDate + "'  And '"+ sToDate +"') And  A.invoiceType ='M' ) Or "
							 + "	   (A.invoiceType ='S' And A.dsDt  BETWEEN '" + sFromDate + "'  And '"+ sToDate +"') )";
		}

		String sSql = "FROM InvoiceModel A " + sSearchCriteria + " ORDER By corpName,invoiceNo asc";
		logger.info("Query for Invoice List Searching : "+sSql);
		if(sInvoices != null){
			Query query = sessionFactory.getCurrentSession().createQuery(sSql.toString());
			query.setParameterList("coverLetterInvoices", sInvoices);
			invoiceModels  = (List<InvoiceModel>) query.list();
		}else{
			invoiceModels = (List<InvoiceModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
			if(sType.equals("D")){
				for(InvoiceModel invoiceModel : invoiceModels){
					if(invoiceModel.getInvoiceType().equals("M")){
						List<InvoiceDetailModel> invoiceDetailModelList = new ArrayList<InvoiceDetailModel>();
						for(InvoiceDetailModel invoiceDetailModel : invoiceModel.getInvoiceDetailModel()){
							if(invoiceDetailModel.getDsDt().before(dtFrom) || invoiceDetailModel.getDsDt().after(dtTo))continue;
							invoiceDetailModelList.add(invoiceDetailModel);
						}
						invoiceModel.setInvoiceDetailModel(invoiceDetailModelList);
					}
				}
			}
		}
		return invoiceModels;
	}
	@Override
	public CoverLetterModel saveCoverLetter(CoverLetterModel coverLetterModel) {
		RelatedInfoModel relatedInfoModel = null;
		String sSql = "FROM RelatedInfoModel WHERE parentId = " + coverLetterModel.getCompId();
		
		@SuppressWarnings("unchecked")
		List<RelatedInfoModel>  relatedInfoModelList =  (List<RelatedInfoModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();

		long lCoverLetterNo=0l;
		String sCompCode="";
		if(relatedInfoModelList.size() > 0){
			relatedInfoModel = relatedInfoModelList.get(0);
			lCoverLetterNo = relatedInfoModel.getLastCoverLetter() == null ? 0 : relatedInfoModel.getLastCoverLetter();
			sCompCode  = relatedInfoModel.getGeneralMasterModel().getItemCode();
			lCoverLetterNo++;
		}
		sSql = "FROM GeneralMasterModel WHERE id = " + coverLetterModel.getBranchId();
		@SuppressWarnings("unchecked")
		List<GeneralMasterModel> generalMasterModelList  =  (List<GeneralMasterModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
		String sBranchCode = generalMasterModelList.get(0).getItemCode();
		coverLetterModel.setLetterNo(String.valueOf(lCoverLetterNo));
		sessionFactory.getCurrentSession().save(coverLetterModel);	
		relatedInfoModel.setLastCoverLetter(lCoverLetterNo);
		sessionFactory.getCurrentSession().update(relatedInfoModel);
		return coverLetterModel;
	}
	@Override
	public CoverLetterModel getCoverLetter(Long coverLetterId) {
		CoverLetterModel coverLetterModel=null;
		coverLetterModel =(CoverLetterModel) sessionFactory.getCurrentSession().get(CoverLetterModel.class, new Long(coverLetterId));
		return coverLetterModel;
	}
	@SuppressWarnings("unchecked")
	public List<DutySlipModel> getDutySlipList( String[] sCriteriaList, String[] sValueList) {
		List<DutySlipModel> dutySlipModelList = null;
		String sSearchCriteria = " WHERE ";
		for (int iCount = 0; iCount < sCriteriaList.length; iCount++) {
			String sCriteria = sCriteriaList[iCount];
			String sValue = sValueList[iCount];
			
			if (sCriteria.equals("invoiceFromDate") || sCriteria.equals("invoiceToDate")) {
				try {
					Date dDate = dateFormat.parse(sValue);
					sValue = new SimpleDateFormat("yyyy-MM-dd").format(dDate);
				} catch (ParseException e) {
					logger.error("",e);
				}
			}else if(sCriteria.equals("sType") && sValue.equals("D")){
				sSearchCriteria += "dutySlipStatus <> 'I' And ";
			}
			
			if (sCriteria.equals("invoiceFromDate")) {
				sSearchCriteria += "(dateFrom  BETWEEN '" + sValue + "'  And ";
			}else if (sCriteria.equals("invoiceToDate")) {
				sSearchCriteria += "'" + sValue + "') ";
			}else if(sCriteria.equals("corpName")){
				sSearchCriteria += " And (bookingDetailModel.bookingMasterModel.corporateId.id = '" + sValue + "' OR '" + sValue + "' = '0' )";
			}else if(sCriteria.equals("branch")){
				sSearchCriteria += " And (bookingDetailModel.branch.id = '" +sValue +"' OR '" + sValue + "' = '0') ";
			}else if(sCriteria.equals("outlet")){
				sSearchCriteria += " And (bookingDetailModel.outlet.id = '" +sValue +"' OR '" + sValue + "' = '0') ";
			}
		}
		
		
		String sSql = "FROM DutySlipModel " + sSearchCriteria + " ORDER By bookingDetailModel.bookingMasterModel.corporateId.name, dutySlipNo";
		logger.info("Query for DutySlip List Searching : "+sSql);
		dutySlipModelList = (List<DutySlipModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
		return dutySlipModelList;
	}
	
	@Override
	public String savePaymentTransaction(TransactionDetailModel transactionDetailModel) {
		sessionFactory.getCurrentSession().save(transactionDetailModel);
		return "saveSuccess";
	}

}
