package nl.xillio.xill.plugins.document.services.xill;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.document.data.UDMDocument;

/**
 * This interface represents an object that can build  {@link UDMDocument} from a {@link MetaExpression}.
 *
 * @author Thomas Biesaart
 */
@ImplementedBy(UDMDocumentFactory.class)
public interface UDMDocumentBuilder {
    /**
     * Parse a metaexpression into a document.
     *
     * @param expression the expresion
     * @return the document
     */
    UDMDocument build(MetaExpression expression);
}
