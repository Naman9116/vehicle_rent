saveUpdateUrl = ctx + "/saveOrUpdateBookingMaster.html";
var form_data= new FormData();
var bookingDetailsModelArray = new Array();

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

	var allUserModels=[];
	var notMappedUserModels=[];
	var toBeMappedUserModels=[];
	function getDetail(){
		var corporateId=$('#corporateId').val();
		var branch=$('#branch').val();
		var outlet=$('#outlet').val();
		var bookedBy=$('#bookedBy').val();
		var shiftTiming=$('#shiftTiming').val();
		var rosterTaken=$('#rosterTaken').val();
		var routeNumber=$('#routeNumber').val();
		var rosterFromDate=$('#rosterFromDate').val();
		var rosterToDate=$('#rosterToDate').val();
		
		//alert(corporateId+" : "+branch+" : "+outlet+" : "+bookedBy+" : "+shiftTiming+" : "+rosterTaken+" : "+routeNumber+" : "+rosterFromDate+" : "+rosterToDate);
		var form_data = new FormData();
		form_data.append("corporateId", corporateId);
		form_data.append("branchId", branch);
		form_data.append("outletId", outlet);
		form_data.append("bookedById", bookedBy);
		form_data.append("shiftTimeId", shiftTiming);
		form_data.append("rosterTaken", rosterTaken);
		form_data.append("routeNumber", routeNumber);
		form_data.append("rosterFromDate", rosterFromDate);
		form_data.append("rosterToDate", rosterToDate);
		
		if(corporateId <= 0) return;
		$.ajax({
			type: "POST",
			processData: false,
		    contentType: false,
			async: false,
			url:  ctx + "/getUsersListByRoster.html",  
			data: form_data,  
			success : function(response) {
				if (response.status == "Success") {
					allUserModels=response.autorizedUserModels;
					notMappedUserModels=allUserModels;
					
					$('#divCars').html(response.dataString1);
					
					notMappedUsersValuesListBox();
			    	toBeMappedUsersValuesListBox();
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
	function notMappedUsersValuesListBox(){
		var dataString="<table width='100%' >";
			
		$.each(notMappedUserModels, function(index,val) {
			dataString=dataString+"<tr>";
			dataString=dataString+"<td align='left'><input type='checkbox' class='notMappedCheckBox' name='subMasterValues' value='"+ val.id+ "'/><input type='hidden' id='userIndex_"+val.id+"' value='"+index+"'></input>"
					+ "<td width='90%' align='left'>"+val.name+"</td>"
					+ "</tr>";
		});
		dataString=dataString+"</table>";
		$('#notMappedDiv').html(dataString);
	}
	function toBeMappedUsersValuesListBox(){
		var dataString="<table width='100%' >";
			
		$.each(toBeMappedUserModels, function(index,val) {
			dataString=dataString+"<tr>";
			dataString=dataString+"<td align='left'><input type='checkbox' class='alreadyMappedCheckBox' name='subMasterValues' value='"+ val.id+ "'/><input type='hidden' id='userIndex_"+val.id+"' value='"+index+"'></input>"
					+ "<td width='90%' align='left'>"+val.name+"</td>"
					+ "</tr>";
		});
		dataString=dataString+"</table>";
		$('#alreadyMappedDiv').html(dataString);
	}
	function moveUsersRight(){
		//var movedCount=0;
		$('.notMappedCheckBox').each(function() {
        	if(this.checked){
        		var index=parseInt($("#userIndex_"+this.value).val());
        		toBeMappedUserModels.push(notMappedUserModels[index]);
        		//index=index-movedCount;
        		notMappedUserModels.splice(index, 1);
        		//movedCount++;

        		notMappedUsersValuesListBox();
            	toBeMappedUsersValuesListBox();
        	}
        });
	}
	function moveUsersLeft(){
		//var movedCount=0;
		$('.alreadyMappedCheckBox').each(function() {
        	if(this.checked){
        		var index=parseInt($("#userIndex_"+this.value).val());
        		notMappedUserModels.push(toBeMappedUserModels[index]);
        		//index=index-movedCount;
        		toBeMappedUserModels.splice(index, 1);
        		//movedCount++;

        		notMappedUsersValuesListBox();
            	toBeMappedUsersValuesListBox();
        	}
        });
	}

	function SaveBookingForRoster(actionType,updateIndex){
		/*if(updateIndex !='')
			validateBookingDetails(actionType,updateIndex);
		else
			validateBookingDetails(actionType,'');*/
		
		var userIds="";
		$.each(toBeMappedUserModels, function(index,val) {
			if(userIds.length==0){
				userIds=val.id;
			}else{
				userIds=userIds+","+val.id;
			}
		});
		isAdd=true;
		if(!isValidationError){
			//checkDuplicateEntry();
			//if(!isDuplicate){
			var actionType="S";
				if(actionType == "N" && form_data.get("corporateId.id") != null) return;
				if((form_data.get("corporateId.id")==null || actionType == "U" || actionType == "S" || actionType == "O" || actionType == "A") && (updateIndex == '')){	    			    		
					form_data = new FormData();
					bookingDetailsModelArray = new Array();
				}
				$("#bookingDetailsDate").val(getTimeStampTo_DDMMYYYY(new Date()));
				form_data.set("bookingNo", "");
				form_data.set("bookingDate", getTimeStampTo_DDMMYYYY(new Date())); 
				form_data.set("corporateId.id", $('#corporateId').val());
				form_data.set("corporateId.name", $('#corporateId option:selected').text());
				form_data.set("bookedBy.id", $('#bookedBy').val());
				form_data.set("bookedBy.name", $('#bookedBy option:selected').text());
				//form_data.set("bookedFor", $("#opClient").prop('checked')==true?"C":"O"); 
				form_data.set("bookedFor", "E"); 
				form_data.set("bookedForName", userIds); 
				form_data.set("mobileNo", "");
				form_data.set("referenceNo", "");
				form_data.set("bookingTakenBy.id", loginUserId);
				form_data.set("smsToClient", /*$('#smsToClient').is(':checked')?"Y":*/"N");
				form_data.set("smsToBooker", /*$('#smsToBooker').is(':checked')?"Y":*/"N");
				form_data.set("smsToOther", "N");
				form_data.set("emailToClient", /*$('#emailToClient').is(':checked')?"Y":*/"N");
				form_data.set("emailToBooker", /*$('#emailToBooker').is(':checked')?"Y":*/"N");
				form_data.set("emailToOther", "N");
				form_data.set("sCriteria", "");
				form_data.set("sValue", "");
				form_data.set("companyName", companyName); 
				form_data.set("companyCode", companyCode); 
				form_data.set("cityName", $('#outlet option:selected').text());
				form_data.set("clientEmail", "");
				form_data.set("bookerEmail", "");

				var bookingDetailsModelArraySize=0,loopCount=0;
				var iCount = 0;
				
				loopCount = daysBetween($('#rosterFromDate').val(), $('#rosterToDate').val()) + 1; 
				fromIndex = bookingDetailsModelArray.length;
				toIndex   = ( fromIndex - 0) + loopCount;

				for (; iCount < (toIndex - fromIndex); iCount++) {
					form_data.set("bookingDetailModel["+iCount+"].company.id", companyName);
					form_data.set("bookingDetailModel["+iCount+"].branch.id", $('#branch').val());
					form_data.set("bookingDetailModel["+iCount+"].outlet.id", $('#outlet').val());
					form_data.set("bookingDetailModel["+iCount+"].outlet.name", $('#outlet option:selected').text());
					/*if (document.getElementById('opDateRange').checked){*/
						if($("#rosterFromDate").val() == "") $("#rosterFromDate").val($("#bookingDetailsDate").val()); //$("#bookingDetailsDate").val()
						form_data.set("bookingDetailModel["+iCount+"].pickUpDate", addDays($('#rosterFromDate').val(),iCount));
					/*}else{
						form_data.set("bookingDetailModel["+iCount+"].pickUpDate", $('#bookingDetailsDate').val());
					}*/
					form_data.set("bookingDetailModel["+iCount+"].startTime", "09:00");//TBD
					form_data.set("bookingDetailModel["+iCount+"].pickUpTime", "09:30");//TBD
					form_data.set("bookingDetailModel["+iCount+"].flightNo", "");
					//if($('#terminal').val()!="0") form_data.set("bookingDetailModel["+iCount+"].terminal.id", $('#terminal').val());
					form_data.set("bookingDetailModel["+iCount+"].mob.id", "226");
					form_data.set("bookingDetailModel["+iCount+"].mob.name", "Credit");
		
					form_data.set("bookingDetailModel["+iCount+"].carModel.id", $('#cars').val()); //TBD
					form_data.set("bookingDetailModel["+iCount+"].carModel.name", $('#cars option:selected').text()); //TBD
					form_data.set("bookingDetailModel["+iCount+"].rentalType.id", "378"); 
					form_data.set("bookingDetailModel["+iCount+"].rentalType.name", "Roster"); 
					form_data.set("bookingDetailModel["+iCount+"].tariff.id", "36"); 
					form_data.set("bookingDetailModel["+iCount+"].reportAt", "E"); 
					form_data.set("bookingDetailModel["+iCount+"].reportingAddress", "ETS"/*$("#opOffice").prop('checked')==true?$("#officeAddress option:selected").text():$("#reportingAddress").val()*/); //TBD
					form_data.set("bookingDetailModel["+iCount+"].toBeRealeseAt", "ETS");
					form_data.set("bookingDetailModel["+iCount+"].instruction", ""); 

					if(actionType=="A")
						bookingDetailsModelArray.push($('#branchName').val());
					else if(actionType=="U" && updateIndex != '0'){
		
					}
					else
						bookingDetailsModelArray = new Array();
				}
				
				form_data.set("idForUpdate", "0");
				form_data.set("idForUpdateDetail", "0");
				
			/*}else{		    	      
			}*/	
				saveOrUpdateRecord(form_data);
		}
	}	
	
	function saveBooking(type) {
		checkAllBlankDetailFields();
		if(!isBlank){
			AssignFormData(type,'');
			 /*$(".mob").each(function(){
			 	var id = (this.id).split("_"); 
		 		form_data.append("name",$("#passengerName_"+id[1]).val());
				form_data.append("mobile",$("#passengerMob_"+id[1]).val());
				form_data.append("age",$("#passengerAge_"+id[1]).val());
				form_data.append("sex",$("#passengerSex_"+id[1]).val());
				form_data.append("idDetails",$("#passengerId_"+id[1]).val());
			});*/
		}
		if(isValidationError){
			if(bookingDetailsModelArray.length > 0 && type == 'N'){
				if(confirm("Are you sure to save this booking without including current booking details.")){
					form_data.set("idForUpdate", $('#idForUpdate').val());
					form_data.set("idForUpdateDetail", "");
					saveOrUpdateRecord(form_data);
					if(!isServerSideError)
						refreshData();			    
				}else{
					return false;
				}
			}
		}else{		    
			if(type == 'O'){
				form_data.set("idForUpdate", "");
			}else{
				form_data.set("idForUpdate", $('#idForUpdate').val());
			}		    	
			form_data.set("idForUpdateDetail", "");
			if(form_data.get("bookingDetailModel[0].branch.id")==null){
				return false;
			}
			saveOrUpdateRecord(form_data,type);
			if(type != "O" && serviceResponse != null){
			    if(serviceResponse.bookingMasterModel !=null){
					$('#bookingNo').val(serviceResponse.bookingMasterModel.bookingNo);
					$('#idForUpdate').val(serviceResponse.bookingMasterModel.id);
			    }
			    $('#dataTable2').html(serviceResponse.dataGrid);					   				    
			}					    				

			if(type == 'O' && !isServerSideError){
				userTable.fnDestroy();
			}
			if(!isServerSideError)
				resetData();
		}
		document.location.reload();
	}
	
	
	/*function saveOrUpdateMappingMaster() {
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
	
	*/
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
        }else{
            $('.alreadyMappedCheckBox').each(function() { 
               	this.checked = false;
            });         
        }
	}

