package nl.xillio.xill.api.components;

import nl.xillio.xill.api.behavior.BooleanBehavior;
import nl.xillio.xill.api.behavior.NumberBehavior;
import nl.xillio.xill.api.behavior.StringBehavior;

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
	public Number getNumberValue();

	/**
	 * @return The string representation of the expression
	 */
	public String getStringValue();

	/**
	 * @return The boolean representation of the expression
	 */
	public boolean getBooleanValue();

	/**
	 * @return true if and only if the expression is considered null
	 */
	public boolean isNull();
}
