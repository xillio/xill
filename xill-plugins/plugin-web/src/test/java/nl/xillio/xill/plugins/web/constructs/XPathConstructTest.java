package nl.xillio.xill.plugins.web.constructs;


import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.PhantomJSPool;
import nl.xillio.xill.plugins.web.services.web.FileService;
import nl.xillio.xill.plugins.web.services.web.StringService;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *Test the {@link XPathConstruct}
 *
 */
public class XPathConstructTest extends ExpressionBuilderHelper {
	
	/**
	 * tests the process under normal circumstances with a page given.
	 * We have a text query and findElements only returns one item.
	 */
	@Test
	public void testNormalUsageWithPageAndTextQueryAndOneElement(){
		// mock
		WebService webService = mock(WebService.class);
		StringService stringService = mock(StringService.class);
		
		//The page
		PhantomJSPool.Entity pageVariable = mock(PhantomJSPool.Entity.class);
		WebDriver driver = mock(WebDriver.class);
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PhantomJSPool.Entity.class)).thenReturn(pageVariable);
		when(pageVariable.getDriver()).thenReturn(driver);
		
		//The xpath
		String xPathValue = "xpath";
		String shortenedXPathValue = "shorter xpath";
		MetaExpression xpath = mock(MetaExpression.class);
		when(xpath.getStringValue()).thenReturn(xPathValue);
		
		//The WebElement
		WebElement element = mock(WebElement.class);
		String elementText = "the text in the attribute of the element";
		
		//process SEL node
		when(stringService.endsWith(xPathValue, "/text()")).thenReturn(true);
		when(stringService.matches(xPathValue, "^.*@\\w+$")).thenReturn(false);
		when(stringService.subString(xPathValue, 0, xPathValue.length() - 7)).thenReturn(shortenedXPathValue);
		when(webService.findElementsWithXpath(driver, shortenedXPathValue)).thenReturn(Arrays.asList(element));
		when(webService.getAttribute(element, "innerHTML")).thenReturn(elementText);
		
		// run
		MetaExpression output = XPathConstruct.process(page, xpath, stringService, webService);
		
		// verify
		verify(stringService, times(1)).endsWith(xPathValue, "/text()");
		verify(stringService, times(1)).matches(xPathValue, "^.*@\\w+$");
		verify(stringService, times(1)).subString(xPathValue, 0, xPathValue.length() - 7);
		verify(stringService, times(0)).lastIndexOf(anyString(), anyChar());
		verify(webService, times(1)).getAttribute(eq(element), anyString());
		
		// assert
		Assert.assertEquals(output.getStringValue(), elementText);
	}
	
	/**
	 * tests the process under normal circumstances with a node given.
	 * We have a text query and findElements returns more items.
	 */
	@Test
	public void testNormalUsageWithNodeAndTextQueryAndMoreElements(){
		// mock
		WebService webService = mock(WebService.class);
		StringService stringService = mock(StringService.class);
		
		//The node
		NodeVariable nodevariable = mock(NodeVariable.class);
		WebDriver driver = mock(WebDriver.class);
		WebElement element = mock(WebElement.class);
		MetaExpression node = mock(MetaExpression.class);
		when(node.getMeta(PhantomJSPool.Entity.class)).thenReturn(null);
		when(node.getMeta(NodeVariable.class)).thenReturn(nodevariable);
		when(nodevariable.getDriver()).thenReturn(driver);
		when(nodevariable.getElement()).thenReturn(element);
		
		//The xpath
		String xPathValue = "xpath";
		String shortenedXPathValue = "shorter xpath";
		MetaExpression xpath = mock(MetaExpression.class);
		when(xpath.getStringValue()).thenReturn(xPathValue);
		
		//The WebElements
		WebElement element1 = mock(WebElement.class);
		String element1Text = "element1";
		WebElement element2 = mock(WebElement.class);
		String element2Text = "element2";
		
		//process SEL node
		when(stringService.endsWith(xPathValue, "/text()")).thenReturn(true);
		when(stringService.matches(xPathValue, "^.*@\\w+$")).thenReturn(false);
		when(stringService.subString(xPathValue, 0, xPathValue.length() - 7)).thenReturn(shortenedXPathValue);
		when(webService.findElementsWithXpath(element, shortenedXPathValue)).thenReturn(Arrays.asList(element1, element2));
		when(webService.getAttribute(element1, "innerHTML")).thenReturn(element1Text);
		when(webService.getAttribute(element2, "innerHTML")).thenReturn(element2Text);
		
		// run
		MetaExpression output = XPathConstruct.process(node, xpath, stringService, webService);
		
		// verify
		verify(stringService, times(1)).endsWith(xPathValue, "/text()");
		verify(stringService, times(1)).matches(xPathValue, "^.*@\\w+$");
		verify(stringService, times(1)).subString(xPathValue, 0, xPathValue.length() - 7);
		verify(stringService, times(0)).lastIndexOf(anyString(), anyChar());
		verify(webService, times(2)).getAttribute(any(), anyString());
		
		// assert
		Assert.assertEquals(output.getType(), LIST);
		@SuppressWarnings("unchecked")
		List<MetaExpression> result = (List<MetaExpression>) output.getValue();
		Assert.assertEquals(result.get(0).getStringValue(), element1Text);
		Assert.assertEquals(result.get(1).getStringValue(), element2Text);
	}
	
	/**
	 * tests the process under normal circumstances with a page given.
	 * We have a text query and findElements returns more items.
	 */
	@Test
	public void testNormalUsageWithPageAndTextQueryAndMoreElements(){
		// mock
		WebService webService = mock(WebService.class);
		StringService stringService = mock(StringService.class);
		
		//The page
		PhantomJSPool.Entity pageVariable = mock(PhantomJSPool.Entity.class);
		WebDriver driver = mock(WebDriver.class);
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PhantomJSPool.Entity.class)).thenReturn(pageVariable);
		when(pageVariable.getDriver()).thenReturn(driver);
		
		//The xpath
		String xPathValue = "xpath";
		String shortenedXPathValue = "shorter xpath";
		MetaExpression xpath = mock(MetaExpression.class);
		when(xpath.getStringValue()).thenReturn(xPathValue);
		
		//The WebElements
		WebElement element1 = mock(WebElement.class);
		String element1Text = "element1";
		WebElement element2 = mock(WebElement.class);
		String element2Text = "element2";
		
		//process SEL node
		when(stringService.endsWith(xPathValue, "/text()")).thenReturn(true);
		when(stringService.matches(xPathValue, "^.*@\\w+$")).thenReturn(false);
		when(stringService.subString(xPathValue, 0, xPathValue.length() - 7)).thenReturn(shortenedXPathValue);
		when(webService.findElementsWithXpath(driver, shortenedXPathValue)).thenReturn(Arrays.asList(element1, element2));
		when(webService.getAttribute(element1, "innerHTML")).thenReturn(element1Text);
		when(webService.getAttribute(element2, "innerHTML")).thenReturn(element2Text);
		
		// run
		MetaExpression output = XPathConstruct.process(page, xpath, stringService, webService);
		
		// verify
		verify(stringService, times(1)).endsWith(xPathValue, "/text()");
		verify(stringService, times(1)).matches(xPathValue, "^.*@\\w+$");
		verify(stringService, times(1)).subString(xPathValue, 0, xPathValue.length() - 7);
		verify(stringService, times(0)).lastIndexOf(anyString(), anyChar());
		verify(webService, times(2)).getAttribute(any(), anyString());
		
		// assert
		Assert.assertEquals(output.getType(), LIST);
		@SuppressWarnings("unchecked")
		List<MetaExpression> result = (List<MetaExpression>) output.getValue();
		Assert.assertEquals(result.get(0).getStringValue(), element1Text);
		Assert.assertEquals(result.get(1).getStringValue(), element2Text);
	}
	
	/**
	 * tests the process under normal circumstances with a page given.
	 * We have a text query and findElements returns no items.
	 */
	@Test
	public void testNormalUsageWithPageAndTextQueryAndNoElements(){
		// mock
		WebService webService = mock(WebService.class);
		StringService stringService = mock(StringService.class);
		
		//The page
		PhantomJSPool.Entity pageVariable = mock(PhantomJSPool.Entity.class);
		WebDriver driver = mock(WebDriver.class);
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PhantomJSPool.Entity.class)).thenReturn(pageVariable);
		when(pageVariable.getDriver()).thenReturn(driver);
		
		//The xpath
		String xPathValue = "xpath";
		String shortenedXPathValue = "shorter xpath";
		MetaExpression xpath = mock(MetaExpression.class);
		when(xpath.getStringValue()).thenReturn(xPathValue);
		
		//process SEL node
		when(stringService.endsWith(xPathValue, "/text()")).thenReturn(true);
		when(stringService.matches(xPathValue, "^.*@\\w+$")).thenReturn(false);
		when(stringService.subString(xPathValue, 0, xPathValue.length() - 7)).thenReturn(shortenedXPathValue);
		when(webService.findElementsWithXpath(driver, shortenedXPathValue)).thenReturn(Arrays.asList());
		
		// run
		MetaExpression output = XPathConstruct.process(page, xpath, stringService, webService);
		
		// verify
		verify(stringService, times(1)).endsWith(xPathValue, "/text()");
		verify(stringService, times(1)).matches(xPathValue, "^.*@\\w+$");
		verify(stringService, times(1)).subString(xPathValue, 0, xPathValue.length() - 7);
		verify(stringService, times(0)).lastIndexOf(anyString(), anyChar());
		verify(webService, times(0)).getAttribute(any(), anyString());
		
		// assert
		Assert.assertEquals(output, NULL);
	}
	
	/**
	 * tests the process under normal circumstances with a page given.
	 * We have an attribute query and findElements returns more items.
	 */
	@Test
	public void testNormalUsageWithPageAndAttributeQueryAndMoreElements(){
		// mock
		WebService webService = mock(WebService.class);
		StringService stringService = mock(StringService.class);
		
		//The page
		PhantomJSPool.Entity pageVariable = mock(PhantomJSPool.Entity.class);
		WebDriver driver = mock(WebDriver.class);
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PhantomJSPool.Entity.class)).thenReturn(pageVariable);
		when(pageVariable.getDriver()).thenReturn(driver);
		
		//The xpath
		String xPathValue = "xpath";
		String shortenedXPathValue = "shorter xpath";
		MetaExpression xpath = mock(MetaExpression.class);
		when(xpath.getStringValue()).thenReturn(xPathValue);
		
		//The WebElements
		WebElement element1 = mock(WebElement.class);
		String element1Text = "element1";
		String element1Result = "Result from getAttribute for element 1";
		WebElement element2 = mock(WebElement.class);
		String element2Text = "element2";
		String element2Result = "Result from getAttribute for element 2";
		
		//process SEL node
		when(stringService.endsWith(xPathValue, "/text()")).thenReturn(false);
		when(stringService.matches(xPathValue, "^.*@\\w+$")).thenReturn(true);
		
		//The attribute part
		when( stringService.lastIndexOf(xPathValue, '@')).thenReturn(9);
		when(stringService.subString(xPathValue, 0, 10)).thenReturn("attribute");
		when(stringService.lastIndexOf(xPathValue, '/')).thenReturn(5);
		when(stringService.subString(xPathValue, 0, 5)).thenReturn(shortenedXPathValue);
		
		when(webService.findElementsWithXpath(driver, shortenedXPathValue)).thenReturn(Arrays.asList(element1, element2));
		when(webService.getAttribute(element1, "innerHTML")).thenReturn(element1Text);
		when(webService.getAttribute(element2, "innerHTML")).thenReturn(element2Text);
		
		//The second attribute part
		when(webService.getAttribute(element1, "attribute")).thenReturn(element1Result);
		when(webService.getAttribute(element2, "attribute")).thenReturn(element2Result);
		
		// run
		MetaExpression output = XPathConstruct.process(page, xpath, stringService, webService);
		
		// verify
		verify(stringService, times(1)).endsWith(xPathValue, "/text()");
		verify(stringService, times(1)).matches(xPathValue, "^.*@\\w+$");
		verify(stringService, times(0)).subString(xPathValue, 0, xPathValue.length() - 7);
		verify(stringService, times(2)).lastIndexOf(anyString(), anyChar());
		verify(webService, times(2)).getAttribute(any(), anyString());
		
		// assert
		Assert.assertEquals(output.getType(), LIST);
		@SuppressWarnings("unchecked")
		List<MetaExpression> result = (List<MetaExpression>) output.getValue();
		Assert.assertEquals(result.get(0).getStringValue(), element1Result);
		Assert.assertEquals(result.get(1).getStringValue(), element2Result);
	}
}
