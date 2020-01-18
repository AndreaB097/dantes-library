package danteslibrary.controller;

import java.io.IOException;
import org.apache.commons.text.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import danteslibrary.util.EmailUtility;
import danteslibrary.util.InputChecker;
import danteslibrary.dao.CustomersDAO;
 
/**
 * Classe che riceve richieste GET e POST per reimpostare la password di un Cliente.
 * @author Andrea Buongusto
 * @author Marco Salierno
 *
 */
@WebServlet("/reset_password")
public class ResetPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
 
    private String host;
    private String port;
    private String user;
    private String pass;
 
    public void init() {
        // Impostazioni SMTP prese da: web.xml
        ServletContext context = getServletContext();
        host = context.getInitParameter("host");
        port = context.getInitParameter("port");
        user = context.getInitParameter("user");
        pass = context.getInitParameter("pass");
    }
 
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	CustomersDAO dao = new CustomersDAO();
        String tmp_link = request.getParameter("link");
        request.getSession().removeAttribute("customer_email");
        if(tmp_link == null || tmp_link.equals("")) {
        	response.sendRedirect("reset_password.jsp");
        	return;
        }
        else {
        	String email = dao.getEmailByTemporaryLink(tmp_link);
        	if(email != null) {
        		request.getSession().setAttribute("customer_email", email);
        	}
        	else {
        		request.setAttribute("error", "Il link non è valido oppure è scaduto.");
        	}
        }
        request.getRequestDispatcher("reset_password.jsp").forward(request, response);
        return; 
    }
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	CustomersDAO dao = new CustomersDAO();
    	
    	if(request.getSession().getAttribute("customer_email") != null && request.getParameter("new_password") != null) {
    		String email = (String) request.getSession().getAttribute("customer_email");
    		String new_password = request.getParameter("new_password");
    		if(!InputChecker.checkPassword(new_password)) {
    			request.setAttribute("error", "La password può contenere solo caratteri alfanumerici e deve avere tra i 6 e i 20 caratteri.");
    			request.getRequestDispatcher("reset_password.jsp").forward(request, response);
        		return;
    		}
    		else if(dao.updateCustomerPassword(email, new_password) != 0) {
    			request.setAttribute("info", "La password è stata reimpostata correttamente.");
    			dao.deleteTemporaryLink(email);
    		}
    		else {
    			request.setAttribute("error", "Errore imprevisto. Riprovare l'operazione. Se il problema persiste, contattare la biblioteca.");
    		}
    		request.getRequestDispatcher("login.jsp").forward(request, response);
    		return;
    	}
    	
        String recipient = request.getParameter("email");
        
        if(recipient == null || !InputChecker.checkEmail(recipient)) {
        	request.setAttribute("error", "Inserisci un indirizzo email valido.");
        	request.getRequestDispatcher("reset_password.jsp").forward(request, response);
        	return;
        }
        
        /*Controllo che l'email di cui si vuole reimpostare la password sia presente nel sistema*/
        if(!dao.checkExistingEmail(recipient)) {
        	request.setAttribute("error", "L'indirizzo email inserito non risulta essere registrato al nostro sistema.");
        	request.getRequestDispatcher("reset_password.jsp").forward(request, response);
        	return;
        }
        
        char [][] pairs = {{'a', 'z'}, {'0', '9'}};
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange(pairs).build();
        String random_link = generator.generate(100);
        dao.setTemporaryLink(recipient, random_link);
        random_link = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getRequestURI() + "?link=" + random_link;
        
        String subject = "[Dante's Library] Password dimenticata";
        String content = "Clicca sul seguente link per reimpostare la tua password: " + random_link;
        
        String message = "";
 
        try {
            EmailUtility.sendEmail(host, port, user, pass,
                    recipient, subject, content);
            message = "Ti abbiamo inviato un'email per aiutarti a reimpostare la tua password. Controlla la tua casella di posta elettronica.";
        } catch (Exception ex) {
            ex.printStackTrace();
            message = "C'è stato un errore. Riprova più tardi.";
        } finally {
            request.setAttribute("info", message);
            request.getRequestDispatcher("reset_password.jsp").forward(request, response);
        }
    }
 
}