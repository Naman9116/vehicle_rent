package com.master.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.common.service.MasterDataService;
import com.master.model.GeneralMasterModel;
import com.master.model.MasterModel;
import com.master.model.TariffSchemeParaModel;
import com.master.service.GeneralMasterService;
import com.master.validator.GeneralMasterValidator;
import com.util.JsonResponse;
import com.util.Message;

@Controller
public class GeneralMasterController {
	private static final Logger logger = Logger.getLogger(GeneralMasterController.class);
	private String masterCode = "";
	private long masterId = 0L;
	
	@Autowired
	private GeneralMasterService generalMasterService;
	
	@Autowired
	private MasterDataService masterDataService;
	
	private GeneralMasterValidator validator = null;

	public GeneralMasterValidator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(GeneralMasterValidator validator) {
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
	
	@RequestMapping("/pageLoadGeneralMaster/{masterCode}")
	public ModelAndView pageLoadGeneralMaster(Map<String, Object> map,HttpSession session,@PathVariable("masterCode") String masterCode) {
		ModelAndView modelAndView=new ModelAndView("generalMaster");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		this.masterCode = masterCode;

		try {
			if(masterCode.equalsIgnoreCase("") || masterCode.isEmpty())
				masterCode = "All";
			List<MasterModel> getMasterData = masterDataService.getMasterData(masterCode);
			map.put("masterDataList",getMasterData);
			map.put("branchList", masterDataService.getGeneralMasterData_UsingCode("VBM-A:"+branchesAssigned));
			map.put("zoneList", masterDataService.getGeneralMasterData_UsingCode("ZONE:"+branchesAssigned));
			if(masterCode.equalsIgnoreCase("ALL")){
				map.put("generalMasterDataList",showGrid());
				this.masterId = 0l;
			}else{
				map.put("generalMasterDataList",showGrid());
				this.masterId = getMasterData.get(0).getId();
			}
			map.put("masterCode", masterCode);
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("generalMasterModel", new GeneralMasterModel());
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/saveOrUpdateGeneralMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateGeneralMaster(
			@ModelAttribute(value="generalMasterModel") GeneralMasterModel generalMasterModel,
			BindingResult bindingResult,
			@RequestParam("idForUpdate") String idForUpdate){
		JsonResponse res = new JsonResponse();
		try{
			validator.validate(generalMasterModel, bindingResult);
			if(!bindingResult.hasErrors()){
				if(idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")){
					generalMasterService.save(generalMasterModel, this.masterCode);
					res.setResult(Message.SAVE_SUCCESS_MESSAGE);
				}
				else{
					generalMasterModel.setId(Long.valueOf(idForUpdate));
					if(generalMasterModel.getTariffSchemeParaModel() != null)
						generalMasterModel.getTariffSchemeParaModel().setParentId(Long.parseLong(idForUpdate));
					if(generalMasterModel.getExtraId() != null && !(this.masterCode.equals("ZONE") || this.masterCode.equals("LOC"))){
						generalMasterModel.getTariffSchemeParaModel().setId(generalMasterModel.getExtraId());
					}
					generalMasterService.update(generalMasterModel, this.masterCode);
					res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
				}
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
			res.setDataGrid(showGrid());
		}
		return res;
	}
	
	@RequestMapping(value="/formFillForEditGeneralMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditGeneralMaster(
			@RequestParam("formFillForEditId") String formFillForEditId,
			@RequestParam("masterId") String masterId){
		JsonResponse res = new JsonResponse();
		GeneralMasterModel generalMasterModel  = null;
		try{
			generalMasterModel=generalMasterService.formFillForEdit(Long.parseLong(formFillForEditId), this.masterCode);
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setGeneralMasterModel(generalMasterModel);
			res.setDataGrid(showGrid());
		}
		return res;
	}
	
	@RequestMapping(value="/deleteGeneralMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteGeneralMaster(
			@ModelAttribute(value="generalMasterModel") GeneralMasterModel generalMasterModel,
			BindingResult bindingResult ,
			@RequestParam("idForDelete") String idForDelete,
			@RequestParam("masterId") String masterId){
		JsonResponse res = new JsonResponse();
		try{
			generalMasterService.delete(Long.valueOf(idForDelete));
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
	
	@RequestMapping(value="/setSortingOrder",method=RequestMethod.POST)
	public @ResponseBody JsonResponse setSortingOrder(
			@ModelAttribute(value="generalMasterModel") GeneralMasterModel generalMasterModel,
			BindingResult bindingResult,
			@RequestParam("masterId") String masterId, @RequestParam("sortedArr") String sortedArr){
		JsonResponse res = new JsonResponse();
		try{
			generalMasterService.setSortOrder(Long.parseLong(masterId), sortedArr);
			res.setStatus("Success");
			res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
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

	public String showGrid(){
		String dataString =	
		" <table class='table table-bordered table-striped table-hover dataTable sorted_table' id='userTable'> 	"+
		"	<thead style='background-color: #FED86F;'>		"+
		" 	<tr> 											"+
	  	" 		<th width="+Message.ACTION_SIZE+">Action</th> 		  "+
	  	"   	<th>Name</th> 			";
	  	if(this.masterCode.equals("ZONE") || this.masterCode.equals("LOC")){
	  		dataString += "<th>Branch</th>";
	  	}
	  	if(this.masterCode.equals("LOC")){
	  		dataString += "<th>Zone</th>";
	  	}
	  	dataString += " 	</tr>						"+
	  	"	</thead>					"+
	  	"   <tbody>					"; 
		for(GeneralMasterModel generalMasterModel:generalMasterService.listMasterWise(this.masterId, this.masterCode) ){
			dataString=dataString+" <tr> "+
			" <td> "+
			" 	<a href='javascript:formFillForEditGeneralMaster("+generalMasterModel.getId()+")'>"+Message.EDIT_BUTTON.replace("./img","../img") +"</a>"+
			" 	<a href='javascript:viewGeneralMaster("+generalMasterModel.getId()+")' >"+Message.VIEW_BUTTON.replace("./img","../img") +"</a> "+ 				
			" 	<a href='javascript:deleteGeneralMaster("+generalMasterModel.getId()+")' >"+Message.DELETE_BUTTON.replace("./img","../img") +"</a> "+ 				
			" </td>"+
			" <td>"+ generalMasterModel.getName()+ "</td>   ";
		  	if(this.masterCode.equals("ZONE") || this.masterCode.equals("LOC")){
		  		dataString += "<td>"+generalMasterModel.getExtraName()+"</td>";
		  	}
		  	if(this.masterCode.equals("LOC")){
		  		dataString += "<td>"+generalMasterModel.getZoneName()+"</td>";
		  	}
		  	dataString += " </tr>";
		}	
		dataString=dataString+"</tbody></table>";
		return dataString;
	}

	@RequestMapping(value="/getDataGridDataGeneralMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getDataGridDataGeneralMaster(
			@ModelAttribute(value="generalMasterModel") GeneralMasterModel generalMasterModel,
			BindingResult bindingResult ,
			@RequestParam("masterId") String master){
		
		
		if(master!="") {
			masterId=Long.parseLong(master);
		}
		
		JsonResponse res = new JsonResponse();
		
		generalMasterModel.setMasterId(master.equalsIgnoreCase("")?0L:Long.parseLong(master));
		try{
			res.setDataGrid(showGrid());
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	
}
