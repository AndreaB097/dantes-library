package danteslibrary.dao;

import java.sql.*;
import danteslibrary.util.DBConnection;
import danteslibrary.model.BooksBean;
import java.util.ArrayList;

public class BooksDAO {
	
public ArrayList<BooksBean> getAllBooks() {
		
		ArrayList<BooksBean> books = new ArrayList<BooksBean>();
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT books.book_id, books.title, books.publisher, books.quantity FROM books;");
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			while(result.next()) {
				/*Ottengo i dati del libro dal DB*/
				int book_id = result.getInt("book_id");
				String title = result.getString("title");
				String publisher = result.getString("publisher");
				int quantity = result.getInt("quantity");
				ArrayList<String> genres = retrieveBookGenres(book_id);
				ArrayList<String> authors = retrieveBookAuthors(book_id);
				
				BooksBean book = new BooksBean();
				book.setBook_id(book_id);
				book.setTitle(title);
				book.setPublisher(publisher);
				book.setQuantity(quantity);
				book.setGenres(genres);
				book.setAuthors(authors);
				books.add(book);
			}
			conn.close();
			return books;
		}
		catch(SQLException e) {
			System.out.println("Errore Database: " + e.getMessage());
		}
		return null;
	}
	
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
	
	public ArrayList<BooksBean> getBooksByFilter(int filter, String keyword) {
		
		String[] filters = {"title", "authors.name", "publisher", "genres.name"};
		ArrayList<BooksBean> books = new ArrayList<BooksBean>();
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT books.book_id, books.title, books.publisher, books.quantity, books.cover FROM books, authors, genres WHERE "+filters[filter]+" LIKE ? GROUP BY title;");
			ps.setString(1, "%"+keyword+"%");
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Nessuna corrispondenza trovata nel DB, restituisco null*/
				return null;
			
			/* Itero fin quando non ci sono piu' libri */
			while(result.next()) {
				/*Ottengo i dati del libro dal DB*/
				int book_id = result.getInt("book_id");
				String title = result.getString("title");
				String publisher = result.getString("publisher");
				String cover = result.getString("cover");
				int quantity = result.getInt("quantity");
				ArrayList<String> genres = retrieveBookGenres(book_id);
				ArrayList<String> authors = retrieveBookAuthors(book_id);
				
				BooksBean book = new BooksBean();
				book.setBook_id(book_id);
				book.setTitle(title);
				book.setPublisher(publisher);
				book.setCover(cover);
				book.setQuantity(quantity);
				book.setGenres(genres);
				book.setAuthors(authors);
				
				/*Aggiungo il libro all'Arraylist*/
				books.add(book);
			}
			conn.close();
			return books;

		}
		catch(SQLException e) {
			System.out.println("Errore Database: " + e.getMessage());
		}
		
		return null;
	}
	
	public int removeBook(String book_id) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE book_id = ?");
			ps.setString(1, book_id);
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
