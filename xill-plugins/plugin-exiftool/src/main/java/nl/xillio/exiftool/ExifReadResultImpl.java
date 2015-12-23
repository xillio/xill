package nl.xillio.exiftool;

import me.biesaart.utils.Log;
import nl.xillio.exiftool.process.ExecutionResult;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * This class represents the base implementation of the ExifReadResult.
 *
 * @author Thomas Biesaart
 */
public class ExifReadResultImpl implements ExifReadResult {

    private static final Logger LOGGER = Log.get();
    private final ExecutionResult executionResult;
    private final int cacheSize;
    private static int counter;
    private Queue<ExifTags> tagsQueue = new ArrayDeque<>();
    private boolean isDone = false;

    public ExifReadResultImpl(ExecutionResult executionResult, int cacheSize) {
        this.executionResult = executionResult;
        this.cacheSize = cacheSize;
        // TODO:: Use a threadpool mechanism to process this
        Thread thread = new Thread(this::run, "ExecutionResult-" + counter++);
        thread.setDaemon(true);
        thread.start();
    }

    private void run() {
        while (executionResult.hasNext()) {
            if (tagsQueue.size() < cacheSize) {
                readOneDocument();
            } else {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    LOGGER.error("Interrupted", e);
                }
            }
        }

        isDone = true;
    }

    private void readOneDocument() {
        LOGGER.debug("Reading document {}", executionResult.next());
    }

    @Override
    public boolean hasNext() {
        while(!isDone && tagsQueue.isEmpty()) {
            // We should have a next but nothing is in the queue. So we wait
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LOGGER.error("Interrupter", e);
            }
        }
        return !isDone;
    }

    @Override
    public ExifTags next() {
        return tagsQueue.remove();
    }
}
