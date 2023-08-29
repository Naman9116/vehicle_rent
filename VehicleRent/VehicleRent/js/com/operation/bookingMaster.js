formFillUrl  = ctx + "/formFillForEditBookingMaster.html";
saveUpdateUrl = ctx + "/saveOrUpdateBookingMaster.html";
deleteUrl  = ctx + "/deleteBookingMaster.html";
fetchExtraData = "&formType=M";
var recNumber=0, fromIndex=0, toIndex=0;
var maxRecordMain = 0;
var bookingDetailsModelArray = new Array();
var isValidationError = false;	
var isDuplicate = true;
var isBlank = false;
var form_data= new FormData();
var userTable;
var clientMobile = new Array();
var BookerEmail = new Array();
var compName = "";
var tariffList;
var checkIsPassengerStatus='N'
var i = 1;

function localScriptWithRefresh(){}

function resetData(actionTyype){
	var actionTyype = actionTyype || "";
	if(actionTyype != "" ){
		isValidationError = false;
		form_data = new FormData();
		bookingDetailsModelArray = new Array();
		if(actionTyype != "O"){
			resetDetail();
		}
	}else{
		var dataString = " <table style='table-layout:fixed; width:99%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
			+ " <thead style='background-color: #FED86F;'> "
			+ " <tr style='font-size:8pt;'> 									"
			+ " 	<th width=6%>Action</th> 		  	"
			+ " 	<th width=9%>Booking Date</th> 		"			
			+ " 	<th width=10%>Branch</th> 			"
			+ " 	<th width=10%>Hub</th> 				"
			+ " 	<th width=10%>Rental Type</th> 		"
			+ " 	<th width=10%>Tariff</th> 			"
			+ " 	<th width=10%>Car Model</th> 		"
			+ " 	<th width=10%>Flight No</th> 		"
			+ " 	<th width=10%>Start Time</th> 		"
			+ " 	<th width=10%>PickUp Time</th> 		"
			+ " 	<th width=10%>Reporting At</th> 	"
			+ " 	<th width=10%>Drop At</th> 			"
			+ " 	<th width=10%>Spl. Instruction</th> "
			+ " </tr>						"
			+ " </thead></table>					" ;
		$("#dataTable2").html(dataString);
		isValidationError = false;		
		resetDetail();
		form_data = new FormData();
		bookingDetailsModelArray = new Array();
	}
}

function resetDetail(){
	$('#branchName').val("0");
	$('#hubName').val("0");
	$('#bookingDetailsDate').val(getTimeStampTo_DDMMYYYY(new Date()));		
	$('#startTime').val("");
	$('#pickUpTime').val("");
	$('#flightNo').val("");
	$('#terminal').val("0");
	$('#MOB option').each(function() {
	       if($(this).text() == 'Credit') {
	           $(this).prop("selected", true);
	       }
	});

	$('#carModel').val("0");
	$('#rentalType').val("0");
	$('#tariff').val("0");
	$('#officeAddress').val("0");
	$('#reportingAddress').val("");
	$('#toBeReleaseAt').val("");
	$('#instruction').val("");
	isValidationError = false;
	$('#saveAsNewButton').parent().parent().find('td:eq( 1 )').attr('width','50%').end();
	$('#saveAsNewButton').parent().parent().find('td:last').attr('width','30%').end();
	$('#saveAsNewButton').hide();
	$('#saveButton').prop('disabled',false);
	$('#updateButton').attr("onclick","updateBooking('O','')");
	isServerSideError = false;
	$('#error').hide();
	$('#info').hide();
}

function checkStart() {
	var diff = null;
	var start = ($("#bookingDetailsDate").val()).concat(" ").concat($("#startTime").val());
	var today = $("#bookingDate").val();
	var ms = moment(start, "DD/MM/YYYY HH:mm").diff(moment(today, "DD/MM/YYYY HH:mm"));
	var d = moment.duration(ms);
	if (d < 0) {
		return false;
	}
	return true;
}

function checkPickUp(){
	if($("#pickUpTime").val() == "00:00" && $("#startTime").val() == "00:00"){
		return true;
	}else{
		var pickup = ($("#bookingDetailsDate").val()).concat(" ").concat($("#pickUpTime").val());
		var start =  ($("#bookingDetailsDate").val()).concat(" ").concat($("#startTime").val());
		var ms = moment(pickup, "DD/MM/YYYY HH:mm").diff(moment(start, "DD/MM/YYYY HH:mm"));
		var d = moment.duration(ms);
		var min = (d / 100) / 60;
		if (min < 60) {
			return false;
		}
		return true;
	}
	
}

function fillTariff(value){
	$("#tariff")
    .find('option')
    .remove()
    .end()
    .append('<option value="0">--- Select ---</option>')
    .val('0');
	$.each(tariffList, function (i, item) {
		if(item.masterId == value){
		    $("#tariff").append($('<option>', { 
		        value: item.id,
		        text : item.name,
		    }));
		}
	});
}

function fillCarModel(){
	var corporateId = $("#corporateId").val();
	var branchId    = $("#branchName").val();
	var tariffId    = $("#tariff").val();
	var bookDate    = $("#bookingDetailsDate").val();
	
	if(corporateId == 0 ) alert("Please select corporate");
	if(branchId == 0 ) alert("Please select branch");
	if(tariffId == 0 ) alert("Please select Tariff Scheme");
	if(corporateId == 0 || branchId == 0 || tariffId == 0) return;
	
	formData = new FormData();
	formData.append("corporateId",corporateId);
	formData.append("branchId",branchId);
	formData.append("tariffId",tariffId);
	formData.append("date",bookDate);
	
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/fillCareModelAsCorpBr.html",  
		processData: false,
		contentType: false,
		async:false,
		data : formData,
		success : function(response) {
			if (response.status == "Success") {
				refillCombo('#carModel',response.carModelList);
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

function fillValues(corporateId){
	if(corporateId <= 0) return;
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/fillAsPerCorporate.html",  
		data: "corporateId="+corporateId,  
		success : function(response) {
			if (response.status == "Success") {
				refillCombo('#bookedBy',response.generalMasterModelList);
				refillCombo('#clientName',response.autorizedUserModelList);
				refillCombo('#branchName',response.branchList);
				refillCombo('#tariff',response.corpTariffList);
				refillCombo('#carModel',response.carModelList);

				response.corporateModel.branId == "0"?"":$("#branchName").val(response.corporateModel.branId);
				response.corporateModel.branId == "0"?"":fillHub(response.corporateModel.branId);
	    	    $('#companyName').val(response.corporateModel.compId.id);
	    	    compName = response.corporateModel.compId.name;
	    	    $('#companyCode').val(response.corporateModel.compId.itemCode);

	    	    $.each(response.autorizedUserModelList, function (i, item) {
					clientMobile[i] = item.remark;
				});
				$.each(response.generalMasterModelList, function (i, item) {
					BookerEmail[i] = item.remark;
				});
				$("#officeAddress")
				.find('option')
				.remove()
				.end()
				.append('<option value="0">--- Select ---</option>')
				.val('0');
				if(response.addressDetail.address1 != ""){
					$("#officeAddress").append($('<option>', { 
						value: "1",
						text : response.addressDetail.address1 + " " + response.addressDetail.address2
					}));
				} 
				if(response.corporateModel.isPassengerInfo=="Y"){
					checkIsPassengerStatus='Y'
					$("#addPassengerButton ").prop('disabled', false);
				}else{
					checkIsPassengerStatus='N'
					$("#addPassengerButton ").prop('disabled', true);
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

function fillClientMobile(){
    if($("#clientName").val()!="0"){
	var mobAndEmail = (clientMobile[$("#clientName option:selected").index() - 1]).split('#');    	
	$("#mobileNo").val(mobAndEmail[0]);
	$("#emailToClient").val(mobAndEmail[1]);
    }else{
	$("#mobileNo").val("");
	$("#emailToClient").val("");
    }    	
}

function fillBookerEmail(){
	$("#emailToBooker").val(BookerEmail[$("#bookedBy option:selected").index() - 1]);
}

function fillBranch(companyId){
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/fillBranchAsCompany.html",  
		data: "companyId="+companyId+"&corporateId="+$("#corporateId").val(),  
		success : function(response) {
			if (response.status == "Success") {
				refillCombo('#branchName',response.generalMasterModelList);
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

function fillHub(branchId){
	var corporateId = $("#corporateId").val();
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/fillHubAsBranch.html",  
		data: "branchId="+branchId+"&corporateId="+corporateId,  
		success : function(response) {
			if (response.status == "Success") {
				refillCombo('#hubName',response.generalMasterModelList);
				refillCombo('#terminal',response.terminalList);
				refillCombo('#rentalType',response.corpRentalList);
				refillCombo('#tariff',response.corpTariffList);
				tariffList = response.corpTariffList;
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

function getCarModel_usingCarType(carTypeId,fieldDefaultSelectedIndex){
	$.ajax({  
		type: "POST",  	
		url:  ctx + "/getCarModelAsCarType.html",  
		data: "carTypeId=" + carTypeId,  
		success: function(response){
			if(response.status == "Success"){
				refillCombo('#carModel',response.generalMasterModelList);
				$('#carModel').val(fieldDefaultSelectedIndex);
			}
			else{
				$('#error').html(response.result);
				$('#info').hide('slow');
				$('#error').show('slow');
			}
		},  
		error: function(e){  
			alert(e.status);
		}  
	});  
}

function AssignFormData(actionType,updateIndex){
	if(updateIndex !='')
		validateBookingDetails(actionType,updateIndex);
	else
		validateBookingDetails(actionType,'');
	
	if(!isValidationError){
		checkDuplicateEntry();
		if(!isDuplicate){
			if(actionType == "N" && form_data.get("corporateId.id") != null) return;
			if((form_data.get("corporateId.id")==null || actionType == "U" || actionType == "S" || actionType == "O" || actionType == "A") && (updateIndex == '')){	    			    		
				form_data = new FormData();
				bookingDetailsModelArray = new Array();
			}
			form_data.set("bookingNo", $('#bookingNo').val());
			form_data.set("bookingDate", $('#bookingDate').val());
			form_data.set("corporateId.id", $('#corporateId').val());
			form_data.set("corporateId.name", $('#corporateId option:selected').text());
			form_data.set("bookedBy.id", $('#bookedBy').val());
			form_data.set("bookedBy.name", $('#bookedBy option:selected').text());
			form_data.set("bookedFor", $("#opClient").prop('checked')==true?"C":"O");
			form_data.set("bookedForName", $("#opClient").prop('checked')==true?$("#clientName option:selected").text():$("#otherClient").val());
			form_data.set("mobileNo", $('#mobileNo').val());
			form_data.set("referenceNo", $('#referenceNo').val());
			form_data.set("bookingTakenBy.id", loginUserId);
			form_data.set("smsToClient", $('#smsToClient').is(':checked')?"Y":"N");
			form_data.set("smsToBooker", $('#smsToBooker').is(':checked')?"Y":"N");
			form_data.set("smsToOther", $('#smsToOther').val());
			form_data.set("emailToClient", $('#emailToClient').is(':checked')?"Y":"N");
			form_data.set("emailToBooker", $('#emailToBooker').is(':checked')?"Y":"N");
			form_data.set("emailToOther", $('#emailToOther').val());
			form_data.set("sCriteria", $('#searchName').val());
			form_data.set("sValue", $('#searchValue').val());
			form_data.set("companyName", compName);
			form_data.set("companyCode", $('#companyCode').val());
			form_data.set("cityName", $('#hubName option:selected').text());
			form_data.set("clientEmail", $('#emailToClient').val());
			form_data.set("bookerEmail", $('#emailToBooker').val());

			var bookingDetailsModelArraySize=0,loopCount=0;
			var iCount = 0;
			if((actionType=="S" || actionType=="O" || actionType == "U" || actionType == "A") && (updateIndex == '')){
				if (document.getElementById('opDateRange').checked){
					loopCount = daysBetween($('#DateRangeFrom').val(), $('#DateRangeTo').val()) + 1;
				}else if(document.getElementById('opNoCars').checked){
					loopCount = parseInt($('#NoOfCars').val());
				}
				fromIndex = bookingDetailsModelArray.length;
				toIndex   = ( fromIndex - 0) + loopCount;
			}else if((actionType=="S" || actionType=="O" || actionType == "U") && (updateIndex != '')){
				//removeFormDataBasedOnIndex(updateIndex);		    		
				fromIndex = updateIndex;
				toIndex = ( fromIndex - 0) + 1;
				iCount = fromIndex;
			}else{
				fromIndex = 0;
				toIndex = 1;
			}
			for (; iCount < (toIndex - fromIndex); iCount++) {
				form_data.set("bookingDetailModel["+iCount+"].company.id", $('#companyName').val());
				form_data.set("bookingDetailModel["+iCount+"].branch.id", $('#branchName').val());
				form_data.set("bookingDetailModel["+iCount+"].outlet.id", $('#hubName').val());
				form_data.set("bookingDetailModel["+iCount+"].outlet.name", $('#hubName option:selected').text());
				if (document.getElementById('opDateRange').checked){
					if($("#DateRangeFrom").val() == "") $("#DateRangeFrom").val($("#bookingDetailsDate").val());
					form_data.set("bookingDetailModel["+iCount+"].pickUpDate", addDays($('#DateRangeFrom').val(),iCount));
				}else{
					form_data.set("bookingDetailModel["+iCount+"].pickUpDate", $('#bookingDetailsDate').val());
				}
				form_data.set("bookingDetailModel["+iCount+"].startTime", $('#startTime').val());
				form_data.set("bookingDetailModel["+iCount+"].pickUpTime", $('#pickUpTime').val());
				form_data.set("bookingDetailModel["+iCount+"].flightNo", $('#flightNo').val());
				if($('#terminal').val()!="0") form_data.set("bookingDetailModel["+iCount+"].terminal.id", $('#terminal').val());
				form_data.set("bookingDetailModel["+iCount+"].mob.id", $('#MOB').val());
				form_data.set("bookingDetailModel["+iCount+"].mob.name", $('#MOB option:selected').text());
	
				form_data.set("bookingDetailModel["+iCount+"].carModel.id", $('#carModel').val());
				form_data.set("bookingDetailModel["+iCount+"].carModel.name", $('#carModel option:selected').text());
				form_data.set("bookingDetailModel["+iCount+"].rentalType.id", $('#rentalType').val());
				form_data.set("bookingDetailModel["+iCount+"].rentalType.name", $('#rentalType option:selected').text());
				form_data.set("bookingDetailModel["+iCount+"].tariff.id", $('#tariff').val());
				form_data.set("bookingDetailModel["+iCount+"].reportAt", $("#opOffice").prop('checked')==true?"F":"O");
				form_data.set("bookingDetailModel["+iCount+"].reportingAddress", $("#opOffice").prop('checked')==true?$("#officeAddress option:selected").text():$("#reportingAddress").val());
				form_data.set("bookingDetailModel["+iCount+"].toBeRealeseAt", $('#toBeReleaseAt').val());
				form_data.set("bookingDetailModel["+iCount+"].instruction", $('#instruction').val());

				if(actionType=="A")
					bookingDetailsModelArray.push($('#branchName').val());
				else if(actionType=="U" && updateIndex != '0'){
	
				}
				else
					bookingDetailsModelArray = new Array();
			}
		}else{		    	      
		}	    	        	    
	}
}	

function AssignFormControls(response){
	if(fetchExtraData == "&formType=D"){
		fetchExtraData == "&formType=M";
		AssignFormDetailControls(response.bookingDetailModel);
	}else{
		AssignFormControlsMain(response.bookingMasterModel);
	}
}

function AssignFormControlsMain(bookingMasterModel){
	$('#idForUpdate').val(bookingMasterModel.id);
	$("#bookingNo").val(bookingMasterModel.bookingNo);
	$("#bookingDate").val(getTimeStampTo_DDMMYYYY(bookingMasterModel.bookingDate));
	$("#bookingNo").val(bookingMasterModel.bookingNo);
	$('#corporateId').val(bookingMasterModel.corporateId.id);
	fillValues(bookingMasterModel.corporateId.id);
	$('#bookedBy').val(bookingMasterModel.bookedBy.id);
	bookingMasterModel.bookedFor=="C"?$("#opClient").prop('checked',true):$("#opOther").prop('checked',true);
	bookingMasterModel.bookedFor=="C"?$("#clientName").val($('select[name="bookedForName"] > option:contains("'+bookingMasterModel.bookedForName+'")').val()):$("#otherClient").val(bookingMasterModel.bookedForName);
	if($("#opClient").prop('checked')){
		$('#clientName').show();
		$('#otherClient').val("");
		$('#otherClient').hide();
	}else{
		$('#clientName').val("0");
		$('#clientName').hide();
		$('#otherClient').show()
	}
	$('#mobileNo').val(bookingMasterModel.mobileNo);
	$('#referenceNo').val(bookingMasterModel.referenceNo);
	$("#bookingTakenBy").val(bookingMasterModel.bookingTakenBy.name);

	bookingMasterModel.smsToClient == "Y"?$('#smsToClient').prop('checked', true):$('#smsToClient').prop('checked', false);
	bookingMasterModel.smsToBooker == "Y"?$('#smsToBooker').prop('checked', true):$('#smsToBooker').prop('checked', false);
	$("#smsToOther").val(bookingMasterModel.smsToOther); 
	bookingMasterModel.emailToClient == "Y"?$('#emailToClient').prop('checked', true):$('#emailToClient').prop('checked', false);
	bookingMasterModel.emailToBooker == "Y"?$('#emailToBooker').prop('checked', true):$('#emailToBooker').prop('checked', false);
	$("#emailToOther").val(bookingMasterModel.emailToOther); 
	AssignFormDetailControls(bookingMasterModel.bookingDetailModel[0]);
	fillBookingTable(bookingMasterModel);
}

function fillBookingTable(bookingMasterModel){
	var dataString = " <table style='table-layout:fixed; width:99%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
		+ " <thead style='background-color: #FED86F;'> "
		+ " <tr style='font-size:8pt;'> 									"
		+ " 	<th width=6%>Action</th> 		  	"
		+ " 	<th width=9%>Booking Date</th> 		"			
		+ " 	<th width=10%>Branch</th> 			"
		+ " 	<th width=10%>Hub</th> 				"
		+ " 	<th width=10%>Rental Type</th> 		"
		+ " 	<th width=10%>Tariff</th> 			"
		+ " 	<th width=10%>Car Model</th> 		"
		+ " 	<th width=10%>Flight No</th> 		"
		+ " 	<th width=10%>Start Time</th> 		"
		+ " 	<th width=10%>PickUp Time</th> 		"
		+ " 	<th width=10%>Reporting At</th> 	"
		+ " 	<th width=10%>Drop At</th> 			"
		+ " 	<th width=10%>Spl. Instruction</th> "
		+ " </tr>						"
		+ " </thead>					" 
		+ " <tbody>					";
	$.each(bookingMasterModel.bookingDetailModel, function (i, item) {
		if(item.status == null || item.status != "N"){
			dataString = dataString + " <tr> " 
			+ " <td>" 
			+ " 	<a href='javascript:formFillDetailRecord("+ item.id + ","+i+")'><img src='./img/editButton.png' border='0' title='Edit' width='20' height='20'/></a> "
			+ "     <a href='javascript:deleteDetailRecord("+ item.id + ")' ><img src='./img/deleteButton.png' border='0' title='Delete / Cancel' width='20' height='20'/></a> " 
			+ " </td> 														"
			+ " <td style='word-wrap:break-word'>" + getTimeStampTo_DDMMYYYY(item.pickUpDate) + "</td>  		"			
			+ " <td style='word-wrap:break-word'>" + item.branch.name + "</td>  		"
			+ " <td style='word-wrap:break-word'>" + item.outlet.name + "</td>  		"
			+ " <td style='word-wrap:break-word'>" + item.rentalType.name + "</td>  		"
			+ " <td style='word-wrap:break-word'>" + item.tariff.name + "</td>  		"
			+ " <td style='word-wrap:break-word'>" + item.carModel.name + "</td>  		"
			+ " <td style='word-wrap:break-word'>" + item.flightNo + "</td>  		"
			+ " <td style='word-wrap:break-word'>" + item.startTime + "</td>  		"
			+ " <td style='word-wrap:break-word'>" + item.pickUpTime + "</td>  		"
			+ " <td style='word-wrap:break-word'>" + item.reportingAddress + "</td>  		"
			+ " <td style='word-wrap:break-word'>" + item.toBeRealeseAt + "</td>  		"
			+ " <td style='word-wrap:break-word'>" + item.instruction + "</td>  		"
			+ " </tr> 		";
		}
	});
	dataString = dataString + "</tbody></table>";
	$('#dataTable2').html(dataString);
	refreshData();
}

function AssignFormDetailControls(bookingDetailModel){
	var responseDetail = bookingDetailModel;
	$('#branchName').val(responseDetail.branch.id);		
	fillHub(responseDetail.branch.id);
	$('#hubName').val(responseDetail.outlet.id);
	$('#bookingDetailsDate').val(getTimeStampTo_DDMMYYYY(responseDetail.pickUpDate));
	$('#startTime').val(responseDetail.startTime);
	$('#pickUpTime').val(responseDetail.pickUpTime);
	$('#flightNo').val(responseDetail.flightNo);
	$('#terminal').val(responseDetail.terminal != null?responseDetail.terminal.id:"0");
	$('#MOB').val(responseDetail.mob.id);

	$('#carModel').val(responseDetail.carModel.id);
	$('#rentalType').val(responseDetail.rentalType.id);
	$('#tariff').val(responseDetail.tariff.id);
	responseDetail.reportAt=="F"?$('#opOffice').prop('checked', true):$('#opReport').prop('checked', true);
	responseDetail.reportAt=="F"?$("#officeAddress").val($('select[name="officeAddress"] > option:contains("'+responseDetail.reportingAddress+'")').val()):$("#reportingAddress").val(responseDetail.reportingAddress);
	if($("#opOffice").prop('checked')){
		$('#officeAddress').show();
		$('#reportingAddress').val("");
		$('#reportingAddress').hide();
	}else{
		$('#officeAddress').val("0");
		$('#officeAddress').hide();
		$('#reportingAddress').show()
	}
	$('#toBeReleaseAt').val(responseDetail.toBeRealeseAt);
	$('#instruction').val(responseDetail.instruction);
}

function addBooking(type){
    validateBookingDetails('A',"");
   
    if(isValidationError){
		return false;
	}else{
		$("#DateRangeFrom").val($("#bookingDetailsDate").val());
		$("#DateRangeTo").val($("#bookingDetailsDate").val());
		$('#multiBookingOption').modal('show');
	}	
}

function callFromModel(){	
	AssignFormData('A','');
	if(isValidationError){
		return false;
	}else{		    
		form_data.set("idForUpdate", $('#idForUpdate').val());
		var tempVal = $('#idForUpdate').val();
		form_data.set("idForUpdateDetail", "");
//		createBookingDataRecord(bookingDetailsModelArray.length-1);
		createBookingDataRecord();
		$('#saveButton').prop('disabled',false);
//		disableMasterFields();
	}
}

function saveOrUpdateBooking(type) {
	AssignFormData('S','');
	if(type=='S'){
		form_data.set("idForUpdate", -1);
	}else{
		form_data.set("idForUpdate", $('#idForUpdate').val());
	}
	form_data.set("idForUpdateDetail", $('#idForUpdateDetail').val());
	saveOrUpdateRecord(form_data);
	
}  

function saveBooking(type) {
	checkAllBlankDetailFields();
	if(!isBlank){
		AssignFormData(type,'');
		 $(".mob").each(function(){
		 	var id = (this.id).split("_"); 
	 		form_data.append("name",$("#passengerName_"+id[1]).val());
			form_data.append("mobile",$("#passengerMob_"+id[1]).val());
			form_data.append("age",$("#passengerAge_"+id[1]).val());
			form_data.append("sex",$("#passengerSex_"+id[1]).val());
			form_data.append("idDetails",$("#passengerId_"+id[1]).val());
		});
	}
	if(isValidationError){
		if(bookingDetailsModelArray.length > 0 && type == 'N'){
			if(confirm("Are you sure to save this booking without including current booking details.")){
				form_data.set("idForUpdate", $('#idForUpdate').val());
				form_data.set("idForUpdateDetail", "");
				saveOrUpdateRecord(form_data);
				if(!isServerSideError)
					refreshData();			    
			}else{
				return false;
			}
		}
	}else{		    
		if(type == 'O'){
			form_data.set("idForUpdate", "");
		}else{
			form_data.set("idForUpdate", $('#idForUpdate').val());
		}		    	
		form_data.set("idForUpdateDetail", "");
		if(form_data.get("bookingDetailModel[0].branch.id")==null){
			return false;
		}
		saveOrUpdateRecord(form_data,type);
		if(type != "O" && serviceResponse != null){
		    if(serviceResponse.bookingMasterModel !=null){
				$('#bookingNo').val(serviceResponse.bookingMasterModel.bookingNo);
				$('#idForUpdate').val(serviceResponse.bookingMasterModel.id);
		    }
		    $('#dataTable2').html(serviceResponse.dataGrid);					   				    
		}					    				

		if(type == 'O' && !isServerSideError){
			userTable.fnDestroy();
		}
		if(!isServerSideError)
			resetData();
	}
	document.location.reload();
}  

function updateBooking(type, tIndex) {
	if((type == 'N' || type == 'U') && tIndex != ''){
		AssignFormData('U',tIndex);
	}else{
		AssignFormData('U','');
	}		
	if(isValidationError){
		return false;
	}else{
		if(type == 'N' && tIndex !=""){
			updateDataTable(tIndex);
			$('#saveButton').prop('disabled',false);
			$('#updateButton').attr("onclick","updateBooking('O','')");
			$('#updateButton').prop('disabled',true);
			$("#addButton").prop('disabled',false);
			return false;
		}else{
			if(type=='S'){
				form_data.set("idForUpdate", -1);
			}else{
				form_data.set("idForUpdate", $('#idForUpdate').val());
			}
			form_data.set("idForUpdateDetail", $('#idForUpdateDetail').val());
			saveOrUpdateRecord(form_data);
			if(!isServerSideError)
				refreshData();
			document.getElementById("addButton").disabled = false;
			document.getElementById("updateButton").disabled = true;
			document.getElementById("saveButton").disabled = false;
			$('#idForUpdate').val('');        		
			$('#idForUpdateDetail').val('');        		
			$('#saveAsNewButton').parent().parent().find('td:eq( 1 )').attr('width','50%').end();
			$('#saveAsNewButton').parent().parent().find('td:last').attr('width','30%').end();
			$('#saveAsNewButton').hide();
		}        		
	}
}  

function SearchBooking(){
	AssignFormData('S','');
	if(isValidationError){
		return false;
	}else{        		
		form_data.set("idForUpdate", -1);
		form_data.set("idForUpdateDetail", $('#idForUpdateDetail').val());
		saveOrUpdateRecord(form_data);        	    	
		recNumber=0;
		maxRecordMain = serviceResponse.bookingMasterList.length;
		if(maxRecordMain == 0){
			$("#recPos").val((recNumber)+"/"+ maxRecordMain);
		}else{
			$("#recPos").val((recNumber + 1)+"/"+ maxRecordMain);
		}
		if(maxRecordMain>0){
			AssignFormControlsMain(serviceResponse.bookingMasterList[recNumber]);
			changeButtonsAttribute();
		}else{
			refreshData();
		}        		   
	}
}

function deleteDetailRecord(idForDelete) {
	if(isAccessDenied("D")) return;	
	if (confirm('Are you sure you want to delete this Record?')) {						
		$.ajax({
			type : "POST",
			url:  ctx + "/deleteBookingDetailRecord.html",				
			data : "idForDelete="+idForDelete+"&idForOperate="+$('#idForUpdate').val(),
			success : function(response) {
				if (response.status == "Success") {
					$('#info').html(response.result);
					$('#dataTable2').html(response.dataGrid);
					refreshData();
					$('#info').show('slow');
					$('#error').hide('slow');
				} else {
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
			},
			error : function(e) {
				alert('Error: ' + e);
			}
		});
	}
}

function formFillDetailRecord(formFillForEditId,iIndex){
	fetchExtraData = "&formType=D";
	var tempVal = $('#idForUpdate').val();
	formFillRecord(formFillForEditId);
	disableMasterFields();
	$('#saveButton').prop('disabled',true);
	$('#idForUpdate').val(tempVal);
	$('#idForUpdateDetail').val(formFillForEditId);
	$('#updateButton').attr("onclick","updateBooking('U','"+iIndex+"')");
}

function viewDetailRecord(formFillForEditId){
	fetchExtraData = "&formType=D";
	var tempVal = $('#idForUpdate').val();
	viewRecord(formFillForEditId);
	$('#idForUpdate').val(tempVal);
}

function prevRecord(){
	disableUpdate();
	if(recNumber > 0){
		recNumber = recNumber - 1;
		AssignFormControlsMain(serviceResponse.bookingMasterList[recNumber]);
		$("#recPos").val((recNumber + 1)+"/"+ maxRecordMain);
	}
	changeButtonsAttribute();
	form_data = new FormData();
	bookingDetailsModelArray = new Array();
	isValidationError = false;
	$('#updateButton').attr("onclick","updateBooking('O','')");
	$('#summuryTable tbody tr td').find(':input,select').prop('disabled',false);
	isServerSideError = false;
}
function nextRecord(){
	disableUpdate();
	if(recNumber < (maxRecordMain - 1)){
		recNumber = recNumber + 1;
		AssignFormControlsMain(serviceResponse.bookingMasterList[recNumber]);
		$("#recPos").val((recNumber + 1)+"/"+ maxRecordMain);
	}
	changeButtonsAttribute();
	form_data = new FormData();
	bookingDetailsModelArray = new Array();
	isValidationError = false;
	$('#updateButton').attr("onclick","updateBooking('O','')");
	$('#summuryTable tbody tr td').find(':input,select').prop('disabled',false);
	isServerSideError = false;
}
function disableUpdate(){
	document.getElementById("addButton").disabled = false;
	document.getElementById("updateButton").disabled = true;
	$('#idForUpdate').val('');
	$('#idForUpdateDetail').val('');
	$('#error').hide();
	$('#info').hide();
}

function validateBookingDetails(actionType,tempIndex){	    	    
	isValidationError = false;
	var validationErrorHtml = '<ol>';
	if(actionType != 'S'){
		if(actionType == 'U' && tempIndex ==''){
			var bookingNo = $('#bookingNo').val();
			if(bookingNo == null || bookingNo == ""){
				validationErrorHtml = validationErrorHtml + "<li> Booking Number can not be Blank.</li>";
				isValidationError = true;
			}
		}
		var bookingDate = $('#bookingDate').val();
		if(bookingDate == null || bookingDate == ""){
			validationErrorHtml = validationErrorHtml + "<li> Booking Date can not be Empty.</li>";
			isValidationError = true;
		}
		var corporateId = $('#corporateId').val();
		if(corporateId == null || corporateId == "" || corporateId == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Corporate Name can not be Blank.</li>";
			isValidationError = true;
		}
		var bookedBy = $('#bookedBy').val();
		if(bookedBy == null || bookedBy == "" || bookedBy == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Booked By can not be Blank.</li>";
			isValidationError = true;
		}
		var bookedFor = $("#opClient").prop('checked')==true?"C":"O";
		if(bookedFor == null || bookedFor == "" || bookedFor == undefined){
			validationErrorHtml = validationErrorHtml + "<li> Booked For can not be Blank.</li>";
			isValidationError = true;
		}
		var bookedForName = $("#opClient").prop('checked')==true?$("#clientName option:selected").text():$("#otherClient").val();
		if(bookedForName == null || bookedForName == "" || bookedForName == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Booked For Name can not be Blank.</li>";
			isValidationError = true;
		}
		var mobileNo = $('#mobileNo').val();
		if(mobileNo == null || mobileNo == ""){
			validationErrorHtml = validationErrorHtml + "<li> Mobile Number can not be Blank.</li>";
			isValidationError = true;
		}
		var pattern = /^\d+$/;		    
		if(mobileNo != null && mobileNo != "" && !pattern.test(mobileNo)){
			validationErrorHtml = validationErrorHtml + "<li> Mobile Number can only contain numbers.</li>";
			isValidationError = true;
		}		    
		if(mobileNo != null && mobileNo != "" && pattern.test(mobileNo) && mobileNo.length != 10){
			validationErrorHtml = validationErrorHtml + "<li> Mobile Number should be of 10 digit.</li>";
			isValidationError = true;
		}
		/*var referenceNo = $('#referenceNo').val();
		    if(referenceNo == null || referenceNo == ""){
			validationErrorHtml = validationErrorHtml + "<li> Reference Number can not be Blank.</li>";
			isValidationError = true;
		    }*/
		var bookingTakenById = loginUserId;
		if(bookingTakenById == null || bookingTakenById == "" || bookingTakenById == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Booking Taken By Id can not be Blank.</li>";
			isValidationError = true;
		}
		/*var smsToClient = $('#smsToClient').is(':checked')?"Y":"N";
		    if(smsToClient == null || smsToClient == "" || smsToClient == undefined){
			validationErrorHtml = validationErrorHtml + "<li> SMS to client can not be Blank.</li>";
			isValidationError = true;
		    }
		    var smsToBooker = $('#smsToBooker').is(':checked')?"Y":"N";
		    if(smsToBooker == null || smsToBooker == "" || smsToBooker == undefined){
			validationErrorHtml = validationErrorHtml + "<li> SMS to booker can not be Blank.</li>";
			isValidationError = true;
		    }
		    var smsToOther = $('#smsToOther').val();
		    if(smsToOther == null || smsToOther == "" || smsToOther == undefined){
			validationErrorHtml = validationErrorHtml + "<li> SMS to other can not be Blank.</li>";
			isValidationError = true;
		    }
		    var emailToClient = $('#emailToClient').is(':checked')?"Y":"N";
		    if(emailToClient == null || emailToClient == "" || emailToClient == undefined){
			validationErrorHtml = validationErrorHtml + "<li> Email to client can not be Blank.</li>";
			isValidationError = true;
		    }
		    var emailToBooker = $('#emailToBooker').is(':checked')?"Y":"N";
		    if(emailToBooker == null || emailToBooker == "" || emailToBooker == undefined){
			validationErrorHtml = validationErrorHtml + "<li> Email to booker can not be Blank.</li>";
			isValidationError = true;
		    }
		    var emailToOther = $('#emailToOther').val();
		    if(emailToOther == null || emailToOther == "" || emailToOther == undefined){
			validationErrorHtml = validationErrorHtml + "<li> Email to other can not be Blank.</li>";
			isValidationError = true;
		    }*/
	}	    
	if(actionType == 'S'){
		var sCriteria = $('#searchName').val();
		if(sCriteria == null || sCriteria == "" || sCriteria == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Search Name can not be Blank.</li>";
			isValidationError = true;
		}
		var sValue = $('#searchValue').val();
		if(sValue == null || sValue == ""){
			validationErrorHtml = validationErrorHtml + "<li> Search Value can not be Blank.</li>";
			isValidationError = true;
		}
	}
	if(actionType != 'S'){
		var branchName = $('#branchName').val();
		if(branchName == null || branchName == "" || branchName == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Branch Name can not be Blank.</li>";
			isValidationError = true;
		}
		var hubName = $('#hubName').val();
		if(hubName == null || hubName == "" || hubName == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Hub Name can not be Blank.</li>";
			isValidationError = true;
		}
		var pickUpDate = $('#bookingDetailsDate').val();
		if(pickUpDate == null || pickUpDate == ""){
			validationErrorHtml = validationErrorHtml + "<li> Booking Date can not be Blank.</li>";
			isValidationError = true;
		
		}

		var startTime = $('#startTime').val();
		if(startTime == null || startTime == ""){
			validationErrorHtml = validationErrorHtml + "<li> Start Time can not be Blank.</li>";
			isValidationError = true;
		}
		var pickUpTime = $('#pickUpTime').val();
		if(pickUpTime == null || pickUpTime == ""){
			validationErrorHtml = validationErrorHtml + "<li> Pick Up Time can not be Blank.</li>";
			isValidationError = true;
		}
		if(startTime!=""){
		    if(!checkStart()){
			validationErrorHtml = validationErrorHtml + "<li> Start Time can not be less than current Date and Time.</li>";
			isValidationError = true;
		    }
		}
		if(pickUpTime!=""){
		    if(!checkPickUp()){
			validationErrorHtml = validationErrorHtml + "<li> Pick Up Time should be atleast 1 hour after Start Time.</li>";
			isValidationError = true;
		    }
		}
		/* var flightNo = $('#flightNo').val();
		    if(flightNo == null || flightNo == ""){
			validationErrorHtml = validationErrorHtml + "<li> Flight Number can not be Blank.</li>";
			isValidationError = true;
		    }*/
		var mob = $('#MOB').val();
		if(mob == null || mob == "" || mob == "0"){
			validationErrorHtml = validationErrorHtml + "<li> MOB can not be Blank.</li>";
			isValidationError = true;
		}		    
		var carModel = $('#carModel').val();
		if(carModel == null || carModel == "" || carModel == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Car Model can not be Blank.</li>";
			isValidationError = true;
		}
		var rentalType = $('#rentalType').val();
		if(rentalType == null || rentalType == "" || rentalType == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Rental Type can not be Blank.</li>";
			isValidationError = true;
		}
		var tariff = $('#tariff').val();
		if(tariff == null || tariff == "" || tariff == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Tariff can not be Blank.</li>";
			isValidationError = true;
		}
		var reportAt = $("#opOffice").prop('checked')==true?"F":"O";
		if(reportAt == null || reportAt == "" || reportAt == undefined){
			validationErrorHtml = validationErrorHtml + "<li> Report At can not be Blank.</li>";
			isValidationError = true;
		}
		var reportingAddress = $("#opOffice").prop('checked')==true?$("#officeAddress option:selected").text():$("#reportingAddress").val();
		if(reportingAddress == null || reportingAddress == "" || reportingAddress == undefined){
			validationErrorHtml = validationErrorHtml + "<li> Reporting Address not be Blank.</li>";
			isValidationError = true;
		}
		var toBeReleaseAt = $('#toBeReleaseAt').val();
		if(toBeReleaseAt == null || toBeReleaseAt == "" || toBeReleaseAt == "0"){
			validationErrorHtml = validationErrorHtml + "<li> To be Release At can not be Blank.</li>";
			isValidationError = true;
		}
		/*var instruction = $('#instruction').val();
		    if(instruction == null || instruction == "" || instruction == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Instruction can not be Blank.</li>";
			isValidationError = true;
		    }*/
		if(checkIsPassengerStatus=='Y'){
			var nValidateStatus=false;
			var mValidateStatus=false;
			var aValidateStatus=false;
			var aLimitValidationStatus=false
			$(".mob").each(function(){
				 var id = (this.id).split("_"); 
				 if($("#passengerName_"+id[1]).val()=="") nValidateStatus=true;
				 if($("#passengerMob_"+id[1]).val()=="")    mValidateStatus=true;
				 if($("#passengerAge_"+id[1]).val()=="")   aValidateStatus=true;
				 else if((parseInt($("#passengerAge_"+id[1]).val())<1 )||( parseInt($("#passengerAge_"+id[1]).val())>120)){
					aLimitValidationStatus=true
				 }
			 });
			if(nValidateStatus){
				validationErrorHtml = validationErrorHtml + "<li>Passenger Name can not be blank</li>";
				isValidationError = true;
			  }if(mValidateStatus){
				validationErrorHtml = validationErrorHtml + "<li>Passenger MobileNo can not be blank</li>";
				isValidationError = true;
			  }if(aValidateStatus){
			     validationErrorHtml = validationErrorHtml + "<li>Passenger Age can not be blank</li>";
				 isValidationError = true;
			  }if(aLimitValidationStatus){
				     validationErrorHtml = validationErrorHtml + "<li>Passenger Age Should not be less then 1 and Greater than 120</li>";
					 isValidationError = true;
			 }  
		}
	}
	
	validationErrorHtml = validationErrorHtml + "</ol>";
	if(isValidationError){
		$('#error').html("Please correct following errors:<br>" + validationErrorHtml);
		$('#info').hide();
		$('#error').show();
	}else{
		$('#error').html("");
		$('#error').hide();
	}
}

function createBookingDataRecord(){	    
	var dataString = "";
	for(;fromIndex < toIndex; fromIndex++){
		dataString = dataString + " <tr class='custom_"+fromIndex+"'> " 
		+ " <td>" 
		+ " 	<a href='javascript:formFillEditTemporaryRecord("+ fromIndex + ")'><img src='./img/editButton.png' border='0' title='Edit' width='20' height='20'/></a> "
		+ "     <a href='javascript:deleteTemporaryRecord("+ fromIndex + ")' ><img src='./img/deleteButton.png' border='0' title='Delete / Cancel' width='20' height='20'/></a> " 
		+ " </td> 														"
		+ " <td style='word-wrap:break-word'>" + form_data.get("bookingDetailModel["+fromIndex+"].pickUpDate") + "</td>  		"			
		+ " <td style='word-wrap:break-word'>" + $("#branchName option[value='"+form_data.get("bookingDetailModel["+fromIndex+"].branch.id")+"']").text() + "</td>  		"
		+ " <td style='word-wrap:break-word'>" + $("#hubName option[value='"+form_data.get("bookingDetailModel["+fromIndex+"].outlet.id")+"']").text() + "</td>  		"
		+ " <td style='word-wrap:break-word'>" + $("#rentalType option[value='"+form_data.get("bookingDetailModel["+fromIndex+"].rentalType.id")+"']").text() + "</td>  		"
		+ " <td style='word-wrap:break-word'>" + $("#tariff option[value='"+form_data.get("bookingDetailModel["+fromIndex+"].tariff.id")+"']").text() + "</td>  		"
		+ " <td style='word-wrap:break-word'>" + $("#carModel option[value='"+form_data.get("bookingDetailModel["+fromIndex+"].carModel.id")+"']").text() + "</td>  		"
		+ " <td style='word-wrap:break-word'>" + form_data.get("bookingDetailModel["+fromIndex+"].flightNo") + "</td>  		"
		+ " <td style='word-wrap:break-word'>" + form_data.get("bookingDetailModel["+fromIndex+"].startTime") + "</td>  		"
		+ " <td style='word-wrap:break-word'>" + form_data.get("bookingDetailModel["+fromIndex+"].pickUpTime") + "</td>  		"
		+ " <td style='word-wrap:break-word'>" + form_data.get("bookingDetailModel["+fromIndex+"].reportingAddress") + "</td>  		"
		+ " <td style='word-wrap:break-word'>" + form_data.get("bookingDetailModel["+fromIndex+"].toBeRealeseAt") + "</td>  		"
		+ " <td style='word-wrap:break-word'>" + form_data.get("bookingDetailModel["+fromIndex+"].instruction") + "</td>  		"
		+ " </tr> 		";				
	}
	refreshData();	
	if(userTable.destroy){
		userTable.destroy();
	}else{
		userTable.fnDestroy();
	}
	$('#dataTable2 tbody').append(dataString);		
	refreshData();	
}

function changeButtonsAttribute(){
	$('#saveAsNewButton').parent().parent().find('td:eq( 1 )').attr('width','40%').end();
	$('#saveAsNewButton').parent().parent().find('td:last').attr('width','40%').end();
	$('#saveAsNewButton').show();
	$('#saveButton').prop('disabled',true);
}

function removeFormDataBasedOnIndex(index){
    form_data.set("bookingDetailModel["+index+"].company.id",$('#companyName').val());
	form_data.set("bookingDetailModel["+index+"].branch.id",$('#branchName').val());
	form_data.set("bookingDetailModel["+index+"].outlet.id",$('#hubName').val());
	form_data.set("bookingDetailModel["+index+"].pickUpDate", $('#bookingDetailsDate').val());    	    
	form_data.set("bookingDetailModel["+index+"].startTime", $('#startTime').val());
	form_data.set("bookingDetailModel["+index+"].pickUpTime", $('#pickUpTime').val());
	form_data.set("bookingDetailModel["+index+"].flightNo", $('#flightNo').val());
	if($('#terminal').val()!="0") form_data.set("bookingDetailModel["+index+"].terminal.id", $('#terminal').val());
	form_data.set("bookingDetailModel["+index+"].mob.id", $('#MOB').val());
	form_data.set("bookingDetailModel["+index+"].carModel.id", $('#carModel').val());
	form_data.set("bookingDetailModel["+index+"].rentalType.id", $('#rentalType').val());
	form_data.set("bookingDetailModel["+index+"].tariff.id", $('#tariff').val());
	form_data.set("bookingDetailModel["+index+"].reportAt", $("#opOffice").prop('checked')==true?"F":"O");
	form_data.set("bookingDetailModel["+index+"].reportingAddress", $("#opOffice").prop('checked')==true?$("#officeAddress option:selected").text():$("#reportingAddress").val());
	form_data.set("bookingDetailModel["+index+"].toBeRealeseAt", $('#toBeReleaseAt').val());
	form_data.set("bookingDetailModel["+index+"].instruction", $('#instruction').val());
}

function disableMasterFields(){
	$('#summuryTable tbody tr td').find(':input,select').prop('disabled',true);		
}

function resetSummuryData(){
	$('#bookingNo').val('');
	$('#bookingDate').val(new Date());
	$('#corporateId').val('0');
	$('#bookedBy').val('0');
	$("#opClient").prop('checked',true);
	$("#otherClient").hide();
	$("#clientName").show();
	$('#mobileNo').val('');
	$('#referenceNo').val('');    	    
	$('#smsToClient').prop('checked',true);
	$('#smsToBooker').prop('checked',false);
	$('#smsToOther').val('');
	$('#emailToClient').prop('checked',false);
	$('#emailToBooker').prop('checked',true);
	$('#emailToOther').val('');
	$('#searchName').val('M');
	$('#searchValue').val('');
	$('#bookedFor').val('');
}

function checkDuplicateEntry(){
	isDuplicate = false;
	for(var i = 0; i<bookingDetailsModelArray.length;i++){
		isDuplicate =true;
		if(form_data.get("bookingDetailModel["+i+"].branch.id") != $('#branchName').val()){
			console.log("branchIn");
			isDuplicate = false;
		}
		if(form_data.get("bookingDetailModel["+i+"].outlet.id") != $('#hubName').val()){
			console.log("hubIn");
			isDuplicate = false;
		}
		if(form_data.get("bookingDetailModel["+i+"].pickUpDate") != $('#bookingDetailsDate').val()){
			console.log("fromdateIn");
			isDuplicate = false;
		}		
		if(form_data.get("bookingDetailModel["+i+"].startTime") != $('#startTime').val()){
			console.log("startTimein");
			isDuplicate = false;
		}
		if(form_data.get("bookingDetailModel["+i+"].pickUpTime") != $('#pickUpTime').val()){
			console.log("pickuptimeiN");
			isDuplicate = false;
		}
		if(form_data.get("bookingDetailModel["+i+"].terminal.id") != $('#terminal').val()){
			console.log("terminalIn");
			isDuplicate = false;
		}
		if(form_data.get("bookingDetailModel["+i+"].mob.id") != $('#MOB').val()){
			console.log("mobIn");
			isDuplicate = false;
		}		
		if(form_data.get("bookingDetailModel["+i+"].carModel.id") != $('#carModel').val()){
			console.log("carModelIn");
			isDuplicate = false;
		}
		if(form_data.get("bookingDetailModel["+i+"].rentalType.id") != $('#rentalType').val()){
			console.log("rentaltypein");
			isDuplicate = false;
		}
		if(form_data.get("bookingDetailModel["+i+"].tariff.id") != $('#tariff').val()){
			console.log("tariffIn");
			isDuplicate = false;
		}
		if(form_data.get("bookingDetailModel["+i+"].reportAt") != $("#opOffice").prop('checked')==true?"F":"O"){
			console.log("opOfficeIn");
			console.log("opOfficeIn values:");
			console.log("reportAt:"+form_data.get("bookingDetailModel["+i+"].reportAt"));
			console.log("opOffice:"+$("#opOffice").prop('checked')==true?"F":"O");
			isDuplicate = false;
		}
		if(form_data.get("bookingDetailModel["+i+"].reportingAddress") != $("#opOffice").prop('checked')==true?$("#officeAddress option:selected").text():$("#reportingAddress").val()){
			console.log("repoatingaddresssIn");
			console.log("reportingAddress values:");
			console.log("reportingAddress:"+form_data.get("bookingDetailModel["+i+"].reportingAddress"));
			console.log("officeAddress:"+$("#opOffice").prop('checked')==true?$("#officeAddress option:selected").text():$("#reportingAddress").val());
			isDuplicate = false;
		}
		if(form_data.get("bookingDetailModel["+i+"].toBeRealeseAt") != $('#toBeReleaseAt').val()){
			console.log("toBeReelaseAtIn");
			isDuplicate = false;
		}
		if(form_data.get("bookingDetailModel["+i+"].instruction") != $('#instruction').val()){
			console.log("instructionIn");
			isDuplicate = false;
		}
		if(isDuplicate == true){
			return;
		}
	}	        	       	        	      	       	    
}
function checkAllBlankDetailFields(){
	isBlank = true;
	if($('#branchName').val() != "0" && $('#branchName').val() != ""){
		isBlank = false;
	}
	if($('#startTime').val() != ""){
		isBlank = false;
	}
	if($('#pickUpTime').val() != ""){
		isBlank = false;
	}
	if($('#terminal').val() != "0" && $('#terminal').val() != ""){
		isBlank = false;
	}
	if($('#MOB').val() != "158"){
		isBlank = false;
	}	    
	if($('#carModel').val() != "0" && $('#carModel').val() != ""){
		isBlank = false;
	}
	if($('#rentalType').val() != "0" && $('#rentalType').val() != ""){
		isBlank = false;
	}
	if($('#tariff').val() != "0" && $('#tariff').val() != ""){
		isBlank = false;
	}	
	if($("#opOffice").prop('checked')==true && ($("#officeAddress").val() != "0" && $("#officeAddress").val() != "")){
		isBlank = false;
	}
	if($("#opOffice").prop('checked')==false && $("#reportingAddress").val() !=""){
		isBlank = false;
	}
	if($('#toBeReleaseAt').val() != ""){
		isBlank = false;
	}
	if($('#instruction').val() != ""){
		isBlank = false;
	}
	
}

function formFillEditTemporaryRecord(index){
    $('#companyName').val(form_data.get("bookingDetailModel["+index+"].company.id"));
	fillBranch(form_data.get("bookingDetailModel["+index+"].company.id"));
	$('#branchName').val(form_data.get("bookingDetailModel["+index+"].branch.id"));
	fillHub(form_data.get("bookingDetailModel["+index+"].branch.id"));
	$('#hubName').val(form_data.get("bookingDetailModel["+index+"].outlet.id"));
	$('#bookingDetailsDate').val(form_data.get("bookingDetailModel["+index+"].pickUpDate"));
	$('#startTime').val(form_data.get("bookingDetailModel["+index+"].startTime"));
	$('#pickUpTime').val(form_data.get("bookingDetailModel["+index+"].pickUpTime"));
	$('#flightNo').val(form_data.get("bookingDetailModel["+index+"].flightNo"));
	$('#terminal').val(form_data.get("bookingDetailModel["+index+"].terminal.id"));
	$('#MOB').val(form_data.get("bookingDetailModel["+index+"].mob.id"));    	    

	$('#carModel').val(form_data.get("bookingDetailModel["+index+"].carModel.id"));
	$('#rentalType').val(form_data.get("bookingDetailModel["+index+"].rentalType.id")); 
	
	$('#tariff').val(form_data.get("bookingDetailModel["+index+"].tariff.id"));
	form_data.get("bookingDetailModel["+index+"].reportAt")=="F"?$('#opOffice').prop('checked', true):$('#opReport').prop('checked', true);
	form_data.get("bookingDetailModel["+index+"].reportAt")=="F"?$("#officeAddress").val($('select[name="officeAddress"] > option:contains("'+form_data.get("bookingDetailModel["+index+"].reportingAddress")+'")').val()):$("#reportingAddress").val(form_data.get("bookingDetailModel["+index+"].reportingAddress"));
	if($("#opOffice").prop('checked')){
		$('#officeAddress').show();
		$('#reportingAddress').val("");
		$('#reportingAddress').hide();
	}else{
		$('#officeAddress').val("0");
		$('#officeAddress').hide();
		$('#reportingAddress').show()
	}
	$('#toBeReleaseAt').val(form_data.get("bookingDetailModel["+index+"].toBeRealeseAt"));
	$('#instruction').val(form_data.get("bookingDetailModel["+index+"].instruction"));
	$('#updateButton').attr("onclick","updateBooking('N','"+index+"')");
	$('#saveButton').prop('disabled',true);
	$('#updateButton').prop('disabled',false);
	$("#addButton").prop('disabled',true);
}

function updateDataTable(tIndex){
	var dataString = "";		
	dataString = dataString + "" 
	+ " <td>" 
	+ " 	<a href='javascript:formFillEditTemporaryRecord("+tIndex + ")'><img src='./img/editButton.png' border='0' title='Edit' width='20' height='20'/></a> "
	+ "     <a href='javascript:deleteTemporaryRecord("+ tIndex + ")' ><img src='./img/deleteButton.png' border='0' title='Delete / Cancel' width='20' height='20'/></a> " 
	+ " </td> 														"
	+ " <td style='word-wrap:break-word'>" + form_data.get("bookingDetailModel["+tIndex+"].pickUpDate") + "</td>  		"
	+ " <td style='word-wrap:break-word'>" + $("#branchName option[value='"+form_data.get("bookingDetailModel["+tIndex+"].branch.id")+"']").text() + "</td>  		"
	+ " <td style='word-wrap:break-word'>" + $("#hubName option[value='"+form_data.get("bookingDetailModel["+tIndex+"].outlet.id")+"']").text() + "</td>  		"
	+ " <td style='word-wrap:break-word'>" + $("#rentalType option[value='"+form_data.get("bookingDetailModel["+tIndex+"].rentalType.id")+"']").text() + "</td>  		"
	+ " <td style='word-wrap:break-word'>" + $("#tariff option[value='"+form_data.get("bookingDetailModel["+tIndex+"].tariff.id")+"']").text() + "</td>  		"
	+ " <td style='word-wrap:break-word'>" + $("#carModel option[value='"+form_data.get("bookingDetailModel["+tIndex+"].carModel.id")+"']").text() + "</td>  		"
	+ " <td style='word-wrap:break-word'>" + form_data.get("bookingDetailModel["+tIndex+"].flightNo") + "</td>  		"
	+ " <td style='word-wrap:break-word'>" + form_data.get("bookingDetailModel["+tIndex+"].startTime") + "</td>  		"
	+ " <td style='word-wrap:break-word'>" + form_data.get("bookingDetailModel["+tIndex+"].pickUpTime") + "</td>  		"
	+ " <td style='word-wrap:break-word'>" + form_data.get("bookingDetailModel["+tIndex+"].reportingAddress") + "</td>  		"
	+ " <td style='word-wrap:break-word'>" + form_data.get("bookingDetailModel["+tIndex+"].toBeRealeseAt") + "</td>  		"
	+ " <td style='word-wrap:break-word'>" + form_data.get("bookingDetailModel["+tIndex+"].instruction") + "</td>  		"
	+ "";						
	if(userTable.destroy){
		userTable.destroy();
	}else{
		userTable.fnDestroy();
	}
	$('#dataTable2 tbody tr.custom_'+tIndex).html(dataString);		
	refreshData();	
}

function deleteTemporaryRecord(dIndex){
	if(isAccessDenied("D")) return;	
	if (confirm('Are you sure you want to delete this Record?')) {
		if(userTable.destroy){
			userTable.destroy();
		}else{
			userTable.fnDestroy();
		}
		$('#dataTable2 tbody tr.custom_'+dIndex).remove();
		if(bookingDetailsModelArray.length>dIndex+1){
			for(var i = dIndex+1; i<bookingDetailsModelArray.length;i++){        		    
				console.log($('#dataTable2 tbody tr.custom_'+i).addClass('custom_'+(i-1)+''));
				$('#dataTable2 tbody tr.custom_'+i).addClass('custom_'+(i-1)+'');
				$('#dataTable2 tbody tr.custom_'+i).removeClass('custom_'+i);
				$('#dataTable2 tbody tr.custom_'+(i-1)+' td:first a:first' ).attr("href","javascript:formFillEditTemporaryRecord("+(i-1) +")");
				$('#dataTable2 tbody tr.custom_'+(i-1)+' td:first a:last' ).attr("href","javascript:deleteTemporaryRecord("+(i-1) +")");
			}
		}
		refreshData();
		reIndexFormData(dIndex);
		$('#info').html('Entry Successfully Deleted');
		$('#error').hide();
		$('#info').show();
	}
}

function reIndexFormData(rIndex){
	var oldFormData = form_data;
	console.log(oldFormData.get("bookingDate"));
	form_data = new FormData();
	form_data.set("bookingNo", oldFormData.get("bookingNo"));
	form_data.set("bookingDate", oldFormData.get("bookingDate"));
	form_data.set("corporateId.id", oldFormData.get("corporateId.id"));
	form_data.set("bookedBy.id", oldFormData.get("bookedBy.id"));
	form_data.set("bookedFor", oldFormData.get("bookedFor"));
	form_data.set("bookedForName", oldFormData.get("bookedForName"));
	form_data.set("mobileNo", oldFormData.get("mobileNo"));
	form_data.set("referenceNo", oldFormData.get("referenceNo"));
	form_data.set("bookingTakenBy.id", oldFormData.get("bookingTakenBy.id"));
	form_data.set("smsToClient", oldFormData.get("smsToClient"));
	form_data.set("smsToBooker", oldFormData.get("smsToBooker"));
	form_data.set("smsToOther", oldFormData.get("smsToOther"));
	form_data.set("emailToClient", oldFormData.get("emailToClient"));
	form_data.set("emailToBooker", oldFormData.get("emailToBooker"));
	form_data.set("emailToOther", oldFormData.get("emailToOther"));
	form_data.set("sCriteria", oldFormData.get("sCriteria"));
	form_data.set("sValue", oldFormData.get("sValue"));	    	

    form_data.set("companyName", oldFormData.get("companyName"));
	form_data.set("cityName", oldFormData.get("cityName"));
	for(var i = 0; i<rIndex;i++){
		form_data.set("bookingDetailModel["+i+"].company.id",oldFormData.get("bookingDetailModel["+i+"].company.id"));
		form_data.set("bookingDetailModel["+i+"].branch.id",oldFormData.get("bookingDetailModel["+i+"].branch.id"));
		form_data.set("bookingDetailModel["+i+"].outlet.id",oldFormData.get("bookingDetailModel["+i+"].outlet.id"));
		form_data.set("bookingDetailModel["+i+"].pickUpDate", oldFormData.get("bookingDetailModel["+i+"].pickUpDate"));    	    
		form_data.set("bookingDetailModel["+i+"].startTime", oldFormData.get("bookingDetailModel["+i+"].startTime"));
		form_data.set("bookingDetailModel["+i+"].pickUpTime", oldFormData.get("bookingDetailModel["+i+"].pickUpTime"));
		form_data.set("bookingDetailModel["+i+"].flightNo", oldFormData.get("bookingDetailModel["+i+"].flightNo"));
		form_data.set("bookingDetailModel["+i+"].terminal.id", oldFormData.get("bookingDetailModel["+i+"].terminal.id"));
		form_data.set("bookingDetailModel["+i+"].mob.id", oldFormData.get("bookingDetailModel["+i+"].mob.id"));

		form_data.set("bookingDetailModel["+i+"].carModel.id", oldFormData.get("bookingDetailModel["+i+"].carModel.id"));
		form_data.set("bookingDetailModel["+i+"].rentalType.id", oldFormData.get("bookingDetailModel["+i+"].rentalType.id"));
		form_data.set("bookingDetailModel["+i+"].tariff.id", oldFormData.get("bookingDetailModel["+i+"].tariff.id"));
		form_data.set("bookingDetailModel["+i+"].reportAt", oldFormData.get("bookingDetailModel["+i+"].reportAt"));
		form_data.set("bookingDetailModel["+i+"].reportingAddress", oldFormData.get("bookingDetailModel["+i+"].reportingAddress"));
		form_data.set("bookingDetailModel["+i+"].toBeRealeseAt", oldFormData.get("bookingDetailModel["+i+"].toBeRealeseAt"));
		form_data.set("bookingDetailModel["+i+"].instruction", oldFormData.get("bookingDetailModel["+i+"].instruction"));
	}
	if(bookingDetailsModelArray.length>rIndex+1){
		for(var i = rIndex+1; i<bookingDetailsModelArray.length;i++){
		    form_data.set("bookingDetailModel["+(i-1)+"].company.id",oldFormData.get("bookingDetailModel["+i+"].company.id"));
			form_data.set("bookingDetailModel["+(i-1)+"].branch.id",oldFormData.get("bookingDetailModel["+i+"].branch.id"));
			form_data.set("bookingDetailModel["+(i-1)+"].outlet.id",oldFormData.get("bookingDetailModel["+i+"].outlet.id"));
			form_data.set("bookingDetailModel["+(i-1)+"].pickUpDate", oldFormData.get("bookingDetailModel["+i+"].pickUpDate"));    	    
			form_data.set("bookingDetailModel["+(i-1)+"].startTime", oldFormData.get("bookingDetailModel["+i+"].startTime"));
			form_data.set("bookingDetailModel["+(i-1)+"].pickUpTime", oldFormData.get("bookingDetailModel["+i+"].pickUpTime"));
			form_data.set("bookingDetailModel["+(i-1)+"].flightNo", oldFormData.get("bookingDetailModel["+i+"].flightNo"));
			form_data.set("bookingDetailModel["+(i-1)+"].terminal.id", oldFormData.get("bookingDetailModel["+i+"].terminal.id"));
			form_data.set("bookingDetailModel["+(i-1)+"].mob.id", oldFormData.get("bookingDetailModel["+i+"].mob.id"));

			form_data.set("bookingDetailModel["+(i-1)+"].carModel.id", oldFormData.get("bookingDetailModel["+i+"].carModel.id"));
			form_data.set("bookingDetailModel["+(i-1)+"].rentalType.id", oldFormData.get("bookingDetailModel["+i+"].rentalType.id"));
			form_data.set("bookingDetailModel["+(i-1)+"].tariff.id", oldFormData.get("bookingDetailModel["+i+"].tariff.id"));
			form_data.set("bookingDetailModel["+(i-1)+"].reportAt", oldFormData.get("bookingDetailModel["+i+"].reportAt"));
			form_data.set("bookingDetailModel["+(i-1)+"].reportingAddress", oldFormData.get("bookingDetailModel["+i+"].reportingAddress"));
			form_data.set("bookingDetailModel["+(i-1)+"].toBeRealeseAt", oldFormData.get("bookingDetailModel["+i+"].toBeRealeseAt"));
			form_data.set("bookingDetailModel["+(i-1)+"].instruction", oldFormData.get("bookingDetailModel["+i+"].instruction"));
		}
	}
	bookingDetailsModelArray.splice(rIndex, 1);
}


	$(function() {
		$("input[name=multipleOption]").change(function(){
			if($("#opDateRange").prop("checked")){
				$("#NoOfCars").val("");
				$("#DateRangeTo").val($("#bookingDetailsDate").val());
				document.getElementById("DateRangeTo").disabled = false;
				document.getElementById("NoOfCars").disabled = true;
			}else {
				$("#DateRangeTo").val("");
				document.getElementById("DateRangeTo").disabled = true;
				document.getElementById("NoOfCars").disabled = false;
			}
		});
	});

/*function enableDisableOption(){
	if($("#opDateRange").prop("checked")){
		$("#NoOfCars").val("");
		$("#DateRangeTo").val($("#bookingDetailsDate").val());
		document.getElementById("DateRangeTo").disabled = false;
		document.getElementById("NoOfCars").disabled = true;
	}else {
		$("#DateRangeTo").val("");
		document.getElementById("DateRangeTo").disabled = true;
		document.getElementById("NoOfCars").disabled = false;
	}
}*/
function showPassengerInfoPanel(){
    $('#passengerInfoPanel').modal('show');
}

function addNewRow(){
	i++;  
	$("#passengerTable").append("<tr id='row_"+i+"'>" +
		"		<td><input type='button' id='btnMin_"+i+"'  class='btn' style='background : #FF9300' value='-' onclick='removeRow(this.id)'/></td>"+
		"     	<td><input type='text' class='form-control' id='passengerName_"+i+"'/></td>" +
		"	  	<td><input type='text' class='form-control mob' id='passengerMob_"+i+"' maxlength='13' onkeypress='isNumeric(event)'/></td>" +
		"     	<td><input type='text' class='form-control' id='passengerAge_"+i+"' onkeypress='isNumeric(event)'/></td>" +
		"     	<td>" +
		"				<select class='form-control' id='passengerSex_"+i+"' name='passengerSex' >" +
	    "					<option value='Male'>Male</option>"+
	    "				    <option value='Female'>Female</option>" +
	    "				    <option value='Other'>Other</option>" +
		"				</select>" +
		"		</td>" +
		"   	<td><input type='text' class='form-control' id='passengerId_"+i+"'/></td>" +
		"	</tr>");
	}

function removeRow(id){
	var btnId = id.split("_");
	$("#row_"+btnId[1]).remove();
}

function validatePassengerDetails(){
	var nValidateStatus=false;
	var mValidateStatus=false;
	var aValidateStatus=false;

	var validationErrorHtml = '<ol>';
	$(".mob").each(function(){
		 var id = (this.id).split("_"); 
		 if($("#passengerName_"+id[1]).val()=="") nValidateStatus=true;
		 if($("#passengerMob_"+id[1]).val()=="")    mValidateStatus=true;
		 if($("#passengerAge_"+id[1]).val()=="")   aValidateStatus=true;	
	 });
	if(nValidateStatus){
		validationErrorHtml = validationErrorHtml + "<li>Passenger Name can not be blank</li>";
		isValidationError = true;
	} if(mValidateStatus){
		validationErrorHtml = validationErrorHtml + "<li>Passenger MobileNo can not be blank</li>";
		isValidationError = true;
	} if(aValidateStatus){
	     validationErrorHtml = validationErrorHtml + "<li>Passenger Age can not be blank</li>";
		 isValidationError = true;
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
	