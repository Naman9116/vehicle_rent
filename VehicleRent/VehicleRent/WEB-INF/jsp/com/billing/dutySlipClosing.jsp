<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.util.Utilities"%>
<%
	String sAccess = "";
	Utilities utils = new Utilities();
	sAccess = utils.userAccessString(session, request, false);
%>
<html>
<head>
<script src="<%=request.getContextPath()%>/js/com/billing/billing.js"></script>
<script src= "<%=request.getContextPath()%>/js/com/common/moment.js"></script>
<script type="text/javascript">
var loginUserId = '${loginUserId}';
var carType = "";
var userPageAccess= '<%=sAccess%>';	
var jobType = '${jobType}';
$(function () {
    //Datemask dd/mm/yyyy
	$("#dsFromDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"})
	$("#dsDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"})
	$("#dsToDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"})
	$("#dsTimeIn").inputmask("hh:mm", {"placeholder": "hh:mm"});
	$("#dsTimeOut").inputmask("hh:mm", {"placeholder": "hh:mm"});
	$("#dsInvoiceDate").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"})

    $("#dsDate").val(getTimeStampTo_DDMMYYYY(new Date()));
    $("#dsInvoiceDate").val(getTimeStampTo_DDMMYYYY(new Date()));
});
function onLoadPage(){
	if(<%=session.getAttribute("loginUserId")%>!=null)
		loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
	assignRights();
	var contextPath="<%=request.getContextPath()%>";
}
function assignFromToDate(){
	var dtDsDate = $("#dsDate").val();
	$("#dsFromDate").val(dtDsDate);
	$("#dsToDate").val(dtDsDate);
}
</script>
</head>
<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
		<form id="bookingMasterForm" method="post">
			<legend><strong>Duty Slip Closing</strong></legend>
			<br>
			<div class="row" style="width: 95%; margin-left: 5px;">
				<div class="col-md-2"><label>Duty Slip Retreive</label></div>
				<div class="col-md-2"><input type="text" id="dSRetreive" name="dSRetreive" value=""class="form-control"></input></div>
	    		<div class="col-md-2">
	    			<input type='button' value='Search' class="btn btn-primary"  data-dismiss="modal" id="Search"  onClick="dutySlipClosingSearch()"/>
	      			<input type='button' value='Edit' class="btn btn-primary" id="Edit" disabled onClick="editDetail()"/>
	      		</div>
	      		<div class="col-md-3"></div>
				<div class="col-md-3" align="right">
					<input type='button' value='save' class="btn btn-primary" id="save" onClick="dutySlipInvoice(this.value)"/>
			    	<input type='button' value='Update' class="btn btn-primary" id="update" onClick="openInvoiceModel()" disabled="disabled"/>
			    	<input type='button' value='Invoice' class="btn btn-primary" id="invoice" onClick="dutySlipInvoice(this.value)"/>
			  		<input type='button' value='Reset' class="btn btn-primary"   id="resetButton" onClick="resetDetail()"/>
			  	</div>
			</div>
			<hr>
			<div class="row" style="width: 95%; margin-left: 5px;">
				<div class="col-md-2"><label>Booking No</label></div>
				<div class="col-md-2"><input type="text" id="dsbookingNo" name="dsbookingNo" value=""class="form-control" readonly="readonly" tabindex="-1"></input></div>
				<div class="col-md-2"><label>Invoice No</label></div>
				<div class="col-md-2"><input type="text" id="dsinvoiceNo" name="dsinvoiceNo" value=""class="form-control"></input></div>
				<div class="col-md-2"><label>Bkg Reference No</label></div>
				<div class="col-md-2"><input type="text" id="bkgReference" name="bkgReference" value=""class="form-control"></input></div>
			</div>
			<hr>
			<%@include file="dutySlipClose.jsp" %>
		    <div class="row" style="margin-top: 25px;">
		    	<div class="col-md-2"></div>
			</div>	
		</form>
	</section>	
</div>	

<!--  Div Loader -->
<div id="dataLoaderFade"></div>
<div id="dataLoaderModal" style="width: 170; height: 170; background-color: transparent;">
      <img id="loader" src="<%=request.getContextPath()%>/img/301(1).gif" width="98" height="98"  />
</div>
<!--  Invoice Model-->
<div id="viewInvoice" class="modal" style='min-width:100%' tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
 <div class="modal-dialog modal-lg">
  		<div class="modal-content" style="position: absolute; left: 51%; margin-left: -403px; top: 0px; width: 731px;margin-top: -24px; overflow-y: auto;overflow-x: hidden; height: 998px;">
      		<div class="modal-header bg-info" style='text-align:center; background-color: FFFFFF; margin-top: -18px;'>
  				<h4><button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:250%; color: #196780;">&times;</button></h4>
      		</div>
      		<div class="modal-body" id="viewInvoice_body">
				<div id="invoice" style="width:100%;overflow:auto;"></div>
		</div> 
      		</div>
	</div>
</div>	
<!--  MultipleInvoice Model-->
<div id="viewMultipleInvoice" class="modal" style='min-width:100%' tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg">
 		<div class="modal-content" style="position: absolute; left: 51%; margin-left: -403px; top: 0px; width: 731px;margin-top: -24px; overflow-y: auto;overflow-x: hidden; height: 642px;">
     		<div class="modal-header bg-info" style='text-align:center; background-color: FFFFFF; margin-top: -18px;'>
 				<h4><button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:250%; color: #196780;">&times;</button></h4>
     		</div>
     		<div class="modal-body" id="viewMultipleInvoice_body" >
				<div id="multipleInvoice" style="width:100%;overflow:auto;"></div>
			</div> 
		</div>	
	</div>
</div>
<!--  invoice Date Panel -->
<div id="invoiceDatePanel" class="modal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="invoiceDateLabel" aria-hidden="true">
	<div class="modal-dialog modal-md">
  		<div class="modal-content">
  			<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
  				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;' id="invoiceDateLabel"><b>Generate</b> Invoice 
      			<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:150%; color: #196780;">&times;</button></h4>
      		</div>
  			<div class="modal-body" id="invoiceDatePanel_body" >
			<div class="row" style="margin-top: 5px">
				<div class="col-md-1"></div>
				<div class="col-md-3"><label>Invoice Date : </label></div>
				<div class="col-md-2">
					<input type="text" id="dsInvoiceDate" name="dsInvoiceDate" value="" class="form-control" data-inputmask="'alias': 'dd/mm/yyyy'" onChange="fetchTaxDetail(this.value)"></input>
				</div>
				<div class="col-md-5">
					<input type="text" id="dsInvoice" name="dsInvoice" class="form-control" value=" " readonly="readonly" tabindex="-1"></input>
				</div>
				<div class="col-md-1"></div>
			</div>
			<div class="row" style="margin-top: 5px">
				<div class="col-md-1"></div>
				<div class="col-md-3"><label>Corporate Name : </label></div>
				<div class="col-md-7">
					<input type="text" id="corpName" name="corpName" class="form-control" value="" readonly="readonly" tabindex="-1"></input>
				</div>
				<div class="col-md-1"></div>
			</div>
			<div class="row" style="margin-top: 5px">
				<div class="col-md-1"></div>
				<div class="col-md-3"><label>Used By : </label></div>
				<div class="col-md-7">
					<input type="text" id="usedBy" name="usedBy" class="form-control" value="" readonly="readonly" tabindex="-1"></input>
				</div>
				<div class="col-md-1"></div>
			</div>
			<div class="row" id = "coverLetterDiv" style="margin-top: 5px;display:none">
				<div class="col-md-1"></div>
				<div class="col-md-3"><label>Cover Letter No : </label></div>
				<div class="col-md-2">
					<input type="text" id="coverLetterId" name="coverLetterId" class="form-control" readonly="readonly" tabindex="-1"></input>
				</div>
				<div class="col-md-3"><label>New Cover Letter No : </label></div>
				<div class="col-md-2">
					<select id="newCoverLetterId" name="newCoverLetterId" class="form-control">
						<option value="0">0</option>
					</select>
				</div>
				<div class="col-md-1"></div>
			</div>
			<div><hr></div>
			<div class="row" >
				<div class="col-md-1" tabindex="-1"></div>
				<div class="col-md-6" style="background-color: #FFDA71; text-align: center;" tabindex="-1">Tax Name</div>
				<div class="col-md-2" style="background-color: #FFDA71; text-align: center;" tabindex="-1">Tax %</div>
				<div class="col-md-2" style="background-color: #FFDA71; text-align: center;" tabindex="-1">Tax Amount</div>
				<div class="col-md-1" tabindex="-1"></div>
			</div>
			<div id='taxDet' class="row">
				
			</div>
			<br/>	  			
			
			<br/>	
			<div class="row" style="height: 45px;">
				<div class="col-sm-8">
					<div id="cancelReasonError" style="color:red;height: auto;font-size:11px;overflow-y:auto;display:none;"></div>
					<div id="cancelReasonInfo" style="color:green;display:none;"></div>
				</div>
				<div class="col-sm-1" style="margin-right: 50px;"><input type='button' value='Save' class="btn btn-primary" id="saveCR"  onClick="saveDs()" style="width:80px; color: black;"/>
				<input type='button' value='Update' class="btn btn-primary" id="ok"  style="width:80px; color: black; display: none;" onclick="saveUpdateDsDutySlipClosing()"/></div>
				<div class="col-sm-1"><input type='button' value='Close ' class="btn btn-primary"  id="closeWindowl"  data-dismiss="modal"  style="width:160%"/></div>
			</div>	
   		</div>
   	 </div>
    	</div>
 </div>
<!-- Update Dispatch Detail Record Panel -->
<div id="updatedispatchDetailRecordPanel" class="modal"
	style='min-width: 100%' tabindex="-1" role="dialog"
	data-backdrop="static" data-keyboard=false
	aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-md">
		<div class="modal-content">
			<div class="modal-header bg-info"
				style='text-align: center; background-color: FFDA71;'>
				<h4 class="modal-title"
					style='font-size: 14px; color: #196780; font-family: Bookman Old Style;'>Update the chauffeur Detail <button type="button" class="close" data-dismiss="modal"
						aria-hidden="true" style="font-size: 150%; color: #196780;">&times;</button>
					</h4>
				</div>
				<div class="modal-body" id="updatedispatchDetailRecordPanel_body">
					<div class="row">
						<div class="col-md-1"></div>
						<div class="col-md-3">
							<label>Car Number</label>
						</div>
						<div class="col-md-7">
							<input type="text" id="carNo" name="carNo" class="form-control"	readonly="readonly"></input>
						</div>
						<div class="col-md-1"></div>
					</div>


					<div class="row">
						<div class="col-md-1"></div>
						<div class="col-md-3">
							<label>Chauffeur Name</label>
						</div>
						<div class="col-md-7">
							<input type="text" id="updateChauffeurName" name="updateChauffeurName" class="form-control"></input>
						</div>
						<div class="col-md-1"></div>
					</div>

					<div class="row">
						<div class="col-md-1"></div>

						<div class="col-md-3">
							<label>Mobile Number</label>
						</div>
						<div class="col-md-7">
							<input type="text" id="updateMobileNumber" 	name="updateMobileNumber" class="form-control" 	onkeypress="return isNumber(event)">
						</div>
						<div class="col-md-1">
							<input type="hidden" value="N" id="isUpdatedChauffeur" />
						</div>
					</div>

					<br>
					<div class="row">
						<div class="col-md-1"></div>
						<div class="col-md-6">
							<div id="chauffeurDetailError"
								style="color: red; height: 40px; font-size: 11px; overflow-y: auto; display: none;">
							</div>
							<div id="chauffeurDetailInfo" style="color: green; display: none;"></div>
						</div>
						<div class="col-md-1">
							<input type='button' value='Ok' class="btn btn-primary" id="updateChauffeur" style="width: 60px"
								onclick="updateChauffeurDetails()" />
						</div>
						<div class="col-md-1"></div>
						<div class="col-md-1">
							<input type='button' value='Close' class="btn btn-primary" id="closeWindowl" data-dismiss="modal" style="width: 80px" />
						</div>
						<div class="col-md-1"></div>

					</div>

				</div>
			</div>
		</div>
	</div>
	
 <!-- Invoice Date Panel  For Ds Close Window-->
<div id="confirmBox" class="modal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard=false aria-labelledby="confirmBoxLabel" aria-hidden="true">
	<div class="modal-dialog modal-sm">
   		<div class="modal-content " style="width: 425px;">
   			<div class="modal-header bg-info" style='text-align:center; background-color: FFDA71;'>
   				<h4 class="modal-title" style='font-size:14px; color: #196780;  font-family: Bookman Old Style;' id="confirmBoxLabel">
       			<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="font-size:150%; color: #196780;" onclick="closePopUpDs()">&times;</button></h4>
       		</div>
   			<div class="modal-body" id="confirmBox_body" style="height: 110px; width: 100%"></div>
   			<div class="modal-footer">
   					<button id="cancel" type="button" class="btn-danger btn" data-dismiss="modal" aria-hidden="true" style="width: 84px;" onclick="closePopUpDs()">No</button>
   					<button id="yes" type="button" class="btn"  style="width: 84px; color: #196780;"  onclick="confirmDs()">Yes</button>
			</div>	
   	 	</div>
	</div>
</div>
</body>
</html>