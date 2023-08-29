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
<script src= "<%=request.getContextPath()%>/js/com/billing/billing.js"></script>
<script src= "<%=request.getContextPath()%>/js/com/common/moment.js"></script>
<script>
	$(function () {
	    //Datemask dd/mm/yyyy
	    $("#invoiceFromDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    $("#invoiceToDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    $("#toDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    $("#letterDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});	    
	    $("#systemDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});	    
	    $("#timeIn").inputmask("hh:mm", {"placeholder": "hh:mm"});
	    $("#invoiceFromDate").val(firstDayOfMonth(new Date()));
	   	$("#invoiceToDate").val(getTimeStampTo_DDMMYYYY(new Date())); 
	   	$("#dsInvoiceDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		$('#dateInvoice').inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		$("#dateInvoice").val(getTimeStampTo_DDMMYYYY(new Date()));
	   	$("#dsInvoiceDate").val(getTimeStampTo_DDMMYYYY(new Date()));
	   	$("#systemDate").val(getTimeStampTo_DDMMYYYY(new Date()));

		$("#dsFromDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"})
		$("#dsToDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"})
		$("#dsTimeIn").inputmask("hh:mm", {"placeholder": "hh:mm"});
		$("#dsTimeOut").inputmask("hh:mm", {"placeholder": "hh:mm"});
	   	
		$("input[name=optionsRadios]").change(function(){
			$('#clientName').toggle();
			$('#otherClient').toggle();	
	    });
	    $("input[name=opReporting]").change(function(){
			$('#officeAddress').toggle();
			$('#reportingAddress').toggle();	
	    });
	    
	});
 
	var loginUserId = '${loginUserId}';
	var carType = "";
	var userPageAccess= '<%=sAccess%>';	
	var jobType = '${jobType}';
	
	function onLoadPage(){
		if(<%=session.getAttribute("loginUserId")%>!=null)
			loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
		assignRights();
		var contextPath="<%=request.getContextPath()%>";
		if (jobType=='DutySlipReceive'|| jobType=='DutySlipClose' || jobType == 'DutySlipUnbilled'){
			searchBilling("R");
		}else if (jobType=='Invoice'){
			searchInvoice();
		}
		
		if(jobType == "CoverLetter"){
			$("#captionText").text("Cover Letter");
		}else if (jobType == "DutySlipReceive"){
			$("#captionText").text("Duty Slip Receive");
		}else if (jobType == "DutySlipClose"){
			$("#captionText").text("Duty Slip Close");
		}else if (jobType == "DutySlipClosing"){
			$("#captionText").text("Duty Slip Closing");
		}else if (jobType == "DutySlipUnbilled"){
			$("#captionText").text("Duty Slip Unbilled");
		}else if (jobType == "Invoice"){
			$("#captionText").text("Invoices");
		}
	}
	
</script>
</head>
<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<form id="bookingMasterForm" method="post">
		<div class="row">
		<legend><strong><span id="captionText"></span></strong></legend>
			<div class="col-md-12" style="background-color: #F6F6F3;">
			
				<c:if test="${jobType=='Invoice' || jobType=='CoverLetter'}">
					<table style="width: 100%;">
					<tr height="25px"> <!-- First Line -->
					<td width=8%><label>From Date</label></td>
					<td width=10%><input type="text" id="invoiceFromDate"  name="invoiceFromDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10"></input></td>
					
					<td width=10%><label>Corporate Name</label></td>
					<td width=14%>
						<select class="form-control" id="corporateId" style="width:100%">
							<option value="0">ALL</option>
							<c:forEach var="customerOrCompanyNameId" items="${customerOrCompanyNameIdList}">
								<option value = '${customerOrCompanyNameId.id}'>${customerOrCompanyNameId.name}</option>
							</c:forEach>	
						</select>
					</td>
					
					<td width=8%><label>Invoice No</label></td>
					<td width=10%>
						<select class="form-control" id="invoiceNo" name="invoiceNo" style="width:100%">
							<option value="0">ALL</option>
							<c:forEach var="invoiceNo" items="${invoiceNoList}">
								<option value = '${invoiceNo.invoiceNo}'>${invoiceNo.invoiceNo}</option>
							</c:forEach>	
						</select>
					</td>
					
					<td width=8%><label>Closed By </label></td>
					<td width=10%>
						<select class="form-control" id="closedBy" style="width:100%">
							<option value="0">ALL</option>
							<c:forEach var="closedBy" items="${closedByList}">
								<option value = '${closedBy.id}'>${closedBy.userName}</option>
							</c:forEach>	
						</select>
					</td>
					
					<td width=8%><label>Booked By</label></td>
					<td width=14%>
						<select class="form-control" id="bookedBy" style="width:100%">
							<option value="0">ALL</option>
							<c:forEach var="bookedBy" items="${bookedByList}">
								<option value = '${bookedBy.id}'>${bookedBy.name}</option>
							</c:forEach>	
						</select>
					</td>	
					
				</tr>
				<tr height="30px">	
					<td><label>To Date</label></td>
					<td><input type="text" id="invoiceToDate"  name="invoiceToDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10"></input></td>
									
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
						<select class="form-control" id="mob" style="width:100%">
							<option value="0">ALL</option>
							<option value="1">PrePaid</option>
							<option value="2">PostPaid</option>
						</select>
					</td>
					<td><label>Used By.</label></td>
					<td>
						<select class="form-control" id="usedBy" name="usedBy" style="width:100%">
							<option value="0">ALL</option>
							<c:forEach var="usedBy" items="${usedByList}">
								<option value = '${usedBy.bookedForName}'>${usedBy.bookedForName}</option>
							</c:forEach>	
						</select>
					</td>
					
					<td>
						<select class="form-control" id="rentalTypeModel" style="width:100%; display: none;">
							<option value="0">---Select---</option>
							<c:forEach var="rentalTypeId" items="${rentalTypeIdList}">
								<option value = '${rentalTypeId.id}'>${rentalTypeId.name}</option>
							</c:forEach>	
						</select>
					</td>
				<tr>
					<td colspan=8></td>
					<td colspan=2 align="right">
						<input type='button'  value='Search'	 class="btn btn-primary" 	id="searchButton"    onclick="searchInvoice()" style="width:80px">
						<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:80px">
					</td>
					<td width=8%><input type="hidden" value="D" id="status"></input> </td>
				</tr>
				</table>
				</c:if>
				
				<c:if test="${jobType == 'DutySlipReceive' || jobType == 'DutySlipClose'}">
					<table style="width: 100%;">
						<tr height="25px"><!-- First Line -->
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
							
							<td width=10%><label>Corporate Name</label></td>
							<td width=14%>
								<select class="form-control" id="corporateId" style="width:100%" >
									<option value="0">ALL</option>
									<c:forEach var="customerOrCompanyNameId" items="${customerOrCompanyNameIdList}">
										<option value = '${customerOrCompanyNameId.id}'>${customerOrCompanyNameId.name}</option>
									</c:forEach>	
								</select>
							</td>
							
							<td width=8%><label>Car No.</label></td>
							<td width=10%>
								<select class="form-control" id="carRegNo" style="width:100%">
									<option value="0">ALL</option>
									<c:forEach var="carRegNoList" items="${carRegNoList}">
										<option value = '${carRegNoList.registrationNo}'>${carRegNoList.registrationNo}</option>
									</c:forEach>	
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
							<td>
								<select class="form-control" id="rentalTypeModel" style="width:100%; display: none;">
									<option value="0">---Select---</option>
									<c:forEach var="rentalTypeId" items="${rentalTypeIdList}">
										<option value = '${rentalTypeId.id}'>${rentalTypeId.name}</option>
									</c:forEach>	
								</select>
							</td>
						</tr>
						<tr>
							<td colspan=2>
								<div class="col-md-12" style="width: auto; float: left;"><label style="color : black;  font-family: cambria;"><font color = "red"  style="font-weight : bold; font-family: Bookman Old Style;">Total DS : </font>&nbsp;
								   	 	 <span id="totalDueDS" style="color: red ; font-size : 16px; font-weight : bold;"></span> </label>
							    </div> 
						    </td>
							<td colspan=6></td>
							<td colspan=2 align="right" style="padding-right: 5px;">
								<input type='button'  value='Search'	 class="btn btn-primary" 	id="searchButton"    onclick="searchBilling('N')" style="width:80px">
								<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:80px">
							</td>
							<td><input type="hidden" value="D" id="status"></input> </td>
						</tr>
						
					</table>
				</c:if>
				
				<c:if test="${jobType == 'DutySlipUnbilled'}">
					<table style="width: 100%;">
						<tr height="25px"><!-- First Line -->
							<td width=10%><label>Branch</label></td>
							<td width=10%>
								<select class="form-control" id="branch" style="width:100%" onChange="fillCurrentHub(this.value)">
									<option value="0">ALL</option>
									<c:forEach var="branch" items="${branchList}">
										<option value = '${branch.id}'>${branch.name}</option>
									</c:forEach>	
								</select>
							</td>
							
							<td width=4%><label>Hub</label></td>
							<td width=10%>
								<select class="form-control" id="outlet" style="width:100%">
									<option value="0">ALL</option>							
								</select>
							</td>
							
							<td width=10%><label>Corporate Name</label></td>
							<td width=16%>
								<select class="form-control" id="corporateId" style="width:100%" >
									<option value="0">ALL</option>
									<c:forEach var="customerOrCompanyNameId" items="${customerOrCompanyNameIdList}">
										<option value = '${customerOrCompanyNameId.id}'>${customerOrCompanyNameId.name}</option>
									</c:forEach>	
								</select>
							</td>
							
							<td width=8%><label>Booked By</label></td>
							<td width=10%>
								<select class="form-control" id="bookedBy" style="width:100%">
									<option value="0">ALL</option>
									<c:forEach var="bookedBy" items="${bookedByList}">
										<option value = '${bookedBy.id}'>${bookedBy.name}</option>
									</c:forEach>
								</select>
							</td>
							
							<td width=6%><label>Used By</label></td>
							<td width=16%>
								<select class="form-control" id="usedBy" style="width:100%">
									<option value="0">ALL</option>
									<c:forEach var="usedBy" items="${usedByList}">
										<option value = '${usedBy.bookedForName}'>${usedBy.bookedForName}</option>
									</c:forEach>	
								</select>
							</td>
						</tr>
						
						<tr>
							<td width=10%><label>Closed By</label></td>
							<td width=10%>
								<select class="form-control" id="closedBy" style="width:100%">
									<option value="0">ALL</option>
									<c:forEach var="closedBy" items="${closedByList}">
										<option value = '${closedBy.id}'>${closedBy.userName}</option>
									</c:forEach>	
								</select>
							</td>
							
							<td width=10%>
								<select class="form-control" id="rentalType" style="width:100%; display: none;">
									<option value="0">ALL</option>
									<c:forEach var="rentalTypeId" items="${rentalTypeIdList}">
										<option value = '${rentalTypeId.id}'>${rentalTypeId.name}</option>
									</c:forEach>	
								</select>
							</td>
							
							<td>
								<select class="form-control" id="rentalTypeModel" style="width:100%; display: none;">
									<option value="0">---Select---</option>
									<c:forEach var="rentalTypeId" items="${rentalTypeIdList}">
										<option value = '${rentalTypeId.id}'>${rentalTypeId.name}</option>
									</c:forEach>	
								</select>
							</td>
						</tr>
						
						<tr>
							<td colspan=8>
								<table style="width: 100%">
									<tr>
										<td width=35%><div style="width:95%;height:30px;overflow:auto;display:none" id="error" class="error"></div></td>
										<td width=35%><div style="width:100%;height:20px;display:none" id="info" class="success"></div></td>
									</tr>
								</table>
							</td>
							<td colspan=2 align="right" style="padding-right: 5px;">
								<input type='button'  value='Search'	 class="btn btn-primary" 	id="searchButton"    onclick="searchBilling('N')" style="width:80px">
								<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:80px">
							</td>
							<td><input type="hidden" value="D" id="status"></input> </td>
						</tr>
					</table>
				</c:if>
				<div class="row"> 
					<div class="col-md-4" style="width: auto; float: left;"><label style="color : black;  font-family: cambria;"><font color = "red"  style="font-weight : bold; font-family: Bookman Old Style;">Branch : </font>&nbsp;
					   	 	 <span id="branchLabels"></span> </label>
				    </div> 
					<c:if test="${jobType == 'DutySlipUnbilled'}">
						<div class="col-md-8" style="width: auto; float: right; margin-left: 10px;"><label style="color : black;  font-family: cambria;"><font color = "red"  style="font-weight : bold; font-family: Bookman Old Style;">Unbilled Amount : </font>&nbsp;
						   	 	 [&nbsp;<span style="color: red ; font-size : 16px; font-weight : bold;" id="unbilledAmt"></span>&nbsp;] </label>
					    </div> 
					</c:if>	
					<c:if test="${jobType == 'Invoice' || jobType == 'CoverLetter'}">
						<div class="col-md-8" style="width: auto; float: right;"><label style="color : black;  font-size : 14px; font-family: cambria;"><font color = "red"  style="font-weight : bold; font-family: Bookman Old Style;">Invoice Amount : </font>&nbsp;
						   	 	 [&nbsp;<span style="color: red ; font-size : 16px; font-weight : bold;" id="invoiceAmt"></span>&nbsp;] </label>
					    </div> 
					</c:if>	
				</div>    
				<div class="row"> 
				    <div class="col-md-12" style="width: auto; float: left;"><label style="color : black;  font-family: cambria;"><font color = "red"  style="font-weight : bold; font-family: Bookman Old Style;">Rental Type : </font>&nbsp;
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
					<div class="box-body" id="dataTable2" style="width:100%;overflow:auto"></div>
				</div>
			</div>
			<br>
			<c:if test="${jobType == 'DutySlipReceive'}">
				<div class="row" style="width:600px; margin-left:401px">
						<div class="col-md-2"><input type='button'  value='Submit'	 class="btn btn-primary" 	id="submitButton"    onclick="updateDutySlip()" style="width:80px"></div>
						<div class="col-md-2"><input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:80px"></div>			
				</div>
			</c:if>
			
			<c:if test="${jobType == 'DutySlipUnbilled'}">
				<div class="row" style="width:508px; margin-left:401px">
						<div class="col-md-3"><input type='button'  value='Generate Invoice'	 class="btn btn-primary" 	id="invoiceButton"   onclick="invoiceDate()"  style="width:100%"></div>
						<div class="col-md-2"><input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:80px"></div>	
				</div>
			</c:if>
			
			<c:if test="${jobType == 'CoverLetter'}">
				<div class="row" style="width:508px; margin-left:401px">
						<div class="col-md-4"><input type='button'  value='Generate Cover Letter'	 class="btn btn-primary" 	id="invoiceButton"   onclick="callConfirmation()"  style="width:100%"></div>
						<div class="col-md-2"><input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:80px"></div>	
				</div>
			</c:if>
			
		
	<!-- View Invoice Record Panel  -->
			
	<div id="viewInvoiceRecordPanel" class="modal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-md">
   		<div class="modal-content">
   			<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
   				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;'>View Invoice
       			<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:150%; color: #196780;">&times;</button></h4>
       		</div>
   			<div class="modal-body" id="viewInvoiceRecordPanel_body" style="height:450px">
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Branch</label></div>
					<div class="col-md-5"><input type="text" id="branchModel" name="branchModel"  class="form-control" readonly="readonly"></input></div>
					<div class="col-md-1"></div>
				</div>   			

				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Hub</label></div>
					<div class="col-md-5"><input type="text" id="hubModel" name="hubModel" class="form-control" readonly="readonly"/></div>
					<div class="col-md-1"></div>
				</div>   			
   			
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Booking Mode</label></div>
					<div class="col-md-5"><input type="text" id="bookingModeModel" name="bookingModeModel" class="form-control" readonly="readonly"/></div>
					<div class="col-md-1"></div>
				</div>   			

				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Invoice No</label></div>
					<div class="col-md-5"><input type="text" id="invoiceNoModel" name="invoiceNoModel" class="form-control" readonly="readonly"/></div>
					<div class="col-md-1"></div>
				</div>   
				
					<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Invoice Date</label></div>
					<div class="col-md-5"><input type="text" id="invoiceDateModel" name="invoiceDateModel" class="form-control" readonly="readonly"/></div>
					<div class="col-md-1"></div>
				</div>   					
   			
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Corporate Name</label></div>
					<div class="col-md-5"><input type="text" id="corporateNameModel" name="corporateNameModel" class="form-control" readonly="readonly"/></div>
					<div class="col-md-1"></div>
				</div>   
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Booked By</label></div>
					<div class="col-md-5"><input type="text" id="bookedByModel" name="bookedByModel" class="form-control" readonly="readonly"/></div>
					<div class="col-md-1"></div>
				</div>   			
						
					<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Client Name</label></div>
					<div class="col-md-5"><input type="text" id="clientNameModel" name="clientNameModel" class="form-control" readonly="readonly"/></div>
					<div class="col-md-1"></div>
				</div>   			

				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Basic Fare</label></div>
					<div class="col-md-5"><input type="text" id="basicFareModel" name="basicFareModel" class="form-control" readonly="readonly" ></input></div>
					<div class="col-md-1"></div>
				</div>   	
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Misc Charges</label></div>
					<div class="col-md-5"><input type="text" id="miscChargesModel" name="miscChargesModel" class="form-control" readonly="readonly" ></input></div>
					<div class="col-md-1"></div>
				</div>   			
						

				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Parking Amount</label></div>
					<div class="col-md-5"><input type="text" id="parkingAmountModel" name="parkingAmountModel" class="form-control" readonly="readonly" ></input></div>
					<div class="col-md-1"></div>
				</div>   			

					<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Toll Tax</label></div>
					<div class="col-md-5"><input type="text" id="tollTaxModel" name="tollTaxModel" class="form-control" readonly="readonly" ></input></div>
					<div class="col-md-1"></div>
				</div>   
				
					<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>State Tax</label></div>
					<div class="col-md-5"><input type="text" id="stateTaxModel" name="stateTaxModel" class="form-control" readonly="readonly" ></input></div>
					<div class="col-md-1"></div>
				</div> 
				
					<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Service Tax</label></div>
					<div class="col-md-5"><input type="text" id="serviceTaxModel" name="serviceTaxModel" class="form-control" readonly="readonly" ></input></div>
					<div class="col-md-1"></div>
				</div> 
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Total Amount</label></div>
					<div class="col-md-5"><input type="text" id="totalAmountModel" name="totalAmountModel" class="form-control" readonly="readonly" ></input></div>
					<div class="col-md-1"></div>
				</div> 
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Closed  By</label></div>
					<div class="col-md-5"><input type="text" id="closedByModel" name="closedByModel" class="form-control" readonly="readonly" ></input></div>
					<div class="col-md-1"></div>
				</div> 
				
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-5"><label>Payment Mode</label></div>
					<div class="col-md-5"><input type="text" id="paymentModeModel" name="paymentModeModel" class="form-control" readonly="readonly" ></input></div>
					<div class="col-md-1"></div>
				</div> 
					
					
				</div>	
	   		</div>
    	 </div>
      </div>
  <!--  Closing Duty Slip Panel -->
  <div id="closingDSPanel" class="modal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg">
   		<div class="modal-content">
   			<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
   				<h4 class="modal-title" style='font-size:20px; color: #196780;  font-family: Bookman Old Style; font-weight: bold;'>Closing Duty Slip
       			<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:250%; color: #196780;">&times;</button></h4>
       		</div>
   			<div class="modal-body" id="closingDSPanel_body">
				<%@include file="dutySlipClose.jsp" %>				
				<div class="row" >
					<div class="col-sm-6" style="float: left;">
						<div id="editDetailError" style="color:red;height:70px;font-size:11px;overflow-y:auto;display:none;">
							<input type="hidden" id="dSRetreive" name="dSRetreive"/>
						</div>
						<div id="editDetailInfo" style="color:green;display:none;">
						</div>
					</div>
					<div class="col-sm-6"  style="float: right;">
						<div><input type='button' value='Save' class="btn btn-primary" id="optionValue" onClick="saveUpdateDs()" style="width:100px"/>
						<input type='button' value='Generate Invoice' class="btn btn-primary" id="buttonInvoice"  onClick="dsInvoice()" style="width:120px;"/>
						<input type='button' value='Edit' class="btn btn-primary"   id="edit" disabled="disabled" style="width:80px"/>
						<input type='button' value='Cancel' class="btn btn-primary"  data-dismiss="modal" id="cancel"  style="width:100px"/></div>
				</div>
			</div>
				<div id="dataLoaderFade"></div>
        		<div id="dataLoaderModal" style="width: 170; height: 170; background-color: transparent;">
            		<img id="loader" src="<%=request.getContextPath()%>/img/dataLoader.gif" width="98" height="98"  />
        		</div>	
	   		</div>
    	 </div>
     	</div>
     </div>
    <div id="viewInvoice" class="modal" style='min-width:100%' tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
	 <div class="modal-dialog modal-lg">
   		<div class="modal-content" style="position: absolute; left: 51%; margin-left: -403px; top: 0px; width: 731px;margin-top: -24px;height: 600px;">
   			<div class="modal-header" style="background-color:white" >
				<div id="controlDiv">
					<div id='print' style="position:absolute;top:0px;left:625px; text-align: right;" class="cls_003"><img style="cursor:pointer" id='print1' src="<%=request.getContextPath()%>/img/ic_print.png" width=35px height=35px onClick="print('S')"/></div>
					<div id='close' style="position:absolute;top:0px;left:675px; text-align: right;" class="cls_003"><img style="cursor:pointer" id="closeWindowl" src="<%=request.getContextPath()%>/img/ic_close.png" width=35px height=35px data-dismiss="modal"/></div>
				</div>
   			</div>
       		<div class="modal-body" id="viewInvoice_body"  style="width:100%;height:550;overflow:auto;">
				<div id="invoice"></div>
			</div> 
       		</div>
		</div>
	</div>	
	<div id="viewMultipleInvoice" class="modal" style='min-width:100%' tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
	 <div class="modal-dialog modal-lg">
   		<div class="modal-content" style="position: absolute; left: 51%; margin-left: -403px; top: 0px; width: 731px;margin-top: -24px; height: 600px;">
   			<div class="modal-header" style="background-color:white" >
				<div id="controlDiv">
					<div id='print' style="position:absolute;top:0px;left:625px; text-align: right;" class="cls_003"><img style="cursor:pointer" id='print1' src="<%=request.getContextPath()%>/img/ic_print.png" width=35px height=35px onClick="print('M')"/></div>
					<div id='close' style="position:absolute;top:0px;left:675px; text-align: right;" class="cls_003"><img style="cursor:pointer" id="closeWindowl" src="<%=request.getContextPath()%>/img/ic_close.png" width=35px height=35px data-dismiss="modal"/></div>
				</div>
   			</div>
       		<div class="modal-body" id="viewMultipleInvoice_body"  style="width:100%;height:550;overflow:auto;">
				<div id="multipleInvoice"></div>
			</div> 
   		</div>
	 </div>
	</div>
	
	 <!-- invoice Date Panel on DS Unbilled Screen -->
	 
	<div id="invoiceDatePanel" class="modal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="invoiceDatePanelLabel" aria-hidden="true">
	<div class="modal-dialog modal-md">
   		<div class="modal-content">
   			<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
   				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;' id="invoiceDatePanelLabel"><b>Generate</b> Invoice 
       			<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:150%; color: #196780;">&times;</button></h4>
       		</div>
   			<div class="modal-body" id="invoiceDatePanel_body" >
				<div class="row" style="margin-top: 5px">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Invoice Date : </label></div>
					<div class="col-md-2">
						<input type="text" id="dateInvoice" name="dateInvoice" value="" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'" maxlength="10" onChange="fetchTaxDetailUn(this.value)"></input>
					</div>
					<div class="col-md-5">
						<input type="text" id="invoiceDS" name="invoiceDS" class="form-control" value=" " readonly="readonly" tabindex="-1"></input>
					</div>
					<div class="col-md-1"></div>
				</div>
				<div class="row" style="margin-top: 5px">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Corporate Name : </label></div>
					<div class="col-md-7">
						<input type="text" id="corpNameDS" name="corpNameDS" class="form-control" value="" readonly="readonly" tabindex="-1"></input>
					</div>
					<div class="col-md-1"></div>
				</div>
				<div class="row" style="margin-top: 5px">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Used By : </label></div>
					<div class="col-md-7">
						<input type="text" id="usedByDS" name="usedByDS" class="form-control" value="" readonly="readonly" tabindex="-1"></input>
					</div>
					<div class="col-md-1"></div>
				</div>
				<div><hr></div>
				<div class="row" >
					<div class="col-md-1" tabindex="-1"></div>
					<div class="col-md-6" style="background-color: #FFDA71; text-align: center;" tabindex="-1">Tax Name</div>
					<div class="col-md-2" style="background-color: #FFDA71; text-align: center;" tabindex="-1">Tax %</div>
					<div class="col-md-2" style="background-color: #FFDA71; text-align: center;" tabindex="-1">Tax Amount</div>
					<div class="col-md-1" tabindex="-1"></div>
				</div>
				<div id='dsTaxDet' class="row">
					
				</div>
				<br/>	  			
				<br/>
				<div class="row" style="height: 45px;">
					<div class="col-sm-8">
						<div id="cancelReasonError" style="color:red;height: auto;font-size:11px;overflow-y:auto;display:none;"></div>
						<div id="cancelReasonInfo" style="color:green;display:none;"></div>
					</div>
					<div class="col-sm-1" style="margin-right: 50px;">
						<input type='button' value='Save' class="btn btn-primary" id="saveCR"   onClick="saveinvoiceDate()" style="width:80px; color: black;"/>
					</div>
					<div class="col-sm-1">
						<input type='button' value='Close ' class="btn btn-primary"  id="closeInvWindow"  data-dismiss="modal"  style="width:160%"/>
					</div>
				</div>	
	   		</div>
    	 </div>
   	</div>
 </div>
  
 	<!-- Invoice Date Panel  For Ds Close Window-->
	<div id="dsInvoiceDatePanel" class="modal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="dsInvoiceDatePanelLabel" aria-hidden="true">
	<div class="modal-dialog modal-md">
   		<div class="modal-content">
   			<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
   				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;' id="dsInvoiceDatePanelLabel"><b>Generate</b> Invoice 
       			<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:150%; color: #196780;">&times;</button></h4>
       		</div>
   			<div class="modal-body" id="dsInvoiceDatePanel_body" >
				<div class="row" style="margin-top: 5px">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Invoice Date : </label></div>
					<div class="col-md-2">
						<input type="text" id="dsInvoiceDate" name="dsInvoiceDate" value="" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'" onChange="fetchTaxDetail(this.value)"></input>
					</div>
					<div class="col-md-5">
						<input type="text" id="dsInv" name="dsInv" class="form-control" value=" " readonly="readonly" tabindex="-1"></input>
					</div>
					<div class="col-md-1"></div>
				</div>
				<div class="row" style="margin-top: 5px">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Corporate Name : </label></div>
					<div class="col-md-7">
						<input type="text" id="dsCorpName" name="dsCorpName" class="form-control" value="" readonly="readonly" tabindex="-1"></input>
					</div>
					<div class="col-md-1"></div>
				</div>
				<div class="row" style="margin-top: 5px">
					<div class="col-md-1"></div>
					<div class="col-md-3"><label>Used By : </label></div>
					<div class="col-md-7">
						<input type="text" id="dsUsedBy" name="dsUsedBy" class="form-control" value="" readonly="readonly" tabindex="-1"></input>
					</div>
					<div class="col-md-1"></div>
				</div>
				<div><hr></div>
				<div class="row" >
					<div class="col-md-1" tabindex="-1"></div>
					<div class="col-md-6" style="background-color: #FFDA71; text-align: center;" tabindex="-1">Tax Name</div>
					<div class="col-md-2" style="background-color: #FFDA71; text-align: center;" tabindex="-1">Tax %</div>
					<div class="col-md-2" style="background-color: #FFDA71; text-align: center;" tabindex="-1">Tax Amount</div>
					<div class="col-md-1" tabindex="-1"></div>
				</div>
				<div id='taxDet' class="row">
					
				</div>
				<br/>	  			
				<br/>		
				<div class="row" style="height: 45px;">
					<div class="col-sm-8">
						<div id="cancelReasonError" style="color:red;height: auto;font-size:11px;overflow-y:auto;display:none;"></div>
						<div id="cancelReasonInfo" style="color:green;display:none;"></div>
					</div>
					<div class="col-sm-1" style="margin-right: 50px;">
						<input type='button' value='Save' class="btn btn-primary" id="saveInvoiceDate"   onClick="saveUpdateDSForInvoicing()" style="width:80px; color: black;"/>
					</div>
					<div class="col-sm-1">
						<input type='button' value='Close ' class="btn btn-primary"  id="closeWindowl"  data-dismiss="modal"  style="width:160%"/>
					</div>
				</div>	
	   		</div>
    	 </div>
     	</div>
   </div>
 	
 	<!-- Invoice Date Panel  For Ds Close Window-->
	<div id="confirmBox" class="modal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="confirmBoxLabel" aria-hidden="true">
		<div class="modal-dialog modal-sm">
	   		<div class="modal-content " style="width: 425px;">
	   			<div class="modal-header bg-info" style='text-align:center; background-color: #FFDA71;'>
	   				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;' id="confirmBoxLabel">
	       			<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:150%; color: #196780;" onclick="closePopUp()">&times;</button></h4>
	       		</div>
	   			<div class="modal-body" id="confirmBox_body" style="height: 110px; width: 100%"></div>
	   			<div class="modal-footer">
					<button id="cancel" type="button" class="btn-danger btn" data-dismiss="modal" aria-hidden="true" style="width: 84px; " onclick="closePopUp()">No</button>
					<button id="yes" type="button" class="btn"  style="width: 84px; color: #196780;"  onclick="confirm()">Yes</button>
				</div>	
	   	 	</div>
		</div>
 	</div>
 	
 	<!-- Invoice Date Panel  For Ds Close Window-->
	<div id="coverLetterInfoPanel" class="modal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="coverLetterInfoPanelLabel" aria-hidden="true">
		<div class="modal-dialog modal-sm">
	   		<div class="modal-content " style="width: 425px;">
	   			<div class="modal-header bg-info" style='text-align:center; background-color: #FFDA71;'>
	   				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;' id="coverLetterInfoPanelLabel">
	       			<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:150%; color: #196780;" onclick="closePopUp()">&times;</button></h4>
	       		</div>
	   			<div class="modal-body" id="coverLetterInfoPanel_body" style="width: 100%">
					<div class="row" style="margin-top: 5px">
						<div class="col-md-4"><label>Submission Date</label></div>
						<div class="col-md-3">
							<input type="text" id="letterDate" name="letterDate" value="" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'" ></input>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4"><label>System Date</label></div>
						<div class="col-md-3">
							<input type="text" id="systemDate" name="systemDate" disabled value="" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'" ></input>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4"><label>Corporate Name</label></div>
						<div class="col-md-8"><span id = "corpName" name="corpName"></span>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4"><label>Billed Amount</label></div>
						<div class="col-md-4"><span id = "totalBillAmt" name="totalBillAmt"></span>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4"><label>Remark</label></div>
						<div class="col-md-8">
							<input type="text" id="coverRemarks" name="coverRemarks" value="" class="form-control" maxlength="50"></input>
						</div>
					</div>
	   			</div>
	   			<div class="modal-footer">
					<button id="yes" type="button" class="btn"  style="width: 84px; color: #196780;"  onclick="generateCoverLetter()">Yes</button>
					<button id="cancel" type="button" class="btn-danger btn" data-dismiss="modal" aria-hidden="true" style="width: 84px; " onclick="closePopUp()">No</button>
				</div>	
	   	 	</div>
		</div>
 	</div>
 	<!-- View & Print Cover Letter-->
 	<div id="viewCoverLetter" class="modal" style='min-width:100%' tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel2" aria-hidden="true">
		<div class="modal-dialog modal-lg">
	   		<div class="modal-content" style="position: absolute;overflow-y: auto;overflow-x: hidden;">
	   			<div class="modal-header">
					<div id="controlDiv">
						<div id='print' style="position:absolute;top:5px;left:725px; text-align: right;" class="cls_003"><img style="cursor:pointer" id='print1' src="<%=request.getContextPath()%>/img/ic_print.png" width=35px height=35px onClick="print('C')"/></div>
						<div id='close' style="position:absolute;top:5px;left:780px; text-align: right;" class="cls_003"><img style="cursor:pointer" id="closeWindowl" src="<%=request.getContextPath()%>/img/ic_close.png" width=35px height=35px data-dismiss="modal" onClick="closePopUp(); resetDetail();"/></div>
					</div>
	       		</div>
	       		<div class="modal-body" id="viewCoverLetter_body">
					<div id="coverLetter" style="width:100%;height:600px;overflow:auto;"></div>
				</div> 
	   		</div>
		</div>
	</div>	
   </form>
  </section>
</div>
</body>
</html>
