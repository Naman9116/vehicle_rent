	saveUpdateUrl = ctx + "/saveOrUpdateRelatedInfo.html";
	formFillUrl  = ctx + "/formFillForEditRelatedInfo.html";
	deleteUrl  = ctx + "/deleteRelatedInfo.html";

	var lastBooking,lastDS,lastInvoice,lastCoverLetter,bankName,bankAccNo,bankIFSC;

	function localScriptWithRefresh(){}
	
	function resetData(){
		refreshData();
		$('#relatedInfoForm').trigger("reset");
		if(pageFor=="Branch") $('select.SlectBox')[0].sumo.unSelectAll();
		responseObj_addressDetail=null;
		responseObj_contactDetail=null;
		document.getElementById("addButton").disabled = false;
		document.getElementById("updateButton").disabled = true;
		$('#idForUpdate').val('');
		$('#error').hide();
		$('#info').hide();
	}

	function AssignCompany(){
		$('#companyName').val($('#company option:selected').text());
	}
	
	function AssignBranch(){
		$('#branchName').val($('#branch option:selected').text());
	}

	function saveOrUpdateRelatedInfo() {
		sTerminals = "";
		$('#airportTerminals :selected').each(function(){
			sTerminals += $(this).val() + ",";
		});
		sTerminals = sTerminals.substring(0,sTerminals.length - 1);
		var sCode = "";
		if(pageFor == "Company") sCode = $('#Code').val();
		if(pageFor == "Branch") sCode = $('#CodeBr').val();
		if(pageFor == "Outlet") sCode = $('#CodeOl').val();

		form_data = new FormData();
		form_data.append("idForUpdate", $('#idForUpdate').val());
		form_data.append("pageFor", pageFor);
		form_data.append("code", sCode);
		form_data.append("company", $('#company').val());
		form_data.append("branch", $('#branch').val());
		form_data.append("outlet", $('#outlet').val());
		form_data.append("cstNo", $('#gstin').val());
		form_data.append("lstNo", $('#lstNo').val());
		form_data.append("girNo", $('#girNo').val());
		form_data.append("panNo", $('#panNo').val());
		form_data.append("tanNo", $('#tanNo').val());
		form_data.append("regDate", $('#regDate').val());
		form_data.append("email", $('#email').val());
		form_data.append("helpLine", $('#helpLine').val());
		form_data.append("webSite", $('#webSite').val());
		form_data.append("companyName", $('#companyName').val());
		form_data.append("branchName", $('#branchName').val());
		form_data.append("outletName", $('#outletName').val());
		form_data.append("airportTerminals", sTerminals);
		form_data.append("gstin", $('#gstin').val());

		form_data.append("lastBooking",lastBooking);
		form_data.append("lastDS",lastDS);
		form_data.append("lastInvoice",lastInvoice);
		form_data.append("lastCoverLetter",lastCoverLetter);
		form_data.append("bankName",bankName);
		form_data.append("bankAccNo",bankAccNo);
		form_data.append("bankIFSC",bankIFSC);
		form_data.append("parentType",pageFor);
		
		
		saveContactDetailForm();
		saveAddressDetailForm();
		saveOrUpdateRecord(form_data);
	}
	
	function viewRelatedInfo(formFillForEditId){
		viewRecord(formFillForEditId);
	}
	
	function AssignFormControls(response){
		$('#dataTable2').html(response.dataGrid);

		if(pageFor=="Company"){
			$('#company').val(response.relatedInfoModel.generalMasterModel.id);
			$('#companyName').val(response.relatedInfoModel.generalMasterModel.name);
			$('#Code').val(response.relatedInfoModel.generalMasterModel.itemCode);
		}else if(pageFor=="Branch"){
			$('#company').val(response.relatedInfoModel.company);
			$('#companyName').val($('#company option:selected').text());
			$('#branch').val(response.relatedInfoModel.generalMasterModel.id);
			$('#branchName').val(response.relatedInfoModel.generalMasterModel.name);
			$('#CodeBr').val(response.relatedInfoModel.generalMasterModel.itemCode);
			$('#gstin').val(response.relatedInfoModel.gstin);
		}else if(pageFor=="Outlet"){
			$('#branch').val(response.relatedInfoModel.branch);
			$('#branchName').val($('#branch option:selected').text());
			$('#outlet').val(response.relatedInfoModel.generalMasterModel.id);
			$('#outletName').val(response.relatedInfoModel.generalMasterModel.name);
			$('#CodeOl').val(response.relatedInfoModel.generalMasterModel.itemCode);
		}
		
		$('#cstNo').val(response.relatedInfoModel.cstNo);
		$('#lstNo').val(response.relatedInfoModel.lstNo);
		$('#girNo').val(response.relatedInfoModel.girNo);
		$('#panNo').val(response.relatedInfoModel.panNo);
		$('#tanNo').val(response.relatedInfoModel.tanNo);

		$('#regDate').val(response.relatedInfoModel.regDate);
		$('#email').val(response.relatedInfoModel.email);
		$('#helpLine').val(response.relatedInfoModel.helpLine);
		$('#webSite').val(response.relatedInfoModel.webSite);

		lastBooking = response.relatedInfoModel.lastBooking;
		lastDS = response.relatedInfoModel.lastDS;
		lastInvoice = response.relatedInfoModel.lastInvoice;
		lastCoverLetter = response.relatedInfoModel.lastCoverLetter;
		bankName = response.relatedInfoModel.bankName;
		bankAccNo = response.relatedInfoModel.bankAccNo;
		bankIFSC = response.relatedInfoModel.bankIFSC;

		responseObj_addressDetail=null;
		responseObj_contactDetail=null;
		
		responseObj_contactDetail = response.relatedInfoModel;
	  	responseObj_addressDetail = response.relatedInfoModel;
		
	  	resetContactDetails();
		resetAddressDetails();

		setAddressValues();
		setContactValues();
		
//		$('select.SlectBox')[0].sumo.unSelectAll();
		if(pageFor=="Branch"){
			if(response.relatedInfoModel.airportTerminals != null){
				sTerminals = response.relatedInfoModel.airportTerminals.split(",");
				var opts = document.getElementById("airportTerminals").options;
				var iIndex = 0;
				if(opts != null || opts != ""){
					for(var i = 0; i < opts.length; i++) {
					    if(opts[i].value == sTerminals[iIndex]) {
							$('select.SlectBox')[0].sumo.selectItem(i);
							iIndex++;
					    }
					}
				}
			}		
		}		
	}
	
	function formFillForEditRelatedInfo(formFillForEditId) {
		formFillRecord(formFillForEditId);		
	}
	
	function deleteRelatedInfo(idForDelete) {
		deleteRecord(idForDelete);		
	}
	
	function getDataGridDataRelatedInfo() {
		document.getElementById("currentPage").value = pageNumber;
		var searchCriteria = document.getElementById("searchCriteria").value;
		var companyCode = "";
		$.ajax({
			type : "POST",
			url:  ctx + "/getDataGridDataRelatedInfo.html",
			data : "companyCode=" + companyCode
					+ "&searchCriteria=" + searchCriteria+"&pageFor=" + pageFor,
			success : function(response) {
				if (response.status == "Success") {
					$('#dataTable2').html(response.dataGrid);
				} else {
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
				addClassForColor();
			},
			error : function(e) {
				alert('Error: ' + e);
			}
		});
	}