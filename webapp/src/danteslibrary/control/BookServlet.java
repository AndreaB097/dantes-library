package danteslibrary.control;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;
import danteslibrary.dao.BooksDAO;

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
		else {
			response.sendError(404);
			return;
		}
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
