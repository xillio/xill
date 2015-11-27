package nl.xillio.xill.plugins.document.data;


import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.xill.plugins.document.exceptions.ValidationException;

/**
 * This interface represents an object that represents a Document in the {@link nl.xillio.udm.UDM}.
 *
 * @author Thomas Biesaart
 */
public interface UDMDocument {
    /**
     * Apply this document to a builder.
     *
     * @param builder the builder
     * @throws ValidationException if structural problems are detected
     */
    void applyTo(DocumentBuilder builder) throws ValidationException;

    /**
     * @return true if and only if this document has not been persisted yet
     */
    boolean isNew();

    /**
     * @return the id string of this document
     */
    String getId();
}
