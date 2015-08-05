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
import nl.xillio.xill.plugins.file.TestInjectorModule;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

/**
 * Test the CopyConstruct
 */
public class CopyConstructTest extends TestInjectorModule {

	@Test
	public void testProcessNormal() throws Exception {
		// Source
		String sourceString = "This is the source file";
		MetaExpression source = mock(MetaExpression.class);
		when(source.getStringValue()).thenReturn(sourceString);

		// Target
		String targetString = "This is the target file";
		MetaExpression target = mock(MetaExpression.class);
		when(target.getStringValue()).thenReturn(targetString);

		// Context
		RobotID robotID = mock(RobotID.class);
		ConstructContext context = mock(ConstructContext.class);
		when(context.getRobotID()).thenReturn(robotID);

		// FileUtils
		FileUtilities fileUtils = mock(FileUtilities.class);

		// Run the method
		CopyConstruct.process(context, fileUtils, source, target);

		// Verify
		verify(fileUtils, times(1)).copy(FILE, FILE);

	}

	@Test
	public void testProcessIOException() throws Exception {

		// Source
		MetaExpression source = mock(MetaExpression.class);

		// Target
		MetaExpression target = mock(MetaExpression.class);

		// Context
		Logger logger = mock(Logger.class);
		RobotID robotID = mock(RobotID.class);
		ConstructContext context = mock(ConstructContext.class);
		when(context.getRobotID()).thenReturn(robotID);
		when(context.getRootLogger()).thenReturn(logger);

		// FileUtilities
		FileUtilities fileUtils = mock(FileUtilities.class);
		doThrow(new IOException("Something went wrong")).when(fileUtils).copy(FILE, FILE);

		// Run the method
		CopyConstruct.process(context, fileUtils, source, target);

		// Verify the error that was logged
		verify(logger).error(eq("Failed to copy null to null: Something went wrong"), any(IOException.class));

	}
}
