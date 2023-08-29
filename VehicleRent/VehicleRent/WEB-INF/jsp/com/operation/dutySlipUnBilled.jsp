<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<HTML>
<HEAD>
	<TITLE> VehicleRent </TITLE>	
	<script src= "<%=request.getContextPath()%>/js/com/operation/dutySlipUnBilled.js"></script>
	<style>
	</style>
	 <script>
		
	var pageNumber=1;
	var contextPath;
	
	function onLoadPage(){
		var contextPath="<%=request.getContextPath()%>";
		pageNumber="${pageNumber}"-0;
		document.getElementById("currentPage").value=pageNumber;
		$('#dataTable2').html("${dutySlipUnBilledDataList}");
		altRows('dataTable');
	}
	
	</script>
	
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<form:form commandName="dutySlipModel" method="post" >
		<table width="100%" id="dataTable" >
			<tr valign="top">
				<td align="left" width='5%'>
					<div class="form-group">				
						<label>Page</label>
						<a href="javascript:getPreviousPageData()" >	
							<img src="<%=request.getContextPath()%>/img/previousButton.png" border="0" title="Previous" width="24" height="24" />
						</a>
						<a href="javascript:getNextPageData()">
							<img src="<%=request.getContextPath()%>/img/nextButton.png" border="0" title="Next" width="24" height="24" />
						</a> 
					</div>	
				</td >
				<td width='16%'>
					<div class="form-group">				
						<label>Corporate</label>
						<form:select path="corporate" class="form-control" id="corporate" style="width:100%" onchange="">
							<form:option value="0" label="--- ALL ---"/>
							<form:options items="${corporateIdList}" itemValue="id" itemLabel="userName"/>	                
						</form:select>
					</div>	
				</td>
				<td width='16%'>
					<div class="form-group">				
						<label>Booked By</label>
						<form:select path="bookedBy" class="form-control" id="bookedBy" style="width:100%">
							<form:option value="0" label="--- ALL ---"/>
							<form:options items="${bookedByIdList}" itemValue="id" itemLabel="userName"/>	                
						</form:select>
					</div>	
				</td>
				<td width='16%'>
					<div class="form-group">				
						<label>Used By</label>
						<form:select path="usedBy" class="form-control" id="usedBy" style="width:100%">
							<form:option value="0" label="--- ALL ---"/>
							<form:options items="${usedByIdList}" itemValue="id" itemLabel="userName"/>	                
						</form:select>
					</div>	
				</td>
				<td width='16%'>
					<div class="form-group">				
						<label>Closed By</label>
						<form:select path="closedBy" class="form-control" id="closedBy" style="width:100%">
							<form:option value="0" label="--- ALL ---"/>
							<form:options items="${closedByIdList}" itemValue="id" itemLabel="userName"/>	                
						</form:select>
					</div>
				</td>
				<td width='13%'>
					<div class="form-group">				
						<label>Hub</label>
						<form:select path="hub" class="form-control" id="hub" style="width:100%">
							<form:option value="0" label="--- ALL ---"/>
							<form:options items="${hubIdList}" itemValue="id" itemLabel="name"/>	                
						</form:select>
					</div>	
				</td>
				<td width='10%'>
					<div class="form-group">				
						<label>Get Data</label>
						<input type="button" id="searchButton" value="Retrive" onclick="getDutySlipData_DutySlipUnBilled()" style="width:100%" />
					</div>
				</td>	
				<td width='8%'>
					<div class="form-group">				
						<label>Page #</label>
						<input type="text" class="form-control" style="width:40px" readonly="true" id="currentPage" name="currentPage" />
					</div>
				</td>	
			</tr>	
		</table>
		<!-- Booking And Vehicle Data Grid Body Div -->
		<div style="width:1340px;height:320px;overflow-Y:scroll;overflow-X:scroll;" class="table table-bordered table-striped" id="dataTable2"></div>

		<legend><strong>Action Required | Status</strong></legend>
		<table width='1340px' height=60px>
			<tr>
				<td align='left' width="30%">	
					<input type='button' style='width:32%;' value='Generate Invoice'	id="addButton" onclick='saveOrUpdateDutySlipUnBilled()' >
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
	</form:form>
	</section>
</div>	
</BODY>
</HTML>


