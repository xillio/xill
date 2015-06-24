package nl.xillio.xill.api.behavior;

import nl.xillio.xill.api.components.Expression;

/**
 * This class represents the behavior of a boolean
 */
public class BooleanBehavior implements Expression {

	private final boolean value;

	/**
	 * Create a new {@link BooleanBehavior}
	 *
	 * @param value
	 */
	public BooleanBehavior(final boolean value) {
		this.value = value;
	}

	@Override
	public void close() throws Exception {}

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

}
