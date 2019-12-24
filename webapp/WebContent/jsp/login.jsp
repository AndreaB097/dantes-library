<%@ page language="java" contentType="text/html; charset=UTF-8" 
pageEncoding="UTF-8"%>

<!doctype html>
<html>
<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" /> 
	<title>Dante's Library | Accedi</title>
	<link rel="stylesheet" href="./css/style.css"/>
	<link rel="stylesheet" href="./css/jquery-ui.css">
	<script src="./scripts/fontawesome.js"></script>
	<script src="./scripts/jquery-3.4.1.min.js"></script>
	<script src="./scripts/jquery-ui.js"></script>
</head>
<body>
<%@ include file="./jsp/layout/header.jsp"%>

<% if(session.getAttribute("user") != null)
	response.sendRedirect("profile.jsp"); /*Se l'utente è già autenticato viene 
											reindirizzato alla pagina del profilo,
											altrimenti alla pagina che stava visitando
											prima di fare il login*/
	session.removeAttribute("referer");
	session.setAttribute("referer", request.getHeader("referer"));
%>

<div id="form-container">
<h2>Accedi</h2>
	<form id="sign-form" class="box" action="login" method="post">
		<label for="email">Email</label>
		<input id="email" type="text" name="email"/>
		<label for="password">Password</label>
		<input id="password" type="password" name="password"/><br/><br/>
		
		<button type="submit">Accedi</button>
	</form>	

<% if(request.getAttribute("error") != null) { %>
		<div class="error"><%=request.getAttribute("error") %></div>
<% } %>
			
	<form id="sign-in-box" class="box" action="registration.jsp">
		<small>Sei un nuovo utente?&nbsp;&nbsp;&nbsp;</small>
		<button type="submit">Registrati</button>
	</form>
</div>
<%@ include file="./jsp/layout/footer.jsp"%>
</body>
</html>