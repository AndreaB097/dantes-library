<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!doctype html>
<html>
<head>
<%@include file="./jsp/layout/head.jsp" %>
	<title>Dante's Library | Homepage</title>
</head>
<body>

<%@include file="./jsp/layout/navbar.jsp" %>
	
<section id="genres" class="home-container">
	<div class="centered-text">	
		<strong>Sfoglia libri in base al genere </strong>
		<p>Azione, Avventura, Drammatico, ...</p>
		<button onclick="window.location='book?list'">Lista Generi</button>
	</div>
</section>
<hr class="section-divider"/>
<section id="random" class="home-container">
	<div class="centered-text">	
		<p>Esplora un libro scelto in maniera casuale...</p>
		<button onclick="window.location='book?random'">Scegli per me</button>
	</div>
</section>

<%@include file="./jsp/layout/footer.jsp" %>

</body>
</html>