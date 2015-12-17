package nl.xillio.xill.plugins.file;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.plugins.file.services.permissions.AclFilePermissionsProvider;
import nl.xillio.xill.plugins.file.services.permissions.FilePermissionsProvider;
import nl.xillio.xill.plugins.file.services.permissions.PosixFilePermissionsProvider;

import java.util.Arrays;
import java.util.List;

/**
 * This package includes all example constructs.
 */
public class FileXillPlugin extends XillPlugin {

    @Provides
    @Singleton
    List<FilePermissionsProvider> filePermissionsProviders(
            AclFilePermissionsProvider acl,
            PosixFilePermissionsProvider posix) {

        return Arrays.asList(posix, acl);
    }
}
