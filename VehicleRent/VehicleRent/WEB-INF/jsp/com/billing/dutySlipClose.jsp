<script>
function closeThisModal(){
	$("#tariffDetPanel").hide();
}
</script>
<div class="row" style="width: 95%; margin-left: 5px;">
	<div class="col-md-2"><label>Duty Slip No</label></div>
	<div class="col-md-2"><input type="text" id="dsNo" name="dsNo" value=""class="form-control" readonly="readonly" tabindex="10"></input></div>
	<div class="col-md-2"><label>Manual Slip No</label></div>
	<div class="col-md-2"><input type="text" id="msNo" name="msNo" value=""class="form-control" tabindex="20"></input></div>
	<div class="col-md-2"><label>Duty Slip Date</label></div>
	<div class="col-md-2"><input type="text" id="dsDate" name="dsDate" value=""class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'" onChange="assignFromToDate()" tabindex="30"></input></div>
</div> 
<div class="row" style="width: 95%; margin-left: 5px;" >
	<div class="col-md-2"><label>Corporate Name</label></div>
	<div class="col-md-4"><select class="form-control" id="corporateName" style="width:100%" onChange="tariffAsPerCorporate(this.value)" tabindex="40">
			<option value="0">---Select---</option>
			<c:forEach var="customerOrCompanyNameId" items="${customerOrCompanyNameIdList}">
				<option value = '${customerOrCompanyNameId.id}'>${customerOrCompanyNameId.name}</option>
			</c:forEach>	
		</select>
	</div>
	<div class="col-md-2"><label>Branch Name</label></div>
	<div class="col-md-4"><select class="form-control" id="branchName" name="branchName"style="width:100%" onChange="fillHub(this.value)" tabindex="60">
			<option value="0">---Select---</option>
		</select>
	</div>
</div> 
<div class="row" style="width: 95%; margin-left: 5px;" >
	<div class="col-md-2"><label>Hub</label></div>
	<div class="col-md-4">
		<select id="hubName" class="form-control" name="hubName" style="width:100%" tabindex="70">
			<option value="0">---Select---</option>
			
		</select>
	</div>
	<div class="col-md-2"><label>Rental Type</label></div>
	<div class="col-md-4">
		<select id="dsRentalType" class="form-control" id="dsRentalType" style="width:100%" onchange="fillTariff(this.value)" tabindex="50">
			<option value="0">---Select---</option>
			<c:forEach var="rentalTypeId" items="${rentalTypeIdList}">
				<option value = '${rentalTypeId.id}'>${rentalTypeId.name}</option>
			</c:forEach>	
		</select>
	</div>
</div>
<div class="row" style="width: 95%; margin-left: 5px;">
	<div class="col-md-2"><label>Booked By </label></div>
	<div class="col-md-4"><select class="form-control" id="dsBookedBy" style="width:100%" tabindex="80">
			<option value="0">---Select---</option>
			<c:forEach var="bookedBy" items="${bookedByList}">
				<option value = '${bookedBy.id}'>${bookedBy.name}</option>
			</c:forEach>	
		</select>
	</div>
	<div class="col-md-2"><label>Car Tariff</label></div>
	<div class="col-md-4">
		<select class="form-control" id="carTariff" style="width:100% " onchange="fillTraiffValuesAsPerCarTraiff(this.value)" tabindex="90">
			<option value="0">---Select---</option>
		</select>
	</div>
</div> 			
<div class="row" style="width: 95%; margin-left: 5px;">
	<div class="col-md-2"><label>Mode Of Booking</label></div>
	<div class="col-md-4">		
		<select class="form-control" id="dsMob" name="dsMob" style="width:100%" tabindex="100">
			<option value="0">---Select---</option>
			<c:forEach var="MOB" items="${MOBList}">
				<option value = '${MOB.id}'>${MOB.name}</option>
			</c:forEach>	
		</select>
	</div>
	<div class="col-md-2"><label>Booked Car Model</label></div>
	<div class="col-md-4">
		<select class="form-control" id="carModel" name="carModel" style="width:100%" onChange="fetchTariffDet()" tabindex="110">
			<option value="0">---Select---</option>
			<c:forEach var="carModelId" items="${carModelIdList}">
				<option value = '${carModelId.id}'>${carModelId.name}</option>
			</c:forEach>	
		</select>
	</div>				
</div> 			
<div class="row" style="width: 95%; margin-left: 5px;">
	<div class="col-md-2"><label><input name="optionsRadios" id="opClient"  type="radio" class="opt" checked > Client&nbsp;&nbsp;<input name="optionsRadios" id="opOther"  type="radio" class="opt" > Other </label></div>
	<div class="col-md-4">
		<select class="form-control" id="dsUsedBy" style="width:100%" tabindex="120">
			<option value="0">---Select---</option>
			<c:forEach var="bookedForId" items="${bookedForList}">
				<option value = '${bookedForId}'>${bookedForId}</option>
			</c:forEach>	
		</select>
		<input id="otherClient"  name="otherClient" type="text" style="width:100%; display:none;" class="form-control" maxlength="50" tabindex="130"></input>
		<input  type="hidden" id="bookedFor"></input>
	</div>
	<div class="col-md-2"><label>Car No.</label></div>
	<div class="col-md-3">
		<input type="text" class="form-control" name="dsCarRegNo" id="dsCarRegNo" list="carDetailsList" style=' width: 100%' onChange="fillCarRelDetails(this.value,'')" tabindex="140">	                    
                 <datalist id="carDetailsList"></datalist>
	</div>
	<div class="col-md-1"><input type='button' value='Tariff' class="btn btn-primary form-control" id="tariffDet" onClick="fetchTariffDet()" style="width:100%;" tabindex="150"/></div>	
</div> 
<div class="row" style="width: 95%; margin-left: 5px;">
	<div class="col-md-2"><label>Reporting Address </label></div>
	<div class="col-md-4"><input type="text" id="dsReportingAddress" name="dsReportingAddress" value=""class="form-control" tabindex="160"></input></div>
	<div class="col-md-2"><label>Car Type</label></div>
	<div class="col-md-4"><input type="text" id="dscarType" name="dscarType" value="" class="form-control"  readonly="readonly" tabindex="-1"></input></div>
</div> 
<div class="row" style="width: 95%; margin-left: 5px;">
	<div class="col-md-2"><label>Drop Location </label></div>
	<div class="col-md-4"><input type="text" id="dsDropAt" name="dsDropAt" value=""class="form-control" tabindex="170"></input></div>
	<div class="col-md-2"><label>Chauffeur</label></div>
	<div class="col-md-3"><input type="text" id="chauffeur" name="chauffeur"  class="form-control" readonly="readonly" tabindex="-1" ></div>
	<div class="col-md-1"><a id='updatechauffeurlink' href="javascript: updatechauffeur()" > <font size="2">Update</font> </a></div>
</div>
<hr>
<div class="row" style="width: 95%; margin-left: 5px;">
	<div class="col-md-2"><label>Kms Out</label></div>
	<div class="col-md-2"><input type="text" id="dsKmOut" name="dsKmOut" value=""class="form-control" tabindex="180"></input></div>
	<div class="col-md-2"><label>Time Out</label></div>
	<div class="col-md-2"><input type="text" id="dsTimeOut" name="dsTimeOut" data-inputmask="'alias': 'hh:mm'"  value=""class="form-control" tabindex="190"></input></div>
	<div class="col-md-2"><label>From Date</label></div>
	<div class="col-md-2"><input type="text" id="dsFromDate" name="dsFromDate" data-inputmask="'alias': 'dd/mm/yyyy'" value=""class="form-control" tabindex="200"></input></div>
</div> 
<div class="row" style="width: 95%; margin-left: 5px;">
	<div class="col-md-2"><label>Kms In</label></div>
	<div class="col-md-2"><input type="text" id="dsKmsIn" name="dsKmsIn" value="" class="form-control" onblur="calculateKmDutySlip()" tabindex="210"></input></div>
	<div class="col-md-2"><label>Time In</label></div>
	<div class="col-md-2"><input type="text" id="dsTimeIn" name="dsTimeIn" data-inputmask="'alias': 'hh:mm'" value="" class="form-control" onblur="dsTimeInCal()" tabindex="220"></input></div>
	<div class="col-md-2"><label>To Date</label></div>
	<div class="col-md-2"><input type="text" id="dsToDate" name="dsToDate" data-inputmask="'alias': 'dd/mm/yyyy'" value="" class="form-control" onChange= "dsTimeInCal()" onblur="countdaysDutySlip()" tabindex="230"></input></div>
</div> 
<div class="row" style="width: 95%; margin-left: 5px;">
	<div class="col-md-2"><label>Total Kms</label></div>
	<div class="col-md-2"><input type="text" id="dsTotalKms" name="dsTotalKms" value=""class="form-control" onblur="calculateExtraChangekm()" readonly="readonly" tabindex="-1"></input></div>
	<div class="col-md-2"><label>Total Hrs</label></div>
	<div class="col-md-2"><input type="text" id="dsTotalHrs" name="dsTotalHrs" value=""class="form-control" readonly="readonly" tabindex="-1"></input></div>
	<div class="col-md-2"><label>Night Stay</label></div>
	<div class="col-md-2"><input type="text" id="dsNightStay" name="dsNightStay" value=""class="form-control" onchange="nightChgDutySlip()" tabindex="240"></input></div>
</div> 
<div class="row" style="width: 95%; margin-left: 5px;">
	<div class="col-md-2"><label>Min.Chg.Kms</label></div>
	<div class="col-md-2"><input type="text" id="dsMinChgKms" name="dsMinChgKms" value=""class="form-control" readonly="readonly" tabindex="-1"></input></div>
	<div class="col-md-2"><label>Min.Chg.Hrs</label></div>
	<div class="col-md-2"><input type="text" id="dsminChgHrs" name="dsminChgHrs" value=""class="form-control" readonly="readonly" tabindex="-1"></input></div>
	<div class="col-md-2"><label>Day Stay</label></div>
	<div class="col-md-2"><input type="text" id="dsDayStay" name="dsDayStay" value=""class="form-control" onchange="dayChgDutySlip()" tabindex="250"></input></div>
</div> 
<div class="row" style="width: 95%; margin-left: 5px;">
      <div class="col-md-2"><label>Extra.Chg.Kms</label></div>
      <div class="col-md-2"><input type="text" id="dsextraChgKm" name="dsextraChgKm" value=""class="form-control" readonly="readonly" tabindex="-1"></input></div>
      <div class="col-md-2"><label>Extra.Chg.Hrs</label></div>
      <div class="col-md-2"><input type="text" id="dsextraChgHr" name="dsextraChgHr" value="" class="form-control" readonly="readonly" tabindex="-1"></input></div>
   <div class="col-md-2"><label>Billing Status</label></div>
   <div class="col-md-2">
		<select id="billingStatus" name="billingStatus" class="form-control" tabindex="260">
			<option value="toBeBillied" selected>To Be Billed</option>
			<option value="complimentary">Complementary</option>
			<option value="cancelled">Cancelled</option>
			<option value="cash">Cash</option>
			<option value="creditCard">Credit Card</option>
			<option value="noShow">No Show</option>
			<c:forEach var="carOwnerTypeList" items="${carOwnerTypeList}">
				<option value="${carOwnerTypeList.id}">${carOwnerTypeList.name}</option>
			</c:forEach>
		</select>
	</div>	
</div> 
<div class="row" style="width: 95%; margin-left: 5px;">
      <div class="col-md-2" ><label>Night Allowance</label></div>
      <div class="col-md-2"><input type="text" id="dsnightAllow" name="dsnightAllow" value=""class="form-control" onchange="totalSlipAmountDutySlip()" tabindex="270"></input></div>
      <div class="col-md-2"><label>Day Allowance</label></div>
      <div class="col-md-2"><input type="text" id="dsdayAllow" name="dsdayAllow" value=""class="form-control" onchange="totalSlipAmountDutySlip()" tabindex="280"></input></div>
      <div class="col-md-2"><label>State Tax</label></div>
      <div class="col-md-2"><input type="text" id="dsstateTax" name="dsstateTax" value=""class="form-control" onchange="totalSlipAmountDutySlip()" tabindex="290"></input></div>
</div>				
<div class="row" style="width: 95%; margin-left: 5px;">
      <div class="col-md-2"><label>Toll Charges</label></div>
      <div class="col-md-2"><input type="text" id="dstollCharge" name="dstollCharge" value=""class="form-control" onchange="totalSlipAmountDutySlip()" tabindex="300"></input></div>
      <div class="col-md-2"><label>Parking Charges</label></div>
      <div class="col-md-2"><input type="text" id="dsparkingCharge" name="dsparkingCharge" value=""class="form-control" onchange="totalSlipAmountDutySlip()" tabindex="310"></input></div>
      <div class="col-md-2"><label>Fuel Charges</label></div>
      <div class="col-md-2"><input type="text" id="fuelCharge" name="fuelCharge" class="form-control" onchange="totalSlipAmountDutySlip()" tabindex="320"></input></div>
</div>
<div class="row" style="width: 95%; margin-left: 5px;">
      <div class="col-md-2"><label>Guide Charges</label></div>
      <div class="col-md-2"><input type="text" id="guideCharge" name="guideCharge" class="form-control" onchange="totalSlipAmountDutySlip()" tabindex="330"></input></div>
      <div class="col-md-2"><label>Misc. Charges</label></div>
      <div class="col-md-2"><input type="text" id="dsmiscCharge" name="dsmiscCharge" class="form-control" onchange="totalSlipAmountDutySlip()" tabindex="340"></input></div>
      <div class="col-md-2"><label>Slip Amount</label></div>
      <div class="col-md-2"><input type="text" id="dsslipAmount" name="dsslipAmount" value=""class="form-control" tabindex="-1"></input></div>
</div>
<div class="row" style="width: 95%; margin-left: 5px;">
      <div class="col-md-2"><label>Remarks</label></div>
      <div class="col-md-6"><input type="text" id="dsremarks" name="dsremarks" value=""class="form-control" tabindex="350"></input></div>
</div>				
   <div class="row" style="margin-top: 25px;">
    <div class="col-md-6">
	     <div id="editDetailError" style="color:red;height:70px;font-size:11px;overflow-y:auto;display:none;"></div>
	     <div id="editDetailInfo" style="color:green;display:none;"></div>
    </div>
</div>    


<!--  invoice Date Panel -->
<div id="tariffDetPanel" class="modal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="tariffDetLabel" aria-hidden="true">
	<div class="modal-dialog modal-md">
  		<div class="modal-content">
  			<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
  				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;' id="tariffDetLabel"><b>Applied Tariff Detail</b> Invoice 
      			<button type="button" class="close" onClick="closeThisModal()" style="font-size:150%; color: #196780;">&times;</button></h4>
      		</div>
  			<div class="modal-body" id="tariffDetPanel_body" >
			<div class="row" >
				<div class="col-md-1" tabindex="-1"></div>
				<div class="col-md-8" style="background-color: #FFDA71; text-align: center;" tabindex="-1">Tariff Name</div>
				<div class="col-md-2" style="background-color: #FFDA71; text-align: center;" tabindex="-1">Value </div>
			</div>
			<div id='tariffDetTab' style="height:300px;width:98%;overflow:auto"></div>
			<div class="row" style="height: 45px;">
				<div class="col-sm-8">
					<div id="cancelReasonError" style="color:red;height: auto;font-size:11px;overflow-y:auto;display:none;"></div>
					<div id="cancelReasonInfo" style="color:green;display:none;"></div>
				</div>
			</div>
			<div class="row" style="height: 45px;">
				<div class="col-sm-5"></div>
				<div class="col-sm-1"><input type='button' value='Close ' class="btn btn-primary" style="width:160%" onClick="closeThisModal()"/></div>
			</div>	
   		</div>
   	 </div>
  	</div>
 </div>
