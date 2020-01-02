package danteslibrary.dao;

import java.sql.*;

import danteslibrary.model.UsersBean;
import danteslibrary.util.DBConnection;
import java.util.ArrayList;


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
			System.out.println("Errore Database: " + e.getMessage());
		}
		
		return true;
	}
	
	public boolean checkExistingCodiceFiscale(String codice_fiscale) {
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
			System.out.println("Errore Database: " + e.getMessage());
		}
		
		return true;
	}
	
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
			System.out.println("Errore Database: " + e.getMessage());
		}
		return null;
	}
	
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
			System.out.println("Errore Database: " + e.getMessage());
		}
		
		return null;
	}
	
	
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
			System.out.println("Errore Database: " + e.getMessage());
		}
		return null;
	}
	
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
			System.out.println("Errore Database: " + e.getMessage());
		}
		return result;
	}
	
	
}