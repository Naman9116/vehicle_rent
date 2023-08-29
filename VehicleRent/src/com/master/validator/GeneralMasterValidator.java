package com.master.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import com.master.model.GeneralMasterModel;

@Component
public class GeneralMasterValidator implements Validator {

	@Override
	public boolean supports(Class<?> c) {
		return GeneralMasterModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
		GeneralMasterModel generalMasterModel = (GeneralMasterModel)command;
        
		if(generalMasterModel.getMasterId()!= null && generalMasterModel.getMasterId()==0){
			errors.rejectValue("name","Master Name Can Not Be Blank");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "Name Can Not Be Blank");
/*		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "code", "Code Can Not Be Blank");
		if(generalMasterModel.getCode()!= null && generalMasterModel.getCode().length()>20){
			errors.rejectValue("code","Code Length Should Be Less Than 20");
		}
*/
	}

}
