package nl.xillio.xill.api.components;

/**
 * <p>
 * This class represents the behavior of a string constant.
 * </p>
 * <p>
 * Values:
 * <ul>
 * <li><b>{@link String}: </b> actual value</li>
 * <li><b>{@link Boolean}: </b> if the value is null or empty then false otherwise true</li>
 * <li><b>{@link Number}: </b> {@link Double#NaN}</li>
 * </ul>
 */
class StringConstantBehavior extends StringBehavior {

    /**
     * Default constructor.
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
