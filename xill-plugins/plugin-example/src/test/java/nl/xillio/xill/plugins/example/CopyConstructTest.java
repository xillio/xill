package nl.xillio.xill.plugins.example;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * Test the behavior of the {@link CopyConstruct}
 */
public class CopyConstructTest {

    /**
     * Test the behavior under normal usage on list
     */
    @Test
    public void testPrepareProcessList() {
	//We do not want to test the ConstructContext so we mock this
	ConstructContext context = mock(ConstructContext.class);

	//To test a construct we need to prepare a process from it.
	ConstructProcessor processor = new CopyConstruct().prepareProcess(context);

	//Build the arguments
	List<MetaExpression> listValues = Arrays.asList(ExpressionBuilder.NULL, ExpressionBuilder.TRUE,
		ExpressionBuilder.emptyObject(), ExpressionBuilder.emptyList());
	MetaExpression list = ExpressionBuilder.fromValue(listValues);
	
	//Process the construct from the argument
	MetaExpression result = ConstructProcessor.Process(processor, list);

	//Make assertions
	assertNotSame(list, result);
	assertNotSame(list.getValue(), result.getValue());
	assertEquals(list, result);
	assertEquals(list.getValue(), result.getValue());
    }
    
    /**
     * Test the behavior under normal usage on object
     */
    @Test
    public void testPrepareProcessObject() {
	//We do not want to test the ConstructContext so we mock this
	ConstructContext context = mock(ConstructContext.class);
	
	//To test a construct we need to prepare a process from it.
	ConstructProcessor processor = new CopyConstruct().prepareProcess(context);

	//Build the arguments
	LinkedHashMap<String, MetaExpression> objectValues = new LinkedHashMap<>();
	objectValues.put("string", ExpressionBuilder.fromValue("stringvalue"));
	objectValues.put("otherObject", ExpressionBuilder.emptyList());
	objectValues.put("otherList", ExpressionBuilder.emptyList());
	objectValues.put("null", ExpressionBuilder.NULL);
	MetaExpression object = ExpressionBuilder.fromValue(objectValues);
	
	//Process the construct from the argument
	MetaExpression result = ConstructProcessor.Process(processor, object);

	//Make assertions
	assertNotSame(object, result);
	assertNotSame(object.getValue(), result.getValue());
	assertEquals(object, result);
	assertEquals(object.getValue(), result.getValue());
    }
}
