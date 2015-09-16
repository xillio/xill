package nl.xillio.xill.plugins.document.services;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import java.util.Collections;
import java.util.List;

/**
 * This class represents a connection to a mongo database.
 *
 * @author Thomas Biesaart
 * @since 3.0.0
 */
public class MongoPersistenceConnection implements AutoCloseable {
	private final MongoClient client;
	private final MongoDatabase database;

	/**
	 * Connect to MongoDB using the default credentials.
	 *
	 * @param host     the host
	 * @param port     the port
	 * @param database the database name
	 */
	public MongoPersistenceConnection(String host, int port, String database) {
		this(host, port, database, Credentials.NONE);
	}

	/**
	 * Connect to MongoDB.
	 * Note that the provided credentials will be disposed after connecting
	 *
	 * @param host        the host
	 * @param port        the port
	 * @param database    the database name
	 * @param credentials the credentials
	 */
	public MongoPersistenceConnection(String host, int port, String database, Credentials credentials) {
		client = new MongoClient(new ServerAddress(host, port), credentials.build(database));
		this.database = client.getDatabase(database);
		credentials.close();
	}

	@Override
	public void close() throws Exception {
		client.close();
	}

	/**
	 * Get the database.
	 *
	 * @return Get the database, not null
	 */
	public MongoClient getClient() {
		return client;
	}

	/**
	 * Get the connection.
	 *
	 * @return the connection, not null
	 */
	public MongoDatabase getDatabase() {
		return database;
	}

	/**
	 * This class represents a set of credentials for MongoDB
	 */
	public static class Credentials {
		static Credentials NONE = new Credentials(null, null);
		private String username;
		private String password;

		public Credentials(String username, String password) {
			this.username = username;
			this.password = password;
		}

		/**
		 * Dispose this object for security reasons.
		 */
		public void close() {
			username = null;
			password = null;
		}

		/**
		 * Build the MongoDB credential configuration.
		 *
		 * @param database the database
		 * @return a list of credentials
		 */
		public List<MongoCredential> build(String database) {
			// No credentials
			if (username == null && password == null) {
				return Collections.emptyList();
			}

			return Collections.singletonList(MongoCredential.createCredential(username, database, password.toCharArray()));
		}
	}
}
