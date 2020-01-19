package danteslibrary.dao;

import java.sql.*;
import danteslibrary.util.DBConnection;
import danteslibrary.model.CardsBean;

import java.util.ArrayList;

/**
 * Classe che si occupa dell’interfacciamento con il database per l’esecuzione
 * di query riguardanti oggetti Tessera.
 * 
 * @author Andrea Buongusto
 * @author Marco Salierno
 * 
 */
public class CardsDAO {

	/**
	 * Cerca all'interno del database i le tessere in base al filtro selezionato e 
	 * alle parole chiave passate come parametro.
	 * @param filter Filtro di ricerca. Vedere i filtri all'interno della pagina
	 * admin.jsp (Sezione Tessere).
	 * @param keyword Stringa che rappresenta le parole chiave.
	 * @return Restituisce una lista di tessere che rispettino filter e keyword.
	 * Restituisce null nel caso in cui non vi sono corrispondenze.
	 */
	public ArrayList<CardsBean> getCardsByFilter(int filter, String keyword) {
		if(keyword.length() <= 0)
			return null;
		String[] filters = {"customers.name", "customers.surname", "customers.email", "cards.codice_fiscale", "card_id"};
		ArrayList<CardsBean> cards = new ArrayList<CardsBean>();
		ResultSet result;
		try {
			Connection conn = DBConnection.getConnection();
			if (filter == 0 || filter == 1 || filter == 2) {
				PreparedStatement ps = conn.prepareStatement("SELECT customers.name, customers.surname,"
						+ " cards.card_id, cards.codice_fiscale, customers.email, cards.associated " 
						+ "FROM cards, customers WHERE " + filters[filter] + " LIKE ? AND cards.codice_fiscale = customers.codice_fiscale");
			
				ps.setString(1, "%"+keyword+"%");
				result = ps.executeQuery();
				if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
					return null;
			
				while(result.next()) {
					/*Ottengo i dati del cliente dal DB*/
					String name = result.getString("name");
					String surname = result.getString("surname");
					String email = result.getString("email");
					String codice_fiscale = result.getString("codice_fiscale");
					int card_id = result.getInt("card_id");
					boolean associated = result.getBoolean("associated");
					
					CardsBean card = new CardsBean();
					card.setEmail(email);
					card.setName(name);
					card.setSurname(surname);
					card.setCodice_fiscale(codice_fiscale);
					card.setCard_id(card_id);
					card.setAssociated(associated);
					cards.add(card);
				
				}	
			}
			if (filter == 3 || filter == 4) {
				PreparedStatement ps = conn.prepareStatement("SELECT cards.card_id, cards.codice_fiscale, cards.associated " 
						+ "FROM cards WHERE " + filters[filter] + " = ?");
				ps.setString(1, keyword);
				result = ps.executeQuery();
				/*Se la tessera appartiene ad un cliente registrato ne prelevo anche nome, cognome e email */

				if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
					return null;
				
				result.first();

				String codice_fiscale = result.getString("codice_fiscale");
				int card_id = result.getInt("card_id");
				boolean associated = result.getBoolean("associated");
				
				CardsBean card = new CardsBean();
				card.setCodice_fiscale(codice_fiscale);
				card.setCard_id(card_id);
				card.setAssociated(associated);

				ps = conn.prepareStatement("SELECT customers.name, customers.surname, customers.email " 
						+ "FROM customers, cards WHERE customers.codice_fiscale = cards.codice_fiscale "
						+ "AND "  + filters[filter] + " = ?");
				ps.setString(1, keyword);
				result = ps.executeQuery();
				String name, surname, email;
				if(!result.isBeforeFirst()) { /*Nessuna corrispondenza trovata nel DB, restituisco null*/
					name = "";
					surname = "";
					email = "";
				}
				else {
					result.first();
					name = result.getString("name");
					surname = result.getString("surname");
					email = result.getString("email");
				}
			
				card.setName(name);
				card.setSurname(surname);
				card.setEmail(email);
				cards.add(card);
			}
			
			conn.close();
			return cards;

		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getCardsByFilter: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Ottiene una lista di tutte le tessere presenti nel database.
	 * @return Restituisce una lista di tutte le tessere presenti nel database.
	 */
	public ArrayList<CardsBean> getAllCards() {
		ArrayList<CardsBean> cards = new ArrayList<CardsBean>();
		ResultSet result;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT cards.*, customers.name, customers.surname, customers.email " 
					+ "FROM customers, cards WHERE cards.associated= true AND customers.codice_fiscale = cards.codice_fiscale");
			result = ps.executeQuery();
			if(result.isBeforeFirst()) {
				while (result.next()) {
					CardsBean card = new CardsBean();
					card.setCodice_fiscale(result.getString("codice_fiscale"));
					card.setCard_id(result.getInt("card_id"));
					card.setAssociated(result.getBoolean("associated"));
					card.setName(result.getString("name"));
					card.setSurname(result.getString("surname"));
					card.setEmail(result.getString("email"));
					cards.add(card);	
			   }
			}
			else cards = null;
			if (cards == null)
				cards = new ArrayList<CardsBean>();
			ps = conn.prepareStatement("SELECT cards.card_id, cards.codice_fiscale, cards.associated " 
						+ "FROM cards WHERE cards.associated= false");
				result = ps.executeQuery();
				if(result.isBeforeFirst()) {
					while(result.next()) {
						CardsBean card = new CardsBean();
						card.setCodice_fiscale(result.getString("codice_fiscale"));
						card.setCard_id(result.getInt("card_id"));
						card.setAssociated(result.getBoolean("associated"));
						card.setName("");
						card.setSurname("");
						card.setEmail("");
						cards.add(card);
						}
				}	
				
			conn.close();
			return cards;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getAllCards: " + e.getMessage());
		}
		return null;	
	}
	
	/**
	 * Cancella la tessera avente come codice quello passato come parametro.
	 * @param card_id Codice della tessera da cancellare.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti.
	 */
	public int removeCard(int card_id) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM cards WHERE card_id = ?");
			ps.setInt(1, card_id);
			result = ps.executeUpdate();
			conn.close();
			return result;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo removeCard: " + e.getMessage());
			return result;
		}
	}
	
	/**
	 * Ottiene la tessera in base all'email passata come parametro.
	 * @param email Email del cliente di cui si vuole ottenere la tessera.
	 * @return Restituisce la tessera del cliente avente come email quella 
	 * passata come parametro.
	 */
	public CardsBean getCardByEmail(String email) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT cards.* FROM cards, customers WHERE cards.codice_fiscale = customers.codice_fiscale "
					+ "AND customers.email = ?");
			ps.setString(1, email);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst())
				return null;
			result.first();
			CardsBean card = new CardsBean();
			card.setCard_id(result.getInt("card_id"));
			card.setCodice_fiscale(result.getString("codice_fiscale"));
			card.setAssociated(result.getBoolean("associated"));
				
			conn.close();
			return card;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getCardByEmail: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Ottiene la tessera in base al codice fiscale passato come parametro.
	 * @param codice_fiscale Codice fiscale del cliente di cui si vuole ottenere la tessera.
	 * @return Restituisce la tessera del cliente avente come codice fiscale quello
	 * passato come parametro.
	 */
	public CardsBean getCardByCodice_fiscale(String codice_fiscale) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT cards.* FROM cards WHERE "
					+ "cards.codice_fiscale = ?");
			ps.setString(1, codice_fiscale);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst())
				return null;
			result.first();
			CardsBean card = new CardsBean();
			card.setCard_id(result.getInt("card_id"));
			card.setCodice_fiscale(result.getString("codice_fiscale"));
			card.setAssociated(result.getBoolean("associated"));
				
			conn.close();
			return card;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getCardByCodice_fiscale: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Ottiene la tessera in base al codice tessera passata come parametro.
	 * @param card_id Codice tessera della tessera da ottenere.
	 * @return Restituisce la tessera in base al codice passato come parametro.
	 */
	public CardsBean getCardById(int card_id) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT cards.* FROM cards WHERE cards.card_id = ?");
			ps.setInt(1, card_id);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst())
				return null;
			result.first();
			CardsBean card = new CardsBean();
			card.setCard_id(result.getInt("card_id"));
			card.setCodice_fiscale(result.getString("codice_fiscale"));
			card.setAssociated(result.getBoolean("associated"));
				
			conn.close();
			return card;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getCardById: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Imposta a true il valore associated della tessera avente come codice
	 * quello passato come parametro.
	 * @param card_id Codice della tessera che si vuole associare.
	 * @return Restituisce 1 se la tessera viene associata, 0 altrimenti.
	 */
	public int associateCard(int card_id) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("UPDATE cards SET associated = true WHERE cards.card_id = ?");
			ps.setInt(1, card_id);
			int result = ps.executeUpdate();
			conn.close();
			
			return result;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo associateCard: " + e.getMessage());
			return 0;
		}
	}

	/**
	 * Inserisce una nuova tessera con un codice auto generato dal database (auto_increment)
	 * e il codice fiscale designato.
	 * @param codice_fiscale Codice fiscale per l'inserimento della nuova tessera.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti.
	 */
	public int newCard(String codice_fiscale) {
		try {
			Connection conn = DBConnection.getConnection();
			String query = "INSERT INTO cards(codice_fiscale, associated) "
					+ "VALUES(?, ?)";
			PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, codice_fiscale);
			ps.setBoolean(2, true);
	        ps.executeUpdate();
	        ResultSet rs = ps.getGeneratedKeys();
	        rs.first();
	        int card_id = rs.getInt(1);
	        return card_id;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo newCard: " + e.getMessage());
			return 0;
		}
	}
	
	/**
	 * Inserisce una nuova tessera prelevando i valori dal Bean Tessera passato
	 * come parametro. Se nel bean è stato specificato un codice tessera != 0
	 * allora il codice tessera non verrà auto generato dal database, altrimenti sì.
	 * @param card Bean contenente le informazioni sulla nuova tessera.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti.
	 */
	public int newCardAdmin(CardsBean card) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			int card_id = card.getCard_id();
			PreparedStatement ps;
			String query;
			if(card_id != 0) {
				query = "INSERT INTO cards(card_id, codice_fiscale, associated) VALUES ("+card_id+", '"+card.getCodice_fiscale()+"', "+ card.isAssociated()+")";
				ps = conn.prepareStatement(query);
				result = ps.executeUpdate();
			 }
			else { query = "INSERT INTO cards(codice_fiscale, associated) VALUES ('"+card.getCodice_fiscale()+"', "+ card.isAssociated()+")";
			       ps = conn.prepareStatement(query);
			       result = ps.executeUpdate();
			}
			conn.close();
			return result;

		   }
		catch(SQLException e) {
			System.out.println("Errore Database metodo newCard: " + e.getMessage());
			return result;
		}
	}
	
}
