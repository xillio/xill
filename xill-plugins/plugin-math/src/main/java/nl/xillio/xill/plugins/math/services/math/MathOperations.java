package nl.xillio.xill.plugins.math.services.math;

import nl.xillio.xill.plugins.math.MathXillPlugin;

/**
 * This interface represents some of the operations for the {@link MathXillPlugin}
 *
 */
public interface MathOperations {

	/**
	 * Get the absolute value
	 * 
	 * @param value
	 *        the value
	 * @return the absolute value
	 */
	public double abs(Number value);

	/**
	 * Round a number to it's nearest integer
	 * 
	 * @param value
	 *        the value
	 * @return the rounded integer or long
	 */
	public long round(Number value);

	/**
	 * Returns a random double between 0 and 1.
	 * 
	 * @return
	 *         A random double between 0 and 1.
	 */
	public double random();

	/**
	 * Returns a random long between 0 and a given value.
	 * 
	 * @param value
	 *        The max value.
	 * @return
	 *         A long between 0 and max value.
	 */
	public long random(long value);
}
