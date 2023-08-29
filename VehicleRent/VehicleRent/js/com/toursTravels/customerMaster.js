formFillUrl  = ctx + "/formFillForEditCustomer.html";
saveUpdateUrl = ctx + "/saveOrUpdateCustomer.html";
deleteUrl  = ctx + "/deleteCustomer.html";

var idForUpdate="";
var isValidationError = false;

function resetAll(){
	document.location.reload();
}

function refreshData(){	
	if($('#userTable').dataTable() != null) $('#userTable').dataTable().fnDestroy();
	userTable = $('#userTable').dataTable({
		"bLengthChange": true,
		"bFilter": true,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": true,
		"aLengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]]
	});
}

function resetData(){
	document.location.reload();
}

function saveOrUpdateCustomer() {
	validateCustomer();
	if(!isValidationError){
		form_data = new FormData();
		form_data.append("idForUpdate", $('#idForUpdate').val());
		form_data.append("name", $('#name').val());
		form_data.append("mobileNo", $('#mobileNo').val());
		form_data.append("email", $('#email').val());
		form_data.append("company", $('#compId option:selected').val());
		saveOrUpdateRecord(form_data);
  }	
}

function AssignFormControls(response){
	$("#idForUpdate").val(response.customerModel.id);
	$("#name").val(response.customerModel.name);
	$("#mobileNo").val(response.customerModel.mobileNo);
	$("#email").val(response.customerModel.email);
	$('#compId').val(response.customerModel.company);
	$('#updateButton').prop('disabled', false);
	$('#addButton').prop('disabled', true);
}

function formFillForEditCustomer(formFillForEditId) {
	formFillRecord(formFillForEditId);
}

function deleteCustomer(idForDelete) {
	deleteRecord(idForDelete);
	refreshData();
} 

function viewCustomer(formFillForEditId){
	viewRecord(formFillForEditId);
}

function validateCustomer(){
	isValidationError = false;
	var validationErrorHtml = '<ol>';
	if( $('#name').val()==""){
		validationErrorHtml = validationErrorHtml + "<li> Select Customer Name</li>";
		isValidationError = true;
	}
	if( $('#mobileNo').val()==""){
		validationErrorHtml = validationErrorHtml + "<li> Select Mobile Number</li>";
		isValidationError = true;
	}
	if( $('#compId option:selected').val()=="0"){
		validationErrorHtml = validationErrorHtml + "<li> Select Company Name</li>";
		isValidationError = true;
	}
	
	validationErrorHtml = validationErrorHtml + "</ol>";
	if(isValidationError){
		$('#error').html("Please correct following errors:<br>" + validationErrorHtml);
		$('#error').hide();
		$('#error').show();
	}else{
		$('#error').html("");
		$('#error').hide();
	}
}