	function refreshData(){
		$('#menuTable').dataTable({
		  "bPaginate": true,
		  "bLengthChange": true,
		  "bFilter": true,
		  "bSort": false,
		  "bInfo": true,
		  "bAutoWidth": false
		});
	}

	function selectAllRole(){
		var checked=document.getElementById("chkAllRole").checked;
        if(checked) {
            $('.roleCheckBox').each(function() { 
           		this.checked = true;                 
            });
        }
        else{
            $('.roleCheckBox').each(function() { 
               	this.checked = false;
            });         
        }
	}

	function selectAllMenu(){
		var checked=document.getElementById("chkAllMenu").checked;
        if(checked) {
            $('.menuCheckBox').each(function() { 
           		this.checked = true;                 
            });
        }
        else{
            $('.menuCheckBox').each(function() { 
               	this.checked = false;
            });         
        }
	}

	function allowPermission() {
		/*Assign All Values for Module / Menu Selected*/
		$("#menuSelection").val('');
		$('.cbk').each(function(){
			 if($(this).prop('checked') == true){
				 var preSel = $("#menuSelection").val();
				 if(preSel != "")
					 $("#menuSelection").val(preSel + ","+ $(this).val());
				 else
					 $("#menuSelection").val($(this).val());
			 }
		});
		
		var roleSelection=$("#roleSelection").val();
		var menuSelection=$("#menuSelection").val();

		if(roleSelection =="" ){
        	alert("Please select atleast one role");
        	return false;
        }
		if(menuSelection==""){
        	alert("Please select atleast one menu");
        	return false;
        }
        
		if(confirm("Are You Sure, You Want to allow permission ?")){
		  	$.ajax({  
			    type: "POST",  	
			    url:  ctx + "/allowPermission.html",  
			    data: "roleSelection=" + roleSelection + "&menuSelection=" + menuSelection,  
			    success: function(response){
			      	if(response.status == "Success"){
				    	$('#info').html(response.result);
					    $('#error').hide('slow');
					    $('#info').show('slow');
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
			    }  
			});  
        }
	}  
