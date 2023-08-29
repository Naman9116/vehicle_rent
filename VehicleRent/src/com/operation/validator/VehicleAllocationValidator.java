package com.operation.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import com.operation.model.VehicleAllocationModel;

@Component
public class VehicleAllocationValidator implements Validator {

	@Override
	public boolean supports(Class<?> c) {
		return VehicleAllocationModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
		VehicleAllocationModel vehicleAllocationModel = (VehicleAllocationModel)command;
        
//		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "Name Can Not Be Blank");
//		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "code", "Code Can Not Be Blank");

	}

}
