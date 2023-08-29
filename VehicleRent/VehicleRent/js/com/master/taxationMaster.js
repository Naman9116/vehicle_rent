	saveUpdateUrl = ctx + "/saveOrUpdateTaxation.html";
	formFillUrl  = ctx + "/formFillForEditTaxation.html";
	deleteUrl  = ctx + "/deleteTaxation.html";

	function localScriptWithRefresh(){}
	function resetData(){
		$('#taxationForm').trigger("reset");
		document.getElementById("addButton").disabled=false;
	  	document.getElementById("updateButton").disabled=true;
	}
	
	function saveOrUpdateTaxation() {
		form_data = new FormData();
		form_data.append("parentId.id", $('#parentId').val());
		form_data.append("efDate", $('#efDate').val());
		form_data.append("calType", $('input[name=calType]:checked').val());
		form_data.append("taxPer", $('#taxPer').val());
		form_data.append("taxVal", $('#taxVal').val());
		form_data.append("idForUpdate", $('#idForUpdate').val());
		saveOrUpdateRecord(form_data);	  	
	}  
	function AssignFormControls(response){
    	$('#dataTable2').html(response.dataGrid);
		$('#parentId').val(response.taxationModel.parentId.id);
		$('#efDate').val(getTimeStampTo_DDMMYYYY(response.taxationModel.efDate));
		if(response.taxationModel.calType=="C")
	  		$('input:radio[name=calType]')[0].checked = true;
	  	else if(response.taxationModel.calType=="L")
	  		$('input:radio[name=calType]')[1].checked = true;
		$('#taxPer').val(response.taxationModel.taxPer);
		$('#taxVal').val(response.taxationModel.taxVal);
		refreshData();
	}
	
	function fillValues(){
	    var rows = $("#userTable").dataTable().fnGetNodes();
        for(var j=0;j<rows.length;j++)
        {
            if($(rows[j]).find("td:eq(1)").text() == $('#parentId option:selected').text()){
        		formFillRecord($(rows[j]).find("td:eq(1)").attr("id"));
            } 
        }
	}
	
	function formFillForEditTaxation(formFillForEditId) {
		formFillRecord(formFillForEditId);
	}  
