package danteslibrary.control;

import javax.servlet.http.*;

import danteslibrary.dao.CardsDAO;
import danteslibrary.dao.UsersDAO;
import danteslibrary.model.UsersBean;
import danteslibrary.model.CardsBean;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;

@WebServlet("/card")
public class CardServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final int GIORNI_TESSERA = 5;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		
		/*Controllo Utente già autenticato*/
		if(session.getAttribute("user") != null) {
			response.sendRedirect("profile.jsp");
			return;
		}
		
		/*************************************************
		 * Registrazione - Passo 2: Associazione tessera *
		 * ***********************************************/
		/*Controllo se il passo 1 e' stato eseguito verificando l'esistenza 
		 *dell' attributo user settato in RegistrationServlet.
		 *Se non esiste, reindirizzo alla pagina di registrazione in quanto si 
		 *sta cercando di accedere a questa servlet senza aver eseguito il passo 1.*/
		if(session.getAttribute("user_incomplete") == null) {
			response.sendRedirect("registration.jsp");
			return;
		}
		else {
			UsersBean user = (UsersBean) session.getAttribute("user_incomplete");
			/**NUOVA TESSERA**/
			if(request.getParameter("new_card") != null) {
				/*Controllo che l'utente non abbia alcuna tessera gia' associata
				 *al suo codice fiscale (null = non ha tessera, altrimenti si)*/
				if(CardsDAO.getCardByCodice_fiscale(user.getCodice_fiscale()) == null) {
					int card_id = CardsDAO.newCard(user.getCodice_fiscale(), true);
					if(card_id == 0) {
						request.setAttribute("error", "Esiste già una tessera associata al codice fiscale: "
								+ user.getCodice_fiscale());
						request.getRequestDispatcher("card.jsp").forward(request, response);
						return;
					}
					else {
						UsersDAO.register(user);
						/*Registrazione completata. Autenticazione*/
						session.removeAttribute("user_incomplete");
						session.setAttribute("user", UsersDAO.login(user.getEmail(), user.getPassword()));
						session.setAttribute("card_date", LocalDate.now().plusDays(GIORNI_TESSERA).format(DateTimeFormatter.ofPattern("d MMMM Y", Locale.ITALY)));
					}
				}
				/*Il codice fiscale dell'utente risulta gia' associato a qualche carta,
				 * quindi mostro un errore*/
				else {
					request.setAttribute("error", "Esiste già una tessera associata al codice fiscale: "
							+ user.getCodice_fiscale());
					request.getRequestDispatcher("card.jsp").forward(request, response);
					return;
				}
			}
			/**TESSERA GIA' IN POSSESSO**/
			else if(request.getParameter("card_id") != null && !request.getParameter("card_id").equals("")) {
				try {
					int card_id = Integer.parseInt(request.getParameter("card_id"));
					CardsBean card = CardsDAO.getCardById(card_id);
					/*Controllo se esiste la tessera, che risulti NON ancora associata
					 * e che abbia lo stesso codice fiscale dell'utente che la sta associando*/
					if(card != null && !card.isAssociated() && card.getCodice_fiscale().equals(user.getCodice_fiscale())) {
						UsersDAO.register(user);
						CardsDAO.associateCard(card_id);
						/*Registrazione completata. Autenticazione*/
						session.removeAttribute("user_incomplete");
						session.setAttribute("user", UsersDAO.login(user.getEmail(), user.getPassword()));
						session.setAttribute("card", CardsDAO.getCardById(card_id));
					}
					else {
						request.setAttribute("error", "Questa tessera non esiste oppure è già associata a qualche cliente."
								+ " Se non ti risulta, per favore contatta la biblioteca.");
						request.getRequestDispatcher("card.jsp").forward(request, response);
						return;
					}
				}
				catch(NumberFormatException e) {
					request.setAttribute("error", "Codice tessera non valido.");
					request.getRequestDispatcher("card.jsp").forward(request, response);
					return;
				}
				
			}
		}
		response.sendRedirect("card.jsp");
		return;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
