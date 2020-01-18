package danteslibrary.controller;

import javax.servlet.http.*;

import danteslibrary.dao.CardsDAO;
import danteslibrary.dao.CustomersDAO;
import danteslibrary.model.CustomersBean;
import danteslibrary.model.CardsBean;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Classe che riceve richieste GET e POST riguardanti il secondo passo della 
 * Registrazione di un nuovo Cliente. Qui il cliente può scegliere se richiedere 
 * una nuova tessera oppure registrarne una inserendone il codice.
 * @author Andrea Buongusto
 * @author Marco Salierno
 *
 */
@WebServlet("/card")
public class CardServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final int GIORNI_TESSERA = 5;
	private CustomersDAO customersDAO = new CustomersDAO();
	private CardsDAO cardsDAO = new CardsDAO();
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		
		/*Controllo se il cliente è già autenticato*/
		if(session.getAttribute("customer") != null) {
			response.sendRedirect("profile.jsp");
			return;
		}
		
		/*************************************************
		 * Registrazione - Passo 2: Associazione tessera *
		 * ***********************************************/
		/*Controllo se il passo 1 e' stato eseguito verificando l'esistenza 
		 *dell' attributo customer_incomplete settato in RegistrationServlet.
		 *Se non esiste, reindirizzo alla pagina di registrazione in quanto si 
		 *sta cercando di accedere a questa servlet senza aver eseguito il passo 1.*/
		if(session.getAttribute("customer_incomplete") == null) {
			response.sendRedirect("registration.jsp");
			return;
		}
		else {
			CustomersBean customer = (CustomersBean) session.getAttribute("customer_incomplete");
			/**NUOVA TESSERA**/
			if(request.getParameter("new_card") != null) {
				/*Controllo che il cliente non abbia alcuna tessera gia' associata
				 *al suo codice fiscale (null = non ha tessera, altrimenti si)*/
				if(cardsDAO.getCardByCodice_fiscale(customer.getCodice_fiscale()) == null) {
					int card_id = cardsDAO.newCard(customer.getCodice_fiscale());
					if(card_id == 0) {
						request.setAttribute("error", "Esiste già una tessera associata al codice fiscale: "
								+ customer.getCodice_fiscale());
						request.getRequestDispatcher("card.jsp").forward(request, response);
						return;
					}
					else {
						customersDAO.register(customer);
						/*Registrazione completata. Autenticazione*/
						session.removeAttribute("customer_incomplete");
						try {
							session.setAttribute("customer", customersDAO.login(customer.getEmail(), customer.getPassword()));
						}
						catch(SQLException e) {
							System.out.println("Errore Database metodo login: " + e.getMessage());
							request.setAttribute("error", "Servizio al momento non disponibile. Riprovare più tardi.");
							request.getRequestDispatcher("login.jsp").forward(request, response);
							return;
						}
						session.setAttribute("card_date", LocalDate.now().plusDays(GIORNI_TESSERA).format(DateTimeFormatter.ofPattern("d MMMM Y", Locale.ITALY)));
					}
				}
				/*Il codice fiscale del cliente risulta gia' associato a qualche carta,
				 * quindi mostro un errore*/
				else {
					request.setAttribute("error", "Esiste già una tessera associata al codice fiscale: "
							+ customer.getCodice_fiscale());
					request.getRequestDispatcher("card.jsp").forward(request, response);
					return;
				}
			}
			/**TESSERA GIA' IN POSSESSO**/
			else if(request.getParameter("card_id") != null && !request.getParameter("card_id").equals("")) {
				try {
					int card_id = Integer.parseInt(request.getParameter("card_id"));
					CardsBean card = cardsDAO.getCardById(card_id);
					/*Controllo se esiste la tessera, che risulti NON ancora associata
					 * e che abbia lo stesso codice fiscale del cliente che la sta associando*/
					if(card != null && !card.isAssociated() && card.getCodice_fiscale().equals(customer.getCodice_fiscale())) {
						customersDAO.register(customer);
						cardsDAO.associateCard(card_id);
						/*Registrazione completata. Autenticazione*/
						session.removeAttribute("customer_incomplete");
						try {
							session.setAttribute("customer", customersDAO.login(customer.getEmail(), customer.getPassword()));
						}
						catch(SQLException e) {
							System.out.println("Errore Database metodo login: " + e.getMessage());
							request.setAttribute("error", "Servizio al momento non disponibile. Riprovare più tardi.");
							request.getRequestDispatcher("login.jsp").forward(request, response);
							return;
						}
						session.setAttribute("card", cardsDAO.getCardById(card_id));
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
