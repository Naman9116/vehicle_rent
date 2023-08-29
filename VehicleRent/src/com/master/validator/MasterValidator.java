package com.master.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import com.master.model.MasterModel;

@Component
public class MasterValidator implements Validator {

	@Override
	public boolean supports(Class<?> c) {
		return MasterModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
		MasterModel masterModel = (MasterModel)command;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "41");
		

		if(masterModel.getName()!= null && masterModel.getName().length()>100){
			errors.rejectValue("name","42");
		}
	}
}
