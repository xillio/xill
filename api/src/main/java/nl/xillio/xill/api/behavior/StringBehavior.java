package nl.xillio.xill.api.behavior;

import nl.xillio.util.MathUtils;
import nl.xillio.xill.api.components.Expression;

import java.util.regex.Pattern;

/**
 * <p>
 * This class represents the behavior of a string.
 * </p>
 * <p>
 * Values:
 * <ul>
 * <li><b>{@link String}: </b> actual value</li>
 * <li><b>{@link Boolean}: </b> if the value is null or empty then false otherwise true</li>
 * <li><b>{@link Number}: </b> if the value is a number then that number as a {@link Double}, otherwise {@link Double#NaN}</li>
 * </ul>
 */
public class StringBehavior implements Expression {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d*(\\.\\d+)?(-?e\\d+)?");
    private Number cachedNumber;
    private final String value;

    /**
     * Create a new {@link StringBehavior}.
     *
     * @param value the value to set
     */
    public StringBehavior(final String value) {
        this.value = value;
    }

    @Override
    public Number getNumberValue() {
        if (cachedNumber == null) {
            if (NUMBER_PATTERN.matcher(value).matches()) {
                cachedNumber = MathUtils.parse(value);
            } else {
                cachedNumber = Double.NaN;
            }
        }

        return cachedNumber;
    }

    @Override
    public String getStringValue() {
        return value;
    }

    @Override
    public boolean getBooleanValue() {
        return value != null && !value.isEmpty();
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
