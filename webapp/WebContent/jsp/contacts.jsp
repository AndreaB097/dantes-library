<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!doctype html>
<html>
<head>
	<meta charset="UTF-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/> 
	<title>Dante's Library | Contatti biblioteca</title>
	<link rel="stylesheet" href="./css/style.css"/>
	<link rel="stylesheet" href="./css/jquery-ui.css">
	<script src="./scripts/fontawesome.js"></script>
	<script src="./scripts/jquery-3.4.1.min.js"></script>
</head>
<body>

<%@ include file="/jsp/layout/header.jsp" %>

<div class="container">
	<div style="padding: 10px">
		<section id="social">
			<h2>Contattaci</h2>
			<p>${library.contacts}</p>
		</section>
	</div>
</div>

<%@ include file="./jsp/layout/footer.jsp" %>

</body>
</html>