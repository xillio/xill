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

import org.openqa.selenium.StaleElementReferenceException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * test the {@link ClickConstruct}.
 */
public class ClickConstructTest extends ExpressionBuilderHelper {

	/**
	 * test the construct with normal input. No exceptions should be thrown, element.click is called once and output is NULL.
	 */
	@Test
	public void testProcessNormalUsage() {
		// mock
		MetaExpression input = mock(MetaExpression.class);
		WebService webService = mock(WebService.class);
		NodeVariable nodeVariable = mock(NodeVariable.class);

		when(input.getMeta(NodeVariable.class)).thenReturn(nodeVariable);

		// run
		MetaExpression output = ClickConstruct.process(input, webService);

		// verify
		verify(input, times(2)).getMeta(NodeVariable.class);
		verify(webService, times(1)).click(any());

		// assert
		Assert.assertEquals(output, NULL);
	}

	/**
	 * test the construct when no node is given.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type. NODE type expected!")
	public void testProcessNoNodeGiven() {
		// mock
		// the input
		MetaExpression input = mock(MetaExpression.class);
		WebService webService = mock(WebService.class);

		when(input.getMeta(NodeVariable.class)).thenReturn(null);

		// run
		ClickConstruct.process(input, webService);
	}

	/**
	 * test the construct when the service fails.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Stale element clicked.")
	public void testProcessFailureToClick() {
		// mock
		MetaExpression input = mock(MetaExpression.class);
		WebService webService = mock(WebService.class);
		NodeVariable nodeVariable = mock(NodeVariable.class);

		when(input.getMeta(NodeVariable.class)).thenReturn(nodeVariable);
		doThrow(new StaleElementReferenceException("")).when(webService).click(any());

		// run
		ClickConstruct.process(input, webService);
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
		MetaExpression output = ClickConstruct.process(input, webService);

		// assert
		Assert.assertEquals(output, NULL);
	}
}
