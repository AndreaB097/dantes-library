package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import danteslibrary.dao.CardsDAO;

class CardsDAOTest {

	@Test
	void testNewCard() {
		CardsDAO cardsDAO = new CardsDAO();
		assertTrue(cardsDAO.newCard("ABCABC00A00A000A") == 0); //TC_3_1
		assertTrue(cardsDAO.newCard("ABCABC00A00A000D") != 0); //TC_3_2
	}

	@Test
	void testGetCardsByFilter() {
		CardsDAO cardsDAO = new CardsDAO();
		assertNull(cardsDAO.getCardsByFilter(1, "")); //TC_15_1
		assertNull(cardsDAO.getCardsByFilter(-1, "")); //TC_15_2
		assertNotNull(cardsDAO.getCardsByFilter(2, "a@a.it")); //TC_15_3
	}
	
	@Test
	void testRemoveCard() {
		CardsDAO cardsDAO = new CardsDAO();
		assertTrue(cardsDAO.removeCard(9999) == 0); //TC_17_1 Tessera non esistente
		assertTrue(cardsDAO.removeCard(10001) != 0); //TC_17_2 Tessera esistente
	}

}