package com.master.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.common.service.MasterDataService;
import com.master.model.CarAllocationModel;
import com.master.service.CarAllocationMasterService;
import com.operation.model.CarDetailModel;
import com.operation.service.CarDetailService;
import com.util.JsonResponse;
import com.util.Message;
import com.util.Utilities;

@Controller
public class CarAllocationController {
	private static final Logger logger = Logger.getLogger(CarAllocationController.class);

	@Autowired
	private CarAllocationMasterService carAllocationMasterService;
	
	@Autowired
	private CarDetailService carDetailService;
	
	@Autowired
	private MasterDataService masterDataService;
	

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields(new String[] {"id"});
		dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
	@RequestMapping("/pageLoadCarAllocation")
	public ModelAndView pageLoadCarAllocation(Map<String, Object> map) {
		ModelAndView modelAndView=new ModelAndView("carAllocation");
		try {
			map.put("existingData", masterDataService.getGeneralMasterData_UsingCode("CARALLOCATION"));
			map.put("carDetailsList", masterDataService.getCarRegistrationNumberList());
			map.put("vendorList", masterDataService.getGeneralMasterData_UsingCode("VENDOR")); 
			map.put("chauffeurList", masterDataService.getGeneralMasterData_UsingCode("CHAUFFEUR")); 
			map.put("carOwnerTypeList", masterDataService.getGeneralMasterData_UsingCode("VOT")); 
			map.put("paymentTrafficList", masterDataService.getGeneralMasterData_UsingCode("POT")); 
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("carAllocationModel", new CarAllocationModel());
			map.put("existingData", showGrid(carAllocationMasterService.list()));
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/saveOrUpdateCarAllocation",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateCarAllocation(
			@ModelAttribute(value="carAllocationModel") CarAllocationModel carAllocationModel,
			BindingResult bindingResult,
			@RequestParam("idForUpdate") String idForUpdate,
			@RequestParam("carStatus") String carStatus,HttpServletRequest request){
		JsonResponse res = new JsonResponse();
		if(bindingResult.hasErrors()){
			res.setStatus("Errors");
			res.setResult(bindingResult.getAllErrors());
			return res;
		}
		
		try{
			if(idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")){
				res.setResult(Message.SAVE_SUCCESS_MESSAGE);
			}else{
				carAllocationModel.setId(Long.valueOf(idForUpdate));
				res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
			}
			CarDetailModel carDetailModel = carDetailService.getCarDetailsBasedOnRegNo(carAllocationModel.getCarDetailModelId().getRegistrationNo());
			carAllocationModel.setCarDetailModelId(carDetailModel);
			List<CarAllocationModel> carAllocationModels = new ArrayList<CarAllocationModel>();
			carAllocationModels.add(carAllocationModel);
			carDetailModel.setCarAllocationModels(carAllocationModels);
			carDetailService.update(carDetailModel);
			res.setStatus("Success");
		}catch(ConstraintViolationException e){
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.DUPLICATE_ERROR_MESSAGE);
		}catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}finally{
			res.setDataGrid(showGrid(carAllocationMasterService.list()));
		}
		return res;
	}
	
	@RequestMapping(value="/formFillForEditCarAllocation",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditCarAllocation(
			@RequestParam("formFillForEditId") String formFillForEditId){
		JsonResponse res = new JsonResponse();
		CarAllocationModel carAllocationModel = null;
		try{
			carAllocationModel=carAllocationMasterService.formFillForEdit(Long.parseLong(formFillForEditId));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setCarAllocationModel(carAllocationModel);
		}
		return res;
	}
	
	@RequestMapping(value="/deleteCarAllocation",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteCarAllocation(
			@RequestParam("idForDelete") String idForDelete){
		JsonResponse res = new JsonResponse();
		try{
			carAllocationMasterService.delete(Long.valueOf(idForDelete));
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
			res.setDataGrid(showGrid(carAllocationMasterService.list()));
		}
		return res;
	}
	
	public String showGrid(List<CarAllocationModel> carAllocationModelDataList){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dataString =" <table class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"+
		"	<thead style='background-color: #FED86F;'>		"+
		" 	<tr> 											"+
	  	" 		<th width="+Message.ACTION_SIZE+">Action</th> "+
	  	"   	<th>Car No.</th> 				"+
	  	"   	<th>Owner Type</th> 	"+
	  	"   	<th>Vendor Name</th> 		"+
	  	"   	<th>Chauffeur Name</th> 		"+
	  	"   	<th>Car Owner Type</th> 		"+
	  	"   	<th>Date of Allotment</th>"+
	  	"   	<th>Car Make</th> 		"+
	  	"   	<th>Car Model</th> 			"+
	  	"   	<th>Body Color</th> 				"+
	  	"   	<th>Payment Tariff</th> 			"+
	  	"   	<th>Company Share</th> 		"+
	  	"   	<th>Duties Allow</th> 		"+
	  	"   	<th>Car Status</th> 		"+
	  	" 	</tr>							"+
	  	"	</thead>						"+
	  	"   </tbody>						"; 
		for(CarAllocationModel carAllocationModel:carAllocationModelDataList ){
			String sDutiesAllow = carAllocationModel.getDutyAllow().equals("Y")?"Yes":"No";
			String sStatus = carAllocationModel.getCarStatus().equals("Y")?"Active":"InActive";
			
			dataString+=" <tr> "+
			" <td> "+
			" 	<a href='javascript:formFillForEdit("+carAllocationModel.getId()+")'>"+Message.EDIT_BUTTON+"</a>"+
			" 	<a href='javascript:viewRecord("+carAllocationModel.getId()+")' >"+Message.VIEW_BUTTON+"</a> "+ 				
			" 	<a href='javascript:deleteRecord("+carAllocationModel.getId()+")' >"+Message.DELETE_BUTTON+"</a> "+ 				
			" </td>"+
			" <td style='font-align : center;'>"+ carAllocationModel.getCarDetailModelId().getRegistrationNo()+ "</td>   ";
			if (carAllocationModel.getCarDetailModelId().getOwnType().equals("C")){
				dataString+=" <td>Company</td>   ";
			}else {
				dataString+=" <td>Vendor</td>   ";
			}
			String sChauffeurName = carAllocationModel.getChauffeurId() ==null?"":carAllocationModel.getChauffeurId().getName();
			dataString+=	" <td>"+ carAllocationModel.getVendorId().getName()+ "</td>   "+
					" <td>"+ sChauffeurName + "</td>   "+
					" <td>"+ carAllocationModel.getCarOwnerType().getName()+"</td>   "+
					" <td>"+ dateFormat.format(carAllocationModel.getDateOfAllotment())+ "</td>   "+
					" <td>"+ carAllocationModel.getCarDetailModelId().getMake().getName()+ "</td>   "+
					" <td>"+ carAllocationModel.getCarDetailModelId().getModel().getName()+ "</td>   "+
					" <td>"+ carAllocationModel.getCarDetailModelId().getBodyColor().getName()+ "</td>   "+
					" <td>"+ carAllocationModel.getPaymentTraffic().getName()+ "</td>   "+
					" <td>"+ carAllocationModel.getCompanyShare()+ "</td>   "+
					" <td>"+ sDutiesAllow + "</td>   "+
					" <td>"+ sStatus + "</td>   "+
					" </tr>";
		}	
		dataString=dataString+"</tbody></table>";
		return dataString;
	}
	
	@RequestMapping(value="/fillCarRelDetails",method=RequestMethod.POST)
	public @ResponseBody JsonResponse fillCarRelDetails(
			@RequestParam("selectedRegNo") String selectedRegNo){
		JsonResponse res = new JsonResponse();
		try{
			CarAllocationModel carAllocationModel = carAllocationMasterService.getCarRelDetailsBasedOnRegNo(selectedRegNo);
			res.setCarAllocationModel(carAllocationModel);
			res.setStatus("Success");
		}		
		catch(Exception e) {
			res.setStatus("Failure");
			logger.error("",e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
		}
		return res;
	}
}
