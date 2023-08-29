package com.master.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.master.model.CorporateTariffDetModel;
import com.master.model.CorporateTariffModel;

@Repository("triffParameterMasterDao")
public class CorporateTariffDaoImpl implements CorporateTariffDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	public List<CorporateTariffModel> list() {
		List<CorporateTariffModel> triffParameterMasterModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CorporateTariffModel.class);
		triffParameterMasterModel = (List<CorporateTariffModel>)criteria.list();
		return triffParameterMasterModel;
	}
	
	@SuppressWarnings("unchecked")
	public List<CorporateTariffModel> listMasterWise(String searchCriteria) {
		List<CorporateTariffModel> triffParameterMasterModel = null;	
		String sql= " SELECT DISTINCT tpmm.id as id, tpmm.customerNameId as customerNameId, tpmm.carModel as carModel, tpmm.carType as carType, tpmm.tariff as tariff, tpmm.dutyType as dutyType FROM TariffParameterMasterModel tpmm, GeneralMasterModel gmm ";
		if(searchCriteria!=null && !searchCriteria.equalsIgnoreCase(""))
			sql=sql+" WHERE (tpmm.carType = gmm.id OR tpmm.carModel = gmm.id) AND gmm.name like :searchCriteria ";
			sql=sql+" ORDER BY tpmm.id DESC ";		
		Query query = sessionFactory.getCurrentSession().createQuery(sql).
		setMaxResults(5).setResultTransformer(Transformers.aliasToBean(CorporateTariffModel.class));
		if(searchCriteria!=null && !searchCriteria.equalsIgnoreCase(""))
			query.setString("searchCriteria", "%"+searchCriteria+"%");	
		triffParameterMasterModel = (List<CorporateTariffModel>)query.list();
		return triffParameterMasterModel;
	}

	
	public String save(CorporateTariffModel corporateTariffModel, String controlsWithValue) {
		String[] controlsWithValueList = controlsWithValue.split(",");
		
		for(int iItem = 0;iItem < controlsWithValueList.length; iItem++ ){
			String sValues = controlsWithValueList[iItem].length() > 6 ? controlsWithValueList[iItem].substring(6,controlsWithValueList[iItem].length()):"";
			if(sValues.equals("")) continue;
			String[] sValuesList = sValues.split("_");

			CorporateTariffDetModel corporateTariffDetModel = new CorporateTariffDetModel();
			if(sValuesList.length == 3){
				corporateTariffDetModel.getCarCatId().setId(Long.parseLong(sValuesList[0]));
				corporateTariffDetModel.getTariffId().setId(Long.parseLong(sValuesList[1]));
				corporateTariffDetModel.setTariffValue(Double.parseDouble(sValuesList[2]));
				corporateTariffDetModel.setCorporateTariffModel(corporateTariffModel);
				corporateTariffModel.getCorporateTariffDetModel().add(corporateTariffDetModel);
			}
		}
		sessionFactory.getCurrentSession().save(corporateTariffModel);
		return "saveSuccess";
	}
	
	@SuppressWarnings("unchecked")
	public List<CorporateTariffModel >list(String corporateId,String branchID) {
		List<CorporateTariffModel> corporateTariffModel  = null;
		String sSql = "FROM CorporateTariffModel WHERE corporateId ="+corporateId+" and branchId="+branchID+"";
		corporateTariffModel  = sessionFactory.getCurrentSession().createQuery(sSql).list();
		return corporateTariffModel ;
	}
	
	public CorporateTariffModel formFillForEdit(Long formFillForEditId) {
		CorporateTariffModel corporateTariffModel=null;
		corporateTariffModel =(CorporateTariffModel) sessionFactory.getCurrentSession().get(CorporateTariffModel.class, new Long(formFillForEditId));
		return corporateTariffModel;
	}
	
	public String delete(Long idForDelete) {
		CorporateTariffModel triffParameterMasterModel =(CorporateTariffModel) sessionFactory.getCurrentSession().get(CorporateTariffModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(triffParameterMasterModel);
		return "deleteSuccess";
	}
	
	public String update(CorporateTariffModel corporateTariffModel, String controlsWithValue){
		if(controlsWithValue!=null)
		{
			String[] controlsWithValueList = controlsWithValue.split(",");
			for(int iItem = 0;iItem < controlsWithValueList.length; iItem++ ){
				String sValues = "";
	
				if(controlsWithValueList[iItem].length() > 6) 
					sValues = controlsWithValueList[iItem].substring(6,controlsWithValueList[iItem].length());
				else
					continue;
	
				String[] sValuesList = sValues.split("_");
	
				CorporateTariffDetModel corporateTariffDetModel = new CorporateTariffDetModel();
				if(sValuesList.length >= 3){
					corporateTariffDetModel.getCarCatId().setId(Long.parseLong(sValuesList[0]));
					corporateTariffDetModel.getTariffId().setId(Long.parseLong(sValuesList[1]));
					if (sValuesList[2] == null || sValuesList[2].trim().isEmpty()){
						corporateTariffDetModel.setTariffValue(null);
						}
					else
					{
					corporateTariffDetModel.setTariffValue(Double.parseDouble(sValuesList[2]));
					}
					if(sValuesList.length >= 4){
						corporateTariffDetModel.setId(Long.parseLong(sValuesList[3]));
					}
					corporateTariffDetModel.setCorporateTariffModel(corporateTariffModel);
					corporateTariffModel.getCorporateTariffDetModel().add(corporateTariffDetModel);
				}
			}
		}
		sessionFactory.getCurrentSession().update(corporateTariffModel);
		return "updateSuccess";
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CorporateTariffModel> getCommonTariffParameter_TariffParameterMaster(CorporateTariffModel corporateTariffModel) {
		List<CorporateTariffModel> corporateTariffModels = null;
/*		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CorporateTariffModel.class);
		criteria.setProjection(Projections.projectionList().
		add(Projections.property("rate"),"rate").
		add(Projections.property("driverAllow"),"driverAllow").
		add(Projections.property("minChgKms"),"minChgKms").
		add(Projections.property("minChgHrs"),"minChgHrs").
		add(Projections.property("extKmsRate"),"extKmsRate").
		add(Projections.property("extHrsRate"),"extHrsRate").
		add(Projections.property("nightDetention"),"nightDetention").
		add(Projections.property("fuelRate"),"fuelRate").
		add(Projections.property("currentFuelRate"),"currentFuelRate")).setResultTransformer(Transformers.aliasToBean(TariffParameterMasterModel.class));
		criteria.add(Restrictions.eq("carModel.id", corporateTariffModel.getCarModel().getId()));
		criteria.add(Restrictions.eq("carType.id", corporateTariffModel.getCarType().getId()));
		criteria.add(Restrictions.eq("tariff.id", corporateTariffModel.getTariff().getId()));
		criteria.add(Restrictions.eq("dutyType.id", corporateTariffModel.getDutyType().getId()));
		criteria.add(Restrictions.eq("tariffParameterFor", "common"));
		criteria.add(Property.forName("effectiveDate").eq(DetachedCriteria.forClass(CorporateTariffModel.class).setProjection(Projections.max("effectiveDate")).
				add(Restrictions.eq("carModel.id", tariffParameterMasterModel.getCarModel().getId())).
				add(Restrictions.eq("carType.id", tariffParameterMasterModel.getCarType().getId())).
				add(Restrictions.eq("tariff.id", tariffParameterMasterModel.getTariff().getId())).
				add(Restrictions.eq("dutyType.id", tariffParameterMasterModel.getDutyType().getId())).
				add(Restrictions.eq("tariffParameterFor", "common"))));
		triffParameterMasterModel = (List<TariffParameterMasterModel>)criteria.list();
*/		return corporateTariffModels;
	}
	
	@SuppressWarnings("unchecked")
	public List<CorporateTariffModel >getCorporateTariff(Long corporateId){
		List<CorporateTariffModel > corporateTariffModels = null;
		String sSql = "FROM CorporateTariffModel WHERE corporateId.id ="+corporateId;
		corporateTariffModels  = sessionFactory.getCurrentSession().createQuery(sSql).setMaxResults(1).list();
		return corporateTariffModels ;
	}
	
	@SuppressWarnings("unchecked")
	public List<CorporateTariffModel >getCorporateFuelHikeDate(Long corporateId, Long branchId){
		List<CorporateTariffModel > corporateTariffModels = null;
		String sSql = "FROM CorporateTariffModel WHERE corporateId.id ="+corporateId+" And branchId.id = "+branchId+"  order by fuelHikeDate asc";
		corporateTariffModels  = sessionFactory.getCurrentSession().createQuery(sSql).list();
		return corporateTariffModels;
	}
	
	@SuppressWarnings("unchecked")
	public List<CorporateTariffModel >listAsPerDate(Long id) {
		List<CorporateTariffModel> corporateTariffModel  = null;
		String sSql = "FROM CorporateTariffModel WHERE id ="+id+"";
		corporateTariffModel  = sessionFactory.getCurrentSession().createQuery(sSql).list();
		return corporateTariffModel ;
	}
}
