package nl.xillio.exiftool;

import me.biesaart.utils.Log;
import nl.xillio.exiftool.process.ExecutionResult;
import nl.xillio.exiftool.query.ExifReadResult;
import nl.xillio.exiftool.query.ExifTags;
import nl.xillio.exiftool.query.TagNameConvention;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * This class represents the base implementation of the ExifReadResult.
 *
 * @author Thomas Biesaart
 */
class ExifReadResultImpl implements ExifReadResult {
    private static final Logger LOGGER = Log.get();
    private final ExecutionResult executionResult;
    private final int cacheSize;
    private final TagNameConvention tagNameConvention;
    private static int counter;
    private final Queue<ExifTags> tagsQueue;
    private ExifTags currentValue;
    private boolean isReading = false;

    public ExifReadResultImpl(ExecutionResult executionResult, int cacheSize, TagNameConvention tagNameConvention) {
        this.executionResult = executionResult;
        this.cacheSize = cacheSize;
        this.tagNameConvention = tagNameConvention;
        this.tagsQueue = new ArrayDeque<>(cacheSize + 3);

        // TODO:: Use a thread pool mechanism to process this
        Thread thread = new Thread(this::run, "ExecutionResult-" + counter++);
        thread.setDaemon(true);
        thread.start();
    }

    @SuppressWarnings("squid:S1068") // Sonar doesn't do lambdas
    private void run() {
        LOGGER.debug("Start processing");
        while (executionResult.hasNext()) {
            if (tagsQueue.size() < cacheSize) {
                if (isReading) {
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
    }

    private void readOneDocument() {
        if (tagsQueue.size() > cacheSize) {
            LOGGER.warn("Slightly exceed cache size: {}/{}", tagsQueue.size(), cacheSize);
        }


        while (executionResult.hasNext()) {
            String line = executionResult.next();

            if (line.startsWith("========")) {
                // This is the start of a file.
                push();
                put("File Path", line.replaceAll("^=+\\s*", ""));

            } else {
                processLine(line);
            }


        }

        push();
    }

    private void processLine(String line) {
        int separator = line.indexOf(":");

        if (separator == -1) {
            LOGGER.error("Failed to parse [{}] as a field", line);
        } else {
            String key = line.substring(0, separator).trim();
            String value = line.substring(separator + 1).trim();

            put(key, value);
        }
    }

    private void push() {
        if (currentValue != null) {
            // Save the last item
            tagsQueue.add(currentValue);
            currentValue = null;
        }
    }

    private void put(String key, String value) {
        if (currentValue == null) {
            currentValue = new ExifTagsImpl();
        }

        currentValue.put(tagNameConvention.toConvention(key), value);
    }

    @Override
    public boolean hasNext() {
        while (tagsQueue.isEmpty() && executionResult.hasNext()) {
            // We should have a next but nothing is in the queue. So we wait
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted", e);
            }
        }

        return !tagsQueue.isEmpty();
    }

    @Override
    public ExifTags next() {
        return tagsQueue.remove();
    }

    @Override
    public void close() {
        executionResult.close();
    }
}
