package nl.xillio.xill.services;

import java.util.Map;

/**
 * This interface represents an object that have properties.
 */
public interface PropertiesProvider extends XillService {
    /**
     * Gets a map of properties that belong to this object, indexed by their name.
     *
     * @return the map, not null
     */
    public Map<String, Object> getProperties();
}
