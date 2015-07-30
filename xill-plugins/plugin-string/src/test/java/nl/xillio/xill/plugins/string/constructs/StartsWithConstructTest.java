package nl.xillio.xill.plugins.string.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.StringService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link StartsWithConstruct}.
 */
public class StartsWithConstructTest {

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

		String prefixValue = "test";
		MetaExpression prefix = mock(MetaExpression.class);
		when(prefix.getStringValue()).thenReturn(prefixValue);
		when(prefix.isNull()).thenReturn(false);

		boolean returnValue = true;
		StringService stringService = mock(StringService.class);
		when(stringService.startsWith(stringValue, prefixValue)).thenReturn(returnValue);
		// Run
		MetaExpression result = StartsWithConstruct.process(string, prefix, stringService);

		// Verify
		verify(stringService, times(1)).startsWith(stringValue, prefixValue);

		// Assert
		Assert.assertEquals(result.getBooleanValue(), returnValue);
	}

	/**
	 * Test the process when the given values are null.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void processNullValueGiven() {
		// Mock
		String stringValue = "testing";
		MetaExpression string = mock(MetaExpression.class);
		when(string.getStringValue()).thenReturn(stringValue);
		when(string.isNull()).thenReturn(true);

		String prefixValue = "test";
		MetaExpression prefix = mock(MetaExpression.class);
		when(prefix.getStringValue()).thenReturn(prefixValue);
		when(prefix.isNull()).thenReturn(true);

		StringService stringService = mock(StringService.class);

		StartsWithConstruct.process(string, prefix, stringService);

	}
}
