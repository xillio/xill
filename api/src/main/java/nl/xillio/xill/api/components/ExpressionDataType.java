package nl.xillio.xill.api.components;

/**
 * This enum represents the type of variables.
 */
public enum ExpressionDataType {
    /**
     * Atomic value (Number, String, Boolean).
     */
    ATOMIC,
    /**
     * A list of values.
     */
    LIST,
    /**
     * An object with named fields.
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
     * Returns the expression datatype corresponding to the provided ordinal.
     *
     * @param ordinal the ordinal
     * @return one of {@link #ATOMIC}, {@link #LIST}, {@link #OBJECT}
     */
    public static ExpressionDataType get(int ordinal) {
        return values[ordinal];
    }


}
