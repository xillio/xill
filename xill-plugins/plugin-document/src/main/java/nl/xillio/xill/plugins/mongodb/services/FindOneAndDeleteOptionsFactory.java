package nl.xillio.xill.plugins.mongodb.services;

import com.google.inject.Inject;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Provides a factory method that creates options objects for the {@link nl.xillio.xill.plugins.mongodb.constructs.FindOneAndDeleteConstruct FindOneAndDeleteConstruct}
 *
 * @author Titus Nachbauer
 */
public class FindOneAndDeleteOptionsFactory {
    private final MongoConverter converter;

    @Inject
    FindOneAndDeleteOptionsFactory(MongoConverter converter) {
        this.converter = converter;
    }

    /**
     * Build {@link FindOneAndDeleteOptions} from a MetaExpression.
     *
     * @param argument the expression. This must be an {@link ExpressionDataType#OBJECT}
     * @return the FindOneAndDeleteOptions
     */
    public FindOneAndDeleteOptions build(MetaExpression argument) {
        if (argument.getType() != ExpressionDataType.OBJECT) {
            throw new IllegalArgumentException("The options argument has to be an object");
        }

        Map<String, MetaExpression> options = argument.getValue();

        FindOneAndDeleteOptions result = new FindOneAndDeleteOptions();

        options.forEach((option, value) -> processOption(option, value, result));

        return result;
    }

    private void processOption(String option, MetaExpression value, FindOneAndDeleteOptions options) {
        switch (option) {
            case "projection":
                options.projection(converter.parse(value));
                break;
            case "sortBy":
                options.sort(converter.parse(value));
                break;
            case "maxTime":
                options.maxTime(value.getNumberValue().longValue(), TimeUnit.MILLISECONDS);
                break;
        }
    }
}