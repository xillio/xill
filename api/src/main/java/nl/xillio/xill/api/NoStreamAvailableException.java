package nl.xillio.xill.api;

import java.io.IOException;

/**
 * This exception is generally thrown when trying to get a stream from an {@link nl.xillio.xill.api.components.IOStream}
 * when none is available.
 *
 * @author Thomas biesaart
 */
public class NoStreamAvailableException extends IOException {
    public NoStreamAvailableException(String message) {
        super(message);
    }
}
