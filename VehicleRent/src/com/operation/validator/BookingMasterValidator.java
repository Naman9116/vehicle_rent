package com.operation.validator;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.operation.model.BookingMasterModel;

@Component
public class BookingMasterValidator implements Validator {

	@Override
	public boolean supports(Class<?> c) {
		return BookingMasterModel.class.isAssignableFrom(c);
	}

	@Override
	public void validate(Object command, Errors errors) {
		BookingMasterModel bookingMasterModel = (BookingMasterModel)command;
		if(bookingMasterModel.getCorporateId().getId() == 0) errors.rejectValue("corporateId.id","Corporate Cant Blank");
		if(bookingMasterModel.getBookingDate() == null) errors.rejectValue("bookingDate","Booking Date Cant Blank");
		if(bookingMasterModel.getBookedBy().getId() == 0) errors.rejectValue("bookedBy.id","Booked By Cant Blank");
		if(bookingMasterModel.getBookedForName().contains("Select") || bookingMasterModel.getBookedForName().equalsIgnoreCase("")) errors.rejectValue("bookedForName","Booked For Cant Blank");
		
		Date sBookDate = bookingMasterModel.getBookingDate();
		
		for(int i=0;i<bookingMasterModel.getBookingDetailModel().size();i++){
			if(bookingMasterModel.getBookingDetailModel().get(i).getCompany().getId() == 0) errors.rejectValue("bookingDetailModel["+i+"].company.id","Company Cant Blank");
			if(bookingMasterModel.getBookingDetailModel().get(i).getBranch().getId() == 0) errors.rejectValue("bookingDetailModel["+i+"].branch.id","Branch Cant Blank");
			if(bookingMasterModel.getBookingDetailModel().get(i).getOutlet().getId() == 0) errors.rejectValue("bookingDetailModel["+i+"].outlet.id","Hub Cant Blank");
			if(bookingMasterModel.getBookingDetailModel().get(i).getPickUpDate() == null) errors.rejectValue("bookingDetailModel["+i+"].pickUpDate","Booking Date Cant Blank");			
			if(bookingMasterModel.getBookingDetailModel().get(i).getMob().getId() == 0) errors.rejectValue("bookingDetailModel["+i+"].mob.id","M.O.B. Cant Blank");
			if(bookingMasterModel.getBookingDetailModel().get(i).getCarModel().getId() == 0) errors.rejectValue("bookingDetailModel["+i+"].carModel.id","Car Model Cant Blank");
			if(bookingMasterModel.getBookingDetailModel().get(i).getRentalType().getId() == 0) errors.rejectValue("bookingDetailModel["+i+"].rentalType.id","Rental Type Cant Blank");
			if(bookingMasterModel.getBookingDetailModel().get(i).getTariff().getId() == 0) errors.rejectValue("bookingDetailModel["+i+"].tariff.id","Tariff Cant Blank");
			
			Date dFromDate = bookingMasterModel.getBookingDetailModel().get(i).getPickUpDate();
			if(sBookDate!=null && dFromDate!=null)
				if(dFromDate.before(sBookDate)){
					errors.rejectValue("bookingDetailModel["+i+"].bookingDate","Invalid Booking Date");
				}				
		}
	
	}

}
