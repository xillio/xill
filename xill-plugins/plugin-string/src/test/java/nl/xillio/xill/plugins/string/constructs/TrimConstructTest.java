package nl.xillio.xill.plugins.string.constructs;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link TrimConstruct}.
 */
public class TrimConstructTest extends ExpressionBuilderHelper {

	/**
	 * Test the process method with a string given and internal set to false.
	 */
	@Test
	public void processAtomicNoInternal() {
		// Mock
		String stringValue = "testing+";
		MetaExpression string = mock(MetaExpression.class);
		when(string.getStringValue()).thenReturn(stringValue);
		when(string.getType()).thenReturn(ATOMIC);
		when(string.isNull()).thenReturn(false);

		boolean internalValue = false;
		MetaExpression internal = mock(MetaExpression.class);
		when(internal.getBooleanValue()).thenReturn(internalValue);

		String replaceValue = "testing";
		String returnValue = "Testing";
		StringUtilityService stringService = mock(StringUtilityService.class);
		when(stringService.replaceAll(stringValue, "\u00A0", " ")).thenReturn(replaceValue);
		when(stringService.trim(replaceValue)).thenReturn(returnValue);
		// Run
		MetaExpression result = TrimConstruct.process(string, internal, stringService);

		// Verify
		verify(stringService, times(1)).replaceAll(stringValue, "\u00A0", " ");
		verify(stringService, times(1)).trim(replaceValue);
		verify(stringService, times(0)).replaceAll(returnValue, "[\\s]+", " ");

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}

	/**
	 * Test the process method with a string given and internal set to true.
	 */
	@Test
	public void processAtomicInternal() {
		// Mock
		String stringValue = "testing+";
		MetaExpression string = mock(MetaExpression.class);
		when(string.getStringValue()).thenReturn(stringValue);
		when(string.getType()).thenReturn(ATOMIC);
		when(string.isNull()).thenReturn(false);

		boolean internalValue = true;
		MetaExpression internal = mock(MetaExpression.class);
		when(internal.getBooleanValue()).thenReturn(internalValue);

		String replaceValue = "testing";
		String trimValue = "Testing";
		String returnValue = "Return";
		StringUtilityService stringService = mock(StringUtilityService.class);
		when(stringService.replaceAll(stringValue, "\u00A0", " ")).thenReturn(replaceValue);
		when(stringService.trim(replaceValue)).thenReturn(trimValue);
		when(stringService.replaceAll(trimValue, "[\\s]+", " ")).thenReturn(returnValue);
		// Run
		MetaExpression result = TrimConstruct.process(string, internal, stringService);

		// Verify
		verify(stringService, times(1)).replaceAll(stringValue, "\u00A0", " ");
		verify(stringService, times(1)).trim(replaceValue);
		verify(stringService, times(1)).replaceAll(trimValue, "[\\s]+", " ");

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}

	/**
	 * Test the process with a list given and internal set to false
	 */
	@Test
	public void processListNoInternal() {
		// Mock
		String firstValue = "first+";
		String firstReplaceValue = "first";
		String firstTrimValue = "FIRST";
		MetaExpression first = mock(MetaExpression.class);
		when(first.getStringValue()).thenReturn(firstValue);

		String secondValue = "second+";
		String secondReplaceValue = "second";
		String secondTrimValue = "SECOND";
		MetaExpression second = mock(MetaExpression.class);
		when(second.getStringValue()).thenReturn(secondValue);

		List<MetaExpression> listValue = Arrays.asList(first, second);
		MetaExpression list = mock(MetaExpression.class);
		when(list.getValue()).thenReturn(listValue);
		when(list.getType()).thenReturn(LIST);
		when(list.isNull()).thenReturn(false);

		boolean internalValue = false;
		MetaExpression internal = mock(MetaExpression.class);
		when(internal.getBooleanValue()).thenReturn(internalValue);

		StringUtilityService stringService = mock(StringUtilityService.class);
		when(stringService.replaceAll(firstValue, "\u00A0", " ")).thenReturn(firstReplaceValue);
		when(stringService.trim(firstReplaceValue)).thenReturn(firstTrimValue);
		when(stringService.replaceAll(secondValue, "\u00A0", " ")).thenReturn(secondReplaceValue);
		when(stringService.trim(secondReplaceValue)).thenReturn(secondTrimValue);

		// Run
		MetaExpression result = TrimConstruct.process(list, internal, stringService);

		// Verify
		verify(stringService, times(1)).replaceAll(firstValue, "\u00A0", " ");
		verify(stringService, times(1)).trim(firstReplaceValue);
		verify(stringService, times(0)).replaceAll(firstTrimValue, "[\\s]+", " ");
		verify(stringService, times(1)).replaceAll(secondValue, "\u00A0", " ");
		verify(stringService, times(1)).trim(secondReplaceValue);
		verify(stringService, times(0)).replaceAll(secondTrimValue, "[\\s]+", " ");

		// Assert
		Assert.assertEquals(result.getType(), LIST);
		@SuppressWarnings("unchecked")
		List<MetaExpression> resultAsList = (List<MetaExpression>) result.getValue();
		Assert.assertEquals(resultAsList.size(), 2);
		Assert.assertEquals(resultAsList.get(0).getStringValue(), firstTrimValue);
		Assert.assertEquals(resultAsList.get(1).getStringValue(), secondTrimValue);
	}

	/**
	 * Test the process method with a list given and internal set to true.
	 */
	@Test
	public void processListInternal() {
		// Mock
		String firstValue = "first+";
		String firstReplaceValue = "first";
		String firstTrimValue = "FIRST";
		String firstReturnValue = "First";
		MetaExpression first = mock(MetaExpression.class);
		when(first.getStringValue()).thenReturn(firstValue);

		String secondValue = "second+";
		String secondReplaceValue = "second";
		String secondTrimValue = "SECOND";
		String secondReturnValue = "Second";
		MetaExpression second = mock(MetaExpression.class);
		when(second.getStringValue()).thenReturn(secondValue);

		List<MetaExpression> listValue = Arrays.asList(first, second);
		MetaExpression list = mock(MetaExpression.class);
		when(list.getValue()).thenReturn(listValue);
		when(list.getType()).thenReturn(LIST);
		when(list.isNull()).thenReturn(false);

		boolean internalValue = true;
		MetaExpression internal = mock(MetaExpression.class);
		when(internal.getBooleanValue()).thenReturn(internalValue);

		StringUtilityService stringService = mock(StringUtilityService.class);
		when(stringService.replaceAll(firstValue, "\u00A0", " ")).thenReturn(firstReplaceValue);
		when(stringService.trim(firstReplaceValue)).thenReturn(firstTrimValue);
		when(stringService.replaceAll(firstTrimValue, "[\\s]+", " ")).thenReturn(firstReturnValue);
		when(stringService.replaceAll(secondValue, "\u00A0", " ")).thenReturn(secondReplaceValue);
		when(stringService.trim(secondReplaceValue)).thenReturn(secondTrimValue);
		when(stringService.replaceAll(secondTrimValue, "[\\s]+", " ")).thenReturn(secondReturnValue);

		// Run
		MetaExpression result = TrimConstruct.process(list, internal, stringService);

		// Verify
		verify(stringService, times(1)).replaceAll(firstValue, "\u00A0", " ");
		verify(stringService, times(1)).trim(firstReplaceValue);
		verify(stringService, times(1)).replaceAll(firstTrimValue, "[\\s]+", " ");
		verify(stringService, times(1)).replaceAll(secondValue, "\u00A0", " ");
		verify(stringService, times(1)).trim(secondReplaceValue);
		verify(stringService, times(1)).replaceAll(secondTrimValue, "[\\s]+", " ");

		// Assert
		Assert.assertEquals(result.getType(), LIST);
		@SuppressWarnings("unchecked")
		List<MetaExpression> resultAsList = (List<MetaExpression>) result.getValue();
		Assert.assertEquals(resultAsList.size(), 2);
		Assert.assertEquals(resultAsList.get(0).getStringValue(), firstReturnValue);
		Assert.assertEquals(resultAsList.get(1).getStringValue(), secondReturnValue);
	}

	/**
	 * Test the process method with a null value given.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void processNullValue() {
		// Mock
		String stringValue = "testing+";
		MetaExpression string = mock(MetaExpression.class);
		when(string.getStringValue()).thenReturn(stringValue);
		when(string.getType()).thenReturn(ATOMIC);
		when(string.isNull()).thenReturn(true);

		boolean internalValue = true;
		MetaExpression internal = mock(MetaExpression.class);
		when(internal.getBooleanValue()).thenReturn(internalValue);

		StringUtilityService stringService = mock(StringUtilityService.class);

		// Run
		TrimConstruct.process(string, internal, stringService);

		// Verify
		verify(stringService, times(0)).replaceAll(anyString(), anyString(), anyString());
		verify(stringService, times(0)).trim(anyString());
		verify(stringService, times(0)).replaceAll(anyString(), anyString(), anyString());
	}
}
