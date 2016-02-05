package nl.xillio.xill.api.io;

import me.biesaart.utils.Log;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class is a simple implementation of {@link IOStream} based on two providers.
 *
 * @author Thomas biesaart
 */
public class SimpleIOStream implements IOStream {
    private static final Logger LOGGER = Log.get();
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public SimpleIOStream(InputStream stream) {
        this(stream, null);
    }

    public SimpleIOStream(OutputStream stream) {
        this(null, stream);
    }

    public SimpleIOStream(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public boolean hasInputStream() {
        return inputStream != null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!hasInputStream()) {
            throw new NoStreamAvailableException("No stream is available");
        }
        return inputStream;
    }

    @Override
    public boolean hasOutputStream() {
        return outputStream != null;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (!hasOutputStream()) {
            throw new NoStreamAvailableException("No stream is available");
        }
        return outputStream;
    }

    @Override
    public void close() {
        tryClose(inputStream);
        tryClose(outputStream);
    }

    private void tryClose(AutoCloseable stream) {
        if (stream == null) {
            return;
        }

        try {
            stream.close();
        } catch (Exception e) {
            LOGGER.error("Exception while closing stream", e);
        }
    }
}
