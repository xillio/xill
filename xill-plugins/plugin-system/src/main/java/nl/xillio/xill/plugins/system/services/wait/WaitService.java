package nl.xillio.xill.plugins.system.services.wait;

import nl.xillio.xill.services.XillService;

/**
 * This service waits when called
 */
public class WaitService implements XillService {
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
			e.printStackTrace();
		}
	}
}
