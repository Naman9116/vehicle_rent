
	 function addTwoFieldsValue(field1, field2,targetField){
		 $('#'+targetField).val(($('#'+field1).val()-0)-($('#'+field2).val()-0));
	 }
	 
	 function dateTimeDifference(field1,field2,targetField){
		 if($('#'+field1).val()=="" || $('#'+field2).val()=="" || $('#dateFrom').val()=="" || $('#dateTo').val()==""){
			 $('#'+field2).val('');
			 return false;
		 }
		 var startDate = $('#dateFrom').val()+" "+$('#'+field1).val();
		 var endDate   = $('#dateTo').val()+" "+$('#'+field2).val();
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/dateTimeDifference.html",  
		    data: "startDate="+startDate+"&endDate="+endDate,  
		    success: function(response){
		      	if(response.status == "Success"){
		      		if(response.dataArray!=null){
			    		$('#totalDay').val(response.dataArray[0]!=null?response.dataArray[0]:"");
			    		$('#totalHrs').val(response.dataArray[1]!=null?response.dataArray[1]:"");
			    		calculateFare($('#totalKms').val(),$('#totalDay').val(),$('#totalHrs').val(),$('#tariff').val());
		    		}
		      		else{
			    		$('#totalDay').val("");
			    		$('#totalHrs').val("");
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
	
	 function parseDate(str) {
	    var mdy = str.split('/')
	    return new Date(mdy[2], mdy[1]-1, mdy[0]);
	 }

	 function daydiff(first, second) {
	    return (second-first)/(1000*60*60*24);
	 }
		
	 function dateDifference(field1,field2,targetField){
		 if($('#'+field1).val()=="" || $('#'+field2).val()=="")
			 return false;
		 $('#'+targetField).val(daydiff(parseDate($('#'+field1).val()),parseDate($('#'+field2).val())));
	 }
	 
	 function resetData(){
		document.forms[0].reset();
	    $('#idForUpdate').val('');
	  	document.getElementById("addButton").disabled=false;
	  	document.getElementById("currentPage").value=1;
	}
	
	function getNextPageData(){
		pageNumber=pageNumber+1;
		getDutySlipData_DutySlipClose();
	}
	
	function getPreviousPageData(){
		pageNumber=pageNumber>1?pageNumber-1:pageNumber;
		getDutySlipData_DutySlipClose();
	}
	
	function saveOrUpdateDutySlipClose() {
		var dutySlipId= $('input[name=dutySlipId]:checked').val();
		var closeKms=$('#closeKms').val();
		var dateTo=$('#dateTo').val();
		var timeTo=$('#timeTo').val();
		
		var totalKms=$('#totalKms').val();
		var totalDay=$('#totalDay').val();
		var totalHrs=$('#totalHrs').val();

		var garageOutKms=$('#garageOutKms').val();
		var garageInKms=$('#garageInKms').val();

		var stateTax=$('#stateTax').val();
		var miscCharge=$('#miscCharge').val();
		var outStationAllow=$('#outStationAllow').val();
		var nightAllow=$('#nightAllow').val();
		var modeOfPayment= $('input[name=modeOfPayment]:checked').val();
	  	$("#addButton").attr("disabled", true);
	  	var tariff=$('#tariff').val();
	  	var totalFare = $('#totalFare').val();
	  	$.ajax({  	
		    type: "POST",  	
		    url:  ctx + "/saveOrUpdateDutySlipClose.html",  
		    data: "pageNumber="+pageNumber+"&dutySlipId="+dutySlipId+"&closeKms="+closeKms+"&dateTo="+dateTo+"&timeTo="+timeTo+"&totalKms="+totalKms
		    	  +"&totalDay="+totalDay+"&totalHrs="+totalHrs +"&garageOutKms="+garageOutKms+"&garageInKms="+garageInKms
		    	  +"&stateTax="+stateTax+"&miscCharge="+miscCharge +"&outStationAllow="+outStationAllow +"&nightAllow="+nightAllow+"&modeOfPayment="+modeOfPayment+"&tariff.id="+tariff+"&totalFare="+totalFare,  
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
	
	function getDataOnSelection_DutySlipClose(dutySlipId){
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/getDataOnSelection_DutySlipClose.html",  
		    data: "dutySlipId="+dutySlipId,  
		    success: function(response){
		      	if(response.status == "Success"){
		      		if(response.dataArray!=null){
		      			resetPartialFormFields();
			    		$('#bookingNo').val(response.dataArray[0]!=null?response.dataArray[0]:"");
			    		$('#manualSlipNo').val(response.dataArray[1]!=null?response.dataArray[1]:"");
			    		$('#openKms').val(response.dataArray[2]!=null?response.dataArray[2]:"");
			    		$('#timeFrom').val(response.dataArray[3]!=null?response.dataArray[3]:"");
			    		$('#dateFrom').val(response.dataArray[4]!=null?getTimeStampTo_DDMMYYYY(response.dataArray[4]):"");
		      			$('#tariffIdDiv').html(response.dataGrid);
		      			$('#tariff').val(response.dataArray[5]!=null?response.dataArray[5]:"");
			      		addClassForColor();
		    		}
		      		else{
		      			resetPartialFormFields();
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
	
	function getDutySlipData_DutySlipClose(){
		//document.getElementById("currentPage").value=pageNumber;
		var searchCriteria=document.getElementById("searchCriteria").value;
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/getDutySlipData_DutySlipClose.html",  
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
	
	function resetPartialFormFields(){
		$('#bookingNo').val("");
		$('#manualSlipNo').val("");
		$('#openKms').val("");
		$('#timeFrom').val("");
		$('#dateFrom').val("");
		$('#tariff').val("");
		
		$('#closeKms').val("");
		$('#dateTo').val("");
		$('#timeTo').val("");
		
		$('#totalKms').val("");
		$('#totalDay').val("");
		$('#totalHrs').val("");

		$('#garageOutKms').val("");
		$('#garageInKms').val("");

		$('#stateTax').val("");
		$('#miscCharge').val("");
		$('#outStationAllow').val("");
		$('#nightAllow').val("");
	}

	function calculateFare(kms,days,hours,tariff){
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/calculateFare.html",  
		    data: "kms="+kms+"&days="+days+"&hours="+hours+"&tariff="+tariff,  
		    success: function(response){
		      	if(response.status == "Success"){
		      		if(response.dataArray!=null){
			    		$('#totalFare').val(response.dataArray[0]!=null?response.dataArray[0]:"");
			    		$('#nightAllow').val(response.dataArray[1]!=null?response.dataArray[1]:"");
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

	
