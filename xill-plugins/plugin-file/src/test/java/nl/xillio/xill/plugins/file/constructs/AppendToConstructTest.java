package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Test the {@link AppendToConstruct}
 *
 * @author Thomas Biesaart
 */
public class AppendToConstructTest {
	/**
	 * Test the process method under normal circumstances
	 */
	@Test
	public void testProcessNormal() throws IOException {
		// Uri
		String pathString = "this is a path";
		MetaExpression path = mock(MetaExpression.class);
		when(path.getStringValue()).thenReturn(pathString);

		// RobotID
		RobotID robotID = mock(RobotID.class);

		// buildFile
		FileUtilities fileUtils = mock(FileUtilities.class);

		// Content
		String textContent = "this is the content";
		MetaExpression content = mock(MetaExpression.class);
		when(content.getStringValue()).thenReturn(textContent);

		// Context
		ConstructContext context = mock(ConstructContext.class);
		when(context.getRobotID()).thenReturn(robotID);

		// File
		File file = mock(File.class);
		when(file.getAbsolutePath()).thenReturn("Absolute Path");
		when(TestUtils.CONSTRUCT_FILE_RESOLVER.buildFile(any(), anyString())).thenReturn(file);

		// Run the method
		MetaExpression result = AppendToConstruct.process(context, fileUtils, path, content);

		// Verify
		verify(fileUtils, times(1)).appendStringToFile(textContent, file);

		// Assert
		assertEquals(result.getStringValue(), "Absolute Path");
	}

	/**
	 * Test the process method when the operation throws an IOException
	 *
	 * @throws Exception
	 */
	@Test()
	public void testProcessIOException() throws Exception {
		// Uri
		MetaExpression path = mock(MetaExpression.class);

		// fileUtils
		FileUtilities fileUtils = mock(FileUtilities.class);
		doThrow(new IOException("Something went wrong")).when(fileUtils).appendStringToFile(anyString(), any(File.class));

		// Logger
		Logger logger = mock(Logger.class);

		// Content
		MetaExpression content = mock(MetaExpression.class);

		// File
		File file = mock(File.class);
		TestUtils.setFileResolverReturnValue(file);

		// Context
		ConstructContext context = mock(ConstructContext.class);
		when(context.getRootLogger()).thenReturn(logger);

		// Run the method
		AppendToConstruct.process(context, fileUtils, path, content);

		// Verify
		verify(logger).error(eq("Failed to write to file: Something went wrong"), any(IOException.class));
	}
}
