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
			
			<% if(request.getAttribute("info_user") != null) { %>
				<div class="info"><%=request.getAttribute("info_user") %></div>
			<% } %>
			
			<%if(request.getAttribute("users") != null) { %>	
			<div class="overflow-container">
			<table>
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
			<%  } %>
			</table>
			</div> 
		 <%} %>
		</div> <!-- fine section-container sezione Utenti -->
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
			<button id="btn-all-books" onClick="window.location = 'admin?books&all_books'">Mostra tutti</button>
			
			<%if(request.getAttribute("info_book") != null) { %>
				<div class="info"><%=request.getAttribute("info_book") %></div>
			<%} %>

			<button id="btn-book">Aggiungi Libro</button>     
			<button id="btn-genre">Aggiungi Genere</button>
			<button id="btn-delgenre">Cancella Genere</button>

			<script>
			$(document).ready(function() {
			    $('.dropdownFilters').selectmenu();
			});
			
			$(document).ready(function() {
				$("#new-book-form").hide();
				$("#update-book-form").hide();
				$("#btn-book").click(function() {
					$("#new-book-form").slideDown();
					$("#update-book-form").hide();
					$("#all-books-div").hide();
				});
				$("#btn-all-books").click(function() {
					$("#all-books-div").slideDown();
					$("#new-book-form").hide();
					$("#update-book-form").hide();
				});
				<%if(request.getAttribute("error") != null) { %>
					$(".overflow-container").slideDown();
				<% } %>
			});	
			</script>
			
			<%if(request.getAttribute("books") != null) { %>
			<div id="all-books-div" class="overflow-container">
			<table>	
					<tr>
						<th>Id</th>
						<th>Titolo</th>
						<th>Autore</th>
						<th>Casa Editrice</th>
						<th>Genere</th>
						<th>Quantità</th>
						<th></th>
						<th></th>
					</tr>
			<%	@SuppressWarnings("unchecked")
				ArrayList<BooksBean> books = (ArrayList<BooksBean>) request.getAttribute("books");
				for(BooksBean book : books)	{ %>
					<tr>
						<td><%=book.getBook_id() %></td>
						<td><%=book.getTitle() %></td>
						<td><% for(String s : book.getAuthors()) { %>
							     <%=s%>
							<% }%>
						</td>
						<td><% for(String s : book.getGenres()) { %>
							     <%=s%>
							<% }%>
						</td>
						<td><%=book.getPublisher() %></td>
						<td><%=book.getQuantity() %></td>
						<td>
							<form action="admin?books" method="post">
								<input type="hidden" name="edit_book" value="<%=book.getBook_id()%>">
								<button id="btn-edit" type="submit"><i style="color: #404040;" class="fas fa-pencil-alt fa-lg"></i></button>
							</form>
						</td>
						<td>
							<form action="admin?books" method="post">
								<input type="hidden" name="remove_book" value="<%=book.getBook_id()%>">
								<button id="btn-remove" type="submit"><i style="color: #e64c4c;" class="fas fa-times fa-lg"></i></button>
							</form>
						</td>
					</tr>
			<%  } %>
			</table>
			</div>
			<%}
			else if(request.getAttribute("edit_book") != null) {
				BooksBean book = (BooksBean) request.getAttribute("edit_book"); %>
			<script>
			$(document).ready(function() {
				$("#update-book-form").slideDown();
				$("#new-book-form").hide();
				$("#all-books-div").hide();
			});
			</script>
			<form id="update-book-form" method="post" class="overflow-container" onsubmit="return validateBook()" enctype="multipart/form-data">
				<div style="margin-bottom: 20px;" id="error-list" tabindex="-1"></div>
				<h3>Modifica Libro</h3>
				<div style="float:left" id="image-preview">
					<img src="<%=book.getCover() %>" alt="Nessun immagine">
				</div>
				
				<label id="btn-upload" for="image"><i class="far fa-images"></i></label>
				<input id="image" type="file" name="file" accept=".jpg, .jpeg, .png">
				<label for="title">Titolo</label>
				<input id="title" name="title" type="text" value="<%=book.getTitle() %>">
				<label for="description">Descrizione</label>
				<textarea id="description" name="description" rows="6" cols="60" value="<%=book.getDescription() %>"></textarea>
				<label for="authors">Autori</label>
				<input id="authors" name="authors" type="text" value="<%=book.getAuthors() %>">
				<label for="publisher">Casa editrice</label>
				<input id="publisher" name="publisher" type="text" value="<%=book.getPublisher() %>">
				<label for="genres">Genere</label>
				<input id="genres" name="genres" type="text" value="<%=book.getGenres() %>">
				<label for="quantity">Quantità</label>
				<input id="quantity" name="quantity" type="text" value="<%=book.getQuantity() %>">
				<button type="submit" class="save" formaction="admin?books&save_book=<%=book.getBook_id()%>"><i class="fas fa-save fa-lg"></i> Salva modifiche</button>
				<button type="reset" class="cancel"><i class="fas fa-times fa-lg"></i> Pulisci campi</button>
				<script>
					$(".cancel").click(function() {
						$("#image-preview").html('<img src="images/no_image.png" alt="Nessun immagine">');
					});
				</script>
			</form>
			<%} %>
			<form id="new-book-form" method="post" class="overflow-container" onsubmit="return validateBook()" enctype="multipart/form-data">
				<div style="margin-bottom: 20px;" id="error-list" tabindex="-1"></div>
				<h3>Inserimento Libro</h3>
				<div style="float:left" id="image-preview">
					<img src="images/no_image.png" alt="Nessun immagine">
				</div>
				
				<label id="btn-upload" for="image"><i class="far fa-images"></i></label>
				<input id="image" type="file" name="file" accept=".jpg, .jpeg, .png">
				<label for="title">Titolo</label>
				<input id="title" name="title" type="text">
				<label for="description">Descrizione</label>
				<textarea id="description" name="description" rows="6" cols="60"></textarea>
				<label for="authors">Autori</label>
				<input id="authors" name="authors" type="text">
				<label for="publisher">Casa editrice</label>
				<input id="publisher" name="publisher" type="text">
				<label for="genres">Genere</label>
				<input id="genres" name="genres" type="text">
				<label for="quantity">Quantità</label>
				<input id="quantity" name="quantity" type="text">
				<button type="submit" class="save" formaction="admin?books&new_book"><i class="fas fa-plus fa-lg"></i> Aggiungi libro</button>
				<button type="reset" class="cancel"><i class="fas fa-times fa-lg"></i> Pulisci campi</button>
				<script>
					$(".cancel").click(function() {
						$("#image-preview").html('<img src="images/no_image.png" alt="Nessun immagine">');
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
		
		</div> <!-- fine section-container sezione Libri -->
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
			
			<%if(request.getAttribute("info_card") != null) { %>
				<div class="info"><%=request.getAttribute("info_card") %></div>
			<%} %>
			
			<%if(request.getAttribute("cards") != null) { %>
			<div class="overflow-container">
			<table>		
					<tr>
						<th>Nome</th>
						<th>Cognome</th>
						<th>Email</th>
						<th>Codice fiscale</th>
						<th>Codice tessera</th>
						<th>Associata</th>
						<th></th>
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
			<%  } %>
			</table>
			</div>
		   <%} %>
			
		</div> <!-- fine section-container sezione Tessere -->
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
			
			<%if(request.getAttribute("info_booking") != null) { %>
				<div class="info"><%=request.getAttribute("info_booking") %></div>
			<%} %>
			
			<script>
			$(document).ready(function() {
			    $('.dropdownFilters').selectmenu();
			});
			</script>
			
			<%if(request.getAttribute("bookings") != null) { %>
			<div class="overflow-container">
			<table>		
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
			<%  } %>
			 </table>
			</div>
			<%} %>
			
		</div> <!-- fine section-container sezione Prenotazioni -->
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
			
			<%if(request.getAttribute("info_manager") != null) { %>
				<div class="info"><%=request.getAttribute("info_manager") %></div>
			<%} %>
			
			<script>
			$(document).ready(function() {
			    $('.dropdownFilters').selectmenu();
			});
			</script>

			<%if(request.getAttribute("managers") != null) { %>
			<div class="overflow-container">
			<table>	
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
			<%  } %>
			</table>
			</div>
			<%} %>
			
		</div> <!-- fine section-container sezione Prenotazioni -->
		
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
		
		<script>
		var file = document.getElementById("image");
		file.style.opacity = 0; /*Nascondo il pulsante di default (pulsante Sfoglia...)*/
		function validateImage() {
			if(file.files[0].type !== 'image/jpeg' && file.files[0].type !== 'image/jpg' && file.files[0].type !== 'image/png') {
				return false;
			}
			else if(file.files[0].size <= 0 || file.files[0].size > 1048576 /*1MB*/) {
				return false;
			}
			return true;
		}
		$("#image").change(function() {
			if(!validateImage())
				return false;
			var img = new Image();
			var canvas = document.createElement("canvas");
			canvas.width = 1000;
			canvas.height = 1000;
			img.onload = function() {
				var ctx = canvas.getContext("2d");
				ctx.drawImage(img, 0, 0, 1000, 1000);
			};
			img.src = URL.createObjectURL(file.files[0]);
			$("#image-preview").html(canvas);
			return true;
		});
		</script>
		
	</div> <!-- Chiusura del div "container" in cima -->
<% } /*Chiusura dell'else in cima (quello che fa iniziare il pannello di controllo)*/%>
	
<%@include file="./jsp/layout/footer.jsp" %>

</body>
</html>