package danteslibrary.dao;

import java.sql.*;
import danteslibrary.util.DBConnection;
import danteslibrary.model.CardsBean;

import java.util.ArrayList;

public class CardsDAO {

	public static ArrayList<CardsBean> getCardsByFilter(int filter, String keyword) {
		String[] filters = {"users.name", "users.surname", "users.email", "cards.codice_fiscale", "card_id"};
		ArrayList<CardsBean> cards = new ArrayList<CardsBean>();
		ResultSet result;
		try {
			Connection conn = DBConnection.getConnection();
			if (filter == 0 || filter == 1 || filter == 2) {
				PreparedStatement ps = conn.prepareStatement("SELECT users.name, users.surname,"
						+ " cards.card_id, cards.codice_fiscale, users.email, cards.associated " 
						+ "FROM cards, users WHERE " + filters[filter] + " = ? AND cards.codice_fiscale = users.codice_fiscale");
			
				ps.setString(1, keyword);
				result = ps.executeQuery();
				if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
					return null;
			
				while(result.next()) {
					/*Ottengo i dati dell'utente dal DB*/
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
				/*Se la tessera appartiene ad un utente registrato ne prelevo anche nome, cognome e email */

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

				ps = conn.prepareStatement("SELECT users.name, users.surname, users.email " 
						+ "FROM users, cards WHERE users.codice_fiscale = cards.codice_fiscale "
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
	
	public static ArrayList<CardsBean> getAllCards() {
		ArrayList<CardsBean> cards = new ArrayList<CardsBean>();
		ResultSet result;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT cards.*, users.name, users.surname, users.email " 
					+ "FROM users, cards WHERE cards.associated= true AND users.codice_fiscale = cards.codice_fiscale");
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

	public static int removeCard(String card_id) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM cards WHERE card_id = ?");
			ps.setString(1, card_id);
			result = ps.executeUpdate();
			conn.close();
			return result;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo removeCard: " + e.getMessage());
			return result;
		}
	}
	
	public static CardsBean getCardByEmail(String email) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT cards.* FROM cards, users WHERE cards.codice_fiscale = users.codice_fiscale "
					+ "AND users.email = ?");
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
	
	public static CardsBean getCardByCodice_fiscale(String codice_fiscale) {
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
	
	public static CardsBean getCardById(int card_id) {
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
	
	public static int associateCard(int card_id) {
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

	public static int newCard(String codice_fiscale, boolean associated) {
		try {
			Connection conn = DBConnection.getConnection();
			String query = "INSERT INTO cards(codice_fiscale, associated) "
					+ "VALUES(?, ?)";
			PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, codice_fiscale);
			ps.setBoolean(2, associated);
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
}
