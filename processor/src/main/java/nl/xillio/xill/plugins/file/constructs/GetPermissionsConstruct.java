package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.permissions.FilePermissions;
import nl.xillio.xill.plugins.file.services.permissions.FilePermissionsProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/**
 * This construct will use the {@link FilePermissionsProvider} to get the declared permissions for a file.
 *
 * @author Thomas Biesaart
 */
public class GetPermissionsConstruct extends Construct {
    private final FilePermissionsProvider permissionsProvider;

    @Inject
    public GetPermissionsConstruct(FilePermissionsProvider permissionsProvider) {
        this.permissionsProvider = permissionsProvider;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                path -> process(path, context),
                new Argument("path", ATOMIC)
        );
    }

    MetaExpression process(MetaExpression path, ConstructContext context) {
        Path file = getPath(context, path);

        FilePermissions permissions = getPermissions(file);

        if (permissions == null) {
            throw new RobotRuntimeException("Failed to read permissions for " + file + ". No results were found.");
        }

        LinkedHashMap<String, Object> mapResult = new LinkedHashMap<>(permissions.toMap());
        return parseObject(mapResult);
    }

    private FilePermissions getPermissions(Path file) {
        try {
            return permissionsProvider.readPermissions(file);
        } catch (NoSuchFileException e) {
            throw new RobotRuntimeException("Could not find " + file, e);
        } catch (IOException e) {
            throw new RobotRuntimeException("Failed to read permissions for " + file + ": " + e.getMessage(), e);
        }
    }
}
