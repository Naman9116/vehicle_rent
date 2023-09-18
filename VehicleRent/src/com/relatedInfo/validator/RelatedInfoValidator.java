package com.relatedInfo.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.relatedInfo.model.RelatedInfoModel;

@Component
public class RelatedInfoValidator implements Validator {

	@Override
	public boolean supports(Class<?> c) {
		return RelatedInfoModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
		RelatedInfoModel relatedInfoModel = (RelatedInfoModel)command;
		if(relatedInfoModel.getParentType().equalsIgnoreCase("company")){
			if(relatedInfoModel.getCompanyName().equals("")) errors.rejectValue("companyName","Company name cannot be blank");
			if(relatedInfoModel.getPanNo().equals("")) errors.rejectValue("panNo","PAN cannot be blank");
			if(relatedInfoModel.getCstNo().equals("")) errors.rejectValue("cstNo","Service Tax No. cannot be blank");
			if(relatedInfoModel.getRegDate().equals("")) errors.rejectValue("regDate","Date of Registration cannot be blank");
			if(relatedInfoModel.getEmail().equals("")) errors.rejectValue("email","E-Mail cannot be blank");
			if (!isValidEmail(relatedInfoModel.getEmail())) {
			    errors.rejectValue("email", "Invalid.email, Enter a valid email address");
			}
		}else if(relatedInfoModel.getParentType().equalsIgnoreCase("branch")){
			if(relatedInfoModel.getCompanyName().equals("")) errors.rejectValue("companyName","Company name cannot be blank");
			if(relatedInfoModel.getBranchName().equals("")) errors.rejectValue("branchName","Branch name cannot be blank");
			if(relatedInfoModel.getRegDate().equals("")) errors.rejectValue("regDate","Date of Opening cannot be blank");
			if(relatedInfoModel.getEmail().equals("")) errors.rejectValue("email","E-Mail cannot be blank");
		}else if(relatedInfoModel.getParentType().equalsIgnoreCase("outlet")){
			if(relatedInfoModel.getBranchName().equals("")) errors.rejectValue("branchName","Branch name cannot be blank");
			if(relatedInfoModel.getOutletName().equals("")) errors.rejectValue("outletName","Outlet name cannot be blank");
			if(relatedInfoModel.getRegDate().equals("")) errors.rejectValue("regDate","Date of Opening cannot be blank");
			if(relatedInfoModel.getEmail().equals("")) errors.rejectValue("email","E-Mail cannot be blank");
		}
		if(relatedInfoModel.getAddressDetailModel().getState().getId()==null || relatedInfoModel.getAddressDetailModel().getState().getId()==0L) errors.rejectValue("addressDetailModel.state.id","State Can Not Be Blank");
		if(relatedInfoModel.getAddressDetailModel().getDistrict().getId()==null || relatedInfoModel.getAddressDetailModel().getDistrict().getId()==0L) errors.rejectValue("addressDetailModel.district.id","District Can Not Be Blank");
		if(relatedInfoModel.getAddressDetailModel().getCity().getId()==null || relatedInfoModel.getAddressDetailModel().getCity().getId()==0L) errors.rejectValue("addressDetailModel.city.id","City Can Not Be Blank");
	}

	private static final String EMAIL_REGEX =
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static boolean isValidEmail(String email) {
        
        return Pattern.matches(EMAIL_REGEX, email);
    }

}
