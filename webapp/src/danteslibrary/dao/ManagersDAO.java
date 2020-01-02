package danteslibrary.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


import danteslibrary.model.ManagersBean;
import danteslibrary.util.DBConnection;

public class ManagersDAO {
	public ManagersBean login(String email, String password) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"SELECT managers.email, managers.name, managers.surname, managers.address, managers.phone "
					+ "FROM managers WHERE email = ? AND password = ?");
			ps.setString(1, email);
			ps.setString(2,	password);
			ResultSet result = ps.executeQuery();
			
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			if(result.first()) {
				/*Ottengo i dati dell'amministratore dal DB*/

				ManagersBean admin = new ManagersBean();
				admin.setEmail(result.getString("email"));
				admin.setName(result.getString("name"));
				admin.setName(result.getString("surname"));
				admin.setAddress(result.getString("address"));
				admin.setPhone(result.getString("phone"));
				admin.setRoles(retrieveManagerRoles(email));
				conn.close();
				
				return admin;
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
	
	public ArrayList<ManagersBean> getManagersByFilter(int filter, String keyword) {
		String[] filters = {"managers.email", "managers.name", "managers.surname", "roles.role_name"};
		ArrayList<ManagersBean> managers = new ArrayList<ManagersBean>();
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT managers.email, managers.name, managers.surname, managers.address, managers.phone " + 
					"FROM managers, roles, managers_roles WHERE "+filters[filter]+" = ?" + 
					"AND managers.email = managers_roles.email AND roles.role_name = managers_roles.role_name;");
			ps.setString(1, keyword);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			while(result.next()) {
				String email = result.getString("email");
				String name = result.getString("name");
				String surname = result.getString("surname");
				String address = result.getString("address");
				String phone = result.getString("phone");
				ArrayList<String> roles = retrieveManagerRoles(email);
				
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
	
	public ArrayList<String> retrieveManagerRoles(String email) {
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT roles.role_name FROM roles, managers_roles WHERE roles.role_name = managers_roles.role_name AND managers_roles.email = ?");
			ps.setString(1, email);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet � vuoto, allora la query non ha prodotto risultati*/
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
					ArrayList<String> roles = retrieveManagerRoles(email);
					
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
	
	
	public void newManager(ManagersBean manager) {
		try {
			Connection conn = DBConnection.getConnection();
			String query = "INSERT INTO managers(email, password, name, surname, address, phone) "
					+ "VALUES(?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, manager.getEmail());
			ps.setString(2, manager.getPassword());
			ps.setString(3, manager.getName());
			ps.setString(4, manager.getSurname());
			ps.setString(5, manager.getAddress());
			ps.setString(6, manager.getPhone());
	        ps.executeUpdate();
			ArrayList<String> roles = manager.getRoles();
			for(int i=0; i< roles.size();i++) {
			query = "INSERT INTO managers_roles(email, role_name) VALUES ('"+manager.getEmail()+"', '"+ roles.get(i)+ "')";
			ps = conn.prepareStatement(query);
			ps.executeUpdate();
		}
	        return;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo newManager: " + e.getMessage());
			return ;
		}
	}
	
	
	public ManagersBean findManagerByEmail(String email) {
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM managers WHERE managers.email = ?");
			ps.setString(1, email);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet � vuoto, allora la query non ha prodotto risultati*/
				return null;
			
			ManagersBean manager = new ManagersBean();
			
			while(result.next()) {
				manager.setEmail(result.getString("email"));
				manager.setPassword(result.getString("password"));
				manager.setName(result.getString("name"));
				manager.setSurname(result.getString("surname"));
				manager.setPhone(result.getString("phone"));
				manager.setAddress(result.getString("address"));
				ArrayList<String> roles = retrieveManagerRoles(email);
				manager.setRoles(roles);
			}

			conn.close();
			return manager;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo findBookById: " + e.getMessage());
		}
		
		return null;
	}
	
	
	public int updateManager(ManagersBean manager, String email) throws SQLException {
		int result = 0;
		Connection conn = null;
		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);
			String query = "UPDATE managers SET email= ?, password= ?, name = ?, surname = ?, phone = ?, address = ? WHERE managers.email = ?";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, manager.getEmail());
			ps.setString(2, manager.getPassword());
			ps.setString(3, manager.getName());
			ps.setString(4, manager.getSurname());
			ps.setString(5, manager.getPhone());
			ps.setString(6, manager.getAddress());
			ps.setString(7, email);
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
