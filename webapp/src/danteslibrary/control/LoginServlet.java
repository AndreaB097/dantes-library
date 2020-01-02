package danteslibrary.control;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;

import danteslibrary.model.UsersBean;
import danteslibrary.model.BookingsBean;
import danteslibrary.model.CardsBean;
import danteslibrary.dao.UsersDAO;
import danteslibrary.dao.BookingsDAO;
import danteslibrary.dao.CardsDAO;
import java.util.ArrayList;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		HttpSession session = request.getSession();
		
		/*Controllo Utente gi� autenticato. */
		if(session.getAttribute("user") != null) {
			response.sendRedirect("profile.jsp");
			return;
		}
		
		if(email == null || password == null) {
			response.sendRedirect("login.jsp");
			return;
		}
		
		/*Autenticazione*/
		else {
			UsersDAO udao = new UsersDAO();
			CardsDAO cdao = new CardsDAO();
			BookingsDAO bdao = new BookingsDAO();
			UsersBean user = udao.login(email, password);		
			if(user != null) {
				ArrayList<BookingsBean> bookings = bdao.getUserBookings(email);
				if(bookings != null)
					session.setAttribute("bookings", bookings);
				CardsBean card = cdao.getCardByEmail(email);
				if(card != null)
					session.setAttribute("card", card);
				session.setAttribute("user", user);
				session.removeAttribute("admin"); /*Distruggo la sessione dell'admin nel caso
													in cui sia collegato*/
			}
			else {
				request.setAttribute("error", "Indirizzo e-mail o password non validi.");
				request.getRequestDispatcher("login.jsp").forward(request, response);
				return;
			}
		}
		
		/*Se l'autenticazione va a buon fine, l'utente viene reindirizzato nella
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
