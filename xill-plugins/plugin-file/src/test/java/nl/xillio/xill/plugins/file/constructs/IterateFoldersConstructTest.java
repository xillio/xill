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
 * Test the IterateFoldersConstruct
 */
public class IterateFoldersConstructTest {

    @Test
    public void testProcessNormalTrue() throws Exception {
        //Recursive
        MetaExpression recursive = mock(MetaExpression.class);
        when(recursive.getBooleanValue()).thenReturn(true);

        //Uri
        String path = "This is the folder uri";
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
        MetaExpression result = IterateFoldersConstruct.process(context, fileUtils, uri, recursive);

        //Verify
        verify(fileUtils, times(1)).buildFile(robotID, path);
        verify(fileUtils, times(1)).iterateFolders(file, true);

        //Assert
        assertNotNull(result.getMeta(MetaExpressionIterator.class));
    }

    @Test(
            expectedExceptions = RobotRuntimeException.class,
            expectedExceptionsMessageRegExp = "Failed to iterate folders: This is an error")
    public void testProcessIOException() throws IOException {
        //FileUtils
        FileUtilities fileUtils = mock(FileUtilities.class);
        when(fileUtils.iterateFolders(any(File.class), anyBoolean())).thenThrow(new IOException("This is an error"));
        when(fileUtils.buildFile(any(RobotID.class), anyString())).thenReturn(mock(File.class));

        //Run the Method
        IterateFoldersConstruct.process(mock(ConstructContext.class), fileUtils, mock(MetaExpression.class), mock(MetaExpression.class));
    }
}