package com.operation.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.billing.model.InvoiceModel;
import com.master.model.CarAllocationModel;
import com.master.model.GeneralMasterModel;
import com.operation.model.BookingDetailModel;
import com.operation.model.CarDetailModel;
import com.operation.model.DeallocateHistoryModel;
import com.operation.model.DutySlipModel;
import com.operation.viewModel.BookingDetailsWithVehicleAllocationModel;
import com.operation.viewModel.DutySlipReceiveViewModel;
import com.relatedInfo.model.RelatedInfoModel;

@Repository("dutySlipDao")
public class DutySlipDaoImpl implements DutySlipDao{
	
	@Autowired
	private SessionFactory sessionFactory;
	private static int pageSize = 3;
	
	@SuppressWarnings("unchecked")
	public List<BookingDetailsWithVehicleAllocationModel> list(int pageNumber) {
		List<BookingDetailsWithVehicleAllocationModel> bookingDetailsWithVehicleAllocationModel = null;
		String hqlQuery =" SELECT bdm.id as bookingDetailId,(SELECT userName FROM UserModel WHERE id=(SELECT customerName.id FROM BookingMasterModel "+ 
			" WHERE id=bdm.bookingMasterModel.id)) as customerName,"+
		    " (SELECT name FROM GeneralMasterModel WHERE id=cdm.carType.id) as carType,"+
		    " (SELECT gmm.name FROM GeneralMasterModel gmm , TariffParameterMasterModel tpm WHERE gmm.id= tpm.tariff.id and tpm.id=bdm.tariff.id) as tariffType,"+
		    " cdm.registrationNo as carNo,"+
		    " (SELECT name FROM GeneralMasterModel WHERE id=cdm.model.id) as carName,"+
		    " bdm.usedBy as usedBy,"+
		    " (SELECT userName FROM UserModel WHERE id=(SELECT bookedBy.id FROM BookingMasterModel WHERE id=bdm.bookingMasterModel.id)) as bookingBy"+
		    " FROM VehicleAllocationModel vam ,BookingDetailModel bdm ,CarDetailModel cdm"+
		    " WHERE vam.vehicleId.id=cdm.id"+
		    " AND vam.bookingId.id=bdm.id"+
		    " AND bdm.id NOT IN (SELECT bookingDetailModel.id FROM DutySlipModel) ";
		Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery).
				setFirstResult(pageSize * (pageNumber - 1)).
				setMaxResults(pageSize).setResultTransformer(Transformers.aliasToBean(BookingDetailsWithVehicleAllocationModel.class));
		bookingDetailsWithVehicleAllocationModel = (List<BookingDetailsWithVehicleAllocationModel>)query.list();
		return bookingDetailsWithVehicleAllocationModel;
	}
	
	@SuppressWarnings("unchecked")
	public List<BookingDetailsWithVehicleAllocationModel> listMasterWise(int pageNumber,String searchCriteria) {
		List<BookingDetailsWithVehicleAllocationModel> bookingDetailsWithVehicleAllocationModel = null;
		String hqlQuery =" SELECT bdm.id as bookingDetailId,(SELECT userName FROM UserModel WHERE id=(SELECT customerName.id FROM BookingMasterModel "+ 
			" WHERE id=bdm.bookingMasterModel.id)) as customerName,"+
		    " (SELECT name FROM GeneralMasterModel WHERE id=cdm.carType.id) as carType,"+
		    " (SELECT name FROM GeneralMasterModel WHERE id=bdm.tariff.id) as tariffType,"+
		    " cdm.registrationNo as carNo,"+
		    " (SELECT name FROM GeneralMasterModel WHERE id=cdm.model.id) as carName,"+
		    " bdm.usedBy as usedBy,"+
		    " (SELECT userName FROM UserModel WHERE id=(SELECT bookedBy.id FROM BookingMasterModel WHERE id=bdm.bookingMasterModel.id)) as bookingBy"+
		    " FROM VehicleAllocationModel vam ,BookingDetailModel bdm ,CarDetailModel cdm"+
		    " WHERE vam.vehicleId.id=cdm.id"+
		    " AND vam.bookingId.id=bdm.id"+
		    " AND bdm.id NOT IN (SELECT bookingDetailModel.id FROM DutySlipModel) ";
		if(searchCriteria!=null && !searchCriteria.trim().equalsIgnoreCase(""))
			hqlQuery=hqlQuery+"and bdm.bookingMasterModel.id="+Long.parseLong(searchCriteria)+"";
		Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery).
				setFirstResult(pageSize * (pageNumber - 1)).
				setMaxResults(pageSize).setResultTransformer(Transformers.aliasToBean(BookingDetailsWithVehicleAllocationModel.class));
		bookingDetailsWithVehicleAllocationModel = (List<BookingDetailsWithVehicleAllocationModel>)query.list();
		return bookingDetailsWithVehicleAllocationModel;
	}

	@SuppressWarnings("unchecked")
	public List<CarDetailModel> getVehicleTypeData(Long selectCarTypeId,String searchCriteria,Long bookingId) {
		List<BookingDetailModel> bookingDetailModelList = sessionFactory.getCurrentSession().createCriteria(BookingDetailModel.class).
				setProjection(Projections.projectionList().
						add(Projections.alias(Projections.property("bookingDate"),"bookingDate"))).
						setResultTransformer(Transformers.aliasToBean(BookingDetailModel.class)).
						add(Restrictions.idEq(bookingId)).list();
		List<CarDetailModel> carDetailModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CarDetailModel.class,"cdm").
				setProjection(Projections.projectionList().
						add(Projections.alias(Projections.property("cdm.id"),"id")).
						add(Projections.alias(Projections.property("cdm.make"),"make")).
						add(Projections.alias(Projections.property("cdm.model"),"model")).
						add(Projections.alias(Projections.property("cdm.bodyColor"),"bodyColor")).
						add(Projections.alias(Projections.property("cdm.registrationNo"),"registrationNo"))).
				setResultTransformer(Transformers.aliasToBean(CarDetailModel.class));
		criteria.add(Restrictions.eq("cdm.carType.id", selectCarTypeId));
		criteria.add(Subqueries.propertyNotIn("cdm.id",DetachedCriteria.forClass(DutySlipModel.class,"vam").
				createCriteria("vam.bookingId","bdm").
				add(Restrictions.eq("bdm.bookingDate",bookingDetailModelList.get(0).getPickUpDate())).
				setProjection(Projections.distinct(Projections.property("vam.vehicleId.id")))));
		if(searchCriteria!=null && !searchCriteria.trim().equalsIgnoreCase(""))
			criteria.add(Restrictions.like("cdm.registrationNo",searchCriteria,MatchMode.ANYWHERE));
		carDetailModel = (List<CarDetailModel>)criteria.list();
		return carDetailModel;
	}

	public String save(DutySlipModel dutySlipModel) {
		sessionFactory.getCurrentSession().save(dutySlipModel);	
		return "saveSuccess";
	}
	
	public DutySlipModel formFillForEdit(Long formFillForEditId) {
		DutySlipModel dutySlipModel=null;
		dutySlipModel =(DutySlipModel) sessionFactory.getCurrentSession().get(DutySlipModel.class, new Long(formFillForEditId));
		return dutySlipModel;
	}
	
	public String delete(Long idForDelete) {
		DutySlipModel dutySlipModel =(DutySlipModel) sessionFactory.getCurrentSession().get(DutySlipModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(dutySlipModel);
		return "deleteSuccess";
	}
	
	@SuppressWarnings("unchecked")
	public String update(DutySlipModel dutySlipModel) {
		String sReturnValue = "updateSuccess";
		if(dutySlipModel.getDutySlipStatus().equals("I") && dutySlipModel.getInvoiceNo().substring(0,5).equals("NewIn")){
			RelatedInfoModel relatedInfoModel = null;
			String sSql = "FROM RelatedInfoModel WHERE parentId = " + dutySlipModel.getBookingDetailModel().getCompany().getId();
			
			List<RelatedInfoModel>  relatedInfoModelList =  (List<RelatedInfoModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
	
			long lInvoiceNo=0l;
			if(relatedInfoModelList.size() > 0){
				relatedInfoModel = relatedInfoModelList.get(0);
				lInvoiceNo = relatedInfoModel.getLastInvoice() == null ? 0 : relatedInfoModel.getLastInvoice();
				lInvoiceNo++;
			}
			sSql = "FROM GeneralMasterModel WHERE id = " + dutySlipModel.getBookingDetailModel().getBranch().getId();
			List<GeneralMasterModel> generalMasterModelList  =  (List<GeneralMasterModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
			String sBranchCode = generalMasterModelList.get(0).getItemCode();
			
			sSql = "FROM InvoiceModel WHERE compId = " + dutySlipModel.getBookingDetailModel().getCompany().getId() + " And invoiceNo LIKE '"+lInvoiceNo+"%'";
			List<InvoiceModel> invoiceModelList  =  (List<InvoiceModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();

			if(invoiceModelList.size() == 0){
				dutySlipModel.setInvoiceNo(lInvoiceNo +"-"+sBranchCode.toUpperCase()+"-"+new SimpleDateFormat("yyyy").format(dutySlipModel.getDutySlipCreatedByDate()));
	
				dutySlipModel.setInvoiceNo(dutySlipModel.getInvoiceNo());
				relatedInfoModel.setLastInvoice(lInvoiceNo);
				sessionFactory.getCurrentSession().update(relatedInfoModel);
				sReturnValue = dutySlipModel.getInvoiceNo();
			}else{
				sReturnValue = "Duplicate";
			}
		}
		sessionFactory.getCurrentSession().update(dutySlipModel);
		return sReturnValue;
	}

	@SuppressWarnings("unchecked")
	public List getDataOnBookingSelection_DutySlip(Long bookingDetailId) {
		String sql=" SELECT bdm.bookingMasterModel.id,tpm.minChgKms,tpm.minChgHrs,tpm.extKmsRate, "+
	    	 " tpm.extHrsRate,bdm.driverName.id,(SELECT cdm.ownType.id FROM CarDetailModel cdm,VehicleAllocationModel vdm "+
	    	 " WHERE vdm.bookingId.id = bdm.id AND cdm.id = vdm.vehicleId.id),tpm.effectiveDate "+
	    	 " FROM BookingDetailModel bdm,TariffParameterMasterModel tpm "+
	    	 " WHERE bdm.carType.id = tpm.carType.id "  +
	    	 " AND bdm.tariff.id = tpm.id  "+
	    	 " AND bdm.carModel.id = tpm.carModel.id "+
	    	 " AND bdm.id = :bookingDetailId "+
	    	 " And tpm.effectiveDate = (SELECT MAX(effectiveDate) FROM TariffParameterMasterModel tpm1 "+ 
							" where bdm.carType.id = tpm1.carType.id "  +
							" AND bdm.tariff.id = tpm1.id "+
							" AND bdm.carModel.id = tpm1.carModel.id "+
							" AND tpm1.effectiveDate = bdm.bookingDate)";
	     Query query = sessionFactory.getCurrentSession().createQuery(sql);
	     query.setParameter("bookingDetailId", bookingDetailId);
	     return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<DutySlipReceiveViewModel> listDutySlipReceive(int pageNumber,String searchCriteria) {
		List<DutySlipReceiveViewModel> dutySlipReceiveViewModel = null;
		String hqlQuery = " SELECT dsm.id AS dutySlipId, "+
						  " bdm.outlet AS hub,"+
						  " cdm.registrationNo AS carNo,"+
						  "	dsm.deviceStatus AS deviceStatus,"+
						  " (SELECT gmm.name FROM GeneralMasterModel gmm WHERE gmm.id=cdm.model.id) AS allotedCar,"+
						  "	(SELECT gmm.name FROM GeneralMasterModel gmm WHERE gmm.id=bdm.carModel.id) AS bookedCar,"+
						  "	dsm.dutySlipDate AS dutySlipDate,"+
					 	  " dsm.dutySlipNo AS dutySlipNo,"+
						  " (SELECT gmm.name FROM GeneralMasterModel gmm WHERE gmm.id=bdm.rentalType.id) AS plan, "+
						  " (SELECT udm.userName FROM BookingMasterModel bmm,UserModel udm "+
						  "				 WHERE bmm.id=bdm.bookingMasterModel.id AND bmm.customerName.id=udm.id) AS corporate,"+
						  " (SELECT udm.userName FROM BookingMasterModel bmm,UserModel udm "+
						  "				 WHERE bmm.id=bdm.bookingMasterModel.id AND bmm.bookedBy.id=udm.id) AS bookedBy,"+
						  " bdm.usedBy AS usedBy,"+
						  " (SELECT contDM.personalMobile FROM BookingMasterModel bmm,UserModel udm ,ContactDetailModel contDM"+ 
						  "							  WHERE bmm.id=bdm.bookingMasterModel.id AND bmm.bookedBy.id=udm.id"+ 
						  "							  AND contDM.userModel.id=udm.id) AS mobileNo"+
						  " FROM DutySlipModel dsm, BookingDetailModel bdm, VehicleAllocationModel vam, CarDetailModel cdm"+
						  " WHERE bdm.id=dsm.bookingDetailModel.id"+
						  " AND vam.vehicleId.id=cdm.id"+
						  " AND vam.bookingId.id=bdm.id AND dsm.isDutySlipReceived=0 ";
						  if(searchCriteria!=null && !searchCriteria.equalsIgnoreCase(""))
							  	hqlQuery=hqlQuery+ " AND cdm.registrationNo like:carNo ";
		Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery).
				setFirstResult(6 * (pageNumber - 1)).
				setMaxResults(6).setResultTransformer(Transformers.aliasToBean(DutySlipReceiveViewModel.class));
		if(searchCriteria!=null && !searchCriteria.equalsIgnoreCase(""))
			query.setString("carNo", "%"+searchCriteria+"%");
		dutySlipReceiveViewModel = (List<DutySlipReceiveViewModel>)query.list();
		return dutySlipReceiveViewModel;
	}

	@Override
	public String saveDutySlipReceive(List<DutySlipModel> dutySlipModels) {
		for(DutySlipModel dutySlipModel:dutySlipModels){
			DutySlipModel dutySlipModel1= (DutySlipModel) sessionFactory.getCurrentSession().get(DutySlipModel.class, dutySlipModel.getId());
			dutySlipModel1.setTollTaxAmount(dutySlipModel.getTollTaxAmount());
			dutySlipModel1.setParkingAmount(dutySlipModel.getParkingAmount());
			dutySlipModel1.setIsDutySlipReceived(true);
			dutySlipModel1.setDutySlipReceivedBy(dutySlipModel.getDutySlipReceivedBy());
			dutySlipModel1.setDutySlipReceivedByDate(new Date());
			sessionFactory.getCurrentSession().update(dutySlipModel1);
		}	
		return "saveSuccess";
	}
	
	@SuppressWarnings("unchecked")
	public List<DutySlipReceiveViewModel> listDutySlipClose(int pageNumber,String searchCriteria) {
		List<DutySlipReceiveViewModel> dutySlipReceiveViewModel = null;
		String hqlQuery = " SELECT dsm.id AS dutySlipId, "+
						  " bdm.outlet AS hub,"+
						  " cdm.registrationNo AS carNo,"+
						  "	dsm.deviceStatus AS deviceStatus,"+
						  " (SELECT gmm.name FROM GeneralMasterModel gmm WHERE gmm.id=cdm.model.id) AS allotedCar,"+
						  "	(SELECT gmm.name FROM GeneralMasterModel gmm WHERE gmm.id=bdm.carModel.id) AS bookedCar,"+
						  "	dsm.dutySlipDate AS dutySlipDate,"+
					 	  " dsm.dutySlipNo AS dutySlipNo,"+
						  " (SELECT gmm.name FROM GeneralMasterModel gmm WHERE gmm.id=bdm.rentalType.id) AS plan, "+
						  " (SELECT udm.userName FROM BookingMasterModel bmm,UserModel udm "+
						  "				 WHERE bmm.id=bdm.bookingMasterModel.id AND bmm.customerName.id=udm.id) AS corporate,"+
						  " (SELECT udm.userName FROM BookingMasterModel bmm,UserModel udm "+
						  "				 WHERE bmm.id=bdm.bookingMasterModel.id AND bmm.bookedBy.id=udm.id) AS bookedBy,"+
						  " bdm.usedBy AS usedBy,"+
						  " (SELECT contDM.personalMobile FROM BookingMasterModel bmm,UserModel udm ,ContactDetailModel contDM"+ 
						  "							  WHERE bmm.id=bdm.bookingMasterModel.id AND bmm.bookedBy.id=udm.id"+ 
						  "							  AND contDM.userModel.id=udm.id) AS mobileNo"+
						  " FROM DutySlipModel dsm, BookingDetailModel bdm, VehicleAllocationModel vam, CarDetailModel cdm"+
						  " WHERE bdm.id=dsm.bookingDetailModel.id"+
						  " AND vam.vehicleId.id=cdm.id"+
						  " AND vam.bookingId.id=bdm.id AND dsm.isDutySlipReceived=1 AND dsm.isDutySlipClosed=0";
						  if(searchCriteria!=null && !searchCriteria.equalsIgnoreCase(""))
							  	hqlQuery=hqlQuery+ " AND cdm.registrationNo like:carNo ";
		Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery).
				setFirstResult(3 * (pageNumber - 1)).
				setMaxResults(3).setResultTransformer(Transformers.aliasToBean(DutySlipReceiveViewModel.class));
		if(searchCriteria!=null && !searchCriteria.equalsIgnoreCase(""))
			query.setString("carNo", "%"+searchCriteria+"%");
		dutySlipReceiveViewModel = (List<DutySlipReceiveViewModel>)query.list();
		return dutySlipReceiveViewModel;
	}

	@Override
	public List getDataOnSelection_DutySlipClose(Long dutySlipId) {
		String sql= " SELECT dsm.bookingMasterModel.id,dsm.manualSlipNo,dsm.openKms,dsm.timeFrom,dsm.dateFrom,dsm.bookingDetailModel.tariff.id "+ 
				    " FROM DutySlipModel dsm "+ 
				    " WHERE dsm.id=:dutySlipId ";
	     Query query = sessionFactory.getCurrentSession().createQuery(sql);
	     query.setParameter("dutySlipId", dutySlipId);
	     return query.list();
	}

	@Override
	public String saveDutySlipClose(DutySlipModel dutySlipModel) {
		DutySlipModel dutySlipModel1= (DutySlipModel) sessionFactory.getCurrentSession().get(DutySlipModel.class, dutySlipModel.getId());
		dutySlipModel1.setCloseKms(dutySlipModel.getCloseKms());
		dutySlipModel1.setDateTo(dutySlipModel.getDateTo());
		dutySlipModel1.setTimeTo(dutySlipModel.getTimeTo());
		dutySlipModel1.setTotalKms(dutySlipModel.getTotalKms());
		dutySlipModel1.setTotalDay(dutySlipModel.getTotalDay());
		dutySlipModel1.setTotalHrs(dutySlipModel.getTotalHrs());
		dutySlipModel1.setGarageOutKms(dutySlipModel.getGarageOutKms());
		dutySlipModel1.setGarageInKms(dutySlipModel.getGarageInKms());
		dutySlipModel1.setStateTax(dutySlipModel.getStateTax());
		dutySlipModel1.setMiscCharge(dutySlipModel.getMiscCharge());
		dutySlipModel1.setOutStationAllowRate(dutySlipModel.getOutStationAllowRate());
		dutySlipModel1.setNightAllow(dutySlipModel.getNightAllow());
		dutySlipModel1.setIsDutySlipClosed(true);
		dutySlipModel1.setModeOfPayment(dutySlipModel.getModeOfPayment());
		dutySlipModel1.setDutySlipClosedBy(dutySlipModel.getDutySlipClosedBy());
		dutySlipModel1.setTariff(dutySlipModel.getTariff());
		dutySlipModel1.setDutySlipClosedByDate(new Date());
		dutySlipModel1.setTotalFare(dutySlipModel.getTotalFare());
		sessionFactory.getCurrentSession().update(dutySlipModel1);
		return "saveSuccess";
	}

	
	@SuppressWarnings("unchecked")
	public List<DutySlipReceiveViewModel> listDutySlipUnBilled(int pageNumber,String searchCriteria[]) {
		List<DutySlipReceiveViewModel> dutySlipReceiveViewModel = null;
		String hqlQuery = " SELECT dsm.id AS dutySlipId, "+
						  " dsm.tollTaxAmount AS tollAmount,"+
						  " dsm.parkingAmount AS parkingAmount,"+
						  " (dsm.stateTax+dsm.miscCharge+dsm.outStationAllow+dsm.nightAllow+dsm.totalFare) as unBilledAmount,"+
						  " (SELECT udm.userName FROM UserModel udm WHERE dsm.dutySlipClosedBy=udm.id) AS closedBy,"+
						  " dsm.dutySlipClosedByDate AS closedByDate,"+
						  " bdm.outlet AS hub,"+
						  " cdm.registrationNo AS carNo,"+
						  "	dsm.deviceStatus AS deviceStatus,"+
						  " (SELECT gmm.name FROM GeneralMasterModel gmm WHERE gmm.id=cdm.model.id) AS allotedCar,"+
						  "	(SELECT gmm.name FROM GeneralMasterModel gmm WHERE gmm.id=bdm.carModel.id) AS bookedCar,"+
						  "	dsm.dutySlipDate AS dutySlipDate,"+
					 	  " dsm.dutySlipNo AS dutySlipNo,"+
						  " (SELECT gmm.name FROM GeneralMasterModel gmm WHERE gmm.id=bdm.rentalType.id) AS plan, "+
						  " (SELECT udm.userName FROM BookingMasterModel bmm,UserModel udm "+
						  "				 WHERE bmm.id=bdm.bookingMasterModel.id AND bmm.customerName.id=udm.id) AS corporate,"+
						  " (SELECT udm.userName FROM BookingMasterModel bmm,UserModel udm "+
						  "				 WHERE bmm.id=bdm.bookingMasterModel.id AND bmm.bookedBy.id=udm.id) AS bookedBy,"+
						  " bdm.usedBy AS usedBy,"+
						  " (SELECT contDM.personalMobile FROM BookingMasterModel bmm,UserModel udm ,ContactDetailModel contDM"+ 
						  "							  WHERE bmm.id=bdm.bookingMasterModel.id AND bmm.bookedBy.id=udm.id"+ 
						  "							  AND contDM.userModel.id=udm.id) AS mobileNo"+
						  " FROM DutySlipModel dsm, BookingDetailModel bdm, VehicleAllocationModel vam, CarDetailModel cdm"+
						  " WHERE bdm.id=dsm.bookingDetailModel.id"+
						  " AND vam.vehicleId.id=cdm.id"+
						  " AND vam.bookingId.id=bdm.id AND dsm.isDutySlipClosed=1 AND dsm.isInvoiceGenerated=0 ";
						  if(searchCriteria!=null){
							  if(searchCriteria[0]!=null && !searchCriteria[0].equalsIgnoreCase("0"))
								  	hqlQuery = hqlQuery + " AND bdm.bookingMasterModel.customerName.id =:corporate ";
							  if(searchCriteria[1]!=null && !searchCriteria[1].equalsIgnoreCase("0"))
								  	hqlQuery = hqlQuery + " AND bdm.bookingMasterModel.bookedBy.id =:bookedBy ";
							  if(searchCriteria[2]!=null && !searchCriteria[2].equalsIgnoreCase("0"))
								  	hqlQuery = hqlQuery + " AND bdm.usedBy  =:usedBy ";
							  if(searchCriteria[3]!=null && !searchCriteria[3].equalsIgnoreCase("0"))
								  	hqlQuery = hqlQuery + " AND cdm.registrationNo  =:closedby ";
							  if(searchCriteria[4]!=null && !searchCriteria[4].equalsIgnoreCase("0"))
								  	hqlQuery = hqlQuery + " AND bdm.outlet  =:hub ";
						  }
		Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery).
				setFirstResult(6 * (pageNumber - 1)).
				setMaxResults(6).setResultTransformer(Transformers.aliasToBean(DutySlipReceiveViewModel.class));
		if(searchCriteria!=null){
			if(searchCriteria[0]!=null && !searchCriteria[0].equalsIgnoreCase("0"))
				query.setLong("corporate", Long.parseLong(searchCriteria[0]));
			if(searchCriteria[1]!=null && !searchCriteria[1].equalsIgnoreCase("0"))
				query.setLong("bookedBy", Long.parseLong(searchCriteria[1]));
			if(searchCriteria[2]!=null && !searchCriteria[2].equalsIgnoreCase("0"))
				query.setLong("usedBy", Long.parseLong(searchCriteria[2]));
			if(searchCriteria[3]!=null && !searchCriteria[3].equalsIgnoreCase("0"))
				query.setLong("closedBy", Long.parseLong(searchCriteria[3]));
			if(searchCriteria[4]!=null && !searchCriteria[4].equalsIgnoreCase("0"))
				query.setLong("hub", Long.parseLong(searchCriteria[4]));
		}
		dutySlipReceiveViewModel = (List<DutySlipReceiveViewModel>)query.list();
		return dutySlipReceiveViewModel;
	}

	@Override
	public String saveDutySlipUnBilled(List<DutySlipModel> dutySlipModels) {
		for(DutySlipModel dutySlipModel:dutySlipModels){
			DutySlipModel dutySlipModel1= (DutySlipModel) sessionFactory.getCurrentSession().get(DutySlipModel.class, dutySlipModel.getId());
			Long maxInvoiceNo = (Long)sessionFactory.getCurrentSession().createQuery("SELECT MAX(invoiceNo) FROM DutySlipModel").uniqueResult();
			dutySlipModel1.setInvoiceGenerateBy(dutySlipModel.getInvoiceGenerateBy());
			dutySlipModel1.setInvoiceGenerateDate(new Date());
			dutySlipModel1.setIsInvoiceGenerated(true);
			sessionFactory.getCurrentSession().update(dutySlipModel1);
		}	
		return "saveSuccess";
	}
	
	@SuppressWarnings("unchecked")
	public List<DutySlipReceiveViewModel> listDutySlipInvoice(int pageNumber,String searchCriteria[]) {
		List<DutySlipReceiveViewModel> dutySlipReceiveViewModel = null;
		String hqlQuery = " SELECT dsm.id AS dutySlipId, "+
						  " dsm.tollTaxAmount AS tollAmount,"+
						  " dsm.parkingAmount AS parkingAmount,"+
						  " (dsm.stateTax+dsm.miscCharge+dsm.outStationAllow+dsm.nightAllow+dsm.totalFare) as unBilledAmount,"+
						  " (SELECT udm.userName FROM UserModel udm WHERE dsm.dutySlipClosedBy=udm.id) AS closedBy,"+
						  " dsm.dutySlipClosedByDate AS closedByDate,"+
						  " bdm.outlet AS hub,"+
							  " cdm.registrationNo AS carNo,"+
						  "	dsm.deviceStatus AS deviceStatus,"+
						  " (SELECT gmm.name FROM GeneralMasterModel gmm WHERE gmm.id=cdm.model.id) AS allotedCar,"+
						  "	(SELECT gmm.name FROM GeneralMasterModel gmm WHERE gmm.id=bdm.carModel.id) AS bookedCar,"+
						  "	dsm.dutySlipDate AS dutySlipDate,"+
					 	  " dsm.dutySlipNo AS dutySlipNo,"+
						  " (SELECT gmm.name FROM GeneralMasterModel gmm WHERE gmm.id=bdm.rentalType.id) AS plan, "+
						  " (SELECT udm.userName FROM BookingMasterModel bmm,UserModel udm "+
						  "				 WHERE bmm.id=bdm.bookingMasterModel.id AND bmm.customerName.id=udm.id) AS corporate,"+
						  " (SELECT udm.userName FROM BookingMasterModel bmm,UserModel udm "+
						  "				 WHERE bmm.id=bdm.bookingMasterModel.id AND bmm.bookedBy.id=udm.id) AS bookedBy,"+
						  " bdm.usedBy AS usedBy,"+
						  " (SELECT contDM.personalMobile FROM BookingMasterModel bmm,UserModel udm ,ContactDetailModel contDM"+ 
						  "							  WHERE bmm.id=bdm.bookingMasterModel.id AND bmm.bookedBy.id=udm.id"+ 
						  "							  AND contDM.userModel.id=udm.id) AS mobileNo"+
						  " FROM DutySlipModel dsm, BookingDetailModel bdm, VehicleAllocationModel vam, CarDetailModel cdm"+
						  " WHERE bdm.id=dsm.bookingDetailModel.id"+
						  " AND vam.vehicleId.id=cdm.id"+
						  " AND vam.bookingId.id=bdm.id AND dsm.isInvoiceGenerated=1 ";
						  if(searchCriteria!=null){
							  if(searchCriteria[0]!=null && !searchCriteria[0].equalsIgnoreCase("0"))
								  	hqlQuery = hqlQuery + " AND bdm.bookingMasterModel.customerName.id =:corporate ";
							  if(searchCriteria[1]!=null && !searchCriteria[1].equalsIgnoreCase("0"))
								  	hqlQuery = hqlQuery + " AND bdm.bookingMasterModel.bookedBy.id =:bookedBy ";
							  if(searchCriteria[2]!=null && !searchCriteria[2].equalsIgnoreCase("0"))
								  	hqlQuery = hqlQuery + " AND bdm.usedBy  =:usedBy ";
							  if(searchCriteria[3]!=null && !searchCriteria[3].equalsIgnoreCase("0"))
								  	hqlQuery = hqlQuery + " AND cdm.registrationNo  =:closedby ";
							  if(searchCriteria[4]!=null && !searchCriteria[4].equalsIgnoreCase("0"))
								  	hqlQuery = hqlQuery + " AND bdm.outlet  =:hub ";
						  }
		Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery).
				setFirstResult(6 * (pageNumber - 1)).
				setMaxResults(6).setResultTransformer(Transformers.aliasToBean(DutySlipReceiveViewModel.class));
		if(searchCriteria!=null){
			if(searchCriteria[0]!=null && !searchCriteria[0].equalsIgnoreCase("0"))
				query.setLong("corporate", Long.parseLong(searchCriteria[0]));
			if(searchCriteria[1]!=null && !searchCriteria[1].equalsIgnoreCase("0"))
				query.setLong("bookedBy", Long.parseLong(searchCriteria[1]));
			if(searchCriteria[2]!=null && !searchCriteria[2].equalsIgnoreCase("0"))
				query.setLong("usedBy", Long.parseLong(searchCriteria[2]));
			if(searchCriteria[3]!=null && !searchCriteria[3].equalsIgnoreCase("0"))
				query.setLong("closedBy", Long.parseLong(searchCriteria[3]));
			if(searchCriteria[4]!=null && !searchCriteria[4].equalsIgnoreCase("0"))
				query.setLong("hub", Long.parseLong(searchCriteria[4]));
		}
		dutySlipReceiveViewModel = (List<DutySlipReceiveViewModel>)query.list();
		return dutySlipReceiveViewModel;
	}

	@Override
	public List calculateFare(String kms, String days, String hours, String tariff) {
		String sql= " SELECT rate,minChgHrs,minChgKms,extHrsRate,extKmsRate, nightDetention"+ 
			    " FROM TariffParameterMasterModel tpm  "+ 
			    " WHERE tpm.id=:tariff ";
		Query query = sessionFactory.getCurrentSession().createQuery(sql);
		query.setParameter("tariff", Long.parseLong(tariff));
		return query.list(); 
	}
	
	@SuppressWarnings("unchecked")
	public DutySlipModel getDSDetails(Long dutySlipDetailsId){
		List<DutySlipModel> dutySlipModels = null;
		dutySlipModels = sessionFactory.getCurrentSession().createQuery("FROM DutySlipModel WHERE id="+dutySlipDetailsId).list();
		if(dutySlipModels!=null && dutySlipModels.size()>0){
			return dutySlipModels.get(0);
		}
		return null;
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
	public DutySlipModel getInvoiceNo(Long id){
		List<DutySlipModel> dutySlipModels = null;
		dutySlipModels = sessionFactory.getCurrentSession().createQuery("SELECT invoiceNo as invoiceNo FROM DutySlipModel WHERE id="+id).setResultTransformer(Transformers.aliasToBean(DutySlipModel.class)).list();
		if(dutySlipModels!=null && dutySlipModels.size()>0){
			return dutySlipModels.get(0);
		}
		return null;	
	}
	
	@SuppressWarnings("unchecked")
	public String getInvoiceNo(){
		String maxInvoiceNo = (String) sessionFactory.getCurrentSession().createQuery("SELECT MAX(invoiceNo) FROM DutySlipModel").uniqueResult();
		return maxInvoiceNo;	
	}
	
	
	@SuppressWarnings("unchecked")
	public DutySlipModel getDsModel(Long bookingDetailsId){
		List<DutySlipModel> dutySlipModels = null;
		dutySlipModels = sessionFactory.getCurrentSession().createQuery("FROM DutySlipModel WHERE bookingDetailModel.id="+bookingDetailsId).list();
		if(dutySlipModels!=null && dutySlipModels.size()>0){
			return dutySlipModels.get(0);
		}
		return null;
	}
	
	public String saveDeallocationModel(DeallocateHistoryModel deallocateHistoryModel) {
		sessionFactory.getCurrentSession().save(deallocateHistoryModel);	
		return "saveSuccess";
	}
	
	@SuppressWarnings("unchecked")
	public DutySlipModel  getDsRowCount(Long booDetId){
		List<DutySlipModel> dutySlipModels = null;
		dutySlipModels = sessionFactory.getCurrentSession().createQuery("FROM DutySlipModel WHERE bookingDetailModel.id="+booDetId).list();
		if(dutySlipModels!=null && dutySlipModels.size()>0){
			return dutySlipModels.get(0);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<DutySlipModel> getlist(Long bookingDetailId, Long carDetailId){
		String sSql = "FROM BookingDetailModel WHERE id = " + bookingDetailId;
		
		List<BookingDetailModel>  bookingDetailModels =  (List<BookingDetailModel>) sessionFactory.getCurrentSession().createQuery(sSql).list();
		
		List<DutySlipModel> dutySlipModels = null;
		System.out.println("hello "+bookingDetailModels.get(0).getPickUpDate());
		dutySlipModels = sessionFactory.getCurrentSession()
						.createQuery("SELECT D FROM DutySlipModel D, BookingDetailModel B  where D.carDetailModel.id  ='"+carDetailId+"' "
										+ "AND D.bookingDetailModel.id = B.id AND B.status <> 'N' And B.status is not null And  B.pickUpDate = '"+bookingDetailModels.get(0).getPickUpDate()+"'").list();
		return dutySlipModels;
		
	}
}
