package nl.xillio.xill.plugins.concurrency.data;

import me.biesaart.utils.Log;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.data.MetadataExpression;
import org.slf4j.Logger;

import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This class represents an iterable expression that is backed by a blocking queue.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
public class XillQueue extends MetaExpressionIterator implements MetadataExpression {
    private static final Logger LOGGER = Log.get();
    private final BlockingQueue<MetaExpression> queue;
    private MetaExpression next;
    private boolean closed;

    public XillQueue(int capacity) {
        super(null, null);
        queue = new ArrayBlockingQueue<>(capacity);
    }

    @Override
    public synchronized MetaExpression next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No next element found");
        }

        MetaExpression result = next;
        next = null;
        return result;
    }

    @Override
    public boolean hasNext() {
        cacheItem();
        return next != null;
    }

    private void cacheItem() {
        while (next == null) {
            next = getNext(100);

            if (closed) {
                break;
            }
        }
    }

    private MetaExpression getNext(int timeout) {
        try {
            return queue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while polling queue", e);
            return null;
        }
    }

    @Override
    public void close() {
        closed = true;
    }

    public void push(MetaExpression expression) {
        try {
            queue.put(expression);
        } catch (InterruptedException e) {
            LOGGER.info("Interrupted while putting", e);
        }
    }

    public void closeAndClear() {
        queue.clear();
        close();
    }
}
