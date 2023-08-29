package com.corporate.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.corporate.model.CorporateModel;
import com.master.model.CorporateTaxDetModel;
import com.master.model.TaxationModel;
import com.master.service.GeneralMasterService;
import com.master.service.MappingMasterService;

@Repository("corporateDap")
public class CorporateDaoImpl implements CorporateDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private MappingMasterService mappingMasterService;
	
	@Autowired
	private GeneralMasterService generalMasterService;
	
	@SuppressWarnings("unchecked")
	public List<CorporateModel> list() {
		List<CorporateModel> CorporateModelList = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CorporateModel.class);
		criteria.addOrder(Order.asc("id"));
		CorporateModelList = (List<CorporateModel>)criteria.list();

		for(CorporateModel corporateModel:CorporateModelList ){
			corporateModel.setAddressDetailModel(corporateModel.getEntityAddress());
			corporateModel.setContactDetailModel(corporateModel.getEntityContact());
		}
		return CorporateModelList;
	}
	
	@SuppressWarnings("unchecked")
	public List<CorporateModel> listMasterWise(String searchCriteria) {
		List<CorporateModel> corporateModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CorporateModel.class,"rim").
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
			add(Projections.property("rim.webSite"),"webSite")).setResultTransformer(Transformers.aliasToBean(CorporateModel.class));
		criteria.addOrder(Order.desc("id"));
		corporateModel = (List<CorporateModel>)criteria.list();
		return corporateModel;
	}

	public String save(CorporateModel corporateModel) {
		sessionFactory.getCurrentSession().save(corporateModel);
		return "SaveSuccess";
	}
	
	public CorporateModel formFillForEdit(Long formFillForEditId) {
		CorporateModel corporateModel=null;
		corporateModel =(CorporateModel) sessionFactory.getCurrentSession().get(CorporateModel.class, new Long(formFillForEditId));
		return corporateModel;
	}
	
	public String delete(Long idForDelete) {
		CorporateModel corporateModel =(CorporateModel) sessionFactory.getCurrentSession().get(CorporateModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(corporateModel);
		return "deleteSuccess";
	}
	
	public String update(CorporateModel corporateModel) {
		sessionFactory.getCurrentSession().update(corporateModel);
		return "updateSuccess";
	}
	
	public String save(CorporateTaxDetModel corporateTaxDetModel) {
		sessionFactory.getCurrentSession().save(corporateTaxDetModel);
		return "SaveSuccess";
	}
	
	@SuppressWarnings("unchecked")
	public List<CorporateTaxDetModel> formFillForEditCT(Long formFillForEditId) {
		List<CorporateTaxDetModel> corporateTaxDetModelList=null;
		corporateTaxDetModelList =(List<CorporateTaxDetModel>) sessionFactory.getCurrentSession()
				.createQuery("FROM CorporateTaxDetModel CT  "
						+ "   WHERE CT.corporateModelId = "+formFillForEditId+" And "
						+ " 		CT.insDate = (SELECT max(CZ.insDate) "
						+ "						  FROM CorporateTaxDetModel CZ "
						+ "						  WHERE CZ.corporateModelId  = CT.corporateModelId And "
						+ "								CZ.taxationModelId = CT.taxationModelId) And "
						+ "			CT.taxationModelId in (SELECT Tx.id "
						+ "								   FROM TaxationModel Tx "
						+ "								   WHERE Tx.endDate >= current_date() OR Tx.endDate is null)").list();
		
		return corporateTaxDetModelList;
	}
	
	public String update(CorporateTaxDetModel corporateTaxDetModel) {
		System.out.println("update query has been executed ...!!");
		sessionFactory.getCurrentSession().merge(corporateTaxDetModel);
		return "updateSuccess";
	}
}
