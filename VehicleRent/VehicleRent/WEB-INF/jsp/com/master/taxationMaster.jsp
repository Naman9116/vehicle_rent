<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.util.Utilities"%>
<%
	String sAccess = "";
	Utilities utils = new Utilities();
	sAccess = utils.userAccessString(session, request, false);
%>

<HTML>
<HEAD>
	<TITLE> VehicleRent </TITLE>
	<script src= "<%=request.getContextPath()%>/js/com/master/taxationMaster.js"></script>
	<script>
	var userPageAccess= '<%=sAccess%>';	
	function onLoadPage(){
		assignRights();
		$('#dataTable2').html("${existingData}");
//		getDataGridDataGeneralMaster();
		refreshData();
	}
	$(function () {
	    //Datemask dd/mm/yyyy
	    $("#efDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
	   // $("#taxPer").inputmask("99.99", {"placeholder": ".00"});
	   // $("#taxVal").inputmask("9999999.99", {"placeholder": ".00"});
	});

	</script>
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<!-- form start -->
	<form:form commandName="taxationModel" method="post" id="taxationForm">
		<div class="row">
			<div class="col-md-5">
				<legend>Master <strong>Taxation</strong></legend>
				<div class="box box-primary">
					<div class="box-body" style="height:65%;">
					<table width=98%>
					<tr height=35px>
						<td width=30%><form:label path="parentId">Taxation Name </form:label></td>
						<td width=70%>
							<form:select path="parentId" class="form-control" onChange="fillValues()">
								<form:options items="${taxationDataList}" itemValue="id" itemLabel="name"/>	                
							</form:select>
						</td>	
					</tr>
					<tr  height=35px>
						<td><form:label path="efDate">Effective Date</form:label></td>
						<td><form:input path="efDate" type="text" style="width:100%" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'"  maxlength="10"></form:input></td>
					</tr>
					<tr  height=35px>	
						<td><form:label path="calType">Calculation Type</form:label></td>
						<td>
							<table width=100%>
							<tr>
								<td width=50%>
									<form:radiobutton path="calType" name="calType" value="C"/> Cumulative
								</td>
								<td width=50%>
									<form:radiobutton path="calType" name="calType" value="L"/> Last Entry
								</td>
							</tr>
							</table>	
						</td>
					</tr>
					<tr height=35px>
						<td><form:label path="taxPer">Tax Percentage</form:label></td>
						<td><form:input path="taxPer" class="form-control" maxlength="5" onkeypress="isDouble(event,this)"></form:input></td>
					</tr>
					<tr  height=35px>
						<td><form:label path="taxVal">Tax Value</form:label></td>
						<td><form:input path="taxVal" class="form-control" maxlength="10" onkeypress="isDouble(event,this)"></form:input></td>
					</tr>
					<tr height="60px" valign="bottom">
						<td width=30%></td>
						<td width=70%>
							<input type='button'  value='Save'	 class="btn btn-primary" 	id="addButton"    onclick="saveOrUpdateTaxation()" style="width:30%">
							<input type='button'  value='Update' class="btn btn-primary"	id="updateButton" onclick="saveOrUpdateTaxation()" disabled style="width:30%">
							<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:30%">
							<input type="hidden" id="idForUpdate" name="idForUpdate" value="0">
						</td>
					</tr><tr height="60px">
						<td colspan=2 align='center'>		
							<div style="width:95%;height:60px;overflow:auto;display:none" id="error" class="error"></div>
							<div style="width:100%;height:20px;display:none" id="info" class="success"></div>
						</td>
					</tr>
					</table>
					</div><!-- /.box -->
				</div>
			</div>
			<div class="col-md-7" >
			<legend>Existing <strong>Taxation</strong> Values</legend>
				<div class="box box-primary">
					<div class="box-body" id="dataTable2" style="height:78%;overflow:auto">
					</div>
				</div>
			</div>
		</div>
	</form:form>
	</section><!-- /.content -->
</div><!-- /.content-wrapper -->
</BODY>
</HTML>
