package danteslibrary.dao;

import java.sql.*;
import danteslibrary.util.DBConnection;
import danteslibrary.model.BooksBean;
import java.util.ArrayList;

public class BooksDAO {
	
	public BooksBean findBookById(String id) {
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM books WHERE books.book_id = ?");
			ps.setString(1, id);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet è vuoto, allora la query non ha prodotto risultati*/
				return null;
			
			BooksBean book = new BooksBean();
			
			while(result.next()) {
				book.setBook_id(result.getInt("book_id"));
				book.setTitle(result.getString("title"));
				book.setDescription(result.getString("description"));
				book.setPublisher(result.getString("publisher"));
				book.setQuantity(result.getInt("quantity"));
				book.setCover(result.getString("cover"));
				
				//Prelevo generi e autori del libro che ha come chiave book_id
				book.setAuthors(retrieveBookAuthors(book.getBook_id()));
				book.setGenres(retrieveBookGenres(book.getBook_id()));
			}

			conn.close();
			return book;
		}
		catch(SQLException e) {
			System.out.println("Errore Database: " + e.getMessage());
		}
		
		return null;
	}
	
	public ArrayList<String> retrieveBookGenres(int book_id) {
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT name FROM books_genres, genres "
					+ "WHERE genres.genre_id = books_genres.genre_id AND book_id = ?");
			ps.setInt(1, book_id);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet è vuoto, allora la query non ha prodotto risultati*/
				return null;
			
			ArrayList<String> genres = new ArrayList<String>();
			
			while(result.next()) {
				genres.add(result.getString("name"));
			}

			conn.close();
			return genres;
		}
		catch(SQLException e) {
			System.out.println("Errore Database: " + e.getMessage());
		}
		
		return null;	
	}
	
	public ArrayList<String> retrieveBookAuthors(int book_id) {
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT name FROM books_authors, authors "
					+ "WHERE authors.author_id = books_authors.author_id AND book_id = ?");
			ps.setInt(1, book_id);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet è vuoto, allora la query non ha prodotto risultati*/
				return null;
			
			ArrayList<String> authors = new ArrayList<String>();
			
			while(result.next()) {
				authors.add(result.getString("name"));
			}

			conn.close();
			return authors;
		}
		catch(SQLException e) {
			System.out.println("Errore Database: " + e.getMessage());
		}
		
		return null;	
	}
}
