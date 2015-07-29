package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertNotNull;

/**
 * Test the IterateFilesConstruct
 */
public class IterateFilesConstructTest {

	@Test
	public void testProcessNormalTrue() throws Exception {
		//Recursive
		MetaExpression recursive = mock(MetaExpression.class);
		when(recursive.getBooleanValue()).thenReturn(true);

		//Uri
		String path = "This is the file uri";
		MetaExpression uri = mock(MetaExpression.class);
		when(uri.getStringValue()).thenReturn(path);

		//Context
		RobotID robotID = mock(RobotID.class);
		ConstructContext context = mock(ConstructContext.class);
		when(context.getRobotID()).thenReturn(robotID);

		//FileUtilities
		File file = mock(File.class);
		when(file.getAbsolutePath()).thenReturn(path);
		FileUtilities fileUtils = mock(FileUtilities.class);
		when(fileUtils.buildFile(robotID, path)).thenReturn(file);


		//Run the Method
		MetaExpression result = IterateFilesConstruct.process(context, fileUtils, uri, recursive);

		//Verify
		verify(fileUtils, times(1)).buildFile(robotID, path);
		verify(fileUtils, times(1)).iterateFiles(file, true);

		//Assert
		assertNotNull(result.getMeta(MetaExpressionIterator.class));
	}

	@Test(
			expectedExceptions = RobotRuntimeException.class,
			expectedExceptionsMessageRegExp = "Failed to iterate files: This is an error")
	public void testProcessIOException() throws IOException {
		//FileUtils
		FileUtilities fileUtils = mock(FileUtilities.class);
		when(fileUtils.iterateFiles(any(File.class), anyBoolean())).thenThrow(new IOException("This is an error"));
		when(fileUtils.buildFile(any(RobotID.class), anyString())).thenReturn(mock(File.class));

		//Run the Method
		IterateFilesConstruct.process(mock(ConstructContext.class), fileUtils, mock(MetaExpression.class), mock(MetaExpression.class));
	}


}