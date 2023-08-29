<HTML>
<HEAD>
	<TITLE> VehicleRent </TITLE>	
	<script src= "<%=request.getContextPath()%>/js/com/operation/vehicleAllocation.js"></script>
	<script>
	
	var pageNumber=1;
	var contextPath;
	
	function onLoadPage(){
		var contextPath="<%=request.getContextPath()%>";
		pageNumber="${pageNumber}"-0;
		document.getElementById("currentPage").value=pageNumber;
		$('#dataTable2').html("${vehicleAllocationDataList}");
		altRows('dataTable');
	}
		
	</script>
	
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
		<form:form commandName="vehicleAllocationModel" method="post">
		<legend><strong>Vehicle Allocation </strong></legend>
		<!-- Booking Data Grid Header Div -->
		<table width="100%" id="dataTable">
		<tr> 
			<td align="left" width='10%'>
				<a href="javascript:getPreviousPageData()" ><img src="<%=request.getContextPath()%>/img/previousButton.png" border="0" title="Previous" width="24" height="24" /></a>
				<a href="javascript:getNextPageData()"><img src="<%=request.getContextPath()%>/img/nextButton.png" border="0" title="Next" width="24" height="24" /></a> 
			</td >
			<td align="left" width='10%'><label>Booking No.</label></td>
			<td valign="center" width='40%'><input class="form-control" onkeypress="isNumeric(event)" type="text" id="searchCriteria" style="width:30%;" name="searchCritera" placeHolder="Search By Booking No." onkeyup="getBookingData_VehicleAllocation()"></td>
			<td valign="center" width='10%' align='right'><label>Page #</label></td>
			<td valign="center" width='10%'><input type="text" size="4" readonly="true" id="currentPage" name="currentPage" style='width:40px'></td>
		</tr>
		</table>
		<div style="width:1340px;height: 140px;overflow-Y:scroll;overflow-X:scroll;" class="table table-bordered table-striped" id="dataTable2"></div>
		<!-- Booking Data Grid Body Div -->

		<!-- Vehicle Data Grid Header Div -->
		<table width="100%" id="dataTable3">
			<tr valign="top"> 
				<td align="left" width='10%'><label>Vehicle Type</label></td>
				<td width='60%' align="left"><input class="form-control" onkeypress="isNumeric(event)" type="text" id="searchCriteria_vehicle" style="width:30%;padding: 2px" name="searchCriteria_vehicle" placeHolder="Seach By Vehicle Type" onkeyup="getVehicleTypeData_VehicleAllocation_Search()"></td>
				<td align="right" valign="top" width='20%'>&nbsp;</td>
			</tr>
		</table>
		<!-- Vehicle Data Grid Body Div -->
		<div style="width:1340px;height: 150px;overflow-Y:scroll;overflow-X:scroll; " class="table table-bordered table-striped" id="dataTable4"></div>

		<legend><strong>Action Required | Status</strong></legend>
		<table width='100%' height=60px;>
			<tr>
				<td align='left' width="40%">	
					<input type='button' style='width:30%;' value='Allocate'	id="addButton" onclick='saveOrUpdateVehicleAllocation()' >
					<input type='reset'  style='width:30%;' value='Reset'	id="resetButton" onclick='resetData()' >
				</td>
				<td width="60%">
					<!-- Error Message Div -->
					<table border=0 width="100%">
						<tr><td colspan=3><div style="width:95%;height:60px;overflow:auto;display:none" id="error" class="error"></div></td></tr>
					</table>
	
					<!-- Success Message Div -->
					<table border=0 width="100%">
						<tr><td><div style="width:100%;height:20px;display:none" id="info" class="success"></div></td></tr>
					</table>
				</td>
			</tr>
		</table>
		<input type='hidden' id='carTypeIdHidden' name='carTypeIdHidden' >
		<input type='hidden' id='bookingIdHidden' name='bookingIdHidden' >
	</form:form>
	</section>
</div>	
</BODY>
</HTML>
