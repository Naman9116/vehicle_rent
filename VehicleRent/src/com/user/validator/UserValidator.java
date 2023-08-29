package com.user.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import com.user.model.UserModel;

@Component
public class UserValidator implements Validator {

	@Override
	public boolean supports(Class<?> c) {
		return UserModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
		UserModel userModel = (UserModel)command;
        
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "User Name Can Not Be Blank");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Passowrd Can Not Be Blank");
	}

}
