package com.operation.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.operation.model.CarDetailModel;
import com.operation.model.DutySlipModel;

@Repository("carDetailDao")
public class CarDetailDaoImpl implements CarDetailDao{
	private static final Logger logger = Logger.getLogger(CarDetailDaoImpl.class);

	@Autowired
	private SessionFactory sessionFactory;
	private static int pageSize = 5;
	
	@SuppressWarnings("unchecked")
	public List<CarDetailModel> list() {
		List<CarDetailModel> carDetailModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CarDetailModel.class);
		criteria.addOrder(Order.asc("id"));
		carDetailModel = (List<CarDetailModel>)criteria.list();
		return carDetailModel;
	}
	
	@SuppressWarnings("unchecked")
	public List<CarDetailModel> listMasterWise(String carDetailCode,String searchCriteria) {
		List<CarDetailModel> carDetailModel = null;
		String sql= " SELECT cdm.registrationNo as registrationNo, cdm.chasisNo as chasisNo, cdm.engineNo as engineNo FROM CarDetailModel cdm  ";
		if(searchCriteria!=null && !searchCriteria.equalsIgnoreCase(""))
			sql=sql+" WHERE cdm.registrationNo like :searchCriteria  OR cdm.chasisNo like :searchCriteria  ";
			
		Query query = sessionFactory.getCurrentSession().createQuery(sql).setResultTransformer(Transformers.aliasToBean(CarDetailModel.class));
		if(searchCriteria!=null && !searchCriteria.equalsIgnoreCase(""))
			query.setString("searchCriteria", "%"+searchCriteria+"%");
		
		carDetailModel = (List<CarDetailModel>)query.list();
		return carDetailModel;
	}

	public String save(CarDetailModel carDetailModel) {
		String sMaxCarNo = (sessionFactory.getCurrentSession().createQuery("SELECT COALESCE(max(cast(carId as integer)),0) FROM CarDetailModel").uniqueResult()).toString();
		if(sMaxCarNo != null)
			carDetailModel.setCarId(String.valueOf(Integer.parseInt(sMaxCarNo) + 1));
		sessionFactory.getCurrentSession().save(carDetailModel);
		return "saveSuccess";
	}
	
	public CarDetailModel formFillForEdit(Long formFillForEditId) {
		CarDetailModel carDetailModel=null;
		carDetailModel =(CarDetailModel) sessionFactory.getCurrentSession().get(CarDetailModel.class, new Long(formFillForEditId));
		return carDetailModel;
	}
	
	public String delete(Long idForDelete) {
		CarDetailModel carDetailModel =(CarDetailModel) sessionFactory.getCurrentSession().get(CarDetailModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(carDetailModel);
		return "deleteSuccess";
	}
	
	public String update(CarDetailModel carDetailModel) {
		sessionFactory.getCurrentSession().update(carDetailModel);
		return "updateSuccess";
	}
	
	@SuppressWarnings("unchecked")
	public CarDetailModel getCarDetailsBasedOnRegNo(String carRegNo){
		CarDetailModel carDetailModel = null;
		List<CarDetailModel> carDetailModels = sessionFactory.getCurrentSession()
				.createQuery("FROM CarDetailModel WHERE registrationNo='"+carRegNo+"'").list(); 
		if(carDetailModels!=null && carDetailModels.size()>0)
			carDetailModel = carDetailModels.get(0);			
		return carDetailModel; 
	}
	
	@SuppressWarnings("unchecked")
	public List<CarDetailModel> getUnReservedCarlist(Date reserveDateTime, Long carSegment){
		List<CarDetailModel> carDetailModels = null;
		carDetailModels = sessionFactory.getCurrentSession()
				.createQuery("SELECT cdm.bodyStyle as bodyStyle, cdm.model as model, cdm.registrationNo as registrationNo, "		
		+ " rcm.dutySlipModel.dutySlipStatus as dutySlipStatus, rcm.reserveBy.userFirstName as reserveBy, "
		+ " rcm.unReserveBy.userFirstName as unReserveBy, rcm.reserveDateTime as reserveDateTime "
		+ " FROM ReservedCarModel as rcm  "
		+ " LEFT OUTER JOIN rcm.carDetailModel as cdm " 
		+ " WHERE CONVERT(varchar(11),rcm.reserveDateTime,103) = '"+reserveDateTime+"' "
		+ " AND (cdm.model.id ="+carSegment+" OR '0' = "+carSegment+")").setResultTransformer(Transformers.aliasToBean(CarDetailModel.class)).list(); 
		return carDetailModels;
	}
	@Override
	@SuppressWarnings("unchecked")
	public List<DutySlipModel> searchDutySlip(String bookingFromDate,String bookingToDate,String flag) {
		List<DutySlipModel> dutySlipModel = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date fDate = dateFormat.parse(bookingFromDate);
			bookingFromDate = new SimpleDateFormat("yyyy-MM-dd").format(fDate);
			Date tDate = dateFormat.parse(bookingToDate);
			bookingToDate = new SimpleDateFormat("yyyy-MM-dd").format(tDate);
		   }catch(ParseException e) {
			logger.error("",e);
		 }
		String sSql = "SELECT d FROM DutySlipModel d WHERE "
				    + "d.bookingDetailModel.pickUpDate >='" + bookingFromDate + "' AND  "
				    + "d.bookingDetailModel.pickUpDate <='" + bookingToDate + "'" ;
		if(flag.equals("A") || flag.equals("D"))
			sSql=sSql+"AND d.dutySlipStatus='" + flag + "' ";
		sSql=sSql+"order by d.bookingDetailModel.pickUpDate desc,d.carDetailModel.registrationNo desc";
		dutySlipModel= sessionFactory.getCurrentSession().createQuery(sSql).list();
		return dutySlipModel;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<CarDetailModel> searchCarDetailsNotInDutySlip(String bookingFromDate,String bookingToDate) {
		List<CarDetailModel> carDetailModel = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date fDate = dateFormat.parse(bookingFromDate);
			bookingFromDate = new SimpleDateFormat("yyyy-MM-dd").format(fDate);
			Date tDate = dateFormat.parse(bookingToDate);
			bookingToDate = new SimpleDateFormat("yyyy-MM-dd").format(tDate);
		}catch(ParseException e) {
			logger.error("",e);
		}
		String sSql = "SELECT cd FROM CarDetailModel cd WHERE cd.id"
					+ " NOT in (SELECT d.carDetailModel.id FROM DutySlipModel d "
					+ "WHERE d.bookingDetailModel.pickUpDate >='" + bookingFromDate + "' AND "
					+ " d.bookingDetailModel.pickUpDate <='" + bookingToDate + "') AND cd.status='Y'" ;
		carDetailModel= sessionFactory.getCurrentSession().createQuery(sSql).list();
		return carDetailModel;
	}
	
}
