<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="e" uri="https://www.owasp.org/index.php/OWASP_Java_Encoder_Project" %>


<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page session="true"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Project</title>
</head>
<body>

	<p>User: <e:forHtml value="${sessionScope.email}" /></p>

	<p><strong>Project <c:out value="${project.name}">does not exist</c:out></strong></p>
	
	<br>
		<p style="white-space: pre-line"><e:forHtml value="${project.content}" /></p>




</body>
</html>