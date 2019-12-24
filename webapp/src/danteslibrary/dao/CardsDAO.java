package danteslibrary.dao;

import java.sql.*;
import danteslibrary.util.DBConnection;
import danteslibrary.model.CardsBean;

import java.util.ArrayList;

public class CardsDAO {
	
		
	public ArrayList<CardsBean> getCardsByFilter(int filter, String keyword) {
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
			System.out.println("Errore Database:1 " + e.getMessage());
		}
		
		return null;
	}
	

}
