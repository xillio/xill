package nl.xillio.xill.plugins.concurrency.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * This construct provides some mock queues and input that allows a single robot to be tested.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
public class TestInputConstruct extends Construct {
    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("config", emptyObject(), OBJECT),
                new Argument("input", emptyList(), LIST)
        );
    }

    private MetaExpression process(MetaExpression configuration, MetaExpression input) {
        return NULL;
    }
}
