package nl.xillio.xill.api.io;

import java.io.IOException;

/**
 * This exception is generally thrown when trying to get a stream from an {@link IOStream}
 * when none is available.
 *
 * @author Thomas biesaart
 */
public class NoStreamAvailableException extends IOException {
    public NoStreamAvailableException(String message) {
        super(message);
    }
}
