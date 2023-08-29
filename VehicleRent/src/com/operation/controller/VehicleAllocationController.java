package com.operation.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.util.JsonResponse;
import com.util.Message;
import com.util.Utilities;
import com.common.service.MasterDataService;
import com.operation.model.BookingMasterModel;
import com.operation.model.CarDetailModel;
import com.operation.model.VehicleAllocationModel;
import com.operation.service.VehicleAllocationService;
import com.operation.validator.VehicleAllocationValidator;

@Controller
public class VehicleAllocationController {
	private static final Logger logger = Logger.getLogger(VehicleAllocationController.class);

	@Autowired
	private VehicleAllocationService vehicleAllocationService;
	
	@Autowired
	private MasterDataService masterDataService;
	
	private VehicleAllocationValidator validator = null;

	public VehicleAllocationValidator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(VehicleAllocationValidator validator) {
		this.validator = validator;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields(new String[] {"id"});
		dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		dateFormat.setLenient(false);
		dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
	@RequestMapping("/pageLoadVehicleAllocation/{pageNumber}")
	public ModelAndView pageLoadVehicleAllocation(Map<String, Object> map,@PathVariable("pageNumber") int pageNumber) {
		ModelAndView modelAndView=new ModelAndView("vehicleAllocation");
		try {
			map.put("vehicleAllocationDataList",showGrid(vehicleAllocationService.list( pageNumber), pageNumber));
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("vehicleAllocationModel", new VehicleAllocationModel());
			map.put("pageNumber",pageNumber);
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/saveOrUpdateVehicleAllocation",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateVehicleAllocation(
			@ModelAttribute(value="vehicleAllocationModel") VehicleAllocationModel vehicleAllocationModel,
			BindingResult bindingResult ,
			@RequestParam("pageNumber") String pageNumber,
			@RequestParam("vehicleIdSelectedValue") String vehicleIdSelectedValue){
		JsonResponse res = new JsonResponse();
		try{
			validator.validate(vehicleAllocationModel, bindingResult);
			if(!bindingResult.hasErrors()){
				vehicleAllocationService.save(vehicleAllocationModel,vehicleIdSelectedValue);
				res.setResult(Message.SAVE_SUCCESS_MESSAGE);
				res.setStatus("Success");
			}
			else{
				res.setStatus("Errors");
				res.setResult(bindingResult.getAllErrors());
			}
		}
		catch(ConstraintViolationException e){
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.DUPLICATE_ERROR_MESSAGE);
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setDataGrid(showGrid(vehicleAllocationService.list( Integer.parseInt(pageNumber)), Integer.parseInt(pageNumber)));
		}
		return res;
	}
	
	@RequestMapping(value="/formFillForEditVehicleAllocation",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditVehicleAllocation(
			@ModelAttribute(value="vehicleAllocationModel") VehicleAllocationModel vehicleAllocationModel,
			BindingResult bindingResult ,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("formFillForEditId") String formFillForEditId){
		JsonResponse res = new JsonResponse();
		try{
			vehicleAllocationModel=vehicleAllocationService.formFillForEdit(Long.parseLong(formFillForEditId));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setDataGrid(showGrid(vehicleAllocationService.list( pageNumber), pageNumber));
			//res.setVehicleAllocationModel(vehicleAllocationModel);
		}
		return res;
	}
	
	@RequestMapping(value="/deleteVehicleAllocation",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteVehicleAllocation(
			@ModelAttribute(value="vehicleAllocationModel") VehicleAllocationModel vehicleAllocationModel,
			BindingResult bindingResult ,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("idForDelete") String idForDelete){
		JsonResponse res = new JsonResponse();
		try{
			vehicleAllocationService.delete(Long.valueOf(idForDelete));
			res.setStatus("Success");
			res.setResult(Message.DELETE_SUCCESS_MESSAGE);
		}
		catch(DataIntegrityViolationException e){
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FOREIGNKEY_ERROR_MESSAGE);
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setDataGrid(showGrid(vehicleAllocationService.list( pageNumber), pageNumber));
		}
		return res;
	}
	
	public String showGrid(List<BookingMasterModel> bookingMasterModelDataList,int pageNumber){
		String dataString =	
		" <table align='center' width='100%' id='dataTable' border='1'> 	"+
		  	" <tr> 																"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='select' name='select' readonly tabindex='-1' size='5' value='Select'></b></th> "+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='16' value='Booking No.'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='18' value='Customer Name'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='16' value='Booking Date'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='18' value='Booked by '></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='12' value='Billing'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='18' value='Booking taken By'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='18' value='Booking Passed To'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='12' value='IATA NO.'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='18' value='Booking From Date'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='18' value='Booking To Date'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='14' value='Outlet'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='14' value='Tariff'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='14' value='Car Type'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='14' value='Rental Type'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='14' value='Flight No.'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='14' value='Start Time'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='14' value='Pick Up Time'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='18' value='Reporting Address'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='18' value='To be Release At'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='16' value='Instruction'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='14' value='Actual Rate'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='14' value='Status'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='17' value='Driver Name'></b></th> 			"+
		  	" <th align='center'><b><input style='text-align:center' type'text' id='heading' name='heading' readonly tabindex='-1' size='16' value='Used By'></b></th> 			"+
		  	" </tr>"; 
			for(BookingMasterModel bookingMasterModel:bookingMasterModelDataList ){
//				String bookingPassedTo = bookingMasterModel.getBookingPassedTo()==null?"":bookingMasterModel.getBookingPassedTo().getUserName();
				dataString=dataString+" <tr style='height:20px;'> "+
/*				" <td align='center'><input onclick='getVehicleTypeData_VehicleAllocation("+bookingMasterModel.getBookingDetailModel().get(0).getCarType().getId()+",this.value)' type='radio' id='bookingId' name='bookingId' value='"+bookingMasterModel.getBookingDetailModel().get(0).getId()+"'></td>"+
				" <td align='center'>"+ bookingMasterModel.getId()+ "</td>   "+
				" <td align='center'>"+ bookingMasterModel.getCustomerName().getUserName()+ "</td>   "+
				" <td align='center'>"+ bookingMasterModel.getBookingDate()+ "</td>   "+
				" <td align='center'>"+ bookingMasterModel.getBookedBy().getUserName()+ "</td>   "+
				" <td align='center'>"+ bookingMasterModel.getBilling()+ "</td>   "+
				" <td align='center'>"+ bookingMasterModel.getBookingTakenBy().getUserName()+ "</td>   "+
				" <td align='center'>"+ bookingPassedTo+ "</td>   "+
				" <td align='center'>"+ bookingMasterModel.getIataNo()+ "</td>   "+
				" <td align='center'>"+ bookingMasterModel.getBookingDetailModel().get(0).getBookingFromDate()+ "</td>   "+
				" <td align='center'>"+ bookingMasterModel.getBookingDetailModel().get(0).getBookingToDate()+ "</td>   "+
				" <td align='center'>"+ bookingMasterModel.getBookingDetailModel().get(0).getOutlet()+ "</td>   "+
//				" <td align='center'>"+ bookingMasterModel.getBookingDetailModel().get(0).getTariff().getTariff().getName()+ "</td>   "+
				" <td align='center'>"+ bookingMasterModel.getBookingDetailModel().get(0).getCarType().getName()+ "</td>   "+
				" <td align='center'>"+ bookingMasterModel.getBookingDetailModel().get(0).getRentalType().getName()+ "</td> "+
				" <td align='center'>"+ bookingMasterModel.getBookingDetailModel().get(0).getFlightNo()+ "</td> "+
				" <td align='center'>"+ bookingMasterModel.getBookingDetailModel().get(0).getStartTime()+"</td> "+
				" <td align='center'>"+ bookingMasterModel.getBookingDetailModel().get(0).getPickUpTime()+"</td> "+
				" <td align='center'>"+ bookingMasterModel.getBookingDetailModel().get(0).getReportingAddress()+ "</td> "+
				" <td align='center'>"+ bookingMasterModel.getBookingDetailModel().get(0).getToBeRealeseAt()+ "</td> "+
				" <td align='center'>"+ bookingMasterModel.getBookingDetailModel().get(0).getInstruction()+ "</td> "+
				" <td align='center'>"+ bookingMasterModel.getBookingDetailModel().get(0).getActualRate()+ "</td> "+
				" <td align='center'>"+ bookingMasterModel.getBookingDetailModel().get(0).getStatus()+ "</td> "+
				" <td align='center'>"+ bookingMasterModel.getBookingDetailModel().get(0).getDriverName().getUserName()+ "</td> "+
				" <td align='center'>"+ bookingMasterModel.getBookingDetailModel().get(0).getUsedBy()+ "</td> "+
*/				" </tr>";
			}	
		dataString=dataString+"</table>";
		return dataString;
	}

	@RequestMapping(value="/getBookingData_VehicleAllocation",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getBookingData_VehicleAllocation(
			@ModelAttribute(value="vehicleAllocationModel") VehicleAllocationModel vehicleAllocationModel,
			BindingResult bindingResult ,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("searchCriteria") String searchCriteria){
		JsonResponse res = new JsonResponse();
		try{
			res.setDataGrid(showGrid(vehicleAllocationService.listMasterWise(pageNumber, searchCriteria),pageNumber));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value="/getVehicleTypeData_VehicleAllocation",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getVehicleTypeData_VehicleAllocation(
			@ModelAttribute(value="vehicleAllocationModel") VehicleAllocationModel vehicleAllocationModel,
			BindingResult bindingResult,
			@RequestParam("carTypeId") String carTypeId,
			@RequestParam("searchCriteria") String searchCriteria){
		JsonResponse res = new JsonResponse();
		try{
			res.setDataGrid(showVehicleDataGrid(vehicleAllocationService.getVehicleTypeData(Long.parseLong(carTypeId),searchCriteria,vehicleAllocationModel.getBookingId().getId())));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	public String showVehicleDataGrid(List<CarDetailModel> carDetailModelList){
		String dataString =	
		" <table align='center' width='100%' id='dataTable5' border='1'> 	"+
		  	" <tr> 																"+
		  	" <th align='center'><b><input type'text' id='select' name='select' style='width:100%;text-align:center' readonly tabindex='-1' size='4' value='Select'></b></th> 			"+
		  	" <th align='center'><b><input type'text' id='heading' style='width:100%;text-align:center' name='heading' readonly tabindex='-1' value='RegistrationNo'></b></th> 			"+
		  	" <th align='center'><b><input type'text' id='heading' style='width:100%;text-align:center' name='heading' readonly tabindex='-1' value='BodyColor'></b></th> 			"+
		  	" <th align='center'><b><input type'text' id='heading' style='width:100%;text-align:center' name='heading' readonly tabindex='-1' value='Make'></b></th> 			"+
		  	" <th align='center'><b><input type'text' id='heading' style='width:100%;text-align:center' name='heading' readonly tabindex='-1' value='Model '></b></th> 			"+
		  	" </tr>"; 
			for(CarDetailModel carDetailModel:carDetailModelList ){
				dataString=dataString+" <tr style='height:20px;'> "+
				" <td align='center'><input type='checkBox' id='vehicleId' name='vehicleId' class='vehicleId' value='"+carDetailModel.getId()+"'></td>"+
				" <td align='center'>"+ carDetailModel.getRegistrationNo()+ "</td>   "+
				" <td align='center'>"+ carDetailModel.getBodyColor().getName()+ "</td> "+
				" <td align='center'>"+ carDetailModel.getMake().getName()+ "</td> "+
				" <td align='center'>"+ carDetailModel.getModel().getName()+ "</td> "+
				" </tr>";
			}	
			if(carDetailModelList!=null && carDetailModelList.size()==0){
				dataString=dataString+" <tr style='height:20px;'><td align='center' colspan=5><B>NO DATA FOUND</B></td></tr>";
			}
		dataString=dataString+"</table>";
		return dataString;
	}

}
