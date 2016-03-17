package nl.xillio.xill.plugins.concurrency.data;

import nl.xillio.xill.api.components.MetaExpression;
import org.slf4j.Logger;

import java.util.Iterator;

/**
 * This implementation of the XillQueue logs every item pushed into it to the console and
 * iterates results from a list.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
public class MockXillQueue extends XillQueue {
    private final Iterator<MetaExpression> iterator;
    private final Logger logger;

    public MockXillQueue(Iterable<MetaExpression> iterable, Logger logger) {
        this(iterable.iterator(), logger);
    }

    public MockXillQueue(Iterator<MetaExpression> input, Logger logger) {
        super(10);
        iterator = input;
        this.logger = logger;
    }

    @Override
    public void push(MetaExpression expression) {
        logger.info("Pushing a value to the mock output: {}", expression);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public synchronized MetaExpression next() {
        return iterator.next();
    }
}
