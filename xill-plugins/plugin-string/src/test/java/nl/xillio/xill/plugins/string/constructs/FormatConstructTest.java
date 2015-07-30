package nl.xillio.xill.plugins.string.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatPrecisionException;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.regex.PatternSyntaxException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.exceptions.FailedToGetMatcherException;
import nl.xillio.xill.plugins.string.services.string.RegexService;
import nl.xillio.xill.plugins.string.services.string.StringService;

import org.testng.annotations.Test;

/**
 * Test the {@link FormatConstruct}.
 */
public class FormatConstructTest {

	/**
	 * Test the process method under normal circumstances.
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	// @Test
	public void processNormalUsage() throws IOException {
		// TODO
	}

	/**
	 * <p>
	 * Tests wheter the process can handle a syntax error in the pattern given to the matcher
	 * </p>
	 * 
	 * @throws FailedToGetMatcherException
	 * @throws IllegalArgumentException
	 * @throws PatternSyntaxException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "SyntaxError in the by the system provided pattern.")
	public void processPatternSyntaxException() throws PatternSyntaxException, IllegalArgumentException, FailedToGetMatcherException {
		// Mock
		String fileNameValue = "decimal %d%n";
		MetaExpression fileName = mock(MetaExpression.class);
		when(fileName.getStringValue()).thenReturn(fileNameValue);
		when(fileName.isNull()).thenReturn(false);

		ArrayList<MetaExpression> listValue = new ArrayList<>();
		MetaExpression list = mock(MetaExpression.class);
		when(list.getValue()).thenReturn(listValue);

		Exception exception = new PatternSyntaxException("", "", 0);
		RegexService regexService = mock(RegexService.class);
		StringService stringService = mock(StringService.class);
		when(regexService.getMatcher(anyString(), anyString(), anyInt())).thenThrow(exception);

		// Run
		FormatConstruct.process(fileName, list, regexService, stringService);

		// Verify
		verify(regexService, times(1)).getMatcher(anyString(), anyString(), anyInt());
		verify(regexService, times(0)).tryMatch(any());
		verify(stringService, times(0)).format(anyString(), any());
	}

	/**
	 * <p>
	 * Tests wheter the process can handle an illegal argument given to the matcher.
	 * </p>
	 * 
	 * @throws FailedToGetMatcherException
	 * @throws IllegalArgumentException
	 * @throws PatternSyntaxException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Illegal argument handed when trying to match.")
	public void processIllegalArgumentException() throws PatternSyntaxException, IllegalArgumentException, FailedToGetMatcherException {
		// Mock
		String fileNameValue = "decimal %d%n";
		MetaExpression fileName = mock(MetaExpression.class);
		when(fileName.getStringValue()).thenReturn(fileNameValue);
		when(fileName.isNull()).thenReturn(false);

		ArrayList<MetaExpression> listValue = new ArrayList<>();
		MetaExpression list = mock(MetaExpression.class);
		when(list.getValue()).thenReturn(listValue);

		Exception exception = new IllegalArgumentException();
		RegexService regexService = mock(RegexService.class);
		StringService stringService = mock(StringService.class);
		when(regexService.getMatcher(anyString(), anyString(), anyInt())).thenThrow(exception);

		// Run
		FormatConstruct.process(fileName, list, regexService, stringService);

		// Verify
		verify(regexService, times(1)).getMatcher(anyString(), anyString(), anyInt());
		verify(regexService, times(0)).tryMatch(any());
		verify(stringService, times(0)).format(anyString(), any());
	}

	/**
	 * <p>
	 * Tests wheter the process can handle an illegal argument given to the matcher.
	 * </p>
	 * 
	 * @throws FailedToGetMatcherException
	 * @throws IllegalArgumentException
	 * @throws PatternSyntaxException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Date/Time conversions are not supported.")
	public void processDateTimeConversion() throws PatternSyntaxException, IllegalArgumentException, FailedToGetMatcherException {
		// Mock
		String fileNameValue = "decimal %T%n";
		MetaExpression fileName = mock(MetaExpression.class);
		when(fileName.getStringValue()).thenReturn(fileNameValue);
		when(fileName.isNull()).thenReturn(false);

		List<MetaExpression> listValue = new ArrayList<>();
		MetaExpression listItem = mock(MetaExpression.class);
		listValue.add(listItem);
		MetaExpression list = mock(MetaExpression.class);
		when(list.getValue()).thenReturn(listValue);

		String errorValue = "T";
		List<String> matchValue = Arrays.asList(errorValue);
		RegexService regexService = mock(RegexService.class);
		StringService stringService = mock(StringService.class);
		when(regexService.tryMatch(any())).thenReturn(matchValue);

		// Run
		FormatConstruct.process(fileName, list, regexService, stringService);

		// Verify
		verify(regexService, times(1)).getMatcher(anyString(), anyString(), anyInt());
		verify(regexService, times(1)).tryMatch(any());
		verify(stringService, times(0)).format(anyString(), any());
	}

	/**
	 * <p>
	 * Tests wheter the process can handle an illegal argument given to the matcher.
	 * </p>
	 * 
	 * @throws FailedToGetMatcherException
	 * @throws IllegalArgumentException
	 * @throws PatternSyntaxException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Unexpected conversion type: Z")
	public void processUnexpectedConversion() throws PatternSyntaxException, IllegalArgumentException, FailedToGetMatcherException {
		// Mock
		String fileNameValue = "decimal %Z%n";
		MetaExpression fileName = mock(MetaExpression.class);
		when(fileName.getStringValue()).thenReturn(fileNameValue);
		when(fileName.isNull()).thenReturn(false);

		List<MetaExpression> listValue = new ArrayList<>();
		MetaExpression listItem = mock(MetaExpression.class);
		listValue.add(listItem);
		MetaExpression list = mock(MetaExpression.class);
		when(list.getValue()).thenReturn(listValue);

		String errorValue = "Z";
		List<String> matchValue = Arrays.asList(errorValue);
		RegexService regexService = mock(RegexService.class);
		StringService stringService = mock(StringService.class);
		when(regexService.tryMatch(any())).thenReturn(matchValue);

		// Run
		FormatConstruct.process(fileName, list, regexService, stringService);

		// Verify
		verify(regexService, times(1)).getMatcher(anyString(), anyString(), anyInt());
		verify(regexService, times(1)).tryMatch(any());
		verify(stringService, times(0)).format(anyString(), any());
	}

	/**
	 * <p>
	 * Tests wheter the process can handle an illegal argument given to the matcher.
	 * </p>
	 * 
	 * @throws FailedToGetMatcherException
	 * @throws IllegalArgumentException
	 * @throws PatternSyntaxException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Not enough arguments.")
	public void processMissingFormatException() throws PatternSyntaxException, IllegalArgumentException, FailedToGetMatcherException {
		// Mock
		String textValue = "decimal";
		MetaExpression text = mock(MetaExpression.class);
		when(text.getStringValue()).thenReturn(textValue);
		when(text.isNull()).thenReturn(false);

		List<MetaExpression> listValue = new ArrayList<>();
		MetaExpression listItem = mock(MetaExpression.class);
		listValue.add(listItem);
		MetaExpression list = mock(MetaExpression.class);
		when(list.getValue()).thenReturn(listValue);

		List<String> matchValue = Arrays.asList();
		RegexService regexService = mock(RegexService.class);
		StringService stringService = mock(StringService.class);
		when(regexService.tryMatch(any())).thenReturn(matchValue);
		when(stringService.format(eq(textValue), any())).thenThrow(new MissingFormatArgumentException("argument"));

		// Run
		FormatConstruct.process(text, list, regexService, stringService);

		// Verify
		verify(regexService, times(1)).getMatcher(anyString(), anyString(), anyInt());
		verify(regexService, times(1)).tryMatch(any());
		verify(stringService, times(1)).format(anyString(), any());
	}

	/**
	 * <p>
	 * Tests wheter the process can handle an illegal argument given to the matcher.
	 * </p>
	 * 
	 * @throws FailedToGetMatcherException
	 * @throws IllegalArgumentException
	 * @throws PatternSyntaxException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Illegal format handed.")
	public void processIllegalFormatException() throws PatternSyntaxException, IllegalArgumentException, FailedToGetMatcherException {
		// Mock
		String textValue = "decimal";
		MetaExpression text = mock(MetaExpression.class);
		when(text.getStringValue()).thenReturn(textValue);
		when(text.isNull()).thenReturn(false);

		List<MetaExpression> listValue = new ArrayList<>();
		MetaExpression listItem = mock(MetaExpression.class);
		listValue.add(listItem);
		MetaExpression list = mock(MetaExpression.class);
		when(list.getValue()).thenReturn(listValue);

		List<String> matchValue = Arrays.asList();
		RegexService regexService = mock(RegexService.class);
		StringService stringService = mock(StringService.class);
		when(regexService.tryMatch(any())).thenReturn(matchValue);
		when(stringService.format(eq(textValue), any())).thenThrow(new IllegalFormatPrecisionException(3));

		// Run
		FormatConstruct.process(text, list, regexService, stringService);

		// Verify
		verify(regexService, times(1)).getMatcher(anyString(), anyString(), anyInt());
		verify(regexService, times(1)).tryMatch(any());
		verify(stringService, times(1)).format(anyString(), any());
	}
}
