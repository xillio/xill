package nl.xillio.xill.plugins.document.constructs;

import com.google.inject.Inject;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.services.XillUDMQueryService;
import nl.xillio.xill.plugins.document.services.xill.DocumentQueryBuilder;
import org.bson.Document;

import java.util.Iterator;
import java.util.Map;

/**
 * This construct will find all documents that match a filter.
 *
 * @author Thomas Biesaart
 */
public class FindConstruct extends Construct {
    private final XillUDMQueryService queryService;
    private final DocumentQueryBuilder queryBuilder;

    @Inject
    public FindConstruct(XillUDMQueryService queryService, DocumentQueryBuilder queryBuilder) {
        this.queryService = queryService;
        this.queryBuilder = queryBuilder;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("filter", emptyObject(), OBJECT),
                new Argument("sort", emptyObject(), OBJECT)
        );
    }

    @SuppressWarnings("squid:UnusedPrivateMethod") // Sonar doesn't understand method references
    private MetaExpression process(MetaExpression filter, MetaExpression sort) {
        // Parse the filter
        Document filterDoc = queryBuilder.parseQuery(filter);
        Document sortDoc = queryBuilder.parseQuery(sort);

        // Run the query
        Iterator<Map<String, Object>> searchResult = getResult(filterDoc, sortDoc);

        // Transform the result
        MetaExpressionIterator<Map<String, Object>> iterator = new MetaExpressionIterator<>(
                searchResult,
                MetaExpression::parseObject
        );

        // Build the result
        MetaExpression result = fromValue("Document.find(" + filter + ")");
        result.storeMeta(iterator);
        return result;
    }

    private Iterator<Map<String, Object>> getResult(Document filterDoc, Document sortDoc) {
        try {
            return queryService.findMapWhere(filterDoc, sortDoc);
        } catch (PersistenceException e) {
            throw new RobotRuntimeException(e.getMessage(), e);
        }
    }
}
