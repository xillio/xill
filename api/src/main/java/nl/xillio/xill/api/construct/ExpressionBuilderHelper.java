package nl.xillio.xill.api.construct;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import nl.xillio.xill.api.behavior.BooleanBehavior;
import nl.xillio.xill.api.behavior.NumberBehavior;
import nl.xillio.xill.api.behavior.StringBehavior;
import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.Expression;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.ImmutableLiteral;
import nl.xillio.xill.api.components.ListExpression;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.ObjectExpression;

/**
 * This class contains various useful utility functions to create Expressions
 */
public class ExpressionBuilderHelper {

	/**
	 * @see ExpressionDataType#LIST
	 */
	protected static final ExpressionDataType LIST = ExpressionDataType.LIST;
	/**
	 * @see ExpressionDataType#ATOMIC
	 */
	protected static final ExpressionDataType ATOMIC = ExpressionDataType.ATOMIC;
	/**
	 * @see ExpressionDataType#OBJECT
	 */
	protected static final ExpressionDataType OBJECT = ExpressionDataType.OBJECT;

	/**
	 * The true literal
	 */
	public static final MetaExpression TRUE = new ImmutableLiteral(new BooleanBehavior(true));
	/**
	 * The false literal
	 */
	public static final MetaExpression FALSE = new ImmutableLiteral(new BooleanBehavior(false));

	/**
	 * The null literal
	 */
	public static final MetaExpression NULL = new ImmutableLiteral(NullLiteral.Instance);

	/**
	 * Create a new expression containing an int
	 *
	 * @param value
	 *        the value of the expression
	 * @return expression
	 */
	public static MetaExpression fromValue(final int value) {
		return new AtomicExpression(new NumberBehavior(value));
	}

	/**
	 * Create a new expression containing a double
	 *
	 * @param value
	 *        the value of the expression
	 * @return expression
	 */
	public static MetaExpression fromValue(final double value) {
		return new AtomicExpression(value);
	}

	/**
	 * Create a new expression containing a boolean
	 *
	 * @param value
	 *        the value of the expression
	 * @return expression
	 */
	public static MetaExpression fromValue(final boolean value) {
		return value ? TRUE : FALSE;
	}

	/**
	 * Create a new expression containing a string
	 *
	 * @param value
	 *        the value of the expression
	 * @return expression
	 */
	public static MetaExpression fromValue(final String value) {
		return new AtomicExpression(new StringBehavior(value));
	}

	/**
	 * Create a new {@link ExpressionDataType#LIST} containing a value. For empty lists you can use {@link ExpressionBuilderHelper#emptyList()}
	 *
	 * @param value
	 *        the value of the expression
	 * @return the expression
	 */
	public static MetaExpression fromValue(final List<MetaExpression> value) {
		return new ListExpression(value);
	}

	/**
	 * Create a new {@link ExpressionDataType#OBJECT} containing a value. For empty lists you can use {@link ExpressionBuilderHelper#emptyObject()}
	 *
	 * @param value
	 *        the value of the expression
	 * @return the expression
	 */
	public static MetaExpression fromValue(final LinkedHashMap<String, MetaExpression> value) {
		return new ObjectExpression(value);
	}

	/**
	 * Create a new {@link ListExpression} with no values
	 *
	 * @return the expression
	 */
	public static MetaExpression emptyList() {
		return fromValue(new ArrayList<>());
	}

	/**
	 * Create a new {@link ObjectExpression} with no values
	 *
	 * @return the expression
	 */
	public static MetaExpression emptyObject() {
		return fromValue(new LinkedHashMap<>());
	}

	/**
	 * A shortcut to {@link MetaExpression#extractValue(MetaExpression)}
	 *
	 * @param expression
	 *        The expression to extract Java objects from
	 * @return The value specified in
	 *         {@link MetaExpression#extractValue(MetaExpression)}
	 * @see MetaExpression#extractValue(MetaExpression)
	 */
	protected static Object extractValue(final MetaExpression expression) {
		return MetaExpression.extractValue(expression);
	}

	/**
	 * A shortcut to {@link MetaExpression#parseObject(Object)}
	 *
	 * @param value
	 *        the object to parse into a {@link MetaExpression}
	 * @return the {@link MetaExpression} not null
	 * @throws IllegalArgumentException
	 *         if parsing the value failed
	 * @see MetaExpression#parseObject(Object)
	 */
	protected static MetaExpression parseObject(final Object value) throws IllegalArgumentException {
		return MetaExpression.parseObject(value);
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
