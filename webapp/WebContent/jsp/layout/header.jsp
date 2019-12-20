<header>
  <div class="container">
    <a href="./index.jsp"><img id="logo" title="HOMEPAGE" src="./images/logo.png" /></a>
	<nav id="menu">
	<% if(session.getAttribute("user") != null || session.getAttribute("admin") != null) {%>
		<a href="./logout.jsp" title="LOGOUT"><i class="fas fa-sign-out-alt fa-lg"></i></a>
	<%}%>
	  <a class="menu-ui" href="./contacts.jsp" title="CONTATTI"><i class="fas fa-question-circle fa-lg"></i></a>
	  <% if(session.getAttribute("user") != null) {%>
		<a href="./profile.jsp" title="AREA UTENTE"><i class="fas fa-user fa-lg"></i><span>${user.name}</span></a>
	<%} else { %>
	  <a href="./login.jsp" title="ACCEDI"><i class="fas fa-user fa-lg"></i></a>
	<%} %>
	  <a id="search-icon" type="submit" title="CERCA"><i class="fas fa-search fa-lg"></i></a>
	  <form id="search" action="./search" onsubmit="return cerca()">
		<input type="search" name="q" placeholder="Ricerca per titolo, autore, genere, ..."/>
	  </form>
	
	<script>
	$("#search input").hide();
	/*Se al caricamento della pagina l'input non e' vuoto, la barra di ricerca deve rimanere larga*/
	$(document).ready(function() {
		if($("#search input").val() != "") {
			$("#search input").animate({width : "100%"}, 500, function() {
				$("#search input").show();
			});
		}
	});
	/*Se l'utente clicca l'icona per la ricerca, la barra si estende (o si restringe se era gia'  estesa).
	Ma, se all'interno della barra l'utente non scrive niente e clicca di nuovo sull'icona, non succede niente.
	Invece, se l'utente ha scritto qualcosa, allora il submit andra'  a buon fine (cerca() restituisce true) e verra' 
	reindirizzato alla jsp dedicata alla ricerca dei libri.*/
	$("#search-icon").click(function() {
	
		if($("#search input").val() == "" && $("#search input").is(":hidden")) {
			$("#search input").show();
			$("#search input").animate({width : "100%"}, 500);
		}
		else if($("#search input").val() == "" && $("#search input").is(":visible")) {
			$("#search input").animate({width : "0%"}, 500, function() {
				$("#search input").hide();
			});

		}
		else {
			$("#search").submit();	
		}
	});

	function cerca() {
		if($("#search input").val() != "" || $("#search-responsive input").val() != "") {
			return true;
		}
		else {
			return false;
		}
	}
	</script>
	</nav>
	
	<nav id="menu-responsive" class="topnav">
	<% if(session.getAttribute("user") != null) {%>
		<a href="./profile.jsp" class="hide-tooltip"><i class="fas fa-user fa-lg"></i>&nbsp;&nbsp;&nbsp;${user.name}</a>
	<%} else { %>
	  <a href="./login.jsp"><i class="fas fa-user fa-lg"></i>&nbsp;&nbsp;&nbsp;Accedi</a>
	<% } %>
	  <a href="./contacts.jsp"><i class="fas fa-question-circle fa-lg"></i>&nbsp;&nbsp;&nbsp;Contatti</a>
	<form id="search-responsive" action="./search" onsubmit="return cerca()">
		<input type="search" name="q" placeholder="Cerca un libro..." />
		<button type="submit"><i class="fas fa-search fa-lg"></i></button>
	</form>

	<script>
	function showMenu() {
		var x = document.getElementById("menu-responsive");
		if (x.className === "topnav") {
			x.className += " responsive";
		} else {
			x.className = "topnav";
		}
	}
	</script>
	<script>
	$( function() {
    	$( "a" ).tooltip({
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
      		}
    	});

 	});
	</script>
    <a class="menu-icon" onclick="showMenu()"><img src="./images/menu-icon.png" /></a>
  </nav>
	
  </div>
</header>