package com.corporate.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.corporate.validator.AuthorisedUserValidator;
import com.master.model.GeneralMasterModel;
import com.master.service.GeneralMasterService;
import com.util.JsonResponse;
import com.util.Message;
import com.util.Utilities;

@Controller
public class AuthorizedUserController {
	private static final Logger logger = Logger.getLogger(AuthorizedUserController.class);

	@Autowired
	private AuthorizedUserService authorizedUserService;
	
	@Autowired
	private MasterDataService masterDataService;
	
	@Autowired
	private GeneralMasterService generalMasterService;
	
	private AuthorisedUserValidator validator = null;

	public AuthorisedUserValidator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(AuthorisedUserValidator validator) {
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
	
	@RequestMapping("/pageLoadAutorizedUser")
	public ModelAndView pageLoadAutorizedUser(Map<String, Object> map,HttpSession session,
			@RequestParam("pageFor") String pageFor) {
		ModelAndView modelAndView=new ModelAndView("autorizedUser");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try {
			map.put("pageFor",pageFor);
			map.put("corporateIdList",masterDataService.getGeneralMasterData_UsingCode("CORP:"+branchesAssigned));
			map.put("priorityIdList",masterDataService.getGeneralMasterData_UsingCode("PRIO"));
			map.put("authorizedUserDataList",showGrid(authorizedUserService.list(pageFor,0l),pageFor));
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("authorizedUserModel", new AutorizedUserModel());
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/saveOrUpdateAuthorisedUser",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateAuthorisedUser(
			@ModelAttribute(value="authorizedUserModel") AutorizedUserModel authorizedUserModel,
			BindingResult bindingResult,
			@RequestParam("idForUpdate") String idForUpdate,
			@RequestParam("pageFor") String pageFor){
		JsonResponse res = new JsonResponse();
		authorizedUserModel.setEntityContact(authorizedUserModel.getContactDetailModel());
		validator.validate(authorizedUserModel, bindingResult);
		if(bindingResult.hasErrors()){
			res.setStatus("Errors");
			res.setResult(bindingResult.getAllErrors());
			return res;
		}
		Long corporateId = authorizedUserModel.getCorporateId().getId().longValue();
		
		try{
			if(idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")){
				authorizedUserService.save(authorizedUserModel);
				res.setResult(Message.SAVE_SUCCESS_MESSAGE);
			}
			else{
				authorizedUserModel.setId(Long.valueOf(idForUpdate));
				authorizedUserService.update(authorizedUserModel);
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
			res.setDataGrid(showGrid(authorizedUserService.list(pageFor,corporateId),pageFor));
		}
		return res;
	}

	@RequestMapping(value="/formFillForEditAuthorisedUser",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditAuthorisedUser(
			@ModelAttribute(value="autorizedUserModel") AutorizedUserModel autorizedUserModel,
			BindingResult bindingResult,
			@RequestParam("formFillForEditId") String formFillForEditId){
		JsonResponse res = new JsonResponse();
		try{
			autorizedUserModel=authorizedUserService.formFillForEdit(Long.parseLong(formFillForEditId));
			autorizedUserModel.setContactDetailModel(autorizedUserModel.getEntityContact());
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setAutorizedUserModel(autorizedUserModel);
		}
		return res;
	}
	
	@RequestMapping(value="/deleteAuthorizedUser",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteAuthorizedUser(
					@RequestParam("idForDelete") String idForDelete,
					@RequestParam("pageFor") String pageFor){
		JsonResponse res = new JsonResponse();
		Long corporateId = 0l;
		try{
			String sStatus = authorizedUserService.delete(Long.valueOf(idForDelete));
			AutorizedUserModel autorizedUserModel=authorizedUserService.formFillForEdit(Long.parseLong(idForDelete));
			corporateId = autorizedUserModel.getCorporateId().getId().longValue();
			if(sStatus.equals("deleteFailed")){
				res.setStatus("Failure");
				res.setResult(Message.FOREIGNKEY_ERROR_MESSAGE);
			}else{
				res.setStatus("Success");
				res.setResult(Message.DELETE_SUCCESS_MESSAGE);
			}
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
			res.setDataGrid(showGrid(authorizedUserService.list(pageFor,corporateId),pageFor));
		}
		return res;
	}
	
	@RequestMapping(value="/getAuthoriser",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getBranchCorporate(
			@RequestParam("corporateId") String corporateId){
		JsonResponse res = new JsonResponse();
		try{
			res.setGeneralMasterModelList(masterDataService.getGeneralMasterData_UsingCode("LinkedAuthoriser:"+corporateId));
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
		return res;
	}

	public String showGrid(List<AutorizedUserModel> AutorizedUserDataList, String pageFor){
		String dataString =	"";
		dataString =	
			" <table class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"+
			"	<thead style='background-color: #FED86F;'>		"+
		  	" 	<tr> 											"+
		  	" 		<th width="+Message.ACTION_SIZE+">Action</th> 		  "+
		  	" 		<th>Corporate Name</th> 			"+
		  	" 		<th>Authorise Name</th> ";
			if(pageFor.equals("Client")){
		  		dataString += " 		<th>Client Name</th> 	" +
		  					  " 		<th>Mobile No.</th> 	" + 
		  					  "         <th>Priority User</th>  " ;
		  	}else{
		  		dataString += " 		<th>Designation</th> 	" +
		  					  " 		<th>Mobile No.</th> 	" + 
		  					  "         <th>E-Mail</th>  ";
		  	}
			dataString += " 		<th>Home Address</th> 	"+
						  " 		<th>Bkg Allow</th> 		"+
					  	  " 		<th>Status</th> 		"+
					  	  " 	</tr>						"+
					  	  "	</thead>						"+
					  	  " <tbody>							"; 
			if(AutorizedUserDataList != null){
				for(AutorizedUserModel autorizedUserModel:AutorizedUserDataList ){
					String sBkgAllow = autorizedUserModel.getBkgAllow().equals("Y")?"Yes":"No";
					String sStatus   = autorizedUserModel.getStatus().equals("Y")?"Active":"InActive";
					List<GeneralMasterModel> generalMasterModelList = masterDataService.getGeneralMasterData_UsingCode("LinkedAuthoriser:"+autorizedUserModel.getCorporateId().getId().toString());
					dataString=dataString+" <tr> "+
					" <td>"+
					" 	<a href='javascript:formFillForEdit("+autorizedUserModel.getId()+")'>"+Message.EDIT_BUTTON+"</a> "+
					" 	<a href='javascript:viewRecord("+autorizedUserModel.getId()+")' >"+Message.VIEW_BUTTON+"</a> 	 "+ 				
					" 	<a href='javascript:deleteRecord("+autorizedUserModel.getId()+")' >"+Message.DELETE_BUTTON+"</a> "+ 				
					" </td> 														"+
					" <td>"+ autorizedUserModel.getCorporateId().getName()+ "</td>  						";
					if(pageFor.equals("Client")){
						String sAuthoriserName = "";
						for(GeneralMasterModel generalMasterModel:generalMasterModelList ){
							if(generalMasterModel.getId() == autorizedUserModel.getParentId()){
								sAuthoriserName = generalMasterModel.getName();
							}
						}
						dataString +=" 	<td>"+ sAuthoriserName + "</td>   							"+
								 " 	<td>"+ autorizedUserModel.getName()+ "</td>"+
								 " 	<td>"+ autorizedUserModel.getEntityContact().getPersonalMobile()+ "</td>"+
								 " 	<td>"+ autorizedUserModel.getPriorityId().getName()+"</td>";
					}else{
						dataString +=" 	<td>"+ autorizedUserModel.getName()+ "</td>   							"+
									 " 	<td>"+ autorizedUserModel.getEntityContact().getContactPerson2()+ "</td>"+
									 " 	<td>"+ autorizedUserModel.getEntityContact().getPersonalMobile()+ "</td>"+
									 " 	<td>"+ autorizedUserModel.getEntityContact().getPersonalEmailId()+"</td>";
					}
					dataString += 	" 	<td>"+ autorizedUserModel.gethAddress().replace("\n", "").replace("\r", "")+ "</td> " +
									" 	<td>"+ sBkgAllow + "</td> 						" +
									" 	<td>"+ sStatus + "</td>   						" + 
									" </tr> 											" ;
				}	
			}
			dataString=dataString+"</tbody></table>";
		return dataString;
	}
	
	@RequestMapping(value="/getLocationASPerCorporate",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getLocationASPerCorporate(
			@RequestParam("corporateId") Long corporateId,
			@RequestParam("zoneId") Long zoneId){
		JsonResponse res = new JsonResponse();
		try{
			res.setLocationMasterModelList(authorizedUserService.getLocationAsPerCorporate(corporateId,zoneId));
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
		return res;
	}
	
	@RequestMapping(value="/getZoneASPerCorporate",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getZoneASPerCorporate(
			@RequestParam("corporateId") Long corporateId){
		JsonResponse res = new JsonResponse();
		try{
			res.setGeneralMasterModelList(authorizedUserService.getZoneAsPerCorporate(corporateId));
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
		return res;
	}

	@RequestMapping(value = "/getUserAsCorporate", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getLocationAsCorporate(
			@RequestParam("corporateId") Long corporateId,
			@RequestParam("pageFor") String pageFor) {
		JsonResponse res = new JsonResponse();
		try {
			res.setDataGrid(showGrid(authorizedUserService.list(pageFor,corporateId),pageFor));
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
}
