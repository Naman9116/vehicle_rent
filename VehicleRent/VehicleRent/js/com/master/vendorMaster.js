	saveUpdateUrl = ctx + "/saveOrUpdateVendor.html";
	formFillUrl  = ctx + "/formFillForEditVendor.html";
	deleteUrl  = ctx + "/deleteVendor.html";
	var isValidationError = false;
	var vendorId;
	var checkedStatus;
	var idForUpdateChauffeur;
	var dlAuthorityAddr;
	var dob;
	var badgeNo;
	var bloodGrp;
	var qualification;
	var experience;
	var idMark;
	var refName;
	var refMobileNo;
	var refAddress;

	function localScriptWithRefresh(){
		document.getElementById("photoImage").src=contextPath + '/img/Vendor/' + $('#name').val();
	}

	function resetData(){
		$('#vendorForm').trigger("reset");
		$("#chauffeurDetails").hide();
		document.getElementById("addButton").disabled=false;
	  	document.getElementById("updateButton").disabled=true;
		document.getElementById("photoImage").src = contextPath + '/img/blankImage.jpg';
		$('#error').hide('slow');
	    $('#info').hide('slow');
	}
	
	var dataTableVendor;
	function saveOrUpdate() {
		validateBookingDetails();	
    	if(!isValidationError){	 
			form_data_Chauffeur = new FormData();
			form_data = new FormData();
			form_data.append("name", $('#name').val());
			form_data.append("pan", $('#pan').val());
			form_data.append("sTaxNo", $('#sTaxNo').val());
			form_data.append("tan", $('#tan').val());
			form_data.append("lstno", $('#lstno').val());
			form_data.append("agDate", $('#agDate').val());
			form_data.append("email", $('#email').val());
			form_data.append("helpLineNo", $('#helpLineNo').val());
			form_data.append("orgTypeId.id", $('#orgTypeId').val());
			form_data.append("dutyAllow", $('#dutyAllow').val());
			form_data.append("status", $('#status').val());
			form_data.append("bankAcName", $('#bankAcName').val());
			form_data.append("bankAcNo", $('#bankAcNo').val());
			form_data.append("bankName", $('#bankName').val());
			form_data.append("bankNEFT", $('#bankNEFT').val());
			form_data.append("bankRTGS", $('#bankRTGS').val());
			form_data.append("contPerson", $('#contPerson').val());
			form_data.append("contPersonMobile", $('#contPersonMobile').val());
			form_data.append("perAddress", $('#perAddress').val());
			form_data.append("perAddressPIN", $('#perAddressPIN').val());
			form_data.append("temAddress", $('#temAddress').val());
			form_data.append("temAddressPIN", $('#temAddressPIN').val());
			form_data.append("isChauffeur", $('#isVendorChauffeur').prop('checked')?"Y":"N");
			form_data.append("userId.id", loginUserId);
			if($("#photoFile").prop("files")[0] != undefined)
				form_data.append("photo", $("#photoFile").prop("files")[0]);
				form_data.append("idForUpdate", $('#idForUpdate').val());
			
			if($('#isVendorChauffeur').prop('checked') == true){
				checkedStatus = "checked";
				form_data_Chauffeur.append("name", $('#name').val());
				form_data_Chauffeur.append("mobileNo", $('#contPersonMobile').val());
				form_data_Chauffeur.append("drivingLicence", $('#dlNo').val());
				form_data_Chauffeur.append("dlIssue", $('#dlIssue').val());
				form_data_Chauffeur.append("dlValidUpto", $('#validUpto').val());
				form_data_Chauffeur.append("dlAuthority", $('#licAuthority').val());
				form_data_Chauffeur.append("doj", $('#agDate').val());
				form_data_Chauffeur.append("dutyAllow", $('#dutyAllow').val());
				form_data_Chauffeur.append("status", $('#status').val());
				form_data_Chauffeur.append("presentAddress", $('#temAddress').val());
				form_data_Chauffeur.append("presentAddressPIN", $('#temAddressPIN').val());
				form_data_Chauffeur.append("peramentAddress", $('#perAddress').val());
				form_data_Chauffeur.append("peramentAddressPIN", $('#perAddressPIN').val());
				if($("#photoFile").prop("files")[0] != undefined)
					form_data_Chauffeur.append("photo", $("#photoFile").prop("files")[0]);
				form_data_Chauffeur.append("userId.id", loginUserId);
				
				if (idForUpdateChauffeur > 0){
					form_data_Chauffeur.append("idForUpdate", idForUpdateChauffeur);
					form_data_Chauffeur.append("dlAuthorityAddr", dlAuthorityAddr);
					form_data_Chauffeur.append("dob",dob);
					form_data_Chauffeur.append("badgeNo", badgeNo);
					form_data_Chauffeur.append("bloodGrp", bloodGrp);
					form_data_Chauffeur.append("qualification", qualification);
					form_data_Chauffeur.append("experience", experience);
					form_data_Chauffeur.append("idMark", idMark);
					form_data_Chauffeur.append("refName", refName);
					form_data_Chauffeur.append("refMobileNo", refMobileNo);
					form_data_Chauffeur.append("refAddress", refAddress);
					form_data_Chauffeur.append("vendorId.id", vendorId);
				}
				else {
					form_data_Chauffeur.append("idForUpdate", "0");
					idForUpdate = 0;
				}
			}
			saveUpdateUrl = ctx + "/saveOrUpdateVendor.html";
			saveOrUpdateRecord(form_data);
			dataTableVendor = (serviceResponse.dataGrid).replace(/\\\"/g,"\"");
	
			if(checkedStatus == "checked"){
				if(idForUpdate == 0){
					form_data_Chauffeur.append("vendorId.id", serviceResponse.dataString2);
				}
				form_data_Chauffeur.append("stau", "V")
				saveUpdateUrl = ctx + "/saveOrUpdateChauffeur.html";
				saveOrUpdateRecord(form_data_Chauffeur);
				if(dataTableVendor != null ){
					$("#dataTable2").html(dataTableVendor);
					dataTableVendor=null;
				}
			}
    	}  
	}
	function AssignFormControls(response){
		if(response.vendorModel != null){
			
			$('#name').val(response.vendorModel.name);
			$('#pan').val(response.vendorModel.pan);
			$('#sTaxNo').val(response.vendorModel.sTaxNo);
			$('#tan').val(response.vendorModel.tan);
			$('#lstno').val(response.vendorModel.lstno);
			$('#agDate').val(getTimeStampTo_DDMMYYYY(response.vendorModel.agDate));
			$('#email').val(response.vendorModel.email);
			$('#helpLineNo').val(response.vendorModel.helpLineNo);
			$('#orgTypeId').val(response.vendorModel.orgTypeId.id);
			$('#dutyAllow').val(response.vendorModel.dutyAllow);
			$('#status').val(response.vendorModel.status);
			$('#bankAcName').val(response.vendorModel.bankAcName);
			$('#bankAcNo').val(response.vendorModel.bankAcNo);
			$('#bankName').val(response.vendorModel.bankName);
			$('#bankNEFT').val(response.vendorModel.bankNEFT);
			$('#bankRTGS').val(response.vendorModel.bankRTGS);
			$('#contPerson').val(response.vendorModel.contPerson);
			$('#contPersonMobile').val(response.vendorModel.contPersonMobile);
			$('#perAddress').val(response.vendorModel.perAddress);
			$('#perAddressPIN').val(response.vendorModel.perAddressPIN);
			$('#temAddress').val(response.vendorModel.temAddress);
			$('#temAddressPIN').val(response.vendorModel.temAddressPIN);
			response.vendorModel.isChauffeur == "Y"?$('#isVendorChauffeur').prop('checked',true):$('#isVendorChauffeur').prop('checked',false);
			document.getElementById("photoImage").src=contextPath + '/img/Vendor/' + $('#name').val();
		}
		if($('#isVendorChauffeur').prop('checked')) {
			$("#chauffeurDetails").show();
		}else{
			$("#chauffeurDetails").hide();
		}
		if(response.chauffeurModel != null){
			$('#validUpto').val(getTimeStampTo_DDMMYYYY(response.chauffeurModel.dlValidUpto));
			$('#dlIssue').val(getTimeStampTo_DDMMYYYY(response.chauffeurModel.dlIssue));
			$('#dlNo').val(response.chauffeurModel.drivingLicence);
			$('#licAuthority').val(response.chauffeurModel.dlAuthority);
			idForUpdateChauffeur = response.chauffeurModel.id;
			if(idForUpdateChauffeur > 0){
				dlAuthorityAddr = response.chauffeurModel.dlAuthorityAddr;
				dob = getTimeStampTo_DDMMYYYY(response.chauffeurModel.dob);
				badgeNo = response.chauffeurModel.badgeNo;
				bloodGrp = 	response.chauffeurModel.bloodGrp;
				qualification = response.chauffeurModel.qualification;
				experience = response.chauffeurModel.experience;
				idMark = response.chauffeurModel.idMark;
				refName = response.chauffeurModel.refName;
				refMobileNo = response.chauffeurModel.refMobileNo;
				refAddress = response.chauffeurModel.refAddress;
				vendorId = response.chauffeurModel.vendorId.id;
			}
		}else{
			$('#validUpto').val('');
			$('#dlIssue').val('');
			$('#dlNo').val('');
			$('#licAuthority').val('');
			idForUpdateChauffeur='0';
		}	
	}
	
	function formFillForEdit(editId) {
		var formFillForEditId = editId.split('/');
		formFillRecord(formFillForEditId[0]);
		formFillRecord(formFillForEditId[0],formFillForEditId[1]);
	}  
 
	function viewRecord(editId){
		
		if(isAccessDenied("V")) return;
		isFromView = true;

		var formFillForEditId = editId.split('/');
		formFillRecord(formFillForEditId[0]);
		formFillRecord(formFillForEditId[0],formFillForEditId[1]);
		$("form :input").attr("disabled","disabled");
		isFromView = false;
	}
	
	function formFillRecord(formFillForEditId,formFillForEditMob) {
		if(!isFromView)
		if(isAccessDenied("E")) return;	
		var mobileData = formFillForEditMob || "";
		var dataURL = ctx + "/formFillForEditVendor.html";
		if(mobileData!=""){
			dataURL = ctx + "/formFillForEditChauffeurDetails.html";
			mobileData = "&formFillForEditMob=" +formFillForEditMob;
		}
		$.ajax({
			type : "POST",
			url : dataURL,
			data : "formFillForEditId=" + formFillForEditId +mobileData+ fetchExtraData,
			async: false,
			success : function(response) {
				if (response.status == "Success") {
					AssignFormControls(response);
				} 
				else {
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
			},
			error : function(e) {
				alert(thrownError);
			}
		});
		$("form :input").prop("disabled",false);
		document.getElementById("addButton").disabled = true;
		document.getElementById("updateButton").disabled = false;
		$('#idForUpdate').val(formFillForEditId);
	}
	
	function showHide(){
		if($('#isVendorChauffeur').prop('checked') == true){
			$("#chauffeurDetails").show();
			}
		else{
			$("#chauffeurDetails").hide();
		} 
	}
		    
	function validateBookingDetails(){	    	    
		isValidationError = false;
		var validationErrorHtml = '<ol>';
		if($('#isVendorChauffeur').prop('checked') == true){
		var validUpto = $('#validUpto').val();
		if(validUpto == null || validUpto == ""){
			validationErrorHtml = validationErrorHtml + "<li>  D/L Valid Upto Date can not be Blank.</li>";
			isValidationError = true;
		}
		var dlIssue = $('#dlIssue').val();
		if(dlIssue == null || dlIssue == ""){
			validationErrorHtml = validationErrorHtml + "<li> D/L Issuse Date can not be Empty.</li>";
			isValidationError = true;
		}
		var dlNo = $('#dlNo').val();
		if(dlNo == null || dlNo == "" ){
			validationErrorHtml = validationErrorHtml + "<li> D/L No. can not be Blank.</li>";
			isValidationError = true;
		}
		var licAuthority = $('#licAuthority').val();
		if(licAuthority == null || licAuthority == "" ){
			validationErrorHtml = validationErrorHtml + "<li> Licence Authority can not be Blank.</li>";
			isValidationError = true;
		}
		
		var mobileNo = $('#contPersonMobile').val();
		if(mobileNo == null || mobileNo == ""){
			validationErrorHtml = validationErrorHtml + "<li> Mobile Number can not be Blank.</li>";
			isValidationError = true;
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
	