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
import com.corporate.model.CorporateModel;
import com.master.model.CorporateTariffDetModel;
import com.master.model.CorporateTariffModel;
import com.master.model.GeneralMasterModel;
import com.master.service.CorporateTariffService;
import com.master.validator.CorporateTariffValidator;
import com.operation.model.BookingMasterModel;
import com.operation.model.CarDetailModel;
import com.util.JsonResponse;
import com.util.Message;
import com.util.Utilities;

@Controller
public class CorporateTariffController {
	private static final Logger logger = Logger.getLogger(CorporateTariffController.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	@Autowired
	private CorporateTariffService corporateTariffService;
	
	@Autowired
	private MasterDataService masterDataService;
	
	private CorporateTariffValidator validator = null;

	public CorporateTariffValidator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(CorporateTariffValidator validator) {
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
	
	@RequestMapping("/pageLoadCorporateTariff")
	public ModelAndView pageLoadTariffParameterMaster(Map<String, Object> map, HttpSession session) {
		ModelAndView modelAndView=new ModelAndView("corporateTariff");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");

		try {
//			map.put("corporateList",masterDataService.getGeneralMasterData_UsingCode("CORP"));
//			map.put("carTypeList",masterDataService.getGeneralMasterData_UsingCode("CarList")); 
			map.put("carCatList",createTariffGrid("0")); 					
//			map.put("tariffIdList",masterDataService.getGeneralMasterData_UsingCode("VTF")); 					
			map.put("branchList",masterDataService.getGeneralMasterData_UsingCode("VBM-A:" + branchesAssigned)); 					
//			map.put("corporateTariffList",showGrid(corporateTariffService.list(),"0"));
			map.put("stateMasterList", masterDataService.getStateMasterData());
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("corporateTariffModel", new CorporateTariffModel());
		}
		return modelAndView;
	}

	@RequestMapping(value="/corporateBranchWise",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteTariffParameterMaster(
			@RequestParam("branchId") String branchId, HttpSession session){
		JsonResponse res = new JsonResponse();
		List<GeneralMasterModel> generalMasterModelList = null;
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try{
			generalMasterModelList = masterDataService.getGeneralMasterData_UsingCode("CORP:" + branchesAssigned);
			
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setDataString1(createTariffGrid(branchId));
			res.setGeneralMasterModelList(generalMasterModelList);
		}
		return res;
	}

	@RequestMapping(value="/tariffCorporateWise",method=RequestMethod.POST)
	public @ResponseBody JsonResponse tariffCorporateWise(
			@RequestParam("corporateId") String corporateId,
			@RequestParam("branchID") String branchID){
		JsonResponse res = new JsonResponse();
		List<GeneralMasterModel> generalMasterModelList = null;
		CorporateTariffModel corporateTariffModel = null;
		List<CorporateTariffModel> corporateTariffModels = corporateTariffService.list(corporateId,branchID);

		if(corporateTariffModels!=null && corporateTariffModels.size() > 0)
			corporateTariffModel = corporateTariffModels.get(corporateTariffModels.size() - 1);
			res.setCorporateTariffModelList(corporateTariffModels);
		try{
			if(corporateTariffModel!= null){
				res.setCorporateTariffDetModelList(corporateTariffModel.getCorporateTariffDetModel());
			}
		 	res.setCorporateTariffModel(corporateTariffModel);
		 	res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setGeneralMasterModelList(generalMasterModelList);
			res.setDataString1(createTariffGrid(corporateId));
			res.setDataGrid(showGrid(corporateTariffService.list(),corporateId));
		}
		return res;
	}
	
	@RequestMapping(value="/saveOrUpdateCorporateTariff",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateTariffParameterMaster(
			@ModelAttribute(value="corporateTariffModel") CorporateTariffModel corporateTariffModel,
			BindingResult bindingResult,
			@RequestParam("controlsWithValue") String controlsWithValue,
			@RequestParam("idForUpdate") String idForUpdate){
			JsonResponse res = new JsonResponse();
		try{
			validator.validate(corporateTariffModel, bindingResult);
			if(!bindingResult.hasErrors()){
				corporateTariffModel.setEntityAddress(corporateTariffModel.getAddressDetailModel());
				if(idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")){
					corporateTariffService.save(corporateTariffModel,controlsWithValue);
					res.setResult(Message.SAVE_SUCCESS_MESSAGE);
				}
				else{
					CorporateTariffModel corporateTariffModel2 = corporateTariffService.formFillForEdit(Long.valueOf(idForUpdate));
					System.out.println("1st "+dateFormat.format(corporateTariffModel.getFuelHikeDate())+"  "+"2nd "+dateFormat.format(corporateTariffModel2.getFuelHikeDate()));
					if(dateFormat.format(corporateTariffModel.getFuelHikeDate()).equals(dateFormat.format(corporateTariffModel2.getFuelHikeDate()))){
						corporateTariffModel.setId(Long.valueOf(idForUpdate));
						corporateTariffService.update(corporateTariffModel,controlsWithValue);
						res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
					}
					else{
						corporateTariffService.save(corporateTariffModel,controlsWithValue);
						res.setResult(Message.SAVE_SUCCESS_MESSAGE);
					}
				}
				res.setStatus("Success");
			}
			else{
				res.setStatus("Errors");
				res.setResult(bindingResult.getAllErrors());
			}
		}
		catch(DataIntegrityViolationException e){
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.DUPLICATE_ERROR_MESSAGE);
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
			res.setDataGrid(showGrid(corporateTariffService.list(),corporateTariffModel.getCorporateId().getId().toString()));
		}
		return res;
	}
	
	@RequestMapping(value="/formFillForEditTariffParameterMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditTariffParameterMaster(
			@ModelAttribute(value="corporateTariffModel") CorporateTariffModel corporateTariffModel,
			BindingResult bindingResult,
			@RequestParam("formFillForEditId") String formFillForEditId){
		JsonResponse res = new JsonResponse();
		try{
			corporateTariffModel=corporateTariffService.formFillForEdit(Long.parseLong(formFillForEditId));
			corporateTariffModel.setAddressDetailModel(corporateTariffModel.getEntityAddress());
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setCorporateTariffModel(corporateTariffModel);
			res.setCorporateTariffDetModelList(corporateTariffModel.getCorporateTariffDetModel());
			res.setDataGrid(showGrid(corporateTariffService.list(),corporateTariffModel.getCorporateId().getId().toString()));
		}
		return res;
	}
	
	@RequestMapping(value="/deleteTariff",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteTariffParameterMaster(
			@ModelAttribute(value="corporateTariffModel") CorporateTariffModel corporateTariffModel,
			BindingResult bindingResult,
			@RequestParam("idForDelete") String idForDelete){
		JsonResponse res = new JsonResponse();
		try{
			corporateTariffService.delete(Long.valueOf(idForDelete));
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
			res.setDataGrid(showGrid(corporateTariffService.list(),corporateTariffModel.getCorporateId().getId().toString()));
		}
		return res;
	}
		
	public String createTariffGrid(String corporateId){	
		List <GeneralMasterModel> tariffIdList = masterDataService.getGeneralMasterData_UsingCode("TariffCorp:" + corporateId);
		List <GeneralMasterModel> carCatList   = masterDataService.getGeneralMasterData_UsingCode("VCT"); 					
		List <GeneralMasterModel> carTypeList  = masterDataService.getGeneralMasterData_UsingCode("CarList");

		String dataString =	"";
		dataString += "<table width=100% class='table table-bordered'> " +
					  "	<thead style='background-color: #FED86F;'> " +
					  " <tr> " +
					  "		<th width=10% align='center'><label>Car Category</label></th> " +
					  "		<th width=15% align='center'><label>Car Type</label></th> ";
		
		for(GeneralMasterModel tariffId:tariffIdList ){
	  		dataString += "	<th align='center'><label>"+tariffId.getName()+"</label></th>";
	  	}	

		dataString += " </tr> " +
					  " </thead> " +
					  " <tbody> ";
	  	for(GeneralMasterModel carCat : carCatList){
	  		dataString += "<tr> " +
	  					  "		<td><label>"+carCat.getName()+"</label></td> ";
	  		String sLabel = "";
		  	for(GeneralMasterModel carType:carTypeList ){
				if(carType.getId().longValue() == carCat.getId().longValue()){
					sLabel += carType.getName() + ",";
				}
		  	}
		  	if(!sLabel.equals("")) sLabel = sLabel.substring(0,sLabel.length() - 1);
		  	dataString += "		<td><lable>"+sLabel+"</lable></td>";
			for(GeneralMasterModel tariffId:tariffIdList ){
				dataString += " <td style='border:0px;'> " +
							  "		<input type='text' id ='TrfVal"+carCat.getId()+"_"+tariffId.getId()+"' style='width:100%;text-align: right;'value='' onkeypress='isDouble(event,this)' onblur='processTariff(this)' onChange='recordChange(this)'/> " +
							  "		<input type = 'hidden' id ='Hidden"+carCat.getId()+"_"+tariffId.getId()+"'/> " +
							  "	</td> ";
			}
			dataString += "</tr>";	
	  	}
	  	dataString += "</tbody> " +						
	  			      "</table>";
	  	return dataString;
	}
	
	public String showGrid(List<CorporateTariffModel> corporateTariffModelList, String corporateId){
		String dataString =	"";
		List <GeneralMasterModel> tariffIdList = masterDataService.getGeneralMasterData_UsingCode("TariffCorp:" + corporateId);
		List <GeneralMasterModel> carTypeList = masterDataService.getGeneralMasterData_UsingCode("CarList");
		dataString =	
			" <table class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"+
			"	<thead style='background-color: #FED86F;'>			"+
		  	" 	<tr> 												"+
		  	" 		<th width="+Message.ACTION_SIZE+">Action</th> 	"+
		  	" 		<th>Corporate Name</th> "+
		  	" 		<th>Branch</th> 		"+
		  	" 		<th>Car Category</th> 	"+
		  	" 		<th>Car Type</th> 		";
		  	for(GeneralMasterModel tariffId:tariffIdList ){
		  		dataString += " 		<th>" + tariffId.getName() + "</th> 		";
		  	}
		  	dataString += " 	</tr>						"+
		  	"	</thead>					"+
		  	"   <tbody>						"; 
		  	Long lPreCatId = 0l, lPreCorpId = 0l, lCatId = 0l, lLastTariffId = 0l;
		  	for(CorporateTariffModel corporateTariffModel:corporateTariffModelList ){
			  	int iListSize = corporateTariffModel.getCorporateTariffDetModel().size();
		  		for(int iRecord = 0; iRecord < iListSize;iRecord++ ){
		  			CorporateTariffDetModel corporateTariffDetModel = corporateTariffModel.getCorporateTariffDetModel().get(iRecord);
		  			lCatId = corporateTariffDetModel.getCarCatId().getId();
		  			if(lPreCatId != lCatId){
		  				if(lPreCatId > 0){
							for(GeneralMasterModel tariffId:tariffIdList ){
								if(tariffId.getId().longValue() > lLastTariffId){
					  				dataString += " 		<td></td> 		";
								}
						  	}
			  				dataString += " 		</tr>";
			  				lLastTariffId = 0l;
		  				}
		  				String sCarTypeList = "";
		  			  	for(GeneralMasterModel carType:carTypeList ){
		  			  		if(carType.getId().longValue() == corporateTariffDetModel.getCarCatId().getId().longValue()){
		  			  			sCarTypeList += carType.getName() + ",";
		  			  		}
		  			  	}
		  			  	if(!sCarTypeList.equals("")){
		  			  		sCarTypeList = sCarTypeList.substring(0,sCarTypeList.length() - 1);
		  			  	}
		  				
						dataString=dataString+" <tr> "+
						" <td>"+
						" 	<a href='javascript:formFillForEditCorporateTariff("+corporateTariffModel.getId()+")'>"+Message.EDIT_BUTTON+"</a> "+
						" 	<a href='javascript:viewCorporateTariff("+corporateTariffModel.getId()+")' >"+Message.VIEW_BUTTON+"</a> "+ 				
						" 	<a href='javascript:deleteCorporateTariff("+corporateTariffModel.getId()+")' >"+Message.DELETE_BUTTON+"</a> "+ 				
						" </td> 														 "+
						" <td>"+ corporateTariffModel.getCorporateId().getName() + "</td>"+
						" <td>"+ corporateTariffModel.getBranchId().getName()    + "</td>"+
						" <td>"+ corporateTariffDetModel.getCarCatId().getName() + "</td>"+
						" <td>"+ sCarTypeList + "</td>";
						for(GeneralMasterModel tariffId:tariffIdList ){
							lLastTariffId = tariffId.getId().longValue();
							if(tariffId.getId().longValue() == corporateTariffDetModel.getTariffId().getId().longValue()){
				  				dataString += " 		<td>" + corporateTariffDetModel.getTariffValue() + "</td> 		";
				  				break;
							}else{
				  				dataString += " 		<td></td> 		";
							}
					  	}
		  			}else{
						for(GeneralMasterModel tariffId:tariffIdList ){
							if(tariffId.getId().longValue() > lLastTariffId){
								if(tariffId.getId().longValue() == corporateTariffDetModel.getTariffId().getId().longValue()){
					  				dataString += " 		<td>" + corporateTariffDetModel.getTariffValue() + "</td> 		";
									lLastTariffId = tariffId.getId().longValue();
					  				break;
								}else{
					  				dataString += " 		<td></td> 		";
								}
							}
					  	}
		  			}	
		  			lPreCatId = lCatId;
		  		}
		  	}
			for(GeneralMasterModel tariffId:tariffIdList ){
				if(tariffId.getId().longValue() > lLastTariffId){
	  				dataString += " 		<td></td> 		";
				}
		  	}
			dataString += " 		</tr>";
		  	dataString=dataString+"</tbody></table>";
		return dataString;
	}

	@RequestMapping(value="/getDataGridDataTariffParameterMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getDataGridDataTariffParameterMaster(
			@ModelAttribute(value="corporateTariffModel") CorporateTariffModel corporateTariffModel,
			BindingResult bindingResult ,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("searchCriteria") String searchCriteria){
		JsonResponse res = new JsonResponse();
		try{
			res.setDataGrid(showGrid(corporateTariffService.listMasterWise(searchCriteria),"0"));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value="/getCarModel_usingCarType",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getCarModel_usingCarType(@RequestParam("carTypeId") String carTypeId,
		@RequestParam("fieldDefaultSelectedIndex") String fieldDefaultSelectedIndex){
		JsonResponse res = new JsonResponse();
		List<CarDetailModel> carDetailModelList=null;
		try{
			carDetailModelList= masterDataService.getCarModel_usingCarType(Long.parseLong(carTypeId));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setDataGrid(carCodeListBox(carDetailModelList,fieldDefaultSelectedIndex).toString());
		}
		return res;
	}
	
	public StringBuilder carCodeListBox(List<CarDetailModel> carDetailModelList,String fieldDefaultSelectedIndex){
		StringBuilder dataString =	new StringBuilder();
		String selectedIndex="";
		dataString.append("<SELECT NAME='carModel' id='carModel' class='form-control' style='width:99%'><OPTION VALUE='0' SELECTED > --- Select --- ");
		for(CarDetailModel carDetailModel:carDetailModelList ){
			if(fieldDefaultSelectedIndex.equalsIgnoreCase(carDetailModel.getModel().getId().toString()))
				selectedIndex="selected";
			else
				selectedIndex="";
			dataString.append("<OPTION VALUE='"+carDetailModel.getModel().getId()+"' "+selectedIndex+"> "+ carDetailModel.getModel().getName());
		}	
		dataString.append("</SELECT>") ;
		return dataString;
	}

	@RequestMapping(value="/getCommonTariffParameter_TariffParameterMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getCommonTariffParameter_TariffParameterMaster(@ModelAttribute(value="corporateTariffModel") CorporateTariffModel corporateTariffModel,
			BindingResult bindingResult){
		JsonResponse res = new JsonResponse();
		List<CorporateTariffModel> corporateTariffModelList=null;
		try{
			corporateTariffModelList= corporateTariffService.getCommonTariffParameter_TariffParameterMaster(corporateTariffModel);
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setCorporateTariffModel(corporateTariffModelList!=null && corporateTariffModelList.size()>0?corporateTariffModelList.get(0):null);
		}
		return res;
	}

	
	@RequestMapping(value="/getCorporateTariff",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getCorporateTariff(
			@ModelAttribute(value="corporateTariffModel") CorporateTariffModel corporateTariffModel,
			BindingResult bindingResult,
			@RequestParam("corporateId") Long corporateId){
		JsonResponse res = new JsonResponse();
		List<CorporateTariffModel> corporateTariffModels = null;
		try{
			corporateTariffModels = corporateTariffService.getCorporateTariff(corporateId);
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			if(corporateTariffModels.size() >0){
				corporateTariffModel = corporateTariffModels.get(0);
				res.setCorporateTariffModel(corporateTariffModel);
				}
			}
			
		return res;
	}
	
	@RequestMapping(value="/getCorporateFuelHikeDate",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getCorporateFuelHikeDate(
			@ModelAttribute(value="corporateTariffModel") CorporateTariffModel corporateTariffModel,
			BindingResult bindingResult,@RequestParam("corporateId") Long corporateId, @RequestParam("branchId") Long branchId){
		JsonResponse res = new JsonResponse();
		List<CorporateTariffModel> corporateTariffModels = null;
		try{
			corporateTariffModels = corporateTariffService.getCorporateFuelHikeDate(corporateId, branchId);
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			if(corporateTariffModels.size() >0){
				res.setCorporateTariffModelList(corporateTariffModels);
				}
			}
			
		return res;
	}
	
	@RequestMapping(value="/corporateTariffAsPerDate",method=RequestMethod.POST)
	public @ResponseBody JsonResponse corporateTariffAsPerDate(
			@RequestParam("corpTariffDateId") Long corpTariffDateId){
		JsonResponse res = new JsonResponse();
		List<GeneralMasterModel> generalMasterModelList = null;
		CorporateTariffModel corporateTariffModel = null;
		List<CorporateTariffModel> corporateTariffModels = corporateTariffService.listAsPerDate(corpTariffDateId);
		if(corporateTariffModels!=null && corporateTariffModels.size()>0)
			corporateTariffModel = corporateTariffModels.get(0);
		try{
			 if(corporateTariffModel!= null){
					res.setCorporateTariffDetModelList(corporateTariffModel.getCorporateTariffDetModel());
				    }
			 	res.setCorporateTariffModel(corporateTariffModel);
			 	res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setGeneralMasterModelList(generalMasterModelList);
			res.setDataString1(createTariffGrid(corporateTariffModel.getCorporateId().getId().toString()));
			res.setDataGrid(showGrid(corporateTariffService.list(),corporateTariffModel.getCorporateId().getId().toString()));
		}
		return res;
	}
	
}
