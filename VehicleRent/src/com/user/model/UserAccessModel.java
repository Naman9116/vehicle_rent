package com.user.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.master.model.GeneralMasterModel;

@Entity
@Table(name="userAccess")

public class UserAccessModel implements Serializable{
	public UserAccessModel(){
		
	}
	
	public UserAccessModel(Long roleId,Long userId,Long menuId){
		this.roleId.setId(roleId);
		this.userId.setId(userId);
		this.menuId.setId(menuId);
	}
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@ManyToOne
	@JoinColumn(name="roleId",referencedColumnName="id",nullable=false,updatable=true)
	private GeneralMasterModel roleId = null;

	@ManyToOne
	@JoinColumn(name="userId",referencedColumnName="id",nullable=true,updatable=true)
	private UserModel userId = null;

	@ManyToOne
	@JoinColumn(name="menuId",referencedColumnName="id",nullable=false,updatable=true)
	private GeneralMasterModel menuId =  null;

	@Column(name="menuAccess",unique=false,updatable=true,length=5)
	private String menuAccess="A";
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public GeneralMasterModel getRoleId() {
		return roleId;
	}

	public void setRoleId(GeneralMasterModel roleId) {
		this.roleId = roleId;
	}

	public GeneralMasterModel getMenuId() {
		return menuId;
	}

	public void setMenuId(GeneralMasterModel menuId) {
		this.menuId = menuId;
	}

	public UserModel getUserId() {
		return userId;
	}

	public void setUserId(UserModel userId) {
		this.userId = userId;
	}

	public String getMenuAccess() {
		return menuAccess;
	}

	public void setMenuAccess(String menuAccess) {
		this.menuAccess = menuAccess;
	}
}
