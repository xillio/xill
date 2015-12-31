package nl.xillio.xill.docgen;

import nl.xillio.xill.docgen.exceptions.ParsingException;

import java.net.URL;

/**
 * This interface represents an object that can parse a {@link DocumentationEntity} from a resource
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public interface DocumentationParser {
    DocumentationEntity parse(URL resource, String identity) throws ParsingException;
}
