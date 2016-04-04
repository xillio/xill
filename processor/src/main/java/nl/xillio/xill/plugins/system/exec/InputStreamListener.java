package nl.xillio.xill.plugins.system.exec;

import me.biesaart.utils.Log;
import nl.xillio.events.Event;
import nl.xillio.events.EventHost;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

/**
 * This class will listen for lines and call an event when data is found
 */
public class InputStreamListener implements Callable<String> {
    private final InputStreamReader input;

    private static final Logger LOGGER = Log.get();

    /**
     * Create a new {@link InputStreamListener}
     *
     * @param input the input
     */
    public
    InputStreamListener(final InputStream input) {
        this.input = new InputStreamReader(input);
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
