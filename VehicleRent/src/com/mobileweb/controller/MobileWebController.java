package com.mobileweb.controller;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
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
import org.springframework.web.bind.annotation.SessionAttributes;

import com.common.global.SMSSender;
import com.corporate.model.AutorizedUserModel;
import com.corporate.service.AuthorizedUserService;
import com.corporate.service.CorporateService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.master.model.ChauffeurModel;
import com.master.model.ChauffeurStatusModel;
import com.master.service.ChauffeurMasterService;
import com.mobileweb.service.MobileWebService;
import com.operation.model.BookingDetailModel;
import com.operation.model.BookingMasterModel;
import com.operation.model.DeallocateHistoryModel;
import com.operation.model.DutySlipModel;
import com.operation.model.TripRoutMapModel;
import com.operation.service.BookingMasterService;
import com.operation.service.DutySlipService;
import com.operation.validator.BookingMasterValidator;
import com.util.DefaultOtpGenerator;
import com.util.JsonResponseWeb;
import com.util.Message;
import com.util.Utilities;

@Controller
@SessionAttributes({ })

public class MobileWebController {
	private static final Logger logger = Logger.getLogger(MobileWebController.class);
	
	@Autowired
	private MobileWebService mobileWebService;
	@Autowired
	private CorporateService corporateService;
	@Autowired
	private AuthorizedUserService authorizedUserService;
	@Autowired
	private BookingMasterService bookingMasterService;
	@Autowired
	private ChauffeurMasterService chauffeurMasterService;
	@Autowired
	private DutySlipService dutySlipService;

	private BookingMasterValidator validator = null;

	public BookingMasterValidator getValidator() {
		return validator;
	}

	
	@Autowired
	public void setValidator(BookingMasterValidator validator) {
		this.validator = validator;
	}
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields(new String[] { "id" });
		dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value="/corporateUserLogin", method= RequestMethod.POST)
	public @ResponseBody JsonResponseWeb corporateUserLogin(Map<String, Object> map,
			@RequestParam("userName") String userName, @RequestParam("password") String password,
			@RequestParam(value = "opType", required=false) String opType,
			@RequestParam(value = "userType", required=false) String userType,
			@RequestParam(value = "otp", required=false) String otp,
			HttpSession session){
		JsonResponseWeb res = new JsonResponseWeb();
		if(opType == null) opType = "";
		if(userType == null) userType = "";
		try{
			if(userType.equals("D")){ /*Driver User Login*/
				ChauffeurModel chauffeurModel = mobileWebService.driverAsUserName(userName);
				if(opType.equals("C")){ /*Change Password*/
					chauffeurModel.setPswd(password);
					chauffeurModel.setFirstLogin("N");
					chauffeurMasterService.update(chauffeurModel);
					res.setStatus("Success");
				}else if(opType.equals("F")){ /*Forgot Password*/
					if(chauffeurModel.getOtp() !=null){
						if(chauffeurModel.getOtp().equals(otp) && !chauffeurModel.getOtp().equals("")){
							chauffeurModel.setPswd(password);
							chauffeurModel.setFirstLogin("N");
							chauffeurModel.setOtp("");
							chauffeurMasterService.update(chauffeurModel);
							res.setStatus("Success");
						}
					}else{
						res.setStatus("Failure");
						res.setReason("Otp Does Not Match---!!!");
					}
				}else{
					if(chauffeurModel == null){ /*user not found for the provided user name*/
						res.setStatus("Failure");
						res.setReason("Invalid user name !!!");
					}else{
						if(!chauffeurModel.getPswd().equals(password)){ /*user found but password not matched*/
							res.setStatus("Failure");
							res.setReason("Invalid password !!!");
						}else{ /*user and password matched*/
							String[] sOtherInfo = mobileWebService.getChauffeurDutyCount(chauffeurModel.getId(),"C");
							res.setChauffeurDutySlipCount(Long.parseLong(sOtherInfo[0]));
							res.setCarRegNo(sOtherInfo[1]);
							
							res.setStatus("Success");
							chauffeurModel.setRefreshMeter("100");
							chauffeurModel.setRefreshTime("30000");
							res.setChauffeurModel(chauffeurModel);
						}
					}
				}
			}else{ /*Corporate User Login*/
				AutorizedUserModel autorizedUserModel = mobileWebService.userAsUserName(userName);
				if(opType.equals("C")){ /*Change Password*/
					autorizedUserModel.setPswd(password);
					autorizedUserModel.setFirstLogin("N");
					authorizedUserService.update(autorizedUserModel);
					res.setStatus("Success");
				}else if(opType.equals("F")){
					if(autorizedUserModel.getOtp() != null){
						if(autorizedUserModel.getOtp().equals(otp) && !autorizedUserModel.getOtp().equals("")){
							autorizedUserModel.setPswd(password);
							autorizedUserModel.setFirstLogin("N");
							autorizedUserModel.setOtp("");
							authorizedUserService.update(autorizedUserModel);
							res.setStatus("Success");
						}
					}else{
						res.setStatus("Failure");
						res.setReason("Otp Does Not Match---!!!");
					}
			     }else{
					if(autorizedUserModel == null){ /*user not found for the provided user name*/
						res.setStatus("Failure");
						res.setReason("Invalid user name !!!");
					}else{
						if(!autorizedUserModel.getPswd().equals(password)){ /*user found but password not matched*/
							res.setStatus("Failure");
							res.setReason("Invalid password !!!");
						}else{ /*user and password matched*/
							autorizedUserModel.setContactDetailModel(autorizedUserModel.getEntityContact());
							res.setStatus("Success");
							res.setAutorizedUserModel(autorizedUserModel);
						}
					}
				}
			}
		}catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	
	@RequestMapping(value="/searchBookingMobile", method= RequestMethod.POST)
	public void searchBooking(Map<String, Object> map,
			@RequestParam("bookedById") Long bookedById,
			@RequestParam("status") String status,
			@RequestParam("fromDate") String fromDate,
			@RequestParam("toDate") String toDate,
			HttpSession session, HttpServletResponse response){
		response.setContentType("application/json");
		HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
		try{
			List<BookingMasterModel> bookingMasterModelList = mobileWebService.SearchBooking(bookedById,status,toDate,fromDate);
			JSONROOT.put("bookingMasterModelMapList", bookingMasterModelList);
		}catch(Exception e) {
			logger.error("",e);
			JSONROOT.put("status", "Failure");
			JSONROOT.put("result", Message.FATAL_ERROR_MESSAGE);
		}finally{
			try{
				String jsonArray = gson.toJson(JSONROOT);
				response.getWriter().print(jsonArray);
				JSONROOT.clear();
			}catch(Exception e){
				logger.error("",e);
			}
		}
	}
	
	@RequestMapping(value = "/saveOrUpdateBookingMasterMobile", method = RequestMethod.POST)
	public @ResponseBody JsonResponseWeb saveOrUpdateBookingMaster(
			@ModelAttribute(value = "bookingMasterModel") BookingMasterModel bookingMasterModel,
		/*	BindingResult bindingResult, */@RequestParam("idForUpdate") String idForUpdate,
			@RequestParam("idForUpdateDetail") String idForUpdateDetail, @RequestParam("sCriteria") String sCriteria,
			@RequestParam("sValue") String sValue, @RequestParam("clientEmail") String clientEmail,
			@RequestParam("bookerEmail") String bookerEmail, @RequestParam(value="bookingPlatform",required=false) String bookingPlatform,
			HttpSession session, HttpServletRequest request) {
		JsonResponseWeb res = new JsonResponseWeb();
	/*	if (!idForUpdate.trim().equals("-1"))
			validator.validate(bookingMasterModel, bindingResult);
		if (bindingResult.hasErrors()) {
			res.setStatus("Errors");
			res.setResult(bindingResult.getAllErrors().toString());
			return res;
		}*/
		try {
			String sStatus = "W";
			AutorizedUserModel  autorizedUserModel = authorizedUserService.formFillForEdit(bookingMasterModel.getBookedBy().getId());
			if(autorizedUserModel.getAuthTypeAdmin().equals("N") && 
					autorizedUserModel.getAuthTypeClient().equals("Y")) sStatus = "P";
			bookingMasterModel.setBookingDate(new Date());
			bookingMasterModel.setBookingPlatform(bookingPlatform);
			bookingMasterModel.setBookerEmail(bookerEmail);
			bookingMasterModel.setClientEmail(clientEmail);
			bookingMasterModel.setStatus(sStatus);
			
			for (BookingDetailModel bookingDetailModel : bookingMasterModel.getBookingDetailModel()) {
				bookingDetailModel.setStatus(sStatus);
				bookingDetailModel.setBookingMasterModel(bookingMasterModel);
				
				if(bookingDetailModel.getReportingAddressLatLong() != null && !bookingDetailModel.getReportAtType().equals("O")){
					if(bookingDetailModel.getReportAtType().equals("H")){
						autorizedUserModel.sethAddress(bookingDetailModel.getReportingAddress());
						autorizedUserModel.sethAddressLatLong(bookingDetailModel.getReportingAddressLatLong());
					}
					if(bookingDetailModel.getReportAtType().equals("W")){
						autorizedUserModel.setoAddress(bookingDetailModel.getReportingAddress());
						autorizedUserModel.setoAddressLatLong(bookingDetailModel.getReportingAddressLatLong());
					}
				}
				
				if(bookingDetailModel.getToBeRealeseAtLatLong() != null && !bookingDetailModel.getReleaseAtType().equals("O")){
					if(bookingDetailModel.getReleaseAtType().equals("H")){
						autorizedUserModel.sethAddress(bookingDetailModel.getToBeRealeseAt());
						autorizedUserModel.sethAddressLatLong(bookingDetailModel.getToBeRealeseAtLatLong());
					}
					if(bookingDetailModel.getReleaseAtType().equals("W")){
						autorizedUserModel.setoAddress(bookingDetailModel.getToBeRealeseAt());
						autorizedUserModel.setoAddressLatLong(bookingDetailModel.getToBeRealeseAtLatLong());
					}
				}
			}
			authorizedUserService.update(autorizedUserModel);
						
			if (idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")) {
				bookingMasterModel = mobileWebService.save(bookingMasterModel);
				res.setResult(Message.SAVE_SUCCESS_MESSAGE);
			} else if (idForUpdate.trim().equals("-1")) {
				// Nothing to do
			} else {
				bookingMasterModel.setId(Long.valueOf(idForUpdate));
				if (!(idForUpdateDetail.trim().equals("") || idForUpdateDetail.trim().equals("0"))) {
					bookingMasterModel.getBookingDetailModel().get(0).setId(Long.valueOf(idForUpdateDetail));
				}
				bookingMasterModel = mobileWebService.update(bookingMasterModel);
				res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
			}
			res.setStatus("Success");
		} catch (ConstraintViolationException e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.DUPLICATE_ERROR_MESSAGE);
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		
		return res;
	}

	@RequestMapping(value="/approveBooking", method= RequestMethod.POST)
	public void approveBooking(Map<String, Object> map,
			@RequestParam("bookingId") Long bookingId,
			@RequestParam("action") String action,
			HttpSession session, HttpServletResponse response){
		response.setContentType("application/json");
		HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
		try{
			String sStatus = "W";
			String sReason = "Web booking cancel by authorizer";
			if(!action.equals("A")) sStatus ="X";

			BookingMasterModel bookingMasterModel = bookingMasterService.formFillForEdit(bookingId);
			bookingMasterModel.setClientApproveDt(new Date());
			bookingMasterModel.setStatus(sStatus);
			for(BookingDetailModel bookingDetailModel : bookingMasterModel.getBookingDetailModel()){
				bookingDetailModel.setCancelReason(sReason);
				bookingDetailModel.setCancelDate(new Date());
				bookingDetailModel.setStatus(sStatus);
			}
			bookingMasterService.update(bookingMasterModel);
				
			JSONROOT.put("status", "Success");
		}catch(Exception e) {
			logger.error("",e);
			JSONROOT.put("status", "Failure");
			JSONROOT.put("result", Message.FATAL_ERROR_MESSAGE);
		}finally{
			try{
				String jsonArray = gson.toJson(JSONROOT);
				response.getWriter().print(jsonArray);
				JSONROOT.clear();
			}catch(Exception e){
				logger.error("",e);
			}
		}
	}
	
	@RequestMapping(value="/saveUpdateChaufferStatus", method= RequestMethod.POST)
	public @ResponseBody JsonResponseWeb saveUpdateChaufferStatus(
			@ModelAttribute(value = "chauffeurStatusModel") ChauffeurStatusModel chauffeurStatusModel,
			BindingResult bindingResult,HttpServletRequest request){
		JsonResponseWeb res = new JsonResponseWeb();
		try{
			ChauffeurStatusModel	 oldChStatus = 	mobileWebService.getChauffeurStatusModel(chauffeurStatusModel.getChauffeurId().getId().longValue());
			if(oldChStatus == null){
				chauffeurStatusModel.setInsDate(new Date());
				mobileWebService.saveChStatus(chauffeurStatusModel);
				res.setResult(Message.SAVE_SUCCESS_MESSAGE);
			}else{
				if(!oldChStatus.getStatus().equals(chauffeurStatusModel.getStatus())){
					chauffeurStatusModel.setInsDate(new Date());
					mobileWebService.saveChStatus(chauffeurStatusModel);
					res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
				}
			}
			res.setStatus("Success");
		}catch(ConstraintViolationException e){
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.DUPLICATE_ERROR_MESSAGE);
		}catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	@RequestMapping(value="/getBookingDetailsAsPerChauffeur", method= RequestMethod.POST)
	public void getBookingDetailsAsPerChauffeur(Map<String, Object> map,
			@RequestParam("chauffeurId") Long chauffeurId,
			@RequestParam("flag") String flag,
			HttpSession session, HttpServletResponse response){
		response.setContentType("application/json");
		HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
		try{
			List<BookingMasterModel> bookingMasterModelList = mobileWebService.getBookingAsPerChauffeurId(chauffeurId,flag);
			JSONROOT.put("bookingMasterModelMapList", bookingMasterModelList);
			JSONROOT.put("backTimeAllow", "120");
		}catch(Exception e) {
			logger.error("",e);
			JSONROOT.put("status", "Failure");
			JSONROOT.put("result", Message.FATAL_ERROR_MESSAGE);
		}finally{
			try{
				String jsonArray = gson.toJson(JSONROOT);
				response.getWriter().print(jsonArray);
				JSONROOT.clear();
			}catch(Exception e){
				logger.error("",e);
			}
		}
	}
	@RequestMapping(value="/approveOrRejectBookingMobile", method= RequestMethod.POST)
	public void approveOrRejectBookingMobile(Map<String, Object> map,
			@RequestParam("dutySlipId") Long dutySlipId,
			@RequestParam("status") String status,
			@RequestParam("chauffeurId") Long chauffeurId,
			@RequestParam(value="tollCharge", required=false) Double tollCharge,
			@RequestParam(value="stateCharge", required=false) Double stateCharge,
			@RequestParam(value="parkingCharge", required=false) Double parkingCharge,
			@RequestParam(value="tollChargeImg", required=false) String[] tollChargeImg,
			@RequestParam(value="stateChargeImg", required=false) String[] stateChargeImg,
			@RequestParam(value="parkingChargeImg", required=false) String[] parkingChargeImg,
			HttpServletResponse response) {
		response.setContentType("application/json");
		HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
		try {
			DutySlipModel dutySlipModel = dutySlipService.getDSDetails(dutySlipId);
			DeallocateHistoryModel deallocateHistoryModel = new DeallocateHistoryModel();
			String sDutySlipStatus = dutySlipModel.getDutySlipStatus();
			/* A=Allocated, U=UnAllocated / UnReserved, M=Reserved, D=Dispatched, 
			 * Y=Driver Accept, N=Driver Reject, S=Start, H=Reached at Client, O=Client onboard, L=Drop, F=Complete */
			if (dutySlipModel.getChauffeurModel().getId().equals(chauffeurId)){ /*if chauffeur is assigned in this duty*/
				if (sDutySlipStatus.equals("A") || sDutySlipStatus.equals("M") || sDutySlipStatus.equals("D")) {/*if Duty is Allocated/Reserved/Dispatched to the chauffeur*/
					if (status.equals("Y")) { /* Accept by chauffeur*/
						dutySlipModel.getBookingDetailModel().setStatus("D");
						dutySlipModel.setDutySlipStatus("Y");
						String[] sSMSVariable = new String[4];
						sSMSVariable[0] = dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedForName();
						sSMSVariable[1] = dutySlipModel.getCarDetailModel().getRegistrationNo();
						sSMSVariable[2] = dutySlipModel.getChauffeurModel().getName();
						sSMSVariable[3] = dutySlipModel.getChauffeurModel().getMobileNo();
						try {
							if (!dutySlipModel.getBookingDetailModel().getBookingMasterModel().getMobileNo().equals("")
									&& dutySlipModel.getBookingDetailModel().getBookingMasterModel().getMobileNo().length() == 10) {
								SMSSender.MyValueFirstSend("carDetailSMS", sSMSVariable, "91"
										+ dutySlipModel.getBookingDetailModel().getBookingMasterModel().getMobileNo());
							}
						} catch (Exception e) {
							logger.error("", e);
						}
					}else if (status.equals("N")) {/* Reject by chauffeur */
						dutySlipModel.getBookingDetailModel().setStatus("N");
						dutySlipModel.setDutySlipStatus("U");
						deallocateHistoryModel.setAllocationDateTime(dutySlipModel.getAllocationDateTime());
						deallocateHistoryModel.setBookingDetailModelId(dutySlipModel.getBookingDetailModel().getId());
						deallocateHistoryModel.setBookingNo(dutySlipModel.getBookingDetailModel().getBookingMasterModel()
								.getBookingNo());
						deallocateHistoryModel.setCarDetailModelId(dutySlipModel.getCarDetailModel().getId());
						if (dutySlipModel.getChauffeurMobile() != null) {
							deallocateHistoryModel.setChauffeurMobile(dutySlipModel.getChauffeurMobile());
						}
						if (dutySlipModel.getChauffeurModel() != null) {
							deallocateHistoryModel.setChauffeurModelId(dutySlipModel.getChauffeurModel().getId());
						}
						if (dutySlipModel.getChauffeurName() != null) {
							deallocateHistoryModel.setChauffeurName(dutySlipModel.getChauffeurName());
						}
						if (dutySlipModel.getIsDsManual() != null) {
							deallocateHistoryModel.setIsDsManual(dutySlipModel.getIsDsManual());
						}
						if (dutySlipModel.getIsUpdatedChauffeur() != null) {
							deallocateHistoryModel.setIsUpdatedChauffeur(dutySlipModel.getIsUpdatedChauffeur());
						}
						if (dutySlipModel.getManualSlipNo() != null) {
							deallocateHistoryModel.setManualSlipNo(dutySlipModel.getManualSlipNo());
						}
						if (dutySlipModel.getManualSlipRemarks() != null) {
							deallocateHistoryModel.setManualSlipRemarks(dutySlipModel.getManualSlipRemarks());
						}
						deallocateHistoryModel.setStatus(sDutySlipStatus);
						deallocateHistoryModel.setVendorModelId(dutySlipModel.getVendorModel().getId());
						deallocateHistoryModel.setDeallocationDateTime(new Date());
						dutySlipService.saveDeallocationModel(deallocateHistoryModel);
					}
				}else {/* Duty running status i.e. start, reach, pickup,drop or complete */
					if(status.equals("F")) { //Complete  by chauffeur 
						dutySlipModel.setDutySlipStatus("F");
						dutySlipModel.setTollTaxAmount(tollCharge);
						dutySlipModel.setParkingAmount(parkingCharge);
						dutySlipModel.setStateTax(stateCharge);
						
						/*saving images and their amount detail*/
						if(tollChargeImg != null) dutySlipModel.setTollTaxDet(imageWrite(tollChargeImg,dutySlipModel.getId().toString(),"toll"));
						if(stateChargeImg != null) dutySlipModel.setStateTaxDet(imageWrite(stateChargeImg,dutySlipModel.getId().toString(),"state"));
						if(parkingChargeImg != null) dutySlipModel.setParkingDet(imageWrite(parkingChargeImg,dutySlipModel.getId().toString(),"parking"));
					}else
						dutySlipModel.setDutySlipStatus(status);
				}
				/*Save Chauffeur duty status*/
				ChauffeurStatusModel chauffeurStatusModel = new ChauffeurStatusModel();
				chauffeurStatusModel.setChauffeurId(dutySlipModel.getChauffeurModel());
				chauffeurStatusModel.setDutySlipId(dutySlipModel);
				chauffeurStatusModel.setInsDate(new Date());
				chauffeurStatusModel.setStatus(dutySlipModel.getDutySlipStatus());
				chauffeurStatusModel.setRemarks("Duty Status from Mobile App");
				mobileWebService.saveChStatus(chauffeurStatusModel);
				
				dutySlipService.update(dutySlipModel);
				JSONROOT.put("status", "Success");
			}else {
				JSONROOT.put("status", "Failure");
				JSONROOT.put("result", "Duty Assign To Someone Else....................!");
			}
		} catch (ConstraintViolationException e) {
			logger.error("", e);
			JSONROOT.put("status", "Failure");
			JSONROOT.put("result", Message.DUPLICATE_ERROR_MESSAGE);
		} catch (Exception e) {
			logger.error("", e);
			JSONROOT.put("status", "Failure");
			JSONROOT.put("result", Message.FATAL_ERROR_MESSAGE);
		} finally {
			try {
				String jsonArray = gson.toJson(JSONROOT);
				response.getWriter().print(jsonArray);
				JSONROOT.clear();
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

	@RequestMapping(value="/getForgetPassword", method= RequestMethod.POST)
	public void getForgetPassword(Map<String, Object> map,
			@RequestParam("userName") String userName,
			@RequestParam("mobileNo") String mobileNo,
			HttpSession session, HttpServletResponse response){
		response.setContentType("application/json");
		HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
		try{
			String password="";
			AutorizedUserModel autorizedUserModel = mobileWebService.userAsUserName(userName);
			if(autorizedUserModel!=null && mobileNo.length() >= 10){
				if(autorizedUserModel.getEntityContact().getPersonalMobile().contains(mobileNo)){
					DefaultOtpGenerator defaultOtpGenerator = new DefaultOtpGenerator(6);
					password=defaultOtpGenerator.generateToken().toString();
					autorizedUserModel.setPswd(password);
					autorizedUserModel.setFirstLogin("Y");
					authorizedUserService.update(autorizedUserModel);
					JSONROOT.put("password", password);
				}else{
					JSONROOT.put("status", "Mobile Does Not Match");
				}
			}
			else{
				JSONROOT.put("status", "UserName And Mobile Does Not Match");
			}
		}catch(Exception e) {
			logger.error("",e);
			JSONROOT.put("status", "Failure");
			JSONROOT.put("result", Message.FATAL_ERROR_MESSAGE);
		}finally{
			try{
				String jsonArray = gson.toJson(JSONROOT);
				response.getWriter().print(jsonArray);
				JSONROOT.clear();
			}catch(Exception e){
				logger.error("",e);
			}
		}
	}
	
	@RequestMapping(value="/generateOtp", method= RequestMethod.POST)
	public @ResponseBody JsonResponseWeb generateOtp(Map<String, Object> map,
			@RequestParam("userName") String userName,
			@RequestParam("userType") String userType,
			HttpSession session){
		JsonResponseWeb res = new JsonResponseWeb();
		try{
			String otp="";
			if(userType.equals("D")){ /*Driver Otp Generation*/
				ChauffeurModel chauffeurModel = mobileWebService.driverAsUserName(userName);
				if(chauffeurModel!=null){
					DefaultOtpGenerator defaultOtpGenerator = new DefaultOtpGenerator(6);
					otp=defaultOtpGenerator.generateToken().toString();
					chauffeurModel.setOtp(otp);
					chauffeurMasterService.update(chauffeurModel);
					String[] sSMSVariable = new String[2];
					sSMSVariable[0] =chauffeurModel.getName();
					sSMSVariable[1] =otp;
					if (chauffeurModel.getMobileNo()!="" &&chauffeurModel.getMobileNo()!=null) {
						SMSSender.MyValueFirstSend("SendOtp", sSMSVariable, "91" + chauffeurModel.getMobileNo());
					}
					res.setStatus("Success");
				}else{
					res.setStatus("Failure");
					res.setReason("Invalid user name !!!");
				}
			}else if(userType.equals("C")){ /*Corporate Otp Generation*/
				AutorizedUserModel autorizedUserModel = mobileWebService.userAsUserName(userName);
				if(autorizedUserModel!=null){
					DefaultOtpGenerator defaultOtpGenerator = new DefaultOtpGenerator(6);
					otp=defaultOtpGenerator.generateToken().toString();
					autorizedUserModel.setOtp(otp);
					authorizedUserService.update(autorizedUserModel);
					String[] sSMSVariable = new String[2];
					sSMSVariable[0] =autorizedUserModel.getName();
					sSMSVariable[1] =otp;
					if (autorizedUserModel.getContactDetailModel().getPersonalMobile()!="" &&autorizedUserModel.getContactDetailModel().getPersonalMobile()!=null) {
						SMSSender.MyValueFirstSend("SendOtp", sSMSVariable, "91"
								+autorizedUserModel.getContactDetailModel().getPersonalMobile());
					}
					res.setStatus("Success");
				}else{
					res.setStatus("Failure");
					res.setReason("Invalid user name !!!");
				}
			}
		}catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	@RequestMapping(value="/saveTripRouteMap", method= RequestMethod.POST)
	public void saveTripRouteMap(Map<String, Object> map,
			@ModelAttribute(value = "tripRoutMapModel") TripRoutMapModel tripRoutMapModel,
			HttpSession session, HttpServletResponse response){
		response.setContentType("application/json");
		HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
		try{
			 mobileWebService.saveTripRouteMap(tripRoutMapModel);
			 JSONROOT.put("status", "SUCCESS");
			 JSONROOT.put("result", Message.SAVE_SUCCESS_MESSAGE);
		}catch(Exception e) {
			logger.error("",e);
			JSONROOT.put("status", "Failure");
			JSONROOT.put("result", Message.FATAL_ERROR_MESSAGE);
		}finally{
			try{
				String jsonArray = gson.toJson(JSONROOT);
				response.getWriter().print(jsonArray);
				JSONROOT.clear();
			}catch(Exception e){
				logger.error("",e);
			}
		}
	}
	
	private String imageWrite(String[] imageArray, String sDSId, String sCharge){
		String sType = "Docs", sAmt="";
		if(imageArray != null){
			for(int iCount = 0; iCount < imageArray.length; iCount++){
				String sFileName = sCharge + "_" + sDSId + "_" + iCount;
				String[] sArrayValues = imageArray[iCount].split("~~");
				sAmt = sAmt + sArrayValues[0] + ",";
				String sBase64String = sArrayValues[1];
				if(sBase64String.startsWith("data:image")){
					Utilities.ImageWrite(sBase64String,sFileName,sType);
				}
			}
		}
		if(!sAmt.equals("")) sAmt = sAmt.substring(0,sAmt.length() - 1);
		return sAmt;
	}
	
	@RequestMapping(value="/saveChauffeurRegistrationId", method= RequestMethod.POST)
	public void saveChauffeurRaegistrationId(Map<String, Object> map,
			@RequestParam("idForUpdate") Long idForUpdate,
			@RequestParam("registrationId") String registrationId,
			HttpSession session, HttpServletResponse response){
		response.setContentType("application/json");
		HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
		try{
			ChauffeurModel chauffeurModel = chauffeurMasterService.formFillForEdit(idForUpdate);
			if(chauffeurModel != null){
				chauffeurModel.setRegId(registrationId);
				chauffeurMasterService.update(chauffeurModel);
				JSONROOT.put("status", "SUCCESS");
				JSONROOT.put("result", Message.UPDATE_SUCCESS_MESSAGE);
			}else{
				JSONROOT.put("status", "Failure");
				JSONROOT.put("result", "Chauffeur not present !!!");
			}
			
		}catch(Exception e) {
			logger.error("",e);
			JSONROOT.put("status", "Failure");
			JSONROOT.put("result", Message.FATAL_ERROR_MESSAGE);
		}finally{
			try{
				String jsonArray = gson.toJson(JSONROOT);
				response.getWriter().print(jsonArray);
				JSONROOT.clear();
			}catch(Exception e){
				logger.error("",e);
			}
		}
	}
	
}
