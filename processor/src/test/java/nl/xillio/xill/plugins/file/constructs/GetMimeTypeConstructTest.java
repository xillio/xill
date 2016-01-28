package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class GetMimeTypeConstructTest {

    @Test
    public void testProcessNormal() throws IOException {
        // Uri
        String s = "image.jpg";
        MetaExpression uri = mock(MetaExpression.class);
        when(uri.getStringValue()).thenReturn(s);

        // Context
        RobotID robotID = mock(RobotID.class);
        ConstructContext context = mock(ConstructContext.class);
        when(context.getRobotID()).thenReturn(robotID);

        // File and path
        File file = mock(File.class);
        when(TestUtils.CONSTRUCT_FILE_RESOLVER.buildFile(any(), anyString())).thenReturn(file);
        Path path = mock(Path.class);
        when(file.toPath()).thenReturn(path);

        // Construct
        String type = "image/jpeg";
        GetMimeTypeConstruct construct = spy(new GetMimeTypeConstruct());
        doReturn(type).when(construct).getMimeType(any());

        // Run the Method
        MetaExpression result = construct.process(context, uri);

        // Verify
        verify(construct, times(1)).getMimeType(any());

        // Assert
        assertEquals(result.getStringValue(), type);
    }

    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testProcessIOException() throws IOException {
        // Construct
        GetMimeTypeConstruct construct = spy(new GetMimeTypeConstruct());
        doThrow(new IOException()).when(construct).getMimeType(any());

        // File
        when(TestUtils.CONSTRUCT_FILE_RESOLVER.buildFile(any(), anyString())).thenReturn(mock(File.class));

        // Run the method
        construct.process(mock(ConstructContext.class), mock(MetaExpression.class));
    }
}
