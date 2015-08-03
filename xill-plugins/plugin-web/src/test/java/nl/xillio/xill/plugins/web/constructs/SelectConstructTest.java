package nl.xillio.xill.plugins.web.constructs;

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

public class SelectConstructTest extends ExpressionBuilderHelper{

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

		SelectConstruct.process(input,TRUE);
	}
  
  
  /**
   *  test the construct with normal input. "Select" is false and the element is not selected. 
   *  element.click is never called. returns NULL
   */
  @Test
  public void testProcessSelectFalseIsSelectedFalse() {
  	// mock
		WebDriver driver = mock(WebDriver.class);
		WebElement element = mock(WebElement.class);
		MetaExpression input = mock(MetaExpression.class);
		MetaExpression meta = NodeVariable.create(driver, element);

		when(NodeVariable.checkType(input)).thenReturn(null);
		when(input.getMeta(String.class)).thenReturn("Selenium:node");
		when(input.getMeta(NodeVariable.class)).thenReturn(meta.getMeta(NodeVariable.class));

		MetaExpression select = mock(MetaExpression.class);
		
		when(select.getBooleanValue()).thenReturn(false); //false
		when(element.isSelected()).thenReturn(false); //false
		
		// run
		MetaExpression output = SelectConstruct.process(input,select);

		// verify
		
		//element.click is never called when both are false.
		verify(element, times(0)).click();
		verify(select,times(1)).getBooleanValue();
		verify(element,times(1)).isSelected();
		
		//assert
		Assert.assertSame(output, NULL);
  }
  
  /**
   *  test the construct with normal input. "Select" is true and the element IS selected. 
   *  element.click is never called. returns NULL.
   */
  @Test
  public void testProcessSelectTrueIsSelectedTrue(){
   	// mock
  		WebDriver driver = mock(WebDriver.class);
  		WebElement element = mock(WebElement.class);
  		MetaExpression input = mock(MetaExpression.class);
  		MetaExpression meta = NodeVariable.create(driver, element);

  		when(NodeVariable.checkType(input)).thenReturn(null);
  		when(input.getMeta(String.class)).thenReturn("Selenium:node");
  		when(input.getMeta(NodeVariable.class)).thenReturn(meta.getMeta(NodeVariable.class));

  		MetaExpression select = mock(MetaExpression.class);
  		
  		when(select.getBooleanValue()).thenReturn(true); //true
  		when(element.isSelected()).thenReturn(true); //true
  		
  		// run
  		MetaExpression output = SelectConstruct.process(input,select);

  		// verify
  		
  		//element.click is never called when both are true
  		verify(element, times(0)).click();
  		verify(select,times(1)).getBooleanValue();
  		verify(element,times(1)).isSelected();
  		
  		//assert
  		Assert.assertSame(output, NULL);
  }
  /**
   *  test the construct with normal input. "Select" is false and the element is selected. 
   *  element.click is called once. returns NULL
   */
  @Test
  public void testProcessSelectFalseIsSelectedTrue(){
   	// mock
  		WebDriver driver = mock(WebDriver.class);
  		WebElement element = mock(WebElement.class);
  		MetaExpression input = mock(MetaExpression.class);
  		MetaExpression meta = NodeVariable.create(driver, element);

  		when(NodeVariable.checkType(input)).thenReturn(null);
  		when(input.getMeta(String.class)).thenReturn("Selenium:node");
  		when(input.getMeta(NodeVariable.class)).thenReturn(meta.getMeta(NodeVariable.class));

  		MetaExpression select = mock(MetaExpression.class);
  		
  		when(select.getBooleanValue()).thenReturn(false); //true
  		when(element.isSelected()).thenReturn(true); //true
  		
  		// run
  		MetaExpression output = SelectConstruct.process(input,select);

  		// verify
  		
  		//element.click IS called when Select is false and isSelected is true.
  		verify(element, times(1)).click();
  		verify(select,times(1)).getBooleanValue();
  		verify(element,times(1)).isSelected();
  		
  		//assert
  		Assert.assertSame(output, NULL);
  }
  /**
   *  test the construct with normal input. "Select" is true and the element is NOT selected. 
   *  element.click is called once. returns NULL
   */
  @Test
  public void testProcessSelectTrueIsSelectedFalse(){
   	// mock
  		WebDriver driver = mock(WebDriver.class);
  		WebElement element = mock(WebElement.class);
  		MetaExpression input = mock(MetaExpression.class);
  		MetaExpression meta = NodeVariable.create(driver, element);

  		when(NodeVariable.checkType(input)).thenReturn(null);
  		when(input.getMeta(String.class)).thenReturn("Selenium:node");
  		when(input.getMeta(NodeVariable.class)).thenReturn(meta.getMeta(NodeVariable.class));

  		MetaExpression select = mock(MetaExpression.class);
  		
  		when(select.getBooleanValue()).thenReturn(true); //true
  		when(element.isSelected()).thenReturn(false); //true
  		
  		// run
  		MetaExpression output = SelectConstruct.process(input,select);

  		// verify
  		
  		//element.click IS called when Select is true and isSelected is false.
  		verify(element, times(1)).click();
  		verify(select,times(1)).getBooleanValue();
  		verify(element,times(1)).isSelected();
  		
  		//assert
  		Assert.assertSame(output, NULL);
  }
}
