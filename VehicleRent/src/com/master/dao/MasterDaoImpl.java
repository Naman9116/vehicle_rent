package com.master.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.master.model.MasterModel;
import com.operation.model.CarDetailModel;

@Repository("masterDao")
public class MasterDaoImpl implements MasterDao{

	@Autowired
	private SessionFactory sessionFactory;
	private static int pageSize = 7;
	
	@SuppressWarnings("unchecked")
	public List<MasterModel> list(int pageNumber) {
		List<MasterModel> masterModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MasterModel.class).
		addOrder(Order.desc("id"));
		criteria.setFirstResult(pageSize * (pageNumber - 1)); 
		criteria.setMaxResults(pageSize);
		masterModel = (List<MasterModel>)criteria.list();
		return masterModel;
	}
	
	public String save(MasterModel masterModel) {
		sessionFactory.getCurrentSession().save(masterModel);
		return "saveSuccess";
	}
	
	public MasterModel formFillForEdit(Long formFillForEditId) {
		MasterModel masterModel=null;
		masterModel =(MasterModel) sessionFactory.getCurrentSession().get(MasterModel.class, new Long(formFillForEditId));
		return masterModel;
	}
	
	public String delete(Long idForDelete) {
		MasterModel masterModel =(MasterModel) sessionFactory.getCurrentSession().get(MasterModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(masterModel);
		return "deleteSuccess";
	}
	
	public String update(MasterModel masterModel) {
		sessionFactory.getCurrentSession().update(masterModel);
		return "updateSuccess";
	}

}
