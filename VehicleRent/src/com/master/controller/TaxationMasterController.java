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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.common.service.MasterDataService;
import com.corporate.service.CorporateService;
import com.master.model.CorporateTaxDetModel;
import com.master.model.TaxationModel;
import com.master.service.TaxationMasterService;
import com.master.validator.TaxationMasterValidator;
import com.util.JsonResponse;
import com.util.Message;

@Controller
public class TaxationMasterController {
	private static final Logger logger = Logger.getLogger(TaxationMasterController.class);

	@Autowired
	private TaxationMasterService taxationMasterService;
	
	@Autowired
	private CorporateService corporateService;
	
	@Autowired
	private MasterDataService masterDataService;
	
	private TaxationMasterValidator validator = null;

	public TaxationMasterValidator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(TaxationMasterValidator validator) {
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
	
	@RequestMapping("/pageLoadTaxation")
	public ModelAndView pageLoadTaxation(Map<String, Object> map) {
		ModelAndView modelAndView=new ModelAndView("taxationMaster");
		try {
			map.put("taxationDataList", masterDataService.getGeneralMasterData_UsingCode("TAX")); 
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("taxationModel", new TaxationModel());
			map.put("existingData", showGrid(taxationMasterService.list()));
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/saveOrUpdateTaxation",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateGeneralMaster(
			@ModelAttribute(value="generalMasterModel") TaxationModel taxationModel,
			BindingResult bindingResult,HttpSession session,
			@RequestParam("idForUpdate") String idForUpdate){
		JsonResponse res = new JsonResponse();
		
		try{
			validator.validate(taxationModel, bindingResult);
			if(!bindingResult.hasErrors()){
				if(idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")){
					taxationMasterService.save(taxationModel);
					CorporateTaxDetModel corporateTaxDetModel = new CorporateTaxDetModel();
					corporateTaxDetModel.setTaxationModelId(taxationModel.getId());
					corporateTaxDetModel.setInsDate(taxationModel.getEfDate());
					corporateTaxDetModel.setTaxVal(taxationModel.getTaxPer());
					if(session.getAttribute("loginUserId")!=null){
						Long loginUserId=(Long) session.getAttribute("loginUserId");
				        corporateTaxDetModel.setUserId(loginUserId);
					}
					taxationMasterService.saveCorpTaxDetail(corporateTaxDetModel);
					res.setResult(Message.SAVE_SUCCESS_MESSAGE);
				}
				else{
					taxationModel.setId(Long.valueOf(idForUpdate));
					taxationMasterService.update(taxationModel);
					CorporateTaxDetModel corporateTaxDetModel = new CorporateTaxDetModel();
					corporateTaxDetModel.setTaxationModelId(taxationModel.getId());
					corporateTaxDetModel.setInsDate(taxationModel.getEfDate());
					corporateTaxDetModel.setTaxVal(taxationModel.getTaxPer());
					if(session.getAttribute("loginUserId")!=null){
						Long loginUserId=(Long) session.getAttribute("loginUserId");
				        corporateTaxDetModel.setUserId(loginUserId);
					}
					taxationMasterService.updateCorpTaxDetail(corporateTaxDetModel);
					taxationMasterService.saveCorpTaxDetail(corporateTaxDetModel);
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
			res.setDataGrid(showGrid(taxationMasterService.list()));
		}
		return res;
	}
	
	@RequestMapping(value="/formFillForEditTaxation",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditTaxation(
			@ModelAttribute(value="taxationModel") TaxationModel taxationModel,
			BindingResult bindingResult ,
			@RequestParam("formFillForEditId") String formFillForEditId){
		JsonResponse res = new JsonResponse();
		try{
			taxationModel=taxationMasterService.formFillForEdit(Long.parseLong(formFillForEditId));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setDataGrid(showGrid(taxationMasterService.list()));
			res.setTaxationModel(taxationModel);
		}
		return res;
	}
	
	@RequestMapping(value="/deleteTaxation",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteGeneralMaster(
			@ModelAttribute(value="taxationModel") TaxationModel taxationModel,
			BindingResult bindingResult ,
			@RequestParam("idForDelete") String idForDelete){
		JsonResponse res = new JsonResponse();
		try{
			taxationMasterService.delete(Long.valueOf(idForDelete));
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
			res.setDataGrid(showGrid(taxationMasterService.list()));
		}
		return res;
	}
	
	public String showGrid(List<TaxationModel> taxationModelDataList){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dataString =	
		" <table class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"+
		"	<thead style='background-color: #FED86F;'>		"+
		" 	<tr> 											"+
	  	" 		<th width="+Message.ACTION_SIZE+">Action</th> "+
	  	"   	<th>Taxation Name</th> 	"+
	  	"   	<th>Effective Date</th> 	"+
	  	"   	<th>Calculation Type</th> 	"+
	  	"   	<th>Tax Percentage</th> 	"+
	  	"   	<th>Tax Value</th> 			"+
	  	" 	</tr>						"+
	  	"	</thead>					"+
	  	"   </tbody>					"; 
		for(TaxationModel taxationModel:taxationModelDataList ){
			dataString+=" <tr> "+
			" <td> "+
			" 	<a href='javascript:formFillForEditTaxation("+taxationModel.getId()+")'>"+Message.EDIT_BUTTON+"</a>"+
			" 	<a href='javascript:viewRecord("+taxationModel.getId()+")' >"+Message.VIEW_BUTTON+"</a> "+ 				
			" 	<a href='javascript:deleteRecord("+taxationModel.getId()+")' >"+Message.DELETE_BUTTON+"</a> "+ 
			" </td>"+
			" <td id='"+taxationModel.getId()+"'>"+ taxationModel.getParentId().getName()+ "</td>   "+
			" <td>"+ dateFormat.format(taxationModel.getEfDate())+ "</td>   ";
			if(taxationModel.getCalType().equalsIgnoreCase("C"))
				dataString += " <td>"+  "Cumulative"+ "</td>   ";
			else
				dataString += " <td>"+  "Last Entry"+ "</td>   ";
			
			dataString += " <td>"+ taxationModel.getTaxPer()+ "</td>   "+
			" <td>"+ taxationModel.getTaxVal()+ "</td>   "+
			" </tr>";
		}	
		dataString=dataString+"</tbody></table>";
		return dataString;
	}
}
