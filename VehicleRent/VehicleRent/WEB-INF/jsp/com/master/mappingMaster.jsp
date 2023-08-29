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
	<TITLE> Vehicle Rent </TITLE>
	<script src= "<%=request.getContextPath()%>/js/com/master/mappingMaster.js"></script>
	<script>
	var pageNumber=1;
	var contextPath;
	var userPageAccess= '<%=sAccess%>';
	function onLoadPage(){
		assignRights();
		var contextPath="<%=request.getContextPath()%>";
		$('#dataTable2').html("${mappingMasterDataList}");
	}
	</script>
	</STYLE>
</HEAD>

<BODY onload="onLoadPage()" >
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<!-- form start -->
	<form:form commandName="mappingMasterModel" method="post">
		<div class="row">
			<div class="col-md-3">
				<legend><strong>Mapping Criteria </strong></legend>
				<div class="box box-primary">
					<!-- Input Fields Div-->
					<div class="box-body">
						<div class="form-group">
							<form:label path="masterId" >Master Name </form:label>
							<form:select path="masterId" class="form-control" onchange="fetchMasterValuesAndSubMasterNamesMappingMaster(this.value)" style="width:100%">
									<form:option value="" label="--- Select ---"/>
								<form:options items="${masterIdList}" itemValue="id" itemLabel="name"/>	                
							</form:select>
						</div>
						<div class="form-group">
							<form:label path="masterValue" >Master Values </form:label></td>
							<div id="masterValueDiv" name="masterValueDiv" >
									<form:select class="form-control" path="masterValue"   style="width:100%">
									<form:option value="" label="--- Select ---"/>
								</form:select>
							</div>	
						</div>
						<div class="form-group">
							<form:label path="subMasterId" >Sub Master Name </form:label></td>
							<div id="subMasterIdDiv" name="subMasterIdDiv">
								<form:select  class="form-control" path="subMasterId"  style="width:100%">
										<form:option value="" label="--- Select ---"/>
								</form:select>
							</div>
						</div>
					</div>
				</div>
			</div>	
			<div class="col-md-8">
				<legend><strong>Mappings</strong></legend>
				<div class="box box-primary">
					<div class="box-body">
						<div class="form-group">
							<table width="100%" >
							<tr>
								<td width="45%">
									<table width="100%" border=0 id="dataTable">
										<caption><b>Not Mapped</b></caption>
										<tr>
											<th align='left'><input type='checkbox' id='selectAllNotMapped' onclick='selectAllNotMappedCheckBox()'/></th>
											<th width='90%' align='left'><b>Select All</b></th> 
										</tr>
									</table>
									<div style="width:100%;height: 250px;overflow:auto;border-radius: 2px; border: solid green 1px;" id="notMappedDiv" name="notMappedDiv"></div>
								</td>
								<td width="10%" align="center">
									<a href="javascript:saveOrUpdateMappingMaster()"><img src="<%=request.getContextPath()%>/img/right-arrow-icon.jpg" name="removeValuesButton" border="0"title="Remove Selected Mappings" width="80%" height="24" /></a><BR><BR> 
									<a href="javascript:removeOrUpdateMappingMaster()" ><img src="<%=request.getContextPath()%>/img/left-arrow-icon.jpg" name="addValuesButton" border="0" title="Add Selected Mappings" width="80%" height="24" /></a>
								</td>
								<td width="45%">
									<table width="100%" border=0 id="dataTable">
										<caption><b>Already Mapped</b></caption>
										<tr>
											<th align='left'><input type='checkbox' class="checkbox" id='selectAllAlreadyMapped' onclick='selectAllAlreadyMappedCheckBox()'/></th>
											<th width='90%' align='left'><b>Select All</b></th> 
										</tr>
									</table>
									<div style="width:100%;height: 250px;overflow:auto;border-radius: 2px; border: solid green 1px;" id="alreadyMappedDiv" name="alreadyMappedDiv"></div>
								</td>
							</tr>
							</table>
						</div>
					</div>
				</div>
			</div>	
			<div class="col-md-6">
				<legend><strong>Action Required | Status</strong></legend>
				<table border=0 width='100%' >
					<tr>	
						<td style='width:80%;height:45px'>
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
					<tr>
						<td width="30%">	
							<input type='reset' class="btn btn-primary"  value='Reset'	id="resetButton"  onclick="resetData()" style="width:20%">
							<input type="hidden" id="idForUpdate" name="idForUpdate" value="0">
						</td>
					</tr>
				</table>
			</div>
		</div>
	</form:form>
	</section>		
</div>	
</BODY>
</HTML>
