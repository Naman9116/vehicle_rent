saveUpdateUrl = ctx + "/saveOrUpdateCarDetail.html";
formFillUrl  = ctx + "/formFillForEditCarDetail.html";
deleteUrl  = ctx + "/deleteCarDetail.html";
var carId="";

function localScriptWithRefresh(){}

function fillOwner(ownerType,selectedIndex){
	if(ownerType == "A"){
		$('#engine').hide('slow');
		$('#chasis').hide('slow');
		$('#key').hide('slow');
		$('#bodyC').show('slow');
		$('#insurenceUptoSpan').hide('slow');
		$('#permitUptoSpan').hide('slow');
		$('#fitnessUptoSpan').hide('slow');
		$('#pucUptoSpan').hide('slow');
		$('#pucUptoSpan').hide('slow');
		$('#nameTextBox').show('slow');
		$('#nameSelectBox').hide('slow');
	}
	else if(ownerType == "V"){
		$('#engine').hide('slow');
		$('#chasis').hide('slow');
		$('#key').hide('slow');
		$('#bodyC').hide('slow');
		$('#nameTextBox').hide('slow');
		$('#nameSelectBox').show('slow');
	}else{
		$('#engine').show('slow');
		$('#chasis').show('slow');
		$('#key').show('slow');
		$('#bodyC').show('slow');
		$('#nameTextBox').hide('slow');
		$('#nameSelectBox').show('slow');
	}
	if((ownerType!= "A")){
  	$.ajax({  
	    type: "POST",  	
	    url:  ctx + "/getVendorCompany.html",  
	    data: "ownerType=" + ownerType,  
	    success: function(response){
	      	if(response.status == "Success"){
				$('#ownName')
			    .find('option')
			    .remove()
			    .end()
			    .append('<option value="0">--- Select ---</option>');
				$.each(response.generalMasterModelList, function (i, item) {
				    $('#ownName').append($('<option>', { 
				        value: item.id,
				        text : item.name 
				    }));
				});
				$('#ownName').val(selectedIndex);
			}
	      	else{
				$('#error').html(response.result);
				$('#info').hide('slow');
				$('#error').show('slow');
			}
		},  
	    error: function(e){  
	    }  
	});  
	}
}	

function fillBranch(companyId,selectedIndex){
	var ownerType =  $('#ownType').val();
  	$.ajax({  
	    type: "POST",  	
	    url:  ctx + "/getBranchAsCompany.html",  
	    data: "companyId=" + companyId + "&ownerType=" + ownerType,  
	    success: function(response){
	      	if(response.status == "Success"){
				$('#branchId')
			    .find('option')
			    .remove()
			    .end()
			    .append('<option value="0">--- Select ---</option>');
				$.each(response.generalMasterModelList, function (i, item) {
				    $('#branchId').append($('<option>', { 
				        value: item.id,
				        text : item.name 
				    }));
				});
				$('#branchId').val(selectedIndex);
			}
	      	else{
				$('#error').html(response.result);
				$('#info').hide('slow');
				$('#error').show('slow');
			}
		},  
	    error: function(e){  
	    }  
	});  
}	

function fillYear(purDate){
	$('#manufacturerYear').val(purDate.substring(6, 10));
}
function resetData(){
	document.location.reload();
} 

function saveOrUpdate() {
	var form_data = new FormData();
	form_data.append("userId.id", loginUserId);
	form_data.append("idForUpdate", $('#idForUpdate').val());
	if($('#idForUpdate').val() != "0" ){
		form_data.append("insuranceDetailModel[0].id", $('#policyId').val());
		form_data.append("taxDetailModel[0].id", $('#rtoTaxId').val());
		form_data.append("taxDetailModel[0].statePermitDetailModel[0].id", $('#fitnessId').val());
		form_data.append("taxDetailModel[0].statePermitDetailModel[1].id", $('#PUCId').val());
		form_data.append("carId", carId);
	}
	form_data.append("ownType", $('#ownType').val());
	if($('#ownType').val()=="A"){
	form_data.append("ownName", $("#ownNameText").val());
	}else{
	form_data.append("ownName", $("#ownName option:selected").text());
	}
	form_data.append("branchId.id", $('#branchId').val());
	form_data.append("fuelId.id", $('#fuelId').val());
	form_data.append("model.id", $('#model').val());
	form_data.append("make.id", $('#make').val());
	form_data.append("registrationNo", $('#registrationNo').val());
	form_data.append("engineNo", $('#engineNo').val());
	form_data.append("chasisNo", $('#chasisNo').val());
	form_data.append("keyNo", $('#keyNo').val());
	form_data.append("bodyColor.id", $('#bodyColor').val());
	form_data.append("bodyStyle.id", $('#bodyStyle').val());
	form_data.append("purchFrom", $('#purchFrom').val());
	form_data.append("purchDate", $('#purchDate').val());
	form_data.append("manufacturerYear", $('#manufacturerYear').val());
	form_data.append("exShPrice", $('#exShPrice').val());
	form_data.append("onRoadPrice", $('#onRoadPrice').val());
	form_data.append("hypothicationTo", $('#hypothicationTo').val());
	form_data.append("emiRs", $('#emiRs').val());
	form_data.append("hypothicationDate", $('#hypothicationDate').val());
	form_data.append("lastDateOfEmi", $('#lastDateOfEmi').val());
	form_data.append("status", $('#status').val());
	form_data.append("regAuth", $('#regAuth').val());
	form_data.append("gpsEnabled", $('#gpsEnabled').val());
	form_data.append("gpsCompany", $('#gpsCompany').val());
	form_data.append("saleDate", $('#saleDate').val());
	form_data.append("byerName", $('#byerName').val());
	form_data.append("byerMobile", $('#byerMobile').val());
	form_data.append("saleAmt", $('#saleAmt').val());
	form_data.append("minBusReq", $('#minBusReq').val());
	form_data.append("minKmsReq", $('#minKmsReq').val());
	form_data.append("mileage", $('#mileage').val());
	/*Insurance Details*/
	if($('#ownType').val()!="A"){
	form_data.append("insuranceDetailModel[0].policyNumber", $('#policyNumber').val());
	form_data.append("insuranceDetailModel[0].insurerName", $('#insurerName').val());
	form_data.append("insuranceDetailModel[0].insuEndDate", $('#insuEndDate').val());
	form_data.append("insuranceDetailModel[0].premium", $('#premium').val());
	/*Permit Details*/
	form_data.append("taxDetailModel[0].rtoTax", $('#rtoTax').val());
	form_data.append("taxDetailModel[0].rtoEndDate", $('#rtoEndDate').val());
	/*Fitness Certificate*/
	form_data.append("taxDetailModel[0].statePermitDetailModel[0].permitName", 'Fitness');
	form_data.append("taxDetailModel[0].statePermitDetailModel[0].permitNo", $('#fitnessNo').val());
	form_data.append("taxDetailModel[0].statePermitDetailModel[0].endDate", $('#fitnessEndDate').val());
	/*PUC Details*/
	form_data.append("taxDetailModel[0].statePermitDetailModel[1].permitName", 'PUC');
	form_data.append("taxDetailModel[0].statePermitDetailModel[1].endDate", $('#pucEndDate').val());
	}
	saveOrUpdateRecord(form_data);
	$('#idForUpdate').val('0');
}  
function AssignFormControls(response){
	carId = response.carDetailModel.carId;
	$('#ownType').val(response.carDetailModel.ownType);
	if(response.carDetailModel.ownType!="A"){
	$("#ownName option:contains(" + response.carDetailModel.ownName + ")").attr('selected', 'selected');
	$('#nameTextBox').hide('slow');
	$('#nameSelectBox').show('slow');
	}
	else{
	$('#ownNameText').val(response.carDetailModel.ownName);
	$('#nameTextBox').show('slow');
	$('#nameSelectBox').hide('slow');
	}
	$('#branchId').val(response.carDetailModel.branchId.id);
	$('#fuelId').val(response.carDetailModel.fuelId.id);
	$('#model').val(response.carDetailModel.model.id);
	$('#make').val(response.carDetailModel.make.id);
	$('#registrationNo').val(response.carDetailModel.registrationNo);
	$('#engineNo').val(response.carDetailModel.engineNo);
	$('#chasisNo').val(response.carDetailModel.chasisNo);
	$('#keyNo').val(response.carDetailModel.keyNo);
	$('#bodyColor').val(response.carDetailModel.bodyColor.id);
	$('#bodyStyle').val(response.carDetailModel.bodyStyle.id);
	$('#purchFrom').val(response.carDetailModel.purchFrom);
	$('#purchDate').val(response.carDetailModel.purchDate==null?"":getTimeStampTo_DDMMYYYY(response.carDetailModel.purchDate));
	$('#manufacturerYear').val(response.carDetailModel.manufacturerYear);
	$('#exShPrice').val(response.carDetailModel.exShPrice);
	$('#onRoadPrice').val(response.carDetailModel.onRoadPrice);
	$('#hypothicationTo').val(response.carDetailModel.hypothicationTo);
	$('#emiRs').val(response.carDetailModel.emiRs);
	$('#hypothicationDate').val(response.carDetailModel.hypothicationDate==null?"":getTimeStampTo_DDMMYYYY(response.carDetailModel.hypothicationDate));
	$('#lastDateOfEmi').val(response.carDetailModel.lastDateOfEmi==null?"":getTimeStampTo_DDMMYYYY(response.carDetailModel.lastDateOfEmi));
	$('#status').val(response.carDetailModel.status);
	$('#regAuth').val(response.carDetailModel.regAuth);
	$('#gpsEnabled').val(response.carDetailModel.gpsEnabled);
	$('#gpsCompany').val(response.carDetailModel.gpsCompany);
	$('#saleDate').val(response.carDetailModel.saleDate==null?"":getTimeStampTo_DDMMYYYY(response.carDetailModel.saleDate));
	$('#byerName').val(response.carDetailModel.byerName);
	$('#byerMobile').val(response.carDetailModel.byerMobile);
	$('#saleAmt').val(response.carDetailModel.saleAmt);
	$('#minBusReq').val(response.carDetailModel.minBusReq);
	$('#minKmsReq').val(response.carDetailModel.minKmsReq);
	$('#mileage').val(response.carDetailModel.mileage);
	/*Insurance Details*/
	if(response.carDetailModel.insuranceDetailModel.length > 0){
		$('#policyId').val(response.carDetailModel.insuranceDetailModel[0].id);
		$('#policyNumber').val(response.carDetailModel.insuranceDetailModel[0].policyNumber);
		$('#insurerName').val(response.carDetailModel.insuranceDetailModel[0].insurerName);
		$('#insuEndDate').val(response.carDetailModel.insuranceDetailModel[0].insuEndDate==null?"":getTimeStampTo_DDMMYYYY(response.carDetailModel.insuranceDetailModel[0].insuEndDate));
		$('#premium').val(response.carDetailModel.insuranceDetailModel[0].premium);
	}else{
		$('#policyId').val('');
		$('#policyNumber').val('');
		$('#insurerName').val('');
		$('#insuEndDate').val('');
		$('#premium').val('');
	}
	/*Permit Details*/
	if(response.carDetailModel.taxDetailModel.length > 0){
		$('#rtoTaxId').val(response.carDetailModel.taxDetailModel[0].id);
		$('#rtoTax').val(response.carDetailModel.taxDetailModel[0].rtoTax);
		$('#rtoEndDate').val(response.carDetailModel.taxDetailModel[0].rtoEndDate==null?"":getTimeStampTo_DDMMYYYY(response.carDetailModel.taxDetailModel[0].rtoEndDate));
		$.each(response.carDetailModel.taxDetailModel[0].statePermitDetailModel, function (i, item) {
			if(i == 0){
	    		/*Fitness Certificate*/
	    		$('#fitnessId').val(item.id);
	    		$('#fitnessNo').val(item.permitNo);
	    		$('#fitnessEndDate').val(item.endDate==null?"":getTimeStampTo_DDMMYYYY(item.endDate));
		    }else if(i == 1){
	    		/*PUC Details*/
	    		$('#PUCId').val(item.id);
	    		$('#pucEndDate').val(item.endDate==null?"":getTimeStampTo_DDMMYYYY(item.endDate));
		    }
		});
	}else{
		$('#rtoTaxId').val('');
		$('#rtoTax').val('');
		$('#rtoEndDate').val('');
		$('#fitnessId').val('');
		$('#fitnessNo').val('');
		$('#fitnessEndDate').val('');
		$('#PUCId').val('');
		$('#pucEndDate').val('');
	}
}
function formFillForEdit(formFillForEditId) {
	formFillRecord(formFillForEditId);		
}  

function searchCarStatus(){
	var bookingFromDate =$('#bookingFromDate').val();
	var bookingToDate =  $('#bookingToDate').val();
	var status =  $('#status option:selected').val();
  	$.ajax({  
	    type: "POST",  	
	    url:  ctx + "/searchCarStatus.html",  
	    data: "bookingFromDate=" + bookingFromDate + "&bookingToDate=" + bookingToDate+"&flag=" + status,  
	    success: function(response){
	      	if(response.status == "Success"){
	      		$('#dataTable2').html(response.dataGrid);
				refreshData();
			}
	      	else{
				$('#error').html(response.result);
				$('#info').hide('slow');
				$('#error').show('slow');
			}
		},  
	    error: function(e){  
	    }  
  	});
}
