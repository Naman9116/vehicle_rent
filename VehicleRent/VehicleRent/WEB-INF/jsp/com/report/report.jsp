<%@ include file="/WEB-INF/jsp/com/common/include.jsp" %>
<HTML>
<HEAD>
	<TITLE> VehicleRent </TITLE>
	
	<style type="text/css">
		@media print {
 		   footer {page-break-after: always;}
		}
		@media all {
			.page-break	{ display: none; }
		}	
	</style>
	
	<script>
	
	var pageNumber=1;
	var contextPath;
	
	function onLoadPage(){
		var contextPath="<%=request.getContextPath()%>";
		altRows('dataTable');
	}
	
	function loadReport(reportType){
		if(reportType=="pdf"){
		    var reporturl  = "<%=request.getContextPath()%>/loadReportPDF.html";
		    $('#dataTable2').load(reporturl ,function(response, status, xhr) {
		        if (status == "error") {
				    var msg = "Sorry but there was an error getting details ! ";
					$("#dataTable2").html(msg + xhr.status + " " + xhr.statusText);
				  }
			});
		}
	}

	 $(function() {
         $("#printViewHeading").find('#printButton').on('click', function() {
	        var sHtml = "<div id='dvPrint'>"+$('#dataTable2').html()+"</div>";
	        var newdiv = document.createElement('div');
	        $(newdiv).html(document.getElementById("dataTable2").innerHTML);
            $.print($(newdiv));
         });
         
     });	
	
	
	 
	</script>
</HEAD>
	

<BODY onload="onLoadPage()" class="InputFormStyle">
	<form:form commandName="generalMasterModel" method="post">
		<ol style='padding: 0 0 0 0px;'>
			<table  width='100%' border=0>
				<tr valign="top">
				<td width='30%'>
					<table border=0 width='100%'>
						<TR>
							<TD>
								<fieldset style='border-radius: 10px;border: solid green 1px;'>
									<legend><strong>${pageFor}</strong></legend>
									<table style='width:100%;' border=0>
									<tr>
									</tr>
									</table>
								</fieldset>
							<TD>
						</TR>
						<TR>
							<TD>
								<fieldset style='border-radius: 10px;border: solid green 1px;'>
								<legend><strong>Action Required | Status </strong></legend>
								<table border=0 width='100%' >
								<tr>
									<td height="65px">
										<!-- Error Message Div -->
										<table border=0 width="100%">
											<tr><td colspan=3><div style="width:95%;height:60px;overflow:auto;display:none" id="error" class="error"></div></td></tr>
										</table>
						
										<!-- Success Message Div -->
										<table border=0 width="100%">
											<tr><td><div style="width:100%;height:20px;display:none" id="info" class="success"></div></td></tr>
										</table>
									</td>
								</tr>
								</table>
								</fieldset>
							</TD>			
						</TR>
					</table>
				</td>
				<td width='70%'>
					<table width='100%'>
						<TR>
							<TD>
								<div style="width:100%;height: 30px;" id="printViewHeading">
									<table align="center" width="100%" id="dataTable" border="0">
										<tr valign="top"> 
											<td width="70%"><input type="button" value="Print" id="printButton"></td>
											<td align="right"><B> Save as: </B> </td>
											<td align="right">&nbsp;</td>
											<td>
												<a href="javascript:loadReport('pdf')" >PDF</a>
											</td>
											<td>
												<a href="<%=request.getContextPath()%>/loadReportXLS.html">XLS</a>
											</td>
											<td>
												<a href="<%=request.getContextPath()%>/loadReportCSV.html">CSV</a>
											</td>
											<td>
												<a href="<%=request.getContextPath()%>/loadReportHTML.html">HTML</a>
											</td>
										</tr>
									</table>
								</div>
								<!-- Data Grid Body Div -->
								<div style="font-size:20px;width:100%;height: 440px;overflow:auto; border-radius: 5px;border: solid green 1px;" id="dataTable2" class="altrowstable"></div>
							</TD>			
						</TR>
					</table>
				</td>
				
				</tr>
			</table>
						
		</ol>
	</form:form>
</BODY>
</HTML>


