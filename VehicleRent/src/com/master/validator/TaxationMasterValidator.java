package com.master.validator;

import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.master.model.GeneralMasterModel;
import com.master.model.TaxationModel;

@Component
public class TaxationMasterValidator implements Validator {
	
   @Override
	public boolean supports(Class<?> c) {
		return TaxationModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
		TaxationModel taxationModel = (TaxationModel)command;
//		if(taxationModel.getParentId().getId().equals("")) errors.rejectValue("parentId.id","Taxation name should select");
//		if(taxationModel.getEfDate().equals("")) errors.rejectValue("efDate","Effective Date cannot be blank");
//		if(taxationModel.getCalType().equals("")) errors.rejectValue("calType","Calculation type should select");
		if(taxationModel.getParentId().getId()!= null && taxationModel.getParentId().getId()==0){
			 errors.rejectValue("parentId.id","Taxation name should select");
		}
		if(taxationModel.getEfDate()==null) errors.rejectValue("efDate","Effective Date cannot be blank");
		if(!taxationModel.getCalType().equals("C")&&!taxationModel.getCalType().equals("L")) errors.rejectValue("calType","Calculation type should select");
		
	}

}
