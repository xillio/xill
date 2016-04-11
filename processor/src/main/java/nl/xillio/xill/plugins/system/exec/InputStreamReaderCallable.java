package nl.xillio.xill.plugins.system.exec;

import me.biesaart.utils.Log;
import org.slf4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

/**
 * This class will read all characters and returns the result in a string
 */
public class InputStreamReaderCallable implements Callable<String> {
    private final BufferedInputStream input;
    private static final Logger LOGGER = Log.get();

    /**
     * Create a new {@link InputStreamReaderCallable}
     *
     * @param input the input
     */
    public InputStreamReaderCallable(final InputStream input) {
        this.input = new BufferedInputStream(input);
    }

    @Override
    public String call() {
        StringBuilder output = new StringBuilder();

        int chars;
        try {
            while ((chars = input.read()) != -1) {
                output.append((char) chars);
            }
        } catch (IOException e) {
            LOGGER.error("Error while listening to input stream: " + e.getMessage(), e);
        }

        return output.toString();
    }
}
