package nl.xillio.xill.plugins.math.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.math.services.math.MathOperations;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link AbsConstruct}.
 */
public class AbsConstructTest {

	/**
	 * Test the process method under normal circumstances.
	 */
	@Test
	public void processNormalUsage() {
		// Mock
		Double numberValue = -504.0;
		MetaExpression value = mock(MetaExpression.class);
		when(value.getNumberValue()).thenReturn(numberValue);

		Double mathReturnValue = 504.0;
		MathOperations math = mock(MathOperations.class);
		when(math.abs(numberValue)).thenReturn(mathReturnValue);

		// Run
		MetaExpression result = AbsConstruct.process(value, math);

		// Verify
		verify(math, times(1)).abs(numberValue);

		// Assert
		Assert.assertEquals(result.getNumberValue().doubleValue(), mathReturnValue);
	}
}
