package nl.xillio.xill.api.components;

/**
 * This enum represents the type of variable
 */
public enum ExpressionDataType {
    /**
     * Single value. (Number, String, Boolean)
     */
    ATOMIC,
    /**
     * A list of values
     */
    LIST,
    /**
     * An object with named fields
     */
    OBJECT;

    /**
     * @return the int identifier of this type
     */
    public int toInt() {
        return this.ordinal();
    }

    private static ExpressionDataType[] values = values();

    /**
     * Returns the {@link ExpressionDataType} corresponding to the provided ordinal
     *
     * @param ordinal the ordinal
     * @return the {@link ExpressionDataType}
     */
    public static ExpressionDataType get(int ordinal) {
        return values[ordinal];
    }


}
