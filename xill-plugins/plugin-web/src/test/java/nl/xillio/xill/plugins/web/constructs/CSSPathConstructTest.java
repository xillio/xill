package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.SearchContext;
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

/**
 * Tests the {@link CSSPathConstruct}.
 *
 */
public class CSSPathConstructTest extends ExpressionBuilderHelper {

	/**
	 * test the construct with normal input. 
	 * 	 
	 */
	@Test
	public void testNormalNodeUsage(){
		//mock
		MetaExpression element = mock(MetaExpression.class);
		MetaExpression cssPath = mock(MetaExpression.class);
		WebService webService = mock(WebService.class);
		NodeVariable nodeVariable = mock(NodeVariable.class);
		WebElement webElement = mock(WebElement.class);
		WebDriver webDriver = mock(WebDriver.class);
		
		WebElement firstResult = mock(WebElement.class);
		WebElement secondResult = mock(WebElement.class);
		String query = "cssPath";
		
		when(cssPath.getStringValue()).thenReturn(query);
		when(element.getMeta(NodeVariable.class)).thenReturn(nodeVariable);
		when(nodeVariable.getElement()).thenReturn(webElement);
		when(nodeVariable.getDriver()).thenReturn(webDriver);
		when(webService.findElements(webElement, query)).thenReturn(Arrays.asList(firstResult, secondResult));
		when(webService.getAttribute(eq(firstResult), anyString())).thenReturn("first");
		when(webService.getAttribute(eq(secondResult), anyString())).thenReturn("second");
		
		//run
		MetaExpression output = CSSPathConstruct.process(element, cssPath, webService);
		
		//verify
		verify(cssPath, times(1)).getStringValue();
		verify(element, times(3)).getMeta(NodeVariable.class);
		verify(nodeVariable, times(1)).getElement();
		verify(nodeVariable, times(1)).getDriver();
		verify(webService, times(1)).findElements(webElement, query);
		verify(webService, times(1)).getAttribute(eq(firstResult), anyString());
		verify(webService, times(1)).getAttribute(eq(secondResult), anyString());
		
		//assert
		Assert.assertEquals(output.getType(), LIST);
		@SuppressWarnings("unchecked")
		List<MetaExpression> result = (List<MetaExpression>) output.getValue();
		Assert.assertEquals(result.size(), 2);
	}
	
	/**
	 * test the construct when a single resultvalue is returned.
	 */
	@Test
	public void testProcessSingleResultValue() {
		//mock
		MetaExpression element = mock(MetaExpression.class);
		MetaExpression cssPath = mock(MetaExpression.class);
		WebService webService = mock(WebService.class);
		NodeVariable nodeVariable = mock(NodeVariable.class);
		WebElement webElement = mock(WebElement.class);
		WebDriver webDriver = mock(WebDriver.class);
		
		WebElement firstResult = mock(WebElement.class);
		String query = "cssPath";
		
		when(cssPath.getStringValue()).thenReturn(query);
		when(element.getMeta(NodeVariable.class)).thenReturn(nodeVariable);
		when(nodeVariable.getElement()).thenReturn(webElement);
		when(nodeVariable.getDriver()).thenReturn(webDriver);
		when(webService.findElements(webElement, query)).thenReturn(Arrays.asList(firstResult));
		when(webService.getAttribute(eq(firstResult), anyString())).thenReturn("first");
		
		//run
		MetaExpression output = CSSPathConstruct.process(element, cssPath, webService);
		
		//verify
		verify(cssPath, times(1)).getStringValue();
		verify(element, times(3)).getMeta(NodeVariable.class);
		verify(nodeVariable, times(1)).getElement();
		verify(nodeVariable, times(1)).getDriver();
		verify(webService, times(1)).findElements(webElement, query);
		verify(webService, times(1)).getAttribute(eq(firstResult), anyString());
		
		//assert
		Assert.assertEquals(output.getType(), ATOMIC);
	}
	
	@Test
	public void testProcessNoValueFound(){
		//mock
		MetaExpression element = mock(MetaExpression.class);
		MetaExpression cssPath = mock(MetaExpression.class);
		WebService webService = mock(WebService.class);
		NodeVariable nodeVariable = mock(NodeVariable.class);
		WebElement webElement = mock(WebElement.class);
		WebDriver webDriver = mock(WebDriver.class);

		WebElement secondResult = mock(WebElement.class);
		String query = "cssPath";
		
		when(cssPath.getStringValue()).thenReturn(query);
		when(element.getMeta(NodeVariable.class)).thenReturn(nodeVariable);
		when(nodeVariable.getElement()).thenReturn(webElement);
		when(nodeVariable.getDriver()).thenReturn(webDriver);
		when(webService.findElements(webElement, query)).thenReturn(Arrays.asList());

		//run
		MetaExpression output = CSSPathConstruct.process(element, cssPath, webService);
		
		//verify
		verify(cssPath, times(1)).getStringValue();
		verify(element, times(3)).getMeta(NodeVariable.class);
		verify(nodeVariable, times(1)).getElement();
		verify(nodeVariable, times(1)).getDriver();
		verify(webService, times(1)).findElements(webElement, query);
		verify(webService, times(0)).getAttribute(any(), anyString());
		
		//assert
		Assert.assertEquals(output, NULL);
	}
}
