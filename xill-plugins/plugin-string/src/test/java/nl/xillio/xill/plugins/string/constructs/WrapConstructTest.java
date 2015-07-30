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
 * Test the {@link WrapConstruct}.
 */
public class WrapConstructTest {

	/**
	 * Test the process method under normal circumstances.
	 */
	@Test
	public void processNormalUsage() {
		// Mock
		String stringValue = "testing";
		MetaExpression string = mock(MetaExpression.class);
		when(string.getStringValue()).thenReturn(stringValue);

		int wrapValue = 5;
		MetaExpression wrap = mock(MetaExpression.class);
		when(wrap.getNumberValue()).thenReturn(wrapValue);

		boolean wrapLongWordsValue = true;
		MetaExpression wrapLongWords = mock(MetaExpression.class);
		when(wrapLongWords.getBooleanValue()).thenReturn(wrapLongWordsValue);

		String returnValue = "testi \n ng";
		StringService stringService = mock(StringService.class);
		when(stringService.wrap(stringValue, wrapValue, wrapLongWordsValue)).thenReturn(returnValue);
		// Run
		MetaExpression result = WrapConstruct.process(string, wrap, wrapLongWords, stringService);

		// Verify
		verify(stringService, times(1)).wrap(stringValue, wrapValue, wrapLongWordsValue);

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}
}
