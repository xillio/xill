package nl.xillio.xill.api.behavior;

import nl.xillio.xill.api.components.Expression;

/**
 * This class represents the behavior of a number.<br/>
 * Values:
 * <ul>
 * 	<li><b>{@link String}: </b> the actual value as a string</li>
 * 	<li><b>{@link Boolean}: </b> if value == 0 then false else true</li>
 * 	<li><b>{@link Number}: </b> the acual value</li>
 * </ul>
 */
public class NumberBehavior implements Expression {

    private final double value;

    /**
     * Create a new {@link NumberBehavior}
     *
     * @param value the value to set
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
