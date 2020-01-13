package danteslibrary.controller;

import javax.servlet.http.*;

import danteslibrary.dao.UsersDAO;

import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

import danteslibrary.model.*;

/**
 * Classe che riceve richieste GET e POST riguardanti il primo passo della 
 * registrazione. Al termine reindirizza su CardServlet per il completamento 
 * della registrazione.
 * @author Andrea Buongusto
 * @author Marco Salierno
 *
 */
@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		/*Controllo Utente già autenticato*/
		if(session.getAttribute("user") != null) {
			response.sendRedirect("index.jsp");
			return;
		}
		
		/*Controllo se l'email e' gia' presente nel sistema.
		 * Se mi viene restituito true, vuol dire che l'utente non può 
		 * registrarsi*/
		UsersDAO dao = new UsersDAO();
		String email = request.getParameter("email");
		if(dao.checkExistingEmail(email) == true) {
			response.setContentType("application/json");
			PrintWriter pw = response.getWriter();
			pw.write("true");
			pw.close();
			return;
		}
		/*Controllo se il codice fiscale e' gia' presente nel sistema.
		 * Se mi viene restituito true, vuol dire che l'utente non può 
		 * registrarsi*/
		String codice_fiscale = request.getParameter("codice_fiscale");
		if(dao.checkExistingCodice_fiscale(codice_fiscale) == true) {
			response.setContentType("application/json");
			PrintWriter pw = response.getWriter();
			pw.write("true");
			pw.close();
			return;
		}
		
		/**************************************************
		 * Registrazione - Passo 1: Registrazione account *
		 * ************************************************/
		String password = request.getParameter("password");
		String name = request.getParameter("name");
		String surname = request.getParameter("surname");
		String address = request.getParameter("address");
		
		/*Controllo se sono stati inviati tutti i parametri 
		 * (se tutto il form e'stato compilato)*/
		if(email == null || password == null || name == null || surname == null || codice_fiscale == null ||  address == null) {
			request.setAttribute("error", "Non tutti i campi sono stati compilati.");
			request.getRequestDispatcher("registration.jsp").forward(request, response);
			return;
		}
		else {
			/*Il form e' stato compilato (con email e codice fiscale validati).
			 * Creo quindi il bean del nuovo utente e lo reindirizzo in card.jsp per 
			 * l'associazione della tessera (obbligatoria per prenotare i libri)*/
			UsersBean user = new UsersBean();
			
			/*Riempiamo il bean, lo inseriamo nella richiesta e inoltriamo il
			 * tutto alla servlet CardServlet per il passo 2 della registrazione*/
			user.setEmail(email);
			user.setName(name);
			user.setSurname(surname);
			user.setPassword(password);
			user.setCodice_fiscale(codice_fiscale);
			user.setAddress(address);
			session.setAttribute("user_incomplete", user);
			request.getRequestDispatcher("card.jsp").forward(request, response);
			return;
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
