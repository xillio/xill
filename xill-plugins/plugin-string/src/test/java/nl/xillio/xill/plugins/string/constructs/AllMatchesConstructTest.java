package nl.xillio.xill.plugins.string.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.exceptions.FailedToGetMatcherException;
import nl.xillio.xill.plugins.string.services.string.RegexService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link AllMatchesConstruct}.
 */
public class AllMatchesConstructTest extends ExpressionBuilderHelper {

	/**
	 * Test the process method under normal circumstances.
	 *
	 * @throws FailedToGetMatcherException
	 * @throws IllegalArgumentException
	 * @throws PatternSyntaxException
	 */
	@Test
	public void processNormalUsage() throws IllegalArgumentException, FailedToGetMatcherException {
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

	/**
	 * Test the process method under normal circumstances.
	 *
	 * @throws FailedToGetMatcherException
	 * @throws IllegalArgumentException
	 * @throws PatternSyntaxException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid pattern: .*")
	public void processInvalidPattern() throws IllegalArgumentException, FailedToGetMatcherException {
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

		RegexService regexService = mock(RegexService.class);
		Arrays.asList("abc", "def", "ghi", "jkl", "Mno");
		when(regexService.getMatcher(regexValue, text, timeoutValue * 1000)).thenThrow(new PatternSyntaxException(regexValue, text, timeoutValue * 1000));

		AllMatchesConstruct.process(value, regex, timeout, regexService);

		// Verify
		verify(regexService, times(0)).tryMatch(any());
		verify(regexService, times(1)).getMatcher(regexValue, text, timeoutValue * 1000);
	}

	/**
	 * Test the process method under normal circumstances.
	 *
	 * @throws FailedToGetMatcherException
	 * @throws IllegalArgumentException
	 * @throws PatternSyntaxException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Illegal argument: .*")
	public void processIllegalArgument() throws IllegalArgumentException, FailedToGetMatcherException {
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

		RegexService regexService = mock(RegexService.class);
		Arrays.asList("abc", "def", "ghi", "jkl", "Mno");
		when(regexService.getMatcher(regexValue, text, timeoutValue * 1000)).thenThrow(new IllegalArgumentException());

		AllMatchesConstruct.process(value, regex, timeout, regexService);

		// Verify
		verify(regexService, times(0)).tryMatch(any());
		verify(regexService, times(1)).getMatcher(regexValue, text, timeoutValue * 1000);
	}
}
