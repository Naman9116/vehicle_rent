package com.operation.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.operation.model.BookingDetailModel;
import com.operation.model.BookingMasterModel;
import com.operation.model.CarDetailModel;
import com.operation.model.VehicleAllocationModel;

@Repository("vehicleAllocationDao")
public class VehicleAllocationDaoImpl implements VehicleAllocationDao{

	@Autowired
	private SessionFactory sessionFactory;
	private static int pageSize = 3;
	
	@SuppressWarnings("unchecked")
	public List<BookingMasterModel> list(int pageNumber) {
		List<BookingMasterModel> bookingMasterModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BookingMasterModel.class);
		criteria.setFetchMode("bookingDetailModel", FetchMode.JOIN).createAlias("bookingDetailModel", "bdm");
		criteria.add(Subqueries.propertyNotIn("bdm.id", DetachedCriteria.forClass(VehicleAllocationModel.class).setProjection(Projections.distinct(Projections.property("bookingId.id")))));
		criteria.addOrder(Order.desc("bdm.bookingDate"));		
		criteria.setFirstResult(pageSize * (pageNumber - 1)); 
		criteria.setMaxResults(pageSize);
		bookingMasterModel = (List<BookingMasterModel>)criteria.list();
		return bookingMasterModel;
	}
	
	@SuppressWarnings("unchecked")
	public List<BookingMasterModel> listMasterWise(int pageNumber,String searchCriteria) {
		List<BookingMasterModel> bookingMasterModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BookingMasterModel.class,"bmm");
		criteria.setFetchMode("bmm.bookingDetailModel", FetchMode.JOIN).createAlias("bmm.bookingDetailModel", "bdm");
		criteria.add(Subqueries.propertyNotIn("bdm.id", DetachedCriteria.forClass(VehicleAllocationModel.class).setProjection(Projections.distinct(Projections.property("bookingId.id")))));
		criteria.addOrder(Order.desc("bdm.bookingFromDate"));
		criteria.addOrder(Order.desc("bdm.bookingToDate"));
		if(searchCriteria!=null && !searchCriteria.trim().equalsIgnoreCase(""))
			criteria.add(Restrictions.eq("bmm.id",Long.parseLong(searchCriteria)));
		criteria.setFirstResult(pageSize * (pageNumber - 1)); 
		criteria.setMaxResults(pageSize);
		bookingMasterModel = (List<BookingMasterModel>)criteria.list();
		return bookingMasterModel;
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
		//criteria.add(Restrictions.eq("cdm.carType.id", selectCarTypeId));
		criteria.add(Subqueries.propertyNotIn("cdm.id",DetachedCriteria.forClass(VehicleAllocationModel.class,"vam").
				createCriteria("vam.bookingId","bdm").
				add(Restrictions.eq("bdm.bookingDate",bookingDetailModelList.get(0).getPickUpDate())).				
				setProjection(Projections.distinct(Projections.property("vam.vehicleId.id")))));
		if(searchCriteria!=null && !searchCriteria.trim().equalsIgnoreCase(""))
			criteria.add(Restrictions.like("cdm.registrationNo",searchCriteria,MatchMode.ANYWHERE));
		carDetailModel = (List<CarDetailModel>)criteria.list();
		return carDetailModel;
	}

	public String save(VehicleAllocationModel vehicleAllocationModel,String vehicleIdSelectedValue) {
		String[] vehicleIds=vehicleIdSelectedValue.split(",");
		VehicleAllocationModel vehicleAllocationModel1=null;
		for(String vehicleId:vehicleIds){
			vehicleAllocationModel1=new VehicleAllocationModel();
			vehicleAllocationModel1.setBookingId(new BookingDetailModel());
			vehicleAllocationModel1.getBookingId().setId(vehicleAllocationModel.getBookingId().getId());
			CarDetailModel carDetailModel=new CarDetailModel();
				carDetailModel.setId(Long.parseLong(vehicleId));
				vehicleAllocationModel1.setVehicleId(carDetailModel);
			sessionFactory.getCurrentSession().save(vehicleAllocationModel1);	
		}
		return "saveSuccess";
	}
	
	public VehicleAllocationModel formFillForEdit(Long formFillForEditId) {
		VehicleAllocationModel vehicleAllocationModel=null;
		vehicleAllocationModel =(VehicleAllocationModel) sessionFactory.getCurrentSession().get(VehicleAllocationModel.class, new Long(formFillForEditId));
		return vehicleAllocationModel;
	}
	
	public String delete(Long idForDelete) {
		VehicleAllocationModel vehicleAllocationModel =(VehicleAllocationModel) sessionFactory.getCurrentSession().get(VehicleAllocationModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(vehicleAllocationModel);
		return "deleteSuccess";
	}
	
	public String update(VehicleAllocationModel vehicleAllocationModel) {
		//vehicleAllocationModel.setMasterModel( (MasterModel) sessionFactory.getCurrentSession().get(MasterModel.class, new Long(vehicleAllocationModel.getMasterId())));
		sessionFactory.getCurrentSession().update(vehicleAllocationModel);
		return "updateSuccess";
	}
}
