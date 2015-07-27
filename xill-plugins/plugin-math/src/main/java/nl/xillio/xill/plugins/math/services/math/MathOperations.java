package nl.xillio.xill.plugins.math.services.math;

import nl.xillio.xill.plugins.math.MathXillPlugin;

/**
 * This interface represents some of the operations for the {@link MathXillPlugin}
 *
 */
public interface MathOperations {
	
	/**
	 * Get the absolute value
	 * @param value the value
	 * @return the absolute value
	 */
	public double abs(Number value);
	
	/**
	 * Round a number to it's nearest integer
	 * @param value the value
	 * @return the rounded integer or long
	 */
	public long round(Number value); 

}
