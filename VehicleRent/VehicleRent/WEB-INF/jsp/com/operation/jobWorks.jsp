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
	<script src= "<%=request.getContextPath()%>/js/com/operation/jobWorks.js"></script>
	<script src= "<%=request.getContextPath()%>/js/com/common/moment.js"></script>
	<script>

	var statePermit_count =0;
	var selectedRowForDelete='';
	var checkedKey='';
	var contextPath;
	var userPageAccess= '<%=sAccess%>';									
	var loginUserId;
	var jobType = '${jobType}';
	
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

		if(jobType == 'Advance'){
			var someFormattedDate = addDays(getTimeStampTo_DDMMYYYY(new Date()),1);
			$("#bookingFromDate").val(someFormattedDate);
			$("#bookingToDate").val(someFormattedDate);
			$("#reserveDate").val(someFormattedDate);
	    }else{
			$("#bookingFromDate").val(getTimeStampTo_DDMMYYYY(new Date()));
		    $("#bookingToDate").val(getTimeStampTo_DDMMYYYY(new Date()));
	    }	    	    
	    $("input[name=optionsRadios]").change(function(){
    		$('#clientName').toggle();
    		$('#otherClient').toggle();	
	    });
	    $("input[name=opReporting]").change(function(){
    		$('#officeAddress').toggle();
    		$('#reportingAddress').toggle();	
	    });
	     $("#sysdate").val(getTimeStampTo_DDMMYYYY(new Date()));
	     updateTime();
//		setInterval(searchBooking,30000);
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
		<legend><strong>${jobType} Jobs</strong></legend>
			<div class="col-md-12" style="background-color: #F6F6F3;">
			
			<c:if test="${jobType=='Current'}">
				<table style="width: 100%;">
				<tr height="25px"> <!-- First Line -->
					<td width=8%><label>From Date</label></td>
					<td width=10%><input type="text" id="bookingFromDate"  name="bookingFromDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" readOnly = "readonly" maxlength="10"></input></td>
					
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
					<td width=8%><label>To Date</label></td>
					<td width=10%><input type="text" id="bookingToDate"  name="bookingToDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" readOnly = "readonly" maxlength="10"></input></td>
					
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
						<input type='button'   value='Update'	 class="btn btn-primary"    id="updateButton"  onclick="updateSaveBooking()" style="width:100px">
					</td>										
				</tr>				
				</table>				
			</c:if>
			
			<c:if test="${jobType=='Advance'}">
				<table style="width: 100%;">
				<tr height="25px"> <!-- First Line -->
					<td width=8%><label>From Date</label></td>
					<td width=10%><input type="text" id="bookingFromDate"  name="bookingFromDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10" ></input></td>
					
					<td width=10%><label>Corporate Name</label></td>
					<td width=14%>
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
					
					<td width=8%><label>Job Status </label></td>
					<td width=10%>
						<select class="form-control" id="jobStatus" name = "jobStatus" style="width:100%">
							<option value="0">ALL</option>
							<option value="N">Advance</option>
							<option value="R">Reseved</option>
							<%-- <c:forEach var="jobStatus" items="${jobStatusList}">
								<option value = '${jobStatus.id}'>${jobStatus.name}</option>
							</c:forEach>	 --%>
						</select>
					</td>
					
					<td width=8%><label>Rental Type</label></td>
					<td width=14%>
						<select class="form-control" id="rentalType" style="width:100%">
							<option value="0">ALL</option>
							<c:forEach var="rentalTypeId" items="${rentalTypeIdList}">
								<option value = '${rentalTypeId.id}'>${rentalTypeId.name}</option>
							</c:forEach>	
						</select>
					</td>	
				</tr>
				<tr height="30px">	
					<td><label>To Date</label></td>
					<td><input type="text" id="bookingToDate"  name="bookingToDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10"></input></td>
									
					<td><label>Branch</label></td>
					<td>
						<select class="form-control" id="branch" style="width:100%" onChange="fillCurrentHub(this.value)">
							<option value="0">ALL</option>
							<c:forEach var="branch" items="${branchList}">
								<option value = '${branch.id}'>${branch.name}</option>
							</c:forEach>	
						</select>
					</td>
					
					<td><label>Hub</label></td>
					<td>
						<select class="form-control" id="outlet" style="width:100%">
							<option value="0">ALL</option>							
						</select>
					</td>
					
					<td><label>Booking Mode</label></td>
					<td>
						<select class="form-control" id="MOB" style="width:100%">
							<option value="0">ALL</option>
							<option value="1">PrePaid</option>
							<option value="2">PostPaid</option>
						</select>
					</td>
					
					<td colspan=2 align="right">
						<input type='button'  value='Search'	 class="btn btn-primary" 	id="searchButton"    onclick="searchBooking()" style="width:70px">
						<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:70px">
						<input type='button'   value='Update'	 class="btn btn-primary"    id="updateButton"  onclick="updateSaveBooking()" style="width:70px">
					</td>										
				</tr>
				</table>
			</c:if>
				
			<c:if test="${jobType=='Status'}">
				<table style="width: 100%;">
				<tr height="25px"> <!-- First Line -->
					<td width=8%><label>From Date</label></td>
					<td width=10%><input type="text" id="bookingFromDate"  name="bookingFromDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10"></input></td>
					
					<td width=10%><label>Corporate Name</label></td>
					<td width=14%>
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
					
					<td width=8%><label>Job Status </label></td>
					<td width=10%>
						<select class="form-control" id="jobStatus" name = "jobStatus" style="width:100%">
							<option value="0">ALL</option>
							<option value="A">Allocated</option>
							<option value="D">Dispatched</option>
							<option value="R">Reserved</option>							
						</select>
					</td>
					
					<td width=8%><label>Rental Type</label></td>
					<td width=14%>
						<select class="form-control" id="rentalType" style="width:100%">
							<option value="0">ALL</option>
							<c:forEach var="rentalTypeId" items="${rentalTypeIdList}">
								<option value = '${rentalTypeId.id}'>${rentalTypeId.name}</option>
							</c:forEach>	
						</select>
					</td>	
				</tr>
				<tr height="30px">	
					<td><label>To Date</label></td>
					<td><input type="text" id="bookingToDate"  name="bookingToDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10"></input></td>
									
					<td><label>Branch</label></td>
					<td>
						<select class="form-control" id="branch" style="width:100%" onChange="fillCurrentHub(this.value)">
							<option value="0">ALL</option>
							<c:forEach var="branch" items="${branchList}">
								<option value = '${branch.id}'>${branch.name}</option>
							</c:forEach>	
						</select>
					</td>
					
					<td><label>Hub</label></td>
					<td>
						<select class="form-control" id="outlet" style="width:100%">
							<option value="0">ALL</option>							
						</select>
					</td>
					
					<td><label>Booking Mode</label></td>
					<td>
						<select class="form-control" id="MOB" style="width:100%">
							<option value="0">ALL</option>
							<option value="1">PrePaid</option>
							<option value="2">PostPaid</option>
						</select>
					</td>
					<td><label>Car No.</label></td>
					<td>
						<select class="form-control" id="carRegNo" name="carRegNo" style="width:100%">
							<option value="0">ALL</option>
							<c:forEach var="carRegNo" items="${carRegNoList}">
								<option value = '${carRegNo.registrationNo}'>${carRegNo.registrationNo}</option>
							</c:forEach>	
						</select>
					</td>
				<tr>
					<td colspan=8></td>
					<td colspan=2 align="right">
						<input type='button'  value='Search'	 class="btn btn-primary" 	id="searchButton"    onclick="searchBooking()" style="width:80px">
						<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:80px">
					</td>
				</tr>
				</table>
			</c:if>

			<c:if test="${jobType=='Cancelled'}">
				<table style="width: 100%;">
				<tr height="25px"> <!-- First Line -->
					<td width=8%><label>From Date</label></td>
					<td width=10%><input type="text" id="bookingFromDate"  name="bookingFromDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10"></input></td>
					
					<td width=10%><label>Corporate Name</label></td>
					<td width=14%>
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
					
					<td width=8%><label>Job Status </label></td>
					<td width=10%>
						<select class="form-control" id="jobStatus" name = "jobStatus" style="width:100%">
							<option value="0">ALL</option>
							<option value="C">Cancelled</option>
							<%-- <c:forEach var="jobStatus" items="${jobStatusList}">
								<option value = '${jobStatus.id}'>${jobStatus.name}</option>
							</c:forEach>	 --%>
						</select>
					</td>
					
					<td width=8%><label>Rental Type</label></td>
					<td width=14%>
						<select class="form-control" id="rentalType" style="width:100%">
							<option value="0">ALL</option>
							<c:forEach var="rentalTypeId" items="${rentalTypeIdList}">
								<option value = '${rentalTypeId.id}'>${rentalTypeId.name}</option>
							</c:forEach>	
						</select>
					</td>	
				</tr>
				<tr height="30px">	
					<td><label>To Date</label></td>
					<td><input type="text" id="bookingToDate"  name="bookingToDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10"></input></td>
									
					<td><label>Branch</label></td>
					<td>
						<select class="form-control" id="branch" style="width:100%" onChange="fillCurrentHub(this.value)">
							<option value="0">ALL</option>
							<c:forEach var="branch" items="${branchList}">
								<option value = '${branch.id}'>${branch.name}</option>
							</c:forEach>	
						</select>
					</td>
					
					<td><label>Hub</label></td>
					<td>
						<select class="form-control" id="outlet" style="width:100%">
							<option value="0">ALL</option>							
						</select>
					</td>
					
					<td><label>Booking Mode</label></td>
					<td>
						<select class="form-control" id="MOB" style="width:100%">
							<option value="0">ALL</option>
							<option value="1">PrePaid</option>
							<option value="2">PostPaid</option>
						</select>
					</td>
					
					<td colspan=2 align="right">
						<input type='button'  value='Search'	 class="btn btn-primary" 	id="searchButton"    onclick="searchBooking()" style="width:80px">
						<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:80px">
					</td>										
				</tr>
				</table>
			</c:if>
			<c:if test="${jobType=='Expired'}">
				<table style="width: 100%;">
				<tr height="25px"> <!-- First Line -->
					<td width=8%><label>From Date</label></td>
					<td width=10%><input type="text" id="bookingFromDate"  name="bookingFromDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10"></input></td>
					
					<td width=10%><label>Corporate Name</label></td>
					<td width=14%>
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
					
					<td width=8%><label>Job Status </label></td>
					<td width=10%>
						<select class="form-control" id="jobStatus" name = "jobStatus" style="width:100%">
							<option value="0">ALL</option>
							<option value="E">Expired</option>							
						</select>
					</td>
					
					<td width=8%><label>Rental Type</label></td>
					<td width=14%>
						<select class="form-control" id="rentalType" style="width:100%">
							<option value="0">ALL</option>
							<c:forEach var="rentalTypeId" items="${rentalTypeIdList}">
								<option value = '${rentalTypeId.id}'>${rentalTypeId.name}</option>
							</c:forEach>	
						</select>
					</td>	
				</tr>
				<tr height="30px">	
					<td><label>To Date</label></td>
					<td><input type="text" id="bookingToDate"  name="bookingToDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10" ></input></td>
									
					<td><label>Branch</label></td>
					<td>
						<select class="form-control" id="branch" style="width:100%" onChange="fillCurrentHub(this.value)">
							<option value="0">ALL</option>
							<c:forEach var="branch" items="${branchList}">
								<option value = '${branch.id}'>${branch.name}</option>
							</c:forEach>	
						</select>
					</td>
					
					<td><label>Hub</label></td>
					<td>
						<select class="form-control" id="outlet" style="width:100%">
							<option value="0">ALL</option>							
						</select>
					</td>
					
					<td><label>Booking Mode</label></td>
					<td>
						<select class="form-control" id="MOB" style="width:100%">
							<option value="0">ALL</option>
							<option value="1">PrePaid</option>
							<option value="2">PostPaid</option>
						</select>
					</td>
					
					<td colspan=2 align="right">
						<input type='button'  value='Search'	 class="btn btn-primary" 	id="searchButton"    onclick="searchBooking()" style="width:80px">
						<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:80px">
					</td>										
				</tr>
				</table>
			</c:if>
				<table width=100%>
					<tr>
						<td width=35%><div style="width:95%;height:30px;overflow:auto;display:none" id="error" class="error"></div></td>
						<td width=35%><div style="width:100%;height:20px;display:none" id="info" class="success"></div></td>
					</tr>
				</table>
				<div class="row"> 
					<c:if test="${(jobType!='Advance') && (jobType!='Current')}">
						<div class="col-md-12" style="width: auto; float: left; margin-left: 10px;"><label style="color : black;  font-family: cambria;"><font color = "red"  style="font-weight : bold; font-family: Bookman Old Style;">Job Status : </font>&nbsp;
					   	 	 <span id="jobStatusLabels"></span> </label>
					    </div> 
				    </c:if>
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
<!--  Edit Booking Details Record  Panel -->
<div id="editDetailRecordPanel" class="modal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-md">
   		<div class="modal-content">
   			<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
   				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;'>Edit Booking Detail Record
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
					<div class="col-md-5"><input type="text" id="toBeReleaseAt" name="toBeReleaseAt" class="form-control" maxlength="100" onkeypress="isAlphaNumeric(event)"></input></div>
					<div class="col-md-1"></div>
				</div>   			

				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Spl.Instruction</label></div>
					<div class="col-md-5"><input type="text" id="instruction" name="instruction" class="form-control" maxlength="100" onkeypress="isAlphaNumeric(event)"></input></div>
					<div class="col-md-1"></div>
				</div>   			

<!-- Changes -->

				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Rental Type</label></div>
					<div class="col-md-5">
						<select class="form-control" id="rentalTypeModel" style="width:100%" onChange="fillTariff(this.value)">
							<option value="0">---Select---</option>
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
						</select>
					</div>
					<div class="col-md-1"></div>
				</div>
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><input type='button'  value='Passenger Details'	 class="btn btn-primary" 	id="addPassengerButton" disabled="disabled"  onclick="showPassengerInfoPanel()" style="width:120px;display:none"></div>
					<div class="col-md-5"></div>
					<div class="col-md-1"></div>
				</div>

				<div class="row">
					<div class="col-md-8">
						<div id="editDetailError" style="color:red;height:70px;font-size:11px;overflow-y:auto;display:none;">
						</div>
						<div id="editDetailInfo" style="color:green;display:none;">
						</div>
					</div>
					<div class="col-md-2"><input type='button' value='Ok' class="btn btn-primary" id="optionValue" onClick="setModelToTable()" style="width:100px"/></div>
					<div class="col-md-2"></div>
				</div>	
	   		</div>
    	 </div>
     	</div>
     </div>
     
     <!-- Dispatch Detail Record Panel -->
     <div id="dispatchDetailRecordPanel" class="modal" style='min-width:100%' tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
	 <div class="modal-dialog modal-md">
   		<div class="modal-content">
   			<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
   				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;'>Dispatch Booking Detail Record
       			<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:150%; color: #196780;">&times;</button></h4>
       		</div>
       		<div class="modal-body" id="dispatchDetailRecordPanel_body">
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Booking-No</label></div>
					<div class="col-md-7"><input type="text" id="bookingNO" name="bookingNo"  class="form-control" readonly="readonly" ></input></div>
					<div class="col-md-1"></div>
				</div>   
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Operation Mode</label></div>					
					<div class="col-md-2" id="dispatchRadioDiv"><input name="dispatch" id="dispatch" checked type="radio" value="D" onChange="disablefield();" /> <font size="1">Dispatch</font></div>
				    <div class="col-md-2" id="allocateRadioDiv" style="display:none;"><input name="dispatch" id="allocation" type="radio" value="A" onChange="disablefield();"/> <font size="1">Allocation</font></div>
				    <!-- <div class="col-md-3"><input name="dispatch" id="dispatchAndAllocation" type="radio" value="D" onChange="disablefield1();"/> <font size="1">Dispatch and allocation</font></div> -->
					<div class="col-md-1"></div>
				</div>   
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Duty Slip No</label></div>
					<div class="col-md-7"><input type="text" id="dutySlipNo" name="dutySlipNo"  class="form-control"  readonly="readonly" ></div>
					<div class="col-md-1"></div>
			    </div>
			    
			    <div class="row">
					<div class="col-md-12"><hr></div>
			    </div> 		
			    			 
			   <div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Car Number</label></div>
				    <div class="col-md-7"><input type="text" name="carNumber" id="carNumber" list="carRegistrationList" style=' width: 100%' onblur="carno()" onChange="fillCarRelDetails(this.value,'')">	                    
	                    <datalist id="carRegistrationList"></datalist>
					</div>
					<div class="col-md-1"></div>
			   </div>  
				
				<div class="row">
					<div class="col-md-1"></div>
			        <div class="col-md-3"><label>Chauffeur Name</label></div>
					<div class="col-md-7"><input type="text" id="chauffeurName" name="chauffeurName"  class="form-control" readonly="readonly"></div>
					<div class="col-md-1"></div>
			   </div> 
			   
			   <div class="row">
					<div class="col-md-1"></div>
			        <div class="col-md-3"><label>Vendor Name</label></div>
					<div class="col-md-7"><input type="text" id="venderName" name="venderName"  class="form-control" readonly="readonly"></div>
					<div class="col-md-1"></div>
			   </div> 
			   
				<div class="row">
					<div class="col-md-1"></div>
		            <div class="col-md-3"><label>Car Model</label></div>
					<div class="col-md-7"><input type="text" id="carModelForAllocation" name="carModelForAllocation"  class="form-control" readonly="readonly"></div>
					<div class="col-md-1"></div>
			   </div> 
			   
				<div class="row">
					<div class="col-md-1"></div>
	             	<div class="col-md-3"><label>Car Owner Type</label></div>
					<div class="col-md-7"><input type="text" id="carOwnerType" name="carOwnerType"  class="form-control" readonly="readonly"></div>
					<div class="col-md-1"></div>
			   	</div> 
			   
				<div class="row">
					<div class="col-md-1"></div>
			        <div class="col-md-3"><label>Pending Duty Slip</label></div>
					<div class="col-md-7"><input type="text" id="pendingDutySlip" name="pendingDutySlip" class="form-control" readonly="readonly"></div>
					<div class="col-md-1"></div>
			   	</div> 
			   
				<div class="row">
					<div class="col-md-1"></div>
			        <div class="col-md-3"><label>Change Chauffeur</label></div>
					<div class="col-md-7"><a href="javascript: updatechauffeur()" > <font size="3">Update Chauffeur Detail</font> </a></div>
					<div class="col-md-1"></div>
			   	</div> 
			   
			    <div class="row">
					<div class="col-md-12"><hr></div>
			   	</div> 	
			   
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Start Time</label></div>
			        <div class="col-md-2"><input type="text" id="date" name="date" data-inputmask="'alias': 'dd/mm/yyyy'" maxlength="10"  class="form-control" readonly="readonly"></div>
					<div class="col-md-1"><input type="text" id="time" name="time"  class="form-control" readonly="readonly"></div>
					<div class="col-md-1"><label>System </label></div>
			        <div class="col-md-2"><input type="text" id="sysdate" name="sysdate" data-inputmask="'alias': 'dd/mm/yyyy'" maxlength="10" class="form-control" readonly="readonly"></div>
					<div class="col-md-1"><input type="text" id="systime" name="systime"  class="form-control" readonly="readonly"></div>
					<div class="col-md-1"></div>
			   	</div> 
			   
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Km Out</label></div>
					<div class="col-md-7"><input type="text" id="kmOut"  name="kmOut"  class="form-control"  onkeypress="return isNumber(event)"></div>
					<div class="col-md-1"></div>
			   </div> 
			   
			   <div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Is Ds Manual</label></div>
					<div class="col-md-7"><label><input name="isDsManual" id="isDsManual" type="checkbox"   onclick="EnableDisableTextBox(this)" /> Yes</label></div>
					<div class="col-md-1"></div>
				</div>   
			   
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Manual Ds No</label></div>
			        <div class="col-md-3"><input type="text" id="manualDSNO" name="manualDSNO"  class="form-control" disabled="disabled"></div>
					<div class="col-md-4"><input type="text" id="remark" name="remark" placeholder="Remarks for Manual DS" class="form-control" disabled="disabled"></div>
					<div class="col-md-1"></div>
			   	</div> 
			   
			  	<div class="row">
					<div class="col-md-12"><hr></div>
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
			   
			   <br>
			   
			   <div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-2"></div>
					<div id='allocationDispatch' class= "col-md-2-offset col-md-6 pull-right">
						<input type='button' value='Dispatch ' class="btn btn-primary" id="dis" style="width:25%" onclick="dispatchRequest('dispatch')"/>
						<input type="button" id="alo" class="btn btn-primary"  value="Allocation" style="display: none;width:25%;" onclick="dispatchRequest('allocate')"/>
						<input type="button" id="disalo" class="btn btn-primary"  value="Dispatch and Allocate" style="display: none;width:35%;" onclick="dispatchRequest('allocateDispatch')"/>
						<input type='button' value='Cancel ' class="btn btn-primary" id="cancelDispatch"  data-dismiss="modal"  style="width:25%"/>
						<input type='button' value='Close ' class="btn btn-primary"  id="closeWindowl"  data-dismiss="modal"  style="width:25%"/>
					</div>
			        <!-- <div class="col-md-2"><input type='button' value='Dispatch ' class="btn btn-primary" id="dis"    style="width:100px"/><input type="button" id="alo" class="btn btn-primary"  value="Allocation" style="display: none;width:100px;"></div>
					<div class="col-md-1"></div>
					<div class="col-md-2"><input type='button' value='Cancel ' class="btn btn-primary" id="cancelDispatch"  data-dismiss="modal"  style="width:100px"/></div>
					<div class="col-md-1"></div>
				    <div class="col-md-2"><input type='button' value='Close ' class="btn btn-primary"  id="closeWindowl"  data-dismiss="modal"  style="width:100px"/></div> -->
			   </div>
			   <div class="row">
			   		<div class="col-md-offset-1 col-md-8">
						<div id="dispatchDetailError" style="color:red;height:70px;font-size:11px;overflow-y:auto;display:none;">
						</div>
						<div id="dispatchDetailInfo" style="color:green;display:none;">
						</div>
					</div>
			   </div> 
			   <div id="dataLoaderFade"></div>
        		<div id="dataLoaderModal">
            		<img id="loader" src="<%=request.getContextPath()%>/img/301(1).gif" width="98" height="98"  />
        		</div>
		   </div>		
    	 </div>
     	</div>
     </div>
     
     <!-- Update Dispatch Detail Record Panel -->
     <div id="updatedispatchDetailRecordPanel" class="modal" style='min-width:100%' tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
	 <div class="modal-dialog modal-md">
   		<div class="modal-content">
   			<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
   				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;'>Update the chauffeur Detail for Booking No- <label id="UpdateBookingNo"></label>
       			<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:150%; color: #196780;">&times;</button></h4>
       		</div>
       		<div class="modal-body" id="updatedispatchDetailRecordPanel_body">
       		<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Car Number</label></div>
					<div class="col-md-7"><input type="text" id="carNo" name="carNo"  class="form-control" readonly="readonly"></input></div>
					<div class="col-md-1"></div>
				</div>   
				
				  
			   <div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Chauffeur Name</label></div>
					<div class="col-md-7"><input type="text" id="updateChauffeurName" name="updateChauffeurName"  class="form-control" ></input></div>
					<div class="col-md-1"></div>
				</div> 
			  
			  <div class="row">
					<div class="col-md-1"></div>

			        <div class="col-md-3"><label>Mobile Number</label></div>
					<div class="col-md-7"><input type="text" id="updateMobileNumber" name="updateMobileNumber" class="form-control" onkeypress="return isNumber(event)"></div>
					<div class="col-md-1"><input type="hidden" value="N" id="isUpdatedChauffeur"/></div>
			   </div> 
			   
			   <br>
			     <div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-6">
						<div id="chauffeurDetailError" style="color:red;height:40px;font-size:11px;overflow-y:auto;display:none;">
						</div>
						<div id="chauffeurDetailInfo" style="color:green;display:none;">
						</div>
					</div>
			        <div class="col-md-1"><input type='button' value='Ok' class="btn btn-primary" id="updateChauffeur"  style="width:60px" onclick="updateChauffeurDetails()"/></div>
					<div class="col-md-1"></div>
				    <div class="col-md-1"><input type='button' value='Close' class="btn btn-primary"  id="closeWindowl"  data-dismiss="modal"  style="width:80px"/></div>
					<div class="col-md-1"></div>
					
			   </div> 
			   
		</div>
		</div>
		</div>
     	</div>
     	
     	<!--  Cancel Booking Record Panel -->
	<div id=bookingCancelPanel class="modal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="bookingCancelLabel" aria-hidden="true">
	<div class="modal-dialog modal-sm">
   		<div class="modal-content">
   			<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
   				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;' id="bookingCancelLabel">Cancel Booking Record
       			<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:150%; color: #196780;">&times;</button></h4>
       		</div>
   			<div class="modal-body" id="bookingCancelPanel_body">
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Reason</label></div>
					<div class="col-md-7"><select class="form-control" id="cancelReason" style="width:100%" onchange="showHide()" >
							<option value="0">---Select---</option>
							<c:forEach var="BCRListId" items="${BCRList}">
								<option value = '${BCRListId.id}'>${BCRListId.name}</option>
							</c:forEach>	
						</select>
					</div>
					<div class="col-md-1"></div>
				</div>
				<br/>	  			
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"></div>
					<div class="col-md-7"><textarea id="otherReason" name="cancelReason" class="form-control" cols="8" rows="2" placeholder="Reason of Cancellation..."></textarea></div>
					<div class="col-md-1"></div>
				</div> 
				<br/>	
				<div class="row">
					<div class="col-sm-8">
						<div id="cancelReasonError" style="color:red;height: auto;font-size:11px;overflow-y:auto;display:none;"></div>
						<div id="cancelReasonInfo" style="color:green;display:none;"></div>
					</div>
					<div class="col-sm-1"><input type='button' value='Save' class="btn btn-primary" id="saveCR"  onClick="saveCancelReason()" style="width:80px; color: white;"/></div>
					<div class="col-sm-1"></div>
				</div>	
	   		</div>
    	 </div>
     	</div>
     </div>
     
     <!-- Reserve Record Panel -->
     <div id="reserveRecordPanel" class="modal" style='min-width:100%' tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
	 <div class="modal-dialog modal-lg">
   		<div class="modal-content">
   			<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
   				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;'>Reserve car
       			<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:150%; color: #196780;">&times;</button></h4>
       		</div>
       		<div class="modal-body" id="reserveRecordPanel_body">
       		   <div class="row">
				   <div class="col-md-1"></div>
	               <div class="col-md-2"><label>Reserve Date</label></div>
				   <div class="col-md-2"><input type="text" id="reserveDate" name="reserveDate"  data-inputmask="'alias': 'dd/mm/yyyy'" maxlength="10"  class="form-control" ></div>
				   <div class="col-md-2"><label>Car Segment</label></div>
				   <div class="col-md-2">
					 	 <select class="form-control" id="carTypeReserve" style="width:100%">
								<option value="0">---Select---</option>
								<c:forEach var="carModelId" items="${carModelIdList}">
									<option value = '${carModelId.id}'>${carModelId.name}</option>
								</c:forEach>	
						 </select>
				   </div>
				   <div class="col-md-offset-1 col-md-1"><input type="button" value="Search" class="btn btn-primary  pull-right" style="width:70px;"/></div>
			   </div><br><hr><br>
			   <div class="row">
					<div id="dataTable3" class="col-md-12" style="width:99%;overflow-y:auto;max-height:320px;"></div>
			   </div><br><hr><br>
			   <div class="row">
					<div class="col-md-3"></div>
					<div class="col-md-2"><input type='button' value='Apply' class="btn btn-primary" id="Apply"  data-dismiss="modal"  style="width:100px"/></div>
			        <div class="col-md-2"><input type='button' value='Reset' class="btn btn-primary" id="Reset"  data-dismiss="modal"  style="width:100px"/></div> 
			        <div class="col-md-2"><input type='button' value='Close Window' class="btn btn-primary"  id="closeWindowl"  data-dismiss="modal"  style="width:100px"/></div>
					<div class="col-md-3"></div>
			   </div> 
			</div>
		</div>
	</div>
	</div>
	 <div id="viewDutySlip" class="modal" style='min-width:100%' tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
	 <div class="modal-dialog modal-lg">
   		<div class="modal-content" style="background:none;">
   			
       		<div class="modal-body" id="viewDutySlip_body">
					<div id="dutySlip" style="width:100%;overflow:auto;"></div>
			</div> 
       		
       	<!-- 	end -->
       		</div>
		</div>
	</div>
	
    <!-- Reserve Detail Record Panel -->
     <div id="reserveCarDetailRecordPanel" class="modal" style='min-width:100%' tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
	 <div class="modal-dialog modal-md">
   		<div class="modal-content">
   			<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
   				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;'>Reserve Car Detail Record
       			<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:150%; color: #196780;">&times;</button></h4>
       		</div>
       		<div class="modal-body" id="reserveCarDetailRecordPanel_body">
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Booking-No</label></div>
					<div class="col-md-7"><input type="text" id="bookingNOR" name="bookingNoR"  class="form-control" readonly="readonly" ></input></div>
					<div class="col-md-1"></div>
				</div>   
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Operation Mode</label></div>					
<!-- 					<div class="col-md-2" id="dispatchRadioDivR"><input name="dispatchR" id="dispatchR" checked type="radio" value="D"  /> <font size="1">Dispatch</font></div> -->
				    <div class="col-md-2" id="reserveRadioDiv"><input name="dispatchR" id="reserve" type="radio" value="M" /> <font size="1">Reserve</font></div>
				    <!-- <div class="col-md-3"><input name="dispatch" id="dispatchAndAllocation" type="radio" value="D" onChange="disablefield1();"/> <font size="1">Dispatch and allocation</font></div> -->
					<div class="col-md-1"></div>
				</div>   
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Corporate Name</label></div>
					<div class="col-md-7"><input type="text" id="corpName" name="corpName"  class="form-control" readonly="readonly" ></input></div>
					<div class="col-md-1"></div>
				</div>  
				
			    <div class="row">
					<div class="col-md-12"><hr></div>
			    </div> 		
			    			 
			   <div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Car Number</label></div>
				    <div class="col-md-7"><input type="text" name="carNumberR" id="carNumberR" list="carRegistrationList" style=' width: 100%' onblur="carno()" onChange="fillCarRelDetailsR(this.value,'')">	                    
	                    <datalist id="carRegistrationList"></datalist>
					</div>
					<div class="col-md-1"></div>
			   </div>  
				
				<div class="row">
					<div class="col-md-1"></div>
			        <div class="col-md-3"><label>Chauffeur Name</label></div>
					<div class="col-md-7"><input type="text" id="chauffeurNameR" name="chauffeurNameR"  class="form-control" tabindex="-1" readonly="readonly"></div>
					<div class="col-md-1"></div>
			   </div> 
			   
			   <div class="row">
					<div class="col-md-1"></div>
			        <div class="col-md-3"><label>Vendor Name</label></div>
					<div class="col-md-7"><input type="text" id="venderNameR" name="venderNameR"  class="form-control" tabindex="-1" readonly="readonly"></div>
					<div class="col-md-1"></div>
			   </div> 
			   
				<div class="row">
					<div class="col-md-1"></div>
		            <div class="col-md-3"><label>Car Model</label></div>
					<div class="col-md-7"><input type="text" id="carModelForAllocationR" name="carModelForAllocationR"  class="form-control" tabindex="-1" readonly="readonly"></div>
					<div class="col-md-1"></div>
			   </div> 
			   
				<div class="row">
					<div class="col-md-1"></div>
	             	<div class="col-md-3"><label>Car Owner Type</label></div>
					<div class="col-md-7"><input type="text" id="carOwnerTypeR" name="carOwnerTypeR"  class="form-control" tabindex="-1" readonly="readonly"></div>
					<div class="col-md-1"></div>
			   	</div> 
			   
				<div class="row">
					<div class="col-md-1"></div>
			        <div class="col-md-3"><label>Pending Duty Slip</label></div>
					<div class="col-md-7"><input type="text" id="pendingDutySlipR" name="pendingDutySlipR" class="form-control" tabindex="1" readonly="readonly"></div>
					<div class="col-md-1"></div>
			   	</div> 
			   
				<div class="row">
					<div class="col-md-1"></div>
			        <div class="col-md-3"><label>Change Chauffeur</label></div>
					<div class="col-md-7"><a href="javascript: updateChauffeurR()" > <font size="2">Update Chauffeur Detail</font> </a></div>
					<div class="col-md-1"></div>
			   	</div> 
			   
			    <div class="row">
					<div class="col-md-12"><hr></div>
			   	</div> 	
			   
				<div class="row">
					<div class="col-md-12" >
						<div id="bookingDetailDataTable" style="width:100%;overflow:auto;"></div>
					</div>
			   	</div> 
			   
			  	<div class="row">
					<div class="col-md-12"><hr></div>
			  	</div> 	
			   
			    <div class="row">
				    <div class="col-md-1"></div> 
					<div class="col-md-3"><label>Send Sms </label></div>
			        <div class="col-md-2"><label class="checkbox-inline"><input name="smsClient" id="smsClientR" type="checkbox" checked = "checked" /> Sms-Client</label></div>
				   	<div class="col-md-2"><label class="checkbox-inline" ><input name="smsBooker" id="smsBookerR" type="checkbox" /> Sms-Booker</label></div>
				   	<div class="col-md-1"><label>other</label></div>
				   	<div class="col-md-2"><input type="text" id="dispatchSmsOtherR" name="dispatchSmsOther" class="form-control" onkeypress="return isNumber(event)" ></div>
				   	<div class="col-md-1"><input type="hidden" value="0" id="dutySlipIdForUpdateR"/></div>
			   </div>
			   
			   <br>
			   
			   <div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-2"></div>
					<div id='reserveDispatch' class= "col-md-2-offset col-md-6 pull-right">
<!-- 						<input type='button' value='Unreserve' class="btn btn-primary" id="unres" style="width:25%; display: none;" onclick="checkCarAvailability('unReserve')"/> -->
						<input type="button" id="res" class="btn btn-primary"  value="Reserve" style="width:25%;" onclick="checkCarAvailability('reserve')"/>
						<input type='button' value='Cancel ' class="btn btn-primary" id="cancelDispatch"  data-dismiss="modal"  style="width:25%"/>
						<input type='button' value='Close ' class="btn btn-primary"  id="closeWindowl"  data-dismiss="modal"  style="width:25%"/>
					</div>
			        <!-- <div class="col-md-2"><input type='button' value='Dispatch ' class="btn btn-primary" id="dis"    style="width:100px"/><input type="button" id="alo" class="btn btn-primary"  value="Allocation" style="display: none;width:100px;"></div>
					<div class="col-md-1"></div>
					<div class="col-md-2"><input type='button' value='Cancel ' class="btn btn-primary" id="cancelDispatch"  data-dismiss="modal"  style="width:100px"/></div>
					<div class="col-md-1"></div>
				    <div class="col-md-2"><input type='button' value='Close ' class="btn btn-primary"  id="closeWindowl"  data-dismiss="modal"  style="width:100px"/></div> -->
			   </div>
			   <div class="row">
			   		<div class="col-md-offset-1 col-md-8">
						<div id="reserveDetailError" style="color:red;height:70px;font-size:11px;overflow-y:auto;display:none;">
						</div>
						<div id="reserveDetailInfo" style="color:green;display:none;">
						</div>
					</div>
			   </div> 
			   <div id="dataLoaderFade"></div>
        		<div id="dataLoaderModal">
            		<img id="loader" src="<%=request.getContextPath()%>/img/301(1).gif" width="98" height="98"  />
        		</div>
		   </div>		
    	 </div>
     	</div>
     </div>
     
     
     <!-- Update Chauffeur Detail Record Panel -->
     <div id="updateChauffuerDetailRecordPanel" class="modal" style='min-width:100%' tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
	 <div class="modal-dialog modal-md">
   		<div class="modal-content">
   			<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
   				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;'>Update the chauffeur Detail for Booking No- <label id="UpdateBookingNoR"></label>
       			<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:150%; color: #196780;">&times;</button></h4>
       		</div>
       		<div class="modal-body" id="updateChauffeurDetailRecordPanel_body">
       		<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Car Number</label></div>
					<div class="col-md-7"><input type="text" id="carNoR" name="carNoR"  class="form-control" readonly="readonly"></input></div>
					<div class="col-md-1"></div>
				</div>   
				
				  
			   <div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Chauffeur Name</label></div>
					<div class="col-md-7"><input type="text" id="updateChauffeurNameR" name="updateChauffeurNameR"  class="form-control" ></input></div>
					<div class="col-md-1"></div>
				</div> 
			  
			  <div class="row">
					<div class="col-md-1"></div>

			        <div class="col-md-3"><label>Mobile Number</label></div>
					<div class="col-md-7"><input type="text" id="updateMobileNumberR" name="updateMobileNumberR" class="form-control" onkeypress="return isNumber(event)"></div>
					<div class="col-md-1"><input type="hidden" value="N" id="isUpdatedChauffeurR"/></div>
			   </div> 
			   
			   <br>
			     <div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-6">
						<div id="chauffeurDetailErrorR" style="color:red;height:40px;font-size:11px;overflow-y:auto;display:none;">
						</div>
						<div id="chauffeurDetailInfoR" style="color:green;display:none;">
						</div>
					</div>
			        <div class="col-md-1"><input type='button' value='Ok' class="btn btn-primary" id="updateChauffeurR"  style="width:60px" onclick="updateChauffeurDetailsR()"/></div>
					<div class="col-md-1"></div>
				    <div class="col-md-1"><input type='button' value='Close' class="btn btn-primary"  id="closeWindowlR"  data-dismiss="modal"  style="width:80px"/></div>
					<div class="col-md-1"></div>
					
			   </div> 
			   
		</div>
		</div>
		</div>
  	</div>

	<!-- UnReserve Detail Record Panel -->
	<div id="unReserveCarDetailRecordPanel" class="modal" style='min-width: 100%' tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false 	aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header bg-info" style='text-align: center; background-color: FFDA71;'>
					<h4 class="modal-title" style='font-size: 14px; color: #196780; font-family: Bookman Old Style;'>UnReserve Car Detail Record
						<button type="button" class="close" data-dismiss="modal"aria-hidden="true" style="font-size: 150%; color: #196780;">&times;</button>
					</h4>
				</div>
				<div class="modal-body" id="unReserveCarDetailRecordPanel_body">
					<div class="row">
						<div class="col-md-1"></div>
						<div class="col-md-3">
							<label>Booking-No</label>
						</div>
						<div class="col-md-7">
							<input type="text" id="bookingNOUr" name="bookingNOUr" class="form-control" readonly="readonly"></input>
						</div>
						<div class="col-md-1"></div>
					</div>


					<div class="row">
						<div class="col-md-1"></div>
						<div class="col-md-3">
							<label>Corporate Name</label>
						</div>
						<div class="col-md-7" style="margin-top: 5px;">
							<input type="text" id="corpNameUr" name="corpName" class="form-control" readonly="readonly"></input>
						</div>
						<div class="col-md-1"></div>
					</div>

					<div class="row">
						<div class="col-md-12"><hr></div>
					</div>

					<div class="row">
						<div class="col-md-12">
							<div id="bookingDetDataTableUnRes" style="width: 100%; overflow: auto;"></div>
						</div>
					</div>

					<div class="row">
						<div class="col-md-12"><hr></div>
					</div>

					<div class="col-md-1">
						<input type="hidden" value="0" id="dutySlipIdForUpdateUR" />
					</div>

					<br>

					<div class="row">
						<div class="col-md-1"></div>
						<div class="col-md-2"></div>
						<div id='unReserveRecords' class="col-md-2-offset col-md-6 pull-right">
							<input type='button' value='Unreserve' class="btn btn-primary" id="unres" style="width:25%; " onclick="unReserve()"/>
							<input type='button' value='Cancel ' class="btn btn-primary" id="cancelUnRes" data-dismiss="modal" style="width: 25%" />
							<input type='button' value='Close ' class="btn btn-primary" 	id="closeUnRes" data-dismiss="modal" style="width: 25%" />
						</div>
					</div>
					<div class="row">
						<div class="col-md-offset-1 col-md-8">
							<div id="unreserveDetailError" style="color: red; height: 70px; font-size: 11px; overflow-y: auto; display: none;">
							</div>
							<div id="unreserveDetailInfo" style="color: green; display: none;"></div>
						</div>
					</div>
					<div id="dataLoaderFade"></div>
					<div id="dataLoaderModal">
						<img id="loader" src="<%=request.getContextPath()%>/img/301(1).gif" width="98" height="98"  />
					</div>
				</div>
			</div>
		</div>
	</div>
	</form>
	</section>
</div>	
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
						<!-- <tr bgcolor="#FFDA71" style="font-family: cambria;font-size: 14; height: 10px;">
							<th>id</th>
							<th>Add</th>
							<th>Name.</th>
							<th>Mobile No</th>
							<th>Age.</th>
							<th>Sex</th>
							<th>Id-Proof</th>
						</tr>
						<tr>
							<td><label id="id_1"></label></td>
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
						</tr> -->
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
