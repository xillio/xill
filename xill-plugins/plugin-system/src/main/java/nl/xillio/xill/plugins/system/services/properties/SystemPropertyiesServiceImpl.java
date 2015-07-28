package nl.xillio.xill.plugins.system.services.properties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.inject.Singleton;

/**
 * This is the default implementation of the {@link SystemPropertiesService}
 */
@Singleton
public class SystemPropertyiesServiceImpl implements SystemPropertiesService {

	@Override
	public Map<String, Object> getProperties() {
		Map<Object, Object> properties = new LinkedHashMap<>();

		properties.putAll(System.getProperties());
		properties.putAll(System.getenv());

		return properties.entrySet().stream().collect(Collectors.toMap(Object::toString, Function.identity()));
	}

	@Override
	public String getProperty(final String key) {
		String result = System.getProperty(key);

		if (result == null) {
			result = System.getenv(key);
		}

		return result;
	}

}
