
	function resetMaintenanceDetails(){
		if(document.getElementById("maintenanceDetailModel_id")!=null)document.getElementById("maintenanceDetailModel_id").value="";
		if(document.getElementById("fitnessCertificateNo")!=null)document.getElementById("fitnessCertificateNo").value="";
		if(document.getElementById("fitnessUpTo")!=null)document.getElementById("fitnessUpTo").value="";
		if(document.getElementById("pucNo")!=null)document.getElementById("pucNo").value="";
		if(document.getElementById("pucUpTo")!=null)document.getElementById("pucUpTo").value="";
		deleteRowTable_maintenanceDetailEditMode('mainTenanceDetailTable');
		//var responseObj=null;
		//var response_serviceDetailObj=null;
	}
	
	var maintenance_count=0;
	var responseObj=null;
	var response_serviceDetailObj=null;
	var response_serviceDetailObj_newValue=null;

	function deleteRowTable_maintenanceDetailEditMode(tableID) {
		try {
			maintenance_count=0;
			var table = document.getElementById(tableID);
			if(table!=null)
				var rowCount = table.rows.length;
				if( rowCount>0){
					for(var i=0; i<rowCount; i++) {
						table.deleteRow(i);
						rowCount--;
						i--;
					}
				}
		}
		catch(e) {
			alert(e);
		}
	}
			
	function deleteRowTable_maintenanceDetail(tableID) {
		selectedRowForDelete='';
		try {
			var table = document.getElementById(tableID);
			if(table!=null)
			var rowCount = table.rows.length;
			if( rowCount>0){
				var agree=confirm("Are You Sure You Wish to Delete?");
				if(agree){
					for(var i=0; i<rowCount; i++) {
						var row = table.rows[i];
						var chkbox = row.cells[0].childNodes[0];
						if(null != chkbox && true == chkbox.checked) {
							selectedRowForDelete=selectedRowForDelete+chkbox.value+"!";
							checkedKey=checkedKey+chkbox.id+"!";
							table.deleteRow(i);
							rowCount--;
							i--;
							maintenance_count--;
						}
					}
				}
				if(selectedRowForDelete==''){
					alert("Please Select Row For Delete!");
					return false;
				}
			}
		}
		catch(e) {
			alert(e);
		}
	}
	
	function saveMaintenanceDetailForm(){
		var maintenanceDetailModel="";
		var maintenanceDetailModel_id=document.getElementById("maintenanceDetailModel_id")!=null?document.getElementById("maintenanceDetailModel_id").value:"";
		var fitnessCertificateNo=document.getElementById("fitnessCertificateNo")!=null?document.getElementById("fitnessCertificateNo").value:"";
		var fitnessUpTo=document.getElementById("fitnessUpTo")!=null?document.getElementById("fitnessUpTo").value:"";
		var pucNo=document.getElementById("pucNo")!=null?document.getElementById("pucNo").value:"";
		var pucUpTo=document.getElementById("pucUpTo")!=null?document.getElementById("pucUpTo").value:"";
		
		maintenanceDetailModel="maintenanceDetailModel.id="+maintenanceDetailModel_id+"&maintenanceDetailModel.fitnessCertificateNo="+fitnessCertificateNo+"&maintenanceDetailModel.fitnessUpTo="+fitnessUpTo+
		"&maintenanceDetailModel.pucNo="+pucNo+"&maintenanceDetailModel.pucUpTo="+pucUpTo+getServiceDetails();
		return 	maintenanceDetailModel;
	}
	
	function loadMaintenanceDetailForm(divName){
		var maintenanceDetailModel="";
		var maintenanceDetailModel_id=document.getElementById("maintenanceDetailModel_id")!=null?document.getElementById("maintenanceDetailModel_id").value:"";
		var fitnessCertificateNo=document.getElementById("fitnessCertificateNo")!=null?document.getElementById("fitnessCertificateNo").value:"";
		var fitnessUpTo=document.getElementById("fitnessUpTo")!=null?document.getElementById("fitnessUpTo").value:null;
		var pucNo=document.getElementById("pucNo")!=null?document.getElementById("pucNo").value:"";
		var pucUpTo=document.getElementById("pucUpTo")!=null?document.getElementById("pucUpTo").value:null;
		response_serviceDetailObj_newValue=getServiceDetails().split(",");
		if(responseObj!=null && responseObj.maintenanceDetailModel!=null){
			maintenanceDetailModel_id=responseObj.maintenanceDetailModel.id;
			fitnessCertificateNo=responseObj.maintenanceDetailModel.fitnessCertificateNo;
			fitnessUpTo=responseObj.maintenanceDetailModel.fitnessUpTo!=null?getTimeStampTo_DDMMYYYY(responseObj.maintenanceDetailModel.fitnessUpTo):null;
			pucNo=responseObj.maintenanceDetailModel.pucNo;
			pucUpTo=responseObj.maintenanceDetailModel.pucUpTo!=null?getTimeStampTo_DDMMYYYY(responseObj.maintenanceDetailModel.pucUpTo):null;
		}
		maintenanceDetailModel="id="+maintenanceDetailModel_id+"&fitnessCertificateNo="+fitnessCertificateNo+"&fitnessUpTo="+fitnessUpTo+"&pucNo="+pucNo+"&pucUpTo="+pucUpTo;
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/maintenanceDetailForm.html",  
		    data: maintenanceDetailModel,  
		    success: function(response){
		      	if(response.status == "Success"){
			    	$('#'+divName).html(response.dataGrid);
					$("#"+divName).show("slow").siblings().hide("slow");
					if(response_serviceDetailObj!=null){
						deleteRowTable_maintenanceDetailEditMode("mainTenanceDetailTable");
					  	for(var i=0;i<response_serviceDetailObj.length;i++){
					  		if(response_serviceDetailObj[i]!=""){
					  			var rowData=response_serviceDetailObj[i].split("!");
					  			addRowTable_MaintenanceDetail('mainTenanceDetailTable');
					  			$('#serviceDate'+i).val(getSqlDateTo_DDMMYYYY(rowData[0]));
					  			$('#garadge'+i).val(rowData[1]);
					  			$('#mainTenanceDetailDescription'+i).val(rowData[2]);
					  		}
					  	}
					  	response_serviceDetailObj_newValue=null;
					}
					if(response_serviceDetailObj_newValue==null){
						response_serviceDetailObj_newValue=getServiceDetails().split(",");
					}
		    		if(response_serviceDetailObj_newValue==null){
			    		var serviceDate=response_serviceDetailObj_newValue[0].split("!");
			    		var garadge=response_serviceDetailObj_newValue[1].split("!");
			    		var mainTenanceDetailDescription=response_serviceDetailObj_newValue[2].split("!");
			    		deleteRowTable_maintenanceDetailEditMode("mainTenanceDetailTable");
					  	for(var i=0;i<serviceDate.length;i++){
					  		if(serviceDate[i]!="" && garadge[i]!="" && mainTenanceDetailDescription[i]!=""){
					  			addRowTable_MaintenanceDetail('mainTenanceDetailTable');
					  			$('#serviceDate'+i).val(serviceDate[i]);
					  			$('#garadge'+i).val(garadge[i]);
					  			$('#mainTenanceDetailDescription'+i).val(mainTenanceDetailDescription[i]);
				  			}
					  	}
		    		}
					response_serviceDetailObj=null;
					responseObj =null;
					addClassForColor();
				}
		      	else{
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
			},  
		    error: function(e){  
		      alert('Error: ' + e);  
		    }  
		});  
	}
	
	function addRowTable_MaintenanceDetail(tableId){
	  	var serviceDateValues=document.getElementsByName('serviceDate')!=null?document.getElementsByName('serviceDate'):null;
	  	var garadgeValues=document.getElementsByName('garadge')!=null?document.getElementsByName('garadge'):null;
	  	
	  	var isValid=true;
		for(var i=0;i<serviceDateValues.length;i++){
			if(jQuery.trim($('#'+serviceDateValues[i].id).val())==""){
				alert("Please fill previous row fields");
				isValid=false;
				break;
			}
			if(jQuery.trim($('#'+garadgeValues[i].id).val())==""){
				alert("Please fill previous row fields");
				isValid=false;
				break;
			}
		}

		if(!isValid)
			return false;
		var table = document.getElementById(tableId);
		//var tbody = table.getElementsByTagName("TBODY")[0];
		var row = document.createElement("TR");
		
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);
		var cell1 = row.insertCell(0);

		var element1 = document.createElement("input");
		element1.type = "checkbox";
		element1.name = "select";
		element1.id = "select"+maintenance_count;
		cell1.appendChild(element1);
		cell1.style.width="10%" ;

		var td1 = document.createElement("TD")
		var id1="\"serviceDate"+maintenance_count+"\"";
		var strHtml1 = "<input type=\"text\" name=\"serviceDate\" id="+id1+" placeholder=\"Service Date\" readonly=\"true\" class=\"datetimepicker\" >";
		td1.innerHTML = strHtml1.replace(/!maintenance_count!/g,maintenance_count);
		td1.style.width='20%';

		var td2 = document.createElement("TD")
		var id2="\"garadge"+maintenance_count+"\"";
		var strHtml2 = "<input type=\"text\"  name=\"garadge\" maxlength=\"30\" id="+id2+" placeholder=\"Garadge\"> ";
		td2.innerHTML = strHtml2.replace(/!maintenance_count!/g,maintenance_count);
		td2.style.width='30%';

		var td3 = document.createElement("TD");
		var id3="\"mainTenanceDetailDescription"+maintenance_count+"\"";
		var strHtml3 = "<INPUT TYPE=\"text\" NAME=\"mainTenanceDetailDescription\" maxlength=\"100\" id="+id3+" placeholder=\"Description\" size=\"30\" >";
		td3.innerHTML = strHtml3.replace(/!maintenance_count!/g,maintenance_count);
		td3.style.width='40%';
		
		row.appendChild(td1);
		row.appendChild(td2);
		row.appendChild(td3);

		//tbody.appendChild(row);
		maintenance_count = parseInt(maintenance_count) + 1;
		addClassForColor();
	 }
		

	
	function getServiceDetails(){
	  	var serviceDateValue="";
	  	var fieldValues=document.getElementsByName('serviceDate')!=null?document.getElementsByName('serviceDate'):null;
		for(var i=0;i<fieldValues.length;i++){
			serviceDateValue=serviceDateValue+"&maintenanceDetailModel.serviceDetailModel["+i+"].serviceDate="+jQuery.trim($('#'+fieldValues[i].id).val());
		}
		
		var garadgeValue="";
	  	var fieldValues=document.getElementsByName('garadge')!=null?document.getElementsByName('garadge'):null;
		for(var i=0;i<fieldValues.length;i++){
			garadgeValue=garadgeValue+"&maintenanceDetailModel.serviceDetailModel["+i+"].garadge="+jQuery.trim($('#'+fieldValues[i].id).val());
		}
		
		var mainTenanceDetailDescriptionValue="";
	  	var fieldValues=document.getElementsByName('mainTenanceDetailDescription')!=null?document.getElementsByName('mainTenanceDetailDescription'):null;
		for(var i=0;i<fieldValues.length;i++){
			mainTenanceDetailDescriptionValue=mainTenanceDetailDescriptionValue+"&maintenanceDetailModel.serviceDetailModel["+i+"].mainTenanceDetailDescription="+jQuery.trim($('#'+fieldValues[i].id).val());
		}
		return serviceDateValue+garadgeValue+mainTenanceDetailDescriptionValue;
	}
	
/*	$(function() {
		$(body).on('focus', '.datetimepicker', function() {
			$(this).datepicker({
				dateFormat : "dd/mm/yy"
			});
		});
	 });
*/	
	