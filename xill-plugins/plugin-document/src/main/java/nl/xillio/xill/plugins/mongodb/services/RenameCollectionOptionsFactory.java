package nl.xillio.xill.plugins.mongodb.services;

import com.google.inject.Inject;
import com.mongodb.client.model.RenameCollectionOptions;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;

import java.util.Map;

/**
 * Provides a factory method that creates options objects for the {@link nl.xillio.xill.plugins.mongodb.constructs.FindOneAndUpdateConstruct FindOneAndUpdateConstruct}
 *
 * @author Titus Nachbauer
 */
public class RenameCollectionOptionsFactory {
    private final MongoConverter converter;

    @Inject
    RenameCollectionOptionsFactory(MongoConverter converter) {
        this.converter = converter;
    }

    /**
     * Build {@link RenameCollectionOptions} from a MetaExpression.
     *
     * @param argument the expression. This must be an {@link ExpressionDataType#OBJECT}
     * @return the RenameCollectionOptions
     */
    public RenameCollectionOptions build(MetaExpression argument) {
        if (argument.getType() != ExpressionDataType.ATOMIC) {
            throw new IllegalArgumentException("The options argument has to be an ATOMIC");
        }

        RenameCollectionOptions result = new RenameCollectionOptions();

        processOption("dropTarget", argument, result);

        return result;
    }

    private void processOption(String option, MetaExpression value, RenameCollectionOptions options) {
        switch (option) {
            case "dropTarget":
                options.dropTarget(value.getBooleanValue());
                break;
        }
    }
}
