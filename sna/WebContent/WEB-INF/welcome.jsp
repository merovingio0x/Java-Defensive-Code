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
<title>Welcome</title>
</head>
<body>

	<img src="data:image/jpg;base64,${image}" width="140" height="150" />
	<p>User: ${sessionScope.email}</p>

	<p>Upload new project</p>
	<form name="projectForm" method="post" action="/sna/private/view"
		enctype="multipart/form-data">
		Project name:<input type="text" name="name"
			<e:forHtmlAttribute value=""/> /> <br /> Project file (.txt):<input
			type="file" name="project" accept="text/plain" value="keys.txt"><br>
		<br /> <input type="submit" value="Upload" />

	</form>

	<p>Project list</p>

	<form action="project" method="post">
		<select name="projectId" style="width:20%">
			<c:forEach items="${project}" var="elem">
				<option value=<e:forHtmlAttribute value="${elem.id}"/>>
				<e:forHtmlAttribute	value="${elem.name}" />
				</option>
			</c:forEach>
		</select> <input type="submit" value="View" />
	</form>
</body>
</html>