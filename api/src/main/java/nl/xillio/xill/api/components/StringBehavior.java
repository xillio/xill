package nl.xillio.xill.api.components;

import nl.xillio.util.MathUtils;

import java.util.Objects;
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
class StringBehavior extends AbstractBehavior {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("((-?\\d*\\.\\d+(E[-\\+]\\d+)?)|(-?\\d+))");
    private Number cachedNumber;
    private final String value;

    /**
     * Create a new {@link StringBehavior}.
     *
     * @param value the value to set
     */
    public StringBehavior(String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    @Override
    public Number getNumberValue() {
        if (cachedNumber == null) {
            if (value == null || value.isEmpty() || !NUMBER_PATTERN.matcher(value).matches()) {
                cachedNumber = Double.NaN;
            } else {
                cachedNumber = MathUtils.parse(value);
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
        return !value.isEmpty();
    }
}
