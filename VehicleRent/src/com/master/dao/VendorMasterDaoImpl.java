package com.master.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.master.model.TaxationModel;
import com.master.model.VendorModel;

@Repository("vendorMasterDao")
public class VendorMasterDaoImpl implements VendorMasterDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	public List<VendorModel> list() {
		List<VendorModel> vendorModelList = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(VendorModel.class);
		vendorModelList = (List<VendorModel>)criteria.addOrder(Order.asc("id")).list();
		return vendorModelList;
	}
	
	public VendorModel formFillForEdit(Long formFillForEditId) {
		VendorModel vendorModel=(VendorModel) sessionFactory.getCurrentSession().get(VendorModel.class, new Long(formFillForEditId));
		return vendorModel;
	}
	
	public String save(VendorModel vendorModel) {
		sessionFactory.getCurrentSession().save(vendorModel);
		return "saveSuccess";
	}
	
	public String update(VendorModel vendorModel) {
		sessionFactory.getCurrentSession().update(vendorModel);
		return "updateSuccess";
	}

	public String delete(Long idForDelete) {
		VendorModel vendorModel =(VendorModel) sessionFactory.getCurrentSession().get(VendorModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(vendorModel);
		return "deleteSuccess";
	}
}
