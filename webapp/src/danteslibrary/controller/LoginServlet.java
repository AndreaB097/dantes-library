package danteslibrary.controller;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;

import danteslibrary.model.CustomersBean;
import danteslibrary.util.InputChecker;
import danteslibrary.model.BookingsBean;
import danteslibrary.model.CardsBean;
import danteslibrary.dao.CustomersDAO;
import danteslibrary.dao.BookingsDAO;
import danteslibrary.dao.CardsDAO;
import java.util.ArrayList;

/**
 * Classe che riceve richieste GET e POST riguardanti l’autenticazione degli 
 * Cliente e che produce output da inviare come risposta.
 * @author Andrea Buongusto
 * @author Marco Salierno
 *
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private CustomersDAO customersDAO = new CustomersDAO();

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		HttpSession session = request.getSession();
		
		/*Controllo Cliente già autenticato. */
		if(session.getAttribute("customer") != null) {
			if(request.getParameter("edit_customer") != null) {
				CustomersBean customer = new CustomersBean();
				String new_email = request.getParameter("new_email");
				String old_email = request.getParameter("old_email");
				String password = request.getParameter("password");
				String address = request.getParameter("address");

				customer = customersDAO.getCustomerByEmail(old_email);
				
				if (new_email != null && old_email != null && !new_email.equals(old_email)) {
					/*Controllo se la nuova email ha un formato valido e se è già presente nel sistema,
					 *ossia se è già in uso da qualche altro cliente.*/
			        if(!InputChecker.checkEmail(new_email)) {
			        	request.setAttribute("error", "Inserire un indirizzo email valido.");
			    		request.getRequestDispatcher("profile.jsp").forward(request, response);
			    		return;
			        }
			        else if(customersDAO.checkExistingEmail(new_email) == true) {
			        	request.setAttribute("error", "Questo indirizzo email è già in uso da un altro cliente. Sceglierne un altro.");
			    		request.getRequestDispatcher("profile.jsp").forward(request, response);
			    		return;
					}
			        else /*La nuova email è utilizzabile, quindi la sovrascrivo a quella vecchia.*/
			        	customer.setEmail(new_email);
				}

				if (password != null && !InputChecker.checkPassword(password) && !password.equals("")) {
					request.setAttribute("error", "La password può contenere solo caratteri alfanumerici e deve avere tra i 6 e i 20 caratteri.");
	        		request.getRequestDispatcher("profile.jsp").forward(request, response);
	        		return;
				}
				else if(password != null && InputChecker.checkPassword(password))
					customersDAO.updateCustomerPassword(old_email, password);
				
				if (address == null || !InputChecker.checkAddress(address)) {
					request.setAttribute("error", "L'indirizzo può contenere solo lettere e numeri e non deve essere vuoto. Lunghezza massima: 100 caratteri.");
	        		request.getRequestDispatcher("profile.jsp").forward(request, response);
	        		return;
				}
				else
					customer.setAddress(address);
				
				try {
					customersDAO.updateCustomer(customer, old_email);
					request.setAttribute("info","Il tuo profilo è stato aggiornato correttamente.");
					session.setAttribute("customer", customer);
				} catch (SQLException e) {
					request.setAttribute("error","Impossibile aggiornare il profilo. Si prega di riprovare più tardi.");
					e.printStackTrace();
				}
			
			request.getRequestDispatcher("profile.jsp").forward(request, response);
			return;	
			}
			
			else
				response.sendRedirect("profile.jsp");
			return;
		}
		
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		if(email == null || password == null) {
			response.sendRedirect("login.jsp");
			return;
		}
		
		/*Autenticazione*/
		else {
			CustomersDAO udao = new CustomersDAO();
			CardsDAO cdao = new CardsDAO();
			BookingsDAO bdao = new BookingsDAO();
			try {
				CustomersBean customer = udao.login(email, password);
				if(customer != null) {
					ArrayList<BookingsBean> bookings = bdao.getCustomerBookings(email);
					if(bookings != null)
						session.setAttribute("bookings", bookings);
					CardsBean card = cdao.getCardByEmail(email);
					if(card != null)
						session.setAttribute("card", card);
					session.setAttribute("customer", customer);
					session.removeAttribute("admin"); /*Distruggo la sessione dell'admin nel caso
														in cui sia collegato*/
				}
				else {
					request.setAttribute("error", "Indirizzo e-mail o password non validi.");
					request.getRequestDispatcher("login.jsp").forward(request, response);
					return;
				}
			}
			catch(SQLException e) {
				System.out.println("Errore Database metodo login: " + e.getMessage());
				request.setAttribute("error", "Servizio al momento non disponibile. Riprovare più tardi.");
				request.getRequestDispatcher("login.jsp").forward(request, response);
				return;
			}
		}
		
		/*Se l'autenticazione va a buon fine, il cliente viene reindirizzato nella
		 * pagina visitata precedentemente. Se non aveva visitato alcuna pagina
		 * (referer nullo) allora il redirect viene fatto all'homepage.*/
		String address;
		if(session.getAttribute("referer") != null ) {
			address = (String) session.getAttribute("referer");
			session.removeAttribute("referer");
		}
		else
			address= "index.jsp";
		
		response.sendRedirect(address);
		return;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
