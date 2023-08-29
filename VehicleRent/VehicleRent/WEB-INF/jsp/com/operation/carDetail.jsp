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
	<script src= "<%=request.getContextPath()%>/js/com/operation/carDetail.js"></script>
	<script src= "<%=request.getContextPath()%>/js/com/common/global/maintenanceDetail.js"></script>
	<style>
		td{
		    padding-top: .05em;
		    padding-bottom: .05em;
		}
	</style>
	<script>
		var loginUserId;
		var contextPath;
		var userPageAccess= '<%=sAccess%>';	
		function onLoadPage() {
			assignRights();
			var contextPath = "<%=request.getContextPath()%>";
			$('#dataTable2').html("${carDetailDataList}");
			if(<%=session.getAttribute("loginUserId")%>!=null)
				loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
			refreshData();	
		}
		$(function () {
		    //Datemask dd/mm/yyyy
		    $("#purchDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		    $("#hypothicationDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		    $("#lastDateOfEmi").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		    $("#insuEndDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		    $("#rtoEndDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		    $("#saleDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		    $("#fitnessEndDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		    $("#pucEndDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		});

	</script>
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<!-- form start -->
	<form:form commandName="carDetailModel" method="post" id="carDetailForm">
		<legend><strong>Car</strong> Master</legend>
		<div class="row">
			<div class="col-md-6">
				<div class="box-body">
					<table width=100%>
					<tr>
						<td width="2%"></td>
						<td width="35%"><form:label path="ownType" >Owner Type</form:label></td>
						<td width="2%"><font color="red">*</font></td>
						<td width="60%">
							<form:select path="ownType" id="ownType" class="form-control" onChange="fillOwner(this.value,0)">
								<form:option value="C" label="Company Car"/>
								<form:option value="V" label="Vendor Car"/>
								<form:option value="A" label="Adhoc Car"/>
							</form:select>
						</td>
						<td width="2%"></td>
					</tr>
					<tr>
						<td width="2%"></td>
						<td width="35%"><form:label path="ownName">Name</form:label></td>
						<td width="2%"><font color="red">*</font></td>
						<td width="60%">
						<div id="nameSelectBox">
							<form:select path="ownName" id="ownName" class="form-control" onChange="fillBranch(this.value,0)"  >
								<form:option value="" label="--- Select ---"/>
								<form:options items="${companyIdList}" itemValue="id" itemLabel="name"/>	                
							</form:select>
							</div>
							<div id="nameTextBox" style="display: none;"><input type="text" id="ownNameText" class="form-control"/></div>
						</td>
						<td width="2%"></td>
					</tr>		
					<tr>
						<td></td>
						<td><form:label path="branchId" >Branch</form:label></td>
						<td><Span id="branchIdSpan"  style="color:red">*</span></td>
						<td><form:select path="branchId" id="branchId" class="form-control">
									<form:option value="" label="--- Select ---"/>
									<form:options items="${branchIdList}" itemValue="id" itemLabel="name"/>	                
							</form:select>
						</td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="fuelId" >Car Version</form:label></td>
						<td><Span id="fuelIdSpan"  style="color:red">*</span></td>
						<td><form:select path="fuelId" id="fuelId" class="form-control">
									<form:option value="" label="--- Select ---"/>
									<form:options items="${fuelIdList}" itemValue="id" itemLabel="name"/>	                
							</form:select>
						</td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="model" >Car Model</form:label></td>
						<td><Span id="carModelSpan"  style="color:red">*</span></td>
						<td>									
							<table width=100%>
							<tr>
								<td width=40%>
									<form:select path="model" id="model" class="form-control">
 										<form:option value="" label="--- Select ---"/>
										<form:options items="${modalIdList}" itemValue="id" itemLabel="name"/>	
									</form:select>
								</td>
								<td width=15%><form:label path="make">Make</form:label></td>
								<td width=45% valign="top">									
									<form:select path="make" id="make" class="form-control" >
 										<form:option value="" label="--- Select ---"/>
										<form:options items="${makeIdList}" itemValue="id" itemLabel="name"/>	
									</form:select>
								</td>
							</tr>
							</table>
						</td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="registrationNo" >Car Registration No</form:label></td>
						<td><font color="red">*</font></td>
						<td><form:input path="registrationNo" id ="registrationNo" class="form-control" maxlength="20"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="engineNo" >Engine No</form:label></td>
						<td><div id="engine"><Span id="engineNoSpan"  style="color:red">*</span></div></td>
						<td><form:input path="engineNo" id="engineNo" class="form-control" maxlength="20"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="chasisNo" >Chasis No</form:label></td>
						<td><div id="chasis"><Span id="chasisNoSpan"  style="color:red">*</span></div></td>
						<td><form:input path="chasisNo" id="chasisNo" class="form-control" maxlength="20"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="keyNo">Key No</form:label></td>
						<td><div id="key"><Span id="keyNoSpan"  style="color:red">*</span></div></td>
						<td><form:input path="keyNo" id="keyNo" class="form-control" maxlength="20"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="bodyColor" >Body Color</form:label></td>
						<td><div id="bodyC"><font color="red">*</font></div></td>
						<td>									
							<table width=100%>
							<tr>
								<td width=30%>
									<form:select path="bodyColor" id="bodyColor" class="form-control">
										<form:option value="" label="--- Select ---"/>
										<form:options items="${bodyColorIdList}" itemValue="id" itemLabel="name"/>	                
									</form:select>
								</td>
								<td width=25%><form:label path="bodyStyle" >Body Style</form:label></td>
								<td width=45%>
									<form:select path="bodyStyle" id="bodyStyle" class="form-control">
											<form:option value="" label="--- Select ---"/>
										<form:options items="${bodyStyleIdList}" itemValue="id" itemLabel="name"/>	                
									</form:select>
								</td>
							</tr>
							</table>
						</td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="purchFrom" >Purchased From</form:label></td>
						<td></td>
						<td><form:input path="purchFrom" id="purchFrom" class="form-control" maxlength="50"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="purchDate" >Date of Purchase</form:label></td>
						<td></td>
						<td>									
							<table width=100%>
							<tr>
								<td width=30%><form:input path="purchDate" id="purchDate" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'" maxlength="10" onChange="fillYear(this.value)"></form:input></td>
								<td width=25%><form:label path="manufacturerYear" >Year</form:label></td>
								<td width=30%><form:input path="manufacturerYear" id="manufacturerYear" class="form-control" disabled="true" maxlength="4"></form:input></td>
							</tr>
							</table>
						</td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="exShPrice" >Ex Showroom Price</form:label></td>
						<td></td>
						<td>									
							<table width=100%>
							<tr>
								<td width=30%><form:input path="exShPrice" id="exShPrice" class="form-control" onkeypress="isDouble(event,this)" maxlength="20"></form:input></td>
								<td width=25%><form:label path="onRoadPrice" >On Road Price</form:label></td>
								<td width=30%><form:input path="onRoadPrice" id="onRoadPrice" class="form-control" onkeypress="isDouble(event,this)" maxlength="20"></form:input></td>
							</tr>
							</table>
						</td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="hypothicationTo">Hypothication</form:label></td>
						<td></td>
						<td><form:input path="hypothicationTo" id="hypothicationTo" class="form-control" maxlength="50"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="emiRs" >EMI</form:label></td>
						<td></td>
						<td><form:input path="emiRs" id="emiRs" class="form-control" onkeypress="isDouble(event,this)" maxlength="15"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="hypothicationDate" >EMI Start Date</form:label></td>
						<td></td>
						<td><form:input path="hypothicationDate" id ="hypothicationDate" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'" maxlength="10"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="lastDateOfEmi" >EMI End Date</form:label></td>
						<td></td>
						<td><form:input path="lastDateOfEmi" id="lastDateOfEmi" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'" maxlength="10"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="status" >Car Status</form:label></td>
						<td><font color="red">*</font></td>
						<td>
							<form:select path="status" id="status" class="form-control">
								<form:option value="Y" label="Active"/>
								<form:option value="N" label="InActive"/>
							</form:select>
						</td>
						<td></td>
					</tr>
					</table>				
				</div>
			</div>
			<div class="col-md-6">
				<div class="box-body">
					<table width=100%>
					<tr>
						<td width="2%"></td>
						<td width="35%"><form:label path="regAuth" >Registration Authority</form:label></td>
						<td width="2%"></td>
						<td width="60%"><form:input path="regAuth" id="regAuth" class="form-control" maxlength="50"></form:input></td>
						<td width="2%"></td>
					</tr>
					<tr>
						<td></td>
						<td><label>Insurance Policy No</label></td>
						<td></td>
						<td><input name="policyNumber" id="policyNumber" class="form-control" maxlength="20"></input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><label>Insurer Name</label></td>
						<td></td>
						<td><input name="insurerName" id="insurerName" class="form-control" maxlength="50"></input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><label>Insurance Upto</label></td>
						<td><Span id="insurenceUptoSpan"  style="color:red">*</span></td>
						<td><input name="insuEndDate" id="insuEndDate" class="form-control" maxlength="30"></input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><label>Insurance Amount</label></td>
						<td></td>
						<td><input name="premium" id="premium" class="form-control" onkeypress="isDouble(event,this)" maxlength="15"></input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><label>Permit No</label></td>
						<td></td>
						<td><input name="rtoTax" id="rtoTax" class="form-control" maxlength="20"></input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><label>Permit Upto</label></td>
						<td><Span id="permitUptoSpan"  style="color:red">*</span></td>
						<td><input name="rtoEndDate" id="rtoEndDate" class="form-control"  data-inputmask="'alias': 'dd/mm/yyyy'" maxlength="10"></input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><label>Fitness Certificate No</label></td>
						<td></td>
						<td><input name="fitnessNo" id="fitnessNo" class="form-control" maxlength="20"></input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><label>Fitness Upto</label></td>
						<td><Span id="fitnessUptoSpan"  style="color:red">*</span></td>
						<td><input name="fitnessEndDate" id="fitnessEndDate" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'" maxlength="10"></input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><label>PUC Upto</label></td>
						<td><Span id="pucUptoSpan"  style="color:red">*</span></td>
						<td><input name="pucEndDate" id="pucEndDate" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'" maxlength="10"></input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="gpsEnabled" >GPS Enabled</form:label></td>
						<td></td>
						<td>
							<form:select path="gpsEnabled" id="gpsEnabled" class="form-control">
								<form:option value="Y" label="Yes"/>
								<form:option value="N" label="No"/>
							</form:select>
						</td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="gpsCompany" >GPS Company</form:label></td>
						<td></td>
						<td><form:input path="gpsCompany" id="gpsCompany" class="form-control" maxlength="50"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="saleDate" >Date of Sale</form:label></td>
						<td></td>
						<td><form:input path="saleDate" id="saleDate" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'" maxlength="10"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="byerName" >Byer Name</form:label></td>
						<td></td>
						<td><form:input path="byerName" id="byerName" class="form-control" maxlength="50"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="byerMobile" >Byer Mobile</form:label></td>
						<td></td>
						<td><form:input path="byerMobile" id="byerMobile" class="form-control" maxlength="30"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="saleAmt" >Selling Amount</form:label></td>
						<td></td>
						<td><form:input path="saleAmt" id="saleAmt" class="form-control" onkeypress="isDouble(event,this)" maxlength="15"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="minBusReq" >Minimum Business Reqd</form:label></td>
						<td></td>
						<td><form:input path="minBusReq" id="minBusReq" class="form-control" onkeypress="isDouble(event,this)" maxlength="15"></form:input></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td><form:label path="minKmsReq" >Min. Kms Reqd</form:label></td>
						<td></td>
						<td>									
							<table width=100%>
							<tr>
								<td width=30%><form:input path="minKmsReq" id="minKmsReq" class="form-control" onkeypress="isDouble(event,this)" maxlength="15"></form:input></td>
								<td width=25%><form:label path="mileage" >Mileage</form:label></td>
								<td width=30%><form:input path="mileage" id="mileage" class="form-control" onkeypress="isDouble(event,this)" maxlength="10"></form:input></td>
							</tr>
							</table>
						</td>
						<td></td>
					</tr>
					</table>
				</div>	
			</div>
		</div>	
		<div class="row">
			<div class="col-md-3"></div>
			<div class="col-md-6">
				<div class="box-body">
				<input type='button'  value='Save'	 class="btn btn-primary" 	id="addButton"    onclick="saveOrUpdate()" style="width:150px">
				<input type='button'  value='Update' class="btn btn-primary"	id="updateButton" onclick="saveOrUpdate()" disabled style="width:150px">
				<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:150px">
				<input type="hidden" id="idForUpdate" name="idForUpdate" value="0">
				<input type="hidden" id="policyId" name="policyId" value="0">
				<input type="hidden" id="rtoTaxId" name="rtoTaxId" value="0">
				<input type="hidden" id="fitnessId" name="fitnessId" value="0">
				<input type="hidden" id="PUCId" name="PUCId" value="0">
				</div>
			</div>
			<div class="col-md-3">
				<div class="box-body">
					<div style="width:95%;height:60px;overflow:auto;display:none" id="error" class="error"></div>
					<div style="width:100%;height:20px;display:none" id="info" class="success"></div>
				</div>
			</div>
		</div>
		<div class="row">	
			<div class="col-md-12" >
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
