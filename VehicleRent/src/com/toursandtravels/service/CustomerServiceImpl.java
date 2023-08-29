package com.toursandtravels.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.toursandtravels.dao.CustomerDao;
import com.toursandtravels.model.CustomerModel;


@Service("customerService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CustomerServiceImpl implements CustomerService {
	@Autowired
	private CustomerDao customerDao;
	public CustomerServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(CustomerModel   customerModel) {
		return customerDao.save(customerModel);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(CustomerModel   customerModel) {
		return customerDao.update(customerModel);
	}
	
	@Override
	public List<CustomerModel> getCustomers() {
		return customerDao.getCustomers();
	}
	
	@Override
	public CustomerModel formFillForEditCustomer(Long id) {
		return customerDao.formFillForEditCustomer(id);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete)  {
		return customerDao.delete(idForDelete);
	}

}
