package nl.xillio.xill.plugins.document.services.xill;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.api.components.MetaExpression;

/**
 * This interface represents an object that can construct the UMD structure from input parameters.
 *
 * @author Thomas Biesaart
 */
@ImplementedBy(UDMDocumentFactory.class)
public interface UDMDocumentConstructor {
    /**
     * Build a document structure.
     *
     * @param contentType    the content type name
     * @param currentVersion the body of the current version
     * @param history        a list of bodies that represent the version history
     * @return the document
     */
    MetaExpression buildStructure(String contentType, MetaExpression currentVersion, MetaExpression history);
}
