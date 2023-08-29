package com.common.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.master.model.CityMasterModel;
import com.master.model.DistrictMasterModel;
import com.master.model.GeneralMasterModel;
import com.master.model.StateMasterModel;
import com.operation.model.CarDetailModel;
import com.operation.model.MaintenanceDetailModel;
import com.util.JsonResponse;
import com.util.Message;
import com.util.Utilities;
import com.common.global.CommonJspPage;
import com.common.model.AddressDetailModel;
import com.common.model.ContactDetailModel;
import com.common.service.MasterDataService;

@Controller
public class CommonJspController {
	private static final Logger logger = Logger.getLogger(CommonJspController.class);

	@Autowired
	private MasterDataService masterDataService;

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		//dataBinder.setDisallowedFields(new String[] {"id"});
		dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value="/contactDetailForm",method=RequestMethod.POST)
	public @ResponseBody JsonResponse contactDetailForm(@ModelAttribute(value="contactDetailModel") ContactDetailModel contactDetailModel,BindingResult bindingResult,HttpSession session){
		JsonResponse res = new JsonResponse();
		try{
			res.setDataGrid(CommonJspPage.contactDetailForm(contactDetailModel));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	@RequestMapping(value="/addressDetailForm",method=RequestMethod.POST)
	public @ResponseBody JsonResponse addressDetailForm(@ModelAttribute(value="addressDetailModel") AddressDetailModel addressDetailModel,BindingResult bindingResult,HttpSession session){
		JsonResponse res = new JsonResponse();
		try{
			res.setDataGrid(CommonJspPage.addressDetailForm(addressDetailModel,masterDataService.getStateMasterData()));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value="/maintenanceDetailForm",method=RequestMethod.POST)
	public @ResponseBody JsonResponse maintenanceDetailForm(@ModelAttribute(value="maintenanceDetailModel") MaintenanceDetailModel maintenanceDetailModel,BindingResult bindingResult){
		JsonResponse res = new JsonResponse();
		try{
			res.setDataGrid(CommonJspPage.maintenanceDetailForm(maintenanceDetailModel));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	@RequestMapping(value="/dateTimeDifference",method=RequestMethod.POST)
	public @ResponseBody JsonResponse dateTimeDifference(@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate){
		JsonResponse res = new JsonResponse();
		Object dataArray[]=null;
		try{
			dataArray=(Object[])CommonJspPage.dateTimeDifferenceWithJoda(startDate,endDate); ;
			res.setDataArray(dataArray);
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value="/getStateDistMasterData_usingPincode",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getStateDistMasterData_usingPincode(@RequestParam("pincode") Long pincode){
		JsonResponse res = new JsonResponse();
		List<DistrictMasterModel> districtMasterModels=null;
		try{
			districtMasterModels= masterDataService.getStateDistMasterData_usingPincode(pincode);
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			if(districtMasterModels!=null && districtMasterModels.size()>0)
				res.setDistrictMasterModel(districtMasterModels.get(0));
		}
		return res;
	}

	@RequestMapping(value="/getDistrictData",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getDistrictData(@RequestParam("stateId") Long stateId,
			@RequestParam("distId") Long distId){
		JsonResponse res = new JsonResponse();
		try{
			res.setDistrictMasterModelList(masterDataService.getDistrictMasterData(stateId,distId));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	public StringBuilder districtListBox(List<DistrictMasterModel> districtMasterModels,Long distId){
		String selectedIndex="";
		StringBuilder dataString =	new StringBuilder();
		dataString.append("<SELECT NAME='district' class='form-control' id='district' onchange='getCityData(this.value,0)'>"
				+ "<OPTION VALUE='0' > ---Select--- ");
		for(DistrictMasterModel districtMasterModel:districtMasterModels ){
			if(distId.equals(districtMasterModel.getId()))
				selectedIndex="selected";
			else
				selectedIndex="";
			dataString.append("<OPTION VALUE='"+districtMasterModel.getId()+"' "+selectedIndex+" > "+ districtMasterModel.getName());
		}	
		dataString.append("</SELECT>") ;
		return dataString;
	}

	@RequestMapping(value="/getCityData",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getCityData(@RequestParam("districtId") Long districtId,
			@RequestParam("pincode") Long pincode,@RequestParam("cityId") Long cityId){
		JsonResponse res = new JsonResponse();
		
		try{
			res.setCityMasterModelsList(masterDataService.getCityMasterData(districtId,pincode));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	public StringBuilder cityListBox(List<CityMasterModel> cityMasterModels,Long cityId){
		String selectedIndex="";
		StringBuilder dataString =	new StringBuilder();
		dataString.append("<SELECT NAME='city' id='city' class='form-control'>"
				+ "<OPTION VALUE='0'> ---Select--- ");
		for(CityMasterModel cityMasterModel:cityMasterModels ){
			if(cityId.equals(cityMasterModel.getId()))
				selectedIndex="selected";
			else
				selectedIndex="";
			dataString.append("<OPTION VALUE='"+cityMasterModel.getId()+"' "+selectedIndex+"> "+ cityMasterModel.getName());
		}	
		dataString.append("</SELECT>") ;
		return dataString;
	}

}
