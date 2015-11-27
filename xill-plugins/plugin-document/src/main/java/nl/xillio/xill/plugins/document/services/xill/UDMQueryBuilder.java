package nl.xillio.xill.plugins.document.services.xill;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import org.bson.Document;

/**
 * This is the main implementation of the {@link DocumentQueryBuilder}.
 *
 * @author Thomas Biesaart
 */
public class UDMQueryBuilder implements DocumentQueryBuilder {

    @Override
    public Document parseQuery(MetaExpression expression) {
        if (expression.getType() != ExpressionDataType.OBJECT) {
            throw new IllegalArgumentException("Can only parse OBJECT to a query");
        }

        String json = expression.toString();
        return Document.parse(json);
    }
}
