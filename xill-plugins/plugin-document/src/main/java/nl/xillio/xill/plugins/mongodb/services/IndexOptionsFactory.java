package nl.xillio.xill.plugins.mongodb.services;

import com.mongodb.client.model.IndexOptions;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;

import java.util.Map;

/**
 * This class is responsible for converting a {@link MetaExpression} to {@link IndexOptions}.
 *
 * @author Thomas Biesaart
 */
public class IndexOptionsFactory {

    public IndexOptions build(MetaExpression argument) {
        if (argument.getType() != ExpressionDataType.OBJECT) {
            throw new IllegalArgumentException("The options argument has to be an object");
        }

        Map<String, MetaExpression> options = argument.getValue();

        IndexOptions result = new IndexOptions();

        options.forEach((option, value) -> processOption(option, value, result));

        return result;
    }

    private void processOption(String option, MetaExpression value, IndexOptions options) {

    }
}
