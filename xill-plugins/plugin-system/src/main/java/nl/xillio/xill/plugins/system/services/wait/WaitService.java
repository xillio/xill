package nl.xillio.xill.plugins.system.services.wait;

import nl.xillio.xill.services.XillService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This service waits when called
 */
public class WaitService implements XillService {

	private static final Logger LOGGER = LogManager.getLogger(WaitService.class);

	/**
	 * Wait for a time
	 * 
	 * @param delay
	 *        in milliseconds
	 */
	public void wait(final int delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}
