<HTML>
<HEAD>
	<TITLE> VehicleRent </TITLE>
	<script src= "<%=request.getContextPath()%>/js/com/operation/dutySlipClose.js"></script>
	<style>
	</style>
	 <script>
	
	 
	var pageNumber=1;
	var contextPath;
	
	function onLoadPage(){
		var contextPath="<%=request.getContextPath()%>";
		pageNumber="${pageNumber}"-0;
		document.getElementById("currentPage").value=pageNumber;
		$('#dataTable2').html("${dutySlipCloseDataList}");
		altRows('dataTable');
	}
	
	$(function() {
		$( "#timeTo" ).datetimepicker({
			datepicker:false,
			format:'H:i:s',
			step:5,
			  onSelectTime:function(){
				  if(jQuery('#timeFrom').val()==""){
					  jQuery('#timeTo').val('');
					  return false;
				  }
			  },
			  onShow:function( ct ){
				   if($('#totalDay').val()==0){
					   this.setOptions({
					    	minTime: jQuery('#timeFrom').val().substring(0,5)
					   })
				   }
			  },
		 });
		
		 jQuery('#dateTo').datetimepicker({
			  format:'d/m/Y',
			  onShow:function( ct ){
			  this.setOptions({
			     minDate:getSqlDateTo_YYYYMMDD(jQuery('#dateFrom').val())?getSqlDateTo_YYYYMMDD(jQuery('#dateFrom').val()):false
			  })
			  },
			  timepicker:false,
			  closeOnDateSelect:true,
			  onSelectDate:function(){
				  jQuery('#timeTo').val('');
				  jQuery('#totalHrs').val('');
				  if(jQuery('#dateFrom').val()==""){
					  jQuery('#dateTo').val('');
					  return false;
				  }
			  }
		 });
	});

	function calculateFare(){
		alert("hello");
	}
	</script>
	
</HEAD>

<BODY onload="onLoadPage()" >
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<form:form commandName="dutySlipModel" method="post" >
		<!-- Duty Slip Data Grid Header Div -->
		<div style="width:1340px;height: 28px;" >
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
					<td width='10%' align="right"><label>Car No #</label></td>
					<td width='20%' align="left"><input type="text" class="form-control" id="searchCriteria" style="width:100%;padding: 2px" name="searchCritera" placeHolder="Search By Car No." ></td>
					<td width='40%' align="left"><input type="button" id="searchButton" value="Retrive" onclick="getDutySlipData_DutySlipClose()" style="width:20%"></td>
					<td align="right" width='15%'><label>Page #</label></td>
					<td width='5%'><input type="text" style="width:40px" readonly="true" id="currentPage" name="currentPage" ></td>
				</tr>	
			</table>
		</div>
		<!-- Booking And Vehicle Data Grid Body Div -->
		<div style="width:1340px;height: 160px;overflow-Y:scroll;overflow-X:scroll;" class="table table-bordered table-striped" id="dataTable2"></div>
		<!-- Duty Slip Form -->
		<table border=0 style="width:100%;" style="text-align:center" >
		<tr>
		<td>
			<legend><strong>Duty Slip Close Form</strong></legend>
			<TABLE width='90%' id="bookingDetailTable" border=0 cellspacing=5>
				<TR valign="top">
					<TD width="10%"><label>Booking No.</label></TD>
					<TD width="14%"><input type='text' name="bookingNo" class="form-control" id="bookingNo" style="width:100%" maxlength="15" readonly="true" tabindex='-1'></TD>												
					<TD width="1%">&nbsp;</TD>
					<TD width="10%"><label>Manual Slip No.</label></DH>
					<TD width="14%"><input type='text' name="manualSlipNo" class="form-control" id="manualSlipNo" style="width:100%" maxlength="15" readonly="true" tabindex='-1'  ></TD>
					<TD width="1%">&nbsp;</TD>
					<TD width="10%"><label>Tariff</label></DH>
					<TD width="14%">													
						<form:select path="tariff.id" id="tariff" style="width:100%" class="form-control">
							<form:option value="" label="--- Select ---"/>
							<form:options items="${tariffIdList}" itemValue="id" itemLabel="name"/>	                
						</form:select>
					</TD>
					<TD width="1%">&nbsp;</TD>
					<TD width="50%" colspan=5>&nbsp;</TD>
				</TR>
				<TR valign="top">
					<TD width="10%"><label>Kms. Out</label></TD>
					<TD width="14%"><input type='text' name="openKms" class="form-control" id="openKms" style="width:100%" maxlength="15" readonly="true" tabindex='-1'  ></TD>												
					<TD width="1%">&nbsp;</TD>
					<TD width="10%"><label>Date Out</label></TD>
					<TD width="14%"><input type='text' name="dateFrom" class="form-control" id="dateFrom" style="width:100%" readonly="true" tabindex='-1' 	 ></TD>
					<TD width="1%">&nbsp;</TD>
					<TD width="10%"><label>Time Out</label></TD>
					<TD width="14%"><input type='text' name="timeFrom" class="form-control" id="timeFrom" style="width:100%" maxlength="15" readonly="true" tabindex='-1'  ></TD>
					<TD width="26%" colspan=3>&nbsp;</TD>
				</TR>
				<TR valign="top">
					<TD width="10%"><label>Kms. In</label></TD>
					<TD width="14%"><input type='text' name="closeKms" class="form-control" id="closeKms" style="width:100%" maxlength="15"   onkeyUp="addTwoFieldsValue('closeKms','openKms','totalKms')" onblur="addTwoFieldsValue('closeKms','openKms','totalKms')" ></TD>												
					<TD width="1%">&nbsp;</TD>
					<TD width="10%"><label>Date In</label></TD>
					<TD width="14%"><input type='text' name="dateTo" class="form-control" id="dateTo" style="width:100%" maxlength="15" readonly="true"  onkeyUp="dateDifference('dateFrom','dateTo','totalDay')" onblur="dateDifference('dateFrom','dateTo','totalDay')"></TD>
					<TD width="1%">&nbsp;</TD>
					<TD width="10%"><label>Time In</label></TD>
					<TD width="14%"><input type='text' name="timeTo" class="form-control" id="timeTo" style="width:100%" maxlength="15"   readonly="true" onkeyUp="dateTimeDifference('timeFrom','timeTo','totalHrs')" onblur="dateTimeDifference('timeFrom','timeTo','totalHrs')" ></TD>
					<TD width="26%" colspan=3>&nbsp;</TD>
				</TR>
				<TR valign="top">
					<TD width="10%"><label>Total Kms</label></TD>
					<TD width="14%"><input type='text' name="totalKms" class="form-control" id="totalKms" style="width:100%" maxlength="15" tabindex='-1' readonly="true" ></TD>												
					<TD width="1%">&nbsp;</TD>
					<TD width="10%"><label>Total Day</label></TD>
					<TD width="14%"><input type='text' name="totalDay" class="form-control" id="totalDay" style="width:100%" maxlength="15" tabindex='-1' readonly="true" ></TD>
					<TD width="1%">&nbsp;</TD>
					<TD width="10%"><label>Total Hrs.</label></TD>
					<TD width="14%"><input type='text' name="totalHrs" class="form-control" id="totalHrs" style="width:100%" maxlength="15" tabindex='-1' readonly="true" ></TD>
					<TD width="26%" colspan=3>&nbsp;</TD>
				</TR>
				<TR valign="top" >
					<TD width="10%"><label>Garage Out Kms.</label></TD>
					<TD width="14%"><input type='text' name="garageOutKms" class="form-control" id="garageOutKms" style="width:100%" maxlength="15" onkeypress="isAlphaNumeric(event)" ></TD>												
					<TD width="1%">&nbsp;</TD>
					<TD width="10%"><label>Garage In Kms.</label></TD>
					<TD width="14%"><input type='text' name="garageInKms" class="form-control" id="garageInKms" style="width:100%" maxlength="15" onkeypress="isAlphaNumeric(event)" ></TD>
					<TD width="1%">&nbsp;</TD>
					<TD width="10%"><label>Mode Of Payment</label></TD>
					<TD width="14%">
						<input type='radio' name="modeOfPayment"  id="modeOfPayment" value="Cash"> Cash&nbsp;&nbsp; 
						<input type='radio' name="modeOfPayment"  id="modeOfPayment" value="Credit"> Credit
					</TD>
					<TD width="1%">&nbsp;</TD>
					<TD width="10%"><label>Total Fare</label></TD>
					<TD width="14%"><input type='text' name="totalFare" class="form-control" id="totalFare" style="width:100%" maxlength="15" readonly="true" tabindex='-1' onchange="calculateFare()"></TD>
					<TD width="1%">&nbsp;</TD>
				</TR>
				<TR valign="top">
					<TD width="10%"><label>State Tax</label></TD>
					<TD width="14%"><input type='text' name="stateTax" class="form-control" id="stateTax" style="width:100%" maxlength="15" onkeypress="isAlphaNumeric(event)" ></TD>												
					<TD width="1%">&nbsp;</TD>
					<TD width="10%"><label>Misc. Charge</label></TD>
					<TD width="14%"><input type='text' name="miscCharge" class="form-control" id="miscCharge" style="width:100%" maxlength="15" maxlength="15" onkeypress="isAlphaNumeric(event)" ></TD>
					<TD width="1%">&nbsp;</TD>
					<TD width="10%"><label>OutStation Allow.</label></TD>
					<TD width="14%"><input type='text' name="outStationAllow" class="form-control" id="outStationAllow" style="width:100%" maxlength="15" onkeypress="isAlphaNumeric(event)" ></TD>
					<TD width="1%">&nbsp;</TD>
					<TD width="10%"><label>Night Allow.</label></TD>
					<TD width="14%"><input type='text' name="nightAllow" class="form-control" id="nightAllow" style="width:100%" maxlength="15" onkeypress="isAlphaNumeric(event)" ></TD>
					<TD width="1%">&nbsp;</TD>
				</TR>
			</TABLE>
			</fieldset>
		</td>
		</tr>	
		</table>

		<legend><strong>Action Required | Status</strong></legend>
		<table width='100%' height=60px>
			<tr>
				<td align='left' width="30%">	
					<input type='button' style='width:32%;' value='Save'	id="addButton" onclick='saveOrUpdateDutySlipClose()' >
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


