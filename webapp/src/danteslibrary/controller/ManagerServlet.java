package danteslibrary.controller;

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

import danteslibrary.dao.*;
import danteslibrary.model.*;
import danteslibrary.util.InputChecker;

import org.json.*;

/**
 * Classe che riceve richieste GET e POST riguardanti l’autenticazione dei 
 * Gestorie le operazioni del pannello di amministrazione.
 * @author Andrea Buongusto
 * @author Marco Salierno
 *
 */
@WebServlet("/admin")
@MultipartConfig /*Necessario perché nella pagina admin.jsp abbiamo una form
					con enctype="multipart/form-data"*/
public class ManagerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private CardsDAO cardsDAO = new CardsDAO();
	private CustomersDAO customersDAO = new CustomersDAO();
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
/**
 * Sezione Clienti
 */
			/* - Ricerca Clienti*/
			if(request.getParameter("keyword_customers") != null && !request.getParameter("keyword_customers").equals("")) {
				String keyword = request.getParameter("keyword_customers");
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
					ArrayList<CustomersBean> customers = customersDAO.getCustomersByFilter(filter, keyword);
					request.setAttribute("customers", customers);
				}
			}
			/* - Mostra tutti i clienti presenti nel database*/
			else if(request.getParameter("all_customers") != null) {
				ArrayList<CustomersBean> customers = customersDAO.getAllCustomers();
				request.setAttribute("customers", customers);
			}
			/* - Rimozione clienti dal database (data la mail in input)*/
			else if(request.getParameter("remove_customer") != null) {
				customersDAO.removeCustomer(request.getParameter("remove_customer"));
				request.setAttribute("info_customer", "Il cliente " + request.getParameter("remove_customer") + " è stato rimosso.");
			}
/**
 * Sezione Libri
 */
			/* - Ricerca Libri*/
			if(request.getParameter("keyword_book") != null && !request.getParameter("keyword_book").equals("")) {
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
					try {
						ArrayList<BooksBean> books = booksDAO.getBooksByFilter(filter, keyword);
						request.setAttribute("books", books);
					}
					catch(SQLException e) {
						System.out.println("Errore Database metodo getBooksByFilter: " + e.getMessage());
						request.setAttribute("error", "Servizio al momento non disponibile. Riprovare più tardi.");
						request.getRequestDispatcher("index.jsp").include(request, response);
						return;
					}
				}
			}
			else if(request.getParameter("edit_book") != null) {
				BooksBean book = booksDAO.getBookById(Integer.parseInt(request.getParameter("edit_book")));
				request.setAttribute("edit_book", book);
				request.getRequestDispatcher("admin.jsp?books").forward(request, response);
				return;
			}
			/* Aggiungi/Salva libro */
			else if(request.getParameter("save_book") != null || request.getParameter("new_book") != null) {
				
				/*Prelevo tutti i parametri che sono stati passati*/
				Part filePart = request.getPart("file"); /*Serve per prelevare dal campo <input type="file">*/
				String link;
				if(request.getParameter("save_book") != null) {
					try {
						link = booksDAO.getBookCoverById(Integer.parseInt(request.getParameter("book_id")));
					} catch(NumberFormatException e) {
						e.printStackTrace();
						request.setAttribute("error", "Si è verificato un errore"); /*Non è arrivato un numero*/
						request.getRequestDispatcher("admin.jsp?books").forward(request, response);
						return;
					}
				}
				else
					link = null;
				
				/*Salvo la nuova immagine SOLO se l'admin ha cambiata, quindi
				 * nel campo input ci sarà un'immagine con dimensione compresa tra 0 e 2MB */
				if(filePart.getSize() != 0 && !filePart.getContentType().equals("image/png") && !filePart.getContentType().equals("image/jpg") && !filePart.getContentType().equals("image/jpeg")) {
					request.setAttribute("error", "L'immagine non può superare i 2MB e deve avere il formato .jpg, .jpeg o .png");
					request.getRequestDispatcher("admin.jsp?books").forward(request, response);
					return;
				}
				else if(filePart.getSize() > 0 && filePart.getSize() <= 2097152) {
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
				else if(filePart.getSize() > 2097152) {
					request.setAttribute("error", "L'immagine non può superare i 2MB e deve avere il formato .jpg, .jpeg o .png");
					request.getRequestDispatcher("admin.jsp?books").forward(request, response);
					return;
				}
				try {
					String title = request.getParameter("title");
					String quantity = request.getParameter("quantity");
					String publisher = request.getParameter("publisher");
					String description = request.getParameter("description");
					
					if(title.equals("") || quantity.equals("") || publisher.equals("") || description.equals("")) {
						request.setAttribute("error", "Non tutti i campi sono stati compilati.");
						request.getRequestDispatcher("admin.jsp?books").forward(request, response);
						return;
					}
					
					if(!InputChecker.checkTitle(title) && !title.equals("")) {
						request.setAttribute("error", "Il titolo può contenere massimo 100 caratteri.");
						request.getRequestDispatcher("admin.jsp?books").forward(request, response);
						return;
					}
					
					if(!InputChecker.checkQuantity(quantity) && !quantity.equals("")) {
						request.setAttribute("error", "La quantità deve essere espressa con un numero positivo (massimo 3 cifre).");
						request.getRequestDispatcher("admin.jsp?books").forward(request, response);
						return;
					}
					
					if(!InputChecker.checkPublisher(publisher) && !publisher.equals("")) {
						request.setAttribute("error", "La casa editrice può contenere massimo 100 caratteri.");
						request.getRequestDispatcher("admin.jsp?books").forward(request, response);
						return;
					}
					
					if(!InputChecker.checkDescription(description) && !description.equals("")) {
						request.setAttribute("error", "La descrizione non può superare i 1000 caratteri.");
						request.getRequestDispatcher("admin.jsp?books").forward(request, response);
						return;
					}
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
						request.setAttribute("error", "Inserire almeno un genere.");
						request.getRequestDispatcher("admin.jsp?books").forward(request, response);
						return;
					}
					if(request.getParameter("authors") != null && !request.getParameter("authors").equals("")) {
						String formatter = request.getParameter("authors");
						if(!formatter.contains(",") && !InputChecker.checkAuthor(formatter)) {
							request.setAttribute("error", "Ciascun autore può contenere solo lettere e spazi. Lunghezza massima di ciascun autore: 100 caratteri.");
							request.getRequestDispatcher("admin.jsp?books").forward(request, response);
							return;
						}
						while(formatter.contains(",")) {
							int comma_index = formatter.indexOf(",");
							String author = formatter.substring(0, comma_index);
							formatter = formatter.substring(comma_index + 1, formatter.length());
							if(InputChecker.checkAuthor(author)) {
								authors.add(author);
							}
							else {
								request.setAttribute("error", "Ciascun autore può contenere solo lettere e spazi. Lunghezza massima di ciascun autore: 100 caratteri.");
								request.getRequestDispatcher("admin.jsp?books").forward(request, response);
								return;
							}
						}
						authors.add(formatter); //Aggiungo l'ultima parola che il ciclo non ha considerato
					}
					if(authors.isEmpty()) {
						request.setAttribute("error", "Inserire almeno un autore.");
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
					book.setQuantity(Integer.parseInt(quantity));
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
					e.printStackTrace();
					request.setAttribute("error", "Errore, qualche campo è vuoto "
							+ "oppure non è stato compilato correttamente.");
					request.getRequestDispatcher("admin.jsp?books").forward(request, response);
					return;
				}
				
			}
			/* - Mostra tutti i libri */
			else if(request.getParameter("all_books") != null) {
				ArrayList<BooksBean> books = booksDAO.getAllBooks();
				request.setAttribute("books", books);
			}
			/* - Cancellazione libro */
			else if(request.getParameter("remove_book") != null) {
				booksDAO.removeBook(Integer.parseInt(request.getParameter("remove_book")));
				request.setAttribute("info_book", "Il libro " + request.getParameter("remove_book") + " è stato rimosso.");
			}
			/* - Aggiunta genere */
			else if(request.getParameter("new_genre") != null) {
				String genre_name = request.getParameter("genre_name");
				if(genre_name != null && !InputChecker.checkGenre(genre_name)) {
					request.setAttribute("error", "Il nome del genere può contenere solo lettere e spazi. Lunghezza massima: 30 caratteri.");
					request.getRequestDispatcher("admin.jsp?books").forward(request, response);
					return;
				}
				if(booksDAO.newGenre(genre_name) != 0)
					request.setAttribute("info_book", "Il genere " + genre_name + " è stato aggiunto.");
				else
					request.setAttribute("error", "Il genere " + genre_name + " è già presente nel sistema!");
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
			/* - Cancellazione genere */
			else if(request.getParameter("remove_genre") != null) {
				booksDAO.removeGenre(request.getParameter("remove_genre"));
				request.setAttribute("info_book", "Il genere " + request.getParameter("remove_genre") + " è stato rimosso.");
			}
			
/**
 * Sezione Tessere
 */
			/* - Ricerca Tessere */
			if(request.getParameter("keyword_card") != null && !request.getParameter("keyword_card").equals("")) {
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
					if(cards != null && !cards.isEmpty())
						request.setAttribute("cards", cards);
				}
			}
			/* - Aggiunta Tessera */
			else if(request.getParameter("new_card") != null) {
		        CardsBean card = new CardsBean();
		        String codice_fiscale = request.getParameter("codice_fiscale");
		        String card_id = request.getParameter("card_id");
		        boolean associated = request.getParameter("associated") != null;
		        
		        if(!InputChecker.checkCodice_fiscale(codice_fiscale) || codice_fiscale.equals("")) {
					request.setAttribute("error", "Inserire un codice fiscale valido.");
					request.getRequestDispatcher("admin.jsp?cards").forward(request, response);
					return;
				}
		        try {
			        if(!card_id.equals("") && !InputChecker.checkCard_id(card_id)) {
						request.setAttribute("error", "Inserire un codice tessera valido.");
						request.getRequestDispatcher("admin.jsp?cards").forward(request, response);
						return;
					}
			        else if(!card_id.equals(""))
			        	card.setCard_id(Integer.parseInt(card_id));
				} catch(Exception  e) {
					e.printStackTrace();
					System.out.println("Hello");
					request.setAttribute("error", "Inserire un codice tessera valido.");
					request.getRequestDispatcher("admin.jsp?cards").forward(request, response);
					return;
				}
		        
		        if(associated && !customersDAO.checkExistingCodice_fiscale(codice_fiscale)) {
		        	request.setAttribute("error", "Il cliente con il codice fiscale: " + codice_fiscale
		        			+ " non è presente nel sistema. Non puoi associare una tessera di un cliente non registrato.");
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
			/* - Mostra tutte le Tessere */
			else if(request.getParameter("all_cards") != null) {
				ArrayList<CardsBean> cards = cardsDAO.getAllCards();
				if(!cards.isEmpty())
					request.setAttribute("cards", cards);
			}
			/* - Cancellazione Tessera */
			else if(request.getParameter("remove_card") != null) {
				cardsDAO.removeCard(Integer.parseInt(request.getParameter("remove_card")));
				request.setAttribute("info_card", "La tessera con codice: " 
				+ request.getParameter("remove_card") + " è stata rimossa.");
			}
			
/**
 * Sezione Prenotazioni
 */
			/* - Ricerca Prenotazioni*/
			if(request.getParameter("keyword_booking") != null && !request.getParameter("keyword_booking").equals("")) {
		        String keyword = request.getParameter("keyword_booking");
		        /*filter puo' assumere 8 valori:
		         * - 0: Codice prenotazione
		         * - 1: Id Libro
		         * - 2: Codice tessera
		         * - 3: Codice fiscale
		         * - 4: Stato
		         * - 5: Email
		         * - 6: Data inizio
		         * - 7: Data fine
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
			/* - Aggiunta Prenotazione*/
			else if(request.getParameter("new_booking") != null) {
				String codice_fiscale = request.getParameter("codice_fiscale");
				/*Controllo formato codice fiscale*/
				if(codice_fiscale == null || !InputChecker.checkCodice_fiscale(codice_fiscale)) {
						request.setAttribute("error", "Inserire un codice fiscale valido.");
						request.getRequestDispatcher("admin.jsp?bookings").forward(request, response);
						return;
				}
		        /*Il codice fiscale è corretto. Mi assicuro che esiste una tessera con il codice fiscale.*/
		    	if(cardsDAO.getCardByCodice_fiscale(codice_fiscale) == null) {
		    		request.setAttribute("error", "Non esiste alcuna tessera legata al codice fiscale inserito.");
		    		request.getRequestDispatcher("admin.jsp?bookings").forward(request, response);
		    		return;
		    	}
		        try {
		        	int book_id = Integer.parseInt(request.getParameter("book_id"));
		            String card_id = request.getParameter("card_id");
		            /* Controllo formato codice tessera */
		            if(card_id == null || !InputChecker.checkCard_id(card_id)) {
		            	request.setAttribute("error", "Inserire un codice tessera valido.");
		        		request.getRequestDispatcher("admin.jsp?bookings").forward(request, response);
		        		return;
		            }
		            /*Il codice tessera ha un formato valido. Mi assicuro che il codice tessera sia lo stesso 
		             * di quello memorizzato nella tessera (in base al codice fiscale inserito).*/
		            else {
		            	if(cardsDAO.getCardByCodice_fiscale(codice_fiscale).getCard_id() != Integer.parseInt(card_id)) {
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
		            if(email_booking != null && !email_booking.equals("") && InputChecker.checkEmail(email_booking)) {
		            	/*Se viene inserita l'email(facoltativa) controllo l'esistenza
		            	 * di un cliente registrato con quella email nel sistema.*/
		            	if(!customersDAO.checkExistingEmail(email_booking)) {
			            	request.setAttribute("error", "Non esiste un cliente registrato con l'indirizzo email: "+email_booking+".");
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
		            if(bookingsDAO.newBooking(email_booking, start_date.toString(), end_date.toString(), state, Integer.parseInt(card_id), book_id) != 0)
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
				try {
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

						if(bookingsDAO.updateBooking(booking_id, state) != 0)
							request.setAttribute("info_booking", "La prenotazione con codice: " + booking_id
									+ " è stata aggiornata.");
						else
							request.setAttribute("error", "Stato prenotazione non valido.");
				} catch(NumberFormatException e) {
					e.printStackTrace();
					request.setAttribute("error", "Non è stato possibile aggiornare la prenotazione. Codice prenotazione non valido.");
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

/**
 * Sezione Gestori
 */
			/* - Ricerca Gestori*/
			if(request.getParameter("keyword_manager") != null && !request.getParameter("keyword_manager").equals("")) {
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
			/* Pulsante modifica gestore premuto*/
			else if(request.getParameter("edit_manager") != null) {
				ManagersBean manager = managersDAO.getManagerByEmail(request.getParameter("edit_manager"));
				request.setAttribute("edit_manager", manager);
				request.getRequestDispatcher("admin.jsp?managers").forward(request, response);
				return;
			}
			/* - Aggiunta/Modifica Gestore*/
			else if(request.getParameter("new_manager") != null || request.getParameter("save_manager") != null) {
				ManagersBean manager = new ManagersBean();
				try {
					
					String manager_email = request.getParameter("email");
					String manager_password = request.getParameter("password");
					String name = request.getParameter("name");
					String surname = request.getParameter("surname");
					String address = request.getParameter("address");
					String phone = request.getParameter("phone");
					
					if(request.getParameter("save_manager") == null && (manager_email == null || manager_email.equals("") || !InputChecker.checkEmail(manager_email))) {
						request.setAttribute("error", "Indirizzo email non valido.");
						request.getRequestDispatcher("admin.jsp?managers").forward(request, response);
						return;
					}
					if(manager_password != null && !manager_password.equals("") && !InputChecker.checkPassword(manager_password)) {
						request.setAttribute("error", "La password deve avere tra i 6 e i 20 caratteri.");
						request.getRequestDispatcher("admin.jsp?managers").forward(request, response);
						return;
					}
					if(name == null || name.equals("") || !InputChecker.checkName(name)) {
						request.setAttribute("error", "Il nome può contenere solo lettere. Lunghezza massima: 30 caratteri.");
						request.getRequestDispatcher("admin.jsp?managers").forward(request, response);
						return;
					}
					if(surname == null || surname.equals("") || !InputChecker.checkSurname(surname)) {
						request.setAttribute("error", "Il cognome può contenere solo lettere. Lunghezza massima: 30 caratteri.");
						request.getRequestDispatcher("admin.jsp?managers").forward(request, response);
						return;
					}
					if(address == null || address.equals("") || !InputChecker.checkAddress(address)) {
						request.setAttribute("error", "L'indirizzo non è compilato correttamente.");
						request.getRequestDispatcher("admin.jsp?managers").forward(request, response);
						return;
					}
					if(phone == null || phone.equals("") || !InputChecker.checkPhone(phone)) {
						request.setAttribute("error", "Inserire un numero di telefono valido.");
						request.getRequestDispatcher("admin.jsp?managers").forward(request, response);
						return;
					}
					
					ArrayList<String> roles = new ArrayList<String>();
					if(request.getParameter("customers_manager") != null && request.getParameter("customers_manager").equals("Gestore Clienti"))
						roles.add("Gestore Clienti");
					if(request.getParameter("books_manager") != null && request.getParameter("books_manager").equals("Gestore Libri"))
						roles.add("Gestore Libri");
					if(request.getParameter("cards_manager") != null && request.getParameter("cards_manager").equals("Gestore Tessere"))
						roles.add("Gestore Tessere");
					if(request.getParameter("bookings_manager") != null && request.getParameter("bookings_manager").equals("Gestore Prenotazioni"))
						roles.add("Gestore Prenotazioni");
					if(request.getParameter("library_manager") != null && request.getParameter("library_manager").equals("Gestore Biblioteca"))
						roles.add("Gestore Biblioteca");
					manager.setEmail(manager_email);
					manager.setPassword(manager_password);
					manager.setName(name);
					manager.setSurname(surname);
					manager.setPhone(phone);
					manager.setAddress(address);
					manager.setRoles(roles);
			
					if(request.getParameter("save_manager") != null) {
						String original_email = request.getParameter("original_email");
						/*Controllo formato vecchio indirizzo email(solo in caso di Modifica Gestore)*/
						if(original_email == null || !InputChecker.checkEmail(original_email)) {
								request.setAttribute("error", "Inserire un indirizzo email valido.");
								request.getRequestDispatcher("admin.jsp?managers").forward(request, response);
								return;
						}
						/*Controllo se la nuova email sia già utilizzata da un altro gestore*/
						if(!manager_email.equals(original_email) && managersDAO.getManagerByEmail(manager_email) != null) {
							request.setAttribute("error", "Questo indirizzo email è già in uso.");
							request.getRequestDispatcher("admin.jsp?managers").forward(request, response);
							return;
						}
						/*Aggiorno il manager nel DB*/
						if(managersDAO.updateManager(manager, original_email) != 0)
							request.setAttribute("info_manager", "Il gestore " + manager.getName() +" "+ manager.getSurname() +" è stato aggiornato.");
						else
							request.setAttribute("error", "Si è verificato un errore.");
						
						request.getRequestDispatcher("admin.jsp?managers").forward(request, response);
						return;
					}
					else {
						
						if(managersDAO.newManager(manager) != 0)
							request.setAttribute("info_manager", "Il gestore " + manager.getName() +" "+ manager.getSurname() +" è stato aggiunto.");
						else
							request.setAttribute("error", "Si è verificato un errore.");
						
						request.getRequestDispatcher("admin.jsp?managers").forward(request, response);
						return;
					}
				}
				catch(Exception e) {
					e.printStackTrace();
					request.setAttribute("error", "Errore, qualche campo è vuoto "
							+ "oppure non è stato compilato correttamente.");
					request.getRequestDispatcher("admin.jsp?managers").forward(request, response);
					return;
				}
			}
			/* - Mostra tutti i gestori presenti nel database*/
			else if(request.getParameter("all_managers") != null) {
				ArrayList<ManagersBean> managers = managersDAO.getAllManagers();
				request.setAttribute("managers", managers);
			}
			/* - Cancellazione Gestore*/
			else if(request.getParameter("remove_manager") != null) {
				managersDAO.removeManager(request.getParameter("remove_manager"));
				request.setAttribute("info_manager", "Il Gestore:  "
				+ request.getParameter("remove_manager") + " è stato rimosso.");
			}
			
/**
 * Sezione Biblioteca
 */
			if(request.getParameter("save_library") != null) {
				Part filePart = request.getPart("file"); /*Serve per prelevare dal campo <input type="file" name="file">*/
				String name = request.getParameter("name");
				String contacts = request.getParameter("contacts");
				LibraryBean library = libraryDAO.getLibraryInfo();
				String fileName = filePart.getSubmittedFileName();
				if(fileName.equals("default_logo.png"))
					library.setLogo("./images/default_logo.png");
				if(name == null || contacts == null || !InputChecker.checkLibraryName(name) || !InputChecker.checkContacts(contacts)) {
					request.setAttribute("error", "Assicurati di aver compilato correttamente il nome e i contatti "
							+ "della biblioteca.");
					request.getRequestDispatcher("admin.jsp?library").forward(request, response);
					return;
				}
				/*Salvo la nuova immagine SOLO se l'admin ha cambiata, quindi
				 * nel campo input ci sarà un'immagine con dimensione diversa da 0 byte */	
				if(filePart.getSize() != 0 && !filePart.getContentType().equals("image/png") && !filePart.getContentType().equals("image/jpg") && !filePart.getContentType().equals("image/jpeg")) {
					request.setAttribute("error", "L'immagine deve avere il formato: .png, .jpg, oppure .jpeg.");
					request.getRequestDispatcher("admin.jsp?library").forward(request, response);
					return;
				}
				else if(filePart.getSize() > 0 && filePart.getSize() <= 2097152) {
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
				else if(filePart.getSize() > 2097152) {
					request.setAttribute("error", "Dimensione massima immagine consentita: 2MB.");
					request.getRequestDispatcher("admin.jsp?library").forward(request, response);
					return;
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
		else if(email == null || password == null) {
			response.sendRedirect("admin.jsp");
			return;
		}
		else {
			/*Autenticazione*/
			try {
				ManagersBean admin = managersDAO.login(email, password);
				if(admin != null) {
					session.setAttribute("admin", admin);
					session.removeAttribute("customer"); /*Distruggo la sessione per il clienti (nel caso in cui sia collegato)*/
				}
				else {
					request.setAttribute("login_error", "Indirizzo e-mail o password non validi.");
					request.getRequestDispatcher("admin.jsp").forward(request, response);
					return;
				}
			}
			catch(SQLException e) {
				System.out.println("Errore Database: " + e.getMessage());
				request.setAttribute("login_error", "Servizio al momento non disponibile. Riprovare più tardi.");
				request.getRequestDispatcher("admin.jsp").forward(request, response);
				return;
			}
		}
		/*Se l'autenticazione va a buon fine, il cliente viene reindirizzato all'
		 * homepage*/
		response.sendRedirect("admin.jsp");
		return;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}