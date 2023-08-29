package com.user.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.common.service.MasterDataService;
import com.master.model.GeneralMasterModel;
import com.operation.service.DutySlipService;
import com.user.model.UserModel;
import com.util.JsonResponse;
import com.util.Message;
import com.util.Utilities;

@Controller
@SessionAttributes({"loginUserName","loginUserCode","loginUser","loginUserId","loginUserData","appVersion","loginCompanyId","loginBranchId",
								"branchesAssigned","moduleList","menuList","userPageAccess","maxInvoiceNo","flag","loginUserRole"})
public class UserLoginAndRegistrationController {
	private static final Logger logger = Logger.getLogger(UserLoginAndRegistrationController.class);

	@Autowired
	private MasterDataService masterDataService;
	
	@Autowired
	private DutySlipService dutySlipService;
	
	@RequestMapping(value="/userLogin", method= RequestMethod.POST)
	public ModelAndView userLogin(Map<String, Object> map,
			@RequestParam("userName") String userName,
			@RequestParam("password") String password,HttpSession session){
		ModelAndView modelAndView = null;
		List<UserModel> userModel=null;
		userModel=masterDataService.userLogin(userName, password);
		
		Boolean loginSuccess=false;
		if(userModel!=null && userModel.size()>0){
			modelAndView = new ModelAndView("homePage");
/*			String invoiceNo = dutySlipService.getInvoiceNo();
			Long maxInvoiceNo  = invoiceNo != null ? Long.valueOf(invoiceNo.substring(0, 5)) : 0;
			System.out.println("Max Invoice No  :" +maxInvoiceNo);
			modelAndView.addObject("maxInvoiceNo", maxInvoiceNo);
*/			modelAndView.addObject("flag", "Y");
			modelAndView.addObject("loginUserName",userModel.get(0).getUserFirstName() + " " + userModel.get(0).getUserLastName());
			modelAndView.addObject("loginUserId",userModel.get(0).getId());
			modelAndView.addObject("loginUser",userModel.get(0).getUserName());
			modelAndView.addObject("loginUserData",userModel.get(0));
			modelAndView.addObject("appVersion",session.getServletContext().getAttribute("appVersion"));
			modelAndView.addObject("loginCompanyId",userModel.get(0).getCompany()!=null?userModel.get(0).getCompany().getId():"");
			modelAndView.addObject("loginCompanyName",userModel.get(0).getCompany()!=null?userModel.get(0).getCompany().getName():"");
			modelAndView.addObject("loginBranchId",userModel.get(0).getBranch()!=null?userModel.get(0).getBranch().getId():"");
			modelAndView.addObject("loginBranchName",userModel.get(0).getBranch()!=null?userModel.get(0).getBranch().getName():"");
			modelAndView.addObject("branchesAssigned",userModel.get(0).getAssignBranches()!=null?","+userModel.get(0).getAssignBranches()+",":"");
			modelAndView.addObject("moduleList",masterDataService.getGeneralMasterData_UsingCode("MODU"));
			modelAndView.addObject("menuList",masterDataService.getGeneralMasterData_Menu(userModel.get(0).getId().toString()));
			modelAndView.addObject("userPageAccess",masterDataService.getUserAccess(userModel.get(0).getId().toString(), "ALL"));
			modelAndView.addObject("loginUserRole",userModel.get(0).getUserRole().getName());
			loginSuccess=true;
			return modelAndView;
		}
		else if(userName.trim().equals("") || password.trim().equals("")){
			modelAndView = new ModelAndView("login");
			modelAndView.addObject("message","User Cant' Blank ");
		}
		else if(password.trim().equals("")){
			modelAndView = new ModelAndView("login");
			modelAndView.addObject("message","Password Cant' Blank ");
		}
		else{
			modelAndView = new ModelAndView("login");
			modelAndView.addObject("message","Invalid User or Password");
		}
		if(!loginSuccess){
			map.put("userModel", new UserModel());
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/userRegistration", method= RequestMethod.POST)
	public ModelAndView userRegistration(@RequestParam("username") String userName,@RequestParam("password") String password){
		ModelAndView modelAndView = null;
		if(userName.trim().equalsIgnoreCase("admin") && password.trim().equalsIgnoreCase("ntplupl")){
			modelAndView = new ModelAndView("homePage");
			return modelAndView;
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/login",method= RequestMethod.GET)
	public ModelAndView login(Map<String, Object> map){
		ModelAndView modelAndView = new ModelAndView("login");
		map.put("userModel", new UserModel());
		return modelAndView;
	}

	@RequestMapping(value="/logout")
	public ModelAndView logout(HttpSession session,SessionStatus status){
		ModelAndView modelAndView = new ModelAndView("logout");
		status.setComplete();
		session.setAttribute("loginUserData",null);
		session.setAttribute("loginUserName",null);
		session.setAttribute("loginUserCode",null);
		session.setAttribute("loginUserId",null);
		return modelAndView;
	}
	
	@RequestMapping(value="/homePage",method= RequestMethod.GET)
	public ModelAndView homePage(Map<String, Object> map){
		ModelAndView modelAndView = new ModelAndView("homePage");
		return modelAndView;
	}
	
	@RequestMapping(value="/getBranchDataUsingCompany",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getBranchDataUsingCompany(@RequestParam("companyId") String companyId){
		JsonResponse res = new JsonResponse();
		List<GeneralMasterModel> generalMasterModels=null;
		try{
			generalMasterModels= masterDataService.getBranchDataUsingCompany(companyId);
			res.setStatus("Success");
		}
		catch(Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		finally{
			if(generalMasterModels!=null && generalMasterModels.size()>0)
				res.setDataGrid(branchListBox(generalMasterModels,0).toString());
			else 
				res.setDataGrid("");
		}
		return res;
	}

	public StringBuilder branchListBox(List<GeneralMasterModel> generalMasterModels,int fieldDefaultSelectedIndex){
		String selectedIndex="";
		StringBuilder dataString =	new StringBuilder();
		dataString.append("<SELECT NAME='branch' id='branch' class='form-control' style='width:99%'>"
				+ "<OPTION VALUE='0' SELECTED > --- Select --- ");
		for(GeneralMasterModel generalMasterModel:generalMasterModels ){
			if(fieldDefaultSelectedIndex==generalMasterModel.getId())
				selectedIndex="selected";
			else
				selectedIndex="";
			dataString.append("<OPTION VALUE='"+generalMasterModel.getId()+"' "+selectedIndex+"> "+ generalMasterModel.getName());
		}	
		dataString.append("</SELECT>") ;
		return dataString;
	}

}
