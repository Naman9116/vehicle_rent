<HTML>
<HEAD>
	<TITLE> VehicleRent </TITLE>
	<script src= "<%=request.getContextPath()%>/js/com/operation/dutySlipReceive.js"></script>
	<style>
	</style>
	 <script>
		
	var pageNumber=1;
	var contextPath;
	
	function onLoadPage(){
		var contextPath="<%=request.getContextPath()%>";
		pageNumber="${pageNumber}"-0;
		document.getElementById("currentPage").value=pageNumber;
		$('#dataTable2').html("${dutySlipReceiveDataList}");
		altRows('dataTable');
	}
	
	</script>
	
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<form:form commandName="dutySlipModel" method="post" >
		<div style="width:1340px;height: 35px;" >
			<table style="text-align:center" width="100%" id="dataTable" border="0" >
				<tr valign="top">
					<td align="left" width='10%'>
						<a href="javascript:getPreviousPageData()" >	
							<img src="<%=request.getContextPath()%>/img/previousButton.png" border="0" title="Previous" width="24" height="24" />
						</a>
						<a href="javascript:getNextPageData()">
							<img src="<%=request.getContextPath()%>/img/nextButton.png" border="0" title="Next" width="24" height="24" />
						</a> 
					</td >
					<td width='10%' align="right"><label>Car No. #</label></td>
					<td width='20%' align="left"><input type="text" class="form-control" id="searchCriteria" style="width:100%;" name="searchCritera" placeHolder="Search By Car No." >&nbsp;</td>
					<td width='40%' align="left"><input type="button" id="searchButton" class="ss" value="Retrive" onclick="getDutySlipData_DutySlipReceive()" style="width:20%"></td>
					<td align="right" width='15%'><label>Page #</label></td>
					<td width='5%'><input type="text" style="width:40px" readonly="true" id="currentPage" name="currentPage" ></td>
				</tr>	
			</table>
		</div>
		<!-- Booking And Vehicle Data Grid Body Div -->
		<div style="width:1340px;height:320px;overflow-Y:scroll;overflow-X:scroll;" class="table table-bordered table-striped" id="dataTable2"></div>
		<legend><strong>Action Required | Status</strong></legend>
		<table width='1340px' height=60px>
			<tr>
				<td align='left' width="30%">	
					<input type='button' style='width:32%;' value='Save'	id="addButton" onclick='saveOrUpdateDutySlipReceive()' >
					<input type='reset'  style='width:32%;' value='Reset'	id="resetButton" onclick='resetData()' >
				</td>
				<td width="70%">
					<!-- Error Message Div -->
					<table border=0 width="100%">
						<tr><td colspan=3><div style="width:95%;height:60px;overflow:auto;display:none" id="error" class="error"></div></td></tr>
					</table>

					<!-- Success Message Div -->
					<table border=0 width="100%">
						<tr><td><div style="width:100	%;height:20px;display:none" id="info" class="success"></div></td></tr>
					</table>
				</td>
			</tr>
		</table>
	</section>
	</form:form>
</div>	
</BODY>
</HTML>


