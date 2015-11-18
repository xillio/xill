package nl.xillio.xill.plugins.document.services.xill;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.api.components.MetaExpression;

/**
 * This interface represents an object that can construct the UMD structure from input parameters.
 */
@ImplementedBy(UDMDocumentFactory.class)
public interface UDMDocumentConstructor {
    MetaExpression buildStructure(String contentType, MetaExpression currentVersion, MetaExpression history);
}
