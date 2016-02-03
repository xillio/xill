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
public interface IOStream {

    /**
     * Check if this IOStream has an input stream.
     *
     * @return true if and only is this IOStream is capable of creating an input stream
     */
    boolean hasInputStream();

    /**
     * Create an input stream.
     * Note: Make sure you close this stream manually.
     *
     * @return the stream
     * @throws IOException if the stream could not be created
     */
    InputStream openInputStream() throws IOException;

    /**
     * Check if this IOStream has an output stream.
     *
     * @return true if and only is this IOStream has an output stream
     */
    boolean hasOutputStream();

    /**
     * Create an output stream.
     * Note: Make sure you close this stream manually
     *
     * @return the stream
     * @throws IOException if the stream could not be created
     */
    OutputStream openOutputStream() throws IOException;

}
