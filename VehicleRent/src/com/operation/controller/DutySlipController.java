package com.operation.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.billing.controller.BillingController;
import com.billing.service.BillingService;
import com.common.service.MasterDataService;
import com.corporate.model.CorporateModel;
import com.corporate.service.CorporateService;
import com.master.model.CarAllocationModel;
import com.master.model.CorporateTariffDetModel;
import com.master.model.CorporateTariffModel;
import com.master.model.GeneralMasterModel;
import com.master.model.TariffSchemeParaModel;
import com.master.service.CorporateTariffService;
import com.operation.model.BookingDetailModel;
import com.operation.model.BookingMasterModel;
import com.operation.model.CarDetailModel;
import com.operation.model.DutySlipModel;
import com.operation.service.BookingMasterService;
import com.operation.service.DutySlipService;
import com.operation.validator.DutySlipValidator;
import com.operation.viewModel.BookingDetailsWithVehicleAllocationModel;
import com.operation.viewModel.DutySlipReceiveViewModel;
import com.user.model.UserModel;
import com.util.JsonResponse;
import com.util.Message;

@Controller
@SessionAttributes({"dutySlip_ReceivedByList"})
public class DutySlipController {
	private static final Logger logger = Logger.getLogger(DutySlipController.class);
	SimpleDateFormat dateFormat;
	SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dateFormatYear = new SimpleDateFormat("yyyy");
	
	String finalInvoiceNo = null;
	String invoiceNo = null;
	@Autowired
	private DutySlipService dutySlipService;
	
	@Autowired
	private MasterDataService masterDataService;
	
	@Autowired
	private CorporateTariffService corporateTariffService;
	
	@Autowired
	private CorporateService corporateService;
	
	@Autowired
	private BookingMasterService bookingMasterService;

	@Autowired
	private BillingService billingService;

	private DutySlipValidator validator = null;

	public DutySlipValidator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(DutySlipValidator validator) {
		this.validator = validator;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields(new String[] {"id"});
		dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
		dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
	@RequestMapping("/pageLoadDutySlip/{pageNumber}")
	public ModelAndView pageLoadDutySlip(Map<String, Object> map,@PathVariable("pageNumber") int pageNumber) {
		ModelAndView modelAndView=new ModelAndView("dutySlip");
		try {
			map.put("ownerTypeIdList",masterDataService.getGeneralMasterData_UsingCode("VOT")); 	// Select Vehicle Owner Type
			map.put("driverUserIdList",masterDataService.getUserDara_UsingCode("VUT","Driver"));  	// Select Vehicle Driver User Type
			map.put("dutySlipDataList",showGrid(dutySlipService.list( pageNumber), pageNumber));
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("dutySlipModel", new DutySlipModel());
			map.put("pageNumber",pageNumber);
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/saveOrUpdateDutySlip",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateDutySlip(
			@ModelAttribute(value="dutySlipModel") DutySlipModel dutySlipModel,
			BindingResult bindingResult ,
			@RequestParam("pageNumber") String pageNumber,HttpSession session){
		JsonResponse res = new JsonResponse();
		try{
			validator.validate(dutySlipModel, bindingResult);
			if(!bindingResult.hasErrors()){
				if(session.getAttribute("loginUserId")!=null){
					Long loginUserId=(Long) session.getAttribute("loginUserId");
					dutySlipModel.setDutySlipCreatedBy(loginUserId);
				}
				dutySlipModel.setDutySlipCreatedByDate(new Date());
				dutySlipService.save(dutySlipModel);
				res.setResult(Message.SAVE_SUCCESS_MESSAGE);
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
			res.setDataGrid(showGrid(dutySlipService.list( Integer.parseInt(pageNumber)), Integer.parseInt(pageNumber)));
		}
		return res;
	}
	
	@RequestMapping(value="/formFillForEditDutySlip",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditDutySlip(
			@ModelAttribute(value="dutySlipModel") DutySlipModel dutySlipModel,
			BindingResult bindingResult ,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("formFillForEditId") String formFillForEditId){
		JsonResponse res = new JsonResponse();
		try{
			dutySlipModel=dutySlipService.formFillForEdit(Long.parseLong(formFillForEditId));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setDataGrid(showGrid(dutySlipService.list( pageNumber), pageNumber));
			//res.setDutySlipModel(dutySlipModel);
		}
		return res;
	}
	
	@RequestMapping(value="/deleteDutySlip",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteDutySlip(
			@ModelAttribute(value="dutySlipModel") DutySlipModel dutySlipModel,
			BindingResult bindingResult ,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("idForDelete") String idForDelete){
		JsonResponse res = new JsonResponse();
		try{
			dutySlipService.delete(Long.valueOf(idForDelete));
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
			res.setDataGrid(showGrid(dutySlipService.list( pageNumber), pageNumber));
		}
		return res;
	}
	
	public String showGrid(List<BookingDetailsWithVehicleAllocationModel> bookingDetailsWithVehicleAllocationModelList,int pageNumber){
		String dataString =	
		" <table align='center' width='100%' id='dataTable' border='1'> 	"+
		  	" <tr> 																"+
		  	" <th align='center'><b><input type'text' id='select'  name='select'  value='Select' 		readonly tabindex='-1' style='text-align:center' size='5' ></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Customer Name' readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Car Type'		readonly tabindex='-1' style='text-align:center' size='16'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Tariff Type'	readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Car Name'		readonly tabindex='-1' style='text-align:center' size='12'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Car No.'		readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Used By'		readonly tabindex='-1' style='text-align:center' size='18' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Booking By'	readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" </tr>"; 
			for(BookingDetailsWithVehicleAllocationModel bookingDetailsWithVehicleAllocationModel:bookingDetailsWithVehicleAllocationModelList ){
				dataString=dataString+" <tr style='height:20px;'> "+
				" <td align='center'><input onclick='getDataOnBookingSelection_DutySlip("+bookingDetailsWithVehicleAllocationModel.getBookingDetailId()+")' type='radio' id='bookingDetailId' name='bookingDetailId' value='"+bookingDetailsWithVehicleAllocationModel.getBookingDetailId()+"'></td>"+
				" <td align='center'>"+ bookingDetailsWithVehicleAllocationModel.getCustomerName()+ "</td>   "+
				" <td align='center'>"+ bookingDetailsWithVehicleAllocationModel.getCarType()+ "</td>   "+
				" <td align='center'>"+ bookingDetailsWithVehicleAllocationModel.getTariffType()+ "</td>   "+
				" <td align='center'>"+ bookingDetailsWithVehicleAllocationModel.getCarName()+ "</td>   "+
				" <td align='center'>"+ bookingDetailsWithVehicleAllocationModel.getCarNo()+ "</td>   "+
				" <td align='center'>"+ bookingDetailsWithVehicleAllocationModel.getUsedBy()+ "</td>   "+
				" <td align='center'>"+ bookingDetailsWithVehicleAllocationModel.getBookingBy()+ "</td>   "+
				" </tr>";
			}	
		dataString=dataString+"</table>";
		return dataString;
	}

	@RequestMapping(value="/getBookingData_DutySlip",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getBookingData_DutySlip(
			@ModelAttribute(value="dutySlipModel") DutySlipModel dutySlipModel,
			BindingResult bindingResult ,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("searchCriteria") String searchCriteria){
		JsonResponse res = new JsonResponse();
		try{
			res.setDataGrid(showGrid(dutySlipService.listMasterWise(pageNumber, searchCriteria),pageNumber));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value="/getVehicleTypeData_DutySlip",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getVehicleTypeData_DutySlip(
			@ModelAttribute(value="dutySlipModel") DutySlipModel dutySlipModel,
			BindingResult bindingResult,
			@RequestParam("carTypeId") String carTypeId,
			@RequestParam("searchCriteria") String searchCriteria){
		JsonResponse res = new JsonResponse();
		try{
			//res.setDataGrid(showVehicleDataGrid(dutySlipService.getVehicleTypeData(Long.parseLong(carTypeId),searchCriteria,dutySlipModel.getBookingId().getId())));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	
	public String showVehicleDataGrid(List<CarDetailModel> carDetailModelList){
		String dataString =	
		" <table align='center' width='100%' id='dataTable5' border='1'> 	"+
		  	" <tr> 																"+
		  	" <th align='center'><b><input type'text' id='select' name='select' style='width:100%;text-align:center' readonly tabindex='-1' size='4' value='Select'></b></th> 			"+
		  	" <th align='center'><b><input type'text' id='heading' style='width:100%;text-align:center' name='heading' readonly tabindex='-1' value='RegistrationNo'></b></th> 			"+
		  	" <th align='center'><b><input type'text' id='heading' style='width:100%;text-align:center' name='heading' readonly tabindex='-1' value='BodyColor'></b></th> 			"+
		  	" <th align='center'><b><input type'text' id='heading' style='width:100%;text-align:center' name='heading' readonly tabindex='-1' value='Make'></b></th> 			"+
		  	" <th align='center'><b><input type'text' id='heading' style='width:100%;text-align:center' name='heading' readonly tabindex='-1' value='Model '></b></th> 			"+
		  	" </tr>"; 
			for(CarDetailModel carDetailModel:carDetailModelList ){
				dataString=dataString+" <tr style='height:20px;'> "+
				" <td align='center'><input type='checkBox' id='vehicleId' name='vehicleId' class='vehicleId' value='"+carDetailModel.getId()+"'></td>"+
				" <td align='center'>"+ carDetailModel.getRegistrationNo()+ "</td>   "+
				" <td align='center'>"+ carDetailModel.getBodyColor().getName()+ "</td> "+
				" <td align='center'>"+ carDetailModel.getMake().getName()+ "</td> "+
				" <td align='center'>"+ carDetailModel.getModel().getName()+ "</td> "+
				" </tr>";
			}	
			if(carDetailModelList!=null && carDetailModelList.size()==0){
				dataString=dataString+" <tr style='height:20px;'><td align='center' colspan=5><B>NO DATA FOUND</B></td></tr>";
			}
		dataString=dataString+"</table>";
		return dataString;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/getDataOnBookingSelection_DutySlip",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getDataOnBookingSelection_DutySlip(@RequestParam("bookingDetailId") String bookingDetailId){
		JsonResponse res = new JsonResponse();
		List dutySlipModelList=null;
		Object dataArray[]=null;
		try{
			dutySlipModelList= dutySlipService.getDataOnBookingSelection_DutySlip(Long.parseLong(bookingDetailId));
			if(dutySlipModelList!=null && dutySlipModelList.size()>0)
				dataArray=(Object[]) dutySlipModelList.get(0);
			res.setDataArray(dataArray);
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			//res.setDataGrid(carCodeListBox(carDetailModelList,fieldDefaultSelectedIndex).toString());
		}
		return res;
	}


	/* ___________________________________________________________________________________________			
	 *     Duty Slip Received Functionality Start
	 * ___________________________________________________________________________________________
	 */
	
	@RequestMapping("/pageLoadDutySlipReceive/{pageNumber}")
	public ModelAndView pageLoadDutySlipReceive(Map<String, Object> map,@PathVariable("pageNumber") int pageNumber,HttpSession session) {
		ModelAndView modelAndView=new ModelAndView("dutySlipReceive");
		try {
			session.setAttribute("dutySlip_ReceivedByList",masterDataService.getUserDara_UsingCode("VUT","Cust"));  	// Select Vehicle Customer User Type
			map.put("dutySlipReceiveDataList",showGridDutySlipReceive(dutySlipService.listDutySlipReceive( pageNumber,null), pageNumber,session));
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("dutySlipModel", new DutySlipModel());
			map.put("pageNumber",pageNumber);
		}
		return modelAndView;
	}

	public String showGridDutySlipReceive(List<DutySlipReceiveViewModel> dutySlipReceiveViewModelList,int pageNumber,HttpSession session){
		List<UserModel> userModelList=null;
		if(session.getAttribute("dutySlip_ReceivedByList")!=null){
			userModelList=(List<UserModel>) (session.getAttribute("dutySlip_ReceivedByList")!=null?session.getAttribute("dutySlip_ReceivedByList"):new ArrayList<UserModel>());
		}
		String dataString =	
		" <table align='center' width='100%' id='dataTable' border='1'> 	"+
		  	" <tr> 																"+
		  	" <th align='center'><b><input type'text' id='select'  name='select'  value='Select' 			readonly tabindex='-1' style='text-align:center' size='5' ></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Toll Tax Amount'	readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Parking Amount'	readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Received By'		readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Hub' 				readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Car No'			readonly tabindex='-1' style='text-align:center' size='16'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Car Status'		readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Alloted Car'		readonly tabindex='-1' style='text-align:center' size='12'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Booked Car'		readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Duty Slip Date'	readonly tabindex='-1' style='text-align:center' size='18' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Duty Slip No.'		readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Plan'				readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Corporate'			readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Booked By'			readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Used By'			readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Mobile No.'		readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" </tr>"; 
			for(DutySlipReceiveViewModel dutySlipReceiveViewModel:dutySlipReceiveViewModelList ){
				dataString=dataString+" <tr style='height:20px;'> "+
				" <td align='center'><input type='checkBox' class='dutySlipId' id='dutySlipId' name='dutySlipId' value='"+dutySlipReceiveViewModel.getDutySlipId()+"'></td>"+
				" <td align='center'><input type='text' id='tollTaxAmount"+dutySlipReceiveViewModel.getDutySlipId()+"' name='tollTaxAmount"+dutySlipReceiveViewModel.getDutySlipId()+"' maxlength=15></td>"+
				" <td align='center'><input type='text' id='parkingAmount"+dutySlipReceiveViewModel.getDutySlipId()+"' name='parkingAmount"+dutySlipReceiveViewModel.getDutySlipId()+"' maxlength=15></td>"+
				" <td align='center'> <Select NAME='receivedBy"+dutySlipReceiveViewModel.getDutySlipId()+"'  id='receivedBy"+dutySlipReceiveViewModel.getDutySlipId()+"' style='width:100%' ><Option value=''>-----select------</Option>"; 
					for(UserModel userModel:userModelList){
						dataString=dataString+"<Option value='"+userModel.getId()+"'>"+userModel.getUserName()+"</Option>"; 
					}
				dataString=dataString+"</Select> </td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getHub()+ 			"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getCarNo()+ 		"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getDeviceStatus()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getAllotedCar()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getBookedCar()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getDutySlipDate()+ "</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getDutySlipNo()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getPlan()+ 		"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getCorporate()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getBookedBy()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getUsedBy()+ 		"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getMobileNo()+ 	"</td>"+
				" </tr>";
			}	
		dataString=dataString+"</table>";
		return dataString;
	}
	
	@RequestMapping(value="/saveOrUpdateDutySlipReceive",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateDutySlipReceive(@RequestParam("pageNumber") String pageNumber,
			@RequestParam("dutySlipReceiveId") String dutySlipReceiveId,
			@RequestParam("tollTaxAmount") String tollTaxAmount,
			@RequestParam("parkingAmount") String parkingAmount,
			@RequestParam("receivedBy") String receivedBy,HttpSession session){
		JsonResponse res = new JsonResponse();
		List<DutySlipModel> dutySlipModels=new ArrayList<DutySlipModel>();
		try{
			String dutySlipId[]=dutySlipReceiveId.split(",");
			String tollTaxAmountArray[]=tollTaxAmount.split(",");
			String parkingAmountArray[]=parkingAmount.split(",");
			String receivedByArray[]=receivedBy.split(",");
			for(int i=0;i<dutySlipId.length;i++){
				dutySlipModels.add(new DutySlipModel(Long.parseLong(dutySlipId[i]),Double.parseDouble(tollTaxAmountArray[i]),Double.parseDouble(parkingAmountArray[i]),Long.parseLong(receivedByArray[i]),null));
			}
			dutySlipService.saveDutySlipReceive(dutySlipModels);
			res.setResult(Message.SAVE_SUCCESS_MESSAGE);
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
		finally{
			res.setDataGrid(showGridDutySlipReceive(dutySlipService.listDutySlipReceive( Integer.parseInt(pageNumber),null), Integer.parseInt(pageNumber),session));
		}
		return res;
	}

	@RequestMapping(value="/getDutySlipData_DutySlipReceive",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getDutySlipData_DutySlipReceive(
			@ModelAttribute(value="dutySlipModel") DutySlipModel dutySlipModel,
			BindingResult bindingResult ,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("searchCriteria") String searchCriteria,HttpSession session){
		JsonResponse res = new JsonResponse();
		try{
			res.setDataGrid(showGridDutySlipReceive(dutySlipService.listDutySlipReceive(pageNumber,searchCriteria), pageNumber,session));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	/* ___________________________________________________________________________________________			
	 *     Duty Slip Closed Functionality Start
	 * ___________________________________________________________________________________________
	 */
	
	@RequestMapping("/pageLoadDutySlipClose/{pageNumber}")
	public ModelAndView pageLoadDutySlipClose(Map<String, Object> map,@PathVariable("pageNumber") int pageNumber) {
		ModelAndView modelAndView=new ModelAndView("dutySlipClose");
		try {
			map.put("tariffIdList",masterDataService.getGeneralMasterData_UsingCode("VTF")); 		// Select Vehicle Tariff
			map.put("dutySlipCloseDataList",showGridDutySlipClose(dutySlipService.listDutySlipClose( pageNumber,null), pageNumber));
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("dutySlipModel", new DutySlipModel());
			map.put("pageNumber",pageNumber);
		}
		return modelAndView;
	}

	public String showGridDutySlipClose(List<DutySlipReceiveViewModel> dutySlipReceiveViewModelList,int pageNumber){
		String dataString =	
		" <table align='center' width='100%' id='dataTable' border='1'> 	"+
		  	" <tr> 																"+
		  	" <th align='center'><b><input type'text' id='select'  name='select'  value='Select' 			readonly tabindex='-1' style='text-align:center' size='5' ></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Hub' 				readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Car No'			readonly tabindex='-1' style='text-align:center' size='16'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Car Status'		readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Alloted Car'		readonly tabindex='-1' style='text-align:center' size='12'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Booked Car'		readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Duty Slip Date'	readonly tabindex='-1' style='text-align:center' size='18' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Duty Slip No.'		readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Plan'				readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Corporate'			readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Booked By'			readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Used By'			readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Mobile No.'		readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" </tr>"; 
			for(DutySlipReceiveViewModel dutySlipReceiveViewModel:dutySlipReceiveViewModelList ){
				dataString=dataString+" <tr style='height:20px;'> "+
				" <td align='center'><input type='radio' class='dutySlipId' id='dutySlipId' name='dutySlipId' value='"+dutySlipReceiveViewModel.getDutySlipId()+"' onclick='getDataOnSelection_DutySlipClose(this.value)'></td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getHub()+ 			"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getCarNo()+ 		"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getDeviceStatus()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getAllotedCar()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getBookedCar()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getDutySlipDate()+ "</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getDutySlipNo()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getPlan()+ 		"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getCorporate()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getBookedBy()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getUsedBy()+ 		"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getMobileNo()+ 	"</td>"+
				" </tr>";
			}	
		dataString=dataString+"</table>";
		return dataString;
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/getDataOnSelection_DutySlipClose",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getDataOnSelection_DutySlipClose(@RequestParam("dutySlipId") String dutySlipId){
		JsonResponse res = new JsonResponse();
		List dutySlipModelList=null;
		Object dataArray[]=null;
		List<CorporateTariffModel> tariffParameterMasterModels=null;
		try{
			tariffParameterMasterModels= masterDataService.selectTariffOnTheBasisOfDutySlipId(dutySlipId);
			dutySlipModelList= dutySlipService.getDataOnSelection_DutySlipClose(Long.parseLong(dutySlipId));
			if(dutySlipModelList!=null && dutySlipModelList.size()>0)
				dataArray=(Object[]) dutySlipModelList.get(0);
			res.setDataArray(dataArray);
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			if(tariffParameterMasterModels!=null && tariffParameterMasterModels.size()>0)
				res.setDataGrid(tariffListBox(tariffParameterMasterModels,Integer.parseInt(dataArray[5].toString())).toString());
			else 
				res.setDataGrid("");
		}
		return res;
	}
	
	@RequestMapping(value="/saveOrUpdateDutySlipClose",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateDutySlipClose(@ModelAttribute(value="dutySlipModel") DutySlipModel dutySlipModel,
			BindingResult bindingResult ,
			@RequestParam("pageNumber") String pageNumber,
			@RequestParam("dutySlipId") String dutySlipId,HttpSession session){
		JsonResponse res = new JsonResponse();
		try{
			dutySlipModel.setId(Long.parseLong(dutySlipId));
			if(session.getAttribute("loginUserId")!=null){
				Long loginUserId=(Long) session.getAttribute("loginUserId");
				dutySlipModel.setDutySlipClosedBy(loginUserId);
			}
			dutySlipService.saveDutySlipClose(dutySlipModel);
			res.setResult(Message.SAVE_SUCCESS_MESSAGE);
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
		finally{
			res.setDataGrid(showGridDutySlipClose(dutySlipService.listDutySlipClose( Integer.parseInt(pageNumber),null), Integer.parseInt(pageNumber)));
		}
		return res;
	}
	
	@RequestMapping(value="/getDutySlipData_DutySlipClose",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getDutySlipData_DutySlipClose(
			@ModelAttribute(value="dutySlipModel") DutySlipModel dutySlipModel,
			BindingResult bindingResult ,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("searchCriteria") String searchCriteria,HttpSession session){
		JsonResponse res = new JsonResponse();
		try{
			res.setDataGrid(showGridDutySlipClose(dutySlipService.listDutySlipClose(pageNumber,searchCriteria), pageNumber));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value="/calculateFare",method=RequestMethod.POST)
	public @ResponseBody JsonResponse calculateFare(@RequestParam("kms") String kms,
			@RequestParam("days") String days,
			@RequestParam("hours") String hours,
			@RequestParam("tariff") String tariff){
		JsonResponse res = new JsonResponse();
		Object object[]=null;
		Object dataArray[]=new Object[2];
		Integer totalhours=0;
		Double nightDetention=0D;
		Double totalFare=0D;
		try{
			List list=dutySlipService.calculateFare(kms,days,hours,tariff);
			Double hoursCeil=Math.ceil(Double.parseDouble(hours.substring(0, 5).replace(":", ".")));
			if(list!=null && list.size()>0){
				object=(Object[])list.get(0);
				if(Integer.parseInt(days)>0){
					totalhours=(int) (hoursCeil + (24*Integer.parseInt(days)));
					nightDetention=Integer.parseInt(days)*Double.parseDouble(object[5].toString());
					if(hoursCeil>12)
						nightDetention=nightDetention+Double.parseDouble(object[5].toString());;
				}
				else{
					totalhours= (int) (hoursCeil+0);
					if(totalhours>12)
						nightDetention=Double.parseDouble(object[5].toString());
				}
				
				Double extraKmsRate=(double) (((int)Double.parseDouble(kms)- (int)Double.parseDouble(object[2].toString())) * ((int)Double.parseDouble(object[4].toString())));   //
				Double extraHoursRate=((totalhours-((int)Double.parseDouble(object[1].toString()))) * Double.parseDouble(object[3].toString()));   //
				Double rate=Double.parseDouble(object[0].toString());
				totalFare= extraKmsRate+extraHoursRate+rate;
			}
			dataArray[0]=totalFare;
			dataArray[1]=nightDetention;
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
	
	
	public StringBuilder tariffListBox(List<CorporateTariffModel> tariffParameterMasterModels,int fieldDefaultSelectedIndex){
		String selectedIndex="";
		StringBuilder dataString =	new StringBuilder();
		dataString.append("<SELECT NAME='tariff' id='tariff'  style='width:99%'>"
				+ "<OPTION VALUE='0' SELECTED > --- Select --- ");
		for(CorporateTariffModel tariffParameterMasterModel:tariffParameterMasterModels ){
			if(fieldDefaultSelectedIndex==tariffParameterMasterModel.getId())
				selectedIndex="selected";
			else
				selectedIndex="";
//			dataString.append("<OPTION VALUE='"+tariffParameterMasterModel.getId()+"' "+selectedIndex+"> "+ tariffParameterMasterModel.getTariffName());
		}	
		dataString.append("</SELECT>") ;
		return dataString;
	}

	
	/* ___________________________________________________________________________________________			
	 *     Duty Slip UnBilled Functionality Start
	 * ___________________________________________________________________________________________
	 */
	
	@RequestMapping("/pageLoadDutySlipUnBilled/{pageNumber}")
	public ModelAndView pageLoadDutySlipUnBilled(Map<String, Object> map,@PathVariable("pageNumber") int pageNumber,HttpSession session) {
		ModelAndView modelAndView=new ModelAndView("dutySlipUnBilled");
		try {
			map.put("corporateIdList",masterDataService.getUserDara_UsingCode("VUT","CUST")); 		// Select Vehicle Customer User Type
			map.put("bookedByIdList",masterDataService.getUserDara_UsingCode("VUT","CUST")); 					// Select Vehicle Customer User Type
			map.put("dutySlipUnBilledDataList",showGridDutySlipUnBilled(dutySlipService.listDutySlipUnBilled( pageNumber,null), pageNumber));
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("dutySlipModel", new DutySlipModel());
			map.put("pageNumber",pageNumber);
		}
		return modelAndView;
	}

	public String showGridDutySlipUnBilled(List<DutySlipReceiveViewModel> dutySlipReceiveViewModelList,int pageNumber){
		String dataString =	
		" <table align='center' width='100%' id='dataTable' border='1'> 	"+
		  	" <tr>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Action' 			readonly tabindex='-1' style='text-align:center' size='6'></b></th> "+
		  	" <th align='center'><b><input type'text' id='select'  name='select'  value='Select' 			readonly tabindex='-1' style='text-align:center' size='5' ></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Toll Tax Amount' 	readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Parking Amount' 	readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Unbilled Amount' 	readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Closed By' 		readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Closed By Date' 	readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Hub'				readonly tabindex='-1' style='text-align:center' size='16'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Car No'			readonly tabindex='-1' style='text-align:center' size='16'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Car Status'		readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Alloted Car'		readonly tabindex='-1' style='text-align:center' size='12'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Booked Car'		readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Duty Slip Date'	readonly tabindex='-1' style='text-align:center' size='18' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Duty Slip No.'		readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Plan'				readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Corporate'			readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Booked By'			readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Used By'			readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Mobile No.'		readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" </tr>"; 
			for(DutySlipReceiveViewModel dutySlipReceiveViewModel:dutySlipReceiveViewModelList ){
				dataString=dataString+" <tr style='height:20px;'> "+
				" <td align='center'> "+
				" <a href='#'> "+
				" 	<img src='./img/editButton.png' border='0' title='Edit' width='16' height='16' />"+ 
				" </a> "+
				" <a href='#' > "+
				" 	<img src='./img/delete_icon.gif' border='0' title='Delete' width='16' height='16'/>"+ 
				" </a> "+
				"</td>"+ 				
				" <td align='center'><input type='checkBox' class='dutySlipId' id='dutySlipId' name='dutySlipId' value='"+dutySlipReceiveViewModel.getDutySlipId()+"'></td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getTollAmount()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getParkingAmount()+"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getUnBilledAmount()+"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getClosedBy()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getClosedByDate()+ "</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getHub()+ 			"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getCarNo()+ 		"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getDeviceStatus()+ "</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getAllotedCar()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getBookedCar()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getDutySlipDate()+ "</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getDutySlipNo()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getPlan()+ 		"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getCorporate()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getBookedBy()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getUsedBy()+ 		"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getMobileNo()+ 	"</td>"+
				" </tr>";
			}	
		dataString=dataString+"</table>";
		return dataString;
	}
	
	@RequestMapping(value="/saveOrUpdateDutySlipUnBilled",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateDutySlipUnBilled(@RequestParam("pageNumber") String pageNumber,
			@RequestParam("dutySlipId") String dutySlipId,HttpSession session){
		JsonResponse res = new JsonResponse();
		List<DutySlipModel> dutySlipModels=new ArrayList<DutySlipModel>();
		try{
			Long loginUserId=null;
			if(session.getAttribute("loginUserId")!=null){
				loginUserId=(Long) session.getAttribute("loginUserId");
			}
			String dutySlipIds[]=dutySlipId.split(",");
			for(int i=0;i<dutySlipIds.length;i++){
				dutySlipModels.add(new DutySlipModel(Long.parseLong(dutySlipIds[i]),null,null,null,loginUserId));
			}
			dutySlipService.saveDutySlipUnBilled(dutySlipModels);
			res.setResult(Message.SAVE_SUCCESS_MESSAGE);
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
		finally{
			res.setDataGrid(showGridDutySlipUnBilled(dutySlipService.listDutySlipUnBilled( Integer.parseInt(pageNumber),null), Integer.parseInt(pageNumber)));
		}
		return res;
	}

	@RequestMapping(value="/getDutySlipData_DutySlipUnBilled",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getDutySlipData_DutySlipUnBilled(
			@ModelAttribute(value="dutySlipModel") DutySlipModel dutySlipModel,
			BindingResult bindingResult ,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("searchCriteria") String searchCriteria,HttpSession session){
		JsonResponse res = new JsonResponse();
		try{
			String searchCriterias[]=searchCriteria.split(",");
			res.setDataGrid(showGridDutySlipUnBilled(dutySlipService.listDutySlipUnBilled(pageNumber,searchCriterias), pageNumber));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	/* ___________________________________________________________________________________________			
	 *     Duty Slip Invoice Functionality Start
	 * ___________________________________________________________________________________________
	 */
	
	@RequestMapping("/pageLoadDutySlipInvoice/{pageNumber}")
	public ModelAndView pageLoadDutySlipInvoice(Map<String, Object> map,@PathVariable("pageNumber") int pageNumber,HttpSession session) {
		ModelAndView modelAndView=new ModelAndView("dutySlipInvoice");
		try {
			map.put("corporateIdList",masterDataService.getUserDara_UsingCode("VUT","CUST")); 		// Select Vehicle Customer User Type
			map.put("bookedByIdList",masterDataService.getUserDara_UsingCode("VUT","CUST")); 		// Select Vehicle Customer User Type
			map.put("dutySlipInvoiceDataList",showGridDutySlipInvoice(dutySlipService.listDutySlipInvoice( pageNumber,null), pageNumber));
		}
		catch(Exception e) {
			logger.error("",e);
		}
		finally{
			map.put("dutySlipModel", new DutySlipModel());
			map.put("pageNumber",pageNumber);
		}
		return modelAndView;
	}

	public String showGridDutySlipInvoice(List<DutySlipReceiveViewModel> dutySlipReceiveViewModelList,int pageNumber){
		String dataString =	
		" <table align='center' width='100%' id='dataTable' border='1'> 	"+
		  	" <tr>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Action' 			readonly tabindex='-1' style='text-align:center' size='6'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Toll Tax Amount' 	readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Parking Amount' 	readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Unbilled Amount' 	readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Closed By' 		readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Closed By Date' 	readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Hub'				readonly tabindex='-1' style='text-align:center' size='16'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Car No'			readonly tabindex='-1' style='text-align:center' size='16'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Car Status'		readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Alloted Car'		readonly tabindex='-1' style='text-align:center' size='12'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Booked Car'		readonly tabindex='-1' style='text-align:center' size='18'></b></th> "+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Duty Slip Date'	readonly tabindex='-1' style='text-align:center' size='18' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Duty Slip No.'		readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Plan'				readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Corporate'			readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Booked By'			readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Used By'			readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" <th align='center'><b><input type'text' id='heading' name='heading' value='Mobile No.'		readonly tabindex='-1' style='text-align:center' size='16' ></b></th>"+
		  	" </tr>"; 
			for(DutySlipReceiveViewModel dutySlipReceiveViewModel:dutySlipReceiveViewModelList ){
				dataString=dataString+" <tr style='height:20px;'> "+
				" <td align='center'> <input type='hidden' class='dutySlipId' id='dutySlipId' name='dutySlipId' value='"+dutySlipReceiveViewModel.getDutySlipId()+"'>"+
				" <a href='#'> "+
				" 	<img src='./img/editButton.png' border='0' title='Edit' width='16' height='16' />"+ 
				" </a> "+
				" <a href='#' > "+
				" 	<img src='./img/delete_icon.gif' border='0' title='Delete' width='16' height='16'/>"+ 
				" </a> "+
				"</td>"+ 				
				" <td align='center'>"+ dutySlipReceiveViewModel.getTollAmount()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getParkingAmount()+"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getUnBilledAmount()+"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getClosedBy()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getClosedByDate()+ "</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getHub()+ 			"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getCarNo()+ 		"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getDeviceStatus()+ "</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getAllotedCar()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getBookedCar()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getDutySlipDate()+ "</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getDutySlipNo()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getPlan()+ 		"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getCorporate()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getBookedBy()+ 	"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getUsedBy()+ 		"</td>"+
				" <td align='center'>"+ dutySlipReceiveViewModel.getMobileNo()+ 	"</td>"+
				" </tr>";
			}	
		dataString=dataString+"</table>";
		return dataString;
	}
	
/*	@RequestMapping(value="/saveOrUpdateDutySlipInvoice",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateDutySlipInvoice(@RequestParam("pageNumber") String pageNumber,
			@RequestParam("dutySlipId") String dutySlipId,HttpSession session){
		JsonResponse res = new JsonResponse();
		List<DutySlipModel> dutySlipModels=new ArrayList<DutySlipModel>();
		try{
			Long loginUserId=null;
			if(session.getAttribute("loginUserId")!=null){
				loginUserId=(Long) session.getAttribute("loginUserId");
			}
			String dutySlipIds[]=dutySlipId.split(",");
			for(int i=0;i<dutySlipIds.length;i++){
				dutySlipModels.add(new DutySlipModel(Long.parseLong(dutySlipIds[i]),null,null,null,loginUserId));
			}
			dutySlipService.saveDutySlipUnBilled(dutySlipModels);
			res.setResult(Message.SAVE_SUCCESS_MESSAGE);
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
		finally{
			res.setDataGrid(showGridDutySlipInvoice(dutySlipService.listDutySlipInvoice( Integer.parseInt(pageNumber),null), Integer.parseInt(pageNumber)));
		}
		return res;
	}
*/
	@RequestMapping(value="/getDutySlipData_DutySlipInvoice",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getDutySlipData_DutySlipInvoice(
			@ModelAttribute(value="dutySlipModel") DutySlipModel dutySlipModel,
			BindingResult bindingResult ,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("searchCriteria") String searchCriteria,HttpSession session){
		JsonResponse res = new JsonResponse();
		try{
			String searchCriterias[]=searchCriteria.split(",");
			res.setDataGrid(showGridDutySlipInvoice(dutySlipService.listDutySlipInvoice(pageNumber,searchCriterias), pageNumber));
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
//from here my code is start 
	
	/* ___________________________________________________________________________________________			
	 *     For Duty Slip Receive Functionality Start
	 * ___________________________________________________________________________________________
	 */
	
	@RequestMapping(value="/updateDutySlip",method=RequestMethod.POST)
	public @ResponseBody JsonResponse updateDutySlip(
			@ModelAttribute(value="dutySlipModel")DutySlipModel dutySlipModel,
			BindingResult bindingResult,
			@RequestParam("id") String[] id, 
			@RequestParam(value="parkingAmount") String[] parkingAmount, 
			@RequestParam(value="tollTaxAmount") String[] tollTaxAmount,
			@RequestParam(value="stateTax") String[] stateTax,
			HttpSession session){
			JsonResponse res = new JsonResponse();
			try {
				Long loginUserId = 0l;
				if(session.getAttribute("loginUserId")!=null){
					loginUserId=(Long) session.getAttribute("loginUserId");
				}

				for(int i= 0;i< id.length; i++){
					DutySlipModel oldDutySlipModel = dutySlipService.formFillForEdit(Long.valueOf(id[i]));
					oldDutySlipModel.setStateTax(Double.valueOf(stateTax[i]));
					oldDutySlipModel.setTollTaxAmount(Double.valueOf(tollTaxAmount[i]));
					oldDutySlipModel.setParkingAmount(Double.valueOf(parkingAmount[i]));
					oldDutySlipModel.setDutySlipStatus("R");
					oldDutySlipModel.setId(Long.valueOf(id[i]));
					oldDutySlipModel.setDutySlipReceivedByDate(new Date());
					oldDutySlipModel.setDutySlipReceivedBy(loginUserId);
					dutySlipService.update(oldDutySlipModel);
				}
				res.setResult("Duty Slip Details are Successfully Received !!"); 
				res.setStatus("Success"); 
			}
			catch(Exception e) {
				logger.error("",e);
				res.setStatus("Failure");
				res.setResult(Message.FATAL_ERROR_MESSAGE);
			}
			finally{
				//res.setDataGrid(showGrid(bookingMasterService.);
				//res.setDutySlipModel(dutySlipModels);
			}
				
			return res;	
	}
	
	/* ___________________________________________________________________________________________			
	 *     Getting All DSDetails  Functionality Start
	 * ___________________________________________________________________________________________
	 */
	@RequestMapping(value = "/getDSDetails", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getDSDetails(
			  @RequestParam(value="dutySlipDetailsId") Long dutySlipDetailsId,
			  HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		try {
			DutySlipModel dutySlipModel = null;
			dutySlipModel = dutySlipService.getDSDetails(dutySlipDetailsId);
			Long branchId = dutySlipModel.getBookingDetailModel().getBranch().getId().longValue();
			Long booDetailId = dutySlipModel.getBookingDetailModel().getId();
			Long carDetailId = dutySlipModel.getCarDetailModel().getId();
			Long corporateId  = dutySlipModel.getBookingDetailModel().getBookingMasterModel().getCorporateId().getId();
			Long minMinuteSlab  = dutySlipModel.getBookingDetailModel().getBookingMasterModel().getCorporateId().getMinRoundSlab();
			
			CarAllocationModel carAllocationModel = dutySlipService.getCarOwnerType(carDetailId);
			BookingDetailModel bookingDetailModel = bookingMasterService.formFillForDetailEdit(booDetailId);
			BookingMasterModel bookingMasterModel = billingService.getDutySlipClosingBookingMasterDetail(dutySlipDetailsId);
			if(bookingDetailModel.getCarModel()!=null){
				List<GeneralMasterModel> generalMasterList = billingService.getCarMasterId(Long.valueOf(bookingDetailModel.getCarModel().getId()));
				if (generalMasterList != null && generalMasterList.size() > 0){
					GeneralMasterModel generalMasterModels= generalMasterList.get(0);
					bookingDetailModel.setGeneralMaster(generalMasterModels);
					String sDate = dateFormat2.format(new Date());
					List<CorporateTariffDetModel> corporateTariffDetList = billingService.getTariffValue(Long.valueOf(bookingDetailModel.getGeneralMaster().getId()),corporateId,sDate,bookingDetailModel.getBranch().getId());
					res.setCorporateTariffDetModelList(corporateTariffDetList);
				}
			}
			res.setCorpTariffList(masterDataService.getGeneralMasterData_UsingCode("TariffScheCorp:" + corporateId + ":" + branchId));
			res.setCorpRentalList(masterDataService.getGeneralMasterData_UsingCode("RentalCorp:" + corporateId + ":" + branchId));
			res.setDutySlipModel(dutySlipModel);
			res.setBookingMasterModel(bookingMasterModel);
			res.setCarAllocationModel(carAllocationModel);
			res.setDsIdCount(minMinuteSlab);
			res.setStatus("Success");
		} catch (DataIntegrityViolationException e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FOREIGNKEY_ERROR_MESSAGE);
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		} 
		return res;
	}
	
	
	/* ___________________________________________________________________________________________			
	 *     For Close Duty Slip from DSClose Screen Functionality Start
	 * ___________________________________________________________________________________________
	 */
	@RequestMapping(value="/saveUpdateDs",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveUpdateDs(
			@ModelAttribute(value="dutySlipModel")DutySlipModel dutySlipModel,
			BindingResult bindingResult,
			@RequestParam("id") Long id, 
			@RequestParam("billingStatus") String billingStatus, HttpSession session){
			JsonResponse res = new JsonResponse();
			try {
						DutySlipModel oldDutySlipModel = dutySlipService.formFillForEdit(id);
						oldDutySlipModel.setStateTax(dutySlipModel.getStateTax());
						oldDutySlipModel.setTollTaxAmount(dutySlipModel.getTollTaxAmount());
						oldDutySlipModel.setParkingAmount(dutySlipModel.getParkingAmount());
						oldDutySlipModel.setCloseKms(dutySlipModel.getCloseKms());
						oldDutySlipModel.setDateTo(dutySlipModel.getDateTo());
						oldDutySlipModel.setExtraChgHrs(dutySlipModel.getExtraChgHrs());
						oldDutySlipModel.setExtraChgKms(dutySlipModel.getExtraChgKms());
						oldDutySlipModel.setManualSlipNo(dutySlipModel.getManualSlipNo());
						oldDutySlipModel.setMinChgHrs(dutySlipModel.getMinChgHrs());
						oldDutySlipModel.setMinChgKms(dutySlipModel.getMinChgKms());
						oldDutySlipModel.setNightAllow(dutySlipModel.getNightAllow());
						oldDutySlipModel.setDayAllow(dutySlipModel.getDayAllow());
						oldDutySlipModel.setRemarks(dutySlipModel.getRemarks());
						oldDutySlipModel.setTotalKms(dutySlipModel.getTotalKms());
						oldDutySlipModel.setTotalHrs(dutySlipModel.getTotalHrs());
						oldDutySlipModel.setTotalFare(dutySlipModel.getTotalFare());
						oldDutySlipModel.setTimeTo(dutySlipModel.getTimeTo());
						oldDutySlipModel.setMiscCharge(dutySlipModel.getMiscCharge());
						oldDutySlipModel.setExtraChgHrs(dutySlipModel.getExtraChgHrs());
						oldDutySlipModel.setExtraChgKms(dutySlipModel.getExtraChgKms());
						oldDutySlipModel.setFuelCharge(dutySlipModel.getFuelCharge());
						oldDutySlipModel.setGuideCharge(dutySlipModel.getGuideCharge());
						oldDutySlipModel.setTimeFrom(dutySlipModel.getTimeFrom());
						oldDutySlipModel.setTotalDay(dutySlipModel.getTotalDay());
						oldDutySlipModel.setDateFrom(dutySlipModel.getDateFrom());
						oldDutySlipModel.setBillingStatus(billingStatus);
						oldDutySlipModel.setTotalNight(dutySlipModel.getTotalNight());
						oldDutySlipModel.setBasicFare(dutySlipModel.getBasicFare());
						oldDutySlipModel.setTariff(dutySlipModel.getTariff());	
						oldDutySlipModel.setNightAllowanceRate(dutySlipModel.getNightAllowanceRate());
						oldDutySlipModel.setOutStationAllowRate(dutySlipModel.getOutStationAllowRate());
						oldDutySlipModel.setDutySlipStatus("C");					
						oldDutySlipModel.setId(id);
						if(session.getAttribute("loginUserId")!=null){
								Long loginUserId=(Long) session.getAttribute("loginUserId");
								oldDutySlipModel.setDutySlipClosedBy(loginUserId);
							}
						oldDutySlipModel.setDutySlipClosedByDate(new Date());
						dutySlipService.update(oldDutySlipModel);
				res.setResult("Duty Slip  are Successfully Colsed !!"); 
				res.setStatus("Success"); 
			}
			catch(Exception e) {
				logger.error("",e);
				res.setStatus("Failure");
				res.setResult(Message.FATAL_ERROR_MESSAGE);
			}
			finally{
				//res.setDataGrid(showGrid(bookingMasterService.);
				//res.setDutySlipModel(dutySlipModels);
			}
				
			return res;	
	}
	
	/* ___________________________________________________________________________________________			
	 *     Generate Invoice of  Duty Slip from DSUnbilled Screen Functionality Start
	 * ___________________________________________________________________________________________
	 */
	@RequestMapping(value="/updateDSForInvoice",method=RequestMethod.POST)
	public @ResponseBody JsonResponse updateDSForInvoice(
			@ModelAttribute(value="dutySlipModel")DutySlipModel dutySlipModel,
			BindingResult bindingResult,
			@RequestParam("id") String[] id, 
			@RequestParam("jobType") String[] jobType,
			@RequestParam("invoiceDate") Date invoiceDate,
			HttpSession session){
			JsonResponse res = new JsonResponse();
			try {
				String[] sTaxN = (dutySlipModel.getTaxName()).split(",");
				String[] sTaxP = (dutySlipModel.getTaxPercentage()).split(",");
				String[] sTaxV = (dutySlipModel.getTaxValues()).split(",");
				String sTaxValues ="", sTaxPercentage = "", sTaxName = "";
				
				for(int i =0; i < sTaxN.length;i++){
					sTaxName = sTaxName + sTaxN[i] + ",";
					sTaxPercentage = sTaxPercentage + sTaxP[i] + ",";
					sTaxValues = sTaxValues + (id.length == 1 ? sTaxP[i] : "0") + ",";
				}
				sTaxName = sTaxName.substring(0,sTaxName.length() - 1);
				sTaxPercentage = sTaxPercentage.substring(0,sTaxPercentage.length() - 1);
				sTaxValues = sTaxValues.substring(0,sTaxValues.length() - 1);

				for(int i= 0;i < id.length; i++){
					DutySlipModel oldDutySlipModel = dutySlipService.formFillForEdit(Long.valueOf(id[i]));
					oldDutySlipModel.setTaxName(sTaxName);
					oldDutySlipModel.setTaxPercentage(sTaxPercentage);
					oldDutySlipModel.setTaxValues(sTaxValues);
					oldDutySlipModel.setDutySlipStatus("I");
					oldDutySlipModel.setBillingStatus("billed");
					oldDutySlipModel.setId(Long.valueOf(id[i]));
					if(session.getAttribute("loginUserId")!=null){
						Long loginUserId=(Long) session.getAttribute("loginUserId");
						oldDutySlipModel.setInvoiceGenerateBy(loginUserId);
					}
					oldDutySlipModel.setInvoiceGenerateDate(new Date());
					oldDutySlipModel.setInvoiceDate(invoiceDate);
					if(i == 0){
						finalInvoiceNo = ("NewIn");
						oldDutySlipModel.setInvoiceNo(finalInvoiceNo);
						finalInvoiceNo = dutySlipService.update(oldDutySlipModel);
						if(finalInvoiceNo.equals("Duplicate")){
							throw new DuplicateKeyException("Invoice Not Generated Due To Duplicate Invoice Number !!!");
						}
					}else{
						oldDutySlipModel.setInvoiceNo(finalInvoiceNo);
						dutySlipService.update(oldDutySlipModel);
					}
				}
				session.setAttribute("flag", "Y");
				dutySlipModel = dutySlipService.getInvoiceNo(Long.parseLong(id[0]));
				if(dutySlipModel!=null){
					invoiceNo = dutySlipModel.getInvoiceNo();
				}
				res.setResult("Invoice Successfully Generated.... !!"); 
				res.setStatus("Success"); 
				res.setDataString1(invoiceNo);	
			}catch(DuplicateKeyException e){
				logger.error("",e);
				res.setStatus("Failure");
				res.setResult(Message.FATAL_ERROR_MESSAGE);
			}catch(Exception e) {
				logger.error("",e);
				res.setStatus("Failure");
				res.setResult(Message.FATAL_ERROR_MESSAGE);
			}
			finally{
				//res.setDataGrid(showGrid(bookingMasterService.);
				//res.setDutySlipModel(dutySlipModels);
			}
			return res;	
	}
	
	/* ___________________________________________________________________________________________			
	 *     Generate Invoice of  Duty Slip from DSClose Or DSClosing Screen Functionality Start
	 * ___________________________________________________________________________________________
	 */
	@RequestMapping(value="/saveUpdateDSForInvoicing",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveUpdateDSForInvoicing(
			@ModelAttribute(value="dutySlipModel")DutySlipModel dutySlipModel,
			BindingResult bindingResult,
			@RequestParam("id") Long id, 
			@RequestParam("billingStatus") String billingStatus, 
			@RequestParam("taxName") String[] taxName,
			@RequestParam("taxPercentage") String[] taxPercentage,
			@RequestParam("taxValues") String[] taxValues,
			@RequestParam("dsInvoiceDate") Date dsInvoiceDate, 	HttpSession session){
			JsonResponse res = new JsonResponse();
			try {
				String sTaxValues ="", sTaxPercentage = "", sTaxName = "";
				for(int i =0; i< taxValues.length;i++){
					if(i == taxValues.length -1){
						sTaxValues = sTaxValues + taxValues[i];
						sTaxPercentage = sTaxPercentage + taxPercentage[i];
						sTaxName = sTaxName + taxName[i];
					}else{
						sTaxValues = sTaxValues + taxValues[i] + ",";
						sTaxPercentage = sTaxPercentage + taxPercentage[i] + ",";
						sTaxName = sTaxName + taxName[i] + ",";
					}
				}
				DutySlipModel oldDutySlipModel = dutySlipService.formFillForEdit(id);
				
				oldDutySlipModel.setStateTax(dutySlipModel.getStateTax());
				oldDutySlipModel.setTollTaxAmount(dutySlipModel.getTollTaxAmount());
				oldDutySlipModel.setParkingAmount(dutySlipModel.getParkingAmount());
				oldDutySlipModel.setCloseKms(dutySlipModel.getCloseKms());
				oldDutySlipModel.setDateTo(dutySlipModel.getDateTo());
				oldDutySlipModel.setExtraChgHrs(dutySlipModel.getExtraChgHrs());
				oldDutySlipModel.setExtraChgKms(dutySlipModel.getExtraChgKms());
				oldDutySlipModel.setManualSlipNo(dutySlipModel.getManualSlipNo());
				oldDutySlipModel.setMinChgHrs(dutySlipModel.getMinChgHrs());
				oldDutySlipModel.setMinChgKms(dutySlipModel.getMinChgKms());
				oldDutySlipModel.setNightAllow(dutySlipModel.getNightAllow());
				oldDutySlipModel.setDayAllow(dutySlipModel.getDayAllow());
				oldDutySlipModel.setRemarks(dutySlipModel.getRemarks());
				oldDutySlipModel.setTotalKms(dutySlipModel.getTotalKms());
				oldDutySlipModel.setTotalHrs(dutySlipModel.getTotalHrs());
				oldDutySlipModel.setTotalFare(dutySlipModel.getTotalFare());
				oldDutySlipModel.setTimeTo(dutySlipModel.getTimeTo());
				oldDutySlipModel.setMiscCharge(dutySlipModel.getMiscCharge());
				oldDutySlipModel.setExtraChgHrs(dutySlipModel.getExtraChgHrs());
				oldDutySlipModel.setExtraChgKms(dutySlipModel.getExtraChgKms());
				oldDutySlipModel.setFuelCharge(dutySlipModel.getFuelCharge());
				oldDutySlipModel.setGuideCharge(dutySlipModel.getGuideCharge());
				oldDutySlipModel.setTimeFrom(dutySlipModel.getTimeFrom());
				oldDutySlipModel.setTotalDay(dutySlipModel.getTotalDay());
				oldDutySlipModel.setDateFrom(dutySlipModel.getDateFrom());
				oldDutySlipModel.setBillingStatus("billed");
				oldDutySlipModel.setTotalNight(dutySlipModel.getTotalNight());
				oldDutySlipModel.setBasicFare(dutySlipModel.getBasicFare());
				oldDutySlipModel.setTariff(dutySlipModel.getTariff());
				oldDutySlipModel.setNightAllowanceRate(dutySlipModel.getNightAllowanceRate());
				oldDutySlipModel.setOutStationAllowRate(dutySlipModel.getOutStationAllowRate());
				oldDutySlipModel.setTaxName(sTaxName);
				oldDutySlipModel.setTaxPercentage(sTaxPercentage);
				oldDutySlipModel.setTaxValues(sTaxValues);
				
				if(dutySlipModel.getBookingDetailModel() != null){
					oldDutySlipModel.getBookingDetailModel().getBookingMasterModel().setBookedFor(dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedFor());
					oldDutySlipModel.getBookingDetailModel().getBookingMasterModel().setBookedForName(dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedForName());
				}				
				oldDutySlipModel.setDutySlipStatus("I");

				Long loginUserId = 0l;
				if(session.getAttribute("loginUserId")!=null){
						loginUserId=(Long) session.getAttribute("loginUserId");
						oldDutySlipModel.setDutySlipClosedBy(loginUserId);
						oldDutySlipModel.setInvoiceGenerateBy(loginUserId);
				}
				oldDutySlipModel.setDutySlipClosedByDate(new Date());
				oldDutySlipModel.setInvoiceGenerateDate(new Date());
				oldDutySlipModel.setInvoiceDate(dsInvoiceDate);

				finalInvoiceNo = ("NewIn");
				oldDutySlipModel.setInvoiceNo(finalInvoiceNo);
				dutySlipService.update(oldDutySlipModel);
				session.setAttribute("flag", "Y");
				Long lCorpId = 0l;
				if(dutySlipModel!=null){
					invoiceNo = oldDutySlipModel.getInvoiceNo();
					lCorpId = oldDutySlipModel.getBookingDetailModel().getBookingMasterModel().getCorporateId().getId().longValue();
				}
				
				BillingController billingController = new BillingController();
				billingController.saveInvoice(invoiceNo, loginUserId, lCorpId,0,0);
				
				res.setDataString1(invoiceNo);
				res.setResult("Invoice Successfully Generated.... !!"); 
				res.setStatus("Success"); 
			}catch(Exception e) {
				logger.error("",e);
				res.setStatus("Failure");
				res.setResult(Message.FATAL_ERROR_MESSAGE);
				session.setAttribute("flag", "Y");
			}
			return res;	
	}
	
	
	@RequestMapping(value="/saveDSInvoiceDate",method=RequestMethod.POST)
	public @ResponseBody JsonResponse saveDSInvoiceDate(
			@ModelAttribute(value="dutySlipModel")DutySlipModel dutySlipModel,
			BindingResult bindingResult,
			@RequestParam("invoiceGeneratedDate") Date invoiceGeneratedDate, 
			@RequestParam("id") Long[] id,
			HttpSession session){
			JsonResponse res = new JsonResponse();
			try {
					for(int i=0; i<id.length;i++){
						DutySlipModel oldDutySlipModel = dutySlipService.formFillForEdit(Long.valueOf(id[i]));
						oldDutySlipModel.setInvoiceGenerateDate(invoiceGeneratedDate);
						oldDutySlipModel.setInvoiceDate(new Date());
						oldDutySlipModel.setDutySlipStatus("I");
						if(session.getAttribute("loginUserId")!=null){
								Long loginUserId=(Long) session.getAttribute("loginUserId");
								oldDutySlipModel.setInvoiceGenerateBy(loginUserId);
						}
						dutySlipService.update(oldDutySlipModel);
					}
				res.setStatus("Success"); 
			}
			catch(Exception e) {
				logger.error("",e);
				res.setStatus("Failure");
				res.setResult(Message.FATAL_ERROR_MESSAGE);
				session.setAttribute("flag", "Y");
			}
			return res;	
	}
	
	/* ___________________________________________________________________________________________			
	 *     For Getting Tariff Scheme Parameter Functionality Start
	 * ___________________________________________________________________________________________
	 */
	@RequestMapping(value = "/getTariffDetailsOnDSClose", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getTariffDetailsOnDSClose(
			  @RequestParam(value="tariffId") Long tariffId,
			  HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		try {
				TariffSchemeParaModel tariffSchemeParaModel = billingService.getTariffSchemePara(tariffId);
				res.setTariffSchemeParaModel(tariffSchemeParaModel);
			res.setStatus("Success");
			
		} catch (DataIntegrityViolationException e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FOREIGNKEY_ERROR_MESSAGE);
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		} 
		return res;
	}

	/* ___________________________________________________________________________________________			
	 *     For Getting Tariff Details Functionality Start
	 * ___________________________________________________________________________________________
	 */
	@RequestMapping(value = "/getTariffDetails", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getTariffDetails(
			  @RequestParam(value="tariffId") Long tariffId,
			  @RequestParam(value="corporateId") Long corporateId,
			  @RequestParam(value="carModelId") Long carModelId,
			  @RequestParam(value="branchId") Long branchId,
			  @RequestParam(value="exInvDate") String exInvDate,
			  HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		try {
				CorporateModel corporateModel = corporateService.formFillForEdit(corporateId);
				Long minMinuteSlab = corporateModel.getMinRoundSlab();
				TariffSchemeParaModel tariffSchemeParaModel = billingService.getTariffSchemePara(tariffId);
				List<GeneralMasterModel> generalMasterList = billingService.getCarMasterId(carModelId);
				if (generalMasterList != null && generalMasterList.size() > 0){
					GeneralMasterModel generalMasterModels= generalMasterList.get(0);
					
					String sDate = dateFormat2.format(dateFormat.parse(exInvDate));
					List<CorporateTariffDetModel> corporateTariffDetList = billingService.getTariffValue(Long.valueOf(generalMasterModels.getId()),corporateId,sDate,branchId);
					res.setCorporateTariffDetModelList(corporateTariffDetList);
				}
				res.setTariffSchemeParaModel(tariffSchemeParaModel);
				res.setDsIdCount(minMinuteSlab);
			res.setStatus("Success");
			
		} catch (DataIntegrityViolationException e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FOREIGNKEY_ERROR_MESSAGE);
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		} 
		return res;
	}
	
	/* ___________________________________________________________________________________________			
	 *     For Getting Tariff Values As Per Corporate Functionality Start
	 * ___________________________________________________________________________________________
	 */
	@RequestMapping(value = "/TraiffValuesAsPerCarTraiff", method = RequestMethod.POST)
	public @ResponseBody JsonResponse TraiffValuesAsPerCarTraiff(
			  @RequestParam(value="tariffId") Long tariffId,
			  HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		try {
				TariffSchemeParaModel tariffSchemeParaModel = billingService.getTariffSchemePara(tariffId);
				res.setTariffSchemeParaModel(tariffSchemeParaModel);
			res.setStatus("Success");
			
		} catch (DataIntegrityViolationException e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FOREIGNKEY_ERROR_MESSAGE);
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		} 
		return res;
	}
	
	/* ___________________________________________________________________________________________			
	 *     For Getting All Tariff Details Functionality Start
	 * ___________________________________________________________________________________________
	 */
	@RequestMapping(value = "/getAllTariffDetails", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getAllTariffDetails(
			  @RequestParam(value="tariffId") String tariffId,
			  HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		try {
			String[] tId = tariffId.split(",");
			Long[] lTId = new Long[tId.length];
			for(int i =0; i<tId.length;i++){
				lTId[i] = Long.parseLong(tId[i]);
			}
			List<TariffSchemeParaModel> tariffSchemeParaModel = billingService.getTariffSchemePara(lTId);
				res.setTariffSchemeParaModelList(tariffSchemeParaModel);
			res.setStatus("Success");
			
		} catch (DataIntegrityViolationException e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FOREIGNKEY_ERROR_MESSAGE);
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		} 
		return res;
	}
}
