package com.master.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.master.model.CorporateTariffModel;

@Component
public class CorporateTariffValidator implements Validator {

	@Override
	public boolean supports(Class<?> c) {
		return CorporateTariffModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
		CorporateTariffModel corporateTariffModel = (CorporateTariffModel)command;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fuelRate","Fuel Rate should provide");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currFuelRate","Current Fuel Rate should provide");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fuelHikeDate","Current Fuel Hile Date should provide");
		
		if(corporateTariffModel.getBranchId() !=null && corporateTariffModel.getBranchId().getId().equals("0")) errors.rejectValue("branchId.id","Branch should select");
		if(corporateTariffModel.getCorporateId()!=null && corporateTariffModel.getCorporateId().getId().equals("0")) errors.rejectValue("corporateId.id","Corporate should select");
		if(corporateTariffModel.getFuelRate()!=null && corporateTariffModel.getFuelRate().equals("0")) errors.rejectValue("fuelRate","Fuel Rate should provide");
		if(corporateTariffModel.getCurrFuelRate()!=null && corporateTariffModel.getCurrFuelRate().equals("0")) errors.rejectValue("currFuelRate","Current Fuel Rate should provide");
		if(corporateTariffModel.getFuelHikeDate()!=null && corporateTariffModel.getFuelHikeDate().equals("dd/mm/yyyy")) errors.rejectValue("fuelHikeDate","Current Fuel Hile Date should provide");

		
	}

}
