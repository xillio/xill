package nl.xillio.xill.plugins.math.constructs;

import static org.mockito.Mockito.*;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.string.constructs.IndexOfConstruct;
import nl.xillio.xill.plugins.string.constructs.JoinConstruct;
import nl.xillio.xill.plugins.string.services.string.StringService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link IndexOfConstruct}.
 */
public class JoinConstructTest  extends ExpressionBuilderHelper{

	/**
	 * Test the process method with an ATOMIC value given.
	 */
	@Test
	public void processAtomicInput() {
		// Mock
		String valueValue = "CORRECT";
		MetaExpression value = mock(MetaExpression.class);
		when(value.getStringValue()).thenReturn(valueValue);
		when(value.getType()).thenReturn(ATOMIC);

		String delimiterValue = "a";
		MetaExpression delimiter = mock(MetaExpression.class);
		when(delimiter.getStringValue()).thenReturn(delimiterValue);

		String returnValue = "CORRECT";
		StringService stringService = mock(StringService.class);
		// Run
		MetaExpression result = JoinConstruct.process(value, delimiter, stringService);

		// Verify
		verify(stringService, times(0)).join(any(), any());

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}
	
	/**
	 * Test the process method with a LIST value given.
	 */
	@Test
	public void processListInput(){
		// Mock
		String valueValue = "CORRECT";
		MetaExpression value = mock(MetaExpression.class);
		when(value.getStringValue()).thenReturn(valueValue);
		when(value.getType()).thenReturn(ATOMIC);

		String delimiterValue = "a";
		MetaExpression delimiter = mock(MetaExpression.class);
		when(delimiter.getStringValue()).thenReturn(delimiterValue);

		String returnValue = "CORRECT";
		StringService stringService = mock(StringService.class);
		// Run
		MetaExpression result = JoinConstruct.process(value, delimiter, stringService);

		// Verify
		verify(stringService, times(0)).join(any(), any());

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}
}
