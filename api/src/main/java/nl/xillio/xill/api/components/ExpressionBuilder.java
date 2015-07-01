package nl.xillio.xill.api.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.NullDebugger;
import nl.xillio.xill.api.behavior.BooleanBehavior;
import nl.xillio.xill.api.behavior.NumberBehavior;
import nl.xillio.xill.api.behavior.StringBehavior;

/**
 * This is a utility class that will build expressions from values
 */
public final class ExpressionBuilder {
	private static final Debugger expressionDebugger = new NullDebugger();
	/**
	 * The true literal
	 */
	public static final MetaExpression TRUE = new AtomicExpression(new BooleanBehavior(true));
	/**
	 * The false literal
	 */
	public static final MetaExpression FALSE = new AtomicExpression(new BooleanBehavior(false));

	/**
	 * The null literal
	 */
	public static final MetaExpression NULL = new AtomicExpression(NullLiteral.Instance);

	/**
	 * Create a new {@link IntegerLiteral}
	 *
	 * @param value
	 * @return expression
	 */
	public static MetaExpression fromValue(final int value) {
		return new AtomicExpression(new NumberBehavior(value));
	}

	/**
	 * Create a new {@link DoubleLiteral}
	 *
	 * @param value
	 * @return expression
	 */
	public static MetaExpression fromValue(final double value) {
		return new AtomicExpression(value);
	}

	/**
	 * Create a new {@link BooleanLiteral}
	 *
	 * @param value
	 * @return expression
	 */
	public static MetaExpression fromValue(final boolean value) {
		return value ? TRUE : FALSE;
	}

	/**
	 * Create a new {@link StringLiteral}
	 *
	 * @param value
	 * @return expression
	 */
	public static MetaExpression fromValue(final String value) {
		return new AtomicExpression(new StringBehavior(value));
	}
	
	/**
	 * Create a new {@link ListExpression}
	 * @param value
	 * @return the expression
	 */
	public static MetaExpression fromValue(final List<MetaExpression> value) {
		return new ListExpression(value).process(expressionDebugger).get();
	}
	
	/**
	 * Create a new {@link ObjectExpression}
	 * @param value
	 * @return the expression
	 */
	public static MetaExpression fromValue(final Map<String, MetaExpression> value) {
		Map<Processable, Processable> procValue = new HashMap<>(value.size());
		
		value.forEach((key, expression) -> {
			procValue.put(fromValue(key), expression);
		});
		
		return new ObjectExpression(procValue).process(expressionDebugger).get();
	}
	
	/**
	 * Create a new {@link ListExpression} with no values
	 * @return the expression
	 */
	public static MetaExpression emptyList() {
	    return fromValue(new ArrayList<>());
	}
	
	/**
	 * Create a new {@link ObjectExpression} with no values
	 * @return the expression
	 */
	public static MetaExpression emptyObject() {
	    return fromValue(new HashMap<>());
	}

	/**
	 * This {@link Expression} represents a null in literal form
	 */
	private static final class NullLiteral implements Expression {

		/**
		 * The single instance of the null literal
		 */
		static final NullLiteral Instance = new NullLiteral();

		private NullLiteral() {}

		@Override
		public Number getNumberValue() {
			return 0;
		}

		@Override
		public String getStringValue() {
			return "";
		}

		@Override
		public boolean getBooleanValue() {
			return false;
		}

		@Override
		public boolean isNull() {
			return true;
		}

		@Override
		public void close() throws Exception {}

	}
}
