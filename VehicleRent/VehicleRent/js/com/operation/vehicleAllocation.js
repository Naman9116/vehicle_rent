
	function resetData(){
	    $('#idForUpdate').val('');
	  	document.getElementById("addButton").disabled=false;
	  	document.getElementById("currentPage").value=1;
	}
	
	function getNextPageData(){
		pageNumber=pageNumber+1;
		getBookingData_VehicleAllocation();
	}
	
	function getPreviousPageData(){
		pageNumber=pageNumber>1?pageNumber-1:pageNumber;
		getBookingData_VehicleAllocation();
	}
	
	function saveOrUpdateVehicleAllocation() {
		var bookingId= $('input[name=bookingId]:checked').val();
		var vehicleIdSelectedValue="";
        $('.vehicleId').each(function() {
        	if(this.checked){
        		vehicleIdSelectedValue=vehicleIdSelectedValue+this.value+",";
        	}
        });
        if(vehicleIdSelectedValue==""){
        	alert("Please Check Atleast One Vehicle ");
        	return false;
        }
	  	$("#addButton").attr("disabled", true);
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/saveOrUpdateVehicleAllocation.html",  
		    data: "pageNumber="+pageNumber+"&bookingId.id="+bookingId+"&vehicleIdSelectedValue=" + vehicleIdSelectedValue,  
		    success: function(response){
		      	if(response.status == "Success"){
			    	$('#info').html(response.result);
			    	$('#dataTable2').html(response.dataGrid);
			    	resetData();
			    	$('#dataTable4').html('');
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
	
	function getBookingData_VehicleAllocation(){
		document.getElementById("currentPage").value=pageNumber;
		var searchCriteria=document.getElementById("searchCriteria").value;
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/getBookingData_VehicleAllocation.html",  
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
	
	function getVehicleTypeData_VehicleAllocation_Search(){
		var carTypeId=$('#carTypeIdHidden').val();
		var bookingId=$('#bookingIdHidden').val();
		getVehicleTypeData_VehicleAllocation(carTypeId,bookingId);
	}
	
	function getVehicleTypeData_VehicleAllocation(carTypeId,bookingId){
		$('#carTypeIdHidden').val(carTypeId);
		$('#bookingIdHidden').val(bookingId);
		var searchCriteria=document.getElementById("searchCriteria_vehicle").value;
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/getVehicleTypeData_VehicleAllocation.html",  
		    data: "carTypeId="+carTypeId+"&bookingId.id="+bookingId+"&searchCriteria="+searchCriteria,  
		    success: function(response){
		      	if(response.status == "Success"){
			    	$('#dataTable4').html(response.dataGrid);
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
	
	