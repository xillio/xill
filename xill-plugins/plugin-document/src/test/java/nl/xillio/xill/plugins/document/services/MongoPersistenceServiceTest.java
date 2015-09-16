package nl.xillio.xill.plugins.document.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import nl.xillio.xill.plugins.document.exceptions.PersistenceException;
import org.bson.Document;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the public api of the MongoPersistenceService
 */
public class MongoPersistenceServiceTest {

	/**
	 * Test if save calls the database
	 */
	@Test
	public void testSaveCallsDatabaseWithDocument() throws PersistenceException {
		// Document
		Document doc = new Document();

		// Collection
		MongoCollection collection = mock(MongoCollection.class);
		doAnswer(inv ->
				((Document) inv.getArguments()[0]).put("_id", "_SOME ID_")
		).when(collection).insertOne(doc);

		// Database
		MongoDatabase db = mock(MongoDatabase.class);
		when(db.getCollection(eq("_CAT_"))).thenReturn(collection);

		// Connection
		MongoPersistenceConnection conn = mock(MongoPersistenceConnection.class);
		when(conn.getDatabase()).thenReturn(db);

		// Persistence
		PersistenceService persistence = new MongoPersistenceService(conn);

		// Call the method
		persistence.save(doc, "_CAT_");

		// Verify calls
		verify(collection).insertOne(doc);
	}
}