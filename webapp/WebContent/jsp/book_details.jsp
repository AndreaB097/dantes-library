<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"
import="java.util.ArrayList, danteslibrary.model.BooksBean, java.time.*,
java.time.format.*, java.util.*"%>

<!doctype html>
<html>
<head>
<%@include file="./jsp/layout/head.jsp" %>
	<title>Dante's Library | Dettagli libro</title>
</head>
<body>

<%@ include file="./jsp/layout/navbar.jsp" %>
	
<div class="container">
	<div class="book-container">
	<%if(request.getAttribute("book") == null) {
		response.sendError(404);
		return;
	}
	BooksBean book = (BooksBean) request.getAttribute("book"); %>
		<div class="cover">
			<img src="<%=book.getCover() %>" alt="Impossibile caricare l'immagine" onerror="this.onerror=null; this.src='./images/no_image.png'">
		</div>
		<div class="details">
			<h1><%=book.getTitle() %></h1>
			<p><%=book.getDescription() %></p>
			<p><strong>Genere: </strong>
			<%
			ArrayList<String> genres = book.getGenres();
			if(genres != null && !genres.isEmpty())
			for(int i = 0; i < genres.size(); i++) {
				if((i+1) < genres.size()) { %>
					<%=genres.get(i) + ", " %>	
				<%} else { %>
					<%=genres.get(i) %>
				<%} %>
			<%} %>
			</p>
			<p><strong>Autori: </strong>
			<%
			ArrayList<String> authors = book.getAuthors();
			if(authors != null && !authors.isEmpty())
			for(int i = 0; i < authors.size(); i++) {
				if((i+1) < authors.size()) { %>
					<%=authors.get(i) + ", " %>	
				<%} else { %>
					<%=authors.get(i) %>
				<%} %>
			<%} %>
			</p>
			<p><strong>Editore: </strong><%=book.getPublisher() %></p>
			<%if(book.getQuantity() > 0) {%>
			<p style="color: #0cc481"><strong>Disponibile</strong></p>
				<% if(request.getAttribute("error") != null) { %>
					<div class="error"><%=request.getAttribute("error") %></div>
				<% } %>

			<form action="booking" method="post">
				<input type="hidden" name="book_id" value="<%=request.getParameter("id")%>">
			<label for="start-date">Data inizio prestito</label> 
			<input type="text" id="start-date" name="start_date" value="" required>
			<label for="end-date">Data fine prestito</label>
			<input type="text" id="end-date" name="end_date" value="" required>
			<script>
			$(document).ready(function() {
				$("#start-date, #end-date").datepicker({
					minDate: "+3d",
					maxDate: "+4m",
					autoSize: true,
					dateFormat: "d MM yy",
					monthNames: [ "Gennaio","Febbraio","Marzo","Aprile","Maggio","Giugno",
						"Luglio","Agosto","Settembre","Ottobre","Novembre","Dicembre" ],
				});
				$('#start-date').datepicker("setDate", "0");
				
				var start_date = $('#start-date').datepicker("getDate");
				var end_date = $('#end-date').datepicker("getDate");
				
				/*Se la data di fine e' stata settata, allora aggiorno l'attributo value,
				altrimenti value=""*/
				if(end_date)
					$('#end-date').attr("value", end_date.getFullYear() + "-" + (end_date.getMonth()+1) + "-" + end_date.getDate());
				
				$('#start-date').attr("value", start_date.getFullYear() + "-" + (start_date.getMonth()+1) + "-" + start_date.getDate());
				
				/*Ogni volta che cambia la data di inizio, ottengo l'oggetto Date con getDate
				e aggiorno l'attribute value di start-date.
				Inoltre, reimposto la data minima selezionabile di end-date in base a quella
				selezionata in start-date.*/
				$('#start-date').change(function() {
					start_date = $('#start-date').datepicker("getDate");
					end_date = $('#end-date').datepicker("getDate");
					$('#start-date').attr("value", start_date.getFullYear() + "-" + (start_date.getMonth()+1) + "-" + start_date.getDate());
					$('#end-date').datepicker("option", "minDate" , $("#start-date").datepicker("getDate"));
				});
				/*Ogni volta che cambia la data di fine, ottengo l'oggetto Date con getDate
				e aggiorno l'attribute value di end-date.*/
				$('#end-date').change(function() {
					end_date = $('#end-date').datepicker("getDate");
					$('#end-date').attr("value", end_date.getFullYear() + "-" + (end_date.getMonth()+1) + "-" + end_date.getDate());
				});
			});
			</script>
			<button type="submit" id="booking-btn">Prenota</button>
			</form>
			<%} else {%>
			<p style="color: #eb4034"><strong>Non Disponibile</strong></p>
			<%} %>

		</div>
	</div>
</div>

<script>
$(document).ready(function() {
   $('.dropdown').selectmenu({
   	width: '120px'
   })
});
</script>
	
<%@ include file="./jsp/layout/footer.jsp" %>

</body>
</html>