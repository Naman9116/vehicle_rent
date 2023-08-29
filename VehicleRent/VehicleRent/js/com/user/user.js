	saveUpdateUrl = ctx + "/saveOrUpdateUser.html";
	formFillUrl  = ctx + "/formFillForEditUser.html";
	deleteUrl  = ctx + "/deleteUser.html";
	var sAssignedBranches;
	function localScriptWithRefresh(){}

	function resetData(){
		refreshData();
		document.forms[0].reset();
		document.getElementById("addButton").disabled = false;
		document.getElementById("updateButton").disabled = true;
		$('#idForUpdate').val('');
		document.getElementById("photoImage").src = contextPath + '/img/blankImage.jpg';
	}

	function saveOrUpdateUser() {
		sAssignedBranches = "";
		$('#assignBranches :selected').each(function(){
			sAssignedBranches += $(this).val() + ",";
		});

		if(sAssignedBranches != ""){
			sAssignedBranches = sAssignedBranches.substring(0,sAssignedBranches.length - 1);
			sBranches = sAssignedBranches.split(",");
			
			form_data = new FormData();
			form_data.append("userName", $('#userName').val());
			form_data.append("password", $('#password').val());
			form_data.append("userRole.id", $('#userRole').val());
			form_data.append("userStatus.id", $('#userStatus').val());
			form_data.append("userFirstName", $('#userFirstName').val());
			form_data.append("userLastName", $('#userLastName').val());
			form_data.append("userMobile", $('#userMobile').val());
			form_data.append("idForUpdate", $('#idForUpdate').val());
			form_data.append("branch.id", sBranches[0]);
			form_data.append("company.id", companyId);
			form_data.append("assignBranches", sAssignedBranches);
	/*		if($("#photoFile").prop("files")[0] != undefined)
				form_data.append("photo", $("#photoFile").prop("files")[0]);
	*/		if($("#photoImage").attr('src') != ""){
				form_data.append("sBase64Image", $("#photoImage").attr('src').replace(',', '~'));
			}
			saveOrUpdateRecord(form_data);
		}else{
			alert("Please assign branch(s) for working !!!");
		}
	}
	
	function viewUser(formFillForEditId){
		viewRecord(formFillForEditId);
	}
	
	function AssignFormControls(response){
		$('#dataTable2').html(response.dataGrid);
		$('#userName').val(response.userModel.userName);
		$('#password').val(response.userModel.password);
		$('#userRole').val(response.userModel.userRole.id);
		$('#userStatus').val(response.userModel.userStatus.id);
		$('#userFirstName').val(response.userModel.userFirstName);
		$('#userLastName').val(response.userModel.userLastName);
		$('#userMobile').val(response.userModel.userMobile);
//		$('#branch').val(response.userModel.branch.id);

		$('select.SlectBox')[0].sumo.unSelectAll();
		if(response.userModel.assignBranches != null){
			sAssignedBranches = response.userModel.assignBranches.split(",");
			var opts = document.getElementById("assignBranches").options;
			var iIndex = 0;
			for(var i = 0; i < opts.length; i++) {
			    if(opts[i].value == sAssignedBranches[iIndex]) {
					$('select.SlectBox')[0].sumo.selectItem(i);
					iIndex++;
			    }
			}
		}		

		document.getElementById("photoImage").src= '/Images/Users/' + response.userModel.id;
		refreshData();
	}
	
	function formFillForEditUser(formFillForEditId) {
		formFillRecord(formFillForEditId);
	}
	
	function deleteUser(idForDelete) {
		deleteRecord(idForDelete);		
	}
	
	
	