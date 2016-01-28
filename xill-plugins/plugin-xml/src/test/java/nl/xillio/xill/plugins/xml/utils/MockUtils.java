package nl.xillio.xill.plugins.xml.utils;

import nl.xillio.xill.api.components.MetaExpression;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Utility code for mocking different kinds of objects.
 */
public class MockUtils {

    /**
     * @param value String value
     * @return A mocked {@link MetaExpression}
     */
    public static MetaExpression mockStringExpression(String value) {
        if (value == null)
            return mockNullExpression();
        MetaExpression expression = mock(MetaExpression.class);
        when(expression.getStringValue()).thenReturn(value);
        return expression;
    }

    /**
     * Mock a {@link MetaExpression} which will return true when isNull is called
     *
     * @return A mocked {@link MetaExpression}
     */
    public static MetaExpression mockNullExpression() {
        MetaExpression expression = mock(MetaExpression.class);
        when(expression.isNull()).thenReturn(true);
        return expression;
    }
}