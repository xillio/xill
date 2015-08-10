package nl.xillio.xill.plugins.database.services;

import java.util.HashMap;
import java.util.Map;

import nl.xillio.xill.plugins.database.util.Database;

public class DatabaseServiceFactory {

	// Map from name to service
	private Map<String, BaseDatabaseService> services = new HashMap<>();

	public DatabaseService getService(String name) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (!services.containsKey(name)) {
			BaseDatabaseService service = Database.findServiceClass(name).newInstance();
			services.put(name, service);
			service.loadDriver();
		}
		return services.get(name);
	}
}
