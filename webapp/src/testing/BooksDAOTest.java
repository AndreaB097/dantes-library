package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import danteslibrary.dao.BooksDAO;
import danteslibrary.model.BooksBean;

class BooksDAOTest {
	
	@Test
	void testRemoveBook() {
		BooksDAO booksDAO = new BooksDAO();
		assertEquals(0, booksDAO.removeBook(999)); //TC_12_1 libro non esistente
		assertEquals(1, booksDAO.removeBook(1)); //TC_12_2 libro esistente
		
	}

	@Test
	void testGetBooksByFilter() throws SQLException {
		BooksDAO booksDAO = new BooksDAO();
		assertNull(booksDAO.getBooksByFilter(1, "")); //TC_9_1
		assertNull(booksDAO.getBooksByFilter(-1, "")); //TC_9_2
		assertNotNull(booksDAO.getBooksByFilter(0, "Necronomicon")); //TC_9_3
	}
	
	@Test
	void testRemoveGenre() {
		BooksDAO booksDAO = new BooksDAO();
		assertTrue(booksDAO.removeGenre("Genere inventato") == 0); //TC_14_1 Genere non esistente
		assertTrue(booksDAO.removeGenre("Religione") != 0); //TC_14_2 Genere esistente
	}

	@Test
	void testNewBook() throws SQLException {
		BooksDAO booksDAO = new BooksDAO();
		BooksBean book = new BooksBean();
		book.setCover("./images/no_image.png");
		book.setTitle("Prova di un titolo lungo almeno 100 caratteri che possa oltrepassare la massima lunghezza e dare così errore.");
		book.setDescription("Descrizione lunga più di 1000 caratteri. Descrizione lunga più di 1000 caratteri. Descrizione lunga più di "
				+ "1000 caratteri. Descrizione lunga più di 1000 caratteri. Descrizione lunga più di 1000 caratteri. Descrizione lunga più "
				+ "di 1000 caratteri. Descrizione lunga più di 1000 caratteri. Descrizione lunga più di 1000 caratteri. Descrizione lunga più "
				+ "di 1000 caratteri. Descrizione lunga più di 1000 caratteri. Descrizione lunga più di 1000 caratteri. Descrizione lunga più "
				+ "di 1000 caratteri. Descrizione lunga più di 1000 caratteri. Descrizione lunga più di 1000 caratteri. Descrizione lunga più "
				+ "di 1000 caratteri. Descrizione lunga più di 1000 caratteri. Descrizione lunga più di 1000 caratteri. Descrizione lunga più di"
				+ " 1000 caratteri. Descrizione lunga più di 1000 caratteri. Descrizione lunga più di 1000 caratteri.Descrizione lunga più di 1000"
				+ " caratteri.Descrizione lunga più di 1000 caratteri. Descrizione lunga più di 1000 caratteri. Descrizione lunga più di 1000 "
				+ "caratteri. Descrizione lunga più di 1000 caratteri.");
		ArrayList<String> authors = new ArrayList<String>();
		authors.add("Ciao 123");
		book.setAuthors(authors);
		ArrayList<String> genres = new ArrayList<String>();
		genres.add("123");
		book.setGenres(genres);
		booksDAO.newBook(book);
	}

}
