package nl.xillio.xill.api.components;

import java.util.ArrayList;
import java.util.Collection;

import nl.xillio.xill.api.behavior.BooleanBehavior;
import nl.xillio.xill.api.behavior.NumberBehavior;
import nl.xillio.xill.api.behavior.StringBehavior;

/**
 * This {@link MetaExpression} represents an expression that holds a single value
 */
public class AtomicExpression extends MetaExpression {

	private final Expression expressionValue;

	/**
	 * Create a new {@link AtomicExpression} that hosts an {@link Expression}
	 *
	 * @param value
	 */
	public AtomicExpression(final Expression value) {
		setValue(value);

		// Save to prevent casting
		expressionValue = value;
	}

	/**
	 * Create a new {@link AtomicExpression} with {@link BooleanBehavior}
	 *
	 * @param value
	 */
	public AtomicExpression(final boolean value) {
		this(new BooleanBehavior(value));
	}

	/**
	 * Create a new {@link AtomicExpression} with {@link NumberBehavior}
	 *
	 * @param value
	 */
	public AtomicExpression(final double value) {
		this(new NumberBehavior(value));
	}

	/**
	 * Create a new {@link AtomicExpression} with {@link StringBehavior}
	 *
	 * @param value
	 */
	public AtomicExpression(final String value) {
		this(new StringBehavior(value));
	}

	@Override
	public Number getNumberValue() {
		return expressionValue.getNumberValue();
	}

	@Override
	public String getStringValue() {
		return expressionValue.getStringValue();
	}

	@Override
	public boolean getBooleanValue() {
		return expressionValue.getBooleanValue();
	}

	@Override
	public boolean isNull() {
		return expressionValue.isNull();
	}

	@Override
	public void close() throws Exception {
		super.close();
		expressionValue.close();
	}

	@Override
	public Collection<Processable> getChildren() {
		return new ArrayList<>();
	}

}
