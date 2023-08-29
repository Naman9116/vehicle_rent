package com.operation.controller;

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
import com.ets.service.LocationMasterService;
import com.master.model.ChauffeurModel;
import com.master.model.GeneralMasterModel;
import com.master.service.ChauffeurMasterService;
import com.operation.model.BookingDetailModel;
import com.operation.model.BookingMasterModel;
import com.operation.model.CarDetailModel;
import com.operation.model.DeallocateHistoryModel;
import com.operation.model.DutySlipModel;
import com.operation.model.PassengerDetailModel;
import com.operation.model.ReservedCarModel;
import com.operation.service.BookingMasterService;
import com.operation.service.CarDetailService;
import com.operation.service.DutySlipService;
import com.operation.validator.BookingMasterValidator;
import com.relatedInfo.model.RelatedInfoModel;
import com.user.model.UserModel;
import com.user.service.UserService;
import com.util.JsonResponse;
import com.util.Message;
import com.util.Utilities;

@Controller
@SessionAttributes({ "bookingDetails_sessionData", "tariffIdList", "carTypeIdList", "rentalTypeIdList", "branchList" })
public class BookingMasterController {
	private static final Logger logger = Logger.getLogger(BookingMasterController.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	@Autowired
	private BookingMasterService bookingMasterService;

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
	private ChauffeurMasterService chauffeurMasterService;
	
	@Autowired
	private LocationMasterService locationMasterService;
	
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

	@RequestMapping("/pageLoadBookingMaster")
	public ModelAndView pageLoadBookingMaster(Map<String, Object> map, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView("bookingMaster");
		try {
			String branchesAssigned = (String) session.getAttribute("branchesAssigned");
			session.setAttribute("bookingDetails_sessionData", null);
			map.put("customerOrCompanyNameIdList",
					masterDataService.getGeneralMasterData_UsingCode("CORP:" + branchesAssigned)); // Type
			map.put("tariffIdList", masterDataService.getGeneralMasterData_UsingCode("VTF"));
			map.put("carModelIdList", masterDataService.getGeneralMasterData_UsingCode("VMD"));
			map.put("rentalTypeIdList", masterDataService.getGeneralMasterData_UsingCode("VRT"));
			map.put("MOBList", masterDataService.getGeneralMasterData_UsingCode("MOP"));
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			map.put("bookingMasterModel", new BookingMasterModel());
		}
		return modelAndView;
	}

	@RequestMapping(value = "/fillCareModelAsCorpBr", method = RequestMethod.POST)
	public @ResponseBody JsonResponse fillCareModelAsCorpBr(@RequestParam("corporateId") String corporateId,
			@RequestParam("branchId") String branchId, @RequestParam("tariffId") String tariffId,
			@RequestParam("date") String date, HttpSession session) {
		JsonResponse res = new JsonResponse();
		String sQryStr = "carModelCorp:" + corporateId + ":" + branchId + ":" + tariffId + ":" + date;
		try {
			List<GeneralMasterModel> carList = masterDataService.getGeneralMasterData_UsingCode(sQryStr);
			res.setCarModelList(carList);
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value = "/fillAsPerCorporate", method = RequestMethod.POST)
	public @ResponseBody JsonResponse fillAsPerCorporate(
			@ModelAttribute(value = "bookingMasterModel") BookingMasterModel bookingMasterModel,
			BindingResult bindingResult, @RequestParam("corporateId") String corporateId, HttpSession session) {
		JsonResponse res = new JsonResponse();
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");

		try {
			CorporateModel corporateModel = corporateService.formFillForEdit(Long.parseLong(corporateId));
			res.setCorporateModel(corporateModel);

			List<GeneralMasterModel> branchList = masterDataService.getGeneralMasterData_UsingCode("LinkedBranchCorp:"
					+ corporateId + ":" + branchesAssigned);
			res.setBranchList(branchList);

			List<GeneralMasterModel> terminalList = masterDataService
					.getGeneralMasterData_UsingCode("LinkedBranchTerminal:" + corporateId);
			res.setTerminalList(terminalList);

			res.setGeneralMasterModelList(masterDataService.getGeneralMasterData_UsingCode("LinkedAuthoriser:"
					+ corporateId));
			res.setAutorizedUserModelList(masterDataService.getGeneralMasterData_UsingCode("LinkedClient:"
					+ corporateId));
			List<GeneralMasterModel> carCatList = masterDataService.getGeneralMasterData_UsingCode("VCT");
			List<GeneralMasterModel> carTypeList = masterDataService.getGeneralMasterData_UsingCode("carModelCorp:"
					+ corporateId);

			List<GeneralMasterModel> carList = new ArrayList<GeneralMasterModel>();
			if (bookingMasterModel.getMobName() != null) {
				for (GeneralMasterModel carCat : carCatList) {
					String sLabel = "";
					int i = 0;
					Long lCarCatId = carCat.getId().longValue();
					for (GeneralMasterModel carType : carTypeList) {
						if (carType.getSortId().longValue() == lCarCatId) {
							sLabel += carType.getName() + ",";
							if (i == 0)
								carCat.setId(carType.getId());
							i++;
						}
					}
					if (!sLabel.equals(""))
						sLabel = sLabel.substring(0, sLabel.length() - 1);
					carCat.setName(carCat.getName() + " [" + sLabel + "]");
					if (i > 0)
						carList.add(carCat);
				}
				res.setCarModelList(carList);
				res.setDataString1("Delhi:Gurugram:NOIDA:Ghaziabad");
			} else {
				res.setCarModelList(carTypeList);
			}

			res.setAddressDetail(masterDataService.getAddressDataFromID(Long.parseLong(corporateId), "Corporate"));
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value = "/fillBranchAsCompany", method = RequestMethod.POST)
	public @ResponseBody JsonResponse fillBranchAsCompany(
			@ModelAttribute(value = "bookingMasterModel") BookingMasterModel bookingMasterModel,
			BindingResult bindingResult, @RequestParam("companyId") String companyId,
			@RequestParam("corporateId") String corporateId) {
		JsonResponse res = new JsonResponse();
		try {
			res.setGeneralMasterModelList(masterDataService.getGeneralMasterData_UsingCode("BranchAsCompany:"
					+ companyId + ":" + corporateId));
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value = "/fillHubAsBranch", method = RequestMethod.POST)
	public @ResponseBody JsonResponse fillHubAsBranch(
			@ModelAttribute(value = "bookingMasterModel") BookingMasterModel bookingMasterModel,
			BindingResult bindingResult, 
			@RequestParam("branchId") String branchId,
			@RequestParam(value = "corporateId", required = false) String corporateId) {
		JsonResponse res = new JsonResponse();
		try {
			res.setGeneralMasterModelList(masterDataService.getGeneralMasterData_UsingCode("HubAsBranch:" + branchId));
			res.setTerminalList(masterDataService.getGeneralMasterData_UsingCode("TerminalAsBranch:" + branchId));
			if(corporateId != null){
				res.setCorpRentalList(masterDataService.getGeneralMasterData_UsingCode("RentalCorp:" + corporateId + ":" + branchId));
				res.setCorpTariffList(masterDataService.getGeneralMasterData_UsingCode("TariffScheCorp:" + corporateId + ":" + branchId));
			}
			res.setZoneList(locationMasterService.getZoneAsBranch(Long.parseLong(branchId)));
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value = "/fillLocationAsZone", method = RequestMethod.POST)
	public @ResponseBody JsonResponse fillLocationAsZone(
			@RequestParam("zoneId") String zoneId) {
		JsonResponse res = new JsonResponse();
		try {
			res.setLocationList(locationMasterService.getLocationAsZone(Long.parseLong(zoneId)));
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	@RequestMapping(value = "/getCarModelAsCarType", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getCarModelAsCarType(
			@ModelAttribute(value = "bookingMasterModel") BookingMasterModel bookingMasterModel,
			BindingResult bindingResult, @RequestParam("carTypeId") String carTypeId) {
		JsonResponse res = new JsonResponse();
		try {
			res.setGeneralMasterModelList(masterDataService.getGeneralMasterData_UsingCode("CarModelAsType:"
					+ carTypeId));
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value = "/saveOrUpdateBookingMaster", method = RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateBookingMaster(
			@ModelAttribute(value = "bookingMasterModel") BookingMasterModel bookingMasterModel,
			BindingResult bindingResult, @RequestParam("idForUpdate") String idForUpdate,
			@RequestParam(value = "idForUpdateDetail", required = false) String idForUpdateDetail,
			@RequestParam("sCriteria") String sCriteria, @RequestParam(value = "sValue") String sValue,
			@RequestParam("clientEmail") String clientEmail,
			@RequestParam(value = "bookerEmail", required = false) String bookerEmail,
			@RequestParam(value = "name", required = false) String[] name,
			@RequestParam(value = "mobile", required = false) String[] mobile,
			@RequestParam(value = "age", required = false) String[] age,
			@RequestParam(value = "sex", required = false) String[] sex,
			@RequestParam(value = "idDetails", required = false) String[] idDetails, HttpSession session,
			HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		if (!idForUpdate.trim().equals("-1"))
			validator.validate(bookingMasterModel, bindingResult);
		if (bindingResult.hasErrors()) {
			res.setStatus("Errors");
			res.setResult(bindingResult.getAllErrors());
			return res;
		}
		try {
			if (idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")) {
				for (BookingDetailModel bookingDetailModel : bookingMasterModel.getBookingDetailModel()) {
					bookingMasterModel.setBookingDate(new Date());
					bookingDetailModel.setBookingMasterModel(bookingMasterModel);
				}
				bookingMasterModel = bookingMasterService.save(bookingMasterModel);
				/*
				 * CorporateModel corporateModel
				 * =corporateService.formFillForEdit
				 * (bookingMasterModel.getCorporateId().getId());
				 * if(corporateModel.getIsPassengerInfo()!=null &&
				 * corporateModel.getIsPassengerInfo().equals("Y")){
				 * List<BookingDetailModel>
				 * BookingDetailModels1=bookingMasterService
				 * .getBookingDetailAsPerBookingMaster
				 * (bookingMasterModel.getId()); for(BookingDetailModel
				 * BookingDetailModel1
				 * :bookingMasterModel.getBookingDetailModel()){
				 * PassengerDetailModel passengerDetailModel =new
				 * PassengerDetailModel(); for(int i= 0; i< mobile.length; i++){
				 * passengerDetailModel.setBookingDetailId(BookingDetailModel1);
				 * passengerDetailModel.setName(name[i]);
				 * passengerDetailModel.setMobile(mobile[i]);
				 * passengerDetailModel.setAge(age[i]);
				 * passengerDetailModel.setSex(sex[i]);
				 * passengerDetailModel.setIdDetails(idDetails[i]);
				 * bookingMasterService.save(passengerDetailModel);
				 * res.setResult(Message.SAVE_SUCCESS_MESSAGE);
				 * res.setStatus("Success"); } } }
				 */SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
				for (BookingDetailModel bookingDetailModel : bookingMasterModel.getBookingDetailModel()) {
					ContactDetailModel contactDetailModel = authorizedUserService.getBookerMobile(bookingMasterModel
							.getBookedBy().getId());
					// Send Booking Confirmation Message
					/*
					 * String[] sVariable where: index 0 : Name 1 : BookingNo 2
					 * : Date 3 : Time 4 : CarType
					 */
					String[] sSMSVariable = new String[5];
					sSMSVariable[0] = bookingMasterModel.getBookedForName();
					sSMSVariable[1] = bookingMasterModel.getBookingNo();
					sSMSVariable[2] = dateFormat.format(bookingDetailModel.getPickUpDate());
					sSMSVariable[3] = bookingDetailModel.getPickUpTime();
					sSMSVariable[4] = bookingDetailModel.getCarModel().getName();
					if (bookingMasterModel.getSmsToClient().equals("Y")) {
						try {
							if (!bookingMasterModel.getMobileNo().equals("")
									&& bookingMasterModel.getMobileNo().length() == 10) {
								SMSSender.MyValueFirstSend("BookingConfirmation", sSMSVariable, "91"
										+ bookingMasterModel.getMobileNo());
							}
						} catch (Exception e) {
						}
					}
					if (bookingMasterModel.getSmsToBooker().equals("Y")) {
						try {
							if (!contactDetailModel.getPersonalMobile().equals("")
									&& contactDetailModel.getPersonalMobile().length() == 10) {
								SMSSender.MyValueFirstSend("BookingConfirmation", sSMSVariable, "91"
										+ contactDetailModel.getPersonalMobile());
							}
						} catch (Exception e) {
						}
					}
					if (!bookingMasterModel.getSmsToOther().equals("")) {
						try {
							if (bookingMasterModel.getSmsToOther() != null
									&& bookingMasterModel.getSmsToOther().length() == 10) {
								SMSSender.MyValueFirstSend("BookingConfirmation", sSMSVariable, "91"
										+ bookingMasterModel.getSmsToOther());
							}
						} catch (Exception e) {
						}
					}
					UserModel userModel = userService.formFillForEdit(bookingDetailModel.getBookingMasterModel()
							.getBookingTakenBy().getId());
					GeneralMasterModel vehicleCategoryType = masterDataService
							.getVehicleCategoryForVehicleModel(bookingDetailModel.getCarModel().getId());
					bookingDetailModel.setCarType(vehicleCategoryType);
					String bookingEmailTemplate = new Utilities().generatePdfForBooking(bookingDetailModel, request,
							servletContext.getRealPath("/WEB-INF/"), userModel);
					String sFilePath = servletContext.getRealPath("/WEB-INF/") + "pdf/Booking"
							+ bookingMasterModel.getBookingNo() + ".pdf";
					if (bookerEmail != null) {
						if (bookingMasterModel.getEmailToBooker().equals("Y") && !bookerEmail.equals("")) {
							Utilities.sentEmail(bookerEmail, "Booking Confirmation", bookingEmailTemplate, sFilePath);
						}
					}
					if (clientEmail != null) {
						if (bookingMasterModel.getEmailToClient().equals("Y") && !clientEmail.equals("")) {
							Utilities.sentEmail(clientEmail, "Booking Confirmation", bookingEmailTemplate, sFilePath);
						}
					}
					if (bookingMasterModel.getEmailToOther() != null) {
						if (!bookingMasterModel.getEmailToOther().equals("")) {
							Utilities.sentEmail(bookingMasterModel.getEmailToOther(), "Booking Confirmation",
									bookingEmailTemplate, sFilePath);
						}
					}
				}
				res.setDataString1("Your Booking Details has been saved with booking number \n"
						+ bookingMasterModel.getBookingNo());
				res.setBookingMasterModel(bookingMasterModel);
				res.setResult(Message.SAVE_SUCCESS_MESSAGE);
			} else if (idForUpdate.trim().equals("-1")) {
				// Nothing to do
			} else {
				bookingMasterModel.setId(Long.valueOf(idForUpdate));
				if (!(idForUpdateDetail.trim().equals("") || idForUpdateDetail.trim().equals("0"))) {
					bookingMasterModel.getBookingDetailModel().get(0).setId(Long.valueOf(idForUpdateDetail));
				}
				for (BookingDetailModel bookingDetailModel : bookingMasterModel.getBookingDetailModel()) {
					if (bookingDetailModel.getBranch().getId() > 0) {
						bookingDetailModel.setBookingMasterModel(bookingMasterModel);
					}
				}
				/*
				 * if(bookingMasterModel.getBookingDetailModel().get(0).getBranch
				 * ().getId() > 0)
				 * bookingMasterModel.getBookingDetailModel().get
				 * (0).setBookingMasterModel(bookingMasterModel);
				 */
				bookingMasterModel = bookingMasterService.update(bookingMasterModel);
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
		} finally {
			if (idForUpdate.trim().equals("-1")) {
				List<BookingMasterModel> bookingMasterList = bookingMasterService.list(sCriteria, sValue);
				res.setBookingMasterList(bookingMasterList);
				res.setDataGrid(showGridDetail(bookingMasterService.list(bookingMasterModel.getId())));
			}
		}
		return res;
	}

	@RequestMapping(value = "/formFillForEditBookingMaster", method = RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditBookingMaster(
			@RequestParam("formFillForEditId") String formFillForEditId, @RequestParam("formType") String formType) {
		JsonResponse res = new JsonResponse();
		BookingDetailModel bookingDetailModel = null;
		BookingMasterModel bookingMasterModel = null;
		try {
			if (formType.equalsIgnoreCase("D")) {
				bookingDetailModel = bookingMasterService.formFillForDetailEdit(Long.parseLong(formFillForEditId));
			} else {
				bookingMasterModel = bookingMasterService.formFillForEdit(Long.parseLong(formFillForEditId));
			}
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		} finally {
			if (formType.equalsIgnoreCase("D")) {
				res.setBookingDetailModel(bookingDetailModel);
			} else {
				res.setBookingMasterModel(bookingMasterModel);
				res.setDataGrid(showGridDetail(bookingMasterService.list(bookingMasterModel.getId())));
			}
		}
		return res;
	}

	@RequestMapping(value = "/deleteBookingMaster", method = RequestMethod.POST)
	public @ResponseBody JsonResponse deleteBookingMaster(
			@ModelAttribute(value = "bookingMasterModel") BookingMasterModel bookingMasterModel,
			BindingResult bindingResult, @RequestParam("idForDelete") String idForDelete) {
		JsonResponse res = new JsonResponse();
		try {
			bookingMasterService.delete(Long.valueOf(idForDelete));
			res.setStatus("Success");
			res.setResult(Message.DELETE_SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FOREIGNKEY_ERROR_MESSAGE);
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		} finally {
		}
		return res;
	}

	@RequestMapping(value = "/deleteBookingDetailRecord", method = RequestMethod.POST)
	public @ResponseBody JsonResponse deleteBookingDetailRecord(@RequestParam("idForDelete") Long idForDelete,
			@RequestParam("idForOperate") Long idForOperate) {
		JsonResponse res = new JsonResponse();
		try {
			bookingMasterService.deleteDetailRecord(idForDelete);
			res.setStatus("Success");
			res.setResult(Message.DELETE_SUCCESS_MESSAGE);
			res.setDataGrid(showGridDetail(bookingMasterService.list(idForOperate)));
		} catch (DataIntegrityViolationException e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FOREIGNKEY_ERROR_MESSAGE);
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		} finally {
		}
		return res;
	}

	public String showGridDetail(List<BookingDetailModel> bookingDetailModelData) {
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
		for (BookingDetailModel bookingDetailModel : bookingDetailModelData) {
			String sDate = bookingDetailModel.getPickUpDate() == null ? "" : dateFormat.format(bookingDetailModel
					.getPickUpDate());
			dataString = dataString
					+ " <tr> "
					+ " <td>"
					+ " 	<a href='javascript:formFillDetailRecord("
					+ bookingDetailModel.getId()
					+ ")'><img src='./img/editButton.png' border='0' title='Edit' width='20' height='20'/></a> "
					+ "     <a href='javascript:deleteDetailRecord("
					+ bookingDetailModel.getId()
					+ ")' ><img src='./img/deleteButton.png' border='0' title='Delete / Cancel' width='20' height='20'/></a> "
					+ " </td> 														" + " <td >" + sDate + "</td>" + " <td >"
					+ bookingDetailModel.getBranch().getName() + "</td>  		" + " <td >"
					+ bookingDetailModel.getOutlet().getName() + "</td>  		" + " <td >"
					+ bookingDetailModel.getRentalType().getName() + "</td>  		" + " <td >"
					+ bookingDetailModel.getTariff().getName() + "</td>  		" + " <td >"
					+ bookingDetailModel.getCarModel().getName() + "</td>  		" + " <td >"
					+ bookingDetailModel.getFlightNo() + "</td>  		" + " <td >" + bookingDetailModel.getStartTime()
					+ "</td>  		" + " <td >" + bookingDetailModel.getPickUpTime() + "</td>  		" + " <td >"
					+ bookingDetailModel.getReportingAddress() + "</td>  		" + " <td >"
					+ bookingDetailModel.getToBeRealeseAt() + "</td>  		" + " <td >"
					+ bookingDetailModel.getInstruction() + "</td>  		" + " </tr> 		";
		}
		dataString = dataString + "</tbody></table>";
		return dataString;
	}

	@RequestMapping("/pageLoadJobWorks/{jobType}")
	public ModelAndView pageLoadJobWorks(Map<String, Object> map, HttpSession session, @PathVariable String jobType) {
		ModelAndView modelAndView = new ModelAndView("jobWorks");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try {
			session.setAttribute("bookingDetails_sessionData", null);
			map.put("customerOrCompanyNameIdList",
					masterDataService.getGeneralMasterData_UsingCode("CORP:" + branchesAssigned)); // Type
			map.put("tariffIdList", masterDataService.getGeneralMasterData_UsingCode("VTF"));
			map.put("carModelIdList", masterDataService.getGeneralMasterData_UsingCode("VMD"));
			map.put("rentalTypeIdList", masterDataService.getGeneralMasterData_UsingCode("VRT"));
			map.put("MOBList", masterDataService.getGeneralMasterData_UsingCode("MOB"));
			map.put("BCRList", masterDataService.getGeneralMasterData_UsingCode("BCR"));
			map.put("branchList", masterDataService.getGeneralMasterData_UsingCode("VBM-A:" + branchesAssigned));
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

	@RequestMapping(value = "/searchBooking", method = RequestMethod.POST)
	public @ResponseBody JsonResponse searchBooking(@RequestParam("jobType") String jobType,
			@RequestParam("sCriteria") String sCriteria, @RequestParam("sValue") String sValue, HttpSession session,
			HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		String branchesAssigned = "";
		branchesAssigned = (String) session.getAttribute("branchesAssigned");
		if(branchesAssigned != null) branchesAssigned = branchesAssigned.replace(",", ":");
		sCriteria = sCriteria + ",branchesAssigned";
		sValue = sValue + "," + branchesAssigned; 
		try {
			String[] sCriteriaList = sCriteria.split(",");
			String[] sValueList = sValue.split(",");
			List<BookingDetailModel> bookingDetailList = bookingMasterService.list(jobType, sCriteriaList, sValueList);

			res.setBookingDetailList(bookingDetailList);
			res.setDataGrid(showGridJobs(bookingDetailList, jobType));
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value = "/operation/getCarForDispatch", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getCarForDispatch(@RequestParam("sModelId") String sModelId, HttpSession session,
			HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		try {
			res.setCarDetailModelList(masterDataService.getCarRegistrationNoList(sModelId));
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	// New Methord Start
	public String showGridJobsCurrent(List<BookingDetailModel> bookingDetailModelData) {
		SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dataString = " <table style='table-layout:fixed; width:99%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>"
				+ " <tr>"
				+ "		<th width='25px' >Alert</th>"
				+ "     <th width='60px' >Direct Dispatch</th>"
				+ " 	<th width='77px' >Booking No</th>"
				+ " 	<th style='display:none; width='77px' >StartTime</th>"
				+ " 	<th width='45px' >Booking Mode</th>"
				+ " 	<th width='60px' >Pickup Date</th>"
				+ " 	<th width='35px' >Pickup Time</th>"
				+ " 	<th width='95px' >Corporate Name</th>"
				+ " 	<th width='60px' >Booked By</th>"
				+ " 	<th width='66px' >Client Name</th>"
				+ " 	<th width='80px' >Client Mobile No</th>"
				+ " 	<th width='150px' >Pickup Location</th>"
				+ " 	<th width='150px' >Drop Location</th>"
				+ " 	<th width='150px' >Special Instruction</th>"
				+ " 	<th width='45px' >Rental Type</th>"
				+ " 	<th width='50px' >Car Type</th>"
				+ " 	<th style='display:none;' width='25px' >Status</th>"
				+ " 	<th width="
				+ Message.ACTION_SIZE
				+ ">Action Mode</th>"
				+ "		<th width='60px' >Created Date & Time</th>"
				+ " 	<th width='45px' >Booking Taken By</th>"
				+ " 	<th style='display:none;' width='25px' >Booked For</th>"
				+ " 	<th style='display:none;' width='25px' >Report At</th>"
				+ " 	<th style='display:none;' width='25px' >Is Passenger</th>"
				+ " 	<th style='display:none;' width='25px' >dispatchCar</th>"
				+ " 	<th style='display:none;' width='25px' >totalNoOfDetails</th>"
				+ " </tr>"
				+ " </thead>"
				+ " <tbody>";
		if (bookingDetailModelData == null) {
			return dataString + "</tbody></table>";
		}
		for (BookingDetailModel bookingDetailModel : bookingDetailModelData) {
			String sPickUpDate = bookingDetailModel.getPickUpDate() == null ? "" : dateFormat.format(bookingDetailModel
					.getPickUpDate());
			String sBookingDate = bookingDetailModel.getBookingMasterModel().getBookingDate() == null ? ""
					: dateFormatWithTime.format(bookingDetailModel.getBookingMasterModel().getBookingDate());

			long lId = bookingDetailModel.getId();
			String sImageSrc = "";
			Date pickupDateTime = null;
			try {
				pickupDateTime = dateFormatWithTime.parse(sPickUpDate + " " + bookingDetailModel.getPickUpTime());
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
			boolean bDateAfter = false;
			if (bookingDetailModel.getStatus() == null && pickupDateTime.before(new Date())) {
				bDateAfter = true;
			}
			bookingDetailModel.setStatus(parseStatus(bookingDetailModel.getStatus(), bDateAfter));

			String dispatchCarNo = "";
			String isPassenger = "N";
			if (bookingDetailModel.getDutySlipModel() != null)
				dispatchCarNo = bookingDetailModel.getDutySlipModel().getCarDetailModel().getRegistrationNo();
			if (bookingDetailModel.getBookingMasterModel().getCorporateId().getIsPassengerInfo() != null)
				isPassenger = bookingDetailModel.getBookingMasterModel().getCorporateId().getIsPassengerInfo();
			dataString = dataString + " <tr> ";
			dataString += " <td> <img src="
					+ sImageSrc
					+ " id='sig' width='16px' height='16px'/>  </td>"
					+ " <td id='Dispatch_"
					+ lId
					+ "'>"
					+ " <a href='javascript:dispatch("
					+ bookingDetailModel.getId()
					+ ")' class='btn btn-primary btn-xs'>Dispatch</a><label >"
					+ dispatchCarNo
					+ "</label></td> "
					+ " <td><textarea id='BookingNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookingNo()
					+ "</textarea></td>"
					+ " <td style='display:none;'><textarea id='startTime_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingDetailModel.getStartTime()
					+ "</textarea></td>"
					+ " <td id='MOB_"
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
					+ bookingDetailModel.getPickUpTime()
					+ "</textarea></td>"
					+ " <td><textarea id='corporateName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:95px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getCorporateId().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedBy().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedForName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:66px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedForName()
					+ "</textarea></td>"
					+ " <td><textarea id='mobileNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:80px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getMobileNo()
					+ "</textarea></td>"
					+ " <td><textarea id='pickupLoc_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getReportingAddress()
					+ "</textarea></td>"
					+ " <td><textarea id='dropAt_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getToBeRealeseAt()
					+ "</textarea></td>"
					+ " <td><textarea id='splIns_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getInstruction()
					+ "</textarea></td>"
					+ " <td><textarea id='rentalType_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingDetailModel.getRentalType().getName()
					+ "</textarea><input type='hidden' id='tariff_"
					+ lId
					+ "' value='"
					+ bookingDetailModel.getTariff().getId()
					+ "'/></td>"
					+ " <td><textarea id='carModel_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ bookingDetailModel.getCarModel().getName()
					+ "</textarea><input type='hidden' id='carModelId_"
					+ lId
					+ "' value='"
					+ bookingDetailModel.getCarModel().getId()
					+ "'/></td>"
					+ " <td style='display:none' id='status_"
					+ lId
					+ "'>"
					+ bookingDetailModel.getStatus()
					+ "</td>"
					+ " <td id='Action_"
					+ lId
					+ "'>"
					+ "<a href='#' onclick = 'formFillForEditDetailRecord("
					+ bookingDetailModel.getId()
					+ ");' id='editButton_"
					+ bookingDetailModel.getId()
					+ "'><span style='color:green'>Edit&nbsp;</span></a> "
					+ " 	<a href='#' onclick = 'cancelRecord("
					+ bookingDetailModel.getId()
					+ ");'><span style='color:red'>Cancel</span></a></td>"
					+ " <td ><textarea id='BookingDate_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
					+ sBookingDate
					+ "</textarea></td>"
					+ " <td ><textarea id='bookingTakenBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookingTakenBy().getUserFirstName()
					+ "</textarea></td>" + " <td id='bookedFor_" + lId + "' style='display:none;' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedFor() + "</td>" + " <td id='reportAt_" + lId
					+ "' style='display:none;' >" + bookingDetailModel.getReportAt() + "</td>"
					+ " <td id='isPassenger_" + lId + "' style='display:none;' >" + isPassenger + "</td>"
					+ " <td id='totalNoOfDetails_" + lId + "' style='display:none;' >"
					+ (bookingDetailModel.getBookingMasterModel().getBookingDetailModel().size()) + "</td>"
					+ " <td id='dispatchCar_" + lId + "' style='display:none;' >" + dispatchCarNo + "</td>" + " </tr>";
		}
		dataString = dataString + "</tbody></table>";
		return dataString;
	}

	public String showGridJobsAdvance(List<BookingDetailModel> bookingDetailModelData) {
		String dataString = "";
		SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dataString = " <table style='table-layout:fixed; width:99%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>"
				+ " <tr>"
				+ " 		<th width='92px' >Reserve</th>"
				+ "    	<th width='80px' >Allocataion cum Dispatch</th>"
				+ " 		<th width='77px' >Booking No</th>"
				+ " 		<th  style='display:none; width='77px' >StartTime</th>"
				+ "		<th width='45px' >Booking Mode</th>"
				+ " 		<th width='60px' >Pickup Date</th>"
				+ " 		<th width='35px' >Pickup Time</th>"
				+ " 		<th width='95px' >Corporate Name</th>"
				+ " 		<th width='60px' >Booked By</th>"
				+ " 		<th width='66px' >Client Name</th>"
				+ " 		<th width='80px' >Client Mobile No</th>"
				+ " 		<th width='150px' >Pickup Location</th>"
				+ " 		<th width='150px' >Drop Location</th>"
				+ " 		<th width='150px' >Special Instruction</th>"
				+ " 		<th width='45px' >Rental Type</th>"
				+ " 		<th width='50px' >Car Type</th>"
				+ " 		<th width='50px' >Job Status</th>"
				+ " 		<th width="
				+ Message.ACTION_SIZE
				+ ">Action Mode</th>"
				+ "		<th width='60px' >Created Date & Time</th>"
				+ " 		<th width='45px' >Booking Taken By</th>"
				+ " 		<th style='display:none;' width='25px' >Booked For</th>"
				+ " 		<th style='display:none;' width='25px' >Report At</th>"
				+ " 		<th style='display:none;' width='25px' >dispatchCar</th>"
				+ " 		<th style='display:none;' width='25px' >totalNoOfDetails</th>" + " 	</tr></thead><tbody>";
		if (bookingDetailModelData == null) {
			return dataString + "</tbody></table>";
		}
		for (BookingDetailModel bookingDetailModel : bookingDetailModelData) {
			String sPickUpDate = bookingDetailModel.getPickUpDate() == null ? "" : dateFormat.format(bookingDetailModel
					.getPickUpDate());
			String sBookingDate = bookingDetailModel.getBookingMasterModel().getBookingDate() == null ? ""
					: dateFormatWithTime.format(bookingDetailModel.getBookingMasterModel().getBookingDate());
			long lId = bookingDetailModel.getId();
			Date pickupDateTime = null;
			try {
				pickupDateTime = dateFormatWithTime.parse(sPickUpDate + " " + bookingDetailModel.getPickUpTime());
			} catch (ParseException e) {
				logger.error("", e);
			}
			boolean bDateAfter = false;
			if (bookingDetailModel.getStatus() == null && pickupDateTime.before(new Date())) {
				bDateAfter = true;
			}
			bookingDetailModel.setStatus(parseStatus(bookingDetailModel.getStatus(), bDateAfter));

			String dispatchCarNo = "";
			if (bookingDetailModel.getDutySlipModel() != null)
				dispatchCarNo = bookingDetailModel.getDutySlipModel().getCarDetailModel().getRegistrationNo();
			dataString = dataString + " <tr> ";
			dataString += "<td  style ='text-align : center;' id='Reserve_" + lId + "'><a href='javascript:reserve("
					+ bookingDetailModel.getId() + ")' class='btn btn-primary btn-xs'>"
					+ (bookingDetailModel.getStatus().equals("Reserved") ? "Dispatch" : "Reserve") + "</a>";
			if (bookingDetailModel.getStatus().equals("Reserved")) {
				dataString += "<a id='unreserveBtn' href='javascript:unreserve("
						+ bookingDetailModel.getId()
						+ ")' class='btn btn-primary btn-xs' style='display:block; background-color: #ff9300; margin-top: 4px;'>Unreserve</a><label style='margin-top:4px;'>"
						+ dispatchCarNo + "</label></td> ";
			}
			dataString += "<td style ='text-align : center;' id='Dispatch_" + lId + "'>"
					+ " <a href='javascript:dispatch(" + bookingDetailModel.getId()
					+ ")' class='btn btn-primary btn-xs'>"
					+ (bookingDetailModel.getStatus().equals("Allocated") ? "Dispatch" : "Allocate") + "</a>";
			if (bookingDetailModel.getStatus().equals("Allocated")) {
				dataString += "<a id='deallocateBtn' href='javascript:deallocate("
						+ bookingDetailModel.getId()
						+ ")' class='btn btn-success btn-xs' style='display:block; background-color: #ff9300; margin-top: 4px;'>Deallocate </a> <label style='margin-top:4px;'>"
						+ dispatchCarNo + "</label></td> ";
			}
			dataString += " <td><textarea id='BookingNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookingNo()
					+ "</textarea>"
					+ "	 <textarea id='BookingMasterId_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; display: none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getId()
					+ "</textarea></td>"
					+ " <td style='display:none;'><textarea id='startTime_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingDetailModel.getStartTime()
					+ "</textarea></td>"
					+ " <td id='MOB_"
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
					+ bookingDetailModel.getPickUpTime()
					+ "</textarea></td>"
					+ " <td><textarea id='corporateName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:95px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getCorporateId().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedBy().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedForName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:66px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedForName()
					+ "</textarea></td>"
					+ " <td><textarea id='mobileNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:80px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getMobileNo()
					+ "</textarea></td>"
					+ " <td><textarea id='pickupLoc_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getReportingAddress()
					+ "</textarea></td>"
					+ " <td><textarea id='dropAt_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getToBeRealeseAt()
					+ "</textarea></td>"
					+ " <td><textarea id='splIns_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getInstruction()
					+ "</textarea></td>"
					+ " <td><textarea id='rentalType_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingDetailModel.getRentalType().getName()
					+ "</textarea><input type='hidden' id='tariff_"
					+ lId
					+ "' value='"
					+ bookingDetailModel.getTariff().getId()
					+ "'/></td>"
					+ " <td><textarea id='carModel_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ bookingDetailModel.getCarModel().getName()
					+ "</textarea><input type='hidden' id=carModelId_"
					+ lId
					+ " value='"
					+ bookingDetailModel.getCarModel().getId()
					+ "'/></td>"
					+ " <td><textarea id='status_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ bookingDetailModel.getStatus()
					+ "</textarea></td>"
					+ " <td id='Action_"
					+ lId
					+ "'>"
					+ "     <a href='#' onclick = 'formFillForEditDetailRecord("
					+ bookingDetailModel.getId()
					+ ");' id='editButton_"
					+ bookingDetailModel.getId()
					+ "'><span style='color:green'>Edit&nbsp;</span></a> "
					+ " 	<a href='#' onclick = 'cancelRecord("
					+ bookingDetailModel.getId()
					+ ");'><span style='color:red'>Cancel</span></a></td>"
					+ " <td ><textarea id='BookingDate_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
					+ sBookingDate
					+ "</textarea></td>"
					+ " <td ><textarea id='bookingTakenBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookingTakenBy().getUserFirstName()
					+ "</textarea></td>" + " <td id='bookedFor_" + lId + "' style='display:none;' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedFor() + "</td>" + " <td id='reportAt_" + lId
					+ "' style='display:none;' >" + bookingDetailModel.getReportAt() + "</td>"
					+ " <td id='totalNoOfDetails_" + lId + "' style='display:none;' >"
					+ (bookingDetailModel.getBookingMasterModel().getBookingDetailModel().size()) + "</td>"
					+ " <td id='dispatchCar_" + lId + "' style='display:none;' >" + dispatchCarNo + "</td> " + "</tr>";
		}
		dataString = dataString + "</tbody></table>";
		return dataString;
	}

	public String showGridJobsStatus(List<BookingDetailModel> bookingDetailModelData) {
		String dataString = "";
		SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dataString = " <table style='table-layout:fixed; width:99%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>"
				+ " <tr>"
				+ " 	<th width='77px' >Booking No</th>"
				+ " 	<th  style='display:none; width='77px' >StartTime</th>"
				+ " 	<th width='90px' >Duty Slip No</th>"
				+ " 	<th width='45px' >Booking Mode</th>"
				+ " 	<th width='60px' >Pickup Date</th>"
				+ " 	<th width='35px' >Pickup Time</th>"
				+ " 	<th width='95px' >Corporate Name</th>"
				+ " 	<th width='60px' >Booked By</th>"
				+ " 	<th width='66px' >Client Name</th>"
				+ " 	<th width='80px' >Client Mobile No</th>"
				+ " 	<th width='150px' >Pickup Location</th>"
				+ " 	<th width='150px' >Drop Location</th>"
				+ " 	<th width='150px' >Special Instruction</th>"
				+ " 	<th width='45px' >Rental Type</th>"
				+ " 	<th width='50px' >Car Type</th>"
				+ "		<th width='57px'>Job Status</th>"
				+ " 	<th width='67px' >Car No</th>"
				+ " 	<th width='75px' >Chauffeur</th>"
				+ " 	<th width='75px' >Chauffeur Mobile No</th>"
				+ " 	<th width='65px' >Dispatched By</th>"
				+ "		<th width='60px' >Ds Date & Time</th>"
				+ " 	<th width='45px' >Booking Taken By</th>"
				+ " 	<th style='display:none;' width='25px' >Booked For</th>"
				+ " 	<th style='display:none;' width='25px' >Report At</th>"
				+ " 	<th style='display:none;' width='25px' >dispatchCar</th>"
				+ " 	<th style='display:none;' width='25px' >totalNoOfDetails</th>" + " </tr></thead>" + " <tbody>";
		if (bookingDetailModelData == null) {
			return dataString + "</tbody></table>";
		}
		String dsDate = "";
		for (BookingDetailModel bookingDetailModel : bookingDetailModelData) {
			String sPickUpDate = bookingDetailModel.getPickUpDate() == null ? "" : dateFormat.format(bookingDetailModel
					.getPickUpDate());
			if (bookingDetailModel.getDutySlipModel() != null) {
				if (bookingDetailModel.getDutySlipModel().getDutySlipCreatedByDate() != null) {
					dsDate = dateFormatWithTime
							.format(bookingDetailModel.getDutySlipModel().getDutySlipCreatedByDate());
				} else {
					dsDate = "";
				}
			} else {
				dsDate = "";
			}
			long lId = bookingDetailModel.getId();
			Date pickupDateTime = null;
			try {
				pickupDateTime = dateFormatWithTime.parse(sPickUpDate + " " + bookingDetailModel.getPickUpTime());
			} catch (ParseException e) {
				logger.error("", e);
			}

			boolean bDateAfter = false;
			if (bookingDetailModel.getStatus() == null && pickupDateTime.before(new Date())) {
				bDateAfter = true;
			}
			bookingDetailModel.setStatus(parseStatus(bookingDetailModel.getStatus(), bDateAfter));

			String sDispatchedBy = "", sCarRegNo = "", sChaufferName = "", sChaufferMobile = "";
			if (bookingDetailModel.getDutySlipModel() != null) {
				if (bookingDetailModel.getDutySlipModel().getCarDetailModel() != null)
					sCarRegNo = bookingDetailModel.getDutySlipModel().getCarDetailModel().getRegistrationNo();
				if (bookingDetailModel.getDutySlipModel().getChauffeurModel() != null) {
					sChaufferName = bookingDetailModel.getDutySlipModel().getChauffeurModel().getName();
					sChaufferMobile = bookingDetailModel.getDutySlipModel().getChauffeurModel().getMobileNo();
				} else if (bookingDetailModel.getDutySlipModel().getIsUpdatedChauffeur() != null
						&& bookingDetailModel.getDutySlipModel().getIsUpdatedChauffeur().equals("Y")) {
					sChaufferName = bookingDetailModel.getDutySlipModel().getChauffeurName();
					sChaufferMobile = bookingDetailModel.getDutySlipModel().getChauffeurMobile();
				}
				if (bookingDetailModel.getDutySlipModel().getDispatchedBy() != null)
					sDispatchedBy = bookingDetailModel.getDutySlipModel().getDispatchedBy().getUserName();
			}
			String dispatchCarNo = "";
			if (bookingDetailModel.getDutySlipModel() != null)
				dispatchCarNo = bookingDetailModel.getDutySlipModel().getCarDetailModel().getRegistrationNo();
			dataString = dataString
					+ " <tr> "
					+ " <td><textarea id='BookingNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookingNo()
					+ "</textarea></td>"
					+ " <td style='display:none;'><textarea id='startTime_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingDetailModel.getStartTime() + "</textarea></td>";
			if (bookingDetailModel.getStatus().equals("Allocated")) {
				dataString += " <td id='DutySlip_" + lId + "'>" + "<a href='javascript:viewDutySlip("
						+ bookingDetailModel.getId()
						+ ")' class='btn btn-primary btn-xs' disabled='disbaled'>View Duty Slip</a></td> ";
			} else {
				dataString += " <td id='DutySlip_" + lId + "'>" + "<a href='javascript:viewDutySlip("
						+ bookingDetailModel.getId() + ")'>" + bookingDetailModel.getDutySlipModel().getDutySlipNo()
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
					+ bookingDetailModel.getPickUpTime()
					+ "</textarea></td>"
					+ " <td><textarea id='corporateName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:95px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getCorporateId().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedBy().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedForName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:66px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedForName()
					+ "</textarea></td>"
					+ " <td><textarea id='mobileNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:80px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getMobileNo()
					+ "</textarea></td>"
					+ " <td><textarea id='pickupLoc_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getReportingAddress()
					+ "</textarea></td>"
					+ " <td><textarea id='dropAt_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getToBeRealeseAt()
					+ "</textarea></td>"
					+ " <td><textarea id='splIns_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getInstruction()
					+ "</textarea></td>"
					+ " <td><textarea id='rentalType_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingDetailModel.getRentalType().getName()
					+ "</textarea><input type='hidden' id='tariff_"
					+ lId
					+ "' value='"
					+ bookingDetailModel.getTariff().getId()
					+ "'/></td>"
					+ " <td><textarea id='carModel_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ bookingDetailModel.getCarModel().getName()
					+ "</textarea><input type='hidden' id=carModelId_"
					+ lId
					+ " value='"
					+ bookingDetailModel.getCarModel().getId()
					+ "'/></td>"
					+ " <td><textarea id='status_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:57px; ' >"
					+ bookingDetailModel.getStatus()
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
					+ dsDate
					+ "</textarea></td>"
					+ " <td ><textarea id='bookingTakenBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookingTakenBy().getUserFirstName()
					+ "</textarea></td>" + " <td id='bookedFor_" + lId + "' style='display:none;' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedFor() + "</td>" + " <td id='reportAt_" + lId
					+ "' style='display:none;' >" + bookingDetailModel.getReportAt() + "</td>"
					+ " <td id='totalNoOfDetails_" + lId + "' style='display:none;' >"
					+ (bookingDetailModel.getBookingMasterModel().getBookingDetailModel().size()) + "</td>"
					+ " <td id='dispatchCar_" + lId + "' style='display:none;' >" + dispatchCarNo + "</td>" + " </tr>";
		}
		dataString = dataString + "</tbody></table>";
		return dataString;
	}

	public String showGridJobsCancelled(List<BookingDetailModel> bookingDetailModelData) {
		String dataString = "";
		SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dataString = " <table style='table-layout:fixed; width:99%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>"
				+ " <tr>"
				+ " 	<th width='77px' >Booking No</th>"
				+ " 	<th  style='display:none; width='77px' >StartTime</th>"
				+ " 	<th width='45px' >Booking Mode</th>"
				+ " 	<th width='60px' >Pickup Date</th>"
				+ " 	<th width='35px' >Pickup Time</th>"
				+ " 	<th width='95px' >Corporate Name</th>"
				+ " 	<th width='60px' >Booked By</th>"
				+ " 	<th width='66px' >Client Name</th>"
				+ " 	<th width='80px' >Client Mobile No</th>"
				+ " 	<th width='150px' >Pickup Location</th>"
				+ " 	<th width='150px' >Drop Location</th>"
				+ " 	<th width='150px' >Special Instruction</th>"
				+ " 	<th width='45px' >Rental Type</th>"
				+ " 	<th width='50px' >Car Type</th>"
				+ "		<th width='50px'>Job Status</th>"
				+ " 	<th width='65px' >Reason of cancelation</th>"
				+ " 	<th width='75px' >Cancelled Date & Time</th>"
				+ " 	<th width='75px' >Cancelled By</th>"
				+ "		<th width='60px' >Created Date & Time</th>"
				+ " 	<th width='45px' >Booking Taken By</th>"
				+ " 	<th style='display:none;' width='25px' >Booked For</th>"
				+ " 	<th style='display:none;' width='25px' >Report At</th>"
				+ " 	<th style='display:none;' width='25px' >dispatchCar</th>"
				+ " 	<th style='display:none;' width='25px' >totalNoOfDetails</th>" + " </tr></thead><tbody>";
		if (bookingDetailModelData == null) {
			return dataString + "</tbody></table>";
		}
		for (BookingDetailModel bookingDetailModel : bookingDetailModelData) {
			String sPickUpDate = bookingDetailModel.getPickUpDate() == null ? "" : dateFormat.format(bookingDetailModel
					.getPickUpDate());
			String sBookingDate = bookingDetailModel.getBookingMasterModel().getBookingDate() == null ? ""
					: dateFormatWithTime.format(bookingDetailModel.getBookingMasterModel().getBookingDate());
			long lId = bookingDetailModel.getId();
			Date pickupDateTime = null;
			try {
				pickupDateTime = dateFormatWithTime.parse(sPickUpDate + " " + bookingDetailModel.getPickUpTime());
			} catch (ParseException e) {
				logger.error("", e);
			}
			boolean bDateAfter = false;
			if (bookingDetailModel.getStatus() == null && pickupDateTime.before(new Date())) {
				bDateAfter = true;
			}
			bookingDetailModel.setStatus(parseStatus(bookingDetailModel.getStatus(), bDateAfter));

			String sCancelDate = "";
			String sCancelledBy = "";
			if (bookingDetailModel.getCancelDate() != null)
				sCancelDate = dateFormatWithTime.format(bookingDetailModel.getCancelDate());
			if (bookingDetailModel.getCancelBy() != null)
				sCancelledBy = bookingDetailModel.getCancelBy().getUserName();
			String dispatchCarNo = "";
			if (bookingDetailModel.getDutySlipModel() != null)
				dispatchCarNo = bookingDetailModel.getDutySlipModel().getCarDetailModel().getRegistrationNo();
			dataString = dataString
					+ " <tr> "
					+ " <td><textarea id='BookingNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookingNo()
					+ "</textarea></td>"
					+ " <td style='display:none;'><textarea id='startTime_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingDetailModel.getStartTime()
					+ "</textarea></td>"
					+ " <td id='MOB_"
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
					+ bookingDetailModel.getPickUpTime()
					+ "</textarea></td>"
					+ " <td><textarea id='corporateName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:95px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getCorporateId().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedBy().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedForName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:66px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedForName()
					+ "</textarea></td>"
					+ " <td><textarea id='mobileNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:80px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getMobileNo()
					+ "</textarea></td>"
					+ " <td><textarea id='pickupLoc_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getReportingAddress()
					+ "</textarea></td>"
					+ " <td><textarea id='dropAt_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getToBeRealeseAt()
					+ "</textarea></td>"
					+ " <td><textarea id='splIns_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getInstruction()
					+ "</textarea></td>"
					+ " <td><textarea id='rentalType_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingDetailModel.getRentalType().getName()
					+ "</textarea><input type='hidden' id='tariff_"
					+ lId
					+ "' value='"
					+ bookingDetailModel.getTariff().getId()
					+ "'/></td>"
					+ " <td><textarea id='carModel_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ bookingDetailModel.getCarModel().getName()
					+ "</textarea><input type='hidden' id=carModelId_"
					+ lId
					+ " value='"
					+ bookingDetailModel.getCarModel().getId()
					+ "'/></td>"
					+ " <td ><textarea id='status_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ bookingDetailModel.getStatus()
					+ "</textarea></td>"
					+ " <td><textarea cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:65px; ' > "
					+ bookingDetailModel.getCancelReason()
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
					+ sBookingDate
					+ "</textarea></td>"
					+ " <td ><textarea id='bookingTakenBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookingTakenBy().getUserFirstName()
					+ "</textarea></td>" + " <td id='bookedFor_" + lId + "' style='display:none;' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedFor() + "</td>" + " <td id='reportAt_" + lId
					+ "' style='display:none;' >" + bookingDetailModel.getReportAt() + "</td>"
					+ " <td id='totalNoOfDetails_" + lId + "' style='display:none;' >"
					+ (bookingDetailModel.getBookingMasterModel().getBookingDetailModel().size()) + "</td>"
					+ " <td id='dispatchCar_" + lId + "' style='display:none;' >" + dispatchCarNo + "</td>" + " </tr>";
		}
		dataString = dataString + "</tbody></table>";
		return dataString;

	}

	public String showGridJobsExpired(List<BookingDetailModel> bookingDetailModelData) {
		String dataString = "";
		SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dataString = " <table style='table-layout:fixed; width:99%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>"
				+ " <tr>"
				+ " 	<th width='77px' >Booking No</th>"
				+ " 	<th  style='display:none; width='77px' >StartTime</th>"
				+ " 	<th width='45px' >Booking Mode</th>"
				+ " 	<th width='60px' >Pickup Date</th>"
				+ " 	<th width='35px' >Pickup Time</th>"
				+ " 	<th width='95px' >Corporate Name</th>"
				+ " 	<th width='60px' >Booked By</th>"
				+ " 	<th width='66px' >Client Name</th>"
				+ " 	<th width='80px' >Client Mobile No</th>"
				+ " 	<th width='150px' >Pickup Location</th>"
				+ " 	<th width='150px' >Drop Location</th>"
				+ " 	<th width='150px' >Special Instruction</th>"
				+ " 	<th width='45px' >Rental Type</th>"
				+ " 	<th width='50px' >Car Type</th>"
				+ "		<th width='50px'>Job Status</th>"
				+ " 	<th width='100px' >Expired Date & Time</th>"
				+ " 	<th width='65px' >Expired By</th>"
				+ "		<th width='60px' >Created Date & Time</th>"
				+ " 	<th width='45px' >Booking Taken By</th>"
				+ " 	<th style='display:none;' width='25px' >Booked For</th>"
				+ " 	<th style='display:none;' width='25px' >Report At</th>"
				+ " 	<th style='display:none;' width='25px' >dispatchCar</th>"
				+ " 	<th style='display:none;' width='25px' >totalNoOfDetails</th>" + " </tr></thead><tbody>";
		if (bookingDetailModelData == null) {
			return dataString + "</tbody></table>";
		}
		for (BookingDetailModel bookingDetailModel : bookingDetailModelData) {
			String sPickUpDate = bookingDetailModel.getPickUpDate() == null ? "" : dateFormat.format(bookingDetailModel
					.getPickUpDate());
			String sBookingDate = bookingDetailModel.getBookingMasterModel().getBookingDate() == null ? ""
					: dateFormatWithTime.format(bookingDetailModel.getBookingMasterModel().getBookingDate());
			long lId = bookingDetailModel.getId();
			Date pickupDateTime = null;
			try {
				pickupDateTime = dateFormatWithTime.parse(sPickUpDate + " " + bookingDetailModel.getPickUpTime());
			} catch (ParseException e) {
				logger.error("", e);
			}
			boolean bDateAfter = false;
			if (bookingDetailModel.getStatus() == null && pickupDateTime.before(new Date())) {
				bDateAfter = true;
			}
			bookingDetailModel.setStatus(parseStatus(bookingDetailModel.getStatus(), bDateAfter));

			String dispatchCarNo = "";
			if (bookingDetailModel.getDutySlipModel() != null)
				dispatchCarNo = bookingDetailModel.getDutySlipModel().getCarDetailModel().getRegistrationNo();
			dataString = dataString
					+ " <tr> "
					+ " <td><textarea id='BookingNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookingNo()
					+ "</textarea></td>"
					+ " <td style='display:none;'><textarea id='startTime_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingDetailModel.getStartTime()
					+ "</textarea></td>"
					+ " <td id='MOB_"
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
					+ bookingDetailModel.getPickUpTime()
					+ "</textarea></td>"
					+ " <td><textarea id='corporateName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:95px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getCorporateId().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedBy().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedForName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:66px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedForName()
					+ "</textarea></td>"
					+ " <td><textarea id='mobileNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:80px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getMobileNo()
					+ "</textarea></td>"
					+ " <td><textarea id='pickupLoc_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getReportingAddress()
					+ "</textarea></td>"
					+ " <td><textarea id='dropAt_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getToBeRealeseAt()
					+ "</textarea></td>"
					+ " <td><textarea id='splIns_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getInstruction()
					+ "</textarea></td>"
					+ " <td><textarea id='rentalType_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingDetailModel.getRentalType().getName()
					+ "</textarea><input type='hidden' id='tariff_"
					+ lId
					+ "' value='"
					+ bookingDetailModel.getTariff().getId()
					+ "'/></td>"
					+ " <td><textarea id='carModel_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ bookingDetailModel.getCarModel().getName()
					+ "</textarea><input type='hidden' id=carModelId_"
					+ lId
					+ " value='"
					+ bookingDetailModel.getCarModel().getId()
					+ "'/></td>"
					+ " <td><textarea id='status_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ bookingDetailModel.getStatus()
					+ "</textarea></td>"
					+ " <td><textarea cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:100px; ' >"
					+ pickupDateTime
					+ "</textarea></td>"
					+ " <td><textarea cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:65px; ' >System</textarea></td>"
					+ " <td ><textarea id='BookingDate_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
					+ sBookingDate
					+ "</textarea></td>"
					+ " <td ><textarea id='bookingTakenBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookingTakenBy().getUserFirstName()
					+ "</textarea></td>" + " <td id='bookedFor_" + lId + "' style='display:none;' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedFor() + "</td>" + " <td id='reportAt_" + lId
					+ "' style='display:none;' >" + bookingDetailModel.getReportAt() + "</td>"
					+ " <td id='totalNoOfDetails_" + lId + "' style='display:none;' >"
					+ (bookingDetailModel.getBookingMasterModel().getBookingDetailModel().size()) + "</td>"
					+ " <td id='dispatchCar_" + lId + "' style='display:none;' >" + dispatchCarNo + "</td>" + "</tr>";
		}
		dataString = dataString + "</tbody></table>";
		return dataString;
	}

	public String showGridJobsWebBooking(List<BookingDetailModel> bookingDetailModelData) {
		SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dataString = " <table style='table-layout:fixed; width:99%' id='userTable' class='table table-bordered table-striped table-hover dataTable'>"
				+ "<thead style='background-color: #FED86F;'>"
				+ " <tr>"
				+ "		<th width='60px' >Action</th>"
				+ "		<th style='display:none; width='77px' >StartTime</th>"
				+ "		<th width='45px' >Booking Mode</th>"
				+ "		<th width='60px' >Pickup Date</th>"
				+ "		<th width='35px' >Pickup Time</th>"
				+ "		<th width='95px' >Corporate Name</th>"
				+ "		<th width='60px' >Booked By</th>"
				+ "		<th width='66px' >Client Name</th>"
				+ "		<th width='80px' >Client Mobile No</th>"
				+ "		<th width='150px' >Pickup Location</th>"
				+ "		<th width='150px' >Drop Location</th>"
				+ "		<th width='150px' >Special Instruction</th>"
				+ "		<th width='45px' >Rental Type</th>"
				+ "		<th width='50px' >Car Type</th>"
				+ "		<th width='60px' >Created Date & Time</th>"
				+ "		<th width='45px' >Booking Taken By</th>"
				+ "		<th width='60px' >Booking PlatForm</th>"
				+ "		<th style='display:none;' width='25px' >Booked For</th>"
				+ "		<th style='display:none;' width='25px' >tariff</th>"
				+ "		<th style='display:none;' width='25px' >Report At</th>"
				+ "		<th style='display:none;' width='25px' >totalNoOfDetails</th>"
				+ "		<th style='display:none;' width='25px' >dispatchCar</th>"
				// +
				// "<th style='display:none;' width='25px' >isPassengerDetails</th>"
				+ "</tr></thead><tbody>";
		if (bookingDetailModelData == null) {
			return dataString + "</tbody></table>";
		}
		String bookingPlatForm = "";
		for (BookingDetailModel bookingDetailModel : bookingDetailModelData) {
			String sPickUpDate = bookingDetailModel.getPickUpDate() == null ? "" : dateFormat.format(bookingDetailModel
					.getPickUpDate());
			String sBookingDate = bookingDetailModel.getBookingMasterModel().getBookingDate() == null ? ""
					: dateFormatWithTime.format(bookingDetailModel.getBookingMasterModel().getBookingDate());
			long lId = bookingDetailModel.getId();
			if (bookingDetailModel.getBookingMasterModel().getBookingPlatform() == null) {
				bookingPlatForm = "";
			} else if (bookingDetailModel.getBookingMasterModel().getBookingPlatform().equals("M")) {
				bookingPlatForm = "Mobile";
			} else if (bookingDetailModel.getBookingMasterModel().getBookingPlatform().equals("W")) {
				bookingPlatForm = "Web-Site";
			}
			String dispatchCarNo = "";
			if (bookingDetailModel.getDutySlipModel() != null)
				dispatchCarNo = bookingDetailModel.getDutySlipModel().getCarDetailModel().getRegistrationNo();
			dataString = dataString + " <tr> ";
			if (bookingDetailModel.getStatus().equals("P")) {
				dataString += " <td id='Dispatch_" + lId + "'>Approval Pending</td> ";
			} else {
				dataString += " <td id='Dispatch_" + lId + "'>" + " <a href='javascript:formFillForEditDetailRecord("
						+ bookingDetailModel.getId() + ")' class='btn btn-primary btn-xs'>Allow</a></td> ";
			}
			String sWebBookingTakenBy = bookingDetailModel.getBookingMasterModel().getWebBookingTakenBy() != null ? bookingDetailModel
					.getBookingMasterModel().getWebBookingTakenBy().getName()
					: "";
			dataString += " <td style='display:none;'><textarea id='startTime_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ bookingDetailModel.getStartTime()
					+ "</textarea></td>"
					+ " <td id='MOB_"
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
					+ bookingDetailModel.getPickUpTime()
					+ "</textarea></td>"
					+ " <td><textarea id='corporateName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:95px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getCorporateId().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedBy().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedForName_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:66px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedForName()
					+ "</textarea></td>"
					+ " <td><textarea id='mobileNo_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:80px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getMobileNo()
					+ "</textarea></td>"
					+ " <td><textarea id='pickupLoc_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getReportingAddress()
					+ "</textarea></td>"
					+ " <td><textarea id='dropAt_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getToBeRealeseAt()
					+ "</textarea></td>"
					+ " <td><textarea id='splIns_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:150px; ' >"
					+ bookingDetailModel.getInstruction()
					+ "</textarea></td>"
					+ " <td><textarea id='rentalType_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ bookingDetailModel.getRentalType().getName()
					+ "</textarea><input type='hidden' id='tariff_"
					+ lId
					+ "' value='"
					+ bookingDetailModel.getTariff().getId()
					+ "'/></td>"
					+ " <td><textarea id='carModel_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ bookingDetailModel.getCarModel().getName()
					+ "</textarea> <input type='hidden' id=carModelId_"
					+ lId
					+ " value='"
					+ bookingDetailModel.getCarModel().getId()
					+ "'/></td>"
					+ " <td><textarea id='BookingDate_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
					+ sBookingDate
					+ "</textarea></td>"
					+ " <td><textarea id='bookingTakenBy_"
					+ lId
					+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:45px; ' >"
					+ sWebBookingTakenBy + "</textarea></td>" + " <td id='bookedPlatForm_" + lId + "' width='60px' >"
					+ bookingPlatForm + "</td>" + " <td id='bookedFor_" + lId + "' style='display:none;' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedFor() + "</td>" + " <td id='tarif_" + lId
					+ "' style='display:none;' >" + bookingDetailModel.getTariff().getName() + "</td>"
					+ " <td id='reportAt_" + lId + "' style='display:none;' >" + bookingDetailModel.getReportAt()
					+ "</td>" + " <td id='totalNoOfDetails_" + lId + "' style='display:none;' >"
					+ (bookingDetailModel.getBookingMasterModel().getBookingDetailModel().size()) + "</td>"
					+ " <td id='dispatchCar_" + lId + "' style='display:none;' >" + dispatchCarNo + "</td>" + "</tr>";
		}
		dataString = dataString + "</tbody></table>";
		dataString = dataString.replaceAll("[\t\n\r]", "");
		return dataString;
	}

	// New Methord End
	public String showGridJobs(List<BookingDetailModel> bookingDetailModelData, String jobType) {
		String dataString = "";
		if (jobType.equals("Current")) {
			return showGridJobsCurrent(bookingDetailModelData);
		} else if (jobType.equals("Status")) {
			return showGridJobsStatus(bookingDetailModelData);
		} else if (jobType.equals("Advance")) {
			return showGridJobsAdvance(bookingDetailModelData);
		} else if (jobType.equals("Expired")) {
			return showGridJobsExpired(bookingDetailModelData);
		} else if (jobType.equals("Cancelled")) {
			return showGridJobsCancelled(bookingDetailModelData);
		} else if (jobType.equals("webBooking")) {
			return showGridJobsWebBooking(bookingDetailModelData);
		}
		return dataString;
	}

	@RequestMapping(value = "/updateBookingDeatil", method = RequestMethod.POST)
	public @ResponseBody JsonResponse updateBookingDeatil(
			@ModelAttribute(value = "bookingMasterModel") BookingMasterModel bookingMasterModel,
			BindingResult bindingResult, HttpSession session) {
		JsonResponse res = new JsonResponse();
		try {
			List<BookingDetailModel> bookingDetailModels = bookingMasterModel.getBookingDetailModel();
			for (BookingDetailModel bookingDetailModel : bookingDetailModels) {
				BookingDetailModel oldBookingDetailModel = bookingMasterService
						.formFillForDetailEdit(bookingDetailModel.getId());
				BookingMasterModel booMasterModel = oldBookingDetailModel.getBookingMasterModel();
				booMasterModel.setCorporateId(bookingDetailModel.getBookingMasterModel().getCorporateId());
				booMasterModel.setBookedBy(bookingDetailModel.getBookingMasterModel().getBookedBy());
				booMasterModel.setBookedFor(bookingDetailModel.getBookingMasterModel().getBookedFor());
				booMasterModel.setBookedForName(bookingDetailModel.getBookingMasterModel().getBookedForName());
				booMasterModel.setMobileNo(bookingDetailModel.getBookingMasterModel().getMobileNo());
				List<BookingDetailModel> booDetailModelList = booMasterModel.getBookingDetailModel();
				List<BookingDetailModel> boModels = new ArrayList<BookingDetailModel>();
				for (BookingDetailModel booDetailModel : booDetailModelList) {
					if (booDetailModel.getId() == oldBookingDetailModel.getId()) {
						booDetailModel.setPickUpDate(bookingDetailModel.getPickUpDate());
						booDetailModel.setPickUpTime(bookingDetailModel.getPickUpTime());
						booDetailModel.setStartTime(bookingDetailModel.getStartTime());
						booDetailModel.setReportAt(bookingDetailModel.getReportAt());
						booDetailModel.setReportingAddress(bookingDetailModel.getReportingAddress());
						booDetailModel.setToBeRealeseAt(bookingDetailModel.getToBeRealeseAt());
						booDetailModel.setInstruction(bookingDetailModel.getInstruction());
						booDetailModel.setRentalType(bookingDetailModel.getRentalType());
						booDetailModel.setCarModel(bookingDetailModel.getCarModel());
						booDetailModel.setTariff(bookingDetailModel.getTariff());
						booDetailModel.setTerminal(bookingDetailModel.getTerminal());
					}
					boModels.add(booDetailModel);
				}
				booMasterModel.setBookingDetailModel(boModels);
				bookingMasterService.update(booMasterModel);
			}
			res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		} finally {
			res.setBookingMasterModel(bookingMasterModel);
		}

		return res;
	}

	@RequestMapping(value = "/allocateOrDispatch", method = RequestMethod.POST)
	public @ResponseBody JsonResponse dispatchOrAllocate(
			@ModelAttribute(value = "dutySlipModel") DutySlipModel dutySlipModel, BindingResult bindingResult,
			HttpSession session, @RequestParam(value = "alDiTime") String alDiTime,
			@RequestParam(value = "smsClient", required = false) String smsClient,
			@RequestParam(value = "smsBooker", required = false) String smsBooker,
			@RequestParam(value = "dispatchSmsOther", required = false) String dispatchSmsOther,
			@RequestParam(value = "dutySlipIdForUpdate") String dutySlipIdForUpdate,
			@RequestParam(value = "oType") String oType) {
		JsonResponse res = new JsonResponse();
		if (!bindingResult.hasErrors()) {
			try {
				if (session.getAttribute("loginUserId") != null) {
					Long loginUserId = (Long) session.getAttribute("loginUserId");
					dutySlipModel.setDutySlipCreatedBy(loginUserId);
				}
				dutySlipModel.setDutySlipCreatedByDate(new Date());
				SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				String resultMessage = null;
				if (oType.equals("allocate")) {
					dutySlipModel.setAllocationDateTime(dateTimeFormat.parse(dateFormat.format(dutySlipModel
							.getAllocationDateTime()) + " " + alDiTime));
					resultMessage = "Entry Successfully Allocated.";
				} else if (oType.equals("dispatch")) {
					dutySlipModel.setDispatchDateTime(dateTimeFormat.parse(dateFormat.format(dutySlipModel
							.getDispatchDateTime()) + " " + alDiTime));

					resultMessage = "Entry Successfully Dispatched.";
				} else {
					dutySlipModel.setAllocationDateTime(dateTimeFormat.parse(dateFormat.format(dutySlipModel
							.getAllocationDateTime()) + " " + alDiTime));
					dutySlipModel.setDispatchDateTime(dateTimeFormat.parse(dateFormat.format(dutySlipModel
							.getDispatchDateTime()) + " " + alDiTime));
					resultMessage = "Entry Successfully Allocated And Dispatched.";
				}
				String status = dutySlipModel.getBookingDetailModel().getStatus();
				BookingDetailModel booDetailModel = bookingMasterService.formFillForDetailEdit(dutySlipModel
						.getBookingDetailModel().getId());
				dutySlipModel.setBookingDetailModel(booDetailModel);
				dutySlipModel.getBookingDetailModel().setStatus(status);
				DutySlipModel dsModel = dutySlipService.getDsRowCount(dutySlipModel.getBookingDetailModel().getId());
				if (dsModel == null) {
					if (dutySlipIdForUpdate == null || dutySlipIdForUpdate.equals("")
							|| dutySlipIdForUpdate.equals("0")) {
						bookingMasterService.allocateOrDispatch(dutySlipModel, "S");
						ContactDetailModel contactDetailModel = authorizedUserService.getBookerMobile(dutySlipModel
								.getBookingDetailModel().getBookingMasterModel().getBookedBy().getId());
						// Send Booking Confirmation Message
						/*
						 * String[] sVariable where: index 0 : Name 1 : Car No.
						 * 2 : Chauffeur Name 3 : Chauffeur Mobile
						 */
						String[] sSMSVariable = new String[4];
						sSMSVariable[0] = dutySlipModel.getBookingDetailModel().getBookingMasterModel()
								.getBookedForName();
						sSMSVariable[1] = dutySlipModel.getCarDetailModel().getRegistrationNo();
						sSMSVariable[2] = dutySlipModel.getCarDetailModel().getCarAllocationModels().get(0)
								.getChauffeurId().getName();
						sSMSVariable[3] = dutySlipModel.getCarDetailModel().getCarAllocationModels().get(0)
								.getChauffeurId().getMobileNo();
						if (smsClient.equals("Y")) {
							try {
								if (!dutySlipModel.getBookingDetailModel().getBookingMasterModel().getMobileNo()
										.equals("")
										&& dutySlipModel.getBookingDetailModel().getBookingMasterModel().getMobileNo()
												.length() == 10) {
									SMSSender.MyValueFirstSend("carDetailSMS", sSMSVariable, "91"
											+ dutySlipModel.getBookingDetailModel().getBookingMasterModel()
													.getMobileNo());
								}
							} catch (Exception e) {
							}
						}
						if (smsBooker.equals("Y")) {
							try {
								System.out.println("Booker Mobile No :- " + contactDetailModel.getPersonalMobile());
								if (!contactDetailModel.getPersonalMobile().equals("")
										&& contactDetailModel.getPersonalMobile().length() == 10) {
									SMSSender.MyValueFirstSend("carDetailSMS", sSMSVariable,
											"91" + contactDetailModel.getPersonalMobile());
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
						dutySlipModel.setId(Long.valueOf(dutySlipIdForUpdate));
						if (dutySlipModel.getId() != null)
							dutySlipModel = bookingMasterService.allocateOrDispatch(dutySlipModel, "U");
					}
				} else {
					dutySlipModel.setId(Long.valueOf(dsModel.getId()));
					if (dutySlipModel.getId() != null)
						dutySlipModel = bookingMasterService.allocateOrDispatch(dutySlipModel, "U");
				}
				/* Send mobile app notification to chauffeur */
				if (dutySlipModel.getChauffeurModel() != null) {
					ChauffeurModel chauffeurModel = chauffeurMasterService.formFillForEdit(dutySlipModel
							.getChauffeurModel().getId());
					if (chauffeurModel.getRegId() != null) {
						String sTitle = "New Booking Recieved";
						String sMessage = "You Have New Booking From "
								+ dutySlipModel.getBookingDetailModel().getReportingAddress() + " to "
								+ dutySlipModel.getBookingDetailModel().getToBeRealeseAt() + " at "
								+ dutySlipModel.getBookingDetailModel().getPickUpTime();
						Utilities.sendNotification(chauffeurModel.getRegId(), sTitle, sMessage);
					}
				}
				res.setDutySlipModel(dutySlipModel);
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

	@RequestMapping(value = "/fillAllocationDetails", method = RequestMethod.POST)
	public @ResponseBody JsonResponse fillAllocationDetails(@RequestParam("bookingDetailId") Long bookingDetailId,
			HttpSession session) {
		JsonResponse res = new JsonResponse();
		try {
			BookingDetailModel bookingDetailModels = null;
			DutySlipModel dutySlipModel = null;
			dutySlipModel = bookingMasterService.getAllocationDetails(bookingDetailId);
			res.setDutySlipModel(dutySlipModel);
			res.setBookingDetailModel(bookingDetailModels);
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value = "/cancelBooking", method = RequestMethod.POST)
	public @ResponseBody JsonResponse cancelBooking(
			@ModelAttribute(value = "bookingDetailModel") BookingDetailModel bookingDetailModel,
			BindingResult bindingResult, @RequestParam(value = "id") Long id, HttpSession session) {
		JsonResponse res = new JsonResponse();
		try {
			int count = 0;
			BookingDetailModel oldBookingDetailModel = bookingMasterService.formFillForDetailEdit(id);
			BookingMasterModel booMasterModel = oldBookingDetailModel.getBookingMasterModel();
			List<BookingDetailModel> booDetailModelList = booMasterModel.getBookingDetailModel();
			List<BookingDetailModel> boModels = new ArrayList<BookingDetailModel>();
			for (BookingDetailModel booDetailModel : booDetailModelList) {
				if (booDetailModel.getStatus() != null) {
					if (booDetailModel.getStatus().equalsIgnoreCase("X")) {
						count++;
					}
				}
				if (booDetailModel.getId() == oldBookingDetailModel.getId()) {
					booDetailModel.setCancelReason(bookingDetailModel.getCancelReason());
					oldBookingDetailModel.setCancelBy(bookingDetailModel.getCancelBy());
					oldBookingDetailModel.setCancelDate(new Date());
					booDetailModel.setStatus("X");
				}
				boModels.add(booDetailModel);
			}
			oldBookingDetailModel.setCancelReason(bookingDetailModel.getCancelReason());
			oldBookingDetailModel.setStatus("X");
			oldBookingDetailModel.setBookingMasterModel(booMasterModel);
			if (count == ((booDetailModelList.size()) - 1)) {
				booMasterModel.setStatus("X");
				booMasterModel.setBookingDetailModel(boModels);
				bookingMasterService.update(booMasterModel);
			} else {
				bookingMasterService.updateDetail(oldBookingDetailModel);
			}
			res.setResult("Booking Successfully Cancelled..!!");
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value = "/getCarListForReserve", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getCarListForReserve(
			@ModelAttribute(value = "dutySlipModel") ReservedCarModel reservedCarModel, BindingResult bindingResult,
			@RequestParam(value = "carSegment", required = false) Long carSegment) {
		JsonResponse res = new JsonResponse();
		try {
			res.setDataGrid(showGridCarList(carDetailService.getUnReservedCarlist(
					reservedCarModel.getReserveDateTime(), carSegment)));
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

	public String showGridCarList(List<CarDetailModel> carDetailModelList) {
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String dataString = " <table class='table table-bordered table-striped table-hover dataTable' id='userTable2'> 	"
				+ "	<thead style='background-color: #FED86F;'>		"
				+ " <tr> 							"
				+ "   	<th>Car Category</th> 		"
				+ "   	<th>Car Type</th> 			"
				+ "   	<th>Car No</th> 			"
				+ "   	<th>Chauffeur</th> 			"
				+ "   	<th>Mobile Number</th> 		"
				+ "   	<th>Select</th> 			"
				+ "   	<th>Status</th> 			"
				+ "   	<th>Reserved By</th> 		"
				+ "   	<th>Un Reserved By</th> 	"
				+ "   	<th>Reserve Date</th> 		"
				+ "   	<th>Time</th> 				" + "</tr></thead><tbody>";
		if (carDetailModelList == null) {
			dataString = dataString + "</tbody></table>";
			return dataString;
		}
		for (CarDetailModel carDetailModel : carDetailModelList) {
			String sCategory = carDetailModel.getBodyStyle() == null ? "" : carDetailModel.getModel().getName();
			String sModel = carDetailModel.getModel() == null ? "" : carDetailModel.getModel().getName();
			String sCarStatus = carDetailModel.getDutySlipStatus().equals("R") ? "Reserved" : "";
			String sReserveBy = carDetailModel.getReserveBy() == null ? "" : carDetailModel.getReserveBy();
			String sUnReserveBy = carDetailModel.getUnReserveBy() == null ? "" : carDetailModel.getUnReserveBy();
			String sReserveDateTime = carDetailModel.getReserveDateTime() == null ? "" : dateTimeFormat
					.format(carDetailModel.getReserveDateTime());
			String sReserveDate = sReserveDateTime.equals("") ? "" : sReserveDateTime.split(" ")[0];
			String sReserveTime = sReserveDateTime.equals("") ? "" : sReserveDateTime.split(" ")[1];

			dataString += " <tr> " + " 	<td>"
					+ sCategory
					+ "</td>   "
					+ " 	<td>"
					+ sModel
					+ "</td>   "
					+ " 	<td>"
					+ carDetailModel.getRegistrationNo()
					+ "</td>   "
					+ " 	<td>"
					+ ""
					+ "</td>   "
					+ " 	<td>"
					+ ""
					+ "</td>   "
					+ " 	<td><input type='checkbox' name='reserveSelect' id='reserveSelect' class='reserveSelect' />  </td>   "
					+ " 	<td>" + sCarStatus + "</td>   " + " 	<td>" + sReserveBy + "</td>   " + " 	<td>" + sUnReserveBy
					+ "</td> " + " 	<td>" + sReserveDate + "</td>   " + " 	<td>" + sReserveTime + "</td>   " + "</tr>";
		}
		dataString = dataString + "</tbody></table>";
		return dataString;
	}

	@RequestMapping(value = "/getDutySlip", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getDutySlip(
			@ModelAttribute(value = "bookingDetailModel") BookingDetailModel bookingDetailModel,
			BindingResult bindingResult, @RequestParam(value = "bookingDetailId") Long bookingDetailId,
			HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		AddressDetailModel addressDetailModels = null;
		RelatedInfoModel relatedinfoModels = null;
		List<AddressDetailModel> addressDetailModelList = bookingMasterService.getAddress(bookingDetailId);
		List<RelatedInfoModel> relatedInfoModelList = bookingMasterService.getEmail(bookingDetailId);
		if (relatedInfoModelList != null && relatedInfoModelList.size() > 0)
			relatedinfoModels = relatedInfoModelList.get(0);

		if (addressDetailModelList != null && addressDetailModelList.size() > 0)
			addressDetailModels = addressDetailModelList.get(0);
		try {
			bookingDetailModel = bookingMasterService.formFillForDetailEdit(bookingDetailId);
			bookingDetailModel.setAddress(addressDetailModels);
			bookingDetailModel.setRelatedInfo(relatedinfoModels);
			String bookingEmailTemplate = new Utilities().generateDutySlip(bookingDetailModel, request,
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

	/**
	 * Get a diff between two dates
	 * 
	 * @param date1
	 *            the oldest date
	 * @param date2
	 *            the newest date
	 * @param timeUnit
	 *            the unit in which you want the diff
	 * @return the diff value, in the provided unit
	 */
	public long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	@RequestMapping(value = "/deallocate", method = RequestMethod.POST)
	public @ResponseBody JsonResponse deallocate(
			@ModelAttribute(value = "deallocateHistoryModel") DeallocateHistoryModel deallocateHistoryModel,
			BindingResult bindingResult, HttpSession session,
			@RequestParam(value = "bookingDetailId") Long bookingDetailId) {
		JsonResponse res = new JsonResponse();
		if (!bindingResult.hasErrors()) {
			try {
				DutySlipModel dutySlipModel = dutySlipService.getDsModel(bookingDetailId);
				deallocateHistoryModel.setAllocationDateTime(dutySlipModel.getAllocationDateTime());
				deallocateHistoryModel.setBookingDetailModelId(bookingDetailId);
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

				deallocateHistoryModel.setStatus(dutySlipModel.getDutySlipStatus());
				deallocateHistoryModel.setVendorModelId(dutySlipModel.getVendorModel().getId());
				deallocateHistoryModel.setDeallocationDateTime(new Date());
				if (session.getAttribute("loginUserId") != null) {
					Long loginUserId = (Long) session.getAttribute("loginUserId");
					deallocateHistoryModel.setDeallocatedBy(loginUserId);
				}

				dutySlipModel.getBookingDetailModel().setStatus("N");
				dutySlipModel.setDutySlipStatus("U");
				dutySlipService.saveDeallocationModel(deallocateHistoryModel);
				dutySlipService.update(dutySlipModel);

				res.setDutySlipModel(dutySlipModel);
				res.setResult("Successfully Deallocated ..!! ");
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

	@RequestMapping(value = "/getBookingDetailsRecord", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getBookingDetailsRecord(
			@RequestParam("bookingMasterModel.id") Long bookingMasterId,
			@RequestParam("bookingFromDate") String bookingFromDate,
			@RequestParam("bookingToDate") String bookingToDate, HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();

		try {
			List<BookingDetailModel> bookingDetailList = bookingMasterService.getList(bookingMasterId, bookingFromDate,
					bookingToDate);

			res.setBookingDetailList(bookingDetailList);
			res.setDataGrid(showGridBookingDetailsRecords(bookingDetailList));
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@SuppressWarnings("unused")
	public String showGridBookingDetailsRecords(List<BookingDetailModel> bookingDetailModelData) {
		SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dataString = " <table style='table-layout:fixed; width:99.5%; text-align: center;' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>"
				+ " <tr>"
				+ " 	<th width='44px' style='text-align: center;'>Pickup Date</th>"
				+ " 	<th width='25px'  style='text-align: center;'> Time</th>"
				+ " 	<th width='35px' style='text-align: center;'>Car Type</th>"
				+ " 	<th width='61px'>Rental Type</th>"
				+ " 	<th width='70px'>Booked By</th>"
				+ " 	<th width='70px'>Client Name</th>"
				+ " 	<th width='44px'  style='text-align: center;'>Mobile No</th>"
				+ " 	<th width='12px' style='text-align: center;'><input type='checkbox' id='checkAll' onchange='checkboxClickAll(this.id)'></input></th>"
				+ " </tr></thead><tbody>";
		if (bookingDetailModelData == null) {
			return dataString + "</tbody></table>";
		}
		for (BookingDetailModel bookingDetailModel : bookingDetailModelData) {
			String sPickUpDate = bookingDetailModel.getPickUpDate() == null ? "" : dateFormat.format(bookingDetailModel
					.getPickUpDate());
			long lId = bookingDetailModel.getId();
			dataString = dataString
					+ " <tr> "
					+ "	<td><textarea id='pickupDate_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; text-align: center; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:78px; ' >"
					+ sPickUpDate
					+ "</textarea></td>"
					+ " <td><textarea id='time_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; text-align: center; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:35px; ' >"
					+ bookingDetailModel.getStartTime()
					+ "</textarea></td>"
					+ " <td><textarea id='carModel_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; text-align: center; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ bookingDetailModel.getCarModel().getName()
					+ "</textarea><input type='hidden' id=carModelId_"
					+ lId
					+ " value='"
					+ bookingDetailModel.getCarModel().getId()
					+ "'/></td>"
					+ " <td><textarea id='rentalType_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:96px; ' >"
					+ bookingDetailModel.getRentalType().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedBy_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:110px ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedBy().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedForName_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:110px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getBookedForName()
					+ "</textarea></td>"
					+ " <td><textarea id='mobileNo_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; text-align: center; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:70px; ' >"
					+ bookingDetailModel.getBookingMasterModel().getMobileNo() + "</textarea></td>"
					+ "	<td align='center' ><input type='checkbox' class='checkbox' id='checkbox_" + lId
					+ "' onchange='checkboxClicked(" + lId + " , this.id)' style='align: center;'/></td>" + "</tr>";
		}
		dataString = dataString + "</tbody></table>";
		return dataString;
	}

	@RequestMapping(value = "/checkCarAvailability", method = RequestMethod.POST)
	public @ResponseBody JsonResponse checkCarAvailability(@RequestParam("carDetailModel.id") Long carDetailId,
			@RequestParam("reserveDateTime") Date[] reserveDateTime,
			@RequestParam("bookingDetailModel.id") Long[] bookingDetailId, HttpSession session,
			HttpServletRequest request) {
		JsonResponse res = new JsonResponse();

		try {
			List<DutySlipModel> dutySlipModels = new ArrayList<DutySlipModel>();
			String dataString2 = "", id = "";
			String date = "";
			String chk = "";
			for (int i = 0; i < bookingDetailId.length; i++) {
				dutySlipModels = dutySlipService.getlist(bookingDetailId[i], carDetailId);
				if (dutySlipModels.size() > 0) {
					if (!date.equals(dateFormat.format(dutySlipModels.get(0).getBookingDetailModel().getPickUpDate()))) {
						dataString2 = dataString2 + " "
								+ (dateFormat.format(dutySlipModels.get(0).getBookingDetailModel().getPickUpDate()));
					}
					date = (dateFormat.format(dutySlipModels.get(0).getBookingDetailModel().getPickUpDate()));
					id = id + " " + bookingDetailId[i];
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

	@RequestMapping(value = "/reserve", method = RequestMethod.POST)
	public @ResponseBody JsonResponse reserve(@ModelAttribute(value = "dutySlipModel") DutySlipModel dutySlipModel,
			BindingResult bindingResult, HttpSession session,
			@RequestParam(value = "bookingDetailModel.id") Long[] bookingDetailId,
			@RequestParam(value = "smsClient", required = false) String smsClient,
			@RequestParam(value = "smsBooker", required = false) String smsBooker,
			@RequestParam(value = "dispatchSmsOther", required = false) String dispatchSmsOther,
			@RequestParam(value = "dutySlipIdForUpdate") String dutySlipIdForUpdate) {
		JsonResponse res = new JsonResponse();
		if (!bindingResult.hasErrors()) {
			try {

				SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				// SimpleDateFormat dateFormat = new
				// SimpleDateFormat("dd/MM/yyyy");
				String resultMessage = null;
				for (int i = 0; i < bookingDetailId.length; i++) {
					resultMessage = "";
					dutySlipModel.setReserveDateTime(dateTimeFormat.parse(dateTimeFormat.format((new Date()))));
					resultMessage = "Entry Successfully Reserved.";
					String status = dutySlipModel.getDutySlipStatus();
					BookingDetailModel booDetailModel = bookingMasterService.formFillForDetailEdit(bookingDetailId[i]);
					dutySlipModel.setBookingDetailModel(booDetailModel);
					booDetailModel.setStatus(status);
					DutySlipModel dsModel = dutySlipService.getDsRowCount(bookingDetailId[i]);
					if (dsModel == null) {
						if (dutySlipIdForUpdate == null || dutySlipIdForUpdate.equals("")
								|| dutySlipIdForUpdate.equals("0")) {
							dutySlipService.save(dutySlipModel);
							// bookingMasterService.updateDetail(booDetailModel);
							/*
							 * ContactDetailModel contactDetailModel =
							 * authorizedUserService
							 * .getBookerMobile(dutySlipModel2
							 * .getBookingDetailModel
							 * ().getBookingMasterModel().getBookedBy
							 * ().getId()); // Send Booking Confirmation Message
							 * String[] sVariable where: index 0 : Name 1 : Car
							 * No. 2 : Chauffeur Name 3 : Chauffeur Mobile
							 * String[] sSMSVariable = new String[4];
							 * sSMSVariable[0] =
							 * dutySlipModel2.getBookingDetailModel
							 * ().getBookingMasterModel().getBookedForName();
							 * sSMSVariable[1] =
							 * dutySlipModel2.getCarDetailModel
							 * ().getRegistrationNo(); sSMSVariable[2] =
							 * dutySlipModel2
							 * .getCarDetailModel().getCarAllocationModels
							 * ().get(0).getChauffeurId().getName();
							 * sSMSVariable[3] =
							 * dutySlipModel2.getCarDetailModel
							 * ().getCarAllocationModels
							 * ().get(0).getChauffeurId().getMobileNo();
							 * if(smsClient.equals("Y")){ try{
							 * if(!dutySlipModel2
							 * .getBookingDetailModel().getBookingMasterModel
							 * ().getMobileNo().equals("") &&
							 * dutySlipModel2.getBookingDetailModel
							 * ().getBookingMasterModel().getMobileNo().length()
							 * == 10 ){
							 * SMSSender.MyValueFirstSend("carDetailSMS"
							 * ,sSMSVariable
							 * ,"91"+dutySlipModel2.getBookingDetailModel
							 * ().getBookingMasterModel().getMobileNo()); }
							 * }catch(Exception e){ } }
							 * if(smsBooker.equals("Y")){ try{
							 * System.out.println
							 * ("Booker Mobile No :- "+contactDetailModel
							 * .getPersonalMobile());
							 * if(!contactDetailModel.getPersonalMobile
							 * ().equals("") &&
							 * contactDetailModel.getPersonalMobile().length()
							 * == 10 ){
							 * SMSSender.MyValueFirstSend("carDetailSMS"
							 * ,sSMSVariable
							 * ,"91"+contactDetailModel.getPersonalMobile()); }
							 * }catch(Exception e){} } if(dispatchSmsOther !=
							 * null && !dispatchSmsOther.equals("")){ try{
							 * if(dispatchSmsOther.length() == 10 ){
							 * SMSSender.MyValueFirstSend
							 * ("carDetailSMS",sSMSVariable
							 * ,"91"+dispatchSmsOther); } }catch(Exception e){}
							 * }
							 */
						} else {
							dutySlipModel.setId(Long.valueOf(dutySlipIdForUpdate));
							dutySlipService.update(dutySlipModel);
						}
					} else {
						dutySlipModel.setId(Long.valueOf(dsModel.getId()));
						dutySlipService.update(dutySlipModel);
					}
				}
				res.setDutySlipModel(dutySlipModel);
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

	@RequestMapping(value = "/getBookingDetailsRecordForUnreserve", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getBookingDetailsRecordForUnreserve(
			@RequestParam("bookingMasterModel.id") Long bookingMasterId,
			@RequestParam("bookingFromDate") String bookingFromDate,
			@RequestParam("bookingToDate") String bookingToDate, HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();

		try {
			List<DutySlipModel> dutySlipModels = bookingMasterService.getReservedList(bookingMasterId, bookingFromDate,
					bookingToDate);
			res.setDutySlipModelList(dutySlipModels);
			res.setDataGrid(showGridBookingDetUnResRecords(dutySlipModels));
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@SuppressWarnings("unused")
	public String showGridBookingDetUnResRecords(List<DutySlipModel> dutySlipModels) {
		SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dataString = " <table style='table-layout:fixed; width:99.5%; text-align: center;' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>"
				+ " <tr>"
				+ " 	<th width='44px' style='text-align: center;'>Pickup Date</th>"
				+ " 	<th width='25px'  style='text-align: center;'> Time</th>"
				+ " 	<th width='55px' style='text-align: center;'>Car Reg No. </th>"
				+ " 	<th width='75px'>Chauffer Name</th>"
				+ " 	<th width='44px'  style='text-align: center;'>Chauffer Mobile No.</th>"
				+ " 	<th width='35px' style='text-align: center;'>Car Type</th>"
				+ " 	<th width='61px'>Rental Type</th>"
				+ " 	<th width='70px'>Booked By</th>"
				+ " 	<th width='70px'>Client Name</th>"
				+ " 	<th width='44px'  style='text-align: center;'>Mobile No</th>"
				+ " 	<th width='12px' style='text-align: center;'><input type='checkbox' id='checkAllU' onchange='checkboxClickAllU(this.id)'></input></th>"
				+ " </tr></thead><tbody>";
		if (dutySlipModels == null) {
			return dataString + "</tbody></table>";
		}
		for (DutySlipModel dutySlipModel : dutySlipModels) {
			String sPickUpDate = dutySlipModel.getBookingDetailModel().getPickUpDate() == null ? "" : dateFormat
					.format(dutySlipModel.getBookingDetailModel().getPickUpDate());
			String chauffeurName = "";
			String chauffeurMobile = "";
			String carRegNo = dutySlipModel.getCarDetailModel().getRegistrationNo() == null ? "" : dutySlipModel
					.getCarDetailModel().getRegistrationNo();
			if (dutySlipModel.getIsUpdatedChauffeur() != null) {
				if (dutySlipModel.getIsUpdatedChauffeur().equals("Y")) {
					chauffeurName = dutySlipModel.getChauffeurName() == null ? "" : dutySlipModel.getChauffeurName();
					chauffeurMobile = dutySlipModel.getChauffeurMobile() == null ? "" : dutySlipModel
							.getChauffeurMobile();
				}
			} else if (dutySlipModel.getChauffeurModel() != null) {
				chauffeurName = dutySlipModel.getChauffeurModel().getName() == null ? "" : dutySlipModel
						.getChauffeurModel().getName();
				chauffeurMobile = dutySlipModel.getChauffeurModel().getMobileNo() == null ? "" : dutySlipModel
						.getChauffeurModel().getMobileNo();
			}
			long lId = dutySlipModel.getBookingDetailModel().getId();
			dataString = dataString
					+ " <tr> "
					+ "	<td><textarea id='pickupDate_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; text-align: center; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:78px; ' >"
					+ sPickUpDate
					+ "</textarea></td>"
					+ " <td><textarea id='time_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; text-align: center; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:35px; ' >"
					+ dutySlipModel.getBookingDetailModel().getStartTime()
					+ "</textarea></td>"
					+ "	<td><textarea id='carReg_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; text-align: center; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:81px; ' >"
					+ carRegNo
					+ "</textarea></td>"
					+ " <td><textarea id='rentalType_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:117px; ' >"
					+ chauffeurName
					+ "</textarea></td>"
					+ " <td><textarea id='chauffuerMob_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; text-align: center;  background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:68px ' >"
					+ chauffeurMobile
					+ "</textarea></td>"
					+ " <td><textarea id='carModel_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; text-align: center; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:50px; ' >"
					+ dutySlipModel.getBookingDetailModel().getCarModel().getName()
					+ "</textarea><input type='hidden' id=carModelId_"
					+ lId
					+ " value='"
					+ dutySlipModel.getBookingDetailModel().getCarModel().getId()
					+ "'/></td>"
					+ " <td><textarea id='rentalType_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:96px; ' >"
					+ dutySlipModel.getBookingDetailModel().getRentalType().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedBy_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:110px ' >"
					+ dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedBy().getName()
					+ "</textarea></td>"
					+ " <td><textarea id='bookedForName_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:110px; ' >"
					+ dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedForName()
					+ "</textarea></td>"
					+ " <td><textarea id='mobileNo_"
					+ lId
					+ "' cols='26' rows='1' readonly='' style='border: medium none; resize: none; text-align: center; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:70px; ' >"
					+ dutySlipModel.getBookingDetailModel().getBookingMasterModel().getMobileNo() + "</textarea></td>"
					+ "	<td align='center' ><input type='checkbox' class='checkboxU' id='checkboxU_" + lId
					+ "' onchange='checkboxClicked(" + lId + " , this.id)' style='align: center;'/></td>" + "</tr>";
		}
		dataString = dataString + "</tbody></table>";
		return dataString;
	}

	@RequestMapping(value = "/unReserveBooking", method = RequestMethod.POST)
	public @ResponseBody JsonResponse unReserveBooking(
			@ModelAttribute(value = "deallocateHistoryModel") DeallocateHistoryModel deallocateHistoryModel,
			BindingResult bindingResult, HttpSession session,
			@RequestParam(value = "bookingDetailModel.id") Long[] bookingDetId) {
		JsonResponse res = new JsonResponse();
		if (!bindingResult.hasErrors()) {
			try {
				DutySlipModel dutySlipModel = new DutySlipModel();
				for (int i = 0; i < bookingDetId.length; i++) {
					dutySlipModel = dutySlipService.getDsModel(bookingDetId[i]);
					deallocateHistoryModel.setAllocationDateTime(dutySlipModel.getAllocationDateTime());
					deallocateHistoryModel.setBookingDetailModelId(bookingDetId[i]);
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
					deallocateHistoryModel.setStatus(dutySlipModel.getDutySlipStatus());
					deallocateHistoryModel.setVendorModelId(dutySlipModel.getVendorModel().getId());
					deallocateHistoryModel.setDeallocationDateTime(new Date());
					if (session.getAttribute("loginUserId") != null) {
						Long loginUserId = (Long) session.getAttribute("loginUserId");
						deallocateHistoryModel.setDeallocatedBy(loginUserId);
					}
					dutySlipModel.getBookingDetailModel().setStatus("N");
					dutySlipModel.setDutySlipStatus("U");
					dutySlipService.saveDeallocationModel(deallocateHistoryModel);
					dutySlipService.update(dutySlipModel);

				}

				res.setDutySlipModel(dutySlipModel);
				res.setResult("Successfully Unreserve ..!! ");
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

	@RequestMapping(value = "/pageLoadWebBooking")
	public ModelAndView pageLoadWebBooking(Map<String, Object> map, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView("webBooking");
		try {
			String branchesAssigned = (String) session.getAttribute("branchesAssigned");
			session.setAttribute("bookingDetails_sessionData", null);
			map.put("customerOrCompanyNameIdList",
					masterDataService.getGeneralMasterData_UsingCode("CORP:" + branchesAssigned)); // Type
			map.put("tariffIdList", masterDataService.getGeneralMasterData_UsingCode("VTF"));
			map.put("carModelIdList", masterDataService.getGeneralMasterData_UsingCode("VMD"));
			map.put("rentalTypeIdList", masterDataService.getGeneralMasterData_UsingCode("VRT"));
			map.put("MOBList", masterDataService.getGeneralMasterData_UsingCode("MOB"));
			map.put("BCRList", masterDataService.getGeneralMasterData_UsingCode("BCR"));
			map.put("branchList", masterDataService.getGeneralMasterData_UsingCode("VBM-A:" + branchesAssigned));

		} catch (Exception e) {
			logger.error("", e);
		} finally {
			map.put("bookingMasterModel", new BookingMasterModel());
		}
		return modelAndView;
	}

	@RequestMapping(value = "/approveORCancelWebBooking", method = RequestMethod.POST)
	public @ResponseBody JsonResponse approveORCancelWebBooking(@RequestParam("idForUpdate") Long bookingMasterId,
			@RequestParam("allowStatus") String allowStatus, @RequestParam("cancelReason") String cancelReason,
			@RequestParam("isSmsClient") String isSmsClient, @RequestParam("isSmsBroker") String isSmsBroker,
			@RequestParam("otherNo") String otherNo, HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();

		try {
			BookingDetailModel bookingDetailModels = bookingMasterService.formFillForDetailEdit(bookingMasterId);
			BookingMasterModel bookingMasterModel = bookingMasterService.formFillForEdit(bookingDetailModels
					.getBookingMasterModel().getId());
			if (allowStatus.equals("C")) {
				bookingDetailModels.setCancelReason(cancelReason);
				bookingDetailModels.setStatus("E");
				bookingMasterModel.setStatus("E");
				bookingDetailModels.setBookingMasterModel(bookingMasterModel);
			} else {
				bookingDetailModels.setStatus(null);
				bookingDetailModels.setBookingMasterModel(bookingMasterModel);
				bookingMasterService.updateBookingNoForWeb(bookingMasterModel);
			}
			bookingMasterService.updateDetail(bookingDetailModels);
			if (!allowStatus.equals("C")) {
				for (BookingDetailModel bookingDetailModel : bookingMasterModel.getBookingDetailModel()) {
					ContactDetailModel contactDetailModel = authorizedUserService.getBookerMobile(bookingMasterModel
							.getBookedBy().getId());
					// Send Booking Confirmation Message
					/*
					 * String[] sVariable where: index 0 : Name 1 : BookingNo 2
					 * : Date 3 : Time 4 : CarType
					 */
					String[] sSMSVariable = new String[5];
					sSMSVariable[0] = bookingMasterModel.getBookedForName();
					sSMSVariable[1] = bookingMasterModel.getBookingNo();
					sSMSVariable[2] = dateFormat.format(bookingDetailModel.getPickUpDate());
					sSMSVariable[3] = bookingDetailModel.getPickUpTime();
					sSMSVariable[4] = bookingDetailModel.getCarModel().getName();
					if (isSmsClient.equals("Y")) {
						try {
							if (!bookingMasterModel.getMobileNo().equals("")
									&& bookingMasterModel.getMobileNo().length() == 10) {
								SMSSender.MyValueFirstSend("BookingConfirmation", sSMSVariable, "91"
										+ bookingMasterModel.getMobileNo());
							}
						} catch (Exception e) {
						}
					}
					if (isSmsBroker.equals("Y")) {
						try {
							if (!contactDetailModel.getPersonalMobile().equals("")
									&& contactDetailModel.getPersonalMobile().length() == 10) {
								SMSSender.MyValueFirstSend("BookingConfirmation", sSMSVariable, "91"
										+ contactDetailModel.getPersonalMobile());
							}
						} catch (Exception e) {
						}
					}
					if (!otherNo.equals("")) {
						try {
							if (bookingMasterModel.getSmsToOther() != null
									&& bookingMasterModel.getSmsToOther().length() == 10) {
								SMSSender.MyValueFirstSend("BookingConfirmation", sSMSVariable, "91" + otherNo);
							}
						} catch (Exception e) {
						}
					}
					UserModel userModel = userService.formFillForEdit(bookingDetailModel.getBookingMasterModel()
							.getBookingTakenBy().getId());
					GeneralMasterModel vehicleCategoryType = masterDataService
							.getVehicleCategoryForVehicleModel(bookingDetailModel.getCarModel().getId());
					bookingDetailModel.setCarType(vehicleCategoryType);
					String bookingEmailTemplate = new Utilities().generatePdfForBooking(bookingDetailModel, request,
							servletContext.getRealPath("/WEB-INF/"), userModel);
					String sFilePath = servletContext.getRealPath("/WEB-INF/") + "/pdf/booking"
							+ bookingMasterModel.getBookingNo() + ".pdf";
					if (bookingMasterModel.getEmailToBooker() != null) {
						if (bookingMasterModel.getEmailToBooker().equals("Y")) {
							Utilities.sentEmail(bookingMasterModel.getBookerEmail(), "Booking Confirmation",
									bookingEmailTemplate, sFilePath);
						}
					}
					if (bookingMasterModel.getEmailToClient() != null) {
						if (bookingMasterModel.getEmailToClient().equals("Y")) {
							Utilities.sentEmail(bookingMasterModel.getClientEmail(), "Booking Confirmation",
									bookingEmailTemplate, sFilePath);
						}
					}
					if (bookingMasterModel.getEmailToOther() != null) {
						if (!bookingMasterModel.getEmailToOther().equals("")) {
							Utilities.sentEmail(bookingMasterModel.getEmailToOther(), "Booking Confirmation",
									bookingEmailTemplate, sFilePath);
						}
					}
				}
			}
			res.setResult("Update Successfully");
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value = "/getPassengerDetails", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getCarModelAsCarType(@RequestParam("bookingDetailId") Long bookingDetailId) {
		JsonResponse res = new JsonResponse();
		try {
			List<PassengerDetailModel> passengerDetailModelList = bookingMasterService
					.getPassengerDetails(bookingDetailId);
			res.setPassengerDetailModelList(passengerDetailModelList);
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value = "/updatePassengerDetails", method = RequestMethod.POST)
	public @ResponseBody JsonResponse uodatePassengerDetails(@RequestParam("name") String[] name,
			@RequestParam("mobile") String[] mobile, @RequestParam("age") String[] age,
			@RequestParam("sex") String[] sex, @RequestParam("idDetails") String[] idDetails,
			@RequestParam("idForUpdate") Long idForUpdate) {
		JsonResponse res = new JsonResponse();
		try {
			BookingDetailModel bookingDetailModel = bookingMasterService.formFillForDetailEdit(idForUpdate);
			if (bookingDetailModel.getBookingMasterModel().getCorporateId().getIsPassengerInfo() != null
					&& bookingDetailModel.getBookingMasterModel().getCorporateId().getIsPassengerInfo().equals("Y")) {
				bookingMasterService.deletePassengerDetails(idForUpdate);
				PassengerDetailModel passengerDetailModel = new PassengerDetailModel();
				for (int i = 0; i < mobile.length; i++) {
					passengerDetailModel.setBookingDetailId(bookingDetailModel);
					passengerDetailModel.setName(name[i]);
					passengerDetailModel.setMobile(mobile[i]);
					passengerDetailModel.setAge(age[i]);
					passengerDetailModel.setSex(sex[i]);
					passengerDetailModel.setIdDetails(idDetails[i]);
					bookingMasterService.save(passengerDetailModel);
					res.setStatus("Success");
				}
			}
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("", e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	private String parseStatus(String sStatus, boolean bDateAfter) {
		String sParsedStatus = "";
		if (sStatus == null) {
			if (bDateAfter)
				sParsedStatus = "Expired";
			else
				sParsedStatus = "New";
		} else if (sStatus.equals("N")) {
			sParsedStatus = "Advance";
		} else if (sStatus.equals("A")) {
			sParsedStatus = "Allocated";
		} else if (sStatus.equals("D")) {
			sParsedStatus = "Dispatched";
		} else if (sStatus.equals("C") || sStatus.equals("X")) {
			sParsedStatus = "Cancelled";
		} else if (sStatus.equals("R")) {
			sParsedStatus = "Reserved";
		}
		return sParsedStatus;
	}
}
