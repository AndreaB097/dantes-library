<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"
import="java.util.ArrayList, danteslibrary.model.BooksBean, java.time.*, 
java.time.format.*, java.util.Locale"%>

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
			<p><%=book.getDescription() %></p>
			<p><strong>Genere: </strong>
			<%
			ArrayList<String> genres = book.getGenres();
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
			for(int i = 0; i < authors.size(); i++) {
				if((i+1) < authors.size()) { %>
					<%=authors.get(i) + ", " %>	
				<%} else { %>
					<%=authors.get(i) %>
				<%} %>
			<%} %>
			</p>
			<%if(book.getQuantity() > 0) {%>
			<p style="color: #0cc481"><strong>Disponibile</strong></p>
			
			<p>Data inizio prestito 
			<%int day = LocalDate.now().getDayOfMonth();
			  int month = LocalDate.now().getMonthValue();
			  int year = LocalDate.now().getYear(); %>
				<select class="dropdown" name="day">
  					<%for(int i = 1; i <= 31; i++) { 
  						if(i == day) { %>
  							<option value="<%=i%>" selected><%=i %></option>
  					  <%} 
  						else {%>
  					 		<option value="<%=i%>"><%=i %></option>
  				  <%   }
  					}%>
				</select>
				<select class="dropdown" name="month">
  					<%for(int i = 1; i <= 12; i++) {
  					  	if(i < month) { %>
  							<option value="<%=month%>" disabled><%=Month.of(i).getDisplayName(TextStyle.FULL, Locale.ITALIAN)%></option>
  					<%	}
  					  	else if(i == month) {%>
  				      	<option value="<%=month%>" selected><%=Month.of(i).getDisplayName(TextStyle.FULL, Locale.ITALIAN)%></option>
  				    <%  }
  					  	else { %>
  					  	<option value="<%=month%>"><%=Month.of(i).getDisplayName(TextStyle.FULL, Locale.ITALIAN)%></option>
  					<%  }
  					}%>
				</select>
				<select class="dropdown" name="year">
  					<option value="<%=LocalDate.now().getYear() %>"><%=LocalDate.now().getYear() %></option>
				</select>
				</p>
				<p>Data fine prestito 
				<select class="dropdown" name="day">
  					<%for(int i = 1; i <= 31; i++) { 
  						if(i == day) { %>
  							<option value="<%=i%>" selected><%=i %></option>
  					  <%} 
  						else {%>
  					 		<option value="<%=i%>"><%=i %></option>
  				  <%   }
  					}%>
				</select>
				<select style="max-height: 100px" class="dropdown" name="month">
  					<%for(int i = 1; i <= 12; i++) {
  					  	if(i < month) { %>
  							<option value="<%=month%>" disabled><%=Month.of(i).getDisplayName(TextStyle.FULL, Locale.ITALIAN)%></option>
  					<%	}
  					  	else if(i == month) {%>
  				      	<option value="<%=month%>" selected><%=Month.of(i).getDisplayName(TextStyle.FULL, Locale.ITALIAN)%></option>
  				    <%  }
  					  	else { %>
  					  	<option value="<%=month%>"><%=Month.of(i).getDisplayName(TextStyle.FULL, Locale.ITALIAN)%></option>
  					<%  }
  					}%>
				</select>
				<select class="dropdown" name="year">
  					<option value="<%=LocalDate.now().getYear() %>"><%=LocalDate.now().getYear() %></option>
				</select>
				</p>
			<button>Prenota</button>
			<%} else {%>
			<p style="color: #eb4034"><strong>Non Disponibile</strong></p>
			<%} %>
		</div>
	</div>
</div>

<script>
$(document).ready(function() {
   $('.dropdown').selectmenu({
   	width: '150px'
   })
});
</script>
	
<%@ include file="./jsp/layout/footer.jsp" %>

</body>
</html>