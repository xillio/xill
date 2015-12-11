package nl.xillio.xill.plugins.system.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.services.json.JsonException;
import nl.xillio.xill.services.json.JsonParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test the {@link ParseJSONConstruct}
 */
public class ParseJSONConstructTest extends TestUtils {

    /**
     * Test the process method under normal circumstances
     */
    @Test
    public void testProcess() throws JsonException {
        // Mock context
        String json = "[\"this\", \"is\", \"valid\", \"json\"]";
        ArrayList<?> expectedResult = new ArrayList<>();
        MetaExpression expression = mockExpression(ATOMIC);
        when(expression.getStringValue()).thenReturn(json);
        JsonParser parser = mock(JsonParser.class);
        when(parser.fromJson(eq(json), any())).thenReturn(expectedResult);

        // Run method
        MetaExpression result = ParseJSONConstruct.process(expression, parser);

        // Verify calls to service
        verify(parser).fromJson(eq(json), any());

        // Assertions
        Assert.assertEquals(result.getValue(), expectedResult);

    }

    /**
     * Test the process method with null input
     */
    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testProcessNullInput() {
        // Run method
        ParseJSONConstruct.process(NULL, null);
    }

    /**
     * Test the process method with invalid json
     *
     * @throws Throwable while testing
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid JSON input: (.+)")
    public void testProcessInvalid() throws Throwable {
        // Mock context
        MetaExpression expression = mockExpression(ATOMIC);
        JsonParser parser = mock(JsonParser.class);
        when(parser.fromJson(anyString(), any())).thenThrow(new JsonException("CORRECT", null));

        // Run method
        ParseJSONConstruct.process(expression, parser);
    }
}
