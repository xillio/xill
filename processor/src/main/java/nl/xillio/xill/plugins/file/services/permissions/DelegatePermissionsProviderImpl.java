package nl.xillio.xill.plugins.file.services.permissions;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * This provider will delegate requests to other providers in list order.
 *
 * @author Thomas Biesaart
 */
@Singleton
public class DelegatePermissionsProviderImpl implements FilePermissionsProvider {
    private final List<FilePermissionsProvider> providers;

    @Inject
    public DelegatePermissionsProviderImpl(List<FilePermissionsProvider> providers) {
        this.providers = providers;
    }

    @Override
    public FilePermissions readPermissions(Path file) throws IOException {
        for (FilePermissionsProvider provider : providers) {
            FilePermissions permissions = provider.readPermissions(file);

            if (permissions != null) {
                return permissions;
            }
        }

        return null;
    }
}
