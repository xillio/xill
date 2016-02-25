package nl.xillio.xill.api.components;

import nl.xillio.xill.api.io.IOStream;

/**
 * This interface represents a language component that can hold a value.
 * Every expression should have all three types: {@link Boolean}, {@link String} and {@link Number}.
 *
 * @see BooleanBehavior
 * @see NumberBehavior
 * @see StringBehavior
 */
public interface Expression extends AutoCloseable {

    /**
     * @return the number representation of the expression
     */
    Number getNumberValue();

    /**
     * @return the string representation of the expression
     */
    String getStringValue();

    /**
     * @return the boolean representation of the expression
     */
    boolean getBooleanValue();

    /**
     * @return {@code true} if and only if the expression is considered null
     */
    boolean isNull();

    /**
     * @return the IOStream representation of the expression
     */
    IOStream getBinaryValue();

    default void close() {
    }
}
