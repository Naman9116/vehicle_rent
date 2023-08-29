<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<HTML>
<HEAD>
	<TITLE> VehicleRent </TITLE>	
	<script src='<%=request.getContextPath()%>/js/com/user/pswdChange.js'></script>
		<script>
		var loginUserId;
		var contextPath="<%=request.getContextPath()%>";
		function onLoadPage(){
			contextPath="<%=request.getContextPath()%>";
		    if(<%=session.getAttribute("loginUserId")%>!=null)
		    	loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
		}
	</script>
	
</HEAD>	
<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
		<form:form commandName="userModel" method="post">
		<div class="row">
			<div class="col-md-12">
				<legend><strong>Change Password</strong></legend>
				<div class="box box-primary">
					<div class="box-body">
					<table width=98%>
					<tr height='60px'>
						<td><label>Old Password</label></td>
						<td><input type = "password" name="oldPass" id="oldPass" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="50"/></td>

						<td><label>New Password</label></td>
						<td><input type = "password" name="newPass" id="newPass" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="50"/></td>

						<td><label>Retype Password</label></td>
						<td><input type = "password" name="confPass" id="confPass" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="50"/></td>
					</tr>
					<tr height='100px'>
						<td align="Right" colspan=3><input type='button'  value='Change Password'	class='btn btn-primary' style='width:150px;' onclick="chnagePassword()" id="addButton"/> <input type='reset'  value='Clear' onclick="resetData()" style='width:100px;'	class='btn btn-primary' id="resetButton"/></td>
						<td colspan=3>
							<div style="width:100%;height:20px;display:none" id="info" class="success"></div>
							<div style="width:95%;height:60px;overflow:auto;display:none" id="error" class="error"></div>
						</td>
					</table>
					</div>
				</div>	
			</div>
		</div>
		</form:form>	
	</section>
</div>	
</BODY>
</HTML>	