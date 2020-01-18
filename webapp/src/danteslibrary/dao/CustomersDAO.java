package danteslibrary.dao;

import java.sql.*;

import danteslibrary.model.CustomersBean;
import danteslibrary.util.DBConnection;
import java.util.ArrayList;
import danteslibrary.util.BCrypt;

/**
 * Classe che si occupa dell’interfacciamento con il database per l’esecuzione
 * di query riguardanti oggetti Cliente.
 * 
 * @author Andrea Buongusto
 * @author Marco Salierno
 * 
 */
public class CustomersDAO {
	
	/**
	 * Ottiene il Cliente in base alle credenziali passate come parametro.
	 * @param email Email del Cliente da autenticare.
	 * @param password Password del Cliente da autenticare.
	 * @return Restituisce il Cliente se sono state trovate corrispondenze, altrimenti null.
	 * @throws SQLException Se il servizio non è disponibile (es. mancata connessione con il database).
	 */
	public CustomersBean login(String email, String password) throws SQLException {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM customers WHERE email = ?");
			ps.setString(1, email);
			ResultSet result = ps.executeQuery();
			
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			if(result.first()) {
				String hashed_password = result.getString("password");
				if(!BCrypt.checkpw(password, hashed_password))
					return null;
				/*Ottengo i dati del cliente dal DB*/
				String name = result.getString("name");
				String surname = result.getString("surname");
				String codice_fiscale = result.getString("codice_fiscale");
				String address = result.getString("address");
				
				conn.close();
				
				CustomersBean customer = new CustomersBean();
				customer.setName(name);
				customer.setSurname(surname);
				customer.setEmail(email);
				customer.setCodice_fiscale(codice_fiscale);
				customer.setAddress(address);
				
				return customer;
			}
			else {
				conn.close();
				return null;
			}
		}
		catch(IllegalArgumentException e) {
			System.out.println("Invalid salt version!");
			return null;
		}
	}
	
	/**
	 * Inserisce il Cliente passato come parametro nel database.
	 * La password viene cifrata con la funzione di hashing BCrypt.
	 * @param customer Bean che contiene le informazioni del Cliente da memorizzare.
	 * @return Restituisce true se l'inserimento è andato a buon fine,
	 * false altrimenti.
	 */
	public boolean register(CustomersBean customer) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("INSERT INTO customers(name, surname, email, password, "
					+ "codice_fiscale, address) VALUES(?, ?, ?, ?, ?, ?);");

			ps.setString(1, customer.getName());
			ps.setString(2, customer.getSurname());
			ps.setString(3, customer.getEmail());
			ps.setString(4, BCrypt.hashpw(customer.getPassword(), BCrypt.gensalt()));
			ps.setString(5,	customer.getCodice_fiscale());
			ps.setString(6, customer.getAddress());
			
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
	 * nella tabella customers.
	 * @param email Email da verificare.
	 * @return Restituisce true se esiste una corrispondenza nel database nella,
	 * tabella customers, false altrimenti.
	 */
	public boolean checkExistingEmail(String email) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT customers.email FROM customers WHERE email = ?");
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
	 * nella tabella customers.
	 * @param codice_fiscale Codice fiscale da verificare.
	 * @return Restituisce true se esiste una corrispondenza nel database nella
	 * tabella customers, false altrimenti.
	 */
	public boolean checkExistingCodice_fiscale(String codice_fiscale) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT customers.codice_fiscale FROM customers WHERE codice_fiscale = ?");
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
	 * Ottiene il Cliente avente email uguale a quella passata come parametro. 
	 * @param customer_email Email per identificare il Cliente da prelevare.
	 * @return Restituisce un Bean Cliente avente l'indirizzo email passato come
	 * parametro, null se non esiste.
	 */
	public CustomersBean getCustomerByEmail(String customer_email) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT customers.name, customers.surname, customers.email, customers.codice_fiscale, customers.address FROM customers WHERE email = ?");
			ps.setString(1, customer_email);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			if(result.first()) {
				/*Ottengo i dati del cliente dal DB*/
				String name = result.getString("name");
				String surname = result.getString("surname");
				String email = result.getString("email");
				String codice_fiscale = result.getString("codice_fiscale");
				String address = result.getString("address");
				
				conn.close();
				
				CustomersBean customer = new CustomersBean();
				customer.setEmail(email);
				customer.setName(name);
				customer.setSurname(surname);
				customer.setCodice_fiscale(codice_fiscale);
				customer.setAddress(address);
				
				return customer;
			}
			else {
				conn.close();
				return null;
			}
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getCustomerByEmail: " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Cerca all'interno del database i Clienti in base al filtro selezionato e 
	 * alle parole chiave passate come parametro.
	 * @param filter Filtro di ricerca. Vedere i filtri all'interno della
	 * pagina admin.jsp (Sezione Clienti).
	 * @param keyword Stringa che rappresenta le parole chiave.
	 * @return Restituisce una lista di clienti che rispettino filter e keyword.
	 * Restituisce null nel caso in cui non vi sono corrispondenze.
	 */
	public ArrayList<CustomersBean> getCustomersByFilter(int filter, String keyword) {
		String[] filters = {"name", "surname", "email", "codice_fiscale"};
		ArrayList<CustomersBean> customers = new ArrayList<CustomersBean>();
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT customers.name, customers.surname, customers.email, customers.codice_fiscale, customers.address FROM customers WHERE "+filters[filter]+" LIKE ?");
			ps.setString(1, "%"+keyword+"%");
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			/* Itero fin quando non ci sono piu' clienti */
			while(result.next()) {
				/*Ottengo i dati del cliente dal DB*/
				String name = result.getString("name");
				String surname = result.getString("surname");
				String email = result.getString("email");
				String codice_fiscale = result.getString("codice_fiscale");
				String address = result.getString("address");
				
				CustomersBean customer = new CustomersBean();
				customer.setEmail(email);
				customer.setName(name);
				customer.setSurname(surname);
				customer.setCodice_fiscale(codice_fiscale);
				customer.setAddress(address);
				
				/*Aggiungo il cliente all'Arraylist*/
				customers.add(customer);
			}
			conn.close();
			return customers;

		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getCustomersByFilter: " + e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Ottiene tutti i clienti presenti nel database.
	 * @return Restituisce una lista di tutti i clienti.
	 */
	public ArrayList<CustomersBean> getAllCustomers() {
		
		ArrayList<CustomersBean> customers = new ArrayList<CustomersBean>();
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT customers.name, customers.surname, customers.email, customers.codice_fiscale, customers.address FROM customers;");
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			while(result.next()) {
				/*Ottengo i dati dei clienti dal DB*/
				String email = result.getString("email");
				String name = result.getString("name");
				String surname = result.getString("surname");
				String codice_fiscale = result.getString("codice_fiscale");
				String address = result.getString("address");
				
				/*Costruisco il Bean da aggiungere poi all'array con tutti
				 * gli altri clienti*/
				CustomersBean customer = new CustomersBean();
				customer.setEmail(email);
				customer.setName(name);
				customer.setSurname(surname);
				customer.setCodice_fiscale(codice_fiscale);
				customer.setAddress(address);
				customers.add(customer);
			}
			conn.close();
			return customers;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getAllCustomers: " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Cancella un cliente dal database.
	 * @param email Email del cliente da cancellare.
	 * @return Restituisce 0 in caso di fallimento, altrimenti il numero di clienti
	 * cancellati (1).
	 */
	public int removeCustomer(String email) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM customers WHERE email = ?");
			ps.setString(1, email);
			result = ps.executeUpdate();
			conn.close();
			return result;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo removeCustomer: " + e.getMessage());
		}
		return result;
	}
	
	/**
	 * Memorizza nel database il link temporaneo (tmp_link) per il recupero 
	 * password associandolo al cliente avente come email quella passata come 
	 * parametro.
	 * @param email Email del cliente al quale associare il link temporaneo.
	 * @param tmp_link Il link temporaneo da associare al cliente.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti.
	 */
	public int setTemporaryLink(String email, String tmp_link) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("UPDATE customers SET tmp_link = ? WHERE email = ? ");
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
	 * Cancella il link temporaneo associato al Cliente avente come email quella
	 * passata come parametro.
	 * @param email Email del Cliente al quale si vuole rimuovere il link temporaneo.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti.
	 */
	public int deleteTemporaryLink(String email) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("UPDATE customers SET tmp_link = NULL WHERE email = ? ");
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
	 * Ottiene l'email del cliente al quale è stato associato il link temporaneo
	 * passato come parametro. Vedere anche il metodo setTemporaryLink.
	 * @param tmp_link Il link temporaneo con il quale si vuole ottenere l'email.
	 * @return Restituisce l'email del cliente al quale è stato associato il link
	 * temporaneo in caso di successo, altrimenti null.
	 */
	public String getEmailByTemporaryLink(String tmp_link) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT customers.email FROM customers WHERE tmp_link = ?");
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
	 * Aggiorna la password del cliente avente come email quella passata come
	 * parametro con la nuova password (new_password).
	 * @param email Email del cliente al quale si vuole cambiare la password.
	 * @param new_password La nuova password del cliente.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti.
	 */
	public int updateCustomerPassword(String email, String new_password) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("UPDATE customers SET password = ? WHERE email = ? ");
			ps.setString(1, BCrypt.hashpw(new_password, BCrypt.gensalt()));
			ps.setString(2, email);
			result = ps.executeUpdate();
			conn.close();
			return result;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo updateCustomerPassword: " + e.getMessage());
		}
		return result;
	}
	
	/**
	 * Aggiorna le informazioni del Cliente passato come parametro (il bean customer).
	 * Il parametro old_email rappresenta il vecchio indirizzo email nel caso in
	 * cui il cliente abbia modificato anche la sua email. Se l'ha modificata, questa
	 * verrà sovrascritta con quella presente nel bean. La vecchia email mi serve
	 * per identificare il cliente al quale apportare le modifiche.
	 * @param customer Contiene le informazioni del cliente da aggiornare (compreso l'eventuale
	 * nuovo indirizzo email).
	 * @param old_email Email del cliente da aggiornare. Verrà sovrascritto con
	 * l'indirizzo email contenuto nel bean.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti.
	 * @throws SQLException Lanciata in caso di malfunzionamento, problemi di connessione.
	 */
	public int updateCustomer (CustomersBean customer, String old_email) throws SQLException {
		Connection conn = null;
		int result = 0;
		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement("UPDATE customers SET email= ?, address = ? WHERE customers.email = ?");
			ps.setString(1, customer.getEmail());
			ps.setString(2, customer.getAddress());
			ps.setString(3, old_email);
			ps.executeUpdate();
			ps = conn.prepareStatement("UPDATE bookings SET email = ? WHERE email = ?");
			ps.setString(1, customer.getEmail());
			ps.setString(2, old_email);
			result = ps.executeUpdate();
			conn.commit();
		}
		catch(SQLException e) {
			if(conn != null) {
				conn.rollback();
				System.out.println("\nRollback! Errore Database metodo updateCustomer: " + e.getMessage());
				return result = 0;
			}
			
		} finally {
			conn.setAutoCommit(true);
			conn.close();
		}
		return result;	
	}
	
}