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
	<script type='text/javascript' src="<%=request.getContextPath()%>/js/bootstrap/bootbox.min.js"></script>
	<script src= "<%=request.getContextPath()%>/js/com/operation/jobWorks.js"></script>
	<script src= "<%=request.getContextPath()%>/js/com/common/moment.js"></script>
	<script>

	var statePermit_count =0;
	var selectedRowForDelete='';
	var checkedKey='';
	var contextPath;
	var userPageAccess= '<%=sAccess%>';									
	var loginUserId;
	var jobType="webBooking"
$(function () {
	    //Datemask dd/mm/yyyy
	    $("#bookingFromDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    $("#bookingToDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    $("#date").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});	    
	    $("#pickupDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    $("#reserveDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    $("#pickupTime").inputmask("hh:mm", {"placeholder": "hh:mm"});
	    $("#startTime").inputmask("hh:mm", {"placeholder": "hh:mm"});
	    $("#time").inputmask("hh:mm", {"placeholder": "hh:mm"});
		$("#sysdate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});;
	    $("#systime").inputmask("hh:mm", {"placeholder": "hh:mm"});
		$("#bookingFromDate").val(getTimeStampTo_DDMMYYYY(new Date()));
		$("#bookingToDate").val(getTimeStampTo_DDMMYYYY(new Date()));
		
		 $("input[name=optionsRadios]").change(function(){
	    		$('#clientName').toggle();
	    		$('#otherClient').toggle();	
		    });
		    $("input[name=opReporting]").change(function(){
	    		$('#officeAddress').toggle();
	    		$('#reportingAddress').toggle();	
		    });
	    	    	    
	 
	});
	 
	function onLoadPage(){
	if(<%=session.getAttribute("loginUserId")%>!=null)
			loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
		assignRights();
		var contextPath="<%=request.getContextPath()%>";
		searchBooking();
	}
	</script>
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<form id="bookingMasterForm" method="post">
		<div class="row">
		<legend><strong>Web Booking</strong></legend>
			<div class="col-md-12" style="background-color: #F6F6F3;">
			
		
				<table style="width: 100%;">
				<tr height="25px"> <!-- First Line -->
					<td style="display: none; width: 8%"><label>From Date</label></td>
					<td style="display: none; width: 10%"><input type="text" id="bookingFromDate"  name="bookingFromDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control"  maxlength="10"></input></td>
					
					<td width=10%><label>Corporate Name</label></td>
					<td width=18%>
						<select class="form-control" id="corporateId" style="width:100%" onChange="fillValues(this.value)">
							<option value="0">ALL</option>
							<c:forEach var="customerOrCompanyNameId" items="${customerOrCompanyNameIdList}">
								<option value = '${customerOrCompanyNameId.id}'>${customerOrCompanyNameId.name}</option>
							</c:forEach>	
						</select>
					</td>
					
					<td width=8%><label>Car Type</label></td>
					<td width=10%>
						<select class="form-control" id="carModel" name="carModel" style="width:100%">
							<option value="0">ALL</option>
							<c:forEach var="carModelId" items="${carModelIdList}">
								<option value = '${carModelId.id}'>${carModelId.name}</option>
							</c:forEach>	
						</select>
					</td>
					
					<td width=8%><label>Booking Mode</label></td>
					<td width=10%>
						<select class="form-control" id="MOB" style="width:100%">
							<option value="0">ALL</option>
							<option value="1">PrePaid</option>
							<option value="2">PostPaid</option>
						</select>
					</td>										
					
					<td width=8%><label>Rental Type</label></td>
					<td width=10%>
						<select class="form-control" id="rentalType" style="width:100%">
							<option value="0">ALL</option>
							<c:forEach var="rentalTypeId" items="${rentalTypeIdList}">
								<option value = '${rentalTypeId.id}'>${rentalTypeId.name}</option>
							</c:forEach>	
						</select>
					</td>
				</tr>
				<tr>
					<td style="display: none; width: 8%"><label>To Date</label></td>
					<td style="display: none; width: 10%"><input type="text" id="bookingToDate"  name="bookingToDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10"></input></td>
					
					<td width=10%><label>Branch</label></td>
					<td width=18%>
						<select class="form-control" id="branch" style="width:100%" onChange="fillCurrentHub(this.value)">
							<option value="0">ALL</option>
							<c:forEach var="branch" items="${branchList}">
								<option value = '${branch.id}'>${branch.name}</option>
							</c:forEach>	
						</select>
					</td>
					
					<td width=8%><label>Hub</label></td>
					<td width=10%>
						<select class="form-control" id="outlet" style="width:100%">
							<option value="0">ALL</option>							
						</select>
					</td>
					
					<td colspan=4 align="right">
						<input type='button'  value='Search'	 class="btn btn-primary" 	id="searchButton"    onclick="searchBooking()" style="width:100px">
						<input type='button'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:100px">
						</td>										
				</tr>				
				</table>				
			<table width=100%>
					<tr>
						<td width=35%><div style="width:95%;height:30px;overflow:auto;display:none" id="error" class="error"></div></td>
						<td width=35%><div style="width:100%;height:20px;display:none" id="info" class="success"></div></td>
					</tr>
				</table>
				<div class="row"> 
					<div class="col-md-12" style="width: auto; float: right; margin-right: 10px;"><label style="color : black;  font-family: cambria;"><font color = "red"  style="font-weight : bold; font-family: Bookman Old Style;">Rental Type : </font>&nbsp;
				   	  <c:forEach var="rentalTypeId" items="${rentalTypeIdList}">
				        	<b>${rentalTypeId.name}</b>&nbsp;[&nbsp;<span style="color: red ; font-size : 16px; font-weight : bold;" id="${rentalTypeId.id}"></span>&nbsp;]&nbsp;&nbsp;
				       </c:forEach> </label>
				   </div>
				</div>
				<hr>
			</div>
		</div>
		<div class="row">	
		<div class="col-md-12" >
			<div id="dataTable2" style="width:100%;overflow:auto;"></div>
		</div>
		</div>

<div id=bookingCancelPanel class="modal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="bookingCancelLabel" aria-hidden="true">
	<div class="modal-dialog modal-sm">
   		<div class="modal-content">
   			<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
   				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;' id="bookingCancelLabel">Allow Web Booking
       			<button type="button" class="close" data-dismiss="modal" aria-hidden="true" onclick="hideAllowPermission()" style="font-size:150%; color: #196780;">&times;</button></h4>
       		</div>
   			<div class="modal-body" id="bookingCancelPanel_body">
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-4"><label>Permission</label></div>	
						<div class="col-md-3"></div>				
					<div class="col-md-2" id="allowPermissionDiv"><input name="permission" id="allowPermission" checked type="radio" value="A" onclick="showCancelReason()" /> <font size="1">Allow</font></div> 
				    <div class="col-md-1" id="cancelPermissionDiv"><input name="permission" id="cancelPermission" type="radio" value="C"  onclick="showCancelReason()"/> <font size="1">Cancel</font></div>
				  	<div class="col-md-1"></div>
				</div>   
				<br/>	  			
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"></div>
					<div class="col-md-7"><textarea id="cancelReason" name="cancelReason" class="form-control" cols="8" rows="2" placeholder="Reason of Cancellation..." disabled="disabled"></textarea></div>
					<div class="col-md-1"></div>
				</div> 
				<br/>	
				<div class="row">
					<div class="col-sm-8">
						<div id="cancelReasonError" style="color:red;height: auto;font-size:11px;overflow-y:auto;display:none;"></div>
						<div id="cancelReasonInfo" style="color:green;display:none;"></div>
					</div>
					<div class="col-sm-1"><input type='button' value='Save' class="btn btn-primary" id="saveCR"  onClick="approvedWebBooking()" style="width:80px; color: white;"/></div>
					<div class="col-sm-1"></div>
				</div>	
	   		</div>
    	 </div>
     	</div>
     </div>
     
<!--      EDIT Booking Panel -->
<div id="editDetailRecordPanel" class="modal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-md">
   		<div class="modal-content">
   			<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
   				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;'>Permission
       			<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:150%; color: #196780;">&times;</button></h4>
       		</div>
   			<div class="modal-body" id="editDetailRecordPanel_body">
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>PickUpDate</label></div>
					<div class="col-md-5"><input type="text" id="pickupDate" name="pickupDate" data-inputmask="'alias': 'dd/mm/yyyy'" maxlength="10" class="form-control"></input></div>
					<div class="col-md-1"></div>
				</div>   			
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Start Time</label></div>
					<div class="col-md-5"><input type='text' class="form-control" name="startTime" id="startTime" style="width:100%" maxlength="5" data-inputmask="'alias': 'hh:mm'"/></div>
					<div class="col-md-1"></div>
				</div>   
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>PickUp Time</label></div>
					<div class="col-md-5"><input type="text" id="pickupTime" name="pickupTime" maxlength="5" data-inputmask="'alias': 'hh:mm'" class="form-control"/></div>
					<div class="col-md-1"></div>
				</div>   			
   			
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Corporate Name</label></div>
					<div class="col-md-5"><select class="form-control" id="corporateName" style="width:100%" onChange="fillValues(this.value)">
							<option value="0">---Select---</option>
							<c:forEach var="customerOrCompanyNameId" items="${customerOrCompanyNameIdList}">
								<option value = '${customerOrCompanyNameId.id}'>${customerOrCompanyNameId.name}</option>
							</c:forEach>	
						</select>
					</div>
					<div class="col-md-1"></div>
				</div>   			

				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Booked By</label></div>
					<div class="col-md-5">
						<select name="bookedBy" class="form-control" id="bookedBy" style="width:100%">
							<option value="0" label="--- Select ---"/>
						</select>
					</div>
					<div class="col-md-1"></div>
				</div>   			
   			
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label><input name="optionsRadios" id="opClient"  checked type="radio"/> Client&nbsp;<input name="optionsRadios" id="opOther" type="radio"/> Other </label></div>
					<div class="col-md-5">
						<select name="bookedForName" class="form-control" id="clientName"  style="width:100%;" onChange="fillClientMobile()">
							<option value="0" label="--- Select ---"/>
						</select>
						<input id="otherClient"  name="otherClient" type="text" style="width:100%;display:none;" class="form-control" maxlength="50"></input>
						<input name="bookedFor" type="hidden" id="bookedFor"></input>
					</div>
					<div class="col-md-1"></div>
				</div>   	
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Client Mobile No</label></div>
					<div class="col-md-5"><input type="text" id="mobileNo" name="mobileNo" class="form-control"/></div>
					<div class="col-md-1"></div>
				</div>   			
						
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label><input name="opReporting" id="opOffice" checked type="radio"> Office &nbsp;<input name="opReporting" id="opReport" type="radio"> Other</label></div>
					<div class="col-md-5">
						<select class="form-control" id="officeAddress" name="officeAddress" style="width:100%;display:block;">
							<option value="0" label="--- Select ---"/>
						</select>
						<input type='text' class="form-control" name="reportingAddress" id="reportingAddress" style="width:100%;display:none;" maxlength="200"></input>
					</div>
					<div class="col-md-1"></div>
				</div>   			

				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>DropAt</label></div>
					<div class="col-md-5"><input type="text" id="toBeReleaseAt" name="toBeReleaseAt" class="form-control" maxlength="200" onkeypress="isAlphaNumeric(event)"></input></div>
					<div class="col-md-1"></div>
				</div>   			

				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Spl.Instruction</label></div>
					<div class="col-md-5"><input type="text" id="instruction" name="instruction" class="form-control" maxlength="50" onkeypress="isAlphaNumeric(event)"></input></div>
					<div class="col-md-1"></div>
				</div>   			

<!-- Changes -->

				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Rental Type</label></div>
					<div class="col-md-5">
						<select class="form-control" id="rentalTypeModel" style="width:100%" onChange="fillTariff(this.value)">
							<option value="0">---Select---</option>
							<c:forEach var="rentalTypeId" items="${rentalTypeIdList}">
								<option value = '${rentalTypeId.id}'>${rentalTypeId.name}</option>
							</c:forEach>	
						</select>
					</div>
					<div class="col-md-1"></div>
				</div>

				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5">
						<label>Tariff Scheme</label>
					</div>
					<div class="col-md-5">
						<select class="form-control" id="tariff" 	style="width: 100%">
							<option value="0">---Select---</option>
							<%-- <c:forEach var="rentalTypeId" items="${rentalTypeIdList}">
								<option value='${rentalTypeId.id}'>${rentalTypeId.name}</option>
							</c:forEach> --%>
						</select>
					</div>
					<div class="col-md-1"></div>
				</div>
													
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Car Type</label></div>
					<div class="col-md-5">
						<select class="form-control" id="carType" style="width:100%">
							<option value="0">---Select---</option>
							<%-- <c:forEach var="carModelId" items="${carModelIdList}">
								<option value = '${carModelId.id}'>${carModelId.name}</option>
							</c:forEach>	 --%>
						</select>
					</div>
					<div class="col-md-1"></div>
					<br>
					<br>
				</div>
				
				   <div class="row">
				    <div class="col-md-1"></div> 
					<div class="col-md-3"><label>Send Sms </label></div>
			        <div class="col-md-2"><label class="checkbox-inline"><input name="smsClient" id="smsClient" type="checkbox" checked = "checked" /> Sms-Client</label></div>
				   	<div class="col-md-2"><label class="checkbox-inline" ><input name="smsBooker" id="smsBooker" type="checkbox" /> Sms-Booker</label></div>
				   	<div class="col-md-1"><label>other</label></div>
				   	<div class="col-md-2"><input type="text" id="dispatchSmsOther" name="dispatchSmsOther" class="form-control" onkeypress="return isNumber(event)" ></div>
				   	<div class="col-md-1"><input type="hidden" value="0" id="dutySlipIdForUpdate"/></div>
			   </div>  
			   <hr>
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5" id="allowPermissionDiv"><input name="permission" id="allowPermission"  type="radio" value="A" onclick="showCancelReason(value)" /> <font size="3" color="green">Allow</font></div> 
				    <div class="col-md-5" id="cancelPermissionDiv"><input name="permission" id="cancelPermission" type="radio" value="C" onclick="showCancelReason(value)"/> <font size="3" color="red">Cancel</font></div>
				  	<div class="col-md-1"></div>
				</div> 
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"></div>
					<div class="col-md-5"><textarea id="cancelReason1" name="cancelReason1" class="form-control" cols="8" rows="2" placeholder="Reason of Cancellation..." style="display: none"></textarea></div>
					<div class="col-md-1"></div>
				</div> 
				
				

				<div class="row">
					<div class="col-md-6">
						<div id="errorValidate" style="color:red;height:70px;font-size:11px;overflow-y:auto;display:none;">
						</div>
						<div id="errorValidate" style="color:green;display:none;">
						</div>
					</div>
					<div class="col-md-2"><input type='button' value='Ok' class="btn btn-primary" id="optionValue" onClick="setModelToTable()" style="width:100px"/></div>
					<div class="col-md-1"></div>
					<div class="col-md-2"><input type='button' value='Close' class="btn btn-primary" id="optionValue" onClick="hideAllowPermission()" style="width:100px"/></div>
					<div class="col-md-1"></div>
				</div>	
	   		</div>
    	 </div>
     	</div>
     </div>

	</form>
	</section>
</div>	

     
</BODY>
</HTML>
