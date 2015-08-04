package nl.xillio.xill.plugins.example;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.example.constructs.CopyConstruct;

/**
 * Test the behavior of the {@link CopyConstruct}
 */
public class CopyConstructTest extends ExpressionBuilderHelper {

	/**
	 * Test the behavior under normal usage on list
	 */
	@Test
	public void testPrepareProcessList() {
		// We do not want to test the ConstructContext so we mock this
		ConstructContext context = mock(ConstructContext.class);

		// To test a construct we need to prepare a process from it.
		ConstructProcessor processor = new CopyConstruct().prepareProcess(context);

		// Build the arguments
		List<MetaExpression> listValues = Arrays.asList(NULL, TRUE,
			emptyObject(), emptyList());
		MetaExpression list = fromValue(listValues);

		// Process the construct from the argument
		MetaExpression result = ConstructProcessor.process(processor, list);

		// Make assertions
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
		// We do not want to test the ConstructContext so we mock this
		ConstructContext context = mock(ConstructContext.class);

		// To test a construct we need to prepare a process from it.
		ConstructProcessor processor = new CopyConstruct().prepareProcess(context);

		// Build the arguments
		LinkedHashMap<String, MetaExpression> objectValues = new LinkedHashMap<>();
		objectValues.put("string", fromValue("stringvalue"));
		objectValues.put("otherObject", emptyList());
		objectValues.put("otherList", emptyList());
		objectValues.put("null", NULL);
		MetaExpression object = fromValue(objectValues);

		// Process the construct from the argument
		MetaExpression result = ConstructProcessor.process(processor, object);

		// Make assertions
		assertNotSame(object, result);
		assertNotSame(object.getValue(), result.getValue());
		assertEquals(object, result);
		assertEquals(object.getValue(), result.getValue());
	}
}
