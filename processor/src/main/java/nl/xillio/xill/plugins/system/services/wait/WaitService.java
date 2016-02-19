package nl.xillio.xill.plugins.system.services.wait;

import me.biesaart.utils.Log;
import nl.xillio.xill.services.XillService;
import org.slf4j.Logger;

/**
 * This service waits when called
 */
public class WaitService implements XillService {

    private static final Logger LOGGER = Log.get();

    /**
     * Wait for a time
     *
     * @param delay in milliseconds
     */
    public void wait(final int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            LOGGER.error("Wait interrupted: " + e.getMessage(), e);
        }
    }
}
