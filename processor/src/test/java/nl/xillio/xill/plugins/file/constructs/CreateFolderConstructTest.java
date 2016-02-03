package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Test the CreateFolderConstruct
 */
public class CreateFolderConstructTest extends TestUtils {

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
        setFileResolverReturnValue(new File("ABSPATH"));

        // FileUtilities
        FileUtilities fileUtils = mock(FileUtilities.class);

        // Run the method
        MetaExpression result = CreateFolderConstruct.process(context, fileUtils, folder);

        // Verify
        verify(fileUtils, times(1)).createFolder(any(File.class));

        // Assert
        assertEquals(result.getStringValue(), new File("ABSPATH").getAbsolutePath());
    }

    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testProcessIOException() throws Exception {

        // Folder
        MetaExpression folder = mock(MetaExpression.class);

        // Context
        ConstructContext context = mock(ConstructContext.class);

        // File
        File file = new File("ABSPATH");
        setFileResolverReturnValue(file);

        // FileUtilities
        FileUtilities fileUtils = mock(FileUtilities.class);
        doThrow(new IOException("Something Failed")).when(fileUtils).createFolder(file);

        // Run the method
        MetaExpression result = CreateFolderConstruct.process(context, fileUtils, folder);
    }
}
