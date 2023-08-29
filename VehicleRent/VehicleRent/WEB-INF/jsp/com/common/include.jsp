<meta charset="UTF-8">
<meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'>
<!-- Bootstrap 3.3.2 -->
<link href="<%=request.getContextPath()%>/css/bootstrap/bootstrap.min.css" rel="stylesheet" type="text/css" />  
<!-- DATA TABLES -->
<link href="<%=request.getContextPath()%>/css/datatables/dataTables.bootstrap.css" rel="stylesheet" type="text/css" />
<!-- AdminLTE Skins. Choose a skin from the css/skins folder instead of downloading all of them to reduce the load. -->
<link href="<%=request.getContextPath()%>/css/bootstrap/_all-skins.min.css" rel="stylesheet" type="text/css" />
<!-- Theme style -->
<link href="<%=request.getContextPath()%>/css/bootstrap/AdminLTE.min.css" rel="stylesheet" type="text/css" />
<!-- iCheck -->
<link href="<%=request.getContextPath()%>/css/bootstrap/blue.css" rel="stylesheet" type="text/css" />
<!-- bootstrap wysihtml5 - text editor -->
<link href="<%=request.getContextPath()%>/css/bootstrap/bootstrap3-wysihtml5.min.css" rel="stylesheet" type="text/css" />
<link href="<%=request.getContextPath()%>/css/bootstrap/app.css" rel="stylesheet" type="text/css" />
<link href="<%=request.getContextPath()%>/css/bootstrap/sumoselect.css" rel="stylesheet" />
<link href="<%=request.getContextPath()%>/css/bootstrap/font-awesome.css" rel="stylesheet" type="text/css">
<!-- Data Tables -->
<link href="<%=request.getContextPath()%>/css/jQuery/jquery.dataTables.min.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath()%>/css/datatables/buttons.dataTables.min.css" rel="stylesheet" type="text/css">

<!-- jQuery 2.1.3 -->
<script src="<%=request.getContextPath()%>/js/jQuery/jQuery-2.1.3.min.js"></script>
<script src="<%=request.getContextPath()%>/js/bootstrap/bootstrap.min.js" type="text/javascript"></script> 
   
<!-- DATA TABES SCRIPT -->
<script src="<%=request.getContextPath()%>/js/datatables/jquery.dataTables.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/datatables/dataTables.buttons.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/datatables/buttons.flash.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/datatables/jszip.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/datatables/pdfmake.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/datatables/vfs_fonts.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/datatables/buttons.html5.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/datatables/buttons.print.min.js" type="text/javascript"></script>

<!-- Sparkline -->
<script src="<%=request.getContextPath()%>/js/jQuery/jquery.sparkline.min.js" type="text/javascript"></script>
<!-- Bootstrap WYSIHTML5 -->
<script src="<%=request.getContextPath()%>/js/bootstrap/bootstrap3-wysihtml5.all.min.js" type="text/javascript"></script>
<!-- iCheck -->
<script src="<%=request.getContextPath()%>/js/bootstrap/icheck.min.js" type="text/javascript"></script>
<!-- Slimscroll -->
<script src="<%=request.getContextPath()%>/js/jQuery/jquery.slimscroll.min.js" type="text/javascript"></script>
<!-- FastClick -->
<script src='<%=request.getContextPath()%>/js/bootstrap/fastclick.min.js'></script>
<!-- AdminLTE App -->
<script src="<%=request.getContextPath()%>/js/bootstrap/app.min.js" type="text/javascript"></script>
 <!-- InputMask -->
<script src="<%=request.getContextPath()%>/js/jQuery/jquery.inputmask.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/jQuery/jquery.inputmask.date.extensions.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/jQuery/jquery.inputmask.extensions.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/jQuery/jquery.sumoselect.min.js"></script>
<script src="<%=request.getContextPath()%>/js/com/common/common.js"></script>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<style>
#dataLoaderFade {
        display: none;
        position:absolute;
        top: 0%;
        left: 0%;
        width: 100%;
        height: 100%;
        background-color: #ababab;
        z-index: 11001;
        -moz-opacity: 0.8;
        opacity: .70;
        filter: alpha(opacity=80);
    }
    #dataLoaderModal {
        display: none;
        position: absolute;
        top: 45%;
        left: 45%;
        width: 120px;
        height: 110px;
        padding:30px 15px 0px;
        border: 3px solid #ababab;
        box-shadow:1px 1px 10px #ababab;
        border-radius:20px;
        background-color: white;
        z-index: 11002;
        text-align:center;
        overflow: auto;
    }
</style>
<div id="wait" align="center" style="display:none;width:100%;height:100%; z-index:1;background-color:gray; opacity:0.5; border:1px solid black;position:absolute;padding:2px;">
	<img style="margin-top:150px;" src='<%=request.getContextPath()%>/images/ajax-loader-blue.gif' width="80" height="80" /><br>
</div>


