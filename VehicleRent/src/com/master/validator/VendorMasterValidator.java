package com.master.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.master.model.GeneralMasterModel;
import com.master.model.TaxationModel;
import com.master.model.VendorModel;

@Component
public class VendorMasterValidator implements Validator {

	@Override
	public boolean supports(Class<?> c) {
		return VendorModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
		VendorModel vendorModel = (VendorModel)command;
		if(vendorModel.getName().equals("")) errors.rejectValue("name","Vendor name cannot be blank");
		if(vendorModel.getPan().equals("")) errors.rejectValue("PAN","PAN cannot be blank");
		if(vendorModel.getAgDate()==null) errors.rejectValue("agDate","Agreement date should provide");
		if(vendorModel.getOrgTypeId().getId() == null) errors.rejectValue("orgTypeId.id","Organazation Type should provide");

		if(vendorModel.getBankAcName().equals("")) errors.rejectValue("bankAcName","Bank Account Holder cannot be blank");
		if(vendorModel.getBankAcNo().equals("")) errors.rejectValue("bankAcNo","Bank A/c No. cannot be blank");
		if(vendorModel.getBankName().equals("")) errors.rejectValue("bankName","Bank Name cannot be blank");
		if(vendorModel.getBankNEFT().equals("")) errors.rejectValue("bankNEFT","NEFT Code cannot be blank");
	}

}
