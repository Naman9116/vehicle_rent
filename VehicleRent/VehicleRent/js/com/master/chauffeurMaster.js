	saveUpdateUrl = ctx + "/saveOrUpdateChauffeur.html";
	formFillUrl  = ctx + "/formFillForEditChauffeur.html";
	deleteUrl  = ctx + "/deleteChauffeur.html";
	var previousPassword="",firstLogin="";

	function localScriptWithRefresh(){}

	function resetData(){
		refreshData();
		$('#chauffeurForm').trigger("reset");
		document.getElementById("addButton").disabled=false;
	  	document.getElementById("updateButton").disabled=true;
		document.getElementById("photoImage").src = contextPath + '/img/blankImage.jpg';
		$('#error').hide('slow');
	    $('#info').hide('slow');
	}
	
	function saveOrUpdate() {
		form_data = new FormData();
		if($('#idForUpdate').val() > "0" && previousPassword != $('#pswd').val()){
			firstLogin = "Y";
		}
		form_data.append("userName", $('#userName').val());
		form_data.append("pswd", $('#pswd').val());
		form_data.append("firstLogin", firstLogin);
		
		form_data.append("vendorId.id", $('#vendorId').val());
		form_data.append("name", $('#name').val());
		form_data.append("mobileNo", $('#mobileNo').val());
		form_data.append("drivingLicence", $('#drivingLicence').val());
		form_data.append("dlIssue", $('#dlIssue').val());
		form_data.append("dlValidUpto", $('#dlValidUpto').val());
		form_data.append("dlAuthority", $('#dlAuthority').val());
		form_data.append("dlAuthorityAddr", $('#dlAuthorityAddr').val());
		form_data.append("dob", $('#dob').val());
		form_data.append("doj", $('#doj').val());
		form_data.append("badgeNo", $('#badgeNo').val());
		form_data.append("bloodGrp", $('#bloodGrp').val());
		form_data.append("dutyAllow", $('#dutyAllow').val());
		form_data.append("status", $('#status').val());
		form_data.append("qualification", $('#qualification').val());
		form_data.append("experience", $('#experience').val());
		form_data.append("idMark", $('#idMark').val());
		form_data.append("presentAddress", $('#presentAddress').val());
		form_data.append("presentAddressPIN", $('#presentAddressPIN').val());
		form_data.append("peramentAddress", $('#peramentAddress').val());
		form_data.append("peramentAddressPIN", $('#peramentAddressPIN').val());
		form_data.append("refName", $('#refName').val());
		form_data.append("refMobileNo", $('#refMobileNo').val());
		form_data.append("refAddress", $('#refAddress').val());
		form_data.append("userId.id", loginUserId);
		
/*		if($("#photoFile").prop("files")[0] != undefined)
			form_data.append("photo", $("#photoFile").prop("files")[0]);
*/		if($("#photoImage").attr('src') != ""){
			form_data.append("sBase64Image", $("#photoImage").attr('src').replace(',', '~'));
		}
		form_data.append("idForUpdate", $('#idForUpdate').val());
		form_data.append("stau", "C");
		saveOrUpdateRecord(form_data);
	}  

	function AssignFormControls(response){
		$('#changePswdLabel').css("display",'block');
		$('#changePswdCheck').css("display",'block');
		$('#pswd').prop("readonly", true);
		$('#userName').val(response.chauffeurModel.userName);
		$('#pswd').val(response.chauffeurModel.pswd);
		firstLogin = response.chauffeurModel.firstLogin;
		
		$('#vendorId').val(response.chauffeurModel.vendorId.id);
		$('#name').val(response.chauffeurModel.name);
		$('#mobileNo').val(response.chauffeurModel.mobileNo);
		$('#drivingLicence').val(response.chauffeurModel.drivingLicence);
		$('#dlIssue').val(getTimeStampTo_DDMMYYYY(response.chauffeurModel.dlIssue));
		$('#dlValidUpto').val(getTimeStampTo_DDMMYYYY(response.chauffeurModel.dlValidUpto));
		$('#dlAuthority').val(response.chauffeurModel.dlAuthority);
		$('#dlAuthorityAddr').val(response.chauffeurModel.dlAuthorityAddr);
		$('#dob').val(getTimeStampTo_DDMMYYYY(response.chauffeurModel.dob));
		$('#doj').val(getTimeStampTo_DDMMYYYY(response.chauffeurModel.doj));
		$('#badgeNo').val(response.chauffeurModel.badgeNo);
		$('#bloodGrp').val(response.chauffeurModel.bloodGrp);
		$('#dutyAllow').val(response.chauffeurModel.dutyAllow);
		$('#status').val(response.chauffeurModel.status);
		$('#qualification').val(response.chauffeurModel.qualification);
		$('#experience').val(response.chauffeurModel.experience);
		$('#idMark').val(response.chauffeurModel.idMark);
		$('#presentAddress').val(response.chauffeurModel.presentAddress);
		$('#presentAddressPIN').val(response.chauffeurModel.presentAddressPIN);
		$('#peramentAddress').val(response.chauffeurModel.peramentAddress);
		$('#peramentAddressPIN').val(response.chauffeurModel.peramentAddressPIN);
		$('#refName').val(response.chauffeurModel.refName);
		$('#refMobileNo').val(response.chauffeurModel.refMobileNo);
		$('#refAddress').val(response.chauffeurModel.refAddress);
		document.getElementById("photoImage").src= '/Images/Chauffeur/' + response.chauffeurModel.id;
	}

	function formFillForEdit(formFillForEditId) {
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