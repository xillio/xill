package nl.xillio.xill.api.behavior;

import nl.xillio.xill.api.components.Expression;

/**
 * <p>
 * This class represents the behavior of a boolean.
 * </p>
 * Values:
 * <ul>
 * <li><b>{@link String}: </b> true or false</li>
 * <li><b>{@link Boolean}: </b> original value</li>
 * <li><b>{@link Number}: </b> if value equals true then 1 else 0</li>
 * </ul>
 */
public class BooleanBehavior implements Expression {

	private final boolean value;

	/**
	 * Create a new {@link BooleanBehavior}
	 *
	 * @param value
	 *        the value to set the boolean to
	 */
	public BooleanBehavior(final boolean value) {
		this.value = value;
	}

	@Override
	public Number getNumberValue() {
		return value ? 1 : 0;
	}

	@Override
	public String getStringValue() {
		return Boolean.toString(value);
	}

	@Override
	public boolean getBooleanValue() {
		return value;
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
