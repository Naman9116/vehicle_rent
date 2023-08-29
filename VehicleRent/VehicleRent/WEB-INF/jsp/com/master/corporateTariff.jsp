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
<script src= "<%=request.getContextPath()%>/js/com/master/corporateTariff.js"></script>
<script src= "<%=request.getContextPath()%>/js/com/common/global/addressDetail.js"></script>

<script>
	var loginUserId = '${loginUserId}';
	var carType = "";
	var userPageAccess= '<%=sAccess%>';	
	function onLoadPage(){
		assignRights();
		$('#tariffInput').html("${carCatList}");
	}
	$(function () {
	    //Datemask dd/mm/yyyy
	    $("#fuelHikeDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	});
</script>
</head>
<BODY onload="onLoadPage()" >
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<form:form commandName="corporateTariffModel" method="post">
		<div class="row">
			<div class="col-md-12">
				<legend>Corporate <strong>Tariff Master</strong></legend>
				<div class="box box-primary" >
					<table width=100% >
					<tr height=5px ></tr>
					<tr>
						<td width=1%></td>
						<td width=8% >
							<form:select path="branchId" class="form-control" onChange="AssignCorporate(this.value,0)" >
								<form:option value="0" label="-- Select Branch --"/>
								<form:options items="${branchList}" itemValue="id" itemLabel="name"/>	                
							</form:select>
						</td>
						<td width=1%></td>
						<td width=15% >
							<form:select path="corporateId" class="form-control" onChange="corporateTariff(this.value)" >
								<form:option value="0" label="--- Select Corporate ---"/>
								<form:options items="${corporateList}" itemValue="id" itemLabel="name"/>	                
							</form:select>
						</td>
						<td width=1%></td>
						<td width=10% >
							<select id="fuelHikeDt" class="form-control" onChange="corporateTariffAsPerDate(this.value)" >
								<option value="0">-- Fuel Hike Date --</option>
							</select>
						</td>
						<td width=1%><form:label path="gstin">GSTIN</form:label></td>
						<td width=10%><form:input path="gstin" style="width:100%;" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="15"></form:input></td>

						<td width=1%></td>
						<td width=6%><form:label path="fuelRate">Fuel Rate</form:label></td>
						<td width=4%><form:input type="text" path="fuelRate" name ="fuelRate" class="form-control" onkeypress="isDouble(event,this)"></form:input></td>
						<td width=1%></td>
						<td width=8%><form:label path="currFuelRate">Current Fuel Rate</form:label></td>
						<td width=4%><form:input type="text" path="currFuelRate" name ="currFuelRate" class="form-control" onkeypress="isDouble(event,this)"></form:input></td>
						<td width=1%></td>
						<td width=8%><form:label path="fuelHikeDate">Fuel Hike Date</form:label></td>
						<td width=6%><form:input type="text" path="fuelHikeDate" id="fuelHikeDate" name ="fuelHikeDate" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'" onChange="changeState()" maxlength="10"></form:input></td>
						<td width=1%></td>
					</tr>
					</table>
					<div style="height:5px"></div>
					<div style="margin-left:1%"><label>Corporate Address</label></div>					
					<table width=100% >
						<tr>
							<td width=1%></td>
							<td width=25%><input id="address1" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="250" placeholder="Address 1"></input></td>
							<td width=10%><input id="address2" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="250" placeholder="Address 2"></input></td>
							<td width=10%><input id="landMark" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="200" placeholder="Landmark"></input></td>
							<td width=5%><input id="pincode" class="form-control" onkeypress='isNumeric(event)' maxlength="6" placeholder="PIN" onblur='getStateDistMasterData_usingPincode(this.value)'></input></td>
							<td width=10%>
								<Select NAME='state'  id='state' class="form-control" onchange='getDistrictData(this.value,0)' >
									<option value = '0'> Select State</option>
									<c:forEach var="stateMaster" items="${stateMasterList}">
										<option value = '${stateMaster.id}'>${stateMaster.name}</option>
									</c:forEach>	
								</select>
							</td>
							<td width=10%>
								<div id='distictDiv' name='distictDiv'> 
									<Select NAME='district'  id='district' class="form-control" onchange='getCityData(this.value,0)'>
										<option value = '0'>Select District</option>
									</select> 
								</div>
							</td>
							<td width=10%>
								<div id='cityDiv' name='cityDiv' > 
									<Select NAME='city'  id='city' class="form-control" >
										<option value = '0'>Select City</option>
									</select> 
								</div>
							</td>
						</tr>
					</table>
					<div class="box-body" id="tariffInput" style="max-height:65%;width:100%;overflow:auto"></div>
					<input type='hidden' name='addressDetailModel_id' id='addressDetailModel_id'/>						
				</div>
			</div>
		</div>								
		<div class="row">
			<div class="col-md-3"></div>
			<div class="col-md-6">
				<div class="box-body">
					<input type='button'  value='Save'	 class="btn btn-primary" 	id="addButton"    onclick="saveOrUpdateCorporateTariff()" style="width:150px;" >
					<input type='button'  value='Update' class="btn btn-primary"	id="updateButton" onclick="saveOrUpdateCorporateTariff()" style="width:150px">
					<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:150px">
					<input type="hidden" id="idForUpdate" name="idForUpdate" value="">
				</div>
			</div>
			<div class="col-md-3">
				<div class="box-body">
					<div style="width:95%;height:60px;overflow:auto;display:none" id="error" class="error"></div>
					<div style="width:100%;height:20px;display:none" id="info" class="success"></div>
				</div>
			</div>
		</div>
			
	</form:form>
	</section>
</div>
</body>
</html>

