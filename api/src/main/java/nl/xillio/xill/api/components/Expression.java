package nl.xillio.xill.api.components;

/**
 * This interface represents a language component that can hold a value
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
	 * @return True if the expression is considered null
	 */
	public boolean isNull();
}
