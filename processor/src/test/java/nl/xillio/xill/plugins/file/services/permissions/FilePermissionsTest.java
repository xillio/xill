package nl.xillio.xill.plugins.file.services.permissions;

import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertEquals;

public class FilePermissionsTest {

    @Test
    public void testToMap() throws Exception {
        FilePermissions permissions = new FilePermissions(new File("."));

        assertEquals(permissions.toMap().toString(), "{groups={}, users={}}");

        permissions.setGroup("myGroup", true, true, false);

        assertEquals(permissions.toMap().toString(), "{groups={myGroup={read=true, write=true, execute=false}}, users={}}");

        permissions.setUser("myUser", false, true, false);

        assertEquals(permissions.toMap().toString(), "{groups={myGroup={read=true, write=true, execute=false}}, users={myUser={read=false, write=true, execute=false}}}");

        // Test the combine functionality
        permissions.setUser("myUser", true, false, false);

        assertEquals(permissions.toMap().toString(), "{groups={myGroup={read=true, write=true, execute=false}}, users={myUser={read=true, write=true, execute=false}}}");
    }
}