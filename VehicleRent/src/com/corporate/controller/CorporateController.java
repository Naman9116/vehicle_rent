package com.corporate.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.corporate.model.CorporateModel;
import com.corporate.service.CorporateService;
import com.corporate.validator.CorporateValidator;
import com.master.model.CorporateTaxDetModel;
import com.master.model.GeneralMasterModel;
import com.master.model.TaxationModel;
import com.master.service.GeneralMasterService;
import com.util.JsonResponse;
import com.util.Message;

@Controller
public class CorporateController {
	private static final Logger logger = Logger.getLogger(CorporateController.class);
	String loginBranchCode;
	String branchesAssigned;

	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	@Autowired
	private CorporateService corporateService;
	
	@Autowired
	private MasterDataService masterDataService;
	
	@Autowired
	private GeneralMasterService generalMasterService;
	
	private CorporateValidator validator = null;

	public CorporateValidator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(CorporateValidator validator) {
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
	
	@RequestMapping("/pageLoadCorporate")
	public ModelAndView pageLoadCorporate(Map<String, Object> map, HttpSession session) {
		ModelAndView modelAndView=new ModelAndView("corporate");
		loginBranchCode = (String) session.getAttribute("loginBranchCode");
		branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try {
			map.put("companyIdList",masterDataService.getGeneralMasterData_UsingCode("VCM"));
			map.put("tariffScheme",masterDataService.getGeneralMasterData_UsingCode("VTF"));
			map.put("tariffCharges",masterDataService.getGeneralMasterData_UsingCode("VTR"));
			map.put("billingCycle",masterDataService.getGeneralMasterData_UsingCode("BICY"));
			List<GeneralMasterModel> taxationModelList = masterDataService.getGeneralMasterData_UsingCode("TaxationList");
			
			for (GeneralMasterModel generalMasterModel : taxationModelList){
				generalMasterModel.setEfDate(generalMasterModel.getEfDate());
				String efDate = dateFormat.format(generalMasterModel.getEfDate());
				if(generalMasterModel.getName().contains("Discount")){
					map.put("efDate", efDate);
					System.out.println("Discount Effective Date :  "+efDate);
				}
			}
			map.put("taxationList",taxationModelList);
			map.put("masterDataList",masterDataService.getMasterData("All"));
			map.put("corporateDataList",showGrid(corporateService.list()));
			map.put("stateMasterList", masterDataService.getStateMasterData());
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("corporateModel", new CorporateModel());
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/saveOrUpdateCorporate",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateCorporate(
			@ModelAttribute(value="corporateModel") CorporateModel corporateModel,
			BindingResult bindingResult,
			@RequestParam("idForUpdate") String idForUpdate,
			@RequestParam(value="id", required=false) Long[] id, 
			@RequestParam(value="taxVal", required=false) Double[] taxVal, 
			@RequestParam(value="valueChanged", required=false) String valueChanged,
			@RequestParam(value="idForUpdateTax", required=false) Long[] idForUpdateTax,
			@RequestParam(value="currentDate", required=false) String[] currentDate,
			@RequestParam(value="saveUpdate", required=false) String[] saveUpdate,
			HttpSession session){
		JsonResponse res = new JsonResponse();
		validator.validate(corporateModel, bindingResult);
		if(bindingResult.hasErrors()){
			res.setStatus("Errors");
			res.setResult(bindingResult.getAllErrors());
			return res;
		}
		Long loginUserId = null;
		try{
			if(session.getAttribute("loginUserId")!=null){
				loginUserId=(Long) session.getAttribute("loginUserId");
			}
			logger.info("corpIdForUpdate : " + idForUpdate);
			CorporateTaxDetModel corporateTaxDetModel = new CorporateTaxDetModel();
			corporateModel.setEntityAddress(corporateModel.getAddressDetailModel());
			corporateModel.setEntityContact(corporateModel.getContactDetailModel());
			if(idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")){
				corporateService.save(corporateModel);
				for(int i= 0;i< id.length; i++){
					corporateTaxDetModel.setTaxationModelId(id[i]);
					corporateTaxDetModel.setCorporateModelId(corporateModel.getId());
					Date insDate = dateFormat.parse(currentDate[i]);
					corporateTaxDetModel.setInsDate(insDate);
					corporateTaxDetModel.setTaxVal(taxVal[i]);
					corporateTaxDetModel.setUserId(loginUserId);
					corporateService.save(corporateTaxDetModel);
				}
				res.setResult(Message.SAVE_SUCCESS_MESSAGE);
			}
			else{
				corporateModel.setId(Long.valueOf(idForUpdate));
				corporateService.update(corporateModel);
				if(valueChanged.equalsIgnoreCase("changed")){
					for(int i= 0;i< id.length; i++){
						Date insDate = dateFormat.parse(currentDate[i]);
						corporateTaxDetModel.setTaxationModelId(id[i]);
						corporateTaxDetModel.setCorporateModelId(Long.valueOf(idForUpdate));
						corporateTaxDetModel.setInsDate(insDate);
						corporateTaxDetModel.setTaxVal(taxVal[i]);
						corporateTaxDetModel.setUserId(loginUserId);
						
						if (saveUpdate[i].equals("U")) {
							corporateTaxDetModel.setId(idForUpdateTax[i]);
							corporateService.update(corporateTaxDetModel);
						} else {
							corporateService.save(corporateTaxDetModel);
						}
					}
				}
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
			res.setDataGrid(showGrid(corporateService.list()));
		}
		return res;
	}
	
	@RequestMapping(value="/formFillForEditCorporate",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditCorporate(
			@ModelAttribute(value="corporateModel") CorporateModel corporateModel,
			BindingResult bindingResult,
			@RequestParam("formFillForEditId") String formFillForEditId){
		JsonResponse res = new JsonResponse();
		try{
			corporateModel=corporateService.formFillForEdit(Long.parseLong(formFillForEditId));
			corporateModel.setAddressDetailModel(corporateModel.getEntityAddress());
			corporateModel.setContactDetailModel(corporateModel.getEntityContact());
			List <CorporateTaxDetModel> corporateTaxDetModelList = corporateService.formFillForEditCT(Long.parseLong(formFillForEditId));

			res.setStatus("Success");
			res.setCorporateTaxDetModelList(corporateTaxDetModelList);
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setCorporateModel(corporateModel);
		}
		return res;
	}
	
	@RequestMapping(value="/getBranchCorporate",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getBranchCorporate(
			@RequestParam("companyId") String companyId, HttpSession session){
		JsonResponse res = new JsonResponse();
		branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try{
			res.setGeneralMasterModelList(masterDataService.getGeneralMasterData_UsingCode("LinkedBranchCompany:"+companyId + ":" + branchesAssigned));
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
	
	@RequestMapping(value="/deleteCorporate",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteCorporate(
			@ModelAttribute(value="corporateModel") CorporateModel corporateModel,
			BindingResult bindingResult ,
			@RequestParam("idForDelete") String idForDelete){
		JsonResponse res = new JsonResponse();
		try{
			corporateService.delete(Long.valueOf(idForDelete));
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
			res.setDataGrid(showGrid(corporateService.list()));
		}
		return res;
	}
	
	public String showGrid(List<CorporateModel> corporateModelDataList){
		String dataString =	"";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dataString =	
			" <table class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"+
			"	<thead style='background-color: #FED86F;'>		"+
		  	" 	<tr> 											"+
		  	" 		<th width="+Message.ACTION_SIZE+">Action</th> 		  "+
		  	" 		<th>ID</th> 			"+
		  	" 		<th>Corporate Name</th> "+
		  	" 		<th>Start Date</th> 	"+
		  	" 		<th>End Date</th> 		"+
		  	" 		<th>Billing Cycle</th> 	"+
		  	" 		<th>Credit Cycle</th> 	"+
		  	" 		<th>Bkg Allow</th> 		"+
		  	" 		<th>Contact Person</th> "+
		  	" 		<th>Designation</th> 	"+
		  	" 		<th>Mobile No.</th> 	"+
		  	" 		<th>E-Mail</th> 		"+
		  	" 		<th>Status</th> 		"+
		  	" 	</tr>						"+
		  	"	</thead>					"+
		  	"   <tbody>						"; 
			for(CorporateModel corporateModel:corporateModelDataList ){
				String sCorpBranch = "," + corporateModel.getBranId() + ",";
				if(!branchesAssigned.contains(sCorpBranch) && corporateModel.getBranId() > 0 ) continue;
				String sBkgAllow = "No", sStatus = "InActive";
				sBkgAllow = corporateModel.getBookAllow().equals("Y")?"Yes":"No";
				sStatus   = corporateModel.getStatus().equals("Y")?"Active":"InActive";
				dataString=dataString+" <tr> "+
				" <td>"+
				" 	<a href='javascript:formFillForEditCorporate("+corporateModel.getId()+")'>"+Message.EDIT_BUTTON+"</a> "+
				" 	<a href='javascript:viewCorporate("+corporateModel.getId()+")' >"+Message.VIEW_BUTTON+"</a> "+ 				
				" 	<a href='javascript:deleteCorporate("+corporateModel.getId()+")' >"+Message.DELETE_BUTTON+"</a> "+ 				
				" </td> 														"+
				" 	<td>"+ corporateModel.getId()+ "</td>  						"+
				" 	<td>"+ corporateModel.getName()+ "</td>   					"+
				" 	<td>"+ dateFormat.format(corporateModel.getAgreementDt())+ "</td>   			"+
				" 	<td>"+ dateFormat.format(corporateModel.getExpiryDt())+ "</td>   				"+
				" 	<td>"+ corporateModel.getBillingCycle().getName()+ "</td>   "+
				" 	<td>"+ corporateModel.getCrCycle()+ "</td>   				"+
				" 	<td>"+ sBkgAllow + "</td> 				"+
				" 	<td>"+ corporateModel.getContactDetailModel().getContactPerson1()+ "</td>  		"+
				" 	<td>"+ corporateModel.getContactDetailModel().getContactPerson2()+ "</td>   	"+
				" 	<td>"+ corporateModel.getContactDetailModel().getPersonalMobile()+ "</td>   	"+
				" 	<td>"+ corporateModel.getContactDetailModel().getPersonalEmailId()+ "</td>   	"+
				" 	<td>"+ sStatus + "</td>   		"+
				" </tr> 		";
			}	
			dataString=dataString+"</tbody></table>";
		return dataString;
	}
}
