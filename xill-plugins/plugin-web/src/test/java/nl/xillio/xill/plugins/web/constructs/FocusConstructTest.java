package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Mockito.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.web.NodeVariable;

public class FocusConstructTest extends ExpressionBuilderHelper{

 // @Test
  public void testProcess() {
 // mock
  	
 		WebDriver driver = mock(WebDriver.class);
 		WebElement element = mock(WebElement.class);
 		MetaExpression input = mock(MetaExpression.class);
 		MetaExpression meta = NodeVariable.create(driver, element);
 		
 		when(NodeVariable.checkType(input)).thenReturn(null);
 		when(input.getMeta(String.class)).thenReturn("Selenium:node");
 		when(input.getMeta(NodeVariable.class)).thenReturn(meta.getMeta(NodeVariable.class));
 		
 		Actions actions = mock(Actions.class);
 		when(actions.moveToElement(element)).thenReturn(actions);
 		doNothing().when(actions).perform();
 		
 		
 		//run
 		MetaExpression result = FocusConstruct.process(input);
 		
 		
 		//verify
 		verify(actions,times(1)).moveToElement(element).perform();
 		
 		//assert
 		Assert.assertEquals(result, NULL);
 		
  }
}
