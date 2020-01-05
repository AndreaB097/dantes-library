package danteslibrary.control;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import danteslibrary.model.LibraryBean;
import danteslibrary.dao.LibraryDAO;

@WebServlet("/library")
public class LibraryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public void init(ServletConfig config) throws ServletException {
		LibraryDAO dao = new LibraryDAO();
		LibraryBean library = dao.getLibraryInfo();
		if(library == null) {
			library = new LibraryBean();
			library.setName("Dante's Library");
			library.setLogo("./images/default_logo.png");
			library.setContacts("Problemi con il nostro software? Contattaci al"
					+ " seguente indirizzo: assistenza@danteslibrary.com");
			dao.updateLibraryInfo(library);
		}
		config.getServletContext().setAttribute("library", library);
	}

}