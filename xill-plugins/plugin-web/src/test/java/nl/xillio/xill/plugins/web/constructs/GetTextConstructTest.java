package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.PhantomJSPool;
import nl.xillio.xill.plugins.web.services.web.WebService;

/**
 * test the {@link GetTextConstruct}.
 *
 */
public class GetTextConstructTest extends ExpressionBuilderHelper {

	/**
	 * test the construct with normal input.
	 * The input is a list with two values. One containing a NODE and with an input tag.
	 * The other a PAGE with a textarea tag.
	 */
	@Test
	public void testProcessNormalUsage(){
		// mock
		WebService webService = mock(WebService.class);
		
		//the first element in the list and what it uses
		MetaExpression first = mock(MetaExpression.class);
		NodeVariable nodeVariable = mock(NodeVariable.class);
		WebDriver firstDriver = mock(WebDriver.class);
		WebElement firstElement = mock(WebElement.class);

		//the second element in the list and what it uses
		MetaExpression second = mock(MetaExpression.class);
		PhantomJSPool.Entity pageVariable = mock(PhantomJSPool.Entity.class);
		WebDriver secondDriver = mock(WebDriver.class);
		WebElement secondElement = mock(WebElement.class);
		
		//the process method
		MetaExpression elementList = mock(MetaExpression.class);
		when(elementList.isNull()).thenReturn(false);
		when(elementList.getType()).thenReturn(LIST);
		when(elementList.getValue()).thenReturn(Arrays.asList(first, second));
		
		//the processItem method for the first variable
		when(first.getMeta(NodeVariable.class)).thenReturn(nodeVariable);
		when(nodeVariable.getDriver()).thenReturn(firstDriver);
		when(nodeVariable.getElement()).thenReturn(firstElement);
		when(webService.getTagName(firstElement)).thenReturn("input");
		when(webService.getAttribute(eq(firstElement), anyString())).thenReturn("pet");

		//the processItem method for the second variable
		when(second.getMeta(NodeVariable.class)).thenReturn(null);
		when(second.getMeta(PhantomJSPool.Entity.class)).thenReturn(pageVariable);
		when(pageVariable.getDriver()).thenReturn(secondDriver);
		when(webService.driverToElement(secondDriver)).thenReturn(secondElement);
		when(webService.getTagName(secondElement)).thenReturn("textarea");
		when(webService.getAttribute(eq(secondElement), anyString())).thenReturn("master");

		// run
		MetaExpression output = GetTextConstruct.process(elementList, webService);

		// verify
		
		//the process method
		verify(elementList, times(1)).isNull();
		verify(elementList, times(1)).getType();
		verify(elementList, times(1)).getValue();
		
		//the processItemMethod for the first variable
		verify(first, times(2)).getMeta(NodeVariable.class);
		verify(nodeVariable, times(1)).getElement();
		verify(webService, times(1)).getTagName(firstElement);
		verify(webService, times(1)).getAttribute(eq(firstElement), anyString());
		
		//the processItemMethod for the second variable
		verify(second, times(1)).getMeta(NodeVariable.class);
		verify(second, times(2)).getMeta(PhantomJSPool.Entity.class);
		verify(pageVariable, times(1)).getDriver();
		verify(webService, times(1)).driverToElement(secondDriver);
		verify(webService, times(2)).getTagName(secondElement);
		verify(webService, times(1)).getAttribute(eq(secondElement), anyString());

		//We hope not to hit this
		verify(webService, times(0)).getText(any());

		// assert
		Assert.assertEquals(output.getStringValue(), "petmaster");
	}
	
	/**
	 * test the construct with normal input.
	 * The input is a ATOMIC object with no required tag.
	 */
	@Test
	public void testProcessNoListWithNoTag(){
		// mock
		//the input
		WebService webService = mock(WebService.class);
		MetaExpression first = mock(MetaExpression.class);
		NodeVariable nodeVariable = mock(NodeVariable.class);
		PhantomJSPool.Entity pageVariable = mock(PhantomJSPool.Entity.class);
		WebDriver firstDriver = mock(WebDriver.class);
		WebElement firstElement = mock(WebElement.class);
		
		//the process method
		when(first.isNull()).thenReturn(false);
		when(first.getType()).thenReturn(ATOMIC);
		
		//the processItem method for the first variable
		when(first.getMeta(NodeVariable.class)).thenReturn(nodeVariable);
		when(nodeVariable.getDriver()).thenReturn(firstDriver);
		when(nodeVariable.getElement()).thenReturn(firstElement);
		when(webService.getTagName(firstElement)).thenReturn("No Input or TextArea");
		when(webService.getText(eq(firstElement))).thenReturn("pet");

		// run
		MetaExpression output = GetTextConstruct.process(first, webService);

		// verify
		
		//the process method
		verify(first, times(1)).isNull();
		verify(first, times(1)).getType();
		verify(first, times(0)).getValue();
		
		//the processItemMethod for the first variable
		verify(first, times(2)).getMeta(NodeVariable.class);
		verify(nodeVariable, times(0)).getDriver();
		verify(nodeVariable, times(1)).getElement();
		verify(webService, times(2)).getTagName(firstElement);
		verify(webService, times(0)).getAttribute(any(), anyString());
		verify(webService, times(1)).getText(firstElement);

		// assert
		Assert.assertEquals(output.getStringValue(), "pet");
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
		when(input.getMeta(NodeVariable.class)).thenReturn(null);


		// run
		ClickConstruct.process(input, webService);
	}
}
