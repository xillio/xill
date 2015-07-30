package nl.xillio.xill.plugins.string.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.string.services.string.StringService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link ToUpperConstruct}.
 */
public class ToUpperConstructTest {

	/**
	 * Test the process method under normal circumstances.
	 */
	@Test
	public void processNormalUsage() {
		// Mock
		String stringValue = "testing";
		MetaExpression string = mock(MetaExpression.class);
		when(string.getStringValue()).thenReturn(stringValue);
		when(string.isNull()).thenReturn(false);

		String returnValue = "TESTING";
		StringService stringService = mock(StringService.class);
		when(stringService.toUpperCase(stringValue)).thenReturn(returnValue);
		// Run
		MetaExpression result = ToUpperConstruct.process(string, stringService);

		// Verify
		verify(stringService, times(1)).toUpperCase(stringValue);

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}
}
