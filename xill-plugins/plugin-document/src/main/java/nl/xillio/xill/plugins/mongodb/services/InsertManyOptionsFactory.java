package nl.xillio.xill.plugins.mongodb.services;

import com.google.inject.Inject;
import com.mongodb.client.model.InsertManyOptions;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;

import java.util.Map;

/**
 * This class is responsible for converting a {@link MetaExpression} to {@link InsertManyOptions}.
 *
 * @author Titus Nachbauer
 */
public class InsertManyOptionsFactory {
    private final MongoConverter converter;

    @Inject
    InsertManyOptionsFactory(MongoConverter converter) {
        this.converter = converter;
    }

    /**
     * Build {@link InsertManyOptions} from a MetaExpression.
     *
     * @param argument the expression. This must be an {@link ExpressionDataType#OBJECT}
     * @return the InsertManyOptions
     */
    public InsertManyOptions build(MetaExpression argument) {
        if (argument.getType() != ExpressionDataType.ATOMIC) {
            throw new IllegalArgumentException("The options argument has to be an ATOMIC");
        }

        InsertManyOptions result = new InsertManyOptions();

        processOption("ordered", argument, result);

        return result;
    }

    private void processOption(String option, MetaExpression value, InsertManyOptions options) {
        switch (option) {
            case "ordered":
                options.ordered(value.getBooleanValue());
                break;
        }
    }
}
