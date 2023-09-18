package com.operation.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.operation.model.CarDetailModel;
import com.operation.model.InsuranceDetailModel;
import com.operation.model.TaxDetailModel;

@Component
public class CarDetailValidator implements Validator {

	@Override
	public boolean supports(Class<?> c) {
		return CarDetailModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
		CarDetailModel carDetailModel = (CarDetailModel)command;
		if(carDetailModel.getOwnType().equals("")) errors.rejectValue("ownType","Owner Type should provide");
		if(carDetailModel.getOwnName().equals("--- Select ---")) errors.rejectValue("ownName","Owner Name should provide");
		if(carDetailModel.getBranchId().getId()== null) errors.rejectValue("branchId","Branch should provide");
		if(carDetailModel.getFuelId().getId()== null) errors.rejectValue("fuelId","Car Version should provide");
		if(carDetailModel.getModel().getId() == null) errors.rejectValue("model","Car Model should provide");
		if(carDetailModel.getMake().getId() == null) errors.rejectValue("make","Car Make should provide");
		if(carDetailModel.getRegistrationNo().equals("")) errors.rejectValue("registrationNo","Car Registration No. should provide");
		/*If ownser type is Company than only validate these*/
		if(carDetailModel.getOwnType().equals("C")){
			if(carDetailModel.getEngineNo().equals("")) errors.rejectValue("engineNo","Car Engine No. should provide");
			if(carDetailModel.getChasisNo().equals("")) errors.rejectValue("chasisNo","Car Chasis No. should provide");
			if(carDetailModel.getKeyNo().equals("")) errors.rejectValue("keyNo","Car Key No. should provide");
			if(carDetailModel.getBodyColor().getId() == null) errors.rejectValue("bodyColor","Car Body Color should provide");
			if(carDetailModel.getBodyStyle().getId() == null) errors.rejectValue("bodyStyle","Car Body Style should provide");
		}		
		if(carDetailModel.getStatus().equals("")) errors.rejectValue("status","Car Status should provide");
		if( carDetailModel.getOwnType().equals("C")||carDetailModel.getOwnType().equals("V")){
//		for( InsuranceDetailModel insuranceDetailModel : carDetailModel.getInsuranceDetailModel()){
//	         if(insuranceDetailModel.getInsuEndDate() !=null && insuranceDetailModel.getInsuEndDate().equals(" ")) errors.rejectValue("insuranceDetailModel.insuEndDate","Insurance Upto should provide");
//				
//			//if(insuranceDetailModel.getInsuEndDate()==null) errors.rejectValue("insuranceDetailModel.insuEndDate","Insurance Upto should provide");
//		}
//		
//		for( TaxDetailModel taxDetailModel : carDetailModel.getTaxDetailModel()){
//			if(taxDetailModel.getRtoEndDate()!=null) errors.rejectValue("taxDetailModel.rtoEndDate","Permit Upto should provide");
//			if(taxDetailModel.getStatePermitDetailModel().get(0).getEndDate()!=null) errors.rejectValue("taxDetailModel.statePermitDetailModel[0].endDate","Fitness Upto should provide");
//			if(taxDetailModel.getStatePermitDetailModel().get(1).getEndDate()!=null) errors.rejectValue("taxDetailModel.statePermitDetailModel[1].endDate","PUC Upto should provide");
//		}
	   }
	}
}
