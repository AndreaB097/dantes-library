package danteslibrary.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


import danteslibrary.model.ManagersBean;
import danteslibrary.util.BCrypt;
import danteslibrary.util.DBConnection;

/**
 * Classe che si occupa dell’interfacciamento con il database per l’esecuzione
 * di query riguardanti oggetti Gestore.
 * 
 * @author Andrea Buongusto
 * @author Marco Salierno
 * 
 */
public class ManagersDAO {
	
	/**
	 * Ottiene il Gestore in base alle credenziali passate come parametro.
	 * @param email Email del Gestore da autenticare.
	 * @param password Password del Gestore da autenticare.
	 * @return Restituisce il Gestore se sono state trovate corrispondenze, altrimenti null.
	 * @throws SQLException Se il servizio non è disponibile (es. mancata connessione con il database).
	 */
	public ManagersBean login(String email, String password) throws SQLException {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM managers WHERE email = ?");
			ps.setString(1, email);
			ResultSet result = ps.executeQuery();
			
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			if(result.first()) {
				String hashed_password = result.getString("password");
				if(!BCrypt.checkpw(password, hashed_password))
					return null;
				/*Ottengo i dati dell'amministratore dal DB*/
				ManagersBean admin = new ManagersBean();
				admin.setEmail(result.getString("email"));
				admin.setName(result.getString("name"));
				admin.setSurname(result.getString("surname"));
				admin.setAddress(result.getString("address"));
				admin.setPhone(result.getString("phone"));
				admin.setRoles(getManagerRoles(email));
				conn.close();
				
				return admin;
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
	 * Cerca all'interno del database i Gestori in base al filtro selezionato e 
	 * alle parole chiave passate come parametro.
	 * @param filter Filtro di ricerca. Vedere i filtri all'interno della pagina
	 * admin.jsp (Sezione Gestori).
	 * @param keyword Stringa che rappresenta le parole chiave.
	 * @return Restituisce una lista di Gestori che rispettino filter e keyword.
	 * Restituisce null nel caso in cui non vi sono corrispondenze.
	 */
	public ArrayList<ManagersBean> getManagersByFilter(int filter, String keyword) {
		String[] filters = {"managers.email", "managers.name", "managers.surname", "roles.role_name"};
		ArrayList<ManagersBean> managers = new ArrayList<ManagersBean>();
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT managers.email, managers.name, managers.surname, managers.address, managers.phone " + 
					"FROM managers, roles, managers_roles WHERE "+filters[filter]+" LIKE ?" + 
					"AND managers.email = managers_roles.email AND roles.role_name = managers_roles.role_name;");
			ps.setString(1, "%"+keyword+"%");
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			while(result.next()) {
				String email = result.getString("email");
				String name = result.getString("name");
				String surname = result.getString("surname");
				String address = result.getString("address");
				String phone = result.getString("phone");
				ArrayList<String> roles = getManagerRoles(email);
				
				ManagersBean manager = new ManagersBean();
				manager.setEmail(email);
				manager.setName(name);
				manager.setSurname(surname);
				manager.setAddress(address);
				manager.setPhone(phone);
				manager.setRoles(roles);


				managers.add(manager);
			}
			conn.close();
			return managers;

		}
		catch(SQLException e) {
			System.out.println("Errore Database: " + e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Ottiene tutti i ruoli di un Gestore
	 * @param email Email del Gesotre di cui si vogliono ottenere i ruoli.
	 * @return Restituisce tutti i ruoli di un Gestore, null altrimenti.
	 */
	public ArrayList<String> getManagerRoles(String email) {
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT roles.role_name FROM roles, managers_roles WHERE roles.role_name = managers_roles.role_name AND managers_roles.email = ?");
			ps.setString(1, email);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet e' vuoto, allora la query non ha prodotto risultati*/
				return null;
			
			ArrayList<String> roles = new ArrayList<String>();
			
			while(result.next()) {
			roles.add(result.getString("role_name"));
			}

			conn.close();
			return roles;
		}
		catch(SQLException e) {
			System.out.println("Errore Database: " + e.getMessage());
		}
		
		return null;	
	}
	
	/**
	 * Ottiene tutti i Gestori presenti nel database.
	 * @return Restituisce la lista di tutti i Gestori presenti nel database, null altrimenti.
	 */
	public ArrayList<ManagersBean> getAllManagers() {
			
			ArrayList<ManagersBean> managers = new ArrayList<ManagersBean>();
			try {
				Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT managers.email, managers.name, managers.surname, managers.address, managers.phone FROM managers");
				ResultSet result = ps.executeQuery();
				if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
					return null;
				
				while(result.next()) {
					/*Ottengo i dati del libro dal DB*/
					String email = result.getString("email");
					String name = result.getString("name");
					String surname = result.getString("surname");
					String address = result.getString("address");
					String phone = result.getString("phone");
					ArrayList<String> roles = getManagerRoles(email);
					
					ManagersBean manager = new ManagersBean();
					manager.setEmail(email);
					manager.setName(name);
					manager.setSurname(surname);
					manager.setAddress(address);
					manager.setPhone(phone);
					manager.setRoles(roles);
					managers.add(manager);

				}
				conn.close();
				return managers;
			}
			catch(SQLException e) {
				System.out.println("Errore Database: " + e.getMessage());
			}
			return null;
		}
	
	/**
	 * Cancella dal database il Gestore avente l'email passata come parametro.
	 * @param email Email del Gestore da cancellare.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti.
	 */
	public int removeManager(String email) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM managers WHERE email = ?");
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
	
	/**
	 * Aggiunge il Gestore passato come parametro al database.
	 * @param manager Il nuovo Gestore da memorizzare.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti.
	 */
	public int newManager(ManagersBean manager) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			String query = "INSERT INTO managers(email, password, name, surname, address, phone) "
					+ "VALUES(?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, manager.getEmail());
			ps.setString(2, BCrypt.hashpw(manager.getPassword(), BCrypt.gensalt()));
			ps.setString(3, manager.getName());
			ps.setString(4, manager.getSurname());
			ps.setString(5, manager.getAddress());
			ps.setString(6, manager.getPhone());
	        ps.executeUpdate();
			ArrayList<String> roles = manager.getRoles();
			for(int i=0; i< roles.size();i++) {
				query = "INSERT INTO managers_roles(email, role_name) VALUES ('"+manager.getEmail()+"', '"+ roles.get(i)+ "')";
				ps = conn.prepareStatement(query);
				result = ps.executeUpdate();
			}
	        return result;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo newManager: " + e.getMessage());
			return result;
		}
	}
	
	/**
	 * Ottiene dal database il Gestore avente l'email passata come parametro.
	 * @param email Email del Gestore che si vuole ottenere.
	 * @return Restituisce il Gestore se esiste, null altrimenti.
	 */
	public ManagersBean getManagerByEmail(String email) {
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM managers WHERE managers.email = ?");
			ps.setString(1, email);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet e' vuoto, allora la query non ha prodotto risultati*/
				return null;
			
			ManagersBean manager = new ManagersBean();
			
			while(result.next()) {
				manager.setEmail(result.getString("email"));
				manager.setName(result.getString("name"));
				manager.setSurname(result.getString("surname"));
				manager.setPhone(result.getString("phone"));
				manager.setAddress(result.getString("address"));
				ArrayList<String> roles = getManagerRoles(email);
				manager.setRoles(roles);
			}

			conn.close();
			return manager;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getManagerByEmail: " + e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Aggiorna il Gestore avente come email quella passata come parametro con i
	 * dati contenuti all'interno del bean Gestore passato come parametro.
	 * L'email viene passata in quanto il bean potrebbe avere un'email diversa.
	 * Infatti se il Gestore cambia indirizzo email, ho la necessità di conoscere
	 * quello vecchio (parametro email) e rimpiazzarlo con quello nuovo (contenuto nel
	 * bean, manager.getEmail()). 
	 * @param manager Bean contenente i nuovi dati del Gestore.
	 * @param email Email attuale del Gestore.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti.
	 * @throws SQLException Lanciata in caso di malfunzionamento, problemi di connessione.
	 */
	public int updateManager(ManagersBean manager, String email) throws SQLException {
		int result = 0;
		Connection conn = null;
		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);
			String query = "";
			PreparedStatement ps;
			if(manager.getPassword() != null && !manager.getPassword().equals("")) {
				query = "UPDATE managers SET email= ?, password= ?, name = ?, surname = ?, phone = ?, address = ? WHERE managers.email = ?";
				ps = conn.prepareStatement(query);
				ps.setString(1, manager.getEmail());
				ps.setString(2, BCrypt.hashpw(manager.getPassword(), BCrypt.gensalt()));
				ps.setString(3, manager.getName());
				ps.setString(4, manager.getSurname());
				ps.setString(5, manager.getPhone());
				ps.setString(6, manager.getAddress());
				ps.setString(7, email);
			}
			else {
				query = "UPDATE managers SET email= ?, name = ?, surname = ?, phone = ?, address = ? WHERE managers.email = ?";
				ps = conn.prepareStatement(query);
				ps.setString(1, manager.getEmail());
				ps.setString(2, manager.getName());
				ps.setString(3, manager.getSurname());
				ps.setString(4, manager.getPhone());
				ps.setString(5, manager.getAddress());
				ps.setString(6, email);
			}
			ps.executeUpdate();

			query ="DELETE FROM managers_roles WHERE email = ?";
			ps = conn.prepareStatement(query);
			ps.setString(1, manager.getEmail());
	    	ps.executeUpdate();
	    	


			ArrayList<String> roles = manager.getRoles();
			for(int i=0; i< roles.size();i++) {
				query = "INSERT INTO managers_roles(email, role_name) VALUES (?, ?)";
				ps = conn.prepareStatement(query);
				ps.setString(1, manager.getEmail());
				ps.setString(2, roles.get(i));
				result = ps.executeUpdate();
			}
			conn.commit();
		} catch(SQLException e) {
			if(conn != null) {
					System.out.println("\nRollback! Non aggiorno il gestore.\n"
							+ "Errore Database metodo updateManager: " + e.getMessage());
					conn.rollback();
					return 0;
			}
		} finally {
			conn.setAutoCommit(true);
		}
		return result;
	}

}
