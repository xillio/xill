package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Test the CreateFolderConstruct
 */
public class CreateFolderConstructTest {

	@Test
	public void testProcessNormal() throws Exception {

		//Folder
		String folderPath = "This is the path to the folder";
		MetaExpression folder = mock(MetaExpression.class);
		when(folder.getStringValue()).thenReturn(folderPath);

		//Context
		RobotID robotID = mock(RobotID.class);
		ConstructContext context = mock(ConstructContext.class);
		when(context.getRobotID()).thenReturn(robotID);

		//FileUtilities
		String fileString = "This is the absolute path";
		File file = mock(File.class);
		when(file.getAbsolutePath()).thenReturn(fileString);
		FileUtilities fileUtils = mock(FileUtilities.class);
		when(fileUtils.buildFile(robotID, folderPath)).thenReturn(file);

		//Run the method
		MetaExpression result = CreateFolderConstruct.process(context, fileUtils, folder);

		//Verify
		verify(fileUtils, times(1)).createFolder(file);

		//Assert
		assertEquals(result.getStringValue(), fileString);
	}

	@Test
	public void testProcessIOException() throws Exception {

		//Folder
		MetaExpression folder = mock(MetaExpression.class);

		//Logger
		Logger logger = mock(Logger.class);

		//Context
		ConstructContext context = mock(ConstructContext.class);
		when(context.getRootLogger()).thenReturn(logger);

		//FileUtilities
		String fileString = "This is the absolute path";
		File file = mock(File.class);
		when(file.getAbsolutePath()).thenReturn(fileString);
		FileUtilities fileUtils = mock(FileUtilities.class);
		when(fileUtils.buildFile(any(RobotID.class), anyString())).thenReturn(file);
		doThrow(new IOException("Something Failed")).when(fileUtils).createFolder(file);

		//Run the method
		MetaExpression result = CreateFolderConstruct.process(context, fileUtils, folder);

		//Verify
		verify(logger).error(eq("Failed to create " + fileString), any(IOException.class));

		//Assert
		assertEquals(result.getStringValue(), fileString);
	}
}