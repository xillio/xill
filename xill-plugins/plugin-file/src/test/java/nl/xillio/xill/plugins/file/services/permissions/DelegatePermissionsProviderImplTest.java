package nl.xillio.xill.plugins.file.services.permissions;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;


public class DelegatePermissionsProviderImplTest {

    @Test
    public void testGetPermissionsDelegation() throws IOException {
        File file = new File(".");
        FilePermissions result = mock(FilePermissions.class, RETURNS_DEEP_STUBS);

        FilePermissionsProvider provider1 = mock(FilePermissionsProvider.class);
        when(provider1.readPermissions(file)).thenReturn(result);

        FilePermissionsProvider provider2 = mock(FilePermissionsProvider.class);

        FilePermissionsProvider delegate = new DelegatePermissionsProviderImpl(Arrays.asList(provider1, provider2));


        FilePermissions permissions = delegate.readPermissions(file);

        // This should be the result from the first provider
        assertEquals(permissions, result);

        // The second provider should not be called
        verify(provider2, times(0)).readPermissions(file);
    }

    @Test
    public void testGetPermissionsDelegation2() throws IOException {
        File file = new File(".");
        FilePermissions result = mock(FilePermissions.class, RETURNS_DEEP_STUBS);

        FilePermissionsProvider provider1 = mock(FilePermissionsProvider.class);

        FilePermissionsProvider provider2 = mock(FilePermissionsProvider.class);
        when(provider2.readPermissions(file)).thenReturn(result);

        FilePermissionsProvider delegate = new DelegatePermissionsProviderImpl(Arrays.asList(provider1, provider2));


        FilePermissions permissions = delegate.readPermissions(file);

        // This should be the result from the second provider
        assertEquals(permissions, result);

        // The second provider should be called
        verify(provider2).readPermissions(file);
    }
}