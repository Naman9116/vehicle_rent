<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@page import="com.util.Utilities"%>
<%
	String sAccess = "";
	Utilities utils = new Utilities();
	sAccess = utils.userAccessString(session, request, true);
%>
<HTML>
<HEAD>
<script src= "<%=request.getContextPath()%>/js/com/corporate/authoriseClientMaster.js"></script>
<script src= "<%=request.getContextPath()%>/js/com/common/global/contactDetail.js"></script>
<style>
	td{
	    padding-top: .05em;
	    padding-bottom: .05em;
	}
	textarea {
    	resize: none;
	}
</style>

<script>
	var pageFor;
	var form_data;
	var loginUserId;
	var userPageAccess= '<%=sAccess%>';	

	function onLoadPage(){
		assignRights();
		pageFor = '${pageFor}';
		deleteExtraData = "&pageFor=" + pageFor;

		$('#dataTable2').html("${authorizedUserDataList}");
		if(<%=session.getAttribute("loginUserId")%>!=null)
			loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
		resetData();
	}
</script>
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
		<form:form commandName="authorizedUserModel" method="post" id="autorizedUserForm">
		<div class="row">
			<div class="col-md-12">
				<c:if test="${pageFor=='Admin'}">
					<legend>Authorise <strong>Client Master</strong></legend>
				</c:if>
				<c:if test="${pageFor=='Client'}">
					<legend>User <strong>Client Master</strong></legend>
				</c:if>
				<div class="row">
					<div class="col-md-6">
						<div class="box-body">
							<table width=100% cellspacing=1>
							<tr>
								<td width="2%"></td>
								<td width="35%"><form:label path="corporateId" >Corporate Name</form:label></td>
								<td width="2%"><font color="red">*</font></td>
								<td width="60%">
									<form:select path="corporateId" id="corporateId" class="form-control" onChange="AssignAuthoriser(this.value,0)">
 										<form:option value="0" label="--- Select ---"/>
										<form:options items="${corporateIdList}" itemValue="id" itemLabel="name"/>	
									</form:select>
								</td>
							</tr>
							<c:if test="${pageFor=='Admin'}">
								<tr>
									<td></td>
									<td><label>Authorization</label></td>
									<td><font color="red">*</font></td>
									<td><table width=100%>
										<tr>
											<td width=30%><form:label path="authTypeAdmin">Admin User</form:label></td>
											<td width=10% valign="top"><form:input type='checkbox' path="authTypeAdmin" name='authTypeAdmin' id = 'authTypeAdmin'></form:input></td>
											<td width=20%></td>
											<td width=30%><form:label path="authTypeClient">Client User</form:label></td>
											<td width=10% align="right" valign="top"><form:input type='checkbox' path="authTypeClient" name='authTypeClient' id = 'authTypeClient'></form:input></td>
										</tr>
										</table>								
									</td>
								</tr>
							</c:if>	
							<c:if test="${pageFor=='Client'}">
								<tr>
									<td width="2%"></td>
									<td width="35%"><form:label path="parentId" >Authoriser</form:label></td>
									<td width="2%"><font color="red">*</font></td>
									<td width="60%">
										<form:select path="parentId" id="parentId" class="form-control">
	 										<form:option value="" label="--- Select ---"/>
											<form:options items="${authoriserIdList}" itemValue="id" itemLabel="name"/>	
										</form:select>
									</td>
								</tr>
							</c:if>
							<tr>
								<td></td>
								<c:if test="${pageFor=='Admin'}">
									<td><form:label path="name">Authorise Name</form:label></td>
								</c:if>	
								<c:if test="${pageFor=='Client'}">
									<td><form:label path="name">Client Name</form:label></td>
								</c:if>	
								<td width="2%"><font color="red">*</font></td>
								<td><form:input path="name" type="text" class="form-control" maxlength="50" ></form:input></td>
							</tr>
							<tr>
								<td></td>
								<td><label>Mobile No.</label></td>
								<td width="2%"><font color="red">*</font></td>
								<td>
									<input id="personalMobile" class="form-control" maxlength="20"></input>
									<input type='hidden' name='contactDetailModel_id' id='contactDetailModel_id'/>						
								</td>
							</tr>
							<tr>
								<td></td>
								<td><label>Gender</label></td>
								<td width="2%"></td>
								<td>									
									<table width=100%>
									<tr>
										<td width=60%>
											<form:select path="gender" id="gender" class="form-control">
		 										<form:option value="" label="--- Select ---"/>
		 										<form:option value="M" label="Male"/>
												<form:option value="F" label="Female"/>
												<form:option value="T" label="Transgender"/>
										   </form:select>										</td>
										<td width=30%><form:label path="status">ETS Allow</form:label></td>
										<td width=2%></td>
										<td width=10% align="right" valign="top">
											<form:input type='checkbox' path="etsAllow" name='etsAllow' id = 'etsAllow'></form:input>										</td>
									</tr>
									</table>									
								</td>	
							</tr>
							<tr>
								<td></td>
								<td><label>Designation</label></td>
								<td width="2%"></td>
								<td><input id="contactPerson2" class="form-control" maxlength="50"></input></td>
							</tr>
							<tr>
								<td></td>
								<td><label>E-Mail</label></td>
								<td width="2%"></td>
								<td><input id="personalEmailId" class="form-control" maxlength="50"></input></td>
							</tr>
							<tr>
								<td></td>
								<td><form:label path="empCode">Employee Id</form:label></td>
								<td></td>
								<td><form:input path="empCode" type="text" class="form-control" maxlength="30" ></form:input></td>
							</tr>
							<tr>
								<td></td>
								<td><form:label path="priorityId">Priority User</form:label></td>
								<td></td>
								<td>
									<form:select path="priorityId" id="priorityId" class="form-control">
										<form:options items="${priorityIdList}" itemValue="id" itemLabel="name"/>	
									</form:select>
								</td>
							</tr>
							<tr>
								<td></td>
								<td><form:label path="bkgAllow">Booking Allow</form:label></td>
								<td></td>
								<td>									
									<table width=100%>
									<tr>
										<td width=40%>
											<form:select path="bkgAllow" id="bkgAllow" class="form-control" >
		 										<form:option value="Y" label="Yes"/>
		 										<form:option value="N" label="No"/>
											</form:select>
										</td>
										<td width=25%><form:label path="status">Status</form:label></td>
										<td width=2%></td>
										<td width=40%>
											<form:select path="status" id="status" class="form-control" >
		 										<form:option value="Y" label="Active"/>
		 										<form:option value="N" label="InActive"/>
											</form:select>
										</td>
									</tr>
									</table>									
								</td>	
							</tr>
							
							<tr>
								<td></td>
								<td><form:label path="userName">User Name</form:label></td>
								<td></td>
								<td>									
									<table width=100%>
									<tr>
										<td width=40%>
										<input id="userName" class="form-control"  style="width: 180px" maxlength="50"></input>
										</td>
										<td id="changePswdLabel"  width=25% style="display: none"><form:label path="pswd">Change Password</form:label></td>
										<td width=2%></td>
										<td id="changePswdCheck" width=5% style="display: none">
											<input id="changePassword" type="checkbox" onclick="enableDisablePassword()"></input>
										</td>
									</tr>
									</table>									
								</td>	
							</tr>
							
							<tr>
								<td></td>
								<td><form:label path="userName">Password</form:label></td>
								<td></td>
								<td>									
									<table>
									<tr>
										<td width=20%>
										<input id="pswd" type="password" class="form-control" style="width: 180px" maxlength="50"></input>
										</td>
									</tr>
									</table>									
								</td>	
							</tr>
							</table>
						</div>		
					</div>
					<div class="col-md-6">
						<div class="box-body">
							<table width=100%>
							<tr>
								<td width=2%></td>
								<td width="25%" valign=top><form:label path="oAddress"><strong>Office</strong> Address</form:label></td>
								<td width=2%></td>
								<td width="60%">
									<form:textarea path="oAddress" id="oAddress" name="oAddress" style="height:84px;width:100%" class="textarea form-control"></form:textarea>
								</td>
								<td width=2%></td>
							</tr>
							<tr>
								<td></td>
								<td valign=top><form:label path="hAddress"><strong>Home</strong> Address</form:label></td>
								<td ></td>
								<td >
									<form:textarea path="hAddress" id="hAddress" name="hAddress" style="height:84px;width:100%" class="textarea form-control"></form:textarea>
								</td>
								<td ></td>
							</tr>
							
								<tr>
								<td></td>
								<td><form:label path="division">Division</form:label></td>
								<td></td>
								<td>									
									<table>
									<tr>
										<td width=20%>
										<input id=division type="text" class="form-control" style="width: 180px" maxlength="50"></input>
										</td>
									</tr>
									</table>									
								</td>	
							</tr>
							
							<tr>
								<td></td>
								<td><label>Zone</label></td>
								<td></td>
								<td width=180px>									
									<table>
									<tr>
										<td >
										<select class="form-control" id="zone" name="zone" style="width:180px" onChange="getLocationASPerCorporate()">
											<option value="">---Select---</option>
										</select>
										</td>
									</tr>
									</table>									
								</td>	
							</tr>
							
								
							<tr>
								<td></td>
								<td><label>Location</label></td>
								<td></td>
								<td>									
									<table>
									<tr>
										<td width=180px>
										<select class="form-control" id="location" name="location" style="width:180px">
											<option value="">---Select---</option>
										</select>
										</td>
									</tr>
									</table>									
								</td>	
							</tr>
							
								<tr>
								<td></td>
								<td><label>City</label></td>
								<td></td>
								<td>									
									<table>
									<tr>
										<td width=180px>
										<select class="form-control" id="city" name="city" style="width:180px">
											<option value="">---Select---</option>
										</select>
										</td>
									</tr>
									</table>									
								</td>	
							</tr>
							</table>
						</div>	
					</div>
				</div>
				<div class="row">
					<div class="col-md-3"></div>
					<div class="col-md-6">
						<div class="box-body">
						<input type='button'  value='Save'	 class="btn btn-primary" 	id="addButton"    onclick="saveOrUpdate()" style="width:150px">
						<input type='button'  value='Update' class="btn btn-primary"	id="updateButton" onclick="saveOrUpdate()" disabled style="width:150px">
						<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:150px">
						<input type="hidden" id="idForUpdate" name="idForUpdate" value="0">
						</div>
					</div>
					<div class="col-md-3">
						<div class="box-body">
							<div style="width:100%;height:60px;overflow:auto;display:none" id="error" class="error"></div>
							<div style="width:100%;height:20px;display:none" id="info" class="success"></div>
						</div>
					</div>
				</div>
			</div>
		</div>	
		<div class="row">	
			<div class="col-md-12" >
				<div class="box box-primary">
					<div class="box-body" id="dataTable2" style="overflow:auto">
					</div>
				</div>
			</div>
		</div>	
	</form:form>
	</section>
</div>	
</BODY>
</HTML>