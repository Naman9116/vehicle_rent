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
<script src= "<%=request.getContextPath()%>/js/com/toursTravels/customerMaster.js"></script>


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
	var loginCompanyId;
    var sTariff;
	var userPageAccess= '<%=sAccess%>';	
	
	function onLoadPage(){
	 	$('#dataTable2').html("${customerList}"); 
		if(<%=session.getAttribute("loginUserId")%>!=null)
			loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
		if(<%=session.getAttribute("loginCompanyId")%>!=null)
			loginCompanyId='<%=(Long) session.getAttribute("loginCompanyId")%>';
		assignRights();
		/* resetData(); */
	}
</script>
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
		<form:form commandName="customerModel" method="post" id="customerForm">
		<div class="row">
			<div class="col-md-12">
				<legend>Customer <strong>Master</strong></legend>
				<div class="row">
					<div class="col-md-6">
						<div class="box-body">
							<table width=100% cellspacing=1>
							<tr>
								<td width="2%"></td>
								<td width="35%"><form:label path="name"> Name</form:label></td>
								<td width="2%"><font color="red">*</font></td>
								<td width="60%">
									<form:input path="name" id="name"  class="form-control" maxlength="50"></form:input>
								</td>
							</tr>
							<tr>
								<td width="2%"></td>
								<td width="35%"><form:label path="company">Company Name</form:label></td>
								<td width="2%"><font color="red">*</font></td>
								<td width="60%">
									<form:select path="company" id="compId" class="form-control">
 										<form:option value="0" label="--- Select ---"/>
										<form:options items="${companyIdList}" itemValue="id" itemLabel="name"/>	
									</form:select>
								</td>
							</tr>
							<tr>
								<td width="2%"></td>
								<td width="35%"><form:label path="mobileNo">Mobile</form:label></td>
								<td width="2%"><font color="red">*</font></td>
								<td width="60%">
									<form:input path="mobileNo" id="mobileNo"  class="form-control" maxlength="50"></form:input>
								</td>
							</tr>
							<tr>
								<td width="2%"></td>
								<td width="35%"><form:label path="email" >Email</form:label></td>
								<td width="2%"><font color="red">*</font></td>
								<td width="60%">
									<form:input path="email" id="email"  class="form-control" maxlength="50"></form:input>
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
						<input type='button'  value='Save'	 class="btn btn-primary" 	id="addButton"    onclick="saveOrUpdateCustomer()" style="width:150px">
						<input type='button'  value='Update' class="btn btn-primary"	id="updateButton" onclick="saveOrUpdateCustomer()" disabled style="width:150px">
						<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:150px">
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
			<legend>Existing <strong>Customer</strong> Details</legend>
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
