package nl.xillio.xill.api.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This interface represents a combination of an {@link InputStream} provider and a {@link OutputStream} provider.
 * Note that both the input and output streams are optional
 *
 * @author Thomas biesaart
 */
public interface IOStream extends AutoCloseable {

    /**
     * Check if this IOStream has an input stream.
     *
     * @return true if and only is this IOStream is capable of creating an input stream
     */
    boolean hasInputStream();

    /**
     * Get the input stream.
     *
     * @return the stream
     * @throws IOException                if the stream could not be created
     * @throws NoStreamAvailableException if there was no stream
     */
    InputStream getInputStream() throws IOException;

    /**
     * Check if this IOStream has an output stream.
     *
     * @return true if and only is this IOStream has an output stream
     */
    boolean hasOutputStream();

    /**
     * Get the output stream.
     *
     * @return the stream
     * @throws IOException                if the stream could not be created
     * @throws NoStreamAvailableException if there was no stream
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Close both streams if they are present.
     */
    void close();

    /**
     * Get the description of this IOStream if there is one.
     *
     * @return the description or null
     */
    String getDescription();

}
