package nl.xillio.xill.docgen;

import nl.xillio.xill.docgen.exceptions.ParsingException;

/**
 * This interface represents an object that can generate output from a {@link DocumentationEntity}
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public interface DocumentationGenerator extends AutoCloseable {
    void generate(DocumentationEntity entity) throws ParsingException;
}
