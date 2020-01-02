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
			System.out.println("Errore Database metodo getLibraryInfo: " + e.getMessage());
		}
		return null;
	}
	
	public int updateLibraryInfo(LibraryBean library) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM library");
			ps.executeUpdate();
			
			ps = conn.prepareStatement("INSERT INTO library(name, logo, contacts) VALUES(?, ?, ?)");
			ps.setString(1, library.getName());
			ps.setString(2, library.getLogo());
			ps.setString(3, library.getContacts());
			result = ps.executeUpdate();
			
			conn.close();
			return result;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo updateLibraryInfo: " + e.getMessage());
			return result;
		}
	}
}
