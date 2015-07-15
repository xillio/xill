package nl.xillio.xill.plugins.example;

import static org.mockito.Mockito.mock;

import org.testng.Assert;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.example.constructs.LifeConstuct;

/**
 * Test the {@link LifeConstuct}
 */
public class LifeConstructTest {

    /**
     * Test the construct under normal usage
     */
    @Test
    public void testPrepareProcess() {
	//We do not want to test the ConstructContext so we mock this
	ConstructContext context = mock(ConstructContext.class);
	
	//To test a construct we need to prepare a process from it.
	ConstructProcessor processor = new LifeConstuct().prepareProcess(context);
	
	//Get the result from the processor
	MetaExpression result = processor.process();
	
	//Make assertions
	Assert.assertEquals(result.getNumberValue().intValue(), 42);
	Assert.assertEquals(processor.getNumberOfArguments(), 0);
    }
}
