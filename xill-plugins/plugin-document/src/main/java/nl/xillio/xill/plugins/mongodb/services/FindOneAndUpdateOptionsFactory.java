package nl.xillio.xill.plugins.mongodb.services;

import com.google.inject.Inject;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class FindOneAndUpdateOptionsFactory {
    private final MongoConverter converter;

    @Inject
    FindOneAndUpdateOptionsFactory(MongoConverter converter) {
        this.converter = converter;
    }

    /**
     * Build {@link FindOneAndUpdateOptions} from a MetaExpression.
     *
     * @param argument the expression. This must be an {@link ExpressionDataType#OBJECT}
     * @return the FindOneAndUpdateOptions
     */
    public FindOneAndUpdateOptions build(MetaExpression argument) {
        if (argument.getType() != ExpressionDataType.OBJECT) {
            throw new IllegalArgumentException("The options argument has to be an object");
        }

        Map<String, MetaExpression> options = argument.getValue();

        FindOneAndUpdateOptions result = new FindOneAndUpdateOptions();

        options.forEach((option, value) -> processOption(option, value, result));

        return result;
    }

    private void processOption(String option, MetaExpression value, FindOneAndUpdateOptions options) {
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
