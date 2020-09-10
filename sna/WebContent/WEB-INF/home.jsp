<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page session="true"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Home</title>
</head>
<body>
	<h1>PUBLIC HOME</h1>

	<c:out value="${sessionScope.email}">User not logged!</c:out>
	<br>
	<br>
	<a href="/sna/public/register" accesskey="1" title="">Register</a>
	<br>
	<a href="/sna/public/login" accesskey="1" title="">Login</a>
	<br>
	<a href="/sna/public/logout" accesskey="1" title="">Logout</a>

</body>
</html>