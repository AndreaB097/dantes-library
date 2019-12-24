package danteslibrary.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import danteslibrary.util.DBConnection;
import danteslibrary.model.LibraryBean;

public class LibraryDAO {
	public LibraryBean getLibraryInfo() {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM library");
			ResultSet result = ps.executeQuery();
			result.first();
			
			LibraryBean library = new LibraryBean();
			library.setName(result.getString("name"));
			library.setLogo(result.getString("logo"));
			library.setContacts(result.getString("contacts"));
			
			conn.close();
			return library;
		}
		catch(SQLException e) {
			System.out.println("Errore Database: " + e.getMessage());
		}
		return null;
	}
}
