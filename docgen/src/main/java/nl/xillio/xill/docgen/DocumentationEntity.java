package nl.xillio.xill.docgen;


/**
 * This interface represents a piece of documentation of xill constructs
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public interface DocumentationEntity extends PropertiesProvider {
    /**
     * Get the identity of this name
     * @return a single word identity that represents this entry. This would generally be a document name.
     */
    String getIdentity();

    /**
     * Get the type of entity
     * @return a single word identifier that represents the entity type
     */
    String getType();
}
