package nl.xillio.xill.api.behavior;

import nl.xillio.xill.api.components.Expression;

/**
 * This class represents the behavior of a number
 */
public class NumberBehavior implements Expression {

    private final double value;

    /**
     * Create a new {@link NumberBehavior}
     *
     * @param value
     */
    public NumberBehavior(final double value) {
	this.value = value;
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public Number getNumberValue() {
	return value;
    }

    @Override
    public String getStringValue() {
	return value == (int) value ? Integer.toString((int) value) : Double.toString(value);
    }

    @Override
    public boolean getBooleanValue() {
	return value != 0;
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
