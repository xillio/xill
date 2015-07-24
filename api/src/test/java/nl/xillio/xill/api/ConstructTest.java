package nl.xillio.xill.api;

import static org.mockito.Mockito.*;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;

/**
 * This class contains some utility for testing constructs
 */
public class ConstructTest extends ExpressionBuilderHelper {
	/**
	 * Mock a {@link MetaExpression} of a specific type
	 *
	 * @param type
	 *        the type of the {@link MetaExpression}
	 * @return the {@link MetaExpression}
	 */
	protected static MetaExpression mockExpression(final ExpressionDataType type) {
		MetaExpression expression = mock(MetaExpression.class);

		when(expression.getType()).thenReturn(type);
		return expression;
	}

	/**
	 * Mock a {@link MetaExpression} that holds a certain value
	 * 
	 * @param type
	 *        the type
	 * @param boolValue
	 *        the result of {@link MetaExpression#getBooleanValue()}
	 * @param numberValue
	 *        the result of {@link MetaExpression#getStringValue()}
	 * @param stringValue
	 *        the result of {@link MetaExpression#getNumberValue()}
	 * @return the expression
	 */
	protected static MetaExpression mockExpression(final ExpressionDataType type, final boolean boolValue, final double numberValue, final String stringValue) {
		MetaExpression expression = mockExpression(type);

		when(expression.getBooleanValue()).thenReturn(boolValue);
		when(expression.getNumberValue()).thenReturn(numberValue);
		when(expression.getStringValue()).thenReturn(stringValue);
		return expression;
	}
}
