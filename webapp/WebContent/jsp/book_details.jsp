<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"
import="java.util.ArrayList, danteslibrary.model.BooksBean, java.time.*, 
java.time.format.*, java.util.Locale"%>

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
				<% if(request.getAttribute("error") != null) { %>
					<div class="error"><%=request.getAttribute("error") %></div>
				<% } %>
			<%int current_day = LocalDate.now().getDayOfMonth();
			  int current_month = LocalDate.now().getMonthValue();
			  int current_year = LocalDate.now().getYear(); %>
			<form action="booking" method="post">
				<input type="hidden" name="book_id" value="<%=request.getParameter("id")%>">
			<label for="start-date">Data inizio prestito</label> 
			<div id="start-date">
				<select class="dropdown" name="start_day">
  					<%for(int i = 1; i <= 31; i++) { 
  						if(i == current_day) { %>
  							<option value="<%=i%>" selected><%=i %></option>
  					  <%} 
  						else {%>
  					 		<option value="<%=i%>"><%=i %></option>
  				  <%   }
  					}%>
				</select>
				<select class="dropdown" name="start_month">
  					<%for(int i = 1; i <= 12; i++) {
  					  	if(i < current_month && i != (current_month % 12) +1) { %>
  							<option value="<%=i%>" disabled><%=Month.of(i).getDisplayName(TextStyle.FULL, Locale.ITALIAN)%></option>
  					<%	}
  					  	else if(i == current_month) {%>
  				      	<option value="<%=i%>" selected><%=Month.of(i).getDisplayName(TextStyle.FULL, Locale.ITALIAN)%></option>
  				    <%  }
  					  	else { %>
  					  	<option value="<%=i%>"><%=Month.of(i).getDisplayName(TextStyle.FULL, Locale.ITALIAN)%></option>
  					<%  }
  					}%>
				</select>
				<select class="dropdown" name="start_year">
  					<option value="<%=LocalDate.now().getYear() %>"><%=LocalDate.now().getYear() %></option>
  					<option value="<%=LocalDate.now().getYear() + 1 %>"><%=LocalDate.now().getYear() + 1%></option>
				</select>
			</div>
			<label for="end-date">Data fine prestito</label>
			<div id="end-date">
				<select class="dropdown" name="end_day">
					<%for(int i = 1; i <= 31; i++) { 
						if(i == current_day) { %>
							<option value="<%=i%>" selected><%=i %></option>
					  <%} 
						else {%>
					 		<option value="<%=i%>"><%=i %></option>
				    <%  }
					}%>
				</select>
				<select class="dropdown" name="end_month">
					<%for(int i = 1; i <= 12; i++) {
					  	if(i < current_month && i != (current_month % 12) +1) { %>
							<option value="<%=i%>" disabled><%=Month.of(i).getDisplayName(TextStyle.FULL, Locale.ITALIAN)%></option>
					<%	}
					  	else if(i == current_month) {%>
				      	<option value="<%=i%>" selected><%=Month.of(i).getDisplayName(TextStyle.FULL, Locale.ITALIAN)%></option>
				    <%  }
					  	else { %>
					  	<option value="<%=i%>"><%=Month.of(i).getDisplayName(TextStyle.FULL, Locale.ITALIAN)%></option>
					<%  }
					}%>
				</select>
				<select class="dropdown" name="end_year">
	 				<option value="<%=LocalDate.now().getYear() %>"><%=LocalDate.now().getYear() %></option>
	 				<option value="<%=LocalDate.now().getYear() + 1 %>"><%=LocalDate.now().getYear() + 1 %></option>
				</select>
			</div>
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