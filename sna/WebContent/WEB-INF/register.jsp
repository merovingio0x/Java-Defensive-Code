<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Registrazione nuovo utente</title>
</head>
<body>
	<form name="registerForm" method="post" action="register">
		Email:<input type="text" name="email" /><br /> <br /> Password:<input
			type="password" name="pw1" /><br /> <br /> Re-Password:<input
			type="password" name="pw2" /><br /> <br /> Immagine del profilo:<input
			type="file" name="propic" accept="image/jpeg" value='progetto.jpeg'><br />
		<br /> <input type="submit" value="Register" />

	</form>

</body>
</html>