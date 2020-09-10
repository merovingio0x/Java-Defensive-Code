<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="e"
	uri="https://www.owasp.org/index.php/OWASP_Java_Encoder_Project"%>

<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page session="true"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Request status</title>
</head>
<body>

	<p><c:out value="${message}"></c:out></p>
	
	<a href="/sna/public/home" accesskey="1" title="">Home</a>
	

</body>
</html>