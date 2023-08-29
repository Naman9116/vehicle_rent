formFillUrl  = ctx + "/formFillForEditBooking.html";
saveUpdateUrl = ctx + "/saveOrUpdateBooking.html";
deleteUrl  = ctx + "/deleteBooking.html";



function refreshData(){	
	if($('#userTable').dataTable() != null) $('#userTable').dataTable().fnDestroy();
	userTable = $('#userTable').dataTable({
		"bLengthChange": true,
		"bFilter": true,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": true,
		"aLengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]]
	});
}
function fillMobile(){
	var mobileNo = $("#mobileNo").val();
	form_data = new FormData();
	form_data.append("mobileNo",mobileNo);
		$.ajax({  
			type: "POST",
			processData: false,
			contentType: false,
			async: false,
			url:  ctx + "/getCustomerAsPerMobile.html",  
			data: form_data,
			success : function(response) {
				if (response.status == "Success") {
					if(response.customerModel != null){
						$('#customerName').val(response.customerModel.name);
						$('#customerId').val(response.customerModel.id);
						$('#company').val(response.customerModel.company);
					
					}else{
						$('#company').val(loginCompanyId);
					}
		    	} else {
		    		$('#company').val(response.customerModel.company.id);
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
			},  
			error: function(e){  
			}  
		}); 
		fillBookings();
	}

function saveOrUpdateBooking() {
	alert("this"+$('#company').val());
	validateCustomer();
	if(!isValidationError){
		form_data = new FormData();
		form_data.append("idForUpdate", $('#idForUpdate').val());
		form_data.set("bookingNo", $('#bookingNo').val());
		form_data.set("bookingDate", $('#bookingDate').val());
		form_data.set("customerId.id", $('#customerId').val());
		form_data.set("name", $('#customerName').val());
		form_data.set("customerName", $('#customerName').val());
		form_data.set("mobileNo", $('#mobileNo').val());
		form_data.set("bookingTakenBy.id", loginUserId);
		form_data.set("branch.id", $('#branchName option:selected').val());
		form_data.set("outlet.id", $('#hubName  option:selected').val());
		form_data.set("pickUpDate", $('#bookingDetailsDate').val());
		form_data.set("startTime", $('#startTime').val());
		form_data.set("pickUpTime", $('#pickUpTime').val());
		form_data.set("flightNo", $('#flightNo').val());
		form_data.set("terminal", $('#terminal').val());
		form_data.set("mob.id", $('#MOB option:selected').val());
		form_data.set("carModel.id", $('#carModel').val());
		form_data.set("carModel.name", $('#carModel option:selected').text());
		form_data.set("rentalType.id", $('#rentalType').val());
		form_data.set("rentalType.name", $('#rentalType option:selected').text());
		form_data.set("tariff.id", $('#tariff').val());
		form_data.set("reportingAddress",$("#reportingAddress").val());
		form_data.set("toBeRealeseAt", $('#toBeReleaseAt').val());
		form_data.set("instruction", $('#instruction').val());
		form_data.set("flightNo", $('#flightNo').val());
		form_data.set("terminal", $('#terminal option:selected').val());
		form_data.set("company.id", $('#company').val());
		saveOrUpdateRecord(form_data);
		if( serviceResponse != null){
		    if(serviceResponse.bookingModel !=null){
				$('#bookingNo').val(serviceResponse.bookingModel.bookingNo);
				$('#idForUpdate').val(serviceResponse.bookingModel.id);
		    }
		}
  }	
}

function AssignFormControls(response){
	$("#idForUpdate").val(response.bookingModel.id);
	$("#bookingNo").val(response.bookingModel.bookingNo);
	$("#bookingDate").val(getTimeStampTo_DDMMYYYY(response.bookingModel.bookingDate));
	$('#customerId').val(response.bookingModel.customerId.id);
	$('#customerName').val(response.bookingModel.customerName);
	$('#mobileNo').val(response.bookingModel.mobileNo);
	$("#bookingTakenBy").val(response.bookingModel.bookingTakenBy.userName);
	$('#branchName').val(response.bookingModel.branch.id);
	fillHub();
	$('#hubName').val(response.bookingModel.outlet.id);
	$('#bookingDetailsDate').val(getTimeStampTo_DDMMYYYY(response.bookingModel.pickUpDate));
	$('#startTime').val(response.bookingModel.startTime);
	$('#pickUpTime').val(response.bookingModel.pickUpTime);
	$('#flightNo').val(response.bookingModel.flightNo);
	$('#terminal').val(response.bookingModel.terminal);
	$('#MOB').val(response.bookingModel.mob.id);
	$('#carModel').val(response.bookingModel.carModel.id);
	$('#rentalType').val(response.bookingModel.rentalType.id);
	$('#reportingAddress').val(response.bookingModel.reportingAddress);
	$('#toBeReleaseAt').val(response.bookingModel.toBeRealeseAt);
	 fillTariff();
	$('#tariff').val(response.bookingModel.tariff.id);
	$('#instruction').val(response.bookingModel.instruction);
	$('#terminal').prop('disabled', true);
	$('#flightNo').prop('disabled', true);
	$('#flightNo').val(response.bookingModel.flightNo);
	$('#terminal').val(response.bookingModel.terminal);
	$('#updateButton').prop('disabled', false);
	$('#addButton').prop('disabled', true);
	}

function formFillDetailRecordBooking(formFillForEditId) {
	formFillRecord(formFillForEditId);
	enableDisableAirportAndTerminal();
}

function deleteBooking(idForDelete) {
	deleteRecord(idForDelete);
} 



function validateCustomer(){
	isValidationError = false;
	var validationErrorHtml = '<ol>';
	if( $('#customerName').val()==""){
		validationErrorHtml = validationErrorHtml + "<li>  Customer Name can not be blank</li>";
		isValidationError = true;
	}
	if( $('#mobileNo').val()==""){
		validationErrorHtml = validationErrorHtml + "<li>  Mobile Number can not be blank</li>";
		isValidationError = true;
	}

	if( $('#mobileNo').val().length<10){
		validationErrorHtml = validationErrorHtml + "<li>Mobile No Should be 10 digit</li>";
		isValidationError = true;
	}
	
	if( $('#branchName option:selected').val()=="0"){
		validationErrorHtml = validationErrorHtml + "<li> Branch can not be blank</li>";
		isValidationError = true;
	}
	if( $('#hubName option:selected').val()=="0"){
		validationErrorHtml = validationErrorHtml + "<li> hub can not be blank</li>";
		isValidationError = true;
	}
	if( $('#bookingDate').val()==""){
		validationErrorHtml = validationErrorHtml + "<li>  Booking Date can not be blank</li>";
		isValidationError = true;
	}
	if( $('#bookingDetailsDate').val()==""){
		validationErrorHtml = validationErrorHtml + "<li>  Pick Up Date can not be blank</li>";
		isValidationError = true;
	}
	if( $('#startTime').val()==""){
		validationErrorHtml = validationErrorHtml + "<li>  Start Time can not be blank</li>";
		isValidationError = true;
	}
	if( $('#pickUpTime').val()==""){
		validationErrorHtml = validationErrorHtml + "<li>  Pick Time can not be blank</li>";
		isValidationError = true;
	}
	
	if( $('#rentalType option:selected').val()=="0"){
		validationErrorHtml = validationErrorHtml + "<li> RentalType can not be blank</li>";
		isValidationError = true;
	}
	if( $('#tariff option:selected').val()=="0"){
		validationErrorHtml = validationErrorHtml + "<li> Tariff can not be blank</li>";
		isValidationError = true;
	}
	if( $('#carModel option:selected').val()=="0"){
		validationErrorHtml = validationErrorHtml + "<li> CarModel can not be blank</li>";
		isValidationError = true;
	}
	if( $('#reportingAddress').val()==""){
		validationErrorHtml = validationErrorHtml + "<li>  Reporting Address can not be blank</li>";
		isValidationError = true;
	}
	if( $('#toBeReleaseAt').val()==""){
		validationErrorHtml = validationErrorHtml + "<li> To Be ReleasedAt can not be blank</li>";
		isValidationError = true;
	}
	if( $('#MOB option:selected').val()=="0"){
		validationErrorHtml = validationErrorHtml + "<li> MOB can not be blank</li>";
		isValidationError = true;
	}
 	if($("#startTime").val()!=""){
	    if(!checkStart()){
	    	validationErrorHtml = validationErrorHtml + "<li>Start Time can not be less than current Date and Time.</li>";
	    	isValidationError = true;
	    }
 	}
	validationErrorHtml = validationErrorHtml + "</ol>";
	if(isValidationError){
		$('#error').html("Please correct following errors:<br>" + validationErrorHtml);
		$('#error').hide();
		$('#error').show();
	}else{
		$('#error').html("");
		$('#error').hide();
	}
}

function fillTariff(){
	var rentalId=$("#rentalType option:selected").val();
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/getTariffAsPerRentalType.html",  
		data: "id="+rentalId,  
		success : function(response) {
			if (response.status == "Success") {
				$("#tariff").empty().append(
				'<option value="0">---Select---</option>');
				$.each(response.generalMasterModelList, function(index,val) {
				$("#tariff").append('<option value="'+val.id+'">' + val.name+ '</option>');
				});
			} 
		},  
		error: function(e){  
		}  
	}); 
	enableDisableAirportAndTerminal();
}

function fillHub(){
	var rentalId=$("#branchName option:selected").val();
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/getTariffAsPerRentalType.html",  
		data: "id="+rentalId,  
		success : function(response) {
			if (response.status == "Success") {
				$("#hubName").empty().append(
				'<option value="0">---Select---</option>');
				$.each(response.generalMasterModelList, function(index,val) {
				$("#hubName").append('<option value="'+val.id+'">' + val.name+ '</option>');
				});
			} 
		},  
		error: function(e){  
		}  
	});  
}
function fillBookings(){
	var mobileNo = $("#mobileNo").val();
	form_data = new FormData();
	form_data.append("mobileNo",mobileNo);
		$.ajax({  
			type: "POST",
			processData: false,
			contentType: false,
			async: false,
			url:  ctx + "/getBookingAsPerMobile.html",  
			data: form_data,
			success : function(response) {
				if (response.status == "Success") {
					if(response.bookingModel != null){
						$("#bookingNo").val(response.bookingModel.bookingNo);
						$("#bookingTakenBy").val(response.bookingModel.bookingTakenBy.userName);
						$('#branchName').val(response.bookingModel.branch.id);
						fillHub();
						$('#hubName').val(response.bookingModel.outlet.id);
						$('#lastBooking').val(getTimeStampTo_DDMMYYYY(response.bookingModel.pickUpDate));
						$('#startTime').val(response.bookingModel.startTime);
						$('#pickUpTime').val(response.bookingModel.pickUpTime);
						$('#flightNo').val(response.bookingModel.flightNo);
						$('#terminal').val(response.bookingModel.terminal);
						$('#MOB').val(response.bookingModel.mob.id);
						$('#carModel').val(response.bookingModel.carModel.id);
						$('#rentalType').val(response.bookingModel.rentalType.id);
						$('#reportingAddress').val(response.bookingModel.reportingAddress);
						$('#toBeReleaseAt').val(response.bookingModel.toBeRealeseAt);
						$('#instruction').val(response.bookingModel.instruction);
						 fillTariff();
						$('#tariff').val(response.bookingModel.tariff.id);
						enableDisableAirportAndTerminal();
						$('#flightNo').val(response.bookingModel.flightNo);
						$('#terminal').val(response.bookingModel.terminal);
					}
		    	} else {
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
			},  
			error: function(e){  
			}  
		});  
	}

function enableDisableAirportAndTerminal(){
	if($("#rentalType option:selected").text()=="Airport Transfer"){
		$('#terminal').prop('disabled', false);
		$('#flightNo').prop('disabled', false);
	}
	else{
		$('#terminal').prop('disabled', true);
		$('#flightNo').prop('disabled', true);
	}
}

function checkStart() {
	var start = ($("#bookingDetailsDate").val()).concat(" ").concat($("#startTime").val());
	var today = getTimeStampTo_ddMMyyyyHHmmss(new Date(),'dd/mm/yyyy hh:mm')
	var ms = moment(start, "DD/MM/YYYY HH:mm").diff(moment(today, "DD/MM/YYYY HH:mm"));
	var d = moment.duration(ms);
	if (d < 0) {
		return false;
	}
	return true;
}
