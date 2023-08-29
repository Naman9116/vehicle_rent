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
	<script src= "<%=request.getContextPath()%>/js/com/toursTravels/booking.js"></script>
	<script src= "<%=request.getContextPath()%>/js/com/common/moment.js"></script>
	
	<script>
	var statePermit_count =0;
	var selectedRowForDelete='';
	var checkedKey='';
	var contextPath;
	var userPageAccess= '<%=sAccess%>';	
	var loginUserId;
	var loginCompanyId;
	$(function () {
	    //Datemask dd/mm/yyyy
	    //$("#bookingDate").inputmask("dd/mm/yyyy hh:mm", {"placeholder": "dd/mm/yyyy hh:mm"});
	    $("#bookingDetailsDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	  	$("#startTime").inputmask("hh:mm", {"placeholder": "hh:mm"});
	    $("#pickUpTime").inputmask("hh:mm", {"placeholder": "hh:mm"});
	    $("#bookingDate").val(getTimeStampTo_ddMMyyyyHHmmss(new Date(),'dd/mm/yyyy hh:mm'));
	    $("#bookingDetailsDate").val(getTimeStampTo_DDMMYYYY(new Date()));
	});
	 
	function onLoadPage(){
		$('#dataTable2').html("${bookingList}"); 
		if(<%=session.getAttribute("loginUserId")%>!=null)
			loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
		if(<%=session.getAttribute("loginCompanyId")%>!=null)
			loginCompanyId='<%=(Long) session.getAttribute("loginCompanyId")%>';
		assignRights();
		var contextPath="<%=request.getContextPath()%>";
		$('#MOB option').each(function() {
		      if($(this).text() == 'Credit') {
		          $(this).prop("selected", true);
		      }
	    });
		refreshData();
	}
	</script>
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<form:form commandName="bookingModel" id="bookingFrom" method="post">
		<div class="row">
			<legend><strong>Booking</strong></legend>
			<div class="col-md-10" style="background-color: #F6F6F3;">
				<table style="width: 100%;" id="summuryTable">
				<tr height="25px"> <!-- Summary First Line -->
					<td width=12%><form:label path="bookingNo">Booking No.</form:label></td>
					<td width=15%><form:input path="bookingNo" class="form-control" id="bookingNo" style="width:100%" readOnly='true'></form:input></td>
					<td width=10%><label>Mobile No.</label></td>
					<td width=15%><form:input  path="mobileNo" class="form-control" id="mobileNo" style="width:100%" maxlength="20" onblur="fillMobile()" onkeypress="isNumeric(event)" ></form:input></td>
					
					<td width=10%><form:label path="customerId.id" >Name</form:label></td>
					<td width=15%><form:input  path="customerId.id" class="form-control" id="customerName" style="width:100%" ></form:input>
					    <form:input  path="customerId.id" class="form-control" id="customerId" style="display:none" ></form:input>
					</td>
					<td width=10%><label>Branch</label></td>
					<td width=15%>
						<select class="form-control" id="branchName" style="width:100%" onchange="fillHub()" >
							<option value="0">---Select---</option>
							<c:forEach var="branchId" items="${branchList}">
								<option value = '${branchId.id}'>${branchId.name}</option>
							</c:forEach>	
						</select>
					</td>
				</tr>
				
				<tr height="25px"> 	
				<td><form:label path="bookingDate">Date</form:label></td>
					<td><form:input path="bookingDate" id="bookingDate"  name="bookingDate" type="text" style="width:100%" class="form-control" maxlength="19" readOnly="true"></form:input></td>
					<td><label>Booking Taken</label></td>
					<td><form:input  path="bookingTakenBy" class="form-control" id="bookingTakenBy" style="width:100%" readOnly='true' value='<%=session.getAttribute("loginUser")%>'></form:input></td>
				</tr>
				
				</table>
			</div>
			<div class="col-md-2" style="background-color: #F6F6F3;">
				<table style="width: 100%;">
				<tr>
					<td width=40%><label>Hub </label></td>
					<td width=60%>
					<form:input  path="company" class="form-control" id="company" style="display:none"  ></form:input>
						<select class="form-control" id="hubName" name="hubName" style="width:100%">
									<option value="0">Select</option>
						</select>
					</td>
				</tr>
			</table>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<hr>
				<table style="width: 100%;">
				<tr height="25px"> <!-- Detail First Line -->
					
					<td width=8%><form:label path="pickUpDate">Booking Date</form:label></td>
					<td width=10%>
						<form:input path="pickUpDate" id="bookingDetailsDate"  name="bookingDetailsDate" type="text" style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10"></form:input>
					</td>
					<td width="8%"><label>Start Time</label></td>
					<td width="9%">
						<input type='text' class="form-control" name="startTime" id="startTime" style="width:100%" maxlength="5" data-inputmask="'alias': 'hh:mm'"/>
					</td>
					<td width="8%"><label>Pick Up Time</label></td>
					<td width="9%"><input type='text' class="form-control" name="pickUpTime" id="pickUpTime" style="width:100%" maxlength="5" data-inputmask="'alias': 'hh:mm'"></td>
					
					<td width="8%"><label>Rental Type</label></td>
					<td width="10%">
						<form:select path="rentalType.id" class="form-control" id="rentalType"  onChange="fillTariff()">
							<form:option value="0" label="--- Select ---"/>
							<form:options items="${rentalTypeIdList}" itemValue="id" itemLabel="name"/>	                
						</form:select>
					</td>
					<td width="8%"><label>Tariff Scheme</label></td>
					<td width="8%">
						<select class="form-control" id="tariff" name="tariff" style="width:100%">
									<option value="0">Select</option>
						</select>
					</td>
				</tr>
				<tr height="25px"> <!-- Detail Second Line -->
				<td><label>Last Booking</label></td>
					<td><input type='text' class="form-control" name="lastBooking" id="lastBooking" style="width:100%" maxlength="30" disabled="disabled"/></td>
					<td><label>Car Model</label></td>
					<td>
						<form:select path="carModel.id" class="form-control" id="carModel" name="carModel" style="width:100%">
							<form:option value="0" label="--- Select ---"/>
 						    	<form:options items="${carModelIdList}" itemValue="id" itemLabel="name"/>
 						</form:select>
					</td>
					<td><label>M.O.B</label></td>
					<td >
						<form:select path="mob.id" class="form-control" id="MOB" style="width: 100%; ">
							<form:option value="0" label="--- Select ---"/>
							<form:options items="${MOBList}" itemValue="id" itemLabel="name"/>	                
						</form:select>
					</td>						
				
					<td><label>Terminal</label></td>
					<td>
						<form:select path="terminal" class="form-control" id="terminal" name="terminal" style="width:100%"  disabled="true" >
							<form:option value="0" label="--- Select ---"/>
 						    	<form:options items="${terminalList}" itemValue="id" itemLabel="name"/>
 						</form:select>
					</td>
						<td><label>Flight No.</label></td>
					<td><input type='text' class="form-control" name="flightNo" id="flightNo" style="width:100%" maxlength="30" onkeypress="isAlphaNumeric(event)" disabled="disabled" /></td>								
					
				</tr>
				<tr height="25px">
					
					<td><label >Pick Up Address</label>
					</td>
					<td colspan="2">
					<input type='text' class="form-control" name="reportingAddress" id="reportingAddress" style="width:100%;" maxlength="200"></input>
					</td>
					<td><label style="margin-left: 20px" >Drop At</label></td>
					<td colspan="2"><input type='text' class="form-control" name="toBeReleaseAt" id="toBeReleaseAt" style="width:115%;margin-left: -42px" maxlength="30" onkeypress="isAlphaNumeric(event)" /></td>
					<td colspan=2><label>Special Instruction</label></td>
					<td colspan=2><input type='text' class="form-control" name="instruction" id="instruction" style="width:142%;margin-left:-91px" maxlength="50" onkeypress="isAlphaNumeric(event)"/></td>															
				</tr>
				<tr height=30px>
										
						
					<td colspan="10" align="right">	
						<input type='button'  value='Save Booking'	 class="btn btn-primary" 	id="addButton"    onclick="saveOrUpdateBooking()" style="width:100px;">
						<input type='button'  value='Update' class="btn btn-primary"	id="updateButton" onclick="saveOrUpdateBooking()" disabled style="width:100px">
						<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:100px">
						<input type="hidden" id="idForUpdate" name="idForUpdate" value="0">
						<input type="hidden" id="idForUpdateDetail" name="idForUpdateDetail" value="0">
					</td>
					
				</tr>
				</table>
			</div>
		</div>
		<div class="row">
			<div class="col-md-4">			
				<!-- Error Message Div -->
				<table border=0 width="100%">
					<tr><td colspan=3><div style="width:95%;height:50px;overflow:auto;display:none" id="error" class="error"></div></td></tr>
				</table>

				<!-- Success Message Div -->
				<table border=0 width="100%">
					<tr><td><div style="width:100%;height:20px;display:none" id="info" class="success"></div></td></tr>
				</table>
			</div>
		</div>
		<div class="row">	
			<hr>
			<div class="col-md-12" >
				<div id="dataTable2" style="width:100%;height:300px;"></div>
			</div>
		</div>	
		</form:form>
		</section>
	</div>	
	
	
</BODY>
</HTML>
