package nl.xillio.xill.plugins.document.services;

import nl.xillio.xill.plugins.document.exceptions.PersistenceException;
import org.bson.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This interface is responsible for the persistence of the UDM.
 *
 * @author Thomas Biesaart
 * @since 3.0.0
 */
public interface PersistenceService extends AutoCloseable {

	/**
	 * Set the id on a document and persist it.
	 *
	 * @param document The document to save
	 * @param type     The document category
	 * @param id       The document id
	 * @throws PersistenceException when saving the document failed
	 */
	void save(Document document, String type, String id) throws PersistenceException;

	/**
	 * Forwarder to {@link PersistenceService#save(Document, String, String)}
	 *
	 * @param provider The object to store
	 * @param type     The document category
	 * @param id       The document id
	 * @throws PersistenceException when saving the document failed
	 */
	default void save(DocumentProvider provider, String type, String id) throws PersistenceException {
		save(provider.buildDocument(), type, id);
	}

	/**
	 * Persist a document.
	 *
	 * @param document the document
	 * @param type     the document category
	 * @return the id of the saved document
	 * @throws PersistenceException when saving the document failed
	 */
	String save(Document document, String type) throws PersistenceException;

	/**
	 * Forwarder to {@link PersistenceService#save(Document, String)}
	 *
	 * @param provider The object to store
	 * @param type     the document category
	 * @return the id of the saved document
	 * @throws PersistenceException when saving the document failed
	 */
	default String save(DocumentProvider provider, String type) throws PersistenceException {
		return save(provider.buildDocument(), type);
	}

	/**
	 * Get a document from the persistence.
	 *
	 * @param type the document category
	 * @param id   the document id
	 * @return the document, not null
	 * @throws PersistenceException when the document could not be recovered
	 */
	Document get(String type, String id) throws PersistenceException;

	/**
	 * Remove a document from the persistence.
	 *
	 * @param type the document category
	 * @param id   the document id
	 * @return true if and only if the document was deleted
	 * @throws PersistenceException when deleting the document fails
	 */
	boolean delete(String type, String id) throws PersistenceException;

	/**
	 * Build a persistence that stores all its data in MongoDB.
	 * <p>
	 * This constructor uses credentials
	 *
	 * @param host     the host name
	 * @param port     the host port
	 * @param database the host database name
	 * @param username the username
	 * @param password the password
	 * @return the PersistenceService
	 */
	static PersistenceService mongo(String host, int port, String database, String username, String password) {
		return new MongoPersistenceService(
			new MongoPersistenceConnection(host, port, database,
				new MongoPersistenceConnection.Credentials(username, password)
			)
		);
	}

	/**
	 * Build a persistence that stores all its data in MongoDB.
	 * <p>
	 * This constructor uses no credentials
	 *
	 * @param host     the host name
	 * @param port     the host port
	 * @param database the host database name
	 * @return the PersistenceService
	 */
	static PersistenceService mongo(String host, int port, String database) {
		return new MongoPersistenceService(
			new MongoPersistenceConnection(host, port, database)
		);
	}

	/**
	 * Load defaults from the properties file and build a persistence around it.
	 *
	 * @return the PersistenceService
	 */
	static PersistenceService mongo() {
		Properties props = new Properties();
		try (InputStream stream = PersistenceService.class.getResourceAsStream("/mongo_defaults.properties")) {
			props.load(stream);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load defaults", e);
		}

		return mongo(
			props.get("host").toString(),
			Integer.parseInt(props.get("port").toString()),
			props.get("database").toString(),
			props.get("username").toString(),
			props.get("password").toString());
	}


}
