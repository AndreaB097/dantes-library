package danteslibrary.control;

import javax.servlet.http.*;

import danteslibrary.dao.UsersDAO;

import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

import danteslibrary.model.*;

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
		
		/*Controllo se l'email e' gia' presente nel sistema*/
		String email = request.getParameter("email");
		if(UsersDAO.checkEmail(email) == false) {
			response.setContentType("application/json");
			PrintWriter pw = response.getWriter();
			pw.write("false");
			pw.close();
			return;
		}
		/*Controllo se il codice fiscale e' gia' presente nel sistema*/
		String codice_fiscale = request.getParameter("codice_fiscale");
		if(UsersDAO.checkCodiceFiscale(codice_fiscale) == false) {
			response.setContentType("application/json");
			PrintWriter pw = response.getWriter();
			pw.write("false");
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
