package nl.xillio.xill.api.io;

import me.biesaart.utils.Log;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * This class represents an abstract implementation of the IOStream.
 *
 * @author Thomas biesaart
 */
public abstract class AbstractIOStream implements IOStream {
    private static final Logger LOGGER = Log.get();
    private final String description;

    protected AbstractIOStream(String description) {
        this.description = description;
    }

    @Override
    public void close() {
        if (hasInputStream()) {
            tryClose(this::getInputStream);
        }

        if (hasOutputStream()) {
            tryClose(this::getOutputStream);
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    private void tryClose(StreamWrapper stream) {
        try {
            stream.get().close();
        } catch (Exception e) {
            LOGGER.error("Exception while closing stream", e);
        }
    }

    @FunctionalInterface
    private interface StreamWrapper {
        AutoCloseable get() throws IOException;
    }
}
