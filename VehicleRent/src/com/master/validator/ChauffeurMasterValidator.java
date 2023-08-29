package com.master.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.master.model.ChauffeurModel;

@Component
public class ChauffeurMasterValidator implements Validator {

	@Override
	public boolean supports(Class<?> c) {
		return ChauffeurModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
		ChauffeurModel chauffeurModel = (ChauffeurModel)command;
		if(chauffeurModel.getVendorId().equals("")) errors.rejectValue("vendorId","Vendor name should provide");
		if(chauffeurModel.getName().equals("")) errors.rejectValue("name","Chauffeur name should provide");
		if(chauffeurModel.getMobileNo().equals("")) errors.rejectValue("mobileNo","Mobile No. should provide");
		if(chauffeurModel.getDrivingLicence().equals("")) errors.rejectValue("drivingLicence","Driving Licence should provide");
		if(chauffeurModel.getDlIssue()==null) errors.rejectValue("dlIssue","Date Of Issue should provide");
		if(chauffeurModel.getDlValidUpto()==null) errors.rejectValue("dlValidUpto","Valid Upto should provide");
		if(chauffeurModel.getDlAuthority().equals("")) errors.rejectValue("dlAuthority","Licence Authority should provide");
		if(chauffeurModel.getDoj()==null) errors.rejectValue("doj","Date Of Joining should provide");
		if(chauffeurModel.getDutyAllow().equals("")) errors.rejectValue("dutyAllow","Duties Allow should provide");
		if(chauffeurModel.getStatus().equals("")) errors.rejectValue("status","Driver Sratus should provide");
		if(chauffeurModel.getUserName().equals("")) errors.rejectValue("userName","User Name cannot be blank");
		if(chauffeurModel.getPswd().length() < 8) errors.rejectValue("pswd","Password Can not be less then 8 Character");
	}

}
