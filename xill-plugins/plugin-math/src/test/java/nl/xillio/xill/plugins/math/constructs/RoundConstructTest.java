package nl.xillio.xill.plugins.math.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.math.services.math.MathOperations;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link RoundConstruct}
 */
public class RoundConstructTest {

	/**
	 * Test the process method under normal circumstances
	 */
	@Test
	public void processNormalUsage() {
		// Mock
		Double numberValue = 42.2;
		MetaExpression value = mock(MetaExpression.class);
		when(value.getNumberValue()).thenReturn(numberValue);

		long mathReturnValue = 42;
		MathOperations math = mock(MathOperations.class);
		when(math.round(numberValue)).thenReturn(mathReturnValue);

		// Run
		MetaExpression result = RoundConstruct.process(value, math);

		// Verify
		verify(math, times(1)).round(numberValue);

		// Assert
		Assert.assertEquals(result.getNumberValue().longValue(), mathReturnValue);

	}
}
