package nl.xillio.xill.plugins.document.services;

import com.mongodb.MongoException;
import nl.xillio.xill.plugins.document.exceptions.PersistenceException;

/**
 * This implementation of the PersistenceService stores its objects in MongoDB
 */
public class MongoPersistenceService implements PersistenceService {

	private MongoPersistenceConnection connection;
	private boolean running;

	public MongoPersistenceService(MongoPersistenceConnection connection) {
		this.connection = connection;
	}

	@Override
	public void start() throws PersistenceException {}

	@Override
	public void close() throws Exception {
		connection.close();
	}
}
