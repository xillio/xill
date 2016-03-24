package nl.xillio.xill.plugins.concurrency.data;

import me.biesaart.utils.Log;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.data.MetadataExpression;
import org.slf4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static nl.xillio.xill.api.components.ExpressionBuilderHelper.NULL;

/**
 * This class represents an iterable expression that is backed by a blocking queue.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
public class XillQueue implements MetadataExpression {
    private static final Logger LOGGER = Log.get();
    private final BlockingQueue<MetaExpression> queue;
    private boolean closed;

    public XillQueue(int capacity) {
        queue = new ArrayBlockingQueue<>(capacity);
    }


    public void push(MetaExpression expression) {
        try {
            expression.preventDisposal();
            queue.put(expression);
        } catch (InterruptedException e) {
            LOGGER.info("Interrupted while putting", e);
        }
    }

    void closeAndClear() {
        queue.clear();
        close();
    }

    public MetaExpression pop() {
        while (true) {
            MetaExpression result = getNext(200);

            if (result != null) {
                result.allowDisposal();
                return result;
            } else if (closed) {
                break;
            }
        }

        return NULL;
    }

    private MetaExpression getNext(int timeout) {
        try {
            return queue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while polling queue", e);
            return null;
        }
    }

    /**
     * Mark this queue as closed so that readers can stop reading.
     * Note that this class does not implement {@link AutoCloseable}
     */
    public void close() {
        closed = true;
    }

    public void clearAndClose() {
        queue.clear();
        close();
    }
}
