package com.relatedInfo.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.master.model.GeneralMasterModel;
import com.master.service.GeneralMasterService;
import com.master.service.MappingMasterService;
import com.relatedInfo.model.RelatedInfoModel;

@Repository("relatedInfoDao")
public class RelatedInfoDaoImpl implements RelatedInfoDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private MappingMasterService mappingMasterService;
	
	@Autowired
	private GeneralMasterService generalMasterService;
	
	@SuppressWarnings("unchecked")
	public List<RelatedInfoModel> list(String pageFor) {
		List<RelatedInfoModel> relatedInfoModelList = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RelatedInfoModel.class);
		criteria.add(Restrictions.eq("parentType", pageFor));
		criteria.addOrder(Order.asc("id"));
		relatedInfoModelList = (List<RelatedInfoModel>)criteria.list();

		Object models[]=null;
		for(RelatedInfoModel relatedInfoModel:relatedInfoModelList ){
			relatedInfoModel.setAddressDetailModel(relatedInfoModel.getEntityAddress());
			relatedInfoModel.setContactDetailModel(relatedInfoModel.getEntityContact());

			Long lChieldId = 0l;
			if(!pageFor.equalsIgnoreCase("Company")){
				lChieldId = relatedInfoModel.getGeneralMasterModel().getId();
			}
			if( lChieldId > 0){
				models=mappingMasterService.fetchSubMasterValuesMappingMaster(lChieldId,0l);
				Long lParentId = (Long) models[0];
				String sParentName = "";
				if(lParentId > 0){
					GeneralMasterModel generalMasterModel=generalMasterService.formFillForEdit(lParentId,"");
					sParentName = generalMasterModel.getName();
				}else{
					sParentName = "All Companies";
				}
				if(pageFor.equalsIgnoreCase("branch")) {
					relatedInfoModel.setCompany(lParentId);
					relatedInfoModel.setCompanyName(sParentName);
				}
				if(pageFor.equalsIgnoreCase("outlet")){
					relatedInfoModel.setBranch(lParentId);
					relatedInfoModel.setBranchName(sParentName);
				}
			}
		}
		return relatedInfoModelList;
	}
	
	@SuppressWarnings("unchecked")
	public List<RelatedInfoModel> listMasterWise(String searchCriteria,String pageFor) {
		List<RelatedInfoModel> relatedInfoModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RelatedInfoModel.class,"rim").
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
			add(Projections.property("rim.webSite"),"webSite")).setResultTransformer(Transformers.aliasToBean(RelatedInfoModel.class));
		criteria.addOrder(Order.desc("id"));
		criteria.add(Restrictions.eq("rim.parentType", pageFor));
		relatedInfoModel = (List<RelatedInfoModel>)criteria.list();
		return relatedInfoModel;
	}

	public String save(RelatedInfoModel relatedInfoModel) {
		String sBranch = relatedInfoModel.getGeneralMasterModel().getId().toString();
		sessionFactory.getCurrentSession().save(relatedInfoModel);
		String sReturnId = "0";
		Query query = sessionFactory.getCurrentSession().createQuery("SELECT MAX(id) FROM RelatedInfoModel");
		Long lastId = (Long) query.list().get(0);
		sReturnId = String.valueOf(lastId);
		if(relatedInfoModel.getParentType().equals("Branch")){
			query = sessionFactory.getCurrentSession().createQuery("UPDATE UserModel SET assignBranches = concat(assignBranches, ',' ,'"+sBranch+"') WHERE name = 'Admin' ");
			query.executeUpdate();
		}		
		return sReturnId;
	}
	
	public RelatedInfoModel formFillForEdit(Long formFillForEditId) {
		RelatedInfoModel relatedInfoModel=null;
		relatedInfoModel =(RelatedInfoModel) sessionFactory.getCurrentSession().get(RelatedInfoModel.class, new Long(formFillForEditId));
		return relatedInfoModel;
	}
	
	public String delete(Long idForDelete) {
		RelatedInfoModel relatedInfoModel =(RelatedInfoModel) sessionFactory.getCurrentSession().get(RelatedInfoModel.class, new Long(idForDelete));
		GeneralMasterModel generalMasterModel = (GeneralMasterModel) sessionFactory.getCurrentSession().get(GeneralMasterModel.class, relatedInfoModel.getGeneralMasterModel().getId());
		sessionFactory.getCurrentSession().delete(relatedInfoModel);
		sessionFactory.getCurrentSession().delete(generalMasterModel);
		return "deleteSuccess";
	}
	
	public String update(RelatedInfoModel relatedInfoModel) {
		sessionFactory.getCurrentSession().update(relatedInfoModel);
		return "updateSuccess";
	}
	
}
