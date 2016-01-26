package nl.xillio.xill.plugins.system.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.services.json.JsonException;
import nl.xillio.xill.services.json.JsonParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the {@link ToJSONConstruct}
 */
public class ToJSONConstructTest extends TestUtils {

    /**
     * Test the process method under normal circumstances
     */
    @Test
    public void testProcess() throws JsonException {
        // Mock context
        String output = "This is the output";
        MetaExpression input = mockExpression(ATOMIC);
        JsonParser parser = mock(JsonParser.class);
        when(parser.toJson(input)).thenReturn(output);

        MetaExpression pretty = mockExpression(ATOMIC);
        when(pretty.getBooleanValue()).thenReturn(false);

        // Run
        MetaExpression result = ToJSONConstruct.process(input, pretty, parser, null);

        // Verify
        verify(parser).toJson(input);

        // Assert
        Assert.assertSame(result.getStringValue(), output);
    }

    /**
     * Test the process method under normal circumstances with pretty printing
     */
    @Test
    public void testProcessPretty() throws JsonException {
        // Mock context
        String output = "This is the output";
        MetaExpression input = mockExpression(ATOMIC);
        JsonParser parser = mock(JsonParser.class);
        when(parser.toJson(input)).thenReturn(output);

        MetaExpression pretty = mockExpression(ATOMIC);
        when(pretty.getBooleanValue()).thenReturn(true);

        // Run
        MetaExpression result = ToJSONConstruct.process(input, pretty, null, parser);

        // Verify
        verify(parser).toJson(input);

        // Assert
        Assert.assertSame(result.getStringValue(), output);
    }

    /**
     * Test the process when a StackOverflowError occurs
     */
    @Test(expectedExceptions = {RobotRuntimeException.class})
    public void testProcessCircularReference() throws JsonException {
        // Mock context
        MetaExpression input = mockExpression(LIST);
        JsonParser parser = mock(JsonParser.class);
        when(parser.toJson(input)).thenThrow(new StackOverflowError());

        // Run
        ToJSONConstruct.process(input, fromValue(false), parser, null);
    }
}
