package danteslibrary.control;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;
import danteslibrary.dao.BooksDAO;

@WebServlet("/book")
public class BookPageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String book_code = request.getParameter("id");
		if(book_code == null || book_code.equals("")) {
			response.sendError(404);
			return;
		}
		BooksDAO dao = new BooksDAO();
		request.setAttribute("book", dao.findProductById(book_code));
		request.getRequestDispatcher("book_details.jsp").forward(request, response);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
