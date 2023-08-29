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
	<script src= "<%=request.getContextPath()%>/js/com/ets/rosterCreation.js"></script>
	<script>

	var statePermit_count =0;
	var selectedRowForDelete='';
	var checkedKey='';
	var contextPath;
	var userPageAccess= '<%=sAccess%>';									
	var loginUserId;
	
	$(function () {
	    //Datemask dd/mm/yyyy
		$("#rosterFromDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		$("#rosterToDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
		$("#pickupTime_1").inputmask("hh:mm", {"placeholder": "hh:mm"});
        $("#rosterFromDate").val(getTimeStampTo_DDMMYYYY(new Date()));
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
					
					<td width=8%></td>
					<td width=10%></td>
					
				</tr>
				
				<tr>			
					<td width=10%><label>Route</label></td>
					<td width=18%>
						<input type="text" id="routeNumber"  name="routeNumber"  style="width:100%" class="form-control" maxlength="10" ></input>
					</td>
					
					<td width=10%><label>From Date</label></td>
					<td width=18%><input type="text" id="rosterFromDate"  name="rosterFromDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10" ></input></td>
					
					<td width=8%><label>To Date</label></td>
					<td width=10%><input type="text" id="rosterToDate"  name="rosterToDate"  style="width:100%" data-inputmask="'alias': 'dd/mm/yyyy'" class="form-control" maxlength="10" ></input></td>
					
				</tr>
			</table>	
			<div class="row">
				<div class="col-md-3"></div>
					<div class="col-md-6">
						<div class="box-body">
						<input type='button' value='Get Detail' class="btn btn-primary" onclick="getDetail()" style="width:150px">
						</div>
				</div>
				<div class="col-md-3">
						<div class="box-body">
							<div style="width:100%;height:60px;overflow:auto;display:none" id="error" class="error"></div>
							<div style="width:100%;height:20px;display:none" id="info" class="success"></div>
						</div>
				</div>				
			</div>
			<div class="container">
  				<div class="row">
     				<div class="col-md-12">           				 
     					<div class="panel panel-info">
     				 		<div class='panel-body'> 
     				 			<div class="row" style="margin-top:5px">
	        				 		<div class="col-md-5">
	        				 			<table width="100%" border=0 >
			   				 				<tr>
												<td colspan="2" align="center"><b>Users</b></td>							
											</tr>
											<tr>
												<th align='left'><input type='checkbox' id='selectAllNotMapped' onclick='selectAllNotMappedCheckBox()' class="form-control"/></th>
												<th width='90%' align='left'><b>Select All</b></th> 
											</tr>
										</table>
										<div class="col-md-11" style="height: 250px;overflow:auto;border-radius: 7px; border: solid green 1px;font-size:12px;" id="notMappedDiv"></div>
	        				 		</div>
	        				 		<div class="col-md-2">
		        				 		<BR><BR><BR><BR>
										<input type="button" name="addValuesButton"    value=">>"    onclick="moveUsersRight()" style="width:80%" class="btn btn-success"><BR><BR><BR>
										<input type="button" name="removeValuesButton" value="<<" onclick="moveUsersLeft()" style="width:80%" class="btn btn-primary"><BR><BR><BR>
	        				 		</div>
	        				 		<div class="col-md-5">
	        				 			<table width="100%" border=0 >
						    				<tr>
												<td colspan="2" align="center"><b>Users for Booking</b></td>							
											</tr>
											<tr>
												<th align='left'><input type='checkbox' class="form-control" id='selectAllAlreadyMapped' onclick='selectAllAlreadyMappedCheckBox()' class="form-control"/></th>
												<th width='90%' align='left'><b>Select All</b></th> 
											</tr>
										</table>
										<div class="col-md-11" style="height: 250px;overflow:auto;border-radius: 7px; border: solid green 1px;font-size:12px;" id="alreadyMappedDiv"></div>
			        				 </div>
			        			</div>
		        				 <div class="row" style="margin-top:5px">
									<div class="col-md-7"></div>
									<div class="col-md-1">
											Select Car:
									</div>
									<div class="col-md-2" id="divCars">
										
									</div>				
								</div>      
		        			</div>
        				</div>
        			</div>
        		</div>
        	</div>  
        				
			<div class="row">
				<div class="col-md-3"></div>
					<div class="col-md-6">
						<div class="box-body">
						<input type='button'  value='Create Booking'	 class="btn btn-primary" 	id="addButton"    onclick="SaveBookingForRoster()" style="width:150px">
						<input type='button'  value='Update Booking' class="btn btn-primary"	id="updateButton" onclick="saveOrUpdate()" disabled style="width:150px">
						<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:150px">
						<input type="hidden" id="idForUpdate" name="idForUpdate" value="0">
						<input type="hidden" id="bookingDetailsDate" name="bookingDetailsDate" value="0">
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
