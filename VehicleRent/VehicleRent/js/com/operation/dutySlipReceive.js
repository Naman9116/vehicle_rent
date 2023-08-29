
	function resetData(){
		document.forms[0].reset();
	    $('#idForUpdate').val('');
	  	document.getElementById("addButton").disabled=false;
	  	document.getElementById("currentPage").value=1;
	}
	
	function getNextPageData(){
		pageNumber=pageNumber+1;
		getDutySlipData_DutySlipReceive();
	}
	
	function getPreviousPageData(){
		pageNumber=pageNumber>1?pageNumber-1:pageNumber;
		getDutySlipData_DutySlipReceive();
	}
	
	function saveOrUpdateDutySlipReceive() {
		var dutySlipReceiveId="";
		var parkingAmount="";
		var tollTaxAmount="";
		var receivedBy="";
        $('.dutySlipId').each(function() {
        	if(this.checked){
        		dutySlipReceiveId = dutySlipReceiveId+this.value+",";
        		parkingAmount = parkingAmount + ($.trim($('#parkingAmount'+this.value).val())!=""?$.trim($('#parkingAmount'+this.value).val()):0.00)+",";
        		tollTaxAmount = tollTaxAmount + ($.trim($('#tollTaxAmount'+this.value).val())!=""?$.trim($('#parkingAmount'+this.value).val()):0.00)+",";
        		receivedBy = receivedBy + ($('#receivedBy'+this.value).val()!=""?$('#receivedBy'+this.value).val():-1)+",";
        	}
        });
        if(dutySlipReceiveId=="")
        	return false;
        if (receivedBy.indexOf("-1") != -1){
        	alert("Please Select Received by value ");
        	return false;
        }
	  	$("#addButton").attr("disabled", true);
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/saveOrUpdateDutySlipReceive.html",  
		    data: "pageNumber="+pageNumber+"&dutySlipReceiveId="+dutySlipReceiveId+"&tollTaxAmount="+tollTaxAmount+"&parkingAmount="+parkingAmount+"&receivedBy="+receivedBy,  
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
	
	function getDutySlipData_DutySlipReceive(){
		//document.getElementById("currentPage").value=pageNumber;
		var searchCriteria=document.getElementById("searchCriteria").value;
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/getDutySlipData_DutySlipReceive.html",  
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
	
