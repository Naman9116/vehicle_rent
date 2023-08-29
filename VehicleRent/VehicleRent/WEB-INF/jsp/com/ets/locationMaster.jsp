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
<script src= "<%=request.getContextPath()%>/js/com/ets/locationMaster.js"></script>


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
	var taxationList;
	var loginUserId;
    var sTariff;
	var userPageAccess= '<%=sAccess%>';
	
	$(function(){
	    //Datemask dd/mm/yyyy
	    $("#effectiveDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	 	$("#effectiveDate").val(getTimeStampTo_DDMMYYYY(new Date()));
		});
	
	function onLoadPage(){
		$('#dataTable2').html("${locationDataList}");
		$('#dataTable1').html("${carModelRateList}");
		if(<%=session.getAttribute("loginUserId")%>!=null)
			loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
		assignRights();
		refreshData();	
	/* 	refreshData1(); */
	}
	
   

</script>
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
		<form:form commandName="locationMasterModel" method="post" id="locationForm">
		<div class="row">
			<div class="col-md-12">
				<legend>Location <strong>Master</strong></legend>
				<div class="row">
					<div class="col-md-6">
						<div class="box-body">
							<table width="100%" cellspacing=1>
							<tr>
								<td width="2%"></td>
								<td width="35%"><label >Corporate Name</label></td>
								<td width="2%"><font color="red">*</font></td>
								<td width="60%">
									<select class="form-control" id="corporateId" name="corporateId" style="width:100%" onChange="fillValues()">
									<option value="0">Select</option>
									<c:forEach var="corporateId" items="${customerOrCompanyNameIdList}">
									<option value = '${corporateId.id}'>${corporateId.name}</option>
							</c:forEach>	
						</select>
								</td>
							</tr>
							<tr>
								<td width="2%"></td>
								<td width="35%"><label >Branch Name</label></td>
								<td width="2%"><font color="red">*</font></td>
								<td width="70%">
									<input id="companyName"  name="companyName" type="hidden" maxlength="50"></input>
									<input id="companyCode"  name="companyCode" type="hidden" maxlength="10"></input>
									<select class="form-control" id="branch" name="branch" style="width:100%" onChange="fillHub()">
									<option value="0">Select</option>
								</select>
									
								</td>
							</tr>
							<tr>
								<td width="2%"></td>
								<td width="35%"><label >Hub</label></td>
								<td width="2%"><font color="red">*</font></td>
								<td width="60%">
									<select class="form-control" id="outlet" name="outlet" style="width:100%">
									<option value="0">Select</option>
								</select>
								</td>
							</tr>
							<tr>
								<td></td>
								<td><label  >Zone</label></td>
								<td><font color="red">*</font></td>
								<td><select class="form-control" id="zone" name="zone" style="width:100%" onChange="fillLocation()">
									<option value="0">Select</option>
									<c:forEach var="zoneId" items="${zoneList}">
									<option value = '${zoneId.id}'>${zoneId.name}</option>
							</c:forEach>	
						</select>
							</tr>
							<tr>
								<td></td>
								<td><label >Location</label></td>
								<td width="2%"></td>
								<td><select class="form-control" id="location" name="location" style="width:100%">
									<option value="0">Select</option>
									</select>
								</td>
							</tr>
							
							<tr>
								<td></td>
								<td><label >Kilometer</label></td>
								<td width="2%"></td>
								<td><input id="kms" type="text"  style="width:100%" class="form-control" onkeypress="isNumeric(event)" ></input></td>
							</tr><tr>
								<td></td>
								<td><label>PinCode</label></td>
								<td></td>
								<td><input id="pinCode" type="text"  class="form-control" maxlength="50" onkeypress="isNumeric(event)" onblur="getCityUsingPincode()"></input></td>
							</tr>
							<tr>
								<td></td>
								<td><label>City</label></td>
								<td></td>
								<td>
									<!-- <input style="width:100%" id="city" class="form-control"  maxlength="250"></input> -->
									<select class="form-control" id="city" name="city" style="width:100%">
									<option value="0">Select</option>
								</select>
								</td>
							</tr>
							
								<tr>
								<td></td>
								<td><label>Effective Date</label></td>
								<td><font color="red">*</font></td>
								<td>
								<input type="text" id="effectiveDate"  name="effectiveDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control"  maxlength="10" ></input>
								</td>
							</tr>
							
							
					</table>
				</div>		
			</div>
			    <div class="col-md-6">
				
					<div class="box-body" id="dataTable1" style="height:210px;width:90%;overflow:auto"></div>
				
				</div>
				<div class="row">
					<div class="col-md-3"></div>
					<div class="col-md-6">
						<div class="box-body">
						<input type='button'  value='Save'	 class="btn btn-primary" 	id="addButton"    onclick="saveOrUpdateLocationMaster()" style="width:150px">
						<input type='button'  value='Update' class="btn btn-primary"	id="updateButton" onclick="saveOrUpdateLocationMaster()" disabled style="width:150px">
						<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetAll()" style="width:150px">
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
