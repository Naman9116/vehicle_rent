package com.relatedInfo.controller;

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
import com.master.model.GeneralMasterModel;
import com.master.model.MappingMasterModel;
import com.master.model.MasterModel;
import com.master.service.GeneralMasterService;
import com.master.service.MappingMasterService;
import com.relatedInfo.model.RelatedInfoModel;
import com.relatedInfo.service.RelatedInfoService;
import com.relatedInfo.validator.RelatedInfoValidator;
import com.util.JsonResponse;
import com.util.Message;
import com.util.Utilities;

@Controller
public class RelatedInfoController {
	private static final Logger logger = Logger.getLogger(RelatedInfoController.class);

	@Autowired
	private RelatedInfoService relatedInfoService;
	
	@Autowired
	private MasterDataService masterDataService;
	
	@Autowired
	private MappingMasterService mappingMasterService;
	
	@Autowired
	private GeneralMasterService generalMasterService;
	
	private RelatedInfoValidator validator = null;

	public RelatedInfoValidator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(RelatedInfoValidator validator) {
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
	
	@RequestMapping("/pageLoadRelatedInfo")
	public ModelAndView pageLoadRelatedInfo(Map<String, Object> map,HttpSession session,
			@RequestParam("pageFor") String pageFor) {
		ModelAndView modelAndView=new ModelAndView("relatedInfo");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try {
			map.put("companyIdList",masterDataService.getGeneralMasterData_UsingCode("VCM"));
			map.put("branchIdList",masterDataService.getGeneralMasterData_UsingCode("VBM-A:"+branchesAssigned));				//  Vehicle Branch Type
			map.put("outletIdList",masterDataService.getGeneralMasterData_UsingCode("VOM"));				//  Vehicle Outlet Type
			map.put("terminalsMaster",masterDataService.getGeneralMasterData_UsingCode("TRM"));				//  Vehicle Outlet Type
			map.put("pageFor",pageFor);
			map.put("masterDataList",masterDataService.getMasterData("All"));
			map.put("relatedInfoDataList",showGrid(relatedInfoService.list(pageFor),pageFor));
			map.put("stateMasterList", masterDataService.getStateMasterData());
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("relatedInfoModel", new RelatedInfoModel());
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/saveOrUpdateRelatedInfo",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateRelatedInfo(
			@ModelAttribute(value="relatedInfoModel") RelatedInfoModel relatedInfoModel,
			BindingResult bindingResult ,
			@RequestParam("pageFor") String pageFor,
			@RequestParam("idForUpdate") String idForUpdate, HttpSession session){
		JsonResponse res = new JsonResponse();
		/*Save new company master and get new id to set in relatedInfoModel*/
		GeneralMasterModel generalMasterModel =new GeneralMasterModel();

		relatedInfoModel.setParentType(pageFor);
		relatedInfoModel.setGeneralMasterModel(generalMasterModel); 
		validator.validate(relatedInfoModel, bindingResult);
		if(bindingResult.hasErrors()){
			res.setStatus("Errors");
			res.setResult(bindingResult.getAllErrors());
			return res;
		}
		MappingMasterModel mappingMasterModel = new MappingMasterModel();
		Long newId = 0l,lRelatedInfoId = 0l;
		List<Long> lnewIdMapping = new ArrayList<Long>();
		try{
			relatedInfoModel.setEntityAddress(relatedInfoModel.getAddressDetailModel());
			relatedInfoModel.setEntityContact(relatedInfoModel.getContactDetailModel());
			
			if(pageFor != null) relatedInfoModel.setParentType(pageFor);
			
			List<MasterModel> masterModels = null;
			if(pageFor.equalsIgnoreCase("Company")) masterModels = masterDataService.getMasterData("VCM");
			if(pageFor.equalsIgnoreCase("Branch"))  masterModels = masterDataService.getMasterData("VBM");
			if(pageFor.equalsIgnoreCase("Outlet"))  masterModels = masterDataService.getMasterData("VOM");
			generalMasterModel.setItemCode(relatedInfoModel.getCode());
			
			if(masterModels!=null && masterModels.size()>0){
				generalMasterModel.setMasterModel(masterModels.get(0));
				generalMasterModel.setMasterId(generalMasterModel.getMasterModel().getId());
				
				if(pageFor.equalsIgnoreCase("Company")) generalMasterModel.setName(relatedInfoModel.getCompanyName());
				if(pageFor.equalsIgnoreCase("Branch"))  generalMasterModel.setName(relatedInfoModel.getBranchName());
				if(pageFor.equalsIgnoreCase("Outlet"))  generalMasterModel.setName(relatedInfoModel.getOutletName());

				if(idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")){
					newId = generalMasterService.saveGeneralMaster(generalMasterModel);
					if(pageFor.equalsIgnoreCase("Company")) relatedInfoModel.setCompany(newId);
					if(pageFor.equalsIgnoreCase("Branch")) relatedInfoModel.setBranch(newId);
					if(pageFor.equalsIgnoreCase("Outlet")) relatedInfoModel.setOutlet(newId);
				}
			}
			if(pageFor.equalsIgnoreCase("Company")){ 
				generalMasterModel.setId(relatedInfoModel.getCompany());
				relatedInfoModel.getGeneralMasterModel().setId(relatedInfoModel.getCompany());
				relatedInfoModel.getGeneralMasterModel().setName(relatedInfoModel.getCompanyName());
			}
			if(pageFor.equalsIgnoreCase("Branch")){
				generalMasterModel.setId(relatedInfoModel.getBranch());
				relatedInfoModel.getGeneralMasterModel().setId(relatedInfoModel.getBranch());
				relatedInfoModel.getGeneralMasterModel().setName(relatedInfoModel.getBranchName());
			}
			if(pageFor.equalsIgnoreCase("Outlet")){
				generalMasterModel.setId(relatedInfoModel.getOutlet());
				relatedInfoModel.getGeneralMasterModel().setId(relatedInfoModel.getOutlet());
				relatedInfoModel.getGeneralMasterModel().setName(relatedInfoModel.getOutletName());
			}

			if(!pageFor.equalsIgnoreCase("Company")){ //Mapping also save in case of branch or outlet
				mappingMasterModel = new MappingMasterModel();
				GeneralMasterModel masterValuesModel = new GeneralMasterModel();
				GeneralMasterModel subMasterValueModel = new GeneralMasterModel();
				
				Long lParentId = null, lChildId = null;
				if(pageFor.equalsIgnoreCase("Branch")){
					lParentId = relatedInfoModel.getCompany();
					lChildId  = relatedInfoModel.getBranch();
				}
				if(pageFor.equalsIgnoreCase("Outlet")){
					lParentId = relatedInfoModel.getBranch();
					lChildId  = relatedInfoModel.getOutlet();
				}
				if(lParentId != null){
					List<GeneralMasterModel> parentList = null;
					if(pageFor.equalsIgnoreCase("Branch")){
						parentList = masterDataService.getGeneralMasterData_UsingCode("VCM");
					}else if(pageFor.equalsIgnoreCase("Outlet")){
						parentList = masterDataService.getGeneralMasterData_UsingCode("VBM");
					}
					boolean bRemoved = false;
					for(GeneralMasterModel parent:parentList ){
						if(lParentId == 0 || ( lParentId == parent.getId().longValue())){
							masterValuesModel.setId(parent.getId());
							subMasterValueModel.setId(lChildId);
							mappingMasterModel.setMasterValuesModel(masterValuesModel);
							mappingMasterModel.setSubMasterValuesModel(subMasterValueModel);
							if(!bRemoved){ 
								mappingMasterService.remove(mappingMasterModel, ""); /*Removing existing mappings*/
								bRemoved = true;
							}
							lnewIdMapping.add(Long.parseLong(mappingMasterService.save(mappingMasterModel,"")));
						}
					}
				}
			}
			
			if(idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")){
				lRelatedInfoId = Long.parseLong(relatedInfoService.save(relatedInfoModel));
				res.setResult(Message.SAVE_SUCCESS_MESSAGE);
			}
			else{
				relatedInfoModel.setId(Long.valueOf(idForUpdate));
				relatedInfoService.update(relatedInfoModel);
				generalMasterService.update(generalMasterModel,"");
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
			if((idForUpdate.trim().equals("") || idForUpdate.trim().equals("0"))){
				if(!pageFor.equalsIgnoreCase("Company")){
					if(lRelatedInfoId == 0){
						generalMasterService.delete(newId);
						for(Long lnewId:lnewIdMapping){
							mappingMasterService.delete(lnewId);
						}
					}else if(lnewIdMapping.isEmpty() && newId > 0){
						generalMasterService.delete(newId);
					}
				}
			}
			res.setDataGrid(showGrid(relatedInfoService.list(pageFor),pageFor));
		}
		return res;
	}
	
	@RequestMapping(value="/formFillForEditRelatedInfo",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditRelatedInfo(
			@ModelAttribute(value="relatedInfoModel") RelatedInfoModel relatedInfoModel,
			BindingResult bindingResult ,
			@RequestParam("pageFor") String pageFor,
			@RequestParam("formFillForEditId") String formFillForEditId){
		JsonResponse res = new JsonResponse();
		Object models[]=null;
		try{
			relatedInfoModel=relatedInfoService.formFillForEdit(Long.parseLong(formFillForEditId));
		
			relatedInfoModel.setAddressDetailModel(relatedInfoModel.getEntityAddress());
			relatedInfoModel.setContactDetailModel(relatedInfoModel.getEntityContact());

			Long lChieldId = 0l;
			if(pageFor.equalsIgnoreCase("branch")){
				lChieldId = relatedInfoModel.getGeneralMasterModel().getId();
			}else if(pageFor.equalsIgnoreCase("outlet")){
				lChieldId = relatedInfoModel.getGeneralMasterModel().getId();
			}
			if( lChieldId > 0){
				models=mappingMasterService.fetchSubMasterValuesMappingMaster(lChieldId,0l);
				Long lParentId = (Long) models[0];
				String sParentName = "";
				if(lParentId > 0){
					GeneralMasterModel generalMasterModel=generalMasterService.formFillForEdit(lParentId,"");
					sParentName = generalMasterModel.getName();
				}else{
					sParentName = "All Companies";
				}
				
				if(pageFor.equalsIgnoreCase("branch")) {
					relatedInfoModel.setCompany(lParentId);
					relatedInfoModel.setCompanyName(sParentName);
				}
				if(pageFor.equalsIgnoreCase("outlet")){
					relatedInfoModel.setBranch(lParentId);
					relatedInfoModel.setBranchName(sParentName);
				}
			}
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setDataGrid(showGrid(relatedInfoService.list(pageFor),pageFor));
			res.setRelatedInfoModel(relatedInfoModel);
		}
		return res;
	}
	
	@RequestMapping(value="/deleteRelatedInfo",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteRelatedInfo(
			@ModelAttribute(value="relatedInfoModel") RelatedInfoModel relatedInfoModel,
			BindingResult bindingResult ,
			@RequestParam("pageFor") String pageFor,
			@RequestParam("idForDelete") String idForDelete){
		JsonResponse res = new JsonResponse();
		try{
			relatedInfoService.delete(Long.valueOf(idForDelete));
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
			res.setDataGrid(showGrid(relatedInfoService.list(pageFor),pageFor));
		}
		return res;
	}
	
	public String showGrid(List<RelatedInfoModel> relatedInfoModelDataList,String pageFor){
		String dataString =	"";
		if(pageFor.equalsIgnoreCase("Company")){
			dataString =	
				" <table class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"+
				"	<thead style='background-color: #FED86F;'>		"+
			  	" 	<tr> 															"+
			  	" 		<th width="+Message.ACTION_SIZE+">Action</th> 		  "+
			  	" 		<th>"+pageFor+" Name</th>	"+
			  	" 		<th>PAN No</th> 			"+
			  	" 		<th>Service Tax No</th> 	"+
			  	" 		<th>TAN No</th> 			"+
			  	" 		<th>LST No</th> 			"+
			  	" 		<th>E-Mail</th> 			"+
			  	" 		<th>Helpline No.</th> 		"+
			  	" 		<th>Website</th> 			"+
			  	" 		<th>D.O.R.</th> 			"+
			  	" 	</tr>							"+
			  	"	</thead>						"+
			  	"   <tbody>							"; 
				for(RelatedInfoModel relatedInfoModel:relatedInfoModelDataList ){
					dataString=dataString+" <tr> "+
					" <td>"+
					" 	<a href='javascript:formFillForEditRelatedInfo("+relatedInfoModel.getId()+")'>"+Message.EDIT_BUTTON+"</a> "+
					" 	<a href='javascript:viewRelatedInfo("+relatedInfoModel.getId()+")' >"+Message.VIEW_BUTTON+"</a> "+ 				
					" 	<a href='javascript:deleteRelatedInfo("+relatedInfoModel.getId()+")' >"+Message.DELETE_BUTTON+"</a> "+ 				
					" </td> 		"+
					" 	<td>"+ relatedInfoModel.getGeneralMasterModel().getName()+ "</td>  	"+
					" 	<td>"+ relatedInfoModel.getPanNo()+ "</td>   		"+
					" 	<td>"+ relatedInfoModel.getCstNo()+ "</td>   		"+
					" 	<td>"+ relatedInfoModel.getTanNo()+ "</td>   		"+
					" 	<td>"+ relatedInfoModel.getLstNo()+ "</td>   		"+
					" 	<td>"+ relatedInfoModel.getEmail()+ "</td>   		"+
					" 	<td>"+ relatedInfoModel.getHelpLine()+ "</td> 		"+
					" 	<td>"+ relatedInfoModel.getWebSite()+ "</td>  		"+
					" 	<td>"+ relatedInfoModel.getRegDate()+ "</td>   		"+
					" </tr> 		";
				}	
				dataString=dataString+"</tbody></table>";
		}else{
			dataString =	
					" <table class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"+
					"	<thead style='background-color: #FED86F;'>"+
				  	" 	<tr> 									  "+
				  	" 		<th width="+Message.ACTION_SIZE+">Action</th> 		  "+
				  	" 		<th>"+pageFor+" Name</th>	";
				  	if(pageFor.equalsIgnoreCase("Branch"))
				  		dataString +=" 		<th>State</th> 				";
				  	else
				  		dataString +=" 		<th>Branch Name</th> 		";
				  	dataString +=" 		<th>Date of Opening</th> 			"+
				  	" 		<th>E-Mail</th> 			"+
				  	" 		<th>Helpline No.</th> 		"+
				  	" 		<th>Contact Person</th> 	"+
				  	" 		<th>Person Contact No</th> 	"+
				  	" 	</tr>							"+
				  	"	</thead>						"+
				  	"   <tbody>							"; 
					for(RelatedInfoModel relatedInfoModel:relatedInfoModelDataList ){
						String sState ="",sContPer  ="",sContPerNo="";
						try{
							if(relatedInfoModel.getEntityAddress() != null) sState   = relatedInfoModel.getEntityAddress().getState().getName();
							if(relatedInfoModel.getEntityContact() != null) sContPer = relatedInfoModel.getEntityContact().getContactPerson1();
							if(relatedInfoModel.getEntityContact() != null) sContPerNo = relatedInfoModel.getEntityContact().getPersonalMobile();
						}catch(Exception e){}
						dataString=dataString+" <tr> "+
						" <td> "+
						" 	<a href='javascript:formFillForEditRelatedInfo("+relatedInfoModel.getId()+")'>"+Message.EDIT_BUTTON+"</a> "+
						" 	<a href='javascript:viewRelatedInfo("+relatedInfoModel.getId()+")' >"+Message.VIEW_BUTTON+"</a> "+ 				
						" 	<a href='javascript:deleteRelatedInfo("+relatedInfoModel.getId()+")' >"+Message.DELETE_BUTTON+"</a> "+ 				
						" </td> 		"+
						" 	<td>"+ relatedInfoModel.getGeneralMasterModel().getName()+ "</td>  	";
					  	if(pageFor.equalsIgnoreCase("Branch"))
					  		dataString +=" 		<td>"+ sState +"</td> ";
					  	else
					  		dataString +=" 		<th>"+ relatedInfoModel.getBranchName() +"</th> 		";
						
					  	dataString +=" 	<td>"+ relatedInfoModel.getRegDate()+ "</td>   		"+
						" 	<td>"+ relatedInfoModel.getEmail()+ "</td>   		"+
						" 	<td>"+ relatedInfoModel.getHelpLine()+ "</td> 		"+
						" 	<td>"+ sContPer+ "</td> 		"+
						" 	<td>"+ sContPerNo+ "</td> 		"+
						" </tr> 		";
					}	
					dataString=dataString+"</tbody></table>";
		}
		return dataString;
	}

	@RequestMapping(value="/getDataGridDataRelatedInfo",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getDataGridDataRelatedInfo(
			@ModelAttribute(value="relatedInfoModel") RelatedInfoModel relatedInfoModel,
			BindingResult bindingResult ,
			@RequestParam("pageFor") String pageFor,
			@RequestParam("companyCode") String companyCode,
			@RequestParam("searchCriteria") String searchCriteria){
		JsonResponse res = new JsonResponse();
		try{
			res.setDataGrid(showGrid(relatedInfoService.listMasterWise(null,pageFor),pageFor));
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
