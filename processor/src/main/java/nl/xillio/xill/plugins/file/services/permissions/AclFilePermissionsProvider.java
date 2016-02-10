package nl.xillio.xill.plugins.file.services.permissions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.*;

/**
 * This provider can read acl permissions.
 *
 * @author Thomas Biesaart
 */
public class AclFilePermissionsProvider implements FilePermissionsProvider {
    @Override
    public FilePermissions readPermissions(Path file) throws IOException {
        AclFileAttributeView aclView = Files.getFileAttributeView(file, AclFileAttributeView.class);
        if (aclView == null) {
            // We have no results
            return null;
        }

        FilePermissions permissions = new FilePermissions(file);

        for (AclEntry entry : aclView.getAcl()) {
            UserPrincipal principal = entry.principal();
            boolean read = entry.permissions().contains(AclEntryPermission.READ_DATA);
            boolean write = entry.permissions().contains(AclEntryPermission.WRITE_DATA);
            boolean execute = entry.permissions().contains(AclEntryPermission.EXECUTE);

            if (principal instanceof GroupPrincipal) {
                permissions.setGroup(principal.getName(), read, write, execute);
            } else {
                permissions.setUser(principal.getName(), read, write, execute);
            }

        }

        return permissions;
    }
}
