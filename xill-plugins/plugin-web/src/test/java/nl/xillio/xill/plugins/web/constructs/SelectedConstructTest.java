package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Mockito.*;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;

public class SelectedConstructTest extends ExpressionBuilderHelper {
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
	 * test the construct with normal input. Element is NOT selected, should return FALSE.
	 */
  @Test
  public void testProcessElementNotSelected(){
  	
  	//mock
  	WebDriver driver = mock(WebDriver.class);
		WebElement element = mock(WebElement.class);
		MetaExpression input = mock(MetaExpression.class);
		MetaExpression meta = NodeVariable.create(driver, element);

		when(NodeVariable.checkType(input)).thenReturn(null);
		when(input.getMeta(String.class)).thenReturn("Selenium:node");
		when(input.getMeta(NodeVariable.class)).thenReturn(meta.getMeta(NodeVariable.class));
		
		when(element.isSelected()).thenReturn(false);
		//run 
		MetaExpression result = SelectedConstruct.process(input);
		
		//verify
		
		verify(input, times(1)).getMeta(String.class);
		verify(input, times(2)).getMeta(NodeVariable.class);
		verify(element,times(1)).isSelected();
		
		//assert
		
		Assert.assertEquals(result, FALSE);
		
  }
  
	/**
	 * test the construct with normal input. Element is selected, should return TRUE.
	 */
  @Test
  public void testProcessElementSelected(){
  //mock
  	WebDriver driver = mock(WebDriver.class);
		WebElement element = mock(WebElement.class);
		MetaExpression input = mock(MetaExpression.class);
		MetaExpression meta = NodeVariable.create(driver, element);

		when(NodeVariable.checkType(input)).thenReturn(null);
		when(input.getMeta(String.class)).thenReturn("Selenium:node");
		when(input.getMeta(NodeVariable.class)).thenReturn(meta.getMeta(NodeVariable.class));
		
		when(element.isSelected()).thenReturn(true);
		//run 
		MetaExpression result = SelectedConstruct.process(input);
		
		//verify
		
		verify(input, times(1)).getMeta(String.class);
		verify(input, times(2)).getMeta(NodeVariable.class);
		verify(element,times(1)).isSelected();
		
		//assert
		
		Assert.assertEquals(result, TRUE);
		
  }
}
