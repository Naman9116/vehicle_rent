
	function resetData(){
		document.forms[0].reset();
	    $('#idForUpdate').val('');
	  	document.getElementById("addButton").disabled=false;
	  	document.getElementById("currentPage").value=1;
	}
	
	function getNextPageData(){
		pageNumber=pageNumber+1;
		getDutySlipData_DutySlipInvoice();
	}
	
	function getPreviousPageData(){
		pageNumber=pageNumber>1?pageNumber-1:pageNumber;
		getDutySlipData_DutySlipInvoice();
	}
	
	function saveOrUpdateDutySlipInvoice() {
		var dutySlipId="";
		var receivedBy="";
        $('.dutySlipId').each(function() {
        	if(this.checked){
        		dutySlipId = dutySlipId+this.value+",";
        	}
        });
        if(dutySlipId=="")
        	return false;

        $("#addButton").attr("disabled", true);
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/saveOrUpdateDutySlipInvoice.html",  
		    data: "pageNumber="+pageNumber+"&dutySlipId="+dutySlipId,  
		    success: function(response){
		      	if(response.status == "Success"){
			    	$('#info').html(response.result);
			    	$('#dataTable2').html(response.dataGrid);
			    	resetData();
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
	
	function getDutySlipData_DutySlipInvoice(){
		var searchCriteria=$('#corporate').val()+","+$('#bookedBy').val()+","+$('#usedBy').val()+","+$('#closedBy').val()+","+$('#hub').val();
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/getDutySlipData_DutySlipInvoice.html",  
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
	
