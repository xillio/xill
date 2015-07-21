package nl.xillio.migrationtool.gui;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.NotImplementedException;

/**
 * This class represents a viewable variable in the {@link VariablePane}
 */
public class ObservableVariable {
	private final String name;
	private final MetaExpression value;
	private final Object source;

	/**
	 * Create a new {@link ObservableVariable}
	 * 
	 * @param name
	 * @param value
	 * @param source
	 */
	public ObservableVariable(final String name, final MetaExpression value, final Object source) {
		this.name = name;
		this.value = value;
		this.source = source;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		if (value == null) {
			return ExpressionBuilderHelper.NULL.toString();
		}

		switch (value.getType()) {
			case ATOMIC:
				return value.toString();
			case LIST:
				return "List [" + value.getNumberValue() + "]";
			case OBJECT:
				return "Object [" + value.getNumberValue() + "]";
			default:
				throw new NotImplementedException("This type has not been implemented in the VariablePane");
		}
	}

	/**
	 * @return the source
	 */
	public Object getSource() {
		return source;
	}
}
