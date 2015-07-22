package nl.xillio.xill.api.construct;

import java.io.InputStream;

import org.apache.commons.lang3.text.WordUtils;

import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This interface contains the core functionality for all constructs
 */
public abstract class Construct extends ExpressionBuilderHelper implements HelpComponent {
	private final String defaultName;

	/**
	 * Instantiate a new {@link Construct} and pick a default name
	 */
	public Construct() {
		// Set the default name
		String name = getClass().getSimpleName();
		String superName = Construct.class.getSimpleName();
		if (name.endsWith(superName)) {
			name = name.substring(0, name.length() - superName.length());
		}
		defaultName = WordUtils.uncapitalize(name);
	}

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
	 * <p>
	 * Returns the name of the construct. This name is also the command by which
	 * this construct can be called inside scripts.
	 * </p>
	 *
	 * <p>
	 * By default the name of a {@link PluginPackage} is the concrete implementation name
	 * acquired using {@link Class#getSimpleName()} without the {@link PluginPackage} suffix.
	 * It is also uncapitalized using {@link WordUtils#uncapitalize(String)}
	 * </p>
	 *
	 * @return the name of the construct. This name is also the command by which
	 *         this construct can be called inside scripts
	 */
	public String getName() {
		return defaultName;
	}

	/**
	 * Build a new process and return it ready for input
	 *
	 * @param context
	 *        The context for which to prepare the {@link Construct}
	 * @return A prepared processor loaded with the {@link Construct} behaviour.
	 */
	public abstract ConstructProcessor prepareProcess(final ConstructContext context);

	/**
	 * Check if a {@link MetaExpression} is a {@link ExpressionDataType}. If not
	 * then a {@link RobotRuntimeException} exception will be thrown informing
	 * the developer about the incorrect type.
	 *
	 * @param expression
	 *        The expression to evaluate
	 * @param argumentName
	 *        The name of the passed argument. This value is used to provide
	 *        the developer with a clear error message.
	 * @param expectedType
	 *        The expected type for the expression
	 * @throws RobotRuntimeException
	 *         when the assertion fails
	 */
	protected static void assertType(final MetaExpression expression, final String argumentName, final ExpressionDataType expectedType) {
		if (expression.getType() != expectedType) {
			throw new RobotRuntimeException("Expected type " + expectedType.toString() + " for " + argumentName);
		}
	}

	/**
	 * Check if a {@link MetaExpression} is <b>NOT</b> a
	 * {@link ExpressionDataType}. If so then a {@link RobotRuntimeException}
	 * exception will be thrown informing the developer about the incorrect
	 * type.
	 *
	 * @param expression
	 *        The expression to evaluate
	 * @param argumentName
	 *        The name of the passed argument. This value is used to provide
	 *        the developer with a clear error message.
	 * @param expectedType
	 *        The illegal type for the expression
	 * @throws RobotRuntimeException
	 *         when the assertion fails
	 */
	protected static void assertNotType(final MetaExpression expression, final String argumentName, final ExpressionDataType expectedType) {
		if (expression.getType() == expectedType) {
			throw new RobotRuntimeException("Expected type " + expectedType.toString() + " for " + argumentName);
		}
	}

	/**
	 * Check if a {@link MetaExpression} is <b>NOT</b> null. This is checked by
	 * calling {@link MetaExpression#isNull()}.
	 *
	 * @param expression
	 *        The expression to check
	 * @param argumentName
	 *        The name of the expression. This is used to generate understandable debug messages.
	 * @throws RobotRuntimeException
	 *         when the assertion fails
	 */
	protected static void assertNotNull(final MetaExpression expression, final String argumentName) {
		if (expression.isNull()) {
			throw new RobotRuntimeException(argumentName + " cannot be null");
		}
	}

	/**
	 * Check if a {@link MetaExpression} is null. This is checked by calling
	 * {@link MetaExpression#isNull()}.
	 *
	 * @param expression
	 *        The expression to check
	 * @param argumentName
	 *        The name of the expression. This is used to generate understandable debug messages.
	 * @throws RobotRuntimeException
	 *         when the assertion fails
	 */
	protected static void assertIsNull(final MetaExpression expression, final String argumentName) {
		if (expression.isNull()) {
			throw new RobotRuntimeException(argumentName + " cannot be null");
		}
	}

	/**
	 * Check if the {@link MetaExpression} contains an instance of meta
	 * information and fetch it
	 *
	 * @param <T>
	 *        The type of meta information to assert
	 *
	 * @param expression
	 *        The expression to check
	 * @param expressionName
	 *        The name of the expression. This would generally be a
	 *        parameter name in the construct. It is used to give the
	 *        developer an understandable message.
	 * @param type
	 *        The {@link Class} of the meta object to fetch
	 * @param friendlyTypeName
	 *        The friendly name of the type. This will be used to generate
	 *        an understandable message
	 * @return the requested meta object
	 * @throws RobotRuntimeException
	 *         when the assertion fails
	 *
	 */
	protected static <T> T assertMeta(final MetaExpression expression, final String expressionName, final Class<T> type, final String friendlyTypeName) {
		T value = expression.getMeta(type);
		if (value == null) {
			throw new RobotRuntimeException("Expected " + expressionName + " to be a " + friendlyTypeName);
		}

		return value;
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

	@Override
	public InputStream openDocumentationStream() {
		return getClass().getResourceAsStream("/" + getClass().getName().replace('.', '/') + ".xml");
	}
	
	/**
	 * Hide this construct from documentation
	 * @return true if it should be hidden
	 */
	public boolean hideDocumentation() {
		return false;
	}
}
