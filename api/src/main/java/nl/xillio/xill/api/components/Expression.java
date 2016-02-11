package nl.xillio.xill.api.components;

import nl.xillio.xill.api.io.IOStream;

/**
 * This interface represents a language component that can hold a value. Every expression should have all three types: {@link Boolean}, {@link String} and {@link Number}
 *
 * @see BooleanBehavior
 * @see NumberBehavior
 * @see StringBehavior
 */
public interface Expression extends AutoCloseable {

    /**
     * @return The number representation of the expression
     */
    Number getNumberValue();

    /**
     * @return The string representation of the expression
     */
    String getStringValue();

    /**
     * @return The boolean representation of the expression
     */
    boolean getBooleanValue();

    /**
     * @return true if and only if the expression is considered null
     */
    boolean isNull();

    /**
     * @return the IOStream representation of the expression
     */
    IOStream getBinaryValue();

    default void close() {
    }
}
