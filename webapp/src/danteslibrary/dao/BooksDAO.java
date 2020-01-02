package danteslibrary.dao;

import java.sql.*;
import danteslibrary.util.DBConnection;
import danteslibrary.model.BooksBean;
import java.util.ArrayList;
import org.json.*;

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
			System.out.println("Errore Database metodo getAllBooks: " + e.getMessage());
		}
		return null;
	}
	
	public BooksBean findBookById(String id) {
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM books WHERE books.book_id = ?");
			ps.setString(1, id);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet � vuoto, allora la query non ha prodotto risultati*/
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
			System.out.println("Errore Database metodo findBookById: " + e.getMessage());
		}
		
		return null;
	}
	
	public ArrayList<String> retrieveBookGenres(int book_id) {
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT genres.genre_name FROM books_genres, genres "
					+ "WHERE genres.genre_name = books_genres.genre_name AND book_id = ?");
			ps.setInt(1, book_id);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet � vuoto, allora la query non ha prodotto risultati*/
				return null;
			
			ArrayList<String> genres = new ArrayList<String>();
			
			while(result.next()) {
				genres.add(result.getString("genre_name"));
			}

			conn.close();
			return genres;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo retrieveBookGenres: " + e.getMessage());
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
			if(!result.isBeforeFirst()) /*Se il ResultSet � vuoto, allora la query non ha prodotto risultati*/
				return null;
			
			ArrayList<String> authors = new ArrayList<String>();
			
			while(result.next()) {
				authors.add(result.getString("name"));
			}

			conn.close();
			return authors;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo retrieveBookAuthors: " + e.getMessage());
		}
		
		return null;	
	}
	
	public JSONArray retrieveJSONAllGenres() {
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT genre_name FROM genres;");
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet è vuoto, allora la query non ha prodotto risultati*/
				return null;
			
			JSONArray genres = new JSONArray();
			
			while(result.next()) {
				genres.put(result.getString("genre_name"));
			}

			conn.close();
			return genres;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo retrieveJSONAllGenres: " + e.getMessage());
			return null;	
		}
	}
	
	public ArrayList<String> retrieveAllGenres() {
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT genre_name FROM genres;");
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet è vuoto, allora la query non ha prodotto risultati*/
				return null;
			
			ArrayList<String> genres = new ArrayList<String>();
			
			while(result.next()) {
				genres.add(result.getString("genre_name"));
			}

			conn.close();
			return genres;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo retrieveAllGenres: " + e.getMessage());
			return null;	
		}
	}
	
	public ArrayList<BooksBean> getBooksByFilter(int filter, String keyword) {
		
		String[] filters = {"title", "authors.name", "publisher", "genres.genre_name"};
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
			System.out.println("Errore Database metodo getBooksByFilter: " + e.getMessage());
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
			System.out.println("Errore Database metodo removeBook: " + e.getMessage());
		}
		return result;
	}
	
	
	public int updateBook(BooksBean book) throws SQLException {
		int result = 0;
		Connection conn = null;
		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);
			String query = "UPDATE books SET title= ?, description= ?, publisher = ?, quantity = ?, cover = ? WHERE book_id = ?";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, book.getTitle());
			ps.setString(2, book.getDescription());
			ps.setString(3, book.getPublisher());
			ps.setInt(4, book.getQuantity());
			
			String link;
			if(System.getProperty("file.separator").equals("\\")) {
				link = book.getCover().replace("\\", "/");
			}
			else {
				link = book.getCover();
			}
			ps.setString(5, link);
			ps.setInt(6, book.getBook_id());
			result = ps.executeUpdate();
			
			/*Cancello gli autori precedentemente associati*/
			ArrayList<String> authors = book.getAuthors();
			for(int i = 0; i < authors.size(); i++) {
				ps = conn.prepareStatement("SELECT * FROM authors, books_authors WHERE authors.author_id = books_authors.author_id "
						+ "AND books_authors.book_id = ?");
				ps.setInt(1, book.getBook_id());
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
					ps = conn.prepareStatement("DELETE FROM authors WHERE author_id = ?");
					ps.setInt(1, rs.getInt("author_id"));
					ps.executeUpdate();
				}
			}
			
			/*Cancello i generi precedentemente associati*/
			ps = conn.prepareStatement("DELETE FROM books_genres WHERE book_id = ?");
			ps.setInt(1, book.getBook_id());
			ps.executeUpdate();
			
			/*Inserisco gli autori aggiornati e li associo anche al libro modificato*/
			for(int i=0; i< authors.size();i++) {
				query = "INSERT INTO authors(name) VALUES (?)";
				ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, authors.get(i));
				result = ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
				rs.first();
				int author_id = rs.getInt(1);
				query = "INSERT INTO books_authors(book_id, author_id) VALUES (?, ?)";
				ps = conn.prepareStatement(query);
				ps.setInt(1, book.getBook_id());
				ps.setInt(2, author_id);
				result = ps.executeUpdate();
			}
			
			/*Inserisco i generi aggiornati e li associo anche al libro modificato*/
			ArrayList<String> genres = book.getGenres();
			for(int i=0; i< genres.size();i++) {
				query = "INSERT INTO books_genres(book_id, genre_name) VALUES (?, ?)";
				ps = conn.prepareStatement(query);
				ps.setInt(1, book.getBook_id());
				ps.setString(2, genres.get(i));
				result = ps.executeUpdate();
			}
			conn.commit();
		} catch(SQLException e) {
			if(conn != null) {
					System.out.println("\nRollback! Non aggiorno il libro.\n"
							+ "Errore Database metodo updateBook: " + e.getMessage());
					conn.rollback();
					return 0;
			}
		} finally {
			conn.setAutoCommit(true);
		}
		return result;
	}
	
	public int newBook(BooksBean book) throws SQLException {
		int result = 0;	
		Connection conn = null;
		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);
			String query = "INSERT INTO books(title, description, quantity, publisher, cover) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, book.getTitle());
			ps.setString(2, book.getDescription());
			ps.setInt(3, book.getQuantity());
			ps.setString(4, book.getPublisher());
			String link;
			if(System.getProperty("file.separator").equals("\\")) {
				link = book.getCover().replace("\\", "/");
			}
			else {
				link = book.getCover();
			}
			ps.setString(5, link);
		    result = ps.executeUpdate();
		    ResultSet rs = ps.getGeneratedKeys();
		    rs.first();
		    int book_id = rs.getInt(1);
			
		    ArrayList<String> genres = book.getGenres();
			for(int i = 0; i < genres.size(); i++) {
				query = "INSERT INTO books_genres(book_id, genre_name) VALUES (?, ?)";
				ps = conn.prepareStatement(query);
				ps.setInt(1, book_id);
				ps.setString(2, genres.get(i));
				result = ps.executeUpdate();
			}
				
			ArrayList<String> authors = book.getAuthors();
			for(int i = 0; i < authors.size(); i++) {
				query = "INSERT INTO authors(name) VALUES (?)";
				ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, authors.get(i));
				result = ps.executeUpdate();
				rs = ps.getGeneratedKeys();
				rs.first();
				int author_id = rs.getInt(1);
				query = "INSERT INTO books_authors(book_id, author_id) VALUES (?, ?)";
				ps = conn.prepareStatement(query);
				ps.setInt(1, book_id);
				ps.setInt(2, author_id);
				result = ps.executeUpdate();
		   }
		  conn.commit();
		} catch(SQLException e) {
			if(conn != null) {
					System.out.println("\nRollback! Non aggiungo il libro.\n"
							+ "Errore Database metodo newBook: " + e.getMessage());
					conn.rollback();
					return 0;
			}
		} finally {
			conn.setAutoCommit(true);
		}
		return result;
	}
	
	public String getBookCoverById(int book_id) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT books.cover FROM books "
					+ "WHERE books.book_id = ?");
			ps.setInt(1, book_id);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet � vuoto, allora la query non ha prodotto risultati*/
				return null;
			result.first();
			String cover = result.getString("cover");
			conn.close();
			return cover;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getBookCoverById: " + e.getMessage());
			return null;
		}	
	}
	
	public int newGenre(String genre_name) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("INSERT INTO genres(genre_name) VALUES (?)");
			ps.setString(1, genre_name);
			result = ps.executeUpdate();
			conn.close();
			return result;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo : " + e.getMessage());
			return result;
		}
	}
	
	public int removeGenre(String genre_name) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM genres WHERE genre_name = ?");
			ps.setString(1, genre_name);
			result = ps.executeUpdate();
			conn.close();
			return result;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo removeGenre: " + e.getMessage());
		}
		return result;
	}
	
}
