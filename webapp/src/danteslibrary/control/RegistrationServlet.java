package danteslibrary.control;

import javax.servlet.http.*;

import danteslibrary.dao.UsersDAO;

import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;
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
		
		String email = request.getParameter("email");
		if(UsersDAO.checkEmail(email) == false) {
			//TO DO: MESSAGGIO D'ERRORE
		}
		String password = request.getParameter("password");
		String name = request.getParameter("name");
		String surname = request.getParameter("surname");
		String codice_fiscale = request.getParameter("codice_fiscale");
		String address = request.getParameter("address");
		
		
		/*Controllo se esistono i parametri*/
		if(email == null || password == null || name == null || surname == null || codice_fiscale == null ||  address == null) {
			response.sendError(404);
			return;
		}
		
		/*Registrazione (inserimento dati nel DB)*/
		else {
			UsersDAO dao = new UsersDAO();
			UsersBean user = new UsersBean();
			
			/*Riempiamo il bean*/
			user.setEmail(email);
			user.setName(name);
			user.setSurname(surname);
			user.setPassword(password);
			user.setCodice_fiscale(codice_fiscale);
			user.setAddress(address);
			/*Inserimento dati*/
			dao.register(user);
			
			/*Autenticazione*/
			session.setAttribute("user", dao.login(email, password)); 
			request.getRequestDispatcher("index.jsp").forward(request, response);
		}
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
