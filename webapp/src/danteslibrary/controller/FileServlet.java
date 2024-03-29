package danteslibrary.controller;

import javax.servlet.http.*;
import javax.servlet.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import javax.servlet.ServletException;

/**
 * Classe che riceve richieste GET riguardanti la localizzazione di file nel 
 * File System e risponde dando in output il file richiesto se presente.
 * @author Andrea Buongusto
 * @author Marco Salierno
 *
 */
@WebServlet("/files/*")
public class FileServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String filename = URLDecoder.decode(request.getPathInfo().substring(1), "UTF-8");
        File file = new File(System.getProperty("upload.location"), filename);
        response.setHeader("Content-Type", getServletContext().getMimeType(filename));
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        
        try {
        	Files.copy(file.toPath(), response.getOutputStream());
        }
        catch(NoSuchFileException e) {
        	System.out.println("Impossibile caricare il file specificato.");
        	e.printStackTrace();
        }
    }

}