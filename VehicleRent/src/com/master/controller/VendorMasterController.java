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
import com.master.model.VendorModel;
import com.master.service.ChauffeurMasterService;
import com.master.service.VendorMasterService;
import com.master.validator.VendorMasterValidator;
import com.util.JsonResponse;
import com.util.Message;
import com.util.Utilities;

@Controller
public class VendorMasterController {
	private static final Logger logger = Logger.getLogger(VendorMasterController.class);

	@Autowired
	private VendorMasterService vendorMasterService;
	
	@Autowired
	private MasterDataService masterDataService;
	
	private VendorMasterValidator validator = null;

	public VendorMasterValidator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(VendorMasterValidator validator) {
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
	
	@RequestMapping("/pageLoadVendor")
	public ModelAndView pageLoadVendor(Map<String, Object> map) {
		ModelAndView modelAndView=new ModelAndView("vendorMaster");
		try {
			map.put("vendorDataList", masterDataService.getGeneralMasterData_UsingCode("VENDOR")); 
			map.put("organizationList", masterDataService.getGeneralMasterData_UsingCode("ORG")); 
			map.put("stateMasterList", masterDataService.getStateMasterData());
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("vendorModel", new VendorModel());
			map.put("existingData", showGrid(vendorMasterService.list()));
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/saveOrUpdateVendor",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateVendor(
			@ModelAttribute(value="vendorModel") VendorModel vendorModel,
			BindingResult bindingResult,
			@RequestParam("idForUpdate") String idForUpdate, 
			@RequestParam(value = "photo", required = false) CommonsMultipartFile photoFile, HttpServletRequest request){
		JsonResponse res = new JsonResponse();
		validator.validate(vendorModel, bindingResult);
		if(bindingResult.hasErrors()){
			res.setStatus("Errors");
			res.setResult(bindingResult.getAllErrors());
			return res;
		}

		try {
			MultipartFile file = photoFile;

			if(file != null){
				if (file.getSize() > 0) {
					String fileName = null;
					InputStream inputStream = null;
					OutputStream outputStream = null;
					inputStream = file.getInputStream();
					fileName = request.getRealPath("") + "/img/Vendor/" + vendorModel.getName();
					outputStream = new FileOutputStream(fileName);
					int readBytes = 0;
					byte[] buffer = new byte[10000];
					while ((readBytes = inputStream.read(buffer, 0, 10000)) != -1) {
						outputStream.write(buffer, 0, readBytes);
					}
					outputStream.close();
					inputStream.close();
				}
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		
		try{
			if(idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")){
				vendorMasterService.save(vendorModel);
				res.setDataString2(vendorModel.getId().toString());
				res.setResult(Message.SAVE_SUCCESS_MESSAGE);
			}
			else{
				vendorModel.setId(Long.valueOf(idForUpdate));
				vendorMasterService.update(vendorModel);
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
			res.setDataGrid(showGrid(vendorMasterService.list()));
		}
		return res;
	}
	
	@RequestMapping(value="/formFillForEditVendor",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditVendor(
			@RequestParam("formFillForEditId") String formFillForEditId){
		JsonResponse res = new JsonResponse();
		VendorModel vendorModel = null;
		try{
			vendorModel=vendorMasterService.formFillForEdit(Long.valueOf(formFillForEditId));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setVendorModel(vendorModel);
		}
		return res;
	}
	
	@RequestMapping(value="/deleteVendor",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteVendor(
			@RequestParam("idForDelete") String idForDelete){
		JsonResponse res = new JsonResponse();
		try{
			vendorMasterService.delete(Long.valueOf(idForDelete));
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
			res.setDataGrid(showGrid(vendorMasterService.list()));
		}
		return res;
	}
	
	public String showGrid(List<VendorModel> vendorModelDataList){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dataString =	
		" <table class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"+
		"	<thead style='background-color: #FED86F;'>		"+
		" 	<tr> 											"+
	  	" 		<th width="+Message.ACTION_SIZE+">Action</th> "+
	  	"   	<th>ID</th> 				"+
	  	"   	<th>Vendor Name</th> 		"+
	  	"   	<th>PAN No.</th> 			"+
	  	"   	<th>Service Tax No.</th> 	"+
	  	"   	<th>TAN No.</th> 			"+
	  	"   	<th>LST No.</th> 			"+
	  	"   	<th>Organization Type</th> 	"+
	  	"   	<th>Helpline No.</th> 		"+
	  	"   	<th>Contact Person</th> 	"+
	  	"   	<th>Mobile No.</th> 		"+
	  	"   	<th>D.O.A.</th> 			"+
	  	" 	</tr>							"+
	  	"	</thead>						"+
	  	"   </tbody>						"; 
		for(VendorModel vendorModel:vendorModelDataList ){
			String eidtId = (vendorModel.getId()+"/"+vendorModel.getContPersonMobile());
			dataString+=" <tr> "+
			" <td> "+
			" 	<a href='javascript:formFillForEdit(\\\""+eidtId+"\\\")'>"+Message.EDIT_BUTTON+"</a>"+
			" 	<a href='javascript:viewRecord(\\\""+eidtId+"\\\")' >"+Message.VIEW_BUTTON+"</a> "+ 				
			" 	<a href='javascript:deleteRecord("+vendorModel.getId()+")' >"+Message.DELETE_BUTTON+"</a> "+ 				
			" </td>"+
			" <td>"+ vendorModel.getId()+ "</td>   "+
			" <td>"+ vendorModel.getName()+ "</td>   "+
			" <td>"+ vendorModel.getPan()+ "</td>   "+
			" <td>"+ vendorModel.getsTaxNo()+ "</td>   "+
			" <td>"+ vendorModel.getTan()+ "</td>   "+
			" <td>"+ vendorModel.getLstno()+ "</td>   "+
			" <td>"+ vendorModel.getOrgTypeId().getName()+ "</td>   "+
			" <td>"+ vendorModel.getHelpLineNo()+ "</td>   "+
			" <td>"+ vendorModel.getContPerson()+ "</td>   "+
			" <td>"+ vendorModel.getContPersonMobile()+ "</td>   "+
			" <td>"+ dateFormat.format(vendorModel.getAgDate())+ "</td>   "+
			" </tr>";
		}	
		dataString=dataString+"</tbody></table>";
		return dataString;
	}
}
