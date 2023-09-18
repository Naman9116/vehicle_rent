package com.corporate.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.common.model.ContactDetailModel;
import com.common.service.MasterDataService;
import com.corporate.model.AutorizedUserModel;
import com.ets.model.LocationMasterModel;
import com.master.model.GeneralMasterModel;
import com.master.service.GeneralMasterService;
import com.master.service.MappingMasterService;

@Repository("autorizedUserDap")
public class AuthorizedUserDaoImpl implements AuthorizedUserDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private MappingMasterService mappingMasterService;
	
	@Autowired
	private GeneralMasterService generalMasterService;
	
	@Autowired
	private MasterDataService masterDataService;
	
	@SuppressWarnings("unchecked")
	public List<AutorizedUserModel> list(String pageFor, Long corporateId) {
		List<AutorizedUserModel> AutorizedUserModelList = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AutorizedUserModel.class);
		if(pageFor.equals("Admin")){
			criteria.add(Restrictions.eq("authTypeAdmin", "Y"));
		}else{
			criteria.add(Restrictions.eq("authTypeClient","Y"));
		}
		criteria.add(Restrictions.eq("corporateId.id", corporateId));
		criteria.addOrder(Order.asc("id"));
		AutorizedUserModelList = (List<AutorizedUserModel>)criteria.list();
		return AutorizedUserModelList;
	}
	
	@SuppressWarnings("unchecked")
	public List<AutorizedUserModel> listMasterWise(String searchCriteria) {
		List<AutorizedUserModel> autorizedUserModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AutorizedUserModel.class,"rim").
				createAlias("rim.generalMasterModel", "gmm").
		setProjection(Projections.projectionList().
			add(Projections.property("rim.id"),"id").
			add(Projections.property("gmm.name"),"parentValue").
			add(Projections.property("rim.panNo"),"panNo").
			add(Projections.property("rim.girNo"),"girNo").
			add(Projections.property("rim.lstNo"),"lstNo").
			add(Projections.property("rim.tanNo"),"tanNo").
			add(Projections.property("rim.cstNo"),"cstNo").
			add(Projections.property("rim.regDate"),"regDate").
			add(Projections.property("rim.email"),"email").
			add(Projections.property("rim.helpLine"),"helpLine").
			add(Projections.property("rim.webSite"),"webSite")).setResultTransformer(Transformers.aliasToBean(AutorizedUserModel.class));
		criteria.addOrder(Order.desc("id"));
		autorizedUserModel = (List<AutorizedUserModel>)criteria.list();
		return autorizedUserModel;
	}

	public String save(AutorizedUserModel autorizedUserModel) {
		sessionFactory.getCurrentSession().save(autorizedUserModel);
		return "SaveSuccess";
	}
	
	public AutorizedUserModel formFillForEdit(Long formFillForEditId) {
		AutorizedUserModel autorizedUserModel=null;
		autorizedUserModel =(AutorizedUserModel) sessionFactory.getCurrentSession().get(AutorizedUserModel.class, new Long(formFillForEditId));
		return autorizedUserModel;
	}
	
	@SuppressWarnings("unchecked")
	public String delete(Long idForDelete) {
		String sStatus ="";
		AutorizedUserModel autorizedUserModel =(AutorizedUserModel) sessionFactory.getCurrentSession().get(AutorizedUserModel.class, new Long(idForDelete));
		
		String sSql = "SELECT A.id as id, A.name as name FROM AutorizedUserModel A WHERE A.parentId = " + autorizedUserModel.getId();
		
		List<GeneralMasterModel> generalMasterModelList=null;
		Query query=sessionFactory.getCurrentSession().createQuery(sSql).setResultTransformer(Transformers.aliasToBean(GeneralMasterModel.class));
		generalMasterModelList = (List<GeneralMasterModel>) query.list();

		if(generalMasterModelList.size() == 0){
			sessionFactory.getCurrentSession().delete(autorizedUserModel);
			sStatus = "deleteSuccess";
		}else{
			sStatus = "deleteFailed";
		}
		return sStatus;
	}
	
	public String update(AutorizedUserModel autorizedUserModel) {
		sessionFactory.getCurrentSession().update(autorizedUserModel);
		return "updateSuccess";
	}
	
	@SuppressWarnings("unchecked")
	public ContactDetailModel getBookerMobile(Long bookedById) {
		ContactDetailModel contactDetailModel =  null;
		List<ContactDetailModel> contactDetailModels = sessionFactory.getCurrentSession()
						.createQuery("SELECT cd.personalMobile as personalMobile FROM ContactDetailModel cd WHERE cd.id = "
															+ "(SELECT au.entityContact as entityContact FROM AutorizedUserModel au  WHERE au.id= '"+bookedById+"') ").setResultTransformer(Transformers.aliasToBean(ContactDetailModel.class)).list();
		if(contactDetailModels!=null && contactDetailModels.size()>0)
			contactDetailModel = contactDetailModels.get(0);			
		return contactDetailModel; 
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<LocationMasterModel> getLocationAsPerCorporate(Long corporateId, Long zoneId) {
		List<LocationMasterModel> locationMasterModelList = null;
		String sSql = "FROM LocationMasterModel L WHERE L.corporateId.id="+corporateId + " And L.zone.id = " + zoneId;
		locationMasterModelList= sessionFactory.getCurrentSession().createQuery(sSql).list();
		return locationMasterModelList;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<GeneralMasterModel> getZoneAsPerCorporate(Long corporateId) {
		List<GeneralMasterModel> zoneMasterModelList = null;
		String sSql = "FROM GeneralMasterModel WHERE id in (SELECT L.zone.id FROM LocationMasterModel L Where L.corporateId.id="+corporateId +")";
		zoneMasterModelList= sessionFactory.getCurrentSession().createQuery(sSql).list();
		return zoneMasterModelList;
	}

	@Override
	public Long mobileNoisPresent(String mobileNo) {
		String hqlQuery = "select count(*)from AutorizedUserModel au where au.entityContact in(select id from ContactDetailModel where personalMobile='"+mobileNo+"'  )";
	    Session session = sessionFactory.openSession();
	        Query query = session.createQuery(hqlQuery);
	        Long count =   (Long) query.uniqueResult();
	        return count;
	}

	@Override
	public Long mobileNoisPresentid(String mobileNo) {
		String hqlQuery = "select id from AutorizedUserModel au where au.entityContact in(select id from ContactDetailModel where personalMobile='"+mobileNo+"'  )";
	    Session session = sessionFactory.openSession();
	        Query query = session.createQuery(hqlQuery);
	        Long id =   (Long) query.uniqueResult();
	        return id;
	}
}
