package nl.xillio.xill.plugins.mongodb.services;

import com.google.inject.Inject;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Provides a factory method that creates options objects for the {@link nl.xillio.xill.plugins.mongodb.constructs.FindOneAndReplaceConstruct FindOneAndReplaceConstruct}
 *
 * @author Titus Nachbauer
 */
public class FindOneAndReplaceOptionsFactory {
    private final MongoConverter converter;

    @Inject
    FindOneAndReplaceOptionsFactory(MongoConverter converter) {
        this.converter = converter;
    }

    /**
     * Build {@link FindOneAndReplaceOptions} from a MetaExpression.
     *
     * @param argument the expression. This must be an {@link ExpressionDataType#OBJECT}
     * @return the FindOneAndReplaceOptions
     */
    public FindOneAndReplaceOptions build(MetaExpression argument) {
        if (argument.getType() != ExpressionDataType.OBJECT) {
            throw new IllegalArgumentException("The options argument has to be an object");
        }

        Map<String, MetaExpression> options = argument.getValue();

        FindOneAndReplaceOptions result = new FindOneAndReplaceOptions();

        options.forEach((option, value) -> processOption(option, value, result));

        return result;
    }

    private void processOption(String option, MetaExpression value, FindOneAndReplaceOptions options) {
        switch (option) {
            case "projection":
                options.projection(converter.parse(value));
                break;
            case "sortBy":
                options.sort(converter.parse(value));
                break;
            case "upsert":
                options.upsert(value.getBooleanValue());
                break;
            case "returnNew":
                if (value.getBooleanValue() == true) {
                    options.returnDocument(ReturnDocument.AFTER);
                } else {
                    options.returnDocument(ReturnDocument.BEFORE);
                }
                break;
            case "maxTime":
                options.maxTime(value.getNumberValue().longValue(), TimeUnit.MILLISECONDS);
                break;
        }
    }
}
