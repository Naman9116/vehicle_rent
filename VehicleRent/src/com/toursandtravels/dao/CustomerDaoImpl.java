package com.toursandtravels.dao;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.toursandtravels.model.CustomerModel;

@Repository("customerDao")
public class CustomerDaoImpl implements CustomerDao {

	private static final Logger logger = Logger.getLogger(BookingDaoImpl.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public String save(CustomerModel   customerModel) {
		sessionFactory.getCurrentSession().save(customerModel);	
		return "SUCCESS";
	}
	
	@Override
	public String update(CustomerModel   customerModel) {
		sessionFactory.getCurrentSession().update(customerModel);	
		return "SUCCESS";
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<CustomerModel> getCustomers() {
		List<CustomerModel> customerModelList = null;
	
		String sSql = "SELECT C FROM CustomerModel C " ;
		customerModelList= sessionFactory.getCurrentSession().createQuery(sSql).list();
		return customerModelList;
	}
	@Override
	public CustomerModel formFillForEditCustomer(Long id){
		CustomerModel customerModel =null;
		customerModel =(CustomerModel) sessionFactory.getCurrentSession()
							.createQuery("SELECT C FROM CustomerModel C  where id = "+id).uniqueResult();
		return customerModel;
	}
	@Override
	public String delete(Long idForDelete) {
		CustomerModel customerModel =(CustomerModel) sessionFactory.getCurrentSession().get(CustomerModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(customerModel);
		return "deleteSuccess";
	}
	

}
