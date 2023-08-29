package com.corporate.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.corporate.model.AutorizedUserModel;

@Component
public class AuthorisedUserValidator implements Validator {

	@Override
	public boolean supports(Class<?> c) {
		return AutorizedUserModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
		AutorizedUserModel autorizedUserModel = (AutorizedUserModel)command;
		if(autorizedUserModel.getCorporateId().getId()== null) errors.rejectValue("corporateId.id","Please, select corporate");
		if(autorizedUserModel.getPageFor().equals("Admin")){
			if(autorizedUserModel.getAuthTypeAdmin().equals("N") && autorizedUserModel.getAuthTypeClient().equals("N")) errors.rejectValue("authTypeAdmin","Please select Authorisation");
			if(autorizedUserModel.getName().equals("")) errors.rejectValue("name","Authorise name cannot be blank");
		}else{
			if(autorizedUserModel.getParentId() == null) errors.rejectValue("parentId","Please select Authoriser");
			if(autorizedUserModel.getName().equals("")) errors.rejectValue("name","Client name cannot be blank");
			
		}
		if(autorizedUserModel.getPageFor().equals("Client")){
			if(autorizedUserModel.getUserName().equals("")) errors.rejectValue("userName","User Name cannot be blank");
			if(autorizedUserModel.getPswd().length()<8) errors.rejectValue("pswd","Password Can not be less then 8 Character");
		}
		if(autorizedUserModel.getEntityContact().getPersonalMobile().equals("")) errors.rejectValue("entityContact.personalMobile","Mobile No. cannot be blank");
//		if(autorizedUserModel.getEntityContact().getContactPerson2().equals("")) errors.rejectValue("entityContact.contactPerson2","Designation cannot be blank");
//		if(autorizedUserModel.getEntityContact().getPersonalEmailId().equals("")) errors.rejectValue("entityContact.personalEmailId","E-Mail cannot be blank"); /*Removed mendatory 27/07/2016*/
	}
}
