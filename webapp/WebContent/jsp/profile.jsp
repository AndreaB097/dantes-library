<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.ArrayList,
    danteslibrary.model.UsersBean, danteslibrary.model.BookingsBean,
    java.text.SimpleDateFormat, java.time.LocalDate"%>

<!doctype html>
<html>
<head>
<%@include file="./jsp/layout/head.jsp" %>
	<title>Dante's Library | Area Utente</title>
</head>
<body>

<%@ include file="./jsp/layout/navbar.jsp" %>

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
				<%
				UsersBean user = (UsersBean)session.getAttribute("user");
				@SuppressWarnings("unchecked")
				ArrayList<BookingsBean> bookings = (ArrayList<BookingsBean>) session.getAttribute("bookings");
			if(bookings != null)  {
				if(!bookings.isEmpty()) { %>
				<tr>
					<th>#Prenotazione</th>
					<th>Libro</th>
					<th>Data Inizio</th>
					<th>Data Fine</th>
					<th>Stato</th>
				</tr>
			<% 
				for(int i = 0; i < bookings.size(); i++) {
					BookingsBean booking = bookings.get(i);
					SimpleDateFormat parsedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					SimpleDateFormat desiredFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					/*TODO Parsing delle date per formattarle meglio*/
				%>
					<tr>
						<td><%=booking.getBooking_id() %></td>
						<td><%=booking.getBook_id() %></td>
						<td><%=booking.getStart_date()%></td>
						<td><%=booking.getEnd_date()%></td>
						<td><i><%=booking.getState_name() %></i></td>
					</tr>
				<% }
			}else {%>
					<tr><td>Non ci sono ordini da visualizzare.</td></tr>
				<% } %>
			</table>
			<!-- Tabella prenotazioni responsive -->
			<table id="bookings-list-responsive">
			<% if(!bookings.isEmpty()) { %>
				<tr>
					<th>#Prenotazioni</th>
					<th>Libro</th>
					<th>Data</th>
				</tr>
			<%
				for(int i = 0; i < bookings.size(); i++) {
					BookingsBean booking = bookings.get(i);
				%>
					<tr class="striped">
						<td><%=bookings.get(i).getBooking_id() %></td>
						<td><%=bookings.get(i).getBook_id() %></td>
					</tr>
					<tr class="striped">
						<td colspan="2"><strong>Stato: </strong><i><%=bookings.get(i).getState_name() %></i></td>
					</tr>
				<% }
			} else {%>
			<tr><td>Non ci sono ordini da visualizzare.</td></tr>
		<% }
		} else {%>
		<tr><td>Non ci sono ordini da visualizzare.</td></tr>
	<% } %>
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