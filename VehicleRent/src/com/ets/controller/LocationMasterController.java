package com.ets.controller;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.common.service.MasterDataService;
import com.ets.model.CarRateModel;
import com.ets.model.LocationMasterModel;
import com.ets.service.LocationMasterService;
import com.master.controller.GeneralMasterController;
import com.master.model.CityMasterModel;
import com.master.model.GeneralMasterModel;
import com.util.JsonResponse;
import com.util.Message;


@Controller
public class LocationMasterController {
  private static final Logger logger = Logger.getLogger(GeneralMasterController.class);

   @Autowired
   private MasterDataService masterDataService;
   
   @Autowired
   private LocationMasterService locationMasterService;

 
  @InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields(new String[] { "id" });
		dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
  
	@RequestMapping("/pageLoadLocationMaster")
	public ModelAndView pageLoadCorporate(Map<String, Object> map, HttpSession session) {
		ModelAndView modelAndView=new ModelAndView("locationMaster");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try {
			map.put("customerOrCompanyNameIdList", masterDataService.getGeneralMasterData_UsingCode("CORP:"+branchesAssigned));
			map.put("zoneList", masterDataService.getGeneralMasterData_UsingCode("ZONE"));
			map.put("locationDataList",showGrid(0l));
			map.put("carModelRateList",createCarCategoryRateGrid());
			}
		catch(Exception e) {
			logger.error("",e);
		}
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/getCityUsingPinCode", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getCityUsingPinCode(
			@RequestParam("pincode") Long pincode) {
		JsonResponse res = new JsonResponse();
		try {
			List<CityMasterModel> CityMasterModelList =locationMasterService.getCityUsingPinCode(pincode);
			res.setCityMasterModelsList(CityMasterModelList);
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	@RequestMapping(value = "/saveOrUpdateLocationMaster", method = RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateLocationMaster(
			@ModelAttribute(value = "locationMasterModel") LocationMasterModel locationMasterModel,
			@RequestParam("idForUpdate") String idForUpdate,
			@RequestParam("carRateIdsToDelete") String carRateIdsToDelete,
			HttpSession session){
		JsonResponse res = new JsonResponse();
		Long corporateId = 0l;
		try {
			for(CarRateModel carRateModel1:locationMasterModel.getCarRateModel()){
				carRateModel1.setLocationMasterId(locationMasterModel);
			}
			corporateId = locationMasterModel.getCorporateId().getId().longValue();
			if(idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")){
				locationMasterModel.setInsDate(new Date());
				locationMasterService.save(locationMasterModel);
				res.setResult(Message.SAVE_SUCCESS_MESSAGE);
			}
			else{
/*				delete carRateModel entries
				String[] sCarRateIds = carRateIdsToDelete.split(",");
				for(int iLoop = 0; iLoop < sCarRateIds.length ; iLoop++){
					if(!sCarRateIds[iLoop].equals("")) locationMasterService.deleteCarRateModel(Long.valueOf(sCarRateIds[iLoop]));
				}
				if(locationMasterModel.getCarRateModel().size() > 0){
					get carRateModelList as per location id and effective date
					SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd");
					String eDate=dateFormat1.format(locationMasterModel.getCarRateModel().get(0).getEffectiveDate());
					List<CarRateModel> carRateModelList = locationMasterService.getCarRateListAsPerAEffectiveDate(Long.valueOf(idForUpdate), eDate);
					Loop for received carRateModelList, if not found then carRate id should null
					for(CarRateModel carRateModel : locationMasterModel.getCarRateModel()){
						boolean bFound = false;
						for(CarRateModel carRateModelExist : carRateModelList){
							if(carRateModel.getId().longValue() == carRateModelExist.getId().longValue()){
								bFound = true;
								break;
							}
						}
						if(bFound == false){
							carRateModel.setId(null);
						}
					}
				}
*/				
				locationMasterModel.setId(Long.valueOf(idForUpdate));
				locationMasterService.update(locationMasterModel);
				/*delete entries if all set to 0*/
				
				res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
			}
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setDataGrid(showGrid(corporateId));
		}
		return res;
	}

	public String showGrid(Long corporateId){
		List<LocationMasterModel> LocationMasterModelList = locationMasterService.getLocationMaster(corporateId) ;
		String dataString =	"";
		dataString =	
			" <table class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"+
			"	<thead style='background-color: #FED86F;'>		"+
		  	" 	<tr> 											"+
		  	" 		<th width="+Message.ACTION_SIZE+">Action</th> 		  "+
		  	" 		<th>ID</th> 			"+
		  	" 		<th>Corporate Name</th> "+
		  	" 		<th>Branch Name</th> 	"+
		  	" 		<th>Hub</th> 		"+
		  	" 		<th>Zone</th> 	"+
		  	" 		<th>Location</th> 	"+
		  	" 		<th>Kilometer</th> 		"+
		  	" 		<th>City</th> 		"+
		  	" 		<th>User</th> 		"+
		  	" 	</tr>						"+
		  	"	</thead>					"+
		  	"   <tbody>						";
		if(corporateId > 0){
			for(LocationMasterModel locationMasterModel:LocationMasterModelList ){
				String location ="",kms="",sCity="";
				if(locationMasterModel.getLocation()!=null)
					location=locationMasterModel.getLocation().getName();
				if(locationMasterModel.getKms()!=null)
					kms=locationMasterModel.getKms().toString();
				if(locationMasterModel.getCity() != null)
					sCity = locationMasterModel.getCity().getName();
				
				dataString=dataString+" <tr> "+
				" <td>"+
				" 	<a href='javascript:formFillForEditLocationMaster("+locationMasterModel.getId()+")'>"+Message.EDIT_BUTTON+"</a> "+
				" 	<a href='javascript:viewLocationMaster("+locationMasterModel.getId()+")' >"+Message.VIEW_BUTTON+"</a> "+ 				
				" 	<a href='javascript:deleteLocationMaster("+locationMasterModel.getId()+")' >"+Message.DELETE_BUTTON+"</a> "+ 				
				" </td> 														"+
				" 	<td>"+ locationMasterModel.getId()+ "</td>  						"+
				" 	<td>"+ locationMasterModel.getCorporateId().getName()+ "</td>   					"+
				" 	<td>"+ locationMasterModel.getBranch().getName()+ "</td>   			"+
				" 	<td>"+ locationMasterModel.getOutlet().getName()+ "</td>   				"+
				" 	<td>"+ locationMasterModel.getZone().getName()+ "</td>   "+
				" 	<td>"+ location+ "</td>   				"+
				" 	<td>"+ kms + "</td> 				"+
				" 	<td>"+ sCity+ "</td>   		"+
				" 	<td>"+ locationMasterModel.getUserId().getUserFirstName() + "</td>   		"+
				" </tr> 		";
			}	
		}
		dataString=dataString+"</tbody></table>";
		return dataString;
	}
	

	@RequestMapping(value="/formFillForEditLocationMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditLocationMaster(
			@RequestParam("formFillForEditId") String formFillForEditId){
		JsonResponse res = new JsonResponse();
		try{
			LocationMasterModel locationMasterModel = locationMasterService.formFillForEditLocationMaster(Long.parseLong(formFillForEditId));
			res.setStatus("Success");
			res.setLocationMasterModel(locationMasterModel);
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		
		return res;
	}
	
	@RequestMapping(value="/deleteLocationMaster",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteLocationMaster(
			@RequestParam("idForDelete") String idForDelete){
		JsonResponse res = new JsonResponse();
		Long corporateId = 0l;
		try{
			LocationMasterModel locationMasterModel = locationMasterService.formFillForEditLocationMaster(Long.parseLong(idForDelete));
			corporateId = locationMasterModel.getCorporateId().getId().longValue();
			locationMasterService.delete(Long.valueOf(idForDelete));
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
			res.setDataGrid(showGrid(corporateId));
		}
		return res;
	}
	
	public String createCarCategoryRateGrid(){	
		List <GeneralMasterModel> carCatList   = masterDataService.getGeneralMasterData_UsingCode("VCT"); 					
		String dataString =	"";
		dataString += "<table class='table table-bordered table-striped table-hover dataTable' style='height:200px;overflow:auto' id='userTable1'>" +
					  "	<thead style='background-color: #FED86F;'> " +
					  " <tr> " +
					  "		<th width=10% align='center'><label>Car Category</label></th> " +
					  "		<th width=8% align='center'><label>One Way [Company]</label></th> " +
					  "		<th width=8% align='center'><label>One Way [Vendor]</label></th> " +
					  "		<th width=8% align='center'><label>Two Way [Company]</label></th> "+
					  "		<th width=8% align='center'><label>Two Way [Vendor]</label></th> "+
					  " </tr> " +
					  " </thead> " +
					  " <tbody> ";
	  	for(GeneralMasterModel carCat : carCatList){
	  		dataString += "<tr> " +
	  					  "		<td><label class='carCategory' id='carCategory_"+carCat.getId()+"'>"+carCat.getName()+"</label></td> "+
	  					  "		<td><input type='text' class='oneWayCompany'  id='oneWayCompany_"+carCat.getId()+"'name='seaterList' maxlength='5' style=' width: 100%'  onkeypress='isDouble(event,this)'></td> "+
	  					  "		<td><input type='text' class='oneWayVendor'  id='oneWayVendor_"+carCat.getId()+"' maxlength='5' style=' width: 100%'  onkeypress='isDouble(event,this)'></td> "+
				 	  	  "		<td><input type='text' class='twoWayCompany' id ='twoWayCompany_"+carCat.getId()+"' style='width:100%' value='' maxlength='5' onkeypress='isDouble(event,this)'/> " +
				 	  	  "	   		<input type = 'hidden' class='carRateId' id ='carRateId_"+carCat.getId()+"' value='' /> " +
				 	  	  "		</td> "+
				 	  	  "		<td><input type='text' class='twoWayVendor'  id='twoWayVendor_"+carCat.getId()+"'maxlength='5' style=' width: 100%'  onkeypress='isDouble(event,this)'></td> "+
	  		              "</tr>";	
	  	}
	  	dataString += "</tbody> " +						
	  			      "</table>";
	  	return dataString;
	}
	
	@RequestMapping(value = "/getCarRate", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getCarRate(
			@RequestParam("locationId") Long locationId) {
		JsonResponse res = new JsonResponse();
		try {
			List<CarRateModel> carRateModelList =locationMasterService.getCarRate(locationId);
			res.setCarRateModelList(carRateModelList);
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	@RequestMapping(value = "/getLocationAsCorporate", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getLocationAsCorporate(
			@RequestParam("corporateId") Long corporateId) {
		JsonResponse res = new JsonResponse();
		try {
			res.setDataGrid(showGrid(corporateId));
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	@RequestMapping(value = "/getZoneAsBranch", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getZoneAsBranch(
			@RequestParam("branchId") Long branchId) {
		JsonResponse res = new JsonResponse();
		try {
			List<GeneralMasterModel> zoneList =locationMasterService.getZoneAsBranch(branchId);
			res.setGeneralMasterModelList(zoneList);
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

}
