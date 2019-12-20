<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="danteslibrary.model.UsersBean"%>

<!doctype html>
<html>
<head>
	<meta charset="UTF-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/> 
	<title>Dante's Library | Area Utente</title>
	<link rel="stylesheet" href="./css/style.css"/>
	<link rel="stylesheet" href="./css/jquery-ui.css"/>
	<script src="./scripts/fontawesome.js"></script>
	<script src="./scripts/jquery-3.4.1.min.js"></script>
	<script src="./scripts/jquery-ui.js"></script>
</head>
<body>

<%@ include file="./jsp/layout/header.jsp" %>

<% if(session.getAttribute("user") == null) {
	response.sendRedirect("login.jsp"); /*Se l'utente non è autenticato viene 
										reindirizzato alla pagina di login*/
	return;
}
%>

<div class="container">
	<h1>Area Utente</h1>
</div>

<%@ include file="./jsp/layout/footer.jsp" %>

</body>
</html>