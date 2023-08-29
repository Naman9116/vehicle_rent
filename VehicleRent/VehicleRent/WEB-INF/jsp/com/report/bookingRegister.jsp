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
	<script src= "<%=request.getContextPath()%>/js/com/report/report.js"></script>
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
	    $("#bookingFromDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    $("#bookingToDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    $("#bookingFromDate").val(firstDayOfMonth(new Date()));
		$("#bookingToDate").val(getTimeStampTo_DDMMYYYY(new Date()));
	});
	 
	function onLoadPage(){
		if(<%=session.getAttribute("loginUserId")%>!=null)
			loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
		assignRights();
		var contextPath="<%=request.getContextPath()%>";
		searchReport('Invoices');
	}
	</script>
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<form id="bookingMasterForm" method="post">
		<div class="row">
		<legend>Booking <strong>Register</strong></legend>
			<div class="col-md-12" style="background-color: #F6F6F3;">
			<table style="width: 100%;">
				<tr height="25px"> <!-- First Line -->
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
					
					<td width=8%><label>From Date</label></td>
					<td width=10%><input type="text" id="bookingFromDate"  name="bookingFromDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10" ></input></td>
					
					<td width=8%><label>To Date</label></td>
					<td width=10%><input type="text" id="bookingToDate"  name="bookingToDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10"></input></td>
					
					<td width=10%><label>Date Range For</label></td>
					<td width=14%>
						<select class="form-control" id="dateRangeFor" name ="dateRangeFor" style="width:100%"">
							<option value="I" selected>Invoices</option>
							<option value="D">Duty Slip</option>
						</select>
					</td>

					<td width=10%><label>Corporate Name</label></td>
					<td width=14%>
						<select class="form-control" id="corporateId" style="width:100%" onChange="fillValues(this.value)">
							<option value="0">ALL</option>
							<c:forEach var="customerOrCompanyNameId" items="${customerOrCompanyNameIdList}">
								<option value = '${customerOrCompanyNameId.id}'>${customerOrCompanyNameId.name}</option>
							</c:forEach>	
						</select>
					</td>
				</tr>
				<tr height="30px">	
					<td colspan=12 align="right">
						<input type='button'  value='Search'	 class="btn btn-primary" 	id="searchButton"    onclick="searchReport('Invoices')" style="width:70px">
						<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:70px">
					</td>										
				</tr>
				</table>
			<table width=100%>
					<tr>
						<td width=35%><div style="width:95%;height:30px;overflow:auto;display:none" id="error" class="error"></div></td>
						<td width=35%><div style="width:100%;height:20px;display:none" id="info" class="success"></div></td>
					</tr>
				</table>
				<hr>
			</div>
		</div>
		<div class="row">	
		<div class="col-md-12" >
			<div id="dataTable2" style="width:100%;overflow:auto;"></div>
		</div>
		</div>
	</form>
	</section>
</div>	

</BODY>
</HTML>
