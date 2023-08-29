<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.util.Utilities"%>
<%
	String sAccess = "";
	Utilities utils = new Utilities();
	sAccess = utils.userAccessString(session, request, false);
%>

<HTML>
<HEAD>
	<TITLE> VehicleRent </TITLE>
	<script src= "<%=request.getContextPath()%>/js/com/master/chauffeurMaster.js"></script>
	<style>
	td{
	    padding-top: .05em;
	    padding-bottom: .05em;
	}
	textarea {
    	resize: none;
	}
	</style>
	<script language="JavaScript">
		var loginUserId;
		var contextPath;
		var userPageAccess= '<%=sAccess%>';	

		function showPhoto(files){
			encodeImageFileAsURL("photoFile","photoImage");
		}

		function onLoadPage(){
			assignRights();
			contextPath="<%=request.getContextPath()%>";
			$('#dataTable2').html("${existingData}");
			if(<%=session.getAttribute("loginUserId")%>!=null)
				loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
			refreshData();	
		}
		$(function () {
		    //Datemask dd/mm/yyyy
		    $("#dlIssue").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		    $("#dlValidUpto").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		    $("#dob").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		    $("#doj").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		});
	</script>
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<!-- form start -->
	<form:form commandName="chauffeurModel" method="post" id="chauffeurForm">
		<div class="row">
			<legend>Chauffeur <strong>Master</strong></legend>
			<div class="col-md-6">
				<div class="box-body">
					<table width=100%>
					<tr>
						<td width="2%"></td>
						<td width="35%"><form:label path="vendorId" >Vendor Name</form:label></td>
						<td width="2%"><font color="red">*</font></td>
						<td width="60%">
							<form:select path="vendorId" class="form-control">
								<form:option value="" label="--- Select ---"/>
								<form:options items="${vendorList}" itemValue="id" itemLabel="name"/>	                
							</form:select>
						</td>
						<td width="2%"></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="name" >Chauffeur Name</form:label></td>
						<td><font color="red">*</font></td>
						<td><form:input path="name" id="name"  class="form-control" maxlength="50"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="mobileNo" >Mobile No.</form:label></td>
						<td><font color="red">*</font></td>
						<td><form:input path="mobileNo" id="mobileNo"  class="form-control" maxlength="20"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="drivingLicence" >Driving Licence No</form:label></td>
						<td><font color="red">*</font></td>
						<td><form:input path="drivingLicence" id="drivingLicence"  class="form-control" maxlength="20"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="dlIssue" >Date of Issue</form:label></td>
						<td><font color="red">*</font></td>
						<td><form:input path="dlIssue" id="dlIssue" style="width:100%" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'"  maxlength="10"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="dlValidUpto" >Valid upto</form:label></td>
						<td><font color="red">*</font></td>
						<td><form:input path="dlValidUpto" id="dlValidUpto" style="width:100%" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'"  maxlength="10"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="dlAuthority" >Licence Authority</form:label></td>
						<td><font color="red">*</font></td>
						<td><form:input path="dlAuthority" id="dlAuthority" class="form-control" maxlength="50"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="dlAuthorityAddr">Authority Address</form:label></td>
						<td></td>
						<td><form:input path="dlAuthorityAddr" id="dlAuthorityAddr" class="form-control" maxlength="200"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="dob">Date of Birth</form:label></td>
						<td></td>
						<td><form:input path="dob" id="dob" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'"  maxlength="10"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="doj">Date of Joining</form:label></td>
						<td><font color="red">*</font></td>
						<td><form:input path="doj" id="doj" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'"  maxlength="10"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="badgeNo">Badge No</form:label></td>
						<td></td>
						<td>									
							<table width=100%>
							<tr>
								<td width=40%><form:input path="badgeNo" id="badgeNo" class="form-control" maxlength="20"></form:input></td>
								<td width=30%><form:label path="bloodGrp">Blood Group</form:label></td>
								<td width=2%></td>
								<td width=25%><form:input path="bloodGrp" id="bloodGrp" class="form-control" maxlength="10"></form:input></td>
							</tr>
							</table>									
						</td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="dutyAllow">Duties Allow</form:label></td>
						<td><font color="red">*</font></td>
						<td>									
							<table width=100%>
							<tr>
								<td width=40%>
									<form:select path="dutyAllow" id="dutyAllow" class="form-control" >
 										<form:option value="Y" label="Yes"/>
 										<form:option value="N" label="No"/>
									</form:select>
								</td>
								<td width=30%><form:label path="status">Driver Status</form:label></td>
								<td width=2%></td>
								<td width=25%>
									<form:select path="status" id="status" class="form-control" >
 										<form:option value="Y" label="Active"/>
 										<form:option value="N" label="InActive"/>
									</form:select>
								</td>
							</tr>
							</table>									
						</td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="qualification" >Qualification</form:label></td>
						<td></td>
						<td><form:input path="qualification" id="qualification"  class="form-control" maxlength="100"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="experience" >Experience</form:label></td>
						<td></td>
						<td><form:input path="experience" id="experience"  class="form-control" maxlength="100"></form:input></td>
						<td></td>
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
				</div><!-- /.box -->
			</div>
			<div class="col-md-6">
				<div class="box-body">
					<table width=100%>
					<tr>
						<td width=2%></td>
						<td width="35%" valign="top"><label>Chauffeur Photo</label></td>
						<td width=2%></td>
						<td width="60%" align="center">
							<a href='' onclick="document.getElementById('photoFile').click(); return false"><img id="photoImage" src="<%=request.getContextPath()%>/img/blankImage.jpg" onerror="if (this.src != '<%=request.getContextPath()%>/img/blankImage.jpg') this.src = '<%=request.getContextPath()%>/img/blankImage.jpg';" title="Upload Chauffeur Photo" height='100px' width='100px'/></a>
							<input type="file" id="photoFile" name="photoFile" onchange='showPhoto(this.files);' style="visibility: hidden; width: 1px; height: 1px" multiple ACCEPT="image/*">
						</td>
						<td width=2%></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="idMark">ID Mark</form:label></td>
						<td></td>
						<td>
							<form:input path="idMark" id="idMark" class="form-control" maxlength="100"></form:input>
						</td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td valign="top"><form:label path="presentAddress"><strong>Present</strong> Address</form:label></td>
						<td></td>
						<td>
							<form:textarea path="presentAddress" id="presentAddress" name="perAddress" style="height:75px;width:100%" class="textarea"></form:textarea>
						</td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="presentAddressPIN">PIN Code</form:label></td>
						<td></td>
						<td><form:input path="presentAddressPIN" id="presentAddressPIN" class="form-control" onkeypress='isNumeric(event)' maxlength="6"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td valign="top"><form:label path="peramentAddress"><strong>Permanent</strong> Address</form:label></td>
						<td></td>
						<td><form:textarea path="peramentAddress" id="peramentAddress" style="height:75px;width:100%" class="textarea"></form:textarea></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="peramentAddressPIN">PIN Code</form:label></td>
						<td></td>
						<td><form:input path ="peramentAddressPIN" id="peramentAddressPIN" class="form-control" onkeypress='isNumeric(event)' maxlength="6"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td width=2%></td>
						<td width="35%"><form:label path="refName">Reference Name</form:label></td>
						<td width=2%></td>
						<td width="60%">
							<form:input path="refName" id="refName" class="form-control" maxlength="50"></form:input>
						</td>
						<td></td>
					</tr>
					<tr>
						<td width=2%></td>
						<td width="35%"><form:label path="refMobileNo">Mobile No</form:label></td>
						<td width=2%></td>
						<td width="60%">
							<form:input path="refMobileNo" id="refMobileNo" class="form-control" maxlength="20"></form:input>
						</td>
						<td></td>
					</tr>
					<tr>
						<td width=2%></td>
						<td width="35%"><form:label path="refAddress">Address</form:label></td>
						<td width=2%></td>
						<td width="60%">
							<form:input path="refAddress" id="refAddress" class="form-control" maxlength="200"></form:input>
						</td>
						<td></td>
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
					<div style="width:95%;height:60px;overflow:auto;display:none" id="error" class="error"></div>
					<div style="width:100%;height:20px;display:none" id="info" class="success"></div>
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
	</section><!-- /.content -->
</div><!-- /.content-wrapper -->
</BODY>
</HTML>
