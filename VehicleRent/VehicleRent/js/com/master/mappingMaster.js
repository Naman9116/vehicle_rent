
	function resetAccordingMasterValues(){
		$('#subMasterId').val('0');
    	$('#notMappedDiv').html("");
    	$('#alreadyMappedDiv').html("");
    	$('#error').hide('slow');
	    $('#info').hide('slow');
	}
	
	function resetData(){
		$('#masterValue').val('0');
		$('#subMasterId').val('0');
    	$('#notMappedDiv').html("");
    	$('#alreadyMappedDiv').html("");
    	$('#error').hide('slow');
	    $('#info').hide('slow');
	}
	
	function saveOrUpdateMappingMaster() {
		var operationType;
		$('#idForUpdate').val() > 0?operationType = "E":operationType = "S";
		if(isAccessDenied(operationType)) return;		

		var masterValue = $('#masterValue').val();
		var subMasterId = $('#subMasterId').val();
		var selectedNotMappedCheckBoxValues="";
        $('.notMappedCheckBox').each(function() {
        	if(this.checked){
        		selectedNotMappedCheckBoxValues=selectedNotMappedCheckBoxValues+this.value+",";
        	}
        });
        if(selectedNotMappedCheckBoxValues==""){
        	alert("Please Check Atleast One Value For Mapping");
        	return false;
        }
        else if(confirm("Are You Sure, You Want to Map Checked Values ?")){
		  	$.ajax({  
			    type: "POST",  	
			    url:  ctx + "/saveOrUpdateMappingMaster.html",  
			    data: "masterValue="+masterValue+"&subMasterId=" + subMasterId+ "&selectedNotMappedCheckBoxValues=" + selectedNotMappedCheckBoxValues,  
			    success: function(response){
			      	if(response.status == "Success"){
					    fetchSubMasterValuesMappingMaster(document.getElementById('subMasterId').value);
				    	$('#info').html(response.result);
					    $('#error').hide('slow');
					    $('#info').show('slow');
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
			      	addClassForColor();
				},  
			    error: function(e){  
		           alert(thrownError);
			    }  
			});  
        }
	}  
	
	function removeOrUpdateMappingMaster(){
		var masterValue = $('#masterValue').val();
		var subMasterId = $('#subMasterId').val();
		var selectedAlreadyMappedCheckBoxValues="";
        $('.alreadyMappedCheckBox').each(function() {
        	if(this.checked){
        		selectedAlreadyMappedCheckBoxValues=selectedAlreadyMappedCheckBoxValues+this.value+",";
        	}
        });
        if(selectedAlreadyMappedCheckBoxValues==""){
        	alert("Please Check Atleast One Value For UnMapping");
        	return false;
        }
        else if(confirm("Are You Sure, You Want to UnMapped Checked Values ?")){
		  	$.ajax({  
			    type: "POST",  	
			    url:  ctx + "/removeOrUpdateMappingMaster.html",  
			    data: "masterValue="+masterValue+"&subMasterId=" + subMasterId+ "&selectedAlreadyMappedCheckBoxValues=" + selectedAlreadyMappedCheckBoxValues,  
			    success: function(response){
			      	if(response.status == "Success"){
				    	$('#info').html(response.result);
				    	//resetData();
					    $('#error').hide('slow');
					    $('#info').show('slow');
					    fetchSubMasterValuesMappingMaster(document.getElementById('subMasterId').value);
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
			      	addClassForColor();
				},  
			    error: function(e){  
		           alert(thrownError);
			    }  
			});
        }
	}  	
	
	function fetchMasterValuesAndSubMasterNamesMappingMaster(selectedId){
		if(selectedId=="")
			return false;
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/fetchMasterValuesAndSubMasterNamesMappingMaster.html",  
		    data: "selectedId="+selectedId,  
		    success: function(response){
		      	if(response.status == "Success"){
		      		$('#info').html(response.result);
		      		var objects =response.dataGrid.split("$$");
		      		$('#masterValueDiv').html(objects[0]);
		      		$('#subMasterIdDiv').html(objects[1]);
			    	$('#notMappedDiv').html("");
			    	$('#alreadyMappedDiv').html("");
					$('#info').hide('slow');
					$('#error').hide('slow');
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
	
	function fetchSubMasterValuesMappingMaster(selectedId){
		var parentId= $('#masterValue').val();
		if(selectedId=="")
			return false;
		if(parentId=="")
			return false;
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/fetchSubMasterValuesMappingMaster.html",  
		    data: "selectedId="+selectedId+"&parentId="+parentId,  
		    success: function(response){
		      	if(response.status == "Success"){
			    	$('#notMappedDiv').html(response.dataString1);
			    	$('#alreadyMappedDiv').html(response.dataString2);
			    	var temp="";
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
	
	
	function selectAllNotMappedCheckBox(){
		var checked=document.getElementById("selectAllNotMapped").checked;
        if(checked) {
            $('.notMappedCheckBox').each(function() { 
           		this.checked = true;                 
            });
        }
        else{
            $('.notMappedCheckBox').each(function() { 
               	this.checked = false;
            });         
        }
	}

	function selectAllAlreadyMappedCheckBox(){
		var checked=document.getElementById("selectAllAlreadyMapped").checked;
        if(checked) {
            $('.alreadyMappedCheckBox').each(function() { 
           		this.checked = true;                 
            });
        }
        else{
            $('.alreadyMappedCheckBox').each(function() { 
               	this.checked = false;
            });         
        }
	}

