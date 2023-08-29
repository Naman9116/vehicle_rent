formFillUrl  = ctx + "/formFillForEditRosterMaster.html";
deleteUrl  = ctx + "/deleteRosterMaster.html";

var i = 1;
var carTypeList ;
var idArr = [];
var isValidationError=false;

function resetData(){
	document.location.reload();
}

var companyName="";
var companyCode="";
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
				refillCombo('#branch',response.branchList);
								
				/*response.corporateModel.branId == "0"?"":$("#branch").val(response.corporateModel.branId);
				response.corporateModel.branId == "0"?"":fillHub(response.corporateModel.branId);
	    	    $('#companyName').val(response.corporateModel.compId.id);
	    	    compName = response.corporateModel.compId.name;
	    	    $('#companyCode').val(response.corporateModel.compId.itemCode);*/
				companyName=response.corporateModel.compId.id;
				companyCode=response.corporateModel.compId.itemCode;
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
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/fillHubAsBranch.html",  
		data: "branchId="+branchId,  
		success : function(response) {
			if (response.status == "Success") {
				refillCombo('#outlet',response.generalMasterModelList);
				
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

function fillClientAsPerMobile(id){
	var mob = $("#"+id ).val();
	var id = id.split("_");
	var corporateId=$('#corporateId option:selected').val();
	validateRosterMaster();
	if(!isValidationError){
		$.ajax({  
			type: "POST",
			async: false,
			url:  ctx + "/fillClientAsPerMobile.html",  
			data:"mob=" + mob + "&corporateId=" + corporateId,  
			success : function(response) {
				if (response.status == "Success") {
					if(response.autorizedUserModel!=null){
						$("#empId_"+id[1]).val(response.autorizedUserModel.empCode);
						$("#authId_"+id[1]).val(response.autorizedUserModel.id);
						$("#empName_"+id[1]).val(response.autorizedUserModel.name);
						$("#empPickupAdd_"+id[1]).val(response.autorizedUserModel.hAddress);
						$("#city_"+id[1]).val(response.autorizedUserModel.city == null ? "" : response.autorizedUserModel.city.name);
						$("#location_"+id[1]).val(response.autorizedUserModel.location == null ? "" : response.autorizedUserModel.location.location);
						$("#zone_"+id[1]).val(response.autorizedUserModel.zone == null ? "" : response.autorizedUserModel.zone.name);
						$("#division_"+id[1]).val(response.autorizedUserModel.division == null ? "" : response.autorizedUserModel.division);
						$('#error').html("");
						$('#error').hide()
					}else{
						$('#error').html("Please Enter Valid Mobile No");
						isValidationError = true;
						$('#error').show();
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
}

function addNewRow(){
	validateRosterDetails();
	if(!isValidationError){
		$('#error').html("");
		$('#error').hide();
		i++;  
		$("#rosterTable").append("<tr id='row_"+i+"'>" +
			"		<td><input type='button' id='btnMin_"+i+"'  class='btn' style='background : #FF9300' value='-' onclick='removeRow(this.id)'/></td>"+
			"     	<td><input type='text' class='form-control mob' id='mobileNo_"+i+"'  maxlength='10' onChange='fillClientAsPerMobile(this.id)' onkeypress='isNumeric(event)'/>" +
			"				<input type='text' class='form-control' id='authId_"+i+"' style='display:none;'/></td>" +
			"	  	<td><input type='text' class='form-control' id='pickupTime_"+i+"' maxlength='5'/></td>" +
			"     	<td><input type='text' class='form-control' id='routeNo_"+i+"'/></td>" +
			"     	<td>" +
			"				<select class='form-control' id='carModel_"+i+"' name='carModel' >" +
			"							<option value='0'>---Select---</option>" +
			"				</select>" +
			"		</td>" +
			"		<td align='center'><input type='checkbox'  id='pickup_"+i+"' checked='checked'/></td>" +
			"		<td align='center'><input type='checkbox' id='drop_"+i+"' checked='checked'/></td>" +
			"   	<td><input type='text' class='form-control' id='empId_"+i+"' readonly='readonly'/></td>" +
			"		<td><input type='text' class='form-control' id='empName_"+i+"' readonly='readonly'/></td>" +
			"		<td><input type='text' class='form-control' id='empPickupAdd_"+i+"' readonly='readonly'/></td>" +
			"		<td><input type='text' class='form-control' id='zone_"+i+"' readonly='readonly'/></td>" +
			"		<td><input type='text' class='form-control' id='location_"+i+"' readonly='readonly'/></td>" +
			"		<td><input type='text' class='form-control' id='city_"+i+"' readonly='readonly'/></td>" +
			"		<td><input type='text' class='form-control' id='division_"+i+"' readonly='readonly'/></td>" +
			"	</tr>");
			fillCarType();
	}
}

function fillCarType(){
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/fillCarType.html",  
		success : function(response) {
			if (response.status == "Success") {
				carTypeList = response.generalMasterModelList;
				$.each(carTypeList,function(index,val){
					$("#carModel_"+i).append('<option value='+val.id+'>' +val.name+ '</option>)');
					$("#pickupTime_"+i).inputmask("hh:mm", {"placeholder": "hh:mm"});
				})
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

function removeRow(id){
	var btnId = id.split("_");
	$("#row_"+btnId[1]).remove();
}

function saveOrUpdate(){
	 form_data = new FormData();
	 form_data.append("idForUpdate", $("#idForUpdate").val());
	 form_data.append("corporateId.id",$("#corporateId").val());
	 form_data.append("rosterTakenBy.id",loginUserId);
	 form_data.append("branch.id",$("#branch").val());
	 form_data.append("outlet.id",$("#outlet").val());
	 form_data.append("bookedBy.id",$("#bookedBy").val());
	 form_data.append("shiftTime.id",$("#shiftTiming").val());
	 form_data.append("rosterDate",$("#rosterDate").val());
	 form_data.append("userId.id",loginUserId);
	 $(".mob").each(function(){
		 if($("#"+this.id).val() != ""){
			 var id = (this.id).split("_"); 
				 form_data.append("authoriseClientId",$("#authId_"+id[1]).val());
				 form_data.append("pickUpTime",$("#pickupTime_"+id[1]).val());
				 form_data.append("isPickup",$("#pickup_"+id[1]).prop("checked") ==  true ? "Y" : "N");
				 form_data.append("isDrop",$("#drop_"+id[1]).prop("checked") ==  true ? "Y" : "N");
				 form_data.append("routeNo",$("#routeNo_"+id[1]).val());
				 form_data.append("carType",$("#carModel_"+id[1]).val());
				 form_data.append("mobileNo",$("#mobileNo_"+id[1]).val());
		  }
	 });
 	validateRosterMaster();
	validateRosterDetails();
	if(!isValidationError){
		 $.ajax({  
				 type: "POST",
				 url:  ctx + "/saveUpdateRoster.html",
				 processData: false,
			     contentType: false,
			     data : form_data,
			     async:false,
			     success : function(response) {
			      if (response.status == "Success") {
			    	alert(response.result);
			    	  document.location.reload();
			    	  if($('#userTable').dataTable() != null) $('#userTable').dataTable().fnDestroy();
					  $('#dataTable2').html(response.dataGrid);
					  refreshData();
			          $('#error').hide();
			          $('#info').show();
			      }else {
			          $('#error').html(response.result);
			          $('#info').hide();
			          $('#error').show();      
			      }
			  },
		     error : function(e) {
		          alert('Error: ' + e);
		     }
	     });
	} 
}

function formFillForEditRosterMaster(formFillForEditId) {
	formFillRecord(formFillForEditId);
}

function viewRosterMaster(formFillForEditId){
	viewRecord(formFillForEditId);
}

function deleteRosterMaster(idForDelete) {
	deleteRecord(idForDelete);
	refreshData();
} 

function AssignFormControls(response){
	$("#idForUpdate").val(response.rosterMasterModel.id);
	$("#corporateId").val(response.rosterMasterModel.corporateId.id);
	fillValues(response.rosterMasterModel.corporateId.id);
	$("#branch").val(response.rosterMasterModel.branch.id);
	fillHub(response.rosterMasterModel.branch.id);
	$("#outlet").val(response.rosterMasterModel.outlet.id);
	$("#bookedBy").val(response.rosterMasterModel.bookedBy.id);
	$("#shiftTiming").val(response.rosterMasterModel.shiftTime.id);
	$("#rosterTaken").val(response.rosterMasterModel.rosterTakenBy.userName);
	$("#rosterDate").val(getTimeStampTo_DDMMYYYY(response.rosterMasterModel.rosterDate));
	fillRosterDetails();
	
	$('#updateButton').prop('disabled', false);
	$('#addButton').prop('disabled', true);
}

function fillRosterDetails(){
	form_data = new FormData();
	 form_data.append("corporateId.id",$("#corporateId").val());
	 form_data.append("rosterTakenBy.id",loginUserId);
	 form_data.append("branch.id",$("#branch").val());
	 form_data.append("outlet.id",$("#outlet").val());
	 form_data.append("bookedBy.id",$("#bookedBy").val());
	 form_data.append("shiftTime.id",$("#shiftTiming").val());
	 form_data.append("rosterDate",$("#rosterDate").val());
	 form_data.append("userId.id",loginUserId);
	
	 $.ajax({  
		 type: "POST",
		 url:  ctx + "/fillRosterDetails.html",
		 processData: false,
	     contentType: false,
	     data : form_data,
	     async:false,
	     success : function(response) {
		      if (response.status == "Success") {
		    	 var recordSize = response.rosterMasterModelList.length; 
		    	 $("#rosterTable").empty();
		    	 $("#rosterTable").append("<tr bgcolor='#FFDA71' style='font-family: cambria;font-size: 14; height: 10px;'>" +
		    	 					"		<th>Add</th>" +
		    	 					"		<th>Mobile No.</th>" +
		    	 					"		<th>PickUp Time</th>" +
		    	 					"		<th>Route No.</th>" +
		    	 					"		<th>Car Type</th>" +
		    	 					"		<th>PickUp</th>" +
		    	 					"		<th>Drop</th>" +
		    	 					"		<th>EmployeeId</th>" +
		    	 					"		<th>Employee Name</th>" +
		    	 					"		<th>PickUp Address</th>" +
		    	 					"		<th>Zone</th>" +
		    	 					"		<th>Location.</th>" +
		    	 					"		<th>City</th>" +
		    	 					"		<th>Division</th>" +
		    	 					"</tr>");
		    	 for(var i = 1; i <= recordSize; i++){
		    		 $.each(response.rosterMasterModelList, function(index,val){
		    			 
		    			 $("#rosterTable").append("<tr id='row_"+i+"'>" +
									"		<td><input type='button' id='btnMin_"+i+"'  class='btn' style='background : #FF9300' value='-' onclick='removeRow(this.id)' /></td>"+
									"     	<td><input type='text' class='form-control mob' id='mobileNo_"+i+"'  maxlength='10' onChange='fillClientAsPerMobile(this.id)' onkeypress='isNumeric(event)'/>" +
									"				<input type='text' class='form-control' id='authId_"+i+"' style='display:none;'/></td>" +
									"	  	<td><input type='text' class='form-control' id='pickupTime_"+i+"' maxlength='5'  /></td>" +
									"     	<td><input type='text' class='form-control' id='routeNo_"+i+"''/></td>" +
									"     	<td>" +
									"				<select class='form-control' id='carModel_"+i+"' name='carModel' >" +
									"							<option value='0'>---Select---</option>" +
									"				</select>" +
									"		</td>" +
									"		<td align='center'><input type='checkbox'  id='pickup_"+i+"'/></td>" +
									"		<td align='center'><input type='checkbox' id='drop_"+i+"'/></td>" +
									"   	<td><input type='text' class='form-control' id='empId_"+i+"' readonly='readonly'/></td>" +
									"		<td><input type='text' class='form-control' id='empName_"+i+"' readonly='readonly'/></td>" +
									"		<td><input type='text' class='form-control' id='empPickupAdd_"+i+"' readonly='readonly'/></td>" +
									"		<td><input type='text' class='form-control' id='zone_"+i+"' readonly='readonly'/></td>" +
									"		<td><input type='text' class='form-control' id='location_"+i+"' readonly='readonly'/></td>" +
									"		<td><input type='text' class='form-control' id='city_"+i+"' readonly='readonly'/></td>" +
									"		<td><input type='text' class='form-control' id='division_"+i+"' readonly='readonly'/></td>" +
									"	</tr>");
		    				 $("#mobileNo_"+i).val(val.mobileNo);
			    			 $("#pickupTime_"+i).val(val.pickUpTime);
			    			 $("#routeNo_"+i).val(val.routeNo);
							fillCarType();
							$("#carModel_"+i).val(val.carType);
							fillClientAsPerMobile($("#mobileNo_"+i).attr("id"));
							if(val.isPickup == "Y"){
								$("#pickup_"+i).prop("checked", true);
							}
							if(val.isDrop == "Y"){
								$("#drop_"+i).prop("checked", true);
							}
			    	 }); 
	    		 }
		      }else {
		          $('#error').html(response.result);
		          $('#info').hide();
		          $('#error').show();      
		      }
		  },
	     error : function(e) {
	          alert('Error: ' + e);
	     }
	 });
}

function validateRosterMaster(){
	isValidationError = false;
	var validationErrorHtml = '<ol>';
	if( $('#corporateId option:selected').val()=="0"){
		validationErrorHtml = validationErrorHtml + "<li> Select Corporate Name</li>";
		isValidationError = true;
	}
	if( $('#branch option:selected').val()=="0"){
		validationErrorHtml = validationErrorHtml + "<li> Select Branch Name</li>";
		isValidationError = true;
	}
	
	if( $('#outlet option:selected').val()=="0"){
		validationErrorHtml = validationErrorHtml + "<li> Select Outlet Name</li>";
		isValidationError = true;
	}
	if( $('#bookedBy option:selected').val()=="0"){
		validationErrorHtml = validationErrorHtml + "<li> Select BookedBy</li>";
		isValidationError = true;
	}
	if( $('#shiftTiming option:selected').val()=="0"){
		validationErrorHtml = validationErrorHtml + "<li> Select Shift Timming </li>";
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

function validateRosterDetails(){
	var pValidateStatus=false;
	var rValidateStatus=false;
	var mValidateStatus=false;
	var cValidateStatus=false;
	isValidationError = false;
	var validationErrorHtml = '<ol>';
	$(".mob").each(function(){
		 var id = (this.id).split("_"); 
		 if($("#pickupTime_"+id[1]).val()=="") pValidateStatus=true;
		 if($("#routeNo_"+id[1]).val()=="")    rValidateStatus=true;
		 if($("#mobileNo_"+id[1]).val()=="")   mValidateStatus=true;	
		 if($("#carModel_"+id[1]).val()=="0")  cValidateStatus=true;	
	 });
	if(pValidateStatus){
		validationErrorHtml = validationErrorHtml + "<li>PickUpTime can not be blank</li>";
		isValidationError = true;
	} if(rValidateStatus){
		validationErrorHtml = validationErrorHtml + "<li>Route No can not be blank</li>";
		isValidationError = true;
	} if(mValidateStatus){
	     validationErrorHtml = validationErrorHtml + "<li>Mobile No can not be blank</li>";
		 isValidationError = true;
	} if(cValidateStatus){
	     validationErrorHtml = validationErrorHtml + "<li>Please Select car model</li>";
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
