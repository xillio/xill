package nl.xillio.exiftool.process;

import java.io.IOException;
import java.util.List;

/**
 * This interface represents an instance of an exiftool process.
 *
 * @author Thomas Biesaart
 */
public interface ExifToolProcess extends AutoCloseable {
    boolean isDeployed();

    void deploy() throws IOException;

    boolean isRunning();
    boolean isClosed();
    boolean isAvailable();

    void start() throws IOException;

    void close();

    List<String> run(String... arguments) throws IOException;
}
