package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertNotNull;

/**
 * Test the IterateFoldersConstruct
 */
public class IterateFoldersConstructTest extends TestUtils {

    @Test
    public void testProcessNormalTrue() throws Exception {
        // Recursive
        MetaExpression recursive = mock(MetaExpression.class);
        when(recursive.getBooleanValue()).thenReturn(true);

        // Uri
        String path = "This is the folder uri";
        MetaExpression uri = mock(MetaExpression.class);
        when(uri.getStringValue()).thenReturn(path);

        // Context
        RobotID robotID = mock(RobotID.class);
        ConstructContext context = mock(ConstructContext.class);
        when(context.getRobotID()).thenReturn(robotID);

        // FileUtilities
        FileUtilities fileUtils = mock(FileUtilities.class);
        this.setFileResolverReturnValue(new File(""));

        // Run the Method
        MetaExpression result = IterateFoldersConstruct.process(context, fileUtils, uri, recursive);

        // Verify
        verify(fileUtils, times(1)).iterateFolders(any(), eq(true));

        // Assert
        assertNotNull(result.getMeta(MetaExpressionIterator.class));
    }

    @Test(
            expectedExceptions = RobotRuntimeException.class
    )
    public void testProcessIOException() throws IOException {
        // FileUtils
        FileUtilities fileUtils = mock(FileUtilities.class);
        when(fileUtils.iterateFolders(any(File.class), anyBoolean())).thenThrow(new IOException("This is an error"));
        this.setFileResolverReturnValue(new File(""));

        // Run the Method
        IterateFoldersConstruct.process(mock(ConstructContext.class), fileUtils, mock(MetaExpression.class), mock(MetaExpression.class));
    }
}
