package nl.xillio.xill.plugins.string.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.string.services.string.RegexService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link AllMatchesConstruct}.
 */
public class AllMatchesConstructTest extends ExpressionBuilderHelper {

	/**
	 * Test the process method under normal circumstances.
	 */
	@Test
	public void processNormalUsage() {
		// Mock
		String text = "abc def ghi jkl. Mno";
		MetaExpression value = mock(MetaExpression.class);
		when(value.getStringValue()).thenReturn(text);

		String regexValue = "\\w+";
		MetaExpression regex = mock(MetaExpression.class);
		when(regex.getStringValue()).thenReturn(regexValue);

		int timeoutValue = 10;
		MetaExpression timeout = mock(MetaExpression.class);
		when(timeout.getNumberValue()).thenReturn(timeoutValue);

		String ReturnValue = "[\"abc\",\"def\",\"ghi\",\"jkl\",\"Mno\"]";
		RegexService regexService = mock(RegexService.class);
		List<String> returnStatement = Arrays.asList("abc", "def", "ghi", "jkl", "Mno");
		when(regexService.tryMatch(any())).thenReturn(returnStatement);

		// Run
		MetaExpression result = AllMatchesConstruct.process(value, regex, timeout, regexService);

		// Verify
		verify(regexService, times(1)).tryMatch(any());
		verify(regexService, times(1)).getMatcher(regexValue, text, timeoutValue * 1000);

		// Assert
		Assert.assertEquals(result.getType(), LIST);
		Assert.assertEquals(result.getStringValue(), ReturnValue);
	}
}
