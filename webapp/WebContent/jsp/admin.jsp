<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"
import="java.util.ArrayList, danteslibrary.model.*, java.text.SimpleDateFormat,
java.time.LocalDate, java.util.Calendar, java.util.Date"%>

<!doctype html>
<html>
<head>
<%@include file="./jsp/layout/head.jsp" %>
	<title>Dante's Library | Area Gestori</title>
</head>
<body>
    
<%@include file="./jsp/layout/navbar.jsp" %>
	
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
	else { 
	ManagersBean admin = (ManagersBean) session.getAttribute("admin");
	ArrayList<String> roles = admin.getRoles();%>
	<!-- Pannello di controllo Gestori -->
	<div class="container">
		<!-- Menu laterale -->
		<div class="sidebar">
			<%if(roles.contains("Gestore Utenti") || roles.contains("Gestore Biblioteca")) {%>
				<section class="active"><i class="fas fa-user-circle fa-lg"></i>&nbsp;&nbsp;&nbsp;Utenti</section>
			<%}
			if(roles.contains("Gestore Libri") || roles.contains("Gestore Biblioteca")) {%>
				<section><i class="fas fa-book fa-lg"></i>&nbsp;&nbsp;&nbsp;&nbsp;Libri</section>
			<%}
			if(roles.contains("Gestore Tessere") || roles.contains("Gestore Biblioteca")) {%>
				<section><i class="fas fa-id-card fa-lg"></i>&nbsp;&nbsp;Tessere</section>
			<%}
			if(roles.contains("Gestore Prenotazioni") || roles.contains("Gestore Biblioteca")) {%>
				<section><i class="fas fa-address-book fa-lg"></i>&nbsp;&nbsp;&nbsp;Prenotazioni</section>
			<%}
			if(roles.contains("Gestore Biblioteca")) {%>
				<section><i class="fas fa-users-cog fa-lg"></i>&nbsp;&nbsp;Gestori</section>
				<section><i class="fas fa-user-shield fa-lg"></i>&nbsp;&nbsp;Gestore biblioteca</section>
			<%} %>
		</div>
		
		<div class="sidebar-responsive">
			<%if(roles.contains("Gestore Utenti") || roles.contains("Gestore Biblioteca")) {%>
				<section class="active"><i class="fas fa-user-circle fa-lg"></i><br><p>Utenti<p></section>
			<%}
			if(roles.contains("Gestore Libri") || roles.contains("Gestore Biblioteca")) {%>
				<section><i class="fas fa-book fa-lg"></i><br><p>Libri</p></section>
			<%}
			if(roles.contains("Gestore Tessere") || roles.contains("Gestore Biblioteca")) {%>
				<section><i class="fas fa-id-card fa-lg"></i><br><p>Tessere</p></section>
			<%}
			if(roles.contains("Gestore Prenotazioni") || roles.contains("Gestore Biblioteca")) {%>
				<section><i class="fas fa-address-book fa-lg"></i><br><p>Prenotazioni</p></section>
			<%}
			if(roles.contains("Gestore Biblioteca")) {%>
				<section><i class="fas fa-users-cog fa-lg"></i><br><p>Gestori</p></section>
				<section><i class="fas fa-user-shield fa-lg"></i><br>Gestore biblioteca</section>
			<%} %>
		</div>
		
		<%if(roles.contains("Gestore Utenti") || roles.contains("Gestore Biblioteca")) {%>
		<!-- Sezione Utenti -->
		<div class="section-container">
			<h2>Sezione Account Utenti</h2>
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
		</div> <!-- fine sezione Utenti -->
		<%} %>
		
		<%if(roles.contains("Gestore Libri") || roles.contains("Gestore Biblioteca")) {%>
		<!--  Sezione Libri -->
		<div class="section-container">
			<h2>Sezione Libri</h2>
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
				<div class="info">Il libro <%=request.getAttribute("info") %> è stato rimosso con successo.</div>
			<%} %>
			
			<!--Added-->
			<button id="btn-book">Aggiungi Libro</button>     
			<button id="btn-genre">Aggiungi Genere</button>
			<button id="btn-delgenre">Cancella Genere</button>
			<!--Added-->
			
			<!--Added-->
			<script>
			$(document).ready(function() {
			    $('.dropdownFilters').selectmenu();
			});
			$(document).ready(function() {
				$("#new-book-form").hide();
				$("#btn-book, #btn-genre").click(function() {
					$("#new-book-form").slideDown();
					$("#update-book-form").hide();
					$("#all-books-div").hide();
				});
				$("#btn-all-books").click(function() {
					$("#all-books-div").slideDown();
					$("#new-book-form").hide();
					$("#update-books-form").hide();
				});
				<%if(request.getAttribute("error") != null) { %>
					$(".overflow-container").slideDown();
				<% } %>
			});	
			</script>
			<!--Added-->
			
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
		
			
			<!--Added-->
			<form id="new-book-form" method="post" class="overflow-container" onsubmit="return validateBook()">
			<h3></h3> <!-- Riempimento dinamico con jQuery, vedi sotto -->
			<div style="float:left" id="image-preview">
				<img src="images/no_image.jpg" alt="Nessun immagine">
			</div>
					<label id="btn-upload" for="image"><i class="far fa-images"></i></label>
					<input id="image" type="file" name="file" accept=".jpg, .jpeg">
					<label for="title">Titolo</label>
					<input id="title" name="title" type="text">
					<label for="authors">Autori</label>
					<input id="authors" name="authors" type="text">
					<label for="publisher">Casa editrice</label>
					<input id="publisher" name="publisher" type="text">
					<label for="genres">Genere</label>
					<input id="genres" name="genres" type="text">
					<label for="quantity">Quantità</label>
					<input id="quantity" name="quantity" type="text">
					<label for="description">Descrizione</label>
					<textarea id="description" name="description" rows="6" cols="60" style="resize: none;"></textarea>
					<script>
					$("#btn-book").click(function() {
						$("#new-book-form h3").text("Inserimento Libro");
					});
					</script>
					<button type="submit" class="save" formaction="admin?books&new_book"><i class="fas fa-plus fa-lg"></i> Aggiungi libro</button>
					<button type="reset" class="cancel"><i class="fas fa-times fa-lg"></i> Pulisci campi</button>
					<script>
						$(".cancel").click(function() {
							$("#image-preview").html('<img src="images/no_image.jpg" alt="Nessun immagine">');
						});
					</script>
		</form>
		
		<script>
				$("#error-list").hide();
				var errors = [];
				function validateBook() {
					var title = document.getElementById("title").value;
					var description = document.getElementById("description").value;
					var authors = document.getElementById("authors").value;
					var publisher = document.getElementById("publisher").value;
					var quantity = document.getElementById("quantity").value;
					var genres = document.getElementById("genres").value;
						
					if(!title || !description || !authors || !publisher || !quantity || !genres) {
						errors.push("Non tutti i campi sono stati compilati.");
					}
				
					if(errors.length != 0) {
						if(!document.getElementById("error-list")) {
							var errors_div = document.createElement("div");
							errors_div.setAttribute("id", "error-list");
						}
						else {
							var errors_div = document.getElementById("error-list");
						}
						var txt = "<ul>";
						$(".overflow-container h3").before(errors_div);
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
						$("#error-list").fadeOut(2500);
						return false;
					}
					
					$("#error-list").hide();
					return true;
				}
				</script>
		
		</div>
				
		<!--Added-->
		
		<%} %>
		
		<%if(roles.contains("Gestore Tessere") || roles.contains("Gestore Biblioteca")) {%>
		<!-- Tessera -->
		<div class="section-container">
			<h2>Sezione Tessere</h2>
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
			<button id="btn-card">Aggiungi Tessera</button> 
			<%if(request.getAttribute("info") != null) { %>
				<div class="info">La tessera <%=request.getAttribute("info") %> è stato rimossa con successo.</div>
			<%} %>

			
			<script>
			$(document).ready(function() {
			    $('.dropdownFilters').selectmenu();
			});
			$(document).ready(function() {
				$("#new-card-form").hide();
				$("#btn-card").click(function() {
					$("#new-card-form").slideDown();
					$("#all-cards-div").hide();
				});
				$("#btn-all-cards").click(function() {
					$("#all-cards-div").slideDown();
					$("#new-card-form").hide();
				});
				<%if(request.getAttribute("error") != null) { %>
					$(".overflow-container").slideDown();
				<% } %>
			});	
			</script>    

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
			
			
			<form id="new-card-form" method="post" class="overflow-container" onsubmit="return validateCard()">
			<h3></h3> <!-- Riempimento dinamico con jQuery, vedi sotto -->

					<label for="codice_fiscale">Codice fiscale</label>
					<input id="codice_fiscale" name="codice_fiscale" type="text">
					<label for="card_id">Codice tessera (facoltativo)</label>
					<input id="card_id" name="card_id" type="text">
					<label for="associated">Associata:</label>
					<input type="checkbox" name="associated" value="associated"> 
					<script>
					$("#btn-card").click(function() {
						$("#new-card-form h3").text("Inserimento Tessera");
					});
					</script>
					<button type="submit" class="save" formaction="admin?cards&new_card"><i class="fas fa-plus fa-lg"></i> Aggiungi Tessera</button>
					<button type="reset" class="cancel"><i class="fas fa-times fa-lg"></i> Pulisci campi</button>
		</form>
		
		<script>
				$("#error-list").hide();
				var errors = [];
				function validateBooking() {
					var codice_fiscale = document.getElementById("codice_fiscale").value;
					var card_id = document.getElementById("card_id").value;

						
					if(!codice_fiscale) {
						errors.push("Non tutti i campi sono stati compilati.");
					}
				
					if(errors.length != 0) {
						if(!document.getElementById("error-list")) {
							var errors_div = document.createElement("div");
							errors_div.setAttribute("id", "error-list");
						}
						else {
							var errors_div = document.getElementById("error-list");
						}
						var txt = "<ul>";
						$(".overflow-container h3").before(errors_div);
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
						$("#error-list").fadeOut(2500);
						return false;
					}
					
					$("#error-list").hide();
					return true;
				}
				</script>
		</div>
		<%} %>
		
		<%if(roles.contains("Gestore Prenotazioni") || roles.contains("Gestore Biblioteca")) {%>
		<!-- Sezione Prenotazioni -->
		<div class="section-container">
			<h2>Sezione Prenotazioni</h2>
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
			<button id="btn-booking">Aggiungi Prenotazione</button> 
			<%if(request.getAttribute("info") != null) { %>
				<div class="info">La prenotazione <%=request.getAttribute("info") %> è stato rimossa con successo.</div>
			<%} %>
			
			<script>
			$(document).ready(function() {
			    $('.dropdownFilters').selectmenu();
			});
			$(document).ready(function() {
				$("#new-booking-form").hide();
				$("#btn-booking").click(function() {
					$("#new-booking-form").slideDown();
					$("#all-bookings-div").hide();
				});
				$("#btn-all-bookings").click(function() {
					$("#all-bookings-div").slideDown();
					$("#new-booking-form").hide();
				});
				<%if(request.getAttribute("error") != null) { %>
					$(".overflow-container").slideDown();
				<% } %>
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
						<th>Data fine</th>
					</tr>
			<%	@SuppressWarnings("unchecked")
				ArrayList<BookingsBean> bookings = (ArrayList<BookingsBean>) request.getAttribute("bookings");
				for(BookingsBean booking : bookings)	{ %>
					<tr>
						<td><%=booking.getBooking_id() %></td>
						<td><%=booking.getBook_id() %></td>
						<td><%=booking.getCard_id() %></td>
						<td><%=booking.getCodice_fiscale() %></td>
						<td><%=booking.getState_name() %></td>
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
			
			<form id="new-booking-form" method="post" class="overflow-container" onsubmit="return validateBooking()">
			<h3></h3> <!-- Riempimento dinamico con jQuery, vedi sotto -->

					<label for="email">Email(facoltativo)</label>
					<input id="email" name="email" type="text">
					<label for="codice_fiscale">Codice fiscale</label>
					<input id="codice_fiscale" name="codice_fiscale" type="text">
					<label for="card_id">Codice Tessera</label>
					<input type="card_id" name="card_id"  type="text"> 
					<label for="book_id">Codice Libro</label>
					<input type="book_id" name="book_id"  type="text"> 
					<label for="start_date">Data inizio</label>
					<input type="start_date" name="start_date"  type="text"> 
					<label for="end_date">Data fine</label>
					<input type="end_date" name="end_date"  type="text"> 
					<label for="state">Stato prenotazione</label>
					<select class="dropdownFilters" name="state">
  						<option value="Non ancora ritirato">Non ancora ritirato</option>
  						<option value="Ritirato">Ritirato</option>
  						<option value="Riconsegnato">Riconsegnato</option>
  						<option value="Annullata">Annullata</option>
					</select>
					<script>
					$("#btn-booking").click(function() {
						$("#new-booking-form h3").text("Inserimento Prenotazione");
					});
					</script>
					<button type="submit" class="save" formaction="admin?bookings&new_booking"><i class="fas fa-plus fa-lg"></i> Aggiungi Prenotazione</button>
					<button type="reset" class="cancel"><i class="fas fa-times fa-lg"></i> Pulisci campi</button>
		</form>
		
		<script>
				$("#error-list").hide();
				var errors = [];
				function validateBooking() {
					var codice_fiscale = document.getElementById("codice_fiscale").value;
					var card_id = document.getElementById("card_id").value;
					var book_id = document.getElementById("book_id").value;
					var start_date = document.getElementById("start_date").value;
					var end_date = document.getElementById("end_date").value;
					
					if(!codice_fiscale || !card_id || !book_id || !start_date || !end_date) {
						errors.push("Non tutti i campi sono stati compilati.");
					}
				
					if(errors.length != 0) {
						if(!document.getElementById("error-list")) {
							var errors_div = document.createElement("div");
							errors_div.setAttribute("id", "error-list");
						}
						else {
							var errors_div = document.getElementById("error-list");
						}
						var txt = "<ul>";
						$(".overflow-container h3").before(errors_div);
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
						$("#error-list").fadeOut(2500);
						return false;
					}
					
					$("#error-list").hide();
					return true;
				}
				</script>
			
			
		</div>
		<%} %>
		
		<%if(roles.contains("Gestore Biblioteca")) {%>
		<!-- Sezione Gestori -->
		<div class="section-container">
			<h2>Sezione Gestori</h2>
			<form method="post">
				<p>Ricerca per: 
				<select class="dropdownFilters" name="filter">
  					<option value="0">Email</option>
  					<option value="1">Nome</option>
  					<option value="2">Cognome</option>
  					<option value="3">Ruolo</option>
				</select>
				</p>
				<div id="search-bar">
					<input class="search-field" type="text" name="keyword_manager" placeholder="Seleziona il filtro ed effettua la ricerca" required/>
					<button class="search-button" type="submit" formaction="admin?managers"><i class="fas fa-search"></i></button>
				</div>
				
			</form>
		
			<button id="show-all-btn" onClick="window.location = 'admin?managers&all_managers'">Mostra tutti</button>
			<button id="btn-manager">Aggiungi Gestore</button> 
			<%if(request.getAttribute("info") != null) { %>
				<div class="info">Il gestore <%=request.getAttribute("info") %> è stato rimosso con successo.</div>
			<%} %>
			
			<script>
			$(document).ready(function() {
			    $('.dropdownFilters').selectmenu();
			});
			$(document).ready(function() {
				$("#new-manager-form").hide();
				$("#btn-manager").click(function() {
					$("#new-manager-form").slideDown();
					$("#all-managers-div").hide();
				});
				$("#btn-all-managers").click(function() {
					$("#all-managers-div").slideDown();
					$("#new-manager-form").hide();
				});
				<%if(request.getAttribute("error") != null) { %>
					$(".overflow-container").slideDown();
				<% } %>
			});	
			</script>
			
			<table>
			<%if(request.getAttribute("managers") != null) { %>		
					<tr>
						<th>Email</th>
						<th>Nome</th>
						<th>Cognome</th>
						<th>Indirizzo</th>
						<th>Telefono</th>
						<th>Ruolo</th>
					</tr>
			<%	@SuppressWarnings("unchecked")
				ArrayList<ManagersBean> managers = (ArrayList<ManagersBean>) request.getAttribute("managers");
				for(ManagersBean manager : managers)	{ %>
					<tr>
						<td><%=manager.getEmail() %></td>
						<td><%=manager.getName() %></td>
						<td><%=manager.getSurname() %></td>
						<td><%=manager.getAddress() %></td>
						<td><%=manager.getPhone() %></td>
						<td> <% for(String s : 	manager.getRoles()) { %>
				               <%=s%>
				         	<% }%>
						</td>
						<td>
							<form action="admin?managers" method="post">
								<input type="hidden" name="remove_manager" value="<%=manager.getEmail()%>">
								<button id="btn-remove" type="submit"><i style="color: #e64c4c;" class="fas fa-times fa-lg"></i></button>
							</form>
						</td>
					</tr>
			<%  } 
			} %>
			</table>
			
			<form id="new-manager-form" method="post" class="overflow-container" onsubmit="return validateManager()">
			<h3></h3> <!-- Riempimento dinamico con jQuery, vedi sotto -->

					<label for="email">Email</label>
					<input id="email" name="email" type="text">
					
					<label for="password">Password</label>
					<input id="password" name="password" type="password">
					
					<label for="repeat_password">Ripeti Password</label>
					<input id="repeat_password" name="repeat_password" type="password">
					
					<label for="name">Nome</label>
					<input type="name" name="name"  type="text"> 
					
					<label for="surname">Cognome</label>
					<input id="surname" name="surname"  type="text"> 
					
					<label for="address">Indirizzo</label>
					<input id="address" name="address"  type="text"> 
					
					<label for="phone">Telefono</label>
					<input id="phone" name="phone"  type="text"> 
					
					<label for="state">Ruolo</label>
						<br>Gestore Utenti<input type="checkbox" name="users_manager" value="Gestore Utenti"><br>
						Gestore Libri<input type="checkbox" name="books_manager" value="Gestore Libri"><br>
						Gestore Tessere<input type="checkbox" name="cards_manager" value="Gestore Tessere"><br>
						Gestore Prenotazioni<input type="checkbox" name="bookings_manager" value="Gestore Prenotazioni"><br>
						Gestore Biblioteca<input type="checkbox" name="library_manager" value="Gestore Biblioteca"><br>


					<script>
					$("#btn-booking").click(function() {
						$("#new-booking-form h3").text("Inserimento Prenotazione");
					});
					</script>
					<button type="submit" class="save" formaction="admin?managers&new_manager"><i class="fas fa-plus fa-lg"></i> Aggiungi Gestore</button>
					<button type="reset" class="cancel"><i class="fas fa-times fa-lg"></i> Pulisci campi</button>
		</form>
		
		<script>
				$("#error-list").hide();
				var errors = [];
				function validateManager() {
					var email = document.getElementById("email").value;
					var name = document.getElementById("name").value;
					var surname = document.getElementById("surname").value;
					var password = document.getElementById("password").value;
					var repeat_password = document.getElementById("repeat_password").value;
					var address = document.getElementById("address").value;
					var phone = document.getElementById("phone").value;
					
					//fare controllo anche sui checkbox
					
					if(!email || !name || !surname || !password || !repeat_password || !address || !phone ) {
						errors.push("Non tutti i campi sono stati compilati.");
					}
					
					if(password != repeat_password){
						errors.push("Le password non corrispondono");
					}

				
					if(errors.length != 0) {
						if(!document.getElementById("error-list")) {
							var errors_div = document.createElement("div");
							errors_div.setAttribute("id", "error-list");
						}
						else {
							var errors_div = document.getElementById("error-list");
						}
						var txt = "<ul>";
						$(".overflow-container h3").before(errors_div);
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
						$("#error-list").fadeOut(2500);
						return false;
					}
					
					$("#error-list").hide();
					return true;
				}
				</script>
		
		</div>
		
		<!-- Sezione Gestore biblioteca -->
		<div class="section-container">
			<h2>Sezione Gestore biblioteca</h2>
		</div>
		<%} %>
		
		
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