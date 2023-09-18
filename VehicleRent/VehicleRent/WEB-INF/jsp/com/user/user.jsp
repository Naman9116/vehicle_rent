<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="com.util.Utilities"%>
<%
	String sAccess = "";
	Utilities utils = new Utilities();
	sAccess = utils.userAccessString(session, request, false);
%>

<HTML>
<HEAD>
	<TITLE> VehicleRent </TITLE>	
	<script src='<%=request.getContextPath()%>/js/com/user/user.js'></script>
	<script>
		var contextPath;
		var branchId ;
		var companyId ;
		var form_data;
		var contextPath="<%=request.getContextPath()%>";
		var userPageAccess= '<%=sAccess%>';	
		function onLoadPage(){
			assignRights();
			contextPath="<%=request.getContextPath()%>";
			$('#dataTable2').html("${userDataList}");
		    refreshData();
		    if(<%=session.getAttribute("loginBranchId")%>!=null)
				branchId='<%=(Long) session.getAttribute("loginBranchId")%>';
		    if(<%=session.getAttribute("loginCompanyId")%>!=null)
				companyId='<%=(Long) session.getAttribute("loginCompanyId")%>';
		}
		function showPhoto(files){
			encodeImageFileAsURL("photoFile","photoImage");
		}
	    $(document).ready(function () {
	        window.asd = $('.SlectBox').SumoSelect({ csvDispCount: 1 });
	    });

	</script>
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
		<form:form commandName="userModel" enctype="multipart/form-data" method="post">
		<div class="row">
			<div class="col-md-12">
				<legend><strong>User Registration</strong></legend>
				<div class="box box-primary">
					<div class="box-body">
					<table width=98%>
					<tr height='35px'>
						<td><form:label path="userName">User Name</form:label></td>
						<td><form:input path="userName" class="form-control" maxlength="50"></form:input></td>
						<td><form:label path="password"> Password</form:label></td>
						<td><form:password path="password" class="form-control" maxlength="50"></form:password></td>
						<td><form:label path="userRole"> User Role</form:label></td>
						<td><form:select path="userRole" class="form-control">
								<form:option value="0" label="--- Select Role ---"/>
								<form:options items="${userRoleList}" itemValue="id" itemLabel="name"/>	
							</form:select>
						</td>
						<td><form:label path="userStatus" > User Status</form:label></td>
						<td><form:select path="userStatus" class="form-control">
								<form:option value="0" label="--- Select Status ---"/>
								<form:options items="${userStatusList}" itemValue="id" itemLabel="name"/> 
							</form:select>
						</td>
					</tr>
					<tr height='35px'>
						<td><form:label path="userFirstName">First Name</form:label></td>
						<td><form:input path="userFirstName" class="form-control" maxlength="50"></form:input></td>
						<td><form:label path="userLastName" >Last Name</form:label></td>
						<td><form:input path="userLastName" class="form-control"  maxlength="50"></form:input></td>
						<td><form:label path="userMobile">Mobile</form:label></td>
						<td><form:input path="userMobile" class="form-control" maxlength="11" onkeypress="isNumeric(event)" ></form:input></td>
						<td><form:label path="branch"> Working Branch(s)</form:label></td>
						<td>
				        	<form:select path="assignBranches" multiple="multiple" placeholder="Assign Branches" class="SlectBox" id="assignBranches" name="assignBranches" onChange="checkForSelect()">
								<form:options items="${userBranchIdList}" itemValue="id" itemLabel="name"/>
			            	</form:select>
			            </td>	
					</tr>
					<tr height='60px'>
						<td><form:label path="userName">Profile Picture</form:label></td>
						<td>
							<a href='' onclick="document.getElementById('photoFile').click(); return false"><img id="photoImage" src="<%=request.getContextPath()%>/img/blankImage.jpg" onerror="if (this.src != '<%=request.getContextPath()%>/img/blankImage.jpg') this.src = '<%=request.getContextPath()%>/img/blankImage.jpg';" title="Upload Profile Photo" height='75px' width='70px'/></a>
							<input type="file" id="photoFile" name="photoFile" onchange='showPhoto(this.files);' style="visibility: hidden; width: 1px; height: 1px" multiple ACCEPT="image/*">
							<input type="hidden" id="idForUpdate" name="idForUpdate" value="0">
						</td>	
						<td align="center" colspan=4>
							<input type='button'  value='Save Record'	class='btn btn-primary' style='width:150px;' onclick="saveOrUpdateUser()" id="addButton"/> 
							<input type='button'  value='Update' class="btn btn-primary"	id="updateButton" onclick="saveOrUpdateUser()" disabled style="width:150px">
							<input type='reset'  value='Clear' onclick="resetData()" style='width:100px;'	class='btn btn-primary' id="addButton"/>
							<div style="width:100%;height:20px;display:none" id="info" class="success"></div>
							<div style="width:95%;height:60px;overflow:auto;display:none" id="error" class="error"></div>
						</td>
					</tr>
					</table>		
					</div>
				</div>
			</div>	
		</div>
		<div class="row">
			<div class="col-md-12" >
				<div class="box box-primary">
					<div class="box-body" id="dataTable2">
					</div>
				</div>
			</div>
		</div>	
		</form:form>
	</section>
</div>
</BODY>
</HTML>



