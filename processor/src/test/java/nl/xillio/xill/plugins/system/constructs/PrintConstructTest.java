package nl.xillio.xill.plugins.system.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import org.slf4j.Logger;
import org.testng.annotations.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test the {@link PrintConstruct}
 */
public class PrintConstructTest extends TestUtils {

    /**
     * Test the process method under normal circumstances
     */
    @Test
    public void testProcessError() {
        // Mock context
        String message = "This is the message";
        String level = "error";
        MetaExpression textVar = mockExpression(ATOMIC);
        when(textVar.getStringValue()).thenReturn(message);
        MetaExpression logLevel = mockExpression(ATOMIC);
        when(logLevel.getStringValue()).thenReturn(level);
        Logger robotLog = mock(Logger.class);

        // Run method
        PrintConstruct.process(textVar, logLevel, robotLog);

        // Verify calls
        verify(robotLog).error(eq(message));
    }

    /**
     * Test the process method under normal circumstances
     */
    @Test
    public void testProcessInfo() {
        // Mock context
        String message = "This is the message";
        String level = "";
        MetaExpression textVar = mockExpression(ATOMIC);
        when(textVar.getStringValue()).thenReturn(message);
        MetaExpression logLevel = mockExpression(ATOMIC);
        when(logLevel.getStringValue()).thenReturn(level);
        Logger robotLog = mock(Logger.class);

        // Run method
        PrintConstruct.process(textVar, logLevel, robotLog);

        // Verify calls
        verify(robotLog).info(eq(message));
    }
}
