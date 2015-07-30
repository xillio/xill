package nl.xillio.xill.plugins.list.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.NotImplementedException;

/**
 *
 * Test the {@link ContainsValueConstruct}
 *
 * @author Sander Visser
 *
 */
public class ContainsValueConstructTest extends ExpressionBuilderHelper {

	/**
	 * Test the process method under normal circumstances with list input.
	 */
	@Test
	public void testProcessCheckExistingValueInList() {

		@SuppressWarnings("unchecked")
		ArrayList<MetaExpression> list = mock(ArrayList.class);

		// mock
		MetaExpression input = mock(MetaExpression.class);
		MetaExpression value = mock(MetaExpression.class);
		MetaExpression expectedOutput = TRUE;
		when(input.getType()).thenReturn(LIST);
		when(input.getValue()).thenReturn(list);
		when(list.contains(value)).thenReturn(true);

		// run
		MetaExpression output = ContainsValueConstruct.process(input, value);

		// verify
		verify(list, times(1)).contains(value);
		verify(input, times(1)).getType();
		verify(input, times(1)).getValue();

		// assert
		Assert.assertEquals(output, expectedOutput);

	}

	/**
	 * Test the process method under normal circumstances with object input.
	 */
	@Test
	public void testProcessCheckExistingValueInObject() {

		@SuppressWarnings("unchecked")
		Map<String, MetaExpression> obj = mock(Map.class);

		// mock
		MetaExpression input = mock(MetaExpression.class);
		MetaExpression value = mock(MetaExpression.class);
		MetaExpression expectedOutput = TRUE;
		when(input.getType()).thenReturn(OBJECT);
		when(input.getValue()).thenReturn(obj);
		when(obj.containsValue(value)).thenReturn(true);

		// run
		MetaExpression output = ContainsValueConstruct.process(input, value);

		// verify
		verify(obj, times(1)).containsValue(value);
		verify(input, times(1)).getType();
		verify(input, times(1)).getValue();

		// assert
		Assert.assertEquals(output, expectedOutput);

	}

	/**
	 * Test the process method with not implemented type
	 * (since the type has not been implemented, we use ATOMIC type to check.
	 *
	 *
	 * @throws Throwable
	 *         while testing
	 */
	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = "This type is not implemented.")
	public void testProcessInvalidTypeCheck() throws Throwable {

		@SuppressWarnings("unchecked")
		ArrayList<MetaExpression> list = mock(ArrayList.class);

		// mock
		MetaExpression input = mock(MetaExpression.class);
		MetaExpression value = mock(MetaExpression.class);
		when(input.getType()).thenReturn(ATOMIC);
		when(input.getValue()).thenReturn(list);
		when(list.contains(value)).thenReturn(true);

		ContainsValueConstruct.process(input, value);

	}

}
