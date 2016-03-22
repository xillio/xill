package nl.xillio.xill.plugins.concurrency.data;

import nl.xillio.xill.api.components.MetaExpression;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static nl.xillio.xill.api.components.ExpressionBuilderHelper.NULL;

/**
 * This implementation of the XillQueue logs every item pushed into it to the console and
 * iterates results from a list.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
public class MockXillQueue extends XillQueue {
    private final Iterator<MetaExpression> iterator;
    /**
     * This is not a normal logger. It is the logger for a robot.
     */
    @SuppressWarnings("squid:S1312")
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

    // The exception is handled by returning null
    // Note that this must happen instead of hasNext to
    // make this method threadsafe
    @SuppressWarnings("squid:S1166")
    @Override
    public MetaExpression pop() {
        try {
            MetaExpression result = iterator.next();
            if (!iterator.hasNext()) {
                close();
            }
            return result;
        } catch (NoSuchElementException e) {
            // This has to be caught because of concurrency issues
            close();
            return NULL;
        }
    }
}
