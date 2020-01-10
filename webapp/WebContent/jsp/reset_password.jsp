<%@ page language="java" contentType="text/html; charset=UTF-8" 
pageEncoding="UTF-8"%>

<!doctype html>
<html>
<head>
<%@include file="./jsp/layout/head.jsp" %>
	<title>Dante's Library | Password Dimenticata</title>
</head>
<body>

<%@ include file="./jsp/layout/navbar.jsp"%>

<div class="container" style="margin-top: 75px">
	<%if(session.getAttribute("user_email") != null) { %>
		<div id="form-container">
		<h2>Reimposta la tua password</h2>
		<div id="error-list" tabindex="-1"></div>
			<form id="sign-form" class="box" method="post" onsubmit="return validateSubmit()">
				<label for="password">Password</label>
				<input id="password" type="password" name="new_password" required/>
				<label for="repeat">Ripeti Password</label>
				<input id="repeat" type="password" required/>
				<button type="submit" formaction="reset_password">Reimposta la mia password</button>
			</form>
			
			<% if(request.getAttribute("info") != null) { %>
				<div class="info"><%=request.getAttribute("info") %></div>
			<% } %>
			<% if(request.getAttribute("error") != null) { %>
				<div class="error"><%=request.getAttribute("error") %></div>
			<% } %>
	</div>
	<script>
	var errors = [];
	function validateSubmit() {
		var password = document.getElementById("password").value;
		var repeat = document.getElementById("repeat").value;
		
		if(password.length < 6 || !(/\d/.test(password))) {
			errors.push("La password deve essere lunga almeno 6 caratteri e deve contenere almeno un numero.");
		}
		if(password != repeat) {
			errors.push("Le password non corrispondono.");
		}
		
		if(errors.length != 0) {
			var errors_div = document.getElementById("error-list");
			var txt = "<ul>";
			$(errors_div).hide();
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
			return false;
		}
		
		$("#error-list").hide();
		return true;
	}
	</script>
	<%}
	else {%>
		<div id="form-container">
		<h2>Password Dimenticata</h2>
		<% if(request.getAttribute("info") != null) { %>
			<div class="info"><%=request.getAttribute("info") %></div>
		<% } %>
		<% if(request.getAttribute("error") != null) { %>
			<div class="error"><%=request.getAttribute("error") %></div>
		<% } %>
			<form id="sign-form" class="box" method="post">
				<p>Inserisci l'indirizzo email che di solito utilizzi per autenticarti.</p>
				<label for="email">Email</label>
				<input id="email" type="text" name="email" required/>
				<button type="submit" formaction="reset_password">Invia</button>
			</form>
		</div>
	<%} %>
</div>

<%@ include file="./jsp/layout/footer.jsp"%>

</body>
</html>