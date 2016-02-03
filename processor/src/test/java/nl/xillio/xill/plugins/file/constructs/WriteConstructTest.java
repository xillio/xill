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
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Test the WriteConstruct
 */
public class WriteConstructTest extends TestUtils {

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

        // File
        File file = mock(File.class);
        when(file.getAbsolutePath()).thenReturn("ABSPATH");
        when(TestUtils.CONSTRUCT_FILE_RESOLVER.buildFile(any(), anyString())).thenReturn(file);

        // FileUtilities
        FileUtilities fileUtils = mock(FileUtilities.class);

        // Run the Method
        MetaExpression result = WriteConstruct.process(context, fileUtils, uri, content);

        // Verify
        verify(fileUtils, times(1)).saveStringToFile(contentString, file);

        // Assert
        assertEquals(result.getStringValue(), file.getAbsolutePath());

    }

    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testProcessIOException() throws IOException {
        // Context
        ConstructContext context = mock(ConstructContext.class);

        // FileUtilities
        FileUtilities fileUtils = mock(FileUtilities.class);
        doThrow(new IOException("Failed to save")).when(fileUtils).saveStringToFile(anyString(), any(File.class));

        // Run the Method
        WriteConstruct.process(context, fileUtils, mock(MetaExpression.class), mock(MetaExpression.class));
    }

    @Test
    public void testProcessNull() throws IOException {

        MetaExpression content = NULL;

        // Uri
        MetaExpression uri = fromValue("uri");

        // Context
        RobotID robotID = mock(RobotID.class);
        ConstructContext context = mock(ConstructContext.class);

        // File
        File file = mock(File.class);
        when(file.getAbsolutePath()).thenReturn("ABSPATH");
        when(TestUtils.CONSTRUCT_FILE_RESOLVER.buildFile(any(), anyString())).thenReturn(file);

        // FileUtilities
        FileUtilities fileUtils = mock(FileUtilities.class);

        // Run the Method
        MetaExpression result = WriteConstruct.process(context, fileUtils, uri, content);

        // Verify
        verify(fileUtils, times(1)).saveStringToFile(null, file);

        // Assert
        assertEquals(result.getStringValue(), file.getAbsolutePath());
    }
}
