package nl.xillio.xill.plugins.system.services.properties;

import com.google.inject.Singleton;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is the default implementation of the {@link SystemPropertiesService}
 */
@Singleton
public class SystemPropertiesServiceImpl implements SystemPropertiesService {

    @Override
    public Map<String, Object> getProperties() {
        Map<Object, Object> properties = new LinkedHashMap<>();

        properties.putAll(System.getProperties());
        properties.putAll(System.getenv());

        return properties.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
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
