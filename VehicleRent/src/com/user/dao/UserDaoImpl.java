package com.user.dao;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.master.model.GeneralMasterModel;
import com.user.model.UserAccessModel;
import com.user.model.UserModel;

@Repository("userDao")
public class UserDaoImpl implements UserDao{

	@Autowired
	private SessionFactory sessionFactory;
	private static int pageSize = 5;
	
	@SuppressWarnings("unchecked")
	public List<UserModel> list() {
		List<UserModel> userModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserModel.class);
		criteria.addOrder(Order.desc("id"));
		userModel = (List<UserModel>)criteria.list();
		return userModel;
	}
	
	@SuppressWarnings("unchecked")
	public List<UserModel> listMasterWise(String userCode,String searchCriteria) {
		List<UserModel> userModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserModel.class).
		addOrder(Order.desc("id"));
		if(userCode!=null && !userCode.equalsIgnoreCase(""))
			criteria.add(Restrictions.eq("userCode", userCode));
		if(searchCriteria!=null && !searchCriteria.trim().equalsIgnoreCase(""))
			criteria.add(Restrictions.like("userName", searchCriteria,MatchMode.ANYWHERE));
		userModel = (List<UserModel>)criteria.list();
		return userModel;
	}

	public String save(UserModel userModel) {
		sessionFactory.getCurrentSession().save(userModel);
		return "saveSuccess";
	}
	
	public UserModel formFillForEdit(Long formFillForEditId) {
		UserModel userModel=null;
		userModel =(UserModel) sessionFactory.getCurrentSession().get(UserModel.class, new Long(formFillForEditId));
		return userModel;
	}
	
	public String delete(Long idForDelete) {
		UserModel userModel =(UserModel) sessionFactory.getCurrentSession().get(UserModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(userModel);
		return "deleteSuccess";
	}
	
	public String update(UserModel userModel) {
		//UserModel userModelForDelete =(UserModel) sessionFactory.getCurrentSession().get(UserModel.class, new Long(userModel.getId()));
		sessionFactory.getCurrentSession().update(userModel);
		//sessionFactory.getCurrentSession().save(userModel);
		return "updateSuccess";
	}

	@Override
	public String saveAllowPermission(String selectedRoleValues, String selectedMenuValues) {
		
		String[] roleValues = selectedRoleValues.split(",");
		String[] menuValues=selectedMenuValues.split(",");
		Arrays.sort(menuValues);
		for(String roleValue:roleValues){
			String sPrefixRole = roleValue.substring(0,1);
			roleValue = roleValue.substring(1,roleValue.length());
			GeneralMasterModel generalMasterModelRole = null;
			UserModel userModel = null;
			if(sPrefixRole.equals("R")){
				generalMasterModelRole = ((GeneralMasterModel) sessionFactory.getCurrentSession().get(GeneralMasterModel.class, Long.parseLong(roleValue)));
			}else{
				userModel = ((UserModel) sessionFactory.getCurrentSession().get(UserModel.class, Long.parseLong(roleValue)));
				generalMasterModelRole = ((GeneralMasterModel) sessionFactory.getCurrentSession().get(GeneralMasterModel.class, userModel.getUserRole().getId()));
			}
			String sUserID = "";
			if(userModel == null)
				sUserID = "null";
			else
				sUserID = userModel.getId().toString();
			
			Query query =  sessionFactory.getCurrentSession().createQuery("DELETE FROM UserAccessModel "+ 
					"WHERE userId.id= "+sUserID+" AND roleId.id= "+generalMasterModelRole.getId()+" ");
			query.executeUpdate();
			
			for (String menuValue:menuValues){
				UserAccessModel userAccessModel=new UserAccessModel();
				String sSufixMenu = menuValue.substring(menuValue.length() - 1, menuValue.length());
				if(sSufixMenu.equals("S") || sSufixMenu.equals("E") || sSufixMenu.equals("V") || sSufixMenu.equals("D") ){
					menuValue = menuValue.substring(1,menuValue.length() - 1);
				}else{
					menuValue = menuValue.substring(1,menuValue.length());
				}
				
				GeneralMasterModel generalMasterModelMenu = ((GeneralMasterModel) sessionFactory.getCurrentSession().get(GeneralMasterModel.class, Long.parseLong(menuValue)));
				
				userAccessModel.setRoleId(generalMasterModelRole);
				userAccessModel.setUserId(userModel);
				userAccessModel.setMenuId(generalMasterModelMenu);
				if(sSufixMenu.equals("S") || sSufixMenu.equals("E") || sSufixMenu.equals("V") || sSufixMenu.equals("D") ) userAccessModel.setMenuAccess(sSufixMenu);

				generalMasterModelRole.getUserAccessModel().add(userAccessModel);
			}
			
			sessionFactory.getCurrentSession().save(generalMasterModelRole);
		}
		return "saveSuccess";
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserAccessModel> getAllowPermission(Long userId) {
		return sessionFactory.getCurrentSession().createQuery("SELECT U FROM UserAccessModel U WHERE userId.id= "+userId+" ").list();	
	}
}
