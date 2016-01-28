package nl.xillio.xill.docgen;

import nl.xillio.xill.docgen.exceptions.ParsingException;

import java.util.IllegalFormatException;

/**
 * This interface represents an object that can generate output from a {@link DocumentationEntity}
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public interface DocumentationGenerator extends AutoCloseable {
    /**
     * Generate a documentation file from a {@link DocumentationEntity}
     *
     * @param entity the {@link DocumentationEntity} to generate the output for
     * @throws ParsingException      if parsing the entity failed
     * @throws IllegalStateException if this DocumentationGenerator has already been closed
     */
    void generate(DocumentationEntity entity) throws ParsingException, IllegalFormatException;

    /**
     * Generate the index of all packages
     *
     * @throws ParsingException if parsing wasn't successful
     */
    void generateIndex() throws ParsingException;

    void setProperty(String name, String value);

    /**
     * Get the name of this generator.
     *
     * @return the name
     */
    String getIdentity();
}
