package nl.xillio.xill.plugins.math.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.constructs.SubstringConstruct;
import nl.xillio.xill.plugins.string.services.string.StringService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link SubstringConstruct}.
 */
public class SubstringConstructTest {

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

		int startValue = 1;
		MetaExpression start = mock(MetaExpression.class);
		when(start.getNumberValue()).thenReturn(startValue);
		when(start.isNull()).thenReturn(false);

		int endValue = 3;
		MetaExpression end = mock(MetaExpression.class);
		when(end.getNumberValue()).thenReturn(endValue);
		when(end.isNull()).thenReturn(false);

		String returnValue = "est";
		StringService stringService = mock(StringService.class);
		when(stringService.subString(stringValue, startValue, endValue)).thenReturn(returnValue);
		// Run
		MetaExpression result = SubstringConstruct.process(string, start, end, stringService);

		// Verify
		verify(stringService, times(1)).subString(stringValue, startValue, endValue);

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}

	/**
	 * Test the process method under normal circumstances.
	 */
	@Test
	public void processEndIs0() {
		// Mock
		String stringValue = "testing";
		MetaExpression string = mock(MetaExpression.class);
		when(string.getStringValue()).thenReturn(stringValue);
		when(string.isNull()).thenReturn(false);

		int startValue = 1;
		MetaExpression start = mock(MetaExpression.class);
		when(start.getNumberValue()).thenReturn(startValue);
		when(start.isNull()).thenReturn(false);

		int endValue = 0;
		MetaExpression end = mock(MetaExpression.class);
		when(end.getNumberValue()).thenReturn(endValue);
		when(end.isNull()).thenReturn(false);

		String returnValue = stringValue;
		StringService stringService = mock(StringService.class);
		when(stringService.subString(stringValue, startValue, stringValue.length())).thenReturn(returnValue);
		// Run
		MetaExpression result = SubstringConstruct.process(string, start, end, stringService);

		// Verify
		verify(stringService, times(1)).subString(stringValue, startValue, stringValue.length());

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}

	/**
	 * Test the process method under normal circumstances.
	 */
	@Test
	public void processHighStartValue() {
		// Mock
		String stringValue = "testing";
		MetaExpression string = mock(MetaExpression.class);
		when(string.getStringValue()).thenReturn(stringValue);
		when(string.isNull()).thenReturn(false);

		int startValue = 7;
		MetaExpression start = mock(MetaExpression.class);
		when(start.getNumberValue()).thenReturn(startValue);
		when(start.isNull()).thenReturn(false);

		int endValue = 3;
		MetaExpression end = mock(MetaExpression.class);
		when(end.getNumberValue()).thenReturn(endValue);
		when(end.isNull()).thenReturn(false);

		String returnValue = "est";
		StringService stringService = mock(StringService.class);
		when(stringService.subString(stringValue, 0, endValue)).thenReturn(returnValue);
		// Run
		MetaExpression result = SubstringConstruct.process(string, start, end, stringService);

		// Verify
		verify(stringService, times(1)).subString(stringValue, 0, endValue);

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}

	/**
	 * Test the process method under normal circumstances.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Index out of bounds.")
	public void processErrorThrown() {
		// Mock
		String stringValue = "testing";
		MetaExpression string = mock(MetaExpression.class);
		when(string.getStringValue()).thenReturn(stringValue);
		when(string.isNull()).thenReturn(false);

		int startValue = 1;
		MetaExpression start = mock(MetaExpression.class);
		when(start.getNumberValue()).thenReturn(startValue);
		when(start.isNull()).thenReturn(false);

		int endValue = 3;
		MetaExpression end = mock(MetaExpression.class);
		when(end.getNumberValue()).thenReturn(endValue);
		when(end.isNull()).thenReturn(false);

		Exception returnValue = new StringIndexOutOfBoundsException();
		StringService stringService = mock(StringService.class);
		when(stringService.subString(stringValue, startValue, endValue)).thenThrow(returnValue);
		SubstringConstruct.process(string, start, end, stringService);

		// Verify
		verify(stringService, times(1)).subString(stringValue, startValue, endValue);
	}
}
