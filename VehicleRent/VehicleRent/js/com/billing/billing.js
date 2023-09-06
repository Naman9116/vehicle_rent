var minKmInHR = 0, minKmInKM = 0, minHrInHR=0, minHrInKM=0, lCoverLetterId = 0, lCorporateId = 0;
var traiffKm, traiffHr;
var sType = "N", isMultiInvoice = "";
var corporateTariffList=null;
var allTariffSchemeParameter = null;
var corporateID=null;
var isValidationError = false;
var invoiveRelatedStyles=null;
var lId=null,fetchModel=null;
var nightCharge = null, dayCharge = null, nightStay = null, finalDayChg = null, dayStay = null;
var extKm = null, extHr = null, totalAmount = null, minCharge = 0.00;
var allOptionValuesMap = new Object();
var usedByOptionValuesMap = new Object();
var tariffDetailMap = null;
var userTable, invoiceGeneratedDate, id;
var toDate = null, idForUpdate=0, dsRetriveString=null;
var branchId=null, CompanyId=null, cityName=null;
var fromIndex=0, toIndex=0;
var bookingDetailIds=null, carAlocationId=null, carDetailId=null, corporateIds=null;
var billingStatus=null, tariff=null;
var dateTimeFromString=null, dateTimeToString=null;
var invoiceNo = null, compName = "", companyIds=null, saveOrInvoice=null, carNO =null;
var extraChgHr,extraChgHrRate = 0, extraChgKm,extraChgKmRate = 0;
var dsnightAllow, fuelCharge, miscCharge, guideCharge, tariffList, rentalList, minMinuteSlab, rentltype, finalBasicFare;
var minChgHr = 0, minChgKM = 0, vendorId, chauffeurId, carModelId=0;
var cModelMap = new Object();
var cCarDetailMap = new Object();
var chModelMap = new Object();
var vModelMap = new Object();
var isDSNoError = false, isNewCoverLetter = false;
var stTax = 0, parking = 0, toll=0;
var resDS,cpId, branchListLocal;

function localScriptWithRefresh(){}

function closeModal(){
	$(".modal").hide();
}
function checkboxClicked(id){
	if($("#checkbox_"+id).is(':checked')){
		$("#checkbox_"+id).addClass("edited");
	}else{
		$("#checkbox_"+id).removeClass("edited");
	}
}

function fillValues(corporateId){
	if(corporateId <= 0){
		$('#bookedBy').val("0");
		$('#clientName').val("0");
		$('#mobileNo').val("");
		$('#officeAddress').val("0");
		return;
	}
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/fillAsPerCorporate.html",  
		data: "corporateId="+corporateId,  
		success : function(response) {
			if (response.status == "Success") {
				refillCombo('#bookedBy',response.generalMasterModelList);
				refillCombo('#clientName',response.autorizedUserModelList);
				$.each(response.autorizedUserModelList, function (i, item) {
					clientMobile[i] = item.remark;
				});
				$("#officeAddress")
				.find('option')
				.remove()
				.end()
				.append('<option value="0">--- Select ---</option>')
				.val('0');
				if(response.addressDetail.address1 != ""){
					$("#officeAddress").append($('<option>', { 
						value: "1",
						text : response.addressDetail.address1 + " " + response.addressDetail.address2
					}));
				}
				$('#mobileNo').val("");
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

function corporateInvoice(corporateId)
{
	corporateID=corporateId;

}

function  invoice()
{
	$("#invoiceFromDate").val(getCurrentDate());
	$("#invoiceToDate").val(getCurrentDate());
	$.ajax({
		type : "POST",
		url:  ctx + '/getInvoice.html',
		data: 'branchID='+branchID+'&corporateID='+corporateID, 
		async:false,
		/*processData: false,
			contentType: false,*/

		success : function(response) {
			if (response.status == "Success") {
				/*	$('#info').html(response.result);*/
				$('#dataTable2').html(response.dataGrid);
				refreshData();
				/*		$('#info').show('slow');*/
				/*		$('#error').hide('slow');*/
			} 
		},
		error : function(e) {
			alert('Error: ' + e);
		}
	});	

}

function searchBilling(sPageStatus){
	if(jobType == 'DutySlipReceive' || jobType == 'DutySlipClose'){	  
		var sCriteria = 'bookingDetailModel.bookingMasterModel.corporateId.id,bookingDetailModel.rentalType.id,bookingDetailModel.branch.id,bookingDetailModel.outlet.id,regNo';
		var sValue = $("#corporateId").val() + "," +
		$("#rentalType").val()+","+
		$("#branch").val()+","+
		$("#outlet").val()+","+
		$("#carRegNo").val();
	} else if(jobType == 'DutySlipUnbilled'){	  
		var sCriteria = 'bookingDetailModel.bookingMasterModel.corporateId.id,closedBy,bookingDetailModel.branch.id,bookingDetailModel.outlet.id,bookingDetailModel.bookingMasterModel.bookedBy,usedBy';
		var sValue = $("#corporateId").val() + "," +
		$("#closedBy").val()+","+
		$("#branch").val()+","+
		$("#outlet").val()+","+
		$("#bookedBy").val()+","+
		$("#usedBy").val();
	}   
	
	if(isValidationError)
		return false;
	form_data = new FormData();
	form_data.append("jobType", jobType);
	form_data.append("sCriteria", sCriteria);
	form_data.append("sValue", sValue);
	form_data.append("sPageStatus", sPageStatus);
	$.ajax({
		type : "POST",
		url:  ctx + '/searchBilling.html',
		async:false,
		processData: false,
		contentType: false,
		data : form_data,
		success : function(response) {
			if (response.status == "Success") {
				$('#info').html(response.result);
				$('#dataTable2').html(response.dataGrid);
				refreshData();
				if(sPageStatus == "R"){
					branchListLocal = response.branchList;
					refillComboWithAll("#branch",response.branchList);
					refillComboWithAll("#outlet",response.hubList);
					refillComboWithAll("#corporateId",response.corporateList);
					refillComboWithAll("#carRegNo",response.carNoList);
					refillComboWithAll("#rentalType",response.rentalTypeList);
					$("#totalDueDS").text('[ '+response.lTotalDs+' ]');
				}
				$('#info').show('slow');
				$('#error').hide('slow');
			} else {
				$('#error').html(response.result);
				$('#info').hide('slow');
				$('#error').show('slow');
			}
		},
		error : function(e) {
			alert('Error: ' + e);
		}
	});
	if(sPageStatus != "R"){
		countBranch();
		countRentalType();
		if(jobType != "DutySlipClose") unbilledAmount();
	}
}

function validateInvoiceSearchDetails(){
	isValidationError = false;
	var validationErrorHtml = '<ol>';
	var dateFrom= new Date($("#invoiceFromDate").val());
	var dateTo= new Date($("#invoiceToDate").val());		
	var thresholdDate = getTimeStampTo_DDMMYYYY(new Date());
	if(daysBetween($("#invoiceFromDate").val(), thresholdDate) < 0){
		validationErrorHtml = validationErrorHtml + "<li>Invoice Date From Should not be Greater Than Current Date !!</li>";
		isValidationError = true;
	}
	if(daysBetween($("#invoiceToDate").val(),thresholdDate) < 0){
		validationErrorHtml = validationErrorHtml + "<li>Invoice Date To Should not be Greater Than Current Date !!</li>";
		isValidationError = true;
	}
	if(daysBetween($("#invoiceFromDate").val(),$("#invoiceToDate").val())<0) {
		validationErrorHtml = validationErrorHtml + "<li>Invoice Date To Should Not be Less Than Invoice Date From !!</li>";
		isValidationError = true;
	}
	validationErrorHtml = validationErrorHtml + "</ol>";
	if(isValidationError){
		$('#error').html("Please correct following errors:<br>" + validationErrorHtml);
		$('#info').hide();
		$('#error').show();
	}else{
		$('#error').html("");
		$('#error').hide();
	}
}

function viewInvoice(lId)
{
	tableToModel(lId);	
	$('#viewInvoiceRecordPanel').modal('show');	
}

function tableToModel(lId)
{
	$("#branchModel").val($("#Branch_"+lId).html());
	$("#hubModel").val($("#Hub_"+lId).html());
	$("#bookingModeModel").val($("#bookingMode_"+lId).html());
	$("#invoiceNoModel").val($("#invoice_"+lId).html());
	$("#invoiceDateModel").val($("#invoiceDate_"+lId).html());
	$("#corporateNameModel").val($("#corporate_"+lId).html());
	$("#bookedByModel").val($("#bookedBy_"+lId).html());
	$("#clientNameModel").val($("#bookedFor_"+lId).html());
	$("#basicFareModel").val($("#basicFare_"+lId).html());
	$("#miscChargesModel").val($("#miscCharges_"+lId).html());
	$("#parkingAmountModel").val($("#parkingAmount_"+lId).html());
	$("#tollTaxModel").val($("#tollTax_"+lId).html());
	$("#stateTaxModel").val($("#stateTax_"+lId).html());
	$("#serviceTaxModel").val($("#serviceTax_"+lId).html());
	$("#totalAmountModel").val($("#totalamount_"+lId).html());
	$("#closedByModel").val($("#closedBy_"+lId).html());
	$("#paymentModeModel").val($("#paymentMode_"+lId).html());
}


function countBranch(){
	var cells = [];
	var allCells = [];
	var count = 0 ;
	var colNo ;
	$("#userTable thead tr th").each(function ( i ) {
		if(jobType == 'Invoice'){
			colNo = 1;
		}else if(jobType == 'DutySlipReceive' || jobType == 'DutySlipClose' || jobType == 'DutySlipUnbilled'){	  
			colNo = 0;
		}
		if (i == colNo){
			var tableContent = new Array();
			$("#branchLabels").html("");
			userTable.column(i).data().sort().each( function ( d, j ) {
				var foundIndex = tableContent.indexOf((d.split('>')[1]).split('</')[0]);
				if(foundIndex>-1)
					tableContent.splice(foundIndex,1);
					tableContent.push((d.split('>')[1]).split('</')[0]);
			});
			$.each(tableContent,function(index,val){
				cells.push(val);
				$("#branchLabels").append('<b>'+cells[index]+'</b>&nbsp;[&nbsp;<span style="color: red ; font-size : 16px; font-weight : bold;" id="'+cells[index]+'"></span>&nbsp;]&nbsp;&nbsp');
			})
			var rows = $("#userTable").dataTable().fnGetNodes();
			count = 0;
			var countValueObject = {};
			for(var k=0;k<rows.length;k++)
			{
				allCells.push($(rows[k]).find("td:eq("+colNo+") textarea").text()); 
				if(cells.indexOf(allCells[k])>-1){
					countValueObject[allCells[k]] = (countValueObject[allCells[k]] == undefined? 1 :(countValueObject[allCells[k]]+1));
				}
				$("#"+allCells[k]).text(countValueObject[allCells[k]]);
			}
		}
	});
}

function countRentalType(){
	var options = document.getElementById("rentalTypeModel");
	var optArray = [];
	var optIdArray = [];
	var count = 0;
	var cells = [];
	var col = 0 ;
	if(jobType == 'DutySlipReceive' || jobType == 'DutySlipClose' || jobType == 'DutySlipUnbilled'){
	    	col = 7;
    }else if(jobType == 'Invoice'){
	    	col=9;
     }
	var rows = $("#userTable").dataTable().fnGetNodes();
		for (var i = 0; i < options.length; i++) {
		optArray.push(options[i].text);
		optIdArray.push(options[i].value);
		count = 0;
		for(var j=0;j<rows.length;j++)
		{
			cells.push($(rows[j]).find("td:eq("+col+")").text()); 
			if(cells[j] == optArray[i]){
				count++;
			}
		}
		$("#"+optIdArray[i]).html(count);
	}
}

function updateDutySlip() {
	form_data = new FormData();
	var i = 0;
	if($('.edited').length<=0)
		return false;
	$(".edited").each(function(){
		var dutySlipId = $(this).attr('id');
		dutySlipId = dutySlipId.substring(9,dutySlipId.length);		
		if($("#checkbox_"+dutySlipId).prop('checked') == true){
			form_data.append("parkingAmount",$("#parking_"+dutySlipId).val() == ''?"0":$("#parking_"+dutySlipId).val());
			form_data.append("tollTaxAmount",$("#toll_"+dutySlipId).val() == ''?"0":$("#toll_"+dutySlipId).val());
			form_data.append("id",dutySlipId);
			form_data.append("stateTax",$("#state_"+dutySlipId).val()==''?"0":$("#state_"+dutySlipId).val());
			i++;
		}
	});
	$("#submitButton").attr("disabled", true);
	$.ajax({  
		type: "POST",  	
		url:  ctx + "/updateDutySlip.html",  
		async:false,
		data: form_data, 
		processData: false,
		contentType: false,
		success: function(response){
			if(response.status == "Success"){
				$('#info').html(response.result);
				$('#dataTable2').html(response.dataGrid);
				refreshData();
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
	setTimeout(function() { document.getElementById("submitButton").disabled=false;},3000);
	searchBilling('R');
}  

function incrementDate(date_str) {
    var parts = date_str.split("-");
    var dt = new Date(
        parseInt(parts[0], 10),        // year
        parseInt(parts[1], 10) - 1,  // month (starts with 0)
        parseInt(parts[2], 10)       // date
    );
    dt.setTime(dt.getTime() + 1 * 86400000);
    parts[0] = "" + dt.getFullYear();
    parts[1] = "" + (dt.getMonth() + 1);
    if (parts[1].length < 2) {
        parts[1] = "0" + parts[1];
    }
    parts[2] = "" + dt.getDate();
    if (parts[2].length < 2) {
        parts[2] = "0" + parts[2];
    }
    toDate = parts.join("-");
    return toDate;
};

function searchInvoice(){
	var sCriteria = 'sType,invoiceFromDate,invoiceToDate,invoiceNo,corpName,closedBy,bookedBy,branch,hub,usedBy,status,jobType';
	var sValue    = "I" + "," + $("#invoiceFromDate").val() + "," + $("#invoiceToDate").val() + "," + $("#invoiceNo").val() + "," + $("#corporateId").val() + "," + $("#closedBy").val() + "," + $("#bookedBy").val() + "," + $("#branch").val() + "," + $("#outlet").val() + "," + $("#usedBy").val() + "," + "Y" + "," + jobType;
	
	validateInvoiceSearchDetails();
	if(isValidationError)
		return false;
	form_data = new FormData();
	form_data.append("sCriteria", sCriteria);
	form_data.append("sValue", sValue);

	$.ajax({
		type : "POST",
		url:  ctx + '/invoiceSearch.html',
		async:false,
		processData: false,
		contentType: false,
		data : form_data,
		success : function(response) {
			if (response.status == "Success") {
				$('#info').html(response.result);
				$('#dataTable2').html(response.dataGrid);
				refreshData();
				$('#info').show('slow');
				$('#error').hide('slow');
			} else {
				$('#error').html(response.result);
				$('#info').hide('slow');
				$('#error').show('slow');
			}
		},
		error : function(e) {
			alert('Error: ' + e);
		}
	});
	countBranch();
	countRentalType();
	invoiceAmount();
}

function generateInvoice(invoiceNo,corpId,type){
	form_data = new FormData();
	form_data.append("invoiceNo", invoiceNo);
	form_data.append("corpId", corpId);
	$.ajax({
		type : "POST",
		url:  ctx + '/getInvoice.html',
		data : form_data,
		processData: false,
		contentType: false,
		async: false,
		success : function(response) {
			$('#info').html(response.result);
			$('#invoice').html(response.dataGrid);

			$('.tot').each(function(){
				if($(this).html() == "0.00"){
					 var $this = $(this).attr('id');
				     $(this).parent('tr').css("display", "none");
				}
			});
			    
			$("#grandTotalWord").html("(Rupees: "+(number2text($('#grand').text()))+" )");
			if(type == "V"){
				$('#print').hide();
			}
			$('#viewInvoice').modal('show');
		},
		error : function(e) {
			alert('Error: ' + e);
		}
	});
}

function number2text(value) {
	var fraction = Math.round(frac(value)*100);
	var f_text  = "";

	if(fraction > 0) {
		f_text = "AND "+convert_number(fraction)+" PAISE";
	}

	return convert_number(value)+" RUPEE "+f_text+" ONLY";
}

function frac(f) {
	return f % 1;
}

var a = ['','one ','two ','three ','four ', 'five ','six ','seven ','eight ','nine ','ten ','eleven ','twelve ','thirteen ','fourteen ','fifteen ','sixteen ','seventeen ','eighteen ','nineteen '];
var b = ['', '', 'twenty','thirty','forty','fifty', 'sixty','seventy','eighty','ninety'];

function convert_number(number) {
	if ((number < 0) || (number > 999999999)) 
	{ 
		return "NUMBER OUT OF RANGE!";
	}
	var Gn = Math.floor(number / 10000000);  /* Crore */ 
	number -= Gn * 10000000; 
	var kn = Math.floor(number / 100000);     /* lakhs */ 
	number -= kn * 100000; 
	var Hn = Math.floor(number / 1000);      /* thousand */ 
	number -= Hn * 1000; 
	var Dn = Math.floor(number / 100);       /* Tens (deca) */ 
	number = number % 100;               /* Ones */ 
	var tn= Math.floor(number / 10); 
	var one=Math.floor(number % 10); 
	var res = ""; 

	if (Gn>0) 
	{ 
		res += (convert_number(Gn) + " CRORE"); 
	} 
	if (kn>0) 
	{ 
		res += (((res=="") ? "" : " ") + 
				convert_number(kn) + " LAKH"); 
	} 
	if (Hn>0) 
	{ 
		res += (((res=="") ? "" : " ") +
				convert_number(Hn) + " THOUSAND"); 
	} 

	if (Dn) 
	{ 
		res += (((res=="") ? "" : " ") + 
				convert_number(Dn) + " HUNDRED"); 
	} 


	var ones = Array("", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX","SEVEN", "EIGHT", "NINE", "TEN", "ELEVEN", "TWELVE", "THIRTEEN","FOURTEEN", "FIFTEEN", "SIXTEEN", "SEVENTEEN", "EIGHTEEN","NINETEEN"); 
	var tens = Array("", "", "TWENTY", "THIRTY", "FOURTY", "FIFTY", "SIXTY","SEVENTY", "EIGHTY", "NINETY"); 

	if (tn>0 || one>0) 
	{ 
		if (!(res=="")) 
		{ 
			res += " AND "; 
		} 
		if (tn < 2) 
		{ 
			res += ones[tn * 10 + one]; 
		} 
		else 
		{ 

			res += tens[tn];
			if (one>0) 
			{ 
				res += ("-" + ones[one]); 
			} 
		} 
	}

	if (res=="")
	{ 
		res = "zero"; 
	}
	return res;
}

function generateMultipleInvoice(invoiceNo,corpId,type){
	form_data = new FormData();
	form_data.append("invoiceNo", invoiceNo);
	form_data.append("corpId", corpId);
	$.ajax({
		type : "POST",
		url:  ctx + '/getMultipleInvoice.html',
		data : form_data,
		processData: false,
		contentType: false,
		async: false,
		success : function(response) {
			$('#info').html(response.result);
			$('#multipleInvoice').html(response.dataGrid);
			
			$('.tot').each(function(){
				if($(this).html() == "0.00"){
					 var $this = $(this);
				     $(this).parent('tr').css("display", "none");
				}
			});
			
			invoiveRelatedStyles = (response.dataGrid).substring((response.dataGrid).indexOf('<style type="text/css">'),((response.dataGrid).indexOf('</style>'))+8);

			$("#grandTotalWord").html("(Rupees: "+(number2text($('#grand').html()))+")");
			if(type == "V"){
				$('#print').hide();
			}else{
				$('#print').show();
			}
			$('#viewMultipleInvoice').modal('show');
		},
		error : function(e) {
			alert('Error: ' + e);
		}
	});
}

function print(type){
	var divContents;
	$('#controlDiv').hide();
	var printWindow = window.open('', '', 'left=0,top=0,width=800,toolbar=0,scrollbars=1,status=0');
	if(type == "S"){
		$('#viewInvoice').modal('hide');
		var divContents = $("#invoice").html();
		printWindow.document.write(divContents);
	}else if(type == "M"){
		$('#viewMultipleInvoice').modal('hide');
		divContents = $("#multipleInvoice").html();
		printWindow.document.write('<html><head>'+invoiveRelatedStyles);
		printWindow.document.write('</head><body >');
		printWindow.document.write(divContents);
		printWindow.document.write('</body></html>');
	}else if(type == "C"){
		$('#viewCoverLetter').modal('hide');
		var divContents = $("#coverLetter").html();
		printWindow.document.write(divContents);
	}
	printWindow.document.close();
	printWindow.print();
	printWindow.close()
	$('#controlDiv').show();
}

function checkMultipleInvoice(invoiceNo,corpId,type){
	form_data = new FormData();
	form_data.append("invoiceNo", invoiceNo);
	form_data.append("corpId", corpId);
	$.ajax({
		type : "POST",
		url:  ctx + '/getCheckInvoice.html',
		processData: false,
		contentType: false,
		data:form_data,
		async:false,
		success : function(response) {
			if(response.dutySlipModelList.length > 0){
				if(response.dutySlipModelList.length == 1){
					generateInvoice(invoiceNo,corpId,type);
				}else{
					generateMultipleInvoice(invoiceNo,corpId,type);
				} 
			}
		},
		error : function(e) {
			alert('Error: ' + e);
		}
	});
}

function closeDS(id,booDetailId){
	$('#closingDSPanel').on('shown.bs.modal', function (e) {
		 /*disable control*/
		 $("#carTariff").attr("disabled", false);
		 $("#dsKmsIn").prop("readonly", false);
		 $("#dsTimeIn").prop("readonly", false);
		 $("#dsToDate").prop("readonly", false);
		 $("#dsNightStay").prop("readonly", false);
		 $("#dsDayStay").prop("readonly", false);
		 $("#billingStatus").prop("readonly", false);
		 $("#dsnightAllow").prop("readonly", false);
		 $("#dsdayAllow").prop("readonly", false);
		 $("#dsstateTax").prop("readonly", false);
		 $("#dstollCharge").prop("readonly", false);
		 $("#dsparkingCharge").prop("readonly", false);
		 $("#fuelCharge").prop("readonly", false);
		 $("#guideCharge").prop("readonly", false);
		 $("#dsmiscCharge").prop("readonly", false);
		 $("#dsremarks").prop("readonly", false);
	});
	$("#dSRetreive").val($("#dutySlipNo_" + id).val());
	dutySlipClosingSearch();
	$('#closingDSPanel').modal('show');
}

function fillTariff(value){
	$("#carTariff").find('option').remove().end()
	.append('<option value="0">--- Select ---</option>').val('0');
	$.each(tariffList, function (i, item) {
		if(item.masterId == value){
		    $("#carTariff").append($('<option>', { 
		        value: item.id,
		        text : item.name,
		    }));
		}
	});
}

function setTableToModel(id,booDetailId){
  idForUpdate = id;
  bDetailId = booDetailId;
  cpId = $("#corporateId_"+id).text();
  $("#dsDate").val($("#dutySlipDate_"+id).html());
  $("#corporateName").val(cpId);
  $("#dsUsedBy").val($("#bookedFor_"+id).html());
  $("#dsBookedBy").val($("#bookedBy_"+id).html());
  $("#dscarType").val($("#allottedCar_"+id).html());
  $("#dsparkingCharge").val(parseInt($("#parking_"+id).val()) == '0'?'':parseInt($("#parking_"+id).val()));
  $("#dstollCharge").val(parseInt($("#toll_"+id).val()) == '0'?'':parseInt($("#toll_"+id).val()));
  $("#dsstateTax").val(parseInt($("#state_"+id).val()) == '0'?'':parseInt($("#state_"+id).val()));
}

function getAllTariffValue(tId){
	form_data = new FormData();
    form_data.append("tariffId", tId);
 $.ajax({
		type : "POST",
		url:  ctx + '/getAllTariffDetails.html',
		processData: false,
		contentType: false,
		async:false,
	    data: form_data, 
	   success : function(response) {
		   $('#info').html(response.result);
		   allTariffSchemeParameter = response.tariffSchemeParaModelList;
      },
		error : function(e) {
			alert('Error: ' + e);
		}
 	});
}
 
function getTariffValue(tariffId){
	form_data = new FormData();
    form_data.append("tariffId", tariffId);
    form_data.append("branchId", branchId);
    $.ajax({
		type : "POST",
				url:  ctx + '/getTariffDetailsOnDSClose.html',
		processData: false,
		contentType: false,
		async:false,
	    data: form_data, 
	    success : function(response) {
		   $('#info').html(response.result);
		   if(rentltype != "Outstation" ){
			   if(response.tariffSchemeParaModel !=null){
				   if(response.tariffSchemeParaModel.minKM != "" || response.tariffSchemeParaModel.minKM != null){
					   $("#dsMinChgKms").val(response.tariffSchemeParaModel.minKM == '0'?'':response.tariffSchemeParaModel.minKM);
					   $("#dsminChgHrs").val(response.tariffSchemeParaModel.minHR == '0'?'':response.tariffSchemeParaModel.minHR);
				   }else{
					   $("#dsMinChgKms").val('');
					   $("#dsminChgHrs").val('');
				   }
			   }
		   }
      },
		error : function(e) {
			alert('Error: ' + e);
		}
 	});
 	if(corporateTariffList != null){
	 	$.each(corporateTariffList,function(index,value){
		    if(value.name == $("#carTariff option:selected").text()){
		    	minCharge = parseFloat(value.tariffValue);
		    	finalBasicFare = minCharge;
		    }
		});
	 	sType = "A";
	 	calculateKm();
	 	timeInCal();
	 	sType = "N";
 	}
}
/*
* for calculate total Kms 
*/
function calculateKm(){
var kmOut = parseInt($("#kmOut").val());
var kmIn   = parseInt($("#kmIn").val());
if (kmIn >= kmOut){
	var totalKm = kmIn  - kmOut;
	$("#totalKms").val(totalKm);
	if($("#dsRentalType").val() == "Local Run"){
		checkKmForCarTariff();
	}
	if($("#dsMinChgKms").val() !="")
		var 	fMinChgKM = $("#dsMinChgKms").val();
		else
			fMinChgKM = '0';
	
	extKm =parseInt(totalKm) - parseInt(fMinChgKM);
}else{
	  $("#kmIn").focus();
      extKm = 0;
}
if(extKm < 0) extKm = 0;
$("#extraChgKm").val(extKm);

totalSlipAmount();
}	
/*
* for Hours Count
*/

function calculateHrs(end,start){
	   return ms = moment(end,"DD/MM/YYYY HH:mm:ss").diff(moment(start,"DD/MM/YYYY HH:mm:ss"));
}

function timeInCal(){
	if($("#timeIn").val() == "") $("#timeIn").val("00:00");
	   var start =($("#fromDate").val()).concat(" ").concat($("#timeOut").val());
	   var end =($("#fromDate").val()).concat(" ").concat($("#timeIn").val());
	   var ms = calculateHrs(end,start);
	   var d = moment.duration(ms);
	   if (d < 0){
		   var endDate = $("#fromDate").val();
		   var edate = endDate.split("/").reverse().join("-");
			incrementDate(edate);
			endDate = toDate.split("-").reverse().join("/");
			$("#toDate").val(endDate);
	   }else{
		   $("#toDate").val($("#fromDate").val());
	   }
	   countHours();
}

function countHours(){
	   var diff = null;
	   var start =($("#fromDate").val()).concat(" ").concat($("#timeOut").val());
	   var end =($("#toDate").val()).concat(" ").concat($("#timeIn").val());
	   var ms = calculateHrs(end,start);
	   var d = moment.duration(ms);
	   var minute = parseInt(moment.utc(ms).format("mm")) ;

	   var roundedMiuntes = Math.ceil(minute / minMinuteSlab);
	   roundedMiuntes = (roundedMiuntes * minMinuteSlab);
	   var fMinChgHr;
	   if($("#minChgHr").val() !="")
		   fMinChgHr = $("#minChgHr").val();
	   else
		   fMinChgHr = '0';
		
	   if(roundedMiuntes == 60){
		   diff = Math.floor(d.asHours() + 1) + ":00";   
		   extHr = parseInt(Math.floor(d.asHours() + 1)) - parseInt(fMinChgHr); 
		   if(extHr < 0 ) extHr = "0";
	   }else{
		   diff = Math.floor(d.asHours()) + ":" + roundedMiuntes;   
		   extHr = parseInt(Math.floor(d.asHours())) - parseInt(fMinChgHr); 
		   if(extHr < 0 ) 
			   extHr = 0;
		   else
			   extHr = extHr + ":" + roundedMiuntes;
 	   }

	   $("#totalHrs").val(diff);
	   if($("#dsRentalType").val() == "Local Run"){
		   checkHrForCarTariff();
		}
	   
	   $("#extraChgHr").val(extHr);
	   setTimeout( "$('#editDetailError').hide();", 10000);
	   countdays();
	}  

/*
* for Calculating Total Slip Amount
*/
function totalSlipAmount(){
	extHr = (extHr == null || extHr == ''? "0": extHr);
	extKm = (extKm == null || extKm == ''? "0": extKm);
	finalBasicFare = (finalBasicFare == null || finalBasicFare == ''? "0": finalBasicFare);

	if($("#extraChgHr").val()!=""){
		extraChgHr=$("#extraChgHr").val();
	}else{
		extraChgHr='0:00';
	}
	
	var extraHrsArr = extraChgHr.split(":"); 
	var coficent = extraHrsArr[1] / 15;
	coficent = coficent * 25;
	extraChgHr = extraHrsArr[0] + "." + coficent;
	
	if($("#extraChgKm").val()!=""){
		extraChgKm=$("#extraChgKm").val();
	}else{
		extraChgKm = '0';
	}
	
	if($("#dayAllow").val()!=""){
		dayAllow=$("#dayAllow").val();
	}else{
		dayAllow ='0';
	}
	
	if($("#nightAllow").val()!=""){
		nightAllow=$("#nightAllow").val();
	}else{
		nightAllow ='0';
	}
	
	if($("#guideCharge").val()!=""){
		guideCharge=$("#guideCharge").val();
	}else{
		guideCharge = '0';
	}
	
	if($("#fuelCharge").val()!=""){
		fuelCharge=$("#fuelCharge").val();
	}else{
		fuelCharge = '0';
	}
	
	if($("#miscCharge").val()!=""){
		miscCharge=$("#miscCharge").val();
	}else{
		miscCharge = '0';
	}
	
	if($("#toll").val()!=""){
		toll = $("#toll").val();
	}else{
		toll = '0';
	}
	
	if($("#parking").val()!=""){
		parking = $("#parking").val();
	}else{
		parking = '0';
	}
	
	if($("#stTax").val()!=""){
		stTax = $("#stTax").val();
	}else{
		stTax = '0';
	}
	
	
	totalAmount = parseFloat(finalBasicFare) + 
						  parseFloat(extraChgHrRate) * (parseFloat(extraChgHr)) + 
						  parseFloat(extraChgKmRate) * (parseFloat(extraChgKm)) + 
						  parseFloat(nightAllow) + parseFloat(dayAllow) +
						  parseFloat(guideCharge) + parseFloat(fuelCharge) + parseFloat(miscCharge) +
						  parseFloat(stTax) + parseFloat(parking) + parseFloat(toll);
	
	$("#slipAmount").val(totalAmount);
 
}
/*
* for Days Count
*/
function countdays(){
  var oneday = 24*60*60*1000;
  var sd = $("#fromDate").val();
  var ed = $("#toDate").val();
  if(treatAsUTC(ed) >= treatAsUTC(sd)){
	  var days = ((treatAsUTC(ed) - treatAsUTC(sd))/(oneday)) + 1;
	  
	  $("#dayStay").val(days);
	  if(days >1){
		  $("#nightStay").val(days - 1);
	 }else{ 
		  $("#nightStay").val("");
	  	  $("#nightAllow").val("");
	  }
  }else{
	  $("#toDate").focus();
  }
  nightChg();
  dayChg();
  totalSlipAmount();
}
/*
 * for  Calculating Night Charge
 */

function nightChg(){
  nightStay = $("#nightStay").val() == ""?'0':$("#nightStay").val();
	  finalNightChg = nightStay * nightCharge;
  $("#nightAllow").val(finalNightChg == '0'?'':finalNightChg);
  totalSlipAmount();
}

/*
 * for  Calculating Day Allowance Charge
 */

function dayChg(){
  dayStay = $("#dayStay").val() == ""?'0':$("#dayStay").val();
	  finalDayChg = dayStay * dayCharge;
  $("#dayAllow").val(finalDayChg == '0'?'':finalDayChg);
  
  if(rentltype == "Outstation"){
	  finalBasicFare = dayStay * minCharge;
	  var finalMinChgHr = dayStay * minChgHr;
	  $("#dsminChgHrs").val(finalMinChgHr);
	  var finalMinChgKm = dayStay * minChgKM;
	  $("#dsMinChgKms").val(finalMinChgKm);
	  
  }else{
	  finalBasicFare = minCharge;
  }
  if(rentltype == 'Outstation'){
	  calculateKm();
	  var fMinChgHr;
	  if($("#minChgHr").val() !="")
		  fMinChgHr = $("#minChgHr").val();
	  else
		   fMinChgHr = '0';
	  
	  extHr = parseInt($("#totalHrs").val()) - parseInt(fMinChgHr); 
	  if(extHr < 0 ) extHr = 0;
	  $("#extraChgHr").val(extHr);
	  
  }
  totalSlipAmount();
}

/*
 * -------------------------------------------------------------------------
  * only Close DS From DS Close Screen 
  * ------------------------------------------------------------------------
  */
function saveUpdateDs(){
	validateDSDetails();
	if(!isValidationError){
		$("#dataLoaderModal").show();
	    $("#dataLoaderFade").show();
		form_data = new FormData();
		form_data.append("id", idForUpdate);
		form_data.append("manualSlipNo",$("#msNo").val() == null?"":$("#msNo").val());
		form_data.append("closeKms",$("#dsKmsIn").val() == null || $("#dsKmsIn").val() == "" ? "0" : $("#dsKmsIn").val());
		form_data.append("timeTo",$("#dsTimeIn").val() == null || $("#dsTimeIn").val() == "" ? "0" : $("#dsTimeIn").val());
		form_data.append("dateTo",$("#dsToDate").val());
		form_data.append("totalKms",$("#dsTotalKms").val() == null || $("#dsTotalKms").val() == "" ? "0" : $("#dsTotalKms").val());
		form_data.append("totalHrs",$("#dsTotalHrs").val() == null || $("#dsTotalHrs").val() == "" ? "0" : $("#dsTotalHrs").val());
		form_data.append("minChgKms",$("#dsMinChgKms").val() == null || $("#dsMinChgKms").val() == "" ? "0" : $("#dsMinChgKms").val());
		form_data.append("minChgHrs",$("#dsminChgHrs").val() == null || $("#dsminChgHrs").val() == "" ? "0" :$("#dsminChgHrs").val());
		form_data.append("extraChgKms",extraChgKmRate);
		form_data.append("extraChgHrs",extraChgHrRate);
		form_data.append("tollTaxAmount",$("#dstollCharge").val() == null || $("#dstollCharge").val() == "" ? "0" : $("#dstollCharge").val());
		form_data.append("parkingAmount",$("#dsparkingCharge").val() == null || $("#dsparkingCharge").val() == "" ? "0" : $("#dsparkingCharge").val());
		form_data.append("stateTax",$("#dsstateTax").val() == null || $("#dsstateTax").val() == "" ? "0" : $("#dsstateTax").val());
		form_data.append("remarks",$("#dsremarks").val() == null ? "" : $("#dsremarks").val());
		form_data.append("totalFare",$("#dsslipAmount").val() == null || $("#dsslipAmount").val() == "" ? "0" : $("#dsslipAmount").val());
		form_data.append("miscCharge",$("#dsmiscCharge").val() == null || $("#dsmiscCharge").val() == "" ? "0" : $("#dsmiscCharge").val());
		form_data.append("dateFrom",$("#dsFromDate").val() == null ? "0" : $("#dsFromDate").val());
		form_data.append("totalDay",$("#dsDayStay").val() == null || $("#dsDayStay").val() == "" ? "0" : $("#dsDayStay").val());
		form_data.append("timeFrom",$("#dsTimeOut").val() == null || $("#dsTimeOut").val() == "" ? "0" : $("#dsTimeOut").val());
		form_data.append("nightAllow",$("#dsnightAllow").val() == null || $("#dsnightAllow").val() == "" ? "0" : $("#dsnightAllow").val());
		form_data.append("dayAllow",$("#dsdayAllow").val() == null || $("#dsdayAllow").val() == "" ? "0" : $("#dsdayAllow").val());
		form_data.append("guideCharge",$("#guideCharge").val() == null || $("#guideCharge").val() == "" ? "0" : $("#guideCharge").val());
		form_data.append("fuelCharge",$("#fuelCharge").val() == null || $("#fuelCharge").val() == "" ? "0" : $("#fuelCharge").val());
		form_data.append("totalNight",$("#dsNightStay").val() == null || $("#dsNightStay").val() == "" ? "0" : $("#dsNightStay").val());
		form_data.append("tariff.id",$('#carTariff').val());
		form_data.append("basicFare",minCharge);
		form_data.append("nightAllowanceRate",nightCharge);
		form_data.append("outStationAllowRate",dayCharge);
		form_data.append("billingStatus",$('#billingStatus').val());
		$.ajax({
				type : "POST",
				url:  ctx + '/saveUpdateDs.html',
				processData: false,
				contentType: false,
				data : form_data,
				async:false,
				success : function(response) {
					if (response.status == "Success") {
						$('#editDetailInfo').html(response.result);
					    $('#editDetailError').hide();
					    $('#editDetailInfo').show();
					    $("#dataLoaderModal").hide();
					    $("#dataLoaderFade").hide();		  
					    setTimeout( "$('#editDetailInfo').hide();", 1000);
					    setTimeout(function(){$("#closingDSPanel").modal('hide');},1000);
					    $("#userTable").dataTable().fnDestroy();
					    refreshData();
					} else {
					    $("#dataLoaderModal").hide();
					    $("#dataLoaderFade").hide();
					    $('#editDetailError').html(response.result);
					    $('#editDetailInfo').hide();
					    $('#editDetailError').show();				    
					}
				},
				error : function(e) {
				    	$("#dataLoaderModal").hide();
				    	$("#dataLoaderFade").hide();
				    	alert('Error: ' + e);
				}
	     });
		 searchBilling('R');
	}	 
}
/*
 * for  Validate Duty Slip Details before Closing And Invoicing
 */
function validateDSDetails(opType){	    	    
	isValidationError = false;
	var dsValidationErrorHtml = '<ol>';
	var kmIn = $('#dsKmsIn').val();
	if(kmIn == null || kmIn == "" || kmIn =="00"){
		dsValidationErrorHtml = dsValidationErrorHtml + "<li> Please Enter Km In.  !!</li>";
	    isValidationError = true;
	}
	var totalHrs = $('#dsTotalKms').val();
	if(totalHrs == null || totalHrs == "" || totalHrs == "0"){
		dsValidationErrorHtml = dsValidationErrorHtml + "<li> Please Enter Correct To Date &  To Time .</li>";
	    isValidationError = true;
	}
	
	if(opType =='I'){
		var invoiceDate = $("#dsInvoiceDate").val();
		var dsDate = $("#dsDate").val();
		var currentDt   = getTimeStampTo_DDMMYYYY(new Date());
		if(daysBetween(invoiceDate,currentDt) < 0 ){
			dsValidationErrorHtml = dsValidationErrorHtml + "<li>Invoice Date Should be Less Than or Equal To Current Date !!</li>";
			isValidationError = true;
		}else if(daysBetween(dsDate,invoiceDate)< 0){
			dsValidationErrorHtml = dsValidationErrorHtml + "<li>Invoice Date Can't be Less Than To Duty Slip Date !!</li>";
			isValidationError = true;
		}
	}
	dsValidationErrorHtml = dsValidationErrorHtml + "</ol>";
	if(isValidationError){
		$('#editDetailError').html("Please correct following errors:<br>" + dsValidationErrorHtml);
		$('#editDetailInfo').hide();
		$('#editDetailError').show();
	}else{
		$('#editDetailError').html("");
		$('#editDetailError').hide();
	}
}

/*
 * for  Calculate whole Unbilled Amount of DS
 */
function unbilledAmount(){
	
	var totalUnbilledAmt = 0.0;
	var tableContent = new Array();

	userTable.column(13).data().sort().each( function ( d, j ) {
		var foundIndex = tableContent.indexOf((d.split('>')[1]).split('</')[0]);
		if(foundIndex > -1)
			tableContent.splice(foundIndex,1);
		unbilledAmt = (d.split('>')[1]).split('</')[0];
		totalUnbilledAmt = parseFloat(totalUnbilledAmt) + parseFloat(unbilledAmt); 
	});
}

/*
 * for  Update  Duty Slip Details Dropdown accornding to DataTable Records
 */
function refreshData12(){
	userTable = $('#userTable').DataTable({
    			"bLengthChange": true,
    			"bFilter": true,
    			"bSort": true,
    			"bInfo": true,
    			"bAutoWidth": true,
    			"aLengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]]
		});
	if(jobType == 'DutySlipUnbilled'){
    		$("#userTable thead tr th").each( function ( i ) {
    			var tableContent = new Array();
    		    var selectBoxField = "";
    		    if(i==8 || i==9 || i==10 || i==0 || i==14){
	    			if(i==8){
	    			    selectBoxField = "corporateId";				
	    			}else if(i==9){
	    			    selectBoxField = "bookedBy";
	    			}else if(i==10){
	    			    selectBoxField = "usedBy";
	    			}else if(i==0){
	    			    selectBoxField = "branch";
	    			}else if(i==14){
	    				selectBoxField = "closedBy";
	    			}
	    			$("#"+selectBoxField+" option").each(function(){
	    				if(selectBoxField == "usedBy"){
	    					usedByOptionValuesMap[$(this).text()] = $(this).val(); 
	    				}else
	    					allOptionValuesMap[$(this).text()] = $(this).val();    				  
	    			});
	    			$("#"+selectBoxField+"").empty().append('<option value="0"> - ALL - </option>');
	    			userTable.column(i).data().sort().each( function ( d, j ) {
	    				var foundIndex = tableContent.indexOf((d.split('>')[1]).split('</')[0]);
	    	 	    	if(foundIndex>-1)
	    	 	    		tableContent.splice(foundIndex,1);
	    	 	    	tableContent.push((d.split('>')[1]).split('</')[0]);
	    	 	    });
	    	 	    $.each(tableContent,function(index,val){
	    	 	    	if(selectBoxField == "usedBy"){
	    	 	    		 $("#"+selectBoxField+"").append('<option value="'+usedByOptionValuesMap[val]+'">'+val+'</option>');
	    				}else
	    					$("#"+selectBoxField+"").append('<option value="'+allOptionValuesMap[val]+'">'+val+'</option>');
	    		        } );
	    	 	    
	    	 	   /* Sorting Dropdown values */
	     	 	   var options = $('select#'+selectBoxField+' option');
	     	 	    var arr = options.map(function(_, o) {
	     	 	        return {
	     	 	            t: $(o).text(),
	     	 	            v: o.value
	     	 	        };
	     	 	    }).get();
	     	 	    arr.sort(function(o1, o2) {
	     	 	        return o1.t > o2.t ? 1 : o1.t < o2.t ? -1 : 0;
	     	 	    });
	     	 	    options.each(function(i, o) {
	     	 	        o.value = arr[i].v;
	     	 	        $(o).text(arr[i].t);
	     	 	    });
    		    }
    		 });
	}else	if((jobType == 'DutySlipClose') || jobType == 'DutySlipReceive'){
		$("#userTable thead tr th").each( function ( i ) {
			var tableContent = new Array();
		    var selectBoxField = "";
		    if(i==8 || i==7 || i==2 || i==0 ){
				if(i==8){
				    selectBoxField = "corporateId";				
				}else if(i==7){
				    selectBoxField = "rentalType";
				}else if(i==2){
				    selectBoxField = "carRegNo";
				}else if(i==0){
				    selectBoxField = "branch";
				}
    			$("#"+selectBoxField+" option").each(function(){
    			    allOptionValuesMap[$(this).text()] = $(this).val();    				  
    			});
    			$("#"+selectBoxField+"").empty().append('<option value="0"> - ALL - </option>');
    			userTable.column(i).data().sort().each( function ( d, j ) {
    				var foundIndex = tableContent.indexOf((d.split('>')[1]).split('</')[0]);
    	 	    	if(foundIndex>-1)
    	 	    		tableContent.splice(foundIndex,1);
    	 	    	tableContent.push((d.split('>')[1]).split('</')[0]);
    	 	    });
    	 	    $.each(tableContent,function(index,val){
    			    $("#"+selectBoxField+"").append('<option value="'+allOptionValuesMap[val]+'">'+val+'</option>');
    		    });
 				var options = $('select#'+selectBoxField+' option');
    	 	    var arr = options.map(function(_, o) {
    	 	        return {
    	 	            t: $(o).text(),
    	 	            v: o.value
    	 	        };
    	 	    }).get();
    	 	    arr.sort(function(o1, o2) {
    	 	        return o1.t > o2.t ? 1 : o1.t < o2.t ? -1 : 0;
    	 	    });
				
    	 	    options.each(function(i, o) {
    	 	        o.value = arr[i].v;
    	 	        $(o).text(arr[i].t);
    	 	    });
		    }
		 });
	}else	if(jobType == 'Invoice'){
		$("#userTable thead tr th").each( function ( i ) {
			var tableContent = new Array();
		    var selectBoxField = "";
		    if(i==8 || i==7 || i==6 || i==1 || i== 4|| i== 17){
				if(i==6){
				    selectBoxField = "corporateId";				
				}else if(i==7){
				    selectBoxField = "bookedBy";
				}else if(i==8){
				    selectBoxField = "usedBy";
				}else if(i==1){
				    selectBoxField = "branch";
				}else if(i==4){
				    selectBoxField = "invoiceNo";
				}else if(i==17){
				    selectBoxField = "closedBy";
				}
				$("#"+selectBoxField+" option").each(function(){
    				if(selectBoxField == "usedBy"){
    					usedByOptionValuesMap[$(this).text()] = $(this).val(); 
    				}else
    					allOptionValuesMap[$(this).text()] = $(this).val();    				  
    			});
    			$("#"+selectBoxField+"").empty().append('<option value="0"> - ALL - </option>');
    			userTable.column(i).data().sort().each( function ( d, j ) {
    				var foundIndex = tableContent.indexOf((d.split('>')[1]).split('</')[0]);
    	 	    	if(foundIndex>-1)
    	 	    		tableContent.splice(foundIndex,1);
    	 	    	tableContent.push((d.split('>')[1]).split('</')[0]);
    	 	    });
    	 	    $.each(tableContent,function(index,val){
    	 	    	if(selectBoxField == "usedBy"){
    	 	    		 $("#"+selectBoxField+"").append('<option value="'+usedByOptionValuesMap[val]+'">'+val+'</option>');
    				}else
    					$("#"+selectBoxField+"").append('<option value="'+allOptionValuesMap[val]+'">'+val+'</option>');
    		        });
    	 	    var options = $('select#'+selectBoxField+' option');
	   	 	    var arr = options.map(function(_, o) {
	   	 	        return {
	   	 	            t: $(o).text(),
	   	 	            v: o.value
	   	 	        };
	   	 	    }).get();
	   	 	    arr.sort(function(o1, o2) {
	   	 	        return o1.t > o2.t ? 1 : o1.t < o2.t ? -1 : 0;
	   	 	    });
	   	 	    options.each(function(i, o) {
	   	 	        o.value = arr[i].v;
	   	 	        $(o).text(arr[i].t);
	   	 	    });
		    }
		 });
	}
}

/*
 * --------------------------------------------------------------------
 * Generate Invoice from DS Unbilled Screen
 * --------------------------------------------------------------------
 */

function genInvoice(){
	var Error = false;
	var bDetailId = null;
	var corpId = null;
	var isSameCorporate = true;
	form_data = new FormData();
	var im = 0;
	if($('.edited').length<=0){
		alert("Please select at least one duty slip for invoice generation !!!");
		return false;
	}
	$(".edited").each(function(){
		var dutySlipId = $(this).attr('id');
		dutySlipId = dutySlipId.substring(9,dutySlipId.length);
		if($("#checkbox_"+dutySlipId).prop('checked') == true){
			var corporateId = $('#corporateId_'+dutySlipId).val();

			if(daysBetween($("#dutySlipDate_" + dutySlipId).text(),$("#dateInvoice").val())< 0){
				alert("One or more duty slip(s) has DS date later on Invoice date, Please correct !!!");
				Error = true;
				return false;
			}
			var taxName = [], taxAmt = [], taxPer = [] ;
			if(im == 0){
				corpId = $('#corporateId_'+dutySlipId).val();
				 var i =1,  actualIdString ;
					$(".taxName").each(function(){
						actualIdString=this.id;
						taxName.push(actualIdString);
					});
					
					var i =0;
					$(".taxPer").each(function(){
						var id = this.id;
						taxPer.push($("#"+id).val() == "" ? "0": $("#"+id).val());
					});
					
					var i =0;
					$(".taxAmt").each(function(){
						var id = this.id;
						taxAmt.push($("#"+id).val() == "" ? "0": $("#"+id).val());
					});
			} 
			if (corporateId == corpId){
					form_data.append("id",dutySlipId);
					form_data.append("jobType", jobType);
					form_data.append("invoiceDate", $("#dateInvoice").val());
					form_data.append("taxName",taxName);
					form_data.append("taxPercentage",taxPer);
					form_data.append("taxValues",taxAmt);
					
			}
			else{
				alert("Please Select Same Corporate For Generating Multiple Invoice");
				Error = true;
				return false;
			}
			im++;
		}
	});
	if(Error == true)
		return false;
	$("#submitButton").attr("disabled", true);
	$("#dataLoaderModal").show();
    $("#dataLoaderFade").show();
    
	$.ajax({  
		type: "POST",  	
		url:  ctx + "/updateDSForInvoice.html",  
		async:false,
		data: form_data, 
		processData: false,
		contentType: false,
		success: function(response){
			if(response.status == "Success"){
				$('#info').html(response.result);
				$('#dataTable2').html(response.dataGrid);
				invoiceNo = response.dataString1;
				alert("Invoice Number :  "+invoiceNo);
				type = "P";
				$("#dataLoaderModal").hide();
			    $("#dataLoaderFade").hide();
			    $("#userTable").dataTable().fnDestroy();
				refreshData();
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
	setTimeout(function() { document.getElementById("invoiceButton").disabled=false;},3000);
	searchBilling('R');
	checkMultipleInvoice(invoiceNo,corpId,type);
	 
}  

/*
 * -------------------------------------------------------------------------------------------------------------------------------
 * Open PopUp for Asking Invoice Date from User in the case of Invoicing on Duty Slip Close Screen
 * -------------------------------------------------------------------------------------------------------------------------------
 */
function dsInvoice(){
	$('#dsInv').val("Invoice No. : ");
	$('#dsCorpName').val($('#corporateName').val());
	$('#dsUsedBy').val($('#usedBy').val());
	$('#dsInvoiceDate').change();
	if(isNewCoverLetter && lCorporateId > 0 && lCorporateId != $("#corporateName").val() ){
		$("#coverLetterId").val(lCoverLetterId);
		$("#coverLetterDiv").show();
	}
	$('#dsInvoiceDatePanel').modal('show');
}

/*
 * for  Update Duty Slip Details in the case of Invoicing
 */
function saveUpdateDSForInvoicing(){
	$('#dsInvoiceDatePanel').modal('hide');
	validateDSDetails('I');
	var corpId = $("#corporateName").val();
	if(!isValidationError){
		$("#dataLoaderModal").show();
	    $("#dataLoaderFade").show();
	    form_data = new FormData();
		form_data.append("id", idForUpdate);
		form_data.append("manualSlipNo",$("#msNo").val() == null?"":$("#msNo").val());
		form_data.append("closeKms",$("#dsKmsIn").val() == null?"0":$("#dsKmsIn").val());
		form_data.append("timeTo",$("#dsTimeIn").val() == null?"0":$("#dsTimeIn").val());
		form_data.append("dateTo",$("#dsToDate").val());
		form_data.append("totalKms",$("#dsTotalKms").val() == null?"0":$("#dsTotalKms").val());
		form_data.append("totalHrs",$("#dsTotalHrs").val() == null?"0":$("#dsTotalHrs").val());
		form_data.append("minChgKms",$("#dsMinChgKms").val() == null?"0":$("#dsMinChgKms").val());
		form_data.append("minChgHrs",$("#dsminChgHrs").val() == null?"0":$("#dsminChgHrs").val());
		form_data.append("extraChgKms",extraChgKmRate);
		form_data.append("extraChgHrs",extraChgHrRate);
		form_data.append("tollTaxAmount",$("#dstollCharge").val() == null?"0":$("#dstollCharge").val());
		form_data.append("parkingAmount",$("#dsparkingCharge").val() == null?"0":$("#dsparkingCharge").val());
		form_data.append("stateTax",$("#dsstateTax").val() == null?"0":$("#dsstateTax").val());
		form_data.append("remarks",$("#dsremarks").val() == null?"":$("#dsremarks").val());
		form_data.append("totalFare",$("#dsslipAmount").val() == null?"0":$("#dsslipAmount").val());
		form_data.append("miscCharge",$("#dsmiscCharge").val() == null?"0":$("#dsmiscCharge").val());
		form_data.append("dateFrom",$("#dsFromDate").val() == null?"0":$("#dsFromDate").val());
		form_data.append("totalDay",$("#dsDayStay").val() == null?"0":$("#dsDayStay").val());
		form_data.append("timeFrom",$("#dsTimeOut").val() == null?"0":$("#dsTimeOut").val());
		form_data.append("nightAllow",$("#dsnightAllow").val() == null?"0":$("#dsnightAllow").val());
		form_data.append("dayAllow",$("#dsdayAllow").val() == null?"0":$("#dsdayAllow").val());
		form_data.append("guideCharge",$("#guideCharge").val() == null?"0":$("#guideCharge").val());
		form_data.append("fuelCharge",$("#fuelCharge").val() == null?"0":$("#fuelCharge").val());
		form_data.append("totalNight",$("#dsNightStay").val() == null?"0":$("#dsNightStay").val());
		form_data.append("tariff.id",$('#carTariff').val());
		form_data.append("basicFare",minCharge);
		form_data.append("nightAllowanceRate",nightCharge);
		form_data.append("outStationAllowRate",dayCharge);
		form_data.append("billingStatus",$('#billingStatus').val());
		form_data.append("extraHr",$("#dsextraChgHr").val());
		form_data.append("extraKm",$("#dsextraChgKm").val());
		form_data.append("jobType", "DutySlipUnbilled");
		form_data.append("dsInvoiceDate",$("#dsInvoiceDate").val());
		 var taxName = [], actualIdString ;
			$(".taxName").each(function(){
				actualIdString=this.id;
				taxName.push(actualIdString);
			});
			
			var taxPer = [] ;
			$(".taxPer").each(function(){
				var id = this.id;
				taxPer.push($("#"+id).val() == "" ? "0": $("#"+id).val());
			});
			
			var taxAmt = [] ;
			$(".taxAmt").each(function(){
				var id = this.id;
				taxAmt.push($("#"+id).val() == "" ? "0": $("#"+id).val());
			});
			
			form_data.append("taxName",taxName);
			form_data.append("taxPercentage",taxPer);
			form_data.append("taxValues",taxAmt);
		
		 $.ajax({
				type : "POST",
				url:  ctx + '/saveUpdateDSForInvoicing.html',
				processData: false,
				contentType: false,
				data : form_data,
				async:false,
				success : function(response) {
					if (response.status == "Success") {
						$('#editDetailInfo').html(response.result);
						invoiceNo = response.dataString1;
						alert("Invoice Number :  "+invoiceNo);
						type = "P";
					    $('#editDetailError').hide();
					    $('#editDetailInfo').show();
					    $("#dataLoaderModal").hide();
					    $("#dataLoaderFade").hide();		  
					    setTimeout( "$('#editDetailInfo').hide();", 1000);
					    //setTimeout(function(){$("#closingDSPanel").modal('hide');},1000);
					    $("#closingDSPanel").modal('hide');
					    $("#userTable").dataTable().fnDestroy();
					    refreshData();
					} else {
					    $("#dataLoaderModal").hide();
					    $("#dataLoaderFade").hide();
					    $('#editDetailError').html(response.result);
					    $('#editDetailInfo').hide();
					    $('#editDetailError').show();				    
					}
				},
				error : function(e) {
				    	$("#dataLoaderModal").hide();
				    	$("#dataLoaderFade").hide();
				    	alert('Error: ' + e);
				}
	     });
		 searchBilling('R');
		 checkMultipleInvoice(invoiceNo,corpId,type);
	}	 
}


/*
 * for  Calculate Total Invoice Amount of DS
 */
function invoiceAmount(){
	var totalInvoiceAmt = 0.0;
	var tableContent = new Array();

	userTable.column(16).data().sort().each( function ( d, j ) {
		var foundIndex = tableContent.indexOf((d.split('>')[1]).split('</')[0]);
		if(foundIndex > -1)
			tableContent.splice(foundIndex,1);
		invoiceAmt = (d.split('>')[1]).split('</')[0];
		totalInvoiceAmt = parseFloat(totalInvoiceAmt) + parseFloat(invoiceAmt); 
	});
	$("#invoiceAmt").html(totalInvoiceAmt);
}

function tariffAsPerCorporate(){
	corporateId= $("#corporateName option:selected").val();
	var invNo = $("#dsinvoiceNo").val();
	$.ajax({
		type : "POST",
		async:false,
		url:  ctx + '/tariffNameCorporateWise.html',
		data: "corporateId="+corporateId+"&invoiceNo="+invNo, 
		success : function(response) {
			if (response.status == "Success") {
				tariffList = response.corpTariffList;
				refillCombo('#dsBookedBy',response.generalMasterModelList);
				refillCombo('#dsUsedBy',response.autorizedUserModelList);
				refillCombo('#branchName',response.branchList);
				refillCombo('#carModel',response.carModelList);
				refillCombo('#newCoverLetterId',response.coverLetterList);
				if(response.coverLetterList != null ){
					if(response.coverLetterList.length > 0) 
						isNewCoverLetter = true;
				}
				response.corporateModel.branId == "0"?"":$("#branchName").val(response.corporateModel.branId).change();
	    	    $('#companyName').val(response.corporateModel.compId.id);
	    	    compName = response.corporateModel.compId.name;
	    	    $('#companyCode').val(response.corporateModel.compId.itemCode);
	    	    companyId=response.corporateModel.compId.id;
	    	    
	    	    if(response.carDetailModelList != null){
		    	    $('#carDetailsList').html("");
					$(response.carDetailModelList).each(function (idx, o) {
				        $('#carDetailsList').append("<option value='" + this.registrationNo + "'>" + this.registrationNo + "</option>");
				    });
				}
	    	    tariffDetailMap = null;
	    	    fetchTariffDet("H");
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

function fillTariff(value){
	$("#carTariff")
    .find('option')
    .remove()
    .end()
    .append('<option value="0">--- Select ---</option>')
    .val('0');
	$.each(tariffList, function (i, item) {
		if(item.masterId == value){
		    $("#carTariff").append($('<option>', { 
		        value: item.id,
		        text : item.name,
		    }));
		}
	});
	var idArr = [];
	$("#carTariff option").each(function(){
		idArr.push($(this).val()); 
	});
	console.log(idArr);
	getAllTariffValue(idArr);
}

function fillHub(){
	corporateId = $("#corporateName").val();
	branchId = $("#branchName").val();
	
	form_data = new FormData();
	form_data.append("branchId", branchId);
	form_data.append("corporateId", corporateId);

	$.ajax({  
		type: "POST",
		url:  ctx + "/fillHubAsBranch.html",  
	    processData: false,
	    contentType: false,
	    data : form_data,
	    async:false,
		success : function(response) {
			if (response.status == "Success") {
				refillCombo('#hubName',response.generalMasterModelList);
				refillCombo('#dsRentalType',response.corpRentalList);
				tariffList = response.corpTariffList;
			} else {
				$('#error').html(response.result);
				$('#info').hide('slow');
				$('#error').show('slow');
			}
		},  
		error: function(e){  
		}  
	});  
	if($("#dsCarRegNo").val() != "" && $('#carTariff').val() != ""){
		fillCarRelDetails($("#dsCarRegNo").val());
	}
}

function disableEnableControls(status){
	$("#dsNo").prop("readonly", status);
	$("#msNo").prop("readonly", status);
	$("#dsDate").prop("readonly", status);
	$("#corporateName").prop('disabled', status);
	$("#dsRentalType").prop('disabled', status);
	$("#dsUsedBy").prop('disabled', status);
	$("#otherClient").prop('disabled', status);
	$("#carTariff").prop('disabled', status);
	$("#dsBookedBy").prop('disabled', status);
	$("#dsMob").prop('disabled', status);
	$("#carModel").prop('disabled', status);
	$("#dscarType").prop('disabled', status);
	$("#dsCarRegNo").prop('disabled', status);
	$("#branchName").prop('disabled', status);
	$("#hubName").prop('disabled', status);
	$("#chauffeur").prop('disabled', status);
	$("#dsReportingAddress").prop("readonly", status);
	$("#dsDropAt").prop("readonly", status);
	$("#dsKmOut").prop("readonly", status);
	$("#dsTimeOut").prop("readonly", status);
	$("#dsFromDate").prop("readonly", status);
	$("#dsKmsIn").prop("readonly", status);
	$("#dsTimeIn").prop("readonly", status);
	$("#dsDate").prop("readonly", status);
	$("dsDate").prop("readonly", status);
	$("#dsDate").prop("readonly", status);
	$("#dsnightAllow").prop("readonly", status);
	$("#dsdayAllow").prop("readonly", status);
	$("#dstollCharge").prop("readonly", status);
	$("#dsparkingCharge").prop("readonly", status);
	$("#dsstateTax").prop("readonly", status);
	$("#dsmiscCharge").prop("readonly", status);
	$("#fuelCharge").prop("readonly", status);
	$("#guideCharge").prop("readonly", status);
	$("#dsremarks").prop("readonly", status);
	$("#dsslipAmount").prop("readonly", status);
	$("#dsinvoiceNo").prop("readonly", status);
	$("#bkgReference").prop("readonly", status);
	$("#dsToDate").prop("readonly", status);
	$("#dsNightStay").prop("readonly", status);
	$("#dsDayStay").prop("readonly", status);
	$("#dsDayStay").prop("readonly", status);
	$("#dsNo").prop("readonly", status);
	$("#update").prop('disabled', status);
	$("#save").prop('disabled', status);
	$("#billingStatus").prop('disabled', status);
}

function dutySlipClosingSearch(){
	checkDSNo();
	if(!isDSNoError){
		if($("#dsinvoiceNo").val() != "") $("#invoice").prop('disabled', false);
		var dSRetreive=	$("#dSRetreive").val();
		disableEnableControls(true);
		$('#updatechauffeurlink').css("pointer-events","none");
		dsRetriveString = dSRetreive;
		$.ajax({
			type : "POST",
			async:false,
			url:  ctx + '/dutySlipClosingSearch.html',
			data: "dSRetreive="+dSRetreive, 
			success : function(response) {
				if(response.dutySlipModel != null){
					resDS = response.dutySlipModel;
					idForUpdate = resDS.id;
					lCoverLetterId = resDS.lCoverLetterId;
					isMultiInvoice = resDS.isMultiInvoice;
					$('#billingStatus').val(resDS.billingStatus);
					$('#dsNo').val(resDS.dutySlipNo);
					$('#dSRetreive').val(resDS.dutySlipNo);
					$('#msNo').val(resDS.manualSlipNo);
					lCorporateId = response.bookingMasterModel.corporateId.id;
					$('#corporateName').val(lCorporateId).change();
					$("#dsMob").val(resDS.bookingDetailModel.mob != null ? resDS.bookingDetailModel.mob.id : "0");
					$("#carModel").val(resDS.bookingDetailModel.carModel != null ? resDS.bookingDetailModel.carModel.id : "0");
					$('#branchName').val(resDS.bookingDetailModel.branch.id).change();
				    branchId=resDS.bookingDetailModel.branch.id;
				    $('#hubName').val(resDS.bookingDetailModel.outlet.id);
				    
				    response.bookingMasterModel.bookedFor =="C"?$("#opClient").prop('checked',true):$("#opOther").prop('checked',true);
				    response.bookingMasterModel.bookedFor =="C"?$("#dsUsedBy").val($('#dsUsedBy option:contains("'+response.bookingMasterModel.bookedForName+'")').val()):$("#otherClient").val(response.bookingMasterModel.bookedForName);
					if($("#opClient").prop('checked')){
						$('#dsUsedBy').show();
						$('#otherClient').val("");
						$('#otherClient').hide();
					}else{
						$('#dsUsedBy').val("0");
						$('#dsUsedBy').hide();
						$('#otherClient').show()
					}
				    /*$('#dsUsedBy').val(response.bookingMasterModel.bookedForName);*/
					$('#dsBookedBy').val(response.bookingMasterModel.bookedBy.id);
					$('#dsReportingAddress').val(resDS.bookingDetailModel.reportingAddress);
					$('#dsDropAt').val(resDS.bookingDetailModel.toBeRealeseAt);
					$('#dsRentalType').val(resDS.bookingDetailModel.rentalType.id).change();
					fetchModel = resDS.bookingDetailModel.carModel.id;
					$('#dsKmOut').val(resDS.openKms);
					$('#dsKmsIn').val(resDS.closeKms);
					$('#dsMinChgKms').val(resDS.minChgKms);
					$('#dsTotalKms').val(resDS.totalKms);

					$('#dscarType').val(resDS.carDetailModel.model.name);
					$('#carTariff').val(resDS.tariff == null ? resDS.bookingDetailModel.tariff.id :  resDS.tariff.id).change();
					$('#dsCarRegNo').val(response.dataString1).change();
					$('#carTariff').change();
					if(response.dutySlipModel.isUpdatedChauffeur == "Y" || resDS.carDetailModel.ownType =='A'){
						$('#chauffeur').val(resDS.chauffeurName+" - " + resDS.chauffeurMobile);
					}else{
						if(resDS.chauffeurModel != null) $('#chauffeur').val(resDS.chauffeurModel.name+" - " + resDS.chauffeurModel.mobileNo);
					}
					
					if(response.carAllocationModel == null){
						for(var iCount = 0; iCount < $("input[name='checkBox1']").length; iCount++){
							if(($('input[name="checkBox1"]:eq('+iCount+')').val()).indexOf("Adhoc") > -1){
								$('input[name="checkBox1"]:eq('+iCount+')').prop('checked',true);
							}
						}
					}else{
						$('#'+response.carAllocationModel.carOwnerType.id).prop('checked',true);
					}
	
					$('#'+resDS.billingStatus).prop('checked',true);
					var allDTime = getTimeStampTo_ddMMyyyyHHmmss(resDS.dispatchDateTime);
				    var allDTimeArray = allDTime.split(" ");
				    allDTimeArray[1] =allDTimeArray[1];
				    var dateFromString =  getTimeStampTo_DDMMYYYY(resDS.dateFrom);
				    var dateToString = getTimeStampTo_DDMMYYYY(resDS.dateTo);
				    var dsDate = getTimeStampTo_DDMMYYYY(resDS.dispatchDateTime);
				    var invDate = getTimeStampTo_DDMMYYYY(resDS.invoiceDate);
				    if(resDS.dateFrom == null){
					    var allDTime = getTimeStampTo_ddMMyyyyHHmmss(resDS.dispatchDateTime);
				    	var allDTimeArray = allDTime.split(" ");
				    	$("#dsFromDate").val(allDTimeArray[0]);
				    	$("#dsToDate").val(allDTimeArray[0]).blur();
				    }else{	
					    $("#dsFromDate").val(dateFromString);
					    $("#dsToDate").val(dateToString).blur();
				    }
				    $("#dsDate").val(dsDate);
					$('#dsInvoiceDate').val(invDate);
					if(resDS.timeFrom != null){
			    		var time = resDS.timeFrom.split(":");
			    		$("#dsTimeOut").val(time[0]+":"+time[1]);
					}else{
			    		var time = allDTimeArray[1].split(":");
			    		$("#dsTimeOut").val((time[0]<10?"0"+time[0]:time[0])+":"+(time[1]<10?"0"+time[1]:time[1]));
					}
				    $('#dsTimeIn').val(resDS.timeTo);
				    var minMintueSlab = response.dsIdCount;
				    var timeDiff = null;
				    var start =(dateFromString).concat(" ").concat($("#dsTimeOut").val());
				    var end =(dateToString).concat(" ").concat($("#dsTimeIn").val());
				    var mst = calculateHrs(end,start);
				    var d = moment.duration(mst);
				    var minute = parseInt(moment.utc(mst).format("mm")) ;
				    var fMinChgHr;
				    if($("#dsminChgHrs").val() !="")
					   fMinChgHr = $("#dsminChgHrs").val();
				    else
					   fMinChgHr = '0';
				    var roundedMiuntes = Math.ceil(minute / minMintueSlab);
				    roundedMiuntes = (roundedMiuntes * minMintueSlab);
				   
				    if(roundedMiuntes == 60){
					   timeDiff = Math.floor(d.asHours() + 1) + ":00";   
					   extHr = parseInt(Math.floor(d.asHours() + 1)) - parseInt(fMinChgHr); 
					   if(extHr < 0 ) extHr = "0";
				    }else{
			    	   timeDiff = Math.floor(d.asHours()) + ":" + ( roundedMiuntes < 10 ? "0"+ roundedMiuntes : roundedMiuntes);   
				       extHr = parseInt(Math.floor(d.asHours())) - parseInt(fMinChgHr); 
					   if(extHr < 0 ) 
						   extHr = 0;
					   else
						   extHr = extHr + ":" + (roundedMiuntes <10 ? "0"+roundedMiuntes : roundedMiuntes);
				    }
			        if(Math.floor(d.asHours()) < 0 ) timeDiff = "0:00";
				    $('#dsTotalHrs').val(timeDiff);
				    $('#dsextraChgHr').val(extHr);
				    var extraKm = 0;
				    if(parseInt(resDS.totalKms) > parseInt(resDS.minChgKms)){
				    	extraKm = parseInt(resDS.totalKms) - parseInt(resDS.minChgKms);
					}
				    $("#dsextraChgKm").val(extraKm);
				    $("#dsnightAllow").val(resDS.nightAllow);
				    $("#dsdayAllow").val(resDS.dayAllow);
				    $('#dstollCharge').val(resDS.tollTaxAmount);
				    $('#dsparkingCharge').val(resDS.parkingAmount);
				    $('#dsstateTax').val(resDS.stateTax);
				    $('#dsmiscCharge').val(resDS.miscCharge);
				    $('#dsremarks').val(resDS.remarks);
				    $('#dsinvoiceNo').val(resDS.invoiceNo);
				    $('#dsbookingNo').val(response.bookingMasterModel.bookingNo);
				    $('#fuelCharge').val(resDS.fuelCharge);
				    $('#guideCharge').val(resDS.guideCharge);
				    $("#dsslipAmount").val(resDS.totalFare);
				    $("#dsNightStay").val(resDS.totalNight);
					$("#dsDayStay").val(resDS.totalDay).change();
					$("#dsnightAllow").val(resDS.nightAllow);
					$("#dsdayAllow").val(resDS.dayAllow);
				    $("#billingStatus").val(resDS.billingStatus);
				    $.each(response.corporateTariffDetModelList,function(index,value){
				        /*if(value.name == 'Extra Hrs'){
				         $("#dsextraChgHr").val(value.tariffValue);
				        }else if(value.name == 'Extra Kms'){
				         $("#dsextraChgKm").val(value.tariffValue);
				        }else*/ 
				    	if(value.name == 'Night Charge'){
				    		nightCharge = value.tariffValue;
				        }else if(value.name == 'Day Allowence'){
					        dayCharge = value.tariffValue;
				        }else if(value.name == resDS.bookingDetailModel.tariff.name){
				        	minCharge = parseFloat(value.tariffValue);
				        	finalBasicFare = minCharge;
				        	dayChgDutySlip();
				        }
				    });
					$("#Edit").prop('disabled', false);
					totalSlipAmountDutySlip();
			    }else{
			    	alert("Please check Duty Slip No and Try Again !!!");
					disableEnableControls(false);
					$('#updatechauffeurlink').css("pointer-events","visible");
					$("#Edit").prop('disabled', true);
			    }
				//calculateKmDutySlip();
				dsTimeInCal();
			},
			error : function(e) {
				alert('Error: ' + e);
			}
		});
	}
	if($("#dsinvoiceNo").val() != "") $("#invoice").prop('disabled', true);
}

function editDetail(){
	disableEnableControls(false);
	if(isMultiInvoice == "M") 	$("#corporateName").prop('disabled', true);
	$("#dsinvoiceNo").prop('readOnly', true);
	$('#updatechauffeurlink').css("pointer-events","visible");
	$("#save").prop('disabled', true);
	$("#Edit").prop('disabled', true);
}

/*
 * ---------------------------------------------------
 * For Check Invoice is Generated or Not   
 * ---------------------------------------------------
 */
function openInvoiceModel(){
	if($('#dsinvoiceNo').val() != null && $('#dsinvoiceNo').val() != ""){
		dutySlipInvoice("");
	}else{
		saveUpdateDsDutySlipClosing();
	}
}
/*
 * -------------------------------------------------------------
 * For Update DutySlip From DS Closing Screen
 * -------------------------------------------------------------
 */

function saveUpdateDsDutySlipClosing(){
	var newCoverLetterId = 0;
	if( isNewCoverLetter && lCorporateId > 0 && lCorporateId != $("#corporateName").val() ){
		newCoverLetterId = $("#newCoverLetterId").val();
		if(newCoverLetterId == 0){
			alert("You are selected new corporate for cover letter generated invoice \n Please also select new coverletter no  !!!");
			return false;
		}
	}
	$('#invoiceDatePanel').modal('hide');
	$("#dataLoaderModal").show();
    $("#dataLoaderFade").show();
	   form_data = new FormData();
	   form_data.append("id", idForUpdate);
	   form_data.append("bookingDetailModel.bookingMasterModel.corporateId.id",$("#corporateName").val());
	   form_data.append("bookingDetailModel.bookingMasterModel.bookedBy.id",$("#dsBookedBy").val());
	   
	   form_data.append("bookingDetailModel.bookingMasterModel.bookedFor", $("#opClient").prop('checked')==true?"C":"O");
	   form_data.append("bookingDetailModel.bookingMasterModel.bookedForName", $("#opClient").prop('checked')==true?$("#dsUsedBy option:selected").text():$("#otherClient").val());

	   form_data.append("bookingDetailModel.mob.id",$("#dsMob").val());
	   form_data.append("bookingDetailModel.carModel.id",$("#carModel").val());
	   form_data.append("bookingDetailModel.branch.id",$("#branchName").val());
	   form_data.append("bookingDetailModel.outlet.id",$("#hubName").val());
	   form_data.append("bookingDetailModel.rentalType.id",$("#dsRentalType").val());
	   
	   form_data.append("manualSlipNo",$("#msNo").val());
	   form_data.append("openKms",$("#dsKmOut").val());
	   form_data.append("timeFrom",$("#dsTimeOut").val());
	   form_data.append("closeKms",$("#dsKmsIn").val());
	   form_data.append("timeTo",$("#dsTimeIn").val());
	   form_data.append("dateTo",$("#dsToDate").val());
	   form_data.append("totalKms",$("#dsTotalKms").val());
	   form_data.append("totalHrs",$("#dsTotalHrs").val());
	   form_data.append("minChgKms",$("#dsMinChgKms").val());
	   form_data.append("minChgHrs",$("#dsminChgHrs").val());
	   form_data.append("extraChgKms",extraChgKmRate);
	   form_data.append("extraChgHrs",extraChgHrRate);
	   form_data.append("nightAllow",$("#dsnightAllow").val());
	   form_data.append("dayAllow",$("#dsdayAllow").val());
	   form_data.append("tollTaxAmount",$("#dstollCharge").val());
	   form_data.append("parkingAmount",$("#dsparkingCharge").val());
	   form_data.append("stateTax",$("#dsstateTax").val());
	   form_data.append("remarks",$("#dsremarks").val());
	   form_data.append("totalFare",$("#dsslipAmount").val());
	   form_data.append("miscCharge",$("#dsmiscCharge").val());
	   form_data.append("dateFrom",$("#dsFromDate").val());
	   form_data.append("carDetailModel.id",carDetailId);
	   form_data.append("tariff.id",$("#carTariff").val());
	 /*  form_data.append("bookingDetailModel.id",bookingDetailIds);*/
	   form_data.append("fuelCharge",$("#fuelCharge").val());
	   form_data.append("guideCharge",$("#guideCharge").val());
	   form_data.append("totalNight",$("#dsNightStay").val());
	   form_data.append("totalDay",$("#dsDayStay").val());
	   if($("#isUpdatedChauffeur").val() == "Y"){
		   form_data.append("isUpdatedChauffeur",$("#isUpdatedChauffeur").val());
		   var chauffeur =  $('#chauffeur').val().split("-");
		   form_data.append("chauffeurName",chauffeur[0]);
		   form_data.append("chauffeurMobile",chauffeur[1]);
	   }else if(chModelMap[$("#chauffeur").val()]!=null && !chModelMap[$("#chauffeur").val()]==""){
		   form_data.append("chauffeurModel.id",chModelMap[$("#chauffeur").val()]);
	   }   
	   form_data.append("vendorModel.id",(vendorId == null ? 0 : vendorId));
	   form_data.append("dispatchDateTime",$("#dsFromDate").val());
	   form_data.append("dutySlipDate",$("#dsDate").val());
       form_data.append("nightAllowanceRate",(nightCharge == null ? 0.00 : nightCharge));
       form_data.append("outStationAllowRate",(dayCharge == null ? 0.00 : dayCharge));
       form_data.append("basicFare",minCharge);
       form_data.append("lCoverLetterId",lCoverLetterId);
       form_data.append("lNewCoverLetterId",newCoverLetterId);

       var taxName = [], actualIdString ;
		$(".taxName").each(function(){
			actualIdString=this.id;
			taxName.push(actualIdString);
		});
		
		var taxPer = [] ;
		$(".taxPer").each(function(){
			var id = this.id;
			taxPer.push($("#"+id).val() == "" ? "0": $("#"+id).val());
		});
		
		var taxAmt = [] ;
		$(".taxAmt").each(function(){
			var id = this.id;
			taxAmt.push($("#"+id).val() == "" ? "0": $("#"+id).val());
		});
		
		form_data.append("taxName",taxName);
		form_data.append("taxPercentage",taxPer);
		form_data.append("taxValues",taxAmt);
	    form_data.append("billingStatus",$("#billingStatus").val());
	   
	    $.ajax({
	     type : "POST",
	     url:  ctx + '/saveUpdateDsDutySlipClosing.html',
	     processData: false,
	     contentType: false,
	     data : form_data,
	     async:false,
	     success : function(response) {
	      if (response.status == "Success") {
	          $('#editDetailInfo').html(response.result);
	      	  $("#dataLoaderModal").hide();
		      $("#dataLoaderFade").hide();
	          $('#editDetailError').hide();
	          $('#editDetailInfo').show();
	          resetDetail();
	      } else {
	          $("#dataLoaderModal").hide();
	          $("#dataLoaderFade").hide();
	          $('#editDetailError').html(response.result);
	          $('#editDetailInfo').hide();
	          $('#editDetailError').show();        
	      }
	     },
	     error : function(e) {
	          alert('Error: ' + e);
	     }
     });

}

function saveDs(){
	if(saveInvoiceFlag == 'Invoice'){
		$('#invoiceDatePanel').modal('hide');
	}
    validateDutySlipDetails();
	if(!isValidationError && $("#dSRetreive").val() != ""){  
		saveUpdateDutySlipForInvoicing();
	}else if(!isValidationError){
	   saveOrInvoice = saveInvoiceFlag;
	   form_data = new FormData();
	   form_data.append("id", idForUpdate);
	   form_data.append("bookedBy.id",$("#dsBookedBy").val());
	   form_data.append("bookingTakenBy.id",loginUserId);
	   form_data.append("cityName",$("#branchName option:selected").text());
	   form_data.append("corporateId.id",$("#corporateName").val())
	   form_data.append("companyName",companyId);
	   form_data.append("branch", branchId);
	   form_data.append("bookedFor", $("#opClient").prop('checked')==true?"C":"O");
	   form_data.append("bookedForName", $("#opClient").prop('checked')==true?$("#dsUsedBy option:selected").text():$("#otherClient").val());
		   
	   var bookingDetailsModelArraySize=0,loopCount=0;
		bookingDetailsModelArray = new Array();
		bookingDetailsModelArraySize = bookingDetailsModelArray.length;
		loopCount = 1;
		fromIndex = (bookingDetailsModelArraySize-0);
		toIndex   = fromIndex + (loopCount-0);
		for (var iCount = fromIndex; iCount < toIndex; iCount++) {
			form_data.set("bookingDetailModel["+iCount+"].company.id",companyId);
			form_data.set("bookingDetailModel["+iCount+"].branch.id", $("#branchName").val());
			form_data.set("bookingDetailModel["+iCount+"].pickUpDate", $("#dsFromDate").val());
			form_data.set("bookingDetailModel["+iCount+"].startTime", $('#dsTimeOut').val());
			form_data.set("bookingDetailModel["+iCount+"].pickUpTime", $('#dsTimeOut').val());
			form_data.set("bookingDetailModel["+iCount+"].mob.id", $('#dsMob').val());
			form_data.set("bookingDetailModel["+iCount+"].mob.name", $('#dsMob option:selected').text());
			form_data.set("bookingDetailModel["+iCount+"].carModel.id", $("#carModel").val());
			form_data.set("bookingDetailModel["+iCount+"].carModel.name", $('#carModel option:selected').text());
			form_data.set("bookingDetailModel["+iCount+"].rentalType.id", $('#dsRentalType').val());
			form_data.set("bookingDetailModel["+iCount+"].rentalType.name", $('#dsRentalType option:selected').text());
			form_data.set("bookingDetailModel["+iCount+"].tariff.id", $('#carTariff').val());
			form_data.set("bookingDetailModel["+iCount+"].reportingAddress", $("#dsReportingAddress").val());
			form_data.set("bookingDetailModel["+iCount+"].toBeRealeseAt", $("#dsDropAt").val());
			form_data.set("bookingDetailModel["+iCount+"].outlet.id", $("#hubName").val());
			bookingDetailsModelArray = new Array();
		}
		   
	   $.ajax({
		     type : "POST",
		     url:  ctx + '/saveDs.html',
		     processData: false,
		     contentType: false,
		     data : form_data,
		     async:false,
		     success : function(response) {
		    	 
		      if (response.status == "Success") {
		    	  bookingDetailIds=response.bookingDetailId;
		    	  $("#dataLoaderModal").hide();
				  $("#dataLoaderFade").hide();
		          $('#editDetailInfo').html(response.result);
		          $('#editDetailError').hide();
		          $('#editDetailInfo').show();
		          alert("Your Booking Details has been saved with booking number :  "+response.bookingMasterModel.bookingNo);
		      } else {
		          $("#dataLoaderModal").hide();
		          $("#dataLoaderFade").hide();
		          $('#editDetailError').html(response.result);
		          $('#cancelReasonError').html(response.result);
		          $('#editDetailInfo').hide();
		          $('#editDetailError').show();      
		          $('#cancelReasonError').show();   
		      }
		     },
		     error : function(e) {
		        
		          alert('Error: ' + e);
		     }
         });
	   	 saveDutySlip();
	}
}

/*
* for calculate total Kms 
*/
function calculateKmDutySlip(){
	if($("#dsKmOut").val() == "" || $("#dsKmsIn").val() == ""){
		return false;
	}
	var kmOut = parseInt($("#dsKmOut").val());
	var kmIn = parseInt($("#dsKmsIn").val());
	if(kmIn != ""){
		if (kmIn >= kmOut){
			var totalKm = kmIn  - kmOut;
			$("#dsTotalKms").val(totalKm);
		    if($("#dsRentalType option:selected").text() == "Local Run"){
		    	checkKmForCarTariffDs();
		    }
		    
			if($("#dsMinChgKms").val() == ""){
		    	extKm = 0;
			}else{ 
				extKm =parseInt(totalKm) - parseInt($("#dsMinChgKms").val());
			}
		}else{
			alert("Kms In is not less then Kms out, Please check !!!");
			$("#dsKmsIn").val($("#dsKmOut").val());
			return false;
		}
		if(extKm < 0) extKm = 0;
		$("#dsextraChgKm").val(extKm);
		totalSlipAmountDutySlip();
	}
}

function dsTimeInCal(){
	   if($("#dsTimeIn").val() == "") return false;
	   if($("#dsToDate").val() == "" ) $("#dsToDate").val($("#dsFromDate").val());
	   
	   var start =($("#dsFromDate").val()).concat(" ").concat($("#dsTimeOut").val());
	   var end =($("#dsToDate").val()).concat(" ").concat($("#dsTimeIn").val());
	   
	   var ms = calculateHrs(end,start);
	   var d = moment.duration(ms);
	   
	   if (d < 0){
		   var endDate = $("#dsFromDate").val();
		   var edate = endDate.split("/").reverse().join("-");
			incrementDate(edate);
			endDate = toDate.split("-").reverse().join("/");
			$("#dsToDate").val(endDate);
	   }
	   countHoursDutySlip();
}

	/*
	* for Hours Count
	*/
	function countHoursDutySlip(){
	   var diff = "";
	   var start =($("#dsFromDate").val()).concat(" ").concat($("#dsTimeOut").val());
	   var end =($("#dsToDate").val()).concat(" ").concat($("#dsTimeIn").val());
	   
	   var ms = calculateHrs(end,start);
	   var d = moment.duration(ms).toString();
	   var hrs="",minute="";
	   var indexOfH = d.indexOf("H");
	   var indexOfM = d.indexOf("M");
	   if(indexOfH != -1){
		   hrs = parseInt(d.substring(2,indexOfH));
	   }
	   if(indexOfM != -1){
		   if(indexOfH == -1) indexOfH = 2; 
		   minute = parseInt(d.substring((indexOfH + 1),indexOfM));
	   }
	   var roundedMiuntes = 0;
	   if(minute > 0){
		   if(minMinuteSlab === undefined) minMinuteSlab = 15;
		   roundedMiuntes = Math.ceil(minute / minMinuteSlab);
		   roundedMiuntes = (roundedMiuntes * minMinuteSlab);
	   }
	   var fMinChgHr;
	   if($("#dsminChgHrs").val() !="")
		   fMinChgHr = $("#dsminChgHrs").val();
	   else
		   fMinChgHr = '0';
	   if(roundedMiuntes == 60){
		   diff = (hrs + 1).toString().concat(":00");   
		   extHr = Math.floor(hrs + 1) - parseInt(fMinChgHr); 
		   if(extHr < 0 ) extHr = "0";
	   }else{
		   diff = ( hrs < 10 ? "0".concat(hrs.toString()) : (hrs.toString())).concat(":").concat( roundedMiuntes < 10 ? "0".concat(roundedMiuntes.toString()) : roundedMiuntes.toString());   
		   extHr = hrs - parseInt(fMinChgHr); 
		   if(extHr < 0 ) 
			   extHr = 0;
		   else
			   extHr = extHr.toString().concat(":").concat(roundedMiuntes.toString());
 	   }

	   $("#dsTotalHrs").val(diff);
	   
	   if($("#dsRentalType option:selected").text() == "Local Run"){
		   checkHrForCarTariffDs();	
	   }else if($("#dsRentalType option:selected").text() == "Outstation"){
		   extHr = 0;
	   }

	   $("#dsextraChgHr").val(extHr);
	   totalSlipAmountDutySlip();
	   setTimeout( "$('#editDetailError').hide();", 10000);
	}  

	/*
	* for Calculating Total Slip Amount
	*/
	function totalSlipAmountDutySlip(){
		extHr = (extHr == null || extHr == ''? "0": extHr);
		extKm = (extKm == null || extKm == ''? "0": extKm);
		finalBasicFare = (finalBasicFare == null || finalBasicFare == ''? "0": finalBasicFare);
		extraChgHr = $("#dsextraChgHr").val()!=""?$("#dsextraChgHr").val():'0:00';
		
		var extraHrsArr = extraChgHr.split(":"); 
		var coficent = extraHrsArr[1] / 15;
		coficent = coficent * 25;
		extraChgHr = extraHrsArr[0] + "." + coficent;
		
		extraChgKm = $("#dsextraChgKm").val()!=""?$("#dsextraChgKm").val():'0';
		dsnightAllow = $("#dsnightAllow").val()!=""?$("#dsnightAllow").val():'0';
		dsdayAllow = $("#dsdayAllow").val()!=""?$("#dsdayAllow").val():'0';
		guideCharge = $("#guideCharge").val()!=""?$("#guideCharge").val():'0';
		fuelCharge = $("#fuelCharge").val()!=""?$("#fuelCharge").val():'0';
		miscCharge = $("#dsmiscCharge").val()!=""?$("#dsmiscCharge").val():'0';
		toll = $("#dstollCharge").val()!=""?$("#dstollCharge").val():'0';
		parking = $("#dsparkingCharge").val()!=""?$("#dsparkingCharge").val():'0';
		stTax = $("#dsstateTax").val()!=""?$("#dsstateTax").val():'0';
				
	    if($("#dsRentalType option:selected").text() == "Outstation") extraChgHr = 0.00;

		totalAmount =  parseFloat(finalBasicFare) + 
					   (parseFloat(extraChgHrRate) * parseFloat(extraChgHr)) + 
					   (parseFloat(extraChgKmRate) * parseFloat(extraChgKm)) + 
					   parseFloat(dsnightAllow) + 
					   parseFloat(dsdayAllow) +
					   parseFloat(guideCharge) + 
					   parseFloat(fuelCharge) + 
					   parseFloat(miscCharge) + 
					   parseFloat(stTax) + 
					   parseFloat(parking) + 
					   parseFloat(toll);
		totalAmount = Math.round(totalAmount * 100 / 100);
		$("#dsslipAmount").val(totalAmount);
	}
	/*
	 * -------------------------------------------------------------------------------------
	* for Days Count on DS CLOSING SCREEN 
	* --------------------------------------------------------------------------------------
	*/
	function countdaysDutySlip(){
	  var oneday = 24*60*60*1000;
	  var sd = $("#dsFromDate").val();
	  var ed = $("#dsToDate").val();
	  if(treatAsUTC(ed) >= treatAsUTC(sd)){
		  var days = ((treatAsUTC(ed) - treatAsUTC(sd))/(oneday)) + 1;
		  
		  if($("#dsRentalType option:selected").text() == 'Outstation'){
			  $("#dsDayStay").val(days);
			  $("#dsMinChgKms").val(minChgKM *  days);
			  finalBasicFare = days * minCharge;
		  }
		  if(days >1){
			  $("#dsNightStay").val(days - 1);
		  }else{ 
			  $("#dsNightStay").val("");
		  	  $("#dsnightAllow").val("");
		  }	  
		  	  //countHoursDutySlip();
		  }else{
			  $("#dsToDate").focus();
		  }
	  nightChgDutySlip();
	  dayChgDutySlip();
	  totalSlipAmountDutySlip();
	}
	/*
	 * ------------------------------------------------------------------------
	 * For  Calculating Night Charge on DS CLOSING SCREEN 
	 *-------------------------------------------------------------------------
	 */
	function nightChgDutySlip(){
	  nightStay = $("#dsNightStay").val()== ""?'0':$("#dsNightStay").val();
	  finalNightChg = nightStay * nightCharge;
	  $("#dsnightAllow").val(finalNightChg == '0'?'':finalNightChg);
	  totalSlipAmountDutySlip();
	}
	/*
	 * -------------------------------------------------------------------------------
	 * For Calculating Day Charge On DS CLOSING SCREEN
	 * -------------------------------------------------------------------------------
	 */
	function dayChgDutySlip(){
	  dayStay = $("#dsDayStay").val()== ""?'0':$("#dsDayStay").val();
	  finalDayChg  = dayStay * dayCharge;
	  $("#dsdayAllow").val(finalDayChg  == '0'?'':finalDayChg );

	  if($("#dsRentalType option:selected").text() == 'Outstation'){
		  $("#dsMinChgKms").val(minChgKM * dayStay);
		  finalBasicFare = dayStay * minCharge;
		  calculateKmDutySlip();
		  fMinChgHr = $("#dsminChgHrs").val();
		  extHr = 0;
		  $("#dsextraChgHr").val(extHr);
	  }
	}
/*
 * ---------------------------------------------------------------------------------------------
 * For Saving DS FROM DS CLOSING SCREEN
 * ---------------------------------------------------------------------------------------------
 */

function saveDutySlip(){
	   form_data = new FormData();
	   form_data.append("manualSlipNo",$("#msNo").val());
	   form_data.append("openKms",$("#dsKmOut").val());
	   form_data.append("timeFrom",$("#dsTimeOut").val());
	   form_data.append("closeKms",$("#dsKmsIn").val());
	   form_data.append("timeTo",$("#dsTimeIn").val());
	   form_data.append("dateTo",$("#dsToDate").val());
	   form_data.append("totalKms",$("#dsTotalKms").val());
	   form_data.append("totalHrs",$("#dsTotalHrs").val());
	   form_data.append("minChgKms",$("#dsMinChgKms").val());
	   form_data.append("minChgHrs",$("#dsminChgHrs").val());
	   form_data.append("extraChgKms",extraChgKmRate);
	   form_data.append("extraChgHrs",extraChgHrRate);
	   form_data.append("nightAllow",$("#dsnightAllow").val());
	   form_data.append("dayAllow",$("#dsdayAllow").val());
	   form_data.append("tollTaxAmount",$("#dstollCharge").val());
	   form_data.append("parkingAmount",$("#dsparkingCharge").val());
	   form_data.append("stateTax",$("#dsstateTax").val());
	   form_data.append("remarks",$("#dsremarks").val());
	   form_data.append("totalFare",$("#dsslipAmount").val());
	   form_data.append("miscCharge",$("#dsmiscCharge").val());
	   form_data.append("dateFrom",$("#dsFromDate").val());
	   form_data.append("carDetailModel.id",carDetailId);
	   form_data.append("tariff.id",$("#carTariff").val());
	   form_data.append("bookingDetailModel.id",bookingDetailIds);
	   form_data.append("fuelCharge",$("#fuelCharge").val());
	   form_data.append("guideCharge",$("#guideCharge").val());
	   form_data.append("totalNight",$("#dsNightStay").val());
	   form_data.append("totalDay",$("#dsDayStay").val());
	   if($("#isUpdatedChauffeur").val() == "Y"){
		   form_data.append("isUpdatedChauffeur",$("#isUpdatedChauffeur").val());
		   var chauffeur =  $('#chauffeur').val().split("-");
		   form_data.append("chauffeurName",chauffeur[0]);
		   form_data.append("chauffeurMobile",chauffeur[1]);
	   }else if(chModelMap[$("#chauffeur").val()]!=null && !chModelMap[$("#chauffeur").val()]==""){
		   form_data.append("chauffeurModel.id",chModelMap[$("#chauffeur").val()]);
	   }  
	   if(vendorId != null || vendorId != undefined)	   form_data.append("vendorModel.id",vendorId);
	   form_data.append("dispatchDateTime",$("#dsDate").val());
	   form_data.append("dutySlipDate",$("#dsDate").val());
	   form_data.append("dispatchedBy.id",loginUserId);
       form_data.append("nightAllowanceRate",nightCharge);
       form_data.append("outStationAllowRate",dayCharge);
       form_data.append("basicFare",minCharge);
	   form_data.append("billingStatus",$("#billingStatus").val());
	    $.ajax({
		     type : "POST",
		     url:  ctx + '/saveDutySlip.html',
		     processData: false,
		     contentType: false,
		     data : form_data,
		     async:false,
		     success : function(response) {
		    	 
		      if (response.status == "Success") {
		    	  alert("Duty Slip No: "+''+response.dataGrid);
		    	  idForUpdate= response.dutySlipModel.id;
	    		  $("#dataLoaderModal").hide();
			      $("#dataLoaderFade").hide();
		          $('#editDetailInfo').html(response.result);
		          $('#editDetailError').hide();
		          $('#editDetailInfo').show();
		      } else {
		          $("#dataLoaderModal").hide();
		          $("#dataLoaderFade").hide();
		          $('#editDetailError').html(response.result);
		          $('#editDetailInfo').hide();
		          $('#editDetailError').show();        
		      }
		     },
		     error : function(e) {
		        
		          alert('Error: ' + e);
		     }
		         });
	    if(saveOrInvoice == 'Invoice'){
  		  saveUpdateDutySlipForInvoicing();
  	    }else{
  	    	resetDetail();
  	    }
}

/*
 * ---------------------------------------------------------------------------------------------
 * For Getting Car Owner Type On DS CLOSING SCREEN
 * ---------------------------------------------------------------------------------------------
 */

function getCarOwnerType(){
	carAlocationId=$("#dsCarRegNo").val();
	$.ajax({
		 type : "POST",
	     url:  ctx + '/getCarOwner.html',
	     data : "id="+carAlocationId, 
	     async:false,
	     success : function(response) {
	      if (response.status == "Success") {
	    	  	vendorId = response.carAllocationModel.vendorId.id;
	    	  	/*$('#chauffeur').append($('<option>', { 
					value: response.carAllocationModel.chauffeurId.id,
					text : response.carAllocationModel.chauffeurId.name +' - ' +response.carAllocationModel.chauffeurId.mobileNo,
				}));	*/
	    	  	chauffeurId = response.carAllocationModel.chauffeurId.id;
	    	  	$('#chauffeur').val(response.carAllocationModel.chauffeurId.name +' - ' +response.carAllocationModel.chauffeurId.mobileNo);
	    	    $('#'+response.carAllocationModel.carOwnerType.id).prop('checked',true);
		        carDetailId = response.carAllocationModel.carDetailModelId.id;
		      } },
         });
	}
/*
 * ---------------------------------------------------------------------------------------------
 * For Filling Branch on DS CLOSING SCREEN
 * ---------------------------------------------------------------------------------------------
 */

function fillBranch(corporateIds,companyIds){
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/fillBranchAsCompany.html",  
		data: "companyId="+companyIds+"&corporateId="+corporateIds,  
		success : function(response) {
			if (response.status == "Success") {
				refillCombo('#branchName',response.generalMasterModelList);
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


/*
 * ---------------------------------------------------------------------------------------------
 * For Filling Current Hub On DS CLOSING SCREEN
 * ---------------------------------------------------------------------------------------------
 */

function fillCurrentHub(branchId){	
	form_data = new FormData();
	form_data.append("branchId",branchId);
	$.ajax({  
		type: "POST",
		url:  ctx + "/fillHubAsBranch.html",  
		data: form_data, 
		processData: false,
		contentType: false,
		success : function(response) {
			if (response.status == "Success") {
				$('#outlet').find('option').remove().end()
				.append('<option value="0">ALL</option>').val('0');
				$.each(response.generalMasterModelList, function (i, item) {
					$('#outlet').append($('<option>', { 
						value: item.id,
						text : item.name,
					}));					
				});				    
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

/*
 * ---------------------------------------------------------------------------------------------
 * For For Getting DS Tariff Values on DS CLOSING SCREEN
 * ---------------------------------------------------------------------------------------------
 */

function getDsTariffValue(carModelId){
	var carModelFetch = carModelId;
/*	if(fetchModel != null){
		carModelFetch = fetchModel;
	}
*/
	if($("#carTariff").val() == "0" || $("#corporateName").val() == "0" || carModelFetch == "0" || branchId == "0") return 1;
	var exInvDate = $('#dsInvoiceDate').val();
	if(exInvDate == "" || exInvDate == "01/01/1970") exInvDate = getTimeStampTo_DDMMYYYY(new Date());

	form_data = new FormData();
    form_data.append("tariffId",  $("#carTariff").val());
    form_data.append("corporateId",$("#corporateName").val());
    form_data.append("carModelId", carModelFetch);
    form_data.append("branchId", branchId);
    form_data.append("exInvDate", exInvDate);
	tariffDetailMap = null;
	$.ajax({
		type : "POST",
		url:  ctx + '/getTariffDetails.html',
		processData: false,
		contentType: false,
		async:false,
	    data: form_data, 
	    success : function(response) {
		   $('#info').html(response.result);
		   minMinuteSlab = response.dsIdCount;
		   if(response.corporateTariffDetModelList.length >0){
		    	corporateTariffList = response.corporateTariffDetModelList;
			    $.each(corporateTariffList,function(index,value){
			    	if(index == 0) tariffDetailMap = new Object();
			    	tariffDetailMap[value.name] = value.tariffValue;
			    	if($("#dsRentalType option:selected").text() == "Airport Transfer" || $("#dsRentalType option:selected").text() =="Railway Transfer"){
				    	if(value.name == 'Extra Hrs'){
					    	extraChgHrRate = 0;
					    } if(value.name == 'Extra Kms'){
					    	extraChgKmRate = 0;
					    }
				    }else {
				    	if ($("#dsRentalType option:selected").text() == "Outstation"){
				    		if(value.name == 'Outstation Per Hr'){
						    	extraChgHrRate = value.tariffValue;
						    } if(value.name == 'Outstation Per Km'){
						    	extraChgKmRate = value.tariffValue;
						    }if(value.name == "Outstation Min Km"){
						    	minChgKM = value.tariffValue;
						    	$("#dsMinChgKms").val(minChgKM);
						    	dayChgDutySlip();
						    }if(value.name == "Outstation Min Hr"){
						    	minChgHr = value.tariffValue;
						    	$("#dsminChgHrs").val(value.tariffValue);
						    }if(value.name == 'Outstation Night Charges'){
						    	nightCharge = value.tariffValue;
						    }if(value.name == "Day Allowence"){
						    	dayCharge = value.tariffValue;
			    			}
				    	}else{
					    	if(value.name == 'Extra Hrs'){
						    	extraChgHrRate = value.tariffValue;
						    } if(value.name == 'Extra Kms'){
						    	extraChgKmRate = value.tariffValue;
						    } if(value.name == 'Night Charges'){
						    	nightCharge = value.tariffValue;
						    } if(value.name == "Day Allowence")
						    	dayCharge = value.tariffValue;
				    		}
			    	}
			    	if(value.name == $("#carTariff option:selected").text()){
				    	minCharge = parseFloat(value.tariffValue);
				    	finalBasicFare = minCharge;
				    }
				});
			}
       },
		error : function(e) {
			alert('Error: ' + e);
		}
 	});
    fillTraiffValuesAsPerCarTraiff($("#carTariff").val());
     //allocatedCarRegNo();
	 //totalSlipAmountDutySlip();
}

function fillCarModel(){
	var corporateId = $("#corporateName").val();
	var branchId    = $("#branchName").val();
	var tariffId    = $("#carTariff").val();
	var bookDate    = $("#dsDate").val();
	
	if(corporateId == 0 ) alert("Please select corporate");
	if(branchId == 0 ) alert("Please select branch");
	if(tariffId == 0 ) alert("Please select Tariff Scheme");
	if(corporateId == 0 || branchId == 0 || tariffId == 0) return;
	
	formData = new FormData();
	formData.append("corporateId",corporateId);
	formData.append("branchId",branchId);
	formData.append("tariffId",tariffId);
	formData.append("date",bookDate);
	
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/fillCareModelAsCorpBr.html",  
		processData: false,
		contentType: false,
		async:false,
		data : formData,
		success : function(response) {
			if (response.status == "Success") {
				refillCombo('#carModel',response.carModelList);
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

/*
 * ---------------------------------------------------------------------------------------------
 * For Filling Tariff Values as Per Car Tariff On DS CLOSING SCREEN
 * ---------------------------------------------------------------------------------------------
 */
function fillTraiffValuesAsPerCarTraiff(tariffId){
	if(tariffId == "") return 1;
	var sCarTariff = $("#carTariff option:selected").text();
	if(corporateTariffList != null){
	 	$.each(corporateTariffList,function(index,value){
		    if(value.name == sCarTariff){
		    	minCharge = parseFloat(value.tariffValue);
		    	finalBasicFare = minCharge;
		    }
		});
	}
    if(sCarTariff == "Outstation") return 1;
    form_data = new FormData();
	form_data.append("tariffId",tariffId);
	$.ajax({
		type : "POST",
		url:  ctx + '/TraiffValuesAsPerCarTraiff.html',
		processData: false,
		contentType: false,
		async:false,
	    data: form_data, 
	    success : function(response) {
		   $('#info').html(response.result);
		   if(response.tariffSchemeParaModel !=null){
			   if(response.tariffSchemeParaModel.minKM != "" || response.tariffSchemeParaModel.minKM != null){
				   minChgKM = response.tariffSchemeParaModel.minKM == '0'?'':response.tariffSchemeParaModel.minKM;
				   minChgHr = response.tariffSchemeParaModel.minHR == '0'?'':response.tariffSchemeParaModel.minHR;
				   $("#dsMinChgKms").val(minChgKM);
				   $("#dsminChgHrs").val(minChgHr);
			   }else{
				   $("#dsMinChgKms").val();
				   $("#dsminChgHrs").val('');
			   }
		   }
	   },
		error : function(e) {
			alert('Error: ' + e);
		}
	});
	sType = "A";
 	calculateKmDutySlip();
 	dsTimeInCal();
 	sType = "N";
 	totalSlipAmountDutySlip();
//	fillCarModel();
}

/*
 * ---------------------------------------------------------------------------------------------
 * Fill values As Per Corporate On  DS CLOSING SCREEN
 * ---------------------------------------------------------------------------------------------
 */

function fillValues(corporateIds){
	if(corporateIds <= 0){
		$('#bookedBy').val("0");
		$('#clientName').val("0");
		$('#mobileNo').val("");
		$('#officeAddress').val("0");
		return;
	}
	$.ajax({  
		type: "POST",
		async: false,	
		url:  ctx + "/fillAsPerCorporate.html",  
		data: "corporateId="+corporateIds,  
		success : function(response) {
			if (response.status == "Success") {
				refillCombo('#bookedBy',response.generalMasterModelList);
				refillCombo('#clientName',response.autorizedUserModelList);
				$.each(response.autorizedUserModelList, function (i, item) {
					clientMobile[i] = item.remark;
				});
				$("#officeAddress")
				.find('option')
				.remove()
				.end()
				.append('<option value="0">--- Select ---</option>')
				.val('0');
				if(response.addressDetail.address1 != ""){
					$("#officeAddress").append($('<option>', { 
						value: "1",
						text : response.addressDetail.address1 + " " + response.addressDetail.address2
					}));
				}
				$('#mobileNo').val("");
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

/*
 * ---------------------------------------------------------------------------------------------
 * Generate Invoice for DS FROM DS CLOSING SCREEN
 * ---------------------------------------------------------------------------------------------
 */

function saveUpdateDutySlipForInvoicing(){
	$("#dataLoaderModal").show();
	$("#dataLoaderFade").show();

	var corpId = $("#corporateName").val();
	
	form_data = new FormData();
	form_data.append("id", idForUpdate);
	form_data.append("manualSlipNo",$("#msNo").val());
	form_data.append("openKms",$("#dsKmOut").val());
	form_data.append("timeFrom",$("#dsTimeOut").val());
	form_data.append("closeKms",$("#dsKmsIn").val());
	form_data.append("timeTo",$("#dsTimeIn").val());
	form_data.append("dateTo",$("#dsToDate").val());
	form_data.append("totalKms",$("#dsTotalKms").val());
	form_data.append("totalHrs",$("#dsTotalHrs").val());
	form_data.append("minChgKms",$("#dsMinChgKms").val());
	form_data.append("minChgHrs",$("#dsminChgHrs").val());
	form_data.append("extraChgKms",extraChgKmRate);
	form_data.append("extraChgHrs",extraChgHrRate);
	form_data.append("nightAllow",$("#dsnightAllow").val());
	form_data.append("dayAllow",$("#dsdayAllow").val());
	form_data.append("tollTaxAmount",$("#dstollCharge").val());
	form_data.append("parkingAmount",$("#dsparkingCharge").val());
	form_data.append("stateTax",$("#dsstateTax").val());
	form_data.append("remarks",$("#dsremarks").val());
	form_data.append("totalFare",$("#dsslipAmount").val());
	form_data.append("miscCharge",$("#dsmiscCharge").val());
	form_data.append("dateFrom",$("#dsFromDate").val());
	form_data.append("carDetailModel.id",carDetailId);
	form_data.append("tariff.id",$("#carTariff").val());
	form_data.append("bookingDetailModel.id",bookingDetailIds);
	form_data.append("fuelCharge",$("#fuelCharge").val());
	form_data.append("guideCharge",$("#guideCharge").val());
	form_data.append("totalNight",$("#dsNightStay").val());
	form_data.append("totalDay",$("#dsDayStay").val());

	form_data.append("dispatchDateTime",$("#dsFromDate").val());
	form_data.append("dutySlipDate",$("#dsDate").val());
	form_data.append("dsInvoiceDate",$('#dsInvoiceDate').val());
	var taxName = [], actualIdString ;
	$(".taxName").each(function(){
		actualIdString=this.id;
		taxName.push(actualIdString);
	});
	
	var taxPer = [] ;
	$(".taxPer").each(function(){
		var id = this.id;
		taxPer.push($("#"+id).val() == "" ? "0": $("#"+id).val());
	});
	
	var taxAmt = [] ;
	$(".taxAmt").each(function(){
		var id = this.id;
		taxAmt.push($("#"+id).val() == "" ? "0": $("#"+id).val());
	});
	form_data.append("taxName",taxName);
	form_data.append("taxPercentage",taxPer);
	form_data.append("taxValues",taxAmt);
	
	form_data.append("billingStatus",$("#billingStatus").val());
	form_data.append("basicFare",minCharge);
    form_data.append("nightAllowanceRate",nightCharge);
    form_data.append("outStationAllowRate",dayCharge);
	form_data.append("jobType", "DutySlipUnbilled");
	form_data.append("type","DSClosing");
	$.ajax({
		type : "POST",
		processData: false,
		contentType: false,
		async:false,
		url:  ctx + "/saveUpdateDSForInvoicing.html",
		data : form_data,

		success : function(response) {
			if (response.status == "Success") {
				$('#editDetailInfo').html(response.result);
				invoiceNo = response.dataString1;
				alert("Invoice Number :  "+invoiceNo);
				type = "P";
				$("#dataLoaderModal").hide();
				$("#dataLoaderFade").hide();
				$('#editDetailError').hide();
				$('#editDetailInfo').show();
				$("#dataLoaderModal").hide();
				$("#dataLoaderFade").hide();		  
				setTimeout( "$('#editDetailInfo').hide();", 1000);
				setTimeout(function(){$("#closingDSPanel").modal('hide');},1000);
				refreshData();
				resetDetail();
			} else {
				$("#dataLoaderModal").hide();
				$("#dataLoaderFade").hide();
				$('#editDetailError').html(response.result);
				$('#editDetailInfo').hide();
				$('#editDetailError').show();				    
			}
		},
		error : function(e) {
			$("#dataLoaderModal").hide();
			$("#dataLoaderFade").hide();
			alert('Error: ' + e);
		}
	});

	checkMultipleInvoice(invoiceNo,corpId,type);

}	 

/*
 * ---------------------------------------------------------------------------------------------
 * For calculating extra Km on DS CLOSING SCREEN
 * ---------------------------------------------------------------------------------------------
 */

function calculateExtraChangekm(){
	var totalkm =parseInt($("#dsTotalKms").val());
	var minChangeKms=parseInt($("#dsMinChgKms").val());
	
	var extraChangekms = totalkm-minChangeKms;
if(extraChangekms>0)
	{
	$("#dsextraChgKm").val(extraChangekms)
	}
}

var invoiceIds;
function callConfirmation(){
	var isSameCorporate = true;
	var corpId = 0;
	var dBilledAmt = 0.00;
	var i = 0;
	invoiceIds = "";
	if($('.edited').length<=0){
		alert("Please Select At Least One INVOICE !!!");
		return false;
	}
	$(".edited").each(function(){
		var invoiceId = $(this).attr('id');
		invoiceId = invoiceId.substring(9,invoiceId.length);
		var corporateId = $('#corporate_'+invoiceId).val();
		if($("#checkbox_"+invoiceId).prop('checked') == true){
			dBilledAmt += parseFloat($('#totalamount_'+invoiceId).val());
			if(i==0){
				corpId = $('#corporate_'+invoiceId).val();
			}
			if (corporateId == corpId){
				invoiceIds = invoiceIds + invoiceId + ",";
			}else{
				alert("Please Select Identical Corporate For Generating Cover Letter");
				isSameCorporate = false;
				return false;
			}
		}
		i++;
	});
	$("#totalBillAmt").text(dBilledAmt);
	$("#corpName").text($("#corporateId option:selected").text());
	if(!isSameCorporate)
		return false;
	$("#submitButton").attr("disabled", true);
	$('#coverLetterInfoPanel').modal('show');
}

function generateCoverLetter(){
	if($("#letterDate").val() == ""){
		alert("Please enter cover letter date !!!")
		return false;
	}
	
	$('#coverLetterInfoPanel').modal('hide');
	form_data = new FormData();
	form_data.append("invoiceIds",invoiceIds);
	form_data.append("letterDate",$("#letterDate").val());
	form_data.append("invFromDate",$("#invoiceFromDate").val());
	form_data.append("invToDate",$("#invoiceToDate").val());
	form_data.append("mailingMode","By Post");
	form_data.append("remarks",$("#coverRemarks").val());
	form_data.append("totalLetterAmount",$("#totalBillAmt").text());
	$.ajax({  
		type: "POST",
		async: false,
		url:  ctx + "/saveCoverLetter.html",  
		data: form_data,  
		async: false,
		processData: false,
		contentType: false,
		success : function(response) {
			if (response.status == "Success") {
				$('#coverLetter').html(response.dataGrid);
				$('#viewCoverLetter').modal('show');
			}
		},  
		error: function(e){  
		}  
	}); 
}

var corpId = null;
var sInvBranchId = null;
var dUnBilledInvAmount = 0.00;
function invoiceDate(){
	var dsDate;
	var bDetailId = null;
	var isSameCorporate = true;
	
	form_data = new FormData();
	var i = 0;
	if($('.edited').length<=0){
		alert("Please select at least one duty slip for invoice generation !!!");
		return false;
	}
	dUnBilledInvAmount = 0.00;
	$(".edited").each(function(){
		var dutySlipId = $(this).attr('id');
		dutySlipId = dutySlipId.substring(9,dutySlipId.length);
		if($("#checkbox_"+dutySlipId).prop('checked') == true){
			var corporateId = $('#corporateId_'+dutySlipId).val();
			if(daysBetween($("#dutySlipDate_" + dutySlipId).text(),$("#dateInvoice").val())< 0){
				alert("One or more duty slip(s) has DS date later on Invoice date, Please correct !!!");
				return false;
			}
			dUnBilledInvAmount = dUnBilledInvAmount + parseFloat($('#unbilledAmt_' + dutySlipId).val());
			if(i==0){
				$.each(branchListLocal, function (i, item) {
					if(item.name == $('#Branch_'+dutySlipId).text()){
						sInvBranchId = item.id;
					}
				});				    
				corpId = $('#corporateId_'+dutySlipId).val();
			}
			if (corporateId == corpId){
				form_data.append("id",dutySlipId);
				form_data.append("jobType", jobType);
				$('#corpNameDS').val($("#corporate_"+dutySlipId).text());
				$('#usedByDS').val($("#bookedFor_"+dutySlipId).text());
			}
			else{
				alert("Please Select Same Corporate For Generating Multiple Invoice");
				isSameCorporate = false;
				return false;
			}
			dsDate = $("#dutySlipDate_" + dutySlipId).text();
			i++;
		}
	});
	if(!isSameCorporate)
		return false;
	$("#submitButton").attr("disabled", true);
	$('#invoiceDS').val("Invoice No. : ");
	$('#dateInvoice').change();
	if(isNewCoverLetter && lCorporateId > 0 && lCorporateId != $("#corporateName").val() ){
		$("#coverLetterId").val(lCoverLetterId);
		$("#coverLetterDiv").show();
	}
	$('#invoiceDatePanel').modal('show');
}

function saveinvoiceDate(){
	var invoiceDate = $("#dateInvoice").val();
	var currentDt   = getTimeStampTo_DDMMYYYY(new Date());
	if(daysBetween(invoiceDate,currentDt) < 0 ){
		alert("Invoice Date Should be Less Than or Equal To  Current Date !!");
		return false;
	}
	$('#invoiceDatePanel').modal('hide');
	 genInvoice();	
}

function dutySlipInvoice(value){
	saveInvoiceFlag = value;
	var sBranchId = $("#branchName").val();
	if(value == 'Invoice'){
		var dsDate = $("#dsDate").val();
		$('#dsInvoice').val($('#dsinvoiceNo').val() == "" ? "Invoice No. : ":"Invoice No. :  "+$('#dsinvoiceNo').val());
		$('#corpName').val($('#corporateName option:selected').text() == "---Select---" ? "" : $('#corporateName option:selected').text());
		$('#usedBy').val($('#opClient').prop("checked")  != true ? $('#otherClient').val() : ($('#dsUsedBy option:selected').text() == "---Select---" || $('#dsUsedBy option:selected').text() == "--- Select ---" ? "": $('#dsUsedBy option:selected').text()));
		if(isNewCoverLetter && lCorporateId > 0 && lCorporateId != $("#corporateName").val() ){
			$("#coverLetterId").val(lCoverLetterId);
			$("#coverLetterDiv").show();
		}
		$('#invoiceDatePanel').modal('show');
	}else if($('#dsinvoiceNo').val() != null && $('#dsinvoiceNo').val() != ""){
		if(resDS.taxName == null || resDS.taxName == ""){
			var dsDate = $("#dsDate").val();
			getAllTaxDetails($('#corporateName').val(),dsDate,sBranchId,"taxDet");
		}else{
			var sTaxName = (resDS.taxName).split(",");
			var sTaxPer = (resDS.taxPercentage).split(",");
			var sTaxVal = (resDS.taxValues).split(",");
			$("#taxDet").empty();
			for(var i =0; i<sTaxName.length; i++ ){
				$("#taxDet").append('<div class="row" style="width:100%; margin-top: 2px; margin-left: 1px; ">'
						+'<div class="col-md-1" tabindex="-1"></div>'
						+'<div class="col-md-6" ><input type="text" id="'+sTaxName[i]+'" class="form-control taxName" value="'+sTaxName[i]+'" tabindex="-1" readOnly="true"></input></div>'
						+'<div class="col-md-2" ><input type="text" id="taxPer'+i+'" class="form-control taxPer" value="'+sTaxPer[i]+'" ></input></div>'
						+'<div class="col-md-2"><input type="text" id="taxAmt'+i+'" class="form-control taxAmt" value="'+sTaxVal[i]+'"></input></div>'
						+'<div class="col-md-1" tabindex="-1"></div></div>')
			}
		}			
		$('#dsInvoice').val($('#dsinvoiceNo').val() == "" ? "Invoice No. : ":"Invoice No. :  "+$('#dsinvoiceNo').val());
		$('#corpName').val($('#corporateName option:selected').text() == "---Select---" ? "" : $('#corporateName option:selected').text());
		$('#usedBy').val($('#opClient').prop("checked")  != true ? $('#otherClient').val() : ($('#dsUsedBy option:selected').text() == "---Select---" || $('#dsUsedBy option:selected').text() == "--- Select ---" ? "": $('#dsUsedBy option:selected').text()));
		$('#saveCR').css("display","none");
		$('#ok').css("display","block");
		if(isNewCoverLetter && lCorporateId > 0 && lCorporateId != $("#corporateName").val() ){
			$("#coverLetterId").val(lCoverLetterId);
			$("#coverLetterDiv").show();
		}
		$('#invoiceDatePanel').modal('show');
	}else{
		saveDs();
	}
}
function validateDutySlipDetails(){	    	    
	isValidationError = false;
	var dsValidationErrorHtml = '<ol>';
	var kmIn = $('#dsKmsIn').val();
	if(kmIn == null || kmIn == "" || kmIn =="00"){
		dsValidationErrorHtml = dsValidationErrorHtml + "<li> Please Enter Km In.  !!</li>";
	    isValidationError = true;
	}
	var totalHrs = $('#dsTotalHrs').val();
	if(totalHrs == null || totalHrs == "" || totalHrs == "0"){
		dsValidationErrorHtml = dsValidationErrorHtml + "<li> Please Enter Correct To Date &  To Time .</li>";
	    isValidationError = true;
	}		
	var invoiceDate = $("#dsInvoiceDate").val();
	if(invoiceDate != null && invoiceDate != ""){
		var dsDate = $("#dsDate").val();
		var currentDt   = getTimeStampTo_DDMMYYYY(new Date());
		if(daysBetween(invoiceDate,currentDt) < 0 ){
			dsValidationErrorHtml = dsValidationErrorHtml + "<li>Invoice Date Should be Less Than or Equal To Current Date !!</li>";
			isValidationError = true;
		}else if(daysBetween(dsDate,invoiceDate)< 0){
			dsValidationErrorHtml = dsValidationErrorHtml + "<li>Invoice Date Can't be Less Than To Duty Slip Date !!</li>";
			isValidationError = true;
		}
	}
	dsValidationErrorHtml = dsValidationErrorHtml + "</ol>";
	if(isValidationError){
		$('#editDetailError').html("Please correct following errors:<br>" + dsValidationErrorHtml);
		$('#editDetailInfo').hide();
		$('#editDetailError').show();
	}else{
		$('#editDetailError').html("");
		$('#editDetailError').hide();
	}
}

function allocatedCarRegNo(){	
	var carModelId=	$("#dscarType option:selected").val();
		$('#dsCarRegNo').empty();
		$('#dsCarRegNo').append('<option value="0">---Select---</option>');
		$.ajax({  
			type: "POST",
			url:  ctx + "/getCarNo.html",  
			data: "carModelId="+carModelId, 
			/*processData: false,
			contentType: false,*/
			success : function(response) {
				if (response.status == "Success") {
					$.each(response.carAllocationModels, function (i, item) {
						$('#dsCarRegNo').append($('<option>', { 
							value: item.id,
							text : item.carDetailModelId.registrationNo,
						}));					
					});				    
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

function updatechauffeur(){
		$('#updatedispatchDetailRecordPanel').modal('show');
		/*$("#carNo").val($("#dsCarRegNo option:selected").text());*/
		$("#carNo").val($("#dsCarRegNo").val());
}

function updateChauffeurDetails(){
    var updatedChauffeurName = $("#updateChauffeurName").val();
    var updatedChauffeurMobile = $("#updateMobileNumber").val();
    var chauffeurValidationErrorHtml = '<ol>';
    if(updatedChauffeurName == null || updatedChauffeurName == "")
	 chauffeurValidationErrorHtml = chauffeurValidationErrorHtml+ "<li>Chauffeur Name Can not be Blank !!!</li>";
    if(updatedChauffeurMobile == null || updatedChauffeurMobile == "")
	 chauffeurValidationErrorHtml = chauffeurValidationErrorHtml+ "<li>Chauffeur Mobile Can not be Blank !!!</li>";
    if(updatedChauffeurMobile != null && updatedChauffeurMobile!="" && updatedChauffeurMobile.length != 10)
	 chauffeurValidationErrorHtml = chauffeurValidationErrorHtml+ "<li>Chauffeur Mobile should be of 10 digit !!!</li>";
    chauffeurValidationErrorHtml = chauffeurValidationErrorHtml + "</ol>";
    if(chauffeurValidationErrorHtml != "<ol></ol>"){
	$('#chauffeurDetailError').html("Please correct following errors:<br>" + chauffeurValidationErrorHtml);
	$('#chauffeurDetailInfo').hide();
	$('#chauffeurDetailError').show();
    }else{
	$('#chauffeurDetailError').html("");
	$('#chauffeurDetailError').hide();
	$("#isUpdatedChauffeur").val("Y");
	$("#chauffeur").val(updatedChauffeurName+" - "+updatedChauffeurMobile);
	$('#updatedispatchDetailRecordPanel').modal('hide');
    }	     
}

/*
 * function For Auto Select Car Tariff Duty Slip Close
*/

function autoSelectCarTariff(){
		if(sType != "A"){
			if(parseInt(minKmInHR) >= parseInt(minKmInKM)){
				$("#carTariff").val(traiffHr);
				$("#minChgKM").val(minKmInHR == '0'?'':minKmInHR);
				$("#minChgHr").val(minHrInHR == '0'?'':minHrInHR);
			}else{
				$("#carTariff").val(traiffKm);
				$("#minChgKM").val(minKmInKM == '0'?'':minKmInKM);
				$("#minChgHr").val(minHrInKM == '0'?'':minHrInKM);
			}
				/*for Extra KM Calculation*/
				
				if($("#minChgKM").val() !="")
					var 	fMinChgKM = $("#minChgKM").val();
				else
					fMinChgKM = '0';
				
				extKm = parseInt($("#totalKms").val() == "" ? "0":$("#totalKms").val()) - parseInt(fMinChgKM);
				if(extKm < 0) extKm = 0;
				$("#extraChgKm").val(extKm);
				
				/* For Extra Time Calculation */	
				   var diff = null;
				   var start =($("#fromDate").val()).concat(" ").concat($("#timeOut").val());
				   var end =($("#toDate").val()).concat(" ").concat($("#timeIn").val() == null ?"0:00" : $("#timeIn").val());
				   var ms = calculateHrs(end,start);
				   var d = moment.duration(ms);
				   var minute = parseInt(moment.utc(ms).format("mm")) ;
	
				   var roundedMiuntes = Math.ceil(minute / minMinuteSlab);
				   roundedMiuntes = (roundedMiuntes * minMinuteSlab);
				   var fMinChgHr;
				   if($("#minChgHr").val() !="")
					   fMinChgHr = $("#minChgHr").val();
				   else
					   fMinChgHr = '0';
					
				   if(roundedMiuntes == 60){
					   diff = Math.floor(d.asHours() + 1) + ":00";   
					   extHr = parseInt(Math.floor(d.asHours() + 1)) - parseInt(fMinChgHr); 
					   if(extHr < 0 ) extHr = "0";
				   }else{
					   diff = Math.floor(d.asHours()) + ":" + roundedMiuntes;   
					   extHr = parseInt(Math.floor(d.asHours())) - parseInt(fMinChgHr); 
					   if(extHr < 0 ) 
						   extHr = 0;
					   else
						   extHr = extHr + ":" + roundedMiuntes;
			 	   }
	
				   $("#totalHrs").val(diff);
				   $("#extraChgHr").val(extHr);
				   setTimeout( "$('#editDetailError').hide();", 10000);
				
				if(corporateTariffList != null){
				 	$.each(corporateTariffList,function(index,val){
					    if(val.name == $("#carTariff option:selected").text()){
					    	minCharge = parseFloat(val.tariffValue);
					    	finalBasicFare = minCharge;
					    }
					});
				}
				countdays();
		}
}

function checkHrForCarTariff(){
	var tariffSchemePara = [[]];
	if(allTariffSchemeParameter !=null){
		var listSize = allTariffSchemeParameter.length;
		tariffSchemePara = $.makeArray(allTariffSchemeParameter);
		 var j = 0; 
		 for(var i = 0;i<tariffSchemePara.length;i++){
			j = i+1;
			if(i != (parseInt(tariffSchemePara.length)-1)){
				 var hr = (parseInt(tariffSchemePara[i].minHR) + (parseInt(tariffSchemePara[j].minHR) - parseInt(tariffSchemePara[i].minHR))/2);
					if(parseFloat($("#totalHrs").val()) <= hr){
						traiffHr =	tariffSchemePara[i].parentId;
						minKmInHR = tariffSchemePara[i].minKM;
						minHrInHR = tariffSchemePara[i].minHR;
						break;
					}
			}else{
						traiffHr = tariffSchemePara[i].parentId;
						minKmInHR = tariffSchemePara[i].minKM;
						minHrInHR = tariffSchemePara[i].minHR;
					}
			 }
		   if(corporateTariffList != null){
			 	$.each(corporateTariffList,function(index,val){
				    if(val.id == traiffHr){
				    	$("#confirmBox_body").html("Your Package is going to Upgrade on  :-  <b>"+val.name+"</b");
				    }
				});
			}
		   if(sType != "A"){
			   if(traiffHr != $("#carTariff").val()){
				   $("#confirmBox").show();        // show the dialog
			   }
		   }
	 }
}

function checkKmForCarTariff(){
	var tariffSchemePara = [[]];
	if(allTariffSchemeParameter !=null){
		var listSize = allTariffSchemeParameter.length;
		 tariffSchemePara = $.makeArray(allTariffSchemeParameter);
		 var j = 0; 
		 for(var i = 0;i<tariffSchemePara.length;i++){
			j = i+1;
		if(i != (parseInt(tariffSchemePara.length)-1)){
			 var km = (parseInt(tariffSchemePara[i].minKM) + (parseInt(tariffSchemePara[j].minKM) - parseInt(tariffSchemePara[i].minKM))/2);
					if(parseInt($("#totalKms").val()) <=  km){
						traiffKm =	tariffSchemePara[i].parentId;
						minKmInKM = tariffSchemePara[i].minKM;
						minHrInKM = tariffSchemePara[i].minHR;
						break;
					}
				}else{
						traiffKm = tariffSchemePara[i].parentId;
						minKmInKM = tariffSchemePara[i].minKM;
						minHrInKM = tariffSchemePara[i].minHR;
					}
			 }
		   if(corporateTariffList != null){
			 	$.each(corporateTariffList,function(index,value){
				    if(value.id == traiffKm){
				    	$("#confirmBox_body").html("Your Package is going to Upgrade on  :-  <b>"+value.name+"</b");
				    	
				    }
				});
			}
		   if(sType !="A"){
			   if(traiffKm != $("#carTariff").val()){
				   $("#confirmBox").show();        // show the dialog
			   }
		   }
	 }
}

function confirm(){
    $("#confirmBox").hide();     // dismiss the dialog
    autoSelectCarTariff();
}
function closePopUp(){
	if(jobType == "CoverLetter"){
		resetDetail();
	    $("#viewCoverLetter").hide();     // dismiss the dialog
	}else{
	    $("#confirmBox").hide();     // dismiss the dialog
	}
}

/*
 * function For Auto Select Car Tariff for duty slip closing
*/

function autoSelectCarTariffDs(){
		if(sType != "A"){
			if(parseInt(minKmInHR) >= parseInt(minKmInKM)){
				$("#carTariff").val(traiffHr);
				$("#dsMinChgKms").val(minKmInHR == '0'?'':minKmInHR);
				$("#dsminChgHrs").val(minHrInHR == '0'?'':minHrInHR);
			}else{
				$("#carTariff").val(traiffKm);
				$("#dsMinChgKms").val(minKmInKM == '0'?'':minKmInKM);
				$("#dsminChgHrs").val(minHrInKM == '0'?'':minHrInKM);
			}
				/*for Extra KM Calculation*/
				
				if($("#dsMinChgKms").val() !="")
					var 	fMinChgKM = $("#dsMinChgKms").val();
				else
					fMinChgKM = '0';
				
				extKm = parseInt($("#dsTotalKms").val() == "" ? "0":$("#dsTotalKms").val()) - parseInt(fMinChgKM);
				if(extKm < 0) extKm = 0;
				$("#dsextraChgKm").val(extKm);
				
				/* For Extra Time Calculation */	
				   var diff = null;
				   var start =($("#dsFromDate").val()).concat(" ").concat($("#dsTimeOut").val());
				   var end =($("#dsToDate").val()).concat(" ").concat($("#dsTimeIn").val() == null ?"0:00" : $("#dsTimeIn").val());
				   var ms = calculateHrs(end,start);
				   var d = moment.duration(ms);
				   var minute = parseInt(moment.utc(ms).format("mm")) ;
	
				   var roundedMiuntes = Math.ceil(minute / minMinuteSlab);
				   roundedMiuntes = (roundedMiuntes * minMinuteSlab);
				   var fMinChgHr;
				   if($("#dsminChgHrs").val() !="")
					   fMinChgHr = $("#dsminChgHrs").val();
				   else
					   fMinChgHr = '0';
					
				   if(roundedMiuntes == 60){
					   diff = Math.floor(d.asHours() + 1) + ":00";   
					   extHr = parseInt(Math.floor(d.asHours() + 1)) - parseInt(fMinChgHr); 
					   if(extHr < 0 ) extHr = "0";
				   }else{
					   diff = Math.floor(d.asHours()) + ":" + roundedMiuntes;   
					   extHr = parseInt(Math.floor(d.asHours())) - parseInt(fMinChgHr); 
					   if(extHr < 0 ) 
						   extHr = 0;
					   else
						   extHr = extHr + ":" + roundedMiuntes;
			 	   }
	
				   $("#dsTotalHrs").val(diff);
				   $("#dsextraChgHr").val(extHr);
				   setTimeout( "$('#editDetailError').hide();", 10000);
				
				if(corporateTariffList != null){
				 	$.each(corporateTariffList,function(index,val){
					    if(val.name == $("#carTariff option:selected").text()){
					    	minCharge = parseFloat(val.tariffValue);
					    	finalBasicFare = minCharge;
					    }
					});
				}
				countdaysDutySlip();
		}
}

function checkHrForCarTariffDs(){
	var tariffSchemePara = [[]];
	if(allTariffSchemeParameter !=null){
		var listSize = allTariffSchemeParameter.length;
		tariffSchemePara = $.makeArray(allTariffSchemeParameter);
		 var j = 0; 
		 for(var i = 0;i<tariffSchemePara.length;i++){
			j = i+1;
			if(i != (parseInt(tariffSchemePara.length)-1)){
				 var hr = (parseInt(tariffSchemePara[i].minHR) + (parseInt(tariffSchemePara[j].minHR) - parseInt(tariffSchemePara[i].minHR))/2);
				 if(parseFloat($("#dsTotalHrs").val()) <= hr){
						traiffHr =	tariffSchemePara[i].parentId;
						minKmInHR = tariffSchemePara[i].minKM;
						minHrInHR = tariffSchemePara[i].minHR;
						break;
						}
				}else{
						traiffHr = tariffSchemePara[i].parentId;
						minKmInHR = tariffSchemePara[i].minKM;
						minHrInHR = tariffSchemePara[i].minHR;
					}
			 }
		   if(corporateTariffList != null){
			 	$.each(corporateTariffList,function(index,val){
				    if(val.id == traiffHr){
				    	$("#confirmBox_body").html("Your Package is going to Upgrade on  :-  <b>"+val.name+"</b");
				    }
				});
			}
		   if(sType !="A"){
			   if(traiffHr != $("#carTariff").val()){
				   $("#confirmBox").show();        // show the dialog
			   }
		  }
	 }
}

function checkKmForCarTariffDs(){
	var tariffSchemePara = [[]];
	if(allTariffSchemeParameter !=null){
		var listSize = allTariffSchemeParameter.length;
		 tariffSchemePara = $.makeArray(allTariffSchemeParameter);
		 var j = 0; 
		 for(var i = 0;i<tariffSchemePara.length;i++){
			j = i+1;
		    if(i != (parseInt(tariffSchemePara.length) - 1)){
			    var km = (parseInt(tariffSchemePara[i].minKM) + (parseInt(tariffSchemePara[j].minKM) - parseInt(tariffSchemePara[i].minKM))/2);
				if(parseInt($("#dsTotalKms").val()) <=  km){
					traiffKm =	tariffSchemePara[i].parentId;
					minKmInKM = tariffSchemePara[i].minKM;
					minHrInKM = tariffSchemePara[i].minHR;
					break;
				}
			}else{
					traiffKm = tariffSchemePara[i].parentId;
					minKmInKM = tariffSchemePara[i].minKM;
					minHrInKM = tariffSchemePara[i].minHR;
				}
		 }
		   
		   if(corporateTariffList != null){
			 	$.each(corporateTariffList,function(index,value){
				    if(value.id == traiffKm){
				    	$("#confirmBox_body").html("Your Package is going to Upgrade on  :-  <b>"+value.name+"</b");
				    	
				    }
				});
			}
		   if(sType !="A"){
			   if(traiffKm != $("#carTariff").val()){
				   $("#confirmBox").show();        // show the dialog
			   }
		   }
	 }
}

function confirmDs(){
    $("#confirmBox").hide();     // dismiss the dialog
    autoSelectCarTariffDs();
}


function closePopUpDs(){
    $("#confirmBox").hide();     // dismiss the dialog
}

function fetchTariffDet(hide){
	getDsTariffValue($("#carModel").val());
	if(tariffDetailMap == null && hide != "H"){
		alert("Car Model is not allowed for selected tariff !!!");
		$("#carModel").val("0");
		return;
	}else{
		$("#tariffDetTab").empty();
		for (var key in tariffDetailMap) {
			$("#tariffDetTab").append('<div class="row" style="width:100%; margin-top: 2px;">'
					+'<div class="col-md-1" tabindex="-1"></div>'
					+'<div class="col-md-8" ><input type="text" class="form-control" value="'+key+'" tabindex="-1" readOnly="true"></input></div>'
					+'<div class="col-md-2" ><input type="text" style="text-align:right" class="form-control" value="'+tariffDetailMap[key]+'" readOnly="true" ></input></div>'
			+'	</div>');
		}
		if(hide != "H") $("#tariffDetPanel").show();
	}
}

function fillCarRelDetails(selectedRegNo){
	var obj=$("#carRegistrationList").find("option[value='"+selectedRegNo+"']")
	if(obj ==null && obj.length <= 0)
		return;
    form_data = new FormData();
    form_data.append("selectedRegNo", selectedRegNo);
    $.ajax({
	type : "POST",
	async: false,
	url:  ctx + '/fillCarRelDetails.html',
	processData: false,
	contentType: false,
	data : form_data,
	success : function(response) {
		if (response.status == "Success") {
			if(response.carAllocationModel.carDetailModelId != null){
	    		$("#dscarType").val(response.carAllocationModel.carDetailModelId.model.name);
		    	cModelMap[response.carAllocationModel.carDetailModelId.model.name] = response.carAllocationModel.carDetailModelId.id;
		    	cCarDetailMap[response.carAllocationModel.carDetailModelId.model.name] = response.carAllocationModel.carDetailModelId.model.id;

		    	carDetailId = response.carAllocationModel.carDetailModelId.id;
		    	
		    	if(response.carAllocationModel.carDetailModelId.ownType != 'A'){
		    		$("#chauffeur").val(response.carAllocationModel.chauffeurId.name +' - ' +response.carAllocationModel.chauffeurId.mobileNo);
			    	vModelMap[response.carAllocationModel.vendorId.contPerson +' - ' +response.carAllocationModel.vendorId.contPersonMobile] = response.carAllocationModel.vendorId.id;
			    	chModelMap[response.carAllocationModel.chauffeurId.name +' - ' +response.carAllocationModel.chauffeurId.mobileNo] = response.carAllocationModel.chauffeurId.id;
			    	vendorId = response.carAllocationModel.vendorId.id;
		    	}
				if($('#carTariff').val() != null ){
					carModelId = $("#carModel").val();
					getDsTariffValue(carModelId);
				}
	    	}
			if(response.carAllocationModel == null || response.carAllocationModel.carOwnerType == null){
				for(var iCount = 0; iCount < $("input[name='checkBox1']").length; iCount++){
					if(($('input[name="checkBox1"]:eq('+iCount+')').val()).indexOf("Adhoc") > -1){
						$('input[name="checkBox1"]:eq('+iCount+')').prop('checked',true);
					}
				}
			}else{
				$('#'+response.carAllocationModel.carOwnerType.id).prop('checked',true);
			}
		} else {
			$('#error').html(response.result);
			$('#info').hide('slow');
			$('#error').show('slow');
		}
	},
	error : function(e) {
		alert('Error: ' + e);
	}
     });
 }

function carno()
{
    carNO = $("#dsCarRegNo").val();
}

$(function() {
	$('.opt').click(function(){
		if(($(this).attr('id') == 'opClient')){
			$("#dsUsedBy").show();
			$("#otherClient").css('display','none');
		}
		else{
			$("#otherClient").css('display','block');
			$("#dsUsedBy").hide();
		}
	});
});

function resetDetail(){
	document.location.reload();
}

function checkDSNo(){
	isDSNoError = false;
	var dSRetreive=	$("#dSRetreive").val();
	$.ajax({
		type : "POST",
		async:false,
		url:  ctx + '/checkDSNo.html',
		data: "dSRetreive="+dSRetreive, 
		success : function(response) {
			if(response.dsIdCount > 0){
				isDSNoError = false;
			}else{
				isDSNoError = true;
				alert("You have Enter Worng Duty Slip Number !!");
			}
		}
	});
}
function fetchTaxDetail(dsDate){
	var sBranchId = $("#branchName").val();
	var corpId = $('#corporateName').val();
	dUnBilledInvAmount = parseFloat($("#dsslipAmount").val());
	getAllTaxDetails(corpId,dsDate,sBranchId,"taxDet");
}

function fetchTaxDetailDS(dsDate){
	var sBranchId = $("#branch").val();
	getAllTaxDetails(cpId,dsDate,sBranchId,"taxDet");
}

function fetchTaxDetailUn(dsDate){
	getAllTaxDetails(corpId,dsDate,sInvBranchId,"dsTaxDet");
}


function getAllTaxDetails(corpId,dsDate,sBranchId,control){
	$.ajax({  
		type: "POST",
		async: false,	
		url:  ctx + "/getAllTaxes.html",  
		data: "corporateId="+corpId+"&insDate="+dsDate+"&branchId="+sBranchId,  
		success : function(response) {
			if (response.status == "Success") {
				if(response.corporateTaxDetModelList != null){
					$("#"+control).empty();
					var i = 1;
					console.log(response.corporateTaxDetModelList);
					$.each(response.corporateTaxDetModelList,function(index,val){
						var dTaxAmt = 0.00;
						if(dUnBilledInvAmount > 0){
							dTaxAmt = Math.round(dUnBilledInvAmount * val.taxVal/100);
						}
						$("#"+control).append('<div class="row" style="width:100%; margin-top: 2px; margin-left: 1px; ">'
											+'<div class="col-md-1" tabindex="-1"></div>'
											+'<div class="col-md-6" ><input type="text" id="'+val.name+'" class="form-control taxName" value="'+val.name+'" tabindex="-1"></input></div>'
											+'<div class="col-md-2" ><input type="text" id="taxPer'+i+'" class="form-control taxPer" value="'+val.taxVal+'" ></input></div>'
											+'<div class="col-md-2"><input type="text" id="taxAmt'+i+'" class="form-control taxAmt" value="'+dTaxAmt+'"></input></div>'
											+'<div class="col-md-1" tabindex="-1"></div></div>')
							i++;
						});
				}
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

function checkboxAllClicked(){
	if($("#checkboxAll").is(':checked')){
		$(".checkbox").prop('checked',true);
		$(".checkbox").addClass("edited");
	}else{
		$(".checkbox").prop('checked',false);
		$(".checkbox").removeClass("edited");
	}
}