package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.permissions.FilePermissionsProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GetPermissionsConstructTest extends TestUtils {

    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testProcessWithIOException() throws IOException {
        FilePermissionsProvider provider = mock(FilePermissionsProvider.class);
        when(provider.readPermissions(any())).thenThrow(new IOException("Something went wrong"));

        setFileResolverReturnValue(new File(""));

        GetPermissionsConstruct construct = new GetPermissionsConstruct(provider);
        construct.process(fromValue("myFile.txt"), null);
    }

    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testProcessWithFileNotFound() throws IOException {
        FilePermissionsProvider provider = mock(FilePermissionsProvider.class);
        when(provider.readPermissions(any())).thenThrow(new NoSuchFileException("Something went wrong"));

        setFileResolverReturnValue(new File(""));

        GetPermissionsConstruct construct = new GetPermissionsConstruct(provider);
        construct.process(fromValue("myFile.txt"), null);
    }


}