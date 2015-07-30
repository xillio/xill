package nl.xillio.xill.plugins.string.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link SplitConstruct}.
 */
public class SplitConstructTest extends ExpressionBuilderHelper {

	/**
	 * Test the process method under normal circumstances.
	 */
	@Test
	public void processNormalUsage() {
		// Mock
		String stringValue = "Eeny meeny miny mo";
		MetaExpression string = mock(MetaExpression.class);
		when(string.getStringValue()).thenReturn(stringValue);
		when(string.isNull()).thenReturn(false);

		String delimiterValue = " ";
		MetaExpression delimiter = mock(MetaExpression.class);
		when(delimiter.getStringValue()).thenReturn(delimiterValue);
		when(delimiter.isNull()).thenReturn(false);

		boolean keepEmptyValue = false;
		MetaExpression keepEmpty = mock(MetaExpression.class);
		when(keepEmpty.getBooleanValue()).thenReturn(keepEmptyValue);

		String firstValue = "Eeny";
		MetaExpression first = mock(MetaExpression.class);
		when(first.getStringValue()).thenReturn(firstValue);
		String secondValue = "meeny";
		MetaExpression second = mock(MetaExpression.class);
		when(second.getStringValue()).thenReturn(secondValue);
		String thirdValue = "miny";
		MetaExpression third = mock(MetaExpression.class);
		when(third.getStringValue()).thenReturn(thirdValue);
		String fourthValue = "mo";
		MetaExpression fourth = mock(MetaExpression.class);
		when(fourth.getStringValue()).thenReturn(fourthValue);

		String returnValue[] = {firstValue, secondValue, thirdValue, fourthValue, ""};
		StringUtilityService stringService = mock(StringUtilityService.class);
		when(stringService.split(stringValue, delimiterValue)).thenReturn(returnValue);
		// Run
		MetaExpression result = SplitConstruct.process(string, delimiter, keepEmpty, stringService);

		// Verify
		verify(stringService, times(1)).split(stringValue, delimiterValue);

		// Assert
		Assert.assertEquals(result.getType(), LIST);
		@SuppressWarnings("unchecked")
		List<MetaExpression> resultAsList = (List<MetaExpression>) result.getValue();
		Assert.assertEquals(resultAsList.size(), 4);
		Assert.assertEquals(resultAsList.get(0).getStringValue(), firstValue);
		Assert.assertEquals(resultAsList.get(1).getStringValue(), secondValue);
		Assert.assertEquals(resultAsList.get(2).getStringValue(), thirdValue);
		Assert.assertEquals(resultAsList.get(3).getStringValue(), fourthValue);
	}

	/**
	 * Test the process method when we want to keep empty values.
	 */
	@Test
	public void processKeepEmpty() {
		// Mock
		String stringValue = "Eeny meeny miny mo";
		MetaExpression string = mock(MetaExpression.class);
		when(string.getStringValue()).thenReturn(stringValue);
		when(string.isNull()).thenReturn(false);

		String delimiterValue = " ";
		MetaExpression delimiter = mock(MetaExpression.class);
		when(delimiter.getStringValue()).thenReturn(delimiterValue);
		when(delimiter.isNull()).thenReturn(false);

		boolean keepEmptyValue = true;
		MetaExpression keepEmpty = mock(MetaExpression.class);
		when(keepEmpty.getBooleanValue()).thenReturn(keepEmptyValue);

		String firstValue = "Eeny";
		MetaExpression first = mock(MetaExpression.class);
		when(first.getStringValue()).thenReturn(firstValue);
		String secondValue = "meeny";
		MetaExpression second = mock(MetaExpression.class);
		when(second.getStringValue()).thenReturn(secondValue);
		String thirdValue = "miny";
		MetaExpression third = mock(MetaExpression.class);
		when(third.getStringValue()).thenReturn(thirdValue);
		String fourthValue = "mo";
		MetaExpression fourth = mock(MetaExpression.class);
		when(fourth.getStringValue()).thenReturn(fourthValue);

		String returnValue[] = {firstValue, secondValue, thirdValue, fourthValue, ""};
		StringUtilityService stringService = mock(StringUtilityService.class);
		when(stringService.split(stringValue, delimiterValue)).thenReturn(returnValue);
		// Run
		MetaExpression result = SplitConstruct.process(string, delimiter, keepEmpty, stringService);

		// Verify
		verify(stringService, times(1)).split(stringValue, delimiterValue);

		// Assert
		Assert.assertEquals(result.getType(), LIST);
		@SuppressWarnings("unchecked")
		List<MetaExpression> resultAsList = (List<MetaExpression>) result.getValue();
		Assert.assertEquals(resultAsList.size(), 5);
		Assert.assertEquals(resultAsList.get(0).getStringValue(), firstValue);
		Assert.assertEquals(resultAsList.get(1).getStringValue(), secondValue);
		Assert.assertEquals(resultAsList.get(2).getStringValue(), thirdValue);
		Assert.assertEquals(resultAsList.get(3).getStringValue(), fourthValue);
	}

	/**
	 * Test the process when the given values are null.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void processNullValueGiven() {
		// Mock
		String stringValue = "Eeny meeny miny mo";
		MetaExpression string = mock(MetaExpression.class);
		when(string.getStringValue()).thenReturn(stringValue);
		when(string.isNull()).thenReturn(true);

		String delimiterValue = " ";
		MetaExpression delimiter = mock(MetaExpression.class);
		when(delimiter.getStringValue()).thenReturn(delimiterValue);
		when(delimiter.isNull()).thenReturn(true);

		boolean keepEmptyValue = true;
		MetaExpression keepEmpty = mock(MetaExpression.class);
		when(keepEmpty.getBooleanValue()).thenReturn(keepEmptyValue);

		StringUtilityService stringService = mock(StringUtilityService.class);
		SplitConstruct.process(string, delimiter, keepEmpty, stringService);
	}
}
