<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="danteslibrary.model.LibraryBean"%>
<header>
  <div class="container">
    <a id="logo" href="./index.jsp"><img title="HOMEPAGE" src="${applicationScope.library.logo}" /><span> ${applicationScope.library.name}</span></a>
	<nav id="menu">
	<% if(session.getAttribute("user") != null || session.getAttribute("admin") != null) {%>
		<a href="./logout.jsp" title="LOGOUT"><i class="fas fa-sign-out-alt fa-lg"></i></a>
	<%}%>
	  <a class="menu-ui" href="./contacts.jsp" title="CONTATTI"><i class="fas fa-question-circle fa-lg"></i></a>
	  <% if(session.getAttribute("user") != null) {%>
		<a href="./profile.jsp" title="AREA UTENTE"><i class="fas fa-user fa-lg"></i><span>${user.name}</span></a>
	  <%} else if(session.getAttribute("admin") != null) { %>
	  	<script>
			$(".menu-ui").hide();
		</script>
		<a href="./admin.jsp" title="PANNELLO DI CONTROLLO"><i class="fas fa-user-shield fa-lg"></i><span>${admin.name}</span></a>
		<%} else {%>
			<a href="./login.jsp" title="ACCEDI"><i class="fas fa-user fa-lg"></i></a>
		<%} %>
	  <a id="search-icon" type="submit" title="CERCA"><i class="fas fa-search fa-lg"></i></a>
	  <form id="search" action="./search">
			<select id="search-filters" class="dropdownFilters" name="filter">
  				<option value="0">Titolo</option>
  				<option value="1">Autore</option>
  				<option value="2">Casa Editrice</option>
  				<option value="3">Genere</option>
			</select>
		<input type="search" name="q" placeholder="Seleziona il filtro ed effettua la ricerca"/>
	  </form>
	
	<script>
	/*Se al caricamento della pagina l'input non e' vuoto, la barra di ricerca deve rimanere larga*/
	$("#search input").hide();
	$("#search-filters").hide();
	$(document).ready(function() {
		$('.dropdownFilters').selectmenu();
		$("#search-filters-button").hide();
		if($("#search input").val() != "") {
			$("#search input").animate({width : "68%"}, 500, function() {
				$("#search input").show();
			});
			$("#search-filters-button").animate({width : "130px"}, 500, function() {
				$("#search-filters-button").show();
			});
		}
	});
	/*Se l'utente clicca l'icona per la ricerca, la barra si estende (o si restringe se era gia' estesa).
	Ma, se all'interno della barra l'utente non scrive niente e clicca di nuovo sull'icona, non succede niente.
	Invece, se l'utente ha scritto qualcosa, allora il submit andra'  a buon fine (cerca() restituisce true) e verra' 
	reindirizzato alla jsp dedicata alla ricerca dei libri.*/
	$("#search-icon").click(function() {
	
		if($("#search input").val() == "" && $("#search input").is(":hidden")) {
			$("#search input, #search-filters-button").show();
			$("#search input").animate({width : "68%"}, 500);
			$("#search-filters-button").animate({width : "130px"}, 500);
		}
		else if($("#search input").val() == "" && $("#search input").is(":visible")) {
			$("#search input, #search-filters-button").animate({width : "1px"}, 500, function() {
				$("#search input, #search-filters-button").hide();
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
	<%} else if(session.getAttribute("admin") != null) { %>
		<a href="./admin.jsp" class="hide-tooltip"><i class="fas fa-user-shield fa-lg"></i>&nbsp;&nbsp;&nbsp;${admin.name}</a>
	<%} else {%>
		<a href="./login.jsp"><i class="fas fa-user fa-lg"></i>&nbsp;&nbsp;&nbsp;Accedi</a>
	<%} %>
	<a href="./contacts.jsp"><i class="fas fa-question-circle fa-lg"></i>&nbsp;&nbsp;&nbsp;Contatti</a>
	<% if(session.getAttribute("user") != null || session.getAttribute("admin") != null) {%>
		<a href="./logout.jsp"><i class="fas fa-sign-out-alt fa-lg"></i>&nbsp;&nbsp;&nbsp;Logout</a>
	<%}%>
	<form id="search-responsive" action="./search">
		<p>Ricerca per  <select id="search-filters-responsive" class="dropdownFilters" name="filter">
			<option value="0">Titolo</option>
			<option value="1">Autore</option>
			<option value="2">Casa Editrice</option>
			<option value="3">Genere</option>
		</select>
		</p>
		<input type="search" name="q" placeholder="Seleziona il filtro ed effettua la ricerca" />
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