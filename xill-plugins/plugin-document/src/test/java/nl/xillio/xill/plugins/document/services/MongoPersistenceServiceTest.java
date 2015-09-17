package nl.xillio.xill.plugins.document.services;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import nl.xillio.xill.plugins.document.exceptions.PersistenceException;
import org.bson.Document;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the public api of the MongoPersistenceService
 */
public class MongoPersistenceServiceTest {

	/**
	 * Test if save calls the database in the insert scenario
	 */
	@Test
	public void testSaveCallsDatabaseWithDocument() throws PersistenceException {
		// Document
		Document doc = new Document();

		// Collection
		MongoCollection collection = mock(MongoCollection.class);
		doAnswer(inv -> ((Document) inv.getArguments()[0]).put("_id", "_SOME ID_")).when(collection).insertOne(doc);

		// Database
		MongoDatabase db = mock(MongoDatabase.class);
		when(db.getCollection(eq("_CAT_"))).thenReturn(collection);

		// Connection
		MongoPersistenceConnection conn = mock(MongoPersistenceConnection.class);
		when(conn.getDatabase()).thenReturn(db);

		// Persistence
		PersistenceService persistence = new MongoPersistenceService(conn);

		// Call the method
		String result = persistence.save(doc, "_CAT_");

		// Verify calls
		verify(collection).insertOne(doc);
	}

	/**
	 * Test if the save calls the database in the update scenario.
	 */
	@Test
	public void testSaveCallsDatabaseWithID() throws PersistenceException {
		// Document
		Document doc = new Document();

		// Collection
		MongoCollection collection = mock(MongoCollection.class);

		// Database
		MongoDatabase db = mock(MongoDatabase.class);
		when(db.getCollection(eq("_CAT_"))).thenReturn(collection);

		// Connection
		MongoPersistenceConnection conn = mock(MongoPersistenceConnection.class);
		when(conn.getDatabase()).thenReturn(db);

		// Persistence
		PersistenceService persistence = new MongoPersistenceService(conn);

		// Call the method
		persistence.save(doc, "_CAT_", "_ID_");

		// Verify calls
		verify(collection).replaceOne(eq(new Document("_id", "_ID_")), same(doc), any(UpdateOptions.class));
	}

	@Test(expectedExceptions = PersistenceException.class)
	public void testUpdateFails() throws PersistenceException {
		// Document
		Document doc = new Document();

		// Connection
		MongoPersistenceConnection conn = mock(MongoPersistenceConnection.class);
		doThrow(new MongoException("_ERROR_")).when(conn).getDatabase();

		// Persistence
		PersistenceService persistence = new MongoPersistenceService(conn);

		// Call the method
		persistence.save(doc, "_CAT_", "_ID_");
	}

	@Test(expectedExceptions = PersistenceException.class)
	public void testSaveFails() throws PersistenceException {
		// Document
		Document doc = new Document();

		// Connection
		MongoPersistenceConnection conn = mock(MongoPersistenceConnection.class);
		doThrow(new MongoException("_ERROR_")).when(conn).getDatabase();

		// Persistence
		PersistenceService persistence = new MongoPersistenceService(conn);

		// Call the method
		persistence.save(doc, "_CAT_");
	}

	@Test
	public void testCloseCallsClose() throws Exception {
		MongoPersistenceConnection connection = mock(MongoPersistenceConnection.class);

		PersistenceService persistence = new MongoPersistenceService(connection);
		persistence.close();

		verify(connection).close();
	}

	/**
	 * Test that all MongoException are caught.
	 * @throws PersistenceException
	 */
	@Test(expectedExceptions = PersistenceException.class)
	public void testGetFails() throws PersistenceException {
		MongoPersistenceConnection conn = mock(MongoPersistenceConnection.class);
		doThrow(new MongoException("")).when(conn).getDatabase();

		PersistenceService persistenceService = new MongoPersistenceService(conn);

		persistenceService.get("_TYPE", "_ID");
	}

	/**
	 * Test that all MongoExceptions are caught.
	 * @throws PersistenceException
	 */
	@Test(expectedExceptions = PersistenceException.class)
	public void testDeleteFails() throws PersistenceException {
		MongoPersistenceConnection conn = mock(MongoPersistenceConnection.class);
		doThrow(new MongoException("")).when(conn).getDatabase();

		PersistenceService persistenceService = new MongoPersistenceService(conn);

		persistenceService.delete("_TYPE", "_ID");
	}

}