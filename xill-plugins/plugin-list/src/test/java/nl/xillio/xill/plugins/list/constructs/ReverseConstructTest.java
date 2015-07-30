package nl.xillio.xill.plugins.list.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.testng.Assert;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.list.services.reverse.Reverse;

/**
 *
 * Test the {@link ReverseConstruct}
 *
 * @author Sander Visser
 *
 */
public class ReverseConstructTest extends ExpressionBuilderHelper {

	/**
	 * Test the process method under normal circumstances.
	 */
	@Test
	public void testProcessWithNormalInput() {

		// mock

		Reverse reverse = mock(Reverse.class);
		when(reverse.asReversed(null, true)).thenReturn(true);
		
		MetaExpression recursive = mock(MetaExpression.class);
		when(recursive.getBooleanValue()).thenReturn(true);

		// run
		MetaExpression output = ReverseConstruct.process(NULL, recursive, reverse);

		// verify
		verify(reverse, times(1)).asReversed(null, true);
		verify(recursive, times(1)).getBooleanValue();

		// assert
		Assert.assertEquals(output, TRUE);
	}
}
