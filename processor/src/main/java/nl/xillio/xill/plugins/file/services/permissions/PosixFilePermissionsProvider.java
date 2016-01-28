package nl.xillio.xill.plugins.file.services.permissions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import static java.nio.file.attribute.PosixFilePermission.*;

/**
 * This provider can read posix permissions.
 *
 * @author Thomas Biesaart
 */
public class PosixFilePermissionsProvider implements FilePermissionsProvider {
    @Override
    public FilePermissions readPermissions(File file) throws IOException {
        PosixFileAttributeView posixView = Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class);

        if (posixView == null) {
            // We have no results
            return null;
        }

        PosixFileAttributes attributes = posixView.readAttributes();
        Set<PosixFilePermission> permissionsSet = attributes.permissions();

        FilePermissions permissions = new FilePermissions(file);

        // Group
        boolean readGroup = permissionsSet.contains(GROUP_READ);
        boolean writeGroup = permissionsSet.contains(GROUP_WRITE);
        boolean executeGroup = permissionsSet.contains(GROUP_EXECUTE);
        permissions.setGroup(attributes.group().getName(), readGroup, writeGroup, executeGroup);

        // User
        boolean readUser = permissionsSet.contains(OWNER_READ);
        boolean writeUser = permissionsSet.contains(OWNER_WRITE);
        boolean executeUser = permissionsSet.contains(OWNER_EXECUTE);
        permissions.setUser(attributes.owner().getName(), readUser, writeUser, executeUser);

        return permissions;
    }
}
