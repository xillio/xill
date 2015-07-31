package nl.xillio.xill.plugins.file.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

/**
 * Test the DeleteConstruct
 */
public class DeleteConstructTest {

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
		File file = mock(File.class);
		FileUtilities fileUtils = mock(FileUtilities.class);
		when(fileUtils.buildFile(robotID, filePath)).thenReturn(file);

		// Run the method
		DeleteConstruct.process(context, fileUtils, uri);

		// Verify
		verify(fileUtils, times(1)).delete(file);
	}

	@Test
	public void testProcessIOException() throws Exception {
		// URI
		String filePath = "This is the file path";
		MetaExpression uri = mock(MetaExpression.class);
		when(uri.getStringValue()).thenReturn(filePath);

		// Logger
		Logger logger = mock(Logger.class);

		// Context
		RobotID robotID = mock(RobotID.class);
		ConstructContext context = mock(ConstructContext.class);
		when(context.getRobotID()).thenReturn(robotID);
		when(context.getRootLogger()).thenReturn(logger);

		// FileUtilities
		File file = mock(File.class);
		when(file.getAbsolutePath()).thenReturn(filePath);
		FileUtilities fileUtils = mock(FileUtilities.class);
		when(fileUtils.buildFile(robotID, filePath)).thenReturn(file);
		doThrow(new IOException("Something crashed")).when(fileUtils).delete(file);

		// Run the method
		DeleteConstruct.process(context, fileUtils, uri);

		// Verify
		verify(logger).error(eq("Failed to delete " + filePath), any(IOException.class));
	}
}
