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
<script src= "<%=request.getContextPath()%>/js/com/operation/carDetail.js"></script>


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
	
	$(function () {
	    //Datemask dd/mm/yyyy
	    $("#bookingFromDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    $("#bookingToDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    
	    $("#bookingFromDate").val(getTimeStampTo_DDMMYYYY(new Date()));
	    $("#bookingToDate").val(getTimeStampTo_DDMMYYYY(new Date()));
	 });
	 function onLoadPage(){
		if(<%=session.getAttribute("loginUserId")%>!=null)
			loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
		assignRights();
		searchCarStatus();
		 refreshData();	 
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
				<legend>Car <strong>Status</strong></legend>
				<div class="row">
					<div class="col-md-4">
						<div class="box-body">
							<table width=100% cellspacing=1>
							<tr>
								<td width="2%"></td>
								<td width="35%"><label >From Date</label></td>
								<td width="2%"></td>
								<td width="60%">
										<td><input type="text" id="bookingFromDate"  name="bookingFromDate"  style="width:203px;margin-left:-248px" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" readOnly = "readonly" maxlength="10"></input>
								</td>
							</tr>
					</table>
				</div>		
			</div>
			    <div class="col-md-4">
						<div class="box-body">
							<table width=100%>
							<tr>
								<td width=2%></td>
								<td width="40%"><label>To Date</td>
								<td width=2%></td>
								<td width="60%">
								    <td><input type="text" id="bookingToDate"  name="bookingFromDate"  style="width:203px;margin-right: 64px" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control"  maxlength="10"></input>
								</td>
							</tr>
							</table>		
						</div>	
					</div>
					  <div class="col-md-4">
						<div class="box-body">
							<table width=100%>
							<tr>
								<td width=2%></td>
								<td width="35%"><label>Status</td>
								<td width=2%></td>
								<td width="60%">
								    <td><select class="form-control" id="status" name="status" style="width:50%;margin-right: 275px">
									<option value="X">All</option>
									<option value="A">Allocated</option>
									<option value="D">Dispatch</option>
									<option value="F">Available</option>
									</select>
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
						<input type='button'  value='Search'	 class="btn btn-primary" 	id="search"    onclick="searchCarStatus()" style="width:150px">
						<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()t" style="width:150px">
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
		<div class="row">	
			<div class="col-md-12" >
			<legend>Car <strong>Status</strong> Details</legend>
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
