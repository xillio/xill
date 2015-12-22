package nl.xillio.exiftool.process;

import java.io.IOException;

/**
 * This interface represents an instance of an exiftool process.
 *
 * @author Thomas Biesaart
 */
public interface ExifToolProcess extends Runnable, AutoCloseable {
    boolean isDeployed();

    void deploy() throws IOException;

    boolean isRunning();
    boolean isClosed();
    boolean isAvailable();

    void close();
}
