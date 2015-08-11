package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.NodeVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * The unit tests for the {@link FocusConstruct}.
 *
 */
public class FocusConstructTest extends ExpressionBuilderHelper {

	/**
	 * test the construct with normal input. No exceptions should be thrown, element.click is called once and output is NULL.
	 */
	@Test
	public void testProcessNormalUsage() {
		// mock
		WebService webService = mock(WebService.class);

		// The input
		MetaExpression element = mock(MetaExpression.class);
		NodeVariable nodeVariable = mock(NodeVariable.class);
		when(element.getMeta(NodeVariable.class)).thenReturn(nodeVariable);

		// run
		MetaExpression output = FocusConstruct.process(element, webService);

		// verify
		verify(element, times(2)).getMeta(NodeVariable.class);
		verify(webService, times(1)).moveToElement(nodeVariable);

		// assert
		Assert.assertEquals(output, NULL);
	}

	/**
	 * Test the process with null input given.
	 */
	@Test
	public void testNullInput() {
		// mock
		WebService webService = mock(WebService.class);
		MetaExpression input = mock(MetaExpression.class);
		when(input.isNull()).thenReturn(true);

		// run
		MetaExpression output = FocusConstruct.process(input, webService);

		// assert
		Assert.assertEquals(output, NULL);
	}

	/**
	 * test the construct when no node is given.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type. NODE type expected!")
	public void testProcessNoNodeGiven() {
		// mock
		WebService webService = mock(WebService.class);

		// The input
		MetaExpression element = mock(MetaExpression.class);
		when(element.getMeta(NodeVariable.class)).thenReturn(null);

		// run
		FocusConstruct.process(element, webService);

		// verify
		verify(element, times(1)).getMeta(NodeVariable.class);
	}

	/**
	 * test the process when the webService fails.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to focus on element.")
	public void testProcessFailedToFocus() {
		// mock
		WebService webService = mock(WebService.class);

		// The input
		NodeVariable nodeVariable = mock(NodeVariable.class);
		MetaExpression element = mock(MetaExpression.class);
		when(element.getMeta(NodeVariable.class)).thenReturn(nodeVariable);

		doThrow(new RobotRuntimeException("Error")).when(webService).moveToElement(any());

		// run
		FocusConstruct.process(element, webService);

		// verify
		verify(element, times(1)).getMeta(NodeVariable.class);
	}
}
