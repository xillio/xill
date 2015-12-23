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
    private final Queue<ExifTags> tagsQueue;
    private ExifTags currentValue;
    private boolean isDone = false;
    private boolean isReading = false;

    public ExifReadResultImpl(ExecutionResult executionResult, int cacheSize) {
        this.executionResult = executionResult;
        this.cacheSize = cacheSize;
        this.tagsQueue = new ArrayDeque<>(cacheSize + 3);

        // TODO:: Use a thread pool mechanism to process this
        Thread thread = new Thread(this::run, "ExecutionResult-" + counter++);
        thread.setDaemon(true);
        thread.start();

    }

    private void run() {
        LOGGER.debug("Start processing");
        while (executionResult.hasNext()) {
            if (tagsQueue.size() < cacheSize) {
                if(isReading) {
                    throw new IllegalStateException("The reader is already being used");
                }
                isReading = true;
                readOneDocument();
                isReading = false;
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
        if(tagsQueue.size() > cacheSize) {
            LOGGER.warn("Slightly exceed cache size: {}/{}", tagsQueue.size(), cacheSize);
        }


        while (executionResult.hasNext()) {
            String line = executionResult.next();

            if (line.startsWith("========")) {
                // This is the start of a file. So skip to next
                if (currentValue != null) {
                    // Save parsed entry
                    tagsQueue.add(currentValue);

                    // Create new entry
                    currentValue = new ExifTagsImpl();
                    currentValue.put("File Path", line.replaceAll("^=+\\s*", ""));

                    // Stop parsing
                    return;
                } else {

                    // Create new entry
                    currentValue = new ExifTagsImpl();
                    currentValue.put("File Path", line.replaceAll("^=+\\s*", ""));
                    continue;
                }
            }

            int separator = line.indexOf(":");

            if (separator == -1) {
                LOGGER.error("Failed to parse [{}] as a field", line);
                continue;
            }

            String key = line.substring(0, separator).trim();
            String value = line.substring(separator + 1).trim();

            currentValue.put(key, value);
        }

        // Save the last item
        tagsQueue.add(currentValue);
        currentValue = null;
    }

    @Override
    public boolean hasNext() {
        while (tagsQueue.isEmpty() && executionResult.hasNext()) {
            // We should have a next but nothing is in the queue. So we wait
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                LOGGER.error("Interrupter", e);
            }
        }

        return !tagsQueue.isEmpty();
    }

    @Override
    public ExifTags next() {
        return tagsQueue.remove();
    }
}
