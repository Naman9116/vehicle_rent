package com.master.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
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
import com.master.model.GeneralMasterModel;
import com.master.model.MappingMasterModel;
import com.master.model.MasterModel;
import com.master.service.MappingMasterService;
import com.master.validator.MappingMasterValidator;

@Controller
public class MappingMasterController {
	private static final Logger logger = Logger.getLogger(MappingMasterController.class);

	@Autowired
	private MappingMasterService mappingMasterService;
	
	@Autowired
	private MasterDataService masterDataService;
	
	private MappingMasterValidator validator = null;

	public MappingMasterValidator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(MappingMasterValidator validator) {
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
	@RequestMapping("/pageLoadMappingMaster")
	public ModelAndView pageLoadMappingMaster(Map<String, Object> map) {
		ModelAndView modelAndView=new ModelAndView("mappingMaster");
		try {
			map.put("masterIdList",masterDataService.getMasterData("All"));
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("mappingMasterModel", new MappingMasterModel());
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/saveOrUpdateMappingMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateMappingMaster(
			@ModelAttribute(value="mappingMasterModel") MappingMasterModel mappingMasterModel,
			BindingResult bindingResult ,
			@RequestParam("selectedNotMappedCheckBoxValues") String selectedNotMappedCheckBoxValues){
		JsonResponse res = new JsonResponse();
		try{
			mappingMasterService.save(mappingMasterModel,selectedNotMappedCheckBoxValues);
			res.setResult("Entry Successfully Mapped");
			res.setStatus("Success");
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
		return res;
	}
	
	@RequestMapping(value="/removeOrUpdateMappingMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse removeOrUpdateMappingMaster(
			@ModelAttribute(value="mappingMasterModel") MappingMasterModel mappingMasterModel,
			BindingResult bindingResult,
			@RequestParam("selectedAlreadyMappedCheckBoxValues") String selectedAlreadyMappedCheckBoxValues){
		JsonResponse res = new JsonResponse();
		try{
			mappingMasterService.remove(mappingMasterModel,selectedAlreadyMappedCheckBoxValues);
			res.setResult("Entry Successfully Unmapped");
			res.setStatus("Success");
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
		return res;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/fetchMasterValuesAndSubMasterNamesMappingMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse fetchMasterValuesAndSubMasterNamesMappingMaster(
			@ModelAttribute(value="mappingMasterModel") MappingMasterModel mappingMasterModel,
			BindingResult bindingResult,
			@RequestParam("selectedId") String selectedId){
		JsonResponse res = new JsonResponse();
		Object models[]=null;
		try{
			models=mappingMasterService.fetchMasterValuesAndSubMasterNamesMappingMaster(Long.valueOf(selectedId));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			if(models!=null)
			res.setDataGrid(masterValuesListBox((List<GeneralMasterModel>) models[0])+"$$"+subMasterNamesListBox((List<MasterModel>) models[1]));
		}
		return res;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/fetchSubMasterValuesMappingMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse fetchSubMasterValuesMappingMaster(
			@ModelAttribute(value="mappingMasterModel") MappingMasterModel mappingMasterModel,
			BindingResult bindingResult,
			@RequestParam("selectedId") String selectedId,
			@RequestParam("parentId") String parentId){
		JsonResponse res = new JsonResponse();
		Object models[]=null;
		try{
			models=mappingMasterService.fetchSubMasterValuesMappingMaster(Long.valueOf(selectedId),Long.valueOf(parentId));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			if(models!=null)
			res.setDataString1(notMappedSubMasterValuesListBox((List<GeneralMasterModel>) models[0]).toString());
			res.setDataString2(alreadyMappedSubMasterValuesListBox((List<GeneralMasterModel>) models[1]).toString());
		}
		return res;
	}

	public StringBuilder masterValuesListBox(List<GeneralMasterModel> generalMasterModelList){
		StringBuilder dataString =	new StringBuilder();
		dataString.append("<SELECT NAME='masterValue' id='masterValue' class='form-control' onchange='resetAccordingMasterValues()' style='width:99%'>"
				+ "<OPTION VALUE='' SELECTED > --- Select --- ");
		for(GeneralMasterModel generalMasterModel:generalMasterModelList ){
			dataString.append("<OPTION VALUE='"+generalMasterModel.getId()+"'> "+ generalMasterModel.getName());
		}	
		dataString.append("</SELECT>") ;
		return dataString;
	}

	public StringBuilder subMasterNamesListBox(List<MasterModel> masterModelList){
		StringBuilder dataString =	new StringBuilder();
		dataString.append("<SELECT NAME='subMasterId' id='subMasterId' class='form-control' onchange='fetchSubMasterValuesMappingMaster(this.value)' style='width:99%'> "
				+ "<OPTION VALUE='' SELECTED style='width:99%'> --- Select --- ");
		for(MasterModel masterModel:masterModelList ){
			dataString.append("<OPTION VALUE='"+masterModel.getId()+"'> "+ masterModel.getName());
		}	
		dataString.append("</SELECT>") ;
		return dataString;
	}
	
	public StringBuilder notMappedSubMasterValuesListBox(List<GeneralMasterModel> generalMasterModelList){
		StringBuilder dataString =	new StringBuilder();
		dataString.append("<table width='100%' >");
			for(GeneralMasterModel generalMasterModel:generalMasterModelList ){
				dataString.append("<tr>");
				dataString.append("<td align='left'><input type='checkbox' class='notMappedCheckBox' name='subMasterValues' value='"+ generalMasterModel.getId()+ "'/>"
						+ "<td width='90%' align='left'>"+generalMasterModel.getName()+"</td>"
						+ "</tr>	");
			}	
			dataString.append("</table>");
		return dataString;
	}

	public StringBuilder alreadyMappedSubMasterValuesListBox(List<GeneralMasterModel> generalMasterModelList){
		StringBuilder dataString =	new StringBuilder();
		dataString.append("<table width='100%' >");
			for(GeneralMasterModel generalMasterModel:generalMasterModelList ){
				dataString.append("<tr> ");
				dataString.append("<td align='left'><input type='checkbox' class='alreadyMappedCheckBox' name='subMasterValues' value='"+ generalMasterModel.getId()+ "'/>");
				dataString.append("<td width='90%' align='left'>"+generalMasterModel.getName()+"</td>");
				dataString.append("</tr>");
			}	
			dataString.append("</table>");
		return dataString;
	}

}
