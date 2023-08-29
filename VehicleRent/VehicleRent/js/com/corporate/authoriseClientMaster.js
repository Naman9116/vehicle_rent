	formFillUrl  = ctx + "/formFillForEditAuthorisedUser.html";
	saveUpdateUrl = ctx + "/saveOrUpdateAuthorisedUser.html";
	deleteUrl  = ctx + "/deleteAuthorizedUser.html";
	var previousPassword="",firstLogin="";
	var sIsAdmin = "N", sIsClient = "N";
	var mCity = new Object();
	
	function resetData(){
		$('#autorizedUserForm').trigger("reset");
		resetContactDetails();
		responseObj_contactDetail=null;
		document.getElementById("addButton").disabled = false;
		document.getElementById("updateButton").disabled = true;
		$('#idForUpdate').val('');
		$('#error').hide();
		$('#info').hide();
		refreshData();
	}

	function AssignAuthoriser(corporateId,parentId){
		if(pageFor == "Client"){
			$.ajax({
				type : "POST",
				url:  ctx + "/getAuthoriser.html",
				data : "corporateId=" + corporateId,
				success : function(response) {
					if (response.status == "Success") {
						$('#parentId')
					    .find('option')
					    .remove()
					    .end()
					    .append('<option value="0">---Select---</option>')
					    .val('0');
						$.each(response.generalMasterModelList, function (i, item) {
						    $('#parentId').append($('<option>', { 
						        value: item.id,
						        text : item.name 
						    }));
						});
						$('#parentId').val(parentId);
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
		getZoneASPerCorporate();
		getUserAsCorporate();
	}
	function saveOrUpdate() {
		form_data = new FormData();
		if($('#idForUpdate').val() > "0" && previousPassword != $('#pswd').val()){
			firstLogin = "Y";
		}else if(firstLogin == undefined){
			firstLogin="N";
		}else if(firstLogin == ""){
			firstLogin="N";
		}
		form_data.append("idForUpdate", $('#idForUpdate').val());
		form_data.append("corporateId.id", $('#corporateId').val());
		if(pageFor == "Client"){
			form_data.append("parentId", $('#parentId').val());
			form_data.append("authTypeAdmin", sIsAdmin);
			form_data.append("authTypeClient", sIsClient);
		}else{
			form_data.append("authTypeAdmin", $('#authTypeAdmin').is(':checked')?"Y":"N");
			form_data.append("authTypeClient", $('#authTypeClient').is(':checked')?"Y":"N");
		}
		form_data.append("etsAllow", $('#etsAllow').is(':checked')?"Y":"N");
		form_data.append("userName", $('#userName').val());
		form_data.append("pswd", $('#pswd').val());
		form_data.append("firstLogin", firstLogin);
		form_data.append("name", $('#name').val());
		form_data.append("empCode", $('#empCode').val());
		form_data.append("priorityId.id", $('#priorityId').val());
		form_data.append("bkgAllow", $('#bkgAllow').val());
		form_data.append("status", $('#status').val());
		form_data.append("oAddress", $('#oAddress').val());
		form_data.append("hAddress", $('#hAddress').val());
		form_data.append("gender", $('#gender option:selected').val());
		form_data.append("division", $('#division').val());
		if($('#location option:selected').val() != "" && $('#location option:selected').val() != "0") form_data.append("location.id", $('#location option:selected').val());
		if($('#zone option:selected').val() != "" && $('#zone option:selected').val() != "0") form_data.append("zone.id", $('#zone option:selected').val());
		if($('#city option:selected').val() != "" && $('#city option:selected').val() != "0") form_data.append("city.id", $('#city option:selected').val());
		form_data.append("userId.id", loginUserId);
		form_data.append("pageFor", pageFor);
		saveContactDetailForm();
		saveOrUpdateRecord(form_data);
	}
	
	function AssignFormControls(response){
		$("#corporateId").val(response.autorizedUserModel.corporateId.id);
		$('#changePswdLabel').css("display",'block');
		$('#changePswdCheck').css("display",'block');
		$('#pswd').prop("readonly", true);
		$('#userName').val(response.autorizedUserModel.userName);
		$('#pswd').val(response.autorizedUserModel.pswd);
		response.autorizedUserModel.etsAllow == "Y"?$('#etsAllow').prop('checked', true):$('#etsAllow').prop('checked', false);
		firstLogin = response.autorizedUserModel.firstLogin;

		if(pageFor == "Client"){
			AssignAuthoriser(response.autorizedUserModel.corporateId.id,response.autorizedUserModel.parentId);
			previousPassword=response.autorizedUserModel.pswd;
			sIsAdmin = response.autorizedUserModel.authTypeAdmin;
			sIsClient = response.autorizedUserModel.authTypeClient;
		}else{
			response.autorizedUserModel.authTypeAdmin == "Y"?$('#authTypeAdmin').prop('checked', true):$('#authTypeAdmin').prop('checked', false);
			response.autorizedUserModel.authTypeClient == "Y"?$('#authTypeClient').prop('checked', true):$('#authTypeClient').prop('checked', false);
		}	
		$('#name').val(response.autorizedUserModel.name);
		$('#empCode').val(response.autorizedUserModel.empCode);
		$('#priorityId').val(response.autorizedUserModel.priorityId.id);
		$('#bkgAllow').val(response.autorizedUserModel.bkgAllow);
		$('#status').val(response.autorizedUserModel.status);
		$('#oAddress').val(response.autorizedUserModel.oAddress);
		$('#hAddress').val(response.autorizedUserModel.hAddress);
		$('#oAddress').val(response.autorizedUserModel.oAddress);
		$('#hAddress').val(response.autorizedUserModel.hAddress);
		$('#division').val(response.autorizedUserModel.division);
		responseObj_contactDetail = response.autorizedUserModel;
		resetContactDetails();
		setContactValues();
		responseObj_contactDetail=null;
		getZoneASPerCorporate();
		getLocationASPerCorporate();
		if(response.autorizedUserModel.zone != null) $('#zone').val(response.autorizedUserModel.zone.id);
		if(response.autorizedUserModel.location != null) $('#location').val(response.autorizedUserModel.location.id);
		if(response.autorizedUserModel.city != null) $('#city').val(response.autorizedUserModel.city.id);
		$('#gender').val(response.autorizedUserModel.gender);
	}
function localScriptWithRefresh(){}
	function formFillForEdit(formFillForEditId) {
		fetchExtraData = "&pageFor=" + pageFor;
		formFillRecord(formFillForEditId);
	}
	
	function enableDisablePassword(){
	  if($('#changePassword').prop('checked') == true){
			$('#pswd').prop("readonly", false);
			$('#pswd').val("");
	  }
	  else{
			$('#pswd').prop("readonly", true);
			$('#pswd').val(previousPassword);
	  }
	}
	
function getZoneASPerCorporate(){
	var corporateId = $("#corporateId").val();
	if(corporateId == "0") return;
	$.ajax({
		type : "POST",
		url:  ctx + "/getZoneASPerCorporate.html",
		async:false,
		data : "corporateId=" + corporateId,
		success : function(response) {
			if (response.status == "Success") {
				$("#zone").empty().append(
				'<option value="0">---Select---</option>');
				$.each(response.generalMasterModelList, function(index,val) {
					$("#zone").append('<option value="'+val.id+'">' + val.name+ '</option>');
					});
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

function getLocationASPerCorporate(){
	var corporateId = $("#corporateId").val();
	var zone = $("#zone").val();
	if(zone == "0" || corporateId == "0") return;
	$.ajax({
		type : "POST",
		url:  ctx + "/getLocationASPerCorporate.html",
		data : "corporateId=" + corporateId + "&zoneId="+zone,
		async:false,
		success : function(response) {
			if (response.status == "Success") {
				mCity = new Object();
				$("#location").empty().append(
				'<option value="0">---Select---</option>');
				/*Fill Locations*/
				$.each(response.locationMasterModelList, function(index,val) {
					$("#location").append('<option value="'+val.id+'">' + val.location+ '</option>');
					if(val.city != "" && val.city != null){ 
						mCity[val.city.id] = val.city.name;
					}
				});
				/*Fill City*/
				$("#city").empty().append('<option value="0">---Select---</option>');
				for (var key in mCity) {
					$("#city").append('<option value="'+key+'">' + mCity[key] + '</option>');
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

function getUserAsCorporate(){
	var corporateId = $("#corporateId").val();
	if(corporateId == 0 || corporateId == "") return;
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/getUserAsCorporate.html",  
		data: "corporateId="+corporateId + "&pageFor=" + pageFor,  
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