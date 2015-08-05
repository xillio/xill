package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.file.TestInjectorModule;
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
 * Test the SaveToConstruct
 */
public class SaveToConstructTest extends TestInjectorModule {

	@Test
	public void testProcessNormal() throws IOException {
		// Content
		String contentString = "This is the content of the file";
		MetaExpression content = mock(MetaExpression.class);
		when(content.getStringValue()).thenReturn(contentString);

		// Uri
		MetaExpression uri = mock(MetaExpression.class);

		// Context
		RobotID robotID = mock(RobotID.class);
		ConstructContext context = mock(ConstructContext.class);
		when(context.getRobotID()).thenReturn(robotID);

		// FileUtilities
		FileUtilities fileUtils = mock(FileUtilities.class);

		// Run the Method
		MetaExpression result = SaveToConstruct.process(context, fileUtils, uri, content);

		// Verify
		verify(fileUtils, times(1)).saveStringToFile(contentString, FILE);

		// Assert
		assertEquals(result.getStringValue(), ABS_PATH);

	}

	@Test
	public void testProcessIOException() throws IOException {
		// Context
		Logger logger = mock(Logger.class);
		ConstructContext context = mock(ConstructContext.class);
		when(context.getRootLogger()).thenReturn(logger);

		// FileUtilities
		FileUtilities fileUtils = mock(FileUtilities.class);
		doThrow(new IOException("Failed to save")).when(fileUtils).saveStringToFile(anyString(), any(File.class));

		// Run the Method
		SaveToConstruct.process(context, fileUtils, mock(MetaExpression.class), mock(MetaExpression.class));

		// Verify
		verify(logger).error(eq("Failed to write to file: Failed to save"), any(IOException.class));

	}
}
