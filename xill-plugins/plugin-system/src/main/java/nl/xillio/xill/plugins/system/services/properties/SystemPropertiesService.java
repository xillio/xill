package nl.xillio.xill.plugins.system.services.properties;

import nl.xillio.xill.services.PropertiesProvider;

/**
 * This {@link PropertiesProvider} can provide system properties
 */
public interface SystemPropertiesService extends PropertiesProvider{
	
	/**
	 * Get a single property
	 * @param key the property key
	 * @return the property value or null
	 */
	public String getProperty(String key);

}
