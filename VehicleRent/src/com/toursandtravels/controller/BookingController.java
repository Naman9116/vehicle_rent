package com.toursandtravels.controller;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.javamail.JavaMailSender;
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

import com.common.global.SMSSender;
import com.common.model.AddressDetailModel;
import com.common.model.ContactDetailModel;
import com.common.service.MasterDataService;
import com.corporate.model.CorporateModel;
import com.corporate.service.AuthorizedUserService;
import com.corporate.service.CorporateService;
import com.master.model.CityMasterModel;
import com.master.model.GeneralMasterModel;
import com.operation.controller.BookingMasterController;
import com.operation.model.BookingDetailModel;
import com.operation.model.BookingMasterModel;
import com.operation.model.DutySlipModel;
import com.operation.service.BookingMasterService;
import com.operation.service.CarDetailService;
import com.operation.service.DutySlipService;
import com.operation.validator.BookingMasterValidator;
import com.relatedInfo.model.RelatedInfoModel;
import com.toursandtravels.model.BookingModel;
import com.toursandtravels.model.CustomerModel;
import com.toursandtravels.model.TDutySlipModel;
import com.toursandtravels.service.BookingService;
import com.toursandtravels.service.CustomerService;
import com.user.model.UserModel;
import com.user.service.UserService;
import com.util.JsonResponse;
import com.util.Message;
import com.util.Utilities;
@Controller
@SessionAttributes({ "bookingDetails_sessionData", "tariffIdList", "carTypeIdList", "rentalTypeIdList", "branchList" })
public class BookingController {
	private static final Logger logger = Logger.getLogger(BookingMasterController.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	@Autowired
	private BookingMasterService bookingMasterService;
	
	@Autowired
	private BookingService bookingService;

	@Autowired
	private CarDetailService carDetailService;

	@Autowired
	private DutySlipService dutySlipService;

	@Autowired
	private MasterDataService masterDataService;

	@Autowired
	private CorporateService corporateService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private AuthorizedUserService authorizedUserService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private CustomerService customerService;


	private BookingMasterValidator validator = null;

	public BookingMasterValidator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(BookingMasterValidator validator) {
		this.validator = validator;
	}

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields(new String[] { "id" });
		dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping("/pageLoadTBookingMaster")
	public ModelAndView pageLoadTBookingMaster(Map<String, Object> map, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView("tBookingMaster");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try {
			session.setAttribute("bookingDetails_sessionData", null);
			map.put("customerOrCompanyNameIdList", masterDataService.getGeneralMasterData_UsingCode("CORP:"+branchesAssigned)); // Type
			map.put("tariffIdList", masterDataService.getGeneralMasterData_UsingCode("VTF"));
			map.put("carModelIdList", masterDataService.getGeneralMasterData_UsingCode("VMD"));
			map.put("rentalTypeIdList", masterDataService.getGeneralMasterData_UsingCode("VRT"));
			map.put("branchList", masterDataService.getGeneralMasterData_UsingCode("VBM-A:"+branchesAssigned));
			map.put("hubList", masterDataService.getGeneralMasterData_UsingCode("VOM"));
			map.put("MOBList", masterDataService.getGeneralMasterData_UsingCode("MOP"));
			map.put("terminalList", masterDataService.getGeneralMasterData_UsingCode("TRM"));
			
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			map.put("bookingModel", new BookingModel());
			map.put("bookingList", showGrid());
		}
		return modelAndView;
	}
	
	@RequestMapping(value = "/getCustomerAsPerMobile", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getCustomerAsPerMobile(
			@RequestParam("mobileNo") String mobileNo) {
		JsonResponse res = new JsonResponse();
		try {
			System.out.print(mobileNo);
			CustomerModel customerModel =bookingService.getCustomerModelAsPerMobile(mobileNo);
			res.setCustomerModel(customerModel);
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	@SuppressWarnings("null")
	@RequestMapping(value = "/saveOrUpdateBooking", method = RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateBooking(
			@ModelAttribute(value = "bookingModel") BookingModel bookingModel,
			@RequestParam("idForUpdate") String idForUpdate,@RequestParam("name") String name
			,HttpSession session){
		JsonResponse res = new JsonResponse();
		try {
			Long loginCompanyId=null;
			if(idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")){
				CustomerModel  customerModel=new CustomerModel();
				CustomerModel customerModel2=bookingService.getCustomerModelAsPerMobile(bookingModel.getMobileNo());
				if(customerModel2 == null){
					customerModel.setName(name);
					customerModel.setMobileNo(bookingModel.getMobileNo());
					customerModel.setCreatedDate(new Date());
					 if(session.getAttribute("loginCompanyId")!=null)
						 loginCompanyId=(Long) session.getAttribute("loginCompanyId");
					customerModel.setCompany(loginCompanyId);
					customerService.save(customerModel);
					CustomerModel  customerModels=bookingService.getCustomerModelAsPerMobile(bookingModel.getMobileNo());
					bookingModel.setCustomerId(customerModels);
					bookingModel.setCompany(bookingModel.getCompany());
				}
				bookingModel.setInsDate(new Date());
				bookingModel=bookingService.save(bookingModel);
				res.setDataString1("Your Booking Details has been saved with booking number "
						+ bookingModel.getBookingNo());
				res.setBookingModel(bookingModel);
				res.setResult(Message.SAVE_SUCCESS_MESSAGE);
			}
			else{
				bookingModel.setId(Long.valueOf(idForUpdate));
				bookingService.update(bookingModel);
				res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
			}
			res.setStatus("Success");
		}catch(ConstraintViolationException e){
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.DUPLICATE_ERROR_MESSAGE);
		}catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			res.setDataGrid(showGrid());
		}
		return res;
	}

	public String showGrid() {
		List<BookingModel> bookingModelList = bookingService.getBooking() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dataString = " <table style='table-layout:fixed; width:99%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>					"
				+ " <tr style='font-size:8pt;'> 									"
				+ " 	<th width="
				+ Message.ACTION_SIZE
				+ ">Action</th> 		  	"
				+ " 	<th width=9%>Booking Date</th> 		"
				+ " 	<th width=10%>Branch</th> 			"
				+ " 	<th width=10%>Hub</th> 				"
				+ " 	<th width=10%>Rental Type</th> 		"
				+ " 	<th width=10%>Tariff</th> 			"
				+ " 	<th width=10%>Car Model</th> 		"
				+ " 	<th width=10%>Flight No</th> 		"
				+ " 	<th width=10%>Start Time</th> 		"
				+ " 	<th width=10%>PickUp Time</th> 		"
				+ " 	<th width=10%>Reporting At</th> 	"
				+ " 	<th width=10%>Drop At</th> 			"
				+ " 	<th width=10%>Spl. Instruction</th> " + " </tr>						" + " </thead>					" + " <tbody>					";
		for (BookingModel bookingModel : bookingModelList) {
			String sDate = bookingModel.getPickUpDate() == null ? "" : dateFormat.format(bookingModel
					.getPickUpDate());
			dataString = dataString
					+ " <tr> "
					+ " <td>"
					+ " 	<a href='javascript:formFillDetailRecordBooking("
					+ bookingModel.getId()
					+ ")'><img src='./img/editButton.png' border='0' title='Edit' width='20' height='20'/></a> "
					+ " <a href='javascript:deleteBooking("
					+ bookingModel.getId()
					+ ")' ><img src='./img/deleteButton.png' border='0' title='Delete / Cancel' width='20' height='20'/></a> "
					+ " </td> 														" + " <td >" + sDate + "</td>" + " <td >"
					+ bookingModel.getBranch().getName() + "</td>  		" + " <td >"
					+ bookingModel.getOutlet().getName() + "</td>  		" + " <td >"
					+ bookingModel.getRentalType().getName() + "</td>  		" + " <td >"
					+ bookingModel.getTariff().getName() + "</td>  		" + " <td >"
					+ bookingModel.getCarModel().getName() + "</td>  		" + " <td >"
					+ bookingModel.getFlightNo() + "</td>  		" + " <td >" + bookingModel.getStartTime()
					+ "</td>  		" + " <td >" + bookingModel.getPickUpTime() + "</td>  		" + " <td >"
					+ bookingModel.getReportingAddress() + "</td>  		" + " <td >"
					+ bookingModel.getToBeRealeseAt() + "</td>  		" + " <td >"
					+ bookingModel.getInstruction() + "</td>  		" + " </tr> 		";
		}
		dataString = dataString + "</tbody></table>";
		return dataString;
	}
	
	@RequestMapping(value="/formFillForEditBooking",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditCustomer(
			@RequestParam("formFillForEditId") String formFillForEditId){
		JsonResponse res = new JsonResponse();
		try{
			BookingModel bookingModel = bookingService.formFillForEditBooking(Long.parseLong(formFillForEditId));
			res.setStatus("Success");
			res.setBookingModel(bookingModel);
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		
		return res;
	}
	
	@RequestMapping(value="/deleteBooking",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteCustomer(
			@RequestParam("idForDelete") String idForDelete){
		JsonResponse res = new JsonResponse();
		try{
			bookingService.delete(Long.valueOf(idForDelete));
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
			res.setDataGrid(showGrid());
		}
		return res;
	}
	

	@RequestMapping(value = "/getTariffAsPerRentalType", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getTariffAsPerRentalType(
			@RequestParam("id") long id) {
		JsonResponse res = new JsonResponse();
		try {
			List<GeneralMasterModel> generalMasterModelList =bookingService.getTarrifAsPerRentleType(id);
			res.setGeneralMasterModelList(generalMasterModelList);
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	@RequestMapping(value = "/getBookingAsPerMobile", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getBookingAsPerMobile(
			@RequestParam("mobileNo") String mobileNo) {
		JsonResponse res = new JsonResponse();
		try {
			BookingModel bookingModel =bookingService.getBookingModelAsPerMobile(mobileNo);
			res.setBookingModel(bookingModel);
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	@RequestMapping("/pageLoadTJobWorks/{jobType}")
	public ModelAndView pageLoadTJobWorks(Map<String, Object> map, HttpSession session, @PathVariable String jobType) {
		ModelAndView modelAndView = new ModelAndView("tJobWorks");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try {
			session.setAttribute("bookingDetails_sessionData", null);
			map.put("customerOrCompanyNameIdList", masterDataService.getGeneralMasterData_UsingCode("CORP:"+branchesAssigned)); // Type
			map.put("companyIdList",masterDataService.getGeneralMasterData_UsingCode("VCM"));
			map.put("tariffIdList", masterDataService.getGeneralMasterData_UsingCode("VTF"));
			map.put("carModelIdList", masterDataService.getGeneralMasterData_UsingCode("VMD"));
			map.put("rentalTypeIdList", masterDataService.getGeneralMasterData_UsingCode("VRT"));
			map.put("MOBList", masterDataService.getGeneralMasterData_UsingCode("MOB"));
			map.put("BCRList", masterDataService.getGeneralMasterData_UsingCode("BCR"));
			map.put("branchList", masterDataService.getGeneralMasterData_UsingCode("VBM-A:"+branchesAssigned));
			if (jobType.equals("Status"))
				map.put("carRegNoList", masterDataService.getCarRegistrationNoList(""));
			map.put("jobType", jobType);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			map.put("bookingMasterModel", new BookingMasterModel());
		}
		return modelAndView;
	}
	
	@RequestMapping(value = "/searchTBooking", method = RequestMethod.POST)
	public @ResponseBody JsonResponse searchTBooking(@RequestParam("jobType") String jobType,
			@RequestParam("sCriteria") String sCriteria, @RequestParam("sValue") String sValue, HttpSession session,
			HttpServletRequest request) {
		JsonResponse res = new JsonResponse();

		try {
			String[] sCriteriaList = sCriteria.split(",");
			String[] sValueList = sValue.split(",");
			List<BookingModel> bookingList = bookingService.list(jobType, sCriteriaList, sValueList);

			res.setBookingModelList(bookingList);
			res.setDataGrid(showGridJobs(bookingList, jobType));
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	// New Methord Start
	public String showGridJobsCurrent(List<BookingModel> bookingModelData) {
		SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dataString = " <table style='table-layout:fixed; width:99%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>" + " <tr>";
		dataString += "<th width='25px' >Alert</th>" + "    <th width='60px' >Direct Dispatch</th>";
		dataString += " 	<th width='77px' >Booking No</th>";
		dataString += " 	<th  style='display:none; width='77px' >StartTime</th>";
		dataString += " 	<th width='45px' >Booking Mode</th>";
		dataString += " 	<th width='60px' >Pickup Date</th>" + " 	<th width='35px' >Pickup Time</th>"
				+ " 	<th width='95px' >Company Name</th>" + " 	<th width='60px' >Booked By</th>"
				+ " 	<th width='66px' >Client Name</th>" + " 	<th width='80px' >Client Mobile No</th>"
				+ " 	<th width='150px' >Pickup Location</th>" + " 	<th width='150px' >Drop Location</th>"
				+ " 	<th width='150px' >Special Instruction</th>" + " 	<th width='45px' >Rental Type</th>"
				+ " 	<th width='50px' >Car Type</th>";
		dataString += " 	<th style='display:none;' width='25px' >Status</th>" + " 	<th width=" + Message.ACTION_SIZE
				+ ">Action Mode</th>" + "<th width='60px' >Created Date & Time</th>";
		dataString += " 	<th width='45px' >Booking Taken By</th>"
				+ " 	<th style='display:none;' width='25px' >Booked For</th>"
				+ " 	<th style='display:none;' width='25px' >dispatchCar</th>"
				+ " </tr>" + " </thead>"
				+ " <tbody>";
		if (bookingModelData == null) {
			return dataString + "</tbody></table>";
		}
//		String dsDate = "";
		for (BookingModel bookingModel : bookingModelData) {
			String sPickUpDate = bookingModel.getPickUpDate() == null ? "" : dateFormat.format(bookingModel
					.getPickUpDate());
			String sBookingDate = bookingModel.getBookingDate() == null ? ""
					: dateFormatWithTime.format(bookingModel.getBookingDate());
			long lId = bookingModel.getId();
			String sImageSrc = "";
			Date pickupDateTime = null;
			try {
				pickupDateTime = dateFormatWithTime.parse(sPickUpDate + " " + bookingModel.getPickUpTime());
			} catch (ParseException e) {
				logger.error("", e);
			}

			if (pickupDateTime.before(new Date())) {
				continue;
			}

			if (getDateDiff(new Date(), pickupDateTime, TimeUnit.MINUTES) <= Long.valueOf("30")
					&& pickupDateTime.after(new Date())) {
				sImageSrc = "'./img/red-signal.png'";
			} else if (getDateDiff(new Date(), pickupDateTime, TimeUnit.MINUTES) <= Long.valueOf("120")
					&& pickupDateTime.after(new Date())) {
				sImageSrc = "'./img/yellow-signal.png'";
			} else if (pickupDateTime.after(new Date())) {
				sImageSrc = "'./img/green-signal.png'";
			}

			if (bookingModel.getStatus() == null) {
				bookingModel.setStatus("New");
			} else if (bookingModel.getStatus().equals("A")) {
				bookingModel.setStatus("Allocated");
			} else if (bookingModel.getStatus().equals("D")) {
				bookingModel.setStatus("Dispatched");
			} else if (bookingModel.getStatus().equals("C")) {
				bookingModel.setStatus("Cancelled");
			} else if (bookingModel.getStatus().equals("R")) {
				bookingModel.setStatus("Reserved");
			} else if (bookingModel.getStatus() == null && pickupDateTime.before(new Date())) {
				bookingModel.setStatus("Expired");
			}
			String dispatchCarNo = "";
		/*	if (bookingDetailModel.getDutySlipModel() != null)*/
				/*dispatchCarNo = bookingDetailModel.getDutySlipModel().getCarDetailModel().getRegistrationNo();*/
			dataString = dataString + " <tr> ";
			dataString += "<td> <img src=" + sImageSrc + " id='sig' width='16px' height='16px'/>  </td>"
					+ " <td id='Dispatch_" + lId + "'>" + " <a href='javascript:dispatch(" + bookingModel.getId()
					+ ")' class='btn btn-primary btn-xs'>Dispatch</a><label >" + dispatchCarNo + "</label></td> ";
			dataString += " <td><textarea id='BookingNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingModel.getBookingNo() + "</textarea></td>";
			dataString += " <td style='display:none;'><textarea id='startTime_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingModel.getStartTime() + "</textarea></td>";

			dataString += " <td id='MOB_"
					+ lId
					+ "'>Postpaid</td> "
					+ " <td><textarea id='pickupDate_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
					+ sPickUpDate
					+ " </textarea></td>"
					+ " <td><textarea id='pickupTime_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:35px; ' >"
					+ bookingModel.getPickUpTime()
					+ "</textarea></td>"
					+ " <td><textarea id='companyName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:95px; ' >"
					+ bookingModel.getCompany().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px ' >"
					+ bookingModel.getBookingTakenBy().getUserName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedForName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:66px; ' >"
					+ bookingModel.getCustomerName()
					+ "</textarea></td>"
					+ " <td><textarea id='mobileNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:80px; ' >"
					+ bookingModel.getCustomerId().getMobileNo()
					+ "</textarea></td>"
					+ " <td><textarea id='pickupLoc_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingModel.getReportingAddress()
					+ "</textarea></td>"
					+ " <td><textarea id='dropAt_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingModel.getToBeRealeseAt()
					+ "</textarea></td>"
					+ " <td><textarea id='splIns_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingModel.getInstruction()
					+ "</textarea></td>"
					+ " <td><textarea id='rentalType_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingModel.getRentalType().getName()
					+ "</textarea>"
					+ "<textarea id='tariff_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; display:none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingModel.getTariff().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='carModel_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ bookingModel.getCarModel().getName() + "</textarea>"
					+ "<input type='hidden' id=carModelId_" + lId + " value='"
					+ bookingModel.getCarModel().getId() + "'/></td>";
			dataString += " <td  style='display:none' id='status_"
					+ lId
					+ "'>"
					+ bookingModel.getStatus()
					+ "</td>"
					+ " <td id='Action_"
					+ lId
					+ "'>"
					+ "     <a href='#' onclick = 'formFillForEditDetailRecord("
					+ bookingModel.getId()
					+ ");' id='editButton_"
					+ bookingModel.getId()
					+ "'><span style='color:green'>Edit&nbsp;</span></a> "
					+ " 	<a href='#' onclick = 'cancelRecord("
					+ bookingModel.getId()
					+ ");'><span style='color:red'>Cancel</span></a></td>"
					+ " <td ><textarea id='BookingDate_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
					+ sBookingDate + "</textarea></td>";
			/*
			 * +
			 * " <td><textarea cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:65px; ' >System</textarea></td>"
			 * ;
			 */
			dataString += " <td ><textarea id='bookingTakenBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingModel.getBookingTakenBy().getUserName() + "</textarea></td>"
					+ " <td id='bookedFor_" + lId + "' style='display:none;' >"
					+ bookingModel.getCustomerName() + "</td>"
					+ " <td id='dispatchCar_" + lId + "' style='display:none;' >" + dispatchCarNo + "</td>" + " </tr>";
		}
		dataString = dataString + "</tbody></table>";
		return dataString;
	}
	

	public String showGridJobsAdvance(List<BookingModel> bookingModelData){
		String dataString="";
		SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dataString = " <table style='table-layout:fixed; width:99%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				   + " <thead style='background-color: #FED86F;'>" + " <tr>";
		dataString += " <th width='92px' >Reserve</th>" + "    <th width='80px' >Allocataion cum Dispatch</th>";
		dataString += " 	<th width='77px' >Booking No</th>";
		dataString += " 	<th  style='display:none; width='77px' >StartTime</th>";
		dataString += " 	<th width='45px' >Booking Mode</th>";
		dataString += " 	<th width='60px' >Pickup Date</th>" + " 	<th width='35px' >Pickup Time</th>"
				   + " 	<th width='95px' >Company Name</th>" + " 	<th width='60px' >Booked By</th>"
				   + " 	<th width='66px' >Client Name</th>" + " 	<th width='80px' >Client Mobile No</th>"
				   + " 	<th width='150px' >Pickup Location</th>" + " 	<th width='150px' >Drop Location</th>"
				   + " 	<th width='150px' >Special Instruction</th>" + " 	<th width='45px' >Rental Type</th>"
				   + " 	<th width='50px' >Car Type</th>" ;
		dataString += " 	<th width='50px' >Job Status</th>" + " 	<th width=" + Message.ACTION_SIZE
				   + ">Action Mode</th>"
				   +"<th width='60px' >Created Date & Time</th>" ;
		dataString +=  " 	<th width='45px' >Booking Taken By</th>"
			   	   + " 	<th style='display:none;' width='25px' >Booked For</th>"
			   	   + " 	<th style='display:none;' width='25px' >dispatchCar</th>"
				   + " </tr>" + " </thead>"
				   + " <tbody>";
		if (bookingModelData == null) {
			return dataString + "</tbody></table>";
		}
//		String dsDate="";
		for (BookingModel bookingModel : bookingModelData) {
			String sPickUpDate = bookingModel.getPickUpDate() == null ? "" : dateFormat.format(bookingModel
					.getPickUpDate());
			String sBookingDate = bookingModel.getBookingDate() == null ? ""
					: dateFormatWithTime.format(bookingModel.getBookingDate());
/*			if(bookingDetailModel.getDutySlipModel()!=null)
			{
				if(bookingDetailModel.getDutySlipModel().getDutySlipCreatedByDate()!=null){
				dsDate=dateFormatWithTime.format(bookingDetailModel.getDutySlipModel().getDutySlipCreatedByDate());
				}	
				else{
				dsDate="";
				}
			}
			else{
				dsDate="";
			}
*/			long lId = bookingModel.getId();
//			String sImageSrc = "";
			Date pickupDateTime = null;
			try {
				pickupDateTime = dateFormatWithTime.parse(sPickUpDate + " " + bookingModel.getPickUpTime());
			} catch (ParseException e) {
				logger.error("",e);
			}
		

/*			if (getDateDiff(new Date(), pickupDateTime, TimeUnit.MINUTES) <= Long.valueOf("30")
					&& pickupDateTime.after(new Date())) {
				sImageSrc = "'./img/red-signal.png'";
			} else if (getDateDiff(new Date(), pickupDateTime, TimeUnit.MINUTES) <= Long.valueOf("120")
					&& pickupDateTime.after(new Date())) {
				sImageSrc = "'./img/yellow-signal.png'";
			} else if (pickupDateTime.after(new Date())) {
				sImageSrc = "'./img/green-signal.png'";
			}
*/
			if (bookingModel.getStatus() == null) {
			    bookingModel.setStatus("Advance");
			}else if (bookingModel.getStatus().equals("N")) {
				bookingModel.setStatus("Advance");
			} else if (bookingModel.getStatus().equals("A")) {
				bookingModel.setStatus("Allocated");
			} else if (bookingModel.getStatus().equals("D")) {
				bookingModel.setStatus("Dispatched");
			} else if (bookingModel.getStatus().equals("C")) {
				bookingModel.setStatus("Cancelled");
			} else if (bookingModel.getStatus().equals("M")) {
				bookingModel.setStatus("Reserved");
			} else if (bookingModel.getStatus() == null && pickupDateTime.before(new Date())) {
				bookingModel.setStatus("Expired");
			}
			String dispatchCarNo="";
			if(bookingModel.gettDutySlipModel()!=null)
				dispatchCarNo=bookingModel.gettDutySlipModel().getCarDetailModel().getRegistrationNo();
			dataString = dataString + " <tr> ";
			dataString += "<td  style ='text-align : center;' id='Reserve_" + lId + "'><a href='javascript:reserve("+ bookingModel.getId() + ")' class='btn btn-primary btn-xs'>"+ (bookingModel.getStatus().equals("Reserved") ? "Dispatch" : "Reserve") +"</a>";
			if(bookingModel.getStatus().equals("Reserved")){
				dataString += "<a id='unreserveBtn' href='javascript:unreserve("+ bookingModel.getId() + ")' class='btn btn-primary btn-xs' style='display:block; background-color: #ff9300; margin-top: 4px;'>Unreserve</a><label style='margin-top:4px;'>"+dispatchCarNo+"</label></td> ";
			} 
			dataString += "<td style ='text-align : center;' id='Dispatch_" + lId + "'>" + " <a href='javascript:dispatch("+ bookingModel.getId() + ")' class='btn btn-primary btn-xs'>"
						+ (bookingModel.getStatus().equals("Allocated") ? "Dispatch" : "Allocate") + "</a>";
			if(bookingModel.getStatus().equals("Allocated")){
				dataString += "<a id='deallocateBtn' href='javascript:deallocate("+ bookingModel.getId() + ")' class='btn btn-success btn-xs' style='display:block; background-color: #ff9300; margin-top: 4px;'>Deallocate </a> <label style='margin-top:4px;'>"+dispatchCarNo+"</label></td> ";
			}
			dataString  += " <td><textarea id='BookingNo_"
					    + lId
					    + "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					    + bookingModel.getBookingNo() + "</textarea>"
			    		+ "<textarea id='BookingMasterId_"+ lId
					    + "' cols='26' rows='3' readonly='' style='border: medium none; display: none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					    + bookingModel.getId() + "</textarea></td>";
			 dataString += " <td style='display:none;'><textarea id='startTime_"
					    + lId
					    + "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					    + bookingModel.getStartTime() + "</textarea></td>";
		     dataString += " <td id='MOB_"
						+ lId
						+ "'>Postpaid</td> "
						+ " <td><textarea id='pickupDate_"
						+ lId
						+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
						+ sPickUpDate
						+ " </textarea></td>"
						+ " <td><textarea id='pickupTime_"
						+ lId
						+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:35px; ' >"
						+ bookingModel.getPickUpTime()
						+ "</textarea></td>"
						+ " <td><textarea id='corporateName_"
						+ lId
						+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:95px; ' >"
						+ bookingModel.getCompany().getName()
						+ "</textarea></td>"
						+ " <td><textarea id='bookedBy_"
						+ lId
						+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px ' >"
						+ bookingModel.getBookingTakenBy().getUserFirstName()
						+ "</textarea></td>"
						+ " <td><textarea id='bookedForName_"
						+ lId
						+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:66px; ' >"
						+ bookingModel.getCustomerName()
						+ "</textarea></td>"
						+ " <td><textarea id='mobileNo_"
						+ lId
						+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:80px; ' >"
						+ bookingModel.getMobileNo()
						+ "</textarea></td>"
						+ " <td><textarea id='pickupLoc_"
						+ lId
						+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
						+ bookingModel.getReportingAddress()
						+ "</textarea></td>"
						+ " <td><textarea id='dropAt_"
						+ lId
						+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
						+ bookingModel.getToBeRealeseAt()
						+ "</textarea></td>"
						+ " <td><textarea id='splIns_"
						+ lId
						+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
						+ bookingModel.getInstruction()
						+ "</textarea></td>"
						+ " <td><textarea id='rentalType_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
						+ bookingModel.getRentalType().getName()
						+ "</textarea>"
						+ "<textarea id='tariff_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; display:none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
						+ bookingModel.getTariff().getName()
						+ "</textarea></td>"
						+ " <td><textarea id='carModel_"
						+ lId
						+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
						+ bookingModel.getCarModel().getName() + "</textarea>"
						+ "<input type='hidden' id=carModelId_"+lId+" value='"+bookingModel.getCarModel().getId()+"'/></td>";
		     dataString += " <td><textarea id='status_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
						+ bookingModel.getStatus() + "</textarea></td>" + " <td id='Action_" + lId + "'>"
						+ "     <a href='#' onclick = 'formFillForEditDetailRecord(" + bookingModel.getId()
						+ ");' id='editButton_" + bookingModel.getId()
						+ "'><span style='color:green'>Edit&nbsp;</span></a> "
						+ " 	<a href='#' onclick = 'cancelRecord(" + bookingModel.getId()
						+ ");'><span style='color:red'>Cancel</span></a></td>"
						+" <td ><textarea id='BookingDate_"
						+ lId
						+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
						+ sBookingDate
						+ "</textarea></td>";
					/*	+ " <td><textarea cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:65px; ' >System</textarea></td>";*/
			 dataString +=  " <td ><textarea id='bookingTakenBy_"
						+ lId
						+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
						+ bookingModel.getBookingTakenBy().getUserName() + "</textarea></td>"
						+ " <td id='bookedFor_" + lId + "' style='display:none;' >"
						+ bookingModel.getCustomerName() + "</td>" 
						+ " <td id='dispatchCar_" + lId + "' style='display:none;' >"
						+ dispatchCarNo+ "</td>" + " </tr>";
			}
		dataString = dataString + "</tbody></table>";
		return dataString;
	}
	
	public String showGridJobsStatus(List<BookingModel> bookingModelData) {
		String dataString = "";
		SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dataString = " <table style='table-layout:fixed; width:99%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>" + " <tr>";
		dataString += " 	<th width='77px' >Booking No</th>";
		dataString += " 	<th  style='display:none; width='77px' >StartTime</th>";
		dataString += " 	<th width='90px' >Duty Slip No</th>";
		dataString += " 	<th width='45px' >Booking Mode</th>";
		dataString += " 	<th width='60px' >Pickup Date</th>" + " 	<th width='35px' >Pickup Time</th>"
				+ " 	<th width='95px' >Company Name</th>" + " 	<th width='60px' >Booked By</th>"
				+ " 	<th width='66px' >Client Name</th>" + " 	<th width='80px' >Client Mobile No</th>"
				+ " 	<th width='150px' >Pickup Location</th>" + " 	<th width='150px' >Drop Location</th>"
				+ " 	<th width='150px' >Special Instruction</th>" + " 	<th width='45px' >Rental Type</th>"
				+ " 	<th width='50px' >Car Type</th>";
		dataString += "<th width='57px'>Job Status</th>" + " 	<th width='67px' >Car No</th>"
				+ " 	<th width='75px' >Chauffeur</th>" + " 	<th width='75px' >Chauffeur Mobile No</th>"
				+ " 	<th width='65px' >Dispatched By</th>" + "<th width='60px' >Ds Date & Time</th>";
		dataString += " 	<th width='45px' >Booking Taken By</th>"
				+ " 	<th style='display:none;' width='25px' >Booked For</th>"
				+ " 	<th style='display:none;' width='25px' >dispatchCar</th>"
				+ " </tr>" + " </thead>"
				+ " <tbody>";
		if (bookingModelData == null) {
			return dataString + "</tbody></table>";
		}
		String dsDate = "";
		for (BookingModel bookingModel : bookingModelData) {
			String sPickUpDate = bookingModel.getPickUpDate() == null ? "" : dateFormat.format(bookingModel
					.getPickUpDate());
/*			String sBookingDate = bookingDetailModel.getBookingMasterModel().getBookingDate() == null ? ""
					: dateFormatWithTime.format(bookingDetailModel.getBookingMasterModel().getBookingDate());
*/			if (bookingModel.gettDutySlipModel() != null) {
				if (bookingModel.gettDutySlipModel().getDutySlipCreatedByDate() != null) {
					dsDate = dateFormatWithTime
							.format(bookingModel.gettDutySlipModel().getDutySlipCreatedByDate());
				} else {
					dsDate = "";
				}
			} else {
				dsDate = "";
			}
			long lId = bookingModel.getId();
//			String sImageSrc = "";
			Date pickupDateTime = null;
			try {
				pickupDateTime = dateFormatWithTime.parse(sPickUpDate + " " + bookingModel.getPickUpTime());
			} catch (ParseException e) {
				logger.error("", e);
			}
/*			if (getDateDiff(new Date(), pickupDateTime, TimeUnit.MINUTES) <= Long.valueOf("30")
					&& pickupDateTime.after(new Date())) {
				sImageSrc = "'./img/red-signal.png'";
			} else if (getDateDiff(new Date(), pickupDateTime, TimeUnit.MINUTES) <= Long.valueOf("120")
					&& pickupDateTime.after(new Date())) {
				sImageSrc = "'./img/yellow-signal.png'";
			} else if (pickupDateTime.after(new Date())) {
				sImageSrc = "'./img/green-signal.png'";
			}
*/
			if (bookingModel.getStatus() == null) {
				bookingModel.setStatus("New");
			} else if (bookingModel.getStatus().equals("N")) {
				bookingModel.setStatus("Advance");
			} else if (bookingModel.getStatus().equals("A")) {
				bookingModel.setStatus("Allocated");
			} else if (bookingModel.getStatus().equals("D")) {
				bookingModel.setStatus("Dispatched");
			} else if (bookingModel.getStatus().equals("X")) {
				bookingModel.setStatus("Cancelled");
			} else if (bookingModel.getStatus().equals("R")) {
				bookingModel.setStatus("Reserved");
			} else if (bookingModel.getStatus() == null && pickupDateTime.before(new Date())) {
				bookingModel.setStatus("Expired");
			}
			String sDispatchedBy = "";
			String sCarRegNo = "";
			String sChaufferName = "";
			String sChaufferMobile = "";
			if (bookingModel.gettDutySlipModel() != null) {
				if (bookingModel.gettDutySlipModel().getCarDetailModel() != null)
					sCarRegNo = bookingModel.gettDutySlipModel().getCarDetailModel().getRegistrationNo();
				if (bookingModel.gettDutySlipModel().getChauffeurModel() != null) {
					sChaufferName = bookingModel.gettDutySlipModel().getChauffeurModel().getName();
					sChaufferMobile = bookingModel.gettDutySlipModel().getChauffeurModel().getMobileNo();
				} else if (bookingModel.gettDutySlipModel().getIsUpdatedChauffeur() != null
						&& bookingModel.gettDutySlipModel().getIsUpdatedChauffeur().equals("Y")) {
					sChaufferName = bookingModel.gettDutySlipModel().getChauffeurName();
					sChaufferMobile = bookingModel.gettDutySlipModel().getChauffeurMobile();
				}
				if (bookingModel.gettDutySlipModel().getDispatchedBy() != null)
					sDispatchedBy = bookingModel.gettDutySlipModel().getDispatchedBy().getUserName();
			}
			String dispatchCarNo = "";
			if (bookingModel.gettDutySlipModel() != null)
				dispatchCarNo = bookingModel.gettDutySlipModel().getCarDetailModel().getRegistrationNo();
			dataString = dataString + " <tr> ";
			dataString += " <td><textarea id='BookingNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingModel.getBookingNo() + "</textarea></td>";
			dataString += " <td style='display:none;'><textarea id='startTime_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingModel.getStartTime() + "</textarea></td>";
			if (bookingModel.getStatus().equals("Allocated")) {
				dataString += " <td id='DutySlip_" + lId + "'>" + "<a href='javascript:viewDutySlip("
						+ bookingModel.getId()
						+ ")' class='btn btn-primary btn-xs' disabled='disbaled'>View Duty Slip</a></td> ";
			} else {
				dataString += " <td id='DutySlip_" + lId + "'>" + "<a href='javascript:viewDutySlip("
						+ bookingModel.getId() + ")'>" + bookingModel.gettDutySlipModel().getDutySlipNo()
						+ "</a></td> ";
			}

			dataString += " <td id='MOB_"
					+ lId
					+ "'>Postpaid</td> "
					+ " <td><textarea id='pickupDate_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
					+ sPickUpDate
					+ " </textarea></td>"
					+ " <td><textarea id='pickupTime_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:35px; ' >"
					+ bookingModel.getPickUpTime()
					+ "</textarea></td>"
					+ " <td><textarea id='corporateName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:95px; ' >"
					+ bookingModel.getCompany().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px ' >"
					+ bookingModel.getBookingTakenBy().getUserFirstName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedForName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:66px; ' >"
					+ bookingModel.getCustomerName()
					+ "</textarea></td>"
					+ " <td><textarea id='mobileNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:80px; ' >"
					+ bookingModel.getMobileNo()
					+ "</textarea></td>"
					+ " <td><textarea id='pickupLoc_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingModel.getReportingAddress()
					+ "</textarea></td>"
					+ " <td><textarea id='dropAt_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingModel.getToBeRealeseAt()
					+ "</textarea></td>"
					+ " <td><textarea id='splIns_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingModel.getInstruction()
					+ "</textarea></td>"
					+ " <td><textarea id='rentalType_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingModel.getRentalType().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='carModel_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ bookingModel.getCarModel().getName() + "</textarea>"
					+ "<input type='hidden' id=carModelId_" + lId + " value='"
					+ bookingModel.getCarModel().getId() + "'/></td>";
			dataString += " <td><textarea id='status_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:57px; ' >"
					+ bookingModel.getStatus()
					+ "</textarea></td>"
					+ " <td><textarea cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:67px; ' >"
					+ sCarRegNo
					+ "</textarea></td>"
					+ " <td><textarea cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"
					+ sChaufferName
					+ "</textarea></td>"
					+ " <td><textarea cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"
					+ sChaufferMobile
					+ "</textarea></td>"
					+ " <td><textarea cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:65px; ' >"
					+ sDispatchedBy
					+ "</textarea></td>"
					+ " <td ><textarea id='BookingDate_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
					+ dsDate + "</textarea></td>";
			/*
			 * +
			 * " <td><textarea cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:65px; ' >System</textarea></td>"
			 * ;
			 */
			dataString += " <td ><textarea id='bookingTakenBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingModel.getBookingTakenBy().getUserName() + "</textarea></td>"
					+ " <td id='bookedFor_" + lId + "' style='display:none;' >"
					+ bookingModel.getCustomerName()+ "</td>" + " <td id='reportAt_" + lId
					+ " <td id='dispatchCar_" + lId + "' style='display:none;' >" + dispatchCarNo + "</td>" + " </tr>";
		}
		dataString = dataString + "</tbody></table>";
		return dataString;
	}
	
	public String showGridJobsCancelled(List<BookingModel> bookingModelData) {
		String dataString = "";
		SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dataString = " <table style='table-layout:fixed; width:99%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>" + " <tr>";
		dataString += " 	<th width='77px' >Booking No</th>";
		dataString += " 	<th  style='display:none; width='77px' >StartTime</th>";
		dataString += " 	<th width='45px' >Booking Mode</th>";
		dataString += " 	<th width='60px' >Pickup Date</th>" + " 	<th width='35px' >Pickup Time</th>"
				+ " 	<th width='95px' >Company Name</th>" + " 	<th width='60px' >Booked By</th>"
				+ " 	<th width='66px' >Client Name</th>" + " 	<th width='80px' >Client Mobile No</th>"
				+ " 	<th width='150px' >Pickup Location</th>" + " 	<th width='150px' >Drop Location</th>"
				+ " 	<th width='150px' >Special Instruction</th>" + " 	<th width='45px' >Rental Type</th>"
				+ " 	<th width='50px' >Car Type</th>";
		dataString += "<th width='50px'>Job Status</th>" + " 	<th width='65px' >Reason of cancelation</th>"
				+ " 	<th width='75px' >Cancelled Date & Time</th>" + " 	<th width='75px' >Cancelled By</th>"
				+ "<th width='60px' >Created Date & Time</th>";
		dataString += " 	<th width='45px' >Booking Taken By</th>"
				+ " 	<th style='display:none;' width='25px' >Booked For</th>"
				+ " 	<th style='display:none;' width='25px' >dispatchCar</th>"
			    + " </thead>"
				+ " <tbody>";
		if (bookingModelData == null) {
			return dataString + "</tbody></table>";
		}
//		String dsDate = "";
		for (BookingModel bookingModel : bookingModelData) {
			String sPickUpDate = bookingModel.getPickUpDate() == null ? "" : dateFormat.format(bookingModel
					.getPickUpDate());
			String sBookingDate = bookingModel.getBookingDate() == null ? ""
					: dateFormatWithTime.format(bookingModel.getBookingDate());
/*			if (bookingDetailModel.getDutySlipModel() != null) {
				if (bookingDetailModel.getDutySlipModel().getDutySlipCreatedByDate() != null) {
					dsDate = dateFormatWithTime
							.format(bookingDetailModel.getDutySlipModel().getDutySlipCreatedByDate());
				} else {
					dsDate = "";
				}
			} else {
				dsDate = "";
			}
*/			long lId = bookingModel.getId();
//			String sImageSrc = "";
			Date pickupDateTime = null;
			try {
				pickupDateTime = dateFormatWithTime.parse(sPickUpDate + " " + bookingModel.getPickUpTime());
			} catch (ParseException e) {
				logger.error("", e);
			}
/*			if (getDateDiff(new Date(), pickupDateTime, TimeUnit.MINUTES) <= Long.valueOf("30")
					&& pickupDateTime.after(new Date())) {
				sImageSrc = "'./img/red-signal.png'";
			} else if (getDateDiff(new Date(), pickupDateTime, TimeUnit.MINUTES) <= Long.valueOf("120")
					&& pickupDateTime.after(new Date())) {
				sImageSrc = "'./img/yellow-signal.png'";
			} else if (pickupDateTime.after(new Date())) {
				sImageSrc = "'./img/green-signal.png'";
			}
*/
			if (bookingModel.getStatus() == null) {
				bookingModel.setStatus("New");

			} else if (bookingModel.getStatus().equals("N")) {
				bookingModel.setStatus("Advance");
			} else if (bookingModel.getStatus().equals("A")) {
				bookingModel.setStatus("Allocated");
			} else if (bookingModel.getStatus().equals("D")) {
				bookingModel.setStatus("Dispatched");
			} else if (bookingModel.getStatus().equals("C")) {
				bookingModel.setStatus("Cancelled");
			} else if (bookingModel.getStatus().equals("R")) {
				bookingModel.setStatus("Reserved");
			} else if (bookingModel.getStatus() == null && pickupDateTime.before(new Date())) {
				bookingModel.setStatus("Expired");
			}
			String sCancelDate = "";
			String sCancelledBy = "";
			if (bookingModel.getCancelDate() != null)
				sCancelDate = dateFormatWithTime.format(bookingModel.getCancelDate());
			if (bookingModel.getCancelBy() != null)
				sCancelledBy = bookingModel.getCancelBy().getUserName();
			String dispatchCarNo = "";
			if (bookingModel.gettDutySlipModel() != null)
				dispatchCarNo = bookingModel.gettDutySlipModel().getCarDetailModel().getRegistrationNo();
			dataString = dataString + " <tr> ";
			dataString += " <td><textarea id='BookingNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingModel.getBookingNo() + "</textarea></td>";
			dataString += " <td style='display:none;'><textarea id='startTime_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingModel.getStartTime() + "</textarea></td>";
			dataString += " <td id='MOB_"
					+ lId
					+ "'>Postpaid</td> "
					+ " <td><textarea id='pickupDate_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
					+ sPickUpDate
					+ " </textarea></td>"
					+ " <td><textarea id='pickupTime_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:35px; ' >"
					+ bookingModel.getPickUpTime()
					+ "</textarea></td>"
					+ " <td><textarea id='corporateName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:95px; ' >"
					+ bookingModel.getCompany().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px ' >"
					+ bookingModel.getBookingTakenBy().getUserName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedForName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:66px; ' >"
					+ bookingModel.getCustomerName()
					+ "</textarea></td>"
					+ " <td><textarea id='mobileNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:80px; ' >"
					+ bookingModel.getMobileNo()
					+ "</textarea></td>"
					+ " <td><textarea id='pickupLoc_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingModel.getReportingAddress()
					+ "</textarea></td>"
					+ " <td><textarea id='dropAt_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingModel.getToBeRealeseAt()
					+ "</textarea></td>"
					+ " <td><textarea id='splIns_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingModel.getInstruction()
					+ "</textarea></td>"
					+ " <td><textarea id='rentalType_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingModel.getRentalType().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='carModel_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ bookingModel.getCarModel().getName() + "</textarea>"
					+ "<input type='hidden' id=carModelId_" + lId + " value='"
					+ bookingModel.getCarModel().getId() + "'/></td>";
			dataString += " <td ><textarea id='status_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ bookingModel.getStatus()
					+ "</textarea></td>"
					+ " <td><textarea cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:65px; ' > "
					+ bookingModel.getCancelReason()
					+ "</textarea></td>"
					+ " <td><textarea cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' > "
					+ sCancelDate
					+ "</textarea></td>"
					+ " <td><textarea cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"
					+ sCancelledBy
					+ "</textarea></td>"
					+ " <td ><textarea id='BookingDate_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
					+ sBookingDate + "</textarea></td>";
			/*
			 * +
			 * " <td><textarea cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:65px; ' >System</textarea></td>"
			 * ;
			 */
			dataString += " <td ><textarea id='bookingTakenBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingModel.getBookingTakenBy().getUserName() + "</textarea></td>"
					+ " <td id='bookedFor_" + lId + "' style='display:none;' >"
					+ bookingModel.getCustomerName() + "</td>" 
					+ " <td id='dispatchCar_" + lId + "' style='display:none;' >" + dispatchCarNo + "</td>" + " </tr>";
		}
		dataString = dataString + "</tbody></table>";
		return dataString;

	}
	
	public String showGridJobsExpired(List<BookingModel> bookingModelData) {
		String dataString = "";
		SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dataString = " <table style='table-layout:fixed; width:99%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>" + " <tr>";
		dataString += " 	<th width='77px' >Booking No</th>";
		dataString += " 	<th  style='display:none; width='77px' >StartTime</th>";
		dataString += " 	<th width='45px' >Booking Mode</th>";
		dataString += " 	<th width='60px' >Pickup Date</th>" + " 	<th width='35px' >Pickup Time</th>"
				+ " 	<th width='95px' >Company Name</th>" + " 	<th width='60px' >Booked By</th>"
				+ " 	<th width='66px' >Client Name</th>" + " 	<th width='80px' >Client Mobile No</th>"
				+ " 	<th width='150px' >Pickup Location</th>" + " 	<th width='150px' >Drop Location</th>"
				+ " 	<th width='150px' >Special Instruction</th>" + " 	<th width='45px' >Rental Type</th>"
				+ " 	<th width='50px' >Car Type</th>";
		dataString += "<th width='50px'>Job Status</th>" + " 	<th width='100px' >Expired Date & Time</th>"
				+ " 	<th width='65px' >Expired By</th>" + "<th width='60px' >Created Date & Time</th>";

		dataString += " 	<th width='45px' >Booking Taken By</th>"
				+ " 	<th style='display:none;' width='25px' >Booked For</th>"
				+ " 	<th style='display:none;' width='25px' >dispatchCar</th>"
				+ " </tr>" + " </thead>"
				+ " <tbody>";
		if (bookingModelData == null) {
			return dataString + "</tbody></table>";
		}
//		String dsDate = "";
		for (BookingModel bookingModel : bookingModelData) {
			String sPickUpDate = bookingModel.getPickUpDate() == null ? "" : dateFormat.format(bookingModel
					.getPickUpDate());
			String sBookingDate = bookingModel.getBookingDate() == null ? ""
					: dateFormatWithTime.format(bookingModel.getBookingDate());
/*			if (bookingDetailModel.getDutySlipModel() != null) {
				if (bookingDetailModel.getDutySlipModel().getDutySlipCreatedByDate() != null) {
					dsDate = dateFormatWithTime
							.format(bookingDetailModel.getDutySlipModel().getDutySlipCreatedByDate());
				} else {
					dsDate = "";
				}
			} else {
				dsDate = "";
			}
*/			long lId = bookingModel.getId();
//			String sImageSrc = "";
			Date pickupDateTime = null;
			try {
				pickupDateTime = dateFormatWithTime.parse(sPickUpDate + " " + bookingModel.getPickUpTime());
			} catch (ParseException e) {
				logger.error("", e);
			}

/*			if (getDateDiff(new Date(), pickupDateTime, TimeUnit.MINUTES) <= Long.valueOf("30")
					&& pickupDateTime.after(new Date())) {
				sImageSrc = "'./img/red-signal.png'";
			} else if (getDateDiff(new Date(), pickupDateTime, TimeUnit.MINUTES) <= Long.valueOf("120")
					&& pickupDateTime.after(new Date())) {
				sImageSrc = "'./img/yellow-signal.png'";
			} else if (pickupDateTime.after(new Date())) {
				sImageSrc = "'./img/green-signal.png'";
			}
*/			if (bookingModel.getStatus() == null) {
			bookingModel.setStatus("New");
			} else if (bookingModel.getStatus().equals("N")) {
				bookingModel.setStatus("Advance");
			} else if (bookingModel.getStatus().equals("A")) {
				bookingModel.setStatus("Allocated");
			} else if (bookingModel.getStatus().equals("D")) {
				bookingModel.setStatus("Dispatched");
			} else if (bookingModel.getStatus().equals("C")) {
				bookingModel.setStatus("Cancelled");
			} else if (bookingModel.getStatus().equals("R")) {
				bookingModel.setStatus("Reserved");
			} else if (bookingModel.getStatus() == null && pickupDateTime.before(new Date())) {
				bookingModel.setStatus("Expired");
			}

			String dispatchCarNo = "";
			if (bookingModel.gettDutySlipModel() != null)
				dispatchCarNo = bookingModel.gettDutySlipModel().getCarDetailModel().getRegistrationNo();
			dataString = dataString + " <tr> ";
			dataString += " <td><textarea id='BookingNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingModel.getBookingNo() + "</textarea></td>";
			dataString += " <td style='display:none;'><textarea id='startTime_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingModel.getStartTime() + "</textarea></td>";
			dataString += " <td id='MOB_"
					+ lId
					+ "'>Postpaid</td> "
					+ " <td><textarea id='pickupDate_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
					+ sPickUpDate
					+ " </textarea></td>"
					+ " <td><textarea id='pickupTime_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:35px; ' >"
					+ bookingModel.getPickUpTime()
					+ "</textarea></td>"
					+ " <td><textarea id='corporateName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:95px; ' >"
					+ bookingModel.getCompany().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px ' >"
					+ bookingModel.getBookingTakenBy().getUserName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedForName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:66px; ' >"
					+ bookingModel.getCustomerName()
					+ "</textarea></td>"
					+ " <td><textarea id='mobileNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:80px; ' >"
					+ bookingModel.getMobileNo()
					+ "</textarea></td>"
					+ " <td><textarea id='pickupLoc_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingModel.getReportingAddress()
					+ "</textarea></td>"
					+ " <td><textarea id='dropAt_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingModel.getToBeRealeseAt()
					+ "</textarea></td>"
					+ " <td><textarea id='splIns_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingModel.getInstruction()
					+ "</textarea></td>"
					+ " <td><textarea id='rentalType_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingModel.getRentalType().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='carModel_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ bookingModel.getCarModel().getName() + "</textarea>"
					+ "<input type='hidden' id=carModelId_" + lId + " value='"
					+ bookingModel.getCarModel().getId() + "'/></td>";
			dataString += " <td><textarea id='status_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ bookingModel.getStatus()
					+ "</textarea></td>"
					+ "<td><textarea cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:100px; ' >"
					+ pickupDateTime
					+ "</textarea></td>"
					+ "<td><textarea cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:65px; ' >System</textarea></td>"
					+ " <td ><textarea id='BookingDate_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
					+ sBookingDate + "</textarea></td>";
			dataString += " <td ><textarea id='bookingTakenBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingModel.getBookingTakenBy().getUserName() + "</textarea></td>"
					+ " <td id='bookedFor_" + lId + "' style='display:none;' >"
					+ bookingModel.getCustomerName() + "</td>" 
					+ " <td id='dispatchCar_" + lId + "' style='display:none;' >" + dispatchCarNo + "</td>" + " </tr>";
		}
		dataString = dataString + "</tbody></table>";
		return dataString;

	}
	
	public String showGridJobs(List<BookingModel> bookingModelData, String jobType) {
		String dataString = "";
		if (jobType.equals("Current")) {
			return showGridJobsCurrent(bookingModelData);
		}else if(jobType.equals("Advance")) {
			return showGridJobsAdvance(bookingModelData);
		}else if(jobType.equals("Status")) {
			return showGridJobsStatus(bookingModelData);
		} else if(jobType.equals("Cancelled")) {
			return showGridJobsCancelled(bookingModelData);
		} else if(jobType.equals("Expired")) {
			return showGridJobsExpired(bookingModelData);
		} 		
		return dataString;
	}


	
	public long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}
	
	@RequestMapping(value = "/tAllocateOrDispatch", method = RequestMethod.POST)
	public @ResponseBody JsonResponse tAllocateOrDispatch(
			@ModelAttribute(value = "tDutySlipModel") TDutySlipModel tDutySlipModel, BindingResult bindingResult,
			HttpSession session, @RequestParam(value = "alDiTime") String alDiTime,
			@RequestParam(value = "smsClient", required = false) String smsClient,
			@RequestParam(value = "smsBooker", required = false) String smsBooker,
			@RequestParam(value = "dispatchSmsOther", required = false) String dispatchSmsOther,
			@RequestParam(value = "dutySlipIdForUpdate") String dutySlipIdForUpdate,
			@RequestParam(value = "oType") String oType) {
		JsonResponse res = new JsonResponse();
		if (!bindingResult.hasErrors()) {
			try {
				tDutySlipModel.setDutySlipCreatedByDate(new Date());
				SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				String resultMessage = null;
				if (oType.equals("allocate")) {
					tDutySlipModel.setAllocationDateTime(dateTimeFormat.parse(dateFormat.format(tDutySlipModel
							.getAllocationDateTime()) + " " + alDiTime));
					resultMessage = "Entry Successfully Allocated.";
				} else if (oType.equals("dispatch")) {
					tDutySlipModel.setDispatchDateTime(dateTimeFormat.parse(dateFormat.format(tDutySlipModel
							.getDispatchDateTime()) + " " + alDiTime));

					resultMessage = "Entry Successfully Dispatched.";
				} else {
					tDutySlipModel.setAllocationDateTime(dateTimeFormat.parse(dateFormat.format(tDutySlipModel
							.getAllocationDateTime()) + " " + alDiTime));
					tDutySlipModel.setDispatchDateTime(dateTimeFormat.parse(dateFormat.format(tDutySlipModel
							.getDispatchDateTime()) + " " + alDiTime));
					resultMessage = "Entry Successfully Allocated And Dispatched.";
				}
				String status = tDutySlipModel.getBookingModel().getStatus();
				BookingModel booDetailModel = bookingService.formFillForEditBooking(tDutySlipModel.getBookingModel().getId());
				tDutySlipModel.setBookingModel(booDetailModel);
				tDutySlipModel.getBookingModel().setStatus(status);
				TDutySlipModel tDsModel =bookingService.getDsRowCount(tDutySlipModel.getBookingModel().getId());
				if (tDsModel == null) {
					if (dutySlipIdForUpdate == null || dutySlipIdForUpdate.equals("")
							|| dutySlipIdForUpdate.equals("0")) {
						bookingService.allocateOrDispatch(tDutySlipModel, "S");
					/*	ContactDetailModel contactDetailModel = authorizedUserService.getBookerMobile(tDutySlipModel.getBookingModel().getBookingTakenBy().getUserMobile());*/
						// Send Booking Confirmation Message
						/*
						 * String[] sVariable where: index 0 : Name 1 : Car No.
						 * 2 : Chauffeur Name 3 : Chauffeur Mobile
						 */
						String[] sSMSVariable = new String[4];
						sSMSVariable[0] = tDutySlipModel.getBookingModel().getCustomerName();
						sSMSVariable[1] = tDutySlipModel.getCarDetailModel().getRegistrationNo();
						sSMSVariable[2] = tDutySlipModel.getCarDetailModel().getCarAllocationModels().get(0)
								.getChauffeurId().getName();
						sSMSVariable[3] = tDutySlipModel.getCarDetailModel().getCarAllocationModels().get(0)
								.getChauffeurId().getMobileNo();
						if (smsClient.equals("Y")) {
							try {
								if (!tDutySlipModel.getBookingModel().getMobileNo()
										.equals("")
										&& tDutySlipModel.getBookingModel().getMobileNo()
												.length() == 10) {
									SMSSender.MyValueFirstSend("carDetailSMS", sSMSVariable, "91"
											+ tDutySlipModel.getBookingModel().getMobileNo());
								}
							} catch (Exception e) {
							}
						}
						if (smsBooker.equals("Y")) {
							try {
								if (!tDutySlipModel.getBookingModel().getBookingTakenBy().getUserMobile().equals("")
										&&tDutySlipModel.getBookingModel().getBookingTakenBy().getUserMobile().length() == 10) {
									SMSSender.MyValueFirstSend("carDetailSMS", sSMSVariable,
											"91" + tDutySlipModel.getBookingModel().getBookingTakenBy().getUserMobile());
								}
							} catch (Exception e) {
							}
						}
						if (dispatchSmsOther != null && !dispatchSmsOther.equals("")) {
							try {
								if (dispatchSmsOther.length() == 10) {
									SMSSender.MyValueFirstSend("carDetailSMS", sSMSVariable, "91" + dispatchSmsOther);
								}
							} catch (Exception e) {
							}
						}
					} else {
						tDutySlipModel.setId(Long.valueOf(dutySlipIdForUpdate));
						tDutySlipModel = bookingService.allocateOrDispatch(tDutySlipModel, "U");
					}
				} else {
					tDutySlipModel.setId(Long.valueOf(tDsModel.getId()));
					tDutySlipModel = bookingService.allocateOrDispatch(tDutySlipModel, "U");
				}

				res.settDutySlipModel(tDutySlipModel);
				res.setResult(resultMessage);
				res.setStatus("Success");
			} catch (Exception e) {
				logger.error("", e);
				res.setStatus("Failure");
				res.setResult(Message.FATAL_ERROR_MESSAGE);
			} finally {
			}
		} else {
			res.setStatus("Errors");
			res.setResult(bindingResult.getAllErrors());
			return res;
		}
		return res;
	}
	
	@RequestMapping(value = "/getTDutySlip/{bookingId}", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getTDutySlip(
			@ModelAttribute(value = "bookingModel") BookingModel bookingModel,
			BindingResult bindingResult, @PathVariable(value = "bookingId") Long bookingId,
			HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		AddressDetailModel addressDetailModels = null;
		RelatedInfoModel relatedinfoModels = null;
		List<AddressDetailModel> addressDetailModelList = bookingService.getAddress(bookingId);
		List<RelatedInfoModel> relatedInfoModelList = bookingService.getEmail(bookingId);
		if (relatedInfoModelList != null && relatedInfoModelList.size() > 0)
			relatedinfoModels = relatedInfoModelList.get(0);
		if (addressDetailModelList != null && addressDetailModelList.size() > 0)
			addressDetailModels = addressDetailModelList.get(0);
		try {
			bookingModel = bookingService.formFillForEditBooking(bookingId);
			bookingModel.setAddress(addressDetailModels);
			bookingModel.setRelatedInfo(relatedinfoModels);
			String bookingEmailTemplate = new Utilities().generateTDutySlip(bookingModel, request,
					servletContext.getRealPath("/WEB-INF/"));
			res.setDataGrid(bookingEmailTemplate);
		} catch (DataIntegrityViolationException e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FOREIGNKEY_ERROR_MESSAGE);
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}

		return res;
	}
	
	@RequestMapping(value = "/checkCarAvailabilityForTDutySlip", method = RequestMethod.POST)
	public @ResponseBody JsonResponse checkCarAvailabilityForTDutySlip(@RequestParam("carDetailModel.id") Long carDetailId,
			@RequestParam("reserveDateTime") Date[] reserveDateTime,
			@RequestParam("bookingModel.id") Long[] bookingId, HttpSession session,
			HttpServletRequest request) {
		JsonResponse res = new JsonResponse();

		try {
			List<TDutySlipModel> tDutySlipModels = new ArrayList<TDutySlipModel>();
			String dataString2 = "", id = "";
			String date = "";
			String chk = "";
			for (int i = 0; i < bookingId.length; i++) {
				tDutySlipModels = bookingService.getlist(bookingId[i], carDetailId);
				if (tDutySlipModels.size() > 0) {
					if (!date.equals(dateFormat.format(tDutySlipModels.get(0).getBookingModel().getPickUpDate()))) {
						dataString2 = dataString2
								+ " " + (dateFormat.format(tDutySlipModels.get(0).getBookingModel().getPickUpDate()));
					}
					date = (dateFormat.format(tDutySlipModels.get(0).getBookingModel().getPickUpDate()));
					id = id + " " + bookingId[i];
					chk = "Y";
				} else {
					chk = "N";
				}
			}
			res.setDataString2(dataString2 == "" ? "Car Availabel on all Date. "
					: "This Car is not Availabel on respective date :    " + dataString2);
			res.setCheck(chk);
			res.setDataString1(id);
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	@RequestMapping(value = "/updateTBookingModel", method = RequestMethod.POST)
	public @ResponseBody JsonResponse updateTBookingModel(
			@ModelAttribute(value = "bookingModel") BookingModel bookingModel,
			@RequestParam("idForUpdate") Long idForUpdate, HttpSession session) {
		JsonResponse res = new JsonResponse();
		try {
			BookingModel bookingModel1 =bookingService.formFillForEditBooking(idForUpdate);
			bookingModel1.setId(idForUpdate);
			bookingModel1.setPickUpDate(bookingModel.getPickUpDate());
			bookingModel1.setPickUpTime(bookingModel.getPickUpTime());
			bookingModel1.setStartTime(bookingModel.getStartTime());
			bookingModel1.setCompany(bookingModel.getCompany());
			bookingModel1.setCustomerName(bookingModel.getCustomerName());
			bookingModel1.setPickUpTime(bookingModel.getPickUpTime());
			bookingModel1.setMobileNo(bookingModel.getMobileNo());
			bookingModel1.setReportingAddress(bookingModel.getReportingAddress());
			bookingModel1.setToBeRealeseAt(bookingModel.getToBeRealeseAt());
			bookingModel1.setInstruction(bookingModel.getInstruction());
			bookingModel1.setRentalType(bookingModel.getRentalType());
			bookingModel1.setCarModel(bookingModel.getCarModel());
			bookingModel1.setTariff(bookingModel.getTariff());
			bookingService.update(bookingModel1);
			res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		} finally {
			res.setBookingModel(bookingModel);
		}

		return res;
	}
	
	@RequestMapping(value = "/cancelTBooking", method = RequestMethod.POST)
	public @ResponseBody JsonResponse cancelBooking(
			@ModelAttribute(value = "bookingModel") BookingModel bookingModel,
			BindingResult bindingResult, @RequestParam(value = "id") Long id, HttpSession session) {
		JsonResponse res = new JsonResponse();
		try {
			BookingModel bookingModel1=bookingService.formFillForEditBooking(id);
			bookingModel1.setCancelReason(bookingModel.getCancelReason());
			bookingModel1.setStatus("X");
			bookingModel1.setCancelDate(new Date());
			bookingModel1.setCancelBy(bookingModel.getCancelBy());
			bookingService.update(bookingModel1);
			res.setResult("Booking Successfully Cancelled..!!");
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
}
