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
<script src= "<%=request.getContextPath()%>/js/com/corporate/corporateMaster.js"></script>
<script src= "<%=request.getContextPath()%>/js/com/common/global/contactDetail.js"></script>
<script src= "<%=request.getContextPath()%>/js/com/common/global/addressDetail.js"></script>

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
	
	function onLoadPage(){
		$('#dataTable2').html("${corporateDataList}");
		if(<%=session.getAttribute("loginUserId")%>!=null)
			loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
		assignRights();
		resetData();
		$("#billingCycle").val(186);
	}
	$(function () {
	    //Datemask dd/mm/yyyy
	    $("#agreementDt").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    $("#expiryDt").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	    $("#discountId").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"}); 
	    
	});
    $(document).ready(function () {
        window.asd = $('.SlectBox').SumoSelect({ csvDispCount: 3 });
    });
</script>
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
		<form:form commandName="corporateModel" method="post" id="corporateForm">
		<div class="row">
			<div class="col-md-12">
				<legend>Corporate <strong>Master</strong></legend>
				<div class="row">
					<div class="col-md-6">
						<div class="box-body">
							<table width=100% cellspacing=1>
							<tr>
								<td width="2%"></td>
								<td width="35%"><form:label path="compId">Company Name</form:label></td>
								<td width="2%"><font color="red">*</font></td>
								<td width="60%">
									<form:select path="compId" id="compId" class="form-control" onChange="getBranchList(this.value,0)">
 										<form:option value="" label="--- Select ---"/>
										<form:options items="${companyIdList}" itemValue="id" itemLabel="name"/>	
									</form:select>
								</td>
							</tr>
							<tr>
								<td width="2%"></td>
								<td width="35%"><form:label path="branId">Branch Name</form:label></td>
								<td width="2%"><font color="red">*</font></td>
								<td width="60%">
									<table width=100%>
									<tr>
										<td width=70%>
											<form:select path="branId" id="branId" class="form-control">
		 										<form:option value="" label="--- Select ---"/>
												<form:options items="${branchIdList}" itemValue="id" itemLabel="name"/>	
											</form:select>
										</td>
										<td width=25%><form:label path="penIndia">PanIndia</form:label></td>
										<td width=5% valign="top"><form:input type='checkbox' path="penIndia" name='penIndia' id = 'penIndia' onchange="changeBranchState(this)"></form:input></td>
									</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td width="2%"></td>
								<td width="35%"><form:label path="name" >Corporate Name</form:label></td>
								<td width="2%"><font color="red">*</font></td>
								<td width="60%">
									<form:input path="name" id="name"  class="form-control" maxlength="70"></form:input>
								</td>
							</tr>
							<%-- <tr>
								<!-- <td></td> -->
								<td><form:label path="agreementDt" >Agreement Date</form:label></td>
								<td><font color="red">*</font></td>
								<td><form:input path="agreementDt" type="text" style="width:100%" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'"  maxlength="10"></form:input></td>
								<td><form:label path="agreementDt" >Passenger Info</form:label></td>
							</tr> --%>
							<tr>
								<td width="2%"></td>
								<td width="35%"><form:label path="branId">Agreement Date</form:label></td>
								<td width="2%"><font color="red">*</font></td>
								<td width="60%">
									<table width=100%>
									<tr>
										<td width=70%>
											<form:input path="agreementDt" type="text" style="width:100%" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'"  maxlength="10"></form:input>
										</td>
										<td width=25%><form:label style="display:none" path="isPassengerInfo">Passenger-Info</form:label></td>
										<td width=5% valign="top"><form:input type='checkbox' path="isPassengerInfo" name='passengerInfo' id = 'passengerInfo' style="display:none" ></form:input></td>
									</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td></td>
								<td><form:label path="expiryDt">Expiring Date</form:label></td>
								<td width="2%"><font color="red">*</font></td>
								<td><form:input path="expiryDt" type="text" style="width:100%" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'"  maxlength="10"></form:input></td>
							</tr>
							
														<tr>
								<td></td>
								<td><label>Calculate S.Tax</label></td>
								<td></td>
								<td><table width=100%>
									<tr>
										<td width=20%><form:label path="sTaxCalBill">Billing</form:label></td>
										<td width=10% valign="top"><form:input type='checkbox' path="sTaxCalBill" name='sTaxCalBill' id = 'sTaxCalBill'></form:input></td>
										<td width=35%><form:label path="sTaxCalFuel">Fuel Surcharge</form:label></td>
										<td width=5% valign="top"><form:input type='checkbox' path="sTaxCalFuel" name='sTaxCalFuel' id = 'sTaxCalFuel'></form:input></td>
										<td width=30%><form:label path="sTaxCalPark">Parking</form:label></td>
										<td width=5% valign="top"><form:input type='checkbox' path="sTaxCalPark" name='sTaxCalPark' id = 'sTaxCalPark'></form:input></td>
									</tr>
									</table>								
								</td>
							</tr>
							<tr>
								<td></td>
								<td><label>Calculate Discount</label></td>
								<td></td>
								<td><table width=100%>
									<tr>
										<td width=20%><form:label path="sDiscCalBill">Billing</form:label></td>
										<td width=10% valign="top"><form:input type='checkbox' path="sDiscCalBill" name='sDiscCalBill' id = 'sDiscCalBill'></form:input></td>
										<td width=35%><form:label path="sDiscCalKm">Extra KM</form:label></td>
										<td width=5% valign="top"><form:input type='checkbox' path="sDiscCalKm" name='sDiscCalKm' id = 'sDiscCalKm'></form:input></td>
										<td width=30%><form:label path="sDiscCalHrs">Extra Hrs</form:label></td>
										<td width=5% valign="top"><form:input type='checkbox' path="sDiscCalHrs" name='sDiscCalHrs' id = 'sDiscCalHrs'></form:input></td>
									</tr>
									</table>								
								</td>
							</tr>
							<tr>
								<td></td>
								<td><form:label path="vatOnPark">Calculate VAT on Parking</form:label></td>
								<td></td>
								<td><table width=100%>
									<tr>
										<td width=10%><form:input type='checkbox' path="vatOnPark" name='vatOnPark' id = 'vatOnPark'></form:input></td>
								        <td width=30%><label>Tariff Plan</label></td>
								        <td width="2%"><font color="red">*</font></td>
								        <td width=60%>
								        	<select multiple="multiple" placeholder="Select Tariff" class="SlectBox" id="tariffs" name="tariffs" onChange="checkForSelect()">
												<c:forEach var="tariffScheme" items="${tariffScheme}">
													<option value = '${tariffScheme.id}'>${tariffScheme.name}</option>
												</c:forEach>	
												<c:forEach var="tariffCharges" items="${tariffCharges}">
													<option value = '${tariffCharges.id}'>${tariffCharges.name}</option>
												</c:forEach>	
							            	</select>
							        	</td>
						        	</tr>
						        	</table>    
							</tr>
							<tr>
								<td></td>
								<td><form:label path="billingCycle">Billing Cycle</form:label></td>
								<td></td>
								<td>
									<table width=100%>
									<tr>
										<td width=40%>
											<form:select path="billingCycle" id="billingCycle" class="form-control" >
												<form:options items="${billingCycle}" itemValue="id" itemLabel="name"/>	
											</form:select>
										</td>
										<td width=30%><form:label path="crCycle">Credit Cycle</form:label></td>
										<td width=2%></td>
										<td width=25%><form:input path="crCycle" type="text" style="width:100%" class="form-control"  maxlength="5"></form:input></td>
									</tr>
									</table>									
								</td>
							</tr>
							<tr>
								<td></td>
								<td><form:label path="bookAllow">Booking Allow</form:label></td>
								<td></td>
								<td>									
									<table width=100%>
									<tr>
										<td width=40%>
											<form:select path="bookAllow" id="bookAllow" class="form-control" >
		 										<form:option value="Y" label="Yes"/>
		 										<form:option value="N" label="No"/>
											</form:select>
										</td>
										<td width=30%><form:label path="status">Status</form:label></td>
										<td width=2%></td>
										<td width=25%>
											<form:select path="status" id="status" class="form-control" >
		 										<form:option value="Y" label="Active"/>
		 										<form:option value="N" label="InActive"/>
											</form:select>
										</td>
									</tr>
									</table>									
								</td>
							</tr>
							<tr>
								<td></td>
								<td><form:label path="minRoundSlab">Rounding Slab (Min.)</form:label></td>
								<td></td>
								<td>									
									<table width=100%>
									<tr>
										<td width=40%>
											<form:select path="minRoundSlab" id="minRoundSlab" class="form-control" >
		 										<form:option value="15" label="15 Minute"/>
		 										<form:option value="30" label="30 Minute"/>
											</form:select>
										</td>
										<td width=50%><form:label path="status">Tax / Discount Rounding</form:label></td>
										<td width=2%></td>
										<td width=5%>
											<form:input path="roundedOff" name="roundedOff" id="roundedOff" type="checkbox"></form:input> 
										</td>
									</tr>
									</table>									
								</td>
							</tr>
								<%-- <td></td>
								<td><label>Taxation</label></td>
								<td></td>
								<td><table width=100%>
									<tr>
									<p>The length of the companies collection is : ${fn:length(taxationList)}</p>	
										<td width=20%><form:label path="sTax">S.Tax</form:label></td>
										 <c:forEach var="taxation" items="${taxationList}">
										 	<c:if test="${taxation.name=='Service Tax'}">
												<td width=25%><form:input path="sTax" type="text" style="width:100%" class="form-control" onkeypress="isDouble(event,this)" value = '${taxation.doubleValue}' maxlength="5"></form:input></td>
											</c:if>
										 </c:forEach>
										 
										<td width=5%><label><strong>%</strong></label></td>
										<td width=20%><form:label path="discount">Disc.</form:label></td>
										 <c:forEach var="taxation" items="${taxationList}">
										 	<c:if test="${taxation.name=='Discount'}">
												<td width=25%><form:input path="discount" type="text" style="width:100%" class="form-control" onkeypress="isDouble(event,this)" value = '${taxation.doubleValue}' maxlength="5"></form:input></td>
											</c:if>
										</c:forEach>		
										<td width=5%><label><strong>%</strong></label></td>
									</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td><table width=100%>
									<tr>
										<td width=20%><form:label path="eduCess">Edu.Cess</form:label></td>
										 <c:forEach var="taxation" items="${taxationList}">
										 	<c:if test="${taxation.name=='Education Cess'}">
												<td width=25%><form:input path="eduCess" type="text" style="width:100%" class="form-control" onkeypress="isDouble(event,this)" value = '${taxation.doubleValue}' maxlength="5"></form:input></td>
											</c:if>
										</c:forEach>	
										<td width=5%><label><strong>%</strong></label></td>
										<td width=20%><form:label path="hec">HEC</form:label></td>
										 <c:forEach var="taxation" items="${taxationList}">
										 	<c:if test="${taxation.name=='Higher Education Cess'}">
												<td width=25%><form:input path="hec" type="text" style="width:100%" class="form-control" onkeypress="isDouble(event,this)" value = '${taxation.doubleValue}' maxlength="5"></form:input></td>
											</c:if>
										</c:forEach>		
										<td width=5%><label><strong>%</strong></label></td>
									</tr>
									</table>
								</td>
							</tr> --%>
							
							</table>
<!--  By Nitin On Date 03-May-2016 -->
							<div class="row"></div>
							<div class="panel panel-default">
								<div class="panel-heading">Taxation / Discount</div>	
								<div class="panel-body">							
									<div class="row">
										 <c:forEach var="taxation" items="${taxationList}" begin="0" end="${fn:length(taxationList)}" step="1">
										 		<div class="col-md-4"><label>${taxation.name}</label></div>
												<div class="col-md-1"  style="width: 42px;"><input type="text" style="width:100%" class="form-control tax"  id="tax_${ taxation.id}" onkeypress="isDouble(event,this)" value = '${taxation.doubleValue}' maxlength="5"></input></div>
										 		<div class="col-md-1" style="width: 5%; margin-left: -4px;"><label><strong>%</strong></label><input class="efDate"  id="efDate_${ taxation.id}"  type="hidden" value='${taxation.efDate}'></input></div>
										 </c:forEach>
									</div>	
									<div class="row">
										<div class="col-md-12">
											<div class="col-md-4"><label style="margin-top: 9px;">Effective Date</label></div>
											<div class="col-md-4">
												<input type="text" class="form-control "  id="discountId"  value='${efDate}' data-inputmask="'alias': 'dd/mm/yyyy'" maxlength="10" ></input>
											</div>
										</div>	
									</div><!-- end by Nitin -->	
								</div>	
							</div>	
						</div>		
					</div>
				
					<div class="col-md-6">
						<div class="box-body">
							<table width=100%>
							<tr>
								<td width=2%></td>
								<td width="35%"><label>Contact Person</label></td>
								<td width=2%></td>
								<td width="60%">
									<input id="contactPerson1" style="width:100%" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="50"></input>
									<input type='hidden' name='contactDetailModel_id' id='contactDetailModel_id'/>						
								</td>
							</tr>
							<tr>
								<td></td>
								<td><label>Designation</label></td>
								<td></td>
								<td><input id="contactPerson2" class="form-control" maxlength="50"></input></td>
							</tr>
							<tr>
								<td></td>
								<td><label>Mobile No.</label></td>
								<td></td>
								<td><input id="personalMobile" class="form-control" maxlength="20"></input></td>
							</tr>
							<tr>
								<td></td>
								<td><label>Telephone</label></td>
								<td></td>
								<td><input id="residentialPhone" class="form-control" maxlength="20"></input></td>
							</tr>
							<tr>
								<td></td>
								<td><label>E-Mail</label></td>
								<td></td>
								<td><input id="personalEmailId" class="form-control" maxlength="50"></input></td>
							</tr>
							<tr>
								<td></td>
								<td><label>Office Address 1</label></td>
								<td></td>
								<td>
									<input style="width:100%" id="address1" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="250"></input>
									<input type='hidden' name='addressDetailModel_id' id='addressDetailModel_id'/>						
								</td>
							</tr>
							<tr>
								<td></td>
								<td><label>Office Address 2</label></td>
								<td></td>
								<td><input style="width:100%" id="address2" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="250"></input></td>
							</tr>
							<tr>
								<td></td>
								<td><label>Land Mark</label></td>
								<td></td>
								<td><input style="width:100%" id="landMark" class="form-control" onkeypress="isAlphaNumeric(event)" maxlength="200"></input></td>
							</tr>
								<tr>
									<td></td>
									<td><label>PIN Code</label></td>
									<td></td>
									<td><input style="width:100%" id="pincode" class="form-control" onkeypress='isNumeric(event)' maxlength="6" onblur='getStateDistMasterData_usingPincode(this.value)'></input></td>
								</tr>
								<tr>
									<td></td>
									<td><label>State</label></td>
									<td width="2%"><font color="red">*</font></td>
									<td>
										<Select NAME='state'  id='state' class="form-control" onchange='getDistrictData(this.value,0)' >
											<option value = '0'>---Select---</option>
											<c:forEach var="stateMaster" items="${stateMasterList}">
												<option value = '${stateMaster.id}'>${stateMaster.name}</option>
											</c:forEach>	
										</select>
									</td>
								</tr>
								<tr>
									<td></td>
									<td><label>District</label></td>
									<td width="2%"><font color="red">*</font></td>
									<td>
										<div id='distictDiv' name='distictDiv'> 
											<Select NAME='district'  id='district' class="form-control" onchange='getCityData(this.value,0)'>
												<option value = '0'>---Select---</option>
											</select> 
										</div>
									</td>
								</tr>
								<tr>
									<td></td>
									<td><label>City</label></td>
									<td width="2%"><font color="red">*</font></td>
									<td>
										<div id='cityDiv' name='cityDiv' > 
											<Select NAME='city'  id='city' class="form-control" >
												<option value = '0'>---Select---</option>
											</select> 
										</div>
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
						<input type='button'  value='Save'	 class="btn btn-primary" 	id="addButton"    onclick="saveOrUpdateCorporate()" style="width:150px">
						<input type='button'  value='Update' class="btn btn-primary"	id="updateButton" onclick="saveOrUpdateCorporate()" disabled style="width:150px">
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
