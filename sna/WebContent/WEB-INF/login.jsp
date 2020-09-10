<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
	
<%@ page errorPage="result.jsp"%>
<%@ page session="true"%>



<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Login</title>
</head>
<body>

	<form name="loginForm" method="post" action="login">
		Email:<input type="text" name="email" value='hello@gmail.com' /><br />
		<br /> Password:<input type="password" value='Tarantola1234!'
			name="pw1" /><br /> <input type="checkbox" name="rememberMe"
			value="true" />Remember me<br /> <input type="submit" value="Login" />
	</form>

</body>
</html>