package com.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.user.dao.UserDao;
import com.user.model.UserAccessModel;
import com.user.model.UserModel;

@Service("userService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserDao userDao;

	public UserServiceImpl() {
		
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String delete(Long idForDelete) {
		return userDao.delete(idForDelete);
	}
	
	@Override
	public UserModel formFillForEdit(Long formFillForEditId) {
		return userDao.formFillForEdit(formFillForEditId);
	}
	
	@Override
	public List<UserModel> list() {
		return userDao.list();
	}
	
	@Override
	public List<UserModel> listMasterWise(String userCode,String searchCriteria) {
		return userDao.listMasterWise(userCode,searchCriteria);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(UserModel userModel) {
		return userDao.save(userModel);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String update(UserModel userModel) {
		return userDao.update(userModel);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String saveAllowPermission(String selectedRoleValues, String selectedMenuValues) {
		return userDao.saveAllowPermission(selectedRoleValues,selectedMenuValues);
	}
	
	public List<UserAccessModel> getAllowPermission(Long userId){
		return userDao.getAllowPermission(userId);
	}

	@Override
	public String isUsernamePresent(String userName) {
		return userDao.isUsernamePresent(userName);
	}

	@Override
	public Long isExistingUser(String existingUser) {
		
		return userDao.isExistingUser(existingUser) ;
	}

}
