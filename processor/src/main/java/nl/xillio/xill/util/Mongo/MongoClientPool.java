package nl.xillio.xill.util.Mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoTimeoutException;

public class MongoClientPool {
	private final Map<String, DB> clients = new HashMap<>();
	private String firstConnection;

	/**
	 * Checks connection on an existing client or creates a new one
	 *
	 * @param hostname
	 * @param port
	 * @return whether the connection was successful.
	 * @throws UnknownHostException
	 */
	public boolean Connect(final String hostname, final int port, final String databaseName) throws UnknownHostException {
		// Get the identifier
		String key = getIdentifier(hostname, port, databaseName);

		// If a client does not exit, create one
		if (!clients.containsKey(key)) {
			DB database = new MongoClient(hostname, port).getDB(databaseName);

			clients.put(key, database);
			if (firstConnection == null) {
				firstConnection = key;
			}
		}

		return canConnect(key);
	}

	public DB getClient(final String mongoKey) {
		if (mongoKey == null && firstConnection != null) {
			return clients.get(firstConnection);
		}
		if (clients.containsKey(mongoKey)) {
			return clients.get(mongoKey);
		}
		return null;
	}

	public static String getIdentifier(final String hostname, final int port, final String databasename) {
		return String.format("mongodb://%1$2s:%2$d/%3$2s", hostname, port, databasename);
	}

	public boolean clientExists(final String mongoKey) {
		// Check if the client exists and we can connect
		return getClient(mongoKey) != null && canConnect(mongoKey);
	}

	public boolean canConnect(final String mongoKey) {
		boolean canConnect = true;
		try {
			getClient(mongoKey).getMongo().getDatabaseNames();
		} catch (MongoTimeoutException e) {
			canConnect = false;
		}
		return canConnect;
	}

	public static BasicDBObject idsFromString(final String id) {
		// Add the valid id's to the list (String and ObjectId)
		List<Object> ids = new ArrayList<>();
		ids.add(id);
		if (ObjectId.isValid(id)) {
			ids.add(new ObjectId(id));
		}
		return new BasicDBObject("$in", ids);
	}
}
