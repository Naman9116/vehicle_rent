package com.toursandtravels.service;
import java.util.List;

import com.toursandtravels.model.CustomerModel;
public interface CustomerService {

	public String save(CustomerModel   customerModel) ;
	public String update(CustomerModel   customerModel) ;
	public List<CustomerModel> getCustomers();
	public CustomerModel formFillForEditCustomer(Long id);
	public String delete(Long idForDelete) ;

}
