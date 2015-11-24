package nl.xillio.xill.plugins.document.constructs;

import com.google.inject.Inject;
import me.biesaart.utils.ExceptionUtils;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.services.XillUDMQueryService;
import nl.xillio.xill.plugins.document.services.xill.DocumentQueryBuilder;
import org.bson.Document;

/**
 * This construct will delete all documents that match a filter.
 *
 * @author Thomas Biesaart
 */
public class DeleteWhereConstruct extends Construct {
    private final XillUDMQueryService queryService;
    private final DocumentQueryBuilder queryBuilder;

    @Inject
    public DeleteWhereConstruct(XillUDMQueryService queryService, DocumentQueryBuilder queryBuilder) {
        this.queryService = queryService;
        this.queryBuilder = queryBuilder;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("filter", emptyObject(), OBJECT)
        );
    }

    private MetaExpression process(MetaExpression filter) {
        // Parse the filter
        Document filterDoc = queryBuilder.parseQuery(filter);

        long count = runQuery(filterDoc);

        return fromValue(count);
    }

    private long runQuery(Document filterDoc) {
        try {
            return queryService.delete(filterDoc);
        } catch (PersistenceException e) {
            throw new RobotRuntimeException(buildErrorMessage(e), e);
        }
    }

    private String buildErrorMessage(PersistenceException e) {
        Throwable root = ExceptionUtils.getRootCause(e);
        String message = e.getMessage();

        if (root != null) {
            message += ": " + root.getMessage();
        }

        return message;
    }
}
