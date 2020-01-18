package danteslibrary.dao;

import java.sql.*;
import danteslibrary.util.DBConnection;
import danteslibrary.model.BooksBean;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.json.*;

/**
 * Classe che si occupa dell’interfacciamento con il database per l’esecuzione 
 * di query riguardanti oggetti Libro.
 * 
 * @author Andrea Buongusto
 * @author Marco Salierno
 * 
 */
public class BooksDAO {
	
	/**
	 * Ottiene tutti i libri presenti nel database.
	 * @return Restituisce una lista di tutti i libri.
	 */
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
				ArrayList<String> genres = getBookGenres(book_id);
				ArrayList<String> authors = getBookAuthors(book_id);
				
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

	/**
	 * Ottiene tutti i libri presenti nel database, suddivisi in base al genere
	 * e disposti in ordine alfabetico.
	 * a cui appartengono.
	 * @return Restituisce una LinkedHashMap contenente tutti i generi e libri. 
	 * Per ogni genere (ordinati in ordine alfabetico), vi è una lista di libri appartenenti a quel genere.
	 * @throws SQLException Se il servizio non è disponibile (es. mancata connessione con il database).
	 */
	public LinkedHashMap<String, ArrayList<BooksBean>> getBookList() throws SQLException {
		LinkedHashMap<String, ArrayList<BooksBean>> list = new LinkedHashMap<String, ArrayList<BooksBean>>();
		ArrayList<String> genres = getAllGenres();
		Connection conn = DBConnection.getConnection();
		PreparedStatement ps;
		/*Per ogni genere nel DB seleziono al piu' 10 libri appartenenti
		 * a quel genere*/
		for(String genre : genres) {
			ps = conn.prepareStatement("SELECT * FROM books, books_genres WHERE books.book_id = books_genres.book_id AND books_genres.genre_name = ? ORDER BY RAND() LIMIT 10");
			ps.setString(1, genre);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Non ci sono libri per il genere 'genre', metto null nell'hashmap*/
				list.put(genre, null);
			else {
				ArrayList<BooksBean> books = new ArrayList<BooksBean>();
				while(result.next()) {
					BooksBean book = new BooksBean();
					book.setBook_id(result.getInt("book_id"));
					book.setTitle(result.getString("title"));
					book.setDescription(result.getString("description"));
					book.setPublisher(result.getString("publisher"));
					book.setQuantity(result.getInt("quantity"));
					book.setCover(result.getString("cover"));
					books.add(book);
				}
				list.put(genre, books);
			}
		}
		conn.close();
		return list;
	}
	
	/**
	 * Ottiene il libro in base al codice passato come parametro.
	 * @param id Codice del libro da ottenere.
	 * @return Restituisce il Libro se esiste, null altrimenti. 
	 */
	public BooksBean getBookById(int id) {
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM books WHERE books.book_id = ?");
			ps.setInt(1, id);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet e' vuoto, allora la query non ha prodotto risultati*/
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
				book.setAuthors(getBookAuthors(book.getBook_id()));
				book.setGenres(getBookGenres(book.getBook_id()));
			}

			conn.close();
			return book;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo findBookById: " + e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Ottiene la lista dei generi ai quali un libro appartiene.
	 * @param book_id Codice del libro del quale si vogliono ottenere i generi.
	 * @return Lista di generi ai quali un libro appartiene.
	 */
	public ArrayList<String> getBookGenres(int book_id) {
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT genres.genre_name FROM books_genres, genres "
					+ "WHERE genres.genre_name = books_genres.genre_name AND book_id = ?");
			ps.setInt(1, book_id);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet e' vuoto, allora la query non ha prodotto risultati*/
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
	
	/**
	 * Ottiene la lista degli autori di un libro.
	 * @param book_id Codice del libro del quale si vogliono ottenere gli autori.
	 * @return Lista di autori che hanno scritto il libro.
	 */
	public ArrayList<String> getBookAuthors(int book_id) {
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT name FROM books_authors, authors "
					+ "WHERE authors.author_id = books_authors.author_id AND book_id = ?");
			ps.setInt(1, book_id);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet e' vuoto, allora la query non ha prodotto risultati*/
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
	
	/**
	 * Ottiene la lista di tutti i generi che un libro può avere (prelevati dal database).
	 * @return Restituisce un JSONArray, utilizzato nel pannello amministratore (file: admin.jsp)
	 * per ottenere la lista dei generi selezionabili nei casi di Aggiunta libro e Modifca libro.
	 */
	public JSONArray getJSONAllGenres() {
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT genre_name FROM genres;");
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet e' vuoto, allora la query non ha prodotto risultati*/
				return null;
			
			JSONArray genres = new JSONArray();
			
			while(result.next()) {
				genres.put(result.getString("genre_name"));
			}

			conn.close();
			return genres;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getJSONAllGenres: " + e.getMessage());
			return null;	
		}
	}
	
	/**
	 * Ottiene la lista di tutti i generi che un libro può avere (prelevati dal database).
	 * @return Restituisce una lista contenente tutti i generi che un libro può avere.
	 */
	public ArrayList<String> getAllGenres() {
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT genre_name FROM genres ORDER BY genre_name ASC;");
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet e' vuoto, allora la query non ha prodotto risultati*/
				return null;
			
			ArrayList<String> genres = new ArrayList<String>();
			
			while(result.next()) {
				genres.add(result.getString("genre_name"));
			}

			conn.close();
			return genres;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getAllGenres: " + e.getMessage());
			return null;	
		}
	}
	
	/**
	 * Cerca all'interno del database i libri in base al filtro selezionato e 
	 * alle parole chiave passate come parametro.
	 * @param filter Filtro di ricerca. Vedere i filtri all'interno delle pagine
	 * jsp navbar.jsp e admin.jsp (Sezione Libri).
	 * @param keyword Stringa che rappresenta le parole chiave.
	 * @return Restituisce una lista di libri che rispettino filter e keyword.
	 * Restituisce null nel caso in cui non vi sono corrispondenze.
	 * @throws SQLException Se il servizio non è disponibile (es. mancata connessione con il database).
	 */
	public ArrayList<BooksBean> getBooksByFilter(int filter, String keyword) throws SQLException {
		
		String[] filters = {"title", "authors.name", "publisher", "genres.genre_name"};
		ArrayList<BooksBean> books = new ArrayList<BooksBean>();
		Connection conn = DBConnection.getConnection();
		PreparedStatement ps = conn.prepareStatement("SELECT books.book_id, books.title, books.publisher, books.quantity, books.cover "
				+ "FROM books, authors, genres, books_authors, books_genres WHERE "+filters[filter]+" LIKE ? "
						+ "AND authors.author_id = books_authors.author_id "
						+ "AND books.book_id = books_authors.book_id "
						+ "AND genres.genre_name = books_genres.genre_name "
						+ "AND books.book_id = books_genres.book_id "
						+ "GROUP BY title;");
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
			ArrayList<String> genres = getBookGenres(book_id);
			ArrayList<String> authors = getBookAuthors(book_id);
			
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
	
	/**
	 * Ottiene il codice libro di un libro dal database in maniera casuale.
	 * @return Restituisce un codice libro casuale, 0 in caso di fallimento.
	 */
	public int getRandomBookId() {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT book_id FROM books ORDER BY RAND() LIMIT 1");
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst())
				return 0;
			result.first();
			int book_id = result.getInt("book_id");
			conn.close();
			return book_id;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo getRandomBookId: " + e.getMessage());
			return 0;
		}
	}
	
	/**
	 * Cancella un libro dal database.
	 * @param book_id Codice del libro da cancellare.
	 * @return 0 in caso di fallimento, altrimenti il numero di libri
	 * cancellati (1).
	 */
	public int removeBook(int book_id) {
		int result = 0;
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE book_id = ?");
			ps.setInt(1, book_id);
			result = ps.executeUpdate();
			conn.close();
			return result;
		}
		catch(SQLException e) {
			System.out.println("Errore Database metodo removeBook: " + e.getMessage());
		}
		return result;
	}
	
	/**
	 * Aggiorna un libro già presente nel database.
	 * @param book Il libro da modificare. Il bean contiene tutti i valori da 
	 * sovrascrivere (tranne l'id che viene utilizzato per riconoscere il libro
	 * da aggiornare).
	 * @return 0 in caso di fallimento. Altrimenti il numero di righe modifcate.
	 * @throws SQLException Lanciata in caso di malfunzionamento, problemi di connessione.
	 */
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
	
	/**
	 * 
	 * Inserisce un nuovo libro nel database.
	 * 
	 * @param book Il libro da aggiungere al database.
	 * @return Restituisce 0 nel caso in cui non ci sono stati cambiamenti, 
	 * altrimenti il numero di righe cambiate.
	 * @throws SQLException Lanciata in caso di malfunzionamento, problemi di connessione.
	 */
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
	
	/**
	 * Ottiene il percorso dove dovrebbe risiedere l'immagine di copertina del libro.
	 * @param book_id Il codice del libro di cui si vuole ottenere il percorso dell'immagine.
	 * @return Restituisce il percorso dove dovrebbe risiedere l'immagine di copertina
	 * del libro.
	 */
	public String getBookCoverById(int book_id) {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT books.cover FROM books "
					+ "WHERE books.book_id = ?");
			ps.setInt(1, book_id);
			ResultSet result = ps.executeQuery();
			if(!result.isBeforeFirst()) /*Se il ResultSet e' vuoto, allora la query non ha prodotto risultati*/
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
	
	/**
	 * Aggiunge un nuovo genere di libri al database.
	 * @param genre_name Il nome del nuovo genere.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti.
	 */
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
	
	/**
	 * Cancella il genere passato come parametro dal database.
	 * @param genre_name Nome del genere da cancellare.
	 * @return Restituisce 1 in caso di successo, 0 altrimenti
	 */
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
