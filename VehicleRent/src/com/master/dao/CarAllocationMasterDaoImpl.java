package com.master.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.master.model.CarAllocationModel;
import com.operation.model.CarDetailModel;

@Repository("carAllocationMasterDao")
public class CarAllocationMasterDaoImpl implements CarAllocationMasterDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	public List<CarAllocationModel> list() {
		List<CarAllocationModel> carAllocationModelList = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CarAllocationModel.class);
		carAllocationModelList = (List<CarAllocationModel>)criteria.list();
		return carAllocationModelList;
	}
	
	public CarAllocationModel formFillForEdit(Long formFillForEditId) {
		CarAllocationModel carAllocationModel=(CarAllocationModel) sessionFactory.getCurrentSession().get(CarAllocationModel.class, new Long(formFillForEditId));
		return carAllocationModel;
	}
	
	public String save(CarAllocationModel carAllocationModel) {
		sessionFactory.getCurrentSession().save(carAllocationModel);
		return "saveSuccess";
	}
	
	public String update(CarAllocationModel carAllocationModel) {
		sessionFactory.getCurrentSession().update(carAllocationModel);
		return "updateSuccess";
	}

	public String delete(Long idForDelete) {
		CarAllocationModel carAllocationModel =(CarAllocationModel) sessionFactory.getCurrentSession().get(CarAllocationModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(carAllocationModel);
		return "deleteSuccess";
	}
	
	@SuppressWarnings("unchecked")
	public CarAllocationModel getCarRelDetailsBasedOnRegNo(String carRegNo){
		CarAllocationModel carAllocationModel = new CarAllocationModel();
		List<CarAllocationModel> carAllocationModels = sessionFactory.getCurrentSession()
				.createQuery("SELECT ca.carDetailModelId as carDetailModelId, ca.vendorId as vendorId,"
							+"ca.carOwnerType as carOwnerType, ca.chauffeurId as chauffeurId, "
							+ "(SELECT COUNT(*) FROM DutySlipModel WHERE dutySlipStatus in ('D','Y','S','H','O','L') And "
							+ "carDetailModel.id = (SELECT id FROM CarDetailModel WHERE registrationNo= '"+carRegNo+"') ) as noOfPendingDutySlip "
							+ " FROM CarAllocationModel ca WHERE ca.carStatus = 'Y' And ca.carDetailModelId='"+carRegNo+"'").setResultTransformer(Transformers.aliasToBean(CarAllocationModel.class)).list();
		if(carAllocationModels!=null && carAllocationModels.size()>0)
			carAllocationModel = carAllocationModels.get(0);
		else{
			CarDetailModel carDetailModel = (CarDetailModel) sessionFactory.getCurrentSession().createQuery("FROM CarDetailModel cd where cd.registrationNo='"+carRegNo+"' ").uniqueResult();
			if(carDetailModel != null){
				Long noOfPendingDutySlip = (Long) sessionFactory.getCurrentSession().createQuery("SELECT COUNT(*) FROM DutySlipModel WHERE dutySlipStatus in ('D','Y','S','H','O','L') And carDetailModel.id = "+carDetailModel.getId()).uniqueResult();
				carAllocationModel.setCarDetailModelId(carDetailModel);
				carAllocationModel.setNoOfPendingDutySlip(noOfPendingDutySlip);
			}
		}
		return carAllocationModel; 
	}
}
