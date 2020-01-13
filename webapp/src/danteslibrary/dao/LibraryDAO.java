package danteslibrary.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import danteslibrary.util.DBConnection;
import danteslibrary.model.LibraryBean;

/**
 * Classe che si occupa dell’interfacciamento con il database per l’esecuzione
 * di query riguardanti oggetti Biblioteca.
 * 
 * @author Andrea Buongusto
 * @author Marco Salierno
 * 
 */
public class LibraryDAO {
	/**
	 * Ottiene le informazioni della biblioteca (salvandole in un bean Biblioteca).
	 * @return Restituisce un bean contenente le informazioni della biblioteca
	 */
	public LibraryBean getLibraryInfo() {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM library");
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst())
				return null;
			
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
	
	/**
	 * Aggiorna le informazioni della biblioteca con quelle contenute nel bean
	 * passato come parametro.
	 * @param library Bean contenente informazioni sulla Biblioteca.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti.
	 */
	public int updateLibraryInfo(LibraryBean library) {
		int result = 0;
		PreparedStatement ps;
		try {
			Connection conn = DBConnection.getConnection();
			 ps = conn.prepareStatement("DELETE FROM library");
			ps.executeUpdate();
			if(library.getLogo() != null) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO library(name, logo, contacts) VALUES(?, ?, ?)");
				ps.setString(1, library.getName());
				ps.setString(2, library.getLogo());
				ps.setString(3, library.getContacts());
			}
			else {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO library(name, contacts) VALUES(?, ?)");
				ps.setString(1, library.getName());
				ps.setString(2, library.getContacts());
			}
			
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
