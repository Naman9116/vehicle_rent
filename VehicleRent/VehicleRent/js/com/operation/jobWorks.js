	formFillUrl  = ctx + "/formFillForEditBookingMaster.html";
	saveUpdateUrl = ctx + "/saveOrUpdateBookingMaster.html";
	deleteUrl  = ctx + "/deleteBookingMaster.html";
	fetchExtraData = "&formType=M";
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
	var isPassengerCheck="N";
	var i = 1;
	var bookingDetailList;
	function localScriptWithRefresh(){
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
		}else if(jobType == 'webBooking'){  
			$("#userTable thead tr th").each( function ( i ) {
	    		var tableContent = new Array();
	    		var selectBoxField = "";
			    if(i==5 || i==12 || i==13 ){
					if(i==5){
					    selectBoxField = "corporateId";				
					}else if(i==12){
					    selectBoxField = "rentalType";
					}else if(i==13){
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
			        });
			    }
			});
		}
	}
	
	function resetData(){
		document.location.reload();
	}

	function searchBooking(){
		if(jobType == 'Current'){
			var sCriteria = 'bookingFromDate,bookingToDate,bookingMasterModel.corporateId.id,carModel.id,rentalType.id,branch.id ,outlet.id';
			var sValue = $("#bookingFromDate").val() + "," + 
					$("#bookingToDate").val() + "," + 
					$("#corporateId").val() + "," + 
					$("#carModel").val() + "," + 
					$("#rentalType").val()+","+
				    $("#branch").val()+","+
				    $("#outlet").val();
		} else if(jobType == 'Advance'){
			var sCriteria = 'bookingFromDate,bookingToDate,bookingMasterModel.corporateId.id,carModel.id,rentalType.id,branch.id,outlet.id,status';
			var sValue = $("#bookingFromDate").val() + "," +
					$("#bookingToDate").val() + "," + 
			 		$("#corporateId").val() + "," + 
			 		$("#carModel").val() + "," + 
					$("#rentalType").val()+","+
			        $("#branch").val()+","+
			        $("#outlet").val()+","+
			        $("#jobStatus").val();
		} else if(jobType == 'Status'){
			var sCriteria = 'bookingFromDate,bookingToDate,bookingMasterModel.corporateId.id,carModel.id,rentalType.id,branch.id,outlet.id,status,regNo';
			var sValue = $("#bookingFromDate").val() + "," + 
					$("#bookingToDate").val() + "," +
					$("#corporateId").val() + "," +
					$("#carModel").val() + "," + 
					$("#rentalType").val()+","+
					$("#branch").val()+","+
					$("#outlet").val()+","+
			        $("#jobStatus").val()+","+
			        $("#carRegNo").val();
		} else if(jobType == 'Cancelled'){
			var sCriteria = 'bookingFromDate,bookingToDate,bookingMasterModel.corporateId.id,carModel.id,rentalType.id,branch.id,outlet.id,status';
			var sValue = $("#bookingFromDate").val() + "," +
					$("#bookingToDate").val() + "," +
					$("#corporateId").val() + "," +
					$("#carModel").val() + "," + 
					$("#rentalType").val()+","+
			        $("#branch").val()+","+
			        $("#outlet").val()+","+
			        $("#jobStatus").val(); 			 		
		} else if(jobType == 'Expired'){
		    var sCriteria = 'bookingFromDate,bookingToDate,bookingMasterModel.corporateId.id,carModel.id,rentalType.id,branch.id,outlet.id,status';
		    var sValue = $("#bookingFromDate").val() + "," +
		    		$("#bookingToDate").val() + "," +
					$("#corporateId").val() + "," +
					$("#carModel").val() + "," + 
					$("#rentalType").val()+","+
			        $("#branch").val()+","+
			        $("#outlet").val()+","+
			        $("#jobStatus").val(); 	
		}else if(jobType == 'webBooking'){
			var sCriteria = 'bookingFromDate,bookingMasterModel.corporateId.id,carModel.id,rentalType.id,branch.id,outlet.id,status';
			var sValue = $("#bookingFromDate").val() + "," +
						$("#corporateId").val() + "," + 
				 		$("#carModel").val() + "," + 
						$("#rentalTypeModel").val()+","+
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
			url:  ctx + '/searchBooking.html',
			async:false,
			processData: false,
			contentType: false,
			data : form_data,
			success : function(response) {
				if (response.status == "Success") {
					bookingDetailList = response.bookingDetailList;
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

	function validateFromDate(){
		var mydate=new Date($("#bookingFromDate").val());
		var dtCurrent = new Date();
		if(jobType == 'Advance' && dtCurrent < mydate){
			alert("Pickup Date From Should Not Less Than Current Date!!!!");
		}
	}
	
	function validateToDate(){
		var dateFrom=new Date($("#bookingFromDate").val());
		var dateTo=new Date($("#bookingToDate").val());
		var dtCurrent = new Date();
		if(jobType == 'Advance' && dtCurrent <= dateTo){
			alert("Pickup Date To Should Not Less Than Current Date!!!!");
		}
		
		if(jobType == 'Advance' && dateFrom > dateTo){
			alert("Pickup Date To Should Not Greater Than PickUp Date From!!!!");
		}

	}
	
	function updateSaveBooking(){
		form_data = new FormData();
		var i = 0;
		if($('.edited').length<=0)
		    return false;
		$(".edited").each(function(){
			var bookingDetailId = $(this).attr('id');
			bookingDetailId = bookingDetailId.substring(11,bookingDetailId.length);			
			form_data.set("bookingDetailModel["+i+"].pickUpDate ",$(this).html());
			form_data.set("bookingDetailModel["+i+"].pickUpTime ",$("#pickupTime_"+bookingDetailId).html());
			form_data.set("bookingDetailModel["+i+"].startTime ",$("#startTime_"+bookingDetailId).html());
			form_data.set("bookingDetailModel["+i+"].id ",bookingDetailId);			
			form_data.set("bookingDetailModel["+i+"].bookingMasterModel.corporateId.id", editedFormDataValuesMap["bookingDetailModel["+bookingDetailId+"].bookingMasterModel.corporateId.id "]);
			//form_data.set("corporateId.name", $('#corporateId option:selected').text());
			form_data.set("bookingDetailModel["+i+"].bookingMasterModel.bookedBy.id", editedFormDataValuesMap["bookingDetailModel["+bookingDetailId+"].bookingMasterModel.bookedBy.id "]);
			//form_data.set("bookedBy.name", $('#bookedBy option:selected').text());
			form_data.set("bookingDetailModel["+i+"].bookingMasterModel.bookedFor", $('#bookedFor_'+bookingDetailId).html());
			form_data.set("bookingDetailModel["+i+"].bookingMasterModel.bookedForName",$("#bookedForName_"+bookingDetailId).html());
			form_data.set("bookingDetailModel["+i+"].bookingMasterModel.mobileNo", $('#mobileNo_'+bookingDetailId).html());
			form_data.set("bookingDetailModel["+i+"].reportAt", $("#reportAt_"+bookingDetailId).html());
			form_data.set("bookingDetailModel["+i+"].reportingAddress", $("#pickupLoc_"+bookingDetailId).html());
			form_data.set("bookingDetailModel["+i+"].toBeRealeseAt", $('#dropAt_'+bookingDetailId).html());
			form_data.set("bookingDetailModel["+i+"].instruction", $('#splIns_'+bookingDetailId).html());
			form_data.set("bookingDetailModel["+i+"].rentalType.id", editedFormDataValuesMap["bookingDetailModel["+bookingDetailId+"].rentalType.id "]);
			//form_data.set("bookingDetailModel["+iCount+"].rentalType.name", $('#rentalType option:selected').text());
			form_data.set("bookingDetailModel["+i+"].carModel.id", editedFormDataValuesMap["bookingDetailModel["+bookingDetailId+"].carModel.id "]);
			form_data.set("bookingDetailModel["+i+"].tariff.id", editedFormDataValuesMap["bookingDetailModel["+bookingDetailId+"].tariff.id "]);
			//form_data.set("bookingDetailModel["+iCount+"].carModel.name", $('#carModel option:selected').text());
			i++;
		});
	
		$.ajax({
			type : "POST",
			url:  ctx + '/updateBookingDeatil.html',
			data : form_data,
			processData: false,
			contentType: false,
			async:false,
			success : function(response) {
				if (response.status == "Success") {
					if(jobType=='webBooking'){
						 approvedWebBooking();
		  	        }
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

	function formFillForEditDetailRecord(id){
		setTableToModel(id);
		$('#editDetailRecordPanel').modal('show');
		$("#pickupDate_"+id).addClass("edited");
		$("#pickupDate_"+id).prop('disabled',false);
		$("#pickupLoc_"+id).prop('disabled',false);
		$("#dropAt_"+id).prop('disabled',false);
		$("#carModel_"+id).prop('disabled',false);
	}
	
	function  setModelToTable(){
		 if(jobType!='webBooking'){
			validateBookingDetails();	
         }else{
			checkAllowPermissionStatus();
		 }
	     if(!isValidationError){	    	    
    	    $('#pickupTime_'+idForUpdate).html($("#pickupTime").val());
    	    $('#startTime_'+idForUpdate).html($("#startTime").val());
    	    $("#tariff_"+idForUpdate).html($("#tariff option:selected" ).text());
    	    $('#pickupDate_'+idForUpdate).html($("#pickupDate").val());
    	    $('#corporateName_'+idForUpdate).html($( "#corporateName option:selected" ).text());
    	    $('#bookedBy_'+idForUpdate).html($( "#bookedBy option:selected" ).text());
    	    $('#bookedFor_'+idForUpdate).html($("#opClient").prop('checked')==true?"C":"O");
    	    $('#bookedForName_'+idForUpdate).html($("#opClient").prop('checked')==true?$("#clientName option:selected").text():$("#otherClient").val());
    	    $('#mobileNo_'+idForUpdate).html($("#mobileNo").val());				
    	    $('#reportAt_'+idForUpdate).html($("#opOffice").prop('checked')==true?"F":"O");
    	    $('#pickupLoc_'+idForUpdate).html($("#opOffice").prop('checked')==true?$("#officeAddress option:selected").text():$("#reportingAddress").val());
    	    $('#dropAt_'+idForUpdate).html($("#toBeReleaseAt").val());
    	    $('#splIns_'+idForUpdate).html($("#instruction").val());
    	    $('#rentalType_'+idForUpdate).html($( "#rentalTypeModel option:selected" ).text());
    	    $('#carModel_'+idForUpdate).html($( "#carType option:selected" ).text());
    	    $("#pickupDate_"+idForUpdate).addClass("edited");
    	    if(jobType!='webBooking'){
    	    	 userTable.destroy();		
 	    	    refreshData();
  	        }
    	    $('#editDetailRecordPanel').modal('hide');
    	    editedFormDataValuesMap["bookingDetailModel["+idForUpdate+"].pickUpDate "] = $("#pickupDate").val();
    	    editedFormDataValuesMap["bookingDetailModel["+idForUpdate+"].pickUpTime "] = $("#pickupTime").val();
    	    editedFormDataValuesMap["bookingDetailModel["+idForUpdate+"].startTime "] = $("#startTime").val();
    	    editedFormDataValuesMap["bookingDetailModel["+idForUpdate+"].tariff.id "] = $("#tariff").val();
    	    editedFormDataValuesMap["bookingDetailModel["+idForUpdate+"].bookingMasterModel.corporateId.id "] = $( "#corporateName").val();
    	    editedFormDataValuesMap["bookingDetailModel["+idForUpdate+"].bookingMasterModel.bookedBy.id "] = $( "#bookedBy").val();
    	    editedFormDataValuesMap["bookingDetailModel["+idForUpdate+"].bookingMasterModel.bookedFor "] = $("#opClient").prop('checked')==true?"C":"O";
    	    editedFormDataValuesMap["bookingDetailModel["+idForUpdate+"].bookingMasterModel.bookedForName "] = $("#opClient").prop('checked')==true?$("#clientName option:selected").text():$("#otherClient").val();
    	    editedFormDataValuesMap["bookingDetailModel["+idForUpdate+"].bookingMasterModel.mobileNo "] = $("#mobileNo").val();
    	    editedFormDataValuesMap["bookingDetailModel["+idForUpdate+"].reportAt "] = $("#opOffice").prop('checked')==true?"F":"O";
    	    editedFormDataValuesMap["bookingDetailModel["+idForUpdate+"].reportingAddress "] = $("#opOffice").prop('checked')==true?$("#officeAddress option:selected").text():$("#reportingAddress").val();
    	    editedFormDataValuesMap["bookingDetailModel["+idForUpdate+"].toBeRealeseAt "] = $("#toBeReleaseAt").val();
    	    editedFormDataValuesMap["bookingDetailModel["+idForUpdate+"].instruction "] = $("#instruction").val();
    	    editedFormDataValuesMap["bookingDetailModel["+idForUpdate+"].rentalType.id "] = $("#rentalTypeModel").val();
    	    editedFormDataValuesMap["bookingDetailModel["+idForUpdate+"].carModel.id "] = $("#carType").val();
    	    if(jobType=='webBooking'){
    	    	updateSaveBooking();
    	    }
    	    if(isPassengerCheck=='Y'){
    	      updatePassengerDetails();
    	    }
    	}		
	}

	function  setTableToModel(id){
		  idForUpdate = id;
		  isPassengerCheck=$("#isPassenger_"+id).html();
	  	  $("#pickupDate").val($("#pickupDate_"+id).html());
		  $("#pickupTime").val($("#pickupTime_"+id).html());
		  $("#startTime").val($("#startTime_"+id).html());
		  $("#corporateName").val($("#corporateName option").filter(function() { return this.text == $("#corporateName_"+id).text()}).val()).change();
		  var brId;		  
		  $.each(bookingDetailList, function (i, item) {
			  if(item.id == idForUpdate){
				brId = item.branch.id;
			  }
		  });
		  
		  fillCurrentHub(brId, $("#corporateName").val());
		  
		  $("#bookedBy").val($("#bookedBy option").filter(function() { return this.text == $("#bookedBy_"+id).html()}).val());		 
		  $("#bookedFor_"+id).html()=="C"?$("#opClient").prop('checked',true):$("#opOther").prop('checked',true);
		  $("#bookedFor_"+id).html()=="C"?$("#clientName").val($("#clientName option").filter(function() { return this.text == $("#bookedForName_"+id).html()}).val()):$("#otherClient").val($("#bookedForName_"+id).html());
		  if($("#opClient").prop('checked')){
			$('#clientName').show();
			$('#otherClient').val("");
			$('#otherClient').hide();
		  }else{
			$('#clientName').val("0");
			$('#clientName').hide();
			$('#otherClient').show()
		  }
		  $("#mobileNo").val($("#mobileNo_"+id).html());
		  $("#reportAt_"+id).html()=="F"?$('#opOffice').prop('checked', true):$('#opReport').prop('checked', true);
		  $("#reportAt_"+id).html()=="F"?$("#officeAddress").val($("#officeAddress option ").filter(function() { return this.text == $("#pickupLoc_"+id).text()}).val()):$("#reportingAddress").val($("#pickupLoc_"+id).text());
		  if($("#opOffice").prop('checked')){
			$('#officeAddress').show();
			$('#reportingAddress').val("");
			$('#reportingAddress').hide();
		  }else{
			$('#officeAddress').val("0");
			$('#officeAddress').hide();
			$('#reportingAddress').show()
		  }
		  $("#toBeReleaseAt").val($("#dropAt_"+id).html());
		  $("#instruction").val($("#splIns_"+id).html());

		  $("#rentalTypeModel").val($("#rentalTypeModel option").filter(function() { return $(this).html() == $("#rentalType_"+id).text()}).val()).change();
		  $("#tariff").val($("#tariff_"+id).val());
		  $("#carType").val($("#carType option").filter(function() { return $(this).html() == $("#carModel_"+id).text()}).val());
		  if(isPassengerCheck=='Y'){
		      getPassengerDetails();
		      $("#addPassengerButton ").prop('disabled', false);
		  }else{
		      $("#addPassengerButton ").prop('disabled', true); 
		  }
	}
	
	
	/*function updateSaveBooking(){
		form_data = new FormData();
		var i = 0;
		$(".edited").each(function(){
			var bookingDetailId = $(this).attr('id');
			bookingDetailId = bookingDetailId.substring(11,bookingDetailId.length);
			form_data.append("bookingDetailModel["+i+"].id",bookingDetailId);
			form_data.append("bookingDetailModel["+i+"].pickUpTime",$('#pickupTime_'+bookingDetailId).html());

			i++;
		});

		$.ajax({
			type : "POST",
			url:  ctx + '/updateBookingDeatil.html',
			data : form_data,
			processData: false,
			contentType: false,
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
	}*/
	
	//Changes
	function fillValues(corporateId){
		if(corporateId <= 0){
		    $('#bookedBy').val("0");
		    $('#clientName').val("0");
		    $('#mobileNo').val("");
		    $('#officeAddress').val("0");
		    return;
		}
		$.ajax({  
			type: "POST",
			async: false,
			url:  ctx + "/fillAsPerCorporate.html",  
			data: "corporateId="+corporateId,  
			success : function(response) {
				if (response.status == "Success") {
//					tariffList = response.corpTariffList;
					refillCombo('#bookedBy',response.autorizedUserModelList);
					refillCombo('#clientName',response.autorizedUserModelList);
					refillCombo('#tariff',response.corpTariffList);
					refillCombo('#carType',response.carModelList);
//					refillCombo('#rentalTypeModel',response.corpRentalList);

					$.each(response.autorizedUserModelList, function (i, item) {
						clientMobile[i] = item.remark;
					});
					$("#officeAddress")
					.find('option')
					.remove()
					.end()
					.append('<option value="0">--- Select ---</option>')
					.val('0');
					if(response.addressDetail.address1 != ""){
						$("#officeAddress").append($('<option>', { 
							value: "1",
							text : response.addressDetail.address1 + " " + response.addressDetail.address2
						}));
					}
					$('#mobileNo').val("");
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
	
	function fillTariff(value){
		$("#tariff")
	    .find('option')
	    .remove()
	    .end()
	    .append('<option value="0">--- Select ---</option>')
	    .val('0');
		$.each(tariffList, function (i, item) {
			if(item.masterId == value){
			    $("#tariff").append($('<option>', { 
			        value: item.id,
			        text : item.name
			    }));
			}
		});
		
	}
	
	function fillClientMobile(){
	    if($("#clientName").val()!="0"){
		var mobAndEmail = (clientMobile[$("#clientName option:selected").index() - 1]).split('#');    	
		$("#mobileNo").val(mobAndEmail[0]);
	    }else{
		$("#mobileNo").val("");
	    }  
		//$("#mobileNo").val(clientMobile[$("#clientName option:selected").index() - 1]);
	}
	
	function checkStart() {
		var diff = null;
		var start = ($("#pickupDate").val()).concat(" ").concat($("#startTime").val());
		var today = $("#pickupDate").val();
		var ms = moment(start, "DD/MM/YYYY HH:mm").diff(moment(today, "DD/MM/YYYY HH:mm"));
		var d = moment.duration(ms);
		if (d < 0) {
			return false;
		}
		return true;
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
		var corporateName = $('#corporateName').val();
		if(corporateName == null || corporateName == "" || corporateName == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Corporate Name can not be Blank.</li>";
			isValidationError = true;
		}
		var bookedBy = $('#bookedBy').val();
		if(bookedBy == null || bookedBy == "" || bookedBy == "0"){
			validationErrorHtml = validationErrorHtml + "<li> Booked By can not be Blank.</li>";
			isValidationError = true;
		}
		var bookedFor = $("#opClient").prop('checked')==true?"C":"O";
		if(bookedFor == null || bookedFor == "" || bookedFor == undefined){
			validationErrorHtml = validationErrorHtml + "<li> Booked For can not be Blank.</li>";
			isValidationError = true;
		}
		var bookedForName = $("#opClient").prop('checked')==true?$("#clientName option:selected").text():$("#otherClient").val();
		if(bookedForName == null || bookedForName == "" || bookedForName == "--- Select ---"){
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
		var reportingAddress = $("#opOffice").prop('checked')==true?$("#officeAddress option:selected").text():$("#reportingAddress").val();
		if(reportingAddress == null || reportingAddress == "" || reportingAddress == "--- Select ---"){
			validationErrorHtml = validationErrorHtml + "<li> Reporting Address not be Blank.</li>";
			isValidationError = true;
		}
		var toBeReleaseAt = $('#toBeReleaseAt').val();
		if(toBeReleaseAt == null || toBeReleaseAt == "" || toBeReleaseAt == "0"){
			validationErrorHtml = validationErrorHtml + "<li> To be Release At can not be Blank.</li>";
			isValidationError = true;
		}	
		if(isPassengerCheck=='Y'){
			var nValidateStatus=false;
			var mValidateStatus=false;
			var aValidateStatus=false;
			var aLimitValidationStatus=false
			$(".mob").each(function(){
				 var id = (this.id).split("_"); 
				 if($("#passengerName_"+id[1]).val()=="") nValidateStatus=true;
				 if($("#passengerMob_"+id[1]).val()=="")    mValidateStatus=true;
				 if($("#passengerAge_"+id[1]).val()=="")   aValidateStatus=true;
				 else if((parseInt($("#passengerAge_"+id[1]).val())<1 )||( parseInt($("#passengerAge_"+id[1]).val())>120)) aLimitValidationStatus=true
				});
			if(nValidateStatus){
				validationErrorHtml = validationErrorHtml + "<li>Passenger Name can not be blank</li>";
				isValidationError = true;
			  }if(mValidateStatus){
				validationErrorHtml = validationErrorHtml + "<li>Passenger MobileNo can not be blank</li>";
				isValidationError = true;
			  }if(aValidateStatus){
			     validationErrorHtml = validationErrorHtml + "<li>Passenger Age can not be blank</li>";
				 isValidationError = true;
			  }if(aLimitValidationStatus){
				     validationErrorHtml = validationErrorHtml + "<li>Passenger Age Should not be less then 1 and Greater than 120</li>";
					 isValidationError = true;
			 }  
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
	
	function EnableDisableTextBox(isDsManual) {
	        var txtPassportNumber = document.getElementById("manualDSNO");
	        txtPassportNumber.disabled = isDsManual.checked ? false : true;
	        if (!txtPassportNumber.disabled) {
	            txtPassportNumber.focus();
	        }
	        var txtremark = document.getElementById("remark");
	        txtremark.disabled = isDsManual.checked ? false : true;
	        if (!txtremark.disabled) {
	        	txtremark.focus();}
	    }    
	 
	 function isNumber(evt) {
		evt = (evt) ? evt : window.event;
		var charCode = (evt.which) ? evt.which : evt.keyCode;
		if (charCode > 31 && (charCode < 48 || charCode > 57)) {
		    return false;
		}
		return true;
	}
	 
	 function disablefield(){
	     	$('#dispatchDetailError').html("");
		   $('#dispatchDetailError').hide();
	     	if (document.getElementById('allocation').checked == 1){ 
	     	$("#allocation").prop('checked',true);
			$("#dispatch").prop('checked',false);
		    $("#alo").show();
		    $("#dis").hide();
		    $("#disalo").hide();
		    document.getElementById('kmOut').disabled='disabled';
		    $("#allocationDispatch").removeClass('col-md-8' ).addClass('col-md-offset-2 col-md-6');	
			
		}
		else{ 

		    document.getElementById('kmOut').disabled=''; 
		    $("#allocationDispatch").removeClass('col-md-8' ).addClass('col-md-offset-2 col-md-6');
			$("#allocation").prop('checked',false);
			$("#dispatch").prop('checked',true);
		    $("#dis").show();
		    $("#alo").hide();
		    $("#disalo").hide();
		    if($("#dutySlipIdForUpdate").val()!="0"){
			updateTime();
		    }
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
		disablefield();
		idForUpdate = id;
	    if($("#status_"+id).html() == 'Allocated'  || $("#status_"+id).text() == 'Reserved'){
		    $("#dataLoaderModal").show();
		    $("#dataLoaderFade").show();
		    form_data = new FormData();
		    form_data.append("bookingDetailId", id);
		     $.ajax({
			type : "POST",
			url:  ctx + '/fillAllocationDetails.html',
			processData: false,
			contentType: false,
			data : form_data,
			success : function(response) {
				if (response.status == "Success") {
				    	$("#dutySlipNo").val(response.dutySlipModel.dutySlipNo);
				    	$("#carNumber").val(response.dutySlipModel.carDetailModel.registrationNo);
				    	carno();
				    	fillCarRelDetails($("#carNumber").val());
				    	if(response.dutySlipModel.isUpdatedChauffeur == "Y"){
				    	    $("#chauffeurName").val(response.dutySlipModel.chauffeurName+" - "+response.dutySlipModel.chauffeurMobile);
				    	}else if(response.dutySlipModel.isUpdatedChauffeur != null){
				    	    $("#chauffeurName").val(response.dutySlipModel.chauffeurModel.name+" - "+response.dutySlipModel.chauffeurModel.mobileNo);
				    	}
				    			    	
				    	if(response.dutySlipModel.isDsManual == "Y"){
				    	    $("#isDsManual").prop('checked',true);
				    	    $("#manualDSNO").val(response.dutySlipModel.manualSlipNo);
				    	    $("#remark").val(response.dutySlipModel.manualSlipRemarks);
				    	    EnableDisableTextBox(document.getElementById("isDsManual"));
				    	}else{
				    	    $("#isDsManual").prop('checked',false);
				    	    $("#manualDSNO").val("");
				    	    $("#remark").val("");
				    	    EnableDisableTextBox(document.getElementById("isDsManual"));
				    	}
				    	$("#dutySlipIdForUpdate").val(response.dutySlipModel.id);
				    	/*var allDTime = getTimeStampTo_ddMMyyyyHHmmss(response.dutySlipModel.allocationDateTime);
				    	var allDTimeArray = allDTime.split(" ");
				    	$("#date").val(allDTimeArray[0]);
				    	$("#time").val(allDTimeArray[1])*/;
				    	$("#dataLoaderModal").hide();
				    	$("#dataLoaderFade").hide();
				    	
				} else {
					$('#dispatchDetailError').html(response.result);
					$('#dispatchDetailInfo').hide();
					$('#dispatchDetailError').show();
					$("#dataLoaderModal").hide();
					$("#dataLoaderFade").hide();
				}
			},
			error : function(e) {
			    	$("#dataLoaderModal").hide();
			    	$("#dataLoaderFade").hide();
			    	alert('Error: ' + e);
			}
		     });
		     $("#allocation").prop('checked',false);
		     $("#dispatch").prop('checked',true);
		     $("#allocateRadioDiv").hide();
		     $("#dispatchRadioDiv").show();
		     disablefield();
		     updateTime();
	    }else{
	  
	    	$("#allocation").prop('checked',false);
			$("#dispatch").prop('checked',true);
			$("#allocateRadioDiv").hide();
			$("#dispatchRadioDiv").show();
			disablefield();
	    }
	    if(jobType== 'Advance' && $("#status_"+id).html()== 'Reserved' ){
	    	$("#allocation").prop('checked',false);
			$("#dispatch").prop('checked',true);
			$("#allocateRadioDiv").hide();
			$("#dispatchRadioDiv").show();
			disablefield();
	    }
	    else if(jobType== 'Advance' && $("#status_"+id).html()!= 'Allocated' ){
			$("#allocation").prop('checked',true);
			$("#dispatch").prop('checked',false);
			$("#allocateRadioDiv").show();
			$("#dispatchRadioDiv").show();
			disablefield();
    	 }
	   
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
		
	 function disablefield1(){
	     	$('#dispatchDetailError').html("");
		$('#dispatchDetailError').hide();
	     	if (document.getElementById('dispatchAndAllocation').checked == 1)
		{ 
		    $("#allocationDispatch").removeClass('col-md-offset-2 col-md-6' ).addClass('col-md-8');
		    $("#disalo").show();
		    $("#dis").hide();
		    $("#alo").hide();
		    document.getElementById('kmOut').disabled='';
		}
		else
	        { 
		 $("#allocationDispatch").removeClass('col-md-8' ).addClass('col-md-offset-2 col-md-6');
		 $("#dis").show();
		 $("#alo").show();
	
	        }
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
	 
	 function updateTime() {
	     var today = new Date();
	     var h = today.getHours();
	     var m = today.getMinutes();
	     var s = today.getSeconds();
	     m = checkTime(m);
	     s = checkTime(s);
	     $('#systime').val(h + ":" + m + ":" + s);
//	     $('#time').val(h + ":" + m + ":" + s);
	     tOut = setTimeout(updateTime, 500);
	 }
	 function checkTime(i) {
	     if (i < 10) {i = "0" + i};  // add zero in front of numbers < 10
	     return i;
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
		 if(oType == "allocate"){
			 checkCarAvailabilityForAllocation(idForUpdate);
			 if(check == "Y"){
				 alert("This Car Already Allocated, Please Select another Car !!");
				 return false;
			 }else if(check == 'N'){
				 if (confirm('Car is availalbel !! \n Are you sure you want to Allocate this Record ?')) {
					 stut = false;
				 }
			 }
		 }else{
	 		stut = false;
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
			 form_data.append("bookingDetailModel.id", idForUpdate);
			 form_data.append("bookingDetailModel.bookingMasterModel.bookingNo", $("#bookingNO").val());
			 form_data.append("bookingDetailModel.status", dutySlipStatus);
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
				url:  ctx + '/allocateOrDispatch.html',
				processData: false,
				contentType: false,
				data : form_data,
				async:false,
				success : function(response) {
					if (response.status == "Success") {
					    $('#dispatchDetailInfo').html(response.result);
					    $("#dutySlipNo").val(response.dutySlipModel.dutySlipNo);
					    $('#dispatchDetailError').hide();
					    $('#dispatchDetailInfo').show();
					    $("#dataLoaderModal").hide();
					    $("#dataLoaderFade").hide();		  
					    setTimeout(function(){$("#dispatchDetailRecordPanel").modal('hide');},1000);
					    if(oType!= "allocate")
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
	 
	/*For Cancellation Booking Record */
	function cancelRecord(id){
		idForUpdate = id;
		$('#bookingCancelPanel').modal('show');
		showHide();
	}
	
	function showHide(){
	    if($("#cancelReason option:selected" ).text() =='Other'){
		$("#otherReason").show();
	    } else 
		$("#otherReason").hide();
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
			url  : ctx + '/cancelBooking.html',
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
	function fillCurrentHub(){		  		 
	    form_data = new FormData();
	    var corporateId=$("#corporateId").val();
	    form_data.append("corporateId",corporateId);
	    form_data.append("branchId",branchId);
	    $.ajax({  
			type: "POST",
			url:  ctx + "/fillHubAsBranch.html",  
			data: form_data, 
		    async:false,
			processData: false,
			contentType: false,
			success : function(response) {
				if (response.status == "Success") {
					tariffList = response.corpTariffList;
					refillCombo("#rentalTypeModel",response.corpRentalList);
					refillCombo("#outlet",response.generalMasterModelList);
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
	    }else if( jobType == 'webBooking'){
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
	
	function getCarListForReserve(id){
	    form_data = new FormData();
	    $('#reserveRecordPanel').modal('show'); 
	    form_data.append("reserveDateTime",$("#reserveDate").val());
	    form_data.append("carSegment",$("#carTypeReserve").val());
	    $.ajax({
			type : "POST",
			url:  ctx + '/getCarListForReserve.html',
			data : form_data,
			processData: false,
			contentType: false,
			async:false,
			success : function(response) {
					$('#info').html(response.result);
					$('#dataTable3').html(response.dataGrid);
					var userTable2 = $('#userTable2').DataTable({
                		        		"bLengthChange": true,
                		        		"bFilter": true,
                		        		"bSort": true,
                		        		"bInfo": true,
                		        		"bAutoWidth": true,
                		        		"aLengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]]
						});
					$("#userTable2 thead tr th").each( function ( i ) {
		        		    var selectBoxField = "";
		        		    if(i==1){		        			
		        			selectBoxField = "carTypeReserve";				
    		        		    	$("#"+selectBoxField+" option").each(function(){
    		        		    	    allOptionValuesMap[$(this).text()] = $(this).val();    				  
    		        		    	});
    		        		    	$("#"+selectBoxField+"").empty().append('<option value="0">ALL</option>');
    		        		    	userTable2.column(i).data().unique().sort().each( function ( d, j ) {			    
    		        		    	    $("#"+selectBoxField+"").append('<option value="'+allOptionValuesMap[d]+'">'+d+'</option>');
    		        		    	});
		        		    }	
		        		});
			},
			error : function(e) {
				alert('Error: ' + e);
			}
		});
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
/*	   		        	console.log('allcells:'+allCells);
	   		        	console.log('cells:'+cells);
*/	   		        	allCells.push($(rows[k]).find("td:eq("+colNo+") textarea").text()); 
	   		        	if(cells.indexOf(allCells[k])>-1){
	   		        		countValueObject[allCells[k]] = (countValueObject[allCells[k]] == undefined? 1 :(countValueObject[allCells[k]]+1));
	   		        	}
	   		        	$("#"+allCells[k]).text(countValueObject[allCells[k]]);
	    	 	   }
	    	}
	    });
	}
	
	function viewDutySlip(id){
	   form_data = new FormData();
	   form_data.append("bookingDetailId",id);
		$.ajax({
			type : "POST",
			url:  ctx + '/getDutySlip.html',
			data : form_data,
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
//	function print()
//	{
//
//		
//		$('#print').hide();
//		$('#close').hide();
//		var prtContent = document.getElementById("dutySlip1");
//		   var divContents = $("#dutySlip").html();
//		   alert(divContents);
//		var WinPrint = window.open('', '', 'left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0');
//		WinPrint.document.write('<html><head><title>DIV Contents</title>');
//		WinPrint.document.write('</head><body >');
//		WinPrint.document.write(divContents );
//		WinPrint.document.write('</body></html>');
//		WinPrint.document.close();
//		WinPrint.focus();
//		WinPrint.print();
//		WinPrint.close();
//		$('#print').show();
//		$('#close').show();
//		
//}
        function print(){
	        var divContents = $("#dutySlipPrint").html();
	        var printWindow = window.open('', '', 'left=0,top=0,width=900,height=580,toolbar=0,scrollbars=0,status=0');
	        printWindow.document.write('<html><head>'+dutySlipRelatedStyles);
	        printWindow.document.write('</head><body >');
	        printWindow.document.write(divContents);
	        printWindow.document.write('</body></html>');
	        printWindow.document.close();
	        printWindow.print();
	        printWindow.close()
        	closeDS();
        }
	
        function closeDS(){
        	$('#viewDutySlip').modal('hide');
        }
        
        function deallocate(id){
        	if (confirm('Are you sure you want to deallocate this Record?')) {
		       	 $("#dataLoaderModal").show();
		   	     $("#dataLoaderFade").show();
		   		 var dutySlipStatus = "";
		   		 form_data = new FormData();
		   		 form_data.append("bookingDetailId", id);
		   		 $.ajax({
		   			type : "POST",
		   			url:  ctx + '/deallocate.html',
		   			processData: false,
		   			contentType: false,
		   			data : form_data,
		   			async:false,
		   			success : function(response) {
		   				if (response.status == "Success") {
		   				    alert(response.result);
		   				    searchBooking();
		   				    $("#dataLoaderModal").hide();
		   			    	$("#dataLoaderFade").hide();
		   				} else {
		   				    alert(response.result);
		   				    $("#dataLoaderModal").hide();
		   			    	$("#dataLoaderFade").hide();
		   				}
		   			},
		   			error : function(e) {
		   			    	$("#dataLoaderModal").hide();
		   			    	$("#dataLoaderFade").hide();
		   			    	alert('Error: ' + e);
		   			}
		     });	
    	 }
   	 }
      
    function reserve(id){
    	if($("#status_"+id).text() == 'Reserved'){
    		dispatch(id);
    	}else{
    		idForViewDutySlip=id;
			getCarForDispatch($("#carModelId_"+id).val());
			getBookingDetailsRecord($("#BookingMasterId_"+id).html());
			$('#dispatchDetailError').html("");
			$('#dispatchDetailError').hide();
			$('#carNumberR').val("");
			$('#chauffeurNameR').val("");
			$("#venderNameR").val("");
			$("#carModelForAllocationR").val("");
			$("#carOwnerTypeR").val("");
			$("#pendingDutySlipR").val("");
			$("#smsClientR").prop("checked",true);
			$("#smsBookerR").prop("checked",false);
			$("#dutySlipIdForUpdateR").val("0");
	     	$('#reserveCarDetailRecordPanel').modal('show');
			$("#bookingNOR").val($("#BookingNo_"+id).html());
			$("#corpName").val($("#corporateName_"+id).html());
			/*$("#date").val($("#pickupDate_"+id).html());
			$("#time").val($("#startTime_"+id).html());*/
			idForUpdate = id;
			$("#reserve").prop('checked',true);
		    $("#dispatchR").prop('checked',false);
    	}
	 }
     
    function updateChauffeurR()
	 {	
		$('#updateChauffuerDetailRecordPanel').modal('show');
		var booking  = document.getElementById("bookingNOR").value;
		$("#UpdateBookingNoR").text(booking);
		$("#carNoR").val($("#carNumberR").val());
	 }
    
    function updateChauffeurDetailsR(){
	     var updatedChauffeurName = $("#updateChauffeurNameR").val();
	     var updatedChauffeurMobile = $("#updateMobileNumberR").val();
	     var chauffeurValidationErrorHtml = '<ol>';
	     if(updatedChauffeurName == null || updatedChauffeurName == "")
	    	 chauffeurValidationErrorHtml = chauffeurValidationErrorHtml+ "<li>Chauffeur Name Can not be Blank !!!</li>";
	     if(updatedChauffeurMobile == null || updatedChauffeurMobile == "")
	    	 chauffeurValidationErrorHtml = chauffeurValidationErrorHtml+ "<li>Chauffeur Mobile Can not be Blank !!!</li>";
	     if(updatedChauffeurMobile != null && updatedChauffeurMobile!="" && updatedChauffeurMobile.length != 10)
			 chauffeurValidationErrorHtml = chauffeurValidationErrorHtml+ "<li>Chauffeur Mobile should be of 10 digit !!!</li>";
		     chauffeurValidationErrorHtml = chauffeurValidationErrorHtml + "</ol>";
	     if(chauffeurValidationErrorHtml != "<ol></ol>"){
			$('#chauffeurDetailErroRr').html("Please correct following errors:<br>" + chauffeurValidationErrorHtml);
			$('#chauffeurDetailInfoR').hide();
			$('#chauffeurDetailErrorR').show();
	     }else{
			$('#chauffeurDetailErrorR').html("");
			$('#chauffeurDetailErrorR').hide();
			$("#isUpdatedChauffeurR").val("Y");
			$("#chauffeurNameR").val(updatedChauffeurName+"  -  "+updatedChauffeurMobile);
			$('#updateChauffuerDetailRecordPanel').modal('hide');
	     }	     
	 }
    
    function getBookingDetailsRecord(bookingMasterId){
		  	 $("#dataLoaderModal").show();
		     $("#dataLoaderFade").show();
			 form_data = new FormData();
			 form_data.append("bookingMasterModel.id", bookingMasterId);
			 form_data.append("bookingFromDate", $("#bookingFromDate").val());
			 form_data.append("bookingToDate", $("#bookingToDate").val());
			 $.ajax({
				type : "POST",
				url:  ctx + '/getBookingDetailsRecord.html',
				processData: false,
				contentType: false,
				data : form_data,
				async:false,
				success : function(response) {
					if (response.status == "Success") {
						$('#info').html(response.result);
						$('#bookingDetailDataTable').html(response.dataGrid);
					    $("#dataLoaderModal").hide();
				    	$("#dataLoaderFade").hide();
					} else {
					    alert(response.result);
					    $("#dataLoaderModal").hide();
				    	$("#dataLoaderFade").hide();
					}
				},
				error : function(e) {
				    	$("#dataLoaderModal").hide();
				    	$("#dataLoaderFade").hide();
				    	alert('Error: ' + e);
				}
		 });
    }
    
    function fillCarRelDetailsR(selectedRegNo){
		    form_data = new FormData();
		    form_data.append("selectedRegNo", selectedRegNo);
		    $.ajax({
			type : "POST",
			url:  ctx + '/fillCarRelDetails.html',
			processData: false,
			contentType: false,
			data : form_data,
			success : function(response) {
				if (response.status == "Success") {
					$("#chauffeurNameR").val(response.carAllocationModel.chauffeurId.name +'  -  ' +response.carAllocationModel.chauffeurId.mobileNo);
					$("#venderNameR").val(response.carAllocationModel.vendorId.contPerson +'  -  ' +response.carAllocationModel.vendorId.contPersonMobile);
					$("#carModelForAllocationR").val(response.carAllocationModel.carDetailModelId.model.name);
					$("#pendingDutySlipR").val(response.carAllocationModel.noOfPendingDutySlip == '' ? "0" : response.carAllocationModel.noOfPendingDutySlip);
					if(response.carAllocationModel.carDetailModelId.ownType == 'V')
			    	    $("#carOwnerTypeR").val("Vendor");
			    	else if(response.carAllocationModel.carDetailModelId.ownType == 'C')
			    	    $("#carOwnerTypeR").val("Company");				    	
			    	cModelMap[response.carAllocationModel.carDetailModelId.model.name] = response.carAllocationModel.carDetailModelId.id;
			    	vModelMap[response.carAllocationModel.vendorId.contPerson +'  -  ' +response.carAllocationModel.vendorId.contPersonMobile] = response.carAllocationModel.vendorId.id;
			    	chModelMap[response.carAllocationModel.chauffeurId.name +'  -  ' +response.carAllocationModel.chauffeurId.mobileNo] = response.carAllocationModel.chauffeurId.id;
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
    
    
    function checkboxClicked(booDetailId,id){
			if($('#'+id).prop('checked') == true){
				$('#'+id).addClass("edited");
			}else{
				$('#'+id).removeClass("edited");
			}
    }
   
    function checkboxClickAll(id){
			if($('#'+id).prop('checked') == true){
				$('.checkbox').addClass("edited");
				$('.checkbox').prop("checked",true);
			}else{
				$('.checkbox').removeClass("edited");
				$('.checkbox').prop("checked",false);
			}
    } 

    
    function reserveRequest(){
    	 $("#res").attr("disabled", true);
    	 form_data = new FormData();
    	 var chauffeurNameMobile = $("#chauffeurNameR").val();
		 var chauffeurNameMobileArray = chauffeurNameMobile.split('  -  ');
		 var  i=0;
    	 $(".edited").each(function(){
			var bookingDetailId = $(this).attr('id');
			bookingDetailId = bookingDetailId.substring(9,bookingDetailId.length);		
			if($("#checkbox_"+bookingDetailId).prop('checked') == true){
				form_data.append("bookingDetailModel.id",bookingDetailId);
				i++;
			}
		});
    	    form_data.append("bookingDetailModel.status", "M");
		    form_data.append("dutySlipStatus", "M");
		    form_data.append("carDetailModel.id", cModelMap[$('#carModelForAllocationR').val()]);
		    form_data.append("carDetailModel.registrationNo",$('#carNumberR').val());
		    /*form_data.append("carDetailModel.carAllocationModels[0].chauffeurId.name", chauffeurNameMobileArray[0]);
	        form_data.append("carDetailModel.carAllocationModels[0].chauffeurId.mobileNo", chauffeurNameMobileArray[1]);*/
	      if($('#isUpdatedChauffeurR').val()=='Y'){
		     form_data.append("isUpdatedChauffeur", "Y");
		     form_data.append("chauffeurName", chauffeurNameMobileArray[0]);
		     form_data.append("chauffeurMobile", chauffeurNameMobileArray[1].trim());
		  }else if(chModelMap[$("#chauffeurNameR").val()]!=null && !chModelMap[$("#chauffeurNameR").val()]==""){
			form_data.append("chauffeurModel.id", chModelMap[$("#chauffeurNameR").val()]);
		  }
         form_data.append("vendorModel.id", vModelMap[$("#venderNameR").val()]);
		 form_data.append("smsClient", $("#smsClientR").prop('checked')==true?"Y":"N");
		 form_data.append("smsBooker", $("#smsBookerR").prop('checked')==true?"Y":"N");
		 if($("#dispatchSmsOther").val()!="")
		     form_data.append("dispatchSmsOther", $("#dispatchSmsOtherR").val());
		 	 form_data.append("dutySlipIdForUpdate", $("#dutySlipIdForUpdateR").val());
		 
		 $.ajax({
			type : "POST",
			url:  ctx + '/reserve.html',
			processData: false,
			contentType: false,
			data : form_data,
			async:false,
			success : function(response) {
				if (response.status == "Success") {
				    $('#reserveDetailInfo').html(response.result);
				    $('#reserveDetailError').hide();
				    $('#reserveDetailInfo').show();
				    $("#dataLoaderModal").hide();
				    $("#dataLoaderFade").hide();		  
				    setTimeout(function(){$("#reserveCarDetailRecordPanel").modal('hide');},2000);
				    searchBooking();
				} else {
				    $("#dataLoaderModal").hide();
				    $("#dataLoaderFade").hide();
				    $('#reserveDetailError').html(response.result);
				    $('#reserveDetailInfo').hide();
				    $('#reserveDetailError').show();				    
				}
			},
			error : function(e) {
			    	$("#dataLoaderModal").hide();
			    	$("#dataLoaderFade").hide();
			    	alert('Error: ' + e);
			}
	     });		 
	 }
    
    
    function validateReserveDetails(opType){	    	    
		isValidationError = false;
		var dispatchValidationErrorHtml = '<ol>';
		var bookingNO = $('#bookingNOR').val();
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
		var carNumber = $('#carNumberR').val();
		if(carNumber == null || carNumber == ""){
		    dispatchValidationErrorHtml = dispatchValidationErrorHtml + "<li> Car Number can not be Empty.</li>";
		    isValidationError = true;
		}		
		var carModelForAllocation = $('#carModelForAllocationR').val();
		if(carModelForAllocation == null || carModelForAllocation == ""){
		    dispatchValidationErrorHtml = dispatchValidationErrorHtml + "<li> Car Model can not be Blank.</li>";
		    isValidationError = true;
		}
		var carOwnerType = $('#carOwnerTypeR').val();
		if(carOwnerType == null || carOwnerType == ""){
		    dispatchValidationErrorHtml = dispatchValidationErrorHtml + "<li> Car Owner Type can not be Blank.</li>";
		    isValidationError = true;
		}
		var dispatchSmsOther = $('#dispatchSmsOtherR').val();						   
		if(dispatchSmsOther != null && dispatchSmsOther != "" && dispatchSmsOther.length != 10){
		    dispatchValidationErrorHtml = dispatchValidationErrorHtml + "<li> Sms Other Mobile Number should be of 10 digit.</li>";
		    isValidationError = true;
		}		
		dispatchValidationErrorHtml = dispatchValidationErrorHtml + "</ol>";
		if(isValidationError){
			$('#reserveDetailError').html("Please correct following errors:<br>" + dispatchValidationErrorHtml);
			$('#reserveDetailInfo').hide();
			$('#reserveDetailError').show();
		}else{
			$('#reserveDetailError').html("");
			$('#reserveDetailError').hide();
		}
	}
    
  
    function checkCarAvailability(oType){
    	if($('.edited').length<=0){
    		alert("Please Select Those Record Which One You Want to Reserve First   !!");
    		return false;
    	 }	
		 $("#dataLoaderModal").show();
	     $("#dataLoaderFade").show();
	     validateReserveDetails(oType);
	     if(!isValidationError){
	    	 form_data = new FormData();
	    	 var i = 0;
	    	 $(".edited").each(function(){
	    			var bookingDetailId = $(this).attr('id');
	    			bookingDetailId = bookingDetailId.substring(9,bookingDetailId.length);		
	    			if($("#checkbox_"+bookingDetailId).prop('checked') == true){
	    				form_data.append("reserveDateTime",$("#pickupDate_"+bookingDetailId).val() == ''?"0":$("#pickupDate_"+bookingDetailId).val());
	    				form_data.append("bookingDetailModel.id",bookingDetailId);
	    				i++;
	    			}
	    		});
	    	 form_data.append("carDetailModel.id", cModelMap[$('#carModelForAllocationR').val()]);
	    		$("#res").attr("disabled", true);
	    		$.ajax({
	    			type : "POST",
	    			url:  ctx + '/checkCarAvailability.html',
	    			processData: false,
	    			contentType: false,
	    			data : form_data,
	    			async:false,
	    			success : function(response) {
	    				if (response.status == "Success"){
	    				    $('#reserveDetailInfo').html(response.result);
	    				    $('#reserveDetailError').hide();
	    				    var idArr = (response.dataString1).split(" ");
	    				    for(var j=1; j<idArr.length; j++){
	    				    	$('#checkbox_'+idArr[j]).prop('checked',false);
    							$('#checkbox_'+idArr[j]).removeClass("edited");
	    				    }
	    				    if (confirm(response.dataString2 +'\n\n Are you sure you want to Reserve?')) {
	    				    	reserveRequest();
	    				    }
	    				    $("#res").attr("disabled", false);
	    				    $("#dataLoaderModal").hide();
	    				    $("#dataLoaderFade").hide();	
	    				} else {
	    				    $("#dataLoaderModal").hide();
	    				    $("#dataLoaderFade").hide();
	    				    $('#reserveDetailError').html(response.result);
	    				    $('#reserveDetailError').show();				    
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
    
    function unreserve(id){
    		idForViewDutySlip=id;
//			getCarForDispatch($("#carModelId_"+id).val());
			getBookingDetailsRecordForUnreserve($("#BookingMasterId_"+id).html());
			$('#unreserveDetailError').html("");
			$('#unreserveDetailInfo').hide();
	     	$('#unReserveCarDetailRecordPanel').modal('show');
			$("#bookingNOUr").val($("#BookingNo_"+id).html());
			$("#corpNameUr").val($("#corporateName_"+id).html());
    	}
	 
    
    function getBookingDetailsRecordForUnreserve(bookingMasterId){
	  	 $("#dataLoaderModal").show();
	     $("#dataLoaderFade").show();
		 form_data = new FormData();
		 form_data.append("bookingMasterModel.id", bookingMasterId);
		 form_data.append("bookingFromDate", $("#bookingFromDate").val());
		 form_data.append("bookingToDate", $("#bookingToDate").val());
		 $.ajax({
			type : "POST",
			url:  ctx + '/getBookingDetailsRecordForUnreserve.html',
			processData: false,
			contentType: false,
			data : form_data,
			async:false,
			success : function(response) {
				if (response.status == "Success") {
					$('#info').html(response.result);
					$('#bookingDetDataTableUnRes').html(response.dataGrid);
				    $("#dataLoaderModal").hide();
			    	$("#dataLoaderFade").hide();
				} else {
				    alert(response.result);
				    $("#dataLoaderModal").hide();
			    	$("#dataLoaderFade").hide();
				}
			},
			error : function(e) {
			    	$("#dataLoaderModal").hide();
			    	$("#dataLoaderFade").hide();
			    	alert('Error: ' + e);
			}
	 });
}
    
    function unReserve(){
    	if($('.edited').length<=0){
    		alert("Please Select Those Record Which One You Want to UnReserve   !!");
    		return false;
    	 }	
    	if (confirm('Are you sure you want to UnReserve Selected Record ?')) {
			 $("#dataLoaderModal").show();
		     $("#dataLoaderFade").show();
		     form_data = new FormData();
	    	 var i = 0;
	    	 $(".edited").each(function(){
	    			var bookingDetId = $(this).attr('id');
	    			bookingDetId = bookingDetId.substring(10,bookingDetId.length);		
	    			if($("#checkboxU_"+bookingDetId).prop('checked') == true){
	    				form_data.append("bookingDetailModel.id",bookingDetId);
	    				i++;
	    			}
	    		});
	    	 $.ajax({
		   			type : "POST",
		   			url:  ctx + '/unReserveBooking.html',
		   			data : form_data,
		   			processData: false,
		   			contentType: false,
		   			async: false,
		   			success : function(response) {
		   				if (response.status == "Success") {
		   				    alert(response.result);
		   				    searchBooking();
		   				    $("#dataLoaderModal").hide();
		   			    	$("#dataLoaderFade").hide();
		   			    	$('#unReserveCarDetailRecordPanel').modal('hide');
		   				} else {
		   				    alert(response.result);
		   				    $("#dataLoaderModal").hide();
		   			    	$("#dataLoaderFade").hide();
		   				}
		   			},
		   			error : function(e) {
		   			    	$("#dataLoaderModal").hide();
		   			    	$("#dataLoaderFade").hide();
		   			    	alert('Error: ' + e);
		   			}
		     });	
    	}
    }
    
    function checkCarAvailabilityForAllocation(bookingDetailId){
    	$("#dataLoaderModal").show();
	    $("#dataLoaderFade").show();
    	form_data = new FormData();
    	form_data.append("bookingDetailModel.id",bookingDetailId);
    	form_data.append("reserveDateTime",$("#pickupDate_"+bookingDetailId).val() == ''?"0":$("#pickupDate_"+bookingDetailId).val());
    	form_data.append("carDetailModel.id", cModelMap[$('#carModelForAllocation').val()]);
		$("#alo").attr("disabled", true);
		$.ajax({
			type : "POST",
			url:  ctx + '/checkCarAvailability.html',
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
    
    function approvedWebBooking(){
    	var isSmsClient="";
    	var isSmsBroker=""	;
    	var otherNo="";	
    	var allowStatus="";
    	var cancelReason=""
    	if (allowPermission == "C"){ 
    		allowStatus=$("#cancelPermission").val();
    		cancelReason=$("#cancelReason1").val();
		}
    	else if(allowPermission == "A"){
    		allowStatus="A";
    	}
    	
    	if (document.getElementById('smsClient').checked == true){ 
    		isSmsClient="Y";
		}
    	else if (document.getElementById('smsBooker').checked == true){ 
    		isSmsBroker="Y";
    	}
    	
    	otherNo=  $("#dispatchSmsOther").val();
	 if(!isValidationError){
		 $.ajax({
			type : "POST",
			url:  ctx + '/approveORCancelWebBooking.html',
		    data: 'idForUpdate='+idForUpdate+'&allowStatus='+allowStatus+'&cancelReason='+cancelReason+'&isSmsClient='+isSmsClient+'&isSmsBroker='+isSmsBroker+'&otherNo='+otherNo, 
			async: false,
			success : function(response) {
				if (response.status == "Success") {
					bootbox.dialog({
    					  message: "Booking Successfully Saved!",
    					  title: "",
    					  buttons: {
    					    success: {
    					      label: "Ok!",
    					      className: "btn-success",
    					   }					   					    
    					  },
    					  close:false
    					});	
				}else {
					bootbox.dialog({
  					  message: " SomeThing Going Wrong",
  					  title: "Success!",
  					  buttons: {
  					    success: {
  					      label: "Ok!",
  					      className: "btn-danger",
  					   }					   					    
  					  },
  					  close:false
  					});	
				}
			},
			error : function(e) {
		    	$("#dataLoaderModal").hide();
		    	$("#dataLoaderFade").hide();
		    	alert('Error: ' + e);
			}
      });	
    }
       $("#bookingCancelPanel").hide();
       searchBooking();
    }
    
    function hideAllowPermission(){
    	$("input:radio").attr("checked", false);
    	$("#errorValidate").hide();
    	$("#editDetailRecordPanel").modal('hide');
    }

    function checkAllowPermissionStatus(){
      	if (allowPermission == "" ){ 
    		isValidationError=true;
      		$('#errorValidate').html("Please enter Valid Action");
    		$('#errorValidate').show();
      	}else if ( allowPermission == "C" && $("#cancelReason1").val() == ""){ 
      		$('#errorValidate').html("Please enter reject reason");
    		$('#errorValidate').show();
      		isValidationError=true;
      	}

      	if($("#startTime").val()!=""){
		    if(!checkStart()){
			    $('#errorValidate').html("Start Time can not be less than current Date and Time.</li>");
				$('#errorValidate').show();	
			    isValidationError = true;
		    }
		}else{
		    $('#errorValidate').html("Start Time can not be empty.</li>");
			$('#errorValidate').show();	
		    isValidationError = true;
		}
    }

    function checkStart() {
    	var start = ($("#pickupDate").val()).concat(" ").concat($("#startTime").val());
    	var today = getTimeStampTo_ddMMyyyyHHmmss(new Date(),'dd/mm/yyyy hh:mm')
    	var ms = moment(start, "DD/MM/YYYY HH:mm").diff(moment(today, "DD/MM/YYYY HH:mm"));
    	var d = moment.duration(ms);
    	if (d < 0) {
    		return false;
    	}
    	return true;
    }
    
    function showPassengerInfoPanel(){
    	$('#passengerInfoPanel').modal('show');
      }

    function addNewRow(){
    	i++;  
    	$("#passengerTable").append("<tr id='row_"+i+"'>" +
    		"	<td style='display:none'><label id='id_"+i+"'></label></td>"+	
    		"		<td><input type='button' id='btnMin_"+i+"'  class='btn' style='background : #FF9300' value='-' onclick='removeRow(this.id)'/></td>"+
    		"     	<td><input type='text' class='form-control' id='passengerName_"+i+"'/></td>" +
    		"	  	<td><input type='text' class='form-control mob' id='passengerMob_"+i+"' maxlength='13' onkeypress='isNumeric(event)'/></td>" +
    		"     	<td><input type='text' class='form-control' id='passengerAge_"+i+"' onkeypress='isNumeric(event)'/></td>" +
    		"     	<td>" +
    		"				<select class='form-control' id='passengerSex_"+i+"' name='passengerSex' >" +
    	    "					<option value='Male'>Male</option>"+
    	    "				    <option value='Female'>Female</option>" +
    	    "				    <option value='Other'>Other</option>" +
    		"				</select>" +
    		"		</td>" +
    		"   	<td><input type='text' class='form-control' id='passengerId_"+i+"'/></td>" +
    		"	</tr>");
    	}

    function removeRow(id){
    	i--;
    	var btnId = id.split("_");
    	$("#row_"+btnId[1]).remove();
    }
    
    function getPassengerDetails(){
    	var count=1;
    	var addOrRemoveFunction="";
    	var buttonValue="";	
    	form_data = new FormData();
    	form_data.append("bookingDetailId",idForUpdate);
         $.ajax({  
   		 type: "POST",
   		 url:  ctx + "/getPassengerDetails.html",
   		 processData: false,
   	     contentType: false,
   	     data : form_data,
   	     async:false,
   	     success : function(response) {
   		      if (response.status == "Success") {
   		    	 var recordSize = response.passengerDetailModelList.length; 
   		    	 $("#passengerTable").empty();
   		    	 $("#passengerTable").append("<tr bgcolor='#FFDA71' style='font-family: cambria;font-size: 14; height: 10px;'>" +
   		    	 					"		<th style='display:none'>id</th>" +
   		    	 					"		<th>Add</th>" +
   		    	 					"		<th>Name.</th>" +
   		    	 					"		<th>Mobile No</th>" +
   		    	 					"		<th>Age.</th>" +
   		    	 					"		<th>Sex</th>" +
   		    	 					"		<th>Id-Proof</th>" +
   		    	 					"</tr>");
   		    	 for(i = 1; i<= recordSize; i++){
   		    		if(i!=1){
   		    			addOrRemoveFunction="'removeRow(this.id)'";
   		    			buttonValue="'-'"
   		    		 }else{
   		    				addOrRemoveFunction="'addNewRow()'";
   		    				buttonValue="'+'"
   		    		 }
   		    		 $("#passengerTable").append("<tr id='row_"+i+"'>" +
		    		    		"	<td style='display:none'><label id='id_"+i+"'></label></td>"+	
		    		    		"		<td><input type='button' id='btnMin_"+i+"'  class='btn' style='background : #FF9300' value="+buttonValue+" onclick="+addOrRemoveFunction+"/></td>"+
		    		    		"     	<td><input type='text' class='form-control' id='passengerName_"+i+"'/></td>" +
		    		    		"	  	<td><input type='text' class='form-control mob' id='passengerMob_"+i+"' maxlength='13' onkeypress='isNumeric(event)'/></td>" +
		    		    		"     	<td><input type='text' class='form-control' id='passengerAge_"+i+"' onkeypress='isNumeric(event)'/></td>" +
		    		    		"     	<td>" +
		    		    		"				<select class='form-control' id='passengerSex_"+i+"' name='passengerSex' >" +
		    		    	    "					<option value='Male'>Male</option>"+
		    		    	    "				    <option value='Female'>Female</option>" +
		    		    	    "				    <option value='Other'>Other</option>" +
		    		    		"				</select>" +
		    		    		"		</td>" +
		    		    		"   	<td><input type='text' class='form-control' id='passengerId_"+i+"'/></td>" +
		    		    		"	</tr>");
   		    	 			}
   		    	 		$.each(response.passengerDetailModelList, function(index,val){
   		    	 				 $("#id_"+count).text(val.id);
				    			 $("#passengerName_"+count).val(val.name);
				    			 $("#passengerMob_"+count).val(val.mobile);
				    			 $("#passengerAge_"+count).val(val.age);
				    			 $("#passengerSex_"+count).val(val.sex);
				    			 $("#passengerId_"+count).val(val.idDetails);
				    			 count++;
		   		    	 	}); 
   		    	 		i=i-1;
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
    
    function updatePassengerDetails(){
    	form_data = new FormData();
    	$(".mob").each(function(){
		 	var id = (this.id).split("_"); 
	 		form_data.append("name",$("#passengerName_"+id[1]).val());
			form_data.append("mobile",$("#passengerMob_"+id[1]).val());
			form_data.append("age",$("#passengerAge_"+id[1]).val());
			form_data.append("sex",$("#passengerSex_"+id[1]).val());
			form_data.append("idDetails",$("#passengerId_"+id[1]).val());
		});
    	form_data.append("idForUpdate",idForUpdate);
		$.ajax({
			type : "POST",
			url:  ctx + '/updatePassengerDetails.html',
			processData: false,
			contentType: false,
			data : form_data,
			async:false,
			success : function(response) {
				if (response.status == "Success"){
			} 
			},
			error : function(e) {
			   alert('Error: ' + e);
			}
	     });	
		
    }

