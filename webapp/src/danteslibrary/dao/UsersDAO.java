package danteslibrary.dao;

import java.sql.*;

import danteslibrary.model.UsersBean;
import danteslibrary.util.DBConnection;

public class UsersDAO {
	
	public UsersBean login(String email, String password) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT users.name, users.surname, users.codice_fiscale, users.address FROM users WHERE email = ? AND password = ?");
			ps.setString(1, email);
			ps.setString(2,	password);
			ResultSet result = ps.executeQuery();
			
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			if(result.first()) {
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
			System.out.println("Errore Database: " + e.getMessage());
		}
		
		return null;
	}

	public boolean register(UsersBean user) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("INSERT INTO users(name, surname, email, password, "
					+ "codice_fiscale, address) VALUES(?, ?, ?, ?, ?, ?);");

			ps.setString(1, user.getName());
			ps.setString(2, user.getSurname());
			ps.setString(3, user.getEmail());
			ps.setString(4, user.getPassword());
			ps.setString(5,	user.getCodice_fiscale());
			ps.setString(6, user.getAddress());
			
			ps.executeUpdate();
				
			conn.close();
			
			return true;
		}
		catch(SQLException e) {
			System.out.println("Errore Database: " + e.getMessage());
		}
		
		return false;
	}
	
	public static boolean checkEmail(String email) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT users.email FROM users WHERE email = '" + email + "'");
			
			ResultSet result = ps.executeQuery();
			
			if(!result.isBeforeFirst()) { /*Nessuna corrispondenza trovata nel DB, restituisco true*/
				conn.close();
				return true;
			}
		}
		catch(SQLException e) {
			System.out.println("Errore Database: " + e.getMessage());
		}
		
		return false;
	}
}