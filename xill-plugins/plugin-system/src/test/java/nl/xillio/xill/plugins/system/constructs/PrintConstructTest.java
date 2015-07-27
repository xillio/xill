package nl.xillio.xill.plugins.system.constructs;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.system.util.ConstructTest;

/**
 * Test the {@link PrintConstruct}
 */
public class PrintConstructTest extends ConstructTest {

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
