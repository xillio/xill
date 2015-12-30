package nl.xillio.xill.plugins.string.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link ToLowerConstruct}.
 */
public class ToLowerConstructTest {

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

		String returnValue = "testing";
		StringUtilityService stringService = mock(StringUtilityService.class);
		when(stringService.toLowerCase(stringValue)).thenReturn(returnValue);
		// Run
		MetaExpression result = ToLowerConstruct.process(string, stringService);

		// Verify
		verify(stringService, times(1)).toLowerCase(stringValue);

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}
}
