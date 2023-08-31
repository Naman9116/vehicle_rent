package com.user.controller;

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
import org.springframework.web.servlet.ModelAndView;

import com.common.service.MasterDataService;
import com.master.model.GeneralMasterModel;
import com.user.model.UserAccessModel;
import com.user.model.UserModel;
import com.user.service.UserService;
import com.user.validator.UserValidator;
import com.util.JsonResponse;
import com.util.Message;
import com.util.Utilities;

@Controller
@SessionAttributes({ "userTypeIdList", "userRoleList", "userDeptList", "userStatusList"})
public class UserController {
	private static final Logger logger = Logger.getLogger(UserController.class);
	String branchesAssigned;

	@Autowired
	private UserService userService;

	@Autowired
	private MasterDataService masterDataService;

	private UserValidator validator = null;

	public UserValidator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(UserValidator validator) {
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

	@RequestMapping("/pageLoadUser")
	public ModelAndView pageLoadUser(Map<String, Object> map, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView("user");
		branchesAssigned = (String) session.getAttribute("branchesAssigned");
		UserModel loggedUserModel = (UserModel) session.getAttribute("loginUserData");
		try {
			map.put("masterDataList", masterDataService.getMasterData("All"));
			map.put("userDataList", showGrid(userService.list()));
			if(loggedUserModel.getUserRole().getName().equalsIgnoreCase("Administrator")){
				map.put("userBranchIdList", masterDataService.getGeneralMasterData_UsingCode("VBM")); 
			}else{
				map.put("userBranchIdList", masterDataService.getGeneralMasterData_UsingCode("VBM-A:"+branchesAssigned)); 
			}			
			session.setAttribute("userTypeIdList", masterDataService.getGeneralMasterData_UsingCode("VUT")); 
			session.setAttribute("userRoleList", masterDataService.getGeneralMasterData_UsingCode("Role")); 
			//session.setAttribute("userDeptList", masterDataService.getGeneralMasterData_UsingCode("Dept")); 
			session.setAttribute("userStatusList", masterDataService.getGeneralMasterData_UsingCode("Stat")); 
		} catch (Exception e) {
			logger.error("",e);
		} finally {
			map.put("userModel", new UserModel());
		}
		return modelAndView;
	}

	@RequestMapping(value = "/saveOrUpdateUser", method = RequestMethod.POST)
	public @ResponseBody JsonResponse saveOrUpdateUser(@ModelAttribute(value = "userModel") UserModel userModel,
			BindingResult bindingResult, @RequestParam("idForUpdate") String idForUpdate,
			@RequestParam(value = "sBase64Image", required = false) String sBase64Image, 
			HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		/* Saving Profile photo to directory */
		validator.validate(userModel, bindingResult);
		if (bindingResult.hasErrors()) {
			res.setStatus("Errors");
			res.setResult(bindingResult.getAllErrors());
			return res;
		}
		try {
			 String userName = userModel.getUserName().toLowerCase();
             System.out.println(userName);
	        // Check for case-sensitive duplicate username using HQL
            
             String existingUser = userService.isUsernamePresent(userName) ;
                   if (existingUser!=null) {		 System.out.println("admin");         
	        	if (idForUpdate.trim().equals("") || idForUpdate.trim().equals("0") ) {
	                // If the username already exists and it's not the same user being updated,
                    // then it's a duplicate.
	                res.setStatus("Failure");
	                res.setResult(Message.DUPLICATE_ERROR_MESSAGE);
	                return res;
	            
	        }
	        }
		
		if (idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")) {
			userModel.setUserName(userName);
			userService.save(userModel);
			res.setResult(Message.SAVE_SUCCESS_MESSAGE);
		} else {
			if(existingUser!=null) {
			Long id1 =userService.isExistingUser(existingUser);
            String existingId = Long.toString(id1);
			
			if (!existingId.equals(idForUpdate) ){
				res.setStatus("Failure");
                res.setResult(Message.DUPLICATE_ERROR_MESSAGE);
                return res;
					
			   }
			else {
	                userModel.setId(Long.valueOf(idForUpdate));
					userService.update(userModel);
					res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
			}
			}
			else {
				 userModel.setId(Long.valueOf(idForUpdate));
					userService.update(userModel);
					res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
			}
			
		}
//			if (idForUpdate.trim().equals("") || idForUpdate.trim().equals("0")) {
//				userService.save(userModel);
//				res.setResult(Message.SAVE_SUCCESS_MESSAGE);
//			} else {
//				userModel.setId(Long.valueOf(idForUpdate));
//				userService.update(userModel);
//				res.setResult(Message.UPDATE_SUCCESS_MESSAGE);
//			}
			if(sBase64Image != null){
				String sType = "Users";
				String sFileName = idForUpdate;
				if(sBase64Image.contains("blankImage.jpg")){
					Utilities.copyFile(sFileName, sFileName,sType);
				}else{
					Utilities.ImageWrite(sBase64Image,sFileName,sType);
				}
			}
			res.setStatus("Success");
		} catch (ConstraintViolationException e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.DUPLICATE_ERROR_MESSAGE);
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		} finally {
			res.setDataGrid(showGrid(userService.list()));
		}
		return res;
	}

	@RequestMapping("/pageLoadPswdChange")
	public ModelAndView pageLoadPswdChange(Map<String, Object> map, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView("pswdChange");
		map.put("userModel", new UserModel());
		return modelAndView;
	}

	@RequestMapping("/pageLoadAccessWizard")
	public ModelAndView pageLoadAccessWizard(Map<String, Object> map, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView("accessWizard");
		modelAndView.addObject("roleUserList",createTreeString(masterDataService.getGeneralMasterData_UsingCode("RoleWithUser"),"R"));
		modelAndView.addObject("moduleMenuList",createTreeString(masterDataService.getGeneralMasterData_UsingCode("MenuWithModule:0"),"M"));
		return modelAndView;
	}

	private String createTreeString(List<GeneralMasterModel> generalMasterModels, String sFor){
		String sTreeString = "";
		String sPrevRole = "",sPreRoleKey = "";
		String sChildern = "";
		try {
			for(GeneralMasterModel generalMasterModel:generalMasterModels){
				String sRoleKey = "R" + generalMasterModel.getId();
				String sRole = generalMasterModel.getName();
				String sUserKey = "U" + generalMasterModel.getMasterId();
				String sUser = generalMasterModel.getRemark();
				String sSelected = "",sViewEditDelete="" ;
				/*if called for menu than only ask for View, Edit or Delete Access*/
				if(sFor.equals("M")) sViewEditDelete = "children:[{title: 'Add',key: '"+sUserKey+"S'},{text: 'View',key: '"+sUserKey+"V'},{text: 'Edit',key: '"+sUserKey+"E'},{text: 'Delete',key: '"+sUserKey+"D'}]";
				
				if(generalMasterModel.getExtraId() != null){
					if(generalMasterModel.getExtraId() >= 1){
						sSelected = ", select: true";
					}else{
						sSelected = "";
					}
				}				
				if(!sPrevRole.equals(sRole) && !sPrevRole.equals("")){
					int iIndex = sChildern.length() - 1;
					if(!sChildern.equals(""))
						if( sChildern.charAt(iIndex) == ',')
							sChildern = sChildern.substring(0,sChildern.length() - 1);
					sTreeString += "{title: '"+sPrevRole+"', key: '"+sPreRoleKey+"',children:[" + sChildern + "]},"; //main role start
					sChildern="";
				}
				if(sUser != null)
					sChildern += "{title: '"+sUser+"',key: '"+sUserKey+"'"+sSelected+"}," ;
				else
					sChildern = "";
				sPreRoleKey = sRoleKey;
				sPrevRole = sRole;
	   		}
			int iIndex = sChildern.length() - 1;
			if(!sChildern.equals(""))
				if( sChildern.charAt(iIndex) == ',') 
					sChildern = sChildern.substring(0,sChildern.length() - 1);
			sTreeString += "{title: '"+sPrevRole+"', key: '"+sPreRoleKey+"',children:[" + sChildern + "]}"; //main role start
			sTreeString = "[" + sTreeString + "]";
			sTreeString = sTreeString.replace(",children:[]", "");
		} catch (Exception e) {
			logger.error("",e);
		}
		return sTreeString;
	}
	
	@RequestMapping(value = "/getAssignPermission", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getAssignPermission( @RequestParam("userId") long userId, HttpSession session) {
		JsonResponse res = new JsonResponse();
		try {
	     List<UserAccessModel> userAccessModel=userService.getAllowPermission(userId);
	 
			res.setStatus("Success");
			res.setUserAccessModel(userAccessModel);
			

		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public @ResponseBody JsonResponse chnagePassword(@RequestParam("loginUserId") Long loginUserId,
			@RequestParam("oldPass") String oldPass, @RequestParam("newPass") String newPass,
			@RequestParam("confPass") String confPass, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		try {
			UserModel userModel = userService.formFillForEdit(loginUserId);
			if (!confPass.equals(newPass)) {
				res.setResult("Retype password is not matched with new password");
				res.setStatus("Failure");
				return res;
			}
			if (!oldPass.equals(userModel.getPassword())) {
				res.setResult("Old password is not correct");
				res.setStatus("Failure");
				return res;
			}
			if (newPass.equals(userModel.getPassword())) {
				res.setResult("New password is same as Old");
				res.setStatus("Failure");
				return res;
			}
			userModel.setPassword(newPass);
			userService.update(userModel);
			res.setResult("Password updated Successfully");
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value = "/getUserIdByName", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getUserIdByName(@RequestParam("userName") String userName,
			HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		try {
			List<UserModel> userModelList = userService.listMasterWise("", userName);
			if(userModelList.size() > 0){
				res.setUserModel(userModelList.get(0));
			}
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value = "/allowPermission", method = RequestMethod.POST)
	public @ResponseBody JsonResponse allowPermission(@RequestParam("roleSelection") String roleSelection,
			@RequestParam("menuSelection") String menuSelection, HttpServletRequest request) {
		JsonResponse res = new JsonResponse();
		try {
			userService.saveAllowPermission(roleSelection, menuSelection);
			res.setResult("Access granted Successfully");
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@RequestMapping(value = "/formFillForEditUser", method = RequestMethod.POST)
	public @ResponseBody JsonResponse formFillForEditUser(@ModelAttribute(value = "userModel") UserModel userModel,
			BindingResult bindingResult, @RequestParam("formFillForEditId") String formFillForEditId) {
		JsonResponse res = new JsonResponse();
		try {
			userModel = userService.formFillForEdit(Long.parseLong(formFillForEditId));
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		} finally {
			res.setDataGrid(showGrid(userService.list()));
			res.setUserModel(userModel);
		}
		return res;
	}

	@RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
	public @ResponseBody JsonResponse deleteUser(@ModelAttribute(value = "userModel") UserModel userModel,
			BindingResult bindingResult, @RequestParam("idForDelete") String idForDelete) {
		JsonResponse res = new JsonResponse();
		try {
			userService.delete(Long.valueOf(idForDelete));
			res.setStatus("Success");
			res.setResult(Message.DELETE_SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FOREIGNKEY_ERROR_MESSAGE);
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		} finally {
			res.setDataGrid(showGrid(userService.list()));
		}
		return res;
	}

	public String showGrid(List<UserModel> userModelDataList) {
		
		String dataString = " <table class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'> "
				+ " 	<tr> 	"
			  	+ " 		<th width="+Message.ACTION_SIZE+">Action</th> "
				+ " 		<th>Role Name</th> 			"
				+ " 		<th>User Name</th> 			"
				+ " 		<th>First Name</th> 		"
				+ " 		<th>Last Name</th> 			"
				+ " 		<th>Department</th> 		"
				+ " 		<th>Mobile No.</th> 		"
				+ " 		<th>Status</th> 			"
				+ " 	</tr>	"
				+ " </thead>	" 
				+ " <tbody> ";
		for (UserModel userModel : userModelDataList) {
			if(!branchesAssigned.contains("," + userModel.getBranch().getId() + ",")) continue;
			dataString = dataString 
					+ "<tr> " 
					+ "	<td> " 
					+ " 	<a href='javascript:formFillForEditUser("+ userModel.getId() + ")'> "+Message.EDIT_BUTTON+ "</a> " 
					+ " 	<a href='javascript:viewUser("+ userModel.getId() + ")' > "+Message.VIEW_BUTTON+ "</a> " 
					+ " 	<a href='javascript:deleteUser("+ userModel.getId() + ")' > "+Message.DELETE_BUTTON+ "</a> " 
					+ " </td>" 
					+ " <td> "+ userModel.getUserRole().getName() + "</td>" + "<td>" + userModel.getUserName() + "</td>" 
					+ " <td> "+ userModel.getUserFirstName() + "</td>" + "<td>" + userModel.getUserLastName() + "</td>" 
					+ " <td> "+ userModel.getBranch().getName() + "</td>" + "<td>" + userModel.getUserMobile() + "</td>"
					+ " <td> " + userModel.getUserStatus().getName() + "</td>" 
					+ "</tr> 		";
		}
		dataString = dataString + "</tbody> </table>";
		return dataString;
	}

	@RequestMapping(value = "/getDataGridDataUser", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getDataGridDataUser(@ModelAttribute(value = "userModel") UserModel userModel,
			BindingResult bindingResult, @RequestParam("pageNumber") int pageNumber,
			@RequestParam("userCode") String userCode, @RequestParam("searchCriteria") String searchCriteria) {
		JsonResponse res = new JsonResponse();
		try {
			res.setDataGrid(showGrid(userService.listMasterWise(userCode, searchCriteria)));
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("",e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getUserType_User", method = RequestMethod.GET, headers = "Accept=*/*")
	public @ResponseBody List<String> getUserType_User(@RequestParam("term") String query, HttpSession session) {
		String userTypeName = null;
		String userTypeId = null;
		query = query.toLowerCase();
		List<String> matched = null;
		List<GeneralMasterModel> generalMasterModelList = (List<GeneralMasterModel>) session
				.getAttribute("userTypeIdList");
		if (generalMasterModelList != null) {
			matched = new ArrayList<String>();
			for (int i = 0; i < generalMasterModelList.size(); i++) {
				userTypeName = generalMasterModelList.get(i).getName().toLowerCase();
				userTypeId = generalMasterModelList.get(i).getId().toString();
				if (userTypeName.startsWith(query)) {
					matched.add(generalMasterModelList.get(i).getName() + "   |   "
							+ generalMasterModelList.get(i).getId());
				} else if (userTypeId.startsWith(query)) {
					matched.add(generalMasterModelList.get(i).getName() + "   |   "
							+ generalMasterModelList.get(i).getId());
				}
			}
		}
		return matched;
	}

}
