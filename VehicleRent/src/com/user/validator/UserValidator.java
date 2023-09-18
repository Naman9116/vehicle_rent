package com.user.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import com.user.model.UserModel;

@Component
public class UserValidator implements Validator {

	private static final String Name_REGEX =
            "[A-Z a-z]+";

    public static boolean isValidName(String name) {
      
    	 return Pattern.matches(Name_REGEX, name);
       
    }  
	@Override
	public boolean supports(Class<?> c) {
		return UserModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
		UserModel userModel = (UserModel)command;
        
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "User Name Can Not Be Blank");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Passowrd Can Not Be Blank");
	if(userModel.getUserFirstName().equals("")||userModel.getUserFirstName().equals(null)){
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userFirstName", "FirstName Can Not Be Blank");
		}
	else {
		if(!isValidName(userModel.getUserFirstName())){
		errors.rejectValue("userFirstName","The first name should only include letters from A to Z, both lowercase and uppercase");
	}
		}
    if(userModel.getUserLastName().equals("")||userModel.getUserLastName().equals(null)){
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userLastName", "LastName Can Not Be Blank");
    }else {
		if(!isValidName(userModel.getUserLastName())){
			errors.rejectValue("userLastName","The first name should only include letters from A to Z, both lowercase and uppercase");
		}
    	}
	}

}
