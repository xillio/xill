package nl.xillio.xill.docgen;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.docgen.impl.XillDocGen;

/**
 * This interface represents the main entry point of the documentation generation system
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
@ImplementedBy(XillDocGen.class)
public interface DocGen {
    DocumentationParser getParser();
    DocumentationGenerator getGenerator(String collectionIdentity);
    DocumentationSearcher getSearcher();
}
