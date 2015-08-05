package nl.xillio.xill.plugins.web.constructs;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.PhantomJSPool;
import nl.xillio.xill.plugins.web.services.web.FileService;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the {@link SelectConstruct}
 *
 */
public class SelectConstructTest extends ExpressionBuilderHelper {
	
	/**
	 * Test the process under normal circumstances.
	 */
	@Test
	public void testProcessNormalUsage(){
			// mock
			WebService webService = mock(WebService.class);
			
			//The element
			NodeVariable nodeVariable = mock(NodeVariable.class);
			WebElement element = mock(WebElement.class);
			MetaExpression node = mock(MetaExpression.class);
			when(node.getMeta(NodeVariable.class)).thenReturn(nodeVariable);
			when(nodeVariable.getElement()).thenReturn(element);
			
			//The select boolean
			MetaExpression select = mock(MetaExpression.class);
			when(select.getBooleanValue()).thenReturn(true);
			
			//the process
			when(webService.isSelected(element)).thenReturn(false);
			
			// run
			MetaExpression output = SelectConstruct.process(node, select, webService);
			
			// verify
			verify(webService, times(1)).isSelected(element);
			verify(webService, times(1)).click(element);
			
			// assert
			Assert.assertEquals(output, NULL);
	}
	
	/**
	 * Tests the process when there is no need to take action.
	 */
	@Test
	public void testProcessNoNeedToClick(){
			// mock
			WebService webService = mock(WebService.class);
			
			//The element
			NodeVariable nodeVariable = mock(NodeVariable.class);
			WebElement element = mock(WebElement.class);
			MetaExpression node = mock(MetaExpression.class);
			when(node.getMeta(NodeVariable.class)).thenReturn(nodeVariable);
			when(nodeVariable.getElement()).thenReturn(element);
			
			//The select boolean
			MetaExpression select = mock(MetaExpression.class);
			when(select.getBooleanValue()).thenReturn(true);
			
			//the process
			when(webService.isSelected(element)).thenReturn(true);
			
			// run
			MetaExpression output = SelectConstruct.process(node, select, webService);
			
			// verify
			verify(webService, times(1)).isSelected(element);
			verify(webService, times(0)).click(element);
			
			// assert
			Assert.assertEquals(output, NULL);
	}
	
	/**
	 * Tests the process when no node is given.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type. NODE type expected!")
	public void testProcessNoNodeGiven(){
			// mock
			WebService webService = mock(WebService.class);
			
			//The element
			MetaExpression node = mock(MetaExpression.class);
			when(node.getMeta(NodeVariable.class)).thenReturn(null);
			
			//The select boolean
			MetaExpression select = mock(MetaExpression.class);
			
			// run
			SelectConstruct.process(node, select, webService);
	}
	
	/**
	 * Tests the process when the WebService breaks.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to access NODE correctly")
	public void testProcessWebServiceFailure(){
		// mock
		WebService webService = mock(WebService.class);
		
		//The element
		NodeVariable nodeVariable = mock(NodeVariable.class);
		WebElement element = mock(WebElement.class);
		MetaExpression node = mock(MetaExpression.class);
		when(node.getMeta(NodeVariable.class)).thenReturn(nodeVariable);
		when(nodeVariable.getElement()).thenReturn(element);
		
		//The select boolean
		MetaExpression select = mock(MetaExpression.class);
		when(select.getBooleanValue()).thenReturn(false);
		
		//the process
		when(webService.isSelected(element)).thenThrow(new RobotRuntimeException("I broke."));
		
		// run
		SelectConstruct.process(node, select, webService);
}

}
