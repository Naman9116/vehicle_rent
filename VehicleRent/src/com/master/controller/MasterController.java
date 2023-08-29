package com.master.controller;

import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.ModelAndView;

import com.common.service.MasterDataService;
import com.master.model.MasterModel;
import com.master.service.MasterService;
import com.master.validator.MasterValidator;
import com.util.OperationMessage;
import com.util.Utilities;

@Controller
public class MasterController {
	private static final Logger logger = Logger.getLogger(MasterController.class);

	@Autowired
	private MasterService masterService;

	@Autowired
	private MasterDataService masterDataService;

	private MasterValidator validator = null;
	
	public MasterValidator getValidator() {
		return validator;
	}
	
	@Autowired
	public void setValidator(MasterValidator validator) {
		this.validator = validator;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields(new String[] {"projectId"});
		dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
	}
	
	@RequestMapping("/pageLoadMaster/{pageNumber}")
	public ModelAndView pageLoadMaster(Map<String, Object> map,
			@PathVariable("pageNumber") int pageNumber) {
		ModelAndView modelAndView=new ModelAndView("cityMaster");
		String operationMessage="";
		try {
			map.put("masterModelDataList", masterService.list(pageNumber));
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("masterModel", new MasterModel());
			map.put("pageNumber",pageNumber);
			String[] arrayResult=OperationMessage.getOperationMessageType(operationMessage);
			map.put("operationMessage",arrayResult[0]);
			map.put("messageType",arrayResult[1]);
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/saveMaster/{pageNumber}", method=RequestMethod.POST)
	public ModelAndView saveMaster(Map<String, Object> map, 
			@ModelAttribute("masterModel") MasterModel  masterModel,
			BindingResult bindingResult,
			@PathVariable("pageNumber") int pageNumber){
		ModelAndView modelAndView=new ModelAndView("master");
		String operationMessage="";
		try {
			validator.validate(masterModel, bindingResult);
			if(bindingResult.hasErrors()){
				map.put("validationError",1);
				operationMessage="validationError";
			}
			else{
				operationMessage = masterService.save(masterModel);
				map.put("masterModel", new MasterModel());
			}
		}
		catch(ConstraintViolationException e){
			operationMessage="duplicateRecord";
			map.put("validationError",1);
		}
		catch(Exception e) {
			logger.error("",e);
			operationMessage="saveError";
			map.put("validationError",1);
		}
		finally{
			map.put("masterModelDataList", masterService.list(pageNumber));
			map.put("pageNumber",pageNumber);
			String[] arrayResult=OperationMessage.getOperationMessageType(operationMessage);
			map.put("operationMessage",arrayResult[0]);
			map.put("messageType",arrayResult[1]);
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/formFillForEditMaster/{formFillForEditId}/{pageNumber}")
	public ModelAndView formFillForEditMaster(Map<String, Object> map,
			@PathVariable("formFillForEditId") Long formFillForEditId,
			@PathVariable("pageNumber") int pageNumber){
		ModelAndView modelAndView=new ModelAndView("master");
		MasterModel masterModel=null;
		String operationMessage="";
		try {
			masterModel=masterService.formFillForEdit(formFillForEditId);
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("masterModel",masterModel );
			map.put("masterModelDataList", masterService.list(pageNumber));
			map.put("IdForUpdate", formFillForEditId);
			map.put("pageNumber",pageNumber);
			String[] arrayResult=OperationMessage.getOperationMessageType(operationMessage);
			map.put("operationMessage",arrayResult[0]);
			map.put("messageType",arrayResult[1]);
		}
		return modelAndView;
	}

	@RequestMapping(value="/deleteMaster/{idForDelete}/{pageNumber}")
	public ModelAndView deleteMaster(Map<String, Object> map,
			@PathVariable("idForDelete") String idForDelete,
			@PathVariable("pageNumber") int pageNumber){
		ModelAndView modelAndView=new ModelAndView("master");
		String operationMessage="";
		try {
			operationMessage=masterService.delete(Long.valueOf(idForDelete));
		}
		catch(DataIntegrityViolationException e){
			operationMessage="foreignKeyConstraint";
		}
		catch(Exception e) {
			logger.error("",e);
			operationMessage="deleteError";
		}
		finally{
			map.put("masterModel", new MasterModel());
			map.put("masterModelDataList", masterService.list(pageNumber));
			map.put("pageNumber",pageNumber);
			String[] arrayResult=OperationMessage.getOperationMessageType(operationMessage);
			map.put("operationMessage",arrayResult[0]);
			map.put("messageType",arrayResult[1]);
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/updateMaster/{IdForUpdate}/{pageNumber}", method=RequestMethod.POST)
	public ModelAndView updateMaster(Map<String, Object> map,
			@ModelAttribute("masterModel") MasterModel masterModel,
			BindingResult bindingResult,
			@PathVariable("IdForUpdate") String IdForUpdate,
			@PathVariable("pageNumber") int pageNumber){
		ModelAndView modelAndView=new ModelAndView("master");
		String operationMessage="";
		try {
			validator.validate(masterModel, bindingResult);
			if(bindingResult.hasFieldErrors()){
				map.put("validationError",2);
				operationMessage="validationeError";
			}
			else{
				masterModel.setId(Long.valueOf(IdForUpdate));
				operationMessage=masterService.update(masterModel);
				map.put("masterModel", new MasterModel());
			}
		}
		catch(DataIntegrityViolationException e){
			operationMessage="uniqueKeyConstraint";
			map.put("IdForUpdate", IdForUpdate);
		}
		catch(Exception e) {
			logger.error("",e);
			map.put("IdForUpdate", IdForUpdate);
			operationMessage="updateError";
		}
		finally{
			map.put("masterModelDataList", masterService.list(pageNumber));
			map.put("pageNumber",pageNumber);
			String[] arrayResult=OperationMessage.getOperationMessageType(operationMessage);
			map.put("operationMessage",arrayResult[0]);
			map.put("messageType",arrayResult[1]);
		}
		return modelAndView;
	}
}
