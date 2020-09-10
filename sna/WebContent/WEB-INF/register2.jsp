<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Registrazione nuovo utente</title>
</head>
<body>
	<form name="registerForm" enctype="multipart/form-data" method="post"
		action="register">
		Email:<input type="text" name="email" value='hello@gmail.com' /><br />
		<br /> Password:<input type="password" value='Tarantola1234!'
			name="pw1" /><br />
		<pre>Password between 8 and 40 characters long 
		MUST Contain at least one digit. 
		MUST Contain at least one lower case character. 
		MUST Contain at least one upper case character. 
		MUST Contain at least one special character from [ @ # $ % ! . ].</pre>
		<br /> Re-Password:<input type="password" name="pw2"
			value="Tarantola1234!" /><br /> <br /> Immagine del profilo:<input
			type="file" name="propic" accept="image/jpeg"><br /> <input
			type="submit" value="Register" />

	</form>

</body>
</html>