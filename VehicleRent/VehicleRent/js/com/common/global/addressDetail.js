	function resetAddressDetails(){
		$("#addressDetailModel_id").val("");
		$("#address1").val("");
		$("#address2").val("");
		$("#landMark").val("");
		$("#pincode").val("");
		$("#state").val(0);
	}
	
	var responseObj_addressDetail = null;	
	function saveAddressDetailForm(){
		form_data.append("addressDetailModel.id", $("#addressDetailModel_id").val()== undefined ? "":$("#addressDetailModel_id").val());
		form_data.append("addressDetailModel.address1", $("#address1").val()== undefined ? "":$("#address1").val());
		form_data.append("addressDetailModel.address2", $("#address2").val()== undefined ? "":$("#address2").val());
		form_data.append("addressDetailModel.landMark", $("#landMark").val()== undefined ? "":$("#landMark").val());
		form_data.append("addressDetailModel.pincode", $("#pincode").val()== undefined ? "":$("#pincode").val());
		form_data.append("addressDetailModel.state.id", $("#state").val()== undefined ? "":$("#state").val());
		form_data.append("addressDetailModel.district.id", $("#district").val()== undefined ? "":$("#district").val());
		form_data.append("addressDetailModel.city.id", $("#city").val()== undefined ? "":$("#city").val());
	}
	
	function setAddressValues(){
		$("#addressDetailModel_id").val(responseObj_addressDetail.addressDetailModel.id);
		$("#address1").val(responseObj_addressDetail.addressDetailModel.address1);
		$("#address2").val(responseObj_addressDetail.addressDetailModel.address2);
		$("#landMark").val(responseObj_addressDetail.addressDetailModel.landMark);
		$("#pincode").val(responseObj_addressDetail.addressDetailModel.pincode);
		$("#state").val(responseObj_addressDetail.addressDetailModel.state.id);
		getDistrictData(responseObj_addressDetail.addressDetailModel.state.id,responseObj_addressDetail.addressDetailModel.district.id);
		getCityData(responseObj_addressDetail.addressDetailModel.district.id,responseObj_addressDetail.addressDetailModel.city.id);
	}

	function loadAddressDetailForm(divName){
		var addressDetailModel="";
		var addressDetailModel_id=$("#addressDetailModel_id").val();
		var address1=$("#address1").val();
		var address2=$("#address2").val();
		var landMark=$("#landMark").val();
		var pincode=$("#pincode").val();
		var state=$("#state").val();
		var district=$("#district").val();
		var city=$("city").val();
		if(responseObj_addressDetail!=null && responseObj_addressDetail.addressDetailModel!=null ){
			addressDetailModel_id=responseObj_addressDetail.addressDetailModel.id;
			address1=responseObj_addressDetail.addressDetailModel.address1;
			address2=responseObj_addressDetail.addressDetailModel.address2;
			landMark=responseObj_addressDetail.addressDetailModel.landMark;
			pincode=responseObj_addressDetail.addressDetailModel.pincode;
			state=responseObj_addressDetail.addressDetailModel.state.id;
			district=responseObj_addressDetail.addressDetailModel.district.id;
			city=responseObj_addressDetail.addressDetailModel.city.id;
		}
		responseObj_addressDetail=null;
		
		addressDetailModel="id="+addressDetailModel_id+"&address1="+address1+"&address2="+address2+
		"&landMark="+landMark+"&pincode="+pincode+
		"&state.id="+state+"&district.id="+district+"&city.id="+city;
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/addressDetailForm.html",  
		    data: addressDetailModel,  
		    success: function(response){
		      	if(response.status == "Success"){
			    	$('#'+divName).html(response.dataGrid);
			    	getDistrictData(state,district);
	      			getCityData(district,city);
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
	
	function getDistrictData(stateId,selectedIndex){
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/getDistrictData.html",  
		    data: "stateId=" + stateId+"&distId="+selectedIndex,  
		    success: function(response){
		      	if(response.status == "Success"){
					$('#district')
				    .find('option')
				    .remove()
				    .end()
				    .append('<option value="0">---Select---</option>');
					$.each(response.districtMasterModelList, function (i, item) {
					    $('#district').append($('<option>', { 
					        value: item.id,
					        text : item.name 
					    }));
					});
					$('#district').val(selectedIndex);
				}
		      	else{
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
			},  
		    error: function(e){  
		    }  
		});  
	}
	
	function getCityData(districtId,cityId){
		var pincode=$('#pincode').val()==''?0:$('#pincode').val();
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/getCityData.html",  
		    data: "districtId=" + districtId+"&pincode="+pincode+"&cityId="+cityId,  
		    success: function(response){
		      	if(response.status == "Success"){
					$('#city')
				    .find('option')
				    .remove()
				    .end()
				    .append('<option value="0">---Select---</option>').val(0);
					$.each(response.cityMasterModelsList, function (i, item) {
					    $('#city').append($('<option>', { 
					        value: item.id,
					        text : item.name 
					    }));
					});
					$('#city').val(cityId);
				}
		      	else{
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
			},  
		    error: function(e){  
		    }  
		});  
	}

	function getStateDistMasterData_usingPincode(pincode){
		if(pincode==''){
			return false;
		}
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/getStateDistMasterData_usingPincode.html",  
		    data: "pincode=" + pincode,  
		    success: function(response){
		      	if(response.status == "Success"){
		      		if(response.districtMasterModel!=null){
		      			$('#state').val(response.districtMasterModel.state);
		      			getDistrictData(response.districtMasterModel.state,response.districtMasterModel.id);
		      			getCityData(response.districtMasterModel.id,0);
		      			$('#pincodeMsgDiv').hide('slow');
	      			}
		      		else{
		      			$('#state').val(0);
		    			$('#district').val(0);
		    			$('#city').val(0);
		    			$('#pincodeMsgDiv').show('slow');		    			
		      		}
		      		addClassForColor();
				}
		      	else{
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
			},  
		    error: function(e){  
		    }  
		});  
	}

