package nl.xillio.exiftool.process;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

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

    Stream<String> run(String... arguments) throws IOException;
}
