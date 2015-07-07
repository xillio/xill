package nl.xillio.xill.api.behavior;

import nl.xillio.xill.api.components.Expression;

/**
 * This class represents the behavior of a string.<br/>
 * Values:
 * <ul>
 * 	<li><b>{@link String}: </b> actual value</li>
 * 	<li><b>{@link Boolean}: </b> if the value is null or empty then false otherwise true</li>
 * 	<li><b>{@link Number}: </b> if the value is a number then that number as a {@link Double}, otherwise {@link Double#NaN}</li>
 * </ul>
 */
public class StringBehavior implements Expression {

    private final String value;

    /**
     * Create a new {@link StringBehavior}
     *
     * @param value
     */
    public StringBehavior(final String value) {
	this.value = value;
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public Number getNumberValue() {
	try {
	    return Double.parseDouble(value);
	} catch (NumberFormatException e) {
	    return Double.NaN;
	}
    }

    @Override
    public String getStringValue() {
	return value;
    }

    @Override
    public boolean getBooleanValue() {
	return value != null || !value.isEmpty();
    }

    @Override
    public boolean isNull() {
	return value == null;
    }

    @Override
    public String toString() {
	return getStringValue();
    }
}
