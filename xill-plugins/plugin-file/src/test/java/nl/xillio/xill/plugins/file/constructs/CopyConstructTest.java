package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.stubbing.OngoingStubbing;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test the CopyConstruct
 */
public class CopyConstructTest extends TestUtils {

	@Test
	public void testProcessNormal() throws Exception {
		// Source
		String sourceString = "This is the source file";
		MetaExpression source = mock(MetaExpression.class);
		when(source.getStringValue()).thenReturn(sourceString);
		setFileResolverReturnValue(new File(sourceString));


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
		verify(fileUtils, times(1)).copy(any(File.class), any(File.class));

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

		setFileResolverReturnValue(new File(""));

		// FileUtilities
		FileUtilities fileUtils = mock(FileUtilities.class);
		doThrow(new IOException("Something went wrong")).when(fileUtils).copy(any(File.class), any(File.class));

		// Run the method
		CopyConstruct.process(context, fileUtils, source, target);

		// Verify the error that was logged
		verify(logger).error(contains("Something went wrong"), any(IOException.class));

	}
}
