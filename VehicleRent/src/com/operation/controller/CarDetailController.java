package com.operation.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import com.operation.model.CarDetailModel;
import com.operation.model.DutySlipModel;
import com.operation.service.CarDetailService;
import com.operation.validator.CarDetailValidator;
import com.util.JsonResponse;
import com.util.Message;

@Controller
public class CarDetailController {
	private static final Logger logger = Logger.getLogger(CarDetailController.class);

	@Autowired
	private CarDetailService carDetailService;
	
	@Autowired
	private MasterDataService masterDataService;
	
	private CarDetailValidator validator = null;

	public CarDetailValidator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(CarDetailValidator validator) {
		this.validator = validator;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields(new String[] {"id"});
		dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
	@RequestMapping("/pageLoadCarDetail")
	public ModelAndView pageLoadCarDetail(Map<String, Object> map, HttpSession session) {
		ModelAndView modelAndView=new ModelAndView("carDetail");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try {
			map.put("fuelIdList",masterDataService.getGeneralMasterData_UsingCode("FUEL")); 			// Select Vehicle Make
			map.put("makeIdList",masterDataService.getGeneralMasterData_UsingCode("VMK")); 			// Select Vehicle Make
			map.put("modalIdList",masterDataService.getGeneralMasterData_UsingCode("VMD")); 			// Select Vehicle Modal
			map.put("bodyStyleIdList",masterDataService.getGeneralMasterData_UsingCode("VBS")); 	// Select Vehicle Body Style
			map.put("bodyColorIdList",masterDataService.getGeneralMasterData_UsingCode("VBC")); 	// Select Vehicle BodyS Color
			map.put("companyIdList",masterDataService.getGeneralMasterData_UsingCode("VCM")); 		// Select Vehicle Company
			map.put("branchIdList",masterDataService.getGeneralMasterData_UsingCode("VBM-A:"+branchesAssigned)); 		// Select Vehicle Branch
			map.put("carDetailDataList",showGrid(carDetailService.list()));
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("carDetailModel", new CarDetailModel());
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/getVendorCompany",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getVendorCompany(@RequestParam("ownerType") String ownerType){
		JsonResponse res = new JsonResponse();
		try{
			if(ownerType.equals("C"))
				res.setGeneralMasterModelList(masterDataService.getGeneralMasterData_UsingCode("VCM"));
			else if(ownerType.equals("V"))
				res.setGeneralMasterModelList(masterDataService.getGeneralMasterData_UsingCode("VENDOR"));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value="/getBranchAsCompany",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getBranchAsCompany(@RequestParam("companyId") String companyId,
			@RequestParam("ownerType") String ownerType, HttpSession session){
		JsonResponse res = new JsonResponse();
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try{
			if(ownerType.equals("C"))
				res.setGeneralMasterModelList(masterDataService.getGeneralMasterData_UsingCode("LinkedBranchCompany:"+companyId+":"+branchesAssigned));
			else if(ownerType.equals("V"))
				res.setGeneralMasterModelList(masterDataService.getGeneralMasterData_UsingCode("VBM-A:"+branchesAssigned));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	@RequestMapping(value="/saveOrUpdateCarDetail",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateCarDetail(
			@ModelAttribute(value="carDetailModel") CarDetailModel carDetailModel,
			BindingResult bindingResult,
			@RequestParam("idForUpdate") String idForUpdate){
		JsonResponse res = new JsonResponse();
		validator.validate(carDetailModel, bindingResult);
		if(bindingResult.hasErrors()){
			res.setStatus("Errors");
			res.setResult(bindingResult.getAllErrors());
			return res;
		}
		try{
		/*	if( carDetailModel.getOwnType().equals("C")||carDetailModel.getOwnType().equals("V")){*/
			if((carDetailModel.getInsuranceDetailModel()!=null)&&(carDetailModel.getInsuranceDetailModel().size() > 0 ))
				carDetailModel.getInsuranceDetailModel().get(0).setCarDetailModel(carDetailModel);
			if((carDetailModel.getInsuranceDetailModel()!=null)&&(carDetailModel.getTaxDetailModel().size() > 0)){
				carDetailModel.getTaxDetailModel().get(0).setCarDetailModel(carDetailModel);
				for(int i=0;i<carDetailModel.getTaxDetailModel().get(0).getStatePermitDetailModel().size();i++){
					carDetailModel.getTaxDetailModel().get(0).getStatePermitDetailModel().get(i).setTaxDetailModel(carDetailModel.getTaxDetailModel().get(0));
				}
			}
			/* }*/
			if(idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")){
				carDetailService.save(carDetailModel);
				res.setResult(Message.SAVE_SUCCESS_MESSAGE);
			}
			else{
				carDetailModel.setId(Long.valueOf(idForUpdate));
				carDetailService.update(carDetailModel);
				res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
			}
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
			res.setDataGrid(showGrid(carDetailService.list()));
		}
		return res;
	}
	
	@RequestMapping(value="/formFillForEditCarDetail",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditCarDetail(
			@ModelAttribute(value="carDetailModel") CarDetailModel carDetailModel,
			BindingResult bindingResult,
			@RequestParam("formFillForEditId") String formFillForEditId){
		JsonResponse res = new JsonResponse();
		try{
			carDetailModel=carDetailService.formFillForEdit(Long.parseLong(formFillForEditId));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setCarDetailModel(carDetailModel);
		}
		return res;
	}
	
	@RequestMapping(value="/deleteCarDetail",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteCarDetail(
			@ModelAttribute(value="carDetailModel") CarDetailModel carDetailModel,
			BindingResult bindingResult,
			@RequestParam("idForDelete") String idForDelete){
		JsonResponse res = new JsonResponse();
		try{
			carDetailService.delete(Long.valueOf(idForDelete));
			res.setStatus("Success");
			res.setResult(Message.DELETE_SUCCESS_MESSAGE);
		}
		catch(DataIntegrityViolationException e){
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FOREIGNKEY_ERROR_MESSAGE);
		}
		catch(Exception e) {
			res.setStatus("Failure");
			logger.error("",e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setDataGrid(showGrid(carDetailService.list()));
		}
		return res;
	}
	
	public String showGrid(List<CarDetailModel> carDetailModelList){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String sInsuEndDate ="";
		String sRtoEndDate="";
		String dataString =	
		" <table class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"+
		"	<thead style='background-color: #FED86F;'>		"+
		" 	<tr> 											"+
	  	" 		<th width="+Message.ACTION_SIZE+">Action</th> "+
	  	"   	<th>Car ID</th> 				"+
	  	"   	<th>Owner Name</th> 		"+
	  	"   	<th>Branch</th> 			"+
	  	"   	<th>Car No.</th> 	"+
	  	"   	<th>Car Model</th> 			"+
	  	"   	<th>Year</th> 			"+
	  	"   	<th>Version</th> 	"+
	  	"   	<th>Body Color</th> 		"+
	  	"   	<th>Purchase Date</th> 	"+
	  	"   	<th>EMI</th> 		"+
	  	"   	<th>EMI Start</th> 			"+
	  	"   	<th>EMI End</th> 			"+
	  	"   	<th>Ins. Upto</th> 			"+
	  	"   	<th>Permit Upto</th> 			"+
	  	"   	<th>Fitness Upto</th> 			"+
	  	"   	<th>Car Status</th> 			"+
	  	" 	</tr>							"+
	  	"	</thead>						"+
	  	"   </tbody>						"; 
		for(CarDetailModel carDetailModel:carDetailModelList ){
			 sInsuEndDate ="";
			 sRtoEndDate="";
			String sBranch = carDetailModel.getBranchId() == null?"":carDetailModel.getBranchId().getName();
			String sModel = carDetailModel.getModel() == null?"":carDetailModel.getModel().getName();
			String sFuel = carDetailModel.getFuelId() == null?"":carDetailModel.getFuelId().getName();
			String sBodyColor = carDetailModel.getBodyColor() ==null?"":carDetailModel.getBodyColor().getName();
			String sCarStatus = carDetailModel.getStatus().equals("Y")?"Active":"InActive";
			String sPurchDate = carDetailModel.getPurchDate()==null?"":dateFormat.format(carDetailModel.getPurchDate());
			String sHypothicationDate = carDetailModel.getHypothicationDate() == null?"":dateFormat.format(carDetailModel.getHypothicationDate());
			String sLastDateOfEmi = carDetailModel.getLastDateOfEmi() == null ?"":dateFormat.format(carDetailModel.getLastDateOfEmi());
			if(carDetailModel.getOwnType().equals("C")||carDetailModel.getOwnType().equals("V")){
			sInsuEndDate = carDetailModel.getInsuranceDetailModel() == null || carDetailModel.getInsuranceDetailModel().size() == 0?"":dateFormat.format(carDetailModel.getInsuranceDetailModel().get(0).getInsuEndDate());
		    sRtoEndDate = carDetailModel.getTaxDetailModel() == null || carDetailModel.getTaxDetailModel().size() == 0?"":dateFormat.format(carDetailModel.getTaxDetailModel().get(0).getRtoEndDate());
			}
			
			String sEMI = carDetailModel.getEmiRs() == null?"":carDetailModel.getEmiRs().toString();
			String sYear =  carDetailModel.getManufacturerYear() == null?"":carDetailModel.getManufacturerYear().toString();
			dataString+=" <tr> "+
			" <td> "+
			" 	<a href='javascript:formFillForEdit("+carDetailModel.getId()+")'>"+Message.EDIT_BUTTON+"</a>"+
			" 	<a href='javascript:viewRecord("+carDetailModel.getId()+")' >"+Message.VIEW_BUTTON+"</a> "+ 				
			" 	<a href='javascript:deleteRecord("+carDetailModel.getId()+")' >"+Message.DELETE_BUTTON+"</a> "+ 				
			" </td>"+
			" <td>"+ carDetailModel.getCarId()+ "</td>   "+
			" <td>"+ carDetailModel.getOwnName()+ "</td>   "+
			" <td>"+ sBranch + "</td>   "+
			" <td>"+ carDetailModel.getRegistrationNo()+ "</td>   "+
			" <td>"+ sModel + "</td>   "+
			" <td>"+ sYear + "</td>   "+
			" <td>"+ sFuel + "</td>   "+
			" <td>"+ sBodyColor + "</td>   "+
			" <td>"+ sPurchDate + "</td>   "+
			" <td>"+ sEMI + "</td>   "+
			" <td>"+ sHypothicationDate + "</td>   "+
			" <td>"+ sLastDateOfEmi+ "</td>   "+
			" <td>"+ sInsuEndDate + "</td>   "+
			" <td>"+ sRtoEndDate + "</td>   "+
			" <td>"+ sRtoEndDate + "</td>   "+
			" <td>"+ sCarStatus + "</td>   "+
			" </tr>";
		}	
		dataString=dataString+"</tbody></table>";
		return dataString;
	}

/*	@RequestMapping(value="/getDataGridDataCarDetail",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getDataGridDataCarDetail(
			@ModelAttribute(value="carDetailModel") CarDetailModel carDetailModel,
			BindingResult bindingResult ,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("carDetailCode") String carDetailCode,
			@RequestParam("searchCriteria") String searchCriteria){
		JsonResponse res = new JsonResponse();
		try{
			res.setDataGrid(showGrid(carDetailService.listMasterWise(carDetailCode, searchCriteria)));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	@RequestMapping(value="/getGeneralMasterData_WithMappingCarDetail",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getGeneralMasterData_WithMappingCarDetail(
			@ModelAttribute(value="carDetailModel") CarDetailModel carDetailModel,
			BindingResult bindingResult ,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("fieldName") String fieldName,
			@RequestParam("parentId") String parentId,
			@RequestParam("fieldDefaultSelectedIndex") int fieldDefaultSelectedIndex){
		JsonResponse res = new JsonResponse();
		List<GeneralMasterModel> generalMasterModels=null;
		try{
			generalMasterModels= masterDataService.getGeneralMasterData_WithMapping(parentId);
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			if(fieldName.equalsIgnoreCase("makeId"))
				res.setDataGrid(modelListBox(generalMasterModels,fieldDefaultSelectedIndex).toString());
			else if(fieldName.equalsIgnoreCase("modelId"))
				res.setDataGrid(varientListBox(generalMasterModels,fieldDefaultSelectedIndex).toString());
		}
		return res;
	}
	
	public StringBuilder modelListBox(List<GeneralMasterModel> generalMasterModelList,int fieldDefaultSelectedIndex){
		String selectedIndex="";
		StringBuilder dataString =	new StringBuilder();
		dataString.append("<SELECT NAME='modelId' id='modelId' onchange='getGeneralMasterData_WithMapping(this.value,this.id,0)' style='width:99%'>"
				+ "<OPTION VALUE='0' SELECTED > --- Select --- ");
		for(GeneralMasterModel generalMasterModel:generalMasterModelList ){
			if(fieldDefaultSelectedIndex==generalMasterModel.getId())
				selectedIndex="selected";
			else
				selectedIndex="";
			dataString.append("<OPTION VALUE='"+generalMasterModel.getId()+"' "+selectedIndex+"> "+ generalMasterModel.getName());
		}	
		dataString.append("</SELECT>") ;
		return dataString;
	}

	public StringBuilder varientListBox(List<GeneralMasterModel> generalMasterModelList,int fieldDefaultSelectedIndex){
		StringBuilder dataString =	new StringBuilder();
		String selectedIndex="";
		dataString.append("<SELECT NAME='varientId' id='varientId'  style='width:99%'>"
				+ "<OPTION VALUE='0' SELECTED > --- Select --- ");
		for(GeneralMasterModel generalMasterModel:generalMasterModelList ){
			if(fieldDefaultSelectedIndex==generalMasterModel.getId())
				selectedIndex="selected";
			else
				selectedIndex="";
			dataString.append("<OPTION VALUE='"+generalMasterModel.getId()+"' "+selectedIndex+"> "+ generalMasterModel.getName());
		}	
		dataString.append("</SELECT>") ;
		return dataString;
	}

	public String getStatePermitDataString(CarDetailModel carDetailModel){
		String statePermitString="";
		if(carDetailModel.getTaxDetailModel()!=null)
		for(StatePermitDetailModel statePermitDetailModelSet:carDetailModel.getTaxDetailModel().getStatePermitDetailModel()){
			statePermitString=statePermitString+statePermitDetailModelSet.getPermitName()+"!"+
												statePermitDetailModelSet.getStartDate()+"!"+
												statePermitDetailModelSet.getEndDate()+"!$";
		}
		return statePermitString;
	}
	
	public String getServiceDetailDataString(CarDetailModel carDetailModel){
		String serviceDetailString="";
		if(carDetailModel.getMaintenanceDetailModel()!=null)
		for(ServiceDetailModel serviceDetailModelSet:carDetailModel.getMaintenanceDetailModel().getServiceDetailModel()){
			serviceDetailString=serviceDetailString+serviceDetailModelSet.getServiceDate()+"!"+
													serviceDetailModelSet.getGaradge()+"!"+
													serviceDetailModelSet.getDescription()+"!$";
		}
		return serviceDetailString;
	}
*/
	@RequestMapping(value="/fillCarRelatedDetails",method=RequestMethod.POST)
	public @ResponseBody JsonResponse fillCarRelatedDetails(
			@RequestParam("selectedRegNo") String selectedRegNo){
		JsonResponse res = new JsonResponse();
		try{
			CarDetailModel carDetailModel = carDetailService.getCarDetailsBasedOnRegNo(selectedRegNo);
			res.setCarDetailModel(carDetailModel);
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
	
	@RequestMapping("/pageLoadAdhocCarDetail")
	public ModelAndView pageLoadAdhocCarDetail(Map<String, Object> map, HttpSession session) {
		ModelAndView modelAndView=new ModelAndView("adhocCarDetail");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try {
			map.put("fuelIdList",masterDataService.getGeneralMasterData_UsingCode("FUEL")); 			// Select Vehicle Make
			map.put("makeIdList",masterDataService.getGeneralMasterData_UsingCode("VMK")); 			// Select Vehicle Make
			map.put("modalIdList",masterDataService.getGeneralMasterData_UsingCode("VMD")); 			// Select Vehicle Modal
			map.put("bodyStyleIdList",masterDataService.getGeneralMasterData_UsingCode("VBS")); 	// Select Vehicle Body Style
			map.put("bodyColorIdList",masterDataService.getGeneralMasterData_UsingCode("VBC")); 	// Select Vehicle BodyS Color
			map.put("companyIdList",masterDataService.getGeneralMasterData_UsingCode("VCM")); 		// Select Vehicle Company
			map.put("branchIdList",masterDataService.getGeneralMasterData_UsingCode("VBM-A:"+branchesAssigned)); 		// Select Vehicle Branch
			map.put("carDetailDataList",showGrid(carDetailService.list()));
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("carDetailModel", new CarDetailModel());
		}
		return modelAndView;
	}

	@RequestMapping("/pageLoadCarStatus")
	public ModelAndView pageLoadCarStatus(Map<String, Object> map) {
		ModelAndView modelAndView=new ModelAndView("carStatus");
		try {
		/*map.put("carDetailDataList",showGrid(carDetailService.list()));*/
		}
		catch(Exception e) {
			logger.error("",e);
		}
		
		return modelAndView;
	}

	@RequestMapping(value = "/searchCarStatus", method = RequestMethod.POST)
	public @ResponseBody JsonResponse searchCarStatus(@RequestParam("bookingFromDate") String bookingFromDate, 
			@RequestParam("bookingToDate") String bookingToDate,
			@RequestParam("flag") String flag,
			HttpSession session,
			HttpServletRequest request) {
			JsonResponse res = new JsonResponse();
			try {
			List<DutySlipModel> dutySlipModelList = carDetailService.searchDutySlip(bookingFromDate, bookingToDate,flag);
			List<CarDetailModel> carDetailModelList=carDetailService.searchCarDetailsNotInDutySlip(bookingFromDate, bookingToDate);
			res.setDataGrid(showGridDetails(dutySlipModelList,carDetailModelList,flag));
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	
	public String showGridDetails(List<DutySlipModel> dutySlipModelList,List<CarDetailModel> carDetailModelList,String flag){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String chauffeurName ="";
		String status ="";
		String action ="";
		String dataString =	
		" <table class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"+
		"	<thead style='background-color: #FED86F;'>		"+
		" 	<tr> 											"+
		"   	<th>Car Details</th> 		"+
	  	"   	<th>Chauffer Name</th> 			"+
	  	"   	<th>Status</th> 	"+
	  	"   	<th>Particulars</th> 			"+
	  	" 	</tr>							"+
	  	"	</thead>						"+
	  	"   </tbody>						"; 
		if(!flag.equals("F")){
			for(DutySlipModel dutySlipModel:dutySlipModelList ){
				if(dutySlipModel.getIsUpdatedChauffeur()!=null&&dutySlipModel.getIsUpdatedChauffeur().equals("Y")){
					chauffeurName=dutySlipModel.getChauffeurName();
				}else{
					chauffeurName=dutySlipModel.getChauffeurModel().getName();
				}if(dutySlipModel.getDutySlipStatus().equals("A")){
					status="Allocated";
					action=dateFormat.format(dutySlipModel.getBookingDetailModel().getPickUpDate());
				}else if(dutySlipModel.getDutySlipStatus().equals("D")||
						 dutySlipModel.getDutySlipStatus().equals("P")||
						 dutySlipModel.getDutySlipStatus().equals("S")){
					status="Dispatch";
					action=dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookingNo();
				}else{
					status="Others";
				}
				dataString+=" <tr> "+
				" <td>"+dutySlipModel.getCarDetailModel().getRegistrationNo()+" ("+ dutySlipModel.getBookingDetailModel().getCarModel().getName()+")</td>   "+
				" <td>"+chauffeurName + "</td>   "+
				" <td>"+status+ "</td>   "+
				" <td>"+action+ "</td>   "+
				" </tr>";
			}	
	      }
		if(!flag.equals("A") && !flag.equals("D")){
			for(CarDetailModel carDetailModel:carDetailModelList ){
				dataString+=" <tr> "+
				" <td>"+carDetailModel.getRegistrationNo()+" ("+carDetailModel.getModel().getName()+ ")</td>   "+
				" <td></td>   "+
				" <td>Available</td>   "+
				" <td></td>   "+
				" </tr>";
			}
		}
		dataString=dataString+"</tbody></table>";
		return dataString;
	}
}
