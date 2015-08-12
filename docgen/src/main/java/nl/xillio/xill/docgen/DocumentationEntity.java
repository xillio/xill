package nl.xillio.xill.docgen;

import java.util.Map;

/**
 * This interface represents a piece of documentation of xill constructs
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public interface DocumentationEntity {
    String getIdentity();
    Map<String, ? extends Object> getProperties();
}
