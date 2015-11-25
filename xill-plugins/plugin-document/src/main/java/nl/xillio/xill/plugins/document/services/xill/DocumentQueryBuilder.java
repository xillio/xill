package nl.xillio.xill.plugins.document.services.xill;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.api.components.MetaExpression;
import org.bson.Document;

/**
 * This interface represents an object that can parse a {@link MetaExpression} to a {@link Document} query.
 *
 * @author Thomas Biesaart
 */
@ImplementedBy(UDMQueryBuilder.class)
public interface DocumentQueryBuilder {
    /**
     * Parse a {@link MetaExpression} to a query.
     *
     * @param expression the expression
     * @return the MongoDB query
     */
    Document parseQuery(MetaExpression expression);
}
