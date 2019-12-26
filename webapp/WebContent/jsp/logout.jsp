<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<!doctype html>
<html>
<head>
<%@include file="./jsp/layout/head.jsp" %>
	<title>Dante's Library | Logout</title>
</head>
<body>
	<% session.invalidate();
	response.sendRedirect("index.jsp");%>
</body>
</html>