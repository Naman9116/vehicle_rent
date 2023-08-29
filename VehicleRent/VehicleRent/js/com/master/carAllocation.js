	saveUpdateUrl = ctx + "/saveOrUpdateCarAllocation.html";
	formFillUrl  = ctx + "/formFillForEditCarAllocation.html";
	deleteUrl  = ctx + "/deleteCarAllocation.html";

	var isValidationError = false;
	function localScriptWithRefresh(){}

	function resetData(){
		$('#carAllocationForm').trigger("reset");
		document.getElementById("addButton").disabled=false;
	  	document.getElementById("updateButton").disabled=true;
		$('#error').hide('slow');
	    $('#info').hide('slow');
	}
	
	function saveOrUpdate() {
		validateCarAllocation();
		if(!isValidationError){
			var vendorId=$('#vendorName').val();
			var regNo= ($('#idForUpdate').val() == 0 ? $('#carNumber').val() : $('#carNumberText').val());
			var carOnwerType=$('#carOwnerType').val();
			var doa=$('#doa').val();
			var chauffeur = $("#chauffeurId").val();
			var paymentTraffic=$('#paymentTraffic').val();
			var compShare=$('#compShare').val();
			var vendorShare=$('#vendorShare').val();
			var dutyAllow=$('#dutyAllow').val();
			var carStatus=$('#carStatus').val();
			var dod=$('#dod').val();
			var reason=$('#reason').val();
			var idForUpdate =  $('#idForUpdate').val()
			
		  	$.ajax({  
		  		 type: "POST",  	
				    url:  ctx + "/saveOrUpdateCarAllocation.html",  
				    data: "vendorId.id="+vendorId+"&carDetailModelId.registrationNo="+regNo+"&carOwnerType.id=" + carOnwerType
				    	  +"&dateOfAllotment="+doa+"&paymentTraffic.id="+paymentTraffic+"&companyShare="+compShare+"&chauffeurId.id="+chauffeur
				    	  +"&vendorShare="+vendorShare+"&dutyAllow="+dutyAllow +"&carStatus="+carStatus+"&dateOfDeallocation="+dod
				    	  +"&reason="+reason+"&userId="+loginUserId +"&idForUpdate="+idForUpdate,  
				    success: function(response){
				    	if(response.status == "Success"){
					    	$('#info').html(response.result);
					    	$('#dataTable2').html(response.dataGrid);
					    	resetData();
					    	window.location.reload(true);
						    $('#error').hide('slow');
						    $('#info').show('slow');
						    addClassForColor();
						}
						else if(response.status == "Errors"){
					    	errorInfo = "";
					    	for(i =0 ; i < response.result.length ; i++){
								errorInfo += "<br>" + (i + 1) +". " + response.result[i].code;
							}
							$('#error').html("Please correct following errors: " + errorInfo);
							$('#info').hide('slow');
							$('#error').show('slow');
						}	   
				      	else{
							$('#error').html(response.result);
							$('#info').hide('slow');
							$('#error').show('slow');
						}
					},  
				    error: function(e){  
				      alert(e.status);
			           alert(e.responseText);
			           alert(thrownError);
				    }  
				}); 
			$("#carRegNumber").css("display","");
			$('#carRegNo').css("display","none");
			$('#idForUpdate').val("0"); 
		}
	}  

	function AssignFormControls(response){
		$('#vendorName').val(response.carAllocationModel.vendorId.id);
		$('#chauffeurId').append( '<option value='+response.carAllocationModel.chauffeurId.id+' >' +response.carAllocationModel.chauffeurId.name + '</option>' );
		$('#chauffeurId').val(response.carAllocationModel.chauffeurId.id);
		$('#carNumber').val(response.carAllocationModel.carDetailModelId.registrationNo);
		$('#carNumberText').val(response.carAllocationModel.carDetailModelId.registrationNo);
		$('#ownerType').val(response.carAllocationModel.carDetailModelId.ownType=="C"?"company":"vendor");
		$('#carOwnerType').val(response.carAllocationModel.carOwnerType.id);
		$('#doa').val(getTimeStampTo_DDMMYYYY(response.carAllocationModel.dateOfAllotment));
		$('#carMake').val(response.carAllocationModel.carDetailModelId.make.name);
		$('#carModel').val(response.carAllocationModel.carDetailModelId.model.name);
		$('#bodyColor').val(response.carAllocationModel.carDetailModelId.bodyColor.name);
		$('#paymentTraffic').val(response.carAllocationModel.paymentTraffic.id);
		$('#compShare').val(response.carAllocationModel.companyShare);
		$('#vendorShare').val(response.carAllocationModel.vendorShare);
		$('#dutyAllow').val(response.carAllocationModel.dutyAllow);
		$('#carStatus').val(response.carAllocationModel.carStatus);
		satuts ();
		$('#dod').val(response.carAllocationModel.dateOfDeallocation==null?null:(getTimeStampTo_DDMMYYYY(response.carAllocationModel.dateOfDeallocation)));
		$('#reason').val(response.carAllocationModel.reason);
		
	}

	function formFillForEdit(formFillForEditId) {
		formFillRecord(formFillForEditId);
		$("#carRegNumber").css("display","none");
		$('#carRegNo').css("display","");
		
	} 
	
	function satuts (){
			if($('#carStatus').val() == 'N')
				{
					$("#dod").removeAttr('disabled');
					$("#reason").removeAttr('disabled');
				}else {
					$("#dod").attr('disabled','disabled');
					$("#reason").attr('disabled','disabled');
				}
	}
	
	$(function(){
		$(".percentage").on('input',function(event){
			var changedValue = $(this).val();
			$(".percentage").val(100-(changedValue-0));
			$(this).val(changedValue);
		});
	})
	
	 function fillCarRelatedDetails(selectedRegNo){
	     form_data = new FormData();
	     form_data.append("selectedRegNo", selectedRegNo);
	     $.ajax({
		type : "POST",
		url:  ctx + '/fillCarRelatedDetails.html',
		processData: false,
		contentType: false,
		data : form_data,
		success : function(response) {
			if (response.status == "Success") {
				if (response.carDetailModel != null){
			    	$("#carMake").val(response.carDetailModel.make.name);
			    	$("#carModel").val(response.carDetailModel.model.name);
			    	$("#bodyColor").val(response.carDetailModel.bodyColor.name);
			    	//cModelMap[response.carDetailModel.model.name] = response.carDetailModel.id;
			    	if(response.carDetailModel.ownType == 'V')
			    	    $("#ownerType").val("Vendor");
			    	else if(response.carDetailModel.ownType == 'C')
			    	    $("#ownerType").val("Company");				    	
				} 
				else{
					$("#carMake").val(" ");
			    	$("#carModel").val(" ");
			    	$("#bodyColor").val(" ");
			    	$("#ownerType").val(" ");
				}
				
			}else {
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

	function validateCarAllocation(){	
		isValidationError = false;
		var validationErrorHtml = '<ol>';
		var vendorName = $('#vendorName option:selected').text();
		if(vendorName == null || vendorName == "" || vendorName == "-- Select --"){
			validationErrorHtml = validationErrorHtml + "<li> Vendor Name can't be Blank.</li>";
			isValidationError = true;
		}
		if($('#idForUpdate').val() == 0){
			var carNumber = $('#carNumber option:selected').text();
			if(carNumber == null || carNumber == ""|| carNumber == "-- Select --"){
				validationErrorHtml = validationErrorHtml + "<li> Car Number can't be Blank.</li>";
				isValidationError = true;
			}
		}
		var carOwnerType = $('#carOwnerType option:selected').text();
		if(carOwnerType == null || carOwnerType == ""|| carOwnerType == "-- Select --"){
			validationErrorHtml = validationErrorHtml + "<li> Car Owner Type can't be Blank.</li>";
			isValidationError = true;
		}
		
		var chauffeurId = $('#chauffeurId option:selected').text();
		if(chauffeurId == null || chauffeurId == '' || chauffeurId == '-- Select --'){
			validationErrorHtml = validationErrorHtml + "<li> Chauffeur can't be Blank.</li>";
			isValidationError = true;
		}
		
		var compShare = $('#compShare').val();
		var vendorShare = $('#vendorShare').val();
		if( compShare < 0 || compShare >100 || vendorShare > 100 || vendorShare < 0){
			validationErrorHtml = validationErrorHtml + "<li> % Share of Vendor or Company should be lies between 0 to 100 .</li>";
			isValidationError = true;
		}
		
		var doa = $('#doa').val();
		if(doa == null || doa == ""){
			validationErrorHtml = validationErrorHtml + "<li> Date of Allotment can't be Blank.</li>";
			isValidationError = true;
		}
		var paymentTraffic = $('#paymentTraffic option:selected').text();
		if(paymentTraffic == null || paymentTraffic == ""|| paymentTraffic == "-- Select --"){
			validationErrorHtml = validationErrorHtml + "<li> Payment Traffic  can't be Blank.</li>";
			isValidationError = true;
		}
		var dutyAllow = $('#dutyAllow option:selected').text();
		if(dutyAllow == null || dutyAllow == ""|| dutyAllow == "-- Select --"){
			validationErrorHtml = validationErrorHtml + "<li> Duty Allow can't be Blank.</li>";
			isValidationError = true;
		}
		var carStatus = $("#carStatus option:selected").text();
		if(carStatus == null || carStatus == "" || carStatus == "InActive"){
			var dod = $("#dod").val();
			var reason = $("#reason").val();
			if(dod ==null || dod == ""){
			validationErrorHtml = validationErrorHtml + "<li> Date of Deallocation can't be Blank.</li>";
			isValidationError = true;
			}
			if(reason ==null || reason == ""){
				validationErrorHtml = validationErrorHtml + "<li> Reason  can not be Blank.</li>";
				isValidationError = true;
			}
		}
				
		validationErrorHtml = validationErrorHtml + "</ol>";
		if(isValidationError){
			$('#carAllocationError').html("Please correct following errors:<br>" + validationErrorHtml);
			$('#carAllocationInfo').hide();
			$('#carAllocationError').show();
		}else{
			$('#carAllocationError').html("");
			$('#carAllocationError').hide();
		}
	}
	
	