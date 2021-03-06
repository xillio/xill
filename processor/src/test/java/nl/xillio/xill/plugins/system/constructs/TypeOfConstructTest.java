package nl.xillio.xill.plugins.system.constructs;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the {@link TypeOfConstruct}
 */
public class TypeOfConstructTest {

    /**
     * Test the only possible usage
     */
    @Test
    public void testProcess() {
        // Mock context
        for (ExpressionDataType expectedType : ExpressionDataType.values()) {
            MetaExpression expression = mock(MetaExpression.class);
            when(expression.getType()).thenReturn(expectedType);

            // Run
            MetaExpression result = TypeOfConstruct.process(expression);

            // Verify
            verify(expression).getType();

            // Assert
            Assert.assertSame(result.getStringValue(), expectedType.toString());
        }
    }
}
