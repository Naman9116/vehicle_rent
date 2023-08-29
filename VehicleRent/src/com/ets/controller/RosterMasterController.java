package com.ets.controller;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.corporate.model.AutorizedUserModel;
import com.corporate.service.AuthorizedUserService;
import com.ets.model.LocationMasterModel;
import com.ets.model.RosterMasterModel;
import com.ets.service.RosterMasterService;
import com.master.controller.GeneralMasterController;
import com.master.model.GeneralMasterModel;
import com.operation.model.DutySlipModel;
import com.util.JsonResponse;
import com.util.Message;


@Controller
public class RosterMasterController {
  private static final Logger logger = Logger.getLogger(GeneralMasterController.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
   @Autowired
   private MasterDataService masterDataService;
   
   @Autowired
   private RosterMasterService rosterMasterService;
   
   @Autowired
   private AuthorizedUserService authorizedUserService; 

 
  @InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields(new String[] { "id" });
		dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
  
	@RequestMapping("/pageLoadRosterMaster")
	public ModelAndView pageLoadCorporate(Map<String, Object> map, HttpSession session) {
		ModelAndView modelAndView=new ModelAndView("rosterMaster");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try {
				map.put("customerOrCompanyNameIdList", masterDataService.getGeneralMasterData_UsingCode("CORPETS:"+branchesAssigned));
				map.put("shiftTimeList", masterDataService.getGeneralMasterData_UsingCode("STM"));
				map.put("carModelIdList", masterDataService.getGeneralMasterData_UsingCode("VMD"));
				map.put("rosterDataList",showGrid());
			}
		catch(Exception e) {
			logger.error("",e);
		}
		
		return modelAndView;
	}
	
	@RequestMapping("/pageLoadRosterCreation")
	public ModelAndView pageLoadRosterCreation(Map<String, Object> map, HttpSession session) {
		ModelAndView modelAndView=new ModelAndView("rosterCreation");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try {
				map.put("customerOrCompanyNameIdList", masterDataService.getGeneralMasterData_UsingCode("CORPETS:"+branchesAssigned));
				map.put("shiftTimeList", masterDataService.getGeneralMasterData_UsingCode("STM"));
				//map.put("carModelIdList", masterDataService.getGeneralMasterData_UsingCode("VMD"));
				//map.put("rosterDataList",showGrid());
			}
		catch(Exception e) {
			logger.error("",e);
		}
		
		return modelAndView;
	}
	
	//2018/09/07
	@RequestMapping(value="/getUsersListByRoster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getClientsListByRoster(
		   @RequestParam("corporateId") Long corporateId, @RequestParam("branchId") Long branch,@RequestParam("outletId") Long outlet, 
		   @RequestParam("bookedById") Long bookedBy, @RequestParam("shiftTimeId") Long shiftTiming, @RequestParam("rosterTaken") String rosterTaken, 
		   @RequestParam("routeNumber") String routeNumber, @RequestParam("rosterFromDate") String rosterFromDate, @RequestParam("rosterToDate") String rosterToDate){
		JsonResponse res = new JsonResponse();
		try{
			List<AutorizedUserModel> autorizedUserModels= rosterMasterService.listAutorizedUsersByRoster(corporateId,branch,outlet,bookedBy,shiftTiming,rosterTaken,routeNumber,rosterFromDate,rosterToDate);
			res.setStatus("Success");
			res.setAutorizedUserModels(autorizedUserModels);
			
			String dataString="<select class='form-control' id='cars' style='width:100%'><option value='0'>ALL</option>";
								
			List<HashMap<String,String>>carsList=rosterMasterService.listCarsByRoster(corporateId,branch,outlet,bookedBy,shiftTiming,rosterTaken,routeNumber,rosterFromDate,rosterToDate);
			for(HashMap<String,String>map : carsList){
				 dataString= dataString+"<option value ="+String.valueOf(map.get("id"))+">"+map.get("name")+"</option>";
			}
			dataString=dataString+"</select>";
			res.setDataString1(dataString);
		}
		catch(Exception e) {
			logger.error("",e);
			e.printStackTrace();
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		
		return res;
	}
	
	//--2018/09/07
	/* ___________________________________________________________________________________________			
	 *     For Getting Client Details As Per Mobile No.
	 * ___________________________________________________________________________________________
	 */
	
	@RequestMapping(value="/fillClientAsPerMobile",method=RequestMethod.POST)
	public @ResponseBody JsonResponse fillClientAsPerMobile(
		   @RequestParam("mob") String mob, @RequestParam("corporateId") Long corporateId){
		JsonResponse res = new JsonResponse();
		try{
			AutorizedUserModel autorizedUserModel = rosterMasterService.fillClientAsPerMobile(mob,corporateId);
			res.setStatus("Success");
			res.setAutorizedUserModel(autorizedUserModel);
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		
		return res;
	}
	
	/* ___________________________________________________________________________________________			
	 *     For Getting Car Model List
	 * ___________________________________________________________________________________________
	 */
	
	@RequestMapping(value="/fillCarType",method=RequestMethod.POST)
	public @ResponseBody JsonResponse fillCarType(){
		JsonResponse res = new JsonResponse();
		try{
			List<GeneralMasterModel> generalMasterModelList = masterDataService.getGeneralMasterData_UsingCode("VMD");
			res.setStatus("Success");
			res.setGeneralMasterModelList(generalMasterModelList);
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		
		return res;
	}
	

	/* ___________________________________________________________________________________________			
	 *     Save OR Update Roster  Functionality Start
	 * ___________________________________________________________________________________________
	 */
	
	@RequestMapping(value="/saveUpdateRoster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveUpdateRoster(
			@ModelAttribute(value="rosterMasterModel") RosterMasterModel rosterMasterModel,
			BindingResult bindingResult,
			@RequestParam("authoriseClientId") Long[] authoriseClientId, 
			@RequestParam("pickUpTime") String[] pickUpTime, 
			@RequestParam("isPickup") String[] isPickup,
			@RequestParam("isDrop") String[] isDrop,
			@RequestParam("routeNo") String[] routeNo,
			@RequestParam("carType") Long[] carType,
			@RequestParam("idForUpdate") String idForUpdate,
			HttpSession session){
			JsonResponse res = new JsonResponse();
			try {
					if(idForUpdate.trim().equals("0") || idForUpdate.trim().equals("")){
						for(int i= 0; i< authoriseClientId.length;  i++){
							rosterMasterModel.setAuthoriseClientId(authoriseClientId[i]);
							rosterMasterModel.setCarType(carType[i]);
							rosterMasterModel.setIsPickup(isPickup[i]);
							rosterMasterModel.setIsDrop(isDrop[i]);
							rosterMasterModel.setPickUpTime(pickUpTime[i]);
							rosterMasterModel.setRouteNo(routeNo[i]);
							rosterMasterModel.setInsDate(new Date());
							rosterMasterService.save(rosterMasterModel);
							res.setResult(Message.SAVE_SUCCESS_MESSAGE);
							res.setStatus("Success");
						}						
					}else{
						for(int i= 0; i< authoriseClientId.length;  i++){
							rosterMasterModel.setId(Long.valueOf(idForUpdate));
							rosterMasterModel.setAuthoriseClientId(authoriseClientId[i]);
							rosterMasterModel.setCarType(carType[i]);
							rosterMasterModel.setIsPickup(isPickup[i]);
							rosterMasterModel.setIsDrop(isDrop[i]);
							rosterMasterModel.setPickUpTime(pickUpTime[i]);
							rosterMasterModel.setRouteNo(routeNo[i]);
							rosterMasterModel.setInsDate(new Date());
							rosterMasterService.update(rosterMasterModel);
							res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
							res.setStatus("Success");
					}
				}
			}catch(ConstraintViolationException e){
				logger.error("",e);
				e.printStackTrace();
				res.setStatus("Failure");
				res.setResult(Message.DUPLICATE_ERROR_MESSAGE);
			}
			catch(Exception e) {
				e.printStackTrace();
				logger.error("",e);
				res.setStatus("Failure");
				res.setResult(Message.FATAL_ERROR_MESSAGE);
			}
			finally{
				res.setDataGrid(showGrid());
			}
			return res;
			
}
	
	public String showGrid(){
		List<RosterMasterModel> rosterMasterModelList = rosterMasterService.listRoster() ;
		String dataString =	"";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dataString =	
			" <table class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"+
			"	<thead style='background-color: #FED86F;'>		"+
		  	" 	<tr> 											"+
		  	" 		<th width="+Message.ACTION_SIZE+">Action</th> 		  "+
		  	" 		<th>ID</th> 			"+
		  	" 		<th>Corporate Name</th> "+
		  	" 		<th>Branch Name</th> 	"+
		  	" 		<th>Hub</th> 		"+
		  	" 		<th>Shift Time</th> 	"+
		  	" 		<th>Roster Date</th> 		"+
		  	" 		<th>Booked By</th> 		"+
		 	" 		<th>Roster Taken</th> 		"+
		  	" 	</tr>						"+
		  	"	</thead>					"+
		  	"   <tbody>						"; 
			for(RosterMasterModel rosterMasterModel:rosterMasterModelList ){
				AutorizedUserModel autorizedUserModel = authorizedUserService.formFillForEdit(rosterMasterModel.getAuthoriseClientId());
				autorizedUserModel.setContactDetailModel(autorizedUserModel.getEntityContact());
				@SuppressWarnings("unused")
				GeneralMasterModel generalMasterModel = rosterMasterService.getCarModelName(rosterMasterModel.getCarType());
					dataString=dataString+" <tr> "+
				" <td>"+
				" 	<a href='javascript:formFillForEditRosterMaster("+rosterMasterModel.getId()+")'>"+Message.EDIT_BUTTON+"</a> "+
				" 	<a href='javascript:viewRosterMaster("+rosterMasterModel.getId()+")' >"+Message.VIEW_BUTTON+"</a> "+ 				
				" 	<a href='javascript:deleteRosterMaster("+rosterMasterModel.getId()+")' >"+Message.DELETE_BUTTON+"</a> "+ 				
				" </td> 														"+
				" 	<td>"+ rosterMasterModel.getId()+ "</td>  						"+
				" 	<td>"+ rosterMasterModel.getCorporateId().getName()+ "</td>   					"+
				" 	<td>"+ rosterMasterModel.getBranch().getName()+ "</td>   			"+
				" 	<td>"+ rosterMasterModel.getOutlet().getName()+ "</td>   				"+
				" 	<td>"+ rosterMasterModel.getShiftTime().getName()+ "</td>   	"+
				" 	<td>"+ dateFormat.format(rosterMasterModel.getRosterDate())+ "</td>   	"+
				" 	<td>"+ autorizedUserModel.getName()+ "</td>   		"+
				" 	<td>"+ rosterMasterModel.getRosterTakenBy().getUserName()+ "</td>   		"+
				" </tr> 		";
			}
			dataString=dataString+"</tbody></table>";
		return dataString;
	}
	
	@RequestMapping(value="/formFillForEditRosterMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditRosterMaster(
			@RequestParam("formFillForEditId") String formFillForEditId){
		JsonResponse res = new JsonResponse();
		try{
			RosterMasterModel rosterMasterModel = rosterMasterService.formFillForEditRosterMaster(Long.parseLong(formFillForEditId));
			AutorizedUserModel autorizedUserModel = authorizedUserService.formFillForEdit(rosterMasterModel.getAuthoriseClientId());
			autorizedUserModel.setContactDetailModel(autorizedUserModel.getEntityContact());
			GeneralMasterModel generalMasterModel = rosterMasterService.getCarModelName(rosterMasterModel.getCarType());
			res.setStatus("Success");
			res.setRosterMasterModel(rosterMasterModel);
			res.setAutorizedUserModel(autorizedUserModel);
			res.setGeneralMasterModel(generalMasterModel);
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		
		return res;
	}
	
	@RequestMapping(value="/deleteRosterMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteRosterMaster(
			@RequestParam("idForDelete") String idForDelete){
		JsonResponse res = new JsonResponse();
		try{
			rosterMasterService.delete(Long.valueOf(idForDelete));
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
			res.setDataGrid(showGrid());
		}
		return res;
	}
	
	
	@SuppressWarnings("null")
	@RequestMapping(value="/fillRosterDetails",method=RequestMethod.POST)
	public @ResponseBody JsonResponse fillRosterDetails(
			@ModelAttribute(value="rosterMasterModel") RosterMasterModel rosterMasterModel){
		JsonResponse res = new JsonResponse();
		try{
			List<RosterMasterModel> rosterMasterModelList = rosterMasterService.fillRosterDetails(rosterMasterModel.getCorporateId().getId(),
					rosterMasterModel.getBranch().getId(),rosterMasterModel.getOutlet().getId(),rosterMasterModel.getBookedBy().getId(),
					rosterMasterModel.getShiftTime().getId(),dateFormat.format(rosterMasterModel.getRosterDate()),rosterMasterModel.getRosterTakenBy().getId());
			res.setStatus("Success");
			res.setRosterMasterModelList(rosterMasterModelList);
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		
		return res;
	}
	
	
	/*
	
	@RequestMapping(value="/formFillForEditLocationMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditLocationMaster(
			@RequestParam("formFillForEditId") String formFillForEditId){
		JsonResponse res = new JsonResponse();
		try{
			LocationMasterModel locationMasterModel = locationMasterService.formFillForEditLocationMaster(Long.parseLong(formFillForEditId));
			res.setStatus("Success");
			res.setLocationMasterModel(locationMasterModel);
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		
		return res;
	}
	
	@RequestMapping(value="/deleteLocationMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteLocationMaster(
			@RequestParam("idForDelete") String idForDelete){
		JsonResponse res = new JsonResponse();
		try{
			locationMasterService.delete(Long.valueOf(idForDelete));
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
			res.setDataGrid(showGrid());
		}
		return res;
	}*/
}
