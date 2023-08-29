<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<header class="main-header" style="height:80px; border-bottom: 2px solid #FFBD00;background-color: white;">
<!-- Logo -->
<a href="#" class="logo"><img src="<%=request.getContextPath()%>/img/ComLogo.png" style="width:200px;height:70px;padding-top: 15px;"/></a>
<!-- Header Navbar: style can be found in header.less -->
<nav class="navbar navbar-static-top" role="navigation">
  <!-- Sidebar toggle button-->
  <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button"><span class="sr-only">Toggle navigation</span>
  </a>
  <img src="<%=request.getContextPath()%>/img/VRMS2.png"/>
  <div class="navbar-custom-menu" >
	<ul class="nav navbar-nav">
	  <!-- User Account: style can be found in dropdown.less -->
	  <li>
		<a href="<%=request.getContextPath()%>/homePage.html" ><img src="<%=request.getContextPath()%>/img/Home.png" title="Home" width="45px" height="45px"/></a>
	  </li>
	  <li>
		<a href="<%=request.getContextPath()%>/pageLoadBookingMaster.html"><img src="<%=request.getContextPath()%>/img/Booking.png" title="Booking" width="45px" height="45px"></a>
	  </li>
  	  <li>
		<a href="<%=request.getContextPath()%>/pageLoadCorporateTariff.html" ><img src="<%=request.getContextPath()%>/img/Tarrif.png" title="Tariff Master" width="45px" height="45px"/></a>
	  </li>

	  <li class="dropdown user user-menu">
		<a href="#" class="dropdown-toggle" data-toggle="dropdown">
		  <img src='/Images/Users/${loginUserId}' onerror="if (this.src != '<%=request.getContextPath()%>/img/blankImage.jpg') this.src = '<%=request.getContextPath()%>/img/blankImage.jpg';" width="45px" height="45px" style="border-color:rgb(255, 218, 113);" border="1" />
		  <span class="hidden-xs">${loginUserName}</span>
		</a>
		<ul class="dropdown-menu">
		  <!-- User image -->
		  <li class="user-header">
			<img src='/Images/Users/${loginUserId}' onerror="if (this.src != '<%=request.getContextPath()%>/img/blankImage.jpg') this.src = '<%=request.getContextPath()%>/img/blankImage.jpg';" class="img-circle" alt="User Image" />
			<p>
			  ${loginUserName}
			</p>
		  </li>
		  <!-- Menu Footer-->
		  <li class="user-footer">
			<div class="pull-left">
				<c:if test="${loginUserRole != 'Administrator'}">
			  		<a href="#" class="btn btn-default btn-flat">Profile</a>
			    </c:if>
				<c:if test="${loginUserRole=='Administrator'}">
				    <a href="<%=request.getContextPath()%>/backup.php" class="btn btn-default btn-flat">MySql Backup</a>
			    </c:if>
			</div>
			<div class="pull-right">
			  <a href="<%=request.getContextPath()%>/login.html" class="btn btn-default btn-flat">Sign out</a>
			</div>
		  </li>
		</ul>
	  </li>
	</ul>
  </div>
</nav>

<script type="text/javascript">
 /*(function(d) {
  if (!d.getElementById("aAyieChat")) {
   var s = d.createElement("script"); 
   var head = d.getElementsByTagName("head")[0]; 
   s.setAttribute("charset","utf-8"); 
   s.id = "aAyieChat"; 
   s.type = "text/javascript"; 
   s.async = true; 
   s.src = "http://54.179.179.187:9090/widget/aAyieChat"; 
   head.appendChild(s);
   }
  })(document);*/
</script>

</header>
