package com.master.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.master.model.MappingMasterModel;

@Component
public class MappingMasterValidator implements Validator {

	@Override
	public boolean supports(Class<?> c) {
		return MappingMasterModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
		MappingMasterModel mappingMasterModel = (MappingMasterModel)command;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "masterId", "Master Name Can Not Be Blank");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "masterValue", "Master Value Can Not Be Blank");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subMasterId", "Sub Master Name Can Not Be Blank");
	}

}
