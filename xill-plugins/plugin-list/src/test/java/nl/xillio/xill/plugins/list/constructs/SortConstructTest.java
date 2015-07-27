package nl.xillio.xill.plugins.list.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.list.services.sort.Sort;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * Test the {@link SortConstruct}
 *
 * @author Sander Visser
 *
 */
public class SortConstructTest extends ExpressionBuilderHelper {

	/**
	 * Test the process method under normal circumstances.
	 */
	@Test
	public void testProcessWithNormalInput() {

		// mock
		String expectedOutput = "This is the expected output";
		MetaExpression outputExpression = fromValue(expectedOutput);

		Sort sort = mock(Sort.class);
		when(sort.asSorted(extractValue(outputExpression), true, true)).thenReturn(true);

		MetaExpression recursive = mock(MetaExpression.class);
		when(recursive.getBooleanValue()).thenReturn(true);

		MetaExpression onKeys = mock(MetaExpression.class);
		when(onKeys.getBooleanValue()).thenReturn(true);

		// run
		MetaExpression output = SortConstruct.process(outputExpression, recursive, onKeys, sort);

		// verify
		verify(sort, times(1)).asSorted(extractValue(outputExpression), true, true);
		verify(recursive, times(1)).getBooleanValue();
		verify(onKeys, times(1)).getBooleanValue();

		// assert
		Assert.assertEquals(output, TRUE);
	}
}
