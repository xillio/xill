package nl.xillio.xill.plugins.file.services.permissions;

import com.google.inject.ImplementedBy;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This interface represents an object that can produce a summary of file permissions regardless of operating system.
 *
 * @author Thomas Biesaart
 */
@ImplementedBy(DelegatePermissionsProviderImpl.class)
public interface FilePermissionsProvider {

    /**
     * read the declared permissions for a specific file.
     *
     * @param file the file
     * @return the permissions or null if no permissions could be extracted
     */
    FilePermissions readPermissions(Path file) throws IOException;
}
