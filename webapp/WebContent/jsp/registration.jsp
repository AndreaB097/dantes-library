<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<!doctype html>
<html>
<head>
<%@include file="./jsp/layout/head.jsp" %>
	<title>Dante's Library | Registrazione</title>
</head>
<body>

<%@ include file="./jsp/layout/navbar.jsp"%>

<div class="container" style="margin-top: 15px">
	<div id="form-container">
	<h2>1. Creazione account</h2>
	
	<div id="error-list" tabindex="-1"></div>
	
	<form id="sign-form" class="box" action="register" method="post" onsubmit="return validateSubmit()">
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
		
		<button type="submit">Avanti</button>
	</form>
	</div>
</div>

<script>
var errors = [];
	function validateSubmit() {
		var mailformat = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
		var codice_fiscale_regex = /^(?:[A-Z][AEIOU][AEIOUX]|[B-DF-HJ-NP-TV-Z]{2}[A-Z]){2}(?:[\dLMNP-V]{2}(?:[A-EHLMPR-T](?:[04LQ][1-9MNP-V]|[15MR][\dLMNP-V]|[26NS][0-8LMNP-U])|[DHPS][37PT][0L]|[ACELMRT][37PT][01LM]|[AC-EHLMPR-T][26NS][9V])|(?:[02468LNQSU][048LQU]|[13579MPRTV][26NS])B[26NS][9V])(?:[A-MZ][1-9MNP-V][\dLMNP-V]{2}|[A-M][0L](?:[1-9MNP-V][\dLMNP-V]|[0L][1-9MNP-V]))[A-Z]$/i;
		
		var name = document.getElementById("name").value;
		var surname = document.getElementById("surname").value;
		var email = document.getElementById("email").value;
		var password = document.getElementById("password").value;
		var repeat = document.getElementById("repeat").value;
		var codice_fiscale = document.getElementById("codice_fiscale").value;
		var address = document.getElementById("address").value;
		
		/* COMMENTATA PER TESTING. BISOGNA RIMUOVERE I COMMENTI AL TERMINE
		if(!codice_fiscale.match(codice_fiscale_regex))
			errors.push("Inserire un codice fiscale valido.");
		*/
		if(!email.match(mailformat)) {
			errors.push("Indirizzo email non valido.");
		}
		
		/*Se mi viene restituito true, vuol dire che esiste già un account con
		questa email, quindi l'utente non si può registrare e stampo l'errore.*/
		$.ajax({
			url: "register",
			async: false,
			data: {email : email},
			type: "POST",
			dataType: "json",
			success: function(response) {
				if(response == true)
					errors.push("Questo indirizzo email è già in uso.");
			}
		});
		
		/*Se mi viene restituito true, vuol dire che esiste già un account con
		questo codice fiscale, quindi l'utente non si può registrare e stampo l'errore.*/
		$.ajax({
			url: "register",
			async: false,
			data: {codice_fiscale : codice_fiscale},
			type: "POST",
			dataType: "json",
			success: function(response) {
				if(response == true)
					errors.push("Questo codice fiscale è già in uso. Se non ti risulta, per favore contatta la biblioteca.");
			}
		});
		
		if(!name || !surname || !email || !password || !repeat || !codice_fiscale || !address) {
			errors.push("Non tutti i campi sono stati compilati.");
		}
		if((password.length < 6 || !(/\d/.test(password))) && password) {
			errors.push("La password deve essere lunga almeno 6 caratteri e deve contenere almeno un numero.");
		}
		if(password != repeat && password && repeat) {
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
<script>
$( function() {
   	$( "input" ).tooltip({
     		position: {
       		my: "center bottom-3",
       		at: "center top",
       		using: function( position, feedback ) {
				$( this ).css( position );
				$( "<div>" )
	            .addClass( "arrow" )
	            .addClass( feedback.vertical )
	            .addClass( feedback.horizontal )
	            .appendTo( this );
			}
     		},
     		tooltipClass: "form-tooltip"
   	});

	});
</script>

<%@ include file="./jsp/layout/footer.jsp"%>

</body>
</html>