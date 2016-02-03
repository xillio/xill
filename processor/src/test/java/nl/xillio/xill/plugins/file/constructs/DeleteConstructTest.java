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

/**
 * Test the DeleteConstruct
 */
public class DeleteConstructTest {

    @Test
    public void testProcessNormal() throws Exception {
        // URI
        String filePath = "This is the file path";
        MetaExpression uri = mock(MetaExpression.class);
        when(uri.getStringValue()).thenReturn(filePath);

        // Context
        RobotID robotID = mock(RobotID.class);
        ConstructContext context = mock(ConstructContext.class);
        when(context.getRobotID()).thenReturn(robotID);

        // FileUtilities
        FileUtilities fileUtils = mock(FileUtilities.class);

        // Run the method
        DeleteConstruct.process(context, fileUtils, uri);

        // Verify
        verify(fileUtils, times(1)).delete(any());
    }

    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testProcessIOException() throws Exception {
        // URI
        MetaExpression uri = mock(MetaExpression.class);

        // Context
        RobotID robotID = mock(RobotID.class);
        ConstructContext context = mock(ConstructContext.class);
        when(context.getRobotID()).thenReturn(robotID);

        // File
        File file = mock(File.class);
        when(file.getAbsolutePath()).thenReturn("FILE");
        when(TestUtils.CONSTRUCT_FILE_RESOLVER.buildFile(any(), anyString())).thenReturn(file);

        // FileUtilities
        FileUtilities fileUtils = mock(FileUtilities.class);
        doThrow(new IOException("Something crashed")).when(fileUtils).delete(any());

        // Run the method
        DeleteConstruct.process(context, fileUtils, uri);
    }
}
