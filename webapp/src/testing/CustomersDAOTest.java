package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import danteslibrary.dao.CustomersDAO;

class CustomersDAOTest {

	@Test
	void testGetCustomersByFilter() {
		CustomersDAO customersDAO = new CustomersDAO();
		assertNull(customersDAO.getCustomersByFilter(1, "")); //TC_7_1
		assertNull(customersDAO.getCustomersByFilter(-1, "")); //TC_7_2
		assertNotNull(customersDAO.getCustomersByFilter(0, "Lino")); //TC_7_3
	}

	@Test
	void testRemoveCustomer() {
		CustomersDAO customersDAO = new CustomersDAO();
		assertTrue(customersDAO.removeCustomer("nonesiste@gmail.it") == 0); //TC_8_1
		assertTrue(customersDAO.removeCustomer("b@b.it") != 0); //TC_8_2
	}

}