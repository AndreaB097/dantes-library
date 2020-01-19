package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import danteslibrary.dao.ManagersDAO;

class ManagersDAOTest {

	@Test
	void testGetManagersByFilter() {
		ManagersDAO managersDAO = new ManagersDAO();
		assertNull(managersDAO.getManagersByFilter(1, "")); //TC_22_1
		assertNull(managersDAO.getManagersByFilter(-1, "")); //TC_22_2
		assertNotNull(managersDAO.getManagersByFilter(0, "admin@admin.it")); //TC_22_3
	}

	@Test
	void testRemoveManager() {
		ManagersDAO managersDAO = new ManagersDAO();
		assertTrue(managersDAO.removeManager("nonesiste@gmail.com") == 0);//TC_25_1 Gestore non esistente
		assertTrue(managersDAO.removeManager("admin2@admin.it") != 0);//TC_25_2 Gestore esistente
	}

}
