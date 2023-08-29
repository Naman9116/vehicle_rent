<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="/WEB-INF/jsp/com/common/include.jsp" %>
<html>
<head>
<style>
	td{
		white-space:nowrap; 
	    padding-top: .05em;
	    padding-bottom: .05em;
	}
	th{white-space:nowrap; }
</style>
<script>
	function getBranchDataUsingCompany(){
		var companyId = $('#company').val();
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/getBranchDataUsingCompany.html",  
		    data: "companyId="+companyId,  
		    success: function(response){
		      	if(response.status == "Success"){
		      		$('#branchDiv').html(response.dataGrid);
				}
		      	else{
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
		      	addClassForColor();
			},  
		    error: function(e){  
		      alert(e.status);
	           alert(e.responseText);
	           alert(thrownError);

		    }  
		});  
	}

	function showImage(){
		var userName = $('#userName').val();
	  	$.ajax({  
		    type: "POST",  	
		    url:  ctx + "/getUserIdByName.html",  
		    data: "userName="+userName,  
		    success: function(response){
		      	if(response.status == "Success"){
		      		$('#userId').val(response.userModel.id);
		    		document.getElementById("userImage").src = '/Images/Users/' + $('#userId').val();
				}
		      	else{
					$('#error').html(response.result);
					$('#info').hide('slow');
					$('#error').show('slow');
				}
		      	addClassForColor();
			},  
		    error: function(e){  
		      alert(e.status);
	           alert(e.responseText);
	           alert(thrownError);

		    }  
		});  
	}

	function userLogin(){
		var company = $('#company').val();
		var branch = $('#branch').val();
		document.forms[0].action="<%=request.getContextPath()%>/userLogin.html";
		document.forms[0].method="Post";
		document.forms[0].submit();
	}
	
	function onLoadPage(){
		$('#error').html("${message}");
		$('#info').hide('slow');
		$('#error').show('slow');
	}

	$(function () {
	$('input').iCheck({
	  checkboxClass: 'icheckbox_square-blue',
	  radioClass: 'iradio_square-blue',
	  increaseArea: '20%' // optional
	});
	});
</script>
</head>
  <body style="background:rgb(255, 218, 113);"class="login-page" onload="onLoadPage()" >
  <div class="row">
	<table width=100% height=100% >
	<tr>
	<td style="vertical-align: middle;">
    <div class="login-box"  style='width:45%;'>
		<div class="login-box-body">
		<form:form commandName="userModel" method="post">
			<table width=100% align=center >
			<tr>
				<td width=25% valign="middle">
					<a href="" class="logo"><img src="<%=request.getContextPath()%>/img/ComLogo.png" style="width:150px;height:70px;"/></a>
				</td>
				<td width=50%>
					<table width=100% >
					<tr><td colspan=2>
						<h4>Login Access in <img src="<%=request.getContextPath()%>/img/VRMS.png"/></h4>
						<hr style="border-color:rgb(255, 218, 113);">
						</td>
					</tr>	
					<tr><td width=30% align=left>Login</td>
						<td width=70%><form:input path="userName" id="userName" class='form-control' maxlength="50" onBlur="showImage()" style="width:100%" autofocus="autofocus"></form:input></td>
					</tr>
					<tr><td colspan=2></td></tr>
					<tr><td width=30% align=left>Password</td>
						<td width=70%><form:password path="password" id="password" class='form-control' maxlength="50" style="width:100%"></form:password></td>
					</tr>
					<tr>
						<td align=center colspan=2>
							<div style="width:95%;height:25px;overflow:auto;display:none;color: red;" id="error" class="error"></div>
							<button type="submit" style='width:100px' class='btn btn-primary' onclick="userLogin()">Sign In</button>
							<input type="hidden" id="userId" name="userId"/>
						</td>
					</tr>	
					</table>
				</td>
				<td width=25% valign='middle' align="center">
					<img id="userImage" src='<%=request.getContextPath()%>/img/blankImage.jpg' onerror="if (this.src != '<%=request.getContextPath()%>/img/blankImage.jpg') this.src = '<%=request.getContextPath()%>/img/blankImage.jpg';" width="75px" height="80px" border="1" style="border-color:rgb(255, 218, 113)"/>
				</td>
			</tr>
			</table>
		</form:form>
      </div><!-- /.login-box-body -->
    </div><!-- /.login-box -->
	</td>
	</tr>
	</table>
  </body>
</html>
