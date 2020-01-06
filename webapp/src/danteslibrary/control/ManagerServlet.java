package danteslibrary.control;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import danteslibrary.dao.*;
import danteslibrary.model.*;

import org.json.*;

@WebServlet("/admin")
@MultipartConfig /*Necessario perché nella pagina admin.jsp abbiamo una form
//con enctype="multipart/form-data"*/
public class ManagerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		HttpSession session = request.getSession();
		
		/*[ FUNZIONALITA' AMMINISTRATORE ]
		 * disponibili solo se e' autenticato (admin != null)*/
		if(session.getAttribute("admin") != null) {

			/*--Sezione Utente--*/
			if(request.getParameter("keyword_users") != null && request.getParameter("keyword_users") != "") {
				String keyword = request.getParameter("keyword_users");
				/*filter puo' assumere 4 valori:
				 * - 0: nome
				 * - 1: cognome
				 * - 2: email
				 * - 3: codice fiscale */
				int filter = Integer.parseInt(request.getParameter("filter"));
				if(filter < 0 || filter > 3) {
					request.setAttribute("info", "Filtro non valido.");
					request.getRequestDispatcher("admin.jsp").forward(request, response);
					return;
				} 
				else {
					UsersDAO dao = new UsersDAO();
					ArrayList<UsersBean> users = dao.getUsersByFilter(filter, keyword);
					request.setAttribute("users", users);
				}
			}
			/* - Mostra tutti gli utenti presenti nel database*/
			else if(request.getParameter("all_users") != null) {
				UsersDAO dao = new UsersDAO();
				ArrayList<UsersBean> users = dao.getAllUsers();
				request.setAttribute("users", users);
			}
			/* - Rimozione utente dal database (data la mail in input)*/
			else if(request.getParameter("remove_user") != null) {
				UsersDAO dao = new UsersDAO();
				dao.removeUser(request.getParameter("remove_user"));
				request.setAttribute("info_user", "L'utente " + request.getParameter("remove_user") + " è stato rimosso.");
			}
		
			/* -- Sezione Libro -- */	
			if(request.getParameter("keyword_book") != null && request.getParameter("keyword_book") != "") {
				String keyword = request.getParameter("keyword_book");
				/*filter puo' assumere 4 valori:
				 * - 0: titolo
				 * - 1: autore
				 * - 2: casa editrice
				 * - 3: Genere */
				int filter = Integer.parseInt(request.getParameter("filter"));
				if(filter < 0 || filter > 3) {
					request.setAttribute("info_book", "Filtro non valido.");
					request.getRequestDispatcher("admin.jsp").forward(request, response);
					return;
				} 
				else {
					BooksDAO dao_books = new BooksDAO();
					ArrayList<BooksBean> books = dao_books.getBooksByFilter(filter, keyword);
					request.setAttribute("books", books);
				}
			}
			else if(request.getParameter("edit_book") != null) {
				BooksDAO dao = new BooksDAO();
				BooksBean book = dao.findBookById(request.getParameter("edit_book"));
				request.setAttribute("edit_book", book);
				request.getRequestDispatcher("admin.jsp?books").forward(request, response);
				return;
			}
			else if(request.getParameter("save_book") != null || request.getParameter("new_book") != null) {
				BooksDAO dao = new BooksDAO();
				/*Prelevo tutti i parametri che sono stati passati*/
				Part filePart = request.getPart("file"); /*Serve per prelevare dal campo <input type="file">*/
				String link;
				if(request.getParameter("save_book") != null)
					link = dao.getBookCoverById(Integer.parseInt(request.getParameter("book_id")));
				else
					link = null;
				/*Salvo la nuova immagine SOLO se l'admin ha cambiata, quindi
				 * nel campo input ci sarà un'immagine con dimensione diversa da 0 byte */	
				if(filePart.getSize() != 0) {
					InputStream input = filePart.getInputStream(); /*Ottengo il flusso dell'immagine*/
					String absolute_path = System.getProperty("upload.location");
					String path = "/book_covers";
					File directory = new File(absolute_path + path);
					if(!directory.exists()) {
						directory.mkdirs();
					}
					/*Viene creato un nome pseudocasuale per l'immagine*/
					String randomFileName = "book-" + new Random().nextInt(100000) + ".png";
					File file = new File(absolute_path + path + randomFileName);
					/*L'immagine viene copiata nel percorso sopra specificato e se esiste un file con lo stesso
					 * nome, viene sovrascritto.
					 * L'immagine precedente invece, viene cancellata.*/
					if(request.getParameter("save_book") != null) {
						String oldFileName = dao.getBookCoverById(Integer.parseInt(request.getParameter("book_id")));
						Files.deleteIfExists(new File(absolute_path + path + oldFileName.substring(oldFileName.lastIndexOf("/") + 1)).toPath());
					}
					Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
					link = "./files" + path + randomFileName;
				}
				/*Se non ricevo il filename dalla form, vuol dire che ho cliccato su
				 * Pulisci Campi, e quindi rimuovo l'immagine mettendone una di default.*/
				else if(filePart.getSubmittedFileName().equals("")) {
					link = "./images/no_image.png";
				}
				try {
					String title = request.getParameter("title");
					int quantity = Integer.parseInt(request.getParameter("quantity"));
					String publisher = request.getParameter("publisher");
					String description = request.getParameter("description");
					ArrayList<String> genres = new ArrayList<String>();
					ArrayList<String> authors = new ArrayList<String>();
					if(request.getParameter("genres") != null && !request.getParameter("genres").equals("")) {
						String formatter = request.getParameter("genres");
						while(formatter.contains(",")) {
							int comma_index = formatter.indexOf(",");
							String genre = formatter.substring(0, comma_index);
							formatter = formatter.substring(comma_index + 1, formatter.length());
							genres.add(genre);
						}
						genres.add(formatter); //Aggiungo l'ultima parola che il ciclo non ha considerato
					}
					if(genres.isEmpty()) {
						request.setAttribute("error", "Generi non validi.");
						request.getRequestDispatcher("admin.jsp?books").forward(request, response);
						return;
					}
					if(request.getParameter("authors") != null && !request.getParameter("authors").equals("")) {
						String formatter = request.getParameter("authors");
						while(formatter.contains(",")) {
							int comma_index = formatter.indexOf(",");
							String author = formatter.substring(0, comma_index);
							formatter = formatter.substring(comma_index + 1, formatter.length());
							authors.add(author);
						}
						authors.add(formatter); //Aggiungo l'ultima parola che il ciclo non ha considerato
					}
					if(authors.isEmpty()) {
						request.setAttribute("error", "Inserisci almeno un autore.");
						request.getRequestDispatcher("admin.jsp?books").forward(request, response);
						return;
					}

					BooksBean book = new BooksBean();
					/*Costruisco il libro (se lo sto modificando, allora mi serve l'id
					 * del libro che sto modificando, altrimenti vuol dire che lo sto
					 * aggiungendo, quindi l'id verra' auto generato dal db)*/
					if(request.getParameter("save_book") != null) {
						book.setBook_id(Integer.parseInt(request.getParameter("book_id")));
					}
					book.setTitle(title.toString());
					book.setDescription(description);
					book.setPublisher(publisher);
					book.setQuantity(quantity);
					book.setAuthors(authors);
					book.setGenres(genres);
					if(link == null) {
						link = "./images/no_image.png"; /*Non ci sono immagini del libro nel db*/
					}
					book.setCover(link);
					if(request.getParameter("save_book") != null) {
						/*Aggiorno il libro nel DB*/
						if(dao.updateBook(book) != 0)
							request.setAttribute("info_book", "Il libro " + book.getTitle() + " è stato aggiornato.");
						else
							request.setAttribute("error", "Si è verificato un errore.");
					}
					else {
						if(dao.newBook(book) != 0)
							request.setAttribute("info_book", "Il libro " + book.getTitle() + " è stato aggiunto.");
						else
							request.setAttribute("error", "Si è verificato un errore.");
					}
				}
				catch(Exception e) {
					request.setAttribute("error", "Errore, qualche campo è vuoto "
							+ "oppure non è stato compilato.");
					return;
				}
				
			}
			else if(request.getParameter("all_books") != null) {
				BooksDAO dao = new BooksDAO();
				ArrayList<BooksBean> books = dao.getAllBooks();
				request.setAttribute("books", books);
			}
			else if(request.getParameter("remove_book") != null) {
				BooksDAO dao = new BooksDAO();
				dao.removeBook(request.getParameter("remove_book"));
				request.setAttribute("info_book", "Il libro " + request.getParameter("remove_book") + " è stato rimosso.");
			}
			else if(request.getParameter("new_genre") != null) {
				BooksDAO dao = new BooksDAO();
				String genre_name = request.getParameter("genre_name");
				if(genre_name != null && !genre_name.equals("")) {
					if(dao.newGenre(genre_name) != 0)
						request.setAttribute("info_book", "Il genere " + genre_name + " è stato aggiunto.");
					else
						request.setAttribute("error", "Il genere " + genre_name + " è già presente nel sistema!");
				}
			}
			/*Con una richiesta $.post("admin?json_genres") chiedo alla servlet tutti i generi
			 * presenti nel database. Rispondo alla jsp mandando un array in formato json
			 * contenenti i generi*/
			else if(request.getParameter("json_genres") != null) {
				BooksDAO dao = new BooksDAO();
				JSONArray genres = dao.getJSONAllGenres();
				response.setContentType("application/json");
				PrintWriter pw = response.getWriter();
				pw.write(genres.toString());
				pw.close();
				return;
			}
			else if(request.getParameter("all_genres") != null) {
				BooksDAO dao = new BooksDAO();
				request.setAttribute("all_genres", dao.getAllGenres());
			}
			else if(request.getParameter("remove_genre") != null) {
				BooksDAO dao = new BooksDAO();
				dao.removeGenre(request.getParameter("remove_genre"));
				request.setAttribute("info_book", "Il genere " + request.getParameter("remove_genre") + " è stato rimosso.");
			}
				
			/* -- Sezione Tessera -- */	
			if(request.getParameter("keyword_card") != null && request.getParameter("keyword_card") != "") {
				String keyword = request.getParameter("keyword_card");
				/*filter puo' assumere 5 valori:
				 * - 0: Nome
				 * - 1: Cognome
				 * - 2: Email
				 * - 3: Codice fiscale
				 * - 4: Codice_tessera */
				int filter = Integer.parseInt(request.getParameter("filter"));
				if(filter < 0 || filter > 4) {
					request.setAttribute("info_card", "Filtro non valido.");
					request.getRequestDispatcher("admin.jsp").forward(request, response);
					return;
				}
				else {
					CardsDAO dao_cards = new CardsDAO();
					ArrayList<CardsBean> cards = dao_cards.getCardsByFilter(filter, keyword);
					if(!cards.isEmpty())
						request.setAttribute("cards", cards);
				}
			}
			else if(request.getParameter("new_card") != null) {
		        CardsDAO dao = new CardsDAO();
		        CardsBean card = new CardsBean();
		        UsersDAO udao = new UsersDAO();
		        String codice_fiscale = request.getParameter("codice_fiscale");
		        boolean associated = request.getParameter("associated") != null;
		        if((request.getParameter("card_id")!=null) && !(request.getParameter("card_id").equals(""))) {
		          try {
		              int card_id = Integer.parseInt(request.getParameter("card_id"));
		              card.setCard_id(card_id);
		          }
		          catch(NumberFormatException  e) {
		            request.setAttribute("error", "Errore formato codice tessera.");
		            request.getRequestDispatcher("admin.jsp?cards").forward(request, response);
		        	return;
		          }
		        }
		        
		        if(associated && !udao.checkExistingCodiceFiscale(codice_fiscale)) {
		        	request.setAttribute("error", "L'utente con il codice fiscale: " + codice_fiscale
		        			+ " non è presente nel sistema. Non puoi associare una tessera di un utente non registrato.");
		        	request.getRequestDispatcher("admin.jsp?cards").forward(request, response);
		        	return;
		        }
		        else {
		        	card.setCodice_fiscale(codice_fiscale);
			        card.setAssociated(associated);
			        if(dao.newCardAdmin(card) != 0)
			        	request.setAttribute("info_card", "La tessera è stata aggiunta con il codice fiscale: " + codice_fiscale);
			        else
			        	request.setAttribute("error", "Non è stato possibile aggiungere la tessera. "
			        			+ "Assicurati che la tessera da inserire non sia già presente o che il codice fiscale non abbia "
			        			+ "già una tessera associata.");
			        request.getRequestDispatcher("admin.jsp?cards").forward(request, response);
		        	return;
		        }
		        
			}
			else if(request.getParameter("all_cards") != null) {
				CardsDAO dao = new CardsDAO();
				ArrayList<CardsBean> cards = dao.getAllCards();
				if(!cards.isEmpty())
					request.setAttribute("cards", cards);
			}
			else if(request.getParameter("remove_card") != null) {
				CardsDAO dao = new CardsDAO();
				dao.removeCard(request.getParameter("remove_card"));
				request.setAttribute("info_card", "La tessera con codice: " 
				+ request.getParameter("remove_card") + " è stata rimossa.");
			}
			
      /* -- Sezione Prenotazione -- */
      if(request.getParameter("keyword_booking") != null && request.getParameter("keyword_booking") != "") {
        String keyword = request.getParameter("keyword_booking");
        /*filter puo' assumere 8 valori:
         * - 0: Codice prenotazione
         * - 1: Id Libro
         * - 2: Data inizio
         * - 3: Data fine
         * - 4: Stato
         * - 5: Email
         * - 6: Codice fiscale
         * - 7: Codice tessera
         *  */
        int filter = Integer.parseInt(request.getParameter("filter"));
        if(filter < 0 || filter > 7) {
          request.setAttribute("info", "Filtro non valido.");
          request.getRequestDispatcher("admin.jsp").forward(request, response);
          return;
        } 
        else {
          BookingsDAO dao_bookings = new BookingsDAO();
          ArrayList<BookingsBean> bookings = dao_bookings.getBookingsByFilter(filter, keyword);
          request.setAttribute("bookings", bookings);
        }
      }
      else if(request.getParameter("new_booking") != null) {
        BookingsDAO dao = new BookingsDAO();
        // TODO cod. fiscale facoltativo String codice_fiscale = request.getParameter("codice_fiscale");
        int book_id = Integer.parseInt(request.getParameter("book_id"));
        int card_id = Integer.parseInt(request.getParameter("card_id"));
        String start_date = request.getParameter("start_date");
        String end_date = request.getParameter("end_date");
        String state = request.getParameter("state");
        String email_booking;
        if((request.getParameter("email")!=null) && !(request.getParameter("email").equals(""))) {
        	email_booking = request.getParameter("email");
        }
        else {
        	email_booking = null;
        }
        try {
			dao.newBooking(email_booking, start_date, end_date, state, card_id, book_id);
			request.setAttribute("info_booking", "La prenotazione è stata aggiunta con successo.");
				
		} catch (SQLException e) {
			e.printStackTrace();
			request.setAttribute("error", "Non è stato possibile aggiungere la prenotazione.");
		}
      }
      /*Mostra tutte le prenotazioni*/
      else if(request.getParameter("all_bookings") != null) {
		BookingsDAO dao = new BookingsDAO();
		ArrayList<BookingsBean> bookings = dao.getAllBookings();
		request.setAttribute("bookings", bookings);
      }
      /*Pulsante di modifca prenotazione premuto*/
      else if(request.getParameter("edit_booking") != null) {
		BookingsDAO dao = new BookingsDAO();
		BookingsBean booking = dao.getBookingById(Integer.parseInt(request.getParameter("edit_booking")));
		request.setAttribute("edit_booking", booking);
		request.getRequestDispatcher("admin.jsp?bookings").forward(request, response);
		return;
      }
      /*Modifica stato prenotazione*/
      else if(request.getParameter("save_booking") != null) {
		BookingsDAO dao = new BookingsDAO();
		int booking_id = Integer.parseInt(request.getParameter("booking_id"));
		String state = request.getParameter("state");
		try {
			dao.updateBooking(booking_id, state);
			request.setAttribute("info_booking", "La prenotazione con codice: " + booking_id
					+ " è stata aggiornata.");
		} catch(SQLException e) {
			e.printStackTrace();
			request.setAttribute("error", "Non è stato possibile aggiornare la prenotazione.");
		}
      }
      /*Cancellazione prenotazione*/
      else if(request.getParameter("remove_booking") != null) {
		BookingsDAO dao = new BookingsDAO();
		try {
			dao.removeBooking(Integer.parseInt(request.getParameter("remove_booking")));
			request.setAttribute("info_booking", "La prenotazione con codice: "
			+ request.getParameter("remove_booking") + " è stata rimossa.");
		} catch(SQLException e) {
			e.printStackTrace();
			request.setAttribute("error", "Non è stato possibile cancellare la prenotazione. Riprovare più tardi.");
		}
      }
				
      /*--Sezione Gestori--*/
      if(request.getParameter("keyword_manager") != null && request.getParameter("keyword_manager") != "") {
        String keyword = request.getParameter("keyword_manager");
        /*filter puo' assumere 4 valori:
         * - 0: Email
         * - 1: Nome
         * - 2: Cognome
         * - 3: Ruolo */
        int filter = Integer.parseInt(request.getParameter("filter"));
        if(filter < 0 || filter > 3) {
          request.setAttribute("info", "Filtro non valido.");
          request.getRequestDispatcher("admin.jsp").forward(request, response);
          return;
        } 
        else {
          ManagersDAO dao = new ManagersDAO();
          ArrayList<ManagersBean> managers = dao.getManagersByFilter(filter, keyword);
          request.setAttribute("managers", managers);
        }
      }
      else if(request.getParameter("edit_manager") != null) {
		ManagersDAO dao = new ManagersDAO();
		ManagersBean manager = dao.findManagerByEmail(request.getParameter("edit_manager"));
		request.setAttribute("edit_manager", manager);
		request.getRequestDispatcher("admin.jsp?managers").forward(request, response);
		return;
      }
      else if(request.getParameter("new_manager") != null || request.getParameter("save_manager") != null) {
        ManagersDAO dao = new ManagersDAO();
        ManagersBean manager = new ManagersBean();
		try {
			String original_email = request.getParameter("original_email");
			String manager_email = request.getParameter("email");
			String manager_password = request.getParameter("password");
			String name = request.getParameter("name");
			String surname = request.getParameter("surname");
			String address = request.getParameter("address");
			String phone = request.getParameter("phone");
			ArrayList<String> roles = new ArrayList<String>();
			if (request.getParameter("users_manager") != null)
				roles.add("Gestore Utenti");
			if (request.getParameter("books_manager") != null)
				roles.add("Gestore Libri");
			if (request.getParameter("cards_manager") != null)
				roles.add("Gestore Tessere");
			if (request.getParameter("bookings_manager") != null)
				roles.add("Gestore Prenotazioni");
			if (request.getParameter("library_manager") != null)
				roles.add("Gestore Biblioteca");
			manager.setEmail(manager_email);
			manager.setPassword(manager_password);
			manager.setName(name);
			manager.setSurname(surname);
			manager.setPhone(phone);
			manager.setAddress(address);
			manager.setRoles(roles);
			
			if(request.getParameter("save_manager") != null) {
				/*Aggiorno il manager nel DB*/
				if(dao.updateManager(manager, original_email) != 0)
					request.setAttribute("info_manager", "Il gestore " + manager.getName() +" "+ manager.getSurname() +" è stato aggiornato.");
				else
					request.setAttribute("error", "Si è verificato un errore.");
			}
			else {
				if(dao.newManager(manager) != 0)
					request.setAttribute("info_manager", "Il gestore " + manager.getName() +" "+ manager.getSurname() +" è stato aggiunto.");
					else
						request.setAttribute("error", "Si è verificato un errore.");
			}
		}
		catch(Exception e) {
			request.setAttribute("error", "Errore, qualche campo è vuoto "
					+ "oppure non è stato compilato.");
			return;
		}
      }
      /* - Mostra tutti i gestori presenti nel database*/
      else if(request.getParameter("all_managers") != null) {
		ManagersDAO dao = new ManagersDAO();
		ArrayList<ManagersBean> managers = dao.getAllManagers();
		request.setAttribute("managers", managers);
      }
      else if(request.getParameter("remove_manager") != null) {
		ManagersDAO dao = new ManagersDAO();
		dao.removeManager(request.getParameter("remove_manager"));
		request.setAttribute("info_manager", "Il Gestore:  "
		+ request.getParameter("remove_manager") + " è stato rimosso.");
      }
      
      /*TODO -- Sezione Biblioteca -- */	
      if(request.getParameter("save_library") != null) {
    	  Part filePart = request.getPart("file"); /*Serve per prelevare dal campo <input type="file" name="file">*/
    	  String name = request.getParameter("name");
    	  String fileName = null;
    	  String contacts = request.getParameter("contacts");
    	  long fileSize = filePart.getSize();
    	  LibraryBean library = new LibraryBean();
    	  
    	  if(name.equals("") || contacts.equals("") || name == null || contacts == null) {
    		  request.setAttribute("error", "Assicurati di aver compilato correttamente il nome e i contatti "
    		  		+ "della biblioteca");
    		  request.getRequestDispatcher("admin.jsp?library").forward(request, response);
    		  return;
    	  }
    	  /*Salvo la nuova immagine SOLO se l'admin ha cambiata, quindi
    	   * nel campo input ci sarà un'immagine con dimensione diversa da 0 byte */	
			if(fileSize != 0) {
				InputStream input = filePart.getInputStream(); /*Ottengo il flusso dell'immagine*/
				String absolute_path = System.getProperty("upload.location");
				fileName = "/library_logo.png";
				File directory = new File(absolute_path);
				if(!directory.exists()) {
					directory.mkdirs();
				}
				File file = new File(absolute_path + fileName);
				/*L'immagine viene copiata nel percorso sopra specificato e se esiste un file con lo stesso
				 * nome, viene sovrascritto.*/
				Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				library.setLogo("./files/" + fileName);
			}

		LibraryDAO dao = new LibraryDAO();
		
		library.setName(name);
		
		library.setContacts(contacts);
		if(dao.updateLibraryInfo(library) != 0) {
			request.setAttribute("info_library", "Le informazioni della biblioteca sono state salvate.");
			getServletContext().setAttribute("library", library);
		}
		else
			request.setAttribute("error", "Errore imprevisto. Se il problema persiste, contattare l'amministratore di sistema.");
      }
      
      
			request.getRequestDispatcher("admin.jsp").forward(request, response);
			return;
	} //chiusura if(session.getAttribute("admin") != null)

		if(email == null || password == null) {
			response.sendRedirect("admin.jsp");
			return;
		}
		
		/*Autenticazione*/
		else {
			ManagersDAO dao = new ManagersDAO();
			ManagersBean admin = dao.login(email, password);
			
			if(admin != null) {
				session.setAttribute("admin", admin);
				session.removeAttribute("user"); /*Distruggo la sessione per l'utente(nel caso in cui sia collegato)*/
			}
			else {
				request.setAttribute("login_error", true);
				request.getRequestDispatcher("admin.jsp").forward(request, response);
				return;
			}
		}
		/*Se l'autenticazione va a buon fine, l'utente viene reindirizzato all'
		 * homepage*/
		response.sendRedirect("admin.jsp");
		return;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}