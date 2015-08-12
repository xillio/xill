package nl.xillio.xill.docgen;

import java.util.List;

import nl.xillio.xill.docgen.impl.ConstructDocumentationEntity;

/**
 * This interface represents an object that can run search queries on a collection of entities
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public interface DocumentationSearcher {
    String[] search(String query);
    
    void index(String packet, ConstructDocumentationEntity entity);
}
