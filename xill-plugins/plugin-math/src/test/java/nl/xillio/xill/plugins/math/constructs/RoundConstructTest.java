package nl.xillio.xill.plugins.math.constructs;

import org.testng.Assert;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.math.services.math.MathOperations;

/**
 * Test the {@link RoundConstruct}
 */
public class RoundConstructTest {

  /**
   * Test the process method under normal circumstances
   */
  @Test
  public void processNormalUsage() {
  	//Mock
  	Double numberValue = 42.2;
  	MetaExpression value = mock(MetaExpression.class);
  	when(value.getNumberValue()).thenReturn(numberValue);
  	
  	long mathReturnValue = 42;
  	MathOperations math = mock(MathOperations.class);
  	when(math.round(numberValue)).thenReturn(mathReturnValue);
  	
  	//Run
  	MetaExpression result = RoundConstruct.process(value, math);
  	
  	//Verify
  	verify(math, times(1)).round(numberValue);
  	
  	//Assert
  	Assert.assertEquals(result.getNumberValue().longValue(), mathReturnValue);
  	
  }
}
