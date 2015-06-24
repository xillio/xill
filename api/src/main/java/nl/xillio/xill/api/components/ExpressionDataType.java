package nl.xillio.xill.api.components;

/**
 * This enum represents the type of variable
 */
public enum ExpressionDataType {
	/**
	 * Single value. (Int, String, Boolean)
	 */
	ATOMIC,
	/**
	 * A list of values
	 */
	LIST,
	/**
	 * An object with named fields
	 */
	OBJECT
}
