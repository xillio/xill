package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.NodeVariable;

public class ClickConstructTest extends ExpressionBuilderHelper {

	/**
	 * test the construct with normal input. No exceptions should be thrown, element.click is called once and output is NULL.
	 */
	@Test
	public void testProcessInputNoExceptions() {
		// mock
		WebDriver driver = mock(WebDriver.class);
		WebElement element = mock(WebElement.class);
		MetaExpression input = mock(MetaExpression.class);
		MetaExpression meta = NodeVariable.create(driver, element);

		when(NodeVariable.checkType(input)).thenReturn(null);
		when(input.getMeta(String.class)).thenReturn("Selenium:node");
		when(input.getMeta(NodeVariable.class)).thenReturn(meta.getMeta(NodeVariable.class));

		// run
		MetaExpression output = ClickConstruct.process(input);

		// verify
		verify(element, times(1)).click();

		verify(input, times(1)).getMeta(String.class);
		verify(input, times(2)).getMeta(NodeVariable.class);

		// assert
		Assert.assertSame(output, NULL);

	}

	/**
	 * test the construct when the input is not a NODE. should throw an RobotRunTimeException.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type. NODE type expected!")
	public void testProcessNotANodeException() {

		// mock
		MetaExpression input = mock(MetaExpression.class);

		when(NodeVariable.checkType(input)).thenReturn(null);
		when(input.getMeta(String.class)).thenReturn(null);
		when(input.getMeta(NodeVariable.class)).thenReturn(null);

		ClickConstruct.process(input);
	}
}
