package danteslibrary.control;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;

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

		
		HttpSession session = request.getSession();
		
		/*Controllo Utente già autenticato. */
		if(session.getAttribute("user") != null) {
			if(request.getParameter("edit_user") != null) {
				UsersDAO dao = new UsersDAO();
				UsersBean user = new UsersBean();
				String email = request.getParameter("email");
				String old_email = request.getParameter("old_email");
				String password = request.getParameter("password");
				String address = request.getParameter("address");
				user = dao.getUserByEmail(old_email);
				if (email != null && !email.equals("")) {
					user.setEmail(email);
				}
				if (password != null && !password.equals("")) {
					dao.updateUserPassword(email, password);
				}
				if (address != null && !address.equals("")) {
					user.setAddress(address);
				}
				try {
					dao.updateUser(user, old_email);
					request.setAttribute("info","Il tuo profilo è stato aggiornato correttamente.");
					session.setAttribute("user", user);
				} catch (SQLException e) {
					request.setAttribute("error","Impossibile aggiornare il profile. Si prega di riprovare più tardi.");
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
