package nl.xillio.xill.docgen.impl;

import nl.xillio.xill.docgen.DocumentationEntity;
import nl.xillio.xill.docgen.DocumentationSearcher;

import java.util.List;

/**
 * //TODO javadoc
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public class ElasticsearchDocumentationSearcher implements DocumentationSearcher {
    @Override
    public List<DocumentationEntity> search(String query) {
        return null;
    }

    @Override
    public void index(DocumentationEntity entity) {

    }
}
