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
	<script src= "<%=request.getContextPath()%>/js/com/ets/rosterMaster.js"></script>
	<script>

	var statePermit_count =0;
	var selectedRowForDelete='';
	var checkedKey='';
	var contextPath;
	var userPageAccess= '<%=sAccess%>';									
	var loginUserId;
	
	$(function () {
	    //Datemask dd/mm/yyyy
		$("#rosterDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});;
		$("#pickupTime_1").inputmask("hh:mm", {"placeholder": "hh:mm"});
        $("#rosterDate").val(getTimeStampTo_DDMMYYYY(new Date()));
// 	     updateTime();
//		setInterval(searchBooking,30000);
	});
	 
	function onLoadPage(){
		$('#dataTable2').html("${rosterDataList}");
		if(<%=session.getAttribute("loginUserId")%>!=null)
			loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
		assignRights();
		refreshData();	
		var contextPath="<%=request.getContextPath()%>";
	}
	</script>
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
		<form:form  method="post" id="rosterForm">
		<div class="row">
		<legend><strong>Roster</strong> Master</legend>
			<div class="col-md-12" style="background-color: #F6F6F3;">
			
				<table style="width: 100%;">
				<tr height="25px"> <!-- First Line -->
					<td width=10%><label>Corporate Name</label></td>
					<td width=18%>
						<select class="form-control" id="corporateId" style="width:100%" onChange="fillValues(this.value)">
							<option value="0">ALL</option>
							<c:forEach var="customerOrCompanyNameId" items="${customerOrCompanyNameIdList}">
								<option value = '${customerOrCompanyNameId.id}'>${customerOrCompanyNameId.name}</option>
							</c:forEach>	
						</select>
					</td>
					
					<td width=10%><label>Branch</label></td>
					<td width=18%>
						<select class="form-control" id="branch" style="width:100%" onChange="fillHub(this.value)">
							<option value="0">ALL</option>
							<c:forEach var="branch" items="${branchList}">
								<option value = '${branch.id}'>${branch.name}</option>
							</c:forEach>	
						</select>
					</td>
					
					<td width=8%><label>Hub</label></td>
					<td width=10%>
						<select class="form-control" id="outlet" style="width:100%">
							<option value="0">ALL</option>							
						</select>
					</td>
					
					<td width=10%><label>Booked By</label></td>
					<td width=18%>
						<select class="form-control" id="bookedBy" style="width:100%">
							<option value="0">ALL</option>
						</select>
					</td>
					
					<%-- <td width=8%><label>Car Type</label></td>
					<td width=10%>
						<select class="form-control" id="carModel" name="carModel" style="width:100%">
							<option value="0">ALL</option>
							<c:forEach var="carModelId" items="${carModelIdList}">
								<option value = '${carModelId.id}'>${carModelId.name}</option>
							</c:forEach>	
						</select>
					</td>
 --%>		
 				</tr>
 				<tr>			
					<td width=10%><label>Shift Timings</label></td>
					<td width=18%>
						<select class="form-control" id="shiftTiming" style="width:100%" >
							<option value="0">ALL</option>
							<c:forEach var="shiftTiming" items="${shiftTimeList}">
								<option value = '${shiftTiming.id}'>${shiftTiming.name}</option>
							</c:forEach>	
						</select>
					</td>
					
					<td width=10%><label>Roster Taken</label></td>
					<td width=18%><input type="text" class="form-control"  id="rosterTaken"  style="width:100%"  readOnly='true' value='<%=session.getAttribute("loginUser")%>'></input></td>
					
					<td width=8%><label>Roster Date</label></td>
					<td width=10%><input type="text" id="rosterDate"  name="rosterDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10" ></input></td>
					
				</tr>
				
				<tr>
					<table  id="rosterTable" border="1"  style="margin-top: 10px">
						<tr bgcolor="#FFDA71" style="font-family: cambria;font-size: 14; height: 10px;">
							<th>Add</th>
							<th>Mobile No.</th>
							<th>PickUp Time</th>
							<th>Route No.</th>
							<th>Car Type</th>
							<th>PickUp</th>
							<th>Drop</th>
							<th>EmployeeId</th>
							<th>Employee Name</th>
							<th>PickUp Address</th>
							<th>Zone</th>
							<th>Location.</th>
							<th>City</th>
							<th>Division</th>	
						</tr>
						<tr>
							<td><input type="button" id="btnAdd"  class="btn btn-primary" value="+" onclick="addNewRow()"/></td>
							<td><input type="text" class="form-control mob" id="mobileNo_1" maxlength="10" onChange="fillClientAsPerMobile(this.id)"  onkeypress='isNumeric(event)'/>
									<input type="text" class="form-control" id="authId_1" style="display: none;"/></td>
							<td><input type="text" class="form-control" id="pickupTime_1" maxlength="5"  data-inputmask="'alias': 'hh:mm'"/></td>
							<td><input type="text" class="form-control" id="routeNo_1"/></td>
							<td>
									<select class="form-control" id="carModel_1" name="carModel" >
										<option value="0">---Select---</option>
										<c:forEach var="carModelId" items="${carModelIdList}">
											<option value = '${carModelId.id}'>${carModelId.name}</option>
										</c:forEach>	
									</select>
							</td>
							<td align="center"><input type="checkbox"  id="pickup_1" checked="checked"/></td>
							<td align="center"><input type="checkbox" id="drop_1" checked="checked"/></td>
							<td><input type="text" class="form-control" id="empId_1" readonly="readonly"/></td>
							<td><input type="text" class="form-control" id="empName_1" readonly="readonly"/></td>
							<td><input type="text" class="form-control" id="empPickupAdd_1" readonly="readonly"/></td>
							<td><input type="text" class="form-control" id="zone_1" readonly="readonly"/></td>
							<td><input type="text" class="form-control" id="location_1" readonly="readonly"/></td>
							<td><input type="text" class="form-control" id="city_1" readonly="readonly"/></td>
							<td><input type="text" class="form-control" id="division_1" readonly="readonly"/></td>
						</tr>
					</table>
				</tr>
			</table>				
			<div class="row">
				<div class="col-md-3"></div>
					<div class="col-md-6">
						<div class="box-body">
						<input type='button'  value='Save'	 class="btn btn-primary" 	id="addButton"    onclick="saveOrUpdate()" style="width:150px">
						<input type='button'  value='Update' class="btn btn-primary"	id="updateButton" onclick="saveOrUpdate()" disabled style="width:150px">
						<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:150px">
						<input type="hidden" id="idForUpdate" name="idForUpdate" value="0">
						</div>
				</div>
				<div class="col-md-3">
						<div class="box-body">
							<div style="width:100%;height:60px;overflow:auto;display:none" id="error" class="error"></div>
							<div style="width:100%;height:20px;display:none" id="info" class="success"></div>
						</div>
					</div>				
			</div>
			<hr>
			</div>
		</div>
		<div class="row">	
			<div class="col-md-12" >
				<div id="dataTable2" style="width:100%;overflow:auto;"></div>
			</div>
		</div>
	</form:form>
	</section>
</div>	
</BODY>
</HTML>
