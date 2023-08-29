package com.operation.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import com.operation.model.DutySlipModel;

@Component
public class DutySlipValidator implements Validator {

	@Override
	public boolean supports(Class<?> c) {
		return DutySlipModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
		DutySlipModel dutySlipModel = (DutySlipModel)command;
        
//		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "Name Can Not Be Blank");
//		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "code", "Code Can Not Be Blank");

	}

}
