<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
 <%@page isErrorPage = "true" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Error</title>
</head>
<body>
<h2>Something bad happened!</h2>
<%= exception.getMessage()%>

<br><br>
<a href="/sna/public/home" accesskey="1" title="">HOME</a>




</body>
</html>