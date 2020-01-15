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
<%@include file="./jsp/layout/header.jsp" %>
	<title>Dante's Library | Tessera</title>
</head>
<body>

<%@ include file="./jsp/layout/navbar.jsp"%>

<div class="container" style="margin-top: 15px">
	<div id="form-container">
	<h2>2. Associazione Tessera</h2>
	
	<div id="error-list" tabindex="-1"></div>
	<% if(request.getAttribute("error") != null) { %>
		<div class="error" tabindex="-1"><%=request.getAttribute("error") %></div>
	<% } %>
	<div id="sign-form" class="box">
	  <small>Per poter prenotare libri occorre associare una tessera bibliotecaria al tuo account.</small>
	  <form class="col-50"  method="post">
	  	  <button id="new-card-btn" formaction="card?new_card">Non ho una tessera bibliotecaria</button>
	  </form>
	  <div class="col-50">
	  	  <button id="register-card-btn" type="button">Possiedo già una tessera bibliotecaria</button>
	  </div>
	  <form id="register-card-form" method="post">
		  <input name="card_id" type="text" placeholder="Inserisci il codice della tua tessera" required/>
		  <button formaction="card" type="submit">Invia codice</button>
	  </form>
	</div>
	<script>
		$("#register-card-form").hide();
		$("#register-card-btn").click(function() {
			$("#register-card-btn").toggleClass("active");
			$("#new-card-btn").toggleClass("disabled");
			document.getElementById("new-card-btn").toggleAttribute("disabled");
			$("#register-card-form").slideToggle();
		});
	</script>
	</div>
</div>

<%@ include file="./jsp/layout/footer.jsp"%>

</body>
</html>