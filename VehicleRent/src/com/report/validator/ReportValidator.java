package com.report.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.relatedInfo.model.RelatedInfoModel;

@Component
public class ReportValidator implements Validator {

	@Override
	public boolean supports(Class<?> c) {
		return RelatedInfoModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
        
		//ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "RelatedInfo Name Can Not Be Blank");
	//	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userCode", "RelatedInfo Code Can Not Be Blank");
		//ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Passowrd Can Not Be Blank");
		//ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dob", "DOB Can Not Be Blank");
		//ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userType.id", "RelatedInfo Type Can Not Be Blank");
		

	}

}
