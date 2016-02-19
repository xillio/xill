package nl.xillio.xill.api.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class is a simple implementation of {@link IOStream} based on two providers.
 *
 * @author Thomas biesaart
 */
public class SimpleIOStream extends AbstractIOStream {
    private final BufferedInputStream inputStream;
    private final OutputStream outputStream;

    public SimpleIOStream(InputStream stream, String description) {
        this(stream, null, description);
    }

    public SimpleIOStream(OutputStream stream, String description) {
        this(null, stream, description);
    }

    public SimpleIOStream(InputStream inputStream, OutputStream outputStream, String description) {
        super(description);
        this.inputStream = toBufferedStream(inputStream);
        this.outputStream = outputStream;
    }

    private BufferedInputStream toBufferedStream(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }

        if (inputStream instanceof BufferedInputStream) {
            return (BufferedInputStream) inputStream;
        }
        return new BufferedInputStream(inputStream);
    }

    @Override
    public boolean hasInputStream() {
        return inputStream != null;
    }

    @Override
    public BufferedInputStream getInputStream() throws IOException {
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
}
