package danteslibrary.control;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.servlet.ServletException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.*;

import danteslibrary.dao.*;
import danteslibrary.model.*;

import org.json.*;

@WebServlet("/admin")
@MultipartConfig /*Necessario perché nella pagina admin.jsp abbiamo una form
					con enctype="multipart/form-data"*/
public class ManagerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private CardsDAO cardsDAO = new CardsDAO();
	private UsersDAO usersDAO = new UsersDAO();
	private BooksDAO booksDAO = new BooksDAO();
	private BookingsDAO bookingsDAO = new BookingsDAO();
	private ManagersDAO managersDAO = new ManagersDAO();
	private LibraryDAO libraryDAO = new LibraryDAO();
	
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
					ArrayList<UsersBean> users = usersDAO.getUsersByFilter(filter, keyword);
					request.setAttribute("users", users);
				}
			}
			/* - Mostra tutti gli utenti presenti nel database*/
			else if(request.getParameter("all_users") != null) {
				ArrayList<UsersBean> users = usersDAO.getAllUsers();
				request.setAttribute("users", users);
			}
			/* - Rimozione utente dal database (data la mail in input)*/
			else if(request.getParameter("remove_user") != null) {
				usersDAO.removeUser(request.getParameter("remove_user"));
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
					ArrayList<BooksBean> books = booksDAO.getBooksByFilter(filter, keyword);
					request.setAttribute("books", books);
				}
			}
			else if(request.getParameter("edit_book") != null) {
				BooksBean book = booksDAO.getBookById(Integer.parseInt(request.getParameter("edit_book")));
				request.setAttribute("edit_book", book);
				request.getRequestDispatcher("admin.jsp?books").forward(request, response);
				return;
			}
			else if(request.getParameter("save_book") != null || request.getParameter("new_book") != null) {
				/*Prelevo tutti i parametri che sono stati passati*/
				Part filePart = request.getPart("file"); /*Serve per prelevare dal campo <input type="file">*/
				String link;
				if(request.getParameter("save_book") != null)
					link = booksDAO.getBookCoverById(Integer.parseInt(request.getParameter("book_id")));
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
						String oldFileName = booksDAO.getBookCoverById(Integer.parseInt(request.getParameter("book_id")));
						Files.deleteIfExists(new File(absolute_path + path + oldFileName.substring(oldFileName.lastIndexOf("/") + 1)).toPath());
					}
					Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
					link = "./files" + path + randomFileName;
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
						if(booksDAO.updateBook(book) != 0)
							request.setAttribute("info_book", "Il libro " + book.getTitle() + " è stato aggiornato.");
						else
							request.setAttribute("error", "Si è verificato un errore.");
					}
					else {
						if(booksDAO.newBook(book) != 0)
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
				ArrayList<BooksBean> books = booksDAO.getAllBooks();
				request.setAttribute("books", books);
			}
			else if(request.getParameter("remove_book") != null) {
				booksDAO.removeBook(request.getParameter("remove_book"));
				request.setAttribute("info_book", "Il libro " + request.getParameter("remove_book") + " è stato rimosso.");
			}
			else if(request.getParameter("new_genre") != null) {
				String genre_name = request.getParameter("genre_name");
				if(genre_name != null && !genre_name.equals("")) {
					if(booksDAO.newGenre(genre_name) != 0)
						request.setAttribute("info_book", "Il genere " + genre_name + " è stato aggiunto.");
					else
						request.setAttribute("error", "Il genere " + genre_name + " è già presente nel sistema!");
				}
			}
			/*Con una richiesta $.post("admin?json_genres") chiedo alla servlet tutti i generi
			 * presenti nel database. Rispondo alla jsp mandando un array in formato json
			 * contenenti i generi*/
			else if(request.getParameter("json_genres") != null) {
				JSONArray genres = booksDAO.getJSONAllGenres();
				response.setContentType("application/json");
				PrintWriter pw = response.getWriter();
				pw.write(genres.toString());
				pw.close();
				return;
			}
			else if(request.getParameter("all_genres") != null) {
				request.setAttribute("all_genres", booksDAO.getAllGenres());
			}
			else if(request.getParameter("remove_genre") != null) {
				booksDAO.removeGenre(request.getParameter("remove_genre"));
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
					ArrayList<CardsBean> cards = cardsDAO.getCardsByFilter(filter, keyword);
					if(!cards.isEmpty())
						request.setAttribute("cards", cards);
				}
			}
			else if(request.getParameter("new_card") != null) {
		        CardsBean card = new CardsBean();
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
		        
		        if(associated && !usersDAO.checkExistingCodiceFiscale(codice_fiscale)) {
		        	request.setAttribute("error", "L'utente con il codice fiscale: " + codice_fiscale
		        			+ " non è presente nel sistema. Non puoi associare una tessera di un utente non registrato.");
		        	request.getRequestDispatcher("admin.jsp?cards").forward(request, response);
		        	return;
		        }
		        else {
		        	card.setCodice_fiscale(codice_fiscale);
			        card.setAssociated(associated);
			        if(cardsDAO.newCardAdmin(card) != 0)
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
				ArrayList<CardsBean> cards = cardsDAO.getAllCards();
				if(!cards.isEmpty())
					request.setAttribute("cards", cards);
			}
			else if(request.getParameter("remove_card") != null) {
				cardsDAO.removeCard(request.getParameter("remove_card"));
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
          ArrayList<BookingsBean> bookings = bookingsDAO.getBookingsByFilter(filter, keyword);
          request.setAttribute("bookings", bookings);
        }
      }
      else if(request.getParameter("new_booking") != null) {
        String codice_fiscale = request.getParameter("codice_fiscale");
        /*Controllo formato codice fiscale*/
        if(codice_fiscale != null && !codice_fiscale.equals("")) {
        	String regex = "[a-zA-Z]{6}\\d\\d[a-zA-Z]\\d\\d[a-zA-Z]\\d\\d\\d[a-zA-Z]";
        	if(!Pattern.matches(regex, codice_fiscale)) {
        		request.setAttribute("error", "Inserire un codice fiscale valido.");
        		request.getRequestDispatcher("admin.jsp?bookings").forward(request, response);
        		return;
        	}
        }
        /*Il codice fiscale è corretto. Mi assicuro che esiste una tessera con il codice fiscale.*/
    	if(cardsDAO.getCardByCodice_fiscale(codice_fiscale) == null) {
    		request.setAttribute("error", "Non esiste alcuna tessera legata al codice fiscale inserito.");
    		request.getRequestDispatcher("admin.jsp?bookings").forward(request, response);
    		return;
    	}
        try {
        	int book_id = Integer.parseInt(request.getParameter("book_id"));
            int card_id = Integer.parseInt(request.getParameter("card_id"));
            /*Controllo formato codice tessera (5 numeri)*/
            if(!Pattern.matches("[0-9]{5}", request.getParameter("card_id"))) {
            	request.setAttribute("error", "Inserire un codice tessera valido.");
        		request.getRequestDispatcher("admin.jsp?bookings").forward(request, response);
        		return;
            }
            /*Il codice tessera ha un formato valido. Mi assicuro che il codice tessera sia lo stesso 
             * di quello memorizzato nella tessera (in base al codice fiscale inserito).*/
            else {
            	if(cardsDAO.getCardByCodice_fiscale(codice_fiscale).getCard_id() != card_id) {
            		request.setAttribute("error", "Non c'è una corrispondenza tra il codice tessera e il codice fiscale inseriti."
            				+ " Assicurarsi che la tessera con codice: "+card_id+ " appartenga effettivamente a: "+codice_fiscale+".");
            		request.getRequestDispatcher("admin.jsp?bookings").forward(request, response);
            		return;
            	}
            }
            /*Controllo correttezza delle date*/
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN);
			Date tmp_date;
			LocalDate start_date, end_date;
		
			tmp_date = formatter.parse(request.getParameter("start_date"));
			start_date = tmp_date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			tmp_date = formatter.parse(request.getParameter("end_date"));
			end_date = tmp_date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			if(start_date.isAfter(end_date)) {
				request.setAttribute("error", "La data di inizio deve precedere la data di fine!");
				request.getRequestDispatcher("admin.jsp?bookings").forward(request, response);
				return;
			}
			
            String state = request.getParameter("state");
            
            String email_booking = request.getParameter("email");
            if(email_booking != null && !email_booking.equals("")) {
            	/*Se viene inserita l'email(facoltativa) controllo l'esistenza
            	 * di un utente registrato con quella email nel sistema.*/
            	if(!usersDAO.checkExistingEmail(email_booking)) {
	            	request.setAttribute("error", "Non esiste un utente registrato con l'indirizzo email: "+email_booking+".");
					request.getRequestDispatcher("admin.jsp?bookings").forward(request, response);
					return;
            	}
            }
            else {
            	email_booking = null;
            }
            if(booksDAO.getBookById(book_id) == null) {
            	request.setAttribute("error", "Il libro con codice: "+book_id+" non esiste.");
				request.getRequestDispatcher("admin.jsp?bookings").forward(request, response);
				return;
            }
            if(bookingsDAO.newBooking(email_booking, start_date.toString(), end_date.toString(), state, card_id, book_id) != 0)
            	request.setAttribute("info_booking", "La prenotazione è stata aggiunta con successo.");
            else
            	request.setAttribute("error", "Errore imprevisto. Impossibile aggiungere la prenotazione.");
        } catch (ParseException e) {
			e.printStackTrace();
			request.setAttribute("error", "Formato delle date non valido.");
			request.getRequestDispatcher("admin.jsp?bookings").forward(request, response);
			return;
		} catch(NumberFormatException e) {
        	e.printStackTrace();
			request.setAttribute("error", "Errore imprevisto. Impossibile aggiungere la prenotazione.");
        } catch (SQLException e) {
			e.printStackTrace();
			request.setAttribute("error", "Servizio non disponibile. Riprovare più tardi.");
		}
      }
      /*Mostra tutte le prenotazioni*/
      else if(request.getParameter("all_bookings") != null) {
		ArrayList<BookingsBean> bookings = bookingsDAO.getAllBookings();
		request.setAttribute("bookings", bookings);
      }
      /*Pulsante di modifca prenotazione premuto*/
      else if(request.getParameter("edit_booking") != null) {
		BookingsBean booking = bookingsDAO.getBookingById(Integer.parseInt(request.getParameter("edit_booking")));
		request.setAttribute("edit_booking", booking);
		request.getRequestDispatcher("admin.jsp?bookings").forward(request, response);
		return;
      }
      /*Modifica stato prenotazione*/
      else if(request.getParameter("save_booking") != null) {
		int booking_id = Integer.parseInt(request.getParameter("booking_id"));
		String state = request.getParameter("state");
		
		/*Controllo se posso modificare lo stato della prenotazione:
		Se lo stato e' gia' Annullata o Riconsegnato non posso modificare lo stato
		e quindi mostro un errore.*/
		BookingsBean bean = bookingsDAO.getBookingById(booking_id);
		if(state.equals(bean.getState_name()) && 
			(bean.getState_name().equals("Annullata") || bean.getState_name().equals("Riconsegnato"))) {
			request.setAttribute("error", "Non è stato possibile aggiornare la prenotazione.");
			request.getRequestDispatcher("admin.jsp?bookings").forward(request, response);
			return;
		}
		/*Modifico lo stato poiche' e' diverso da Annullata o Riconsegnato.
		 * Nel caso in cui la stringa (stato) ricevuta sia sconosciuta, mostro un errore.*/
		try {
			if(bookingsDAO.updateBooking(booking_id, state) != 0)
				request.setAttribute("info_booking", "La prenotazione con codice: " + booking_id
					+ " è stata aggiornata.");
			else
				request.setAttribute("error", "Stato prenotazione non valido.");
		} catch(SQLException e) {
			e.printStackTrace();
			request.setAttribute("error", "Non è stato possibile aggiornare la prenotazione.");
		}
      }
      /*Cancellazione prenotazione*/
      else if(request.getParameter("remove_booking") != null) {
		try {
			bookingsDAO.removeBooking(Integer.parseInt(request.getParameter("remove_booking")));
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
          ArrayList<ManagersBean> managers = managersDAO.getManagersByFilter(filter, keyword);
          request.setAttribute("managers", managers);
        }
      }
      else if(request.getParameter("edit_manager") != null) {
		ManagersBean manager = managersDAO.getManagerByEmail(request.getParameter("edit_manager"));
		request.setAttribute("edit_manager", manager);
		request.getRequestDispatcher("admin.jsp?managers").forward(request, response);
		return;
      }
      else if(request.getParameter("new_manager") != null || request.getParameter("save_manager") != null) {
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
				if(managersDAO.updateManager(manager, original_email) != 0)
					request.setAttribute("info_manager", "Il gestore " + manager.getName() +" "+ manager.getSurname() +" è stato aggiornato.");
				else
					request.setAttribute("error", "Si è verificato un errore.");
			}
			else {
				if(managersDAO.newManager(manager) != 0)
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
		ArrayList<ManagersBean> managers = managersDAO.getAllManagers();
		request.setAttribute("managers", managers);
      }
      else if(request.getParameter("remove_manager") != null) {
		managersDAO.removeManager(request.getParameter("remove_manager"));
		request.setAttribute("info_manager", "Il Gestore:  "
		+ request.getParameter("remove_manager") + " è stato rimosso.");
      }
      
      /*-- Sezione Biblioteca -- */	
      if(request.getParameter("save_library") != null) {
    	  Part filePart = request.getPart("file"); /*Serve per prelevare dal campo <input type="file" name="file">*/
    	  String name = request.getParameter("name");
    	  String contacts = request.getParameter("contacts");
    	  long fileSize = filePart.getSize();
    	  LibraryBean library = libraryDAO.getLibraryInfo();
    	  String fileName = filePart.getSubmittedFileName();
    	  System.out.println("filename: " + fileName);
    	  if(fileName.equals("default_logo.png"))
    		  library.setLogo("./images/default_logo.png");
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
				library.setLogo("./files" + fileName);
			}
		
		library.setName(name);
		
		library.setContacts(contacts);
		if(libraryDAO.updateLibraryInfo(library) != 0) {
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
			ManagersBean admin = managersDAO.login(email, password);
			
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