package com.ets.dao;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ets.model.CarRateModel;
import com.ets.model.LocationMasterModel;
import com.master.model.CityMasterModel;
import com.master.model.GeneralMasterModel;
import com.mobileweb.dao.MobileWebDaoImpl;

@Repository("locationMasterDao")
public class LocationMasterDaoImpl implements LocationMasterDao {
	
	private static final Logger logger = Logger.getLogger(MobileWebDaoImpl.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public LocationMasterModel save(LocationMasterModel   locationMasterModel) {
		sessionFactory.getCurrentSession().save(locationMasterModel);	
		return locationMasterModel;
	}
	
	@Override
	public String update(LocationMasterModel   locationMasterModel) {
		sessionFactory.getCurrentSession().update(locationMasterModel);	
		return "SUCCESS";
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<LocationMasterModel> getLocationMaster(Long corporateId) {
		List<LocationMasterModel> locationMasterModelList = null;
	
		String sSql = "SELECT L FROM LocationMasterModel L Where L.corporateId.id=" + corporateId ;
		locationMasterModelList= sessionFactory.getCurrentSession().createQuery(sSql).list();
		return locationMasterModelList;
	}
	@Override
	public LocationMasterModel formFillForEditLocationMaster(Long id){
		LocationMasterModel locationMasterModel =null;
		locationMasterModel =(LocationMasterModel) sessionFactory.getCurrentSession()
							.createQuery("SELECT L FROM LocationMasterModel L  where id = "+id).uniqueResult();
		return locationMasterModel;
	}
	@Override
	public String delete(Long idForDelete) {
		LocationMasterModel locationMasterModel =(LocationMasterModel) sessionFactory.getCurrentSession().get(LocationMasterModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(locationMasterModel);
		return "deleteSuccess";
	}
	@Override
	@SuppressWarnings("unchecked")
	public List<CityMasterModel> getCityUsingPinCode(long pincode) {
		List<CityMasterModel> cityMasterModel = null;
		cityMasterModel = sessionFactory.getCurrentSession().
				createQuery("SELECT C FROM CityMasterModel C WHERE C.pincode="+pincode).list();
		return cityMasterModel;
	}
	

	@Override
	public String save(CarRateModel   carRateModel) {
		sessionFactory.getCurrentSession().save(carRateModel);	
		return "SUCCESS";
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<CarRateModel> getCarRate(long locationId) {
		List<CarRateModel> carRateModels = null;
		carRateModels = sessionFactory.getCurrentSession().
				createQuery("SELECT C FROM CarRateModel C WHERE C.effectiveDate=(SELECT MAX(cr.effectiveDate) FROM  CarRateModel cr WHERE cr.locationMasterId.id='"+locationId+"') AND C.locationMasterId.id="+locationId  ).list();
		return carRateModels;
	}
	
	@Override
	public String update(CarRateModel   carRateModel) {
		sessionFactory.getCurrentSession().update(carRateModel);	
		return "SUCCESS";
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CarRateModel> getCarRateListAsPerAEffectiveDate(long locationId,String effectiveDate) {
		List<CarRateModel> carRateModels = null;
		carRateModels = sessionFactory.getCurrentSession().
				createQuery("SELECT C FROM CarRateModel C WHERE C.effectiveDate='"+effectiveDate+"' AND C.locationMasterId.id="+locationId).list();
		return carRateModels;
	}
	@Override
	public String deleteCarRateModel(Long idForDelete) {
		CarRateModel carRateModel =(CarRateModel) sessionFactory.getCurrentSession().get(CarRateModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(carRateModel);
		return "deleteSuccess";
	}
	@Override
	public List<GeneralMasterModel> getZoneAsBranch(Long branchId) {
		List<GeneralMasterModel> generalMasterModel = null;
		String sSql = "SELECT A.id as id, A.name as name, "
				    + "       A.remark as remark, A.sortId as sortId, "
				    + "       C.id as extraId,"
				    + "       C.name as extraName "
				    + " FROM GeneralMasterModel A, MappingMasterModel B,GeneralMasterModel C, MasterModel M "
				    + " WHERE M.code = 'ZONE' And "
				    + "       M.id   = A.masterModel.id And "
				    + "       B.subMasterValuesModel.id = A.id And "
				    + "       B.masterValuesModel.id = C.id And "
				    + "       C.id = '"+branchId+"'";
		try{
			generalMasterModel = (List<GeneralMasterModel>) sessionFactory.getCurrentSession().createQuery(sSql).setResultTransformer(Transformers.aliasToBean(GeneralMasterModel.class)).list();
		}catch(Exception e){
			e.printStackTrace();
		}
		return generalMasterModel;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GeneralMasterModel> getLocationAsZone(Long zoneId) {
		List<GeneralMasterModel> generalMasterModel = null;
		String sSql = "SELECT A.id as id, A.name as name, "
				    + "       A.remark as remark, A.sortId as sortId, "
				    + "       C.id as extraId,"
				    + "       C.name as extraName "
				    + " FROM GeneralMasterModel A, MappingMasterModel B,GeneralMasterModel C, MasterModel M "
				    + " WHERE M.code = 'LOC' And "
				    + "       M.id   = A.masterModel.id And "
				    + "       B.subMasterValuesModel.id = A.id And "
				    + "       B.masterValuesModel.id = C.id And "
				    + "       C.id = '"+zoneId+"'";
		try{
			generalMasterModel = (List<GeneralMasterModel>) sessionFactory.getCurrentSession().createQuery(sSql).setResultTransformer(Transformers.aliasToBean(GeneralMasterModel.class)).list();
		}catch(Exception e){
			e.printStackTrace();
		}
		return generalMasterModel;
	}
}
