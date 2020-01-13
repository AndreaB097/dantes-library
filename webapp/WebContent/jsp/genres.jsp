<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.*, danteslibrary.model.BooksBean"%>

<!doctype html>
<html>
<head>
<%@include file="./jsp/layout/header.jsp" %>
	<title>Dante's Library | Lista Generi</title>
</head>
<body>

<%@include file="./jsp/layout/navbar.jsp" %>

<% 
if(request.getAttribute("list") == null) {
	response.sendRedirect("book?list");
	return;
}	
%>

<div class="container">
	<%
	@SuppressWarnings("unchecked")
	LinkedHashMap<String, ArrayList<BooksBean>> list = (LinkedHashMap<String, ArrayList<BooksBean>>) request.getAttribute("list"); 
	Set<String> genres = list.keySet();
	for(String key : genres) { %>
		
		<%ArrayList<BooksBean> books = list.get(key);
		if(books != null) { %>
		<div class="section-genre">
		<h3><%=key %></h3>
			<%for(BooksBean book : books) { %>
				<a class="card" href="book?id=<%=book.getBook_id() %>">
					<img src="<%=book.getCover() %>" alt="Impossibile caricare l'immagine" onerror="this.onerror=null; this.src='./images/no_image.png'"/>
					<span class="card-title"><%=book.getTitle() %></span>
				</a>
			<%} %>
		</div>
		<%}
	}%>
</div>

<%@include file="./jsp/layout/footer.jsp" %>

</body>
</html>