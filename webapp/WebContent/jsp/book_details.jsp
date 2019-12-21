<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"
import="java.util.ArrayList, danteslibrary.model.BooksBean"%>

<!doctype html>
<html>
<head>
	<meta charset="UTF-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<title>Dante's Library | Dettagli libro</title>
	<link rel="stylesheet" href="./css/style.css"/>
	<link rel="stylesheet" href="./css/jquery-ui.css"/>
	<script src="./scripts/fontawesome.js"></script>
	<script src="./scripts/jquery-3.4.1.min.js"></script>
	<script src="./scripts/jquery-ui.js"></script>
</head>
<body>

<%@ include file="./jsp/layout/header.jsp" %>
	
<div class="container">
	<div class="book-container">
	<%BooksBean book = (BooksBean) request.getAttribute("book"); %>
		<div class="cover">
			<img src="<%=book.getCover() %>" alt="Impossibile caricare l'immagine">
		</div>
		<div class="details">
			<h1><%=book.getTitle() %></h1>
			<p><strong>Genere: </strong>
			<%for(String s : book.getGenres()) {%>
				<%=s %>
			<%} %>
			</p>
			<p><strong>Autori: </strong>
			<%for(String s : book.getAuthors()) {%>
				<%=s %>
			<%} %>
			</p>
			<%if(book.getQuantity() > 0) {%>
			<p style="color: #0cc481"><strong>Disponibile</strong></p>
			<button>Prenota</button>
			<%} else {%>
			<p style="color: #eb4034"><strong>Non Disponibile</strong></p>
			<%} %>
		</div>
	</div>
</div>
	
<%@ include file="./jsp/layout/footer.jsp" %>

</body>
</html>