package nl.xillio.xill.plugins.date.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;

import nl.xillio.xill.api.components.MetaExpression;

public class MockUtils {
	public static MetaExpression mockBoolExpression(boolean value) {
		MetaExpression expression = mock(MetaExpression.class);
		when(expression.getBooleanValue()).thenReturn(value);
		return expression;
	}

	public static MetaExpression mockDateExpression(ZonedDateTime value) {
		MetaExpression expression = mock(MetaExpression.class);
		when(expression.getMeta(ZonedDateTime.class)).thenReturn(value);
		return expression;
	}

	public static MetaExpression mockStringExpression(String value) {
		MetaExpression expression = mock(MetaExpression.class);
		when(expression.getStringValue()).thenReturn(value);
		return expression;
	}

	public static MetaExpression mockNullExpression() {
		MetaExpression expression = mock(MetaExpression.class);
		when(expression.isNull()).thenReturn(true);
		return expression;
	}
}
