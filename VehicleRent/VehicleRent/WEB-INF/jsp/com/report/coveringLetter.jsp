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
	    $("#bookingFromDate").val(getTimeStampTo_DDMMYYYY(new Date()));
		$("#bookingToDate").val(getTimeStampTo_DDMMYYYY(new Date()));
	    $("#coverDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    $("#coverDate").val(getTimeStampTo_DDMMYYYY(new Date()));
	 

	});
	 
	function onLoadPage(){
		if(<%=session.getAttribute("loginUserId")%>!=null)
			loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
		assignRights();
		var contextPath="<%=request.getContextPath()%>";
	}
	</script>
</HEAD>

<BODY onload="onLoadPage()">
	<div class="content-wrapper">
		<!-- Main content -->
		<section class="content">
			<form id="bookingMasterForm" method="post">
				<div class="row">
					<legend><strong>Covering Letter Report</strong></legend>
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
								
								<td><label>To Date</label></td>
								<td><input type="text" id="bookingToDate"  name="bookingToDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10"></input></td>
								
								<td width=10%><label>Corporate Name</label></td>
								<td width=14%>
									<select class="form-control" id="corporateId" style="width:100%" onChange="fillValues(this.value)">
										<option value="0">ALL</option>
										<c:forEach var="customerOrCompanyNameId" items="${customerOrCompanyNameIdList}">
											<option value = '${customerOrCompanyNameId.id}'>${customerOrCompanyNameId.name}</option>
										</c:forEach>	
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
							
							</tr>
							<tr height="30px">	
								<td colspan=12 align="right">
									<input type='button'  value='Search'	 class="btn btn-primary" 	id="searchButton"    onclick="searchReport('CoverLetter')" style="width:70px">
									<input type='button'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetDetail()" style="width:70px">
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
					<div class="col-md-12">
						<div id="dataTable2" style="width:100%"></div>
					</div>	
				</div>	
			</form>
		</section>
	</div>	
 	<!-- View & Print Cover Letter-->
 	<div id="viewCoverLetter" class="modal" style='min-width:100%' tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel2" aria-hidden="true">
		<div class="modal-dialog modal-lg">
	   		<div class="modal-content" style="position: absolute;overflow-y: auto;overflow-x: hidden;">
	   			<div class="modal-header">
					<div id="controlDiv">
						<div id='print' style="position:absolute;top:5px;left:725px; text-align: right;" class="cls_003"><img style="cursor:pointer" id='print1' src="<%=request.getContextPath()%>/img/ic_print.png" width=35px height=35px onClick="print('C')"/></div>
						<div id='close' style="position:absolute;top:5px;left:780px; text-align: right;" class="cls_003"><img style="cursor:pointer" id="closeWindowl" src="<%=request.getContextPath()%>/img/ic_close.png" width=35px height=35px data-dismiss="modal" onClick="closePopUp()"/></div>
					</div>
	       		</div>
	       		<div class="modal-body" id="viewCoverLetter_body">
					<div id="coverLetter" style="width:100%;height:600px;overflow:auto;"></div>
				</div> 
	   		</div>
		</div>
	</div>	
</BODY>
</HTML>
