package nl.xillio.xill.plugins.math.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.regex.PatternSyntaxException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.constructs.ReplaceConstruct;
import nl.xillio.xill.plugins.string.services.string.RegexService;
import nl.xillio.xill.plugins.string.services.string.StringService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link ReplaceConstruct}.
 */
public class ReplaceConstructTest {

	/**
	 * Test the process method when we use regex and want to replace all.
	 */
	@Test
	public void processUseRegexReplaceAll() {
		// Mock
		String textValue = "ReplaceR";
		MetaExpression text = mock(MetaExpression.class);
		when(text.getStringValue()).thenReturn(textValue);

		String needleValue = "R";
		MetaExpression needle = mock(MetaExpression.class);
		when(needle.getStringValue()).thenReturn(needleValue);

		String replacementValue = "O";
		MetaExpression replacement = mock(MetaExpression.class);
		when(replacement.getStringValue()).thenReturn(replacementValue);

		boolean useRegexValue = true;
		MetaExpression useRegex = mock(MetaExpression.class);
		when(useRegex.getBooleanValue()).thenReturn(useRegexValue);

		boolean replaceAllValue = true;
		MetaExpression replaceAll = mock(MetaExpression.class);
		when(replaceAll.getBooleanValue()).thenReturn(replaceAllValue);

		int timeoutValue = 10;
		MetaExpression timeout = mock(MetaExpression.class);
		when(timeout.getNumberValue()).thenReturn(timeoutValue);

		String returnValue = "OeplaceO";
		RegexService regexService = mock(RegexService.class);
		StringService stringService = mock(StringService.class);
		when(regexService.replaceAll(any(), eq(replacementValue))).thenReturn(returnValue);

		// Run
		MetaExpression args[] = {text, needle, replacement, useRegex, replaceAll, timeout};
		MetaExpression result = ReplaceConstruct.process(args, regexService, stringService);

		// Verify
		verify(regexService, times(1)).getMatcher(needleValue, textValue, timeoutValue * 1000);
		verify(regexService, times(1)).replaceAll(any(), eq(replacementValue));
		verify(regexService, times(0)).replaceFirst(any(), eq(replacementValue));
		verify(stringService, times(0)).replaceAll(textValue, needleValue, replacementValue);
		verify(stringService, times(0)).replaceFirst(textValue, needleValue, replacementValue);

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}

	/**
	 * Test the process method when we use regex and want to replace the first
	 */
	@Test
	public void processUseRegexReplaceFirst() {
		// Mock
		String textValue = "ReplaceR";
		MetaExpression text = mock(MetaExpression.class);
		when(text.getStringValue()).thenReturn(textValue);

		String needleValue = "R";
		MetaExpression needle = mock(MetaExpression.class);
		when(needle.getStringValue()).thenReturn(needleValue);

		String replacementValue = "O";
		MetaExpression replacement = mock(MetaExpression.class);
		when(replacement.getStringValue()).thenReturn(replacementValue);

		boolean useRegexValue = true;
		MetaExpression useRegex = mock(MetaExpression.class);
		when(useRegex.getBooleanValue()).thenReturn(useRegexValue);

		boolean replaceAllValue = false;
		MetaExpression replaceAll = mock(MetaExpression.class);
		when(replaceAll.getBooleanValue()).thenReturn(replaceAllValue);

		int timeoutValue = 10;
		MetaExpression timeout = mock(MetaExpression.class);
		when(timeout.getNumberValue()).thenReturn(timeoutValue);

		String returnValue = "OeplaceO";
		RegexService regexService = mock(RegexService.class);
		StringService stringService = mock(StringService.class);
		when(regexService.replaceFirst(any(), eq(replacementValue))).thenReturn(returnValue);

		// Run
		MetaExpression args[] = {text, needle, replacement, useRegex, replaceAll, timeout};
		MetaExpression result = ReplaceConstruct.process(args, regexService, stringService);

		// Verify
		verify(regexService, times(1)).getMatcher(needleValue, textValue, timeoutValue * 1000);
		verify(regexService, times(0)).replaceAll(any(), eq(replacementValue));
		verify(regexService, times(1)).replaceFirst(any(), eq(replacementValue));
		verify(stringService, times(0)).replaceAll(textValue, needleValue, replacementValue);
		verify(stringService, times(0)).replaceFirst(textValue, needleValue, replacementValue);

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}

	/**
	 * Test the process method when we don't use regex and want to replace all.
	 */
	@Test
	public void processDontUseRegexReplaceAll() {
		// Mock
		String textValue = "ReplaceR";
		MetaExpression text = mock(MetaExpression.class);
		when(text.getStringValue()).thenReturn(textValue);

		String needleValue = "R";
		MetaExpression needle = mock(MetaExpression.class);
		when(needle.getStringValue()).thenReturn(needleValue);

		String replacementValue = "O";
		MetaExpression replacement = mock(MetaExpression.class);
		when(replacement.getStringValue()).thenReturn(replacementValue);

		boolean useRegexValue = false;
		MetaExpression useRegex = mock(MetaExpression.class);
		when(useRegex.getBooleanValue()).thenReturn(useRegexValue);

		boolean replaceAllValue = true;
		MetaExpression replaceAll = mock(MetaExpression.class);
		when(replaceAll.getBooleanValue()).thenReturn(replaceAllValue);

		int timeoutValue = 10;
		MetaExpression timeout = mock(MetaExpression.class);
		when(timeout.getNumberValue()).thenReturn(timeoutValue);

		String returnValue = "OeplaceO";
		RegexService regexService = mock(RegexService.class);
		StringService stringService = mock(StringService.class);
		when(stringService.replaceAll(textValue, needleValue, replacementValue)).thenReturn(returnValue);

		// Run
		MetaExpression args[] = {text, needle, replacement, useRegex, replaceAll, timeout};
		MetaExpression result = ReplaceConstruct.process(args, regexService, stringService);

		// Verify
		verify(regexService, times(0)).getMatcher(needleValue, textValue, timeoutValue * 1000);
		verify(regexService, times(0)).replaceAll(any(), eq(replacementValue));
		verify(regexService, times(0)).replaceFirst(any(), eq(replacementValue));
		verify(stringService, times(1)).replaceAll(textValue, needleValue, replacementValue);
		verify(stringService, times(0)).replaceFirst(textValue, needleValue, replacementValue);

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}

	/**
	 * Test the process method when we don't use regex and want to replace only the first.
	 */
	@Test
	public void processDontUseRegexReplaceFirst() {
		// Mock
		String textValue = "ReplaceR";
		MetaExpression text = mock(MetaExpression.class);
		when(text.getStringValue()).thenReturn(textValue);

		String needleValue = "R";
		MetaExpression needle = mock(MetaExpression.class);
		when(needle.getStringValue()).thenReturn(needleValue);

		String replacementValue = "O";
		MetaExpression replacement = mock(MetaExpression.class);
		when(replacement.getStringValue()).thenReturn(replacementValue);

		boolean useRegexValue = false;
		MetaExpression useRegex = mock(MetaExpression.class);
		when(useRegex.getBooleanValue()).thenReturn(useRegexValue);

		boolean replaceAllValue = false;
		MetaExpression replaceAll = mock(MetaExpression.class);
		when(replaceAll.getBooleanValue()).thenReturn(replaceAllValue);

		int timeoutValue = 10;
		MetaExpression timeout = mock(MetaExpression.class);
		when(timeout.getNumberValue()).thenReturn(timeoutValue);

		String returnValue = "OeplaceO";
		RegexService regexService = mock(RegexService.class);
		StringService stringService = mock(StringService.class);
		when(stringService.replaceFirst(textValue, needleValue, replacementValue)).thenReturn(returnValue);

		// Run
		MetaExpression args[] = {text, needle, replacement, useRegex, replaceAll, timeout};
		MetaExpression result = ReplaceConstruct.process(args, regexService, stringService);

		// Verify
		verify(regexService, times(0)).getMatcher(needleValue, textValue, timeoutValue * 1000);
		verify(regexService, times(0)).replaceAll(any(), eq(replacementValue));
		verify(regexService, times(0)).replaceFirst(any(), eq(replacementValue));
		verify(stringService, times(0)).replaceAll(textValue, needleValue, replacementValue);
		verify(stringService, times(1)).replaceFirst(textValue, needleValue, replacementValue);

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}
	
	/**
	 * Test the process method when getMatcher throws a PatternSyntax
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid pattern in regex\\(\\)")
	public void processInvalidException() {
		// Mock
		String textValue = "ReplaceR";
		MetaExpression text = mock(MetaExpression.class);
		when(text.getStringValue()).thenReturn(textValue);

		String needleValue = "R";
		MetaExpression needle = mock(MetaExpression.class);
		when(needle.getStringValue()).thenReturn(needleValue);

		String replacementValue = "O";
		MetaExpression replacement = mock(MetaExpression.class);
		when(replacement.getStringValue()).thenReturn(replacementValue);

		boolean useRegexValue = true;
		MetaExpression useRegex = mock(MetaExpression.class);
		when(useRegex.getBooleanValue()).thenReturn(useRegexValue);

		boolean replaceAllValue = true;
		MetaExpression replaceAll = mock(MetaExpression.class);
		when(replaceAll.getBooleanValue()).thenReturn(replaceAllValue);

		int timeoutValue = 10;
		MetaExpression timeout = mock(MetaExpression.class);
		when(timeout.getNumberValue()).thenReturn(timeoutValue);

		Exception returnValue = new PatternSyntaxException(needleValue, textValue, timeoutValue);
		RegexService regexService = mock(RegexService.class);
		StringService stringService = mock(StringService.class);
		when(regexService.getMatcher(needleValue, textValue, timeoutValue * 1000)).thenThrow(returnValue);

		// Run
		MetaExpression args[] = {text, needle, replacement, useRegex, replaceAll, timeout};
		ReplaceConstruct.process(args, regexService, stringService);

		// Verify
		verify(regexService, times(1)).getMatcher(needleValue, textValue, timeoutValue * 1000);
		verify(regexService, times(0)).replaceAll(any(), eq(replacementValue));
		verify(regexService, times(0)).replaceFirst(any(), eq(replacementValue));
		verify(stringService, times(0)).replaceAll(textValue, needleValue, replacementValue);
		verify(stringService, times(0)).replaceFirst(textValue, needleValue, replacementValue);
	}
	
	/**
	 * Test the method when getMatcher returns an illegalArgumentException.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Error while executing the regex")
	public void processExecuteError() {
		// Mock
		String textValue = "ReplaceR";
		MetaExpression text = mock(MetaExpression.class);
		when(text.getStringValue()).thenReturn(textValue);

		String needleValue = "R";
		MetaExpression needle = mock(MetaExpression.class);
		when(needle.getStringValue()).thenReturn(needleValue);

		String replacementValue = "O";
		MetaExpression replacement = mock(MetaExpression.class);
		when(replacement.getStringValue()).thenReturn(replacementValue);

		boolean useRegexValue = true;
		MetaExpression useRegex = mock(MetaExpression.class);
		when(useRegex.getBooleanValue()).thenReturn(useRegexValue);

		boolean replaceAllValue = true;
		MetaExpression replaceAll = mock(MetaExpression.class);
		when(replaceAll.getBooleanValue()).thenReturn(replaceAllValue);

		int timeoutValue = 10;
		MetaExpression timeout = mock(MetaExpression.class);
		when(timeout.getNumberValue()).thenReturn(timeoutValue);

		Exception returnValue = new IllegalArgumentException();
		RegexService regexService = mock(RegexService.class);
		StringService stringService = mock(StringService.class);
		when(regexService.getMatcher(needleValue, textValue, timeoutValue * 1000)).thenThrow(returnValue);

		// Run
		MetaExpression args[] = {text, needle, replacement, useRegex, replaceAll, timeout};
		ReplaceConstruct.process(args, regexService, stringService);

		// Verify
		verify(regexService, times(1)).getMatcher(needleValue, textValue, timeoutValue * 1000);
		verify(regexService, times(0)).replaceAll(any(), eq(replacementValue));
		verify(regexService, times(0)).replaceFirst(any(), eq(replacementValue));
		verify(stringService, times(0)).replaceAll(textValue, needleValue, replacementValue);
		verify(stringService, times(0)).replaceFirst(textValue, needleValue, replacementValue);
	}
}
