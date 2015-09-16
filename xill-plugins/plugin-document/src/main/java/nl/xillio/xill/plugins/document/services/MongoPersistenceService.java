package nl.xillio.xill.plugins.document.services;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.model.UpdateOptions;
import nl.xillio.xill.plugins.document.exceptions.PersistenceException;
import org.bson.Document;

/**
 * This implementation of the PersistenceService stores its objects in MongoDB.
 */
public class MongoPersistenceService implements PersistenceService {
	private static final UpdateOptions UPDATE_OPTIONS = new UpdateOptions().upsert(true);

	private final MongoPersistenceConnection connection;

	MongoPersistenceService(MongoPersistenceConnection connection) {
		this.connection = connection;
	}

	@Override
	public void close() throws Exception {
		connection.close();
	}

	@Override
	public void save(Document document, String type, String id) throws PersistenceException {
		document.put("_id", id);
		save(document, type);
	}

	@Override
	public String save(Document document, String type) throws PersistenceException {
		if (document.get("_id") != null) {
			// This document has an id!
			update(document, type);
		} else {
			insert(document, type);
		}

		return document.get("_id").toString();
	}

	@Override
	public Document get(String type, String id) throws PersistenceException {
		try {
			return connection.getDatabase().getCollection(type).find(new BasicDBObject("_id", id)).first();
		} catch (MongoException e) {
			throw new PersistenceException("Failed to get document", e);
		}
	}

	@Override
	public boolean delete(String type, String id) throws PersistenceException {
		try {
			return connection.getDatabase().getCollection(type).deleteOne(new BasicDBObject("_id", id)).wasAcknowledged();
		} catch (MongoException e) {
			throw new PersistenceException("Failed to delete document", e);
		}
	}

	/**
	 * Update or save a document that already has an id.
	 *
	 * @param doc  the document
	 * @param type the type
	 */
	void update(Document doc, String type) throws PersistenceException {
		try {
			connection.getDatabase().getCollection(type).replaceOne(new Document("_id", doc.get("_id")), doc, UPDATE_OPTIONS);
		} catch (MongoException e) {
			throw new PersistenceException("Failed to update document", e);
		}
	}

	/**
	 * Insert a document into the database.
	 *
	 * @param doc  the document
	 * @param type the type
	 * @return the assigned id of the document
	 */
	void insert(Document doc, String type) throws PersistenceException {
		try {
			connection.getDatabase().getCollection(type).insertOne(doc);
		} catch (MongoException e) {
			throw new PersistenceException("Failed to insert document", e);
		}
	}
}
