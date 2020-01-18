<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.ArrayList,danteslibrary.model.CustomersBean,danteslibrary.model.BookingsBean,danteslibrary.model.CardsBean,java.util.Locale,java.time.LocalDate,java.time.format.DateTimeFormatter"%>

<!doctype html>
<html>
<head>
<%@include file="./jsp/layout/header.jsp" %>
	<title>Dante's Library | Area Cliente</title>
</head>
<body>

<%@ include file="./jsp/layout/navbar.jsp" %>

<%
	if(session.getAttribute("customer") == null) {
	response.sendRedirect("login.jsp"); /*Se il cliente non è autenticato viene 
								reindirizzato alla pagina di login*/
	return;
}
%>

<div class="container">
	<div class="profile-container">		
		<div id="name"><h1>${customer.name} ${customer.surname }</h1></div>
		
		<%if(request.getAttribute("info") != null) { %>
			<div class="info"><%=request.getAttribute("info")%></div>
		<% } %>
		<%if(request.getAttribute("error") != null) {%>
			<div class="error"><%=request.getAttribute("error")%></div>
		<% } %>
				
		<button class="dropdown-btn"><i class="fas fa-id-card fa"></i>&nbsp;&nbsp;Tessera</button>
		<div class="dropdown-content">
			<div id="card">
			<%if(session.getAttribute("card_date") != null) {%>
				<p id="card-date">Puoi ritirare la tua tessera a partire dal: <strong><%=session.getAttribute("card_date")%></strong></p>
				<p>Puoi già effettuare prenotazioni. Quando ti recherai in biblioteca per ritirare il libro,
				assicurati di ritirare anche la tua tessera!</p>
			<%}
			else if(session.getAttribute("card") != null) { %>
				<p>Codice Tessera: <b>${card.card_id }</b><br/>
				<%CardsBean card = (CardsBean) session.getAttribute("card");
					if(card.isAssociated()) { %>
						Stato: <i class="fas fa-check-circle fa-lg" style="color: #50ebaf"></i>
				<% }
					else { %>				
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
				CustomersBean customer = (CustomersBean)session.getAttribute("customer");
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
					<% 	for(int i = 0; i < bookings.size(); i++) {
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
					}
					else {%>
					<tr><td>Non ci sono ordini da visualizzare.</td></tr>
				<%  } %>
			</table>
			<!-- Tabella prenotazioni responsive -->
			<table id="bookings-list-responsive">
			<% if(!bookings.isEmpty()) { %>
				<tr>
					<th>#Prenotazioni</th>
					<th>Libro</th>
					<th>Data</th>
				</tr>
			<% for(int i = 0; i < bookings.size(); i++) {
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
			}
			else {%>
			<tr><td>Non ci sono ordini da visualizzare.</td></tr>
		 <% }
		 } else {%>
		<tr><td>Non ci sono ordini da visualizzare.</td></tr>
		<% } %>
			</table>
		</div>
		<button class="dropdown-btn"><i class="fas fa-user"></i>&nbsp;&nbsp;Dati personali</button>
		<div class="dropdown-content" style="margin: 0; text-align: left;">
			<div id="error-list" tabindex="-1"></div>
			<div class="box">
				<form method ="post" action="login?edit_customer" onsubmit="return validateSubmit()">
					<label for="email">Email</label>
					<input id="new_email" name="new_email" class="editable" type="text" value="${customer.email}" readonly style="width: 100%; margin-bottom: 20px">
					<input id="old_email" name="old_email" type="hidden" value="${customer.email}" readonly> 
					
					<label id="lbl_password" for="password">Password</label>
					<input id="password" name="password" placeholder="Lascia vuoto per non cambiare" class="editable" type="password" value="" readonly style="width: 100%; margin-bottom: 20px">  
					
					<label id="lbl_repeat" for="repeat">Ripeti Password</label>
					<input id="repeat" placeholder="Lascia vuoto per non cambiare" class="editable" type="password" value="" readonly style="width: 100%; margin-bottom: 20px">
					
					<label for="address">Indirizzo</label>
					<input id="address" name="address" class="editable" type="text" value="${customer.address}" readonly style="width: 100%; margin-bottom: 20px">
					
					<button id="btn-sv" type="submit">Salva</button>
		
					<button type="button" id="btn-customer">Modifica Dati</button>  
					<script>
						$("#btn-sv").hide();
						$("#lbl_password, #password").hide();
						$("#lbl_repeat, repeat").hide();
						$("#btn-customer").click(function() {
							$(".editable").removeAttr("readonly");
							$("#btn-sv").show();
							$("#lbl_password, #password").show();
							$("#lbl_repeat, #repeat").show();
							$("#btn-customer").hide();
						});
					</script>
				</form>
				<script>
				var errors = [];
					function validateSubmit() {
						var email_regex = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
						var password_regex = /^.{6,20}$/;
						var address_regex = /^[A-zÀ-ú0-9 ,]{5,100}$/;

						var new_email = document.getElementById("new_email").value;
						var old_email = document.getElementById("old_email").value;
						var password = document.getElementById("password").value;
						var repeat = document.getElementById("repeat").value;
						var address = document.getElementById("address").value;
						
						if(!new_email || !address || (password && !repeat) || (repeat && !password)) {
							errors.push("Non tutti i campi sono stati compilati.");
						}
						
						if((!new_email.match(email_regex) || (new_email.length < 5 || new_email.length > 100)) && new_email)
							errors.push("Indirizzo email non valido. Lunghezza massima 100 caratteri.");
						
						if(!password.match(password_regex) && password)
							errors.push("La password deve avere tra i 6 e i 20 caratteri.");
						
						if(password != repeat && password && repeat)
							errors.push("Le password non corrispondono.");
						
						if(!address.match(address_regex) && address)
							errors.push("L'indirizzo non è compilato correttamente.");

						if(errors.length != 0) {
							var errors_div = document.getElementById("error-list");
							var txt = "<ul>";
							$(errors_div).hide();
							errors_div.className = "error";
							errors.forEach(showErrors);
							errors_div.innerHTML = txt;
							
							function showErrors(value, index, array) {
								txt = txt + "<li>" + value + "</li>";
							}
							
							errors_div.innerHTML = txt + "</ul>";
							$(errors_div).fadeIn(300);
							errors = [];
							errors_div.focus();
							return false;
						}
						$("#error-list").hide();
						return true;
					}
				</script>
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