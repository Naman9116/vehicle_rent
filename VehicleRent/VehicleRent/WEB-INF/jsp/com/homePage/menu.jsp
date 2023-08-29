<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!-- Left side column. contains the logo and sidebar -->
<aside class="main-sidebar">
<!-- sidebar: style can be found in sidebar.less -->
<section class="sidebar" style="height: 100%; overflow: scroll; width: auto;">
  <!-- sidebar menu: : style can be found in sidebar.less -->
  <ul class="sidebar-menu">
	<c:forEach var="moduleList" items="${moduleList}">
		<li class="treeview">
		  <a href="#">
			<span><c:out value='${moduleList.name}'/></span>
		  </a>
		  <ul class="treeview-menu">
			  <c:forEach var="menuList" items="${menuList}">
			  	<c:if test="${moduleList.id==menuList.masterId}">
					<c:if test="${menuList.remark != null && menuList.remark != ''}">
			  			<li><a href="<%=request.getContextPath()%>/<c:out value='${menuList.remark}'/>"><c:out value='${menuList.name}' /></a></li>
			  		</c:if>	
					<c:if test="${menuList.remark == '' || menuList.remark == null}">
			  			<li><a href="#"><c:out value='${menuList.name}' /></a></li>
			  		</c:if>	
		  		</c:if>
			  </c:forEach>
		  </ul>
		</li>
	</c:forEach>
</ul>
</section>
<!-- /.sidebar -->
</aside>
