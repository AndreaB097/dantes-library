package danteslibrary.control;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.servlet.ServletException;

import java.io.*;
import java.util.ArrayList;


import danteslibrary.dao.*;
import danteslibrary.model.*;



@WebServlet("/admin")

public class ManagerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		HttpSession session = request.getSession();
		
		/*[ FUNZIONALITÀ AMMINISTRATORE ]
		 * disponibili solo se è autenticato (admin != null)*/
		if(session.getAttribute("admin") != null) {

			/*--Sezione Utente--*/
			if(request.getParameter("keyword_users") != null && request.getParameter("keyword_users") != "") {
				String keyword = request.getParameter("keyword_users");
				/*filter puo' assumere 4 valori:
				 * - 0: nome
				 * - 1: cognome
				 * - 2: email
				 * - 3: codice fiscale */
				int filter = Integer.parseInt(request.getParameter("filter"));
				if(filter < 0 || filter > 3) {
					request.setAttribute("info", "Filtro non valido.");
					request.getRequestDispatcher("admin.jsp").forward(request, response);
					return;
				} 
				else {
					UsersDAO dao = new UsersDAO();
					ArrayList<UsersBean> users = dao.getUsersByFilter(filter, keyword);
					request.setAttribute("users", users);
				}
			}
			/* - Mostra tutti gli utenti presenti nel database*/
			else if(request.getParameter("all_users") != null) {
				UsersDAO dao = new UsersDAO();
				ArrayList<UsersBean> users = dao.getAllUsers();
				request.setAttribute("users", users);
			}
			/* - Rimozione utente dal database (data la mail in input)*/
			else if(request.getParameter("remove_user") != null) {
				UsersDAO dao = new UsersDAO();
				dao.removeUser(request.getParameter("remove_user"));
				request.setAttribute("info", request.getParameter("remove_user"));
			}
		
			/* -- Sezione Libro -- */	
				if(request.getParameter("keyword_book") != null && request.getParameter("keyword_book") != "") {
					String keyword = request.getParameter("keyword_book");
					/*filter puo' assumere 4 valori:
					 * - 0: titolo
					 * - 1: autore
					 * - 2: casa editrice
					 * - 3: Genere */
					int filter = Integer.parseInt(request.getParameter("filter"));
					if(filter < 0 || filter > 3) {
						request.setAttribute("info", "Filtro non valido.");
						request.getRequestDispatcher("admin.jsp").forward(request, response);
						return;
					} 
					else {
						BooksDAO dao_books = new BooksDAO();
						ArrayList<BooksBean> books = dao_books.getBooksByFilter(filter, keyword);
						request.setAttribute("books", books);
					}
				}
				else if(request.getParameter("all_books") != null) {
					BooksDAO dao = new BooksDAO();
					ArrayList<BooksBean> books = dao.getAllBooks();
					request.setAttribute("books", books);
				}
				else if(request.getParameter("remove_book") != null) {
					BooksDAO dao = new BooksDAO();
					dao.removeBook(request.getParameter("remove_book"));
					request.setAttribute("info", request.getParameter("remove_book"));
				}
				
				
				/* -- Sezione Tessera -- */	
				if(request.getParameter("keyword_card") != null && request.getParameter("keyword_card") != "") {
					String keyword = request.getParameter("keyword_card");
					/*filter puo' assumere 5 valori:
					 * - 0: Nome
					 * - 1: Cognome
					 * - 2: Email
					 * - 3: Codice fiscale
					 * - 4: Codice_tessera */
					int filter = Integer.parseInt(request.getParameter("filter"));
					if(filter < 0 || filter > 4) {
						request.setAttribute("info", "Filtro non valido.");
						request.getRequestDispatcher("admin.jsp").forward(request, response);
						return;
					} 
					else {
						CardsDAO dao_cards = new CardsDAO();
						ArrayList<CardsBean> cards = dao_cards.getCardsByFilter(filter, keyword);
						request.setAttribute("cards", cards);
					}
				}
				else if(request.getParameter("all_cards") != null) {
					CardsDAO dao = new CardsDAO();
					ArrayList<CardsBean> cards = dao.getAllCards();
					request.setAttribute("cards", cards);
				}
				else if(request.getParameter("remove_card") != null) {
					CardsDAO dao = new CardsDAO();
					dao.removeCard(request.getParameter("remove_card"));
					request.setAttribute("info", request.getParameter("remove_card"));
				}
			
				/* -- Sezione Prenotazione -- */	
				if(request.getParameter("keyword_booking") != null && request.getParameter("keyword_booking") != "") {
					String keyword = request.getParameter("keyword_booking");
					/*filter puo' assumere 8 valori:
					 * - 0: Codice prenotazione
					 * - 1: Id Libro
					 * - 2: Data inizio
					 * - 3: Data fine
					 * - 4: Stato
					 * - 5: Email
					 * - 6: Codice fiscale
					 * - 7: Codice tessera
					 *  */
					int filter = Integer.parseInt(request.getParameter("filter"));
					if(filter < 0 || filter > 7) {
						request.setAttribute("info", "Filtro non valido.");
						request.getRequestDispatcher("admin.jsp").forward(request, response);
						return;
					} 
					else {
						BookingsDAO dao_bookings = new BookingsDAO();
						ArrayList<BookingsBean> bookings = dao_bookings.getBookingsByFilter(filter, keyword);
						request.setAttribute("bookings", bookings);
					}
				}
				else if(request.getParameter("all_bookings") != null) {
					BookingsDAO dao = new BookingsDAO();
					ArrayList<BookingsBean> bookings = dao.getAllBookings();
					request.setAttribute("bookings", bookings);
				}
				else if(request.getParameter("remove_booking") != null) {
					BookingsDAO dao = new BookingsDAO();
					dao.removeBooking(request.getParameter("remove_booking"));
					request.setAttribute("info", request.getParameter("remove_booking"));
				}	
				
				/*--Sezione Gestori--*/
				if(request.getParameter("keyword_manager") != null && request.getParameter("keyword_manager") != "") {
					String keyword = request.getParameter("keyword_manager");
					/*filter puo' assumere 4 valori:
					 * - 0: Email
					 * - 1: Nome
					 * - 2: Cognome
					 * - 3: Ruolo */
					int filter = Integer.parseInt(request.getParameter("filter"));
					if(filter < 0 || filter > 3) {
						request.setAttribute("info", "Filtro non valido.");
						request.getRequestDispatcher("admin.jsp").forward(request, response);
						return;
					} 
					else {
						ManagersDAO dao = new ManagersDAO();
						ArrayList<ManagersBean> managers = dao.getManagersByFilter(filter, keyword);
						request.setAttribute("managers", managers);
					}
				}
				/* - Mostra tutti i gestori presenti nel database*/
				else if(request.getParameter("all_managers") != null) {
					ManagersDAO dao = new ManagersDAO();
					ArrayList<ManagersBean> managers = dao.getAllManagers();
					request.setAttribute("managers", managers);
				}
				else if(request.getParameter("remove_manager") != null) {
					ManagersDAO dao = new ManagersDAO();
					dao.removeManager(request.getParameter("remove_manager"));
					request.setAttribute("info", request.getParameter("remove_manager"));
				}
				
				
			request.getRequestDispatcher("admin.jsp").forward(request, response);
			return;
			
			
		}
		
		
		
		if(email == null || password == null) {
			response.sendRedirect("admin.jsp");
			return;
		}
		
		/*Autenticazione*/
		else {
			ManagersDAO dao = new ManagersDAO();
			ManagersBean admin = dao.login(email, password);
			
			if(admin != null) {
				session.setAttribute("admin", admin);
				session.removeAttribute("user"); /*Distruggo la sessione per l'utente(nel caso in cui sia collegato)*/
			}
			else {
				request.setAttribute("login_error", true);
				request.getRequestDispatcher("admin.jsp").forward(request, response);
				return;
			}
		}
		/*Se l'autenticazione va a buon fine, l'utente viene reindirizzato all'
		 * homepage*/
		response.sendRedirect("admin.jsp");
		return;
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}