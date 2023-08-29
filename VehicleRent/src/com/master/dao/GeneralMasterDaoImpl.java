package com.master.dao;

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

import com.corporate.model.AutorizedUserModel;
import com.master.model.GeneralMasterModel;
import com.master.model.MappingMasterModel;
import com.master.model.MasterModel;
import com.master.model.TariffSchemeParaModel;
import com.master.service.GeneralMasterService;
import com.operation.model.TempModel;

@Repository("generalMasterDao")
public class GeneralMasterDaoImpl implements GeneralMasterDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private GeneralMasterService generalMasterService;
	
	@SuppressWarnings("unchecked")
	public List<GeneralMasterModel> list() {
		List<GeneralMasterModel> generalMasterModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(GeneralMasterModel.class).
		setProjection(Projections.projectionList().
			add(Projections.property("id"),"id").
			add(Projections.property("remark"),"remark").
			add(Projections.property("name"),"name")).setResultTransformer(Transformers.aliasToBean(GeneralMasterModel.class));
		criteria.addOrder(Order.asc("name"));

		generalMasterModel = (List<GeneralMasterModel>)criteria.list();
		return generalMasterModel;
	}
	
	@SuppressWarnings("unchecked")
	public List<GeneralMasterModel> listMasterWise(Long masterId, String masterCode) {
		List<GeneralMasterModel> generalMasterModel = null;
		String otherInfo = "";

		if(masterCode.equals("ZONE")){
			otherInfo  = " ,(SELECT B.id FROM GeneralMasterModel B, MappingMasterModel MM WHERE MM.subMasterValuesModel.id = A.id And B.id = MM.masterValuesModel.id) as extraId,"
						+"  (SELECT B.name FROM GeneralMasterModel B, MappingMasterModel MM WHERE MM.subMasterValuesModel.id = A.id And B.id = MM.masterValuesModel.id) as extraName ";
		}else if(masterCode.equals("LOC")){
			otherInfo  = " ,(SELECT B.id FROM GeneralMasterModel B, MappingMasterModel MM WHERE MM.subMasterValuesModel.id = A.id And B.id = (SELECT MMM.masterValuesModel.id FROM MappingMasterModel MMM WHERE MMM.subMasterValuesModel.id = MM.masterValuesModel.id)) as extraId,"
						+"  (SELECT B.name FROM GeneralMasterModel B, MappingMasterModel MM WHERE MM.subMasterValuesModel.id = A.id And B.id = (SELECT MMM.masterValuesModel.id FROM MappingMasterModel MMM WHERE MMM.subMasterValuesModel.id = MM.masterValuesModel.id)) as extraName, "
					    +"  (SELECT B.id FROM GeneralMasterModel B, MappingMasterModel MM WHERE MM.subMasterValuesModel.id = A.id And B.id = MM.masterValuesModel.id) as zoneId,"
						+"  (SELECT B.name FROM GeneralMasterModel B, MappingMasterModel MM WHERE MM.subMasterValuesModel.id = A.id And B.id = MM.masterValuesModel.id) as zoneName ";
		}
		
		String sSql = "SELECT A.id as id, A.name as name, "
				    + "       A.remark as remark, A.sortId as sortId ";
			   sSql	+= otherInfo;
			   sSql += " FROM GeneralMasterModel A, MasterModel M  "
				     + " WHERE M.id = A.masterModel.id And "
				     + "      (M.code = '"+masterCode+"' OR '"+masterCode+"' = 'ALL') And "
				     + "      (M.id   = '"+masterId+"' OR '"+masterId+"' = '0') ";
		try{
			generalMasterModel = (List<GeneralMasterModel>) sessionFactory.getCurrentSession().createQuery(sSql).setResultTransformer(Transformers.aliasToBean(GeneralMasterModel.class)).list();
		}catch(Exception e){
			e.printStackTrace();
		}
		return generalMasterModel;
	}

	public String save(GeneralMasterModel generalMasterModel, String masterCode) {
		generalMasterModel.setMasterModel( (MasterModel) sessionFactory.getCurrentSession().get(MasterModel.class, new Long(generalMasterModel.getMasterId())));
		sessionFactory.getCurrentSession().save(generalMasterModel);
		if(generalMasterModel.getTariffSchemeParaModel() != null){
			generalMasterModel.getTariffSchemeParaModel().setParentId(generalMasterModel.getId().longValue());
			sessionFactory.getCurrentSession().save(generalMasterModel.getTariffSchemeParaModel());
		}
		if(masterCode.equals("ZONE") || masterCode.equals("LOC")){
			MappingMasterModel mappingMasterModel = new MappingMasterModel();
			GeneralMasterModel parentId = new GeneralMasterModel();
			if(masterCode.equals("ZONE")){
				parentId.setId(generalMasterModel.getExtraId());
			}else if(masterCode.equals("LOC")){
				parentId.setId(generalMasterModel.getZoneId());
			}
			mappingMasterModel.setMasterValuesModel(parentId);
			mappingMasterModel.setSubMasterValuesModel(generalMasterModel);
			sessionFactory.getCurrentSession().save(mappingMasterModel);
		}
		return "saveSuccess";
	}
	public Long saveGeneralMaster(GeneralMasterModel generalMasterModel) {
		generalMasterModel.setMasterModel( (MasterModel) sessionFactory.getCurrentSession().get(MasterModel.class, new Long(generalMasterModel.getMasterId())));
		sessionFactory.getCurrentSession().save(generalMasterModel);
		Query query = sessionFactory.getCurrentSession().createQuery("SELECT MAX(id) FROM GeneralMasterModel");
		Long maxId = (Long) query.list().get(0);
		return maxId;
	}
	
	
	public GeneralMasterModel formFillForEdit(Long formFillForEditId, String masterCode) {
		GeneralMasterModel generalMasterModel=null;
		generalMasterModel =(GeneralMasterModel) sessionFactory.getCurrentSession().get(GeneralMasterModel.class, new Long(formFillForEditId));

		if(masterCode.equals("ZONE") || masterCode.equals("LOC")){
			MappingMasterModel branchZoneMapping = null;
			MappingMasterModel zoneLocationMapping = null;
			Query query;
			if(masterCode.equals("ZONE")){
				query = sessionFactory.getCurrentSession().createQuery("FROM MappingMasterModel WHERE subMasterValuesModel.id = '"+generalMasterModel.getId().longValue()+"'");
				branchZoneMapping = query.list().size() > 0 ? (MappingMasterModel)query.list().get(0) : null;
				if(branchZoneMapping != null) generalMasterModel.setExtraId(branchZoneMapping.getMasterValuesModel().getId().longValue());
			}else if(masterCode.equals("LOC")){
				query = sessionFactory.getCurrentSession().createQuery("FROM MappingMasterModel WHERE subMasterValuesModel.id = '"+generalMasterModel.getId().longValue()+"'");
				zoneLocationMapping = query.list().size() > 0 ? (MappingMasterModel)query.list().get(0) : null;
				if(zoneLocationMapping != null) generalMasterModel.setZoneId(zoneLocationMapping.getMasterValuesModel().getId().longValue());

				query = sessionFactory.getCurrentSession().createQuery("FROM MappingMasterModel WHERE subMasterValuesModel.id = '"+zoneLocationMapping.getMasterValuesModel().getId().longValue()+"'");
				branchZoneMapping = query.list().size() > 0 ? (MappingMasterModel)query.list().get(0) : null;
				if(branchZoneMapping != null) generalMasterModel.setExtraId(branchZoneMapping.getMasterValuesModel().getId().longValue());
			}
		}
	
		TariffSchemeParaModel tariffSchemeParaModel = (TariffSchemeParaModel)sessionFactory.getCurrentSession().createQuery("FROM TariffSchemeParaModel WHERE parentId = " + generalMasterModel.getId().toString()).uniqueResult();
		generalMasterModel.setTariffSchemeParaModel(tariffSchemeParaModel);
		return generalMasterModel;
	}
	
	public String delete(Long idForDelete) {
		GeneralMasterModel generalMasterModel =(GeneralMasterModel) sessionFactory.getCurrentSession().get(GeneralMasterModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(generalMasterModel);
		return "deleteSuccess";
	}
	
	public String update(GeneralMasterModel generalMasterModel, String masterCode) {
		generalMasterModel.setMasterModel( (MasterModel) sessionFactory.getCurrentSession().get(MasterModel.class, new Long(generalMasterModel.getMasterId())));
		sessionFactory.getCurrentSession().update(generalMasterModel);
		if(generalMasterModel.getTariffSchemeParaModel() != null){
			generalMasterModel.getTariffSchemeParaModel().setParentId(generalMasterModel.getId().longValue());
			sessionFactory.getCurrentSession().merge(generalMasterModel.getTariffSchemeParaModel());
		}
		
		if(masterCode.equals("ZONE") || masterCode.equals("LOC")){
			MappingMasterModel branchZoneMapping = null;
			MappingMasterModel zoneLocationMapping = null;
			Query query;
			GeneralMasterModel branchZoneModel = new GeneralMasterModel();
			if(masterCode.equals("ZONE")){
				query = sessionFactory.getCurrentSession().createQuery("FROM MappingMasterModel WHERE subMasterValuesModel.id = '"+generalMasterModel.getId().longValue()+"'");
				branchZoneMapping = query.list().size() > 0 ? (MappingMasterModel)query.list().get(0) : null;
				
				if(branchZoneMapping == null){
					branchZoneMapping = new MappingMasterModel();
					branchZoneMapping.setSubMasterValuesModel(generalMasterModel);
				}
				branchZoneModel.setId(generalMasterModel.getExtraId());
				branchZoneMapping.setMasterValuesModel(branchZoneModel);
			}else if(masterCode.equals("LOC")){
				query = sessionFactory.getCurrentSession().createQuery("FROM MappingMasterModel WHERE subMasterValuesModel.id = '"+generalMasterModel.getId().longValue()+"'");
				zoneLocationMapping = query.list().size() > 0 ? (MappingMasterModel)query.list().get(0) : null;
				if(zoneLocationMapping == null){
					zoneLocationMapping = new MappingMasterModel();
					zoneLocationMapping.setSubMasterValuesModel(generalMasterModel);
				}
				branchZoneModel.setId(generalMasterModel.getZoneId());
				zoneLocationMapping.setMasterValuesModel(branchZoneModel);

				query = sessionFactory.getCurrentSession().createQuery("FROM MappingMasterModel WHERE subMasterValuesModel.id = '"+zoneLocationMapping.getMasterValuesModel().getId().longValue()+"'");
				branchZoneMapping = query.list().size() > 0 ? (MappingMasterModel)query.list().get(0) : null;
				if(branchZoneMapping == null){
					branchZoneMapping = new MappingMasterModel();
					branchZoneMapping.setSubMasterValuesModel(zoneLocationMapping.getMasterValuesModel());
				}
				branchZoneModel = new GeneralMasterModel();
				branchZoneModel.setId(generalMasterModel.getExtraId());
				branchZoneMapping.setMasterValuesModel(branchZoneModel);
			}

			if(branchZoneMapping != null){
				sessionFactory.getCurrentSession().merge(branchZoneMapping);
			}
			if(zoneLocationMapping != null){
				sessionFactory.getCurrentSession().merge(zoneLocationMapping);
			}
			
		}
		return "updateSuccess";
	}

	@Override
	public String setSortOrder(Long masterId, String sortedArr) {
		String[] sSortedId = sortedArr.split("~");
		List<GeneralMasterModel> generalMasterModelList =generalMasterService.listMasterWise(masterId,"ALL");
		for(GeneralMasterModel gMM:generalMasterModelList ){
			if(gMM != null){
				for(int iLoop=0; iLoop < sSortedId.length; iLoop++){
					if(gMM.getName().equals(sSortedId[iLoop])){
						gMM.setSortId((long) iLoop);
						gMM.setMasterId(masterId);
						generalMasterService.update(gMM,"");
					}
				}
			}
		}
		return "Success";
	}
}
