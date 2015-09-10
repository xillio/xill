package nl.xillio.xill.plugins.document.services;

import com.mongodb.MongoException;
import nl.xillio.xill.plugins.document.exceptions.PersistenceException;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test the MongoPersistenceService
 */
public class MongoPersistenceServiceTest {
	private static final String HOST = "host";
	private static final int PORT = 1337;
	private static final String DATABASE = "db";
	/**
	 * Test that the service starts if the connect is successful
	 */
	@Test
	public void testStartConnectSuccess() throws PersistenceException {
		// Persistence
		MongoPersistenceService persistence = mockPersistence();

		// Don't start yet
		assertFalse(persistence.isRunning());

		// Call the method
		persistence.start();

		// Check if started
		assertTrue(persistence.isRunning());
	}

	/**
	 * Test that the service throws an exception if connecting fails
	 */
	@Test(expectedExceptions = PersistenceException.class)
	public void testStartConnectFails() throws PersistenceException {
		// Persistence
		MongoPersistenceService persistence = mockPersistence();
		doThrow(new MongoException("ERROR")).when(persistence).connect(HOST, PORT, DATABASE);

		// Call the method
		persistence.start();
	}

	private MongoPersistenceService mockPersistence() {
		MongoPersistenceService persistence = spy(new MongoPersistenceService(HOST, PORT, DATABASE));
		doNothing().when(persistence).connect(HOST, PORT, DATABASE);

		return persistence;
	}


}