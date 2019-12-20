package danteslibrary.control;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;

import danteslibrary.model.UsersBean;
import danteslibrary.dao.UsersDAO;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		HttpSession session = request.getSession();
		
		/*Controllo Utente già autenticato. */
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
			UsersBean user = udao.login(email, password);		
			if(user != null) {
				session.setAttribute("user", user);
			}
			else {
				/* TODO
				 * Manda messaggio di errore. Autenticazione fallita*/
				return;
			}
		}
		
		response.sendRedirect("index.jsp");
		return;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
