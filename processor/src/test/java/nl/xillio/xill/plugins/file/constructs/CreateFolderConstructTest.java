package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test the CreateFolderConstruct
 */
public class CreateFolderConstructTest {

    @Test
    public void testProcessNormal() throws Exception {

        // Folder
        String folderPath = "This is the path to the folder";
        MetaExpression folder = mock(MetaExpression.class);
        when(folder.getStringValue()).thenReturn(folderPath);

        // Context
        RobotID robotID = mock(RobotID.class);
        ConstructContext context = mock(ConstructContext.class);
        when(context.getRobotID()).thenReturn(robotID);

        // File
        File file = mock(File.class);
        when(file.getAbsolutePath()).thenReturn("ABSPATH");
        when(TestUtils.CONSTRUCT_FILE_RESOLVER.buildFile(any(), anyString())).thenReturn(file);

        // FileUtilities
        FileUtilities fileUtils = mock(FileUtilities.class);

        // Run the method
        MetaExpression result = CreateFolderConstruct.process(context, fileUtils, folder);

        // Verify
        verify(fileUtils, times(1)).createFolder(any());

        // Assert
        assertEquals(result.getStringValue(), "ABSPATH");
    }

    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testProcessIOException() throws Exception {

        // Folder
        MetaExpression folder = mock(MetaExpression.class);

        // Context
        ConstructContext context = mock(ConstructContext.class);

        // File
        File file = mock(File.class);
        when(file.getAbsolutePath()).thenReturn("ABSPATH");
        when(TestUtils.CONSTRUCT_FILE_RESOLVER.buildFile(any(), anyString())).thenReturn(file);

        // FileUtilities
        FileUtilities fileUtils = mock(FileUtilities.class);
        doThrow(new IOException("Something Failed")).when(fileUtils).createFolder(file);

        // Run the method
        MetaExpression result = CreateFolderConstruct.process(context, fileUtils, folder);
    }
}
