package com.master.dao;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.master.model.GeneralMasterModel;
import com.master.model.MappingMasterModel;
import com.master.model.MasterModel;

@Repository("mappingMasterDao")
public class MappingMasterDaoImpl implements MappingMasterDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	public List<MappingMasterModel> list() {
		List<MappingMasterModel> mappingMasterModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MappingMasterModel.class).
		setProjection(Projections.projectionList().
			add(Projections.property("id"),"id").
			add(Projections.property("parentId"),"parentId").
			add(Projections.property("childId"),"childId")).setResultTransformer(Transformers.aliasToBean(MappingMasterModel.class));
		criteria.addOrder(Order.desc("id"));
		mappingMasterModel = (List<MappingMasterModel>)criteria.list();
		return mappingMasterModel;
	}
	
	@SuppressWarnings("unchecked")
	public List<MappingMasterModel> listMasterWise(Long masterId) {
		List<MappingMasterModel> mappingMasterModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MappingMasterModel.class).
		setProjection(Projections.projectionList().
			add(Projections.property("id"),"id").
			add(Projections.property("parentId"),"parentId").
			add(Projections.property("childId"),"childId")).setResultTransformer(Transformers.aliasToBean(MappingMasterModel.class));
		criteria.addOrder(Order.desc("id"));
		criteria.add(Restrictions.eq("masterMasterModel.masterId", masterId));
		mappingMasterModel = (List<MappingMasterModel>)criteria.list();
		return mappingMasterModel;
	}

	public String save(MappingMasterModel mappingMasterModel,String selectedNotMappedCheckBoxValues) {
		if(selectedNotMappedCheckBoxValues.equalsIgnoreCase("")){
			String sReturnId = "0";
			sessionFactory.getCurrentSession().save(mappingMasterModel);
			Query query = sessionFactory.getCurrentSession().createQuery("SELECT MAX(id) FROM MappingMasterModel");
			Long lastId = (Long) query.list().get(0);
			sReturnId = String.valueOf(lastId);
			return sReturnId;
		}
		
		String[] checkBoxValues=selectedNotMappedCheckBoxValues.split(",");
		Long subMasteId=mappingMasterModel.getMasterValue();
		GeneralMasterModel generalMasterModel=((GeneralMasterModel) sessionFactory.getCurrentSession().get(GeneralMasterModel.class, subMasteId));
		
		GeneralMasterModel generalMasterModel2=null;
		MappingMasterModel mappingMasterModel2=null;
		for (String checkBoxValue:checkBoxValues){
			generalMasterModel2=new GeneralMasterModel();
			mappingMasterModel2=new MappingMasterModel();
			
			mappingMasterModel2.setMasterValuesModel(generalMasterModel2);
			mappingMasterModel2.getMasterValuesModel().setId(subMasteId);
			
			generalMasterModel2=new GeneralMasterModel();
			mappingMasterModel2.setSubMasterValuesModel(generalMasterModel2);
			mappingMasterModel2.getSubMasterValuesModel().setId(Long.parseLong(checkBoxValue));
			
			generalMasterModel.getMappingMasterModel().add(mappingMasterModel2);
		}
		System.out.println(" generalMasterModel.getMappingMasterModel().size() : "+generalMasterModel.getMappingMasterModel().size());
		
		sessionFactory.getCurrentSession().save(generalMasterModel);
		return "saveSuccess";
	}
	
	public MappingMasterModel formFillForEdit(Long formFillForEditId) {
		MappingMasterModel mappingMasterModel=null;
		mappingMasterModel =(MappingMasterModel) sessionFactory.getCurrentSession().get(MappingMasterModel.class, new Long(formFillForEditId));
		return mappingMasterModel;
	}

	@SuppressWarnings("unchecked")
	public String remove(MappingMasterModel mappingMasterModel,String selectedAlreadyMappedCheckBoxValues) {
		if(selectedAlreadyMappedCheckBoxValues.equalsIgnoreCase("")){
			Query query = sessionFactory.getCurrentSession().createQuery("DELETE FROM MappingMasterModel "
					+ "WHERE childId = " + mappingMasterModel.getSubMasterValuesModel().getId()) ;
			query.executeUpdate();
			return "deleteSuccess";
		}
		
		String[] checkBoxValues=selectedAlreadyMappedCheckBoxValues.split(",");
		Long []checkBoxValuesLongs=new Long[checkBoxValues.length];
		checkBoxValuesLongs=new Long[checkBoxValues.length];
		for(int i=0;i<checkBoxValuesLongs.length;i++){
			checkBoxValuesLongs[i]=Long.parseLong(checkBoxValues[i]);
		}
		List<MappingMasterModel> mappingMasterModelList = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MappingMasterModel.class);
		criteria.add(Restrictions.eq("masterValuesModel.id", mappingMasterModel.getMasterValue()));
		criteria.add(Restrictions.in("subMasterValuesModel.id", checkBoxValuesLongs));
		mappingMasterModelList = (List<MappingMasterModel>)criteria.list();
		for(MappingMasterModel mappingMasterModel1:mappingMasterModelList){
			sessionFactory.getCurrentSession().delete(mappingMasterModel1);
		}
		return "deleteSuccess";
	}
	
	public String delete(Long idForDelete) {
		MappingMasterModel mappingMasterModel =(MappingMasterModel) sessionFactory.getCurrentSession().get(MappingMasterModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(mappingMasterModel);
		return "deleteSuccess";
	}
	
	public String update(MappingMasterModel mappingMasterModel) {
		sessionFactory.getCurrentSession().update(mappingMasterModel);
		return "updateSuccess";
	}
	
	@SuppressWarnings("unchecked")
	public Object[] fetchMasterValuesAndSubMasterNamesMappingMaster(Long selectedId){
		Object objects[]=null;
		objects=new Object[2];
		List<GeneralMasterModel> masterValues=null;
		List<MasterModel> subMasterNames=null;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(GeneralMasterModel.class).
		setProjection(Projections.projectionList().
				add(Projections.property("id"),"id").
				add(Projections.property("name"),"name")).setResultTransformer(Transformers.aliasToBean(GeneralMasterModel.class));
		criteria.addOrder(Order.asc("name"));
		criteria.add(Restrictions.eq("masterModel.id", selectedId));
		
		masterValues = (List<GeneralMasterModel>)criteria.list();  // Select Master Values 

		String sSql = "select A.id as id, A.name as name from MasterModel A, MasterMapModel B where A.id = B.subMasterValuesModel.id and B.masterValuesModel.id = " + selectedId;
		Query query =  sessionFactory.getCurrentSession().createQuery(sSql).setResultTransformer(Transformers.aliasToBean(MasterModel.class));
		subMasterNames = query.list();
/*		criteria = sessionFactory.getCurrentSession().createCriteria(MasterModel.class).
		setProjection(Projections.projectionList().
				add(Projections.property("id"),"id").
				add(Projections.property("name"),"name")).setResultTransformer(Transformers.aliasToBean(MasterModel.class));
		criteria.addOrder(Order.desc("id"));
		criteria.add(Restrictions.eq("parentid", selectedId));
		subMasterNames = (List<MasterModel>)criteria.list();           	//  Select Sub Master name
*/		
		objects[0]=masterValues;
		objects[1]=subMasterNames;
		return objects;
	}
	
	@SuppressWarnings("unchecked")
	public Object[] fetchSubMasterValuesMappingMaster(Long selectedId,Long parentId){
		Object objects[]=null;
		objects=new Object[2];
		List<GeneralMasterModel> generalMasterModel=null;
		if(parentId == 0){
			Long lParentId = 0l;
			Query query = sessionFactory.getCurrentSession().createQuery("SELECT distinct masterValuesModel.id FROM MappingMasterModel WHERE subMasterValuesModel.id = " + selectedId);
			if(!query.list().isEmpty())
				if(query.list().size() > 1)
					lParentId = 0l;
				else
					lParentId = (Long) query.list().get(0);
			objects[0] = lParentId;
			return objects;
		}
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(GeneralMasterModel.class).
		setProjection(Projections.projectionList().
			add(Projections.property("id"),"id").
			add(Projections.property("name"),"name")).setResultTransformer(Transformers.aliasToBean(GeneralMasterModel.class));
		criteria.addOrder(Order.asc("name"));

		criteria.add(Restrictions.eq("masterModel.id", selectedId));
		criteria.add(Subqueries.propertyNotIn("id", DetachedCriteria.forClass(MappingMasterModel.class).
				add(Restrictions.eq("masterValuesModel.id",parentId)).
				setProjection(Projections.distinct(Projections.property("subMasterValuesModel.id")))));
		generalMasterModel = (List<GeneralMasterModel>)criteria.list();
		objects[0]=generalMasterModel;

		criteria = sessionFactory.getCurrentSession().createCriteria(GeneralMasterModel.class).
		setProjection(Projections.projectionList().
			add(Projections.property("id"),"id").
			add(Projections.property("name"),"name")).setResultTransformer(Transformers.aliasToBean(GeneralMasterModel.class));
		criteria.addOrder(Order.asc("name"));

		criteria.add(Restrictions.eq("masterModel.id", selectedId));
		criteria.add(Subqueries.propertyIn("id", DetachedCriteria.forClass(MappingMasterModel.class).
				add(Restrictions.eq("masterValuesModel.id",parentId)).
				setProjection(Projections.distinct(Projections.property("subMasterValuesModel.id")))));

		generalMasterModel = (List<GeneralMasterModel>)criteria.list();
		objects[1]=generalMasterModel;
		return objects;
	}
		
}
