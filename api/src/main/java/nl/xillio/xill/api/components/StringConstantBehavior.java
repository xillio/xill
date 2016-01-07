package nl.xillio.xill.api.components;

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
class StringConstantBehavior extends StringBehavior {

    /**
     * Create a new {@link StringConstantBehavior}
     *
     * @param value the value to set
     */
    public StringConstantBehavior(final String value) {
        super(value);
    }

    @Override
    public Number getNumberValue() {
        return Double.NaN;
    }
}
