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
	<script src= "<%=request.getContextPath()%>/js/com/master/vendorMaster.js"></script>
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
		function showPhoto(files)
		{
		    var oFiles = document.getElementById("photoFile").files;
			var nBytes = oFiles[0].size / 1024;
			if(nBytes > 100){
				alert("Please attach profile file upto 100 KB");
				return false;
			}
			var objImage = document.getElementById("photoImage");
			objImage.src = window.URL.createObjectURL(files[0]);
			objImage.onload = function() {
		        window.URL.revokeObjectURL(this.src);
		    }
		}

		var loginUserId;
		var contextPath;
		var userPageAccess= '<%=sAccess%>';	
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
		    $("#agDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		    $("#dlIssue").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		    $("#validUpto").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		    $("#doj").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		});
	</script>
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<!-- form start -->
	<form:form commandName="vendorModel" method="post" id="vendorForm">
		<div class="row">
			<legend>Vendor <strong>Master</strong></legend>
			<div class="col-md-6">
				<div class="box-body">
					<table width=100%>
					<tr>
						<td width="2%"></td>
						<td width="35%"><form:label path="name" >Vendor Name</form:label></td>
						<td width="2%"><font color="red">*</font></td>
						<td width="60%"><form:input path="name" id="name"  class="form-control" maxlength="50"></form:input></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="pan" >PAN No.</form:label></td>
						<td><font color="red">*</font></td>
						<td><form:input path="pan" id="pan"  class="form-control" maxlength="10"></form:input></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="sTaxNo" >Service Tax No.</form:label></td>
						<td></td>
						<td><form:input path="sTaxNo" id="sTaxNo"  class="form-control" maxlength="20"></form:input></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="tan" >TAN No.</form:label></td>
						<td></td>
						<td><form:input path="tan" id="tan"  class="form-control" maxlength="10"></form:input></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="lstno" >LST No.</form:label></td>
						<td></td>
						<td><form:input path="lstno" id="lstno"  class="form-control" maxlength="20"></form:input></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="agDate" >Date of Agreement</form:label></td>
						<td><font color="red">*</font></td>
						<td><form:input path="agDate" id="agDate" style="width:100%" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'"  maxlength="10"></form:input></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="email">E-mail</form:label></td>
						<td></td>
						<td><form:input style="width:100%"  path="email" id="email" class="form-control" onkeypress='isValidEmail(event)' maxlength="50"></form:input></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="helpLineNo">Help Line No.</form:label></td>
						<td></td>
						<td><form:input style="width:100%" path="helpLineNo" id="helpLineNo" class="form-control" maxlength="30"></form:input></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="orgTypeId">Organization Type</form:label></td>
						<td><font color="red">*</font></td>
						<td>
							<form:select path="orgTypeId" NAME='orgTypeId'  id='orgTypeId' class="form-control">
								<form:option value = ''>---Select---</form:option>
								<c:forEach var="organization" items="${organizationList}">
									<form:option value = '${organization.id}'>${organization.name}</form:option>
								</c:forEach>	
							</form:select>
						</td>
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
								<td width=30%><form:label path="status">Vendor Status</form:label></td>
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
					</tr>
					<tr>
						<td></td>
						<td><form:label path="bankAcName" >Bank Account Holder</form:label></td>
						<td><font color="red">*</font></td>
						<td><form:input path="bankAcName" id="bankAcName"  class="form-control" maxlength="50"></form:input></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="bankAcNo" >Bank A/c No.</form:label></td>
						<td><font color="red">*</font></td>
						<td><form:input path="bankAcNo" id="bankAcNo"  class="form-control" maxlength="20"></form:input></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="bankName" >Bank Name</form:label></td>
						<td><font color="red">*</font></td>
						<td><form:input path="bankName" id="bankName"  class="form-control" maxlength="50"></form:input></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="bankNEFT" >NEFT Code</form:label></td>
						<td><font color="red">*</font></td>
						<td>
							<table width=100%>
							<tr>
								<td width=30%>
									<form:input path="bankNEFT" id="bankNEFT"  class="form-control" maxlength="20"></form:input>
								</td>
								<td width=30%><form:label path="bankRTGS">RTGS Code</form:label></td>
								<td width=2%></td>
								<td width=30%>
									<form:input path="bankRTGS" id="bankRTGS"  class="form-control" maxlength="20"></form:input>
								</td>
							</tr>
							</table>			
						</td>
					</tr>
					<tr>
						<td></td>
						<td><label> is Vendor also Chauffeur?  </label></td>		
						<td></td>	
						<td><input id="isVendorChauffeur"    type='checkbox' onchange="showHide()" ></input></td>		
					</tr>
					<tr id="chauffeurDetails" style="display: none;">
						<td></td>
						<td style="width: 30%;">
							<table  style="width: 278%;">
								<tr>
									<td></td>
									<td width=36%><label >D/L No.</label></td>
									<td><font color="red">*</font></td>
									<td><input name="dlNo" id='dlNo' class="form-control"></input></td>
								</tr>
								<tr>
									<td></td>
									<td width=36%><label>D/L Issue</label></td>
									<td><font color="red">*</font></td>
									<td>
										<table style="width:100%">
										<tr>
											<td width=30%>
												<input name="dlIssue" id='dlIssue' style="width:100%" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'"  maxlength="10"></input>
											</td>
											<td width=30%><label>Valid Upto</label><span><font color="red">*</font></span></td>
											<td width=2%></td>
											<td width=30%>
												<input id="validUpto" name='validUpto' style="width:100%" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'"  maxlength="10"></input>
											</td>
										</tr>
										</table>			
									</td>
								</tr>
								<tr>
									<td></td>
									<td width=36%><label >Licence Authority</label></td>
									<td><font color="red">*</font></td>
									<td><input name="licAuthority" id='licAuthority' class="form-control"></input></td>
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
						<td width="35%"><label>Vendor Photo</label></td>
						<td width=2%></td>
						<td width="60%" align="center">
							<a href='' onclick="document.getElementById('photoFile').click(); return false"><img id="photoImage" src="<%=request.getContextPath()%>/img/blankImage.jpg" onerror="if (this.src != '<%=request.getContextPath()%>/img/blankImage.jpg') this.src = '<%=request.getContextPath()%>/img/blankImage.jpg';" title="Upload Vendor Photo" height='100px' width='100px'/></a>
							<input type="file" id="photoFile" name="photoFile" onchange='showPhoto(this.files);' style="visibility: hidden; width: 1px; height: 1px" multiple ACCEPT="image/*">
						</td>
					</tr>
					<tr>
						<td width=2%></td>
						<td width="35%"><form:label path="contPerson">Contact Person</form:label></td>
						<td width=2%></td>
						<td width="60%">
							<form:input path="contPerson" id="contPerson" style="width:100%" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="50"></form:input>
						</td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="contPersonMobile">Mobile No.</form:label><span><font color="red">*</font></span></td>
						<td></td>
						<td><form:input path="contPersonMobile" id="contPersonMobile" class="form-control" maxlength="20"></form:input></td>
					</tr>
					<tr>
						<td></td>
						<td valign="top"><form:label path="perAddress"><strong>Present</strong> Address</form:label></td>
						<td></td>
						<td>
							<form:textarea path="perAddress" id="perAddress" name="perAddress" style="height:105px;width:100%" class="textarea form-control"></form:textarea>
						</td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="perAddressPIN">PIN Code</form:label></td>
						<td></td>
						<td><form:input path="perAddressPIN" style="width:100%" id="perAddressPIN" class="form-control" onkeypress='isNumeric(event)' maxlength="6"></form:input></td>
					</tr>
					<tr>
						<td></td>
						<td valign="top"><form:label path="temAddress"><strong>Permanent</strong> Address</form:label></td>
						<td></td>
						<td><form:textarea path="temAddress" id="temAddress" name="temAddress" style="height:105px;width:100%" class="textarea form-control"></form:textarea></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="temAddressPIN">PIN Code</form:label></td>
						<td></td>
						<td><form:input path ="temAddressPIN" style="width:100%" id="temAddressPIN" class="form-control" onkeypress='isNumeric(event)' maxlength="6"></form:input></td>
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
