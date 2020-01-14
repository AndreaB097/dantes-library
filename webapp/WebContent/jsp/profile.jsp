<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.ArrayList,
    danteslibrary.model.UsersBean, danteslibrary.model.BookingsBean,
    danteslibrary.model.CardsBean, java.util.Locale, java.time.LocalDate,
    java.time.format.DateTimeFormatter"%>

<!doctype html>
<html>
<head>
<%@include file="./jsp/layout/header.jsp" %>
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
		
		<% if(request.getAttribute("info") != null) { %>
			<div class="info"><%=request.getAttribute("info") %></div>
		<% } %>
		<% if(request.getAttribute("error") != null) { %>
			<div class="error"><%=request.getAttribute("error") %></div>
		<% } %>
				
		<button class="dropdown-btn"><i class="fas fa-id-card fa"></i>&nbsp;&nbsp;Tessera</button>
		<div class="dropdown-content">
			<div id="card">
			<%if(session.getAttribute("card_date") != null) { %>
				<p id="card-date">Puoi ritirare la tua tessera a partire dal: <strong><%=session.getAttribute("card_date") %></strong></p>
				<p>Puoi già effettuare prenotazioni. Quando ti recherai in biblioteca per ritirare il libro,
				assicurati di ritirare anche la tua tessera!</p>
			<%}
			else if(session.getAttribute("card") != null) { %>
				<p>Codice Tessera: <b>${card.card_id }</b><br/>
				<%CardsBean card = (CardsBean) session.getAttribute("card");
				if(card.isAssociated()) { %>
					Stato: <i class="fas fa-check-circle fa-lg" style="color: #50ebaf"></i>
				<%}
				else {%>				
					Stato: <i class="fas fa-times-circle fa-lg" style="color: #eb5050"></i>
					<br/>
					<br/>
					La tua tessera non risulta associata, quindi non ti è 
					possibile effettuare alcuna prenotazione. Per favore contatta la biblioteca.
			  <%} %>
				</p>
			<%}
			  else { %>
				  <p><b>ATTENZIONE!</b> Non riusciamo a rilevare la tua tessera.
				  In questo stato non potrai effettuare prenotazioni. Contatta la
				  biblioteca per maggiori informazioni.
				  </p>
			<%}%>
			</div>
		</div>
		<button class="dropdown-btn"><i class="fas fa-address-book fa"></i>&nbsp;&nbsp;Storico prenotazioni</button>
		<div class="dropdown-content">
			<table id="bookings-list">
				<%
				UsersBean user = (UsersBean)session.getAttribute("user");
				@SuppressWarnings("unchecked")
				ArrayList<BookingsBean> bookings = (ArrayList<BookingsBean>) session.getAttribute("bookings");
			if(bookings != null)  {
				if(!bookings.isEmpty()) { %>
				<tr>
					<th>Codice</th>
					<th>Libro</th>
					<th>Data Inizio</th>
					<th>Data Fine</th>
					<th>Stato</th>
					<th></th>
				</tr>
			<% 
				for(int i = 0; i < bookings.size(); i++) {
					BookingsBean booking = bookings.get(i); %>
					<tr>
						<td><%=booking.getBooking_id() %></td>
						<td><%=booking.getTitle() %></td>
						<td><%=booking.getStart_date().format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ITALIAN))%></td>
						<td><%=booking.getEnd_date().format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ITALIAN))%></td>
						<td><i><%=booking.getState_name() %></i></td>
						<%if(booking.getState_name().equals("Non ancora ritirato"))  {%>
						<td>
						<form action="booking?cancel_booking" method="post">
								<input type="hidden" name="booking_id" value="<%=booking.getBooking_id()%>">
								<button id="btn-cancel_booking" type="submit">Annulla Prenotazione</button>
						</form>
						</td>
						<%}
						else {%>
						<td></td>
						<%}%>
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
						<td><%=bookings.get(i).getTitle() %></td>
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
		<button class="dropdown-btn"><i class="fas fa-user"></i>&nbsp;&nbsp;Dati personali</button>
		<div class="dropdown-content" style="margin: 0; text-align: left;">
			<div class="box">
			<form method ="post" action="login?edit_user">
			<label for="email">Email</label>
			<input id="email" name="email" class="editable" type="text" value="${user.email}" readonly>
			<input id="old_email" name="old_email" class="editable" type="hidden" value="${user.email}" readonly> 
			
			<label id="lbl_password" for="password">Password</label>
			<input id="password" name="password" placeholder="Lascia vuoto per non cambiare" class="editable" type="password" value="" readonly>  
		
			<label for="address">Indirizzo</label>
			<input id="address" name="address" class="editable" type="text" value="${user.address}" readonly>
			
			<button id="btn-sv" type="submit">Salva</button>

			<button type="button" id="btn-user">Modifica Dati</button>  
			<script>
				$("#btn-sv").hide();
				$("#lbl_password").hide();
				$("#password").hide();
				$("#btn-user").click(function() {
				$(".editable").removeAttr("readonly");
				$("#btn-sv").show();
				$("#password").show();
				$("#lbl_password").show();
				$("#btn-user").hide();
				});
			</script>
		</form>
		</div>
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