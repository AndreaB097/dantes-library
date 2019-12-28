<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<%if(session.getAttribute("user") != null) {
	response.sendRedirect("profile.jsp");
	return;
}
else if(session.getAttribute("admin") != null) {
	response.sendRedirect("admin.jsp");
	return;
}
else if(session.getAttribute("user_incomplete") == null) {
	response.sendRedirect("registration.jsp");
	return;
} %>

<!doctype html>
<html>
<head>
<%@include file="./jsp/layout/head.jsp" %>
	<title>Dante's Library | Tessera</title>
</head>
<body>

<%@ include file="./jsp/layout/navbar.jsp"%>

<div class="container" style="margin-top: 15px">
	<div id="form-container">
	<h2>2. Associazione Tessera</h2>
	
	<div id="error-list" tabindex="-1"></div>
	
	<form id="sign-form" class="box" method="post">
	  <small>Per poter prenotare libri occorre associare una tessera bibliotecaria al tuo account.</small>
	  <div class="col-50">
	    <button formaction="card?new_card">Non ho una tessera bibliotecaria</button>
		</div>
		<div class="col-50">
		  <button formaction="card?register_card">Possiedo già una tessera bibliotecaria</button>
		</div>
	</form>
	</div>
</div>

<%@ include file="./jsp/layout/footer.jsp"%>

</body>
</html>