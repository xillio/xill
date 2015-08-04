package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.NodeVariableService;
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
		WebElement element = mock(WebElement.class);
		WebDriver page = mock(WebDriver.class);
		WebService webService = mock(WebService.class);
		NodeVariableService nodeVariableService = mock(NodeVariableService.class);


		boolean isNode = true;
		when(nodeVariableService.checkType(input)).thenReturn(isNode);
		when(nodeVariableService.get(input)).thenReturn(element);
		when(nodeVariableService.getDriver(input)).thenReturn(page);

		// run
		MetaExpression output = FocusConstruct.process(input, webService, nodeVariableService);

		// verify
		verify(nodeVariableService, times(1)).checkType(input);
		verify(nodeVariableService, times(1)).get(input);
		verify(nodeVariableService, times(1)).getDriver(input);
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
		NodeVariableService nodeVariableService = mock(NodeVariableService.class);

		boolean isNode = false;
		when(nodeVariableService.checkType(input)).thenReturn(isNode);
		when(nodeVariableService.get(input)).thenReturn(element);
		when(nodeVariableService.getDriver(input)).thenReturn(page);

		// run
		FocusConstruct.process(input, webService, nodeVariableService);

		// verify
		verify(nodeVariableService, times(1)).checkType(input);
		verify(nodeVariableService, times(0)).get(input);
		verify(nodeVariableService, times(0)).getDriver(input);
		verify(webService, times(0)).moveToElement(page, element);
	}
}
