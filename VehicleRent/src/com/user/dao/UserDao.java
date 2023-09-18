package com.user.dao;

import java.util.List;

import com.user.model.UserAccessModel;
import com.user.model.UserModel;

public interface UserDao {

	public List<UserModel> list() ;
	
	public List<UserModel> listMasterWise(String userCode,String searchCriteria) ;
	
	public String save(UserModel userModel);
	
	public UserModel formFillForEdit(Long formFillForEditId);
	
	public String delete(Long idForDelete) ;
	
	public String update(UserModel userModel) ;

	public String saveAllowPermission(String selectedRoleValues, String selectedMenuValues);

	public List<UserAccessModel> getAllowPermission(Long userId);

	public String isUsernamePresent(String userName);

	public Long isExistingUser(String existingUser);
}
