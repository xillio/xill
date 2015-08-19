package nl.xillio.xill.docgen;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.docgen.exceptions.ParsingException;
import nl.xillio.xill.docgen.impl.XillDocGen;

/**
 * This interface represents the main entry point of the documentation generation system
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
@ImplementedBy(XillDocGen.class)
public interface DocGen {
	/**
	 * Create a {@link DocumentationParser}
	 *
	 * @return the parser
	 * @throws ParsingException if the parser could not be initialized
	 */
	DocumentationParser getParser();

	/**
	 * Create a {@link DocumentationGenerator}
	 *
	 * @param collectionIdentity the name of this generator
	 * @return the generator
	 */
	DocumentationGenerator getGenerator(String collectionIdentity);

	/**
	 * Create a {@link DocumentationSearcher}
	 *
	 * @return the searcher
	 */
	DocumentationSearcher getSearcher();

	/**
	 * Get the configuration that was used to initialize this {@link DocGen}
	 *
	 * @return the configuration
	 */
	DocGenConfiguration getConfig();

	/**
	 * Generate the global index
	 *
	 * @throws ParsingException if generating the index fails
	 */
	void generateIndex() throws ParsingException;
}
