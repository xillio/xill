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
 * Test the {@link RepeatConstruct}.
 */
public class RepeatConstructTest {

	/**
	 * Test the process method under normal circumstances.
	 */
	@Test
	public void processNormalUsage() {
		// Mock
		String stringValue = "eat, sleep, rave, repeat. ";
		MetaExpression string = mock(MetaExpression.class);
		when(string.getStringValue()).thenReturn(stringValue);

		int repeatValue = 4;
		MetaExpression repeat = mock(MetaExpression.class);
		when(repeat.getNumberValue()).thenReturn(repeatValue);

		String returnValue = "eat, sleep, rave, repeat. eat, sleep, rave, repeat. eat, sleep, rave, repeat. eat, sleep rave, repeat. ";
		StringUtilityService stringService = mock(StringUtilityService.class);
		when(stringService.repeat(stringValue, repeatValue)).thenReturn(returnValue);
		// Run
		MetaExpression result = RepeatConstruct.process(string, repeat, stringService);

		// Verify
		verify(stringService, times(1)).repeat(stringValue, repeatValue);

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}
}
