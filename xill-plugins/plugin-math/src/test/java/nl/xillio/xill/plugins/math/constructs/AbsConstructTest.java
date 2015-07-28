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
 * Test the {@link AbsConstruct}
 */
public class AbsConstructTest {

	/**
	 * Test the process method under normal circumstances
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
	
	/**
	 * <p>Checks wheter the process works when the NULL value is given.</p>
	 * <p> Note that this actually only checks wheter NULL.toNumber() gives zero hence I do not know if it's out of place </p>
	 */
	@Test
	public void processNullValue() {
		//Mock
		MathOperations math = mock(MathOperations.class);
		when(math.abs(0)).thenReturn(0.0);
		
		//Run
		MetaExpression result = AbsConstruct.process(ExpressionBuilder.NULL, math);
		
		//Verify
		verify(math, times(1)).abs(0);
		
		//Assert
		Assert.assertEquals(result.getNumberValue().doubleValue(), 0);
	}
}
