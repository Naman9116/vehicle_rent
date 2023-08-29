
	function resetData(){
		document.forms[0].reset();
	    $('#idForUpdate').val('');
	  	document.getElementById("addButton").disabled=false;
	  	document.getElementById("currentPage").value=1;
	}
	
	function getNextPageData(){
		pageNumber=pageNumber+1;
		getBookingData_DutySlip();
	}
	
	function getPreviousPageData(){
		pageNumber=pageNumber>1?pageNumber-1:pageNumber;
		getBookingData_DutySlip();
	}
	
	function saveOrUpdateDutySlip() {
		var bookingNo=$('#bookingNo').val();
		var bookingDetailId= $('input[name=bookingDetailId]:checked').val();
		var manualSlipNo=$('#manualSlipNo').val();
		var dutySlipNo=$('#dutySlipNo').val();
		var dutySlipDate=$('#dutySlipDate').val();
		var openKms=$('#openKms').val();
		var timeFrom=$('#timeFrom').val();
		var dateFrom=$('#dateFrom').val();
		var driverAdvance=$('#driverAdvance').val();
		var minChgKms=$('#minChgKms').val();
		var minChgHrs=$('#minChgHrs').val();
		var extraChgKms=$('#extraChgKms').val();
		var extraChgHrs=$('#extraChgHrs').val();
		var pickupFrom=$('#pickupFrom').val();
		var dropTo=$('#dropTo').val();
		var ownType=$('#ownTypeHidden').val();
		var driverName=$('#driverName').val();
		var remarks=$('#remarks').val();
		var driverDetails_name=$('#driverDetails_name_Hidden').val();
		var driverDetails_mobileNo=$('#driverDetails_mobileNo_Hidden').val();
		
	  	$("#addButton").attr("disabled", true);
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/saveOrUpdateDutySlip.html",  
		    data: "pageNumber="+pageNumber+"&bookingMasterModel.id="+bookingNo+"&bookingDetailModel.id="+bookingDetailId+"&manualSlipNo=" + manualSlipNo
		    	  +"&dutySlipNo="+dutySlipNo+"&dutySlipDate="+dutySlipDate
		    	  +"&openKms="+openKms+"&timeFrom="+timeFrom+"&dateFrom="+dateFrom+"&driverAdvance="+driverAdvance
		    	  +"&minChgKms="+minChgKms+"&minChgHrs="+minChgHrs +"&extraChgKms="+extraChgKms+"&extraChgHrs="+extraChgHrs
		    	  +"&pickupFrom="+pickupFrom+"&dropTo="+dropTo +"&ownType.id="+ownType+"&driverNameId="+driverName +"&remarks="+remarks
		    	  +"&additionalDriverDetailModel.name="+driverDetails_name+"&additionalDriverDetailModel.mobileNo="+driverDetails_mobileNo,  
		    success: function(response){
		      	if(response.status == "Success"){
			    	$('#info').html(response.result);
			    	$('#dataTable2').html(response.dataGrid);
			    	resetData();
			    	//$('#dataTable4').html('');
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
	  	setTimeout(function() { document.getElementById("addButton").disabled=false;},3000);
	}  
	
	function getBookingData_DutySlip(){
		document.getElementById("currentPage").value=pageNumber;
		var searchCriteria=document.getElementById("searchCriteria").value;
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/getBookingData_DutySlip.html",  
		    data: "pageNumber=" + pageNumber+"&searchCriteria="+searchCriteria,  
		    success: function(response){
		      	if(response.status == "Success"){
			    	$('#dataTable2').html(response.dataGrid);
				}
		      	else{
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
		      	addClassForColor();
			},  
		    error: function(e){  
		      alert('Error: ' + e);  
		    }  
		});  
	}
	
	function getVehicleTypeData_DutySlip_Search(){
		getVehicleTypeData_DutySlip(carTypeId,bookingId);
	}
	
	function getDataOnBookingSelection_DutySlip(bookingDetailId){
		//$('#bookingNo').val(bookingDetailId);
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/getDataOnBookingSelection_DutySlip.html",  
		    data: "bookingDetailId="+bookingDetailId,  
		    success: function(response){
		      	if(response.status == "Success"){
		      		if(response.dataArray!=null){
			    		$('#bookingNo').val(response.dataArray[0]!=null?response.dataArray[0]:"");
			    		$('#minChgKms').val(response.dataArray[1]!=null?response.dataArray[1]:"");
			    		$('#minChgHrs').val(response.dataArray[2]!=null?response.dataArray[2]:"");
			    		$('#extraChgKms').val(response.dataArray[3]!=null?response.dataArray[3]:"");
			    		$('#extraChgHrs').val(response.dataArray[4]!=null?response.dataArray[4]:"");
			    		$('#driverName').val(response.dataArray[5]!=null?response.dataArray[5]:"");
			    		$('#ownType').val(response.dataArray[6]!=null?response.dataArray[6]:"");
			    		$('#ownTypeHidden').val(response.dataArray[6]!=null?response.dataArray[6]:"");
			    		if($('#ownType').find('option:selected').text()=="DCO Scheme"){
			    			$('#addDiverNameButton').attr("disabled", false);
			    		}
		    		}
		      		else{
			    		$('#bookingNo').val("");
			    		$('#minChgKms').val("");
			    		$('#minChgHrs').val("");
			    		$('#extraChgKms').val("");
			    		$('#extraChgHrs').val("");
			    		$('#ownType').val("");
			    		$('#driverName').val("");
		      		}
				}
		      	else{
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
		      	addClassForColor();
			},  
		    error: function(e){  
		      alert('Error: ' + e);  
		    }  
		});  
	}
	
	
	
	
