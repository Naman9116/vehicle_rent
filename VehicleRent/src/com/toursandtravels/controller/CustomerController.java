package com.toursandtravels.controller;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.javamail.JavaMailSender;
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
import com.corporate.service.AuthorizedUserService;
import com.corporate.service.CorporateService;
import com.operation.controller.BookingMasterController;
import com.operation.service.BookingMasterService;
import com.operation.service.CarDetailService;
import com.operation.service.DutySlipService;
import com.operation.validator.BookingMasterValidator;
import com.toursandtravels.model.CustomerModel;
import com.toursandtravels.service.CustomerService;
import com.user.service.UserService;
import com.util.JsonResponse;
import com.util.Message;
@Controller
public class CustomerController {

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
	private CustomerService customerService;


	@Autowired
	private UserService userService;

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
	@RequestMapping("/pageLoadCustomerMaster")
	public ModelAndView pageLoadCustomerMaster(Map<String, Object> map,HttpSession session) {
		ModelAndView modelAndView=null;
		try {
			modelAndView = new ModelAndView("tCustomerMaster");
			map.put("companyIdList",masterDataService.getGeneralMasterData_UsingCode("VCM"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally{
			map.put("customerModel", new CustomerModel());
			map.put("customerList",showGrid());
		}
		return modelAndView;
	}
	
	@RequestMapping(value = "/saveOrUpdateCustomer", method = RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateCustomer(
			@ModelAttribute(value = "customerModel") CustomerModel customerModel,
			@RequestParam("idForUpdate") String idForUpdate,HttpSession session){
		JsonResponse res = new JsonResponse();
		try {
			if(idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")){
				customerModel.setCreatedDate(new Date());
				customerService.save(customerModel);
				res.setResult(Message.SAVE_SUCCESS_MESSAGE);
			}
			else{
				customerModel.setId(Long.valueOf(idForUpdate));
				customerModel.setCreatedDate(new Date());
				customerService.update(customerModel);
				res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
			}
			res.setStatus("Success");
		}catch(ConstraintViolationException e){
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult("Mobile Number Already Exists");
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

	public String showGrid(){
		List<CustomerModel> customerModelList = customerService.getCustomers() ;
		String dataString =	"";
//		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dataString =	
			" <table class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"+
			"	<thead style='background-color: #FED86F;'>		"+
		  	" 	<tr> 											"+
		  	" 		<th width="+Message.ACTION_SIZE+">Action</th> 		  "+
		  	" 		<th>ID</th> 			"+
		  	" 		<th>Name</th> "+
		  	" 		<th>Mobile No</th> 	"+
		  	" 		<th>Email</th> 		"+
			" 	</tr>						"+
		  	"	</thead>					"+
		  	"   <tbody>						"; 
			for(CustomerModel customerModel:customerModelList ){
				String email="";
				if(customerModel.getEmail()!=null)
					email=customerModel.getEmail();
				dataString=dataString+" <tr> "+
				" <td>"+
				" 	<a href='javascript:formFillForEditCustomer("+customerModel.getId()+")'>"+Message.EDIT_BUTTON+"</a> "+
				" 	<a href='javascript:viewCustomer("+customerModel.getId()+")' >"+Message.VIEW_BUTTON+"</a> "+ 				
				" 	<a href='javascript:deleteCustomer("+customerModel.getId()+")' >"+Message.DELETE_BUTTON+"</a> "+ 				
				" </td> 														"+
				" 	<td>"+ customerModel.getId()+ "</td>  						"+
				" 	<td>"+ customerModel.getName()+ "</td>   					"+
				" 	<td>"+ customerModel.getMobileNo()+ "</td>   			"+
				" 	<td>"+ email+ "</td>   				"+
				" </tr> 		";
			}	
			dataString=dataString+"</tbody></table>";
		return dataString;
	}
	@RequestMapping(value="/formFillForEditCustomer",method=RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditCustomer(
			@RequestParam("formFillForEditId") String formFillForEditId){
		JsonResponse res = new JsonResponse();
		try{
			CustomerModel customerModel = customerService.formFillForEditCustomer(Long.parseLong(formFillForEditId));
			res.setStatus("Success");
			res.setCustomerModel(customerModel);
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		
		return res;
	}
	
	@RequestMapping(value="/deleteCustomer",method=RequestMethod.POST)
	public @ResponseBody JsonResponse deleteCustomer(
			@RequestParam("idForDelete") String idForDelete){
		JsonResponse res = new JsonResponse();
		try{
			customerService.delete(Long.valueOf(idForDelete));
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
}
