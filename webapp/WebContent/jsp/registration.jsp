<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" import="java.util.Calendar"%>

<!doctype html>
<html>
<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/> 
	<title>Dante's Library | Registrazione</title>
	<link rel="stylesheet" href="./css/style.css"/>
	<link rel="stylesheet" href="./css/jquery-ui.css">
	<script src="./scripts/fontawesome.js"></script>
	<script src="./scripts/jquery-3.4.1.min.js"></script>
	<script src="./scripts/jquery-ui.js"></script>
</head>
<body>

<%@ include file="./jsp/layout/header.jsp"%>

	
<div id="form-container">
<h2>Creazione account</h2>

<div id="error-list" tabindex="-1"></div>

<form id="sign-form" class="box" action="register" method="post">
  <small>(Attenzione! TUTTI i campi sono obbligatori.)</small>
  <div class="col-50">
    <label for="name">Nome</label>				
		<input id="name" type="text" name="name" placeholder="Inserisci il tuo nome"/>
	</div>
	<div class="col-50">
	  <label for="surname">Cognome</label>
    <input id="surname" type="text" name="surname" placeholder="Inserisci il tuo cognome"/>
	</div>
	<label for="email">Email</label>
  <input id="email" type="text" name="email" placeholder="Inserisci il tuo indirizzo email (es. mariorossi@gmail.com)"/>
	<label for="password">Password</label>
	<input id="password" type="password" name="password" placeholder="Inserisci una password"
		title="La password deve essere lunga almeno 6 caratteri e deve contenere almeno un numero." />
	<label for="repeat">Ripeti password</label>
	<input id="repeat" type="password" name="password" placeholder="Conferma password" />
	  <label for="codice_fiscale">Codice fiscale</label>
	  <input id="codice_fiscale" type="text" name="codice_fiscale" placeholder="Inserisci il tuo codice fiscale"/>
		<label for="address">Indirizzo</label>
		<input id="address" type="text" name="address" placeholder="Via Roma 10" />
	<br><br>
	
	<button type="submit">Conferma</button>
</form>
</div>	


<%@ include file="./jsp/layout/footer.jsp"%>

</body>
</html>