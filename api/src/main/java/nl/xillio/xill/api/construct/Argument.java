package nl.xillio.xill.api.construct;

import nl.xillio.xill.api.components.MetaExpression;

/**
 * Handles the definition of arguments/parameters that are accepted by a certain construct or custom routine.
 */
public class Argument {

	private final String name;
	private MetaExpression defaultValue;
	private MetaExpression value;

	/**
	 * Creates a complex argument, which accepts various variable types.
	 *
	 * @param name
	 *        the name of the argument
	 */
	public Argument(final String name) {
		this.name = name;
	}

	/**
	 * Creates a simple argument with a default value.
	 *
	 * @param name
	 *        the name of the argument
	 * @param defaultvalue
	 *        the default value
	 */
	public Argument(final String name, final MetaExpression defaultvalue) {
		this(name);
		defaultValue = defaultvalue;
	}

	/**
	 * Returns the name of the argument.
	 *
	 * @return the name of the argument
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the default value for this argument.
	 *
	 * @return the default value for this argument
	 */
	public MetaExpression getValue() {
		return value == null ? defaultValue : value;
	}

	/**
	 * Sets the value of this argument.
	 *
	 * @param value
	 *        the value to set
	 */
	void setValue(final MetaExpression value) {
		this.value = value;
	}

	/**
	 * Checks if this argument has been set.
	 *
	 * @return whether the argument holds a value
	 */
	public boolean isSet() {
		return defaultValue != null || value != null;
	}

	/**
	 * Resets the argument to initial state.
	 */
	public void clear() {
		value = null;
	}

	@Override
	public String toString() {
		String name = getName();
		if (defaultValue != null) {
			name += " = " + defaultValue;
		}
		return name;
	}

}
