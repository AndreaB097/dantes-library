package danteslibrary.control;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.IOException;

import danteslibrary.model.LibraryBean;
import danteslibrary.dao.LibraryDAO;

@WebServlet("/library")
public class LibraryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		HttpSession session = request.getSession();
		
		LibraryDAO dao = new LibraryDAO();
		LibraryBean library = dao.getLibraryInfo();
		session.setAttribute("library", library);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}