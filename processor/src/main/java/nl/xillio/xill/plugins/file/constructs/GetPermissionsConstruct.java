package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.OperationFailedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.permissions.FilePermissions;
import nl.xillio.xill.plugins.file.services.permissions.FilePermissionsProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/**
 * This construct will use the {@link FilePermissionsProvider} to get the declared permissions for a file.
 *
 * @author Thomas Biesaart
 */
public class GetPermissionsConstruct extends AbstractFilePropertyConstruct<FilePermissions> {
    private final FilePermissionsProvider permissionsProvider;

    @Inject
    public GetPermissionsConstruct(FilePermissionsProvider permissionsProvider) {
        this.permissionsProvider = permissionsProvider;
    }

    @Override
    protected FilePermissions process(Path path) throws IOException {
        try {
            return permissionsProvider.readPermissions(path);
        } catch (IOException e) {
            throw new OperationFailedException("read permissions for " + path, e.getMessage(), e);
        }
    }

    @Override
    protected MetaExpression parse(FilePermissions input) {
        LinkedHashMap<String, Object> mapResult = new LinkedHashMap<>(input.toMap());
        return parseObject(mapResult);
    }
}
