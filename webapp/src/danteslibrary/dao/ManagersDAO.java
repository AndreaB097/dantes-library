package danteslibrary.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import danteslibrary.model.ManagersBean;
import danteslibrary.util.DBConnection;

public class ManagersDAO {
	public ManagersBean login(String email, String password) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT managers.email  FROM managers WHERE email = ? AND password = ?");
			ps.setString(1, email);
			ps.setString(2,	password);
			ResultSet result = ps.executeQuery();
			
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			if(result.first()) {
				/*Ottengo i dati dell'amministratore dal DB*/

				ManagersBean admin = new ManagersBean();
				admin.setEmail(result.getString("email"));
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


}
