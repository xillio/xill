package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
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
 * Test the SaveConstruct
 */
public class SaveConstructTest extends TestUtils {

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
        MetaExpression result = SaveConstruct.process(context, fileUtils, uri, content);

        // Verify
        verify(fileUtils, times(1)).saveStringToFile(contentString, file);

        // Assert
        assertEquals(result.getStringValue(), file.getAbsolutePath());

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
        MetaExpression result = SaveConstruct.process(context, fileUtils, mock(MetaExpression.class), mock(MetaExpression.class));

        // Verify
        verify(logger).error(eq("Failed to write to file: Failed to save"), any(IOException.class));

        assertEquals(result,FALSE);
    }

    @Test
    public void testProcessNull() throws IOException {
        String contentString = null;
        MetaExpression content = mock(MetaExpression.class);

        // Uri
        MetaExpression uri = mock(MetaExpression.class);

        // Context
        RobotID robotID = mock(RobotID.class);
        ConstructContext context = mock(ConstructContext.class);
        when(context.getRobotID()).thenReturn(robotID);
        when(content.isNull()).thenReturn(true);

        // File
        File file = mock(File.class);
        when(file.getAbsolutePath()).thenReturn("ABSPATH");
        when(TestUtils.CONSTRUCT_FILE_RESOLVER.buildFile(any(), anyString())).thenReturn(file);

        // FileUtilities
        FileUtilities fileUtils = mock(FileUtilities.class);

        // Run the Method
        MetaExpression result = SaveConstruct.process(context, fileUtils, uri, content);

        // Verify
        verify(fileUtils, times(1)).saveStringToFile(null, file);
        verify(content,never()).getStringValue();
        verify(content,times(1)).isNull();


        // Assert
        assertEquals(result.getStringValue(), file.getAbsolutePath());
    }

}
