package danteslibrary.dao;

import java.sql.*;

import danteslibrary.model.UsersBean;
import danteslibrary.util.DBConnection;
import java.util.ArrayList;
import danteslibrary.util.BCrypt;

/**
 * Classe che si occupa dell’interfacciamento con il database per l’esecuzione
 * di query riguardanti oggetti Utente.
 * 
 * @author Andrea Buongusto
 * @author Marco Salierno
 * 
 */
public class UsersDAO {
	
	/**
	 * Ottiene l'Utente in base alle credenziali passate come parametro.
	 * @param email Email dell'Utente da autenticare.
	 * @param password Password dell'Utente da autenticare.
	 * @return Restituisce l'Utente se sono state trovate corrispondenze, altrimenti null.
	 */
	public UsersBean login(String email, String password) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
			ps.setString(1, email);
			ResultSet result = ps.executeQuery();
			
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			if(result.first()) {
				String hashed_password = result.getString("password");
				if(!BCrypt.checkpw(password, hashed_password))
					return null;
				/*Ottengo i dati dell'utente dal DB*/
				String name = result.getString("name");
				String surname = result.getString("surname");
				String codice_fiscale = result.getString("codice_fiscale");
				String address = result.getString("address");
				
				conn.close();
				
				UsersBean user = new UsersBean();
				user.setName(name);
				user.setSurname(surname);
				user.setEmail(email);
				user.setCodice_fiscale(codice_fiscale);
				user.setAddress(address);
				
				return user;
			}
			else {
				conn.close();
				return null;
			}
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo login: " + e.getMessage());
		}
		catch(IllegalArgumentException e) {
			System.out.println("Invalid salt version!");
			return null;
		}
		
		return null;
	}
	
	/**
	 * Inserisce l 'Utente passato come parametro nel database.
	 * La password viene cifrata con la funzione di hashing BCrypt.
	 * @param user Bean che contiene le informazioni dell'Utente da memorizzare.
	 * @return Restituisce true se l'inserimento è andato a buon fine,
	 * false altrimenti.
	 */
	public boolean register(UsersBean user) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("INSERT INTO users(name, surname, email, password, "
					+ "codice_fiscale, address) VALUES(?, ?, ?, ?, ?, ?);");

			ps.setString(1, user.getName());
			ps.setString(2, user.getSurname());
			ps.setString(3, user.getEmail());
			ps.setString(4, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
			ps.setString(5,	user.getCodice_fiscale());
			ps.setString(6, user.getAddress());
			
			ps.executeUpdate();
				
			conn.close();
			
			return true;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo register: " + e.getMessage());
		}
		
		return false;
	}
	
	/**
	 * Controlla se l'email passata come parametro è memorizzata nel database
	 * nella tabella users.
	 * @param email Email da verificare.
	 * @return Restituisce true se esiste una corrispondenza nel database nella,
	 * tabella users, false altrimenti.
	 */
	public boolean checkExistingEmail(String email) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT users.email FROM users WHERE email = ?");
			ps.setString(1, email);
			ResultSet result = ps.executeQuery();
			
			if(!result.isBeforeFirst()) { /*Nessuna corrispondenza trovata nel DB, restituisco false*/
				conn.close();
				return false;
			}
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo checkExistingEmail: " + e.getMessage());
		}
		
		return true;
	}
	
	/**
	 * Controlla se il codice fiscale passato come parametro è memorizzato nel database
	 * nella tabella users.
	 * @param codice_fiscale Codice fiscale da verificare.
	 * @return Restituisce true se esiste una corrispondenza nel database nella
	 * tabella users, false altrimenti.
	 */
	public boolean checkExistingCodice_fiscale(String codice_fiscale) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT users.codice_fiscale FROM users WHERE codice_fiscale = ?");
			ps.setString(1, codice_fiscale);
			ResultSet result = ps.executeQuery();
			
			if(!result.isBeforeFirst()) { /*Nessuna corrispondenza trovata nel DB, restituisco false*/
				conn.close();
				return false;
			}
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo checkExistingCodiceFiscale: " + e.getMessage());
		}
		
		return true;
	}
	
	/**
	 * Ottiene l'Utente avente email uguale a quella passata come parametro. 
	 * @param user_email Email per identificare l'Utente da prelevare.
	 * @return Restituisce un Bean Utente avente l'indirizzo email passato come
	 * parametro, null se non esiste.
	 */
	public UsersBean getUserByEmail(String user_email) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT users.name, users.surname, users.email, users.codice_fiscale, users.address FROM users WHERE email = ?");
			ps.setString(1, user_email);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			if(result.first()) {
				/*Ottengo i dati dell'utente dal DB*/
				String name = result.getString("name");
				String surname = result.getString("surname");
				String email = result.getString("email");
				String codice_fiscale = result.getString("codice_fiscale");
				String address = result.getString("address");
				
				conn.close();
				
				UsersBean user = new UsersBean();
				user.setEmail(email);
				user.setName(name);
				user.setSurname(surname);
				user.setCodice_fiscale(codice_fiscale);
				user.setAddress(address);
				
				return user;
			}
			else {
				conn.close();
				return null;
			}
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getUserByEmail: " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Cerca all'interno del database gli Utenti in base al filtro selezionato e 
	 * alle parole chiave passate come parametro.
	 * @param filter Filtro di ricerca. Vedere i filtri all'interno della
	 * pagina admin.jsp (Sezione Utenti).
	 * @param keyword Stringa che rappresenta le parole chiave.
	 * @return Restituisce una lista di utenti che rispettino filter e keyword.
	 * Restituisce null nel caso in cui non vi sono corrispondenze.
	 */
	public ArrayList<UsersBean> getUsersByFilter(int filter, String keyword) {
		String[] filters = {"name", "surname", "email", "codice_fiscale"};
		ArrayList<UsersBean> users = new ArrayList<UsersBean>();
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT users.name, users.surname, users.email, users.codice_fiscale, users.address FROM users WHERE "+filters[filter]+" = ?");
			ps.setString(1, keyword);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			/* Itero fin quando non ci sono piu' utenti */
			while(result.next()) {
				/*Ottengo i dati dell'utente dal DB*/
				String name = result.getString("name");
				String surname = result.getString("surname");
				String email = result.getString("email");
				String codice_fiscale = result.getString("codice_fiscale");
				String address = result.getString("address");
				
				UsersBean user = new UsersBean();
				user.setEmail(email);
				user.setName(name);
				user.setSurname(surname);
				user.setCodice_fiscale(codice_fiscale);
				user.setAddress(address);
				
				/*Aggiungo l'utente all'Arraylist*/
				users.add(user);
			}
			conn.close();
			return users;

		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getUsersByFilter: " + e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Ottiene tutte gli utenti presenti nel database.
	 * @return Restituisce una lista di tutti gli utenti.
	 */
	public ArrayList<UsersBean> getAllUsers() {
		
		ArrayList<UsersBean> users = new ArrayList<UsersBean>();
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT users.name, users.surname, users.email, users.codice_fiscale, users.address FROM users;");
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			while(result.next()) {
				/*Ottengo i dati dell'utente dal DB*/
				String email = result.getString("email");
				String name = result.getString("name");
				String surname = result.getString("surname");
				String codice_fiscale = result.getString("codice_fiscale");
				String address = result.getString("address");
				
				/*Costruisco il Bean da aggiungere poi all'array con tutti
				 * gli altri utenti*/
				UsersBean user = new UsersBean();
				user.setEmail(email);
				user.setName(name);
				user.setSurname(surname);
				user.setCodice_fiscale(codice_fiscale);
				user.setAddress(address);
				users.add(user);
			}
			conn.close();
			return users;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getAllUsers: " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Cancella un utente dal database.
	 * @param email Email dell'utente da cancellare.
	 * @return Restituisce 0 in caso di fallimento, altrimenti il numero di utenti
	 * cancellati (1).
	 */
	public int removeUser(String email) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE email = ?");
			ps.setString(1, email);
			result = ps.executeUpdate();
			conn.close();
			return result;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo removeUser: " + e.getMessage());
		}
		return result;
	}
	
	/**
	 * Memorizza nel database il link temporaneo (tmp_link) per il recupero 
	 * password associandolo all'utente avente come email quella passata come 
	 * parametro.
	 * @param email Email dell'utente al quale associare il link temporaneo.
	 * @param tmp_link Il link temporaneo da associare all'utente.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti.
	 */
	public int setTemporaryLink(String email, String tmp_link) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("UPDATE users SET tmp_link = ? WHERE email = ? ");
			ps.setString(1, tmp_link);
			ps.setString(2, email);
			result = ps.executeUpdate();
			conn.close();
			return result;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo setTemporaryLink: " + e.getMessage());
		}
		return result;
	}
	
	/**
	 * Cancella il link temporaneo associato all'Utente avente come email quella
	 * passata come parametro.
	 * @param email Email dell'Utente al quale si vuole rimuovere il link temporaneo.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti.
	 */
	public int deleteTemporaryLink(String email) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("UPDATE users SET tmp_link = NULL WHERE email = ? ");
			ps.setString(1, email);
			result = ps.executeUpdate();
			conn.close();
			return result;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo deleteTemporaryLink: " + e.getMessage());
		}
		return result;
	}
	
	/**
	 * Ottiene l'email dell'utente al quale è stato associato il link temporaneo
	 * passato come parametro. Vedere anche il metodo setTemporaryLink.
	 * @param tmp_link Il link temporaneo con il quale si vuole ottenere l'email.
	 * @return Restituisce l'email dell'utente al quale è stato associato il link
	 * temporaneo in caso di successo, altrimenti null.
	 */
	public String getEmailByTemporaryLink(String tmp_link) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT users.email FROM users WHERE tmp_link = ?");
			ps.setString(1, tmp_link);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) {
				conn.close();
				return null;
			}
			result.first();
			String email = result.getString("email");
			conn.close();
			return email;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getEmailByTemporaryLink: " + e.getMessage());
			return null;
		}	
	}
	
	/**
	 * Aggiorna la password dell'utente avente come email quella passata come
	 * parametro con la nuova password (new_password).
	 * @param email Email dell'utente al quale si vuole cambiare la password.
	 * @param new_password La nuova password dell'utente.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti.
	 */
	public int updateUserPassword(String email, String new_password) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("UPDATE users SET password = ? WHERE email = ? ");
			ps.setString(1, BCrypt.hashpw(new_password, BCrypt.gensalt()));
			ps.setString(2, email);
			result = ps.executeUpdate();
			conn.close();
			return result;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo updateUserPassword: " + e.getMessage());
		}
		return result;
	}
	
	/**
	 * Aggiorna le informazioni dell'Utente passato come parametro (il bean user).
	 * Il parametro old_email rappresenta il vecchio indirizzo email nel caso in
	 * cui l'utente abbia modificato anche la sua email. Se l'ha modificata, questa
	 * verrà sovrascritta con quella presente nel bean. La vecchia email mi serve
	 * per identificare l'utente al quale apportare le modifiche.
	 * @param user Contiene le informazioni dell'utente da aggiornare (compreso l'eventuale
	 * nuovo indirizzo email).
	 * @param old_email Email dell'utente da aggiornare. Verrà sovrascritto con
	 * l'indirizzo email contenuto nel bean.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti.
	 * @throws SQLException Lanciata in caso di malfunzionamento, problemi di connessione.
	 */
	public int updateUser (UsersBean user, String old_email) throws SQLException {
		Connection conn = null;
		int result = 0;
		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement("UPDATE users SET email= ?, address = ? WHERE users.email = ?");
			ps.setString(1, user.getEmail());
			ps.setString(2, user.getAddress());
			ps.setString(3, old_email);
			ps.executeUpdate();
			ps = conn.prepareStatement("UPDATE bookings SET email = ? WHERE email = ?");
			ps.setString(1, user.getEmail());
			ps.setString(2, old_email);
			result = ps.executeUpdate();
			conn.commit();
		}
		catch(SQLException e) {
			if(conn != null) {
				conn.rollback();
				System.out.println("\nRollback! Errore Database metodo updateUser: " + e.getMessage());
				return result = 0;
			}
			
		} finally {
			conn.setAutoCommit(true);
			conn.close();
		}
		return result;	
	}
	
}