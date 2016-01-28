package nl.xillio.xill.plugins.database.services;

import com.google.inject.Singleton;
import nl.xillio.xill.plugins.database.util.Database;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates services for specific DBMSs.
 *
 * @author Geert Konijnendijk
 */
@Singleton
public class DatabaseServiceFactory {

    // Map from name to service
    private Map<String, BaseDatabaseService> services = new HashMap<>();

    /**
     * Get a service given a DBMS name from {@link Database}
     *
     * @param name The name from a {@link Database} value
     * @return A {@link DatabaseService} suited for the given DBMS
     * @throws ReflectiveOperationException When the driver for the service can not be loaded
     */
    public DatabaseService getService(String name) throws ReflectiveOperationException, IllegalArgumentException {
        if (!services.containsKey(name)) {
            BaseDatabaseService service = Database.findServiceClass(name).newInstance();
            services.put(name, service);
            service.loadDriver();
        }
        return services.get(name);
    }
}
