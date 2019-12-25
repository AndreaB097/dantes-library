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
	<h2>Accedi come Gestore</h2>
	<form id="sign-form" class="box" action="admin" method="post">
		<label for="email">Email</label>
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
				<p>Ricerca per: 
				<select class="dropdownFilters" name="filter">
  					<option value="0">Nome</option>
  					<option value="1">Cognome</option>
  					<option value="2">Email</option>
  					<option value="3">Codice Fiscale</option>
				</select>
				</p>
				<div id="search-bar">
					<input class="search-field" type="text" name="keyword_users" placeholder="Seleziona il filtro ed effettua la ricerca" required/>
					<button class="search-button" type="submit" formaction="admin?users"><i class="fas fa-search"></i></button>
				</div>
				<script>
				$(document).ready(function() {
				    $('.dropdownFilters').selectmenu();
				});
				</script>
				
			</form>
			<button id="show-all-btn" onClick="window.location = 'admin?all_users'">Mostra tutti</button>
			<% if(request.getAttribute("info") != null) { %>
				<div class="info">L'utente <%=request.getAttribute("info") %> è stato rimosso con successo.</div>
			<% } %>
			<div class="overflow-container">
			<table>
			<%if(request.getAttribute("users") != null) { %>		
					<tr>
						<th>Email</th>
						<th>Nome</th>
						<th>Cognome</th>
						<th>Codice fiscale</th>
						<th>Indirizzo</th>
					</tr>
			<%	@SuppressWarnings("unchecked")
				ArrayList<UsersBean> users = (ArrayList<UsersBean>) request.getAttribute("users");
				for(UsersBean user : users)	{ %>
					<tr>
						<td><%=user.getEmail() %></td>
						<td><%=user.getName() %></td>
						<td><%=user.getSurname() %></td>
						<td><%=user.getCodice_fiscale() %></td>
						<td><%=user.getAddress() %></td>
						<td>
							<form action="admin?account" method="post">
								<input type="hidden" name="remove_user" value="<%=user.getEmail()%>">
								<button id="btn-remove" type="submit"><i style="color: #e64c4c;" class="fas fa-times fa-lg"></i></button>
							</form>
						</td>
					</tr>
			<%  } 
			} %>
			</table>
			</div> <!-- fine overflow-container -->
		</div> <!-- fine sezione Account -->
		
		<!--  Sezione Libri -->
		<div class="section-container">
			<h2>Gestione Libri</h2>
			<form method="post">
				<p>Ricerca per: 
				<select class="dropdownFilters" name="filter">
  					<option value="0">Titolo</option>
  					<option value="1">Autore</option>
  					<option value="2">Casa Editrice</option>
  					<option value="3">Genere</option>
				</select>
				</p>
				<div id="search-bar">
					<input class="search-field" type="text" name="keyword_book" placeholder="Seleziona il filtro ed effettua la ricerca" required/>
					<button class="search-button" type="submit" formaction="admin?books"><i class="fas fa-search"></i></button>
				</div>
				
			</form>
		
			<button id="show-all-btn" onClick="window.location = 'admin?books&all_books'">Mostra tutti</button>
			<%if(request.getAttribute("info") != null) { %>
				<div class="info">Il libro <%=session.getAttribute("info") %> è stato rimosso con successo.</div>
			<%session.removeAttribute("info"); 
			} %>
			
			<script>

			
			$(document).ready(function() {
			    $('.dropdownFilters').selectmenu();
			});
			</script>
			
			<table>
			<%if(request.getAttribute("books") != null) { %>		
					<tr>
						<th>Id</th>
						<th>Titolo</th>
						<th>Autore</th>
						<th>Casa Editrice</th>
						<th>Genere</th>
						<th>Quantita'</th>
					</tr>
			<%	@SuppressWarnings("unchecked")
				ArrayList<BooksBean> books = (ArrayList<BooksBean>) request.getAttribute("books");
				for(BooksBean book : books)	{ %>
					<tr>
						<td><%=book.getBook_id() %></td>
						<td><%=book.getTitle() %></td>
						<td> <% for(String s : book.getAuthors()) { %>
							                <%=s%>
							         <% }%>
						</td>
						<td><%=book.getPublisher() %></td>
												<td> <% for(String s : book.getGenres()) { %>
							                <%=s%>
							         <% }%>
						</td>
						<td><%=book.getQuantity() %></td>
						<td>
							<form action="admin?books" method="post">
								<input type="hidden" name="remove_book" value="<%=book.getBook_id()%>">
								<button id="btn-remove" type="submit"><i style="color: #e64c4c;" class="fas fa-times fa-lg"></i></button>
							</form>
						</td>
					</tr>
			<%  } 
			} %>
			</table>
		</div>
		
		<!-- Tessera -->
		<div class="section-container">
			<h2>Gestione Tessere</h2>
			<form method="post">
				<p>Ricerca per: 
				<select class="dropdownFilters" name="filter">
  					<option value="0">Nome</option>
  					<option value="1">Cognome</option>
  					<option value="2">Email</option>
  					<option value="3">Codice fiscale</option>
  					<option value="4">Codice tessera</option>
  					
				</select>
				</p>
				<div id="search-bar">
					<input class="search-field" type="text" name="keyword_card" placeholder="Seleziona il filtro ed effettua la ricerca" required/>
					<button class="search-button" type="submit" formaction="admin?cards"><i class="fas fa-search"></i></button>
				</div>
				
			</form>
		
			<button id="show-all-btn" onClick="window.location = 'admin?cards&all_cards'">Mostra tutti</button>
			<%if(request.getAttribute("info") != null) { %>
				<div class="info">La tessera <%=session.getAttribute("info") %> è stato rimossa con successo.</div>
			<%session.removeAttribute("info"); 
			} %>
			
			<table>
			<%if(request.getAttribute("cards") != null) { %>		
					<tr>
						<th>Nome</th>
						<th>Cognome</th>
						<th>Email</th>
						<th>Codice fiscale</th>
						<th>Codice tessera</th>
						<th>Associata</th>
					</tr>
			<%	@SuppressWarnings("unchecked")
				ArrayList<CardsBean> cards = (ArrayList<CardsBean>) request.getAttribute("cards");
				for(CardsBean card : cards)	{ %>
					<tr>
					 <% if (card.getName() != null && card.getSurname()!= null && card.getEmail()!= null ) {%>
						<td><%=card.getName() %></td>
						<td><%=card.getSurname() %></td>
						<td><%=card.getEmail() %></td>
					<% } else { %>
					 	<td><td>
					 	<td><td>
					 	<td><td>
					 <%} %>
						<td><%=card.getCodice_fiscale() %></td>
						<td><%=card.getCard_id() %></td>
						<td><%=card.isAssociated() %></td>
						<td>
							<form action="admin?cards" method="post">
								<input type="hidden" name="remove_card" value="<%=card.getCard_id()%>">
								<button id="btn-remove" type="submit"><i style="color: #e64c4c;" class="fas fa-times fa-lg"></i></button>
							</form>
						</td>
					</tr>
			<%  } 
			} %>
			</table>
		</div>
		
		<!-- Sezione Prenotazioni -->
		<div class="section-container">
			<h2>Gestione Prenotazioni</h2>
			<form method="post">
				<p>Ricerca per: 
				<select class="dropdownFilters" name="filter">
  					<option value="0">Codice prenotazione</option>
  					<option value="1">Codice Libro</option>
  					<option value="2">Codice Tessera</option>
  					<option value="3">Codice fiscale</option>
  					<option value="4">Stato</option>
  					<option value="5">Email</option>
  					<option value="6">Data inizio</option>
  					<option value="7">Data fine</option>
				</select>
				</p>
				<div id="search-bar">
					<input class="search-field" type="text" name="keyword_booking" placeholder="Seleziona il filtro ed effettua la ricerca" required/>
					<button class="search-button" type="submit" formaction="admin?bookings"><i class="fas fa-search"></i></button>
				</div>
				
			</form>
		
			<button id="show-all-btn" onClick="window.location = 'admin?bookings&all_bookings'">Mostra tutti</button>
			<%if(request.getAttribute("info") != null) { %>
				<div class="info">La prenotazione <%=session.getAttribute("info") %> è stato rimossa con successo.</div>
			<%session.removeAttribute("info"); 
			} %>
			
			<script>
			$(document).ready(function() {
			    $('.dropdownFilters').selectmenu();
			});
			</script>
			
			<table>
			<%if(request.getAttribute("bookings") != null) { %>		
					<tr>
						<th>Codice prenotazione</th>
						<th>Codice Libro</th>
						<th>Codice Tessera</th>
						<th>Codice fiscale</th>
						<th>Stato</th>
						<th>Data inizio</th>
						<th>Data fine'</th>
					</tr>
			<%	@SuppressWarnings("unchecked")
				ArrayList<BookingsBean> bookings = (ArrayList<BookingsBean>) request.getAttribute("bookings");
				for(BookingsBean booking : bookings)	{ %>
					<tr>
						<td><%=booking.getBooking_id() %></td>
						<td><%=booking.getBook_id() %></td>
						<td><%=booking.getCard_id() %></td>
						<td><%=booking.getCodice_fiscale() %></td>
						<td><%=booking.getState_id() %></td>
						<td><%=booking.getStart_date() %></td>
						<td><%=booking.getEnd_date() %></td>
						<td>
							<form action="admin?bookings" method="post">
								<input type="hidden" name="remove_booking" value="<%=booking.getBooking_id()%>">
								<button id="btn-remove" type="submit"><i style="color: #e64c4c;" class="fas fa-times fa-lg"></i></button>
							</form>
						</td>
					</tr>
			<%  } 
			} %>
			</table>
		</div>
		
		
	<!-- Script per il cambio di sezioni (cambio highlight della sezione "attiva" e relativo container di destra) -->
	<script>
		$(".section-container").hide();
		$(".sidebar section, .sidebar-responsive section").removeClass("active");
		<%if(request.getParameter("account") != null) {%>
			$(".section-container").eq(0).show();
			$(".sidebar section").eq(0).addClass("active");
			$(".sidebar-responsive section").eq(0).addClass("active");
		<% } else if(request.getParameter("books") != null) {%>
			$(".section-container").eq(1).show();
			$(".sidebar section").eq(1).addClass("active");
			$(".sidebar-responsive section").eq(1).addClass("active");
		<%} else if(request.getParameter("cards") != null) { %>
			$(".section-container").eq(2).show();
			$(".sidebar section").eq(2).addClass("active");
			$(".sidebar-responsive section").eq(2).addClass("active");
		<%} else if(request.getParameter("bookings") != null) { %>
			$(".section-container").eq(3).show();
			$(".sidebar section").eq(3).addClass("active");
			$(".sidebar-responsive section").eq(3).addClass("active");
			<%} else if(request.getParameter("managers") != null) { %>
			$(".section-container").eq(4).show();
			$(".sidebar section").eq(4).addClass("active");
			$(".sidebar-responsive section").eq(4).addClass("active");
			<%} else if(request.getParameter("library_manager") != null) { %>
			$(".section-container").eq(5).show();
			$(".sidebar section").eq(5).addClass("active");
			$(".sidebar-responsive section").eq(5).addClass("active"); 
		<%} else {%>
			$(".section-container").eq(0).show();
			$(".sidebar section").eq(0).addClass("active");
			$(".sidebar-responsive section").eq(0).addClass("active");
		<%}%>
		
			$("section").click(function() {
				if($("section").hasClass("active")) {
					$("section").removeClass("active");
					$(".section-container").hide();
					$(this).addClass("active");
					$(".section-container").eq($(this).index()).show();
				}
			});
			
		</script>
	</div>
<% } /*Chiusura dell'else in cima (quello che fa iniziare il pannello di controllo)*/%>
	
<%@include file="./jsp/layout/footer.jsp" %>

</body>
</html>