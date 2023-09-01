	saveUpdateUrl = ctx + "/saveOrUpdateGeneralMaster.html";
	formFillUrl  = ctx + "/formFillForEditGeneralMaster.html";
	deleteUrl  = ctx + "/deleteGeneralMaster.html";
	var tariffParaId=null;
	var sSorted = 0;
	
	function getTable(){
    	//gets table
    	var sortedArr;
    	var index = 0;
    	var sortedValue="";
    	var oTable = document.getElementById("userTable");
    	if(oTable == null)
    		return false;
    	//gets rows of table
    	var rowLength = oTable.rows.length;
    	sortedArr = new Array(rowLength - 1);
    	//loops through rows    
    	for (var i = 1; i < rowLength; i++){
    	   //gets cells of current row
    	   var oCells = oTable.rows.item(i).cells;
    	   //gets amount of cells of current row
    	   var cellLength = oCells.length;
    	   //loops through each cell in current row
    	   for(var j = 0; j < cellLength; j++){
	    	      /* get your cell info here */
	    	  var cellVal = oCells.item(j).innerHTML; 
	    	  if(j == 1){ 
	    		  sortedArr[index] = cellVal;
	    		  sortedValue = sortedValue + cellVal + "~";
	    		  index = index + 1;
	    	  }
    	   }
    	}
    	if(masterCode=='ALL') masterId = $("#masterId").val();
    	
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/setSortingOrder.html",  
		    data: "masterId="+masterId+"&sortedArr=" + sortedValue ,  
		    success: function(response){
		      	if(response.status == "Success"){
					$('#dataTable2').html(response.dataGrid);
			    	refreshData();
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

	function localScriptWithRefresh(){
		$('.sorted_table').sortable({
			  containerSelector: 'table',
			  itemPath: '> tbody',
			  itemSelector: 'tr',
			  placeholder: '<tr class="placeholder"/>'
		});
		if(masterCode == 'ALL') masterId=$("#masterId").val();
		fetchExtraData = "&masterId="+masterId;
		deleteExtraData = "&masterId=" + masterId;
	}

	function resetData(){
	//	document.location.reload();
	}

	function saveOrUpdateGeneralMaster() {
		form_data = new FormData();
		form_data.append("masterId", masterId);
		form_data.append("code", '');
		form_data.append("name", $('#name').val());
		form_data.append("remark", $('#remark').val());
		form_data.append("sortId", sSorted);
		form_data.append("idForUpdate", $('#idForUpdate').val());
		if(masterCode == "ZONE"){
			if($('#extraId').val() == "0"){
				alert("Please select branch !!!");
				return 0;
			}
			form_data.append("extraId", $('#extraId').val());
		}
		if(masterCode == "LOC"){
			if($('#extraId').val() == "0"){
				alert("Please select branch !!!");
				return 0;
			}
			if($('#zoneId').val() == "0"){
				alert("Please select zone !!!");
				return 0;
			}
			
			form_data.append("extraId", $('#extraId').val());
			form_data.append("zoneId", $('#zoneId').val());
		}
		if(masterId == '8'){
			if(tariffParaId != null)
				form_data.append("extraId", tariffParaId);
			form_data.append("tariffSchemeParaModel.minKM", $('#minKM').val());
			form_data.append("tariffSchemeParaModel.minHR", $('#minHR').val());
		}
		saveOrUpdateRecord(form_data);
		refreshData();
	}  
	
	function viewGeneralMaster(formFillForEditId){
		viewRecord(formFillForEditId);
	}

	function AssignFormControls(response){
	    $('#dataTable2').html(response.dataGrid);
		masterId = response.generalMasterModel.masterModel.id;
    	if(masterCode == 'ALL')	$('#masterId').val(masterId);
	    $('#name').val(response.generalMasterModel.name);
	    $('#remark').val(response.generalMasterModel.remark);
	    sSorted = response.generalMasterModel.sortId;
		if(masterId == '8'){
			if(response.generalMasterModel.tariffSchemeParaModel != null){
				tariffParaId = response.generalMasterModel.tariffSchemeParaModel.id;
				$('#minKM').val(response.generalMasterModel.tariffSchemeParaModel.minKM);
				$('#minHR').val(response.generalMasterModel.tariffSchemeParaModel.minHR);
			}else{
				tariffParaId = null;
				$('#minKM').val('');
				$('#minHR').val('');
			}
		}
		if(masterCode == "ZONE" && response.generalMasterModel.extraId != null){
			$('#extraId').val(response.generalMasterModel.extraId);
		}
		if(masterCode == "LOC" ){
			if(response.generalMasterModel.extraId != null)
				$('#extraId').val(response.generalMasterModel.extraId);
			if(response.generalMasterModel.zoneId != null)
				fillZone(response.generalMasterModel.zoneId);
		}
	}
	function formFillForEditGeneralMaster(formFillForEditId) {
		formFillRecord(formFillForEditId);
		refreshData();
	}  
	
	function deleteGeneralMaster(idForDelete) {
		deleteRecord(idForDelete);
		refreshData();
	}  

	function getDataGridDataGeneralMaster(){
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/getDataGridDataGeneralMaster.html",  
		    data: "masterId="+  masterId,
		    success: function(response){
		      	if(response.status == "Success"){
					$('#dataTable2').html(response.dataGrid);
					refreshData();
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

	function fillZone(zoneId){
		if(masterCode == "LOC"){
			var branchId = $('#extraId').val();
		  	$.ajax({  
			    type: "POST",  	
			    url:  ctx + "/getZoneAsBranch.html",  
			    data: "branchId="+branchId,
			    success: function(response){
			      	if(response.status == "Success"){
						$("#zoneId").empty().append('<option value="0">---Select---</option>');
						$.each(response.generalMasterModelList, function(index,val) {
							if(zoneId == val.id){
								$("#zoneId").append('<option value="'+val.id+'" selected>' + val.name+ '</option>');
							}else{
								$("#zoneId").append('<option value="'+val.id+'">' + val.name+ '</option>');
							}
						});
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
	}