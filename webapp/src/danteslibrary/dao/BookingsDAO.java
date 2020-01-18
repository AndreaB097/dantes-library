package danteslibrary.dao;

import java.sql.*;
import java.time.LocalDate;

import danteslibrary.util.DBConnection;
import danteslibrary.model.BookingsBean;

import java.util.ArrayList;

/**
 * Classe che si occupa dell’interfacciamento con il database per l’esecuzione
 * di query riguardanti oggetti Prenotazione.
 * 
 * @author Andrea Buongusto
 * @author Marco Salierno
 * 
 */
public class BookingsDAO {
	/**
	 * 
	 * Inserisce una nuova prenotazione nel database.
	 * 
	 * @param email Email del cliente che fa la prenotazione.
	 * @param start_date Data d'inizio prenotazione.
	 * @param end_date Data di fine prenotazione.
	 * @param state_name Stato della nuova prenotazione. Nel caso la prenotazione la stia creando un cliente, il valore
	 * sarà "Non ancora ritirato".
	 * @param card_id Codice della tessera con cui si vuole prenotare il libro.
	 * @param book_id Il libro da prenotare.
	 * @return Restituisce 0 nel caso in cui non ci sono stati cambiamenti,
	 * altrimenti il numero di righe cambiate.
	 * @throws SQLException Lanciata in caso di malfunzionamento, problemi di connessione.
	 */
	public int newBooking(String email, String start_date, String end_date, String state_name, int card_id, int book_id) throws SQLException {
		int result = 0;
		Connection conn = null;
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement("SELECT books.title FROM books WHERE book_id = ?");
			ps.setInt(1, book_id);
			ResultSet rs = ps.executeQuery();
			rs.first();
			String title = rs.getString("title");
			
			ps = conn.prepareStatement("INSERT INTO bookings(email, start_date, end_date, state_name, card_id, book_id, title) "
					+ "VALUES(?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, email);
			ps.setString(2, start_date);
			ps.setString(3, end_date);
			ps.setString(4, state_name);
			ps.setInt(5, card_id);
			ps.setInt(6, book_id);
			ps.setString(7, title);
			
			result =  ps.executeUpdate();
			
			ps = conn.prepareStatement("UPDATE books SET quantity = quantity - 1 WHERE book_id = ?");
			ps.setInt(1, book_id);
			result = ps.executeUpdate();
		try {	
			conn.commit();
		}
		catch(SQLException e) {
			if(conn != null) {
				System.out.println("\nRollback! Errore Database metodo newBooking: " + e.getMessage());
				conn.rollback();
				return 0;
			}
		} finally {
			conn.setAutoCommit(true);
			conn.close();
		}
		return result;
	}
	
	/**
	 * Cerca all'interno del database le prenotazioni in base al filtro selezionato e 
	 * alle parole chiave passate come parametro.
	 * @param filter Filtro di ricerca. Vedere i filtri all'interno della
	 * pagina admin.jsp (Sezione Prenotazioni).
	 * @param keyword Stringa che rappresenta le parole chiave.
	 * @return Restituisce una lista di prenotazioni che rispettino filter e keyword.
	 * Restituisce null nel caso in cui non vi sono corrispondenze.
	 */
	public ArrayList<BookingsBean> getBookingsByFilter(int filter, String keyword) {
		String[] filters = {"booking_id", "book_id", "bookings.card_id", "cards.codice_fiscale", "state_name", "email", "start_date", "end_date"};
		ArrayList<BookingsBean> bookings = new ArrayList<BookingsBean>();
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT bookings.booking_id, bookings.card_id, bookings.book_id, cards.codice_fiscale, bookings.state_name, bookings.start_date, bookings.end_date, bookings.title  FROM bookings, cards WHERE "+filters[filter]+" LIKE ? AND cards.card_id = bookings.card_id");
			ps.setString(1, "%"+keyword+"%");
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			while(result.next()) {
				int booking_id = result.getInt("booking_id");
				int card_id = result.getInt("card_id");
				int book_id = result.getInt("book_id");
				String title = result.getString("title");
				String codice_fiscale = result.getString("codice_fiscale");
				String state_name = result.getString("state_name");
				LocalDate start_date = result.getDate("start_date").toLocalDate();
				LocalDate end_date = result.getDate("end_date").toLocalDate();
				
				BookingsBean booking = new BookingsBean();
				booking.setBooking_id(booking_id);
				booking.setCard_id(card_id);
				booking.setBook_id(book_id);
				booking.setTitle(title);
				booking.setCodice_fiscale(codice_fiscale);
				booking.setState_name(state_name);
				booking.setStart_date(start_date);
				booking.setEnd_date(end_date);
	
				bookings.add(booking);
			}
			conn.close();
			return bookings;
		}
		catch(SQLException e) {
			System.out.println("Errore Database: " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Ottiene tutte le prenotazioni presenti nel database.
	 * @return Restituisce una lista di tutte le prenotazioni.
	 */
	public ArrayList<BookingsBean> getAllBookings() {
		
		ArrayList<BookingsBean> bookings = new ArrayList<BookingsBean>();
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT bookings.booking_id, bookings.card_id, bookings.book_id, cards.codice_fiscale, bookings.state_name, bookings.start_date, bookings.end_date, bookings.title  FROM bookings, cards WHERE  cards.card_id = bookings.card_id");
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			while(result.next()) {
				int booking_id = result.getInt("booking_id");
				int card_id = result.getInt("card_id");
				int book_id = result.getInt("book_id");
				String title = result.getString("title");
				String codice_fiscale = result.getString("codice_fiscale");
				String state_name = result.getString("state_name");
				LocalDate start_date = result.getDate("start_date").toLocalDate();
				LocalDate end_date = result.getDate("end_date").toLocalDate();
				
				BookingsBean booking = new BookingsBean();
				booking.setBooking_id(booking_id);
				booking.setCard_id(card_id);
				booking.setBook_id(book_id);
				booking.setTitle(title);
				booking.setCodice_fiscale(codice_fiscale);
				booking.setState_name(state_name);
				booking.setStart_date(start_date);
				booking.setEnd_date(end_date);

				bookings.add(booking);
			}
			conn.close();
			return bookings;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getAllBookings: " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Cancella una prenotazione dal database.
	 * @param booking_id Codice della prenotazione da cancellare.
	 * @return Restituisce 0 in caso di fallimento, altrimenti il numero di prenotazioni
	 * cancellate (1).
	 * @throws SQLException Lanciata in caso di malfunzionamento, problemi di connessione.
	 */
	public int removeBooking(int booking_id) throws SQLException {
		int result = 0;
		Connection conn = null;
		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM bookings WHERE booking_id = ?");
			ps.setInt(1, booking_id);
			ResultSet rs = ps.executeQuery();
			rs.first();
			String booking_state = rs.getString("state_name");
			int book_id = rs.getInt("book_id");
			if(!booking_state.equals("Annullata") && !booking_state.equals("Riconsegnato")) {
				ps = conn.prepareStatement("UPDATE books SET quantity = quantity + 1 WHERE book_id = ?");
				ps.setInt(1, book_id);
				ps.executeUpdate();
			}
			ps = conn.prepareStatement("DELETE FROM bookings WHERE booking_id = ?");
			ps.setInt(1, booking_id);
			result = ps.executeUpdate();
			conn.commit();
		}
		catch(SQLException e) {
			if(conn != null) {
				conn.rollback();
				System.out.println("\nRollback! Errore Database metodo removeBooking: " + e.getMessage());
				return 0;
			}
		} finally {
			conn.setAutoCommit(true);
			conn.close();
		}
		return result;
	}
	
	/**
	 * Ottiene le prenotazioni effettuate da un Cliente data la sua email.
	 * @param email Email del cliente del quale si vogliono ottenere le prenotazioni.
	 * @return Restituisce la lista delle prenotazioni effettuate da un cliente.
	 */
	public ArrayList<BookingsBean> getCustomerBookings(String email) {
		ArrayList<BookingsBean> bookings = new ArrayList<BookingsBean>();
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT bookings.* FROM bookings WHERE bookings.email = ?");
			ps.setString(1, email);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			while(result.next()) {
				int booking_id = result.getInt("booking_id");
				int card_id = result.getInt("card_id");
				int book_id = result.getInt("book_id");
				String title = result.getString("title");
				String state_name = result.getString("state_name");
				LocalDate start_date = result.getDate("start_date").toLocalDate();
				LocalDate end_date = result.getDate("end_date").toLocalDate();
				
				BookingsBean booking = new BookingsBean();
				booking.setBooking_id(booking_id);
				booking.setCard_id(card_id);
				booking.setBook_id(book_id);
				booking.setTitle(title);
				booking.setState_name(state_name);
				booking.setStart_date(start_date);
				booking.setEnd_date(end_date);

				bookings.add(booking);
			}
			conn.close();
			return bookings;
		}
		catch(SQLException e) {
			System.out.println("Errore Database: " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Ottiene la prenotazione in base al codice prenotazione (booking_id) 
	 * passato come parametro.
	 * @param booking_id Codice della prenotazione da ottenere.
	 * @return Restituisce la prenotazione avente come id booking_id, 
	 * null altrimenti.
	 */
	public BookingsBean getBookingById(int booking_id) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM bookings WHERE bookings.booking_id = ?");
			ps.setInt(1,booking_id);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet è vuoto, allora la query non ha prodotto risultati*/
				return null;
			BookingsBean booking = new BookingsBean();		
			result.first();
			int card_id = result.getInt("card_id");
			String email = result.getString("email");
			int book_id = result.getInt("book_id");
			String title = result.getString("title");
			String state_name = result.getString("state_name");
			LocalDate start_date = result.getDate("start_date").toLocalDate();
			LocalDate end_date = result.getDate("end_date").toLocalDate();
			booking.setCard_id(card_id);
			booking.setBook_id(book_id);
			booking.setTitle(title);
			booking.setEmail(email);
			booking.setState_name(state_name);
			booking.setStart_date(start_date);
			booking.setEnd_date(end_date);
			booking.setBooking_id(booking_id);
			ps = conn.prepareStatement("SELECT codice_fiscale FROM cards WHERE card_id = ?");
			ps.setInt(1,card_id);
			result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet e' vuoto, allora la query non ha prodotto risultati*/
				return null;
			result.first();
			String codice_fiscale = result.getString("codice_fiscale");
			booking.setCodice_fiscale(codice_fiscale);
			conn.close();
			return booking;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getBookingById: " + e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Aggiorna lo stato di una prenotazione.
	 * @param booking_id Il codice prenotazione della prenotazione da modificare.
	 * @param state Il nuovo stato da assegnare alla prenotazione.
	 * @return 0 in caso di fallimento. Altrimenti il numero di righe modifcate.
	 * @throws SQLException Lanciata in caso di malfunzionamento, problemi di connessione.
	 */
	public int updateBooking(int booking_id, String state) throws SQLException {
		int result = 0;
		Connection conn = null;
		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement ps;
			if(state.equals("Annullata") || state.equals("Riconsegnato")) {
				ps = conn.prepareStatement("SELECT * FROM bookings WHERE booking_id = ?");
				ps.setInt(1, booking_id);
				ResultSet rs = ps.executeQuery();
				rs.first();
				int book_id = rs.getInt("book_id");
				ps = conn.prepareStatement("UPDATE books SET quantity = quantity + 1 WHERE books.book_id = ?");
				ps.setInt(1, book_id);
				ps.executeUpdate();
			}
			ps = conn.prepareStatement("UPDATE bookings SET state_name= ? WHERE bookings.booking_id = ?");
			ps.setString(1, state);
			ps.setInt(2, booking_id);
			result = ps.executeUpdate();
			conn.commit();
		} catch(SQLException e) {
			if(conn != null) {
				conn.rollback();
				System.out.println("\nRollback! Errore Database metodo updateBooking: " + e.getMessage());
				return 0;
			}
		} finally {
			conn.setAutoCommit(true);
			conn.close();
		}
	return result;
	}

}
