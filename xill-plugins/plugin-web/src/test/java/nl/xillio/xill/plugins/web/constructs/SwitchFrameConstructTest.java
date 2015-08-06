package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.PhantomJSPool;
import nl.xillio.xill.plugins.web.services.web.FileService;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *Test the {@link SwithToFrameConstruct}.
 */
public class SwitchFrameConstructTest extends ExpressionBuilderHelper {
	
	/**
	 * Test the process under normal circumstances with a webelement given.
	 */
	@Test
	public void testProcessWithWebElement(){
			// mock
			WebService webService = mock(WebService.class);
			
			//the page
			PhantomJSPool.Entity pageVariable = mock(PhantomJSPool.Entity.class);
			WebDriver driver = mock(WebDriver.class);
			MetaExpression page = mock(MetaExpression.class);
			when(page.getMeta(PhantomJSPool.Entity.class)).thenReturn(pageVariable);
			when(pageVariable.getDriver()).thenReturn(driver);
			
			//The element
			NodeVariable nodeVariable = mock(NodeVariable.class);
			WebElement element = mock(WebElement.class);
			MetaExpression node = mock(MetaExpression.class);
			when(node.getMeta(NodeVariable.class)).thenReturn(nodeVariable);
			when(nodeVariable.getElement()).thenReturn(element);
		
			
			// run
			MetaExpression output = SwitchFrameConstruct.process(page, node, webService);
			
			// verify
			verify(page, times(2)).getMeta(PhantomJSPool.Entity.class);
			verify(pageVariable, times(1)).getDriver();
			verify(node, times(2)).getMeta(NodeVariable.class);
			verify(nodeVariable, times(1)).getElement();
			verify(webService, times(1)).switchToFrame(driver, element);
			
			// assert
			Assert.assertEquals(output, NULL);
	}
	
	/**
	 * Test the process under normal circumstances with an integer given.
	 */
	@Test
	public void testProcessWithInteger(){
			// mock
			WebService webService = mock(WebService.class);
			
			//the page
			PhantomJSPool.Entity pageVariable = mock(PhantomJSPool.Entity.class);
			WebDriver driver = mock(WebDriver.class);
			MetaExpression page = mock(MetaExpression.class);
			when(page.getMeta(PhantomJSPool.Entity.class)).thenReturn(pageVariable);
			when(pageVariable.getDriver()).thenReturn(driver);
			
			
			//The frame
			int frameValue = 42;
			MetaExpression frame = mock(MetaExpression.class);
			when(frame.getValue()).thenReturn(frameValue);
			when(frame.getMeta(NodeVariable.class)).thenReturn(null);
			when(frame.getType()).thenReturn(ATOMIC);
			when(frame.getNumberValue()).thenReturn(frameValue);

			
			// run
			MetaExpression output = SwitchFrameConstruct.process(page, frame, webService);
			
			// verify
			verify(page, times(2)).getMeta(PhantomJSPool.Entity.class);
			verify(pageVariable, times(1)).getDriver();
			verify(frame, times(1)).getValue();
			verify(webService, times(1)).switchToFrame(driver, frameValue);
			
			// assert
			Assert.assertEquals(output, NULL);
	}
	
	/**
	 * Test the process under normal circumstances with a String given.
	 */
	@Test
	public void testProcessWithString(){
			// mock
			WebService webService = mock(WebService.class);
			
			//the page
			PhantomJSPool.Entity pageVariable = mock(PhantomJSPool.Entity.class);
			WebDriver driver = mock(WebDriver.class);
			MetaExpression page = mock(MetaExpression.class);
			when(page.getMeta(PhantomJSPool.Entity.class)).thenReturn(pageVariable);
			when(pageVariable.getDriver()).thenReturn(driver);
			
			//The frame
			String frameValue = "frame as a String";
			MetaExpression frame = mock(MetaExpression.class);
			when(frame.getValue()).thenReturn(frameValue);
			when(frame.getMeta(NodeVariable.class)).thenReturn(null);
			when(frame.getType()).thenReturn(ATOMIC);
			when(frame.getStringValue()).thenReturn(frameValue);
			when(frame.getNumberValue()).thenReturn(Double.NaN);
			
			// run
			MetaExpression output = SwitchFrameConstruct.process(page, frame, webService);
			
			// verify
			verify(page, times(2)).getMeta(PhantomJSPool.Entity.class);
			verify(pageVariable, times(1)).getDriver();
			verify(frame, times(1)).getValue();
			verify(webService, times(1)).switchToFrame(driver, frameValue);
			
			// assert
			Assert.assertEquals(output, NULL);
	}

}
