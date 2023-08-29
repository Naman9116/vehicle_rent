	formFillUrl  = ctx + "/formFillForEditCorporate.html";
	saveUpdateUrl = ctx + "/saveOrUpdateCorporate.html";
	deleteUrl  = ctx + "/deleteCorporate.html";
	
	var taxid = [];
	var taxOldValues = [];
	var taxIdForUpdate = [];
	var taxationId = [];
	var saveUpdate=[];
	var taxEfDate = [];
	var oldEffDate;
	
	//function localScriptWithRefresh(){}
	function resetData(){
		refreshData();
		$('#corporateForm').trigger("reset");
		$("#billingCycle").val(186);
	    $('select.SlectBox')[0].sumo.unSelectAll();
		resetContactDetails();
		resetAddressDetails();
		responseObj_addressDetail=null;
		responseObj_contactDetail=null;
		document.getElementById("addButton").disabled = false;
		document.getElementById("updateButton").disabled = true;
		$('#idForUpdate').val('');
		$('#error').hide();
		$('#info').hide();
	}
	function changeBranchState(element){
	    if(element.checked){
	    	document.getElementById("branId").value = 0;
	    	document.getElementById("branId").disabled = true;	
	    }else{
	    	document.getElementById("branId").disabled = false;    
	    }
	}
	
	function getBranchList(companyId,branchId){
		$.ajax({
			type : "POST",
			url:  ctx + "/getBranchCorporate.html",
			data : "companyId=" + companyId,
			success : function(response) {
				if (response.status == "Success") {
					$('#branId')
				    .find('option')
				    .remove()
				    .end()
				    .append('<option value="0">---Select---</option>')
				    .val('0');
					$.each(response.generalMasterModelList, function (i, item) {
					    $('#branId').append($('<option>', { 
					        value: item.id,
					        text : item.name 
					    }));
					});
					$('#branId').val(branchId);
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
	
	function saveOrUpdateCorporate() {
		sTariff = "";
		$('#tariffs :selected').each(function(){
			sTariff += $(this).val() + ",";
		});
		sTariff = sTariff.substring(0,sTariff.length - 1);
		form_data = new FormData();
		form_data.append("idForUpdate", $('#idForUpdate').val());
		form_data.append("name", $('#name').val());
		form_data.append("agreementDt", $('#agreementDt').val());
		form_data.append("expiryDt", $('#expiryDt').val());
		form_data.append("sTaxCalBill", $('#sTaxCalBill').is(':checked')?"Y":"N");
		form_data.append("sTaxCalFuel", $('#sTaxCalFuel').is(':checked')?"Y":"N");
		form_data.append("sTaxCalPark", $('#sTaxCalPark').is(':checked')?"Y":"N");
		form_data.append("sDiscCalBill", $('#sDiscCalBill').is(':checked')?"Y":"N");
		form_data.append("sDiscCalKm", $('#sDiscCalKm').is(':checked')?"Y":"N");
		form_data.append("sDiscCalHrs", $('#sDiscCalHrs').is(':checked')?"Y":"N");
		form_data.append("vatOnPark", $('#vatOnPark').is(':checked')?"Y":"N");
		form_data.append("crCycle", $('#crCycle').val());
		form_data.append("compId.id", $('#compId').val());
		form_data.append("branId", $('#branId').val());
		form_data.append("penIndia", $('#penIndia').is(':checked')?"Y":"N");
		form_data.append("billingCycle.id", $('#billingCycle').val());
		form_data.append("bookAllow", $('#bookAllow').val());
		form_data.append("status", $('#status').val());
		form_data.append("userId.id", loginUserId);
		form_data.append("tariffs",sTariff);
		form_data.append("minRoundSlab",$('#minRoundSlab').val());
		form_data.append("roundedOff", $('#roundedOff').is(':checked')?"Y":"N");
		form_data.append("isPassengerInfo", $('#passengerInfo').is(':checked')?"Y":"N");
		var i = 0;
		var isChanged = false;
		$(".tax").each(function(){
			var taxId = $(this).attr('id');
			var taxMasterId = taxId.substring(4,taxId.length);

			if ($('#idForUpdate').val() == "0" || $('#idForUpdate').val() == ""){
				form_data.append("id",taxMasterId);
				form_data.append("taxVal",$("#"+taxId).val() == ''?"0":$("#"+taxId).val());
				form_data.append("currentDate",$("#discountId").val());
				form_data.append("idForUpdateTax","0");
				form_data.append("saveUpdate","S");
			}else{
				var found = false;
				for(var ele = 0; ele < taxIdForUpdate.length; ele++){
					if(taxMasterId == taxationId[ele]){
						found = true;
						if(taxOldValues[ele] != $("#"+taxId).val()){
							isChanged = true;
							form_data.append("id",taxMasterId);
							form_data.append("taxVal",$("#"+taxId).val() == ''?"0":$("#"+taxId).val());
							form_data.append("currentDate",$("#discountId").val());
							if(daysBetween(taxEfDate[ele],$("#discountId").val()) == 0){
								form_data.append("saveUpdate","U");
							}else{
								form_data.append("saveUpdate","S");
							}
							form_data.append("idForUpdateTax",taxIdForUpdate[ele]);
						}
					}
				}
				if(found == false){
					isChanged = true;
					form_data.append("id",taxMasterId);
					form_data.append("taxVal",$("#"+taxId).val() == ''?"0":$("#"+taxId).val());
					form_data.append("currentDate",$("#discountId").val());
					form_data.append("saveUpdate","S");
					form_data.append("idForUpdateTax","0");
				}
			}
		});
		if(isChanged == true)
			form_data.append("valueChanged","changed");
		else 
			form_data.append("valueChanged","noChanged");
		
		saveContactDetailForm();
		saveAddressDetailForm();
		saveOrUpdateRecord(form_data);
	}
	
	function viewCorporate(formFillForEditId){
		viewRecord(formFillForEditId);
	}

	function deleteCorporate(formFillForEditId){
		deleteRecord(formFillForEditId);
	}
	function AssignFormControls(response){
		taxIdForUpdate=[];
		taxOldValues=[];
		taxid=[];
		taxEfDate = [];
		$('#name').val(response.corporateModel.name);
		$('#agreementDt').val(getTimeStampTo_DDMMYYYY(response.corporateModel.agreementDt));
		$('#expiryDt').val(getTimeStampTo_DDMMYYYY(response.corporateModel.expiryDt));
		
		$(".tax").each(function(){
			var taxIds = $(this).attr('id');
			taxid.push(taxIds);
		});
		var i=0;
		$.each(response.corporateTaxDetModelList,function(index,val){
			if($("#tax_"+val.taxationModelId).length > 0){
				$("#tax_"+val.taxationModelId).val(val.taxVal);
				taxIdForUpdate.push(val.id);
				taxationId.push(val.taxationModelId);
				taxOldValues.push(val.taxVal);
				taxEfDate.push(getTimeStampTo_DDMMYYYY(val.insDate));
				if(daysBetween($("#discountId").val(),getTimeStampTo_DDMMYYYY(val.insDate)) > 0){
					$("#discountId").val(getTimeStampTo_DDMMYYYY(val.insDate));
					oldEffDate = getTimeStampTo_DDMMYYYY(val.insDate);
				}
			}
		});

		response.corporateModel.sTaxCalBill == "Y"?$('#sTaxCalBill').prop('checked', true):$('#sTaxCalBill').prop('checked', false);
		response.corporateModel.sTaxCalFuel == "Y"?$('#sTaxCalFuel').prop('checked', true):$('#sTaxCalFuel').prop('checked', false);
		response.corporateModel.sTaxCalPark == "Y"?$('#sTaxCalPark').prop('checked', true):$('#sTaxCalPark').prop('checked', false);
		response.corporateModel.sDiscCalBill == "Y"?$('#sDiscCalBill').prop('checked', true):$('#sDiscCalBill').prop('checked', false);
		response.corporateModel.sDiscCalKm == "Y"?$('#sDiscCalKm').prop('checked', true):$('#sDiscCalKm').prop('checked', false);
		response.corporateModel.sDiscCalHrs == "Y"?$('#sDiscCalHrs').prop('checked', true):$('#sDiscCalHrs').prop('checked', false);
		response.corporateModel.vatOnPark == "Y"?$('#vatOnPark').prop('checked', true):$('#vatOnPark').prop('checked', false);
		response.corporateModel.roundedOff == "Y"?$('#roundedOff').prop('checked', true):$('#roundedOff').prop('checked', false);
		response.corporateModel.isPassengerInfo == "Y"?$('#passengerInfo').prop('checked', true):$('#passengerInfo').prop('checked', false);
		$('#bookAllow').val(response.corporateModel.bookAllow);
		$('#crCycle').val(response.corporateModel.crCycle);
		$('#compId').val(response.corporateModel.compId.id);
		getBranchList(response.corporateModel.compId.id,response.corporateModel.branId);
		response.corporateModel.penIndia == "Y"?$('#penIndia').prop('checked', true):$('#penIndia').prop('checked', false);
		$('#billingCycle').val(response.corporateModel.billingCycle.id);
		$('#minRoundSlab').val(response.corporateModel.minRoundSlab);

		$('select.SlectBox')[0].sumo.unSelectAll();
		if(response.corporateModel.tariffs != null){
			sTariff = response.corporateModel.tariffs.split(",");
			var opts = document.getElementById("tariffs").options;
			var tarifIndex = 0;
			for(var i = 0; i < opts.length; i++) {
			    if(opts[i].value == sTariff[tarifIndex]) {
					$('select.SlectBox')[0].sumo.selectItem(i);
					tarifIndex++;
			    }
			}
		}		

		response.corporateModel.penIndia == "Y"?document.getElementById("branId").disabled = true:document.getElementById("branId").disabled = false;
	  	responseObj_contactDetail = response.corporateModel;
	  	responseObj_addressDetail = response.corporateModel;
		resetContactDetails();
		resetAddressDetails();

		setAddressValues();
		setContactValues();

		responseObj_addressDetail=null;
		responseObj_contactDetail=null;
	}
	function formFillForEditCorporate(formFillForEditId) {
		formFillRecord(formFillForEditId);
	}

	function selectedTariff(){
	}

	
/*	function checkForSelect(){
		alert($('#tariffs option:').text());
		if($('#tariffs option').is(":selected")){
			if($('#tariffs option:selected').text().indexOf("Outstation") > -1){
				var i=0;
				$('#tariffs option').each(function(){
					if(($(this).text()).indexOf("Outstation") > -1){
						$('select.SlectBox')[0].sumo.unSelectItem(i);
					}	
					i = i + 1;
				});
			}
		}else{
			if($('#tariffs option:selected').text().indexOf("Outstation") > -1){
				var i=0;
				$('#tariffs option').each(function(){
					if(($(this).text()).indexOf("Outstation") > -1){
						$('select.SlectBox')[0].sumo.selectItem(i);
					}	
					i = i + 1;
				});
			}
		}
		
		
		
		$('#tariffs :selected').each(function(){
			if(($(this).text()).indexOf("Outstation") > -1){
				var i=0;
				$('#tariffs option').each(function(){
					if(($(this).text()).indexOf("Outstation") == 0){
						$('select.SlectBox')[0].sumo.selectItem(i);
					}	
					i = i + 1;
				});
			}
		});
	}*/