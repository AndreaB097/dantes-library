<%@ page language="java" contentType="text/html; charset=UTF-8" 
pageEncoding="UTF-8"%>

<!doctype html>
<html>
<head>
<%@include file="./jsp/layout/head.jsp" %>
	<title>Dante's Library | Accedi</title>
</head>
<body>

<%@ include file="./jsp/layout/navbar.jsp"%>

<% if(session.getAttribute("user") != null) {
	/*Se l'utente è già autenticato viene reindirizzato alla pagina del profilo,
	Altrimenti alla pagina che stava visitando prima di fare il login*/
	response.sendRedirect("profile.jsp"); 
   }
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