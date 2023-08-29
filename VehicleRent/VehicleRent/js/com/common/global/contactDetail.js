	function resetContactDetails(){
		$("#contactDetailModel_id").val("");
		$("#contactPerson1").val("");
		$("#contactPerson2").val("");
		$("#residentialPhone").val("");
		$("#officialPhone").val("");
		$("#personalMobile").val("");
		$("#officialMobile").val("");
		$("#personalEmailId").val("");
		$("#officialEmailId").val("");
		$("#residentialFaxNo").val("");
		$("#officialFaxNo").val("");
		$("#personalWebsite").val("");
		$("#officialWebsite").val("");
	}

	var responseObj_contactDetail = null;

	function saveContactDetailForm(){
		form_data.append("contactDetailModel.id", $("#contactDetailModel_id").val() == undefined ? "":$("#contactDetailModel_id").val());
		form_data.append("contactDetailModel.contactPerson1", $("#contactPerson1").val()== undefined ? "":$("#contactPerson1").val());
		form_data.append("contactDetailModel.contactPerson2", $("#contactPerson2")== undefined ? "":$("#contactPerson2").val());
		form_data.append("contactDetailModel.residentialPhone", $("#residentialPhone").val()== undefined ? "":$("#residentialPhone").val());
		form_data.append("contactDetailModel.officialPhone", $("#officialPhone").val()== undefined ? "":$("#officialPhone").val());
		form_data.append("contactDetailModel.personalMobile", $("#personalMobile").val()== undefined ? "":$("#personalMobile").val());
		form_data.append("contactDetailModel.officialMobile", $("#officialMobile").val()== undefined ? "":$("#officialMobile").val());
		form_data.append("contactDetailModel.personalEmailId", $("#personalEmailId").val()== undefined ? "":$("#personalEmailId").val());
		form_data.append("contactDetailModel.officialEmailId", $("#officialEmailId").val()== undefined ? "":$("#officialEmailId").val());
		form_data.append("contactDetailModel.residentialFaxNo", $("#residentialFaxNo").val()== undefined ? "":$("#residentialFaxNo").val());
		form_data.append("contactDetailModel.officialFaxNo", $("#officialFaxNo").val()== undefined ? "":$("#officialFaxNo").val());
		form_data.append("contactDetailModel.personalWebsite", $("#personalWebsite").val()== undefined ? "":$("#personalWebsite").val());
		form_data.append("contactDetailModel.officialWebsite", $("#officialWebsite").val()== undefined ? "":$("#officialWebsite").val());
	}
	
	function setContactValues(){
		$("#contactDetailModel_id").val(responseObj_contactDetail.contactDetailModel.id);
		$("#contactPerson1").val(responseObj_contactDetail.contactDetailModel.contactPerson1);
		$("#contactPerson2").val(responseObj_contactDetail.contactDetailModel.contactPerson2);
		$("#residentialPhone").val(responseObj_contactDetail.contactDetailModel.residentialPhone);
		$("#officialPhone").val(responseObj_contactDetail.contactDetailModel.officialPhone);
		$("#personalMobile").val(responseObj_contactDetail.contactDetailModel.personalMobile);
		$("#officialMobile").val(responseObj_contactDetail.contactDetailModel.officialMobile);
		$("#personalEmailId").val(responseObj_contactDetail.contactDetailModel.personalEmailId);
		$("#officialEmailId").val(responseObj_contactDetail.contactDetailModel.officialEmailId);
		$("#residentialFaxNo").val(responseObj_contactDetail.contactDetailModel.residentialFaxNo);
		$("#officialFaxNo").val(responseObj_contactDetail.contactDetailModel.officialFaxNo);
		$("#personalWebsite").val(responseObj_contactDetail.contactDetailModel.personalWebsite);
		$("#officialWebsite").val(responseObj_contactDetail.contactDetailModel.officialWebsite);
	}
	function loadContactDetailForm(divName){
		var contactDetailModel="";
		var contactDetailModel_id=document.getElementById("contactDetailModel_id")!=null?document.getElementById("contactDetailModel_id").value:"";
		var contactPerson1=document.getElementById("contactPerson1")!=null?document.getElementById("contactPerson1").value:"";
		var contactPerson2=document.getElementById("contactPerson2")!=null?document.getElementById("contactPerson2").value:"";
		var residentialPhone=document.getElementById("residentialPhone")!=null?document.getElementById("residentialPhone").value:"";
		var officialPhone=document.getElementById("officialPhone")!=null?document.getElementById("officialPhone").value:"";
		var personalMobile=document.getElementById("personalMobile")!=null?document.getElementById("personalMobile").value:"";
		var officialMobile=document.getElementById("officialMobile")!=null?document.getElementById("officialMobile").value:"";
		var personalEmailId=document.getElementById("personalEmailId")!=null?document.getElementById("personalEmailId").value:"";
		var officialEmailId=document.getElementById("officialEmailId")!=null?document.getElementById("officialEmailId").value:"";
		var residentialFaxNo=document.getElementById("residentialFaxNo")!=null?document.getElementById("residentialFaxNo").value:"";
		var officialFaxNo=document.getElementById("officialFaxNo")!=null?document.getElementById("officialFaxNo").value:"";
		var personalWebsite=document.getElementById("personalWebsite")!=null?document.getElementById("personalWebsite").value:"";
		var officialWebsite=document.getElementById("officialWebsite")!=null?document.getElementById("officialWebsite").value:"";
		if(responseObj_contactDetail!=null && responseObj_contactDetail.contactDetailModel!=null){
			contactDetailModel_id=responseObj_contactDetail.contactDetailModel.id;
			contactPerson1=responseObj_contactDetail.contactDetailModel.contactPerson1;
			contactPerson2=responseObj_contactDetail.contactDetailModel.contactPerson2;
			residentialPhone=responseObj_contactDetail.contactDetailModel.residentialPhone;
			officialPhone=responseObj_contactDetail.contactDetailModel.officialPhone;
			personalMobile=responseObj_contactDetail.contactDetailModel.personalMobile;
			officialMobile=responseObj_contactDetail.contactDetailModel.officialMobile;
			personalEmailId=responseObj_contactDetail.contactDetailModel.personalEmailId;
			officialEmailId=responseObj_contactDetail.contactDetailModel.officialEmailId;
			residentialFaxNo=responseObj_contactDetail.contactDetailModel.residentialFaxNo;
			officialFaxNo=responseObj_contactDetail.contactDetailModel.officialFaxNo;
			personalWebsite=responseObj_contactDetail.contactDetailModel.personalWebsite;
			officialWebsite=responseObj_contactDetail.contactDetailModel.officialWebsite;
		}
		responseObj_contactDetail=null;
		
		contactDetailModel="id="+contactDetailModel_id+"&contactPerson1="+contactPerson1+"&contactPerson2="+contactPerson2+
		"&residentialPhone="+residentialPhone+"&officialPhone="+officialPhone+
		"&personalMobile="+personalMobile+"&officialMobile="+officialMobile+"&personalEmailId="+personalEmailId+
		"&officialEmailId="+officialEmailId+"&residentialFaxNo="+residentialFaxNo+
		"&officialFaxNo="+officialFaxNo+"&personalWebsite="+personalWebsite+"&officialWebsite="+officialWebsite;
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/contactDetailForm.html",  
		    data: contactDetailModel,  
		    success: function(response){
		      	if(response.status == "Success"){
			    	$('#'+divName).html(response.dataGrid);
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
