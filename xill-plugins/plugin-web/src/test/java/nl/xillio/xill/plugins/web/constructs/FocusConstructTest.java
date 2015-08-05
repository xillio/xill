package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
		// the input
		MetaExpression input = mock(MetaExpression.class);
		NodeVariable nodeVariable = mock(NodeVariable.class);
		WebElement element = mock(WebElement.class);
		WebDriver page = mock(WebDriver.class);
		WebService webService = mock(WebService.class);


		when(input.getMeta(NodeVariable.class)).thenReturn(nodeVariable);
		when(nodeVariable.getElement()).thenReturn(element);
		when(nodeVariable.getDriver()).thenReturn(page);

		// run
		MetaExpression output = FocusConstruct.process(input, webService);

		// verify
		verify(input, times(3)).getMeta(NodeVariable.class);
		verify(nodeVariable, times(1)).getElement();
		verify(nodeVariable, times(1)).getDriver();
		verify(webService, times(1)).moveToElement(page, element);

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
		WebElement element = mock(WebElement.class);
		WebDriver page = mock(WebDriver.class);
		WebService webService = mock(WebService.class);

		boolean isNode = false;
		when(input.getMeta(NodeVariable.class)).thenReturn(null);
		// run
		FocusConstruct.process(input, webService);

		// verify
		verify(input, times(1)).getMeta(NodeVariable.class);
	}
}
