package nl.xillio.xill.docgen;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This interface represents an object that can hold properties
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public interface PropertiesProvider {
    Map<String, Object> getProperties();

    /**
     * Extract the properties from a collection of PropertiesProviders
     *
     * @param collection the properties collection
     * @return a list of sets of properties
     */
    static List<Map<String, Object>> extractContent(Collection<? extends PropertiesProvider> collection) {
        return collection.stream()
                .map(PropertiesProvider::getProperties)
                .collect(Collectors.toList());
    }
}
