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
	<div class="profile-container">		
		<div id="name"><h1>${user.name} ${user.surname }</h1></div>
		<button class="dropdown-btn">Storico prenotazioni</button>
		<div class="dropdown-content">
			<table id="bookings-list">
				<tr><td>TODO: Non ci sono ordini da visualizzare.</td></tr>
			</table>
			<!-- Tabella ordini responsive -->
			<table id="bookings-list-responsive">
				<tr><td>Non ci sono ordini da visualizzare.</td></tr>
			</table>
		</div>
		<button class="dropdown-btn">Dati personali</button>
		<div id="user-info" class="dropdown-content">
			<p><strong>Nome: </strong>${user.name}</p>
			<p><strong>Cognome: </strong>${user.surname}</p>
			<p><strong>Email: </strong>${user.email}</p>
			<p><strong>Indirizzo: </strong>${user.address}</p>
		</div>
	</div>
</div>

<script>
$(".dropdown-content").hide();
$(".dropdown-btn")
	.click(function() {
		$(this).toggleClass("active");
		$(this).next().slideToggle();
		
	});
</script>

<%@ include file="./jsp/layout/footer.jsp" %>

</body>
</html>