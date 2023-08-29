<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<HTML>
<HEAD>
	<TITLE> VehicleRent </TITLE>	

	<link href="<%=request.getContextPath()%>/css/bootstrap/ui.fancytree.css" rel="stylesheet" type="text/css">
	
	<script src="<%=request.getContextPath()%>/js/jQuery/jquery-ui.custom.js" type="text/javascript"></script>
	<script src="<%=request.getContextPath()%>/js/jQuery/jquery.cookie.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/js/jQuery/jquery.fancytree.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/js/jQuery/jquery.fancytree.table.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/js/bootstrap/prettify.js" type="text/javascript"></script>
	<script src='<%=request.getContextPath()%>/js/com/user/accessWizard.js'></script>
	
	<style type="text/css">
	  td.alignRight {
	     text-align: right;
	  }
	</style>
	<script type="text/javascript">
	var eventHandler="";
	var userIds="";
	var userId="";
	$(function(){
	   // Attach the fancytree widget to an existing <div id="tree"> element
	  // and pass the tree options as an argument to the fancytree() function:
	  $("#moduleMenuTbl").fancytree({
		 extensions: ["table"],
	    checkbox: true,
	    icons:false,
	    scrollParent: $("#moduleMenuDiv"),
	    selectMode:3 ,
	    table: {
	      indentation: 20,      // indent 20px per node level
	      nodeColumnIdx: 0     // render the node title into the 0 column
	    },
	    source: moduleMenuList,
	   renderColumns: function(event, data) {
	      var node = data.node,
	      $tdList = $(node.tr).find(">td");
	      $tdList.eq(1).html("<input type='checkbox' class='cbk cbk" + node.key + "' name='Add' value='" + node.key + "S'>");
	      $tdList.eq(2).html("<input type='checkbox' class='cbk cbk" + node.key + "' name='Edit'  value='" + node.key + "E'>");
	      $tdList.eq(3).html("<input type='checkbox' class='cbk cbk" + node.key + "' name='View'  value='" + node.key + "V'>");
	      $tdList.eq(4).html("<input type='checkbox' class='cbk cbk" + node.key + "' name='Delete' value='" + node.key + "D'>");
	    },
	   select: function(event, data) {
	    	// Display list of selected nodes
			data.node.setExpanded(true);
	    	if(eventHandler==""){
       		$('.cbk' + data.node.key ).each(function(){
           		  var childs = data.node.getChildren();
           			if(childs == null){
               		  if(data.node.selected === true)
       	        		  $(this).prop('checked',true);
               		  else
       	        		  $(this).prop('checked',false);
           		  }else{
               		  if(data.node.selected === true)
       	        		  $(this).prop('checked',true);
               		  else
       	        		  $(this).prop('checked',false);
	           		  $.each(childs, function(index,value){
	                   	  $('.cbk' + value.key ).each(function(){
	                   		  if(data.node.selected === true)
	           	        		  $(this).prop('checked',true);
	                   		  else
	           	        		  $(this).prop('checked',false);
	                   	  });
	           		  });
           		  }
   	     	  });
	    	}
	        }, 
	    click: function(event, data) {
		   var count=1;
		   var formatedNodeKey="";
		   var nodeKey="";
		   var node = data.node;
			 $('.cbk' +data.node.key ).each(function(){
				nodeKey =$(this).val();
				formatedNodeKey=nodeKey.substring(0,(nodeKey.length - 1));
				var dataNode=data.node;
				if($(this).prop('checked') == true){
				if(count==4){
				node.selected = true;
				 }
				count++;
				}
		     });
			// We should not toggle, if target was "checkbox", because this
	          // would result in double-toggle (i.e. no toggle)
	          var selNodes = data.tree.getSelectedNodes();
	    		// convert to title/key array
 	          if( $.ui.fancytree.getEventTargetType(event) === "title" ){
	            data.node.toggleSelected();
	          }
 	        },
	        keydown: function(event, data) {
	          if( event.which === 32 ) {
	            data.node.toggleSelected();
	            return false;
	          }
	        },
	    cookieId: "fancytree-Cb1",
        idPrefix: "fancytree-Cb1-"
	  });
	  
	  $("#roleUserTbl").fancytree({
//	      extensions: ["table"],
	      checkbox: true,
    	  icons:false,
	      source: roleUserList,
	      generateIds: true,
	      selectMode: 3,
	      table: {
	        indentation: 30,      // indent 20px per node level
	        nodeColumnIdx: 0     // render the node title into the 0 column
	      },
	      select: function(event, data) {
	    	  userId="";
	    	  userIds="";
	    	  $('.cbk').each(function(){
           		  $(this).prop('checked',false);
           	  });
	    	 // Display list of selected nodes
	          var selNodes = data.tree.getSelectedNodes();
	          // convert to title/key array
	          var selKeys = $.map(selNodes, function(node){
	               return node.key;
	            });
	          $("#roleSelection").val(selKeys.join(","));
			 //Disable And Enable Show Button---------Sahil
	     	   if($("#roleSelection").val().indexOf(',') != -1 ){
	     			 $("#viewButton").attr("disabled",false);
	     		   var arr = $("#roleSelection").val().split(",");
	     		   if(arr.length == 2){
		     			   if(arr[0].indexOf("R") != -1){
		     				  userIds=arr[1];
		     				 $("#viewButton").attr("disabled",false);
		     			   }else{
	     				 	$("#viewButton").attr("disabled","disabled");
	     			   }
	     		   }else{
	     			  $("#viewButton").attr("disabled","disabled");
	     		   }
	      		
	          }else{
	    		 if($("#roleSelection").val()!=""){
	        	 $("#viewButton").removeAttr("disabled");
	        	 }
	        	  else{
	        		  $("#viewButton").attr("disabled","disabled");
	        	  }
	          }
	        },
	        click: function(event, data) {
	       		// We should not toggle, if target was "checkbox", because this
	          // would result in double-toggle (i.e. no toggle)
	          if( $.ui.fancytree.getEventTargetType(event) === "title" ){
	            data.node.toggleSelected();
	          }
	        },
	        
	        keydown: function(event, data) {
	          if( event.which === 32 ) {
	            data.node.toggleSelected();
	            return false;
	          }
	        },
	        // The following options are only required, if we have more than one tree on one page:
	        cookieId: "fancytree-Cb2",
	        idPrefix: "fancytree-Cb2-"

	    });
	});
	 function showPermission(){
		 $('#wait').show();
		 eventHandler="Y";
			 $("#moduleMenuTbl").fancytree("getRootNode").visit(function(node){
		        node.setExpanded(true);
		   });
		var menuIdString="";
		var menuId="";
		 if(userIds!=""){
			 userId=userIds.substring(1)
		 }
		 else{
			 userId=$("#roleSelection").val().substring(1); 
		 }
			$.ajax({
				type : "POST",
				url:  ctx + '/getAssignPermission.html',
				data : "userId=" + userId,
				async : false,
				success : function(response) {
					if (response.status == "Success") {
						$.each(response.userAccessModel, function(index,val) {
							menuIdString="R"+val.menuId.id+val.menuAccess;
							menuId="U"+val.menuId.id+val.menuAccess;
						  	$('.cbk').each(function(){
							if($(this).val() == menuIdString){
								var nodeValue=$(this).val();
								var nodeValues=nodeValue.substring(0,(nodeValue.length - 1));
							    $(this).prop("checked",true);
								$("#moduleMenuTbl").fancytree("getRootNode").visit(function(node){
									if(node.key==nodeValues){
										node.setSelected(true);
									}
							     }); 
					            } 
					         });
						$('.cbk').each(function(){
							if($(this).val() == menuId){ 
							var nodeSubValue=$(this).val();
							var nodeSubValues=nodeSubValue.substring(0,(nodeSubValue.length - 1));
							$(this).prop("checked",true);
							$("#moduleMenuTbl").fancytree("getRootNode").visit(function(node){
								if(node.key==nodeSubValues){
									node.setSelected(true);
								}
							  }); 
						     } 
						   });
							menuId="";
							menuIdString="";
				        });
					  $("#viewButton").attr("disabled","disabled");
			        }
		           },
	             });
			eventHandler="";	
			 $('#wait').hide();
         }

	 </script>
	
	<script>
		var loginUserId;
		var roleUserList = ${roleUserList};	
		var moduleMenuList = ${moduleMenuList};
		function onLoadPage(){
			refreshData();
			contextPath="<%=request.getContextPath()%>";
		    if(<%=session.getAttribute("loginUserId")%>!=null)
		    	loginUserId='<%=(Long) session.getAttribute("loginUserId")%>';
		    	
		    	
		}
	</script>
</HEAD>	
<BODY onload="onLoadPage()">
<div class="content-wrapper">
	<!-- Main content -->
	<section class="content">
		<form:form commandName="userModel" method="post">
		<div class="row">
			<div class="col-md-12">
				<legend><strong>Access Wizard</strong></legend>
				<br><br>
				<table width=100%>
				<tr>
					<td width=2%></td>
					<td width=35% valign="top">
						<div class="box box-primary">
							<div class="box-body">
							<legend><strong>Role / User </strong></legend>
							<div id="roleUserTbl"></div>
							 <input type="hidden" id="roleSelection"/>
							</div>
						</div>
					</td>
					<td width=2%></td>
					<td width=50% valign="top">
						<div class="box box-primary">
							<div class="box-body">
							<legend><strong>Module / Menu Access </strong></legend>
							<div id="moduleMenuDiv" style="height: 400px; overflow: auto;">
							  <!-- Add a <table> element where the tree should appear: -->
							  <table id="moduleMenuTbl" width=100%>
							    <colgroup>
							    <col width="60%"></col>
							    <col width="10%" align="center"></col>
							    <col width="10%" align="center"></col>
							    <col width="10%" align="center"></col>
							    <col width="10%" align="center"></col>
							    </colgroup>
							    <thead>
							      <tr>
							      	<th></th>
							      	<th align="center">Add</th> 
							      	<th align="center">Edit</th> 
							      	<th align="center">View</th> 
							      	<th align="center">Delete</th> 
							      </tr>
							    </thead>
							    <tbody>
							      <tr> 
							      	<td align="center"></td> 
							      	<td align="center"></td> 
							      	<td align="center"></td> 
							      	<td align="center"></td> 
							      	<td align="center"></td> 
							      </tr>
							    </tbody>
							  </table>
							  </div>
  							 <input type="hidden" id="menuSelection"/>
							</div>
						</div>
					</td>
					<td width=2%></td>
				</tr>
				<tr>
					<td align="Right" colspan=2>
						<input type='button'  value='Show Permission'	class='btn btn-primary' style='width:150px;' disabled="disabled" onclick="showPermission()" id="viewButton"/>
						<input type='button'  value='Allow Permission'	class='btn btn-primary' style='width:150px;' onclick="allowPermission()" id="addButton"/>
						<input type='reset'  value='Clear' onclick="resetData()" style='width:100px;'	class='btn btn-primary' id="resetButton"/></td>
					<td colspan=2>
						<div style="width:100%;height:20px;display:none" id="info" class="success"></div>
						<div style="width:95%;height:60px;overflow:auto;display:none" id="error" class="error"></div>
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