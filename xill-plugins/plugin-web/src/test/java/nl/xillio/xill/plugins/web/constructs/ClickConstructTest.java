package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Mockito.*;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

public class ClickConstructTest extends ExpressionBuilderHelper {

	/**
	 * test the construct with normal input. No exceptions should be thrown, element.click is called once and output is NULL.
	 */
	@Test
	public void testProcessNormalUsage(){
		// mock
		//the input
		MetaExpression input = mock(MetaExpression.class);
		WebService webService = mock(WebService.class);
		//NodeVariable nodeVariable = mock(NodeVariable.class);
		
		boolean isNode = true;
		when(NodeVariable.checkType(input)).thenReturn(isNode);


		// run
		MetaExpression output = ClickConstruct.process(input, webService);

		// verify
		verify(NodeVariable.checkType(input), times(1));
		verify(NodeVariable.get(input), times(1));
		verify(webService, times(1)).click(any());

		// assert
		Assert.assertEquals(output, NULL);
	}
	
	/**
	 * test the construct when no node is given.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type. NODE type expected!")
	public void testProcessNoNodeGiven(){
		// mock
		//the input
		MetaExpression input = mock(MetaExpression.class);
		WebService webService = mock(WebService.class);
		//NodeVariable nodeVariable = mock(NodeVariable.class);
		
		boolean isNode = false;
		when(NodeVariable.checkType(input)).thenReturn(isNode);


		// run
		ClickConstruct.process(input, webService);

		// verify
		verify(NodeVariable.checkType(input), times(1));
		verify(NodeVariable.get(input), times(0));
		verify(webService, times(0)).click(any());
	}
	
	/**
	 * test the construct when the service fails.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "StaleElementClicked.")
	public void testProcessFailureToClick(){
		// mock
		//the input
		MetaExpression input = mock(MetaExpression.class);
		WebService webService = mock(WebService.class);
		//NodeVariable nodeVariable = mock(NodeVariable.class);
		
		boolean isNode = true;
		when(NodeVariable.checkType(input)).thenReturn(isNode);
		doThrow(new StaleElementReferenceException("")).when(webService).click(any());

		// run
		ClickConstruct.process(input, webService);

		// verify
		verify(NodeVariable.checkType(input), times(1));
		verify(NodeVariable.get(input), times(0));
		verify(webService, times(0)).click(any());
	}
}
