package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Test the SizeConstruct
 */
public class SizeConstructTest {

	@Test
	public void testProcessNormal() throws IOException {
		//Uri
		String path = "This is the path";
		MetaExpression uri = mock(MetaExpression.class);
		when(uri.getStringValue()).thenReturn(path);

		//Context
		RobotID robotID = mock(RobotID.class);
		ConstructContext context = mock(ConstructContext.class);
		when(context.getRobotID()).thenReturn(robotID);

		//FileUtilities
		File file = mock(File.class);
		long size = 10;
		FileUtilities fileUtils = mock(FileUtilities.class);
		when(fileUtils.buildFile(robotID, path)).thenReturn(file);
		when(fileUtils.getByteSize(file)).thenReturn(size);

		//Run the Method
		MetaExpression result = SizeConstruct.process(context, fileUtils, uri);

		//Verify
		verify(fileUtils, times(1)).buildFile(robotID, path);
		verify(fileUtils, times(1)).getByteSize(file);

		//Assert
		assertEquals(result.getStringValue(), path);
	}

	@Test(
			expectedExceptions = RobotRuntimeException.class,
			expectedExceptionsMessageRegExp = "Failed to get size of file: This is an error message")
	public void testProcessIOException() throws IOException {
		//FileUtils
		FileUtilities fileUtils = mock(FileUtilities.class);
		when(fileUtils.getByteSize(any())).thenThrow(new IOException("This is an error message"));

		//Run the method
		SizeConstruct.process(mock(ConstructContext.class), fileUtils, mock(MetaExpression.class));
	}
}