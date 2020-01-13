<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!doctype html>
<html>
<head>
<%@include file="./jsp/layout/header.jsp" %>
	<title>Dante's Library | Risultati ricerca</title>
</head>
<body>

<%@ include file="./jsp/layout/navbar.jsp" %>

<div class="container">
	<h2>Contattaci</h2>
	<p style="white-space: pre-line">${applicationScope.library.contacts}</p>
</div>

<%@ include file="./jsp/layout/footer.jsp" %>

</body>
</html>