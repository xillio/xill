package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class InputConstructTest extends ExpressionBuilderHelper {
	
	/**
	 * Tests the construct under normal circumstances
	 */
	@Test
	public void testProcessNormalUsage(){
		// mock
		WebService webService = mock(WebService.class);
		
		//The input
		MetaExpression input = mock(MetaExpression.class);
		NodeVariable nodeVariable = mock(NodeVariable.class);
		WebElement element = mock(WebElement.class);
		
		//The text input
		MetaExpression text = mock(MetaExpression.class);
		
		when(input.getMeta(NodeVariable.class)).thenReturn(nodeVariable);
		when(nodeVariable.getElement()).thenReturn(element);
		when(text.getStringValue()).thenReturn("Text");


		// run
		MetaExpression output = InputConstruct.process(input, text, webService);

		// verify
		verify(input, times(2)).getMeta(NodeVariable.class);
		verify(nodeVariable, times(1)).getElement();
		verify(webService, times(1)).clear(element);
		verify(webService, times(1)).sendKeys(element, "Text");

		// assert
		Assert.assertEquals(output, NULL);
	}
	
	/**
	 * Test the process when no node was in the expression.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type. NODE type expected!")
	public void testProcessNoNodeGiven(){
		// mock
		WebService webService = mock(WebService.class);
		
		//The input
		MetaExpression input = mock(MetaExpression.class);
		
		//The text input
		MetaExpression text = mock(MetaExpression.class);
		
		when(input.getMeta(NodeVariable.class)).thenReturn(null);

		// run
		MetaExpression output = InputConstruct.process(input, text, webService);

		// verify
		verify(input, times(2)).getMeta(NodeVariable.class);

		// assert
		Assert.assertEquals(output, NULL);
	}

}
