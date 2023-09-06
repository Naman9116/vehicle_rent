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
<script src= "<%=request.getContextPath()%>/js/com/relatedInfo/relatedInfo.js"></script>
<script src= "<%=request.getContextPath()%>/js/com/common/global/contactDetail.js"></script>
<script src= "<%=request.getContextPath()%>/js/com/common/global/addressDetail.js"></script>

<style>
	td{
	    padding-top: .05em;
	    padding-bottom: .05em;
	}
</style>

<script>
	var count =0;
	var count1 =0;
	var selectedRowForDelete='';
	var checkedKey='';
	
	var pageFor='';
	var form_data;
	var userPageAccess= '<%=sAccess%>';	
	function onLoadPage(){
		pageFor='${pageFor}';
		fetchExtraData = "&pageFor=" + pageFor;
		deleteExtraData = "&pageFor=" + pageFor;

		$('#dataTable2').html("${relatedInfoDataList}");
		if(pageFor=="Branch")
			document.getElementById("branchTD").style.display='block';
		if(pageFor=="Company")
			document.getElementById("companyTD").style.display='block';
		if(pageFor=="Outlet")
			document.getElementById("outletTD").style.display='block';
		assignRights();
		resetData();
	}
	$(function () {
	    //Datemask dd/mm/yyyy
	    $("#regDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	});
    $(document).ready(function () {
        window.asd = $('.SlectBox').SumoSelect({ csvDispCount: 3 });
    });

</script>
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
		<form:form commandName="relatedInfoModel" method="post" id="relatedInfoForm">
		<div class="row">
			<div class="col-md-12">
				<legend>${pageFor} <strong>Registration</strong></legend>
				<div class="row">
					<div class="col-md-6">
						<div class="box-body">
							<table width=100% cellspacing=1>
							<c:if test="${pageFor=='Branch'}">
							<tr>
								<td width="2%"></td>
								<td width="35%"><form:label path="company">Company Name</form:label></td>
								<td width="2%"><font color="red">*</font></td>
								<td width="60%">
									<form:select path="company" id="company" class="form-control" onChange="AssignCompany()">
 										<form:option value="" label="--- Select ---"/>
 										<form:option value="0" label="All Companies"/>
										<form:options items="${companyIdList}" itemValue="id" itemLabel="name"/>	
									</form:select>
								</td>
							</tr>
							</c:if>
							<c:if test="${pageFor=='Outlet'}">
							<tr>
								<td width="2%"></td>
								<td width="35%"><form:label path="branch" >Branch Name</form:label></td>
								<td width="2%"><font color="red">*</font></td>
								<td width="60%">
									<form:select path="branch" id="branch" class="form-control" onChange="AssignBranch()">
 										<form:option value="" label="--- Select ---"/>
										<form:options items="${branchIdList}" itemValue="id" itemLabel="name"/>	
									</form:select>
								</td>
							</tr>
							</c:if>
							<tr>
								<td width="2%"></td>
								<td width="35%"><form:label path="${pageFor}" >${pageFor} Name</form:label></td>
								<td width="2%"><font color="red">*</font></td>
								<td width="70%" id="companyTD" style="display:none;width:100%;">
									<table width="100%">
										<tr><td width="70%">									
												<form:input type = "hidden" path="company" id="company"></form:input>
												<form:input path="companyName" id="companyName"  class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="50"></form:input>
											</td>
											<td width="10%">
												<label>Code</label>
											</td>
											<td width="20%">
												<input name="Code" id="Code"  class="form-control" maxlength="10"></input>
											</td>
										</tr>
									</table>
								</td>
								<td width="60%" id="branchTD" style="display:none;width:100%;">
									<table width="100%">
										<tr><td width="70%">									
												<form:input type = "hidden" path="branch" id="branch"></form:input>
												<form:input path="branchName" id="branchName"  class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="50"></form:input>
											</td>
											<td width="10%">
												<label>Code</label>
											</td>
											<td width="20%">
												<input name="CodeBr" id="CodeBr"  class="form-control" maxlength="10"></input>
											</td>
										</tr>
									</table>
								</td>
								<td width="60%" id="outletTD" style="display:none;width:100%;">
									<table width="100%">
										<tr><td width="70%">									
												<form:input type = "hidden" path="outlet" id="outlet"></form:input>
												<form:input path="outletName" id="outletName"  class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="50"></form:input>
											</td>
											<td width="10%">
												<label>Code</label>
											</td>
											<td width="20%">
												<input name="CodeOl" id="CodeOl"  class="form-control" maxlength="10"></input>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<c:if test="${pageFor=='Company'}">
							<tr>
								<td></td>
								<td><form:label path="panNo" >PAN No.</form:label></td>
								<td><font color="red">*</font></td>
								<td><form:input path="panNo" style="width:100%" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="10"></form:input></td>
							</tr>
							<tr>
								<td></td>
								<td><form:label path="gstin">GSTIN</form:label></td>
								<td width="2%"><font color="red">*</font></td>
								<td><form:input style="width:100%" path="gstin" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="15"></form:input></td>
							</tr>
							<tr>
								<td></td>
								<td><form:label path="tanNo">TAN No.</form:label></td>
								<td><font color="red">*</font></td>
								<td><form:input style="width:100%" path="tanNo" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="20"></form:input></td>
							</tr>
							<tr>
								<td></td>
								<td><form:label path="lstNo">LST No.</form:label></td>
								<td></td>
								<td><form:input style="width:100%" path="lstNo" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="20"></form:input></td>
							</tr>
							</c:if>	
							<tr>
								<td></td>
								<td><form:label path="regDate"><c:if test="${pageFor=='Company'}">Date of Registration</c:if><c:if test="${pageFor!='Company'}">Date of Opening</c:if></form:label></td>
								<td><font color="red">*</font></td>
								<td><form:input path="regDate" type="text" style="width:100%" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'"  maxlength="10"></form:input></td>
							</tr>
							<tr>
								<td></td>
								<td><form:label path="email">E-mail</form:label></td>
								<td><font color="red">*</font></td>
								<td><form:input style="width:100%" path="email" class="form-control" onkeypress='isValidEmail(event)' maxlength="50"></form:input></td>
							</tr>
							<tr>
								<td></td>
								<td><form:label path="helpLine">Help Line No.</form:label></td>
								<td></td>
								<td><form:input style="width:100%" path="helpLine" class="form-control" maxlength="50"></form:input></td>
							</tr>
							<c:if test="${pageFor=='Company'}">
							<tr>
								<td></td>
								<td><form:label path="webSite">Web Site</form:label></td>
								<td></td>
								<td><form:input path="webSite" style="width:100%;" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="50"></form:input></td>
							</tr>
							</c:if>	
							<c:if test="${pageFor=='Branch'}">
							<tr>
								<td></td>
								<td><form:label path="airportTerminals">Airport Terminals</form:label></td>
								<td></td>
								<td><select multiple="multiple" placeholder="Select Terminals" class="SlectBox form-control"  style="width:100%;" id="airportTerminals" name="airportTerminals">
										<c:forEach var="terminalsMaster" items="${terminalsMaster}">
											<option value = '${terminalsMaster.id}'>${terminalsMaster.name}</option>
										</c:forEach>	
					            	</select>
								</td>
							</tr>
							<tr>
								<td></td>
								<td><form:label path="gstin">GSTIN</form:label></td>
								<td></td>
								<td><form:input path="gstin" style="width:100%;" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="15"></form:input></td>
							</tr>
							</c:if>
							</table>
						</div>		
					</div>
					<div class="col-md-6">
						<div class="box-body">
							<table width=100%>
							<tr>
								<td width=2%></td>
								<td width="35%"><label>Contact Person</label></td>
								<td width=2%></td>
								<td width="60%">
									<input id="contactPerson1" style="width:100%" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="50"></input>
									<input type='hidden' name='contactDetailModel_id' id='contactDetailModel_id'/>						
								</td>
							</tr>
							<tr>
								<td></td>
								<td><label>Person Contact No.</label></td>
								<td></td>
								<td><input id="personalMobile" class="form-control" maxlength="20"></input></td>
							</tr>
							<tr>
								<td></td>
								<td><label>Registered Address 1</label></td>
								<td></td>
								<td>
									<input style="width:100%" id="address1" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="250"></input>
									<input type='hidden' name='addressDetailModel_id' id='addressDetailModel_id'/>						
								</td>
							</tr>
							<tr>
								<td></td>
								<td><label>Registered Address 2</label></td>
								<td></td>
								<td><input style="width:100%" id="address2" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="250"></input></td>
							</tr>
							<tr>
								<td></td>
								<td><label>Land Mark</label></td>
								<td></td>
								<td><input style="width:100%" id="landMark" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="200"></input></td>
							</tr>
							<tr>
								<td></td>
								<td><label>PIN Code</label></td>
								<td></td>
								<td><input style="width:100%" id="pincode" class="form-control" onkeypress='isNumeric(event)' maxlength="6" onblur='getStateDistMasterData_usingPincode(this.value)'></input></td>
							</tr>
							<tr>
								<td></td>
								<td><label>State</label></td>
								<td width="2%"><font color="red">*</font></td>
								<td>
									<Select NAME='state'  id='state' class="form-control" onchange='getDistrictData(this.value,0)' >
										<option value = '0'>---Select---</option>
										<c:forEach var="stateMaster" items="${stateMasterList}">
											<option value = '${stateMaster.id}'>${stateMaster.name}</option>
										</c:forEach>	
									</select>
								</td>
							</tr>
							<tr>
								<td></td>
								<td><label>District</label></td>
								<td width="2%"><font color="red">*</font></td>
								<td>
									<div id='distictDiv' name='distictDiv'> 
										<Select NAME='district'  id='district' class="form-control" onchange='getCityData(this.value,0)'>
											<option value = '0'>---Select---</option>
										</select> 
									</div>
								</td>
							</tr>
							<tr>
								<td></td>
								<td><label>City</label></td>
								<td width="2%"><font color="red">*</font></td>
								<td>
									<div id='cityDiv' name='cityDiv' > 
										<Select NAME='city'  id='city' class="form-control" >
											<option value = '0'>---Select---</option>
										</select> 
									</div>
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
							<input type='button'  value='Save'	 class="btn btn-primary" 	id="addButton"    onclick="saveOrUpdateRelatedInfo()" style="width:150px">
							<input type='button'  value='Update' class="btn btn-primary"	id="updateButton" onclick="saveOrUpdateRelatedInfo()" disabled style="width:150px">
							<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:150px">
							<input type="hidden" id="idForUpdate" name="idForUpdate" value="0">
						</div>
					</div>
					<div class="col-md-3"></div>
				</div>
				<div class="row">
					<div class="col-md-3"></div>
					<div class="col-md-6">
						<div class="box-body">
							<div style="width:95%;height:60px;overflow:auto;display:none" id="error" class="error"></div>
							<div style="width:100%;height:20px;display:none;text-align: center" id="info" class="success"></div>
						</div>
					</div>
					<div class="col-md-3"></div>
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



