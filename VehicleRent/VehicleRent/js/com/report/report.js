var coverRelatedStyles="";
function localScriptWithRefresh(){}
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
function getCoverLetter(coverLetterId,view){
	var invFromDt= $("#bookingFromDate").val();
	var invToDt  = $("#bookingToDate").val();
	form_data = new FormData();
	form_data.append("coverLetterId", coverLetterId);
	$.ajax({
		type : "POST",
		url:  ctx + '/getCoverLetter.html',
		async:false,
		processData: false,
		contentType: false,
		data : form_data,
		success : function(response) {
			if (response.status == "Success") {
				$('#info').html(response.result);
				var dataGrid = response.dataGrid;
				$('#coverLetter').html(dataGrid);
				$("#grandTotalWord").html("(Rupees: "+(number2text($('#grand').html()))+")");
				if(view == "V"){
					$('#print').hide();
				}else{
					$('#print').show();
				}
				$('#viewCoverLetter').show();
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
}
function print(type){
	var divContents;
	$('#viewCoverLetter').modal('hide');
	$('#controlDiv').hide();
	var divContents = $("#coverLetter").html();
	var printWindow = window.open('', '', 'left=0,top=0,width=800,toolbar=0,scrollbars=1,status=0');
	printWindow.document.write(divContents);
	printWindow.document.close();
	printWindow.print();
	printWindow.close()
	$('#controlDiv').show();
}
function searchReport(sPurpose){
	var sType = "";
	if(sPurpose == "Invoices"){
		sType = $("#dateRangeFor").val();
	}
	
	var sCriteria = 'sType,invoiceFromDate,invoiceToDate,corpName,branch,hub,status';
	var sValue =sType + "," +
				$("#bookingFromDate").val() + "," +
				$("#bookingToDate").val() + "," +
				$("#corporateId").val() + "," +
				$("#branch").val()+","+
				$("#outlet").val() + ",Y";
	form_data = new FormData();
	form_data.append("sCriteria", sCriteria);
	form_data.append("sValue", sValue);
	form_data.append("sPurpose", sPurpose);
	$.ajax({
		type : "POST",
		url:  ctx + '/searchReport.html',
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
}
function closePopUp(){
    $("#viewCoverLetter").hide();
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
