	var isAdd  = false;
	var isEdit = false;
	var isView = false;
	var isDelete = false;
	var isFromView = false;

	var fetchExtraData = "";
	var deleteExtraData = "";
	
	var saveUpdateUrl="";
	var formFillUrl = "";
	var deleteUrl = "";
	var serviceResponse;
	var isServerSideError = false;
	
	// For todays date;
	Date.prototype.today = function () { 
	    return ((this.getDate() < 10)?"0":"") + this.getDate() +"/"+(((this.getMonth()+1) < 10)?"0":"") + (this.getMonth()+1) +"/"+ this.getFullYear();
	}
	// For the time now
	Date.prototype.timeNow = function () {
	     return ((this.getHours() < 10)?"0":"") + this.getHours() +":"+ ((this.getMinutes() < 10)?"0":"") + this.getMinutes();
	}

	function timeDiff(startTime, endTime) {
		startTime = startTime.split(":");
		endTime = endTime.split(":");
	    var startDate = new Date(0, 0, 0, startTime[0], startTime[1], 0);
	    var endDate = new Date(0, 0, 0, endTime[0], endTime[1], 0);
	    var diff = endDate.getTime() - startDate.getTime();
	    var hours = Math.floor(diff / 1000 / 60 / 60);
	    diff -= hours * 1000 * 60 * 60;
	    var minutes = Math.floor(diff / 1000 / 60);

	    // If using time pickers with 24 hours format, add the below line get exact hours
	    if (hours < 0)
	       hours = hours + 24;

	    return (hours > 0 && hours <= 9 ? "0" : "") + hours + ":" + (minutes <= 9 ? "0" : "") + minutes;
	}
	
	function assignRights(){
		var userAccess = userPageAccess.split('');
		$.each(userAccess, function (i, item) {
			if(item == "S") isAdd = true;
			if(item == "E") isEdit = true;
			if(item == "V") isView = true;
			if(item == "D") isDelete = true;
		});			
	}

	function isAccessDenied(operationType){
		if(operationType =="S" && isAdd == false){
			alert("Access for SAVE record is not granted");
			return true;
		}else if(operationType == "E"  && isEdit == false){
			alert("Access for EDIT record is not granted");
			return true;
		}else if(operationType == "V"  && isView == false){
			alert("Access for VIEW record is not granted");
			return true;
		}else if(operationType == "D"  && isDelete == false){
			alert("Access for DELETE record is not granted");
			return true;
		}
		return false;
	}
	
	function saveOrUpdateRecord(form_data) {
		var operationType;
		$('#idForUpdate').val() > 0?operationType = "E":operationType = "S";
		if(isAccessDenied(operationType)) return;		
		var actionType = actionType || "";
		document.getElementById("addButton").disabled = true;
		$('#idForUpdate').val('');
		$.ajax({
			type : "POST",
			url :  saveUpdateUrl,
			processData: false,
			contentType: false,
			async:false,
			data : form_data,
			success : function(response) {
				if (response.status == "Success") {
					serviceResponse = response;					
					if($('#userTable').dataTable() != null) $('#userTable').dataTable().fnDestroy();
					$('#dataTable2').html(response.dataGrid);
					isServerSideError = false;
					//resetData(actionType);
					if(response.dataString1 !=null) alert(response.dataString1);
					$('#info').html(response.result);
					$('#error').hide('slow');
					$('#info').show('slow');
				} else if (response.status == "Errors") {
					errorInfo = "";
					for (i = 0; i < response.result.length; i++) {
						errorInfo += "<br>" + (i + 1) + ". "
								+ response.result[i].code;
					}
					$('#error').html("Please correct following errors: " + errorInfo);
					$('#info').hide('slow');
					$('#error').show('slow');
					isServerSideError = true;
				} else {
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
					isServerSideError = true;
				}
				document.getElementById("addButton").disabled=false;
			},
			error : function(e) {
				alert(e);
			}
		});
		
	}

	function viewRecord(formFillForEditId){
		if(isAccessDenied("V")) return;
		isFromView = true;

		formFillRecord(formFillForEditId);
		$("form :input").attr("disabled","disabled");
		isFromView = false;
	}
	
	function formFillRecord(formFillForEditId) {
		if(!isFromView)
			if(isAccessDenied("E")) return;	
		$.ajax({
			type : "POST",
			url : formFillUrl,
			async:false,
			data : "formFillForEditId=" + formFillForEditId + fetchExtraData,
			success : function(response) {
				if (response.status == "Success") {
					$('#info').hide('slow');
					$('#error').hide('slow');
					AssignFormControls(response);
				} 
				else {
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
			},
			error : function(e) {
				console.log(e);
			}
		});
		$("form :input").prop("disabled",false);
		document.getElementById("addButton").disabled = true;
		document.getElementById("updateButton").disabled = false;
		$('#idForUpdate').val(formFillForEditId);
	}
	
	function deleteRecord(idForDelete) {
		if(isAccessDenied("D")) return;	
		if (confirm('Are you sure you want to delete this Record?')) {
			$.ajax({
				type : "POST",
				url : deleteUrl,
				data : "idForDelete=" + idForDelete + deleteExtraData,
				success : function(response) {
					if (response.status == "Success") {
						$('#info').html(response.result);
						$('#dataTable2').html(response.dataGrid);
						resetData();
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
	}

	function refillCombo(controlId,Array){
		$(controlId)
	    .find('option')
	    .remove()
	    .end()
	    .append('<option value="0">--- Select ---</option>')
	    .val('0');
		if(Array != null){
			$.each(Array, function (i, item) {
				$(controlId).append($('<option>', { 
					value: item.id,
					text : item.name
				}));
			});
		}	
	}


	function addClassForColor(){
		$("input[type='text']").addClass('colorClass');
		$("select").addClass('colorClass');
		$("input[type='password']").addClass('colorClass');
		$("textarea").addClass('colorClass');
	}
	
	function trim(str1) {
		var str = new String(str1)
		while (str.indexOf(' ') == 0) {
			str = str.replace(' ', '');
		}
		len = str.length;
		while (str.lastIndexOf(' ') == len - 1 && len > 0) {
			str = str.substring(0, len - 1);
			len = str.length;
		}
		return str;
	}
	
	function alphaNumChat(text) {
		aevent = arguments.callee.caller.arguments[0] || window.event;
		var c = aevent.keyCode;
		aevent.keyCode = (!((c >= 65 && c <= 90) || (c >= 97 && c <= 122)
				|| (c == 32) || (c >= 48 && c <= 57) || (c == 45) || (c == 47)
				|| (c == 44) || (c == 46))) ? 0 : aevent.keyCode;
	}
	
	function alphaNumWithoutSpace() {
		aevent = arguments.callee.caller.arguments[0] || window.event;
		var c = aevent.keyCode;
		aevent.keyCode = (!((c >= 65 && c <= 90) || (c >= 97 && c <= 122) || (c >= 48 && c <= 57))) ? 0
				: aevent.keyCode;
	}
	
	function withoutSpace() {
		aevent = arguments.callee.caller.arguments[0] || window.event;
		var c = aevent.keyCode;
		aevent.keyCode = (c == 32) ? 0 : aevent.keyCode;
	}
	
	// Allow only alphabets
	function isValidAlpha() {
		evt = window.event || arguments.callee.caller.arguments[0];
		var c = evt.keyCode;
		evt.keyCode = (!((c >= 65 && c <= 90) || (c >= 97 && c <= 122) || (c == 32))) ? 0
				: evt.keyCode;
	}
	
	// Allow only Alphanumeric keys
	function isValidNumAlpha() {
		evt = window.event || arguments.callee.caller.arguments[0];
		var c = evt.keyCode;
		evt.keyCode = (!((c >= 65 && c <= 90) || (c >= 97 && c <= 122) || (c == 32) || (c >= 48 && c <= 57))) ? 0
				: evt.keyCode;
	}

	function isDouble(evt,fieldId){ 	// Numbers only
		var fieldValue=fieldId.value;
	    var charCode = (evt.which) ? evt.which : evt.keyCode;
	    if (charCode > 31 && (charCode < 48 || charCode > 57)){
	    	if(charCode==0 || charCode==9||charCode==8||charCode==32 ||charCode==37 ||charCode==38 ||charCode==39 ||charCode==40)
	    		return true;
	    	 var parts =fieldValue.split('.');
	    	 if (parts.length > 1 && charCode == 46){
	    		 evt.preventDefault();
	             return false;
	    	 }
             if (charCode == 46){
	 		   if(fieldValue.indexOf('.')!=-1){         
			       if(fieldValue.split(".")[1].length > 2){                
			           fieldId.value = parseFloat(fieldValue).toFixed(2);
			       }  
			    }            
	    		return true;
	    	}
	    	evt.preventDefault();
	    	return false;
	    }
	    return true;
	}

	function isNumeric(evt){ 	// Numbers only
	    var charCode = (evt.which) ? evt.which : evt.keyCode;
	    if (charCode > 31 && (charCode < 48 || charCode > 57)){
	    	evt.preventDefault();
	        return false;
	    }
	    return true;
	}
	function isAlpha(evt){ // Alpha only
	    var charCode = (evt.which) ? evt.which : evt.keyCode;
	    if((((charCode >= 65 && charCode <= 90) || (charCode >= 97 && charCode <= 122)) || (charCode == 32)) || (charCode==0 || charCode==9||charCode==8||charCode==32||charCode==46 ||charCode==37 ||charCode==38 ||charCode==39 ||charCode==40)){
	    	return true;
	    }
	    else{
	    	evt.preventDefault();
	    	return false;
	    }
	}

	function MaskMoney(evt) {
		if (!(evt.keyCode == 46 || (evt.keyCode >= 48 && evt.keyCode <= 57))) return false;
		var parts = evt.srcElement.value.split('.');
		if (parts.length > 2) return false;
		if (evt.keyCode == 46) return (parts.length == 1);
		if (parts[0].length >= 14) return false;
		if (parts.length == 2 && parts[1].length >= 2) return false;
    }

	function isAlphaNumeric(evt){ // Alphanumeric only
		var charCode = (evt.which) ? evt.which : evt.keyCode;
	    if((charCode>47 && charCode<58) || /*0-9*/
		   (charCode>64 && charCode<91) || /*A-Z*/
		   (charCode>96 && charCode<123)|| /*a-z*/
		   charCode==0 || charCode==9 || charCode==8 || /*null, tab, backspace*/
		   charCode==32||charCode==46 ||charCode==37 || /*space,dot,%*/
		   charCode==38 ||charCode==39 ||charCode==40|| charCode==41|| charCode==44){ /**/
    	   return true;
	    }
	    else{
	    	evt.preventDefault();
	    	return false;
	    }
	}

	function getTimeStampTo_DDMMYYYY(timeStampString){
		var dateValue = new Date(timeStampString);
		var month=(dateValue.getMonth()+1);
		if(month.toString().length==1)
			month='0'+month;
		
		var day=(dateValue.getDate());
		if(day.toString().length==1)
			day='0'+day;
			
		var formattedDate = day+ "/" + month + "/" + dateValue.getFullYear();
		return formattedDate;
	}
	
	function getTimeStampTo_ddMMyyyyHHmmss(timeStampString,format){
		var format = format || "dd/mm/yyyy hh:mm:ss";
	    	var dateValue = new Date(timeStampString);
		var month=(dateValue.getMonth()+1);
		if(month.toString().length==1)
			month='0'+month;
		
		var day=(dateValue.getDate());
		if(day.toString().length==1)
			day='0'+day;
		var formattedDate = "";
		if(format == "dd/mm/yyyy hh:mm:ss")	
		    formattedDate = day+ "/" + month + "/" + dateValue.getFullYear()+" "+dateValue.getHours()+":"+dateValue.getMinutes()+":"+dateValue.getSeconds();
		else
		    formattedDate = day+ "/" + month + "/" + dateValue.getFullYear()+" "+dateValue.getHours()+":"+dateValue.getMinutes();
		return formattedDate;
	}
	
	function getSqlDateTo_DDMMYYYY(sqlString){
		if(sqlString.length>8){
			sqlString=sqlString.replace("-","").replace("-","");
			var subString=sqlString.substring(6,8)+"/"+sqlString.substring(4,6)+"/"+sqlString.substring(0,4);
		}
		else {
			subString="";
		}
		return subString;
	}

	function getSqlDateTo_YYYYMMDD(sqlString){
		if(sqlString.length>8){
			sqlString=sqlString.replace("/","").replace("/","");
			var subString=sqlString.substring(8,4)+"/"+sqlString.substring(4,2)+"/"+sqlString.substring(2,0);
		}
		else {
			subString="";
		}
		return subString;
	}
	
	$(function(){
		$(document).ajaxStart(function(){
			$("#wait").show();
		});
	});

	$(function(){
		$(document).ajaxComplete(function(){
			$("#wait").hide();
		  });
	}); 
	
/*	$.ajaxPrefilter(function( options ) {
	    if ( !options.beforeSend) {
	        options.beforeSend = function (xhr) { 
	            xhr.setRequestHeader('ORIGINAPP', 'OWNAPP');
	        }
	    }
	});
*/	
	$(document).on('hide.bs.modal', function () {
		$("#wait").hide();
	});

	function altRows(id){
		var table = document.getElementById(id);  
		var rows = table.getElementsByTagName("tr"); 
		 
		for(i = 0; i < rows.length; i++){          
			if(i % 2 == 0){
				rows[i].className = "evenrowcolor";
			}else{
				rows[i].className = "oddrowcolor";
			}      
		}
	}
	
	function treatAsUTC(date) {
		var sDate = date.split('/');
	    var result = new Date(sDate[2],sDate[1] - 1, sDate[0]);
	    result.setMinutes(result.getMinutes() - result.getTimezoneOffset());
	    return result;
	}

	function daysBetween(startDate, endDate) {
	    var millisecondsPerDay = 24 * 60 * 60 * 1000;
	    return (treatAsUTC(endDate) - treatAsUTC(startDate)) / millisecondsPerDay;
	}

	function addDays(theDate, days) {
		if(days == 0)
			return theDate;
		var sDate = theDate.split('/');
	    var result = new Date(sDate[2],sDate[1] - 1, sDate[0]);
	    var newDate = new Date(result.getTime() + days*24*60*60*1000);
	    var strNewDate = (newDate.getDate()<10?"0":"") + newDate.getDate() + "/" + (newDate.getMonth()+1<10?"0":"") + (newDate.getMonth()+1)+"/"+newDate.getFullYear();
	    return strNewDate;
	}

	function firstDayOfMonth(theDate) {
		var dateValue = new Date(theDate);
		var month=(dateValue.getMonth()+1);
		if(month.toString().length==1)
			month='0'+month;
		
		var day=(dateValue.getDate());
		if(day.toString().length==1)
			day='0'+day;
			
		var formattedDate = "01/" + month + "/" + dateValue.getFullYear();
		return formattedDate;
	}

	function lastDayOfMonth(theDate) {
		var sDate = theDate.split('/');
		var y = sDate[2], m = sDate[1];
		var lastDay = new Date(y, m + 1, 0);
		return (lastDay.getDate()<10?"0":"") + lastDay.getDate() + "/" + (lastDay.getMonth()+1<10?"0":"") + (lastDay.getMonth()+1)+"/"+lastDay.getFullYear();
	}
	function getCurrentDate(){
		var currentdate = new Date(); 
		return (currentdate.getDate() < 10?"0":"") + currentdate.getDate() + "/" 
			 + (currentdate.getMonth() + 1 < 10?"0":"") + (currentdate.getMonth() + 1) + "/"
			 +  currentdate.getFullYear();
	}
	function getCurrentDateTime(){
		var currentdate = new Date(); 
		return (currentdate.getDate() < 10?"0":"") + currentdate.getDate() + "/" 
			 + (currentdate.getMonth() + 1 < 10?"0":"") + (currentdate.getMonth() + 1) + "/"
			 +  currentdate.getFullYear() + " "  
		     + (currentdate.getHours() < 10?"0":"") + currentdate.getHours() + ":"  
		     + (currentdate.getMinutes() < 10?"0":"") + currentdate.getMinutes();
	}	
	
	function showPhoto(files){
		encodeImageFileAsURL("photoFile","photoImage");
	}

    function encodeImageFileAsURL(inputFileToLoad,imgControl){
		var filesSelected = document.getElementById(inputFileToLoad).files;
		if (filesSelected.length > 0)
		{
		    var reader = new FileReader();
		    reader.readAsArrayBuffer(filesSelected[0]);
		    reader.onload = function (event) {
		      // blob stuff
		      var blob = new Blob([event.target.result]); // create blob...
		      var windowURL = window.webkitURL || window.URL;
		      var blobURL = windowURL.createObjectURL(blob); // and get it's URL
		      // helper Image object
		      var image = new Image();
		      image.src = blobURL;
		      image.onload = function() {
		    	  resize_image(image, document.getElementById(imgControl)); // send it to canvas
		      }
		    };	
		}
	}

    function resize_image( src, dst, type, quality ) {
	   var tmp = new Image(), canvas, context, cW, cH;
	 
	   type = type || 'image/jpeg';
	   quality = quality || 0.92;
	 
	   cW = src.naturalWidth;
	   cH = src.naturalHeight;
	 
	   tmp.src = src.src;
	   tmp.onload = function() {
		   canvas = document.createElement( 'canvas' );
		   var iFactorW = Math.floor(cW/500) + 1;
		   var iFactorH = Math.floor(cH/500) + 1;
		   var iFactor = iFactorW;

		   if(iFactorW > iFactorH) 
			   iFactor = iFactorH;
		   else if(iFactorH > iFactorW) 
			   iFactor = iFactorW;
			   
		   cW /= iFactor;
		   cH /= iFactor;

		   canvas.width = cW;
	       canvas.height = cH;
	       context = canvas.getContext( '2d' );
	       context.drawImage( tmp, 0, 0, cW, cH );

	       dst.src = canvas.toDataURL( type, quality );
	       if ( cW <= src.width || cH <= src.height )
	         return;
		   tmp.src = dst.src;
	   }
	   
	}    

    Array.prototype.contains = function(v) {
        for(var i = 0; i < this.length; i++) {
            if(this[i] === v) return true;
        }
        return false;
    };

    Array.prototype.unique = function() {
        var arr = [];
        for(var i = 0; i < this.length; i++) {
            if(!arr.contains(this[i])) {
                arr.push(this[i]);
            }
        }
        return arr; 
    }
    
	function resetData(){
		document.location.reload();
	}
	
	function refillComboWithAll(controlId,Array){
		$(controlId)
	    .find('option')
	    .remove()
	    .end()
	    .append('<option value="0">- ALL -</option>')
	    .val('0');
		$.each(Array, function (i, item) {
	        if(controlId == "#carRegNo"){
				$(controlId).append($('<option>', { 
					value: item.name,
					text : item.name,
			    }));
	        }else{
				$(controlId).append($('<option>', { 
					value: item.id,
					text : item.name,
			    }));
	        }
		});
		sortSelect(controlId);
	}

	function sortSelect(control){
		var options = $(control + ' option');
		var arr = options.map(function(_, o) { return { t: $(o).text(), v: o.value }; }).get();
		arr.sort(function(o1, o2) { return o1.t > o2.t ? 1 : o1.t < o2.t ? -1 : 0; });
		options.each(function(i, o) {
		  o.value = arr[i].v;
		  $(o).text(arr[i].t);
		});					
	}
	
	function refreshData(){
		if($('#userTable').dataTable() != null) $('#userTable').dataTable().fnDestroy();
		userTable = $('#userTable').DataTable({
			language : {
		        sLengthMenu: "Show _MENU_"
		    },
			"aaSorting": [],
			"bLengthChange": true,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": true,
			"aLengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
			dom: 'Blfrtip',
	        buttons: [
	            'copy','csv', 'excel', 'pdf', 'print'
	        ]
		});
		//localScriptWithRefresh();
	}

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
