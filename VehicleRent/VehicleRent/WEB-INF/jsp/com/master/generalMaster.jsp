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
	<script src= "<%=request.getContextPath()%>/js/com/master/generalMaster.js"></script>
	<script src= "<%=request.getContextPath()%>/js/jQuery/jquery-sortable.js"></script>

	<script>
	var masterCode = '${masterCode}';
	var contextPath="<%=request.getContextPath()%>";
	var userPageAccess= '<%=sAccess%>';
	var masterId='';
	
	
function onLoadPage(){
		
		if(masterCode!='ALL'){
			masterId='${masterDataList[0].id}';
			getDataGridDataGeneralMaster( );
		}
		assignRights();
		getDataGridDataGeneralMaster(masterId);
		if(masterCode == "LOC"){
			$("#extraId").change();
		}
	} // Function to handle changes in the UI list
   
function onMasterSelectChange() {
		
        masterId = $("#masterId").val(); // Update masterId when a new item is selected
        onLoadPage(); // Reload the page with the updated masterId
    }
</script>
</HEAD>

<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
	<!-- form start -->
	<form:form commandName="generalMasterModel" method="post">
		<div class="row">
			<div class="col-md-5">
				<c:if test="${masterCode=='ALL'}">
					<legend><strong>General Master</strong></legend>
				</c:if>	
				<c:if test="${masterCode!='ALL'}">
					<legend><strong>${masterDataList[0].name} </strong></legend>
				</c:if>	
				<div class="box box-primary">
					<div class="box-body" style='height:65%;'>
					<table width=100%>
					<c:if test="${masterCode=='ALL'}">
						<tr height='35px' id='row1'>
							<td width=30%><form:label path="masterId">Master Name </form:label></td>
							<td width=70%>
								<form:select path="masterId" class="form-control" onchange="onMasterSelectChange()" >
								     <form:option value="0" label="--- Select ---"/>
									<form:options items="${masterDataList}" itemValue="id" itemLabel="name"/>	                
								</form:select>
							</td>	
						</tr>
					</c:if>	
					<c:if test="${masterCode=='ZONE' || masterCode=='LOC'}">
						<tr height='35px' id='branchRow'>	
							<td width=30%><form:label path="extraId">Branch Name</form:label></td>
							<td width=70%>
								<form:select path="extraId" class="form-control" onChange="fillZone()">
									<form:options items="${branchList}" itemValue="id" itemLabel="name"/>	                
								</form:select>
							</td>
						</tr>
					</c:if>	
					<c:if test="${masterCode=='LOC'}">
						<tr height='35px' id='branchRow'>	
							<td width=30%><form:label path="zoneId">Zone Name</form:label></td>
							<td width=70%>
								<form:select path="zoneId" class="form-control">
									<form:options items="${zoneList}" itemValue="id" itemLabel="name"/>	                
								</form:select>
							</td>
						</tr>
					</c:if>	
					<tr height='35px' id='row3'>	
						<td width=30%><form:label path="name">Name</form:label></td>
						<td width=70%><form:input path="name" class="form-control" placeholder="Name" maxlength="50"></form:input></td>
					</tr>
					<tr height='35px' id='row2'>
						<td><form:label path="name">Remark</form:label></td>
						<td><form:input path="remark" class="form-control" placeholder="Remark" maxlength="50" ></form:input></td>
					</tr>
					<c:if test="${masterDataList[0].name =='Tariff Schemes'}">
						<tr height='35px'>	
							<td width=30%><label>Minimum Hr.</label></td>
							<td width=70%><input id="minHR" class="form-control" placeholder="Minimum Hr." maxlength="5"></input></td>
						</tr>
						<tr height='35px'>	
							<td width=30%><label>Minimum Km.</label></td>
							<td width=70%><input id="minKM" class="form-control" placeholder="Minimum KM" maxlength="5"></input></td>
						</tr>
					</c:if>	
					<tr height="60px" valign="bottom" id='row4'>
						<td></td>
						<td>
							<input type='button'  value='Save'	 class="btn btn-primary" 	id="addButton"    onclick="saveOrUpdateGeneralMaster()" style="width:30%"/>
							<input type='button'  value='Update' class="btn btn-primary"	id="updateButton" onclick="saveOrUpdateGeneralMaster()" disabled style="width:30%"/>
							<input type='reset'   value='Reset'	 class="btn btn-primary"    id="resetButton"  onclick="resetData()" style="width:30%"/>
							<input type="hidden" id="idForUpdate" name="idForUpdate" value="0"/>
						</td>
					</tr>
					<tr height="60px" id='row5'>
						<td></td>
						<td colspan=2 align='center'>		
							<div style="width:100%;height:60px;overflow:auto;display:none" id="error" class="error"></div>
							<div style="width:100%;height:20px;display:none" id="info" class="success"></div>
						</td>
					</tr>
					</table>
					</div><!-- /.box -->
				</div>
			</div>
			<div class="col-md-7" >
				<c:if test="${masterCode=='ALL'}">
					<legend><strong>Existing General Master Values</strong></legend>
				</c:if>
				<c:if test="${masterCode!='ALL'}">
					<legend><strong>Existing ${masterDataList[0].name} Values</strong></legend>
				</c:if>		
				<div class="box box-primary">
					<div class="box-body" id="dataTable2" style="height:78%;overflow:auto"></div>
					<input type='button'  value='Save Sorting Order' class="btn btn-primary" 	id="sortButton"    onclick="getTable()" style="width:30%">
				</div>
			</div>
		</div>
	</form:form>
	</section><!-- /.content -->
</div><!-- /.content-wrapper -->
</BODY>
</HTML>
