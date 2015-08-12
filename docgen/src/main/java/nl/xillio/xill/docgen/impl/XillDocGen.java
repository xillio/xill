package nl.xillio.xill.docgen.impl;

import nl.xillio.xill.docgen.DocGen;
import nl.xillio.xill.docgen.DocumentationGenerator;
import nl.xillio.xill.docgen.DocumentationParser;
import nl.xillio.xill.docgen.DocumentationSearcher;

/**
 * This {@link DocGen} uses a template system and a elasticsearch backed searcher
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public class XillDocGen implements DocGen {

    public XillDocGen() {
        //TODO Build elasticsearch node
    }

    @Override
    public DocumentationParser getParser() {
        return new XmlDocumentationParser();
    }

    @Override
    public DocumentationGenerator getGenerator(String collectionIdentity) {
        return new FreeMarkerDocumentationGenerator(collectionIdentity);
    }

    @Override
    public DocumentationSearcher getSearcher() {
        return new ElasticsearchDocumentationSearcher();
    }


}
