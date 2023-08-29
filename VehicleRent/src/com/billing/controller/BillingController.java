package com.billing.controller;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.billing.model.CoverLetterModel;
import com.billing.model.InvoiceModel;
import com.billing.model.TransactionDetailModel;
import com.billing.service.BillingService;
import com.common.model.AddressDetailModel;
import com.common.service.MasterDataService;
import com.corporate.model.CorporateModel;
import com.corporate.service.CorporateService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.master.model.CarAllocationModel;
import com.master.model.CorporateTariffDetModel;
import com.master.model.CorporateTaxDetModel;
import com.master.model.GeneralMasterModel;
import com.master.model.MasterModel;
import com.master.service.CorporateTariffService;
import com.operation.model.BookingDetailModel;
import com.operation.model.BookingMasterModel;
import com.operation.model.DutySlipModel;
import com.operation.model.HistoryDutySlipModel;
import com.operation.service.BookingMasterService;
import com.operation.service.DutySlipService;
import com.relatedInfo.model.RelatedInfoModel;
import com.relatedInfo.service.RelatedInfoService;
import com.user.model.UserModel;
import com.user.service.UserService;
import com.util.JsonResponse;
import com.util.Message;
import com.util.Utilities;

@Controller
@SessionAttributes({ "bookingDetails_sessionData", "tariffIdList", "carTypeIdList", "rentalTypeIdList", "branchList" })

public class BillingController {
	private static final Logger logger = Logger.getLogger(BillingController.class);
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
	long bookingmasterid;
	String sBookingNo=null;
	long lInvoiceNo;
	
	@Autowired
	private CorporateTariffService corporateTariffService;
	
	@Autowired
	private CorporateService corporateService;
	
	@Autowired
	private MasterDataService masterDataService;
	
	@Autowired
	private BillingService billingService;

	@Autowired
	private BookingMasterService bookingMasterService;
	
	@Autowired
	private DutySlipService dutySlipService;
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private UserService userService;

	@Autowired
	private RelatedInfoService relatedInfoService;

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields(new String[] { "id" });
		dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
	@RequestMapping("/pageLoadBilling/{jobType}")
	public ModelAndView pageLoadBilling(Map<String, Object> map, HttpSession session, @PathVariable String jobType) {
		ModelAndView modelAndView = new ModelAndView("billing");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try {
			session.setAttribute("bookingDetails_sessionData", null);
			map.put("customerOrCompanyNameIdList", masterDataService.getGeneralMasterData_UsingCode("CORP:" + branchesAssigned + ":" + jobType)); // Type
			map.put("branchList", masterDataService.getGeneralMasterData_UsingCode("VBM-A:"+branchesAssigned));
			map.put("tariffIdList", masterDataService.getGeneralMasterData_UsingCode("VTF"));
			map.put("carModelIdList", masterDataService.getGeneralMasterData_UsingCode("VMD"));
			map.put("rentalTypeIdList", masterDataService.getGeneralMasterData_UsingCode("VRT"));
			map.put("MOBList", masterDataService.getGeneralMasterData_UsingCode("MOP"));
			map.put("bookedByList", billingService.getBookedBy());
			map.put("BCRList", masterDataService.getGeneralMasterData_UsingCode("BCR"));
			map.put("carRegNoList", masterDataService.getCarRegistrationNoList(""));
			map.put("carOwnerTypeList", masterDataService.getGeneralMasterData_UsingCode("VOT"));
			map.put("usedByList", billingService.getUsedBy());
			map.put("closedByList", billingService.getClosedBy());
			map.put("invoiceNoList", billingService.getInvoiceNo());
			map.put("jobType", jobType); 
			
			if(jobType.equals("Invoice")){
				List<MasterModel> masterModels =  billingService.getAllInvoiceNo();
				if(masterModels.size() > 0){
					for(MasterModel masterModel : masterModels) {
						saveInvoice(masterModel.getName(),masterModel.getId().longValue(),masterModel.getParentid().longValue(),0,0);
					}
				}
			}
		} catch (Exception e) {
			logger.error("",e);
		} finally {
			map.put("bookingMasterModel", new BookingMasterModel());
		}
		return modelAndView;
	}

	@RequestMapping(value = "/searchBilling", method = RequestMethod.POST)
	public void searchBilling(@RequestParam("sCriteria") String sCriteria,
			   @RequestParam("jobType") String jobType,
			   @RequestParam("sValue") String sValue, 
			   @RequestParam(value="sPageStatus", required=false) String sPageStatus, 
			   HttpSession session, HttpServletRequest request,HttpServletResponse response) {
		response.setContentType("application/json");
		HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
		try {
			String[] sCriteriaList = sCriteria.split(",");
			String[] sValueList = sValue.split(",");
			List<DutySlipModel> dutySlipModels = billingService.list(jobType,sCriteriaList, sValueList);
			if(sPageStatus.equals("R")){
				Long lTotalDs = 0l;
				HashMap<Long, String> branch = new HashMap<Long, String>();
				HashMap<Long, String> hub = new HashMap<Long, String>();
				HashMap<Long, String> corporate = new HashMap<Long, String>();
				HashMap<Long, String> carNo = new HashMap<Long, String>();
				HashMap<Long, String> rentalType = new HashMap<Long, String>();
				for(DutySlipModel dutySlipModel : dutySlipModels ){
					branch.put(dutySlipModel.getBookingDetailModel().getBranch().getId(),dutySlipModel.getBookingDetailModel().getBranch().getName());
					hub.put(dutySlipModel.getBookingDetailModel().getOutlet().getId(), dutySlipModel.getBookingDetailModel().getOutlet().getName());
					corporate.put(dutySlipModel.getBookingDetailModel().getBookingMasterModel().getCorporateId().getId(), dutySlipModel.getBookingDetailModel().getBookingMasterModel().getCorporateId().getName());
					carNo.put(dutySlipModel.getCarDetailModel().getId(), dutySlipModel.getCarDetailModel().getRegistrationNo());
					rentalType.put(dutySlipModel.getBookingDetailModel().getRentalType().getId(), dutySlipModel.getBookingDetailModel().getRentalType().getName());
					lTotalDs++;
				}
				JSONROOT.put("branchList", getListFromMap(branch));
				JSONROOT.put("hubList", getListFromMap(hub));
				JSONROOT.put("corporateList", getListFromMap(corporate));
				JSONROOT.put("carNoList", getListFromMap(carNo));
				JSONROOT.put("rentalTypeList", getListFromMap(rentalType));
				JSONROOT.put("lTotalDs", lTotalDs);
			}else{
//				res.setDutySlipModelList(dutySlipModels);
				JSONROOT.put("dataGrid", showGrid(dutySlipModels,jobType));
			}
			JSONROOT.put("status", "Success");
		}catch(ConstraintViolationException e){
			logger.error("",e);
			JSONROOT.put("status", "Failure");
			JSONROOT.put("result", Message.DUPLICATE_ERROR_MESSAGE);
		}catch(Exception e) {
			logger.error("",e);
			JSONROOT.put("status", "Failure");
			JSONROOT.put("result", Message.FATAL_ERROR_MESSAGE);
		}finally{
			try{
				String jsonArray = gson.toJson(JSONROOT);
				response.getWriter().print(jsonArray);
				JSONROOT.clear();
			}catch(Exception e){}
		}
	}

	
	private List<GeneralMasterModel> getListFromMap(HashMap<Long, String> mapObject){
		List<GeneralMasterModel> objectList = new ArrayList<GeneralMasterModel>();
		for ( Map.Entry<Long, String> entry : mapObject.entrySet()) {
			GeneralMasterModel gmObj = new GeneralMasterModel();
			gmObj.setId(entry.getKey());
			gmObj.setName(entry.getValue());
			objectList.add(gmObj);
		}
		return objectList;
	}
	public String showGrid(List<DutySlipModel> dutySlipModels, String jobType) {
		SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dataString = " <table style='table-layout:fixed; width:99% height:60%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>					"
				+ " <tr style='font-size:8pt;'> ";
		 if(jobType.equals("DutySlipReceive") || jobType.equals("DutySlipClose") || jobType.equals("DutySlipUnbilled")){
					dataString += " <th width='55px'>Branch</th> 		"
							+ " 	<th width='55px'>Hub</th> 					"
							+ " 	<th width='60px'>Car No.</th> 				"
							+ " 	<th width='70px'>Booked Car</th> 		"
							+ " 	<th width='70px'>Allotted Car</th> 		"
							+ " 	<th width='80px'>Duty Slip Date </th> 	"
							+ " 	<th width='80px'>Duty Slip No.</th> 		"
							+ " 	<th width='65px'>Rental Type</th> 		"
							+ " 	<th width='95px'>Corporate Name</th> 	"
							+ " 	<th width='60px'>Booked By</th> 			"
							+ " 	<th width='66px'>Client Name</th> 		"
							+ " 	<th width='66px'>Client Mobile No.</th>";
		 		}if(jobType.equals("DutySlipReceive") || jobType.equals("DutySlipClose")){ 
		 			dataString +="	<th width='45px'>Parking Amount</th> 	"
							+ " 	<th width='45px'>Toll Tax</th> 				"
							+ " 	<th width='45px'>State Tax</th> 			";
			 	}if(jobType.equals("DutySlipClose")){
					dataString +=" 	<th width='65px'>Received By</th>"
							+ " 	<th width='80px'>Received Date</th> 	 ";
				}if(jobType.equals("DutySlipUnbilled")){
					dataString +="	<th width='45px'>Parking & Taxes</th> 	"
							+ " 	<th width='45px'>Unbilled Amount</th> 			"
							+" 	<th width='65px'>Closed By</th> 	 		"
							+ " 	<th width='80px'>Closed Date</th> 		";
				}
			dataString 	+=" 	<th width='70px'>Action</th> 			 ";
			if(jobType.equals("DutySlipUnbilled")){
				dataString +=" 	<th width='65px'><input type='checkbox' class='checkbox' id='checkboxAll' onClick='checkboxAllClicked()' style='align: center;'/><label style='align: center;'>Select All</label></th>";
			}
			dataString 	+= " </tr>			" + " </thead><tbody>         ";
		if (dutySlipModels == null) {
			return dataString + "</tbody></table>";
		}
		for (DutySlipModel dutySlipModel : dutySlipModels) {
			if(dutySlipModel == null) continue;
			String sClosedUser = "";
			String sReceivedUser = "";
			if(jobType.equals("DutySlipUnbilled") || jobType.equals("DutySlipClose")){
				List<UserModel> userModelList = userService.list();
				for(UserModel userModel : userModelList){
					if (jobType.equals("DutySlipUnbilled")){
						if(dutySlipModel.getDutySlipClosedBy()!= null && dutySlipModel.getDutySlipClosedBy() == userModel.getId().longValue())
							sClosedUser = userModel.getUserName();
					}
					if (jobType.equals("DutySlipClose")){
						if(dutySlipModel.getDutySlipReceivedBy() == null){
							sReceivedUser = "System";
						}else if(dutySlipModel.getDutySlipReceivedBy() == userModel.getId().longValue())
							sReceivedUser = userModel.getUserName();
					}
				}
			}
					
			DecimalFormat df=new DecimalFormat("#.#");
			long lId = dutySlipModel.getId();	
			Date dispatchDate = dutySlipModel.getDispatchDateTime();
			Date dSReceiveDate = dutySlipModel.getDutySlipReceivedByDate();
			Double allTaxs=0.00;	
			Double parkingAmount=0.00;
			Double tollTax=0.00;
			Double stateTax=0.00;
			
			if(dutySlipModel.getParkingAmount()!=null)
			{
				parkingAmount=dutySlipModel.getParkingAmount();
			}
			if(dutySlipModel.getTollTaxAmount()!=null)
			{
				tollTax=dutySlipModel.getTollTaxAmount();
			}
			
			if(dutySlipModel.getStateTax()!=null)
			{
				stateTax=dutySlipModel.getStateTax();
			}
			allTaxs = parkingAmount+tollTax+stateTax;
			dataString = dataString 
					+ " <tr> "
					+ " <td><textarea id='Branch_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
									+dutySlipModel.getBookingDetailModel().getBranch().getName()+"</textarea></td>"
								+ " <td><textarea id='Hub_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
									+ dutySlipModel.getBookingDetailModel().getOutlet().getName() + "</textarea></td>";
					 if (jobType.equals("DutySlipReceive") || jobType.equals("DutySlipClose") || jobType.equals("DutySlipUnbilled")) { 
						dataString +=  "<td><textarea id='carNo_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"
								 		+ dutySlipModel.getCarDetailModel().getRegistrationNo()+ "</textarea></td>"
						 		+"<td><textarea id='bookedCar_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"
								 		+ dutySlipModel.getBookingDetailModel().getCarModel().getName()+ "</textarea></td>"
						 		+"<td><textarea id='allottedCar_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"
								 		+ dutySlipModel.getCarDetailModel().getModel().getName()+ "</textarea></td>";
								if(dispatchDate!=null)	{	
						 		dataString+="<td><textarea id='dutySlipDate_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"
								 		+ dateFormat.format(dispatchDate)+ "</textarea></td>";
								}
								else{
									dataString+="<td><textarea id='dutySlipDate_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' ></textarea></td>";
								}
						 	dataString+="<td><textarea id='dutySlipNo_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"
								 		+ dutySlipModel.getDutySlipNo()+ "</textarea></td>"
						 		+"<td><textarea id='rentalType_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"
								 		+ dutySlipModel.getBookingDetailModel().getRentalType().getName()+ "</textarea></td>"	
						 		+ " <td><textarea id='corporate_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:95px; ' >"
						 				+ dutySlipModel.getBookingDetailModel().getBookingMasterModel().getCorporateId().getName()+ "</textarea>"
				 						+ "<input id='corporateId_"+ lId+ "' type='hidden' value= '" + dutySlipModel.getBookingDetailModel().getBookingMasterModel().getCorporateId().getId()+ "'/></td>"
				 				+" <td><textarea id='bookedBy_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >" 
						 				+ dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedBy().getName() + "</textarea></td>";
								if(dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedForName()!=null){
				 				dataString+= " <td><textarea id='bookedFor_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:66px; ' >" 
				 						+ dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedForName() + "</textarea></td>";
								}
								else{
									dataString+= " <td><textarea id='bookedFor_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:66px; ' ></textarea></td>";
								}
					 } if (jobType.equals("DutySlipReceive")) { 
							dataString +=  "<td id='clientName_"+lId+"'>"+dutySlipModel.getBookingDetailModel().getBookingMasterModel().getMobileNo()+"</td>"
											  +"<td width='45px' align='center' ><input type='text'  class='parking'  id='parking_"+ lId+ "' style= 'width: 45px; align: center;' /></td>"
											  +"<td width='45px' align='center' ><input type='text' class='toll' id='toll_"+lId+"' style= 'width: 45px; align: center;' /></td>"
											  +"<td width='45px' align='center' ><input type='text' class='state' id='state_"+lId+"' style= 'width: 45px; align: center;' /></td>";
					 }if (jobType.equals("DutySlipClose") || jobType.equals("DutySlipUnbilled")) {
						 if(dutySlipModel.getBookingDetailModel().getBookingMasterModel().getMobileNo()!=null){
						 	dataString +=  "<td id='clientName_"+lId+"'>"+dutySlipModel.getBookingDetailModel().getBookingMasterModel().getMobileNo()+"</td>";
						 }
						 else{
							 dataString +=  "<td id='clientName_"+lId+"'></td>";
						 }
					 }if (jobType.equals("DutySlipClose")) {
						 String sTollTax = null;
						 String sStateTax = null;
						 String sParking = null;
						 if(dutySlipModel.getTollTaxAmount() != null){
							 sTollTax = df.format(dutySlipModel.getTollTaxAmount());
						 }else{
							 sTollTax =""; 
						 }
						 if(dutySlipModel.getStateTax() != null){
							 sStateTax = df.format(dutySlipModel.getStateTax());
						 }else{
							 sStateTax =""; 
						 }
						 if(dutySlipModel.getParkingAmount() != null){
							 sParking = df.format(dutySlipModel.getParkingAmount());
							 
						 }else{
							 sParking =""; 
						 }
						 	dataString +=  "<td width='45px' align='center' ><input type='text'  class='parking'  id='parking_"+ lId+ "' style= 'width: 45px; align: center;' value='"+sParking+"' /></td>"
								    +"<td width='45px' align='center' ><input type='text' class='toll' id='toll_"+lId+"' style= 'width: 45px; align: center;' value='"+sTollTax+"'/></td>"
								    +"<td width='45px' align='center' ><input type='text' class='state' id='state_"+lId+"' style= 'width: 45px; align: center;' value='"+sStateTax+"'/></td>"
						 			+" <td><textarea id='dutySlipReceivedBy_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:95px; ' >"
										+ sReceivedUser+ "</textarea></td>"
							 		+" <td><textarea id='dutySlipReceivedDate_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >" 
								 		+ dateFormat.format(dSReceiveDate) + "</textarea></td>"
							 		+ " <td>"+ " <a href='javascript:closeDS("+ dutySlipModel.getId()+ ","+dutySlipModel.getBookingDetailModel().getId()+")'><img src='../img/crossButton.png'  title='Close' width='20' height='20'/></a> "
							 			+ "  <a href='javascript:viewDS("+ dutySlipModel.getId()+ ")'><img src='../img/viewButton.png'  title='View' width='20' height='20'/></a> "
				 						+ "  <a href='javascript: printDS("+ dutySlipModel.getId()+ ")' ><img src='../img/print.gif' ' title='Print' width='20' height='20'/></a> "+ " </td> ";
					 	}if (jobType.equals("DutySlipUnbilled")) {
						 	dataString +=  "<td width='45px' align='center' ><textarea class='allTaxs' cols='26' rows='1' id='allTaxs_"+ lId+ "' style= 'border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px;'>"+allTaxs+"</textarea></td>"
						 			+"<td width='45px' align='center' ><textarea class='unbilledAmt'  cols='26' rows='1' id='unbilledAmt_"+ lId+ "' style= 'border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px;'>"
						 			+ (dutySlipModel.getTotalFare() != null ? Math.round(dutySlipModel.getTotalFare()) : "" )+"</textarea></td>"
						 			+"<input type='text'  class='corp'  id='corp_"+ lId+ "' style= 'width: 45px; align: center; display: none;' value='"+dutySlipModel.getBookingDetailModel().getBookingMasterModel().getCorporateId().getId()+"' />";
						 			dataString	+=" <td><textarea id='dutySlipClosedBy_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:95px; ' >"
										+ sClosedUser+ "</textarea></td>";
						 			
							 		dataString+=" <td><textarea id='dutySlipClosedDate_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >" 
								 		+ dateFormat.format(dutySlipModel.getDutySlipClosedByDate()) + "</textarea></td>"
							 		+ "  <td>"+ " <a href='#'><img src='../img/crossButton.png'  title='Close' width='20' height='20'/></a> "
						 			+ "  <a href='javascript:viewDS("+ dutySlipModel.getId()+ ")'><img src='../img/viewButton.png'  title='View' width='20' height='20'/></a> "
			 						+ "  <a href='javascript: printDS("+ dutySlipModel.getId()+ ")' ><img src='../img/print.gif' ' title='Print' width='20' height='20'/></a> "+ " </td> ";
					 	}if (jobType.equals("DutySlipReceive") || jobType.equals("DutySlipUnbilled")) { 						  
					 		dataString +=  "<td align='center' ><input type='checkbox' class='checkbox' id='checkbox_"+lId+"' onClick='checkboxClicked(" + lId + ")' style='align: center;'/></td>";
					 	}
		}
		dataString = dataString + " </tr></tbody></table>";
		return dataString;
	}
	
	@RequestMapping(value = "/invoiceSearch", method = RequestMethod.POST)
	public @ResponseBody JsonResponse invoiceSearch(@RequestParam("sCriteria") String sCriteria, 
			@RequestParam("sValue") String sValue,
			HttpSession session,HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		String jobType = "";
		try {
			String[] sCriteriaList = sCriteria.split(",");
			String[] sValueList = sValue.split(",");
			
			for(int iCount = 0 ; iCount < sCriteriaList.length; iCount++){
				if(sCriteriaList[iCount].equals("jobType")) jobType = sValueList[iCount];
			}
			List<InvoiceModel> invoiceModels = billingService.getInvoiceList(sCriteriaList, sValueList);
			res.setDataGrid(showGridDetailInv(invoiceModels,jobType));
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value = "/invoicecorporateBranchWise", method = RequestMethod.POST)
	public @ResponseBody JsonResponse invoicecorporateBranchWise(@RequestParam("branchId") String branchId) {
		JsonResponse res = new JsonResponse();
		List<GeneralMasterModel> generalMasterModelList = null;
		try {
			generalMasterModelList = masterDataService.getGeneralMasterData_UsingCode("CORP:," + branchId + ",");

			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		} finally {
			res.setGeneralMasterModelList(generalMasterModelList);
		}
		return res;
	}
	
	public String showGridDetail(List<DutySlipModel> dutySlipModels) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dataString = " <table style='table-layout:fixed; width:99% height:60%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>					"
				+ " <tr style='font-size:8pt;'> 									"
				+ " 	<th width="
				+ Message.ACTION_SIZE
				+ ">Action</th> 		  	"
				+ " 	<th width='77px'>Branch</th> 		"
				+ " 	<th width='60px'>Hub</th> 				"
				+ " 	<th width='45px'>Booking Mode</th> 			"
				+ " 	<th width='75px'>Invoice No</th> 		"
				+ " 	<th width='75px'>Invoice Date</th> 			"
				+ " 	<th width='95px'>Corporate Name</th> 			"
				+ " 	<th width='60px'>Booked By</th> 		"
				+ " 	<th width='66px'>Client Name</th> 		"
				+ " 	<th width='65px'>Rental Type</th> 		"
				+ " 	<th width='80px'>Basic Fare</th> 		"
				+ " 	<th width='60px'>Misc Charges</th> 		"
				+ " 	<th width='60px'>Parking Amount</th> 	"
				+ " 	<th width='50px'>Toll Tax</th> 			"
				+ " 	<th width='60px'>State Tax</th> 			"
				+ " 	<th width='60px'>GST</th> 			"
				+ " 	<th width='60px'>Total Amount</th> 			"
				+ " 	<th width='75px'>Closed By</th> 			"
				+ " 	<th width='65px'>Payment Mode</th>   "  + " </tr>	</thead>					" + " <tbody>					";	
		
		if (dutySlipModels == null) {
			return dataString + "</tbody></table>";
		}

		String sInvoiceNo = "" ,sServiceTaxs ="",sInvoiceDate="",sBranch="",sOutlet="",sCorporate="",sBookedByName="",sClosedUser="",sMobName="",sCorpId="";
		String sBookedForName="",sRentalTypeName="", sBasicFare = "",sMisc = "",sParking = "",sToll = "",sState = "",sDiscount = "",sFinalGrandTotal = "";
		HashMap<String,Object> mapReturn = getInvoiceValues(dutySlipModels);

		for(Entry<String, Object> invoicesMap : mapReturn.entrySet()) {
			HashMap<String,String> mapReturnIn = (HashMap<String,String>) invoicesMap.getValue();
			long lId = Long.parseLong(mapReturnIn.get("lId"));
			sInvoiceNo = mapReturnIn.get("invoiceNo");
			sInvoiceDate = mapReturnIn.get("invoiceDate");
			sBranch = mapReturnIn.get("branchName");
			sOutlet = mapReturnIn.get("outletName");
			sCorporate = mapReturnIn.get("corporateName");
			sBookedByName = mapReturnIn.get("bookedByName");
			sBookedForName = mapReturnIn.get("bookedForName");
			sRentalTypeName = mapReturnIn.get("rentalTypeName");
			sBasicFare = mapReturnIn.get("dbasicFare");
			sMisc = mapReturnIn.get("dMisc");
			sParking = mapReturnIn.get("dParking");
			sToll = mapReturnIn.get("dToll");
			sState = mapReturnIn.get("dState");
			sServiceTaxs = mapReturnIn.get("sServiceTaxs");
			sDiscount = mapReturnIn.get("ssdiscount");
			sFinalGrandTotal = mapReturnIn.get("finalGrandTotal");
			sClosedUser = mapReturnIn.get("sClosedUser");
			sClosedUser = mapReturnIn.get("sClosedUser");
			sMobName = mapReturnIn.get("mobName");
			sCorpId = mapReturnIn.get("corpId");

			dataString += " <tr> "
					+ " <td>"
					+ " 	<a href='javascript: checkMultipleInvoice(\""+sInvoiceNo+"\",\""+sCorpId+"\",\"V\")' ><img src='../img/viewButton.png' border='0' title='View' width='20' height='20'/></a> "
					+ " 	<a href='javascript: checkMultipleInvoice(\""+sInvoiceNo+"\",\""+sCorpId+"\",\"P\")' ><img src='../img/print.gif' border='0' title='Print' width='20' height='20'/></a> "
					+ " </td> "		
					+ " <td><textarea id='Branch_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ sBranch+"</textarea></td>"
					+ " <td><textarea id='Hub_"+ lId	+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
					+ sOutlet + "</textarea></td>"
					+ " <td id='bookingMode_"+lId+"'>Postpaid</td>"
					+ " <td><textarea id='invoice_"+lId+"' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"
					+ sInvoiceNo+ "</textarea></td>"
					+ " <td><textarea id='invoiceDate_"+ lId	+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"
					+ sInvoiceDate + "</textarea></td>"
					+ " <td><textarea id='corporate_"+ lId	+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:95px; ' >"
					+ sCorporate+ "</textarea></td>"
					+ " <td><textarea id='bookedBy_"+ lId	+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >" 
					+ sBookedByName + "</textarea></td>"
				    + " <td><textarea id='bookedFor_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:66px; ' >" 
					+ sBookedForName + "</textarea></td>"
					+ "<td><textarea id='rentalType_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"
					+ sRentalTypeName+ "</textarea></td>"
					+ " <td><textarea id='basicFare_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:80px; ' >"  
					+ sBasicFare  + "</textarea></td>"
					+ " <td><textarea id='miscCharges_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"   
					+ sMisc + "</textarea></td>"
					+ " <td><textarea id='parkingAmount_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"        
					+ sParking+ "</textarea></td>"
					+ " <td><textarea id='tollTax_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"          
					+ sToll+ "</textarea></td>"  
					+ " <td><textarea id='stateTax_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"          
					+ sState+ "</textarea></td>"
					+ " <td><textarea id='serviceTax_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"           
					+ sServiceTaxs + "</textarea></td>"  
					+ " <td><textarea class='invoiceAmt' id='totalamount_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"            
					+ sFinalGrandTotal+ "</textarea></td>"  
				 	+ " <td><textarea id='closedBy_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"            
					+ sClosedUser + "</textarea></td>"  
				 	+ " <td><textarea id='paymentMode_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:65px; ' >"              
					+ sMobName+ "</textarea></td>"; 
			}
		dataString = dataString + "</tbody></table>";
		return dataString;
	}

	public String showGridDetailInv(List<InvoiceModel> invoiceModels, String jobType) {
		String dataString = " <table style='table-layout:fixed; width:99% height:60%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>					"
				+ " <tr style='font-size:8pt;'> 									"
				+ " 	<th width="
				+ Message.ACTION_SIZE
				+ ">";
		if(jobType.equals("CoverLetter")){ 
			dataString +=" <input type='checkbox' class='checkbox' id='checkboxAll' onClick='checkboxAllClicked()' style='align: center;'/><label style='align: center;'>Select All</label>";
		}else{
			dataString +="Action</th> 		  	";
		}
		dataString +=" 	<th width='77px'>Branch</th> 		"
				+ " 	<th width='60px'>Hub</th> 				"
				+ " 	<th width='45px'>Booking Mode</th> 			"
				+ " 	<th width='75px'>Invoice No</th> 		"
				+ " 	<th width='75px'>Invoice Date</th> 			"
				+ " 	<th width='95px'>Corporate Name</th> 			"
				+ " 	<th width='60px'>Booked By</th> 		"
				+ " 	<th width='66px'>Client Name</th> 		"
				+ " 	<th width='65px'>Rental Type</th> 		"
				+ " 	<th width='80px'>Basic Fare</th> 		"
				+ " 	<th width='60px'>Misc Charges</th> 		"
				+ " 	<th width='60px'>Parking Amount</th> 	"
				+ " 	<th width='50px'>Toll Tax</th> 			"
				+ " 	<th width='60px'>State Tax</th> 			"
				+ " 	<th width='60px'>GST</th> 			"
				+ " 	<th width='60px'>Total Amount</th> 			"
				+ " 	<th width='75px'>Closed By</th> 			"
				+ " 	<th width='65px'>Payment Mode</th>   "  
				+ " </tr>	"
				+ "</thead>					" 
				+ " <tbody>					";	
		
		if (invoiceModels == null) {
			return dataString + "</tbody></table>";
		}
		SimpleDateFormat opFormat = new SimpleDateFormat("dd/MM/yyyy");
		DecimalFormat twoDigit = new DecimalFormat("0.00");
		for(InvoiceModel invoiceModel : invoiceModels) {
			long lId = invoiceModel.getId().longValue();
			String[] sTaxVal = invoiceModel.getTaxValues().split("\\##");
			double dTotalTax = 0;
			double dTotalBasicFair = invoiceModel.getTotalBasicFair() + invoiceModel.getExtraHrTotal() + invoiceModel.getExtraKmTotal();
			
			for(String sTax : sTaxVal){
				if(!sTax.isEmpty() && !sTax.contains("-")) dTotalTax += Double.parseDouble(sTax);
			}

			if(jobType.equals("CoverLetter")){
		 		dataString +=  "<tr><td align='center' ><input type='checkbox' class='checkbox' id='checkbox_"+lId+"' onClick='checkboxClicked(" + lId + ")' style='align: center;'/></td>";
			}else{
				dataString += " <tr> "
						+ " <td>"
						+ " 	<a href='javascript: checkMultipleInvoice(\""+invoiceModel.getInvoiceNo()+"\",\""+invoiceModel.getCorpId()+"\",\"V\")' ><img src='../img/viewButton.png' border='0' title='View' width='20' height='20'/></a> "
						+ " 	<a href='javascript: checkMultipleInvoice(\""+invoiceModel.getInvoiceNo()+"\",\""+invoiceModel.getCorpId()+"\",\"P\")' ><img src='../img/print.gif' border='0' title='Print' width='20' height='20'/></a> "
						+ " </td> "	;	
			}
			dataString += 
					 " <td><textarea id='Branch_"+ lId + "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:77px; ' >"
					+ invoiceModel.getBrName() +"</textarea></td>"
					+ " <td><textarea id='Hub_"+ lId	+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"
					+ invoiceModel.getBrName() + "</textarea></td>"
					+ " <td id='bookingMode_"+lId+"'>Postpaid</td>"
					+ " <td><textarea id='invoice_"+lId+"' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"
					+ invoiceModel.getInvoiceNo()+ "</textarea></td>"
					+ " <td><textarea id='invoiceDate_"+ lId	+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"
					+ opFormat.format(invoiceModel.getInvoiceDt()) + "</textarea></td>"
					+ " <td><textarea id='corporate_"+ lId	+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:95px; ' >"
					+ invoiceModel.getCorpName()+ "</textarea><input id='corporateId_"+ lId+ "' type='hidden' value= '" + invoiceModel.getCorpId()+ "'/></td>"
					+ " <td><textarea id='bookedBy_"+ lId	+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >" 
					+ invoiceModel.getBookedBy() + "</textarea></td>"
				    + " <td><textarea id='bookedFor_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:66px; ' >" 
					+ invoiceModel.getUsedBy() + "</textarea></td>"
					+ "<td><textarea id='rentalType_"+ lId+ "' cols='26' rows='3' disabled='disabled' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"
					+ invoiceModel.getRentalType()+ "</textarea></td>"
					+ " <td><textarea id='basicFare_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:80px; ' >"  
					+ twoDigit.format(dTotalBasicFair)  + "</textarea></td>"
					+ " <td><textarea id='miscCharges_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"   
					+ twoDigit.format(invoiceModel.getMiscAllowTotal()) + "</textarea></td>"
					+ " <td><textarea id='parkingAmount_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"        
					+ twoDigit.format(invoiceModel.getParking())+ "</textarea></td>"
					+ " <td><textarea id='tollTax_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"          
					+ twoDigit.format(invoiceModel.getToll())+ "</textarea></td>"  
					+ " <td><textarea id='stateTax_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"          
					+ twoDigit.format(invoiceModel.getStateTax())+ "</textarea></td>"
					+ " <td><textarea id='serviceTax_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"           
					+ twoDigit.format(dTotalTax) + "</textarea></td>"  
					+ " <td><textarea class='invoiceAmt' id='totalamount_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:60px; ' >"            
					+ twoDigit.format(invoiceModel.getGrandTotal())+ "</textarea></td>"  
				 	+ " <td><textarea id='closedBy_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:75px; ' >"            
					+ invoiceModel.getClosedBy() + "</textarea></td>"  
				 	+ " <td><textarea id='paymentMode_"+ lId+ "' cols='26' rows='3' readonly='' style='border: medium none; resize: none; background: transparent none repeat scroll 0% 0%;  font-size: 11px; width:65px; ' >"              
					+ invoiceModel.getPaymentMode()+ "</textarea></td>"; 
			}
		dataString = dataString + "</tbody></table>";
		return dataString;
	}

	/*
	 * -------------------------------------------------------------------------
	 * Get DS Details data For Single Invoicing Case
	 * -------------------------------------------------------------------------
	 */
	
	@RequestMapping(value = "/getInvoice", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getInvoice(
			  @ModelAttribute(value = "dutySlipModel") DutySlipModel dutySlipModel,
			  BindingResult bindingResult, 
			  @RequestParam(value = "invoiceNo") String invoiceNo,
			  @RequestParam(value = "corpId") String corpId,
			  HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		
		/*check if invoice is present in invoice table*/
		List <InvoiceModel> invoiceModelList = billingService.getInvoice(invoiceNo,Long.parseLong(corpId));
		if(invoiceModelList.size() > 0){
			String invoiceTemplate = new Utilities().getInvoice(invoiceModelList.get(0), servletContext.getRealPath("/WEB-INF/"));
			res.setDataGrid(invoiceTemplate);
		}else{
			long id=0;
			long tId=0;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			List<DutySlipModel> dutySlipModellist = billingService.getds(invoiceNo,Long.parseLong(corpId));
			if(dutySlipModellist.size() > 0){
				dutySlipModel = dutySlipModellist.get(0);
			}
			AddressDetailModel addressDetailModels = null;
			RelatedInfoModel relatedinfoModels = null;
			GeneralMasterModel generalMasterModels=null;
			List<AddressDetailModel> addressDetailModelList = bookingMasterService.getAddress(dutySlipModel.getBookingDetailModel().getId());
			List<RelatedInfoModel> relatedInfoModelList = bookingMasterService.getEmail(dutySlipModel.getBookingDetailModel().getId());
		
			if (relatedInfoModelList != null && relatedInfoModelList.size() > 0)
				relatedinfoModels = relatedInfoModelList.get(0);
	
			if (addressDetailModelList != null && addressDetailModelList.size() > 0)
				addressDetailModels = addressDetailModelList.get(0);
			
			try {
				BookingDetailModel bookingDetailModel = bookingMasterService.formFillForDetailEdit(dutySlipModel.getBookingDetailModel().getId());
				
				if(bookingDetailModel.getCarModel()!=null)
					id=bookingDetailModel.getCarModel().getId();
				List<GeneralMasterModel> generalMasterList = billingService.getCarMasterId(id);
				if (generalMasterList != null && generalMasterList.size() > 0)
					generalMasterModels= generalMasterList.get(0);
				bookingDetailModel.setAddress(addressDetailModels);
				bookingDetailModel.setRelatedInfo(relatedinfoModels);
				bookingDetailModel.setGeneralMaster(generalMasterModels);
				tId=bookingDetailModel.getGeneralMaster().getId();
				List<CorporateTariffDetModel> corporateTariffDetList = billingService.getTariffValue(tId,bookingDetailModel.getBookingMasterModel().getCorporateId().getId(),dateFormat2.format(dutySlipModel.getDutySlipClosedByDate()),bookingDetailModel.getBranch().getId());
				bookingDetailModel.setCorporateTariffDetModels( corporateTariffDetList);
				List<CorporateTaxDetModel> corporateTaxDetModels = billingService.getTaxs(bookingDetailModel.getBookingMasterModel().getCorporateId().getId(),dutySlipModellist.get(0).getInvoiceDate().toString());
				
				HashMap<String, Object> utilResponse = new Utilities().generateInvoice(bookingDetailModel); 
				InvoiceModel invoiceModel = (InvoiceModel) utilResponse.get("InvoiceModel");

				String invoiceTemplate = new Utilities().getInvoice(invoiceModel, servletContext.getRealPath("/WEB-INF/"));

				billingService.saveInvoice(invoiceModel);
				
				CorporateModel  corporateModel=corporateService.formFillForEdit(bookingDetailModel.getBookingMasterModel().getCorporateId().getId());
				res.setCorporateModel(corporateModel);
				res.setDataGrid(invoiceTemplate);
				res.setDutySlipModel(dutySlipModel);
				res.setCorporateTaxDetModelList(corporateTaxDetModels);
			} catch (DataIntegrityViolationException e) {
				logger.error("",e);
				res.setStatus("Failure");
				res.setResult(Message.FOREIGNKEY_ERROR_MESSAGE);
			} catch (Exception e) {
				res.setStatus("Failure");
				logger.error("",e);
				res.setResult(Message.FATAL_ERROR_MESSAGE);
			}
		}
		return res;
	}

	/*
	 * -------------------------------------------------------------------------
	 * Get DS Details data For Multiple Invoicing Case
	 * -------------------------------------------------------------------------
	 */
	
	@RequestMapping(value = "/getMultipleInvoice", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getMultipleInvoice(
			@ModelAttribute(value = "dutySlipModel") DutySlipModel dutySlipModel,
			BindingResult bindingResult, 
			@RequestParam(value = "invoiceNo") String invoiceNo,
			@RequestParam(value = "corpId") String corpId,
			HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		/*check if invoice is present in invoice table*/
		List <InvoiceModel> invoiceModelList = billingService.getInvoice(invoiceNo,Long.parseLong(corpId));
		if(invoiceModelList.size() > 0){
			String invoiceTemplate = new Utilities().getInvoiceMultiple(invoiceModelList.get(0), servletContext.getRealPath("/WEB-INF/"));
			res.setDataGrid(invoiceTemplate);
		}else{
			long id=0;
			long tId=0;
		    List<DutySlipModel> dutySlipModellist = billingService.getds(invoiceNo,Long.parseLong(corpId));
			AddressDetailModel addressDetailModels = null;
			RelatedInfoModel relatedinfoModels = null;
			GeneralMasterModel generalMasterModels=null;
			List<AddressDetailModel> addressDetailModelList = bookingMasterService.getAddress(dutySlipModellist.get(0).getBookingDetailModel().getId());
			List<RelatedInfoModel> relatedInfoModelList = bookingMasterService.getEmail(dutySlipModellist.get(0).getBookingDetailModel().getId());
			if (relatedInfoModelList != null && relatedInfoModelList.size() > 0)
				relatedinfoModels = relatedInfoModelList.get(0);
	
			if (addressDetailModelList != null && addressDetailModelList.size() > 0)
				addressDetailModels = addressDetailModelList.get(0);
			
			try {
				BookingDetailModel bookingDetailModel = bookingMasterService.formFillForDetailEdit(dutySlipModellist.get(0).getBookingDetailModel().getId());
				if(bookingDetailModel.getCarModel()!=null)
					id=bookingDetailModel.getCarModel().getId();
				List<GeneralMasterModel> generalMasterList = billingService.getCarMasterId(id);
				if (generalMasterList != null && generalMasterList.size() > 0)
					generalMasterModels= generalMasterList.get(0);
				bookingDetailModel.setAddress(addressDetailModels);
				bookingDetailModel.setRelatedInfo(relatedinfoModels);
				bookingDetailModel.setGeneralMaster(generalMasterModels);
				tId=bookingDetailModel.getGeneralMaster().getId();
				List<CorporateTaxDetModel> corporateTaxDetModels = billingService.getTaxs(bookingDetailModel.getBookingMasterModel().getCorporateId().getId(), dutySlipModellist.get(0).getInvoiceDate().toString());
				List<CorporateTariffDetModel> corporateTariffDetList = billingService.getTariffValue(tId,bookingDetailModel.getBookingMasterModel().getCorporateId().getId(),dateFormat2.format(dutySlipModellist.get(0).getDutySlipClosedByDate()),bookingDetailModel.getBranch().getId());
				bookingDetailModel.setCorporateTariffDetModels( corporateTariffDetList);
				bookingDetailModel.setDutyslip(dutySlipModellist);
				
				HashMap<String, Object> utilResponse = new Utilities().generateMultipleInvoice(bookingDetailModel,dutySlipModellist); 
				InvoiceModel invoiceModel = (InvoiceModel) utilResponse.get("InvoiceModel");

				String multipleInvoiceTemplate = new Utilities().getInvoiceMultiple(invoiceModel, servletContext.getRealPath("/WEB-INF/"));
				billingService.saveInvoice(invoiceModel);
	
				logger.debug(multipleInvoiceTemplate);
				CorporateModel  corporateModel=corporateService.formFillForEdit(bookingDetailModel.getBookingMasterModel().getCorporateId().getId());
				res.setCorporateModel(corporateModel);
				res.setDataGrid(multipleInvoiceTemplate);
				res.setCorporateTaxDetModelList(corporateTaxDetModels);
				res.setDutySlipModelList(dutySlipModellist);
				res.setDutySlipModel(dutySlipModellist.get(0));
			} catch (DataIntegrityViolationException e) {
				logger.error("",e);
				res.setStatus("Failure");
				res.setResult(Message.FOREIGNKEY_ERROR_MESSAGE);
			} catch (Exception e) {
				logger.error("",e);
				res.setStatus("Failure");
				res.setResult(Message.FATAL_ERROR_MESSAGE);
			}
		}
		return res;
	}
	@RequestMapping(value = "/getCheckInvoice", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getCheckInvoice(
			@ModelAttribute(value = "dutySlipModel") DutySlipModel dutySlipModel,
			BindingResult bindingResult, 
			@RequestParam(value = "invoiceNo") String invoiceNo,
			@RequestParam(value = "corpId") String corpId,
			HttpSession session, HttpServletRequest request) {
			JsonResponse res = new JsonResponse();
		List <DutySlipModel> dutySlipModels = billingService.getds(invoiceNo, Long.parseLong(corpId));
		res.setDutySlipModelList(dutySlipModels);
		return res;
	}
	/*
	 * Sahil
	 */
	@RequestMapping("/pageLoadDutySlipClosing/{jobType}")
	public ModelAndView pageLoadDutySlipClosing(Map<String, Object> map, HttpSession session, @PathVariable String jobType) {
		ModelAndView modelAndView = new ModelAndView("dutySlipClosing");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try {
			session.setAttribute("bookingDetails_sessionData", null);
			map.put("customerOrCompanyNameIdList", masterDataService.getGeneralMasterData_UsingCode("CORP:"+branchesAssigned)); // Type
			map.put("carModelIdList", masterDataService.getGeneralMasterData_UsingCode("VMD"));
			map.put("rentalTypeIdList", masterDataService.getGeneralMasterData_UsingCode("VRT"));
			map.put("bookedByList", billingService.getBookedBy());
			map.put("chauffeurList", masterDataService.getGeneralMasterData_UsingCode("CHAUFFEUR")); 
			map.put("carDetailsList", billingService.getAlocationCarRegistrationNo());
			map.put("bookedForList", billingService.getBookedFor());
			map.put("carOwnerTypeList", masterDataService.getGeneralMasterData_UsingCode("VOT"));
			map.put("MOBList", masterDataService.getGeneralMasterData_UsingCode("MOP")); 
			map.put("jobType", jobType); 
		} catch (Exception e) {
			logger.error("",e);
		} finally {
			map.put("bookingMasterModel", new BookingMasterModel());
		}
		return modelAndView;
	}
	@RequestMapping(value="/tariffNameCorporateWise",method=RequestMethod.POST)
	public @ResponseBody JsonResponse tariffNameCorporateWise(Map<String, Object> map, HttpSession session,
			@RequestParam("corporateId") String corporateId,
			@RequestParam("invoiceNo") String sInvoiceNo){
			JsonResponse res = new JsonResponse();
			String branchesAssigned = (String) session.getAttribute("branchesAssigned");
 		try{
 			CorporateModel corporateModel = corporateService.formFillForEdit(Long.parseLong(corporateId));
			res.setCorporateModel(corporateModel);
			
			List<GeneralMasterModel> branchList = masterDataService.getGeneralMasterData_UsingCode("LinkedBranchCorp:" + corporateId + ":" + branchesAssigned);
			res.setBranchList(branchList);
			if(!sInvoiceNo.isEmpty()){
				List<GeneralMasterModel> coverLetterList = masterDataService.getGeneralMasterData_UsingCode("CoverLetterList:" + corporateId + ":" + sInvoiceNo);
				res.setCoverLetterList(coverLetterList);
			}
			
			List<GeneralMasterModel> terminalList = masterDataService.getGeneralMasterData_UsingCode("LinkedBranchTerminal:" + corporateId);
			res.setTerminalList(terminalList);

			res.setGeneralMasterModelList(masterDataService.getGeneralMasterData_UsingCode("LinkedAuthoriser:"+ corporateId));
			res.setAutorizedUserModelList(masterDataService.getGeneralMasterData_UsingCode("LinkedClient:"+ corporateId));
			
			res.setCarDetailModelList(billingService.getAlocationCarRegistrationNo());
			res.setCarModelList(masterDataService.getGeneralMasterData_UsingCode("carModelCorp:"+ corporateId)); 
			
			res.setAddressDetail(masterDataService.getAddressDataFromID(Long.parseLong(corporateId), "Corporate" ));
			
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			
		
			
		}
		return res;
	}
	
	@RequestMapping(value="/dutySlipClosingSearch",method=RequestMethod.POST)
	public @ResponseBody JsonResponse dutySlipClosingSearch( HttpSession session,
			@RequestParam("dSRetreive") String dSRetreive)
			{
			JsonResponse res = new JsonResponse();
 		try{
 	  List<DutySlipModel> dutySlipModellist = billingService.getDutySlipClosingDetail(dSRetreive);
 	  List<BookingMasterModel> bookingMasterModelList =billingService.getDutySlipClosingBookingMasterDetail(dSRetreive);
		DutySlipModel dutySlipModels = null;
		BookingMasterModel bookingMasterModels = null;
		if (dutySlipModellist != null && dutySlipModellist.size() > 0)
			dutySlipModels = dutySlipModellist.get(0);
		if (bookingMasterModelList != null && bookingMasterModelList.size() > 0)
			bookingMasterModels = bookingMasterModelList.get(0);
		Long carDetailId=null;
		Long booDetailId = dutySlipModels.getBookingDetailModel().getId();
		Long corporateId  = dutySlipModels.getBookingDetailModel().getBookingMasterModel().getCorporateId().getId();
		Long minMintueSlab = dutySlipModels.getBookingDetailModel().getBookingMasterModel().getCorporateId().getMinRoundSlab();
		if(dutySlipModels.getCarDetailModel()!=null)
			carDetailId= dutySlipModels.getCarDetailModel().getId();
		
		CarAllocationModel carAllocationModel = billingService.getCarOwnerType(carDetailId);
		CarAllocationModel carAllocationModels=billingService.getSearchCarReg(carDetailId);
		BookingDetailModel bookingDetailModel = bookingMasterService.formFillForDetailEdit(booDetailId);
		Long  carAllocationId=carAllocationModels ==null? null :carAllocationModels.getId();
		String regNo = dutySlipModels.getCarDetailModel().getRegistrationNo();
		if(bookingDetailModel.getCarModel()!=null){
			List<GeneralMasterModel> generalMasterList = billingService.getCarMasterId(Long.valueOf(bookingDetailModel.getCarModel().getId()));
			if (generalMasterList != null && generalMasterList.size() > 0){
				GeneralMasterModel generalMasterModels= generalMasterList.get(0);
				bookingDetailModel.setGeneralMaster(generalMasterModels);
				String sDate = dateFormat2.format(dutySlipModels.getDispatchDateTime());
				List<CorporateTariffDetModel> corporateTariffDetList = billingService.getTariffValue(Long.valueOf(bookingDetailModel.getGeneralMaster().getId()),corporateId,sDate,bookingDetailModel.getBranch().getId());
				res.setCorporateTariffDetModelList(corporateTariffDetList);
			}
		}
		res.setDutySlipModel(dutySlipModels);
		res.setBookingMasterModel(bookingMasterModels);
		res.setCarAllocationModel(carAllocationModel);
		res.setCarAllocationId(carAllocationId);
		res.setDataString1(regNo);
		res.setDsIdCount(minMintueSlab);
		res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			
		
			
		}
		return res;
	}
	
	@RequestMapping(value="/saveDs",method=RequestMethod.POST)
	 public @ResponseBody JsonResponse saveUpdateDs(
	   @ModelAttribute(value="bookingMasterModel")BookingMasterModel bookingMasterModel,
	   BindingResult bindingResult, HttpSession session){
	   JsonResponse res = new JsonResponse();
	   try {
		for(BookingDetailModel bookingDetailModel : bookingMasterModel.getBookingDetailModel()){
			bookingDetailModel.setBookingMasterModel(bookingMasterModel);
			bookingDetailModel.setStatus("D");
		}
		bookingMasterModel.setBookingDate(new Date());
		bookingMasterModel = bookingMasterService.save(bookingMasterModel);
	    bookingmasterid=bookingMasterModel.getId();
		BookingDetailModel bookingDetailModel1=billingService.getBookingDetailId(bookingmasterid);
		long bookingDetailId=bookingDetailModel1.getId();
		sBookingNo=bookingDetailModel1.getBookingMasterModel().getBookingNo();
		res.setResult("Duty Slip  are Successfully Saved !!"); 
	    res.setBookingDetailId(bookingDetailId);
	    res.setBookingMasterModel(bookingMasterModel);
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
	
	@RequestMapping(value="/saveDutySlip",method=RequestMethod.POST)
	 public @ResponseBody JsonResponse saveDutySlip(
	   @ModelAttribute(value="dutySlipModel")DutySlipModel dutySlipModel,
	   BindingResult bindingResult, HttpSession session){
	   JsonResponse res = new JsonResponse();
	   try {
			BookingDetailModel bookingDetailModel = billingService.getBookingDetailModel(dutySlipModel.getBookingDetailModel().getId());
			dutySlipModel.setBookingDetailModel(bookingDetailModel);
		   if(session.getAttribute("loginUserId")!=null){
		        Long loginUserId=(Long) session.getAttribute("loginUserId");
		        dutySlipModel.setDispatchedBy(dutySlipModel.getDispatchedBy());
		        dutySlipModel.setDutySlipClosedBy(loginUserId);
		        dutySlipModel.setDutySlipReceivedBy(loginUserId);
		        dutySlipModel.setDutySlipCreatedBy(loginUserId);
	       }
		   dutySlipModel.setDutySlipCreatedByDate(dutySlipModel.getDutySlipDate());
		   dutySlipModel.setDutySlipClosedByDate(dutySlipModel.getDutySlipDate());
		   dutySlipModel.setDutySlipReceivedByDate(dutySlipModel.getDutySlipDate());
		   dutySlipModel.setDutySlipStatus("C");
		   
		 billingService.save(dutySlipModel,sBookingNo);
		 String dutySlipNo=dutySlipModel.getDutySlipNo();
		 res.setDutySlipModel(dutySlipModel);
		 res.setDataGrid(dutySlipNo);
		 res.setResult(Message.SAVE_SUCCESS_MESSAGE); 
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
	
	@RequestMapping(value="/getCarOwner",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getCarOwner( @RequestParam("id") String id,	  
			 HttpSession session)
			{
			JsonResponse res = new JsonResponse();
 		try{
 	      CarAllocationModel carAllocationModel=billingService.getCarOwner(id);
 		  res.setStatus("Success"); 
 	      res.setCarAllocationModel(carAllocationModel);
 	     }
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			
		
			
		}
		return res;
	}
	
	@RequestMapping(value = "/getExtraValue", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getExtraValue(@RequestParam("tariffid") long tariffid,
			   @RequestParam("corporateIds") long corporateIds,
			   @RequestParam("branchId") long branchId,
			   HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		try {
			List<GeneralMasterModel> generalMasterList = billingService.getCarMasterId(Long.valueOf(tariffid));
			if (generalMasterList != null && generalMasterList.size() > 0){
				GeneralMasterModel generalMasterModels= generalMasterList.get(0);
				String sDate = dateFormat2.format(new Date());
				List<CorporateTariffDetModel> corporateTariffDetList = billingService.getTariffValue(Long.valueOf(generalMasterModels.getId()),corporateIds,sDate,branchId);
				res.setCorporateTariffDetModelList(corporateTariffDetList);
				
			}
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	/* ___________________________________________________________________________________________			
	 *     Update Duty Slip from DSClosing Screen Functionality Start
	 * ___________________________________________________________________________________________
	 */
	@RequestMapping(value="/saveUpdateDsDutySlipClosing",method=RequestMethod.POST)
	 public @ResponseBody JsonResponse saveUpdateDsDutySlipClosing(
	   @ModelAttribute(value="dutySlipModel") DutySlipModel dutySlipModel,
	   BindingResult bindingResult,
	   @RequestParam("id") Long id, HttpSession session){
	   JsonResponse res = new JsonResponse();
	   try {
	      DutySlipModel oldDutySlipModel = dutySlipService.formFillForEdit(id);
/*	      Save History DutySlip*/
	      HistoryDutySlipModel hds = new HistoryDutySlipModel();
	      hds.setStateTax(oldDutySlipModel.getStateTax());
	      hds.setCarDetailModel(oldDutySlipModel.getCarDetailModel());
	      hds.setBookingDetailModel(oldDutySlipModel.getBookingDetailModel());
	      hds.setTollTaxAmount(oldDutySlipModel.getTollTaxAmount());
	      hds.setParkingAmount(oldDutySlipModel.getParkingAmount());
	      hds.setCloseKms(oldDutySlipModel.getCloseKms());
	      hds.setOpenKms(oldDutySlipModel.getOpenKms());
	      hds.setDateTo(oldDutySlipModel.getDateTo());
	      hds.setDateFrom(oldDutySlipModel.getDateFrom());
	      hds.setExtraChgHrs(oldDutySlipModel.getExtraChgHrs());
	      hds.setExtraChgKms(oldDutySlipModel.getExtraChgKms());
	      hds.setManualSlipNo(oldDutySlipModel.getManualSlipNo());
	      hds.setMinChgHrs(oldDutySlipModel.getMinChgHrs());
	      hds.setMinChgKms(oldDutySlipModel.getMinChgKms());
	      hds.setNightAllow(oldDutySlipModel.getNightAllow());
	      hds.setDayAllow(oldDutySlipModel.getDayAllow());
	      hds.setRemarks(oldDutySlipModel.getRemarks());
	      hds.setTotalKms(oldDutySlipModel.getTotalKms());
	      hds.setTotalHrs(oldDutySlipModel.getTotalHrs());
	      hds.setTotalFare(oldDutySlipModel.getTotalFare());
	      hds.setTimeTo(oldDutySlipModel.getTimeTo());
	      hds.setTimeFrom(oldDutySlipModel.getTimeFrom());
	      hds.setMiscCharge(oldDutySlipModel.getMiscCharge());
	      hds.setChauffeurName(oldDutySlipModel.getChauffeurName());
	      hds.setDutySlipNo(oldDutySlipModel.getDutySlipNo());
	      hds.setBillingStatus(oldDutySlipModel.getBillingStatus());
	      hds.setDutySlipStatus(oldDutySlipModel.getDutySlipStatus());
	      hds.setReserveDateTime(oldDutySlipModel.getReserveDateTime());
	      hds.setNightAllowanceRate(oldDutySlipModel.getNightAllowanceRate());
	      hds.setOutStationAllowRate(oldDutySlipModel.getOutStationAllowRate());
	      hds.setTaxName(oldDutySlipModel.getTaxName());
	      hds.setTaxPercentage(oldDutySlipModel.getTaxPercentage());
	      hds.setTaxValues(oldDutySlipModel.getTaxValues());
	      hds.setDutySlipClosedBy(oldDutySlipModel.getDutySlipClosedBy());
	      hds.setDutySlipClosedByDate(oldDutySlipModel.getDutySlipClosedByDate());
	      hds.setDutySlipCreatedBy(oldDutySlipModel.getDutySlipCreatedBy());
	      hds.setDutySlipCreatedByDate(oldDutySlipModel.getDutySlipCreatedByDate());
	      hds.setDutySlipReceivedBy(oldDutySlipModel.getDutySlipReceivedBy());
	      hds.setDutySlipReceivedByDate(oldDutySlipModel.getDutySlipReceivedByDate());
	      hds.setInvoiceGenerateBy(oldDutySlipModel.getInvoiceGenerateBy());
	      hds.setInvoiceGenerateDate(oldDutySlipModel.getInvoiceGenerateDate());
	      hds.setDutySlipDate(oldDutySlipModel.getDutySlipDate());
	      hds.setIsUpdatedChauffeur(oldDutySlipModel.getIsUpdatedChauffeur());
	      hds.setChauffeurMobile(oldDutySlipModel.getChauffeurMobile());
	      hds.setChauffeurModel(oldDutySlipModel.getChauffeurModel());
	      hds.setBasicFare(oldDutySlipModel.getBasicFare());
	      hds.setInvoiceNo(oldDutySlipModel.getInvoiceNo());
	      hds.setAllocationDateTime(oldDutySlipModel.getAllocationDateTime());
	      hds.setDispatchDateTime(oldDutySlipModel.getDispatchDateTime());
	      hds.setDispatchedBy(oldDutySlipModel.getDispatchedBy());
	      hds.setDsUpdateBy(oldDutySlipModel.getDsUpdateBy());
	      hds.setDsUpdateDateTime(oldDutySlipModel.getDsUpdateDateTime());
	      hds.setFuelCharge(oldDutySlipModel.getFuelCharge());
	      hds.setGuideCharge(oldDutySlipModel.getGuideCharge());
	      hds.setModeOfPayment(oldDutySlipModel.getModeOfPayment());
	      hds.setStateTax(oldDutySlipModel.getStateTax());
	      hds.setTariff(oldDutySlipModel.getTariff());
	      hds.setTotalDay(oldDutySlipModel.getTotalDay());
	      hds.setTotalNight(oldDutySlipModel.getTotalNight());
	      hds.setVendorModel(oldDutySlipModel.getVendorModel());
	      hds.setDayAllow(oldDutySlipModel.getDayAllow());
	      hds.setdSId(oldDutySlipModel.getId());
	      
	      if(session.getAttribute("loginUserId")!=null){
	        Long loginUserId=(Long) session.getAttribute("loginUserId");
	        hds.setDsUpdateBy(loginUserId);
	       }
	      hds.setDsUpdateDateTime(new Date());
	      
	    /*  Update Duty Slip*/
	      BookingMasterModel bookingMasterModel = bookingMasterService.formFillForEdit(oldDutySlipModel.getBookingDetailModel().getBookingMasterModel().getId());
	      bookingMasterModel.setCorporateId(dutySlipModel.getBookingDetailModel().getBookingMasterModel().getCorporateId());
	      bookingMasterModel.setBookedBy(dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedBy());
	      bookingMasterModel.setBookedFor(dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedFor());
	      bookingMasterModel.setBookedForName(dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedForName());
	      bookingMasterService.update(bookingMasterModel);
	      
	      oldDutySlipModel.setTariff(dutySlipModel.getTariff());
	      oldDutySlipModel.getBookingDetailModel().setTariff(dutySlipModel.getTariff());
	      oldDutySlipModel.getBookingDetailModel().setRentalType(dutySlipModel.getBookingDetailModel().getRentalType());
	      oldDutySlipModel.getBookingDetailModel().setBranch(dutySlipModel.getBookingDetailModel().getBranch());
	      oldDutySlipModel.getBookingDetailModel().setOutlet(dutySlipModel.getBookingDetailModel().getOutlet());
	      oldDutySlipModel.getBookingDetailModel().setMob(dutySlipModel.getBookingDetailModel().getMob());
	      oldDutySlipModel.getBookingDetailModel().setCarModel(dutySlipModel.getBookingDetailModel().getCarModel());
	      
	      oldDutySlipModel.setStateTax(dutySlipModel.getStateTax());
	      oldDutySlipModel.setTollTaxAmount(dutySlipModel.getTollTaxAmount());
	      oldDutySlipModel.setParkingAmount(dutySlipModel.getParkingAmount());
	      oldDutySlipModel.setCloseKms(dutySlipModel.getCloseKms());
	      oldDutySlipModel.setOpenKms(dutySlipModel.getOpenKms());
	      oldDutySlipModel.setDateTo(dutySlipModel.getDateTo());
	      oldDutySlipModel.setDateFrom(dutySlipModel.getDateFrom());
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
	      oldDutySlipModel.setTimeFrom(dutySlipModel.getTimeFrom());
	      oldDutySlipModel.setMiscCharge(dutySlipModel.getMiscCharge());
	      oldDutySlipModel.setCarDetailModel(dutySlipModel.getCarDetailModel());
	      oldDutySlipModel.setChauffeurName(dutySlipModel.getChauffeurName());
	      oldDutySlipModel.setTotalDay(dutySlipModel.getTotalDay());
	      oldDutySlipModel.setTotalNight(dutySlipModel.getTotalNight());
	      oldDutySlipModel.setBillingStatus(dutySlipModel.getBillingStatus());
	      oldDutySlipModel.setFuelCharge(dutySlipModel.getFuelCharge());
	      oldDutySlipModel.setGuideCharge(dutySlipModel.getGuideCharge());
	      oldDutySlipModel.setDispatchDateTime(dutySlipModel.getDispatchDateTime());
	      oldDutySlipModel.setNightAllowanceRate(dutySlipModel.getNightAllowanceRate());
	      oldDutySlipModel.setOutStationAllowRate(dutySlipModel.getOutStationAllowRate());
	      oldDutySlipModel.setBasicFare(dutySlipModel.getBasicFare());
	      oldDutySlipModel.setlCoverLetterId(dutySlipModel.getlCoverLetterId());
	      oldDutySlipModel.setlNewCoverLetterId(dutySlipModel.getlNewCoverLetterId());
	      
	      if(oldDutySlipModel.getDutySlipStatus().equalsIgnoreCase("I")){
	    	  String taxN[] = dutySlipModel.getTaxName().split(",");
	    	  String taxP[] = dutySlipModel.getTaxPercentage().split(",");
	    	  String taxV[] = dutySlipModel.getTaxValues().split(",");
	    	  String sTaxValues ="", sTaxPercentage = "", sTaxName = "";
				for(int i =0; i< taxN.length;i++){
					if(i == taxV.length -1){
						sTaxValues = sTaxValues + taxV[i];
						sTaxPercentage = sTaxPercentage + taxP[i];
						sTaxName = sTaxName + taxN[i];
					}else{
						sTaxValues = sTaxValues + taxV[i] + ",";
						sTaxPercentage = sTaxPercentage + taxP[i] + ",";
						sTaxName = sTaxName + taxN[i] + ",";
					}
				}
	    	  oldDutySlipModel.setTaxName(sTaxName);
		      oldDutySlipModel.setTaxPercentage(sTaxPercentage);
		      oldDutySlipModel.setTaxValues(sTaxValues);
	      }
	      oldDutySlipModel.setId(id);
	      
	      Long loginUserId =0l;
	      if(session.getAttribute("loginUserId")!=null){
	    	  loginUserId=(Long) session.getAttribute("loginUserId");
	    	  oldDutySlipModel.setDsUpdateBy(loginUserId);
	      }

	      oldDutySlipModel.setDsUpdateDateTime(new Date());
	      billingService.save(hds);
	      dutySlipService.update(oldDutySlipModel);
	      
	      //Updated invoice insert into invoice Table
	      if(oldDutySlipModel.getInvoiceNo() != null){
	    	  if(!oldDutySlipModel.getInvoiceNo().equals("")){ 
	    		  long lCoptId = dutySlipModel.getBookingDetailModel().getBookingMasterModel().getCorporateId().getId().longValue();
	    		  saveInvoice(oldDutySlipModel.getInvoiceNo(),loginUserId,lCoptId,dutySlipModel.getlCoverLetterId(),dutySlipModel.getlNewCoverLetterId() );
	    	  }
	      }

	      res.setResult("Duty Slip  are Successfully Updated !!"); 
	      res.setStatus("Success"); 
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
	
	@RequestMapping(value = "/getCarNo", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getCarNo(@RequestParam("carModelId") long carModelId, 
			   HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		try {
			List<CarAllocationModel> carAllocationModels = billingService.getAlocationCarNo(carModelId);
			
			res.setStatus("Success");
			res.setCarAllocationModels(carAllocationModels);
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	@RequestMapping(value="/checkDSNo",method=RequestMethod.POST)
	public @ResponseBody JsonResponse checkDSNo( HttpSession session,
			@RequestParam("dSRetreive") String dSRetreive)
			{
			JsonResponse res = new JsonResponse();
 		try{
	 	    List<DutySlipModel> dutySlipModellist = billingService.getDutySlipClosingDetail(dSRetreive);
			if (dutySlipModellist != null && dutySlipModellist.size() > 0){
				res.setDsIdCount(dutySlipModellist.size());
				res.setStatus("Success");
			}else{
				res.setDsIdCount(0);
			}
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			
		}
		return res;
	}
	
	@RequestMapping(value = "/getAllTaxes", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getAllTaxes(
			  @ModelAttribute(value = "dutySlipModel") DutySlipModel dutySlipModel,
			  BindingResult bindingResult, @RequestParam("corporateId") Long corporateId,
			  @RequestParam("insDate") String dsDate,
			  @RequestParam(value="branchId", required=false ) String branchId,
			  HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			String sGSTIN = null;
			List<RelatedInfoModel> relatedInfoList =  relatedInfoService.list("branch");			
			for(RelatedInfoModel branchInfo : relatedInfoList){
				if(branchInfo.getGeneralMasterModel().getId() == Long.parseLong(branchId)){
					sGSTIN = branchInfo.getGstin();
				}
			}
			
			String invoiceDate = dateFormat.format(df.parse(dsDate));
			List<CorporateTaxDetModel> corporateTaxDetResp = new ArrayList<CorporateTaxDetModel>();
			List<CorporateTaxDetModel> corporateTaxDetModels = billingService.getAllTaxes(corporateId,invoiceDate);
			for(CorporateTaxDetModel corporateTaxDetModel : corporateTaxDetModels){
				String sTaxName = corporateTaxDetModel.getName();
				if(sGSTIN != null){
					if(sGSTIN.length() < 10 && (sTaxName.equals("CGST") || sTaxName.equals("SGST"))) continue;
					if(sGSTIN.length() > 10 && sTaxName.equals("IGST"))continue; 
				}else if((sTaxName.equals("CGST") || sTaxName.equals("SGST"))){
					continue;
				}
				corporateTaxDetResp.add(corporateTaxDetModel);
			}
			res.setCorporateTaxDetModelList(corporateTaxDetResp);
			res.setStatus("Success");
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("",e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	@RequestMapping(value = "/saveCoverLetter", method = RequestMethod.POST)
	public @ResponseBody JsonResponse saveCoverLetter(
			  @ModelAttribute(value = "coverLetterModel") CoverLetterModel coverLetterModel,
			  BindingResult bindingResult, HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		try {
			List<InvoiceModel> invoiceModelList = billingService.getInvoice(coverLetterModel.getInvoiceIds(),0);
			if(invoiceModelList.size() > 0){
				Long loginUserId =0l;
				if(session.getAttribute("loginUserId")!=null){
					loginUserId=(Long) session.getAttribute("loginUserId");
				}
	
				coverLetterModel.setCreatedBy(loginUserId);
				coverLetterModel.setCompId(invoiceModelList.get(0).getCompId());
				coverLetterModel.setBranchId(invoiceModelList.get(0).getBranchId());
				coverLetterModel.setBranch(invoiceModelList.get(0).getBrName());
				coverLetterModel.setHubId(invoiceModelList.get(0).getHubId());
				coverLetterModel.setHub(invoiceModelList.get(0).getHubName());
				coverLetterModel.setCorporateId(invoiceModelList.get(0).getCorpId());
				coverLetterModel.setCorporate(invoiceModelList.get(0).getCorpName());
				coverLetterModel = billingService.saveCoverLetter(coverLetterModel);
	
				/*Generate Cover Letter for display*/
				String sCoverLetterHTML = new Utilities().generateCoveringLetter(invoiceModelList,coverLetterModel,servletContext.getRealPath("/WEB-INF/"));
				res.setSaveId(coverLetterModel.getId().toString());
				res.setDataGrid(sCoverLetterHTML);
				res.setStatus("Success");
			}else{
				res.setStatus("Failure");
				res.setResult("Invoices Not Found");
			}
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("",e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	@RequestMapping(value = "/getCoverLetter", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getCoverLetter(
			@RequestParam("coverLetterId") String coverLetterId,
			HttpSession session, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		try {
			String sCoverLetterHTML = "Data Not Found ....";
			CoverLetterModel coverLetterModel = billingService.getCoverLetter(Long.parseLong(coverLetterId));
			if(coverLetterModel != null ){
				List<InvoiceModel> invoiceModelList = billingService.getInvoice(coverLetterModel.getInvoiceIds(),0);
				if(invoiceModelList.size() > 0){
					/*Generate Cover Letter for display*/
					sCoverLetterHTML = new Utilities().generateCoveringLetter(invoiceModelList,coverLetterModel,servletContext.getRealPath("/WEB-INF/"));
				}
				res.setDataGrid(sCoverLetterHTML);
				res.setStatus("Success");
			}else{
				res.setStatus("Failure");
				res.setResult("Cover Letter Not Found");
			}
		} catch (Exception e) {
			res.setStatus("Failure");
			logger.error("",e);
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	public HashMap<String,Object> getInvoiceValues(List<DutySlipModel> dutySlipModels){
		HashMap<String,Object> mapReturn = new HashMap<String,Object>();
		
		String traiffName=null;
		double ssdiscount=0.00,outStationAllowence=0,nightAllowence=0,miscCharges=0,finalTotal = 0.0;
		double totalWithoutParking = 0,totalExtraKmUsed =0,extraHrsUsed = 0,totalExtraHrsUsed = 0;
		double dTotBasicFare=0.00, dTotMisc=0.00, dTotParking=0.00, dTotToll=0.00, dTotStateTax=0.00; 

		DecimalFormat sr = new DecimalFormat("0.00");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		String sNextInvoiceNo = "";
		double dParking = 0.00, dToll = 0.00, dStateTax = 0.00, dMisc = 0.00, dbasicFare = 0.00, dInvoiceTotal=0.00;
		
		List<UserModel> userModelList = userService.list();
		int iTotalSize = dutySlipModels.size(), iInstance = 0;
		for (DutySlipModel dutySlipModel : dutySlipModels) {
			String sClosedUser = ""; iInstance++;
			String invoiceNo = dutySlipModel.getInvoiceNo();
			
			for(UserModel userModel : userModelList){
				if(dutySlipModel.getDutySlipClosedBy() != null){
					if(dutySlipModel.getDutySlipClosedBy() == userModel.getId().longValue())
						sClosedUser = userModel.getUserName();
				}
			}

			long lId = dutySlipModel.getId();
			
			if(dutySlipModel.getTotalFare() == null) dutySlipModel.setTotalFare(0.00);
			if(dutySlipModel.getParkingAmount() == null) dutySlipModel.setParkingAmount(0.00);
			if(dutySlipModel.getStateTax() == null) dutySlipModel.setStateTax(0.00);
			if(dutySlipModel.getTollTaxAmount() == null) dutySlipModel.setTollTaxAmount(0.00);
			if(dutySlipModel.getMiscCharge() == null) dutySlipModel.setMiscCharge(0.00);
			if(dutySlipModel.getFuelCharge() == null) dutySlipModel.setFuelCharge(0.00);
			if(dutySlipModel.getGuideCharge() == null) dutySlipModel.setGuideCharge(0.00);
			if(dutySlipModel.getBasicFare() == null) dutySlipModel.setBasicFare(0.00);
			if(dutySlipModel.getOutStationAllowRate() == null) dutySlipModel.setOutStationAllowRate(0.00); 
			if(dutySlipModel.getDayAllow()== null) dutySlipModel.setDayAllow(0.00);
			if(dutySlipModel.getNightAllow()== null) dutySlipModel.setNightAllow(0.00);

			traiffName = dutySlipModel.getTariff() == null ?"":dutySlipModel.getTariff().getName();
			dbasicFare = dutySlipModel.getBasicFare();
			if(traiffName.equalsIgnoreCase("Outstation")){
				dbasicFare += dbasicFare * Double.valueOf(dutySlipModel.getTotalDay() == null ? 1 : dutySlipModel.getTotalDay());
			}
			String sInvoiceDate = dutySlipModel.getInvoiceDate() == null ? "" : dateFormat.format(dutySlipModel.getInvoiceDate());
			double extraKmUsed = 0.00;
			double dMinChg = dutySlipModel.getMinChgKms() == null || dutySlipModel.getMinChgKms().toString().equals("") || dutySlipModel.getMinChgKms().toString().length() == 0 ? 0:dutySlipModel.getMinChgKms();
			double dtotalKms = dutySlipModel.getTotalKms() == null || dutySlipModel.getTotalKms().toString().equals("") || dutySlipModel.getTotalKms().toString().length() == 0 ? 0:dutySlipModel.getTotalKms();
			if(dtotalKms > dMinChg){
				extraKmUsed = dtotalKms - dMinChg;
			}
			double getTotalHour=0;
			if(dutySlipModel.getTotalHrs() ==null || dutySlipModel.getTotalHrs().toString().length() == 0 || dutySlipModel.getTotalHrs().toString().equals("")) 
				dutySlipModel.setTotalHrs("0");
			
			String totalhrs = dutySlipModel.getTotalHrs();
			String[] ttlhrs = totalhrs.split(":");
			long lTlhrs = 0l;
			
			if(ttlhrs.length == 2) lTlhrs = Long.parseLong(ttlhrs[1]);
				getTotalHour=Double.parseDouble(ttlhrs[0]);
			
			String sMinHrsChg = dutySlipModel.getMinChgHrs() == null ? "0.00":String.valueOf(dutySlipModel.getMinChgHrs());
			if(getTotalHour >= Double.parseDouble(sMinHrsChg))
				extraHrsUsed = getTotalHour - Double.parseDouble(sMinHrsChg);	
			DecimalFormat hh = new DecimalFormat("#");
			String extraHrsUsedString= "0:00";
			
			if(getTotalHour >= Double.parseDouble(sMinHrsChg)){
				extraHrsUsed = getTotalHour - Double.parseDouble(sMinHrsChg);
				if(lTlhrs > 0) 
					extraHrsUsedString=  hh.format(extraHrsUsed) + ":" + ttlhrs[1];
				else
					extraHrsUsedString=  hh.format(extraHrsUsed) + ":" + "00";
			}
			else{
				extraHrsUsedString= "0:00";
			}
			
			if(dutySlipModel.getExtraChgKms() == null) dutySlipModel.setExtraChgKms(0.00);
			totalExtraKmUsed=extraKmUsed* dutySlipModel.getExtraChgKms();
			String [] extraHrsArr = extraHrsUsedString.split(":"); 
			int coficent =Integer.parseInt(extraHrsArr[1]) / 15;
			coficent = coficent * 25;
			String extraChgHr = extraHrsArr[0] + "." + coficent;
			totalExtraHrsUsed=Double.parseDouble(extraChgHr) * (dutySlipModel.getExtraChgHrs() == null ? 0:dutySlipModel.getExtraChgHrs());
			
			if(dutySlipModel.getParkingAmount() == null)  dutySlipModel.setParkingAmount(0.00);
			if(dutySlipModel.getTollTaxAmount() == null)   dutySlipModel.setTollTaxAmount(0.00);
			
			dParking  			= dutySlipModel.getParkingAmount();
			dToll     			= dutySlipModel.getTollTaxAmount();
			dStateTax 			= dutySlipModel.getStateTax();
			dMisc	  			= ((dutySlipModel.getMiscCharge())+(dutySlipModel.getFuelCharge())+(dutySlipModel.getGuideCharge()));
			outStationAllowence	= dutySlipModel.getDayAllow();
			nightAllowence		= dutySlipModel.getNightAllow();
			
			miscCharges=((dutySlipModel.getMiscCharge())+(dutySlipModel.getFuelCharge())+(dutySlipModel.getGuideCharge()));
			
				finalTotal=	dbasicFare +
						totalExtraHrsUsed +
						totalExtraKmUsed +
						outStationAllowence +
						nightAllowence +
						miscCharges +
						dParking +
						dStateTax +
						dToll;
				totalWithoutParking =	dbasicFare +
										totalExtraHrsUsed +
										totalExtraKmUsed +
										outStationAllowence +
										nightAllowence +
										miscCharges ;

			dTotBasicFare += dbasicFare; 
			dTotMisc      += dMisc; 
			dTotParking   += dParking; 
			dTotToll      += dToll; 
			dTotStateTax  += dStateTax; 
			dInvoiceTotal += finalTotal;
			
			if(iTotalSize > iInstance){
				sNextInvoiceNo = dutySlipModels.get(iInstance).getInvoiceNo();
			}
			
			if(!(sNextInvoiceNo.equalsIgnoreCase(invoiceNo) && iTotalSize > iInstance)){
				/*	Taxes*/ 
				String tax = "tax";
				String taxName = "taxName";
				String ssTax =null; 
				String ssTaxName = null;
				String ssTaxVal = null;
				double finalCalculatedTax = 0.00;
				if(dutySlipModel.getTaxName() != null && !dutySlipModel.getTaxName().isEmpty()){
					String[] sTaxName = (dutySlipModel.getTaxName()).split(",");
					String[] sTaxPer = (dutySlipModel.getTaxPercentage()).split(",");
					String[] sTaxVal = (dutySlipModel.getTaxValues()).split(",");
					
					for(int i = 0;  i<sTaxName.length; i++){
						 ssTax = tax.concat(String.valueOf(i));
						 ssTaxName = taxName.concat(String.valueOf(i));	
						 ssTax = sTaxPer[i];
						 ssTaxName = sTaxName[i];
						 ssTaxVal = sTaxVal[i];
						 logger.info("name : "+ssTaxName + "tax : "+ssTax+" amt :" +ssTaxVal);
						 
						/*Calculate Percent	*/ 
						double calculatedTax = 0.00;
//							calculatedTax = (Double.parseDouble(ssTax)/100)*finalTotalWithoutParking;
//							if(sRoundOff.equals("Y")) calculatedTax = Math.round(calculatedTax);
						if(dutySlipModel.getBookingDetailModel().getBookingMasterModel().getCorporateId().getsTaxCalPark().equals("Y")){
							calculatedTax = Math.round((Double.parseDouble(ssTax)/100) * dInvoiceTotal);
						}else{
							calculatedTax = Math.round((Double.parseDouble(ssTax)/100) * totalWithoutParking);
						}	
						if(ssTaxName.contains("Discount")){
							ssdiscount=Double.parseDouble(ssTaxVal);
							if(calculatedTax > 0 && Double.parseDouble(ssTaxVal) > 0){
								calculatedTax = calculatedTax > Double.parseDouble(ssTaxVal) ? Double.parseDouble(ssTaxVal): calculatedTax ;
							}else{
								calculatedTax = calculatedTax > 0 ? calculatedTax : Double.parseDouble(ssTaxVal);
							}	
							finalCalculatedTax = finalCalculatedTax - calculatedTax;
						}else{
							calculatedTax = calculatedTax > Double.parseDouble(ssTaxVal) ? calculatedTax : Double.parseDouble(ssTaxVal);
							finalCalculatedTax += calculatedTax;
						}
					}
				}	
				dInvoiceTotal += finalCalculatedTax;
				HashMap<String,String> mapReturnIn = new HashMap<String,String>();

				mapReturnIn.put("lId", String.valueOf(lId));
				mapReturnIn.put("invoiceNo", invoiceNo);
				mapReturnIn.put("invoiceDate", sInvoiceDate);
				mapReturnIn.put("branchName", dutySlipModel.getBookingDetailModel().getBranch().getName());
				mapReturnIn.put("outletName", dutySlipModel.getBookingDetailModel().getOutlet().getName());
				mapReturnIn.put("corporateName", dutySlipModel.getBookingDetailModel().getBookingMasterModel().getCorporateId().getName());
				mapReturnIn.put("corpId", String.valueOf(dutySlipModel.getBookingDetailModel().getBookingMasterModel().getCorporateId().getId()));
				mapReturnIn.put("bookedByName", dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedBy().getName());
				if(dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedForName() !=null){		
					mapReturnIn.put("bookedForName", dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedForName());
				}else{
					mapReturnIn.put("bookedForName", "");
				}
				if(dutySlipModel.getBookingDetailModel().getMob()!=null){
					mapReturnIn.put("mobName", dutySlipModel.getBookingDetailModel().getMob().getName());
				}else{
					mapReturnIn.put("mobName", "");
				}
				mapReturnIn.put("rentalTypeName",dutySlipModel.getBookingDetailModel().getRentalType().getName());
				mapReturnIn.put("sClosedUser", String.valueOf(sClosedUser));
				mapReturnIn.put("dutyslipNo",dutySlipModel.getDutySlipNo());
				mapReturnIn.put("dispatchDateTime",dateFormat.format(dutySlipModel.getDispatchDateTime()));
	
				mapReturnIn.put("dbasicFare", String.valueOf(dTotBasicFare));
				mapReturnIn.put("dMisc", String.valueOf(dTotMisc));
				mapReturnIn.put("dParking", String.valueOf(dTotParking));
				mapReturnIn.put("dToll", String.valueOf(dTotToll));
				mapReturnIn.put("dState", String.valueOf(dTotStateTax));
				mapReturnIn.put("sServiceTaxs", String.valueOf(finalCalculatedTax));
				mapReturnIn.put("ssdiscount", String.valueOf(ssdiscount));
				mapReturnIn.put("finalGrandTotal", String.valueOf(Math.round(dInvoiceTotal)));
				
				dTotBasicFare=0.00; dTotMisc=0.00; dTotParking=0.00; dTotToll=0.00; dTotStateTax=0.00;dInvoiceTotal = 0.00;

				mapReturn.put(invoiceNo, mapReturnIn);
			}	
		}
		return mapReturn;
	}
	public String saveInvoice(String invoiceNo, long lLoginUserId, long lCorpId, long lCoverLetterId, long lNewCoverLetterId){
		String sReturn = "Success";
		long id=0,tId=0;
		try {
			AddressDetailModel 	addressDetailModels = null;
			RelatedInfoModel 	relatedinfoModels 	= null;
			GeneralMasterModel 	generalMasterModels	= null;
	
			List <DutySlipModel> dutySlipModels = billingService.getds(invoiceNo,lCorpId);
			DutySlipModel dutySlipModel = dutySlipModels.get(0);
			
			List<AddressDetailModel> addressDetailModelList = bookingMasterService.getAddress(dutySlipModel.getBookingDetailModel().getId());
			List<RelatedInfoModel> relatedInfoModelList = bookingMasterService.getEmail(dutySlipModel.getBookingDetailModel().getId());
			BookingDetailModel bookingDetailModel = bookingMasterService.formFillForDetailEdit(dutySlipModel.getBookingDetailModel().getId());
			
			if (relatedInfoModelList != null && relatedInfoModelList.size() > 0) relatedinfoModels = relatedInfoModelList.get(0);
			if (addressDetailModelList != null && addressDetailModelList.size() > 0) addressDetailModels = addressDetailModelList.get(0);
	
			if(bookingDetailModel.getCarModel()!=null) id=bookingDetailModel.getCarModel().getId();
			List<GeneralMasterModel> generalMasterList = billingService.getCarMasterId(id);
			if (generalMasterList != null && generalMasterList.size() > 0) generalMasterModels= generalMasterList.get(0);
	
			bookingDetailModel.setAddress(addressDetailModels);
			bookingDetailModel.setRelatedInfo(relatedinfoModels);
			bookingDetailModel.setGeneralMaster(generalMasterModels);
			tId=bookingDetailModel.getGeneralMaster().getId();
	
			List<CorporateTariffDetModel> corporateTariffDetList = billingService.getTariffValue(tId,bookingDetailModel.getBookingMasterModel().getCorporateId().getId(),dateFormat2.format(dutySlipModel.getDutySlipClosedByDate()),bookingDetailModel.getBranch().getId());
	
			bookingDetailModel.setCorporateTariffDetModels( corporateTariffDetList);

			HashMap<String, Object> utilResponse = new HashMap<String, Object>();
			InvoiceModel invoiceModel = null;

			if(dutySlipModels.size() > 1){ //Multiple Invoice
				bookingDetailModel.setDutyslip(dutySlipModels);
				utilResponse = new Utilities().generateMultipleInvoice(bookingDetailModel, dutySlipModels); 
				invoiceModel = (InvoiceModel) utilResponse.get("InvoiceModel");
			}else{ // Single Invoice
				utilResponse = new Utilities().generateInvoice(bookingDetailModel); 
				invoiceModel = (InvoiceModel) utilResponse.get("InvoiceModel");
			} 
			String sClosedUser = "";
			List<UserModel> userModelList = userService.list();
			for(UserModel userModel : userModelList){
				if(dutySlipModel.getDutySlipClosedBy() != null){
					if(dutySlipModel.getDutySlipClosedBy() == userModel.getId().longValue()){
						sClosedUser = userModel.getUserName();
						break;
					}
				}
			}
			invoiceModel.setClosedBy(sClosedUser);
			invoiceModel.setClosedById(dutySlipModel.getDutySlipClosedBy());
			invoiceModel.setCreatedBy(lLoginUserId);
			invoiceModel.setCoverLetterId(lCoverLetterId);
			invoiceModel.setlNewCoverLetterId(lNewCoverLetterId);
			billingService.saveInvoice(invoiceModel);
		}catch (Exception e) {
			logger.error("",e);
			sReturn = "Failure";
		}
		return sReturn;
	}
	
	@RequestMapping(value = "/saveTransactionResponse", method = RequestMethod.POST)
	public @ResponseBody JsonResponse saveTransactionResponse(
			@RequestParam("order_id") String order_id,
			@RequestParam("tracking_id") String tracking_id,
			@RequestParam("bank_ref_no") String bank_ref_no,
			@RequestParam("order_status") String order_status,
			@RequestParam("failure_message") String failure_message,
			@RequestParam("payment_mode") String payment_mode,
			@RequestParam("card_name") String card_name,
			@RequestParam("status_code") String status_code,
			@RequestParam("status_message") String status_message,
			@RequestParam("currency") String currency,
			@RequestParam("amount") String amount,
			@RequestParam("discount_value") String discount_value,
			@RequestParam("mer_amount") String mer_amount,
			@RequestParam("response_code") String response_code,
			@RequestParam("trans_date") String trans_date
			/*@RequestParam("billing_name") String billing_name,
			@RequestParam("billing_address") String billing_address,
			@RequestParam("billing_city") String billing_city,
			@RequestParam("billing_state") String billing_state,
			@RequestParam("billing_zip") String billing_zip,
			@RequestParam("billing_country") String billing_country,
			@RequestParam("billing_tel") String billing_tel,
			@RequestParam("billing_email") String billing_email,
			@RequestParam("delivery_name") String delivery_name,
			@RequestParam("delivery_address") String delivery_address,
			@RequestParam("delivery_city") String delivery_city,
			@RequestParam("delivery_state") String delivery_state,
			@RequestParam("delivery_zip") String delivery_zip,
			@RequestParam("delivery_country") String delivery_country,
			@RequestParam("delivery_tel") String delivery_tel,
			@RequestParam("merchant_param1") String merchant_param1,
			@RequestParam("merchant_param2") String merchant_param2,
			@RequestParam("merchant_param3") String merchant_param3,
			@RequestParam("merchant_param4") String merchant_param4,
			@RequestParam("merchant_param5") String merchant_param5
			@RequestParam("billing_notes") String billing_notes,
			@RequestParam("bin_country") String bin_country
			@RequestParam("vault") String vault,
			@RequestParam("offer_type") String offer_type,
			@RequestParam("offer_code") String offer_code*/){
			JsonResponse res = new JsonResponse();
		String jobType = "";
		try {
			TransactionDetailModel transactionModel=new TransactionDetailModel();
			transactionModel.setOrder_id(order_id);
			transactionModel.setTracking_id(tracking_id);
			transactionModel.setBank_ref_no(bank_ref_no);
			transactionModel.setStatus_code(status_code);
			transactionModel.setOrder_status(order_status);
			transactionModel.setFailure_message(failure_message);
			transactionModel.setPayment_mode(payment_mode);
			transactionModel.setCard_name(card_name);
			transactionModel.setStatus_code(status_code);
			transactionModel.setStatus_message(status_message);
			transactionModel.setCurrency(currency);
			transactionModel.setAmount(amount);
			transactionModel.setDiscount_value(discount_value);
			transactionModel.setMer_amount(mer_amount);
			transactionModel.setResponse_code(response_code);
			
			/*DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date dateTime=dateFormat.parse(trans_date);
			
			transactionModel.setTrans_date(dateTime);*/
			
			billingService.savePaymentTransaction(transactionModel);
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

}
