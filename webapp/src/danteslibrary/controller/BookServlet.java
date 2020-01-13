package danteslibrary.controller;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;

import danteslibrary.dao.BooksDAO;
import danteslibrary.model.BooksBean;

/**
 * Classe che riceve richieste GET e POST riguardanti le ricerche di Libri e che
 * produce output da inviare come risposta.
 * @author Andrea Buongusto
 * @author Marco Salierno
 *
 */
@WebServlet("/book")
public class BookServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		BooksDAO dao = new BooksDAO();
		String book_id = request.getParameter("id");
		
		if(book_id != null && !book_id.equals("")) {
			request.setAttribute("book", dao.getBookById(Integer.parseInt(book_id)));
			request.getRequestDispatcher("book_details.jsp").forward(request, response);
			return;
		}
		/*Preleva 10 libri per ogni genere e li mette in un 
		 * hashmap(genre, libri[]) + redirect a genre.jsp per la visualizzazione*/
		else if(request.getParameter("list") != null) {
			request.setAttribute("list", dao.getBookList());
			request.getRequestDispatcher("genres.jsp").forward(request, response);
			return;
		}
		else if(request.getParameter("random") != null) {
			int id = dao.getRandomBookId();
			if(id != 0) {
				response.sendRedirect("book?id=" + id);
				return;
			}
			else {
				request.setAttribute("error", "Servizio al momento non disponibile. Riprovare più tardi.");
				request.getRequestDispatcher("index.jsp").forward(request, response);
				return;
			}
		}
		else if(request.getParameter("search") != null) {
			int filter = Integer.parseInt(request.getParameter("filter"));
			String query = request.getParameter("query");
			
			/*Controllo se l'utente ha inserito correttamente filtro e keyword*/
			if(filter < 0 || filter > 4 || query.equals("") || query == null) {
				request.setAttribute("error", "Assicurati di aver selezionato un filtro di ricerca.");
				request.getRequestDispatcher(request.getHeader("referer")).forward(request, response);
				return;
			}
			
			ArrayList<BooksBean> bean = dao.getBooksByFilter(filter, query);
			request.setAttribute("search", bean);
			request.getRequestDispatcher("search.jsp").include(request, response);
		}
		else {
			response.sendError(404);
			return;
		}
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
