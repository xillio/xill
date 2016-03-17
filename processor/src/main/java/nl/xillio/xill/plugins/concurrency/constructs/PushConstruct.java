package nl.xillio.xill.plugins.concurrency.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * This construct will push an item into a queue. It will block while the queue is full and throw an error of the queue
 * is closed.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
public class PushConstruct extends Construct {

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("item"),
                new Argument("outputQueue", ATOMIC)
        );
    }

    private MetaExpression process(MetaExpression item, MetaExpression outputQueue) {
        return NULL;
    }
}
