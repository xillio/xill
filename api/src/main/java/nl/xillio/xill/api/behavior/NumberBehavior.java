package nl.xillio.xill.api.behavior;

import nl.xillio.xill.api.components.Expression;

/**
 * <p>
 * This class represents the behavior of a number.
 * </p>
 * Values:
 * <ul>
 * <li><b>{@link String}: </b> the actual value as a string</li>
 * <li><b>{@link Boolean}: </b> if value == 0 then false else true</li>
 * <li><b>{@link Number}: </b> the acual value</li>
 * </ul>
 */
public class NumberBehavior implements Expression {

	private final Number value;

	/**
	 * Create a new {@link NumberBehavior}
	 *
	 * @param value
	 *        the value to set
	 */
	public NumberBehavior(final Number value) {
		this.value = value;
	}

	@Override
	public void close() throws Exception {}

	@Override
	public Number getNumberValue() {
		return value;
	}

	@Override
	public String getStringValue() {
		double doubleValue = value.doubleValue();
		long longValue = value.longValue();

		// Range within which the long and double values have to be in order to assume there are no decimal places
		double epsilon = Math.ulp(doubleValue);

		return longValue >= doubleValue - epsilon && longValue <= doubleValue + epsilon
				? Long.toString(longValue) : Double.toString(doubleValue);
	}

	@Override
	public boolean getBooleanValue() {
		return value.doubleValue() != 0;
	}

	@Override
	public boolean isNull() {
		return false;
	}

	@Override
	public String toString() {
		return getStringValue();
	}
}
