package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.file.TestInjectorModule;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test the DeleteConstruct
 */
public class DeleteConstructTest extends TestInjectorModule {

	@Test
	public void testProcessNormal() throws Exception {
		// URI
		String filePath = "This is the file path";
		MetaExpression uri = mock(MetaExpression.class);
		when(uri.getStringValue()).thenReturn(filePath);

		// Context
		RobotID robotID = mock(RobotID.class);
		ConstructContext context = mock(ConstructContext.class);
		when(context.getRobotID()).thenReturn(robotID);

		// FileUtilities
		FileUtilities fileUtils = mock(FileUtilities.class);

		// Run the method
		DeleteConstruct.process(context, fileUtils, uri);

		// Verify
		verify(fileUtils, times(1)).delete(FILE);
	}

	@Test
	public void testProcessIOException() throws Exception {
		// URI
		MetaExpression uri = mock(MetaExpression.class);

		// Logger
		Logger logger = mock(Logger.class);

		// Context
		RobotID robotID = mock(RobotID.class);
		ConstructContext context = mock(ConstructContext.class);
		when(context.getRobotID()).thenReturn(robotID);
		when(context.getRootLogger()).thenReturn(logger);

		// FileUtilities
		FileUtilities fileUtils = mock(FileUtilities.class);
		doThrow(new IOException("Something crashed")).when(fileUtils).delete(FILE);

		// Run the method
		DeleteConstruct.process(context, fileUtils, uri);

		// Verify
		verify(logger).error(eq("Failed to delete " + ABS_PATH), any(IOException.class));
	}
}
