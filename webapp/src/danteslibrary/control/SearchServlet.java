package danteslibrary.control;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;
import danteslibrary.dao.BooksDAO;
import danteslibrary.model.BooksBean;
import java.util.ArrayList;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		int filter = Integer.parseInt(request.getParameter("filter"));
		String query = request.getParameter("q");
		
		/*Controllo se l'utente ha inserito correttamente filtro e keyword*/
		if(filter < 0 || filter > 4 || query.equals("") || query == null) {
			request.setAttribute("error", "Assicurati di aver selezionato un filtro di ricerca.");
			request.getRequestDispatcher(request.getHeader("referer")).forward(request, response);
			return;
		}
		
		BooksDAO dao = new BooksDAO();
		ArrayList<BooksBean> bean = dao.getBooksByFilter(filter, query);
		request.setAttribute("search", bean);
		request.getRequestDispatcher("search.jsp").include(request, response);
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
