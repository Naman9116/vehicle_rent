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
	<script src= "<%=request.getContextPath()%>/js/com/master/carAllocation.js"></script>
	<style>
	td{
	    padding-top: .05em;
	    padding-bottom: .05em;
	}
	textarea {
    	resize: none;
	}
</style>
	<script language="JavaScript">
		
		var loginUserId;
		var contextPath;
		var userPageAccess= '<%=sAccess%>';	
		function onLoadPage(){
			assignRights();
			contextPath="<%=request.getContextPath()%>";
			$('#dataTable2').html("${existingData}");
			refreshData();
			if(<%=session.getAttribute("loginUserId")%>!=null)
				loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
			refreshData();
		}
		$(function () {
		    //Datemask dd/mm/yyyy
		    $("#doa").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		    $("#dod").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		    satuts();
		});
		
	</script>
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<!-- form start -->
	<form:form commandName="carAllocationModel" method="post" id="carAllocationForm">
		<div class="row">
			<legend>Car Allocation <strong>Master</strong></legend></br>
			<div class="col-md-12">
				<table style="text-align:center" style="width: 80%;">
				<tr> <!-- First Line -->
					<td  style="width: 10%"><form:label path="carDetailModelId">Car Number</form:label><span style="color: red">*</span></td>
					<td  style="width: 16%;" id='carRegNumber'>
	                    <form:select class="form-control" path="carDetailModelId" id="carNumber" style="width:100%" onchange="fillCarRelatedDetails(this.value)">
							<form:option value="0" >-- Select --</form:option>
							<c:forEach var="carDetailsList" items="${carDetailsList}">
								<form:option value = '${carDetailsList.registrationNo}'>${carDetailsList.registrationNo}</form:option>
							</c:forEach>	
						</form:select>
					</td>
					<td style="display: none; width: 16%;" id='carRegNo'><form:input type="text"  path="carDetailModelId" id="carNumberText" name="carNumber"  class="form-control" readonly="true" ></form:input></td>
					<td  width="8%"></td>
					<td  style="width: 10%"><form:label path="paymentTraffic">Payment Tariff</form:label><span style="color: red">*</span></td>
					<td  style="width: 16%">
	                    <form:select class="form-control"  path="paymentTraffic" id="paymentTraffic" style="width:100%" >
							<form:option value="0">-- Select --</form:option>
							<c:forEach var="paymentTrafficList" items="${paymentTrafficList}">
								<form:option value = '${paymentTrafficList.id}'>${paymentTrafficList.name}</form:option>
							</c:forEach>	
						</form:select>
					</td>
				</tr>		
				<tr> <!-- Second Line -->
					<td><form:label path="carDetailModelId">Owner Type</form:label><span style="color: red">*</span></td>
					<td><form:input type="text"  path="carDetailModelId" id="ownerType" name="ownerType"  class="form-control" readonly="true"></form:input></td>
					<td  width="8%"></td>
					<td><form:label path="companyShare">Company Share (%)</form:label></td>
					<td>
						<table style="width: 100%;">
						<tr style="border: 1px solid #EEEEEE">	
							<td width="5%"><form:input type="text" path="companyShare" id="compShare" name="compShare"  class="form-control percentage" value="0" onkeypress="isNumeric(event)" maxlength="3" ></form:input></td>
							<td width="10%" style="border: 2px solid #EEEEEE;text-align: center;"><form:label path="vendorShare">Vendor Share (%)</form:label></td>
							<td width="5%"><form:input type="text" path="vendorShare" id="vendorShare" name="vendorShare"  class="form-control percentage" value="100" onkeypress="isNumeric(event)" maxlength="3"></form:input></td>
						</tr></table>
					</td>
					
				</tr>
				<tr><!--  3rd Line -->
					<td><form:label path="vendorId">Vendor Name</form:label><span style="color: red">*</span></td>
					<td>
	                    <form:select class="form-control" path="vendorId" id="vendorName" style="width:100%" >
							<form:option value="0">-- Select --</form:option>
							<c:forEach var="vendorList" items="${vendorList}">
								<form:option value = '${vendorList.id}'>${vendorList.name}</form:option>
							</c:forEach>	
						</form:select>
					</td>
					
					<td  width="8%"></td>
					<td><form:label path="dutyAllow">Duties Allow</form:label><span style="color: red">*</span></td>
					<td>
							<form:select path="dutyAllow" id="dutyAllow" class="form-control" >
									<form:option value="Y" label="Yes"/>
									<form:option value="N" label="No"/>
							</form:select>
					</td>
				</tr>	
				<tr>
					<td><form:label path="chauffeurId">Chauffeur Name</form:label><span style="color: red">*</span></td>
					<td>
							<form:select path="chauffeurId" id="chauffeurId" class="form-control" style="width:100%"  >
									<form:option value="0">-- Select --</form:option>
										<c:forEach var="chauffeurList" items="${chauffeurList}">
											<form:option value = '${chauffeurList.id}'>${chauffeurList.name}</form:option>
										</c:forEach>	
							</form:select>
					</td>
					
					<td  width="8%"></td>
					<td><form:label path="carStatus">Car Status</form:label><span style="color: red">*</span></td>
					<td>
							<form:select path="carStatus" id="carStatus" class="form-control" onchange="satuts()">
									<form:option value="Y" label="Active"/>
									<form:option value="N" label="InActive"/>
							</form:select>
					</td>
				</tr>
				<tr><!-- 4th Line -->
					<td><form:label path="carOwnerType">Car Owner Type</form:label><span style="color: red">*</span></td>
					<td>
	                    <form:select class="form-control" path="carOwnerType" id="carOwnerType" style="width:100%" >
							<form:option value="0" >-- Select --</form:option>
							<c:forEach var="carOwnerTypeList" items="${carOwnerTypeList}">
								<form:option value = '${carOwnerTypeList.id}'>${carOwnerTypeList.name}</form:option>
							</c:forEach>	
						</form:select>
					</td>
					
					<td  width="8%"></td>
					<td><form:label path="dateOfDeallocation">Date of Deallocation</form:label></td>
					<td><form:input type="text" path="dateOfDeallocation" id="dod" name="dod"  class="form-control"  ></form:input></td>
				</tr>
				
				<tr> <!-- 5th Line -->
					<td><form:label path="dateOfAllotment">Date of Allotment</form:label><span style="color: red">*</span></td>
					<td><form:input type="text" path="dateOfAllotment" id="doa" name="doa"  class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'"  maxlength="10"></form:input></td>
					
					<td  width="8%"></td>
					<td><form:label path="reason">Reason</form:label></td>
					<td><form:input type="text" path="reason" id="reason" name="reason"  class="form-control"  ></form:input></td>
				</tr>	
						
				<tr> <!-- 6th Line -->
					<td><form:label path="carDetailModelId">Car Make</form:label><span style="color: red">*</span></td>
					<td><form:input type="text" path="carDetailModelId" id="carMake" name="carMake"  class="form-control"  readonly="true"></form:input></td>
				</tr>

				<tr> <!-- 7th Line -->
					<td><form:label path="carDetailModelId">Car Model</form:label><span style="color: red">*</span></td>
					<td><form:input type="text" path="carDetailModelId" id="carModel" name="carModel"  class="form-control"  readonly="true"></form:input></td>
				</tr>		
				<tr> <!-- 8th Line -->
					<td><form:label path="carDetailModelId">Body Color</form:label><span style="color: red">*</span></td>
					<td><form:input type="text" path="carDetailModelId"  id="bodyColor" name="bodyColor"  class="form-control"  readonly="true"></form:input></td>
				</tr>
				</table>
					
			</div><!-- /.box -->
		<div class="row">
			<div class="col-md-3" style="margin-left: 15px;">
						<div id="carAllocationError" style="color:red;height: auto;font-size:11px;overflow-y:auto;display:none;"></div>
						<div id="carAllocationInfo" style="color:green;display:none;"></div>
			</div>
			<div class="col-md-6" style="float: right;">
				<div class="box-body">
				<input type='button'  value='Save'	 class="btn btn-primary" 	id="addButton"    onclick="saveOrUpdate()" style="width:150px">
				<input type='button'  value='Update' class="btn btn-primary"	id="updateButton" onclick="saveOrUpdate()" disabled style="width:150px">
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
		<div class="row" style="margin-left: 2px;">
			<div class="col-md-12" >
				<div class="box box-primary">
					<div class="box-body" id="dataTable2" style="overflow:auto">
					</div>
				</div>
			</div>
		</div>	
	</form:form>
	</section><!-- /.content -->
</div><!-- /.content-wrapper -->
</BODY>
</HTML>
