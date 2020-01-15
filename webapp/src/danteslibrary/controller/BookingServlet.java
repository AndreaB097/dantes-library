package danteslibrary.controller;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import danteslibrary.dao.BookingsDAO;
import danteslibrary.dao.CardsDAO;
import danteslibrary.model.UsersBean;
import danteslibrary.model.CardsBean;
import danteslibrary.model.BookingsBean;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Classe che riceve richieste GET e POST riguardanti le Prenotazioni e che 
 * produce output da inviare come risposta.
 * @author Andrea Buongusto
 * @author Marco Salierno
 *
 */
@WebServlet("/booking")
public class BookingServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		BookingsDAO bookingsDAO = new BookingsDAO();
		CardsDAO cardsDAO = new CardsDAO();
		
		HttpSession session = request.getSession();
			
		if(session.getAttribute("user") == null) {
			session.setAttribute("referer", request.getHeader("referer"));
			request.getRequestDispatcher("login.jsp").forward(request, response);
			return;
		}
		/*Annulla prenotazione*/
		else if(request.getParameter("cancel_booking") != null) {
			UsersBean user = (UsersBean) session.getAttribute("user");
			String email = user.getEmail();
			int booking_id = Integer.parseInt(request.getParameter("booking_id"));
			BookingsBean booking = bookingsDAO.getBookingById(booking_id);
			String state = "Annullata";
			/*Controllo se la prenotazione sia già stata Annullata*/
			if(booking.getState_name().equals(state)) {
				request.setAttribute("error", "Errore: Questa prenotazione è già stata annullata.");
				request.getRequestDispatcher("profile.jsp").forward(request, response);
				return;
			}
			/*Cerco di annullare la prenotazione*/
			try {
				bookingsDAO.updateBooking(booking_id, state);
				request.setAttribute("info", "La prenotazione è stata annullata con successo.");
				
			} catch(SQLException e) {
				e.printStackTrace();
				request.setAttribute("error", "Errore: l'annullamento della prenotazione non è andato buon fine");
			}
			
			ArrayList<BookingsBean> bookings = bookingsDAO.getUserBookings(email);
			if(bookings != null)
				session.setAttribute("bookings", bookings);
			request.getRequestDispatcher("profile.jsp").forward(request, response);
			return;
		}
		
		UsersBean user = (UsersBean) session.getAttribute("user");
		String email = user.getEmail();

		/*Il libro non esiste*/
		if(request.getParameter("book_id") == null || request.getParameter("book_id").equals("")) {
			response.sendError(404);
	    	return;
		}
		
		int book_id = 0;
		try {
			book_id = Integer.parseInt(request.getParameter("book_id"));
		
			SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN);
			Date tmp_date;
			LocalDate start_date, end_date;
		
			tmp_date = formatter.parse(request.getParameter("start_date"));
			start_date = tmp_date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			tmp_date = formatter.parse(request.getParameter("end_date"));
			end_date = tmp_date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			
			if(start_date.isAfter(end_date)) {
				request.setAttribute("error", "La data di inizio deve precedere la data di fine!");
				request.getRequestDispatcher("book?id="+book_id).forward(request, response);
				return;
			}
			
			CardsBean user_card = cardsDAO.getCardByEmail(email);
			Integer card_id = user_card.getCard_id();
			/*Controllo che la tessera dell'utente che sta prenotando sia associata*/
			if(!user_card.isAssociated()) {
				request.setAttribute("error", "Attenzione! La tua tessera non risulta associata, pertanto "
						+ "non puoi effettuare alcuna prenotazione. Per eventuali problemi, puoi contattare la biblioteca.");
				request.getRequestDispatcher("book?id="+book_id).forward(request, response);
				return;
			}
			/*Tutto in regola, procedo con la prenotazione. Se newBooking restituisce 0
			 * - il database non risponde
			 * - libro, tessera o utente, non esistono nel database*/
			if(bookingsDAO.newBooking(email, start_date.toString(), end_date.toString(), "Non ancora ritirato", card_id, book_id) != 0) {
				ArrayList<BookingsBean> bookings = bookingsDAO.getUserBookings(email);
				if(bookings != null)
					session.setAttribute("bookings", bookings);
				request.setAttribute("info", "Prenotazione effettuata! Controlla lo Storico Prenotazioni.");
				request.getRequestDispatcher("profile.jsp").forward(request, response);
				return;
			}
			else {
				request.setAttribute("error", "Si è verificato un errore durante la prenotazione. "
						+ "Assicurarsi che il libro sia ancora disponibile e riprovare più tardi.");
				request.getRequestDispatcher("book?id="+book_id).forward(request, response);
				return;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			request.setAttribute("error", "Formato delle date non valido.");
			request.getRequestDispatcher("book?id="+book_id).forward(request, response);
			return;
		} catch(NumberFormatException e) {
			e.printStackTrace();
			response.sendError(404);
			return;
		} catch(NullPointerException e) {
			request.setAttribute("error", "Abbiamo rilevato un problema con la tua tessera. Per favore contatta la biblioteca.");
			request.getRequestDispatcher("book?id="+book_id).forward(request, response);
			return;
		} catch(SQLException e) {
			request.setAttribute("error", "Il sistema non è al momento disponibile. Riprovare più tardi.");
			request.getRequestDispatcher("book?id="+book_id).forward(request, response);
			return;
		}
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
