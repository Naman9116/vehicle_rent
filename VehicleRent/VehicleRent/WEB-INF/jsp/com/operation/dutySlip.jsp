<HTML>
<HEAD>
	<TITLE> VehicleRent </TITLE>
	<script src= "<%=request.getContextPath()%>/js/com/operation/dutySlip.js"></script>
	<style>
	</style>
	 <script>
		
	 $(function() {
		var dialog, form;
		
		function addUser() {
			var valid = true;
			$("#driverDetails_name_Hidden").val($("#driverDetails_name").val());
			$("#driverDetails_mobileNo_Hidden").val($("#driverDetails_mobileNo").val());
			$("#driverDetails_Text").val($("#driverDetails_name_Hidden").val()+"   |   "+$("#driverDetails_mobileNo_Hidden").val());
			dialog.dialog( "close" );
			return valid;
		}
		 
		dialog = $( "#dialog-form" ).dialog({ autoOpen: false,	 height: 400, width: 600, modal: true, 
		buttons: {
			 	"Add": addUser,
			 	Cancel: function() {
					dialog.dialog("close");
			 	}
		 	},
		 	close: function() {
				 form[0].reset();
		 	}
		});
		
		form = dialog.find( "form" ).on( "submit", function( event ) {
			 event.preventDefault();
			 addUser();
		});
	
		$( "#addDriverNameButton" ).on( "click", function() {
			 dialog.dialog( "open" );
		});
		
	});	
	 
	var pageNumber=1;
	var contextPath;
	
	function onLoadPage(){
		var contextPath="<%=request.getContextPath()%>";
		pageNumber="${pageNumber}"-0;
		document.getElementById("currentPage").value=pageNumber;
		$('#dataTable2').html("${dutySlipDataList}");
		$('#ownType').attr("disabled", true); 
		$('#addDiverNameButton').attr("disabled", true);
		altRows('dataTable');
	}
	
	$(function() {
		$( "#dutySlipDate" ).datetimepicker({
			   format:'d/m/Y',
			   step:5,
			   maxdate:0,
			   closeOnDateSelect:true,
	    });
		$( "#timeFrom" ).datetimepicker({
			   datepicker:false,
			   format:'H:i:s',
			   step:5,
	    });
		$( "#dateFrom" ).datetimepicker({
			   format:'d/m/Y',
			   step:5,
			   maxdate:0,
			   closeOnDateSelect:true,
	    });

	});

	</script>
	
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<form:form commandName="dutySlipModel" method="post" >
		<!-- Booking And Vehicle Data Grid Header Div -->
		<table width="100%" id="dataTable" >
			<tr>
				<td align="left" width='10%'>
					<a href="javascript:getPreviousPageData()" >	
						<img src="<%=request.getContextPath()%>/img/previousButton.png" border="0" title="Previous" width="24" height="24" />
					</a>
					<a href="javascript:getNextPageData()">
						<img src="<%=request.getContextPath()%>/img/nextButton.png" border="0" title="Next" width="24" height="24" />
					</a> 
				</td >
				<td width='10%' align="right"><label>Booking #</label></td>
				<td width='60%' align="left"><input class="form-control" onkeypress="isNumeric(event)" type="text" id="searchCriteria" style="width:30%;padding: 2px" name="searchCritera" placeHolder="Search By Booking No." onkeyup="getBookingData_DutySlip()"></td>
				<td align="right" width='15%'><label>Page #</label></td>
				<td width='5%'><input type="text" style="width:40px" readonly="true" id="currentPage" name="currentPage" ></td>
			</tr>	
		</table>
		<!-- Booking And Vehicle Data Grid Body Div -->
		<div style="width:100%;height: 160px;overflow-Y:scroll;overflow-X:scroll;" class="table table-bordered table-striped" id="dataTable2"></div>
		</td>
	</tr>
	</table>
			
	<!-- Duty Slip Form -->
	<legend><strong>Duty Slip Form</strong></legend>
	<TABLE width='100%' id="bookingDetailTable" cellspacing=4>
		<TR>
			<TD width="12%"><label>Booking No.</label></TD>
			<TD width="12%"><input type='text' class="form-control" name="bookingNo" id="bookingNo" style="width:100%" maxlength="15" readonly="true" tabindex='-1'></TD>												
			<TD width="76%" colspan=9>&nbsp;</TD>
		</TR>
		<TR>
			<TD width="12%"><label>Manual Slip No.</label></DH>
			<TD width="12%"><input type='text' class="form-control" name="manualSlipNo" id="manualSlipNo" style="width:100%" maxlength="15" onkeypress="isAlphaNumeric(event)"  ></TD>
			<TD width="1%">&nbsp;</TD>
			<TD width="12%"><label>Duty Slip No.</label></TD>
			<TD width="12%"><input type='text' class="form-control" name="dutySlipNo" id="dutySlipNo" style="width:100%" maxlength="15" onkeypress="isAlphaNumeric(event)"  ></TD>
			<TD width="1%">&nbsp;</TD>
			<TD width="12%"><label>Duty Slip Date</label></TD>
			<TD width="12%"><input type='text' class="form-control" name="dutySlipDate" id="dutySlipDate" style="width:100%" readonly="true" ></TD>
			<TD width="25%" colspan=3>&nbsp;</TD>
		</TR>
		<TR>
			<TD width="12%"><label>Open Kms.</label></TD>
			<TD width="12%"><input type='text' class="form-control" name="openKms" id="openKms" style="width:100%" maxlength="15" onkeypress="isAlphaNumeric(event)" ></TD>												
			<TD width="1%">&nbsp;</TD>
			<TD width="12%"><label>Time From</label></TD>
			<TD width="12%"><input type='text' class="form-control" name="timeFrom" id="timeFrom" style="width:100%" maxlength="15" readonly="true" ></TD>
			<TD width="1%">&nbsp;</TD>
			<TD width="12%"><label>Date From</label></TD>
			<TD width="12%"><input type='text' class="form-control" name="dateFrom" id="dateFrom" style="width:100%" readonly="true" ></TD>
			<TD width="1%">&nbsp;</TD>
			<TD width="12%"><label>Driver Advance</label></TD>
			<TD width="12%"><input type='text' class="form-control" name="driverAdvance" id="driverAdvance" style="width:100%" maxlength="15" onkeypress="isAlphaNumeric(event)" ></TD>
		</TR>
		<TR>
			<TD width="12%"><label>Min Charge Kms.</label></TD>
			<TD width="12%"><input type='text' class="form-control" name="minChgKms" id="minChgKms" style="width:100%" maxlength="15" readonly="true" ></TD>												
			<TD width="1%">&nbsp;</TD>
			<TD width="12%"><label>Min Charge Hrs.</label></TD>
			<TD width="12%"><input type='text' class="form-control" name="minChgHrs" id="minChgHrs" style="width:100%" maxlength="15" readonly="true" ></TD>
			<TD width="1%">&nbsp;</TD>
			<TD width="12%"><label>Extra Charge Kms.</label></TD>
			<TD width="12%"><input type='text' class="form-control" name="extraChgKms" id="extraChgKms" style="width:100%" maxlength="15" readonly="true" ></TD>
			<TD width="1%">&nbsp;</TD>
			<TD width="12%"><label>Extra Charge Hrs.</label></TD>
			<TD width="12%"><input type='text' class="form-control" name="extraChgHrs" id="extraChgHrs" style="width:100%" maxlength="15" readonly="true" ></TD>
		</TR>
		<TR>
			<TD width="12%"><label>Pickup From</label></TD>
			<TD width="26%" colspan=4><input type='text' class="form-control" name="pickupFrom" id="pickupFrom" style="width:100%" maxlength="200" onkeypress="isAlphaNumeric(event)" ></TD>
			<TD width="1%">&nbsp;</TD>
			<TD width="12%"><label>Drop To</label></TD>
			<TD width="26%" colspan=4><input type='text' class="form-control" name="dropTo" id="dropTo" style="width:100%" maxlength="200" onkeypress="isAlphaNumeric(event)" ></TD>
		</TR>
		<TR>
			<TD width="12%"><label>Owner Type</label></TD>
			<TD width="12%">
				<form:select path="ownType.id" id="ownType" class="form-control" style="width:100%">
						<form:option value="" label="--- Select ---"/>
						<form:options items="${ownerTypeIdList}" itemValue="id" itemLabel="name"/>	                
				</form:select><input type="hidden" id="ownTypeHidden" name="ownTypeHidden">
			</td>
			<TD width="1%">&nbsp;</TD>
			<TD width="12%"><label>Driver Name</label></TD>
			<TD width="12%">
				<form:select path="driverNameId" id="driverName" class="form-control" style="width:100%">
						<form:option value="" label="--- Select ---"/>
					<form:options items="${driverUserIdList}" itemValue="id" itemLabel="userName"/>	                
				</form:select>
			</TD>
			<TD width="1%" colspan=	2><input type="button" name="addDiverNameButton" disabled id="addDriverNameButton" value="Add New Driver"></TD>
			<TD width="1%" colspan=4><input type="text" class="form-control" name="driverDetails_Text" tabindex='-1' id="driverDetails_Text" readonly="true" style="width:100%"></TD>
		</TR>
		<TR>
			<TD width="12%"><label>Remarks</label></TD>
			<TD width="38%" colspan=4><input type='text' class="form-control" name="remarks" id="remarks" style="width:100%" maxlength="200" onkeypress="isAlphaNumeric(event)" ></TD>
			<TD width="50%" colspan=5>&nbsp;</TD>
		</TR>
	</TABLE>

	<legend><strong>Action Required | Status</strong></legend>
	<table width='100%' height=60px>
		<tr>
			<td align='left' width="30%">	
				<input type='button' style='width:32%;' value='Add'	id="addButton" onclick='saveOrUpdateDutySlip()' >
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
	<input type='hidden' id='carTypeIdHidden' name='carTypeIdHidden' >
	<input type='hidden' id='bookingIdHidden' name='bookingIdHidden' >
	<input type='hidden' id='driverDetails_name_Hidden' name='driverDetails_name_Hidden' >
	<input type='hidden' id='driverDetails_mobileNo_Hidden' name='driverDetails_mobileNo_Hidden' >
	</form:form>
</BODY>
</section>
</div>
</HTML>

<div id="dialog-form" title="Driver Details"class="ui-widget" style="display:none">
	<form>
	<fieldset>
		<table>
			<tr>
				<td><label for="name">Name</label></td>
				<td><input type="text" name="driverDetails_name" id="driverDetails_name"  onkeypress="isAlphaNumeric(event)" maxlength=50 class="text ui-widget-content ui-corner-all"></td>
				<td>&nbsp;</td>
				<td><label for="email">Mobile No.</label></td>
				<td><input type="text" name="driverDetails_mobileNo" id="driverDetails_mobileNo" onkeypress="isNumeric(event)" maxlength=10  class="text ui-widget-content ui-corner-all"></td>
			</tr>
		</table>
	</fieldset>
	</form>
</div>
