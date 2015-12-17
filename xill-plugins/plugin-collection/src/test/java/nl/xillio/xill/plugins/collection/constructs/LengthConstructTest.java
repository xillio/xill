package nl.xillio.xill.plugins.collection.constructs;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;

/**
 * 
 * Test the {@link LengthConstruct}
 * 
 * @author Sander Visser
 *
 */
public class LengthConstructTest extends ExpressionBuilderHelper  {

	/**
	 * Test the process method under normal circumstances with list input.
	 */
  @Test
  public void testProcessWithList() {
  
  	int size = 5;
  	//mock
  	@SuppressWarnings("unchecked")
		ArrayList<MetaExpression> list = mock(ArrayList.class);
  	MetaExpression input = mock(MetaExpression.class);
  	when(input.getType()).thenReturn(LIST);
  	when(input.getValue()).thenReturn(list);
  	when(list.size()).thenReturn(size);
  	
  	//run
  	MetaExpression output = LengthConstruct.process(input);
  	
  	//verify
  	verify(input,times(1)).getType();
  	verify(input,times(1)).getValue();
  	verify(list,times(1)).size();
  	
  	//assert
  	Assert.assertEquals(output, fromValue(size));
  	
  }

	/**
	 * Test the process method under normal circumstances with object input.
	 */
  @Test
  public void testProcessWithObject() {
  
  	int size = 5;
  	//mock
		@SuppressWarnings("unchecked")
		Map<String, MetaExpression> obj = mock(Map.class);
  	MetaExpression input = mock(MetaExpression.class);
  	when(input.getType()).thenReturn(OBJECT);
  	when(input.getValue()).thenReturn(obj);
  	when(obj.size()).thenReturn(size);
  	
  	//run
  	MetaExpression output = LengthConstruct.process(input);
  	
  	//verify
  	verify(input,times(1)).getType();
  	verify(input,times(1)).getValue();
  	verify(obj,times(1)).size();
  	
  	//assert
  	Assert.assertEquals(output, fromValue(size));
  	
  }
}
