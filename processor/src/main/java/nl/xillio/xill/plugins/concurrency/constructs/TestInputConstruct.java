package nl.xillio.xill.plugins.concurrency.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.components.WrappingIterator;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.concurrency.data.MockXillQueue;

import java.util.LinkedHashMap;

/**
 * This construct provides some mock queues and input that allows a single robot to be tested.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
class TestInputConstruct extends Construct {

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                (config, input) -> process(config, input, context),
                new Argument("config", emptyObject(), OBJECT),
                new Argument("input", emptyList(), LIST)
        );
    }

    private MetaExpression process(MetaExpression configuration, MetaExpression input, ConstructContext context) {
        LinkedHashMap<String, MetaExpression> result = configuration.getValue();
        result.put("input", buildQueue(input, context));
        result.put("output", buildQueue(input, context));
        result.put("threadId", fromValue(0));
        return fromValue(result);
    }

    private MetaExpression buildQueue(MetaExpression input, ConstructContext context) {
        MetaExpressionIterator<MetaExpression> iterator = WrappingIterator.identity(input);

        MetaExpression result = fromValue("[Mock Queue]");
        result.storeMeta(new MockXillQueue(iterator, context.getRootLogger()));
        result.registerReference();
        return result;
    }
}
