package nl.xillio.xill.docgen;

import java.util.Map;

/**
 * //TODO javadoc
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public interface PropertiesProvider {
    Map<String, ? extends Object> getProperties();
}
