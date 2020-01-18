package danteslibrary.controller;

import javax.servlet.http.*;

import danteslibrary.dao.CustomersDAO;

import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

import danteslibrary.model.*;
import danteslibrary.util.InputChecker;

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
	private CustomersDAO customersDAO = new CustomersDAO();

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		
		/*Controllo se il Cliente è già autenticato*/
		if(session.getAttribute("customer") != null) {
			response.sendRedirect("index.jsp");
			return;
		}
		
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String name = request.getParameter("name");
		String surname = request.getParameter("surname");
		String codice_fiscale = request.getParameter("codice_fiscale");
		String address = request.getParameter("address");
		
		/*Controllo se e' stata fatta una richiesta con Ajax*/
		if("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			/*Risposta JSON a registration.jsp per controllare se l'email e' gia' presente nel sistema.
			 * Se mi viene restituito true, vuol dire che il cliente non può 
			 * registrarsi*/
			if(email != null && customersDAO.checkExistingEmail(email) == true) {
				response.setContentType("application/json");
				PrintWriter pw = response.getWriter();
				pw.write("true");
				pw.close();
			}
			/*Controllo se il codice fiscale e' gia' presente nel sistema.
			 * Se mi viene restituito true, vuol dire che il cliente non può 
			 * registrarsi*/
			if(codice_fiscale != null && customersDAO.checkExistingCodice_fiscale(codice_fiscale) == true) {
				response.setContentType("application/json");
				PrintWriter pw = response.getWriter();
				pw.write("true");
				pw.close();
			}
			return;
		}
		
		/*Controllo se sono stati inviati tutti i parametri 
		 * (se tutto il form e'stato compilato)*/
		if(email.equals("") || password.equals("") || name.equals("") || surname.equals("") || codice_fiscale.equals("") ||  address.equals("")
			|| email == null || password == null || name == null || surname == null || codice_fiscale == null || address == null) {
			request.setAttribute("error", "Non tutti i campi sono stati compilati.");
			request.getRequestDispatcher("registration.jsp").forward(request, response);
			return;
		}
		
		/*Controllo formato email*/
        if(!InputChecker.checkEmail(email)) {
        	request.setAttribute("error", "Indirizzo email non valido. Lunghezza massima 100 caratteri.");
    		request.getRequestDispatcher("registration.jsp").forward(request, response);
    		return;
        }
        else if (customersDAO.checkExistingEmail(email)) {
        	request.setAttribute("error", "Questo indirizzo email è già in uso.");
    		request.getRequestDispatcher("registration.jsp").forward(request, response);
    		return;
        }
        
		/*Controllo formato codice fiscale*/
        if(!InputChecker.checkCodice_fiscale(codice_fiscale)) {
        		request.setAttribute("error", "Inserire un codice fiscale valido.");
        		request.getRequestDispatcher("registration.jsp").forward(request, response);
        		return;
        }
        else if (customersDAO.checkExistingCodice_fiscale(codice_fiscale)) {
        	request.setAttribute("error", "Questo codice fiscale è già in uso. Se non ti risulta, per favore contatta la biblioteca.");
    		request.getRequestDispatcher("registration.jsp").forward(request, response);
    		return;
        }
        
        /*Controllo formato password*/
        if(!InputChecker.checkPassword(password)) {
        		request.setAttribute("error", "La password deve avere tra i 6 e i 20 caratteri.");
        		request.getRequestDispatcher("registration.jsp").forward(request, response);
        		return;
        }
        
        /*Controllo formato nome*/
        if(!InputChecker.checkName(name)) {
        		request.setAttribute("error", "Il nome può contenere solo lettere. Lunghezza massima: 30 caratteri.");
        		request.getRequestDispatcher("registration.jsp").forward(request, response);
        		return;
        }
        
        /*Controllo formato cognome*/
        if(!InputChecker.checkSurname(surname)) {
        		request.setAttribute("error", "Il cognome può contenere solo lettere. Lunghezza massima: 30 caratteri.");
        		request.getRequestDispatcher("registration.jsp").forward(request, response);
        		return;
        }
        
        /*Controllo formato indirizzo*/
        if(!InputChecker.checkAddress(address)) {
        		request.setAttribute("error", "L'indirizzo non è compilato correttamente.");
        		request.getRequestDispatcher("registration.jsp").forward(request, response);
        		return;
        }
        
       
		/**************************************************
		 * Registrazione - Passo 1: Registrazione account *
		 * ************************************************/
		/*Il form e' stato compilato correttamente.
		 * Creo quindi il bean del nuovo cliente e lo reindirizzo in card.jsp per 
		 * l'associazione della tessera (obbligatoria per prenotare i libri)*/
		CustomersBean customer = new CustomersBean();
		
		/*Riempiamo il bean, lo inseriamo nella richiesta e inoltriamo il
		 * tutto alla servlet CardServlet per il passo 2 della registrazione*/
		customer.setEmail(email);
		customer.setName(name);
		customer.setSurname(surname);
		customer.setPassword(password);
		customer.setCodice_fiscale(codice_fiscale);
		customer.setAddress(address);
		session.setAttribute("customer_incomplete", customer);
		request.getRequestDispatcher("card.jsp").forward(request, response);
		return;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
