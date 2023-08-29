	saveUpdateUrl = ctx + "/saveOrUpdateCorporateTariff.html";
	formFillUrl  = ctx + "/formFillForEditTariffParameterMaster.html";
	deleteUrl  = ctx + "/deleteTariff.html";
	
	var branchID = null;
	var corpTariffList;
	var InputId = [];
	var InputVal = [];
	
	function changeState(){
		var operator_id = 1;
		  for (var i = 0; i < $('select#fuelHikeDt option').length; i++) {
		    if ($('select#fuelHikeDt option')[i].text == $("#fuelHikeDate").val()) {
		        alert("Fuel Hike Date is Already Present !!!");
		    }else{
				$("#idForUpdate").val("");
		    }
		}
	}
	
	function AssignCorporate(branchId,corporateId){
		branchID=branchId;
	  	$.ajax({  
		    type: "POST",
		    async: false,
		    url:  ctx + "/corporateBranchWise.html",  
		    data: "branchId="+branchId,  
			success : function(response) {
				if (response.status == "Success") {
					$('#dataTable2').html(response.dataGrid);
					$('#corporateId')
				    .find('option')
				    .remove()
				    .end()
				    .append('<option value="0">--- Select Corporate ---</option>')
				    .val('0');
					$.each(response.generalMasterModelList, function (i, item) {
					    $('#corporateId').append($('<option>', { 
					        value: item.id,
					        text : item.name 
					    }));
					});
					$('#corporateId').val(corporateId);
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
	
	function corporateTariff(corporateId){
		if(corporateId == 0 || branchID == 0 || branchID == null) return;
		$.ajax({  
		    type: "POST",
		    async: false,
		    url:  ctx + "/tariffCorporateWise.html",  
		    data: 'branchID='+branchID+'&corporateId='+corporateId, 
		    success : function(response) {
				if (response.status == "Success") {
					$('#dataTable2').html(response.dataGrid);
					$('#tariffInput').html(response.dataString1);
					$("#fuelHikeDt").empty().append('<option value="0">-- Select Fuel Hike Date --</option>');
					var latestDt;
					$.each(response.corporateTariffModelList, function (i, item) {
					    $("#fuelHikeDt").append('<option value="'+item.id+'">'+ getTimeStampTo_DDMMYYYY(item.fuelHikeDate)+'</option>');
					    latestDt = item.id;
					});
					$("#fuelHikeDt").val(latestDt);
					if(response.corporateTariffModel!=null){
						$.each(response.corporateTariffDetModelList, function (i, item) {
						   if(response.corporateTariffModel.id==item.corporateTariffModel.id){
								$("#addButton").hide();
								$("#updateButton").show();
						   }else{
							    $("#addButton").show();
							    $("#updateButton").hide();
						   }
		    			});
						formFillForEditCorporateTariff(response.corporateTariffModel.id);
						$('#gstin').val(response.corporateTariffModel.gstin);
						$('#fuelRate').val(response.corporateTariffModel.fuelRate);
						$('#currFuelRate').val(response.corporateTariffModel.currFuelRate);
					 	$('#fuelHikeDate').val(getTimeStampTo_DDMMYYYY(response.corporateTariffModel.fuelHikeDate));
				    }else{
						$("#addButton").removeAttr("disabled");
						$("#addButton").show();
					  	$("#updateButton").hide();
					 	$('#gstin').val("");
					 	$('#fuelRate').val("");
						$('#currFuelRate').val("");
						$('#fuelHikeDate').val(getTimeStampTo_DDMMYYYY(""));
					}
				} else{
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
			},  
		    error: function(e){  
		    }  
		});  
	}	
	
	function recordChange(contral){
		var sInputID = $(contral).attr('id');
		$('#'+sInputID).val();
		if(InputId.indexOf(sInputID) == -1){
    		InputId.push(sInputID);
    	    InputVal.push($('#'+sInputID).val());
	    }else{
	    	InputVal[InputId.indexOf(sInputID)]= $('#'+sInputID).val(); 
	    }
	}
	
	function processTariff(contral){
		var sInputID = $(contral).attr('id');
		var tId = sInputID.split("_");
		if(tId[1] == '201' || tId[1] == '204'){ /*201 - Outstation Per Km And 204 - Outstation Min Km*/
			var val201 = $('#' + tId[0] + "_201").val();
			var val204 = $('#' + tId[0] + "_204").val();
			var oMultiply = val201 * val204;
			if((oMultiply == 0 || isNaN(oMultiply))){
				oMultiply = "";
			}
			$('#' + tId[0] + "_260").val(oMultiply);
			$('#' + tId[0] + "_260").trigger("change");
			sInputID = $('#' + tId[0] + "_260").attr('id');
			if(InputId.indexOf(sInputID) == -1){
	    		InputId.push(sInputID);
	    	    InputVal.push($('#'+sInputID).val());
		    }else{
		    	InputVal[InputId.indexOf(sInputID)]= $('#'+sInputID).val(); 
		    }
		}
	}
	function saveOrUpdateCorporateTariff() {
		var controlsWithValue = [];
		console.log(InputId);
		var InputID;
		if ($('#idForUpdate').val() == "") {
			$('input').each(
					function() {
						InputID = $(this).attr('id');
						InputID.match("^TrfVal") ? controlsWithValue.push($(this).attr('id')+ "_" + $(this).val()) : "";
					});
		} else {
			if($("#fuelHikeDt").val() == '0'){
				alert('Please select fuel hike date from selection box !!!');
				return;
			}
			for (var j = 0; j < InputId.length; j++) {
				InputID = InputId[j];
				// getCorporateTariff($('#idForUpdate').val());
				if ($("#fuelHikeDt option:selected").text() != $("#fuelHikeDate").val())
				{
					InputID.match("^TrfVal") ? controlsWithValue.push($(this).attr('id')+ "_" + $(this).val()) : "";
				}else {
					var hiddenId = InputID.substring(6);
					InputID.match("^TrfVal") ? controlsWithValue.push(InputId[j]+ "_" + InputVal[j] + "_"+ $("#Hidden" + hiddenId).val()) : "";
				}
			}
		}
		form_data = new FormData();
		form_data.append("controlsWithValue", controlsWithValue);
		form_data.append("corporateId.id", $("#corporateId").val());
		form_data.append("branchId.id", $("#branchId").val());
		form_data.append("fuelRate",$("#fuelRate").val());
		form_data.append("currFuelRate",$("#currFuelRate").val());
		form_data.append("fuelHikeDate",$("#fuelHikeDate").val());
		form_data.append("userId.id",loginUserId);
		form_data.append("gstin", $('#gstin').val());
		form_data.append("idForUpdate", $('#idForUpdate').val());
		saveAddressDetailForm();
		saveOrUpdateRecord(form_data);
		$('#idForUpdate').val("");
		$("#fuelHikeDt").empty().append('<option value="0">-- Select Fuel Hike Date --</option>');
		InputId = [];
		InputVal = [];
	}  
	
	function viewCorporateTariff(formFillForEditId){
		viewRecord(formFillForEditId);
	}
	
	function AssignFormControls(response){
//		document.forms[0].reset();
		$('#branchId').val(response.corporateTariffModel.branchId.id);
		AssignCorporate(response.corporateTariffModel.branchId.id,response.corporateTariffModel.corporateId.id);
		/*$('#fuelRate').val(response.corporateTariffModel.fuelRate);
		$('#currFuelRate').val(response.corporateTariffModel.currFuelRate);
		$('#fuelHikeDate').val(getTimeStampTo_DDMMYYYY(response.corporateTariffModel.fuelHikeDate));*/
		$('input').each(function() {
			var InputID = $(this).attr('id');
			if(InputID.match("TrfVal")){
    			$.each(response.corporateTariffDetModelList, function (i, item) {
    				var tariffControlId = "TrfVal" + item.carCatId.id + "_" + item.tariffId.id ;
    				InputID == tariffControlId?$("#"+InputID).val(item.tariffValue) :"";
    				InputID == tariffControlId?$("#Hidden"+item.carCatId.id + "_" + item.tariffId.id).val(item.id) :"0";
    			});
			}
		});
	  	responseObj_addressDetail = response.corporateTariffModel;
		resetAddressDetails();
		setAddressValues();
	}
	function formFillForEditCorporateTariff(formFillForEditId) {
		formFillRecord(formFillForEditId);
	}  
	
	function deleteCorporateTariff(idForDelete) {
		deleteRecord(idForDelete);
	}  
	
	function getCorporateTariff(corporateId){
		$.ajax({  
		    type: "POST",
		    async: false,
		    url:  ctx + "/getCorporateTariff.html",  
		    data: 'corporateId='+corporateId, 
		    success : function(response) {
				if (response.status == "Success") {
						fuelHikeDate = getTimeStampTo_DDMMYYYY(response.corporateTariffModel.fuelHikeDate);
				}
		    }
	    });
	}
	
	function corporateFuelHikeDate(corporateId){
		InputId =[];
		InputVal = [];
		$.ajax({  
		    type: "POST",
		    async: false,
		    url:  ctx + "/getCorporateFuelHikeDate.html",  
		    data: 'corporateId='+corporateId+'&branchId='+branchID, 
		    success : function(response) {
				if (response.status == "Success") {
					/*$("#fuelHikeDt").empty().append('<option value="0">-- Select Fuel Hike Date --</option>');*/
					$("#fuelHikeDt").find('option').remove().end();
					if(response.corporateTariffModelList != null){
						var dateId;
						$.each(response.corporateTariffModelList,function(index,val){
							 $("#fuelHikeDt").append('<option value="'+val.id+'">'+ getTimeStampTo_DDMMYYYY(val.fuelHikeDate)+'</option>');
							 if(index == 0)  dateId = val.id;
						});
						corporateTariffAsPerDate(dateId);
					}else{
						corporateTariff(corporateId);
					}
					$('input').change(function(){
						 if(InputId.indexOf($(this).attr('id')) == -1){
							InputId.push($(this).attr('id'));
						    InputVal.push($(this).val());
						 }else{
							InputVal[InputId.indexOf($(this).attr('id'))]= $(this).val(); 
						 }
				    });
				}
		    }
	    });
	}
	
	function corporateTariffAsPerDate(corpTariffDateId){
		InputId =[];
		InputVal = [];
		$.ajax({  
		    type: "POST",
		    async: false,
		    url:  ctx + "/corporateTariffAsPerDate.html",  
		    data: 'corpTariffDateId='+corpTariffDateId, 
		    success : function(response) {
				if (response.status == "Success") {
					$('#dataTable2').html(response.dataGrid);
					$('#tariffInput').html(response.dataString1);
					if(response.corporateTariffModel!=null){
						$.each(response.corporateTariffDetModelList, function (i, item) {
					   if(response.corporateTariffModel.id==item.corporateTariffModel.id){
							$("#addButton").hide();
							$("#updateButton").show();
					   }
					   else{
						   $("#addButton").show();
							$("#updateButton").hide();}
		    			});
						formFillForEditCorporateTariff(response.corporateTariffModel.id);
						$('#gstin').val(response.corporateTariffModel.gstin);
						$('#fuelRate').val(response.corporateTariffModel.fuelRate);
						$('#currFuelRate').val(response.corporateTariffModel.currFuelRate);
					 	$('#fuelHikeDate').val(getTimeStampTo_DDMMYYYY(response.corporateTariffModel.fuelHikeDate));
					 	$('input').change(function(){
							 if(InputId.indexOf($(this).attr('id')) == -1){
								InputId.push($(this).attr('id'));
							    InputVal.push($(this).val());
							 }else{
								InputVal[InputId.indexOf($(this).attr('id'))]= $(this).val(); 
							 }
					    });
				    }else{
						$("#addButton").removeAttr("disabled");
						$("#addButton").show();
					  	$("#updateButton").hide();
					 	$('#gstin').val("");
					 	$('#fuelRate').val("");
						$('#currFuelRate').val("");
						$('#fuelHikeDate').val(getTimeStampTo_DDMMYYYY(""));
					}
				} else{
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
			},  
		    error: function(e){  
		    }  
		});  
	}	
	