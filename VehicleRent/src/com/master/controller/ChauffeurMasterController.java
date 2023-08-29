package com.master.controller;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.common.service.MasterDataService;
import com.master.model.ChauffeurModel;
import com.master.service.ChauffeurMasterService;
import com.master.validator.ChauffeurMasterValidator;
import com.util.JsonResponse;
import com.util.Message;
import com.util.Utilities;

@Controller
public class ChauffeurMasterController {
	private static final Logger logger = Logger.getLogger(ChauffeurMasterController.class);

	@Autowired
	private ChauffeurMasterService chauffeurMasterService;
	
	@Autowired
	private MasterDataService masterDataService;
	
	private ChauffeurMasterValidator validator = null;

	public ChauffeurMasterValidator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(ChauffeurMasterValidator validator) {
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
	
	@RequestMapping("/pageLoadChauffeur")
	public ModelAndView pageLoadChauffeur(Map<String, Object> map) {
		ModelAndView modelAndView=new ModelAndView("chauffeurMaster");
		try {
			map.put("existingData", masterDataService.getGeneralMasterData_UsingCode("CHAUFFEUR"));
			map.put("vendorList", masterDataService.getGeneralMasterData_UsingCode("VENDOR")); 
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("chauffeurModel", new ChauffeurModel());
			map.put("existingData", showGrid(chauffeurMasterService.list()));
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/saveOrUpdateChauffeur",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateChauffeur(
			@ModelAttribute(value="chauffeurModel") ChauffeurModel chauffeurModel,
			BindingResult bindingResult,
			@RequestParam("idForUpdate") String idForUpdate,
			@RequestParam("stau") String stau,
			@RequestParam(value = "sBase64Image", required = false) String sBase64Image, 
			HttpServletRequest request){
		JsonResponse res = new JsonResponse();
		if(stau.equals("C")){
			validator.validate(chauffeurModel, bindingResult);
		}
		if(bindingResult.hasErrors()){
			res.setStatus("Errors");
			res.setResult(bindingResult.getAllErrors());
			return res;
		}
		
		try{
			if(idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")){
				chauffeurMasterService.save(chauffeurModel);
				res.setResult(Message.SAVE_SUCCESS_MESSAGE);
			}
			else{
				chauffeurModel.setId(Long.valueOf(idForUpdate));
				chauffeurMasterService.update(chauffeurModel);
				res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
			}
			if(sBase64Image != null){
				String sType = "Chauffeur";
				String sFileName = idForUpdate;
				if(sBase64Image.contains("blankImage.jpg")){
					Utilities.copyFile(sFileName, sFileName,sType);
				}else{
					Utilities.ImageWrite(sBase64Image,sFileName,sType);
				}
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
			res.setDataGrid(showGrid(chauffeurMasterService.list()));
		}
		return res;
	}
	
	@RequestMapping(value="/formFillForEditChauffeur",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditChauffeur(
			@RequestParam("formFillForEditId") String formFillForEditId){
		JsonResponse res = new JsonResponse();
		ChauffeurModel chauffeurModel = null;
		try{
			chauffeurModel=chauffeurMasterService.formFillForEdit(Long.parseLong(formFillForEditId));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setChauffeurModel(chauffeurModel);
		}
		return res;
	}
	
	@RequestMapping(value="/deleteChauffeur",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteChauffeur(
			@RequestParam("idForDelete") String idForDelete){
		JsonResponse res = new JsonResponse();
		try{
			chauffeurMasterService.delete(Long.valueOf(idForDelete));
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
			res.setDataGrid(showGrid(chauffeurMasterService.list()));
		}
		return res;
	}
	
	public String showGrid(List<ChauffeurModel> chauffeurModelDataList){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dataString =	
		" <table class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"+
		"	<thead style='background-color: #FED86F;'>		"+
		" 	<tr> 											"+
	  	" 		<th width="+Message.ACTION_SIZE+">Action</th> "+
	  	"   	<th>C.ID</th> 				"+
	  	"   	<th>Chauffeur Name</th> 	"+
	  	"   	<th>Mobile No.</th> 		"+
	  	"   	<th>Vendor Name</th> 		"+
	  	"   	<th>Driving Licence No.</th>"+
	  	"   	<th>Valid Upto</th> 		"+
	  	"   	<th>Authority</th> 			"+
	  	"   	<th>DOJ</th> 				"+
	  	"   	<th>Badge No.</th> 			"+
	  	"   	<th>Blood Group</th> 		"+
	  	"   	<th>Duties Allow</th> 		"+
	  	"   	<th>Driver Status</th> 		"+
	  	"   	<th>Qualification</th> 		"+
	  	"   	<th>Experience</th> 		"+
	  	" 	</tr>							"+
	  	"	</thead>						"+
	  	"   </tbody>						"; 
		for(ChauffeurModel chauffeurModel:chauffeurModelDataList ){
			String sDutiesAllow = chauffeurModel.getDutyAllow().equals("Y")?"Yes":"No";
			String sStatus = chauffeurModel.getStatus().equals("Y")?"Active":"InActive";
			dataString+=" <tr> "+
			" <td> "+
			" 	<a href='javascript:formFillForEdit("+chauffeurModel.getId()+")'>"+Message.EDIT_BUTTON+"</a>"+
			" 	<a href='javascript:viewRecord("+chauffeurModel.getId()+")' >"+Message.VIEW_BUTTON+"</a> "+ 				
			" 	<a href='javascript:deleteRecord("+chauffeurModel.getId()+")' >"+Message.DELETE_BUTTON+"</a> "+ 				
			" </td>"+
			" <td>"+ chauffeurModel.getId()+ "</td>   "+
			" <td>"+ chauffeurModel.getName()+ "</td>   "+
			" <td>"+ chauffeurModel.getMobileNo()+ "</td>   "+
			" <td>"+ chauffeurModel.getVendorId().getName()+ "</td>   "+
			" <td>"+ chauffeurModel.getDrivingLicence()+ "</td>   "+
			" <td>"+ dateFormat.format(chauffeurModel.getDlValidUpto())+ "</td>   "+
			" <td>"+ chauffeurModel.getDlAuthority()+ "</td>   "+
			" <td>"+ dateFormat.format(chauffeurModel.getDoj())+ "</td>   "+
			" <td>"+ chauffeurModel.getBadgeNo()+ "</td>   "+
			" <td>"+ chauffeurModel.getBloodGrp()+ "</td>   "+
			" <td>"+ sDutiesAllow + "</td>   "+
			" <td>"+ sStatus + "</td>   "+
			" <td>"+ chauffeurModel.getQualification()+ "</td>   "+
			" <td>"+ chauffeurModel.getExperience()+ "</td>   "+
			" </tr>";
		}	
		dataString=dataString+"</tbody></table>";
		return dataString;
	}
	
	@RequestMapping(value="/formFillForEditChauffeurDetails",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditChauffeurDetails(
			@RequestParam("formFillForEditId") String formFillForEditId,
			@RequestParam("formFillForEditMob") String formFillForEditMob){
		JsonResponse res = new JsonResponse();
		ChauffeurModel chauffeurModel = null;
		try{
			chauffeurModel=chauffeurMasterService.formFillForEditChauffeurDetails(Long.parseLong(formFillForEditId), formFillForEditMob);
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setChauffeurModel(chauffeurModel);
		}
		return res;
	}
}
