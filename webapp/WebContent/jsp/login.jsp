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

<div class="container" style="margin-top: 75px">
	<div id="form-container">
	<h2>Accedi</h2>
	<% if(request.getAttribute("info") != null) { %>
			<div class="info"><%=request.getAttribute("info") %></div>
	<% } %>
		
	<% if(request.getAttribute("error") != null) { %>
			<div class="error"><%=request.getAttribute("error") %></div>
	<% } %>
		<form id="sign-form" class="box" method="post">
			<label for="email">Email</label>
			<input id="email" type="text" name="email"/>
			<label for="password">Password</label>
			<input id="password" type="password" name="password"/>
			<a href="./reset_password" style="font-size: 13px; color: #0099ff">Password dimenticata?</a><br/><br/>
			<button type="submit" formaction="login">Accedi</button>
		</form>	
	
				
		<form id="sign-in-box" class="box" action="registration.jsp">
			<small>Sei un nuovo utente?&nbsp;&nbsp;&nbsp;</small>
			<button type="submit">Registrati</button>
		</form>
	</div>
</div>

<%@ include file="./jsp/layout/footer.jsp"%>

</body>
</html>