package nl.xillio.xill.plugins.string.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.regex.PatternSyntaxException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.exceptions.FailedToGetMatcherException;
import nl.xillio.xill.plugins.string.services.string.RegexService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link MatchesConstruct}.
 */
public class MatchesConstructTest {

	/**
	 * Test the process method with an ATOMIC value given.
	 * 
	 * @throws FailedToGetMatcherException
	 * @throws IllegalArgumentException
	 * @throws PatternSyntaxException
	 */
	@Test
	public void processStandardInput() throws IllegalArgumentException, FailedToGetMatcherException {
		// Mock
		String valueValue = "I need a doctor";
		MetaExpression value = mock(MetaExpression.class);
		when(value.getStringValue()).thenReturn(valueValue);

		String regexValue = ".*doctor*.";
		MetaExpression regex = mock(MetaExpression.class);
		when(regex.getStringValue()).thenReturn(regexValue);

		int timeoutValue = 10;
		MetaExpression timeout = mock(MetaExpression.class);
		when(timeout.getNumberValue()).thenReturn(timeoutValue);

		boolean returnValue = true;
		RegexService regexService = mock(RegexService.class);
		when(regexService.matches(any())).thenReturn(returnValue);
		// Run
		MetaExpression result = MatchesConstruct.process(value, regex, timeout, regexService);

		// Verify
		verify(regexService, times(1)).matches(any());
		verify(regexService, times(1)).getMatcher(regexValue, valueValue, timeoutValue * 1000);

		// Assert
		Assert.assertEquals(result.getBooleanValue(), returnValue);
	}

	/**
	 * Test the process method for when it throws an error.
	 * 
	 * @throws FailedToGetMatcherException
	 * @throws IllegalArgumentException
	 * @throws PatternSyntaxException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid pattern in matches.")
	public void processInvalidPattern() throws IllegalArgumentException, FailedToGetMatcherException {
		// Mock
		String valueValue = "I need a doctor";
		MetaExpression value = mock(MetaExpression.class);
		when(value.getStringValue()).thenReturn(valueValue);

		String regexValue = ".*doctor*.";
		MetaExpression regex = mock(MetaExpression.class);
		when(regex.getStringValue()).thenReturn(regexValue);

		int timeoutValue = 10;
		MetaExpression timeout = mock(MetaExpression.class);
		when(timeout.getNumberValue()).thenReturn(timeoutValue);

		Exception returnValue = new PatternSyntaxException(regexValue, regexValue, timeoutValue);
		RegexService regexService = mock(RegexService.class);
		when(regexService.getMatcher(regexValue, valueValue, timeoutValue * 1000)).thenThrow(returnValue);
		MatchesConstruct.process(value, regex, timeout, regexService);

		// Verify
		verify(regexService, times(1)).matches(any());
		verify(regexService, times(1)).getMatcher(regexValue, valueValue, timeoutValue * 1000);
	}

	/**
	 * Test the process method for when it throws an error.
	 * 
	 * @throws FailedToGetMatcherException
	 * @throws IllegalArgumentException
	 * @throws PatternSyntaxException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Illegal argument given.")
	public void processIllegalArgument() throws IllegalArgumentException, FailedToGetMatcherException {
		// Mock
		String valueValue = "I need a doctor";
		MetaExpression value = mock(MetaExpression.class);
		when(value.getStringValue()).thenReturn(valueValue);

		String regexValue = ".*doctor*.";
		MetaExpression regex = mock(MetaExpression.class);
		when(regex.getStringValue()).thenReturn(regexValue);

		int timeoutValue = 10;
		MetaExpression timeout = mock(MetaExpression.class);
		when(timeout.getNumberValue()).thenReturn(timeoutValue);

		Exception returnValue = new IllegalArgumentException();
		RegexService regexService = mock(RegexService.class);
		when(regexService.getMatcher(regexValue, valueValue, timeoutValue * 1000)).thenThrow(returnValue);
		MatchesConstruct.process(value, regex, timeout, regexService);

		// Verify
		verify(regexService, times(1)).matches(any());
		verify(regexService, times(1)).getMatcher(regexValue, valueValue, timeoutValue * 1000);
	}
}
