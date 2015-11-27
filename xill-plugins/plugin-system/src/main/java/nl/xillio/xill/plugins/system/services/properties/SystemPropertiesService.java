package nl.xillio.xill.plugins.system.services.properties;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.services.PropertiesProvider;

/**
 * This {@link PropertiesProvider} can provide system properties
 */
@ImplementedBy(SystemPropertiesServiceImpl.class)
public interface SystemPropertiesService extends PropertiesProvider {

    /**
     * Get a single property
     *
     * @param key the property key
     * @return the property value or null
     */
    public String getProperty(String key);

}
