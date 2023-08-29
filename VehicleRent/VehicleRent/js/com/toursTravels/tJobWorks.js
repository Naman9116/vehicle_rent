var recNumber;
var maxRecordMain = 0;
var idForUpdate=0;
var clientMobile = new Array();
var allOptionValuesMap = new Object();
var editedFormDataValuesMap = new Object();
var userTable;
var isValidationError = false;
var carNO =null;
var cModelMap = new Object();
var vModelMap = new Object();
var chModelMap = new Object();
var tOut;
var dutySlipRelatedStyles = "";
var idForViewDutySlip="";
var stut = true;
var check = "";
var tariffList;
var allowPermission="";
var isValidateAllowPermission=true;
function refreshData(){
		if($('#userTable').dataTable() != null) $('#userTable').dataTable().fnDestroy();
	userTable = $('#userTable').DataTable({
				"aaSorting": [],
    			"bLengthChange": true,
    			"bFilter": true,
    			"bSort": true,
    			"bInfo": true,
    			"bAutoWidth": true,
    			"aLengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]]
	});
	if(jobType == 'Current'){
    		$("#userTable thead tr th").each( function ( i ) {
    			var tableContent = new Array();
    		    var selectBoxField = "";
    		    if(i==7 || i==14 || i==15 ){
    			if(i==7){
    			    selectBoxField = "corporateId";				
    			}else if(i==14){
    			    selectBoxField = "rentalType";
    			}else if(i==15){
    			    selectBoxField = "carModel";
    			}
    			$("#"+selectBoxField+" option").each(function(){
    			    allOptionValuesMap[$(this).text()] = $(this).val();    				  
    			});
    			$("#"+selectBoxField+"").empty().append('<option value="0">ALL</option>');
    			userTable.column(i).data().sort().each( function ( d, j ) {
    				var foundIndex = tableContent.indexOf((d.split('>')[1]).split('</')[0]);
    	 	    	if(foundIndex>-1)
    	 	    		tableContent.splice(foundIndex,1);
    	 	    	tableContent.push((d.split('>')[1]).split('</')[0]);
    	 	    });
    	 	    $.each(tableContent,function(index,val){
    			    $("#"+selectBoxField+"").append('<option value="'+allOptionValuesMap[val]+'">'+val+'</option>');
    		        } );
    		    }
    		 });
    		$("#bookingFromDate").val(getCurrentDate());
	}else if(jobType == 'Advance'){
    		$("#userTable thead tr th").each( function ( i ) {
    			var tableContent = new Array();
    		    var selectBoxField = "";
    		    if(i==7 || i==14 || i==15 || i==16){
    			if(i==7){
    			    selectBoxField = "corporateId";				
    			}else if(i==14){
    			    selectBoxField = "rentalType";
    			}else if(i==15){
    			    selectBoxField = "carModel";
    			}else if(i==16){
    			    selectBoxField = "jobStatus";
    			}
    			$("#"+selectBoxField+" option").each(function(){
    			    allOptionValuesMap[$(this).text()] = $(this).val();    				  
    			});
    			$("#"+selectBoxField+"").empty().append('<option value="0">ALL</option>');
    			userTable.column(i).data().sort().each( function ( d, j ) {
    				var foundIndex = tableContent.indexOf((d.split('>')[1]).split('</')[0]);
    	 	    	if(foundIndex>-1)
    	 	    		tableContent.splice(foundIndex,1);
    	 	    	tableContent.push((d.split('>')[1]).split('</')[0]);
    	 	    });
    	 	    $.each(tableContent,function(index,val){
    			    $("#"+selectBoxField+"").append('<option value="'+allOptionValuesMap[val]+'">'+val+'</option>');
    		        } );
    			
    		    }
    		 });
	}else if((jobType == 'Cancelled')|| (jobType == 'Expired')){    		
		$("#userTable thead tr th").each( function ( i ) {
		var tableContent = new Array();
		var selectBoxField = "";
	    if(i==5 || i==12 || i==13 || i==14){
		if(i==5){
		    selectBoxField = "corporateId";				
		}else if(i==12){
		    selectBoxField = "rentalType";
		}else if(i==13){
		    selectBoxField = "carModel";
		}else if(i==14){
		    selectBoxField = "jobStatus";
		}
		$("#"+selectBoxField+" option").each(function(){
		    allOptionValuesMap[$(this).text()] = $(this).val();    				  
		});
		$("#"+selectBoxField+"").empty().append('<option value="0">ALL</option>');
		userTable.column(i).data().sort().each( function ( d, j ) {
			var foundIndex = tableContent.indexOf((d.split('>')[1]).split('</')[0]);
 	    	if(foundIndex>-1)
 	    		tableContent.splice(foundIndex,1);
 	    	tableContent.push((d.split('>')[1]).split('</')[0]);
 	    });
 	    $.each(tableContent,function(index,val){
		    $("#"+selectBoxField+"").append('<option value="'+allOptionValuesMap[val]+'">'+val+'</option>');
	        } );
		
	    }
	 });
	}else if((jobType == 'Status')){
    		$("#userTable thead tr th").each( function ( i ) {
    		var tableContent = new Array();	
    		var selectBoxField = "";
		    if(i==6 || i==13 || i==14 || i==15 || i ==16){
			if(i==6){
			    selectBoxField = "corporateId";				
			}else if(i==13){
			    selectBoxField = "rentalType";
			}else if(i==14){
			    selectBoxField = "carModel";
			}else if(i==15){
			    selectBoxField = "jobStatus";
			}else if(i==16){
			    selectBoxField = "carRegNo";
			}
			$("#"+selectBoxField+" option").each(function(){
			    allOptionValuesMap[$(this).text()] = $(this).val();    				  
			});
			$("#"+selectBoxField+"").empty().append('<option value="0">ALL</option>');
			userTable.column(i).data().sort().each( function ( d, j ) {
				var foundIndex = tableContent.indexOf((d.split('>')[1]).split('</')[0]);
	 	    	if(foundIndex>-1)
	 	    		tableContent.splice(foundIndex,1);
	 	    	tableContent.push((d.split('>')[1]).split('</')[0]);
	 	    });
	 	    $.each(tableContent,function(index,val){
			    $("#"+selectBoxField+"").append('<option value="'+allOptionValuesMap[val]+'">'+val+'</option>');
		        } );
			
		    }
		 });
   }
}

 function resetData(){
	document.location.reload();
}

 function searchBooking(){
	if(jobType == 'Current'){
		var sCriteria = 'bookingFromDate,carModel.id,rentalType.id,branch.id ,outlet.id';
		var sValue = $("#bookingFromDate").val() + "," + 
				$("#carModel").val() + "," + 
				$("#rentalType").val()+","+
			        $("#branch").val()+","+
			        $("#outlet").val();
	} else if(jobType == 'Advance'){
		var sCriteria = 'bookingFromDate,bookingToDate,carModel.id,rentalType.id,branch.id,outlet.id,status';
		var sValue = $("#bookingFromDate").val() + "," +
				$("#bookingToDate").val() + "," + 
				$("#carModel").val() + "," + 
				$("#rentalType").val()+","+
			        $("#branch").val()+","+
			        $("#outlet").val()+","+
			        $("#jobStatus").val();
	} else if(jobType == 'Status'){
		var sCriteria = 'bookingFromDate,bookingToDate,carModel.id,rentalType.id,branch.id,outlet.id,status,regNo';
		var sValue = $("#bookingFromDate").val() + "," + 
				$("#bookingToDate").val() + "," +
				$("#carModel").val() + "," + 
				$("#rentalType").val()+","+
				$("#branch").val()+","+
				$("#outlet").val()+","+
			        $("#jobStatus").val()+","+
			        $("#carRegNo").val();
	} else if(jobType == 'Cancelled'){
		var sCriteria = 'bookingFromDate,bookingToDate,carModel.id,rentalType.id,branch.id,outlet.id,status';
		var sValue = $("#bookingFromDate").val() + "," +
				$("#bookingToDate").val() + "," +
				$("#carModel").val() + "," + 
				$("#rentalType").val()+","+
			        $("#branch").val()+","+
			        $("#outlet").val()+","+
			        $("#jobStatus").val(); 			 		
	} else if(jobType == 'Expired'){
	    var sCriteria = 'bookingFromDate,bookingToDate,carModel.id,rentalType.id,branch.id,outlet.id,status';
	    var sValue = $("#bookingFromDate").val() + "," +
				$("#bookingToDate").val() + "," +
				$("#carModel").val() + "," + 
				$("#rentalType").val()+","+
			        $("#branch").val()+","+
			        $("#outlet").val()+","+
			        $("#jobStatus").val(); 	
	}
	validateSearchFields();
	if(isValidationError)
	    return false;
	form_data = new FormData();
	form_data.append("jobType", jobType);
	form_data.append("sCriteria", sCriteria);
	form_data.append("sValue", sValue);
	$.ajax({
		type : "POST",
		url:  ctx + '/searchTBooking.html',
		async:false,
		processData: false,
		contentType: false,
		data : form_data,
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
	countRentalType();
	countJobStatus();
}	

 function validateSearchFields(){
    isValidationError = false;
    var validationErrorHtml = '<ol>';
    var bookingFromDate = $("#bookingFromDate").val();
    if(bookingFromDate == ""){
	validationErrorHtml = validationErrorHtml + "<li>Please Enter Pickup Date From!!</li>";
	isValidationError = true;
    }
    var bookingToDate = $("#bookingToDate").val();
	var thresholdDate = getTimeStampTo_DDMMYYYY(new Date());
    if(jobType != 'Current'){
		if( bookingToDate == ""){
		    validationErrorHtml = validationErrorHtml + "<li>Please Enter Pickup Date To!!</li>";
		    isValidationError = true;
		}
    }
    if(jobType == 'Advance') {
		if(daysBetween( $("#bookingFromDate").val(), thresholdDate) >= 0){
			validationErrorHtml = validationErrorHtml + "<li>Pickup Date From Should be Greater Than Current Date!!</li>";
			isValidationError = true;
		}
		if(daysBetween( $("#bookingToDate").val(), thresholdDate) > 0){
		    validationErrorHtml = validationErrorHtml + "<li>Pickup Date To Should be Greater Than Current Date!!</li>";
		    isValidationError = true;
		}
    }else if((jobType == 'Cancelled') || (jobType == 'Status')) {
		if(daysBetween( $("#bookingFromDate").val(), thresholdDate) < 0){
			validationErrorHtml = validationErrorHtml + "<li>Pickup Date From Should not be Greater Than Current Date!!</li>";
			isValidationError = true;
		}
		if(daysBetween( $("#bookingToDate").val(), thresholdDate) < 0){
		    validationErrorHtml = validationErrorHtml + "<li>Pickup Date To Should not be Greater Than Current Date!!</li>";
		    isValidationError = true;
		}
    }else if(jobType == 'Expired') {
		if(daysBetween( $("#bookingFromDate").val(), thresholdDate) < 0){
			validationErrorHtml = validationErrorHtml + "<li>Pickup Date From Should not be Greater Than Current Date!!</li>";
			isValidationError = true;
		}
		if(daysBetween( $("#bookingToDate").val(), thresholdDate) < 0){
		    validationErrorHtml = validationErrorHtml + "<li>Pickup Date To Should not be Greater Than Current Date!!</li>";
		    isValidationError = true;
		}
    }
	if(daysBetween($("#bookingToDate").val(),$("#bookingFromDate").val()) > 0) {
	    validationErrorHtml = validationErrorHtml + "<li>Pickup Date To Should Not be Less Than Pickup Date From!!</li>";
	    isValidationError = true;
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

 function getCarForDispatch(modelId){
 form_data = new FormData();
 form_data.append("sModelId", modelId);
 $.ajax({
		type : "POST",
		url:  ctx + '/operation/getCarForDispatch.html',
		processData: false,
		contentType: false,
		data : form_data,
		success : function(response) {
			if (response.status == "Success") {
				$('#carRegistrationList').html("");
				$(response.carDetailModelList).each(function (idx, o) {
			        $('#carRegistrationList').append("<option value='" + this.registrationNo + "'>" + this.registrationNo + "</option>");
			    });
			}else{
			}
		},
		error : function(e) {
	    	alert('Error: ' + e);
		}
     });
 }

 function dispatch(id){
	idForViewDutySlip=id;
	getCarForDispatch($("#carModelId_"+id).val()); 
	$('#dispatchDetailError').html("");
	$('#dispatchDetailError').hide();
	$('#dutySlipNo').val("");
	$('#carNumber').val("");
	$('#chauffeurName').val("");
	$("#venderName").val("");
	$("#carModelForAllocation").val("");
	$("#carOwnerType").val("");
	$("#pendingDutySlip").val("");
	$("#kmOut").val("");
	$("#isDsManual").prop('checked',false);
	$("#manualDSNO").val('');
	$("#remark").val("");
//		$("#smsClient").prop("checked",false);
	$("#smsBooker").prop("checked",false);
	$("#dutySlipIdForUpdate").val("0");
 	$('#dispatchDetailRecordPanel').modal('show');
	$("#bookingNO").val($("#BookingNo_"+id).html());
	$("#date").val($("#pickupDate_"+id).html());
	$("#time").val($("#startTime_"+id).html());
	idForUpdate = id;
   }

function fillCarRelDetails(selectedRegNo,op){
	if(op=="entry"){
		var obj=$("#carRegistrationList").find("option[value='"+selectedRegNo+"']")
		if(obj ==null && obj.length <= 0)
			return;
	} 
    form_data = new FormData();
    form_data.append("selectedRegNo", selectedRegNo);
    $.ajax({
	type : "POST",
	url:  ctx + '/fillCarRelDetails.html',
	processData: false,
	contentType: false,
	data : form_data,
	async: false,
	success : function(response) {
		if (response.status == "Success") {
			$("#pendingDutySlip").val(response.carAllocationModel.noOfPendingDutySlip == '' ? "0" : response.carAllocationModel.noOfPendingDutySlip);
	    	if(response.carAllocationModel.carDetailModelId == null){
		    	cModelMap[response.carAllocationModel.carDetailModelId.model.name] = response.carAllocationModel.carDetailModelId.id;
	    	}else{
				if(response.carAllocationModel.carDetailModelId.ownType == 'V'){
		    	    $("#carOwnerType").val("Vendor");
				}else if(response.carAllocationModel.carDetailModelId.ownType == 'C'){
		    	    $("#carOwnerType").val("Company");
				}else if(response.carAllocationModel.carDetailModelId.ownType == 'A'){
		    	    $("#carOwnerType").val("Adhoc Car");
					$("#venderName").val(response.carAllocationModel.carDetailModelId.ownName);
		    	}
		    	$("#carModelForAllocation").val(response.carAllocationModel.carDetailModelId.model.name);
		    	cModelMap[response.carAllocationModel.carDetailModelId.model.name] = response.carAllocationModel.carDetailModelId.id;
		    	if(response.carAllocationModel.carDetailModelId.ownType != 'A'){
					$("#chauffeurName").val(response.carAllocationModel.chauffeurId.name +' - ' +response.carAllocationModel.chauffeurId.mobileNo);
					$("#venderName").val(response.carAllocationModel.vendorId.contPerson +' - ' +response.carAllocationModel.vendorId.contPersonMobile);
			    	vModelMap[response.carAllocationModel.vendorId.contPerson +' - ' +response.carAllocationModel.vendorId.contPersonMobile] = response.carAllocationModel.vendorId.id;
			    	chModelMap[response.carAllocationModel.chauffeurId.name +' - ' +response.carAllocationModel.chauffeurId.mobileNo] = response.carAllocationModel.chauffeurId.id;
		    	}
	    	}

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

 function updatechauffeur()
 {	
	$('#updatedispatchDetailRecordPanel').modal('show');
	var booking  = document.getElementById("bookingNO").value;
	$("#UpdateBookingNo").text(booking);
	document.getElementById("carNo").value = carNO;
 }
		 
 function carno()
 {
     carNO = document.getElementById("carNumber").value;
 }
 
 function updateChauffeurDetails(){
     var updatedChauffeurName = $("#updateChauffeurName").val();
     var updatedChauffeurMobile = $("#updateMobileNumber").val();
     var chauffeurValidationErrorHtml = '<ol>';
     if(updatedChauffeurName == null || updatedChauffeurName == "")
	 chauffeurValidationErrorHtml = chauffeurValidationErrorHtml+ "<li>Chauffeur Name Can not be Blank !!!</li>";
     if(updatedChauffeurMobile == null || updatedChauffeurMobile == "")
	 chauffeurValidationErrorHtml = chauffeurValidationErrorHtml+ "<li>Chauffeur Mobile Can not be Blank !!!</li>";
     if(updatedChauffeurMobile != null && updatedChauffeurMobile!="" && updatedChauffeurMobile.length != 10)
	 chauffeurValidationErrorHtml = chauffeurValidationErrorHtml+ "<li>Chauffeur Mobile should be of 10 digit !!!</li>";
     chauffeurValidationErrorHtml = chauffeurValidationErrorHtml + "</ol>";
     if(chauffeurValidationErrorHtml != "<ol></ol>"){
	$('#chauffeurDetailError').html("Please correct following errors:<br>" + chauffeurValidationErrorHtml);
	$('#chauffeurDetailInfo').hide();
	$('#chauffeurDetailError').show();
     }else{
	$('#chauffeurDetailError').html("");
	$('#chauffeurDetailError').hide();
	$("#isUpdatedChauffeur").val("Y");
	$("#chauffeurName").val(updatedChauffeurName+"-"+updatedChauffeurMobile);
	$('#updatedispatchDetailRecordPanel').modal('hide');
     }	     
 }
 
 function dispatchRequest(oType){
	 if(oType == "dispatch"){
		 checkCarAvailabilityForDispatch(idForUpdate,$("#pickupDate_"+idForUpdate).text());
			if(check=="Y"){
				alert("This car is already assigned")
				return false;
			}else if(check == 'N'){
				 if (confirm('Car is availalbel !! \n Are you sure you want to Dispatch this Record ?')) {
					 stut = false;
				 }
			 }
		}
	 if(!stut){
		 $("#dataLoaderModal").show();
	     $("#dataLoaderFade").show();
	     validateDispatchDetails(oType);
	     if(!isValidationError){
		 var dutySlipStatus = "";
		 if(oType=="allocate")
		     dutySlipStatus = "A"
		 else
		     dutySlipStatus = "D"
		 var chauffeurNameMobile = $("#chauffeurName").val();
		 var chauffeurNameMobileArray = chauffeurNameMobile.split('-');
		 form_data = new FormData();
		 form_data.append("bookingModel.id", idForUpdate);
		 form_data.append("bookingModel.bookingNo", $("#bookingNO").val());
		 form_data.append("bookingModel.status", dutySlipStatus);
		 form_data.append("dutySlipStatus", dutySlipStatus);
		 form_data.append("carDetailModel.id", cModelMap[$('#carModelForAllocation').val()]);
		 form_data.append("carDetailModel.registrationNo",$('#carNumber').val());
		 form_data.append("carDetailModel.carAllocationModels[0].chauffeurId.name", chauffeurNameMobileArray[0]);
	     form_data.append("carDetailModel.carAllocationModels[0].chauffeurId.mobileNo", chauffeurNameMobileArray[1]);
	     if($('#isUpdatedChauffeur').val()=='Y'){
		     form_data.append("isUpdatedChauffeur", "Y");
		     form_data.append("chauffeurName", chauffeurNameMobileArray[0]);
		     form_data.append("chauffeurMobile", chauffeurNameMobileArray[1]);
		 }else if(chModelMap[$("#chauffeurName").val()]!=null && !chModelMap[$("#chauffeurName").val()]==""){
			form_data.append("chauffeurModel.id", chModelMap[$("#chauffeurName").val()]);
		 }
	     if(vModelMap[$("#venderName").val()] != undefined) form_data.append("vendorModel.id", vModelMap[$("#venderName").val()]);
	     if(oType == "allocate")
		     form_data.append("allocationDateTime", $("#date").val());
		 else if(oType == "dispatch")		     
		     form_data.append("dispatchDateTime", $("#date").val());
		 else if(oType == "allocateDispatch"){
		     form_data.append("allocationDateTime", $("#date").val());
		     form_data.append("dispatchDateTime", $("#date").val());
		 }		     
		 form_data.append("alDiTime", $('#time').val());
		 if(dutySlipStatus=="D"){
		     form_data.append("openKms", $("#kmOut").val());
		     form_data.append("dispatchedBy.id", loginUserId);
		 }
		 if($("#isDsManual").prop("checked") == true){
		     form_data.append("isDsManual", "Y");
		     form_data.append("manualSlipNo", $("#manualDSNO").val());
		     form_data.append("manualSlipRemarks", $("#remark").val());
		 }
		 form_data.append("smsClient", $("#smsClient").prop('checked')==true?"Y":"N");
		 form_data.append("smsBooker", $("#smsBooker").prop('checked')==true?"Y":"N");
		 if($("#dispatchSmsOther").val()!="")
		     form_data.append("dispatchSmsOther", $("#dispatchSmsOther").val());
		 if($("#dutySlipNo").val()!="")
		     form_data.append("dutySlipNo", $("#dutySlipNo").val());
		 form_data.append("dutySlipIdForUpdate", $("#dutySlipIdForUpdate").val());
		 form_data.append("oType", oType);
		 $.ajax({
			type : "POST",
			url:  ctx + '/tAllocateOrDispatch.html',
			processData: false,
			contentType: false,
			data : form_data,
			async:false,
			success : function(response) {
				if (response.status == "Success") {
				    $('#dispatchDetailInfo').html(response.result);
				    $("#dutySlipNo").val(response.tDutySlipModel.dutySlipNo);
				    $('#dispatchDetailError').hide();
				    $('#dispatchDetailInfo').show();
				    $("#dataLoaderModal").hide();
				    $("#dataLoaderFade").hide();		  
				    setTimeout(function(){$("#dispatchDetailRecordPanel").modal('hide');},1000);
				    viewDutySlip(idForViewDutySlip);
				    searchBooking();
				} else {
				    $("#dataLoaderModal").hide();
				    $("#dataLoaderFade").hide();
				    $('#dispatchDetailError').html(response.result);
				    $('#dispatchDetailInfo').hide();
				    $('#dispatchDetailError').show();				    
				}
			},
			error : function(e) {
			    	$("#dataLoaderModal").hide();
			    	$("#dataLoaderFade").hide();
			    	alert('Error: ' + e);
			}
		     });		 
	     }else{
		 $("#dataLoaderModal").hide();
		 $("#dataLoaderFade").hide();
	     }
	} 
 }
 
 	function checkboxClickAllU(id){
		if($('#'+id).prop('checked') == true){
			$('.checkboxU').addClass("edited");
			$('.checkboxU').prop("checked",true);
		}else{
			$('.checkboxU').removeClass("edited");
			$('.checkboxU').prop("checked",false);
		}
    } 
    
    function showWebBookingApprovedPanel(id){
    	idForUpdate=id;
        $("#bookingCancelPanel").show();
    }
    
    function showCancelReason(value){
    	allowPermission=value;
    	if(value == "A"){
    		 $("#cancelReason1").hide();
		}
    	else{
    		 $("#cancelReason1").show();
    	}
    }

    function validateDispatchDetails(opType){	    	    
		isValidationError = false;
		var dispatchValidationErrorHtml = '<ol>';
		var bookingNO = $('#bookingNO').val();
		if(bookingNO == null || bookingNO == ""){
		    dispatchValidationErrorHtml = dispatchValidationErrorHtml + "<li> Booking Number can not be Blank.</li>";
		    isValidationError = true;
		}
		var dutySlipStatus = "";
		var opSelected = $("input[name='dispatch']:checked");
		if (opSelected.length > 0) {
		    dutySlipStatus = opSelected.val();
		}
		if(dutySlipStatus == null || dutySlipStatus == "" || dutySlipStatus == undefined){
		    dispatchValidationErrorHtml = dispatchValidationErrorHtml + "<li> Operation Mode can not be Blank.</li>";
		    isValidationError = true;
		}
		
		var sChauffeurName = $("#chauffeurName").val();
		if(sChauffeurName == null || sChauffeurName == ""){
		    dispatchValidationErrorHtml = dispatchValidationErrorHtml + "<li> Chauffeur can not be Blank !!!</li>";
		    isValidationError = true;
		}		
		
		var carNumber = $('#carNumber').val();
		if(carNumber == null || carNumber == ""){
		    dispatchValidationErrorHtml = dispatchValidationErrorHtml + "<li> Car Number can not be Empty.</li>";
		    isValidationError = true;
		}		
		var carModelForAllocation = $('#carModelForAllocation').val();
		if(carModelForAllocation == null || carModelForAllocation == ""){
		    dispatchValidationErrorHtml = dispatchValidationErrorHtml + "<li> Car Model can not be Blank.</li>";
		    isValidationError = true;
		}
		var carOwnerType = $('#carOwnerType').val();
		if(carOwnerType == null || carOwnerType == ""){
		    dispatchValidationErrorHtml = dispatchValidationErrorHtml + "<li> Car Owner Type can not be Blank.</li>";
		    isValidationError = true;
		}
		var dispatchDate = $('#date').val();
		if(dispatchDate == null || dispatchDate == ""){
		    dispatchValidationErrorHtml = dispatchValidationErrorHtml + "<li> Dispatch Date can not be Blank.</li>";
		    dispatchValidationErrorHtml = true;
		}
		var dispatchTime = $('#time').val();
		if(dispatchTime == null || dispatchTime == ""){
		    dispatchValidationErrorHtml = dispatchValidationErrorHtml + "<li> Dispatch Time can not be Empty.</li>";
			isValidationError = true;
		}
		if(opType=='dispatch'){
		    var kmOut = $('#kmOut').val();
		    if(kmOut == null || kmOut == ""){
			dispatchValidationErrorHtml = dispatchValidationErrorHtml + "<li> KM Out can not be Empty.</li>";
			isValidationError = true;
		    }
		}
		if($('#isDsManual').prop('checked') == true){
		    var manualDSNO = $('#manualDSNO').val();
		    if(manualDSNO == null || manualDSNO == ""){
			dispatchValidationErrorHtml = dispatchValidationErrorHtml + "<li> Manual Ds No can not be Empty.</li>";
			isValidationError = true;
		    }
		}
		var dispatchSmsOther = $('#dispatchSmsOther').val();						   
		if(dispatchSmsOther != null && dispatchSmsOther != "" && dispatchSmsOther.length != 10){
		    dispatchValidationErrorHtml = dispatchValidationErrorHtml + "<li> Sms Other Mobile Number should be of 10 digit.</li>";
		    isValidationError = true;
		}		
		dispatchValidationErrorHtml = dispatchValidationErrorHtml + "</ol>";
		if(isValidationError){
			$('#dispatchDetailError').html("Please correct following errors:<br>" + dispatchValidationErrorHtml);
			$('#dispatchDetailInfo').hide();
			$('#dispatchDetailError').show();
		}else{
			$('#dispatchDetailError').html("");
			$('#dispatchDetailError').hide();
		}
	}
    
    function viewDutySlip(id){
    	$.ajax({
			type : "POST",
			url:  ctx + '/getTDutySlip/'+id+'.html',
			processData: false,
			contentType: false,
			async:false,
			success : function(response) {
				$('#info').html(response.result);
				$('#dutySlip').html(response.dataGrid);
				dutySlipRelatedStyles = (response.dataGrid).substring((response.dataGrid).indexOf('<style type="text/css">'),((response.dataGrid).indexOf('</style>'))+8);
				$('#viewDutySlip').modal('show');
		               },
			error : function(e) {
				alert('Error: ' + e);
			}
		               
		});
		
	}
    
    function checkCarAvailabilityForDispatch(bookingId,pickupDate){
    	$("#dataLoaderModal").show();
	    $("#dataLoaderFade").show();
    	form_data = new FormData();
    	form_data.append("bookingModel.id",bookingId);
    	form_data.append("reserveDateTime",pickupDate);
    	form_data.append("carDetailModel.id", cModelMap[$('#carModelForAllocation').val()]);
		$("#alo").attr("disabled", true);
		$.ajax({
			type : "POST",
			url:  ctx + '/checkCarAvailabilityForTDutySlip.html',
			processData: false,
			contentType: false,
			data : form_data,
			async:false,
			success : function(response) {
				if (response.status == "Success"){
				    $('#dispatchDetailInfo').html(response.result);
				    $('#dispatchDetailError').hide();
				    check = response.check;
				    $("#dataLoaderModal").hide();
				    $("#dataLoaderFade").hide();	
				} else {
				    $("#dataLoaderModal").hide();
				    $("#dataLoaderFade").hide();
				    $('#dispatchDetailError').html(response.result);
				    $('#dispatchDetailError').show();				    
				}
			},
			error : function(e) {
			    	$("#dataLoaderModal").hide();
			    	$("#dataLoaderFade").hide();
			    	alert('Error: ' + e);
			}
	     });	
		$("#alo").attr("disabled", false);
    }
    
    function print(){
    	$('#print').hide();
    	$('#close').hide();
        var divContents = $("#dutySlip1").html();
        var printWindow = window.open('', '', 'left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0');
        printWindow.document.write('<html><head>'+dutySlipRelatedStyles);
        printWindow.document.write('</head><body >');
        printWindow.document.write(divContents);
        printWindow.document.write('</body></html>');
        printWindow.document.close();
        printWindow.print();
        printWindow.close()
        $('#print').show();
	    $('#close').show();
    }
    
    function formFillForEditDetailRecord(id){
		setTableToModel(id);
		$('#editDetailRecordPanel').modal('show');
		$("#pickupDate_"+id).addClass("edited");
		$("#pickupDate_"+id).prop('disabled',false);
		$("#pickupLoc_"+id).prop('disabled',false);
		$("#dropAt_"+id).prop('disabled',false);
		$("#carModel_"+id).prop('disabled',false);
	}
    
    function setTableToModel(id){
  	  idForUpdate = id;
      $("#pickupDate").val($("#pickupDate_"+id).html());
	  $("#pickupTime").val($("#pickupTime_"+id).html());
	  $("#startTime").val($("#startTime_"+id).html());
	  $("#companyName").val($("#companyName option").filter(function() { return this.text == $("#companyName_"+id).html()}).val());
	  $("#bookedBy").val($("#bookedBy_"+id).html());
	  $("#tariff").val($("#tariff option").filter(function() { return this.text == $("#tariff_"+id).html()}).val());
	  $("#bookedFor").val($("#bookedForName_"+id).html());
	  $("#mobileNo").val($("#mobileNo_"+id).html());
	  $("#reportingAddress").val($("#pickupLoc_"+id).html());
	  $("#toBeReleaseAt").val($("#dropAt_"+id).html());
	  $("#instruction").val($("#splIns_"+id).html());		  		  
	  $("#rentalTypeModel").val($("#rentalTypeModel option").filter(function() { return this.text == $("#rentalType_"+id).html()}).val());
	  fillTariff();
	  $("#tariff").val($("#tariff option").filter(function() { return this.text == $("#tariff_"+id).html()}).val());
	  $("#carType").val($("#carType option").filter(function() { return this.text == $("#carModel_"+id).html()}).val());
  	}
    
    function fillTariff(){
    	var rentalId=$("#rentalTypeModel option:selected").val();
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
    	}
    
    function  setModelToTable(){
		 validateBookingDetails();	
		 if(!isValidationError){	    	    
   	    $('#pickupTime_'+idForUpdate).html($("#pickupTime").val());
   	    $('#startTime_'+idForUpdate).html($("#startTime").val());
   	    $("#tariff_"+idForUpdate).html($("#tariff option:selected" ).text());
   	    $('#pickupDate_'+idForUpdate).html($("#pickupDate").val());
   	    $('#corporateName_'+idForUpdate).html($( "#companyName option:selected" ).text());
   	    $('#bookedBy_'+idForUpdate).html($("#bookedBy").val());
   	    $('#bookedFor_'+idForUpdate).html($("#opClient").prop('checked')==true?"C":"O");
   	    $('#bookedForName_'+idForUpdate).html($("#bookedFor").val());
   	    $('#mobileNo_'+idForUpdate).html($("#mobileNo").val());				
   	    $('#pickupLoc_'+idForUpdate).html($("#reportingAddress").val());
   	    $('#dropAt_'+idForUpdate).html($("#toBeReleaseAt").val());
   	    $('#splIns_'+idForUpdate).html($("#instruction").val());
   	    $('#rentalType_'+idForUpdate).html($( "#rentalTypeModel option:selected" ).text());
   	    $('#carModel_'+idForUpdate).html($( "#carType option:selected" ).text());
   	    $("#pickupDate_"+idForUpdate).addClass("edited");
   	    userTable.destroy();		
	    refreshData();
 	    $('#editDetailRecordPanel').modal('hide');
 	    editedFormDataValuesMap["pickUpDate "] = $("#pickupDate").val();
	    editedFormDataValuesMap["pickUpTime "] = $("#pickupTime").val();
	    editedFormDataValuesMap["startTime "] = $("#startTime").val();
	    editedFormDataValuesMap["tariff.id "] = $("#tariff").val();
	    editedFormDataValuesMap["company.id "] = $( "#companyName").val();
	    editedFormDataValuesMap["bookingTakenBy.id "] = $( "#bookedBy").val();
	    editedFormDataValuesMap["customerName "] = $( "#bookedFor").val();
	    editedFormDataValuesMap["mobileNo "] = $("#mobileNo").val();
	    editedFormDataValuesMap["reportingAddress "] = $("#reportingAddress").val();
	    editedFormDataValuesMap["toBeRealeseAt "] = $("#toBeReleaseAt").val();
	    editedFormDataValuesMap["instruction "] = $("#instruction").val();
	    editedFormDataValuesMap["rentalType.id "] = $("#rentalTypeModel").val();
	    editedFormDataValuesMap["carModel.id "] = $("#carType").val();
   	 	}		
	   }
    
	function validateBookingDetails(){	    	    
		isValidationError = false;
		var validationErrorHtml = '<ol>';
		var pickupDate = $('#pickupDate').val();
		if(pickupDate == null || pickupDate == ""){
			validationErrorHtml = validationErrorHtml + "<li> Pickup Date can not be Blank.</li>";
			isValidationError = true;
		}
		var pickupTime = $('#pickupTime').val();
		if(pickupTime == null || pickupTime == ""){
			validationErrorHtml = validationErrorHtml + "<li> Pickup Time can not be Empty.</li>";
			isValidationError = true;
		}
		
		var startTime = $('#startTime').val();
		if(startTime == null || startTime == ""){
			validationErrorHtml = validationErrorHtml + "<li> Start Time can not be Blank.</li>";
			isValidationError = true;
		}
		
		if(startTime!=""){
		    if(!checkStart()){
			validationErrorHtml = validationErrorHtml + "<li> Start Time can not be less than current Date and Time.</li>";
			isValidationError = true;
		    }
		}
		
		if(pickupTime!=""){
		    if(!checkPickUp()){
			validationErrorHtml = validationErrorHtml + "<li> Pick Up Time should be atleast 1 hour after Start Time.</li>";
			isValidationError = true;
		    }
		}
		
		if(pickupDate!="" && pickupTime !=""){
		    if(!checkPickupDateTime()){
			validationErrorHtml = validationErrorHtml + "<li> Pickup Date and Time can not be less than current Date and Time.</li>";
			isValidationError = true;
		    }
		}
		var corporateName = $('#companyName').val();
		if(corporateName == null || corporateName == "" || corporateName == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Company Name can not be Blank.</li>";
			isValidationError = true;
		}
		var bookedBy = $('#bookedBy').val();
		if(bookedBy == null || bookedBy == "" || bookedBy == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Booked By can not be Blank.</li>";
			isValidationError = true;
		}
		
		var bookedForName =$("#bookedFor").val();
		if(bookedForName == null || bookedForName == ""){
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
		var rentalType = $('#rentalTypeModel').val();
		if(rentalType == null || rentalType == "" || rentalType == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Rental Type can not be Blank.</li>";
			isValidationError = true;
		}
		
		var tariff = $("#tariff").val()
		if(tariff == null || tariff == "" || tariff == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Tariff Scheme can't be Blank.</li>";
			isValidationError = true;
		}		
		
		var carType = $('#carType').val();
		if(carType == null || carType == "" || carType == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Car Type can not be Blank.</li>";
			isValidationError = true;
		}	
		
		var reportAt = $("#opOffice").prop('checked')==true?"F":"O";
		if(reportAt == null || reportAt == "" || reportAt == undefined){
			validationErrorHtml = validationErrorHtml + "<li> Report At not be Blank.</li>";
			isValidationError = true;
		}
		var reportingAddress =$("#reportingAddress").val();
		if(reportingAddress == null || reportingAddress == ""){
			validationErrorHtml = validationErrorHtml + "<li> Reporting Address not be Blank.</li>";
			isValidationError = true;
		}
		var toBeReleaseAt = $('#toBeReleaseAt').val();
		if(toBeReleaseAt == null || toBeReleaseAt == "" || toBeReleaseAt == "0"){
			validationErrorHtml = validationErrorHtml + "<li> To be Release At can not be Blank.</li>";
			isValidationError = true;
		}		
		validationErrorHtml = validationErrorHtml + "</ol>";
		if(isValidationError){
			$('#editDetailError').html("Please correct following errors:<br>" + validationErrorHtml);
			$('#editDetailInfo').hide();
			$('#editDetailError').show();
		}else{
			$('#editDetailError').html("");
			$('#editDetailError').hide();
		}
	}
	function checkStart() {
		var diff = null;
		var start = ($("#pickupDate").val()).concat(" ").concat($("#startTime").val());
		var today = getTimeStampTo_ddMMyyyyHHmmss(new Date(),'dd/mm/yyyy hh:mm')
		var ms = moment(start, "DD/MM/YYYY HH:mm").diff(moment(today, "DD/MM/YYYY HH:mm"));
		var d = moment.duration(ms);
		if (d < 0) {
			return false;
		}
		return true;
	}
	
	function checkPickupDateTime(){
	    if(jobType == 'Current' || jobType == 'Advance'){
		var diff = ( new Date($("#pickupDate").val() + " " + $("#pickupTime").val()) - new Date(new Date().today() + " " + new Date().timeNow()) );
		var hours = Math.floor(diff/1000/60/60);
		if(hours < 0){
			return false;
		}
		return true;
	    }
	}
	
	function checkPickUp(){
		if($("#pickupTime").val() == "00:00" && $("#startTime").val() == "00:00"){
			return true;
		}else{
			var pickup = ($("#pickupDate").val()).concat(" ").concat($("#pickupTime").val());
			var start =  ($("#pickupDate").val()).concat(" ").concat($("#startTime").val());
			var ms = moment(pickup, "DD/MM/YYYY HH:mm").diff(moment(start, "DD/MM/YYYY HH:mm"));
			var d = moment.duration(ms);
			var min = (d / 100) / 60;
			if (min < 60) {
				return false;
			}
			return true;
		}
	}
	
	function updateSaveBooking(){
		form_data = new FormData();
			var bookingDetailId =idForUpdate;
			form_data.set("pickUpDate ",$("#pickupDate_"+bookingDetailId).html());
			form_data.set("pickUpTime ",$("#pickupTime_"+bookingDetailId).html());
			form_data.set("startTime ",$("#startTime_"+bookingDetailId).html());
			form_data.set("idForUpdate",bookingDetailId);			
			form_data.set("company.id", editedFormDataValuesMap["company.id "]);
			form_data.set("customerName",$("#bookedForName_"+bookingDetailId).html());
			form_data.set("mobileNo", $('#mobileNo_'+bookingDetailId).html());
			form_data.set("reportingAddress", $("#pickupLoc_"+bookingDetailId).html());
			form_data.set("toBeRealeseAt", $('#dropAt_'+bookingDetailId).html());
			form_data.set("instruction", $('#splIns_'+bookingDetailId).html());
			form_data.set("rentalType.id", editedFormDataValuesMap["rentalType.id "]);
			form_data.set("carModel.id", editedFormDataValuesMap["carModel.id "]);
			form_data.set("tariff.id", editedFormDataValuesMap["tariff.id "]);
			$.ajax({
			type : "POST",
			url:  ctx + '/updateTBookingModel.html',
			data : form_data,
			processData: false,
			contentType: false,
			async:false,
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
		searchBooking();
	}
	
	/*For Cancellation Booking Record */
	function cancelRecord(id){
		idForUpdate = id;
		$('#bookingCancelPanel').modal('show');
		showHide();
	}
	
	function saveCancelReason(){
		validateCancelReason()
		form_data = new FormData();
		if(!isValidationError){
		    if($("#cancelReason option:selected" ).text() =='Other'){
			form_data.append("cancelReason", $("#otherReason").val());
		    }else{
			form_data.append("cancelReason", $("#cancelReason option:selected").text());
		    }
		    form_data.append("id", idForUpdate);
		    form_data.append("cancelBy.id",loginUserId);
		    $.ajax({
			type : "POST",
			url  : ctx + '/cancelTBooking.html',
			data : form_data,
			processData: false,
			contentType: false,
			async:false,
			success : function(response) {
			    if(response.status == "Success"){
				$('#cancelReasonInfo').html(response.result);
				$('#cancelReasonInfo').show();
				clearCancelFormData();
				setTimeout(function(){$('#bookingCancelPanel').modal('hide');},700);
			    }else{
				$('#cancelReasonError').html(response.result);
				$('#cancelReasonError').show();
			    }				
			},
			error : function(e) {
				alert('Error: ' + e);
			}
		    });		    
		    searchBooking();
		}		
	}
	
	function validateCancelReason(){
		isValidationError = false;
		var validationErrorHtml = '<ol>';
		var cancelReason = $('#cancelReason option:selected').text();
		if(cancelReason == '---Select---'){
			validationErrorHtml = validationErrorHtml + "<li> Please Select Cancel Reason.</li>";
			isValidationError = true;
		}
		if($('#cancelReason option:selected').text() == 'Other'){
			var otherReason = $('#otherReason').val();
			if(otherReason == null || otherReason == ''){
				validationErrorHtml = validationErrorHtml + "<li> Please Provide Valid Cancel Reason.</li>";
				isValidationError = true;
			}
		}
		validationErrorHtml = validationErrorHtml + "</ol>";
		if(isValidationError){
			$('#cancelReasonError').html("Please correct following errors:<br>" + validationErrorHtml);
			$('#cancelReasonInfo').hide();
			$('#cancelReasonError').show();
		}else{
			$('#cancelReasonError').html("");
			$('#cancelReasonError').hide();
		}
	}
		
	function clearCancelFormData(){
		$('#cancelReason option:selected').val('---Select---');
		$('#otherReason').val("");			
	}
	
	function showHide(){
	    if($("#cancelReason option:selected" ).text() =='Other'){
		$("#otherReason").show();
	    } else 
		$("#otherReason").hide();
	}
	
	function countRentalType(){
	    var options = document.getElementById("rentalTypeModel");
	    var optArray = [];
	    var optIdArray = [];
	    var count = 0;
	    var cells = [];
	    var col =0 ;
	    if(jobType == 'Current' || jobType == 'Advance'){
	    	col = 14;
	    }else if(jobType == 'Status'){
	    	col=13;
	    }else if(  jobType == 'Cancelled'){
	    	col = 12;
	    }else if( jobType == 'Expired'){
	    	col = 12;
	    }
	    var rows = $("#userTable").dataTable().fnGetNodes();
	    for (var i = 0; i < options.length; i++) {
	        optArray.push(options[i].text);
	        optIdArray.push(options[i].value);
	        count = 0;
	        for(var j=0;j<rows.length;j++)
	        {
	            cells.push($(rows[j]).find("td:eq("+col+")").text()); 
	        	if(cells[j] == optArray[i]){
	        		count++;
	        	  }
	        }
	          $("#"+optIdArray[i]).html(count);
	    }
	}
	
	function countJobStatus(){
		var cells = [];
	    var allCells = [];
	    var count = 0 ;
	    $("#userTable thead tr th").each(function ( i ) {
	    	var colNo = 0;
	    	if(jobType=='Cancelled'||jobType=='Expired')
	    		colNo = 14;
	    	else if(jobType=='Status')
	    		colNo = 15;
	    
	    	if (i==colNo){
	    		var tableContent = new Array();
	    		$("#jobStatusLabels").html("");
	    	 	    userTable.column(i).data().sort().each( function ( d, j ) {
	    	 	    	var foundIndex = tableContent.indexOf((d.split('>')[1]).split('</')[0]);
	    	 	    	if(foundIndex>-1)
	    	 	    		tableContent.splice(foundIndex,1);
	    	 	    	tableContent.push((d.split('>')[1]).split('</')[0]);
	    	 	    });
	    	 	    $.each(tableContent,function(index,val){
	    	 	    	cells.push(val);
	    	 	    	$("#jobStatusLabels").append('<b>'+cells[index]+'</b>&nbsp;[&nbsp;<span style="color: red ; font-size : 16px; font-weight : bold;" id="'+cells[index]+'"></span>&nbsp;]&nbsp;&nbsp');
	    	 	    })
	    	 	   var rows = $("#userTable").dataTable().fnGetNodes();
	    	 	   count = 0;
	    	 	   var countValueObject = {};
	    	 	   for(var k=0;k<rows.length;k++)
	    	 	   {
	   		        	console.log('allcells:'+allCells);
	   		        	console.log('cells:'+cells);
	   		        	allCells.push($(rows[k]).find("td:eq("+colNo+") textarea").text()); 
	   		        	if(cells.indexOf(allCells[k])>-1){
	   		        		countValueObject[allCells[k]] = (countValueObject[allCells[k]] == undefined? 1 :(countValueObject[allCells[k]]+1));
	   		        	}
	   		        	$("#"+allCells[k]).text(countValueObject[allCells[k]]);
	    	 	   }
	    	}
	    });
	}
	
	function fillHub(){
		var rentalId=$("#branch option:selected").val();
		$.ajax({  
			type: "POST",
			async: false,
			url:  ctx + "/getTariffAsPerRentalType.html",  
			data: "id="+rentalId,  
			success : function(response) {
				if (response.status == "Success") {
					$("#outlet").empty().append(
					'<option value="0">---Select---</option>');
					$.each(response.generalMasterModelList, function(index,val) {
					$("#outlet").append('<option value="'+val.id+'">' + val.name+ '</option>');
					});
				} 
			},  
			error: function(e){  
			}  
		});  
	}

