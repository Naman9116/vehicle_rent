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
<script src= "<%=request.getContextPath()%>/js/com/report/report.js"></script>
<script src= "<%=request.getContextPath()%>/js/com/common/moment.js"></script>
<script>
	$(function () {
	    //Datemask dd/mm/yyyy
	    $("#invoiceFromDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    $("#invoiceToDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    $("#invoiceFromDate").val(firstDayOfMonth(new Date()));
	   	$("#invoiceToDate").val(getTimeStampTo_DDMMYYYY(new Date())); 
	});
 
	var loginUserId = '${loginUserId}';
	var carType = "";
	var userPageAccess= '<%=sAccess%>';	
	
	function onLoadPage(){
		if(<%=session.getAttribute("loginUserId")%>!=null)
			loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
		assignRights();
		var contextPath="<%=request.getContextPath()%>";
	}
	
</script>
</head>
<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<form id="bookingMasterForm" method="post">
		<div class="row">
			<legend><strong><span id="captionText">Invoice | Duty Slip Data Export</span></strong></legend>
			<div class="col-md-12" style="background-color: #F6F6F3;">
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
							<option value="0"> - ALL - </option>
							<c:forEach var="branch" items="${branchList}">
								<option value = '${branch.id}'>${branch.name}</option>
							</c:forEach>	
						</select>
					</td>
					
					<td><label>Hub</label></td>
					<td>
						<select class="form-control" id="outlet" style="width:100%">
							<option value="0"> - ALL - </option>							
						</select>
					</td>
					
					<td><label>Booking Mode</label></td>
					<td>
						<select class="form-control" id="mob" style="width:100%">
							<option value="0"> - ALL - </option>
							<option value="1">PrePaid</option>
							<option value="2">PostPaid</option>
						</select>
					</td>
					<td><label>Used By.</label></td>
					<td>
						<select class="form-control" id="usedBy" name="usedBy" style="width:100%">
							<option value="0"> - ALL - </option>
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
			</div>
		</div>		
	</form>
	</section>	

    <div id="viewInvoice" class="modal" style='min-width:100%' tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
	 <div class="modal-dialog modal-lg">
   		<div class="modal-content" style="position: absolute; left: 51%; margin-left: -403px; top: 0px; width: 731px;margin-top: -24px; overflow-y: auto;overflow-x: hidden; height: 998px;">
       		<div class="modal-body" id="viewInvoice_body">
				<div id="invoice" style="width:100%;overflow:auto;"></div>
			</div> 
       		</div>
		</div>
	</div>	

	<div id="viewMultipleInvoice" class="modal" style='min-width:100%' tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
	 <div class="modal-dialog modal-lg">
   		<div class="modal-content" style="position: absolute; left: 51%; margin-left: -403px; top: 0px; width: 731px;margin-top: -24px; overflow-y: auto;overflow-x: hidden; height: 998px;">
   			<div class="modal-header bg-info" style="background-color:white" >
				<div id="controlDiv">
					<div id='print' style="position:absolute;top:0px;left:625px; text-align: right;" class="cls_003"><img style="cursor:pointer" id='print1' src="<%=request.getContextPath()%>/img/ic_print.png" width=35px height=35px onClick="print('M')"/></div>
					<div id='close' style="position:absolute;top:0px;left:675px; text-align: right;" class="cls_003"><img style="cursor:pointer" id="closeWindowl" src="<%=request.getContextPath()%>/img/ic_close.png" width=35px height=35px data-dismiss="modal"/></div>
				</div>
   			</div>
       		<div class="modal-body" id="viewMultipleInvoice_body" >
				<div id="multipleInvoice" style="width:100%;overflow:auto;"></div>
			</div> 
   		</div>
	 </div>
	</div>
</body>
</html>