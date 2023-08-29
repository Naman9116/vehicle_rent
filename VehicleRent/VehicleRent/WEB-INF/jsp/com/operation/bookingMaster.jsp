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
	<script src= "<%=request.getContextPath()%>/js/com/operation/bookingMaster.js"></script>
	<script src= "<%=request.getContextPath()%>/js/com/common/moment.js"></script>
	
	<script>
	var statePermit_count =0;
	var selectedRowForDelete='';
	var checkedKey='';
	var contextPath;
	var userPageAccess= '<%=sAccess%>';	
	var loginUserId;
	$(function () {
	    //Datemask dd/mm/yyyy
	    //$("#bookingDate").inputmask("dd/mm/yyyy hh:mm", {"placeholder": "dd/mm/yyyy hh:mm"});
	    $("#bookingDetailsDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    $("#DateRangeFrom").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"})
	    $("#DateRangeTo").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"})

	    $("#startTime").inputmask("hh:mm", {"placeholder": "hh:mm"});
	    $("#pickUpTime").inputmask("hh:mm", {"placeholder": "hh:mm"});
	    $("input[name=optionsRadios]").change(function(){
    		$('#clientName').toggle();
    		$('#otherClient').toggle();	
	    });
	    $("input[name=opReporting]").change(function(){
    		$('#officeAddress').toggle();
    		$('#reportingAddress').toggle();	
	    });
	    $("#bookingDate").val(getTimeStampTo_ddMMyyyyHHmmss(new Date(),'dd/mm/yyyy hh:mm'));
	    $("#bookingDetailsDate").val(getTimeStampTo_DDMMYYYY(new Date()));
	});
	 
	function onLoadPage(){
		if(<%=session.getAttribute("loginUserId")%>!=null)
			loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
		assignRights();
		var contextPath="<%=request.getContextPath()%>";
		resetData();
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
	<form:form commandName="bookingMasterModel" id="bookingMasterFrom" method="post">
		<div class="row">
			<legend><strong>Booking</strong></legend>
			<div class="col-md-10" style="background-color: #F6F6F3;">
				<table style="width: 100%;" id="summuryTable">
				<tr height="25px"> <!-- Summary First Line -->
					<td width=12%><form:label path="bookingNo">Booking No.</form:label></td>
					<td width=15%><form:input path="bookingNo" class="form-control" id="bookingNo" style="width:100%" readOnly='true'></form:input></td>
					<td width=8%><form:label path="bookingDate">Date</form:label></td>
					<td width=10%><form:input path="bookingDate" id="bookingDate"  name="bookingDate" type="text" style="width:100%" class="form-control" maxlength="19" readOnly="true"></form:input></td>
					<td width=10%><form:label path="corporateId.id">Corporate Name</form:label></td>
					<td width=15%><form:select path="corporateId.id" class="form-control" id="corporateId" style="width:100%" onChange="fillValues(this.value)">
							<form:option value="0" label="--- Select ---"/>
							<form:options items="${customerOrCompanyNameIdList}" itemValue="id" itemLabel="name"/>	                
						</form:select>
					</td>
					<td width=10%><form:label path="bookedBy.id">Booked By</form:label></td>
					<td width=15%>
						<form:select path="bookedBy.id" class="form-control" id="bookedBy" style="width:100%" onChange="fillBookerEmail()">
							<form:option value="0" label="--- Select ---"/>
						</form:select>
						<input path="bookingTakenBy" type="hidden" id="bookingTakenBy" value='<%=session.getAttribute("loginUserName")%>'></input>
					</td>
				</tr>
				<tr height="25px"> 	
					<td><form:label path="bookedFor"><input name="optionsRadios" id="opClient" checked type="radio"> Client&nbsp;&nbsp;<input name="optionsRadios" id="opOther"  type="radio"> Other </form:label>
					</td>
					<td>
						<form:select path="bookedForName" class="form-control" id="clientName"  style="width:100%" onChange="fillClientMobile()">
							<form:option value="0" label="--- Select ---"/>
						</form:select>
						<input id="otherClient"  name="otherClient" type="text" style="width:100%;display:none;" class="form-control" maxlength="50"></input>
						<input path="bookedFor" type="hidden" id="bookedFor"></input>
					</td>				
					<td><label>Mobile No.</label></td>
					<td><form:input  path="mobileNo" class="form-control" id="mobileNo" style="width:100%" maxlength="20" ></form:input></td>
					<td><label>Reference No.</label></td>
					<td><form:input  path="referenceNo" class="form-control" id="referenceNo" style="width:100%" maxlength="20" ></form:input></td>
					<td><label>Booking Taken</label></td>
					<td><form:input  path="bookingTakenBy" class="form-control" id="bookingTakenBy" style="width:100%" readOnly='true' value='<%=session.getAttribute("loginUser")%>'></form:input></td>
				</tr>
				<tr height="25px">
					<td><label>SMS - Client</label> <form:input path="smsToClient" type="checkbox" checked="checked"/></td>
					<td><label>SMS - Booker</label> <form:input path="smsToBooker" type="checkbox"/></td>
					<td><label>SMS - Other</label></td>
					<td><form:input path="smsToOther" type="text" class="form-control" maxlength="20"/></td>
					<td><label>Mail - Client</label> <form:input path="emailToClient" type="checkbox"/></td>
					<td><label>Mail - Booker</label> <form:input path="emailToBooker" type="checkbox" checked="checked"/></td>
					<td><label>Mail - Other</label></td>
					<td><form:input path="emailToOther" type="text" class="form-control" maxlength="50"/></td>
				</tr>
				</table>
			</div>
			<div class="col-md-2" style="background-color: #E4E4D7;">
				<table style="width: 100%;">
				<tr>
					<td width=40%><label>Search On </label></td>
					<td width=60%>
						<select class="form-control" name="searchName" id="searchName" style="width:100%">
							<option value="M">Mobile No.</option>
							<option value="B">Booking No.</option>
							<option value="N">Client Name</option>
						</select>
					</td>
				</tr>
				<tr>	
					<td><label>Value </label></td>
					<td><input type="text" id="searchValue" name ="searchValue" class="form-control" maxlength="50"/></td>
				</tr>
				<tr>
					<td colspan=2><a href='javascript:prevRecord()'><img src='<%=request.getContextPath()%>/img/previousButton.png' border='0' title='Previous Booking' width='30' height='20'/></a>
						<input type='text' id="recPos" name='recPos' value='0/0' style='width:45px;border:0' readOnly='true' />
						<a href='javascript:nextRecord()'><img src='<%=request.getContextPath()%>/img/nextButton.png' border='0' title='Next Booking' width='30' height='20'/></a>
						<input type='button'  value='Search'	 class="btn btn-primary" 	id="searchButton"    onclick="SearchBooking()" style="width:33%">
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
					<td width=10%><label>Branch</label></td>
					<td width=15%>
						<input id="companyName"  name="companyName" type="hidden" maxlength="50"></input>
						<input id="companyCode"  name="companyCode" type="hidden" maxlength="10"></input>
						<select class="form-control" name="branchName" id="branchName" style="width:100%" onChange="fillHub(this.value)">
							<option value="0" label="--- Select ---"/>
						</select>
					</td>
					<td width=10%><label>Hub</label></td>
					<td width=15%>
						<select class="form-control" name="hubName" id="hubName" style="width:100%">
							<option value="0" label="--- Select ---"/>
						</select>
					</td>
					<td width=8%><form:label path="bookingDetailModel[0].pickUpDate">Booking Date</form:label></td>
					<td width=10%>
						<form:input path="bookingDetailModel[0].pickUpDate" id="bookingDetailsDate"  name="bookingDetailsDate" type="text" style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10"></form:input>
					</td>
					<td width=8%><label>Start Time</label></td>
					<td width=7%>
						<input type='text' class="form-control" name="startTime" id="startTime" style="width:100%" maxlength="5" data-inputmask="'alias': 'hh:mm'"/>
					</td>
					<td width=8%><label>Pick Up Time</label></td>
					<td width=7%><input type='text' class="form-control" name="pickUpTime" id="pickUpTime" style="width:100%" maxlength="5" data-inputmask="'alias': 'hh:mm'"></td>
				</tr>
				<tr height="25px"> <!-- Detail Second Line -->
					<td><label>Rental Type</label></td>
					<td>
						<form:select path="bookingDetailModel[0].rentalType.id" class="form-control" id="rentalType" style="width:100%" onChange="fillTariff(this.value)">
							<form:option value="0" label="--- Select ---"/>
							<form:options items="${rentalTypeIdList}" itemValue="id" itemLabel="name"/>	                
						</form:select>
					</td>							 					
					<td><label>Tariff Scheme</label></td>
					<td>
						<form:select path="bookingDetailModel[0].tariff.id" class="form-control" id="tariff" onChange="fillCarModel()" style="width:100%">
							<form:option value="0" label="--- Select ---"/>
						</form:select>
					</td>
					<td><label>Car Model</label></td>
					<td>
						<form:select path="bookingDetailModel[0].carModel.id" class="form-control" id="carModel" name="carModel" style="width:100%">
							<form:option value="0" label="--- Select ---"/>
<%-- 							<form:options items="${carModelIdList}" itemValue="id" itemLabel="name"/>
 --%>						</form:select>
					</td>				
					<td><label>Flight No.</label></td>
					<td><input type='text' class="form-control" name="flightNo" id="flightNo" style="width:100%" maxlength="30" onkeypress="isAlphaNumeric(event)" /></td>
					<td><label>Terminal</label></td>
					<td>
						<select class="form-control" id="terminal" name="terminal" style="width:100%">
							<option value="0" label="--- Select ---"/>
						</select>
					</td>
				</tr>
				<tr height="25px">
					<td><label>M.O.B</label></td>
					<td>
						<form:select path="bookingDetailModel[0].mob.id" class="form-control" id="MOB" style="width:100%">
							<form:option value="0" label="--- Select ---"/>
							<form:options items="${MOBList}" itemValue="id" itemLabel="name"/>	                
						</form:select>
					</td>										
					
					<td><label><input name="opReporting" id="opOffice" checked type="radio"> Office &nbsp;
							   <input name="opReporting" id="opReport"  type="radio"> Other</label>
					</td>
					<td colspan=2><select class="form-control" id="officeAddress" name="officeAddress" style="width:100%">
							<option value="0" label="--- Select ---"/>
						</select>
						<input type='text' class="form-control" name="reportingAddress" id="reportingAddress" style="width:100%;display:none;" maxlength="200"></input>
					</td>
					<td><label>Drop At</label></td>
					<td colspan=4><input type='text' class="form-control" name="toBeReleaseAt" id="toBeReleaseAt" style="width:100%" maxlength="100" onkeypress="isAlphaNumeric(event)" /></td>
				</tr>
				<tr height=30px>
					<td><label>Special Instruction</label></td>
					<td colspan=2><input type='text' class="form-control" name="instruction" id="instruction" style="width:100%" maxlength="100" onkeypress="isAlphaNumeric(event)"/></td>																				
					<td align="center">	
						<input type='button'  value='Add Booking'	 class="btn btn-primary" 	id="addButton"    onclick="addBooking('O')" style="width:120px">
					</td>
					<td>	
						<input type='button'  value='Add Passenger'	 class="btn btn-primary" 	id="addPassengerButton" disabled="disabled"    onclick="showPassengerInfoPanel()" style="width:120px;display:none">
					</td>		
					<td colspan=7 align="right">	
						<input type='button'  value='Save Booking'	 class="btn btn-primary" 	id="saveButton"    onclick="saveBooking('N')" style="width:100px;">
						<input type='button'  value='Save As New Booking'	 class="btn btn-primary" 	id="saveAsNewButton"    onclick="saveBooking('O')" style="width:130px;display:none;">
						<input type='button'  value='Update' class="btn btn-primary"	id="updateButton" onclick="updateBooking('O','')" disabled style="width:100px">
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
	
	<div id="multiBookingOption" class="modal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-sm"  >
   			<div class="modal-content">
   				<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
   					<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;'>Multiple Bookings
       				<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:200%; color: #196780;">&times;</button></h4>
       			</div>
	   			<div class="modal-body" id="editDetailRecordPanel_body">
				<div class="row" style="height:35px;">
					<div class="col-md-1"></div>
					<div class="col-md-5"><input name="multipleOption" id="opDateRange" type="radio"  checked="checked" /> <label>Date Range </label></div>
				</div>
				<div class="row" style="height:35px;">
					<div class="col-md-3"></div>
					<div class="col-md-2"><label>From</label></div>
					<div class="col-md-4"><input type='text' class="form-control" name="DateRangeFrom" id="DateRangeFrom" style="width:100%" maxlength="10" data-inputmask="'alias': 'dd/mm/yyyy'" disabled="true"/></div>
				</div>
				<div class="row" style="height:35px;"  id="dvRangeTo">
					<div class="col-md-3"></div>
					<div class="col-md-2"><label>To</label></div>
					<div class="col-md-4"><input type='text' class="form-control" name="DateRangeTo" id="DateRangeTo" style="width:100%" maxlength="10" data-inputmask="'alias': 'dd/mm/yyyy'"/></div>
				</div>	
				<div class="row" style="height:35px;">
					<div class="col-md-1"></div>
					<div class="col-md-5"><input name="multipleOption" id="opNoCars"  type="radio" /><label> Car Type</label></div>
				</div>	
				<div class="row" style="height:40px;" id="dvCarNo">
					<div class="col-md-2"></div>
					<div class="col-md-3"><label>No.Of Cars</label></div>
					<div class="col-md-4"><input type='text' class="form-control" name="NoOfCars" id="NoOfCars" disabled="disabled" style="width:100%" maxlength="5"></div>
		   		</div>
				<div class="row">
					<div class="col-md-4"></div>
					<div class="col-md-2">
						<input type='button' value='Ok' class="btn btn-primary" id="optionValue"  data-dismiss="modal" onClick="callFromModel()" style="width:100px">
					</div>
					<div class="col-md-5"></div>
				</div>
			</div>
		</div>	
	</div>
	</div>
<!-- 	Passenger Info Model -->

<div id="passengerInfoPanel" class="modal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg" style="width:617px;height: 235px">
   			<div class="modal-content" >
   				<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
   					<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;'>Passenger Details
       				<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:200%; color: #196780;">&times;</button></h4>
       			</div>
	   			<div class="modal-body" id="passengerInfoPanel_body">
				<div class="row">
				   <div class="col-md-12" style="width:617px;height: 235px;overflow-y:auto;overflow-x:hidden">
					<table  id="passengerTable" border="1" >
						<tr bgcolor="#FFDA71" style="font-family: cambria;font-size: 14; height: 10px;">
							<th>Add</th>
							<th>Name.</th>
							<th>Mobile No</th>
							<th>Age.</th>
							<th>Sex</th>
							<th>Id</th>
						</tr>
						<tr>
							<td><input type="button" id="btnAdd"  class="btn btn-primary" value="+" onclick="addNewRow()"/></td>
							<td><input type="text" class="form-control" id="passengerName_1"/></td>
							<td><input type="text" class="form-control mob" id="passengerMob_1" maxlength="13" onkeypress='isNumeric(event)'/></td>
							<td><input type="text" class="form-control" id="passengerAge_1" onkeypress='isNumeric(event)'/></td>
							<td>
								<select class="form-control" id="passengerSex_1" name="passengerSex" >
									<option value="Male">Male</option>
									<option value="Female">Female</option>
									<option value="other">Other</option>
								</select>
							</td>
							<td><input type="text" class="form-control" id="passengerId_1" /></td>
						</tr>
					</table>
				
					
				</div>
			</div>	
			 <div class="row">
						 <div class="col-md-4"></div>
						 <div class="col-md-2">
							<input type='button' value='Ok' class="btn btn-primary" id="savePassenger" style="margin-top:29px;width:100px;margin-left: 291px" data-dismiss="modal" style="width:100px">
						</div>
						 <div class="col-md-5"></div>
					</div>
		</div>
	</div>	
</div>
	</div>
</BODY>
</HTML>
