<%@ include file="/WEB-INF/jsp/com/common/include.jsp" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<html>
<style type="text/css">
body
 {
     background-image: url(<%=request.getContextPath()%>/img/HomePage.jpg);
     background-repeat: no-repeat;
 }
 </style>
<body class="skin-yellow fixed" >
 <div class="wrapper">
	<tiles:insertAttribute name="header"/>
	<tiles:insertAttribute name="menu"/>
	<tiles:insertAttribute name="body"/>
	<tiles:insertAttribute name="footer"/>
</div>
</body>
</html>
