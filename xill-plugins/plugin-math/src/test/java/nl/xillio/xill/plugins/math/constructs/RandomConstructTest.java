package nl.xillio.xill.plugins.math.constructs;

import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.math.services.math.MathOperations;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;

/**
 * Test the {@link RandomConstruct}
 */
public class RandomConstructTest extends ExpressionBuilderHelper{

  /**
   * Test the process method with a positive value given
   */
  @Test
  public void processPositiveLong() {
  	//Mock
  	long numberValue = 42;
  	MetaExpression value = mock(MetaExpression.class);
  	when(value.getNumberValue()).thenReturn(numberValue);
  	
  	long mathReturnValue = 12;
  	MathOperations math = mock(MathOperations.class);
  	when(math.random(numberValue)).thenReturn(mathReturnValue);
  	
  	//Run
  	MetaExpression result = RandomConstruct.process(value, math);
  	
  	//Verify
  	verify(math, times(1)).random(numberValue);
  	
  	//Assert
  	Assert.assertEquals(result.getNumberValue().longValue(), mathReturnValue);
  	
  }
  
  /**
   * Test the process method with a negative value given.
   */
  @Test
  public void processNegativeLong(){
  	//Mock
  	long numberValue = -42;
  	MetaExpression value = mock(MetaExpression.class);
  	when(value.getNumberValue()).thenReturn(numberValue);
  	
  	double mathReturnValue = 0.01;
  	MathOperations math = mock(MathOperations.class);
  	when(math.random()).thenReturn(mathReturnValue);
  	
  	//Run
  	MetaExpression result = RandomConstruct.process(value, math);
  	
  	//Verify
  	verify(math, times(1)).random();
  	
  	//Assert
  	Assert.assertEquals(result.getNumberValue().doubleValue(), mathReturnValue);
  }
  
  /**
   * Test the process method with a list value given
   */
  @Test
  public void processList(){
  	//Mock
  	MetaExpression value = mock(MetaExpression.class);
  	when(value.getValue()).thenReturn(Arrays.asList(
  		fromValue("Test"),
  		fromValue("Command"),
  		fromValue("T-1")));
  	when(value.getType()).thenReturn(LIST);
  	
  	long mathReturnValue = 1;
  	MathOperations math = mock(MathOperations.class);
  	when(math.random(3)).thenReturn(mathReturnValue);
  	
  	//Run
  	MetaExpression result = RandomConstruct.process(value, math);
  	
  	//Verify
  	verify(math, times(1)).random(3);
  	
  	//Assert
  	Assert.assertEquals(result, nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue("Command"));
  }
  
  /*
   * (Arrays.asList(
			fromValue("Test"),
			fromValue("command"),
			fromValue("-t")));
   */
}
