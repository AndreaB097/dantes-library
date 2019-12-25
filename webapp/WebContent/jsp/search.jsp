<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.ArrayList, danteslibrary.model.*"%>
<!doctype html>
<html>
<head>
	<meta charset="UTF-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/> 
	<title>Dante's Library | Risultati ricerca</title>
	<link rel="stylesheet" href="./css/style.css"/>
	<link rel="stylesheet" href="./css/jquery-ui.css"/>
	<script src="./scripts/fontawesome.js" ></script>
	<script src="./scripts/jquery-3.4.1.min.js"></script>
	<script src="./scripts/jquery-ui.js"></script>
</head>
<body>

<%@ include file="./jsp/layout/header.jsp"%>

<div class="container">
	<% if(request.getAttribute("search") != null) {
		@SuppressWarnings("unchecked")
		ArrayList<BooksBean> rows = (ArrayList<BooksBean>) request.getAttribute("search"); %> 
		<h2>Risultati trovati per: "<%=request.getParameter("q") %>"</h2>
	 <hr>
	 <% for(int i = 0; i < rows.size(); i++) { 
	 %>
		<div class="row">
			<a href="book?id=<%=rows.get(i).getBook_id()%>">
				<img src="<%=rows.get(i).getCover()%>" />
				<strong id="book-title"><%=rows.get(i).getTitle()%></strong><br/>
				<span><b>Genere: </b>
				<%ArrayList<String> genres = rows.get(i).getGenres();
				for(int j = 0; j < genres.size(); j++) {
					if((j+1) < genres.size()) { %>
						<%=genres.get(j) + ", " %>	
					<%} else { %>
						<%=genres.get(j) %>
					<%} %>
				<%} %>
				</span>
				<span><b>Autori: </b>
				<%
				ArrayList<String> authors = rows.get(i).getAuthors();
				for(int j = 0; j < authors.size(); j++) {
					if((j+1) < authors.size()) { %>
						<%=authors.get(j) + ", " %>	
					<%} else { %>
						<%=authors.get(j) %>
					<%} %>
				<%} %>
				</span>
				<span><b>Casa Editrice: </b><%=rows.get(i).getPublisher() %></span>
			</a>
		</div>
		<hr>
		<%} %>
		
	<%} else { %>
	<h2 id="search-title">Nessun risultato trovato per: "<%=request.getParameter("q") %>".</h2>
	<%} %>
</div>

<%@ include file="./jsp/layout/footer.jsp"%>

</body>
</html>