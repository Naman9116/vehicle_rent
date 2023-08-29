formFillUrl  = ctx + "/formFillForEditLocationMaster.html";
saveUpdateUrl = ctx + "/saveOrUpdateLocationMaster.html";
deleteUrl  = ctx + "/deleteLocationMaster.html";

var idForUpdate="";
var isValidationError = false;

function resetAll(){
	document.location.reload();
}

function localScriptWithRefresh(){}

function refreshData1(){	
	if($('#userTable1').dataTable() != null) $('#userTable1').dataTable().fnDestroy();
	userTable1 = $('#userTable1').dataTable({
		"bLengthChange": true,
		"bFilter": true,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": true,
		"aLengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]]
	});
}

function resetData(){
	document.location.reload();
}

function fillValues(){
	var corporateId=$("#corporateId option:selected").val();
	if(corporateId <= 0) return;
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/fillAsPerCorporate.html",  
		data: "corporateId="+corporateId,  
		success : function(response) {
			if (response.status == "Success") {
				refillCombo('#branch',response.branchList);
				response.corporateModel.branId == "0"?"":$("#branchName").val(response.corporateModel.branId);
				 $('#companyName').val(response.corporateModel.compId.id);
		    	 $('#companyCode').val(response.corporateModel.compId.itemCode);
	    	} else {
				$('#error').html(response.result);
				$('#info').hide('slow');
				$('#error').show('slow');
			}
			getLocationAsCorporate();
		},  
		error: function(e){  
		}  
	});  
}

function fillHub(){
	var branchId=$("#branch option:selected").val();
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/fillHubAsBranch.html",  
		data: "branchId="+branchId,  
		success : function(response) {
			if (response.status == "Success") {
				refillCombo('#outlet',response.generalMasterModelList);
				refillCombo('#zone',response.zoneList);
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

function fillLocation(){
	var zoneId=$("#zone option:selected").val();
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/fillLocationAsZone.html",  
		data: "zoneId="+zoneId,  
		success : function(response) {
			if (response.status == "Success") {
				refillCombo('#location',response.locationList);
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

function getCityUsingPincode(){
	var pincode=$("#pinCode").val();
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/getCityUsingPinCode.html",  
		data: "pincode="+pincode,  
		success : function(response) {
			if (response.status == "Success") {
				$("#city").empty().append(
				'<option value="0">---Select---</option>');
				$.each(response.cityMasterModelsList, function(index,val) {
					$("#city").append('<option value="'+val.id+'">' + val.name+ '</option>');
					});
			} 
		},  
		error: function(e){  
		}  
	});  
}

function getLocationAsCorporate(){
	var corporateId = $("#corporateId").val();
	if(corporateId == 0 || corporateId == "") return;
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/getLocationAsCorporate.html",  
		data: "corporateId="+corporateId,  
		success : function(response) {
			if (response.status == "Success") {
				$('#dataTable2').html(response.dataGrid);
				refreshData();
			} 
		},  
		error: function(e){  
		}  
	});  
}

function saveOrUpdateLocationMaster() {
	var i=0;
	var carRateIdsToDelete="";
	validateLocationMaster();
	if(!isValidationError){
		form_data = new FormData();
		form_data.append("corporateId.id", $('#corporateId option:selected').val());
		form_data.append("branch.id", $('#branch option:selected').val());
		form_data.append("outlet.id", $('#outlet option:selected').val());
		form_data.append("zone.id", $('#zone option:selected').val());
		form_data.append("location.id", $('#location').val());
		form_data.append("kms", $('#kms').val());
		form_data.append("pinCode", $('#pinCode').val());
		form_data.append("city.id", $('#city option:selected').val());
		form_data.append("userId.id", loginUserId);
		
		 $(".carCategory").each(function(){
			 var id = (this.id).split("_")[1]; 
			 var carRateId = $("#carRateId_"+id).val();
			 var oneWayRateCompany = $("#oneWayCompany_"+id).val() == ""?"0":$("#oneWayCompany_"+id).val();
			 var twoWayRateCompany = $("#twoWayCompany_"+id).val() == ""?"0":$("#twoWayCompany_"+id).val();
			 var oneWayRateVendor = $("#oneWayVendor_"+id).val()==""?"0":$("#oneWayVendor_"+id).val();
			 var twoWayRateVendor = $("#twoWayVendor_"+id).val()==""?"0":$("#twoWayVendor_"+id).val();
			 
			 
			 if(oneWayRateCompany == "0" && twoWayRateCompany == "0" && oneWayRateVendor =="0" && twoWayRateVendor == "0"){
				 if(carRateId != ""){
					 carRateIdsToDelete = carRateIdsToDelete + carRateId + ",";
					 form_data.append("carRateModel["+i+"].id",carRateId);
					 form_data.append("carRateModel["+i+"].carCategoryId",id);
					 form_data.append("carRateModel["+i+"].oneWayRate",oneWayRateCompany);
					 form_data.append("carRateModel["+i+"].twoWayRate",twoWayRateCompany);
					 form_data.append("carRateModel["+i+"].oneWayRateVendor",oneWayRateVendor);
					 form_data.append("carRateModel["+i+"].twoWayRateVendor",twoWayRateVendor);
					 form_data.append("carRateModel["+i+"].effectiveDate",$('#effectiveDate').val());
					 form_data.append("carRateModel["+i+"].status","D");
				 }
			 }else{
				 form_data.append("carRateModel["+i+"].id",carRateId);
				 form_data.append("carRateModel["+i+"].carCategoryId",id);
				 form_data.append("carRateModel["+i+"].oneWayRate",oneWayRateCompany);
				 form_data.append("carRateModel["+i+"].twoWayRate",twoWayRateCompany);
				 form_data.append("carRateModel["+i+"].oneWayRateVendor",oneWayRateVendor);
				 form_data.append("carRateModel["+i+"].twoWayRateVendor",twoWayRateVendor);
				 form_data.append("carRateModel["+i+"].effectiveDate",$('#effectiveDate').val());
				 form_data.append("carRateModel["+i+"].status","A");
				 i++;
			 }
		 });
		form_data.append("idForUpdate", $('#idForUpdate').val());
		form_data.append("carRateIdsToDelete", carRateIdsToDelete);

		saveOrUpdateRecord(form_data);
  }
}

function AssignFormControls(response){
	$("#idForUpdate").val(response.locationMasterModel.id);
	$("#corporateId").val(response.locationMasterModel.corporateId.id);
	fillValues(response.locationMasterModel.corporateId.id);
	$("#branch").val(response.locationMasterModel.branch.id);
	fillHub(response.locationMasterModel.branch.id);
	$("#outlet").val(response.locationMasterModel.outlet.id);
	$("#zone").val(response.locationMasterModel.zone.id);
	fillLocation(response.locationMasterModel.zone.id);
	$("#location").val(response.locationMasterModel.location.id);
	$("#kms").val(response.locationMasterModel.kms);
	$("#pinCode").val(response.locationMasterModel.pinCode);
	getCityUsingPincode();
	$("#city").val(response.locationMasterModel.city.id);
	$('#updateButton').prop('disabled', false);
	$('#addButton').prop('disabled', true);

}

function formFillForEditLocationMaster(formFillForEditId) {
	formFillRecord(formFillForEditId);
	getCarRate(formFillForEditId)
}

function deleteLocationMaster(idForDelete) {
	deleteRecord(idForDelete);
	refreshData();
} 

function viewLocationMaster(formFillForEditId){
	viewRecord(formFillForEditId);
	getCarRate(formFillForEditId)
}

function validateLocationMaster(){
	isValidationError = false;
	var validationErrorHtml = '<ol>';
	if( $('#corporateId option:selected').val()=="0"){
		validationErrorHtml = validationErrorHtml + "<li> Select Corporate Name</li>";
		isValidationError = true;
	}
	if( $('#corporateId option:selected').val()=="0"){
		validationErrorHtml = validationErrorHtml + "<li> Select Branch Name</li>";
		isValidationError = true;
	}
	if( $('#outlet option:selected').val()=="0"){
		validationErrorHtml = validationErrorHtml + "<li> Select Outlet Name</li>";
		isValidationError = true;
	}
	if( $('#zone option:selected').val()=="0"){
		validationErrorHtml = validationErrorHtml + "<li> Select Zone Name</li>";
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

function getCarRate(locationId){
	var carCategoryId=[];
		var i=0;	
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/getCarRate.html",  
		data: "locationId="+locationId,  
		success : function(response) {
			if (response.status == "Success") {
				$(".oneWayCompany").each(function(){
				       var id = (this.id).split("_"); 
				       carCategoryId.push(id[1]);
				});

				$.each(carCategoryId, function(index,val){
					$("#carRateId_"+val).val("");
					$("#oneWayCompany_"+val).val("");
					$("#oneWayVendor_"+val).val("");
					$("#twoWayCompany_"+val).val("");
					$("#twoWayVendor_"+val).val("");
					$.each(response.carRateModelList, function(index1,val1) {
						if(val == val1.carCategoryId && val1.status == 'A'){
							$("#carRateId_"+val).val(val1.id);
							$("#oneWayCompany_"+val).val(val1.oneWayRate);
							$("#oneWayVendor_"+val).val(val1.oneWayRateVendor);
							$("#twoWayCompany_"+val).val(val1.twoWayRate);
							$("#twoWayVendor_"+val).val(val1.twoWayRateVendor);
							$('#effectiveDate').val(getTimeStampTo_DDMMYYYY(val1.effectiveDate));
						}
					});
				});
			} else {
				$('#error').html(response.error);
				$('#info').hide('slow');
				$('#error').show('slow');
			}
		},  
		error: function(e){  
		}  
	});  
}