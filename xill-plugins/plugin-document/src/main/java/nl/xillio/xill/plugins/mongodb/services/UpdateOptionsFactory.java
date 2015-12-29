package nl.xillio.xill.plugins.mongodb.services;

import com.google.inject.Inject;
import com.mongodb.client.model.UpdateOptions;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;

import java.util.Map;

/**
 *
 */
public class UpdateOptionsFactory {
    private final MongoConverter converter;

    @Inject
    UpdateOptionsFactory(MongoConverter converter) {
        this.converter = converter;
    }

    /**
     * Build {@link UpdateOptions} from a MetaExpression.
     *
     * @param argument the expression. This must be an {@link ExpressionDataType#OBJECT}
     * @return the UpdateOptions
     */
    public UpdateOptions build(MetaExpression argument) {
        if (argument.getType() != ExpressionDataType.OBJECT) {
            throw new IllegalArgumentException("The options argument has to be an object");
        }

        Map<String, MetaExpression> options = argument.getValue();

        UpdateOptions result = new UpdateOptions();

        options.forEach((option, value) -> processOption(option, value, result));

        return result;
    }

    private void processOption(String option, MetaExpression value, UpdateOptions options) {
        switch (option) {
            case "upsert":
                options.upsert(value.getBooleanValue());
                break;
        }
    }
}
