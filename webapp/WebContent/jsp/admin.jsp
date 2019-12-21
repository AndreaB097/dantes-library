<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"
import="java.util.ArrayList, danteslibrary.model.*,
java.text.SimpleDateFormat, java.time.LocalDate, java.util.Calendar,
java.util.Date"%>

<!doctype html>
<html>
<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<title>Dante's Library | Gestore</title>
	<link rel="stylesheet" href="./css/style.css"/>
	<link rel="stylesheet" href="./css/jquery-ui.css"/>
	<script src="./scripts/fontawesome.js"></script>
	<script src="./scripts/jquery-3.4.1.min.js"></script>
	<script src="./scripts/jquery-ui.js"></script>
</head>
<body>
    
<%@include file="./jsp/layout/header.jsp" %>
	
<% if(session.getAttribute("admin") == null) { %>
<!-- Sezione LOGIN Gestori -->
<div id="form-container">
	<h2>Accedi come Amministratore</h2>
	<form id="sign-form" class="box" action="admin" method="post">
		<label for="enail">Email</label>
		<input id="email" type="text" name="email"/>
		<label for="password">Password</label>
		<input id="password" type="password" name="password"/><br/><br/>
		
		<button type="submit">Accedi</button>
	</form>
	<% if(request.getAttribute("login_error") != null) { %>
		<div class="error">Indirizzo e-mail o password non validi.</div>
	<% } %>
</div>

<% } 
	else { %>
	<!-- Pannello di controllo Gestori -->
	<div class="container">
		<!-- Menu laterale -->
		<div class="sidebar">
			<section class="active"><i class="fas fa-user-circle fa-lg"></i>&nbsp;&nbsp;&nbsp;Utenti</section>
			<section><i class="fas fa-book fa-lg"></i>&nbsp;&nbsp;&nbsp;&nbsp;Libri</section>
			<section><i class="fas fa-id-card fa-lg"></i>&nbsp;&nbsp;Tessere</section>
			<section><i class="fas fa-address-book fa-lg"></i>&nbsp;&nbsp;&nbsp;Prenotazioni</section>
			<section><i class="fas fa-users-cog fa-lg"></i>&nbsp;&nbsp;Gestori</section>
			<section><i class="fas fa-user-shield fa-lg"></i>&nbsp;&nbsp;Gestore biblioteca</section>
		</div>
		
		<div class="sidebar-responsive">
			<section class="active"><i class="fas fa-user-circle fa-lg"></i><br><p>Utenti<p></section>
			<section><i class="fas fa-book fa-lg"></i><br><p>Libri</p></section>
			<section><i class="fas fa-id-card fa-lg"></i><br><p>Tessere</p></section>
			<section><i class="fas fa-address-book fa-lg"></i><br><p>Prenotazioni</p></section>
			<section><i class="fas fa-users-cog fa-lg"></i><br><p>Gestori</p></section>
			<section><i class="fas fa-user-shield fa-lg"></i><br>Gestore biblioteca</section>
		</div>
		
		<!-- Sezione Utenti -->
		<div class="section-container">
			<h2>Gestione account utenti</h2>
			<form method="post">	
				<div id="search-bar">
					<input type="text" name="user_email" placeholder="Inserisci l'email dell'utente da cercare"/>
					<button type="submit" formaction="admin?account"><i class="fas fa-search"></i></button>
				</div>
				<button id="show-all-btn" type="submit" formaction="admin?all_users">Mostra tutti</button>
			</form>
		
			<% if(request.getAttribute("info") != null) { %>
				<div class="info">L'utente è stato rimosso con successo.</div>
			<% } %>
			<div class="overflow-container">
			<table>
			<%if(request.getAttribute("user_search") != null) { %>
					<tr>
						<th>Email</th>
						<th>Nome</th>
						<th>Cognome</th>
						<th>Codice fiscale</th>
						<th>Indirizzo</th>
					</tr>
					<tr>
						<td>${user_search.email}</td>
						<td>${user_search.name}</td>
						<td>${user_search.surname}</td>
						<td>${user_search.codice_fiscale}</td>
						<td>${user_search.address}</td>
						<td>
							<form action="admin?account" method="post">
								<input type="hidden" name="remove_user" value="${user_search.email}">
								<button id="btn-remove" type="submit"><i style="color: #e64c4c;" class="fas fa-times fa-lg"></i></button>
							</form>
						</td>
					</tr>
			<% } else if(request.getAttribute("all_users") != null) { %>
				<tr>
						<th>Email</th>
						<th>Nome</th>
						<th>Cognome</th>
						<th>Codice Fiscale</th>
						<th>Indirizzo</th>
				</tr>
				<%
				@SuppressWarnings("unchecked")
				ArrayList<UsersBean> users = (ArrayList<UsersBean>)request.getAttribute("all_users");
				for(int i = 0; i < users.size(); i++) {
					UsersBean user = users.get(i); %>
					<tr>
						<td><%=user.getEmail()%></td>
						<td><%=user.getName()%></td>
						<td><%=user.getSurname()%></td>
						<td><%=user.getCodice_fiscale()%></td>
						<td><%=user.getAddress()%></td>
						<td>
							<form action="admin?account" method="post">
								<input type="hidden" name="remove_user" value="<%=user.getEmail()%>">
								<button id="btn-remove" type="submit"><i style="color: #e64c4c;" class="fas fa-times fa-lg"></i></button>
							</form>
						</td>
					</tr>
			<% }
			} %>
			</table>
			</div> <!-- fine overflow-container -->
		</div> <!-- fine sezione Account -->
		
	</div>
<%} /*Chiusura dell'else in cima (quello che fa iniziare il pannello di controllo)*/%>
	
<%@include file="./jsp/layout/footer.jsp" %>

</body>
</html>