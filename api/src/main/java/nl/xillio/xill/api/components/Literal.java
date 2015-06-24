package nl.xillio.xill.api.components;

import nl.xillio.xill.api.behavior.BooleanBehavior;
import nl.xillio.xill.api.behavior.NumberBehavior;
import nl.xillio.xill.api.behavior.StringBehavior;

/**
 * This is the base class for all literals
 */
public final class Literal {
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
	 * @return The literal
	 */
	public static MetaExpression fromValue(final int value) {
		return new AtomicExpression(new NumberBehavior(value));
	}

	/**
	 * Create a new {@link DoubleLiteral}
	 *
	 * @param value
	 * @return The literal
	 */
	public static MetaExpression fromValue(final double value) {
		return new AtomicExpression(new NumberBehavior(value));
	}

	/**
	 * Create a new {@link BooleanLiteral}
	 *
	 * @param value
	 * @return The literal
	 */
	public static MetaExpression fromValue(final boolean value) {
		return value ? TRUE : FALSE;
	}

	/**
	 * Create a new {@link StringLiteral}
	 *
	 * @param value
	 * @return The literal
	 */
	public static MetaExpression fromValue(final String value) {
		return new AtomicExpression(new StringBehavior(value));
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
