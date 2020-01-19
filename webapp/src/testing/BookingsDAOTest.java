package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import danteslibrary.dao.BookingsDAO;
import java.sql.SQLException;

class BookingsDAOTest {

	@Test
	void testGetBookingsByFilter() {
		BookingsDAO bookingsDAO = new BookingsDAO();
		assertNull(bookingsDAO.getBookingsByFilter(1, "")); //TC_18_1
		assertNull(bookingsDAO.getBookingsByFilter(-1, "")); //TC_18_2
		assertNotNull(bookingsDAO.getBookingsByFilter(5, "a@a.it")); //TC_18_3
	}

	@Test
	void testRemoveBooking() throws SQLException {
		BookingsDAO bookingsDAO = new BookingsDAO();
		assertTrue(bookingsDAO.removeBooking(999) == 0);//TC_21_1 Prenotazione non esistente
		assertTrue(bookingsDAO.removeBooking(2) != 0);//TC_21_2 Prenotazione esistente
	}
	
	@Test
	void testUpdateBooking() throws SQLException {
		BookingsDAO bookingsDAO = new BookingsDAO();
		//Stato precedente Annullata
		assertEquals(0, bookingsDAO.updateBooking(2, "Annullata")); //TC_20_1
		//Stato precedente Riconsegnato
		assertEquals(0, bookingsDAO.updateBooking(1, "Riconsegnato")); //TC_20_2
		//Stato precedente Ritirato
		assertEquals(1, bookingsDAO.updateBooking(4, "Ritirato")); //TC_20_3
		//Stato precedente Ritirato
		assertEquals(1, bookingsDAO.updateBooking(4, "Riconsegnato")); //TC_20_4
		//Stato precedente Non ancora ritirato
		assertEquals(1, bookingsDAO.updateBooking(3, "Non ancora ritirato")); //TC_20_5
		//Stato precedente Non ancora ritirato
		assertEquals(1, bookingsDAO.updateBooking(3, "Ritirato")); //TC_20_6
		//Stato precedente Non ancora ritirato
		assertEquals(1, bookingsDAO.updateBooking(5, "Annullata")); //TC_20_7
	}

}
