package danteslibrary.control;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;
import danteslibrary.dao.BookingsDAO;
import danteslibrary.dao.CardsDAO;
import danteslibrary.model.UsersBean;
import danteslibrary.model.BookingsBean;
import java.time.LocalDate;
import java.util.ArrayList;

@WebServlet("/booking")
public class BookingServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession();
		
		if(request.getParameter("book_id") == null || request.getParameter("book_id").equals("")) {
			response.sendError(404);
			return;
		}
		
		if(session.getAttribute("user") == null) {
			request.getRequestDispatcher("login.jsp").forward(request, response);
			return;
		}
		
		int start_day = Integer.parseInt(request.getParameter("start_day"));
		int start_month = Integer.parseInt(request.getParameter("start_month"));
		int start_year = Integer.parseInt(request.getParameter("start_year"));
		LocalDate start_date = LocalDate.of(start_year, start_month, start_day);
		int end_day = Integer.parseInt(request.getParameter("end_day"));
		int end_month = Integer.parseInt(request.getParameter("end_month"));
		int end_year = Integer.parseInt(request.getParameter("end_year"));
		LocalDate end_date = LocalDate.of(end_year, end_month, end_day);
		UsersBean user = (UsersBean) session.getAttribute("user");
		String email = user.getEmail();
		int book_id = Integer.parseInt(request.getParameter("book_id"));
		BookingsDAO dao = new BookingsDAO();
		CardsDAO cardsDAO = new CardsDAO();
		Integer card_id = null;
		try {
			card_id = cardsDAO.getCardByEmail(email).getCard_id();
		}
		catch(NullPointerException e) {
			request.setAttribute("error", "Abbiamo rilevato un problema con la tua tessera. Per favore contatta la biblioteca.");
			request.getRequestDispatcher("book?id="+book_id).forward(request, response);
			return;
		}
		if(start_date.isAfter(end_date)) {
			request.setAttribute("error", "La data di inizio deve precedere la data di fine!");
			request.getRequestDispatcher("book?id="+book_id).forward(request, response);
			return;
		}
		/*Prenotazione avvenuta con successo*/
		if(dao.newBooking(email, start_date.toString(), end_date.toString(), "Non ancora ritirato", card_id, book_id) > 0) {
			ArrayList<BookingsBean> bookings = dao.getUserBookings(email);
			if(bookings != null)
				session.setAttribute("bookings", bookings);
			response.sendRedirect("profile.jsp");
		}
		else {
			request.setAttribute("error", "Errore imprevisto durante la prenotazione.");
			request.getRequestDispatcher(request.getHeader("referer")).forward(request, response);
			return;
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
