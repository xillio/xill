package nl.xillio.xill.services;

import java.util.Map;

/**
 * This interface represents an object that have properties
 */
public interface PropertiesProvider extends XillService {
    /**
     * Get a map of properties indexed by their name that belong to this object.
     *
     * @return the map, not null
     */
    public Map<String, Object> getProperties();
}
