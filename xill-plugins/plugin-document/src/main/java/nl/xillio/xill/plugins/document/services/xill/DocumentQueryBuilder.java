package nl.xillio.xill.plugins.document.services.xill;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.document.services.xill.UDMQueryBuilder;
import org.bson.Document;

/**
 * This interface represents an object that can parse a {@link MetaExpression} to a {@link Document} query.
 */
@ImplementedBy(UDMQueryBuilder.class)
public interface DocumentQueryBuilder {
    Document parseQuery(MetaExpression expression);
}
