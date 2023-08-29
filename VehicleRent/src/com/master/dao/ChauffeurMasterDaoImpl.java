package com.master.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.master.model.ChauffeurModel;

@Repository("chauffeurMasterDao")
public class ChauffeurMasterDaoImpl implements ChauffeurMasterDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	public List<ChauffeurModel> list() {
		List<ChauffeurModel> chauffeurModelList = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChauffeurModel.class);
		chauffeurModelList = (List<ChauffeurModel>)criteria.list();
		return chauffeurModelList;
	}
	
	public ChauffeurModel formFillForEdit(Long formFillForEditId) {
		ChauffeurModel chauffeurModel=(ChauffeurModel) sessionFactory.getCurrentSession().get(ChauffeurModel.class, new Long(formFillForEditId));
		return chauffeurModel;
	}
	
	public String save(ChauffeurModel chauffeurModel) {
		sessionFactory.getCurrentSession().save(chauffeurModel);
		return "saveSuccess";
	}
	
	public String update(ChauffeurModel chauffeurModel) {
		sessionFactory.getCurrentSession().update(chauffeurModel);
		return "updateSuccess";
	}

	public String delete(Long idForDelete) {
		ChauffeurModel chauffeurModel =(ChauffeurModel) sessionFactory.getCurrentSession().get(ChauffeurModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(chauffeurModel);
		return "deleteSuccess";
	}
	@SuppressWarnings("unchecked")
	public ChauffeurModel formFillForEditChauffeurDetails(Long formFillForEditId, String formFillForEditMob) {
		List<ChauffeurModel> chauffeurModelList = null;
		 chauffeurModelList = sessionFactory.getCurrentSession()
								.createQuery("FROM ChauffeurModel WHERE vendorId.id = "+formFillForEditId+" AND mobileNo = '"+formFillForEditMob+"'").list();
		if(chauffeurModelList !=null  && chauffeurModelList.size()>0)
			return chauffeurModelList.get(0);
		else
		 return null;
	}
	
}
