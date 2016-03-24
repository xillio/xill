package nl.xillio.xill.plugins.concurrency.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.concurrency.data.XillQueue;

/**
 * This construct will take an item from a queue. It will block while the queue is empty or return null
 * if the queue is closed.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
class PopConstruct extends Construct {

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("inputQueue", ATOMIC)
        );
    }

    @SuppressWarnings("squid:UnusedPrivateMethod") // Sonar does not do method references
    private MetaExpression process(MetaExpression outputQueue) {
        XillQueue queue = assertMeta(outputQueue, "inputQueue", XillQueue.class, "Concurrency Queue");
        return queue.pop();
    }
}
