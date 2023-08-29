	function resetData(){
		document.forms[0].reset();
		$('#error').hide('slow');
		$('#info').hide('slow');
		document.getElementById("addButton").disabled = false;
	}

	function chnagePassword() {
		var oldPass = document.getElementById("oldPass").value;
		var newPass = document.getElementById("newPass").value;
		var confPass = document.getElementById("confPass").value;

		document.getElementById("addButton").disabled = true;
		$.ajax({
			type : "POST",
			url:  ctx + "/changePassword.html",
			data:"loginUserId=" + loginUserId + "&oldPass=" + oldPass + "&newPass=" + newPass + "&confPass=" + confPass,
			success : function(response) {
				if (response.status == "Success") {
					$('#info').html(response.result);
					resetData();
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
				} else {
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
			},
			error : function(e) {
				alert(e.status);
				alert(e.responseText);
			}
		});
		setTimeout(function() { document.getElementById("addButton").disabled=false;},3000);
	}
