package com.corporate.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.corporate.model.CorporateModel;

@Component
public class CorporateValidator implements Validator {

	@Override
	public boolean supports(Class<?> c) {
		return CorporateModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
		CorporateModel corporateModel = (CorporateModel)command;
		if(corporateModel.getCompId().getId()== null) errors.rejectValue("compId.id","Please, select company");
		if(corporateModel.getName().equals("")) errors.rejectValue("name","Corporate name cannot be blank");
		if(corporateModel.getAgreementDt()==null|| corporateModel.getAgreementDt().equals("")) errors.rejectValue("agreementDt","Agreement date cannot be blank");
		if(corporateModel.getExpiryDt()==null|| corporateModel.getExpiryDt().equals("")) errors.rejectValue("expiryDt","expiry date cannot be blank");
		if(corporateModel.getAddressDetailModel().getState().getId()==null || corporateModel.getAddressDetailModel().getState().getId()==0L) errors.rejectValue("addressDetailModel.state.id","State Can Not Be Blank");
		if(corporateModel.getAddressDetailModel().getDistrict().getId()==null || corporateModel.getAddressDetailModel().getDistrict().getId()==0L) errors.rejectValue("addressDetailModel.district.id","District Can Not Be Blank");
		if(corporateModel.getAddressDetailModel().getCity().getId()==null || corporateModel.getAddressDetailModel().getCity().getId()==0L) errors.rejectValue("addressDetailModel.city.id","City Can Not Be Blank");
		if(corporateModel.getMinRoundSlab().equals("")) errors.rejectValue("minRoundSlab", "Rounding Slab (Min) can't be blank");
	}

}
